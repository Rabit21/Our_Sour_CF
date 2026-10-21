# Backend Our Sour

Infraestructura académica con Spring Boot 3.5.16, Spring Cloud 2025.0.3 y Maven Wrapper 3.9.16. Compila con Java 21 para bytecode Java 17. Android funciona de forma independiente en esta evaluación.

## Compilar

Desde esta carpeta, ejecutar `mvnw.cmd package` en Windows o `./mvnw package` en Linux/macOS. La primera ejecución descarga Maven y dependencias.

## Configuración local

Se utiliza MariaDB 10.4.32, compatible con el protocolo MySQL. Crear la base exclusiva our_sour con `create-databases.sql` y configurar las variables de entorno MYSQL_HOST, MYSQL_PORT, MYSQL_USER, MYSQL_PASSWORD y JWT_SECRET (al menos 32 caracteres, el mismo en Gateway y servicios) en las terminales que ejecuten los servicios. ADMIN_EMAIL y ADMIN_PASSWORD son opcionales y crean el administrador ficticio al arrancar ms-auth. `.env.example` documenta los nombres; Spring no carga automáticamente ese archivo. No guardar contraseñas en archivos versionados.

Todos los servicios escuchan en 127.0.0.1 para pruebas locales:

| Servicio | Puerto | Ejecución desde backend |
|---|---|---|
| eureka-server | 8761 | `java -jar eureka-server/target/eureka-server-0.1.0-SNAPSHOT.jar` |
| api-gateway | 8080 | `java -jar api-gateway/target/api-gateway-0.1.0-SNAPSHOT.jar` |
| ms-auth | 8081 | `java -jar ms-auth/target/ms-auth-0.1.0-SNAPSHOT.jar` |
| ms-catalogo | 8082 | `java -jar ms-catalogo/target/ms-catalogo-0.1.0-SNAPSHOT.jar` |
| ms-pedidos | 8083 | `java -jar ms-pedidos/target/ms-pedidos-0.1.0-SNAPSHOT.jar` |

Iniciar Eureka primero y luego los otros procesos en terminales separadas. El Gateway resuelve los servicios registrados en Eureka. Cada proceso publica `/actuator/health`. Las rutas iniciales `/api/auth/estado`, `/api/catalogo/estado` y `/api/pedidos/estado` consultan `SELECT 1` en la base our_sour.

## Ejemplo de uso

Con los servicios en marcha, desde PowerShell:

```powershell
$login = Invoke-RestMethod -Method Post -Uri http://127.0.0.1:8080/api/auth/login -ContentType 'application/json' -Body '{"email":"<correo>","password":"<clave>"}'
$h = @{ Authorization = "Bearer $($login.token)" }
Invoke-RestMethod 'http://127.0.0.1:8080/api/catalogo/recetas/recomendaciones?ingredientes=1,2'
Invoke-RestMethod -Method Put -Uri http://127.0.0.1:8080/api/pedidos/carro/lineas/1 -Headers $h -ContentType 'application/json' -Body '{"cantidad":2}'
```

Primero se registra un cliente con `POST /api/auth/registro`. Los identificadores de productos e ingredientes se consultan con `GET /api/catalogo/productos` y `GET /api/catalogo/ingredientes`.

## Estado comprobado

`mvnw.cmd clean package` compila los cinco módulos y ejecuta 31 pruebas unitarias. El arranque, el registro en Eureka, las rutas de estado y todos los endpoints de las secciones siguientes se verificaron con MariaDB 10.4.32 en el puerto 3308 a través del Gateway; el detalle está en `../docs/pruebas.md`. Los tres servicios de negocio usan la base exclusiva our_sour, con tablas separadas por prefijos de servicio (`auth_`, `catalogo_`, `pedidos_`).

## Catálogo persistente

`ms-catalogo` almacena productos e ingredientes en `our_sour.catalogo_productos` y `our_sour.catalogo_ingredientes`. Al arrancar crea, si no existen por nombre, los datos sintéticos de demostración. No reemplaza registros existentes ni cambia precios o stock al reiniciar.

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

La lectura (GET) del catálogo es pública; crear, editar y eliminar exige un token con rol ADMIN (ver Autenticación y roles).

## Autenticación y roles

`ms-auth` guarda usuarios en `auth_usuarios` con la contraseña cifrada con BCrypt y emite tokens JWT firmados con HS256 (jjwt 0.12.6, una hora de duración). El token lleva el id del usuario como `sub` y los claims `email`, `nombre` y `rol`.

| Método | Ruta | Acceso | Resultado |
|---|---|---|---|
| POST | /api/auth/registro | Público | `{"nombre", "email", "password"}` → 201 con el usuario (siempre rol CLIENTE); 400 datos inválidos; 409 correo repetido |
| POST | /api/auth/login | Público | `{"email", "password"}` → 200 `{"token", "tipo": "Bearer", "expiraEnSegundos", "usuario"}`; 401 credenciales inválidas |
| GET | /api/auth/perfil | Token | Datos del usuario del token; 401 sin token o con token inválido |

