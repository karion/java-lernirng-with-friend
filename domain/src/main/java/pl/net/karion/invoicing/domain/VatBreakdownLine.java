package pl.net.karion.invoicing.domain;

import java.util.Objects;

import pl.net.karion.shared.domain.DomainException;
import pl.net.karion.money.domain.Currency;
import pl.net.karion.money.domain.Money;
import pl.net.karion.money.domain.VatRate;

public record VatBreakdownLine(VatRate rate, Money net, Money vat, Money gross) {
    public VatBreakdownLine {
        Objects.requireNonNull(rate, "rate");
        Objects.requireNonNull(net, "net");
        Objects.requireNonNull(vat, "vat");
        Objects.requireNonNull(gross, "gross");
        // opcjonalnie: waluty muszą się zgadzać
        if (net.currency() != vat.currency() || net.currency() != gross.currency()) {
            throw new DomainException(Currency.ERR_CURRENCY_MISMATCH);
        }
    }
}
