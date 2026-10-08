package com.bancoias.backend.repository;

import com.bancoias.backend.model.Preaprobado;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface PreaprobadoRepository extends ReactiveCrudRepository<Preaprobado, Long> {

    Mono<Preaprobado> findByIdPreaprobado(String idPreaprobado);

    @Modifying
    @Query("""
        UPDATE preaprobado
        SET monto_disponible = monto_disponible - :monto
        WHERE id_preaprobado = :idPreaprobado
          AND id_cliente = :idCliente
          AND estado = 'ACTIVE'
          AND monto_disponible >= :monto
        """)
    Mono<Integer> descontarMonto(
            String idPreaprobado,
            String idCliente,
            BigDecimal monto
    );
}