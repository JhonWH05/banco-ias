package com.bancoias.backend.controller;

import com.bancoias.backend.dto.SolicitudUsoRequest;
import com.bancoias.backend.dto.SolicitudUsoResponse;
import com.bancoias.backend.service.SolicitudUsoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
public class SolicitudUsoController {

    private final SolicitudUsoService solicitudUsoService;

    @PostMapping
    public Mono<SolicitudUsoResponse> procesarSolicitud(
            @Valid @RequestBody SolicitudUsoRequest request) {

        return solicitudUsoService.procesarSolicitud(request);
    }

    @GetMapping("/{referenciaSolicitud}")
    public Mono<SolicitudUsoResponse> consultarPorReferencia(
            @PathVariable String referenciaSolicitud) {

        return solicitudUsoService
                .consultarPorReferencia(referenciaSolicitud);
    }

    @GetMapping
    public Flux<SolicitudUsoResponse> consultarRecientes() {

        return solicitudUsoService.consultarRecientes();
    }
}