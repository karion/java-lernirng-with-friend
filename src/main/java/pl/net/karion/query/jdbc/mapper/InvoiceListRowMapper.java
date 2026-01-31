package pl.net.karion.query.jdbc.mapper;

import pl.net.karion.query.model.InvoiceItemRow;
import pl.net.karion.query.model.InvoiceListRow;
import pl.net.karion.query.model.MoneyDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InvoiceListRowMapper {

    public InvoiceListRow mapRow(ResultSet invoiceRS) throws SQLException {
        Date issuedAt = invoiceRS.getDate("issued_at");

        InvoiceListRow row = new InvoiceListRow(
            (UUID) invoiceRS.getObject("id"),
            invoiceRS.getString("number"),
            invoiceRS.getString("status"),
            (issuedAt != null) ? issuedAt.toString() : null,
            invoiceRS.getString("currency"),
            null,
            null
        );
        return row;
    }

    public List<InvoiceListRow> enrichInvoiceListRowsWithTotals(
        List<InvoiceListRow> invoiceRows,
        List<InvoiceItemRow> itemRows
    ) {
        
        List<InvoiceListRow> enrichedRows = invoiceRows.stream()
            .map(invoiceRow -> {
                return this.enrichInvoiceListRowWithTotals(invoiceRow, itemRows);
            })
            .toList();
        
        return enrichedRows;
    }

    public InvoiceListRow enrichInvoiceListRowWithTotals(
        InvoiceListRow invoiceRow,
        List<InvoiceItemRow> itemRows
    ) {

        long totalNetCents = itemRows.stream()
            .filter(itemRow -> itemRow.invoiceId().equals(invoiceRow.id()))
            .mapToLong(itemRow -> itemRow.totalNet().amount())
            .sum();

        long totalGrossCents = itemRows.stream()
            .filter(itemRow -> itemRow.invoiceId().equals(invoiceRow.id()))
            .mapToLong(itemRow -> itemRow.totalGross().amount())
            .sum();

        return new InvoiceListRow(
            invoiceRow.id(),
            invoiceRow.number(),
            invoiceRow.status(),
            invoiceRow.issuedAt(),
            invoiceRow.currency(),
            new MoneyDTO(totalNetCents, invoiceRow.currency()),
            new MoneyDTO(totalGrossCents, invoiceRow.currency())
        );
    }

    public List<InvoiceItemRow> mapItemRow(ResultSet itemRS) throws SQLException {

        List<InvoiceItemRow> rows = new ArrayList<>();
        
        while (itemRS.next()) {
            long vatRate = itemRS.getLong("vat_rate");
            long quantity = itemRS.getLong("quantity");
            String currency = itemRS.getString("currency");
            long netPriceCents = itemRS.getLong("net_price_cents");
            long totalNetCents = netPriceCents * quantity;

            long totalGrossCents = BigDecimal.valueOf(totalNetCents)
                .multiply(
                    BigDecimal
                        .valueOf(vatRate     + 100)
                        .divide(BigDecimal.valueOf(100))
                )
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact()
            ;
            
            rows.add(
                new InvoiceItemRow(
                    (UUID) itemRS.getObject("invoice_id"),
                    itemRS.getString("name"),
                    quantity,
                    vatRate,
                    new MoneyDTO(netPriceCents, currency),
                    new MoneyDTO(totalNetCents, currency),
                    new MoneyDTO(totalGrossCents, currency)
                )
            );
        }
        return rows;
    }
}
