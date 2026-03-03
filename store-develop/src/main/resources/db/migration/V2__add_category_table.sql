CREATE TABLE IF NOT EXISTS category (
    id serial not null,
    label text not null,
    category_type text not null,
    organization_id uuid not null,
    PRIMARY KEY(id)
);
