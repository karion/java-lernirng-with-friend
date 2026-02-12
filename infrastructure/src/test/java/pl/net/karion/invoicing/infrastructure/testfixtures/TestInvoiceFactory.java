package pl.net.karion.invoicing.infrastructure.testfixtures;

import java.time.Instant;

import pl.net.karion.domain.invoicing.Invoice;
import pl.net.karion.domain.invoicing.InvoiceId;
import pl.net.karion.domain.invoicing.InvoiceItem;
import pl.net.karion.domain.invoicing.InvoiceNumber;
import pl.net.karion.domain.invoicing.Quantity;
import pl.net.karion.money.domain.Currency;
import pl.net.karion.money.domain.Money;
import pl.net.karion.money.domain.VatRate;

public final class TestInvoiceFactory {
    public static Invoice invoice(
            String number,
            Instant issueDate,
            long itemNetPriceCents,
            int quantity
    ) {
        InvoiceItem item = new InvoiceItem(
                "Usługa",
                new Quantity(quantity),
                new Money(itemNetPriceCents, Currency.PLN),
                VatRate.VAT_23
        );

        Invoice invoice = new Invoice(InvoiceId.newId(), Currency.PLN);
        invoice.addItem(item);
        invoice.issue(
                new InvoiceNumber(number),
                issueDate
        );

        return invoice;
    }
}
