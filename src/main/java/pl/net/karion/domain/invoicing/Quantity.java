package pl.net.karion.domain.invoicing;

import pl.net.karion.domain.DomainException;

public record Quantity(int value) {
    public Quantity {
        if (value <= 0) throw new DomainException("Quantity must be > 0");
    }
}
