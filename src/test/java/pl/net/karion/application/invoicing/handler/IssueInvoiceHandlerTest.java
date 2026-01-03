package pl.net.karion.application.invoicing.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import pl.net.karion.application.invoicing.InvoiceRepository;
import pl.net.karion.application.invoicing.command.IssueInvoiceCommand;
import pl.net.karion.domain.invoicing.Invoice;
import pl.net.karion.domain.invoicing.InvoiceId;
import pl.net.karion.domain.invoicing.InvoiceItem;
import pl.net.karion.domain.invoicing.InvoiceStatus;
import pl.net.karion.domain.invoicing.Quantity;
import pl.net.karion.domain.money.Currency;
import pl.net.karion.domain.money.Money;
import pl.net.karion.domain.money.VatRate;
import pl.net.karion.infrastructure.invoicing.InMemoryInvoiceRepository;

public class IssueInvoiceHandlerTest {
    
    @Test
    public void should_issue_invoice() {
        InvoiceRepository repo = new InMemoryInvoiceRepository();
        IssueInvoiceHandler handler = new IssueInvoiceHandler(repo);

        InvoiceId invoiceId = InvoiceId.newId();
        Invoice invoice = new Invoice(
            invoiceId,
            Currency.PLN
        );

        InvoiceItem item = new InvoiceItem(
            "Test Item",
            new Quantity(2),
            new Money(10000, Currency.PLN),
            VatRate.VAT_23
        );

        invoice.addItem(item);
        repo.save(invoice);

        Instant fixedTime = Instant.parse("2025-12-31T00:00:00Z");

        handler.handle(
            new IssueInvoiceCommand(
                invoiceId.value(),
                "FV/2024/001",
                fixedTime
            )
        );

        Invoice issuedInvoice = repo.findById(invoiceId).orElseThrow();

        assertThat(issuedInvoice.id().value()).isEqualTo(invoiceId.value());
        assertThat(issuedInvoice.number().number()).isEqualTo("FV/2024/001");
        assertThat(issuedInvoice.status()).isEqualTo(InvoiceStatus.ISSUED);
        assertThat(issuedInvoice.issuedAt()).isEqualTo(fixedTime);
    }
}
