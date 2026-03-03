-- Crea la tabla para almacenar la información de las empresas/organizaciones
CREATE TABLE organizations (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    logo_url TEXT,
    phone VARCHAR(50),
    email VARCHAR(255),
    address TEXT
);

-- Inserta la organización por defecto que ya estamos usando en el sistema.
-- ¡Esto es crucial para que la aplicación siga funcionando!
INSERT INTO organizations (id, name)
VALUES ('188b524e-e6eb-4907-8067-2ed0eb423e5c', 'Mi Tienda Deportiva (Default)');