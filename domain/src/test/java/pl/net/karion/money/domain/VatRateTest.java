package pl.net.karion.domain.money;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class VatRateTest {
    
    @ParameterizedTest
    @CsvSource({
            "0,0",
            "1,1",
            "2,2",
            "3,4",
            "100,123",
            "-100,-123"
    })
    void add_tax_to_net_money(long input, long expected) {
        Money netValue = new Money(input, Currency.from("PLN"));

        assertThat(VatRate.VAT_23.applyTo(netValue).amountInCents()).isEqualTo(expected);
    }
    
    @ParameterizedTest
    @CsvSource({
            "0,0",
            "123,100",
            "1,1",
            "2,2",
            "3,2"
    })
    void sub_tax_from_gross_money(long input, long expected) {
        Money grossValue = new Money(input, Currency.from("PLN"));

        assertThat(VatRate.VAT_23.extractFrom(grossValue).amountInCents()).isEqualTo(expected);
    }


}
