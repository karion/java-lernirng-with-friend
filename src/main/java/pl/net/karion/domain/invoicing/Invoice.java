package pl.net.karion.domain.invoicing;

import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import pl.net.karion.domain.DomainException;
import pl.net.karion.domain.money.Currency;
import pl.net.karion.domain.money.Money;
import pl.net.karion.domain.money.VatRate;

public final class Invoice {

    private final InvoiceId id;
    private final Currency currency;
    
    private InvoiceStatus status = InvoiceStatus.DRAFT;
    private List<InvoiceItem> items = new ArrayList<>();

    private InvoiceNumber number;
    private Instant issuedAt;

    public Invoice(
        InvoiceId id,
        Currency currency
    ) {
        this.id = Objects.requireNonNull(id);
        this.currency = Objects.requireNonNull(currency);
    }

    public InvoiceId id() { return id;}
    public Currency currency() { return currency;}
    public InvoiceStatus status() { return status;}
    public Instant issuedAt() { return issuedAt;}
    public InvoiceNumber number() { return number;}
    public List<InvoiceItem> items() { return items;}

    private void ensureDraft() {
        if (status != InvoiceStatus.DRAFT) {
            throw new DomainException("Invoice is not editable once issued");
        }
    }

    public void addItem(InvoiceItem item) {
        ensureDraft();
        Objects.requireNonNull(item);
        if (this.currency != item.currency()) {
            throw new DomainException("Incorrect currency.");
        }
        this.items.add(item);
    }

    public void removeItem(int index) {
        ensureDraft();
        this.items.remove(index);
    }

    public void issue(InvoiceNumber number, Instant date) {
        ensureDraft();
        if (this.items.isEmpty()) { throw new DomainException("Cannot issue invoice without items");}

        this.number = Objects.requireNonNull(number);
        this.issuedAt = Objects.requireNonNull(date);
        this.status = InvoiceStatus.ISSUED;
    }
    
    public InvoiceTotals totals() {

        Money totalNet = new Money(0, currency);
        Money totalGross = new Money(0, currency);

        Map<VatRate, Money> netByRate = new EnumMap<>(VatRate.class);
        Map<VatRate, Money> grossByRate = new EnumMap<>(VatRate.class);

        for (InvoiceItem item : items) {
            Money lineNet = item.netValue();
            Money lineGross = item.grossValue();
            VatRate rate = item.vatRate();

            totalNet = totalNet.add(lineNet);
            totalGross = totalGross.add(lineGross);

            netByRate.merge(rate, lineNet, Money::add);
            grossByRate.merge(rate, lineGross, Money::add);
    }

        Money totalVat = totalGross.sub(totalNet);

        List<VatBreakdownLine> breakdown = netByRate.keySet().stream()
                .sorted((a,b) -> a.name().compareTo(b.name()))
                .map(rate -> {
                    Money net = netByRate.get(rate);
                    Money gross = grossByRate.get(rate);
                    Money vat = gross.sub(net);
                    return new VatBreakdownLine(rate, net, vat, gross);
                })
                .toList();

        return new InvoiceTotals(totalNet, totalVat, totalGross, breakdown);
    }

}
