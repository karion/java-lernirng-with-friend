package pl.net.karion.invoicing.application.handler;

import pl.net.karion.application.invoicing.InvoiceRepository;
import pl.net.karion.application.invoicing.command.IssueInvoiceCommand;
import pl.net.karion.domain.invoicing.Invoice;
import pl.net.karion.domain.invoicing.InvoiceId;
import pl.net.karion.domain.invoicing.InvoiceNumber;

public final class IssueInvoiceHandler {
    private final InvoiceRepository repo;
    public IssueInvoiceHandler(InvoiceRepository invoiceRepository) {
        this.repo = invoiceRepository;
    }

    public void handle(IssueInvoiceCommand command) {
        Invoice invoice = this.repo.findById(new InvoiceId(command.invoiceId()))
            .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        invoice.issue(
            new InvoiceNumber(command.number()),
            command.issueDate()
        );

        this.repo.save(invoice);
    }
}
