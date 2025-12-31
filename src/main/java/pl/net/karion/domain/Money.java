package pl.net.karion.domain;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class Money {
    private final long cents;
    private final String currency;

    public Money(long cents, String currency) throws DomainException {
        this.cents = cents;
        if (!this.validateCurrency(currency)) {
            throw new DomainException("Unknown Currency");
        }
        this.currency = currency;
    }

    public String print() {
        BigDecimal amount = BigDecimal
            .valueOf(cents)
            .movePointLeft(2);

        return String.format(
            Locale.ROOT,
            "%.2f%s",
            amount,
            this.currency
        );
    }

    private boolean validateCurrency(String currency) {
        List<String> avaibleCurreny = Arrays.asList(new String[] {"PLN", "USD", "EUR"});

        return avaibleCurreny.contains(currency);
    }
    
    public Money add(Money other) throws DomainException {
        if (other == null) {
            throw new NullPointerException();
        }

        if (!other.currency.equals(this.currency)) {
            throw new DomainException("Can't convert currency.");
        }

        return new Money(
            Math.addExact(this.cents, other.cents),
            this.currency
        );
    }

    public Money sub(Money other) throws DomainException {
        if (other == null) {
            throw new NullPointerException();
        }

        if (!other.currency.equals(this.currency)) {
            throw new DomainException("Can't convert currency.");
        }
       
        return new Money(
            this.cents - other.cents,
            this.currency
        );
    }

    public long getValue() {
        return cents;
    }
}
