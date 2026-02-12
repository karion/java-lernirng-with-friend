package pl.net.karion.invoicing.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import pl.net.karion.domain.DomainException;
import pl.net.karion.money.domain.Currency;
import pl.net.karion.money.domain.Money;
import pl.net.karion.money.domain.VatRate;

public class InvoiceTest {

    @Test
    public void issue_should_fail_when_empty() {
        Invoice invoice = new Invoice(InvoiceId.newId(), Currency.PLN);
        Instant fixedTime = Instant.parse("2025-01-01T00:00:00Z");

        assertThatThrownBy(() -> invoice.issue(new InvoiceNumber("No items"), fixedTime))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining(Invoice.ERR_NO_ITEMS);
    }

    @Test
    public void setup_invoice_on_issue() {
        Invoice invoice = new Invoice(InvoiceId.newId(), Currency.PLN);
        Instant fixedTime = Instant.parse("2025-01-01T00:00:00Z");
        InvoiceItem item = new InvoiceItem(
            "Item",
            new Quantity(10),
            new Money(2500, Currency.PLN),
            VatRate.VAT_23
        );

        assertEquals(invoice.status(), InvoiceStatus.DRAFT);

        invoice.addItem(item);
        invoice.issue(new InvoiceNumber("Correct number"), fixedTime);

        assertEquals(invoice.status(), InvoiceStatus.ISSUED);
        assertEquals(invoice.issuedAt(), fixedTime);
        assertEquals(invoice.currency(), Currency.PLN);

        InvoiceTotals total = invoice.totals();
        assertThat(total.totalGross().amountInCents()).isEqualTo(30750);
        assertThat(total.totalNet().amountInCents()).isEqualTo(25000);
        
        assertThat(invoice.items().size()).isEqualTo(1);
    }

    @Test
    public void adding_item_with_other_currency_should_fail() {
        Invoice invoice = new Invoice(InvoiceId.newId(), Currency.PLN);
        InvoiceItem item = new InvoiceItem(
            "Item",
            new Quantity(10),
            new Money(2500, Currency.EUR),
            VatRate.VAT_23
        );

        assertThatThrownBy(() -> invoice.addItem(item))
            .isInstanceOf(DomainException.class)
            .hasMessageContaining(Currency.ERR_CURRENCY_MISMATCH);
    }

    @Test
    public void correctly_count_vat_taxes() {
        Invoice invoice = new Invoice(InvoiceId.newId(), Currency.PLN);
        InvoiceItem item1 = new InvoiceItem(
            "First items",
            new Quantity(10),
            new Money(2500, Currency.PLN),
            VatRate.VAT_23
        );

        InvoiceItem item2 = new InvoiceItem(
            "Secound items",
            new Quantity(2),
            new Money(500, Currency.PLN),
            VatRate.VAT_8
        );

        invoice.addItem(item1);
        invoice.addItem(item2);

        InvoiceTotals total = invoice.totals();

        assertThat(total.totalGross().amountInCents()).isEqualTo(31830);
        assertThat(total.totalNet().amountInCents()).isEqualTo(26000);
        assertThat(total.byVatRate().size()).isEqualTo(2);

        assertThat(total.byVatRate())
        .extracting(
            VatBreakdownLine::rate,
            l -> l.net().amountInCents(),
            l -> l.vat().amountInCents(),
            l -> l.gross().amountInCents()
        )
        .containsExactlyInAnyOrder(
            tuple(VatRate.VAT_23, 25000L, 5750L, 30750L),
            tuple(VatRate.VAT_8,  1000L, 80L, 1080L)
        );
    }
}
