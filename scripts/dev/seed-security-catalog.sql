-- Catalogo base de seguridad para desarrollo (MySQL).
-- Requiere el esquema newtonbox y las tablas creadas por Hibernate.
-- Ejecutar el script completo, sin ejecuciones simultaneas:
-- roles.role_enum no tiene una restriccion UNIQUE.
-- Las ejecuciones sucesivas insertan solo valores ausentes.
-- No elimina duplicados preexistentes ni modifica role_permissions.

USE newtonbox;

START TRANSACTION;

INSERT INTO roles (role_enum)
SELECT catalog.role_enum
FROM (
    SELECT 'ADMIN' AS role_enum
    UNION ALL SELECT 'RESEARCHER'
    UNION ALL SELECT 'REVIEWER'
) AS catalog
WHERE NOT EXISTS (
    SELECT 1
    FROM roles AS existing
    WHERE existing.role_enum = catalog.role_enum
);

INSERT INTO permissions (permission_enum)
SELECT catalog.permission_enum
FROM (
    SELECT 'CREATE' AS permission_enum
    UNION ALL SELECT 'READ'
    UNION ALL SELECT 'UPDATE'
    UNION ALL SELECT 'DELETE'
) AS catalog
WHERE NOT EXISTS (
    SELECT 1
    FROM permissions AS existing
    WHERE existing.permission_enum = catalog.permission_enum
);

COMMIT;
