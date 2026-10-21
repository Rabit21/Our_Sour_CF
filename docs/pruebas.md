# Pruebas de la segunda evaluación

Resultados obtenidos el 21/10/2026 con el código de la rama `main`. Las pruebas automáticas se pueden repetir con los comandos indicados; las comprobaciones REST requieren MariaDB y los cinco servicios en ejecución.

## Android

Desde `android/`:

```powershell
.\gradlew.bat clean assembleDebug testDebugUnitTest lintDebug
.\gradlew.bat connectedDebugAndroidTest   # requiere dispositivo con depuración USB autorizada
```

| Tipo | Clases | Resultado |
|---|---|---|
| Unitarias (JVM) | `RecommendationTest`, `RecipeProductTest`, `CartTest`, `CheckoutTest`, `AdminTest`, `ProductQrTest` | 15 aprobadas |
| Instrumentadas (Samsung Galaxy Tab S8, SM-X700) | `SessionFlowTest`, `FrequentPersistenceTest`, `RecipeToCartFlowTest`, `PurchaseFlowTest`, `AdminFlowTest` | 6 aprobadas |
| Lint | `lintDebug` | Sin errores |

Qué cubren:

- Acceso demo con ambos roles, cierre de sesión y perfil de demostración con validación.
- Recomendaciones: selección vacía, coincidencia total, coincidencias parciales con desempate por identificador e ingrediente desconocido.
- Receta → producto → carro: productos para los faltantes, ingredientes que no se venden, producto eliminado, categorías, y el recorrido en pantalla hasta el carro.
- Room: guardar sin duplicados, conservar tras cerrar y reabrir la base, y eliminar.
- Carro: cantidades válidas e inválidas, stock, subtotal y producto desconocido.
- Compra: validación de dirección, despacho, pago rechazado y aprobado, y pedido visible en Mis pedidos.
- Administración: rol requerido, validaciones y CRUD con identificadores estables.
- QR: formato válido, inválido, identificador inexistente, vacío y desbordado.

Pruebas manuales en tablet: lectura física de los códigos de `docs/qr/` (producto 1, formato inválido y producto inexistente) y rechazo del permiso de cámara con acceso a ajustes.

## Backend

Desde `backend/`:

```powershell
.\mvnw.cmd clean package
```

| Módulo | Clases | Resultado |
|---|---|---|
| ms-auth | `AuthServiceTest`, `JwtServiceTest` | 6 aprobadas |
| ms-catalogo | `ProductoServiceTest`, `IngredienteServiceTest`, `RecetaServiceTest`, `MotorRecomendacionTest` | 18 aprobadas |
| ms-pedidos | `PedidoServiceTest` | 7 aprobadas |
| eureka-server, api-gateway | — | Compilan; se comprueban con el recorrido REST |

Las pruebas unitarias usan Mockito y no necesitan base de datos.

### Recorrido REST contra MariaDB

Se ejecutó a través del Gateway (`http://127.0.0.1:8080`), con Eureka mostrando registrados API-GATEWAY, MS-AUTH, MS-CATALOGO y MS-PEDIDOS:

- Catálogo e ingredientes: alta, duplicado 409, validaciones 400, inexistentes 404, edición, eliminación y persistencia tras reiniciar.
- Recetas: recomendaciones vacías, totales y parciales con faltantes, valor no numérico 400, ingrediente repetido o inexistente 400, producto asociado obligatorio, ingrediente o producto en uso 409.
- Seguridad (21 comprobaciones): registro 201, correo repetido 409, datos inválidos 400, un rol enviado en el registro se ignora, login incorrecto 401, perfil sin token o con token falso 401, catálogo público, escritura de catálogo 401 sin token, 403 como cliente y permitida al administrador, y el servicio rechaza solicitudes directas sin pasar por el Gateway.
- Carro y pedidos (30 comprobaciones): carro sin token 401, cantidades inválidas, producto inexistente y stock superado, despacho cobrado y gratis, comuna inválida, pago rechazado 402 con carro intacto, pedido 201 con totales del servidor, stock descontado, carro vaciado, pedido ajeno 404, cambio de estado solo por el administrador, stock modificado antes de pagar 409 y descuento de stock bloqueado para usuarios.
- Disponibilidad: con `ms-catalogo` detenido, el carro responde 503; tras reiniciar `ms-pedidos`, los pedidos se conservan.

Los datos creados durante las pruebas se eliminaron al terminar.

## Limitaciones conocidas

- La app Android funciona sin backend en esta evaluación; la integración con Retrofit y JWT corresponde a la siguiente unidad.
- Las métricas de administración muestran datos de demostración; el registro de búsquedas reales no está implementado.
- Descontar stock y guardar el pedido ocurren en servicios distintos, sin transacción distribuida.
