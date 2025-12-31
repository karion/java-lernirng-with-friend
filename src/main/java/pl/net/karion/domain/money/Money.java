package pl.net.karion.domain.money;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Objects;

import pl.net.karion.domain.DomainException;

public final class Money {
    private final long cents;
    private final Currency currency;

    public Money(long cents, Currency currency) {
        this.cents = cents;
        this.currency = Objects.requireNonNull(currency);
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

    public Money add(Money other) {
        Objects.requireNonNull(other, "other");

        ensureSameCurrency(other);

        return new Money(
            Math.addExact(this.cents, other.cents),
            this.currency
        );
    }

    public Money sub(Money other) {
        Objects.requireNonNull(other, "other");

        ensureSameCurrency(other);
       
        return new Money(
            Math.subtractExact(this.cents, other.cents),
            this.currency
        );
    }

    public long amountInCents() {
        return cents;
    }

    public Currency getCurrency()
    {
        return this.currency;
    }

    private void ensureSameCurrency(Money other) {
        if (this.currency != other.currency) {
            throw new DomainException("Currency mismatch");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money other)) return false;
        return cents == other.cents && currency == other.currency;
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(cents);
        result = 31 * result + currency.hashCode();
        return result;
    }
}
