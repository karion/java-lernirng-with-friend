package pl.net.karion.invoicing.application.command;

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

        if (name.isBlank()) throw new IllegalArgumentException("name must not be blank");
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be > 0");
        // jeśli dopuszczasz ceny ujemne? zwykle nie:
        if (netPrice < 0) throw new IllegalArgumentException("netPrice must be >= 0");
        if (vatRate < 0) throw new IllegalArgumentException("vatRate must be >= 0");
    }
}
