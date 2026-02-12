package pl.net.karion.invoicing.application;

import java.util.Optional;

import pl.net.karion.invoicing.domain.Invoice;
import pl.net.karion.invoicing.domain.InvoiceId;

public interface InvoiceRepository {
    public static final String ERR_INVOICE_NOT_FOUND = "Invoice not found";
    
    Optional<Invoice> findById(InvoiceId id);
    void save (Invoice invoice);
}