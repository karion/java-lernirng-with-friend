package pl.net.karion.query.jdbc.mapper;

import java.sql.*;
import java.util.List;
import java.util.UUID;

import pl.net.karion.query.model.InvoiceDetails;
import pl.net.karion.query.model.InvoiceItemRow;
import pl.net.karion.query.model.MoneyDTO;

public class InvoiceDetailsRowMapper {

    public InvoiceDetails mapRow(ResultSet rs, List<InvoiceItemRow> itemRows) throws SQLException {

        UUID invoiceId = rs.getObject("id", UUID.class);
        String currency = rs.getString("currency");

        long totalNetCents = itemRows.stream()
            .mapToLong(itemRow -> itemRow.totalNet().amount())
            .sum();

        long totalGrossCents = itemRows.stream()
            .mapToLong(itemRow -> itemRow.totalGross().amount())
            .sum();

        return new InvoiceDetails(
            invoiceId,
            rs.getString("number"),
            rs.getString("status"),
            rs.getDate("issued_at").toString(),
            new MoneyDTO(totalNetCents, currency),
            new MoneyDTO(totalGrossCents, currency),
            new MoneyDTO(totalGrossCents - totalNetCents, currency),
            itemRows
        );
    }
}
