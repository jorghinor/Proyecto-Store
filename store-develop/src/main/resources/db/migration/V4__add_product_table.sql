CREATE TABLE IF NOT EXISTS product (
    id serial not null,
    name text not null,
    organization_id uuid not null,
    deleted boolean DEFAULT false,
    PRIMARY KEY(id)
);
