package pl.net.karion.query.model;

import java.util.List;
import java.util.UUID;

public record InvoiceDetails(
    UUID id,
    String number,
    String status,
    String issuedAt,
    MoneyDTO totalNet,
    MoneyDTO totalGross,
    MoneyDTO totalTax,
    List<InvoiceItemRow> items
) {

}
