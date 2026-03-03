-- Añade los campos de marca y URL de imagen a la tabla de productos
ALTER TABLE product ADD COLUMN brand VARCHAR(100);
ALTER TABLE product ADD COLUMN image_url TEXT;