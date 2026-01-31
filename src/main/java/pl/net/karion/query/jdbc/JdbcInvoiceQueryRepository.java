package pl.net.karion.query.jdbc;

import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import pl.net.karion.query.jdbc.mapper.InvoiceDetailsRowMapper;
import pl.net.karion.query.jdbc.mapper.InvoiceListRowMapper;
import pl.net.karion.query.model.InvoiceDetails;
import pl.net.karion.query.model.InvoiceItemRow;
import pl.net.karion.query.model.InvoiceListRow;
import pl.net.karion.query.port.InvoiceQueryRepository;

public class JdbcInvoiceQueryRepository implements InvoiceQueryRepository {

    private final String jdbcUrl;
    private final String username;
    private final String password;
    private final InvoiceListRowMapper invoiceListRowMapper;
    private final InvoiceDetailsRowMapper invoiceDetailsRowMapper;

    public JdbcInvoiceQueryRepository(
        String jdbcUrl,
        String username,
        String password,
        InvoiceListRowMapper invoiceListRowMapper,
        InvoiceDetailsRowMapper invoiceDetailsRowMapper
    ) {
        this.jdbcUrl = requireNotBlank(jdbcUrl, "jdbcUrl");
        this.username = requireNotBlank(username, "username");
        this.password = password; // password może być pusty w dev/test, więc nie wymuszam
        this.invoiceListRowMapper = invoiceListRowMapper;
        this.invoiceDetailsRowMapper = invoiceDetailsRowMapper;
    }

    public List<InvoiceListRow> findAllInvoices(int limit, int offset) {
        return this.getInvoiceListRows(limit, offset);
    }

    public Optional<InvoiceDetails> findById(UUID id) {
        try {
            Connection c = this.getConnection();
            PreparedStatement ps = c.prepareStatement(
                """
                SELECT id, currency, number, status, issued_at
                FROM invoices
                WHERE id = ?
                """
            );
            ps.setObject(1, id);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return Optional.empty();
            }

            List<InvoiceItemRow> itemRows = this.getItemRows(new UUID[]{id});

            InvoiceDetails invoiceDetails = this.invoiceDetailsRowMapper.mapRow(rs, itemRows);

            return Optional.of(invoiceDetails);
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching invoice details", e);
        }
    }

    private List<InvoiceItemRow> getItemRows(UUID[] uuids) throws SQLException {

        final String sql = """
            SELECT invoice_id, name, quantity, currency, net_price_cents, vat_rate
            FROM invoice_items
            WHERE invoice_id = ANY (?)
            ORDER BY name ASC
            """;

        try (
            Connection connection = this.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
        ){
        
            Array sqlArray = connection.createArrayOf("uuid", uuids);

            ps.setArray(1, sqlArray);

            ResultSet rs = ps.executeQuery();

            List<InvoiceItemRow> items = this.invoiceListRowMapper.mapItemRow(rs);

            return items;
        }
    }

    private List<InvoiceListRow> getInvoiceListRows(int limit, int offset) {

        final String sql = """
            SELECT id, currency, number, status, issued_at
            FROM invoices
            ORDER BY issued_at DESC
            LIMIT ? OFFSET ?
            """;

        List<InvoiceListRow> rows = new ArrayList<>();

        try (
            Connection c = getConnection();
            PreparedStatement ps = c.prepareStatement(sql)
        ) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return rows;

                InvoiceListRow row = this.invoiceListRowMapper.mapRow(rs);

                rows.add(row);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching invoice list rows", e);
        }

        UUID[] uuids = rows.stream()
            .map(InvoiceListRow::id)
            .toArray(UUID[]::new);

        try {
            return this.invoiceListRowMapper.enrichInvoiceListRowsWithTotals(
                rows,
                this.getItemRows(uuids)
            );
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching invoice list rows", e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }
    
    private static String requireNotBlank(String v, String name) {
        if (v == null || v.isBlank()) throw new IllegalArgumentException(name + " must not be blank");
        return v;
    }
}
