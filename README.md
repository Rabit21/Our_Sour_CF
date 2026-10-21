# Our Sour

Proyecto académico DSY1105 para consultar recetas a partir de ingredientes y simular la compra de productos.

## Estado actual

Android contiene una pantalla inicial con Material 3, colores crema, verde oscuro y cítrico, texto adaptable y desplazamiento para pantallas pequeñas. Esta versión incluye acceso demo y navegación; incluye recomendaciones y detalles de recetas; incluye persistencia de frecuentes con Room; incluye compra y pedidos simulados; incluye lector QR por cámara. El backend se describe en la sección Infraestructura backend.

- `android/`: proyecto Android con Gradle Wrapper.
- `backend/`: carpeta destinada a los servicios de negocio, Eureka y Gateway.
- `docs/`: carpeta destinada a documentación de ejecución, datos y pruebas.

## Ejecutar Android

Abrir `android/` en Android Studio. Usar JDK 21 y disponer de Android SDK 36; la aplicación admite Android 8.0 (API 26) o posterior. Android Studio configura la ubicación local del SDK. Desde la carpeta `android/`, en Windows:

```powershell
.\gradlew.bat assembleDebug testDebugUnitTest
.\gradlew.bat installDebug
```

Para instalar se necesita un dispositivo conectado con depuración USB y autorización del equipo. El APK de depuración se genera en `app/build/outputs/apk/debug/app-debug.apk`.

La configuración utiliza Gradle 8.13, Android Gradle Plugin 8.13.2, Kotlin 2.0.21 y Compose BOM 2024.09.00. Compilación assembleDebug completada correctamente el 21/10/2026; instalación y apertura verificadas en Samsung Galaxy Tab S8 (SM-X700). La pantalla inicial se revisó mediante captura del dispositivo. Las pruebas unitarias de lógica se describen junto a cada función.

## Alcance académico

La aplicación móvil de esta evaluación funciona sin conexión al backend. Las funciones posteriores utilizarán datos ficticios y compras simuladas; no se procesarán pagos reales. Los servicios backend se entregarán por separado.

## Acceso de demostración

La pantalla de entrada permite elegir cliente o administrador ficticios y crear un perfil temporal de cliente con nombre y correo de ejemplo. No se guardan contraseñas ni se autentican cuentas reales. Cerrar sesión devuelve al acceso y elimina el destino anterior de la navegación.

Para comprobarlo, entra con cada rol y cierra sesión; prueba crear un perfil vacío y luego uno con nombre y correo válido. Las dos pruebas instrumentadas de acceso y navegación pasaron en Galaxy Tab S8 con `gradlew.bat connectedDebugAndroidTest`.

## Recetas por ingredientes

Desde Inicio, pulsa Buscar recetas, selecciona ingredientes y abre las recomendaciones. Se ordenan por proporción de ingredientes disponibles; los empates se resuelven por identificador. Cada detalle muestra cantidades para una porción, preparación y faltantes. Una selección vacía solicita elegir ingredientes. Los datos son sintéticos y no se usa aprendizaje automático.

Cuatro pruebas unitarias verifican selección vacía, coincidencia total, coincidencias parciales con orden estable e identificadores desconocidos. Compilación y pruebas completadas correctamente.

## Ingredientes frecuentes

En Tus ingredientes, Guardar agrega un ingrediente a frecuentes y Quitar lo elimina. Room conserva esta lista localmente entre cierres; no permite duplicados. La lista pertenece al dispositivo y se comparte entre perfiles demo. Las tres pruebas instrumentadas pasaron en Galaxy Tab S8, incluida inserción repetida, cierre/reapertura de la base y eliminación. Android Lint terminó sin errores.

## Catálogo y carro

Inicio → Explorar catálogo muestra productos ficticios con precio en CLP y stock. Desde el detalle se agregan al carro; allí se aumentan/reducen cantidades y se eliminan líneas. El subtotal se calcula con pesos enteros. Se rechazan cantidades fuera de 1 a 99 o superiores al stock. El carro es temporal y se limpia al cerrar sesión.

