package pl.net.karion.domain.invoicing;

import java.util.Objects;

import pl.net.karion.domain.DomainException;
import pl.net.karion.domain.money.Currency;
import pl.net.karion.domain.money.Money;
import pl.net.karion.domain.money.VatRate;

public final class InvoiceItem {
    
    public static final String ERR_ITEM_NAME_REQUIRED = "Item name is required.";

    private final String name;
    private final Quantity quantity;
    private final Money netPrice;
    private final VatRate vatRate;

    public InvoiceItem(
        String name,
        Quantity quantity,
        Money netPrice,
        VatRate vatRate
    ){
        if(name == null || name.isBlank()) throw new DomainException(ERR_ITEM_NAME_REQUIRED);
        this.name = name;
        this.quantity = Objects.requireNonNull(quantity, "quantity");
        this.netPrice  = Objects.requireNonNull(netPrice, "netPrice");
        this.vatRate   = Objects.requireNonNull(vatRate, "vatRate");
    }

    public String name() { return name;}
    public Quantity quantity() { return quantity;}
    public Money netPrice() { return netPrice;}
    public Currency currency() { return netPrice.getCurrency();}
    public VatRate vatRate() { return vatRate;}
    
    public Money netValue() {
        long cents = Math.multiplyExact(quantity.value(), netPrice.amountInCents());
        return new Money(cents, currency());
    }

    public Money grossValue() {
        return vatRate.applyTo(netValue());
    }
}
