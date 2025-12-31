package pl.net.karion.domain.money;

import java.math.BigDecimal;
import java.math.RoundingMode;

public enum VatRate {
    VAT_23(23),
    VAT_8(8),
    VAT_5(5);

    private final long value;

    VatRate(long value) {
        this.value = value;
    }

    private BigDecimal getMultiply() {
        return BigDecimal
            .valueOf(this.value + 100)
            .divide(BigDecimal.valueOf(100))
        ;
    }

    public Money applyTo(Money net) {

        return new Money (
            BigDecimal.valueOf(net.amountInCents())
                .multiply(this.getMultiply())
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact(),
            net.getCurrency()
        );
    }

    public Money extractFrom(Money gross) {
        return new Money (
            BigDecimal.valueOf(gross.amountInCents())
                .divide(this.getMultiply(), 0, RoundingMode.HALF_UP)
                .longValueExact(),
            gross.getCurrency()
        );
    }
}