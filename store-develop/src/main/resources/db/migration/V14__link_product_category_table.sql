-- Este script crea las relaciones que faltan en la tabla 'product_categories'.
-- !! IMPORTANTE !!
-- Reemplaza los valores de 'product_id' y 'category_id' con los IDs reales de tu base de datos.

-- Ejemplo: Unir el Producto con ID 1 a la Categoría con ID 1
INSERT INTO product_category (product_id, category_id, organization_id)
VALUES (2, 2, '188b524e-e6eb-4907-8067-2ed0eb423e5c');

-- Ejemplo: Unir el Producto con ID 2 a la Categoría con ID 2
INSERT INTO product_category (product_id, category_id, organization_id)
VALUES (4, 3, '188b524e-e6eb-4907-8067-2ed0eb423e5c');

-- Ejemplo: Unir el Producto con ID 3 a la Categoría con ID 3
INSERT INTO product_category (product_id, category_id, organization_id)
VALUES (5, 4, '188b524e-e6eb-4907-8067-2ed0eb423e5c');

-- ... Repite esto para tus 7 productos y 7 categorías ...

-- Ejemplo: Unir el Producto con ID 4 a la Categoría con ID 4
INSERT INTO product_category (product_id, category_id, organization_id)
VALUES (6, 7, '188b524e-e6eb-4907-8067-2ed0eb423e5c');

-- Ejemplo: Unir el Producto con ID 5 a la Categoría con ID 5
INSERT INTO product_category (product_id, category_id, organization_id)
VALUES (7, 8, '188b524e-e6eb-4907-8067-2ed0eb423e5c');

-- Ejemplo: Unir el Producto con ID 6 a la Categoría con ID 6
INSERT INTO product_category (product_id, category_id, organization_id)
VALUES (8, 9, '188b524e-e6eb-4907-8067-2ed0eb423e5c');

-- Ejemplo: Unir el Producto con ID 7 a la Categoría con ID 7
INSERT INTO product_category (product_id, category_id, organization_id)
VALUES (2, 9, '188b524e-e6eb-4907-8067-2ed0eb423e5c');