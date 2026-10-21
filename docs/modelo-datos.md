# Modelo de datos de Our Sour

## Separación por servicio

Los tres servicios usan una base exclusiva MariaDB llamada our_sour. Cada servicio mantiene la propiedad de sus tablas mediante prefijos auth_, catalogo_ y pedidos_. Eureka y Gateway no almacenan datos de negocio. Los identificadores de usuario y producto que cruzan servicios se guardan como valores, sin claves foráneas entre tablas de distintos servicios.

| Servicio | Esquema | Responsabilidad |
|---|---|---|
| ms-auth | our_sour (auth_*) | Usuarios, credenciales cifradas con hash y roles |
| ms-catalogo | our_sour (catalogo_*) | Ingredientes, productos, recetas y dosificaciones |
| ms-pedidos | our_sour (pedidos_*) | Carro, dirección y pedidos con valores históricos |

## Diseño lógico

Este es el diseño de las entidades de negocio. Por ahora existen las tablas `catalogo_productos` y `catalogo_ingredientes`, creadas por JPA en our_sour; el resto se agregará al implementar y verificar cada conjunto.

```mermaid
erDiagram
    USUARIO {
        long id PK
        string nombre
        string email UK
        string password_hash
        string rol
    }
    INGREDIENTE {
        long id PK
        string nombre UK
    }
    PRODUCTO {
        long id PK
        string nombre
        string descripcion
        decimal precio
        int stock
    }
    RECETA {
        long id PK
        string nombre
        string preparacion
    }
    RECETA_INGREDIENTE {
        long id PK
        long receta_id FK
        long ingrediente_id FK
        decimal cantidad
        string unidad
    }
    CARRO {
        long id PK
        long usuario_id UK
    }
    LINEA_CARRO {
        long id PK
        long carro_id FK
        long producto_id
        int cantidad
    }
    PEDIDO {
        long id PK
        long usuario_id
        string destinatario
        string calle
        string comuna
        decimal subtotal
        decimal despacho
        decimal total
        string estado
    }
    LINEA_PEDIDO {
        long id PK
        long pedido_id FK
        long producto_id
        string nombre_producto
        decimal precio_unitario
        int cantidad
    }
    RECETA ||--|{ RECETA_INGREDIENTE : requiere
    INGREDIENTE ||--o{ RECETA_INGREDIENTE : integra
    CARRO ||--o{ LINEA_CARRO : contiene
    PEDIDO ||--|{ LINEA_PEDIDO : conserva
```

La dosificación pertenece a la relación entre receta e ingrediente. El pedido conserva nombre y precio histórico; editar un producto no debe modificar una compra anterior. El servidor calcula subtotal, despacho y total. El identificador del propietario se obtiene del usuario autenticado, no de un campo confiado al cliente.

## Almacenamiento móvil de esta evaluación

Room usa la tabla `frequent_ingredients` con `ingredientId` como clave primaria. El DAO ignora inserciones repetidas y permite observar y eliminar los frecuentes. Los perfiles, carro, catálogo editable y pedidos móviles son estados de demostración en memoria; no están conectados a MySQL.
