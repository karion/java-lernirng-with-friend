package pl.net.karion.invoicing.application.handler;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import pl.net.karion.InMemoryInvoiceRepository;
import pl.net.karion.application.invoicing.InvoiceRepository;
import pl.net.karion.application.invoicing.command.CreateInvoiceCommand;
import pl.net.karion.domain.invoicing.Invoice;
import pl.net.karion.domain.invoicing.InvoiceId;
import pl.net.karion.domain.money.Currency;

public class CreateInvoiceTest {

    @Test
    public void should_create_invoice() {
        InvoiceRepository repo = new InMemoryInvoiceRepository();
        CreateInvoiceHandler handler = new CreateInvoiceHandler(repo);

        UUID invoiceId = handler.handle(
            new CreateInvoiceCommand(
                Currency.USD
            )
        );

        Invoice createdInvoice = repo.findById(
            new InvoiceId(invoiceId)
        ).orElseThrow();

        assertThat(createdInvoice.id().value()).isEqualTo(invoiceId);
        assertThat(createdInvoice.currency()).isEqualTo(Currency.USD);
    }

}
