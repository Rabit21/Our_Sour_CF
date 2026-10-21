# Backend Our Sour

Infraestructura académica con Spring Boot 3.5.16, Spring Cloud 2025.0.3 y Maven Wrapper 3.9.16. Compila con Java 21 para bytecode Java 17. Android funciona de forma independiente en esta evaluación.

## Compilar

Desde esta carpeta, ejecutar `mvnw.cmd package` en Windows o `./mvnw package` en Linux/macOS. La primera ejecución descarga Maven y dependencias.

## Configuración local

Se utiliza MariaDB 10.4.32, compatible con el protocolo MySQL. Crear la base exclusiva our_sour con `create-databases.sql` y configurar las variables de entorno MYSQL_HOST, MYSQL_PORT, MYSQL_USER y MYSQL_PASSWORD en las terminales que ejecuten los servicios. `.env.example` documenta los nombres; Spring no carga automáticamente ese archivo. No guardar contraseñas en archivos versionados.

Todos los servicios escuchan en 127.0.0.1 para pruebas locales:

| Servicio | Puerto | Ejecución desde backend |
|---|---|---|
| eureka-server | 8761 | `java -jar eureka-server/target/eureka-server-0.1.0-SNAPSHOT.jar` |
| api-gateway | 8080 | `java -jar api-gateway/target/api-gateway-0.1.0-SNAPSHOT.jar` |
| ms-auth | 8081 | `java -jar ms-auth/target/ms-auth-0.1.0-SNAPSHOT.jar` |
| ms-catalogo | 8082 | `java -jar ms-catalogo/target/ms-catalogo-0.1.0-SNAPSHOT.jar` |
| ms-pedidos | 8083 | `java -jar ms-pedidos/target/ms-pedidos-0.1.0-SNAPSHOT.jar` |

Iniciar Eureka primero y luego los otros procesos en terminales separadas. El Gateway resuelve los servicios registrados en Eureka. Cada proceso publica `/actuator/health`. Las rutas iniciales `/api/auth/estado`, `/api/catalogo/estado` y `/api/pedidos/estado` consultan `SELECT 1` en la base our_sour.

## Estado comprobado

Los cinco módulos compilaron y generaron sus JAR. La infraestructura inicial no incluía entidades de negocio; el catálogo se describe en la sección siguiente. La autenticación todavía no está implementada. El arranque, registro en Eureka y las tres rutas de estado se verificaron con MariaDB 10.4.32 en el puerto 3308. Los tres servicios utilizan la base exclusiva our_sour, con tablas separadas por prefijos de servicio. No se presenta la compilación como prueba de funcionamiento de la base de datos.

## Catálogo persistente

`ms-catalogo` almacena productos e ingredientes en `our_sour.catalogo_productos` y `our_sour.catalogo_ingredientes`. Al arrancar con tablas vacías incorpora datos sintéticos. El servidor no reemplaza registros existentes al reiniciar.

Rutas a través de `http://127.0.0.1:8080`:

| Método | Ruta | Resultado |
|---|---|---|
| GET | /api/catalogo/productos | Lista ordenada por ID |
| GET | /api/catalogo/productos/{id} | Detalle o 404 |
| POST | /api/catalogo/productos | Alta 201 y cabecera Location |
| PUT | /api/catalogo/productos/{id} | Actualización o 404 |
| DELETE | /api/catalogo/productos/{id} | Eliminación 204 o 404 |

Las mismas operaciones existen para `/api/catalogo/ingredientes`. Producto recibe `nombre`, `descripcion`, `precio` y `stock`; ingrediente recibe `nombre` y `categoria` (`OUR_SOUR`, `DESTILADO`, `FRUTA`, `MIXER` u `OTRO`). Los precios son pesos enteros de 1 a 10.000.000, stock de 0 a 9999 y nombres de 2 a 80 caracteres. Datos inválidos devuelven 400 y nombres duplicados 409. El ID se asigna en la base; no se toma del cuerpo de entrada.

Pruebas: `mvnw.cmd -pl ms-catalogo -am test` ejecuta las pruebas unitarias del servicio, entre ellas las de `ProductoService` e `IngredienteService`, con Mockito, sin base de datos. El recorrido REST completo (alta, duplicado, validación, 404, actualización, reinicio y eliminación) se comprobó contra MariaDB 10.4.32 a través del Gateway.

## Recetas y recomendaciones

Las recetas se guardan en `catalogo_recetas` y su dosificación en `catalogo_receta_ingredientes` (receta, ingrediente, cantidad y unidad; un ingrediente no se repite en la misma receta). Cada receta tiene un producto Our Sour asociado. Al arrancar se crean, si no existen por nombre, los ingredientes, productos y recetas de demostración (Sour clásico, Limonada fresca, Limonada de jengibre y Sour express), iguales a los de la app; los identificadores pueden diferir de los de Android porque los asigna la base. No se modifican precios ni stock existentes.

| Método | Ruta | Resultado |
|---|---|---|
| GET | /api/catalogo/recetas | Lista con ingredientes y cantidades |
| GET | /api/catalogo/recetas/{id} | Detalle o 404 |
| GET | /api/catalogo/recetas/recomendaciones?ingredientes=1,2 | Recomendaciones ordenadas |
| POST | /api/catalogo/recetas | Alta 201 |
| PUT | /api/catalogo/recetas/{id} | Reemplaza nombre, preparación y dosificación |
| DELETE | /api/catalogo/recetas/{id} | Eliminación 204 o 404 |

Cuerpo de alta o edición: `{"nombre": "...", "preparacion": "...", "productoId": 1, "ingredientes": [{"ingredienteId": 1, "cantidad": 30, "unidad": "ml"}]}`. Se exigen entre 1 y 20 ingredientes existentes y sin repetir, cantidad mayor que cero con hasta dos decimales y unidad de hasta 20 caracteres; si no se cumple, la respuesta es 400. Un nombre de receta repetido devuelve 409. El producto debe existir (si no, 400). Un ingrediente o producto usado por una receta no se puede eliminar (409).

Cada recomendación incluye `recetaId`, `nombre`, `disponibles`, `total`, `proporcion` y `faltantes` (con cantidad y unidad) y el `productoId` sugerido. Se descartan recetas sin coincidencias; el orden es por proporción descendente y, en empate, por ID de receta. Se aceptan hasta 50 ingredientes por consulta; un valor no numérico devuelve 400.

Pruebas: `MotorRecomendacionTest` reproduce los casos de Android (selección vacía, coincidencia total, parciales con empate e ingrediente desconocido) y `RecetaServiceTest` comprueba ingrediente repetido, ingrediente o producto inexistente, exceso de selección y receta inexistente; `IngredienteServiceTest` comprueba que la categoría se guarda.

Estas rutas todavía no tienen autenticación y solo escuchan en loopback. La seguridad se incorporará antes de la entrega completa.
