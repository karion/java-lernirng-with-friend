package pl.net.karion.domain.money;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.assertj.core.api.Assertions.*;

public class MoneyTest {

    @ParameterizedTest
    @CsvSource({
        "0,'PLN','0.00PLN'",
        "1,'PLN','0.01PLN'",
        "150,'PLN','1.50PLN'",
        "-2000,'EUR','-20.00EUR'"
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
            Money aMoney = new Money(a,  Currency.PLN);
            Money bMoney = new Money(b, Currency.PLN);
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
            Money aMoney = new Money(a, Currency.PLN);
            Money bMoney = new Money(b, Currency.PLN);
            Money result = aMoney.sub(bMoney);
            assertThat(result.amountInCents()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
        "100,'PLN',true",
        "0,'PLN',false",
        "101,'PLN',false",
        "99,'PLN',false",
        "100,'EUR',false"
    })
    void is_equal_to_100PLN(long a, String c, boolean expected) {

        Money orgin = new Money(100, Currency.PLN);
            
        Money other = new Money(a,  Currency.from(c));
        
        assertThat(orgin.equals(other)).isEqualTo(expected);
    }

    @Test
    void equals_should_be_true_for_same_amount_and_currency() {
        Money a = new Money(100, Currency.PLN);
        Money b = new Money(100, Currency.PLN);

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void equals_should_be_false_for_different_currency() {
        Money a = new Money(100, Currency.PLN);
        Money b = new Money(100, Currency.EUR);

        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equals_should_be_false_for_null_and_other_types() {
        Money a = new Money(100, Currency.PLN);

        assertThat(a.equals(null)).isFalse();
        assertThat(a.equals("100 PLN")).isFalse();
    }
}
