package pl.net.karion.application.invoicing;

import java.util.Optional;

import pl.net.karion.domain.invoicing.Invoice;
import pl.net.karion.domain.invoicing.InvoiceId;

public interface InvoiceRepository {
    public static final String ERR_INVOICE_NOT_FOUND = "Invoice not found";
    
    Optional<Invoice> findById(InvoiceId id);
    void save (Invoice invoice);
}
