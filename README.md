# BancoIAS - Backend

API REST desarrollada con Java 17, Spring Boot y MySQL para gestionar solicitudes de uso de cupos preaprobados.

El sistema permite validar el cupo disponible de un cliente, autorizar o rechazar solicitudes, evitar descuentos duplicados y consultar el historial de operaciones.

## 1. Tecnologías utilizadas

- Java 17
- Spring Boot 3.5.6
- Spring WebFlux
- Spring Data R2DBC
- MySQL
- Gradle
- JUnit 5, Mockito y Reactor Test
- OpenAPI 3.0 y Swagger UI

## 2. Funcionalidades

La aplicación permite:

- Procesar solicitudes de uso de cupos preaprobados.
- Validar la existencia del preaprobado y su relación con el cliente.
- Verificar que el preaprobado esté activo.
- Validar que el monto solicitado sea mayor que cero.
- Comprobar la disponibilidad del cupo antes de autorizar.
- Registrar solicitudes autorizadas y rechazadas.
- Evitar descuentos repetidos mediante una referencia única.
- Consultar una solicitud por su referencia.
- Consultar las últimas 20 solicitudes procesadas.
- Manejar errores funcionales y de sistema.

## 3. Requisitos previos

Para ejecutar el proyecto es necesario tener instalado:

- JDK 17.
- MySQL 8 o una versión compatible.
- Git, para clonar el repositorio.
- IntelliJ IDEA o cualquier editor compatible con Java y Gradle.

No es necesario instalar Gradle globalmente, ya que el proyecto incluye Gradle Wrapper.

## 4. Instalación

Clonar el repositorio:

```bash
git clone https://github.com/JhonWH05/banco-ias-backend.git
```

Ingresar al proyecto:

```bash
cd banco-ias-backend
```

Abrir el proyecto en IntelliJ IDEA y esperar a que Gradle descargue las dependencias.

## 5. Configuración de la base de datos

La aplicación utiliza una base de datos MySQL llamada `banco_ias`.

Dentro de `src/main/resources` se encuentran los archivos:

- `schema.sql`: crea la base de datos y las tablas necesarias.
- `data.sql`: inserta los registros iniciales de preaprobados.

### Pasos para preparar la base de datos

1. Iniciar el servicio de MySQL.
2. Abrir MySQL Workbench o cualquier cliente SQL.
3. Ejecutar primero `schema.sql`.
4. Ejecutar después `data.sql`.
5. Verificar que se hayan creado las tablas `preaprobado` y `solicitud_uso`.

Los scripts se ejecutan manualmente. La aplicación no reinicializa automáticamente la base de datos al iniciar.

### Datos iniciales

| Preaprobado | Cliente | Estado | Cupo disponible inicial |
|---|---|---|---|
| PRA-1001 | USR-10 | ACTIVE | $1.000.000 |
| PRA-1002 | USR-10 | BLOCKED | $800.000 |
| PRA-2001 | USR-20 | ACTIVE | $2.000.000 |

Los montos corresponden a los valores iniciales. Pueden cambiar después de procesar solicitudes autorizadas.

## 6. Variables de entorno

La conexión a MySQL se configura en `application.yml` mediante variables de entorno:

```yaml
spring:
  application:
    name: banco-ias-backend

  r2dbc:
    url: ${DB_URL:r2dbc:mysql://localhost:3306/banco_ias}
    username: ${DB_USER:root}
    password: ${DB_PASSWORD}
```

| Variable | Descripción | Valor predeterminado |
|---|---|---|
| DB_URL | URL de conexión a MySQL mediante R2DBC | r2dbc:mysql://localhost:3306/banco_ias |
| DB_USER | Usuario de MySQL | root |
| DB_PASSWORD | Contraseña de MySQL | Obligatoria |

### Ejemplo en PowerShell

```powershell
$env:DB_PASSWORD="tu_contraseña_mysql"
```

Si se utiliza IntelliJ IDEA, también se puede configurar `DB_PASSWORD` desde las variables de entorno de la configuración de ejecución.

No se incluyen credenciales reales dentro del repositorio.

# **Nota:**
La conexión a la base de datos también puede configurarse directamente en el archivo `application.yml`, indicando la URL, el usuario y la contraseña de MySQL correspondientes al equipo donde se ejecute la aplicación. Sin embargo, se recomienda utilizar variables de entorno para evitar exponer credenciales en el código fuente o en el repositorio.
## 7. Ejecución del backend

Desde la raíz del proyecto, ejecutar:

**Windows:**

```powershell
.\gradlew bootRun
```

**Linux o macOS:**

```bash
./gradlew bootRun
```

También se puede ejecutar la clase principal de Spring Boot desde IntelliJ IDEA.

La aplicación inicia en:

`http://localhost:8080`

Es importante que MySQL esté iniciado y que la variable `DB_PASSWORD` esté configurada antes de ejecutar la aplicación.

## 8. Endpoints disponibles

| Método | Endpoint | Descripción |
|---|---|---|
| POST | /api/solicitudes | Procesar una solicitud |
| GET | /api/solicitudes/{referenciaSolicitud} | Consultar una solicitud por referencia |
| GET | /api/solicitudes | Consultar las últimas 20 solicitudes |

### 8.1. Procesar solicitud

**POST** `/api/solicitudes`

