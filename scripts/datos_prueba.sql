-- Datos de prueba de Epicentro Gourmet (Hito 2).
-- Correr DESPUES de arrancar la aplicacion una vez (Hibernate crea las tablas con ddl-auto=update).
-- Se puede volver a correr: borra los datos y los vuelve a cargar.
--
-- Usuarios (usuario / contrasena):
--   admin        / admin123   Administrador
--   resp.parrilla / resp123   Responsable de "Parrilla Don Mario" (Food Truck)
--   resp.dulces  / resp123    Responsable de "Dulces del Sur" (Puesto Desarmable)
--   empleados    / emp123     Personal de "Parrilla Don Mario" y "Dulces del Sur" (usuario = su email)
--
-- Festival 1 ("Primavera") esta EN CURSO y dura 42 dias, para probar liquidacion con mas de 30 dias.

USE bd_epicentro_gourmet_hito2;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE detalle_pedido;
TRUNCATE TABLE pedido;
TRUNCATE TABLE plato;
TRUNCATE TABLE cajero;
TRUNCATE TABLE cocinero;
TRUNCATE TABLE food_track;
TRUNCATE TABLE desarmable;
TRUNCATE TABLE unidad_venta;
TRUNCATE TABLE staff;
TRUNCATE TABLE user_role;
TRUNCATE TABLE users;
TRUNCATE TABLE costo;
TRUNCATE TABLE festival;
SET FOREIGN_KEY_CHECKS = 1;

-- ------------------------------------------------------------------ festivales y costos
INSERT INTO festival (id, nombre, temporada, fecha_inicio, fecha_fin) VALUES
 (1, 'Festival Gastronomico de Primavera', 'Primavera', '2026-09-20', '2026-10-31'),
 (2, 'Sabores de Verano',                  'Verano',    '2026-12-10', '2026-12-24'),
 (3, 'Feria del Otono',                    'Otono',     '2026-03-10', '2026-04-08');

INSERT INTO costo (id, festival_id, costo_por_superficie, costo_por_montaje, plus_electricidad, sueldo_base,
                   monto_anio_antiguedad, plus_cocinero, plus_cocinero_ayudante, plus_lavaplatos) VALUES
 (1, 1, 500, 10, 2000, 100000, 5000, 150000, 100000, 50000),
 (2, 2, 500, 10, 2000, 100000, 5000, 150000, 100000, 50000),
 (3, 3, 500, 10, 2000, 100000, 5000, 150000, 100000, 50000);

-- ------------------------------------------------------------------ usuarios y roles
-- Hashes BCrypt de admin123 / resp123 / emp123
INSERT INTO users (id, username, password, enabled, created_at, updated_at) VALUES
 (1, 'admin',                       '$2a$10$KiMX1hN/9vmaLKEQeyPCqOqht25UW1tjoI0h8szrROSyEO1cwQ3XS', 1, NOW(6), NOW(6)),
 (2, 'resp.parrilla',               '$2a$10$Ia1Z5gZQ6r/9AXhLSnPLJufl4EMgOZYzx5dnD0DYfk9bRAVn3MRQS', 1, NOW(6), NOW(6)),
 (3, 'resp.dulces',                 '$2a$10$Ia1Z5gZQ6r/9AXhLSnPLJufl4EMgOZYzx5dnD0DYfk9bRAVn3MRQS', 1, NOW(6), NOW(6)),
 (4, 'lucia.cocina@epicentro.test',  '$2a$10$q7CTmUaOVYxZ0ytgL7J3L.afgm6h0YiF6FDDAtX5tpBduZee8zyw6', 1, NOW(6), NOW(6)),
 (5, 'pablo.cocina@epicentro.test',  '$2a$10$q7CTmUaOVYxZ0ytgL7J3L.afgm6h0YiF6FDDAtX5tpBduZee8zyw6', 1, NOW(6), NOW(6)),
 (6, 'sofia.platos@epicentro.test',  '$2a$10$q7CTmUaOVYxZ0ytgL7J3L.afgm6h0YiF6FDDAtX5tpBduZee8zyw6', 1, NOW(6), NOW(6)),
 (7, 'marcos.caja@epicentro.test',   '$2a$10$q7CTmUaOVYxZ0ytgL7J3L.afgm6h0YiF6FDDAtX5tpBduZee8zyw6', 1, NOW(6), NOW(6)),
 (8, 'carla.caja@epicentro.test',    '$2a$10$q7CTmUaOVYxZ0ytgL7J3L.afgm6h0YiF6FDDAtX5tpBduZee8zyw6', 1, NOW(6), NOW(6));

