package pl.net.karion.application.invoicing.command;

import java.util.Objects;

import pl.net.karion.domain.money.Currency;

public record AddItemToInvoiceCommand(
    String name,
    int quantity,
    long netPrice,
    Currency currency,
    long vatRate,
    java.util.UUID invoiceId
) {
    public AddItemToInvoiceCommand {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(quantity, "quantity");
        Objects.requireNonNull(netPrice, "netPrice");
        Objects.requireNonNull(currency, "currency");
        Objects.requireNonNull(vatRate, "vatRate");
        Objects.requireNonNull(invoiceId, "invoiceId");
    }
}
