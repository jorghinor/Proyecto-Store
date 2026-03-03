CREATE TABLE IF NOT EXISTS item_property_value (
    id serial not null,
    item_property_id serial not null,
    item_property_value text not null,
    organization_id uuid not null,
    deleted boolean DEFAULT false,
    PRIMARY KEY(id)
);

ALTER TABLE item_property_value ADD CONSTRAINT item_property_value_fk1 FOREIGN KEY (item_property_id) REFERENCES item_property (id);