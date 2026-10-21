# Our Sour

Proyecto académico de la asignatura DSY1105 (Desarrollo de Aplicaciones Móviles), basado en el caso Our Sour: a partir de los ingredientes que una persona tiene en casa, la app recomienda recetas de cócteles y mocktails, muestra lo que falta y permite agregar productos Our Sour a un carro para simular la compra. Todos los datos son ficticios.

## Contenido del repositorio

- `android/`: aplicación Android en Kotlin con Jetpack Compose (Gradle Wrapper).
- `backend/`: microservicios Spring Boot: Eureka, API Gateway, `ms-auth`, `ms-catalogo` y `ms-pedidos` (Maven Wrapper). Detalle en `backend/README.md`.
- `docs/`: modelo de datos (`modelo-datos.md`), resultados de pruebas (`pruebas.md`) y códigos QR de prueba (`qr/`).

## Alcance de esta entrega

En esta evaluación la app funciona sin conexión al backend, como pide la asignatura: usa datos locales, guarda los ingredientes frecuentes con Room y lee códigos QR con la cámara. El backend se entrega por separado y ya implementa catálogo, recetas, recomendaciones, autenticación con JWT y roles, carro y pedidos. Conectar la app con el backend mediante Retrofit corresponde a la siguiente unidad.

## Aplicación Android

### Ejecutar

Abrir `android/` en Android Studio con JDK 21 y Android SDK 36. La app admite Android 8.0 (API 26) o posterior. Desde `android/`, en Windows:

```powershell
.\gradlew.bat assembleDebug testDebugUnitTest
.\gradlew.bat installDebug
```

Para instalar se necesita un dispositivo con depuración USB autorizada. El APK de depuración queda en `app/build/outputs/apk/debug/app-debug.apk`.

Versiones: Gradle 8.13, Android Gradle Plugin 8.13.2, Kotlin 2.0.21, Compose BOM 2024.09.00 con Material 3.

### Organización del código

Paquete `cl.oursour`:

| Paquete | Responsabilidad |
|---|---|
| `model` | Datos de la app: ingredientes y categorías, recetas, productos, carro, dirección, pedidos, usuario demo y formato QR |
| `dao` | Consultas Room de ingredientes frecuentes |
| `database` | Base Room `OurSourDatabase` |
| `repository` | Datos ficticios de recetas y productos, regla de recomendación y acceso a Room |
| `viewmodel` | Estado de sesión, selección de ingredientes, frecuentes, catálogo, carro y pedidos (`StateFlow`) |
| `view` | Pantallas Compose |
| `navigation` | Rutas y navegación con Navigation Compose |
| `ui.theme` | Colores, tipografía y tema Material 3 |

Dependencias principales: Navigation Compose 2.9.5 (navegación entre pantallas), Lifecycle ViewModel Compose 2.10.0 (ViewModel en Compose), Room 2.8.3 con KSP (base de datos local) y ZXing Android Embedded 4.3.0 (lectura de QR con la cámara, https://github.com/journeyapps/zxing-android-embedded).

### Funcionalidades

- **Acceso de demostración**: entrar como cliente o administrador ficticios, o crear un perfil temporal de cliente con nombre y correo. No se guardan contraseñas ni se autentican cuentas reales. Cerrar sesión vuelve al acceso y borra el historial de navegación.
- **Ingredientes**: selección agrupada por categoría (productos Our Sour, destilados y licores, frutas y hierbas, jugos y mixers, otros).
- **Ingredientes frecuentes (Room)**: guardar y quitar ingredientes frecuentes, sin duplicados. La lista se conserva al cerrar la app y se comparte entre perfiles demo del dispositivo.
- **Recetas recomendadas**: ordenadas por proporción de ingredientes disponibles, de mayor a menor; los empates se resuelven por identificador de receta. No se usa aprendizaje automático. Sin selección, la app pide elegir ingredientes.
- **Detalle de receta**: cantidades para una porción, preparación, ingredientes disponibles y faltantes, botones para agregar al carro los productos que cubren lo que falta y el producto Our Sour sugerido para la receta. Si un producto no tiene stock o fue eliminado, se indica.
- **Catálogo y carro**: productos con precio en pesos y stock; aumentar, reducir y eliminar líneas, con cantidades de 1 a 99 sin superar el stock.
- **Compra simulada**: dirección con destinatario, calle y comuna (Santiago, Providencia o Maipú); despacho de $3.500, gratis desde $30.000 en productos; resumen con pago simulado aprobado o rechazado. No se piden datos de tarjeta ni se hacen cobros.
- **Pedidos**: confirmación, lista y detalle con precios históricos y dirección. El carro, la dirección y los pedidos duran mientras la sesión está abierta.
- **Lector QR**: lee códigos con formato `OURSOUR:PRODUCTO:1` y abre el producto. Informa formato inválido, producto inexistente y permiso de cámara rechazado, con acceso a ajustes. Los códigos de `docs/qr/` sirven para probarlo. No se guardan fotos.
- **Administración**: el perfil administrador crea, edita y elimina productos con validación de nombre, descripción, precio y stock; el borrado pide confirmación. El cliente no ve estas opciones. La pantalla de métricas muestra el conteo real del catálogo local y datos de búsqueda identificados como ejemplos ficticios.

### Pruebas

15 pruebas unitarias, 6 pruebas instrumentadas en Samsung Galaxy Tab S8 y Android Lint sin errores. El detalle está en `docs/pruebas.md`.

## Backend

Cinco módulos con Spring Boot 3.5.16 y Spring Cloud 2025.0.3 sobre una base MariaDB `our_sour`, con tablas separadas por prefijo de servicio (`auth_`, `catalogo_`, `pedidos_`) y sin claves foráneas entre servicios:

- **Eureka** (8761): registro y descubrimiento de servicios.
- **API Gateway** (8080): rutas `/api/auth`, `/api/catalogo` y `/api/pedidos`, y primera validación del token y del rol.
- **ms-auth** (8081): registro, login con BCrypt y emisión de JWT con rol CLIENTE o ADMIN.
- **ms-catalogo** (8082): ingredientes con categoría, productos, recetas con dosificación y producto asociado, y motor de recomendación con la misma regla de la app.
- **ms-pedidos** (8083): carro por usuario, despacho, checkout con pago simulado y pedidos con precios históricos. Precio y stock se consultan al catálogo por REST.

La configuración, las variables de entorno, los endpoints y las reglas de acceso están en `backend/README.md`. Hay 31 pruebas unitarias y un recorrido REST contra MariaDB descrito en `docs/pruebas.md`.

## Limitaciones conocidas

- La app no consume el backend todavía: el acceso es una simulación local y el catálogo, el carro y los pedidos viven en memoria durante la sesión.
- Las métricas de administración usan datos de ejemplo; el registro de búsquedas reales no está implementado.
- No se genera APK firmado en esta entrega.
- En el backend, descontar stock y guardar el pedido ocurren en servicios distintos, sin transacción distribuida.
