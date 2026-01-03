package pl.net.karion.domain.money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

import pl.net.karion.domain.DomainException;

public enum VatRate {
    VAT_23(23),
    VAT_8(8),
    VAT_5(5);

    public static final String ERR_UNKNOWN_VAT_RATE = "Unknown VAT rate";

    private final long value;

    VatRate(long value) {
        this.value = value;
    }

    public static VatRate fromPercent(long percent) {
        return Arrays.stream(values())
            .filter(v -> v.value == percent)
            .findFirst()
            .orElseThrow(() -> 
                new DomainException(ERR_UNKNOWN_VAT_RATE)
            );
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
            net.currency()
        );
    }

    public Money extractFrom(Money gross) {
        return new Money (
            BigDecimal.valueOf(gross.amountInCents())
                .divide(this.getMultiply(), 0, RoundingMode.HALF_UP)
                .longValueExact(),
            gross.currency()
        );
    }
}