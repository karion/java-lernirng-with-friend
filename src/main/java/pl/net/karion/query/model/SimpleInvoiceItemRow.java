package pl.net.karion.query.model;

import java.util.UUID;

public record SimpleInvoiceItemRow(
    UUID invoiceId,
    String name,
    long quantity,
    long vatRatePercent,
    long netPrice
) {}
