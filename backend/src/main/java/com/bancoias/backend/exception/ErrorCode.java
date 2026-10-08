package com.bancoias.backend.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    REFERENCIA_DUPLICADA(
            TipoError.FUNCIONAL,
            "001",
            "La referencia ya existe con información diferente",
            HttpStatus.CONFLICT
    ),

    DATOS_INVALIDOS(
            TipoError.FUNCIONAL,
            "002",
            "Los datos enviados no son válidos",
            HttpStatus.BAD_REQUEST
    ),

    SOLICITUD_NO_ENCONTRADA(
            TipoError.FUNCIONAL,
            "003",
            "La solicitud no existe",
            HttpStatus.NOT_FOUND
    ),

    ERROR_INTERNO(
            TipoError.SISTEMA,
            "500",
            "Ocurrió un error interno en el sistema",
            HttpStatus.INTERNAL_SERVER_ERROR
    );

    private final TipoError tipo;
    private final String codigo;
    private final String mensaje;
    private final HttpStatus httpStatus;
}