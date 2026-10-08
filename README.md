# BancoIAS - Prueba Técnica Full Stack

Solución Full Stack desarrollada con **Java 17, Spring Boot WebFlux, MySQL y Angular 22**, orientada al procesamiento de solicitudes de utilización de cupos preaprobados.

El sistema permite registrar solicitudes, validar reglas de negocio, autorizar o rechazar operaciones, conservar sus resultados y consultar el historial. También incorpora mecanismos de idempotencia y control de concurrencia para proteger el cupo disponible.

## 1. Tecnologías utilizadas

### Backend

- Java 17
- Spring Boot 3.5.6
- Spring WebFlux
- Spring Data R2DBC
- MySQL
- Gradle
- JUnit 5, Mockito y Reactor Test
- OpenAPI 3.0 y Swagger UI

### Frontend

- Angular 22
- TypeScript
- HTML y CSS
- Angular Router
- HttpClient
- Node.js y npm

## 2. Estructura del proyecto

```text
banco-ias/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   └── test/
│   ├── build.gradle
│   ├── gradlew
│   ├── gradlew.bat
│   ├── README.md
│   ├── DECISIONES-TECNICAS.md
│   └── USO-IA.md
├── frontend/
│   ├── src/
│   │   └── app/
│   │       ├── components/
│   │       ├── models/
│   │       └── services/
│   ├── angular.json
│   ├── package.json
│   └── README.md
└── README.md
```

El backend contiene la API REST, las reglas de negocio, la persistencia y las pruebas automatizadas. El frontend proporciona la interfaz web para interactuar con los servicios.

## 3. Funcionalidades implementadas

| Requisito | Funcionalidad |
|---|---|
| RF01 | Registrar solicitudes de utilización de cupos preaprobados y su fecha de procesamiento |
| RF02 | Validar monto, existencia del preaprobado, cliente, estado y cupo disponible |
| RF03 | Conservar solicitudes autorizadas y rechazadas con sus respectivos resultados |
| RF04 | Proteger el cupo disponible mediante una actualización SQL condicional |
| RF05 | Manejar referencias repetidas y evitar descuentos duplicados |
| RF06 | Consultar solicitudes por referencia y obtener las últimas 20 operaciones |
| RF07 | Registrar, consultar y visualizar solicitudes desde Angular |

### Reglas de negocio

Una solicitud solo puede ser autorizada cuando:

- El monto solicitado es mayor que cero.
- El preaprobado existe.
- El preaprobado corresponde al cliente informado.
- El preaprobado está en estado `ACTIVE`.
- El monto no supera el cupo disponible.

Cuando una solicitud no cumple las reglas correspondientes, se registra su resultado de rechazo sin descontar el cupo.

La referencia única de cada solicitud permite identificar operaciones repetidas y evitar procesarlas nuevamente con los mismos datos.

## 4. Requisitos previos

Para ejecutar el proyecto completo se necesita:

- Git
- JDK 17
- MySQL 8 o una versión compatible
- Node.js y npm
- IntelliJ IDEA o un entorno compatible con Java y Gradle
- Visual Studio Code u otro editor compatible con Angular

El backend incluye **Gradle Wrapper**, por lo que no es necesario instalar Gradle globalmente.

Angular CLI puede utilizarse para ejecutar comandos de desarrollo, aunque también se puede iniciar la aplicación mediante npm.

## 5. Clonar el repositorio

Ejecutar:

```bash
git clone https://github.com/JhonWH05/banco-ias.git
```

Ingresar al proyecto:

```bash
cd banco-ias
```

La carpeta clonada contiene tanto el backend como el frontend.

## 6. Configuración de la base de datos

La aplicación utiliza MySQL con una base de datos denominada:

```text
banco_ias
```

Los scripts SQL se encuentran en:

```text
backend/src/main/resources/
├── schema.sql
└── data.sql
```

### Pasos de configuración

