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

Los cinco módulos compilaron y generaron sus JAR. Esta versión de infraestructura todavía no tiene pruebas unitarias de negocio, entidades de negocio ni autenticación implementada. El arranque, registro en Eureka y las tres rutas de estado se verificaron con MariaDB 10.4.32 en el puerto 3308. Los tres servicios utilizan la base exclusiva our_sour, con tablas separadas por prefijos de servicio. No se presenta la compilación como prueba de funcionamiento de la base de datos.
