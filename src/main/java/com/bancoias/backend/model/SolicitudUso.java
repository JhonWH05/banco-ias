package com.bancoias.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("solicitud_uso")
public class SolicitudUso {

    @Id
    private Long id;

    @Column("referencia_solicitud")
    private String referenciaSolicitud;

    @Column("id_preaprobado")
    private String idPreaprobado;

    @Column("id_cliente")
    private String idCliente;

    private BigDecimal monto;

    private String estado;

    private String motivo;

    @Column("fecha_procesamiento")
    private LocalDateTime fechaProcesamiento;
}