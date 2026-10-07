package com.bancoias.backend.exception;

import com.bancoias.backend.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> manejarApiException(
            ApiException exception) {

        ErrorCode errorCode = exception.getErrorCode();

        ErrorResponse.ErrorDetalle detalle =
                new ErrorResponse.ErrorDetalle(
                        errorCode.getTipo().name(),
                        errorCode.getCodigo(),
                        errorCode.getMensaje()
                );

        ErrorResponse response =
                new ErrorResponse(detalle);

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(response);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ErrorResponse> manejarValidacion(
            WebExchangeBindException exception) {

        ErrorCode errorCode =
                ErrorCode.DATOS_INVALIDOS;

        String mensaje = exception
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse(errorCode.getMensaje());

        ErrorResponse.ErrorDetalle detalle =
                new ErrorResponse.ErrorDetalle(
                        errorCode.getTipo().name(),
                        errorCode.getCodigo(),
                        mensaje
                );

        ErrorResponse response =
                new ErrorResponse(detalle);

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarErrorSistema(
            Exception exception) {

        ErrorCode errorCode =
                ErrorCode.ERROR_INTERNO;

        ErrorResponse.ErrorDetalle detalle =
                new ErrorResponse.ErrorDetalle(
                        errorCode.getTipo().name(),
                        errorCode.getCodigo(),
                        errorCode.getMensaje()
                );

        ErrorResponse response =
                new ErrorResponse(detalle);

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(response);
    }
}