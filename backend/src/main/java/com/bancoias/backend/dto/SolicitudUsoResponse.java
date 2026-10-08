package com.bancoias.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SolicitudUsoResponse(
        String referenciaSolicitud,
        String idPreaprobado,
        String idCliente,
        BigDecimal monto,
        String estado,
        String motivo,
        LocalDateTime fechaProcesamiento
) {
}