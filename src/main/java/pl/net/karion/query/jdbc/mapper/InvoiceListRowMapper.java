package pl.net.karion.query.jdbc.mapper;

import pl.net.karion.domain.money.Currency;
import pl.net.karion.domain.money.Money;
import pl.net.karion.domain.money.VatRate;
import pl.net.karion.query.model.InvoiceItemRow;
import pl.net.karion.query.model.InvoiceListRow;
import pl.net.karion.query.model.MoneyDTO;

import java.sql.*;
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

    public InvoiceItemRow mapItemRow(ResultSet itemRS) throws SQLException {

        long vatRate = itemRS.getLong("vat_rate");
        long quantity = itemRS.getLong("quantity");
        String currency = itemRS.getString("currency");
        long netPriceCents = itemRS.getLong("net_price");
        long totalNetCents = netPriceCents * quantity;

        VatRate vr = VatRate.fromPercent( vatRate);
        long totalGrossCents = vr.applyTo(new Money(totalNetCents, Currency.valueOf(currency))).amountInCents();

        return new InvoiceItemRow(
            (UUID) itemRS.getObject("invoice_id"),
            itemRS.getString("name"),
            quantity,
            vatRate,
            new MoneyDTO(netPriceCents, currency),
            new MoneyDTO(totalNetCents, currency),
            new MoneyDTO(totalGrossCents, currency)
        );
    }

}
