package pl.net.karion.invoicing.application.handler;

import java.util.Objects;
import java.util.UUID;

import pl.net.karion.application.invoicing.InvoiceRepository;
import pl.net.karion.application.invoicing.command.CreateInvoiceCommand;
import pl.net.karion.domain.invoicing.Invoice;
import pl.net.karion.domain.invoicing.InvoiceId;

public final class CreateInvoiceHandler {
    private final InvoiceRepository repo;

    public CreateInvoiceHandler(InvoiceRepository repo) {
        this.repo = repo;
    }

    public UUID handle(CreateInvoiceCommand command) {
        InvoiceId id = InvoiceId.newId();
        Invoice invoice = new Invoice(
            id,
            Objects.requireNonNull(command.currency(), "currency")
        );

        repo.save(invoice);

        return id.value();
    }
}