El token se envía en la cabecera `Authorization: Bearer <token>`. El registro no permite elegir rol: un campo `rol` en la solicitud se ignora. El administrador ficticio solo se crea si existen ADMIN_EMAIL y ADMIN_PASSWORD en el entorno local; no hay contraseñas escritas en el código.

Reglas aplicadas en el Gateway y, de nuevo, en cada servicio (Spring Security como servidor de recursos con el mismo secreto):

| Ruta | Regla |
|---|---|
| /api/auth/registro, /api/auth/login, rutas `/estado` | Públicas |
| GET /api/catalogo/** | Pública |
| POST, PUT, DELETE /api/catalogo/** | Rol ADMIN (sin token 401, cliente 403) |
| /api/pedidos/** | Rol CLIENTE o ADMIN |
| PATCH /api/pedidos/{id}/estado | Rol ADMIN |
| POST /api/catalogo/productos/stock/descontar | Rol SERVICIO (solo el token interno de ms-pedidos) |

Pruebas: `AuthServiceTest` (hash BCrypt y rol CLIENTE en registro, correo repetido, contraseña incorrecta y correo inexistente con 401, login correcto) y `JwtServiceTest` (claims del token, rechazo con otro secreto y secreto corto). El recorrido por el Gateway contra MariaDB comprobó registro 201, duplicado 409, datos inválidos 400, login incorrecto 401, perfil sin token 401 y con token 200, catálogo público, escritura de catálogo 401 sin token, 403 como cliente y 201/204 como administrador, y que ms-catalogo rechaza directamente (sin Gateway) solicitudes sin token o de cliente.

## Carro y pedidos

`ms-pedidos` guarda un carro por usuario (`pedidos_carros`, `pedidos_lineas_carro`) y los pedidos confirmados (`pedidos_pedidos`, `pedidos_lineas_pedido`). El usuario siempre se toma del token, nunca de la URL ni del cuerpo. No hay tabla de productos ni claves foráneas hacia otros servicios: el carro guarda solo id de producto y cantidad, y el precio y el stock se consultan a `ms-catalogo` por REST (Eureka resuelve `http://ms-catalogo`).

| Método | Ruta | Resultado |
|---|---|---|
| GET | /api/pedidos/carro | Líneas con precio actual, subtotal, despacho y total |
| PUT | /api/pedidos/carro/lineas/{productoId} | `{"cantidad": 1..99}`; 400 cantidad inválida, 404 producto inexistente, 409 supera el stock |
| DELETE | /api/pedidos/carro/lineas/{productoId} | Quita la línea |
| DELETE | /api/pedidos/carro | Vacía el carro (204) |
| POST | /api/pedidos/checkout | `{"destinatario", "calle", "comuna", "pagoAprobado"}` → 201 con el pedido; 400 carro vacío, dirección o comuna inválida; 402 pago simulado rechazado (el carro se conserva); 409 sin stock o producto eliminado |
| GET | /api/pedidos | Pedidos propios (el administrador ve todos) |
| GET | /api/pedidos/{id} | Detalle; un pedido ajeno responde 404 |
| PATCH | /api/pedidos/{id}/estado | `{"estado": "SOLICITADO" \| "EN_PREPARACION" \| "ENTREGADO"}`, solo ADMIN |

Reglas: despacho de $3.500, gratis desde $30.000 en productos, para Santiago, Providencia y Maipú (igual que la app). El servidor recalcula precios, subtotal, despacho y total con el catálogo; cualquier monto enviado por el cliente se ignora. El pago es una simulación: `pagoAprobado` indica el resultado y no se procesan tarjetas. Al aprobarse, `ms-pedidos` descuenta el stock con un token interno de rol SERVICIO (60 segundos) en una sola transacción del catálogo, que bloquea las filas y revierte todo si un producto no alcanza; luego guarda el pedido con nombre y precio históricos en estado SOLICITADO y vacía el carro. Si el catálogo no responde, las operaciones devuelven 503.

Limitación: el descuento de stock y el guardado del pedido ocurren en dos servicios distintos; si el guardado fallara después del descuento, el stock no se restituye automáticamente.

Pruebas: `PedidoServiceTest` (siete casos: regla de despacho, carro vacío y comuna inválida, pago rechazado que conserva el carro, pago aprobado con precios del servidor, descuento y vaciado, stock insuficiente, pedido ajeno y cantidad mayor al stock) y dos casos nuevos de `ProductoServiceTest` para el descuento. El recorrido por el Gateway contra MariaDB (30 comprobaciones) incluyó carro sin token 401, cantidades inválidas, 404 y 409, despacho cobrado y gratis, comuna inválida 400, pago rechazado 402 con carro intacto, pedido 201 con total del servidor aunque el cliente envió otro, stock descontado, carro vacío tras comprar, pedido ajeno 404, cambio de estado 403 para cliente y 200 para administrador, stock modificado antes de pagar 409, descuento directo de stock rechazado para cliente y administrador, catálogo detenido 503 y pedidos conservados tras reiniciar el servicio.
