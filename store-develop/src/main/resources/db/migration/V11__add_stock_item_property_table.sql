CREATE TABLE IF NOT EXISTS stock_item_property (
    id serial not null,
    stock_item_id serial not null,
    item_property_id serial not null,
    item_property_value_id serial not null,
    organization_id uuid not null,
    PRIMARY KEY(id)
);

ALTER TABLE stock_item_property ADD CONSTRAINT stock_item_property_fk1 FOREIGN KEY (stock_item_id) REFERENCES stock_item (id);
ALTER TABLE stock_item_property ADD CONSTRAINT stock_item_property_fk2 FOREIGN KEY (item_property_id) REFERENCES item_property (id);
ALTER TABLE stock_item_property ADD CONSTRAINT stock_item_property_fk3 FOREIGN KEY (item_property_value_id) REFERENCES item_property_value (id);
