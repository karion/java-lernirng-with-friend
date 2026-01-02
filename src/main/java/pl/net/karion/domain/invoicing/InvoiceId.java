package pl.net.karion.domain.invoicing;

import java.util.Objects;
import java.util.UUID;

public record InvoiceId(UUID value) {
    public InvoiceId {
        Objects.requireNonNull(value, "value");
    }

    public static InvoiceId newId() {
        return new InvoiceId(UUID.randomUUID());
    }
}
