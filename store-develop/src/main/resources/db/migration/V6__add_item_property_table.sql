CREATE TABLE IF NOT EXISTS item_property (
    id serial not null,
    label text not null,
    organization_id uuid not null,
    deleted boolean DEFAULT false,
    PRIMARY KEY(id)
);