Compilación y seis pruebas unitarias aprobadas, incluidas operaciones del carro, subtotal, cantidades inválidas y productos sin stock.

## Compra y pedidos de demostración

Desde el carro, Continuar compra permite ingresar destinatario, calle y comuna ficticios. El despacho demo cuesta $3.500 y es gratis desde $30.000 en productos. El resumen permite simular pago rechazado (conserva el carro) o aprobado (genera pedido y descuenta stock local). No se solicitan datos de tarjeta ni se hacen cobros o envíos reales.

Mis pedidos muestra los pedidos de la sesión con sus cantidades, precios históricos y dirección. Al cerrar sesión se eliminan estos pedidos, la dirección y el carro; Room conserva únicamente los ingredientes frecuentes.

Ocho pruebas unitarias y cuatro instrumentadas aprobadas en Galaxy Tab S8, incluido el recorrido de compra con rechazo y aprobación. Compilación y Lint correctos.

## Administración local

El perfil administrador demo permite crear, editar y eliminar productos con validación de nombre, descripción, precio y stock. El cliente no ve estas opciones. El borrado pide confirmación y quita el producto del carro, manteniendo intactos los pedidos anteriores. Los cambios de catálogo duran durante la ejecución de la app.

Métricas de demostración distingue el conteo del catálogo local de las búsquedas y receta destacada ficticias. No se registran búsquedas reales todavía. Diez pruebas unitarias y cinco instrumentadas aprobadas, incluyendo CRUD de administración y navegación por rol.

## Lector QR por cámara

Inicio → Leer QR abre la cámara y resuelve códigos con formato `OURSOUR:PRODUCTO:1` contra el catálogo local. La app informa formato inválido, producto inexistente y permiso rechazado, con acceso a ajustes para recuperarlo. Una captura devuelve un solo resultado; se puede iniciar otra lectura al volver. No se guardan fotos.

Se utiliza ZXing Android Embedded 4.3.0: https://github.com/journeyapps/zxing-android-embedded. Los códigos de `docs/qr/` permiten probar producto 1, formato inválido y producto inexistente. La lectura válida se comprobó en Galaxy Tab S8; el usuario confirmó los casos inválido e inexistente. El permiso rechazado se verificó directamente en la tablet. Compilación, once pruebas unitarias y Lint correctos.

## Infraestructura backend

Los módulos Eureka, Gateway, autenticación, catálogo y pedidos compilan con Maven Wrapper. Usan una base exclusiva MariaDB llamada our_sour en el entorno local. Consulta backend/README.md para configurar las variables y ejecutar cada servicio. Además de la infraestructura y los endpoints de estado, ya están implementados el catálogo de productos e ingredientes y las recetas con recomendaciones; autenticación y pedidos backend siguen pendientes.

## Catálogo backend

Productos e ingredientes tienen CRUD persistente en our_sour, validación de campos, rechazo de duplicados y respuestas para recursos inexistentes. Las rutas están documentadas en backend/README.md. Seis pruebas unitarias cubren alta, duplicados, actualización e inexistentes en los servicios de catálogo; el CRUD se comprobó contra MariaDB a través del Gateway, incluida la persistencia tras reiniciar el servicio. La autenticación backend todavía está pendiente.

## Recetas y recomendaciones backend

`ms-catalogo` guarda recetas con su dosificación (cantidad y unidad por ingrediente para una porción) e incluye las tres recetas sintéticas de la app. `GET /api/catalogo/recetas/recomendaciones?ingredientes=1,2` aplica la misma regla que Android: proporción de ingredientes disponibles, orden descendente, empate por identificador y lista de faltantes. Una selección vacía o con ingredientes desconocidos devuelve una lista vacía. No se usa aprendizaje automático. La app Android todavía no consume esta API.

Ocho pruebas unitarias cubren el motor de recomendación (mismos casos que Android) y las validaciones de recetas. El recorrido REST se comprobó contra MariaDB a través del Gateway.
