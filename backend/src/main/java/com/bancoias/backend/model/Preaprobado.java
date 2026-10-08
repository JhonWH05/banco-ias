package com.bancoias.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("preaprobado")
public class Preaprobado {

    @Id
    private Long id;

    @Column("id_preaprobado")
    private String idPreaprobado;

    @Column("id_cliente")
    private String idCliente;

    private String estado;

    @Column("monto_disponible")
    private BigDecimal montoDisponible;
}