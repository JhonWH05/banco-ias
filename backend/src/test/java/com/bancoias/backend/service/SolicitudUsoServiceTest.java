package com.bancoias.backend.service;

import com.bancoias.backend.exception.ApiException;
import com.bancoias.backend.exception.ErrorCode;
import com.bancoias.backend.repository.PreaprobadoRepository;
import com.bancoias.backend.repository.SolicitudUsoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bancoias.backend.dto.SolicitudUsoRequest;
import com.bancoias.backend.model.Preaprobado;
import com.bancoias.backend.model.SolicitudUso;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolicitudUsoServiceTest {

    @Mock
    private PreaprobadoRepository preaprobadoRepository;

    @Mock
    private SolicitudUsoRepository solicitudUsoRepository;

    private SolicitudUsoService solicitudUsoService;

    @BeforeEach
    void setUp() {
        solicitudUsoService = new SolicitudUsoService(
                preaprobadoRepository,
                solicitudUsoRepository
        );
    }

    @Test
    void debeAutorizarSolicitudCuandoCumpleLasCondiciones() {

        // Arrange
        var request = new SolicitudUsoRequest(
                "REF-TEST-001",
                "PRA-1001",
                "USR-10",
                new BigDecimal("600000")
        );

        var preaprobado = new Preaprobado(
                1L,
                "PRA-1001",
                "USR-10",
                "ACTIVE",
                new BigDecimal("1000000")
        );

        when(solicitudUsoRepository
                .findByReferenciaSolicitud("REF-TEST-001"))
                .thenReturn(Mono.empty());

        when(preaprobadoRepository
                .findByIdPreaprobado("PRA-1001"))
                .thenReturn(Mono.just(preaprobado));

        when(preaprobadoRepository
                .descontarMonto(
                        eq("PRA-1001"),
                        eq("USR-10"),
                        eq(new BigDecimal("600000"))
                ))
                .thenReturn(Mono.just(1));

        when(solicitudUsoRepository.save(any(SolicitudUso.class)))
                .thenAnswer(invocation -> {
                    SolicitudUso solicitud = invocation.getArgument(0);
                    solicitud.setId(1L);
                    return Mono.just(solicitud);
                });

        // Act + Assert
        StepVerifier.create(
                        solicitudUsoService.procesarSolicitud(request)
                )
                .assertNext(response -> {
                    assertEquals("REF-TEST-001", response.referenciaSolicitud());
                    assertEquals("AUTHORIZED", response.estado());
                    assertEquals(
                            "Solicitud autorizada correctamente",
                            response.motivo()
                    );
                })
                .verifyComplete();
    }

    @Test
    void debeRechazarSolicitudCuandoElCupoEsInsuficiente() {

        // Arrange
        var request = new SolicitudUsoRequest(
                "REF-TEST-002",
                "PRA-1001",
                "USR-10",
                new BigDecimal("500000")
        );

        var preaprobado = new Preaprobado(
                1L,
                "PRA-1001",
                "USR-10",
                "ACTIVE",
                new BigDecimal("400000")
        );

        when(solicitudUsoRepository
                .findByReferenciaSolicitud("REF-TEST-002"))
                .thenReturn(Mono.empty());

        when(preaprobadoRepository
                .findByIdPreaprobado("PRA-1001"))
                .thenReturn(Mono.just(preaprobado));

        when(solicitudUsoRepository.save(any(SolicitudUso.class)))
                .thenAnswer(invocation -> {
                    SolicitudUso solicitud = invocation.getArgument(0);
                    solicitud.setId(2L);
                    return Mono.just(solicitud);
                });

        // Act + Assert
        StepVerifier.create(
                        solicitudUsoService.procesarSolicitud(request)
                )
                .assertNext(response -> {
                    assertEquals("REF-TEST-002", response.referenciaSolicitud());
                    assertEquals("REJECTED", response.estado());
                    assertEquals(
                            "Cupo disponible insuficiente",
                            response.motivo()
                    );
                })
                .verifyComplete();
    }

    @Test
    void debeRechazarSolicitudCuandoPreaprobadoEstaBloqueado() {

        // Arrange
        var request = new SolicitudUsoRequest(
                "REF-TEST-003",
                "PRA-1002",
                "USR-10",
                new BigDecimal("300000")
        );

        var preaprobado = new Preaprobado(
                2L,
                "PRA-1002",
                "USR-10",
                "BLOCKED",
                new BigDecimal("800000")
        );

        when(solicitudUsoRepository
                .findByReferenciaSolicitud("REF-TEST-003"))
                .thenReturn(Mono.empty());

        when(preaprobadoRepository
                .findByIdPreaprobado("PRA-1002"))
                .thenReturn(Mono.just(preaprobado));

        when(solicitudUsoRepository.save(any(SolicitudUso.class)))
                .thenAnswer(invocation -> {
                    SolicitudUso solicitud = invocation.getArgument(0);
                    solicitud.setId(3L);
                    return Mono.just(solicitud);
                });

        // Act + Assert
        StepVerifier.create(
                        solicitudUsoService.procesarSolicitud(request)
                )
                .assertNext(response -> {
                    assertEquals("REF-TEST-003", response.referenciaSolicitud());
                    assertEquals("REJECTED", response.estado());
                    assertEquals(
                            "El preaprobado no está activo",
                            response.motivo()
                    );
                })
                .verifyComplete();

        verify(preaprobadoRepository, never())
                .descontarMonto(
                        any(),
                        any(),
                        any()
                );
    }

    @Test
    void debeRechazarSolicitudCuandoPreaprobadoNoPerteneceAlCliente() {

        var request = new SolicitudUsoRequest(
                "REF-TEST-004",
                "PRA-1001",
                "USR-99",
                new BigDecimal("100000")
        );

        var preaprobado = new Preaprobado(
                1L,
                "PRA-1001",
                "USR-10",
                "ACTIVE",
                new BigDecimal("400000")
        );

        when(solicitudUsoRepository
                .findByReferenciaSolicitud("REF-TEST-004"))
                .thenReturn(Mono.empty());

        when(preaprobadoRepository
                .findByIdPreaprobado("PRA-1001"))
                .thenReturn(Mono.just(preaprobado));

        when(solicitudUsoRepository.save(any(SolicitudUso.class)))
                .thenAnswer(invocation ->
                        Mono.just(invocation.getArgument(0))
                );

        StepVerifier.create(solicitudUsoService.procesarSolicitud(request))
                .assertNext(response -> {
                    assertEquals("REJECTED", response.estado());
                    assertEquals(
                            "El preaprobado no pertenece al cliente",
                            response.motivo()
                    );
                })
                .verifyComplete();

        verify(preaprobadoRepository, never())
                .descontarMonto(any(), any(), any());
    }

    @Test
    void debeRechazarSolicitudCuandoPreaprobadoNoExiste() {

        var request = new SolicitudUsoRequest(
                "REF-TEST-005",
                "PRA-9999",
                "USR-10",
                new BigDecimal("200000")
        );

        when(solicitudUsoRepository
                .findByReferenciaSolicitud("REF-TEST-005"))
                .thenReturn(Mono.empty());

        when(preaprobadoRepository
                .findByIdPreaprobado("PRA-9999"))
                .thenReturn(Mono.empty());

        when(solicitudUsoRepository.save(any(SolicitudUso.class)))
                .thenAnswer(invocation ->
                        Mono.just(invocation.getArgument(0))
                );

        StepVerifier.create(solicitudUsoService.procesarSolicitud(request))
                .assertNext(response -> {
                    assertEquals("REJECTED", response.estado());
                    assertEquals(
                            "El preaprobado no existe",
                            response.motivo()
                    );
                })
                .verifyComplete();

        verify(preaprobadoRepository, never())
                .descontarMonto(any(), any(), any());
    }

    @Test
    void debeGenerarErrorCuandoReferenciaExisteConDatosDiferentes() {

        var request = new SolicitudUsoRequest(
                "REF-TEST-001",
                "PRA-1001",
                "USR-10",
                new BigDecimal("900000")
        );

        var solicitudExistente = new SolicitudUso(
                1L,
                "REF-TEST-001",
                "PRA-1001",
                "USR-10",
                new BigDecimal("600000"),
                "AUTHORIZED",
                "Solicitud autorizada correctamente",
                java.time.LocalDateTime.now()
        );

        when(solicitudUsoRepository
                .findByReferenciaSolicitud("REF-TEST-001"))
                .thenReturn(Mono.just(solicitudExistente));

        StepVerifier.create(
                        solicitudUsoService.procesarSolicitud(request)
                )
                .expectErrorMatches(error ->
                        error instanceof ApiException apiException
                                && apiException.getErrorCode()
                                == ErrorCode.REFERENCIA_DUPLICADA
                )
                .verify();

        verify(preaprobadoRepository, never())
                .descontarMonto(any(), any(), any());

        verify(solicitudUsoRepository, never())
                .save(any(SolicitudUso.class));
    }

    @Test
    void debeRechazarSolicitudCuandoElCupoCambioDuranteElProcesamiento() {

        SolicitudUsoRequest request = new SolicitudUsoRequest(
                "REF-CONC-002",
                "PRA-1001",
                "USR-10",
                new BigDecimal("700000")
        );

        Preaprobado preaprobado = new Preaprobado(
                1L,
                "PRA-1001",
                "USR-10",
                "ACTIVE",
                new BigDecimal("1000000")
        );

        SolicitudUso solicitudGuardada = new SolicitudUso(
                10L,
                "REF-CONC-002",
                "PRA-1001",
                "USR-10",
                new BigDecimal("700000"),
                "REJECTED",
                "El cupo disponible cambió durante el procesamiento",
                LocalDateTime.now()
        );

        when(solicitudUsoRepository
                .findByReferenciaSolicitud("REF-CONC-002"))
                .thenReturn(Mono.empty());

        when(preaprobadoRepository
                .findByIdPreaprobado("PRA-1001"))
                .thenReturn(Mono.just(preaprobado));

        // Simulamos que otra solicitud consumió el cupo
        // antes de que esta pudiera descontarlo.
        when(preaprobadoRepository.descontarMonto(
                "PRA-1001",
                "USR-10",
                new BigDecimal("700000")
        )).thenReturn(Mono.just(0));

        when(solicitudUsoRepository.save(any(SolicitudUso.class)))
                .thenReturn(Mono.just(solicitudGuardada));

        StepVerifier.create(
                        solicitudUsoService.procesarSolicitud(request)
                )
                .assertNext(response -> {
                    assertEquals("REJECTED", response.estado());
                    assertEquals(
                            "El cupo disponible cambió durante el procesamiento",
                            response.motivo()
                    );
                })
                .verifyComplete();

        verify(preaprobadoRepository, times(1))
                .descontarMonto(
                        "PRA-1001",
                        "USR-10",
                        new BigDecimal("700000")
                );
    }
}