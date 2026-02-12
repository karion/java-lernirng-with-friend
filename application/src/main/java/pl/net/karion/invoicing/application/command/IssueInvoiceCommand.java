package pl.net.karion.invoicing.application.command;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record IssueInvoiceCommand(
    UUID invoiceId,
    String number,
    Instant issueDate
) {
    public IssueInvoiceCommand {
        Objects.requireNonNull(invoiceId, "invoiceId");
        Objects.requireNonNull(number, "number");
        Objects.requireNonNull(issueDate, "issueDate");

        if (number.isBlank()) throw new IllegalArgumentException("number must not be blank");
    }
}
