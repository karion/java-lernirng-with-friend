package pl.net.karion.invoicing.application.handler;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import pl.net.karion.InMemoryInvoiceRepository;
import pl.net.karion.invoicing.application.InvoiceRepository;
import pl.net.karion.invoicing.application.command.AddItemToInvoiceCommand;
import pl.net.karion.domain.invoicing.Invoice;
import pl.net.karion.domain.invoicing.InvoiceId;
import pl.net.karion.domain.invoicing.InvoiceItem;
import pl.net.karion.money.domain.Currency;
import pl.net.karion.money.domain.VatRate;

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

        InvoiceItem addedItem = updatedInvoice.items().get(0);
        assertThat(addedItem.name()).isEqualTo("Test Item");
        assertThat(addedItem.quantity().value()).isEqualTo(2);
        assertThat(addedItem.netPrice().amountInCents()).isEqualTo(10000);
        assertThat(addedItem.netPrice().currency()).isEqualTo(Currency.PLN);
        assertThat(addedItem.vatRate()).isEqualTo(VatRate.VAT_23);
    }
}
