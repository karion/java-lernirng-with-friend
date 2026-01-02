package pl.net.karion.domain.invoicing;

import java.util.Objects;

public record InvoiceNumber(String number) {
    public InvoiceNumber {
        Objects.requireNonNull(number, "number");
    }
}
