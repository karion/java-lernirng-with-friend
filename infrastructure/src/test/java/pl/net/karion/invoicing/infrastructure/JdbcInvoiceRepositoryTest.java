package pl.net.karion.invoicing.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import pl.net.karion.PostgresTestContainer;
import pl.net.karion.domain.invoicing.Invoice;
import pl.net.karion.domain.invoicing.InvoiceId;
import pl.net.karion.domain.money.Currency;

public class JdbcInvoiceRepositoryTest extends PostgresTestContainer {
    private JdbcInvoiceRepository repo;

    @BeforeEach
    void setup() {
        repo = new JdbcInvoiceRepository(
            POSTGRES.getJdbcUrl(),
            POSTGRES.getUsername(),
            POSTGRES.getPassword()
        );
    }

    @Test
    void save_then_findById_returns_invoice() {
        // given
        InvoiceId id = InvoiceId.newId();
        Invoice invoice = new Invoice(id, Currency.PLN);

        // when
        repo.save(invoice);

        // then

        Optional<Invoice> result = repo.findById(invoice.id());
        
        assertThat(result)
            .isPresent()
            .get()
            .satisfies(found -> {
                assertThat(found.id()).isEqualTo(id);
                assertThat(found.currency()).isEqualTo(Currency.PLN);
            });
    }
}
