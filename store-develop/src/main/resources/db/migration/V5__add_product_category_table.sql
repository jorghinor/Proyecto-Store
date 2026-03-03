CREATE TABLE IF NOT EXISTS product_category (
    id serial not null,
    product_id serial not null,
    category_id serial not null,
    organization_id uuid not null,
    PRIMARY KEY(id)
);

ALTER TABLE product_category ADD CONSTRAINT product_category_fk1 FOREIGN KEY (product_id) REFERENCES product (id);
ALTER TABLE product_category ADD CONSTRAINT product_category_fk2 FOREIGN KEY (category_id) REFERENCES category (id);