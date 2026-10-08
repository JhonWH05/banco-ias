CREATE DATABASE IF NOT EXISTS banco_ias;

USE banco_ias;

CREATE TABLE IF NOT EXISTS preaprobado (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_preaprobado VARCHAR(20) NOT NULL UNIQUE,
    id_cliente VARCHAR(20) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    monto_disponible DECIMAL(15,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS solicitud_uso (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    referencia_solicitud VARCHAR(50) NOT NULL UNIQUE,
    id_preaprobado VARCHAR(20) NOT NULL,
    id_cliente VARCHAR(20) NOT NULL,
    monto DECIMAL(15,2) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    motivo VARCHAR(150),
    fecha_procesamiento TIMESTAMP NOT NULL
);