INSERT INTO user_role (id, user_id, role, created_at, updated_at) VALUES
 (1, 1, 'ROLE_ADMIN',       NOW(6), NOW(6)),
 (2, 2, 'ROLE_RESPONSABLE', NOW(6), NOW(6)),
 (3, 3, 'ROLE_RESPONSABLE', NOW(6), NOW(6)),
 (4, 4, 'ROLE_EMPLEADO',    NOW(6), NOW(6)),
 (5, 5, 'ROLE_EMPLEADO',    NOW(6), NOW(6)),
 (6, 6, 'ROLE_EMPLEADO',    NOW(6), NOW(6)),
 (7, 7, 'ROLE_EMPLEADO',    NOW(6), NOW(6)),
 (8, 8, 'ROLE_EMPLEADO',    NOW(6), NOW(6));

-- ------------------------------------------------------------------ personal
-- Ids 1 y 2: responsables (Staff "puro"). 3 a 6: Parrilla Don Mario. 7: Dulces del Sur (cajera).
INSERT INTO staff (id, nombre, apellido, dni, email, fecha_nacimiento, fecha_ingreso, eliminado, user_id) VALUES
 (1, 'Mario',  'Gomez',    '20111222', 'mario.gomez@epicentro.test',     '1980-04-12', '2026-09-01', 0, 2),
 (2, 'Elena',  'Ruiz',     '22333444', 'elena.ruiz@epicentro.test',      '1985-08-30', '2026-09-01', 0, 3),
 (3, 'Lucia',  'Fernandez','30444555', 'lucia.cocina@epicentro.test',    '1990-02-14', '2022-03-01', 0, 4),
 (4, 'Pablo',  'Torres',   '31555666', 'pablo.cocina@epicentro.test',    '1992-11-03', '2024-01-15', 0, 5),
 (5, 'Sofia',  'Martinez', '35666777', 'sofia.platos@epicentro.test',    '1998-06-21', '2025-05-10', 0, 6),
 (6, 'Marcos', 'Diaz',     '33777888', 'marcos.caja@epicentro.test',     '1994-09-09', '2023-05-15', 0, 7),
 (7, 'Carla',  'Suarez',   '36888999', 'carla.caja@epicentro.test',      '1999-01-25', '2025-11-01', 0, 8);

INSERT INTO cocinero (id, rol_cocina) VALUES
 (3, 'COCINERO'),
 (4, 'COCINERO_AYUDANTE'),
 (5, 'LAVAPLATOS');

INSERT INTO cajero (id, turno) VALUES
 (6, 'MANANA'),
 (7, 'NOCHE');

-- ------------------------------------------------------------------ unidades de venta
-- Codigo: FT-NNNNNNN (Food Truck) o PD-NNNNNNN (Puesto Desarmable), 10 caracteres.
INSERT INTO unidad_venta (id, codigo, nombre_comercial, superficie, festival_id, responsable_id) VALUES
 (1, 'FT-1000001', 'Parrilla Don Mario', 12, 1, 1),
 (2, 'PD-2000001', 'Dulces del Sur',     20, 1, 2);

INSERT INTO food_track (id, patente, requiere_conexion_electrica) VALUES
 (1, 'AB123CD', 1);

INSERT INTO desarmable (id, cant_carpas, tiempo_montaje) VALUES
 (2, 3, 90);

UPDATE staff SET unidad_venta_id = 1 WHERE id IN (1, 3, 4, 5, 6);
UPDATE staff SET unidad_venta_id = 2 WHERE id IN (2, 7);

-- ------------------------------------------------------------------ platos
INSERT INTO plato (id, nombre, precio_venta, costo_produccion, unidad_venta_id) VALUES
 (1, 'Hamburguesa completa', 8500, 3500, 1),
 (2, 'Papas con cheddar',    4000, 1200, 1),
 (3, 'Gaseosa 500 ml',       2500,  800, 1),
 (4, 'Brownie con helado',   3500, 1100, 2),
 (5, 'Cafe con medialunas',  2200,  500, 2);

-- ------------------------------------------------------------------ pedidos con sus items
INSERT INTO pedido (id, fecha, festival_id, unidad_venta_id) VALUES
 (1, '2026-09-25', 1, 1),
 (2, '2026-09-26', 1, 1),
 (3, '2026-10-02', 1, 1),
 (4, '2026-09-27', 1, 2),
 (5, '2026-10-03', 1, 2);

INSERT INTO detalle_pedido (id, pedido_id, plato_id, cantidad) VALUES
 (1, 1, 1, 2),   -- 2 x 8500 = 17000
 (2, 1, 3, 2),   -- 2 x 2500 =  5000
 (3, 2, 1, 1),   --            8500
 (4, 2, 2, 1),   --            4000
 (5, 3, 1, 3),   -- 3 x 8500 = 25500
 (6, 3, 2, 2),   -- 2 x 4000 =  8000
 (7, 4, 4, 2),   -- 2 x 3500 =  7000
 (8, 5, 5, 4);   -- 4 x 2200 =  8800
-- Recaudacion esperada: Parrilla Don Mario = 68000, Dulces del Sur = 15800.
