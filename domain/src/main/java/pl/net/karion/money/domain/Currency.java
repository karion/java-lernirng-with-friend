package pl.net.karion.money.domain;

import java.util.Locale;

import pl.net.karion.shared.domain.DomainException;

public enum Currency {
    PLN, USD, EUR;

    public static final String ERR_CURRENCY_MISMATCH = "Currency mismatch";
    public static final String ERR_UNKNOWN_CURRENCY = "Unknown currency";

    public static Currency from(String value) {
        if (value == null || value.isBlank()) {
            throw new DomainException(ERR_UNKNOWN_CURRENCY);
        }
        try {
            return Currency.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new DomainException(ERR_UNKNOWN_CURRENCY, e);
        }
    }

}