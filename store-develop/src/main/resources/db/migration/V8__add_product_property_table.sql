CREATE TABLE IF NOT EXISTS product_property (
    id serial not null,
    product_id serial not null,
    item_property_id serial not null,
    organization_id uuid not null,
    PRIMARY KEY(id)
);

ALTER TABLE product_property ADD CONSTRAINT product_property_fk1 FOREIGN KEY (product_id) REFERENCES product (id);
ALTER TABLE product_property ADD CONSTRAINT product_property_fk2 FOREIGN KEY (item_property_id) REFERENCES item_property (id);