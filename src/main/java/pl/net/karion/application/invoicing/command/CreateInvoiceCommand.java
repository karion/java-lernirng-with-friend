package pl.net.karion.application.invoicing.command;

import pl.net.karion.domain.money.Currency;

public record CreateInvoiceCommand(
    Currency currency
) {

}