1. Iniciar el servicio de MySQL.
2. Abrir MySQL Workbench o un cliente SQL compatible.
3. Ejecutar primero `backend/src/main/resources/schema.sql`.
4. Ejecutar después `backend/src/main/resources/data.sql`.
5. Verificar que existan las tablas `preaprobado` y `solicitud_uso`.

Los scripts se ejecutan manualmente. La aplicación no reinicializa automáticamente la base de datos al arrancar.

### Datos iniciales

| Preaprobado | Cliente | Estado | Cupo disponible inicial |
|---|---|---|---|
| PRA-1001 | USR-10 | ACTIVE | $1.000.000 COP |
| PRA-1002 | USR-10 | BLOCKED | $800.000 COP |
| PRA-2001 | USR-20 | ACTIVE | $2.000.000 COP |

Estos valores corresponden al estado inicial. El cupo disponible puede cambiar después de procesar solicitudes autorizadas.

## 7. Configuración del backend

La configuración de Spring Boot se encuentra en:

```text
backend/src/main/resources/application.yaml
```

La conexión MySQL utiliza las siguientes variables de entorno:

| Variable | Descripción | Valor predeterminado |
|---|---|---|
| `DB_URL` | URL R2DBC de MySQL | `r2dbc:mysql://localhost:3306/banco_ias` |
| `DB_USER` | Usuario de MySQL | `root` |
| `DB_PASSWORD` | Contraseña de MySQL | Obligatoria |

Ejemplo de configuración:

```yaml
spring:
  application:
    name: banco-ias-backend
  r2dbc:
    url: ${DB_URL:r2dbc:mysql://localhost:3306/banco_ias}
    username: ${DB_USER:root}
    password: ${DB_PASSWORD}
```

### Configuración en Windows PowerShell

```powershell
$env:DB_PASSWORD="tu_contraseña_mysql"
```

Si el usuario o la URL son diferentes, también se pueden configurar:

```powershell
$env:DB_USER="root"
$env:DB_URL="r2dbc:mysql://localhost:3306/banco_ias"
```

Si se ejecuta desde IntelliJ IDEA, estas variables pueden configurarse en **Run/Debug Configurations**, dentro de la configuración de ejecución de Spring Boot.

**Nota:** para pruebas locales también es posible configurar directamente la URL, el usuario y la contraseña en `application.yaml`. Sin embargo, se recomienda utilizar variables de entorno y evitar publicar credenciales reales en GitHub.

## 8. Ejecutar el backend

Desde la raíz del repositorio, ingresar a la carpeta:

```bash
cd backend
```

### Windows PowerShell

```powershell
.\gradlew.bat bootRun
```

### Linux o macOS

```bash
./gradlew bootRun
```

También puede abrirse la carpeta `backend` desde IntelliJ IDEA y ejecutar la clase principal:

```text
BancoIasBackendApplication
```

Una vez iniciado correctamente, el backend estará disponible en:

http://localhost:8080

### Documentación Swagger

Swagger UI:

http://localhost:8080/swagger-ui.html

Especificación OpenAPI:

http://localhost:8080/openapi.yaml

Desde Swagger UI se pueden consultar los endpoints, revisar los modelos de datos y ejecutar peticiones de prueba.

Las solicitudes realizadas desde Swagger pueden modificar los registros y el cupo disponible de la base de datos.

## 9. API REST

La API expone los siguientes endpoints:

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/solicitudes` | Procesar una solicitud de utilización de cupo |
| GET | `/api/solicitudes/{referenciaSolicitud}` | Consultar una solicitud por referencia |
| GET | `/api/solicitudes` | Consultar las últimas 20 solicitudes procesadas |

### 9.1. Registrar una solicitud

**POST** `http://localhost:8080/api/solicitudes`

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

Ejemplo de respuesta rechazada:

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

Las fechas mostradas son ilustrativas. El sistema registra la fecha real de procesamiento.

### 9.2. Consultar una solicitud

