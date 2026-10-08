package com.bancoias.backend.service;

import com.bancoias.backend.dto.SolicitudUsoRequest;
import com.bancoias.backend.dto.SolicitudUsoResponse;
import com.bancoias.backend.exception.ApiException;
import com.bancoias.backend.exception.ErrorCode;
import com.bancoias.backend.model.SolicitudUso;
import com.bancoias.backend.repository.PreaprobadoRepository;
import com.bancoias.backend.repository.SolicitudUsoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SolicitudUsoService {

    private final PreaprobadoRepository preaprobadoRepository;
    private final SolicitudUsoRepository solicitudUsoRepository;

    @Transactional
    public Mono<SolicitudUsoResponse> procesarSolicitud(SolicitudUsoRequest request) {

        return solicitudUsoRepository
                .findByReferenciaSolicitud(request.referenciaSolicitud())
                .flatMap(solicitudExistente -> {

                    boolean mismosDatos =
                            solicitudExistente.getIdPreaprobado()
                                    .equals(request.idPreaprobado())
                                    && solicitudExistente.getIdCliente()
                                    .equals(request.idCliente())
                                    && solicitudExistente.getMonto()
                                    .compareTo(request.monto()) == 0;

                    if (!mismosDatos) {
                        return Mono.error(
                                new ApiException(ErrorCode.REFERENCIA_DUPLICADA)
                        );
                    }

                    return Mono.just(
                            convertirAResponse(solicitudExistente)
                    );
                })
                .switchIfEmpty(
                        Mono.defer(() -> procesarNuevaSolicitud(request))
                );
    }

    private Mono<SolicitudUsoResponse> procesarNuevaSolicitud(
            SolicitudUsoRequest request) {

        return preaprobadoRepository
                .findByIdPreaprobado(request.idPreaprobado())
                .flatMap(preaprobado -> {

                    if (!preaprobado.getIdCliente().equals(request.idCliente())) {
                        return rechazar(request, "El preaprobado no pertenece al cliente");
                    }

                    if (!"ACTIVE".equals(preaprobado.getEstado())) {
                        return rechazar(request, "El preaprobado no está activo");
                    }

                    if (preaprobado.getMontoDisponible().compareTo(request.monto()) < 0) {
                        return rechazar(request, "Cupo disponible insuficiente");
                    }

                    return autorizar(request);
                })
                .switchIfEmpty(
                        Mono.defer(() ->
                                rechazar(request, "El preaprobado no existe")
                        )
                );
    }

    private Mono<SolicitudUsoResponse> rechazar(
            SolicitudUsoRequest request,
            String motivo) {

        SolicitudUso solicitud = new SolicitudUso();

        solicitud.setReferenciaSolicitud(request.referenciaSolicitud());
        solicitud.setIdPreaprobado(request.idPreaprobado());
        solicitud.setIdCliente(request.idCliente());
        solicitud.setMonto(request.monto());
        solicitud.setEstado("REJECTED");
        solicitud.setMotivo(motivo);
        solicitud.setFechaProcesamiento(java.time.LocalDateTime.now());

        return solicitudUsoRepository
                .save(solicitud)
                .map(this::convertirAResponse);
    }

    private Mono<SolicitudUsoResponse> autorizar(
            SolicitudUsoRequest request) {

        return preaprobadoRepository
                .descontarMonto(
                        request.idPreaprobado(),
                        request.idCliente(),
                        request.monto()
                )
                .flatMap(filasActualizadas -> {

                    if (filasActualizadas == 1) {

                        SolicitudUso solicitud = new SolicitudUso();

                        solicitud.setReferenciaSolicitud(request.referenciaSolicitud());
                        solicitud.setIdPreaprobado(request.idPreaprobado());
                        solicitud.setIdCliente(request.idCliente());
                        solicitud.setMonto(request.monto());
                        solicitud.setEstado("AUTHORIZED");
                        solicitud.setMotivo("Solicitud autorizada correctamente");
                        solicitud.setFechaProcesamiento(
                                java.time.LocalDateTime.now()
                        );

                        return solicitudUsoRepository
                                .save(solicitud)
                                .map(this::convertirAResponse);
                    }

                    return rechazar(
                            request,
                            "El cupo disponible cambió durante el procesamiento"
                    );
                });
    }

    public Mono<SolicitudUsoResponse> consultarPorReferencia(
            String referenciaSolicitud) {

        return solicitudUsoRepository
                .findByReferenciaSolicitud(referenciaSolicitud)
                .map(this::convertirAResponse)
                .switchIfEmpty(
                        Mono.error(
                                new ApiException(
                                        ErrorCode.SOLICITUD_NO_ENCONTRADA
                                )
                        )
                );
    }

    public Flux<SolicitudUsoResponse> consultarRecientes() {

        return solicitudUsoRepository
                .findTop20ByOrderByFechaProcesamientoDesc()
                .map(this::convertirAResponse);
    }

    private SolicitudUsoResponse convertirAResponse(
            SolicitudUso solicitud) {

        return new SolicitudUsoResponse(
                solicitud.getReferenciaSolicitud(),
                solicitud.getIdPreaprobado(),
                solicitud.getIdCliente(),
                solicitud.getMonto(),
                solicitud.getEstado(),
                solicitud.getMotivo(),
                solicitud.getFechaProcesamiento()
        );
    }
}