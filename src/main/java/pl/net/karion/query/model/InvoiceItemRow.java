package pl.net.karion.query.model;

import java.util.UUID;

public record InvoiceItemRow(
    UUID invoiceId,
    String name,
    long quantity,
    long vatRatePercent,
    MoneyDTO unitPriceNet,
    MoneyDTO totalNet,
    MoneyDTO totalGross
) {}