**GET** `http://localhost:8080/api/solicitudes/REF-001`

Permite recuperar los datos y el resultado de una solicitud previamente procesada.

Si la referencia no existe, la API responde con HTTP `404`.

### 9.3. Consultar el historial

**GET** `http://localhost:8080/api/solicitudes`

Devuelve las últimas 20 solicitudes registradas, ordenadas desde la más reciente.

No se implementó paginación avanzada porque no forma parte del alcance obligatorio.

## 10. Manejo de errores

El backend utiliza un manejador global de excepciones para ofrecer respuestas con una estructura uniforme.

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

| Código HTTP | Descripción |
|---|---|
| 400 | Datos de entrada inválidos |
| 404 | Solicitud no encontrada |
| 409 | Referencia repetida con información diferente |
| 500 | Error interno del sistema |

Los rechazos propios de las reglas de negocio se conservan como solicitudes procesadas con estado `REJECTED`.

## 11. Instalación y ejecución del frontend

Con el backend en funcionamiento, abrir una segunda terminal.

Desde la raíz del repositorio:

```bash
cd frontend
```

Instalar las dependencias:

```bash
npm install
```

Iniciar Angular:

```bash
npm start
```

Alternativamente:

```bash
ng serve
```

Acceder desde el navegador a:

http://localhost:4200

### Funcionalidades de la interfaz

**Registro de solicitudes**

Permite ingresar la referencia, el identificador del preaprobado, el identificador del cliente y el monto solicitado. El resultado se presenta después de procesar la operación en el backend.

**Consulta por referencia**

Permite recuperar una solicitud existente y visualizar su estado, monto y demás información registrada.

**Historial de solicitudes**

Permite visualizar las solicitudes procesadas recientemente, incluyendo las operaciones autorizadas y rechazadas.

### Comunicación con el backend

El frontend consume la API REST disponible en:

```text
http://localhost:8080/api/solicitudes
```

El backend incluye configuración CORS para permitir solicitudes desde `http://localhost:4200`.

El frontend no se conecta directamente a MySQL. Las operaciones de persistencia y las reglas de negocio se ejecutan exclusivamente en el backend.

## 12. Compilación del frontend

Desde la carpeta `frontend`, ejecutar:

```bash
npm run build
```

También se puede utilizar:

```bash
ng build
```

Los archivos compilados se generan en el directorio `dist/`.

## 13. Pruebas automatizadas

El backend contiene pruebas automatizadas desarrolladas con:

- JUnit 5
- Mockito
- Reactor Test / StepVerifier

Los escenarios contemplados incluyen:

- Solicitudes autorizadas.
- Rechazo por cupo insuficiente.
- Preaprobados inactivos.
- Preaprobados inexistentes.
- Solicitudes con referencias repetidas.
- Referencias repetidas con información diferente.
- Casos en los que la actualización del cupo no modifica registros.

### Ejecutar las pruebas del backend

Desde `backend`:

**Windows PowerShell:**

```powershell
.\gradlew.bat clean test
```

**Linux o macOS:**

```bash
./gradlew clean test
```

También pueden ejecutarse directamente desde IntelliJ IDEA.

Las pruebas unitarias validan comportamientos del servicio, pero no sustituyen una prueba de integración concurrente real contra MySQL.

## 14. Escenarios para comprobar el funcionamiento

Con la base de datos inicializada y sin solicitudes anteriores:

| Referencia | Preaprobado | Cliente | Monto | Resultado esperado |
|---|---|---|---|---|
| REF-001 | PRA-1001 | USR-10 | $600.000 | AUTHORIZED |
| REF-002 | PRA-1001 | USR-10 | $500.000 | REJECTED: cupo insuficiente |
| REF-003 | PRA-1002 | USR-10 | $300.000 | REJECTED: preaprobado inactivo |
| REF-004 | PRA-9999 | USR-10 | $200.000 | REJECTED: preaprobado inexistente |

