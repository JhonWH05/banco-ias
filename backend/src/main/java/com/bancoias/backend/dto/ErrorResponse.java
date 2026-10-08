package com.bancoias.backend.dto;

public record ErrorResponse(
        ErrorDetalle error
) {
    public record ErrorDetalle(
            String tipo,
            String codigo,
            String mensaje
    ) {
    }
}