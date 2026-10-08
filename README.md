# BancoIAS - Prueba Técnica Full Stack

Solución Full Stack con Java Spring Boot WebFlux y Angular.

El objetivo es permitir la utilización de cupos preaprobados, garantizando la consistencia del cupo disponible, el registro de solicitudes y el manejo de solicitudes repetidas o simultáneas.

## Tecnologías utilizadas

**Backend**

- Java 17
- Spring Boot 3.5.6
- Spring WebFlux
- Spring Data R2DBC
- MySQL
- Gradle
- JUnit y Mockito
- Swagger / OpenAPI

**Frontend**

- Angular 22
- TypeScript
- HTML y CSS
- Angular Router
- HttpClient

## Estructura del repositorio

```text
banco-ias/
├── backend/       # API REST, lógica de negocio, persistencia y pruebas
├── frontend/      # Aplicación web Angular
└── README.md      # Documentación general
```

## Funcionalidades implementadas

- Registro de solicitudes de utilización de cupos preaprobados.
- Validación de reglas de negocio.
- Autorización o rechazo según el cupo disponible.
- Persistencia de solicitudes procesadas.
- Manejo de referencias repetidas mediante idempotencia.
- Control de concurrencia para proteger el cupo disponible.
- Consulta de solicitudes por referencia.
- Consulta del historial reciente.
- Interfaz Angular integrada con el backend.

## Ejecución del proyecto

### Backend

1. Configurar MySQL y crear la base de datos utilizando los scripts disponibles en `backend/src/main/resources/`.
2. Configurar las credenciales de conexión.
3. Ejecutar la aplicación Spring Boot desde IntelliJ IDEA o mediante Gradle.

La API estará disponible en:

[http://localhost:8080](http://localhost:8080)

Documentación Swagger:

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Frontend

Ingresar al directorio:

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

Acceder a:

[http://localhost:4200](http://localhost:4200)

Para conocer los detalles de configuración y ejecución, consultar los archivos README de cada proyecto.

## Pruebas automatizadas

El backend incluye pruebas automatizadas sobre la lógica de procesamiento de solicitudes.

Las pruebas pueden ejecutarse desde IntelliJ IDEA o utilizando Gradle, con el entorno Java correctamente configurado.

## Documentación adicional

- [Documentación del backend](backend/README.md)
- [Documentación del frontend](frontend/README.md)
- [Decisiones técnicas](backend/DECISIONES-TECNICAS.md)
- [Registro de uso de IA](backend/USO-IA.md)
- [Especificación OpenAPI](backend/src/main/resources/static/openapi.yaml)

## Alcance y limitaciones

Se implementaron los requisitos funcionales RF01 a RF07 definidos en el enunciado.

La solución utiliza MySQL como mecanismo de persistencia y una estrategia de actualización atómica para proteger el cupo disponible ante solicitudes concurrentes.

No se implementó RabbitMQ, ya que corresponde a un punto opcional del ejercicio.

No se incluyen módulos de autenticación, administración de clientes ni administración de preaprobados, dado que están fuera del alcance obligatorio.

## Repositorios originales

- Backend: [https://github.com/JhonWH05/banco-ias-backend](https://github.com/JhonWH05/banco-ias-backend)
- Frontend: [https://github.com/JhonWH05/banco-ias-frontend](https://github.com/JhonWH05/banco-ias-frontend)

Ambos proyectos se integraron en este repositorio principal conservando sus historiales de commits.
