package com.bancoias.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SolicitudUsoRequest(

        @NotBlank(message = "La referencia de solicitud es obligatoria")
        String referenciaSolicitud,

        @NotBlank(message = "El ID del preaprobado es obligatorio")
        String idPreaprobado,

        @NotBlank(message = "El ID del cliente es obligatorio")
        String idCliente,

        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(value = "0.01", message = "El monto debe ser mayor que cero")
        BigDecimal monto
) {
}