package pl.net.karion.invoicing.application.command;

import pl.net.karion.money.domain.Currency;

public record CreateInvoiceCommand(
    Currency currency
) {

}
