CREATE TABLE IF NOT EXISTS stock_item_detail (
    id serial not null,
    stock_item_id serial not null,
    quantity integer not null,
    unit_price numeric(8,3) not null,
    total_price numeric(8,3) not null,
    organization_id uuid not null,
    PRIMARY KEY(id)
);

ALTER TABLE stock_item_detail ADD CONSTRAINT stock_item_detail_fk1 FOREIGN KEY (stock_item_id) REFERENCES stock_item (id);

