package pl.net.karion.domain.invoicing;

import java.util.Objects;

import pl.net.karion.domain.money.Money;
import pl.net.karion.domain.money.VatRate;

public record VatBreakdownLine(VatRate rate, Money net, Money vat, Money gross) {
    public VatBreakdownLine {
        Objects.requireNonNull(rate, "rate");
        Objects.requireNonNull(net, "net");
        Objects.requireNonNull(vat, "vat");
        Objects.requireNonNull(gross, "gross");
        // opcjonalnie: waluty muszą się zgadzać
        if (net.getCurrency() != vat.getCurrency() || net.getCurrency() != gross.getCurrency()) {
            throw new IllegalArgumentException("Currency mismatch in VAT breakdown line");
        }
    }
}
