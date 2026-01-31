package pl.net.karion.infrastructure.invoicing;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import pl.net.karion.application.invoicing.InvoiceRepository;
import pl.net.karion.domain.DomainException;
import pl.net.karion.domain.invoicing.Invoice;
import pl.net.karion.domain.invoicing.InvoiceId;
import pl.net.karion.domain.invoicing.InvoiceItem;
import pl.net.karion.domain.invoicing.InvoiceNumber;
import pl.net.karion.domain.invoicing.InvoiceStatus;
import pl.net.karion.domain.invoicing.Quantity;
import pl.net.karion.domain.money.Currency;
import pl.net.karion.domain.money.Money;
import pl.net.karion.domain.money.VatRate;

public final class JdbcInvoiceRepository implements InvoiceRepository {
    private final String jdbcUrl;
    private final String username;
    private final String password;

    public JdbcInvoiceRepository(String jdbcUrl, String username, String password) {
        this.jdbcUrl = requireNotBlank(jdbcUrl, "jdbcUrl");
        this.username = requireNotBlank(username, "username");
        this.password = password; // password może być pusty w dev/test, więc nie wymuszam
    }

    @Override
    public Optional<Invoice> findById(InvoiceId id) {
        try (Connection c = getConnection()) {
            c.setAutoCommit(false);
            try {
                Optional<Invoice> result = findByIdTx(c, id);
                c.commit();
                return result;
            } catch (Exception e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error in findById", e);
        }
    }

    @Override
    public void save(Invoice invoice) {
        try (Connection c = getConnection()) {
            c.setAutoCommit(false);
            try {
                upsertInvoice(c, invoice);
                replaceItems(c, invoice);
                c.commit();
            } catch (Exception e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error in save", e);
        }
    }

    // -------------------- internals --------------------

    private Optional<Invoice> findByIdTx(Connection c, InvoiceId id) throws SQLException {
        InvoiceRow row = selectInvoiceRow(c, id.value());
        if (row == null) return Optional.empty();

        List<InvoiceItemRow> items = selectInvoiceItems(c, id.value());

        // Odtworzenie agregatu: najpierw DRAFT + itemy, potem ewentualne issue()
        Invoice invoice = new Invoice(new InvoiceId(row.id), Currency.valueOf(row.currency));

        for (InvoiceItemRow it : items) {
            InvoiceItem item = new InvoiceItem(
                it.name,
                new Quantity(it.quantity),
                new Money(it.netPriceCents, Currency.valueOf(it.currency)),
                vatRateFromPercent(it.vatRatePercent)
            );
            invoice.addItem(item);
        }

        InvoiceStatus status = InvoiceStatus.valueOf(row.status);
        if (status == InvoiceStatus.ISSUED) {
            // number i issuedAt powinny być w DB
            if (row.number == null || row.number.isBlank()) {
                throw new RuntimeException("Corrupted invoice row: status=ISSUED but number is null/blank");
            }
            if (row.issuedAt == null) {
                throw new RuntimeException("Corrupted invoice row: status=ISSUED but issued_at is null");
            }

            invoice.issue(new InvoiceNumber(row.number), row.issuedAt);
        } else if (status != InvoiceStatus.DRAFT) {
            // masz DELETED w enumie – na razie nie masz zachowania "delete" w domenie,
            // więc albo ignoruj, albo rzuć wyjątek:
            // throw new RuntimeException("Unsupported invoice status: " + status);
        }

        return Optional.of(invoice);
    }

    private void upsertInvoice(Connection c, Invoice invoice) throws SQLException {
        final String sql = """
            INSERT INTO invoices (id, currency, status, number, issued_at)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE
            SET currency = EXCLUDED.currency,
                status = EXCLUDED.status,
                number = EXCLUDED.number,
                issued_at = EXCLUDED.issued_at
            """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setObject(1, invoice.id().value());
            ps.setString(2, invoice.currency().name());
            ps.setString(3, invoice.status().name());

            // number / issuedAt mogą być null w DRAFT
            if (invoice.number() != null) {
                // InvoiceNumber jest record( String number ) – najpewniej masz accessor number()
                // Jeśli masz inaczej, dostosuj:
                ps.setString(4, invoice.number().number());
            } else {
                ps.setNull(4, Types.VARCHAR);
            }

            if (invoice.issuedAt() != null) {
                ps.setTimestamp(5, Timestamp.from(invoice.issuedAt()));
            } else {
                ps.setNull(5, Types.TIMESTAMP_WITH_TIMEZONE);
            }

            ps.executeUpdate();
        }
    }

    private void replaceItems(Connection c, Invoice invoice) throws SQLException {
        // Proste podejście na start: kasujemy itemy i wstawiamy aktualne
        try (PreparedStatement del = c.prepareStatement("DELETE FROM invoice_items WHERE invoice_id = ?")) {
            del.setObject(1, invoice.id().value());
            del.executeUpdate();
        }

        final String ins = """
            INSERT INTO invoice_items (invoice_id, name, quantity, currency, net_price_cents, vat_rate)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ps = c.prepareStatement(ins)) {
            for (InvoiceItem item : invoice.items()) {
                ps.setObject(1, invoice.id().value());
                ps.setString(2, item.name());
                ps.setInt(3, item.quantity().value());
                ps.setString(4, item.currency().name());                
                ps.setLong(5, item.netPrice().amountInCents());
                ps.setLong(6, vatPercent(item.vatRate()));
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private InvoiceRow selectInvoiceRow(Connection c, UUID id) throws SQLException {
        final String sql = """
            SELECT id, currency, status, number, issued_at
            FROM invoices
            WHERE id = ?
            """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                InvoiceRow row = new InvoiceRow();
                row.id = (UUID) rs.getObject("id");
                row.currency = rs.getString("currency");
                row.status = rs.getString("status");
                row.number = rs.getString("number");

                Timestamp ts = rs.getTimestamp("issued_at");
                row.issuedAt = (ts != null) ? ts.toInstant() : null;

                return row;
            }
        }
    }

    private List<InvoiceItemRow> selectInvoiceItems(Connection c, UUID invoiceId) throws SQLException {
        final String sql = """
            SELECT name, quantity, currency, net_price_cents, vat_rate
            FROM invoice_items
            WHERE invoice_id = ?
            ORDER BY name ASC
            """;

        List<InvoiceItemRow> items = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setObject(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InvoiceItemRow r = new InvoiceItemRow();
                    r.name = rs.getString("name");
                    r.quantity = rs.getInt("quantity");
                    r.currency = rs.getString("currency");
                    r.netPriceCents = rs.getLong("net_price_cents");
                    r.vatRatePercent = rs.getLong("vat_rate");
                    items.add(r);
                }
            }
        }
        return items;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    private static String requireNotBlank(String v, String name) {
        if (v == null || v.isBlank()) throw new IllegalArgumentException(name + " must not be blank");
        return v;
    }

    // --- VAT mapping helpers ---
    private static long vatPercent(VatRate rate) {
        // Jeśli masz w enumie pole percent() – użyj go.
        // Jeśli nie masz, mapuj po nazwie:
        return switch (rate) {
            case VAT_23 -> 23L;
            case VAT_8 -> 8L;
            case VAT_5 -> 5L;
        };
    }

    private static VatRate vatRateFromPercent(long percent) {
        // Jeśli masz VatRate.fromPercent(percent) w domenie – użyj:
        // return VatRate.fromPercent(percent);

        return switch ((int) percent) {
            case 23 -> VatRate.VAT_23;
            case 8 -> VatRate.VAT_8;
            case 5 -> VatRate.VAT_5;
            default -> throw new DomainException("Unknown VAT rate: " + percent);
        };
    }

    // --- simple row holders ---
    private static final class InvoiceRow {
        UUID id;
        String currency;
        String status;
        String number;
        Instant issuedAt;
    }

    private static final class InvoiceItemRow {
        String name;
        int quantity;
        String currency;
        long netPriceCents;
        long vatRatePercent;
    }
}
