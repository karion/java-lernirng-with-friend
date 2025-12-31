package pl.net.karion.domain.money;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;



import static org.assertj.core.api.Assertions.*;

public class MoneyTest {

    @ParameterizedTest
    @CsvSource({
            "0,'PLN','0.00PLN'",
            "1,'PLN','0.01PLN'",
            "150,'PLN','1.50PLN'",
            "-2000,'EUR', '-20.00EUR'"
    })
    void print_money(long a, String c, String expected) {
        Money aMoney = new Money(a, Currency.from(c));
        assertThat(aMoney.print()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "0,0,0",
            "1,2,3",
            "150,70,220",
            "-10,10,0",
            "-5,-6,-11"
    })
    void add_should_sum_cents(long a, long b, long expected) {
            Money aMoney = new Money(a,  Currency.from("PLN"));
            Money bMoney = new Money(b, Currency.from("PLN"));
            Money result = aMoney.add(bMoney);
            assertThat(result.amountInCents()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "200,50,150",
            "200,300,-100",
            "150,150,0",
            "-10,10,-20",
            "-50,10,-60"
    })
    void sub_should_subtract_cents(long a, long b, long expected) {
            Money aMoney = new Money(a,  Currency.from("PLN"));
            Money bMoney = new Money(b, Currency.from("PLN"));
            Money result = aMoney.sub(bMoney);
            assertThat(result.amountInCents()).isEqualTo(expected);
    }
}
