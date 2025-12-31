package pl.net.karion.domain.money;

import java.util.Locale;

import pl.net.karion.domain.DomainException;

public enum Currency {
    PLN, USD, EUR;

    static Currency from(String value) {
        try {
            return Currency.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            throw new DomainException("Unknown currency: " + value);
        }
    }
}