Ejemplo de petición:

```json
{
  "referenciaSolicitud": "REF-001",
  "idPreaprobado": "PRA-1001",
  "idCliente": "USR-10",
  "monto": 600000
}
```

Ejemplo de respuesta autorizada:

```json
{
  "referenciaSolicitud": "REF-001",
  "idPreaprobado": "PRA-1001",
  "idCliente": "USR-10",
  "monto": 600000,
  "estado": "AUTHORIZED",
  "motivo": "Solicitud autorizada correctamente",
  "fechaProcesamiento": "2026-10-07T14:30:00"
}
```

Cuando la solicitud no cumple alguna regla de negocio, se registra como `REJECTED` con el motivo correspondiente.

Ejemplo de rechazo:

```json
{
  "referenciaSolicitud": "REF-002",
  "idPreaprobado": "PRA-1001",
  "idCliente": "USR-10",
  "monto": 500000,
  "estado": "REJECTED",
  "motivo": "Cupo disponible insuficiente",
  "fechaProcesamiento": "2026-10-07T14:35:00"
}
```

Los ejemplos dependen de los datos disponibles en la base de datos. Para realizar nuevas pruebas se recomienda utilizar referencias diferentes.

### 8.2. Consultar por referencia

**GET** `/api/solicitudes/REF-001`

Permite consultar una solicitud previamente procesada.

Si la referencia no existe, la API responde con HTTP `404`.

### 8.3. Consultar historial

**GET** `/api/solicitudes`

Devuelve las últimas 20 solicitudes registradas, ordenadas desde la más reciente.

## 9. Manejo de errores

La API cuenta con un manejador global de excepciones para mantener un formato uniforme en las respuestas de error.

Ejemplo:

```json
{
  "error": {
    "tipo": "FUNCIONAL",
    "codigo": "003",
    "mensaje": "La solicitud no existe"
  }
}
```

Los principales errores HTTP manejados son:

| HTTP | Descripción |
|---|---|
| 400 | Datos de entrada inválidos |
| 404 | Solicitud no encontrada |
| 409 | Referencia duplicada con información diferente |
| 500 | Error interno del sistema |

Los rechazos por reglas de negocio, como cupo insuficiente o preaprobado inactivo, se registran como solicitudes procesadas con estado `REJECTED`.

## 10. Documentación Swagger

La API cuenta con documentación OpenAPI definida en el archivo:

`src/main/resources/static/openapi.yaml`

Con el backend ejecutándose, se puede acceder a:

**Swagger UI:**

http://localhost:8080/swagger-ui.html

**Especificación OpenAPI YAML:**

http://localhost:8080/openapi.yaml

Desde Swagger UI es posible consultar los endpoints, revisar los modelos de datos y ejecutar peticiones de prueba.

Las solicitudes POST realizadas desde Swagger pueden modificar el cupo disponible de la base de datos.

## 11. Pruebas automatizadas

Se implementaron pruebas unitarias para validar las principales reglas de negocio del servicio.

Se utilizaron JUnit 5, Mockito y StepVerifier.

Entre los escenarios evaluados están:

- Solicitudes autorizadas.
- Cupo insuficiente.
- Preaprobados inactivos.
- Preaprobados inexistentes.
- Solicitudes repetidas.
- Referencias duplicadas con información diferente.
- Casos donde la actualización del cupo no afecta registros.

Para ejecutar las pruebas:

**Windows:**

```powershell
.\gradlew clean test
```

**Linux o macOS:**

```bash
./gradlew clean test
```

También pueden ejecutarse desde IntelliJ IDEA.

Las pruebas unitarias no reemplazan una prueba de concurrencia real contra MySQL.

## 12. Consideraciones técnicas

Para controlar el uso simultáneo del cupo, se implementó una actualización SQL condicional que verifica el saldo disponible antes de descontarlo.

El procesamiento utiliza transacciones reactivas para mantener la consistencia entre el descuento y el registro de la solicitud.

La idempotencia se controla mediante la referencia única de cada solicitud, evitando procesar nuevamente una operación con los mismos datos.

Las decisiones de implementación, alternativas y limitaciones se explican en:

`DECISIONES-TECNICAS.md`

## 13. Documentación adicional

- `DECISIONES-TECNICAS.md`: explica las principales decisiones de arquitectura y diseño.
- `USO-IA.md`: describe cómo se utilizó inteligencia artificial como apoyo durante el desarrollo.
- `src/main/resources/static/openapi.yaml`: contiene el contrato OpenAPI de los endpoints.

## 14. Frontend

La interfaz web fue desarrollada en Angular y se encuentra en un repositorio independiente.

Permite registrar solicitudes, consultar por referencia y visualizar el historial de operaciones.

Para utilizar la aplicación completa, el backend debe estar ejecutándose en el puerto `8080` y el frontend en el puerto `4200`.

Repositorio frontend:

https://github.com/JhonWH05/banco-ias-frontend.git

## 15. Posibles mejoras

Como mejoras futuras se podrían incorporar:

- Autenticación y autorización.
- Paginación y filtros para el historial.
- Pruebas de integración de concurrencia contra MySQL.
- Monitoreo y métricas de la aplicación.
- Despliegue en un entorno de pruebas.

Estas funcionalidades no forman parte del alcance implementado para esta prueba técnica.