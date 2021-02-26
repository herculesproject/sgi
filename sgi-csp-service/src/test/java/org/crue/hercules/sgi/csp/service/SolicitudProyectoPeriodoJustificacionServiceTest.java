package org.crue.hercules.sgi.csp.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoPeriodoJustificacionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoSocioNotFoundException;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoDatos;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioRepository;
import org.crue.hercules.sgi.csp.service.impl.SolicitudProyectoPeriodoJustificacionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * SolicitudProyectoPeriodoJustificacionServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class SolicitudProyectoPeriodoJustificacionServiceTest {

  @Mock
  private SolicitudProyectoPeriodoJustificacionRepository repository;

  @Mock
  private SolicitudProyectoSocioRepository solicitudProyectoSocioRepository;

  @Mock
  private SolicitudService solicitudService;

  private SolicitudProyectoPeriodoJustificacionService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new SolicitudProyectoPeriodoJustificacionServiceImpl(repository, solicitudProyectoSocioRepository,
        solicitudService);
  }

  @Test
  public void update_ReturnsSolicitudProyectoPeriodoJustificacion() {
    // given: una lista con uno de los SolicitudProyectoPeriodoJustificacion
    // actualizado,
    // otro nuevo y sin el otros existente
    Long solicitudProyectoSocioId = 1L;

    List<SolicitudProyectoPeriodoJustificacion> solicitudProyectoPeriodoJustificacionExistentes = new ArrayList<>();
    solicitudProyectoPeriodoJustificacionExistentes.add(generarSolicitudProyectoPeriodoJustificacion(2L, 1L));
    solicitudProyectoPeriodoJustificacionExistentes.add(generarSolicitudProyectoPeriodoJustificacion(4L, 1L));
    solicitudProyectoPeriodoJustificacionExistentes.add(generarSolicitudProyectoPeriodoJustificacion(5L, 1L));

    SolicitudProyectoPeriodoJustificacion newSolicitudProyectoPeriodoJustificacion = generarSolicitudProyectoPeriodoJustificacion(
        null, 1L);
    newSolicitudProyectoPeriodoJustificacion.setMesInicial(1);
    newSolicitudProyectoPeriodoJustificacion.setMesFinal(3);
    SolicitudProyectoPeriodoJustificacion updatedSolicitudProyectoPeriodoJustificacion = generarSolicitudProyectoPeriodoJustificacion(
        4L, 1L);
    updatedSolicitudProyectoPeriodoJustificacion.setMesInicial(6);
    updatedSolicitudProyectoPeriodoJustificacion.setMesFinal(10);

    List<SolicitudProyectoPeriodoJustificacion> solicitudProyectoPeriodoJustificacionActualizar = new ArrayList<>();
    solicitudProyectoPeriodoJustificacionActualizar.add(newSolicitudProyectoPeriodoJustificacion);
    solicitudProyectoPeriodoJustificacionActualizar.add(updatedSolicitudProyectoPeriodoJustificacion);

    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(newSolicitudProyectoPeriodoJustificacion.getSolicitudProyectoSocio()));

    // TODO is editable solicitud

    BDDMockito.given(repository.findAllBySolicitudProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(solicitudProyectoPeriodoJustificacionExistentes);

    BDDMockito.doNothing().when(repository)
        .deleteAll(ArgumentMatchers.<SolicitudProyectoPeriodoJustificacion>anyList());

    BDDMockito.given(repository.saveAll(ArgumentMatchers.<SolicitudProyectoPeriodoJustificacion>anyList()))
        .will((InvocationOnMock invocation) -> {
          List<SolicitudProyectoPeriodoJustificacion> periodosPagos = invocation.getArgument(0);
          return periodosPagos.stream().map(periodoPago -> {
            if (periodoPago.getId() == null) {
              periodoPago.setId(6L);
            }
            periodoPago.getSolicitudProyectoSocio().setId(solicitudProyectoSocioId);
            return periodoPago;
          }).collect(Collectors.toList());
        });

    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: se actualiza solicitudProyectoPeriodoJustificacionActualizar
    List<SolicitudProyectoPeriodoJustificacion> periodosPagoActualizados = service.update(solicitudProyectoSocioId,
        solicitudProyectoPeriodoJustificacionActualizar);

    // then: El SolicitudProyectoPeriodoJustificacion se actualiza correctamente.

    // then: Se crea el nuevo ConvocatoriaPeriodoJustificacion, se actualiza el
    // existe y se elimina el otro
    Assertions.assertThat(periodosPagoActualizados.get(0).getId()).as("get(0).getId()").isEqualTo(6L);
    Assertions.assertThat(periodosPagoActualizados.get(0).getSolicitudProyectoSocio().getId())
        .as("get(0).getSolicitudProyectoSocio().getId()").isEqualTo(solicitudProyectoSocioId);
    Assertions.assertThat(periodosPagoActualizados.get(0).getMesInicial()).as("get(0).getMesInicial()")
        .isEqualTo(newSolicitudProyectoPeriodoJustificacion.getMesInicial());
    Assertions.assertThat(periodosPagoActualizados.get(0).getMesFinal()).as("get(0).getMesFinal()")
        .isEqualTo(newSolicitudProyectoPeriodoJustificacion.getMesFinal());
    Assertions.assertThat(periodosPagoActualizados.get(0).getFechaInicio()).as("get(0).getFechaInicio()")
        .isEqualTo(newSolicitudProyectoPeriodoJustificacion.getFechaInicio());
    Assertions.assertThat(periodosPagoActualizados.get(0).getFechaFin()).as("get(0).getFechaFin()")
        .isEqualTo(newSolicitudProyectoPeriodoJustificacion.getFechaFin());
    Assertions.assertThat(periodosPagoActualizados.get(0).getNumPeriodo()).as("get(0).getNumPeriodo()")
        .isEqualTo(newSolicitudProyectoPeriodoJustificacion.getNumPeriodo());
    Assertions.assertThat(periodosPagoActualizados.get(0).getObservaciones()).as("get(0).getObservaciones()")
        .isEqualTo(newSolicitudProyectoPeriodoJustificacion.getObservaciones());

    Assertions.assertThat(periodosPagoActualizados.get(1).getId()).as("get(1).getId()")
        .isEqualTo(updatedSolicitudProyectoPeriodoJustificacion.getId());
    Assertions.assertThat(periodosPagoActualizados.get(1).getSolicitudProyectoSocio().getId())
        .as("get(1).getSolicitudProyectoSocio().getId()").isEqualTo(solicitudProyectoSocioId);
    Assertions.assertThat(periodosPagoActualizados.get(1).getMesInicial()).as("get(1).getMesInicial()")
        .isEqualTo(updatedSolicitudProyectoPeriodoJustificacion.getMesInicial());
    Assertions.assertThat(periodosPagoActualizados.get(1).getMesFinal()).as("get(1).getMesFinal()")
        .isEqualTo(updatedSolicitudProyectoPeriodoJustificacion.getMesFinal());
    Assertions.assertThat(periodosPagoActualizados.get(1).getFechaInicio()).as("get(1).getFechaInicio()")
        .isEqualTo(updatedSolicitudProyectoPeriodoJustificacion.getFechaInicio());
    Assertions.assertThat(periodosPagoActualizados.get(1).getFechaFin()).as("get(1).getFechaFin()")
        .isEqualTo(updatedSolicitudProyectoPeriodoJustificacion.getFechaFin());
    Assertions.assertThat(periodosPagoActualizados.get(1).getNumPeriodo()).as("get(1).getNumPeriodo()")
        .isEqualTo(updatedSolicitudProyectoPeriodoJustificacion.getNumPeriodo());
    Assertions.assertThat(periodosPagoActualizados.get(1).getObservaciones()).as("get(1).getObservaciones()")
        .isEqualTo(updatedSolicitudProyectoPeriodoJustificacion.getObservaciones());

    Mockito.verify(repository, Mockito.times(1))
        .deleteAll(ArgumentMatchers.<SolicitudProyectoPeriodoJustificacion>anyList());
    Mockito.verify(repository, Mockito.times(1))
        .saveAll(ArgumentMatchers.<SolicitudProyectoPeriodoJustificacion>anyList());

  }

  @Test
  public void update_WithSolicitudProyectoSocioNotExist_ThrowsSolicitudProyectoSocioNotFoundException() {
    // given: a SolicitudProyectoPeriodoJustificacion with non existing
    // SolicitudProyectoSocio
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyectoPeriodoJustificacion solicitudProyectoPeriodoJustificacion = generarSolicitudProyectoPeriodoJustificacion(
        1L, 1L);

    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaEntidadGestora
        () -> service.update(solicitudProyectoSocioId, Arrays.asList(solicitudProyectoPeriodoJustificacion)))
        // then: throw exception as SolicitudProyectoSocio is not found
        .isInstanceOf(SolicitudProyectoSocioNotFoundException.class);
  }

  @Test
  public void update_WithIdNotExist_ThrowsSolicitudProyectoPeriodoJustificacionNotFoundException() {
    // given: Un SolicitudProyectoPeriodoJustificacion actualizado con un id que no
    // existe
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyectoPeriodoJustificacion solicitudProyectoPeriodoJustificacion = generarSolicitudProyectoPeriodoJustificacion(
        1L, 1L);

    List<SolicitudProyectoPeriodoJustificacion> solicitudProyectoPeriodoJustificacionExistentes = new ArrayList<>();
    solicitudProyectoPeriodoJustificacionExistentes.add(generarSolicitudProyectoPeriodoJustificacion(3L, 1L));

    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoPeriodoJustificacion.getSolicitudProyectoSocio()));

    BDDMockito.given(repository.findAllBySolicitudProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(solicitudProyectoPeriodoJustificacionExistentes);

    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoPeriodoJustificacion
    // then: Lanza una excepcion porque el SolicitudProyectoPeriodoJustificacion no
    // existe
    Assertions
        .assertThatThrownBy(
            () -> service.update(solicitudProyectoSocioId, Arrays.asList(solicitudProyectoPeriodoJustificacion)))
        .isInstanceOf(SolicitudProyectoPeriodoJustificacionNotFoundException.class);
  }

  @Test
  public void update_WithSolicitudProyectoSocioChange_ThrowsIllegalArgumentException() {
    // given:Se actualiza SolicitudProyectoSocio
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyectoPeriodoJustificacion solicitudProyectoPeriodoJustificacion = generarSolicitudProyectoPeriodoJustificacion(
        1L, 1L);

    solicitudProyectoPeriodoJustificacion.getSolicitudProyectoSocio().setId(2L);

    List<SolicitudProyectoPeriodoJustificacion> solicitudProyectoPeriodoJustificacionExistentes = new ArrayList<>();
    solicitudProyectoPeriodoJustificacionExistentes.add(generarSolicitudProyectoPeriodoJustificacion(1L, 1L));

    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoPeriodoJustificacion.getSolicitudProyectoSocio()));

    BDDMockito.given(repository.findAllBySolicitudProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(solicitudProyectoPeriodoJustificacionExistentes);

    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoSocio del
    // SolicitudProyectoPeriodoJustificacion
    // then: Lanza una excepcion porque no se puede modificar el campo
    // SolicitudProyectoSocio
    Assertions
        .assertThatThrownBy(
            () -> service.update(solicitudProyectoSocioId, Arrays.asList(solicitudProyectoPeriodoJustificacion)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("No se puede modificar la solicitud proyecto socio del SolicitudProyectoPeriodoJustificacion");
  }

  @Test
  public void update_WithMesSolapamiento_ThrowsIllegalArgumentException() {
    // given: Se actualiza SolicitudProyectoPeriodoJustificacion cuyo mes es
    // superior a la
    // duración de solicitud proyecto datos
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyectoPeriodoJustificacion solicitudProyectoPeriodoJustificacion1 = generarSolicitudProyectoPeriodoJustificacion(
        1L, 1L);
    SolicitudProyectoPeriodoJustificacion solicitudProyectoPeriodoJustificacion2 = generarSolicitudProyectoPeriodoJustificacion(
        2L, 1L);

    List<SolicitudProyectoPeriodoJustificacion> solicitudProyectoPeriodoJustificacionExistentes = new ArrayList<>();
    solicitudProyectoPeriodoJustificacionExistentes.add(generarSolicitudProyectoPeriodoJustificacion(1L, 1L));
    solicitudProyectoPeriodoJustificacionExistentes.add(generarSolicitudProyectoPeriodoJustificacion(2L, 1L));

    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoPeriodoJustificacion1.getSolicitudProyectoSocio()));

    BDDMockito.given(repository.findAllBySolicitudProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(solicitudProyectoPeriodoJustificacionExistentes);

    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoPeriodoJustificacion
    // then: Lanza una excepcion porque el mes es superior a la duración de
    // solicitud proyecto datos
    Assertions
        .assertThatThrownBy(() -> service.update(solicitudProyectoSocioId,
            Arrays.asList(solicitudProyectoPeriodoJustificacion1, solicitudProyectoPeriodoJustificacion2)))
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El periodo se solapa con otro existente");
  }

  @Test
  public void update_WithMesInicialPosteriorMesFinal_ThrowsIllegalArgumentException() {
    // given: Se actualiza SolicitudProyectoPeriodoJustificacion cuyo mes inicial es
    // posterior al mes final
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyectoPeriodoJustificacion solicitudProyectoPeriodoJustificacion1 = generarSolicitudProyectoPeriodoJustificacion(
        1L, 1L);
    SolicitudProyectoPeriodoJustificacion solicitudProyectoPeriodoJustificacion2 = generarSolicitudProyectoPeriodoJustificacion(
        2L, 1L);

    solicitudProyectoPeriodoJustificacion1.setMesInicial(6);

    solicitudProyectoPeriodoJustificacion1.setMesFinal(5);

    List<SolicitudProyectoPeriodoJustificacion> solicitudProyectoPeriodoJustificacionExistentes = new ArrayList<>();
    solicitudProyectoPeriodoJustificacionExistentes.add(generarSolicitudProyectoPeriodoJustificacion(1L, 1L));
    solicitudProyectoPeriodoJustificacionExistentes.add(generarSolicitudProyectoPeriodoJustificacion(2L, 1L));

    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoPeriodoJustificacion1.getSolicitudProyectoSocio()));

    BDDMockito.given(repository.findAllBySolicitudProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(solicitudProyectoPeriodoJustificacionExistentes);

    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoPeriodoJustificacion
    // then: Lanza una excepcion porque mes inicial es
    // posterior al mes final
    Assertions
        .assertThatThrownBy(() -> service.update(solicitudProyectoSocioId,
            Arrays.asList(solicitudProyectoPeriodoJustificacion1, solicitudProyectoPeriodoJustificacion2)))
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El mes final tiene que ser posterior al mes inicial");
  }

  @Test
  public void update_WithFechaInicioPosteriorFechaFin_ThrowsIllegalArgumentException() {
    // given: Se actualiza SolicitudProyectoPeriodoJustificacion con fecha inicio
    // superior a fecha fin
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyectoPeriodoJustificacion solicitudProyectoPeriodoJustificacion1 = generarSolicitudProyectoPeriodoJustificacion(
        1L, 1L);
    SolicitudProyectoPeriodoJustificacion solicitudProyectoPeriodoJustificacion2 = generarSolicitudProyectoPeriodoJustificacion(
        2L, 1L);

    solicitudProyectoPeriodoJustificacion1.setFechaInicio(LocalDate.of(2021, 12, 19));

    List<SolicitudProyectoPeriodoJustificacion> solicitudProyectoPeriodoJustificacionExistentes = new ArrayList<>();
    solicitudProyectoPeriodoJustificacionExistentes.add(generarSolicitudProyectoPeriodoJustificacion(1L, 1L));
    solicitudProyectoPeriodoJustificacionExistentes.add(generarSolicitudProyectoPeriodoJustificacion(2L, 1L));

    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoPeriodoJustificacion1.getSolicitudProyectoSocio()));

    BDDMockito.given(repository.findAllBySolicitudProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(solicitudProyectoPeriodoJustificacionExistentes);

    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoPeriodoJustificacion
    // then: Lanza una excepcion porque fecha inicio superior a fecha fin
    Assertions
        .assertThatThrownBy(() -> service.update(solicitudProyectoSocioId,
            Arrays.asList(solicitudProyectoPeriodoJustificacion1, solicitudProyectoPeriodoJustificacion2)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La fecha de fin tiene que ser posterior a la fecha de inicio");
  }

  @Test
  public void update_WithMesFinalSuperiorDuracion_ThrowsIllegalArgumentException() {
    // given: Se actualiza SolicitudProyectoPeriodoJustificacion con mes final
    // superior a la duración
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyectoPeriodoJustificacion solicitudProyectoPeriodoJustificacion1 = generarSolicitudProyectoPeriodoJustificacion(
        1L, 1L);
    SolicitudProyectoPeriodoJustificacion solicitudProyectoPeriodoJustificacion2 = generarSolicitudProyectoPeriodoJustificacion(
        2L, 1L);

    solicitudProyectoPeriodoJustificacion2.setMesFinal(20);

    List<SolicitudProyectoPeriodoJustificacion> solicitudProyectoPeriodoJustificacionExistentes = new ArrayList<>();
    solicitudProyectoPeriodoJustificacionExistentes.add(generarSolicitudProyectoPeriodoJustificacion(1L, 1L));
    solicitudProyectoPeriodoJustificacionExistentes.add(generarSolicitudProyectoPeriodoJustificacion(2L, 1L));

    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoPeriodoJustificacion1.getSolicitudProyectoSocio()));

    BDDMockito.given(repository.findAllBySolicitudProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(solicitudProyectoPeriodoJustificacionExistentes);

    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoPeriodoJustificacion
    // then: Lanza una excepcion porque mes final superior a la duración
    Assertions
        .assertThatThrownBy(() -> service.update(solicitudProyectoSocioId,
            Arrays.asList(solicitudProyectoPeriodoJustificacion1, solicitudProyectoPeriodoJustificacion2)))
        .isInstanceOf(IllegalArgumentException.class).hasMessage(
            "El mes final no puede ser superior a la duración en meses indicada en la solicitud proyecto datos");
  }

  @Test
  public void update_WithSolapamientoPeriodo_ThrowsIllegalArgumentException() {
    // given: Se actualiza SolicitudProyectoPeriodoJustificacion con fecha inicio
    // superior a fecha fin
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyectoPeriodoJustificacion solicitudProyectoPeriodoJustificacion1 = generarSolicitudProyectoPeriodoJustificacion(
        1L, 1L);
    SolicitudProyectoPeriodoJustificacion solicitudProyectoPeriodoJustificacion2 = generarSolicitudProyectoPeriodoJustificacion(
        2L, 1L);

    List<SolicitudProyectoPeriodoJustificacion> solicitudProyectoPeriodoJustificacionExistentes = new ArrayList<>();
    solicitudProyectoPeriodoJustificacionExistentes.add(generarSolicitudProyectoPeriodoJustificacion(1L, 1L));
    solicitudProyectoPeriodoJustificacionExistentes.add(generarSolicitudProyectoPeriodoJustificacion(2L, 1L));

    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoPeriodoJustificacion1.getSolicitudProyectoSocio()));

    BDDMockito.given(repository.findAllBySolicitudProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(solicitudProyectoPeriodoJustificacionExistentes);

    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoPeriodoJustificacion
    // then: Lanza una excepcion porque fecha inicio superior a fecha fin
    Assertions
        .assertThatThrownBy(() -> service.update(solicitudProyectoSocioId,
            Arrays.asList(solicitudProyectoPeriodoJustificacion1, solicitudProyectoPeriodoJustificacion2)))
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El periodo se solapa con otro existente");
  }

  @Test
  public void findById_ReturnsSolicitudProyectoPeriodoJustificacion() {
    // given: Un SolicitudProyectoPeriodoJustificacion con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado))
        .willReturn(Optional.of(generarSolicitudProyectoPeriodoJustificacion(idBuscado, 1L)));

    // when: Buscamos el SolicitudProyectoPeriodoJustificacion por su id
    SolicitudProyectoPeriodoJustificacion solicitudProyectoPeriodoJustificacion = service.findById(idBuscado);

    // then: el SolicitudProyectoPeriodoJustificacion
    Assertions.assertThat(solicitudProyectoPeriodoJustificacion).as("isNotNull()").isNotNull();
    Assertions.assertThat(solicitudProyectoPeriodoJustificacion.getId()).as("getId()").isEqualTo(idBuscado);
  }

  @Test
  public void findById_WithIdNotExist_ThrowsSolicitudProyectoPeriodoJustificacionNotFoundException() throws Exception {
    // given: Ningun SolicitudProyectoPeriodoJustificacion con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el SolicitudProyectoPeriodoJustificacion por su id
    // then: lanza un SolicitudProyectoPeriodoJustificacionNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(SolicitudProyectoPeriodoJustificacionNotFoundException.class);
  }

  @Test
  public void findAllBySolicitud_ReturnsPage() {
    // given: Una lista con 37 SolicitudProyectoPeriodoJustificacion
    Long solicitudProyectoSocioId = 1L;
    List<SolicitudProyectoPeriodoJustificacion> solicitudProyectoPeriodoJustificacion = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      solicitudProyectoPeriodoJustificacion.add(generarSolicitudProyectoPeriodoJustificacion(i, i));
    }

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<SolicitudProyectoPeriodoJustificacion>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<SolicitudProyectoPeriodoJustificacion>>() {
          @Override
          public Page<SolicitudProyectoPeriodoJustificacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > solicitudProyectoPeriodoJustificacion.size()
                ? solicitudProyectoPeriodoJustificacion.size()
                : toIndex;
            List<SolicitudProyectoPeriodoJustificacion> content = solicitudProyectoPeriodoJustificacion
                .subList(fromIndex, toIndex);
            Page<SolicitudProyectoPeriodoJustificacion> page = new PageImpl<>(content, pageable,
                solicitudProyectoPeriodoJustificacion.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<SolicitudProyectoPeriodoJustificacion> page = service.findAllBySolicitudProyectoSocio(solicitudProyectoSocioId,
        null, paging);

    // then: Devuelve la pagina 3 con los Programa del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      SolicitudProyectoPeriodoJustificacion solicitudProyectoPeriodoJustificacionRecuperado = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(solicitudProyectoPeriodoJustificacionRecuperado.getId()).isEqualTo(i);
    }
  }

  /**
   * Función que devuelve un objeto SolicitudProyectoPeriodoJustificacion
   * 
   * @param solicitudProyectoPeriodoJustificacionId
   * @param solicitudProyectoSocioId
   * @return el objeto SolicitudProyectoPeriodoJustificacion
   */
  private SolicitudProyectoPeriodoJustificacion generarSolicitudProyectoPeriodoJustificacion(
      Long solicitudProyectoPeriodoJustificacionId, Long solicitudProyectoSocioId) {

    SolicitudProyectoPeriodoJustificacion solicitudProyectoPeriodoJustificacion = SolicitudProyectoPeriodoJustificacion
        .builder().id(solicitudProyectoPeriodoJustificacionId)
        .solicitudProyectoSocio(SolicitudProyectoSocio.builder().id(solicitudProyectoSocioId)
            .solicitudProyectoDatos(SolicitudProyectoDatos.builder().id(1L)
                .solicitud(Solicitud.builder().id(1L).activo(Boolean.TRUE).build()).build())
            .build())
        .numPeriodo(2).mesInicial(1).mesFinal(3).fechaInicio(LocalDate.of(2020, 12, 19))
        .fechaFin(LocalDate.of(2021, 2, 9)).observaciones("Periodo 1").build();

    solicitudProyectoPeriodoJustificacion.getSolicitudProyectoSocio().getSolicitudProyectoDatos().getSolicitud()
        .setEstado(new EstadoSolicitud());
    solicitudProyectoPeriodoJustificacion.getSolicitudProyectoSocio().getSolicitudProyectoDatos().getSolicitud()
        .getEstado().setEstado(EstadoSolicitud.Estado.BORRADOR);

    solicitudProyectoPeriodoJustificacion.getSolicitudProyectoSocio().getSolicitudProyectoDatos().setDuracion(15);
    return solicitudProyectoPeriodoJustificacion;
  }

}
