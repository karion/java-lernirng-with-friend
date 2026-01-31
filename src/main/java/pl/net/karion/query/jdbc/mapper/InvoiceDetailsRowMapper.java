package pl.net.karion.query.jdbc.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import pl.net.karion.query.model.InvoiceDetails;
import pl.net.karion.query.model.InvoiceItemRow;
import pl.net.karion.query.model.MoneyDTO;

public class InvoiceDetailsRowMapper {

    public InvoiceDetails mapRow(ResultSet rs, List<InvoiceItemRow> items) throws SQLException {

        UUID invoiceId = rs.getObject("id", UUID.class);
        String currency = rs.getString("currency");

        
        // for (SimpleInvoiceItemRow itemRow : itemRS) {
        //     long quantity = itemRow.quantity();
        //     long netPrice = itemRow.netPrice();
        //     long vatRate = itemRow.vatRatePercent();
        //     long totalNet = Math.multiplyExact(quantity, netPrice);
        //     long totalGross = BigDecimal.valueOf(totalNet)
        //         .multiply(
        //             BigDecimal
        //                 .valueOf(vatRate     + 100)
        //                 .divide(BigDecimal.valueOf(100))
        //         )
        //         .setScale(0, RoundingMode.HALF_UP)
        //         .longValueExact()
        //     ;
            
        //     items.add(
        //         new InvoiceItemRow(
        //             itemRow.invoiceId(),
        //             itemRow.name(),
        //             quantity,
        //             vatRate,
        //             new MoneyDTO(netPrice, currency),
        //             new MoneyDTO(totalNet, currency),
        //             new MoneyDTO(totalGross, currency)
        //         )
        //     );
        // }


        long totalNetCents = 0;
        long totalGrossCents = 0;

        if (items != null) {
            totalNetCents = items.stream()
                .mapToLong(itemRow -> itemRow.totalNet().amount())
                .sum();

            totalGrossCents = items.stream()
                .mapToLong(itemRow -> itemRow.totalGross().amount())
                .sum();
        }
        return new InvoiceDetails(
            invoiceId,
            rs.getString("number"),
            rs.getString("status"),
            rs.getDate("issued_at").toString(),
            new MoneyDTO(totalNetCents, currency),
            new MoneyDTO(totalGrossCents, currency),
            new MoneyDTO(totalGrossCents - totalNetCents, currency),
            items
        );
    }
}
