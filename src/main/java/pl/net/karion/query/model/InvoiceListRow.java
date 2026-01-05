package pl.net.karion.query.model;

import java.util.UUID;

public record InvoiceListRow(
    UUID id,
    String number,
    String status,
    String issuedAt,
    String currency,
    MoneyDTO totalNet,
    MoneyDTO totalGross
) {
}
