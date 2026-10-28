# TP Grupal OO2 2026 - Epicentro Gourmet (Hito 2: Spring Boot)

**Número de grupo:** 02

## Integrantes

| Nombre y Apellido | Usuario GitHub |
|---|---|
| Juan Domenighetti | Juandome11 |
| Mariano Augusto Ribe | MarioArcade |
| Nahuel Portel | nahuelp22 |
| Richart Chaves | richarttttt |

## Diagrama de clases

[docs/Diagrama_Clases_Hito2.pdf](docs/Diagrama_Clases_Hito2.pdf) (iteración del diagrama del Hito 1: se suman `Costo`, `User` y `UserRole`; `Staff` deja de ser abstracta y `FoodTrack` suma `requiereConexionElectrica`).

## Tecnologías

Java 17 · Spring Boot 3.3 · Maven · MySQL · Spring Data JPA · Spring Security · Thymeleaf · OpenPDF · JavaMail.

## Pasos para correr la aplicación

1. **Requisitos:** JDK 17 o superior y MySQL en `localhost:3306`. No hace falta instalar Maven (se usa el wrapper `mvnw`).
2. **Base de datos:** no hace falta crearla a mano; la URL incluye `createDatabaseIfNotExist=true` y Hibernate crea las tablas (`ddl-auto=update`).
3. **Variables de entorno:**

   | Variable | Para qué | Ejemplo |
   |---|---|---|
   | `DB_URL` | conexión a MySQL | `jdbc:mysql://localhost:3306/bd_epicentro_gourmet_hito2?createDatabaseIfNotExist=true&serverTimezone=America/Argentina/Buenos_Aires` |
   | `DB_USERNAME` | usuario de MySQL | `root` |
   | `DB_PASSWORD` | contraseña de MySQL | `root` |
   | `MAIL_USERNAME` | cuenta que envía los mails | `micuenta@gmail.com` |
   | `MAIL_PASSWORD` | contraseña de **aplicación** de esa cuenta | (16 caracteres de Google) |
   | `MAIL_HOST` / `MAIL_PORT` | opcionales, por defecto `smtp.gmail.com` / `587` | |

   > No usar `USERNAME` como nombre de variable: en Windows ya existe y trae el usuario del sistema.
   > Sin credenciales de mail la aplicación funciona igual: el alta de empleados se completa y avisa que no pudo enviar el mail.

4. **Primer arranque** (crea las tablas):
   ```
   ./mvnw spring-boot:run          (Windows: .\mvnw.cmd spring-boot:run)
   ```
5. **Datos de prueba:** con la aplicación ya iniciada una vez, correr [scripts/datos_prueba.sql](scripts/datos_prueba.sql) en MySQL Workbench (o `mysql -uroot -p < scripts/datos_prueba.sql`). Se puede volver a correr cuando se quiera: borra y recarga todo.
6. Abrir http://localhost:8080

### Usuarios de prueba

| Rol | Usuario | Contraseña |
|---|---|---|
| Administrador | `admin` | `admin123` |
| Responsable (Food Truck "Parrilla Don Mario") | `resp.parrilla` | `resp123` |
| Responsable (Puesto "Dulces del Sur") | `resp.dulces` | `resp123` |
| Responsable (Food Truck "Parrilla del Otono", festival ya terminado) | `resp.otono` | `resp123` |
| Empleado (cocinero) | `lucia.cocina@epicentro.test` | `emp123` |
| Empleado (cajero) | `marcos.caja@epicentro.test` | `emp123` |
| Empleado con historial en 2 festivales (Lucía: Otoño y Primavera) | `lucia.cocina@epicentro.test` | `emp123` |

### Tests

`./mvnw test` corre los tests unitarios de la lógica de negocio: liquidación de haberes (incluye los dos ejemplos numéricos de la consigna), cálculo de canon y validaciones de carga.

## Casos de uso implementados y responsables

Base grupal inicial (Hito 2), subida directo a `main`:

| Caso de uso | Rol | Responsable | PR |
|---|---|---|---|
| Ver festivales en la página de inicio | público | Grupo | base inicial |
| Login / logout con roles | todos | Grupo | base inicial |
| Registro de Unidad de Venta con su Responsable (genera el usuario) | público | Grupo | base inicial |
| Alta de empleados con envío de mail (usuario + contraseña generada) | Responsable | Grupo | base inicial |
| Modificación y baja lógica de empleados, reactivación, filtros por varios atributos | Responsable | Grupo | base inicial |
| Alta y listado de platos | Responsable | Grupo | base inicial |
| Cálculo de canon (Food Truck / Puesto) | Responsable | Grupo | base inicial |
| Reporte de recaudación (`ReporteVentaDTO`) | Responsable | Grupo | base inicial |
| Ver festivales y unidades de venta de un festival | Administrador | Grupo | base inicial |
| Ver festival y unidad actual, historial de festivales/unidades en los que trabajó con el sueldo de cada uno, y descarga del recibo en PDF por festival | Empleado | Grupo | base inicial |
| Reasignación de un empleado que ya trabajó en un festival anterior (conserva su usuario y no se le envía otro mail) | Responsable | Grupo | base inicial |

Casos de uso individuales (2 por integrante, cada uno en su rama `CUFuncionalidadNombreApellido` + Pull Request):

| Caso de uso | Rol | Responsable | PR |
|---|---|---|---|
| Ranking de Unidades por recaudación de un festival | Administrador | MarioArcade | [PR #1](https://github.com/Juandome11/TPGrupalOO2Hito2Grupo02/pull/1) |
| Unidades con Mayor Canon (`ReporteMayoresCanonDTO`, parámetro `x`) | Administrador | MarioArcade | [PR #1](https://github.com/Juandome11/TPGrupalOO2Hito2Grupo02/pull/1) |
| Alta de festival con sus costos (valida que el período sea válido) | Administrador | MarioArcade | [PR #4](https://github.com/Juandome11/TPGrupalOO2Hito2Grupo02/pull/4) |
| Rentabilidad Neta de una unidad en un festival | Responsable | nahuelp22 | [PR #2](https://github.com/Juandome11/TPGrupalOO2Hito2Grupo02/pull/2) |
| Rentabilidad Neta entre dos fechas | Responsable | nahuelp22 | [PR #2](https://github.com/Juandome11/TPGrupalOO2Hito2Grupo02/pull/2) |
| Plato Estrella (`PlatoDTO`) con ranking de platos | Administrador | richarttttt | [PR #3](https://github.com/Juandome11/TPGrupalOO2Hito2Grupo02/pull/3) |
| Carga de pedidos con validación de fecha | Responsable | richarttttt | [PR #3](https://github.com/Juandome11/TPGrupalOO2Hito2Grupo02/pull/3) |
| _(pendiente)_ Cerrar Caja del cajero | Empleado (cajero) | Juandome11 | _a completar_ |

Validaciones de carga implementadas por los integrantes:

- Período del festival válido (la fecha de fin no puede ser anterior a la de inicio).
- Rango de fechas del reporte válido (desde no posterior a hasta, y dentro del festival).
- La fecha del pedido debe ser la del día en que se carga.
- El pedido debe tener al menos un ítem, y los platos tienen que pertenecer a la misma unidad.

Pendiente: Cerrar Caja y las validaciones de formato de DNI, formato de patente, plato sin nombre repetido y nombre comercial único por festival.
