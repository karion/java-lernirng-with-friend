package pl.net.karion.query.port;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import pl.net.karion.query.model.InvoiceDetails;
import pl.net.karion.query.model.InvoiceListRow;

public interface InvoiceQueryRepository {
    public List<InvoiceListRow> findAllInvoices(int limit, int offset);
    public Optional<InvoiceDetails> findById(UUID id);
}
