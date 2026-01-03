package pl.net.karion.domain.invoicing;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import pl.net.karion.domain.money.Currency;
import pl.net.karion.domain.money.Money;
import pl.net.karion.domain.money.VatRate;

public class InvoiceItemTest {

    @Test
    public void it_count_net_value() {
        InvoiceItem item = new InvoiceItem(
            new String("Pizza"),
            new Quantity(100),
            new Money(2500, Currency.PLN),
            VatRate.VAT_23
        );

        assertThat(item.netValue().amountInCents()).isEqualTo(250000);
        assertThat(item.netValue().currency()).isEqualTo(Currency.PLN);
    }
    
    @Test
    public void it_count_gross_value() {
        InvoiceItem item = new InvoiceItem(
            "Pizza",
            new Quantity(100),
            new Money(2500, Currency.PLN),
            VatRate.VAT_23
        );

        assertThat(item.grossValue().amountInCents()).isEqualTo(307500);
        assertThat(item.grossValue().currency()).isEqualTo(Currency.PLN);
    }
}
