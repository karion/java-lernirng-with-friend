package pl.net.karion.application.invoicing.handler;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import pl.net.karion.application.invoicing.InvoiceRepository;
import pl.net.karion.application.invoicing.command.AddItemToInvoiceCommand;
import pl.net.karion.domain.invoicing.Invoice;
import pl.net.karion.domain.invoicing.InvoiceId;
import pl.net.karion.domain.money.Currency;
import pl.net.karion.infrastructure.invoicing.InMemoryInvoiceRepository;

public class AddItemToInvoiceHandlerTest {

    @Test
    public void should_add_item_to_invoice() {
        InvoiceRepository repo = new InMemoryInvoiceRepository();
        AddItemToInvoiceHandler handler = new AddItemToInvoiceHandler(repo);

        InvoiceId invoiceId = InvoiceId.newId();
        Invoice invoice = new Invoice(
            invoiceId,
            Currency.PLN
        );
        repo.save(invoice);

        AddItemToInvoiceCommand command = new AddItemToInvoiceCommand(
            "Test Item",
            2,
            10000,
            Currency.PLN,
            23,
            invoiceId.value()
        );

        handler.handle(command);

        Invoice updatedInvoice = repo.findById(invoiceId).orElseThrow();

        assertThat(updatedInvoice.items().size()).isEqualTo(1);
        assertThat(updatedInvoice.items().get(0).name()).isEqualTo("Test Item");
    }
}
