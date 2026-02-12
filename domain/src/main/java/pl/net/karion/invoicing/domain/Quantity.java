package pl.net.karion.invoicing.domain;

import pl.net.karion.shared.domain.DomainException;

public record Quantity(int value) {

    public static final String ERR_QUANTITY_BIGGER_THEN_ZERO = "Quantity must be > 0";
    public Quantity {
        if (value <= 0) throw new DomainException(ERR_QUANTITY_BIGGER_THEN_ZERO);
    }
}
