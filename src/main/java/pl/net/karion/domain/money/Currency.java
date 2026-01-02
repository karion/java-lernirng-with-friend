package pl.net.karion.domain.money;

import java.util.Locale;

import pl.net.karion.domain.DomainException;

public enum Currency {
    PLN, USD, EUR;

    public static final String ERR_CURRENCY_MISMATCH = "Currency mismatch";
    public static final String ERR_UNKNOWN_CURRENCY = "Unknown currency";

    static Currency from(String value) {
        try {
            return Currency.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            throw new DomainException(ERR_UNKNOWN_CURRENCY);
        }
    }
}