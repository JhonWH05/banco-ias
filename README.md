# BancoIAS - Frontend

Aplicación web desarrollada en Angular para gestionar solicitudes de utilización de cupos preaprobados.

Este proyecto forma parte de una prueba técnica Full Stack y consume una API REST desarrollada con Java y Spring Boot.

## 1. Tecnologías utilizadas

- Angular 22
- TypeScript
- HTML y CSS
- Angular Router
- HttpClient
- Node.js y npm

## 2. Funcionalidades

La aplicación permite realizar las siguientes operaciones:

**Registrar solicitudes**

Permite ingresar una referencia de solicitud, el identificador del preaprobado, el identificador del cliente y el monto solicitado. La información se envía al backend para su procesamiento.

**Consultar solicitudes**

Permite buscar una solicitud mediante su referencia y visualizar la información registrada, incluyendo el resultado de la operación.

**Historial de solicitudes**

Permite consultar las solicitudes procesadas y visualizar su estado, monto y demás información relevante.

## 3. Requisitos previos

Para ejecutar el proyecto es necesario contar con:

- Node.js y npm
- Angular CLI
- Backend de BancoIAS configurado y en ejecución

## 4. Instalación

Clonar el repositorio:

```bash
git clone https://github.com/JhonWH05/banco-ias-frontend.git
```

Ingresar a la carpeta del proyecto:

```bash
cd banco-ias-frontend
```

Instalar las dependencias:

```bash
npm install
```

## 5. Ejecución

Iniciar el servidor de desarrollo:

```bash
npm start
```

También puede utilizarse:

```bash
ng serve
```

Una vez iniciado, acceder desde el navegador a:

http://localhost:4200

## 6. Comunicación con el backend

El frontend consume los servicios REST expuestos por el backend de BancoIAS.

**URL base de la API:**

```text
http://localhost:8080/api/solicitudes
```

**Endpoints utilizados:**

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/solicitudes` | Registrar una solicitud de utilización de cupo |
| GET | `/api/solicitudes/{referenciaSolicitud}` | Consultar una solicitud por referencia |
| GET | `/api/solicitudes` | Consultar el historial de solicitudes |

Para utilizar correctamente la aplicación, el backend debe estar ejecutándose en el puerto `8080`.

El backend incluye una configuración CORS para permitir las solicitudes provenientes del frontend ejecutado en `http://localhost:4200`.

## 7. Compilación

Para verificar que el proyecto compile correctamente:

```bash
ng build
```

Los archivos generados se almacenan en el directorio `dist/`.

## 8. Estructura del proyecto

```text
src/
├── app/
│   ├── components/      # Componentes y pantallas
│   ├── models/          # Interfaces y modelos de datos
│   ├── services/        # Comunicación con la API REST
│   ├── app.config.ts    # Configuración de Angular
│   ├── app.routes.ts    # Rutas de navegación
│   ├── app.ts           # Componente principal
│   ├── app.html         # Plantilla principal
│   └── app.css          # Estilos del componente principal
├── index.html
├── main.ts
└── styles.css
```

## 9. Repositorios

**Frontend (Angular):**

https://github.com/JhonWH05/banco-ias-frontend

**Backend (Spring Boot):**

https://github.com/JhonWH05/banco-ias-backend

La configuración de la base de datos, las reglas de negocio, las pruebas automatizadas y la documentación de la API se encuentran en el repositorio del backend.

## 10. Consideraciones

- El frontend no se conecta directamente a MySQL; toda la información se consulta y procesa a través de la API REST.
- La autorización o el rechazo de una solicitud es responsabilidad del backend.
- La aplicación muestra los resultados devueltos por la API y permite consultar las operaciones registradas.
- Para ejecutar el sistema completo se deben iniciar tanto el backend como el frontend.