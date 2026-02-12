package pl.net.karion.invoicing.application.handler;

import pl.net.karion.invoicing.application.InvoiceRepository;
import pl.net.karion.invoicing.application.command.AddItemToInvoiceCommand;
import pl.net.karion.shared.domain.DomainException;
import pl.net.karion.domain.invoicing.Invoice;
import pl.net.karion.domain.invoicing.InvoiceId;
import pl.net.karion.domain.invoicing.InvoiceItem;
import pl.net.karion.domain.invoicing.Quantity;
import pl.net.karion.money.domain.Money;
import pl.net.karion.money.domain.VatRate;

public final class AddItemToInvoiceHandler {
    private final InvoiceRepository repo;

    public AddItemToInvoiceHandler(InvoiceRepository invoiceRepository) {
        this.repo = invoiceRepository;
    }

    public void handle(AddItemToInvoiceCommand command) {
        Invoice invoice = this.repo.findById(new InvoiceId(command.invoiceId()))
            .orElseThrow(() -> new DomainException(InvoiceRepository.ERR_INVOICE_NOT_FOUND));

        invoice.addItem(
            new InvoiceItem(
                command.name(),
                new Quantity(command.quantity()),
                new Money(
                    command.netPrice(),
                    command.currency()
                ),
                VatRate.fromPercent(command.vatRate())
            )
        );

        this.repo.save(invoice);
    }

}
