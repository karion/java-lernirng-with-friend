package pl.net.karion.query.jdbc;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;

import pl.net.karion.PostgresTestContainer;
import pl.net.karion.application.invoicing.InvoiceRepository;
import pl.net.karion.domain.invoicing.Invoice;
import pl.net.karion.infrastructure.invoicing.JdbcInvoiceRepository;
import pl.net.karion.infrastructure.invoicing.testfixtures.TestInvoiceFactory;
import pl.net.karion.query.jdbc.mapper.InvoiceDetailsRowMapper;
import pl.net.karion.query.jdbc.mapper.InvoiceListRowMapper;
import pl.net.karion.query.model.InvoiceDetails;
import pl.net.karion.query.port.InvoiceQueryRepository;

public class JdbcInvoiceQueryRepositoryTest extends PostgresTestContainer {

    private InvoiceRepository invoiceRepository;       // write-side
    private InvoiceQueryRepository queryRepository;    // read-side

    @BeforeEach
    void setup() {
        invoiceRepository = new JdbcInvoiceRepository(
            POSTGRES.getJdbcUrl(),
            POSTGRES.getUsername(),
            POSTGRES.getPassword()
        );

        queryRepository = new JdbcInvoiceQueryRepository(
            POSTGRES.getJdbcUrl(),
            POSTGRES.getUsername(),
            POSTGRES.getPassword(),
            new InvoiceListRowMapper(),
            new InvoiceDetailsRowMapper()
        );
    }

    @Test
    public void should_show_all_invoice_details() {
        // given
        Invoice invoice = TestInvoiceFactory.invoice(
            "FV/2025/01/001",
            Instant.parse("2025-12-15T10:00:00Z"),
            10000,
            2
        );
        invoiceRepository.save(invoice);

        // when
        InvoiceDetails details = queryRepository.findById(invoice.id().value()).orElseThrow();

        // then
        assertThat(details.id()).isEqualTo(invoice.id().value());
        assertThat(details.number()).isEqualTo("FV/2025/01/001");
        assertThat(details.status()).isEqualTo("ISSUED");
        assertThat(details.issuedAt()).isEqualTo("2025-12-15");
        assertThat(details.totalNet().amount()).isEqualTo(20000);
        assertThat(details.totalGross().amount()).isEqualTo(24600);
        assertThat(details.totalTax().amount()).isEqualTo(4600);
        assertThat(details.totalNet().currency()).isEqualTo("PLN");
        assertThat(details.items()).hasSize(1);
        assertThat(details.items().get(0).name()).isEqualTo("Usługa");
        assertThat(details.items().get(0).quantity()).isEqualTo(2);
    }
}
