package pl.net.karion;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import pl.net.karion.invoicing.application.InvoiceRepository;
import pl.net.karion.domain.invoicing.Invoice;
import pl.net.karion.domain.invoicing.InvoiceId;

public class InMemoryInvoiceRepository implements InvoiceRepository{

    private final Map<InvoiceId, Invoice> storage = new HashMap<>();

    @Override
    public Optional<Invoice> findById(InvoiceId id) {
        return Optional.ofNullable(this.storage.get(id));
    }

    @Override
    public void save(Invoice invoice) {

        this.storage.put(
            invoice.id(),
            invoice
        );
    }
}
