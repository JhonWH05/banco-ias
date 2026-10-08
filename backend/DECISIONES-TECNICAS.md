# Decisiones técnicas – BancoIAS

## 1. Descripción de la solución

Para esta prueba desarrollé una aplicación que permite procesar solicitudes de uso de cupos preaprobados, validar si un cliente puede utilizar el monto solicitado y guardar el resultado de cada operación.

Uno de los puntos más importantes fue controlar el cupo disponible, especialmente cuando se reciben varias solicitudes al mismo tiempo, y evitar que una misma solicitud descuente el saldo más de una vez.

## 2. Tecnologías utilizadas

Para el backend utilicé Java 17, Spring Boot 3.5.6, WebFlux, R2DBC y MySQL. Para el frontend trabajé con Angular.

Elegí WebFlux y R2DBC porque ya he trabajado con programación reactiva y me permiten mantener ese enfoque tanto en los servicios como en el acceso a la base de datos.

También utilicé Gradle para las dependencias, JUnit y Mockito para las pruebas, y Swagger con OpenAPI YAML para documentar los endpoints.

## 3. Manejo del cupo y concurrencia

Para evitar que dos solicitudes consuman el mismo saldo, implementé una actualización condicional directamente en MySQL.

```sql
UPDATE preaprobado
SET monto_disponible = monto_disponible - :monto
WHERE id_preaprobado = :idPreaprobado
  AND id_cliente = :idCliente
  AND estado = 'ACTIVE'
  AND monto_disponible >= :monto;
```

De esta manera, el descuento solamente se realiza si el preaprobado está activo y tiene cupo suficiente.

También utilicé `@Transactional` para manejar el descuento y el registro de la solicitud dentro de una misma transacción.

Consideré consultar primero el saldo y después actualizarlo, pero preferí la actualización condicional para reducir los problemas que podrían presentarse con solicitudes simultáneas.

## 4. Idempotencia

Implementé el control de solicitudes repetidas utilizando `referenciaSolicitud`.

Si llega una solicitud con una referencia que ya fue procesada y los mismos datos, se devuelve el resultado anterior sin descontar nuevamente el cupo.

Si la referencia existe, pero los datos son diferentes, se responde con un error `409 Conflict`.

Adicionalmente, definí una restricción `UNIQUE` en la base de datos para evitar registros duplicados.

## 5. Validaciones y manejo de errores

Antes de autorizar una solicitud, valido que el preaprobado exista, pertenezca al cliente, esté activo y tenga saldo suficiente.

Las solicitudes que no cumplen las reglas de negocio se guardan como `REJECTED`, indicando el motivo. Esto permite consultar posteriormente qué ocurrió con cada operación.

Para los errores de la API implementé un `GlobalExceptionHandler`, diferenciando errores funcionales y errores del sistema.

## 6. Base de datos e historial

Utilicé dos tablas principales: `preaprobado` y `solicitud_uso`.

La primera almacena los cupos disponibles y la segunda registra las solicitudes procesadas.

Decidí guardar también las solicitudes rechazadas, incluso cuando el preaprobado no existe, para conservar el historial de los intentos de uso.

La API permite consultar una solicitud por referencia y obtener las últimas 20 solicitudes registradas.

## 7. Pruebas realizadas

Realicé pruebas unitarias con JUnit, Mockito y StepVerifier para validar diferentes escenarios, como solicitudes autorizadas, cupo insuficiente, preaprobados inactivos y referencias repetidas.

También probé los endpoints y la integración con Angular.

Para una versión más completa, agregaría pruebas de integración que ejecuten solicitudes simultáneas directamente contra MySQL, con el fin de validar la concurrencia en condiciones reales.

## 8. Consideraciones finales

Para esta prueba preferí mantener una solución sencilla, sin agregar componentes como RabbitMQ, ya que podía resolver los requerimientos con Spring Boot y MySQL.

Tampoco implementé autenticación porque no hacía parte del alcance trabajado.

Considero que la solución cumple con las funcionalidades principales solicitadas y deja una base organizada para agregar mejoras como seguridad, paginación y pruebas de carga.