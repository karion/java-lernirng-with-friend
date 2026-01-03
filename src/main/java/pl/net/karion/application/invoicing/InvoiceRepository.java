package pl.net.karion.application.invoicing;

import java.util.Optional;

import pl.net.karion.domain.invoicing.Invoice;
import pl.net.karion.domain.invoicing.InvoiceId;

public interface InvoiceRepository {
    Optional<Invoice> findById(InvoiceId id);
    void save (Invoice invoice);
}
