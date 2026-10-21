# Our Sour

Proyecto académico DSY1105 para consultar recetas a partir de ingredientes y simular la compra de productos.

## Estado actual

Android contiene una pantalla inicial con Material 3, colores crema, verde oscuro y cítrico, texto adaptable y desplazamiento para pantallas pequeñas. Esta versión incluye acceso demo y navegación; todavía no incluye recetas, persistencia, compras ni lector QR. El backend aún no está implementado.

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

La configuración utiliza Gradle 8.13, Android Gradle Plugin 8.13.2, Kotlin 2.0.21 y Compose BOM 2024.09.00. Compilación assembleDebug completada correctamente el 21/10/2026; instalación y apertura verificadas en Samsung Galaxy Tab S8 (SM-X700). La pantalla inicial se revisó mediante captura del dispositivo. Todavía no hay pruebas unitarias de lógica porque esta versión solo contiene la pantalla inicial.

## Alcance académico

La aplicación móvil de esta evaluación funciona sin conexión al backend. Las funciones posteriores utilizarán datos ficticios y compras simuladas; no se procesarán pagos reales. Los servicios backend se entregarán por separado.

## Acceso de demostración

La pantalla de entrada permite elegir cliente o administrador ficticios y crear un perfil temporal de cliente con nombre y correo de ejemplo. No se guardan contraseñas ni se autentican cuentas reales. Cerrar sesión devuelve al acceso y elimina el destino anterior de la navegación.

Para comprobarlo, entra con cada rol y cierra sesión; prueba crear un perfil vacío y luego uno con nombre y correo válido. Las dos pruebas instrumentadas de acceso y navegación pasaron en Galaxy Tab S8 con `gradlew.bat connectedDebugAndroidTest`.
