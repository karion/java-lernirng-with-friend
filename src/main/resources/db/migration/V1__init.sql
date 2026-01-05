CREATE TABLE invoices (
    id UUID PRIMARY KEY,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(20) NOT NULL,
    number VARCHAR(50),
    issued_at TIMESTAMPTZ
);

CREATE TABLE invoice_items (
    invoice_id UUID NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    quantity INT NOT NULL,
    net_price_cents BIGINT NOT NULL,
    vat_rate SMALLINT NOT NULL
);
