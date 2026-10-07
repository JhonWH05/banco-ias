package com.bancoias.backend.repository;

import com.bancoias.backend.model.SolicitudUso;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SolicitudUsoRepository
        extends ReactiveCrudRepository<SolicitudUso, Long> {

    Mono<SolicitudUso> findByReferenciaSolicitud(String referenciaSolicitud);

    Flux<SolicitudUso> findTop20ByOrderByFechaProcesamientoDesc();
}