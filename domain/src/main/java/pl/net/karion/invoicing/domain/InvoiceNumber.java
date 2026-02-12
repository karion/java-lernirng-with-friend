package pl.net.karion.invoicing.domain;

import java.util.Objects;

public record InvoiceNumber(String number) {
    public InvoiceNumber {
        Objects.requireNonNull(number, "number");
    }
}
