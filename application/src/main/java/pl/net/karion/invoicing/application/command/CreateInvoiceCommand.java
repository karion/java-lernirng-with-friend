package pl.net.karion.invoicing.application.command;

import pl.net.karion.domain.money.Currency;

public record CreateInvoiceCommand(
    Currency currency
) {

}
