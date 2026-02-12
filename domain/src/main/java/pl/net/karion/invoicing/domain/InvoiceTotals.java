package pl.net.karion.invoicing.domain;

import java.util.List;
import java.util.Objects;
import pl.net.karion.money.domain.Money;

public record InvoiceTotals(
        Money totalNet,
        Money totalVat,
        Money totalGross,
        List<VatBreakdownLine> byVatRate
) {
    public InvoiceTotals {
        Objects.requireNonNull(totalNet, "totalNet");
        Objects.requireNonNull(totalVat, "totalVat");
        Objects.requireNonNull(totalGross, "totalGross");
        Objects.requireNonNull(byVatRate, "byVatRate");
    }
}
