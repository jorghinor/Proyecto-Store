CREATE TABLE IF NOT EXISTS stock_item (
    id serial not null,
    product_id serial not null,
    product_category_id serial not null,
    organization_id uuid not null,
    PRIMARY KEY(id)
);

ALTER TABLE stock_item ADD CONSTRAINT stock_item_fk1 FOREIGN KEY (product_id) REFERENCES product (id);
ALTER TABLE stock_item ADD CONSTRAINT stock_item_fk2 FOREIGN KEY (product_category_id) REFERENCES product_category (id);