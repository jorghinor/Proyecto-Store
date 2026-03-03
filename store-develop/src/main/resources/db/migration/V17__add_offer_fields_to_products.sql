-- Añade campos para ofertas y novedades a la tabla de productos
ALTER TABLE product ADD COLUMN discount_percentage NUMERIC(5, 2);
ALTER TABLE product ADD COLUMN on_promotion BOOLEAN DEFAULT FALSE;
ALTER TABLE product ADD COLUMN is_new_arrival BOOLEAN DEFAULT FALSE;