Después de autorizar `REF-001`, el cupo disponible de `PRA-1001` debe quedar en $400.000 COP.

También se recomienda verificar:

1. Reenviar `REF-001` con los mismos datos y comprobar que no se descuente nuevamente el cupo.
2. Reenviar `REF-001` con un monto diferente y comprobar que se maneje el conflicto sin modificar la solicitud original.
3. Consultar una referencia existente.
4. Consultar una referencia inexistente.
5. Revisar el historial desde Angular.
6. Confirmar la persistencia de los resultados en MySQL.

Para repetir las pruebas con los mismos identificadores, es necesario preparar nuevamente el estado de la base de datos.

## 15. Decisiones técnicas

### Persistencia

Se utiliza MySQL para conservar los preaprobados y las solicitudes procesadas.

El acceso reactivo a los datos se realiza mediante Spring Data R2DBC.

### Consistencia del cupo

Para controlar solicitudes simultáneas, el backend utiliza una actualización SQL condicional que verifica el cupo disponible antes de descontarlo.

Esta estrategia busca evitar que dos solicitudes autoricen conjuntamente un monto superior al cupo existente.

### Transacciones

El procesamiento utiliza transacciones reactivas para coordinar el descuento del cupo y el registro de la solicitud.

### Idempotencia

Cada solicitud utiliza una referencia única.

Cuando se recibe nuevamente una referencia procesada con los mismos datos, se conserva el resultado original sin realizar un nuevo descuento.

Si la referencia llega con información diferente, la API maneja el conflicto de forma controlada.

### Manejo de errores

Se utiliza un manejador global para centralizar las respuestas de error y distinguir los rechazos de negocio de los errores de la API.

Las alternativas consideradas, justificaciones, riesgos y demás detalles se documentan en:

[DECISIONES-TECNICAS.md](backend/DECISIONES-TECNICAS.md)

## 16. Uso de inteligencia artificial

Durante el desarrollo se utilizaron herramientas de inteligencia artificial como apoyo para actividades de análisis, implementación, revisión y documentación.

El registro correspondiente describe las actividades apoyadas, los resultados aprovechados, las validaciones realizadas y las consideraciones sobre información sensible.

Consultar:

[USO-IA.md](backend/USO-IA.md)

## 17. Alcance y limitaciones

La solución contempla los requisitos funcionales RF01 a RF07 definidos en el ejercicio.

Las siguientes funcionalidades no forman parte del alcance implementado:

- RabbitMQ, correspondiente a un punto opcional.
- Autenticación y autorización de usuarios.
- Administración de clientes.
- Administración de preaprobados.
- Paginación avanzada del historial.
- Despliegue en un entorno productivo.
- Pruebas de integración concurrente real contra MySQL.

Como mejoras futuras se podrían incorporar pruebas adicionales de concurrencia, monitoreo, métricas, paginación y mecanismos de despliegue automatizado.

## 18. Documentación complementaria

Aunque este README reúne las instrucciones generales del sistema, los proyectos mantienen su documentación específica:

- [README del backend](backend/README.md)
- [README del frontend](frontend/README.md)
- [Decisiones técnicas](backend/DECISIONES-TECNICAS.md)
- [Uso de inteligencia artificial](backend/USO-IA.md)
- [Especificación OpenAPI](backend/src/main/resources/static/openapi.yaml)

## 19. Repositorio y control de versiones

**Repositorio principal:**

https://github.com/JhonWH05/banco-ias

Los proyectos backend y frontend se desarrollaron inicialmente en repositorios independientes y posteriormente se integraron mediante Git Subtree, conservando sus historiales de commits.

Repositorios originales:

- Backend: https://github.com/JhonWH05/banco-ias-backend
- Frontend: https://github.com/JhonWH05/banco-ias-frontend

El repositorio principal permite consultar el código fuente, las pruebas, la documentación y el historial de desarrollo desde un único lugar.