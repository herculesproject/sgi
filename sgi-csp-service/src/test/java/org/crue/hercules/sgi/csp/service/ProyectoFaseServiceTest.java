package org.crue.hercules.sgi.csp.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ProyectoFaseNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoFase;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.repository.ModeloTipoFaseRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoFaseRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.service.impl.ProyectoFaseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * ProyectoFaseServiceTest
 */

public class ProyectoFaseServiceTest extends BaseServiceTest {

  @Mock
  private ProyectoFaseRepository repository;

  @Mock
  private ProyectoRepository proyectoRepository;

  @Mock
  private ModeloTipoFaseRepository modeloTipoFaseRepository;

  private ProyectoFaseService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ProyectoFaseServiceImpl(repository, proyectoRepository, modeloTipoFaseRepository);
  }

  @Test
  public void create_ReturnsProyectoFase() {
    // given: Un nuevo ProyectoFase
    ProyectoFase proyectoFase = generarMockProyectoFase(1L);
    proyectoFase.setId(null);

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoFase.getProyecto().getModeloEjecucion()));
    BDDMockito
        .given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockModeloTipoFase(1L, proyectoFase, Boolean.TRUE)));
    BDDMockito
        .given(
            repository.findAll(ArgumentMatchers.<Specification<ProyectoFase>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<ProyectoFase> page = new PageImpl<>(new ArrayList<ProyectoFase>(), pageable, 0);
          return page;
        });

    BDDMockito.given(repository.save(proyectoFase)).will((InvocationOnMock invocation) -> {
      ProyectoFase proyectoFaseCreado = invocation.getArgument(0);
      proyectoFaseCreado.setId(1L);
      return proyectoFaseCreado;
    });

    // when: Creamos el ProyectoFase
    ProyectoFase proyectoFaseCreado = service.create(proyectoFase);

    // then: El ProyectoFase se crea correctamente
    Assertions.assertThat(proyectoFaseCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoFaseCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(proyectoFaseCreado.getProyecto().getId()).as("getProyecto().getId()")
        .isEqualTo(proyectoFase.getProyecto().getId());
    Assertions.assertThat(proyectoFaseCreado.getFechaInicio()).as("getFechaInicio()")
        .isEqualTo(proyectoFase.getFechaInicio());
    Assertions.assertThat(proyectoFaseCreado.getFechaFin()).as("getFechaFin()").isEqualTo(proyectoFase.getFechaFin());
    Assertions.assertThat(proyectoFaseCreado.getObservaciones()).as("getObservaciones()")
        .isEqualTo(proyectoFase.getObservaciones());
    Assertions.assertThat(proyectoFaseCreado.getTipoFase().getId()).as("getTipoFase().getId()")
        .isEqualTo(proyectoFase.getTipoFase().getId());
    Assertions.assertThat(proyectoFaseCreado.getGeneraAviso()).as("getGeneraAviso()")
        .isEqualTo(proyectoFase.getGeneraAviso());
  }

  @Test
  public void create_WithRangoFechasAnterior_ReturnsProyectoFaseWithGeneraAvisoFalse() {
    // given: Un nuevo ProyectoFase con rango de fechas pasado
    ProyectoFase proyectoFase = generarMockProyectoFase(1L);
    proyectoFase.setId(null);
    proyectoFase.setFechaInicio(LocalDateTime.now().minusDays(2));
    proyectoFase.setFechaFin(LocalDateTime.now().minusDays(1));
    proyectoFase.setGeneraAviso(Boolean.TRUE);

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoFase.getProyecto().getModeloEjecucion()));
    BDDMockito
        .given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockModeloTipoFase(1L, proyectoFase, Boolean.TRUE)));
    BDDMockito
        .given(
            repository.findAll(ArgumentMatchers.<Specification<ProyectoFase>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<ProyectoFase> page = new PageImpl<>(new ArrayList<ProyectoFase>(), pageable, 0);
          return page;
        });

    BDDMockito.given(repository.save(proyectoFase)).will((InvocationOnMock invocation) -> {
      ProyectoFase proyectoFaseCreado = invocation.getArgument(0);
      proyectoFaseCreado.setId(1L);
      return proyectoFaseCreado;
    });

    // when: Creamos el ProyectoFase
    ProyectoFase proyectoFaseCreado = service.create(proyectoFase);

    // then: El ProyectoFase se crea correctamente
    Assertions.assertThat(proyectoFaseCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoFaseCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(proyectoFaseCreado.getProyecto().getId()).as("getProyecto().getId()")
        .isEqualTo(proyectoFase.getProyecto().getId());
    Assertions.assertThat(proyectoFaseCreado.getFechaInicio()).as("getFechaInicio()")
        .isEqualTo(proyectoFase.getFechaInicio());
    Assertions.assertThat(proyectoFaseCreado.getFechaFin()).as("getFechaFin()").isEqualTo(proyectoFase.getFechaFin());
    Assertions.assertThat(proyectoFaseCreado.getObservaciones()).as("getObservaciones()")
        .isEqualTo(proyectoFase.getObservaciones());
    Assertions.assertThat(proyectoFaseCreado.getTipoFase().getId()).as("getTipoFase().getId()")
        .isEqualTo(proyectoFase.getTipoFase().getId());
    Assertions.assertThat(proyectoFaseCreado.getGeneraAviso()).as("getGeneraAviso()").isEqualTo(Boolean.FALSE);
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo ProyectoFase que ya tiene id
    ProyectoFase proyectoFase = generarMockProyectoFase(1L);
    // when: Creamos el ProyectoFase
    // then: Lanza una excepcion porque el ProyectoFase ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(proyectoFase)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ProyectoFase id tiene que ser null para crear un nuevo ProyectoFase");
  }

  @Test
  public void create_WithoutProyectoId_ThrowsIllegalArgumentException() {
    // given: a ProyectoFase without ProyectoId
    ProyectoFase proyectoFase = generarMockProyectoFase(1L);
    proyectoFase.setId(null);
    proyectoFase.setProyecto(null);

    Assertions.assertThatThrownBy(
        // when: create ProyectoFase
        () -> service.create(proyectoFase))
        // then: throw exception as ProyectoId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id Proyecto no puede ser null para realizar la acción sobre ProyectoFase");
  }

  @Test
  public void create_WithoutTipoFaseId_ThrowsIllegalArgumentException() {
    // given: a ProyectoFase without TipoFaseId
    ProyectoFase proyectoFase = generarMockProyectoFase(1L);
    proyectoFase.setId(null);
    proyectoFase.setTipoFase(null);

    Assertions.assertThatThrownBy(
        // when: create ProyectoFase
        () -> service.create(proyectoFase))
        // then: throw exception as TipoFaseId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id Tipo Fase no puede ser null para realizar la acción sobre ProyectoFase");
  }

  @Test
  public void create_WithoutFechas_ThrowsIllegalArgumentException() {
    // given: a ProyectoFase without Fecha
    ProyectoFase proyectoFase = generarMockProyectoFase(1L);
    proyectoFase.setId(null);
    proyectoFase.setFechaInicio(null);
    proyectoFase.setFechaFin(null);

    Assertions.assertThatThrownBy(
        // when: create ProyectoFase
        () -> service.create(proyectoFase))
        // then: throw exception as Fecha is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Debe indicarse al menos una fecha para realizar la acción sobre ProyectoFase");
  }

  @Test
  public void create_WithFechaInicioGreaterThanFechaFin_ThrowsIllegalArgumentException() {
    // given: Fecha Inicio > fechaFin
    ProyectoFase proyectoFase = generarMockProyectoFase(1L);
    proyectoFase.setId(null);
    proyectoFase.setFechaInicio(proyectoFase.getFechaFin().plusDays(1));

    // when: create ProyectoFase
    // then: throw exception as Inicio > fechaFin
    Assertions.assertThatThrownBy(() -> service.create(proyectoFase)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La fecha de fin debe ser posterior a la fecha de inicio");
  }

  @Test
  public void create_WithNoExistingProyecto_ThrowsProyectoNotFoundException() {
    // given: a ProyectoFase with non existing Proyecto
    ProyectoFase proyectoFase = generarMockProyectoFase(1L);
    proyectoFase.setId(null);

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: create ProyectoFase
        () -> service.create(proyectoFase))
        // then: throw exception as Proyecto is not found
        .isInstanceOf(ProyectoNotFoundException.class);
  }

  @Test
  public void create_WithoutModeloEjecucion_ThrowsIllegalArgumentException() {
    // given: ProyectoFase con Proyecto sin Modelo de Ejecucion
    ProyectoFase proyectoFase = generarMockProyectoFase(1L);
    proyectoFase.setId(null);
    proyectoFase.getProyecto().setModeloEjecucion(null);

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);

    Assertions.assertThatThrownBy(
        // when: create ProyectoFase
        () -> service.create(proyectoFase))
        // then: throw exception as ModeloEjecucion not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoFase '%s' no disponible para el ModeloEjecucion '%s'", proyectoFase.getTipoFase().getNombre(),
            "Proyecto sin modelo asignado");
  }

  @Test
  public void create_WithoutModeloTipoFase_ThrowsIllegalArgumentException() {
    // given: ProyectoFase con TipoFase no asignado al Modelo de Ejecucion de la
    // proyecto
    ProyectoFase proyectoFase = generarMockProyectoFase(1L);
    proyectoFase.setId(null);

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: create ProyectoFase
        () -> service.create(proyectoFase))
        // then: throw exception as ModeloTipoFase not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoFase '%s' no disponible para el ModeloEjecucion '%s'", proyectoFase.getTipoFase().getNombre(),
            "Proyecto sin modelo asignado");
  }

  @Test
  public void create_WithDisabledModeloTipoFase_ThrowsIllegalArgumentException() {
    // given: ProyectoFase con la asignación de TipoFase al Modelo de Ejecucion
    // de la proyecto inactiva
    ProyectoFase proyectoFase = generarMockProyectoFase(1L);
    proyectoFase.setId(null);

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoFase.getProyecto().getModeloEjecucion()));
    BDDMockito
        .given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockModeloTipoFase(1L, proyectoFase, Boolean.FALSE)));

    Assertions.assertThatThrownBy(
        // when: create ProyectoFase
        () -> service.create(proyectoFase))
        // then: throw exception as ModeloTipoFase is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloTipoFase '%s' no está activo para el ModeloEjecucion '%s'",
            proyectoFase.getTipoFase().getNombre(), proyectoFase.getProyecto().getModeloEjecucion().getNombre());
  }

  @Test
  public void create_WithDisabledTipoFase_ThrowsIllegalArgumentException() {
    // given: ProyectoFase TipoFase disabled
    ProyectoFase proyectoFase = generarMockProyectoFase(1L);
    proyectoFase.setId(null);
    proyectoFase.getTipoFase().setActivo(Boolean.FALSE);

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoFase.getProyecto().getModeloEjecucion()));
    BDDMockito
        .given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockModeloTipoFase(1L, proyectoFase, Boolean.TRUE)));

    Assertions.assertThatThrownBy(
        // when: create ProyectoFase
        () -> service.create(proyectoFase))
        // then: throw exception as TipoFase is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoFase '%s' no está activo", proyectoFase.getTipoFase().getNombre());
  }

  @Test
  public void create_WithExistingDateOverlap_ThrowsIllegalArgumentException() {
    // given: a existing ProyectoFase with date ranges overlapping
    ProyectoFase proyectoFaseExistente = generarMockProyectoFase(2L);
    proyectoFaseExistente.setFechaInicio(LocalDateTime.of(2020, 10, 18, 0, 0, 0));
    proyectoFaseExistente.setFechaFin(LocalDateTime.of(2020, 10, 22, 23, 59, 59));

    ProyectoFase proyectoFase = generarMockProyectoFase(1L);
    proyectoFase.setId(null);

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoFase.getProyecto().getModeloEjecucion()));
    BDDMockito
        .given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockModeloTipoFase(1L, proyectoFase, Boolean.TRUE)));
    BDDMockito
        .given(
            repository.findAll(ArgumentMatchers.<Specification<ProyectoFase>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<ProyectoFase> page = new PageImpl<>(Arrays.asList(proyectoFaseExistente), pageable, 0);
          return page;
        });

    Assertions.assertThatThrownBy(
        // when: create ConvocatoriaFase
        () -> service.create(proyectoFase))
        // then: throw exception as date overlaps
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un registro para la misma Fase en ese rango de fechas");
  }

  @Test
  public void update_ReturnsProyectoFase() {
    // given: Un nuevo ProyectoFase con el tipoFase actualizado
    ProyectoFase proyectoFase = generarMockProyectoFase(1L);
    ProyectoFase proyectoFaseActualizado = generarMockProyectoFase(1L);
    proyectoFaseActualizado.setTipoFase(generarMockTipoFase(2L, Boolean.TRUE));

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyectoFase));

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoFase.getProyecto().getModeloEjecucion()));
    BDDMockito
        .given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockModeloTipoFase(1L, proyectoFaseActualizado, Boolean.TRUE)));
    BDDMockito
        .given(
            repository.findAll(ArgumentMatchers.<Specification<ProyectoFase>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<ProyectoFase> page = new PageImpl<>(new ArrayList<ProyectoFase>(), pageable, 0);
          return page;
        });

    BDDMockito.given(repository.save(ArgumentMatchers.<ProyectoFase>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el ProyectoFase
    ProyectoFase updated = service.update(proyectoFaseActualizado);

    // then: El ProyectoFase se actualiza correctamente.
    Assertions.assertThat(updated).as("isNotNull()").isNotNull();
    Assertions.assertThat(updated.getId()).as("getId()").isEqualTo(proyectoFase.getId());
    Assertions.assertThat(updated.getProyecto().getId()).as("getProyecto().getId()")
        .isEqualTo(proyectoFase.getProyecto().getId());
    Assertions.assertThat(updated.getObservaciones()).as("getObservaciones()")
        .isEqualTo(proyectoFaseActualizado.getObservaciones());
    Assertions.assertThat(updated.getTipoFase().getId()).as("getTipoFase().getId()")
        .isEqualTo(proyectoFaseActualizado.getTipoFase().getId());
    Assertions.assertThat(updated.getFechaInicio()).as("getFechaInicio()")
        .isEqualTo(proyectoFaseActualizado.getFechaInicio());
    Assertions.assertThat(updated.getFechaFin()).as("getFechaFin()").isEqualTo(proyectoFaseActualizado.getFechaFin());
    Assertions.assertThat(updated.getGeneraAviso()).as("getGeneraAviso()")
        .isEqualTo(proyectoFaseActualizado.getGeneraAviso());
  }

  @Test
  public void update_WithRangoFechasAnterior_ReturnsProyectoFaseWithGeneraAvisoFalse() {
    // given: Un nuevo ProyectoFase con el tipoFase actualizado
    ProyectoFase proyectoFase = generarMockProyectoFase(1L);
    ProyectoFase proyectoFaseActualizado = generarMockProyectoFase(1L);
    proyectoFaseActualizado.setTipoFase(generarMockTipoFase(2L, Boolean.TRUE));
    proyectoFaseActualizado.setFechaInicio(LocalDateTime.now().minusDays(2));
    proyectoFaseActualizado.setFechaFin(LocalDateTime.now().minusDays(1));
    proyectoFaseActualizado.setGeneraAviso(Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyectoFase));

    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoFase.getProyecto().getModeloEjecucion()));
    BDDMockito
        .given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockModeloTipoFase(1L, proyectoFaseActualizado, Boolean.TRUE)));
    BDDMockito
        .given(
            repository.findAll(ArgumentMatchers.<Specification<ProyectoFase>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<ProyectoFase> page = new PageImpl<>(new ArrayList<ProyectoFase>(), pageable, 0);
          return page;
        });

    BDDMockito.given(repository.save(ArgumentMatchers.<ProyectoFase>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Actualizamos el ProyectoFase
    ProyectoFase updated = service.update(proyectoFaseActualizado);

    // then: El ProyectoFase se actualiza correctamente.
    Assertions.assertThat(updated).as("isNotNull()").isNotNull();
    Assertions.assertThat(updated.getId()).as("getId()").isEqualTo(proyectoFase.getId());
    Assertions.assertThat(updated.getProyecto().getId()).as("getProyecto().getId()")
        .isEqualTo(proyectoFase.getProyecto().getId());
    Assertions.assertThat(updated.getObservaciones()).as("getObservaciones()")
        .isEqualTo(proyectoFaseActualizado.getObservaciones());
    Assertions.assertThat(updated.getTipoFase().getId()).as("getTipoFase().getId()")
        .isEqualTo(proyectoFaseActualizado.getTipoFase().getId());
    Assertions.assertThat(updated.getFechaInicio()).as("getFechaInicio()")
        .isEqualTo(proyectoFaseActualizado.getFechaInicio());
    Assertions.assertThat(updated.getFechaFin()).as("getFechaFin()").isEqualTo(proyectoFaseActualizado.getFechaFin());
    Assertions.assertThat(updated.getGeneraAviso()).as("getGeneraAviso()").isEqualTo(Boolean.FALSE);
  }

  @Test
  public void update_WithIdNotExist_ThrowsProyectoFaseNotFoundException() {
    // given: Un ProyectoFase a actualizar con un id que no existe
    ProyectoFase proyectoFase = generarMockProyectoFase(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    // when: Actualizamos el ProyectoFase
    // then: Lanza una excepcion porque el ProyectoFase no existe
    Assertions.assertThatThrownBy(() -> service.update(proyectoFase)).isInstanceOf(ProyectoFaseNotFoundException.class);
  }

  @Test
  public void update_WithoutProyectoId_ThrowsIllegalArgumentException() {
    // given: a ProyectoFase without ProyectoId
    ProyectoFase proyectoFase = generarMockProyectoFase(1L);
    proyectoFase.setObservaciones("observaciones modificado");
    proyectoFase.setProyecto(null);

    Assertions.assertThatThrownBy(
        // when: update ProyectoFase
        () -> service.update(proyectoFase))
        // then: throw exception as ProyectoId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id Proyecto no puede ser null para realizar la acción sobre ProyectoFase");
  }

  @Test
  public void update_WithoutTipoFaseId_ThrowsIllegalArgumentException() {
    // given: a ProyectoFase without TipoFaseId
    ProyectoFase proyectoFase = generarMockProyectoFase(1L);
    proyectoFase.setObservaciones("observaciones modificado");
    proyectoFase.setTipoFase(null);

    Assertions.assertThatThrownBy(
        // when: update ProyectoFase
        () -> service.update(proyectoFase))
        // then: throw exception as TipoFaseId is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id Tipo Fase no puede ser null para realizar la acción sobre ProyectoFase");
  }

  @Test
  public void update_WithoutFecha_ThrowsIllegalArgumentException() {
    // given: a ProyectoFase without Fecha
    ProyectoFase proyectoFase = generarMockProyectoFase(1L);
    proyectoFase.setObservaciones("observaciones modificado");
    proyectoFase.setFechaInicio(null);
    proyectoFase.setFechaFin(null);

    Assertions.assertThatThrownBy(
        // when: update ProyectoFase
        () -> service.update(proyectoFase))
        // then: throw exception as Fecha is null
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Debe indicarse al menos una fecha para realizar la acción sobre ProyectoFase");
  }

  @Test
  public void update_WithFechaInicioGreaterThanFechaFin_ThrowsIllegalArgumentException() {
    // given: Fecha Inicio > fechaFin
    ProyectoFase proyectoFaseOriginal = generarMockProyectoFase(1L);
    ProyectoFase proyectoFase = generarMockProyectoFase(1L);
    proyectoFase.setObservaciones("observaciones modificado");
    proyectoFase.setFechaInicio(proyectoFase.getFechaFin().plusDays(1));

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyectoFaseOriginal));

    Assertions.assertThatThrownBy(
        // when: update ProyectoFase
        () -> service.update(proyectoFase))
        // then: throw exception as Inicio > fechaFin
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La fecha de fin debe ser posterior a la fecha de inicio");
  }

  @Test
  public void update_WithNoExistingProyecto_ThrowsProyectoNotFoundException() {
    // given: a ProyectoFase with non existing Proyecto
    ProyectoFase proyectoFaseOriginal = generarMockProyectoFase(1L);
    ProyectoFase proyectoFase = generarMockProyectoFase(1L);
    proyectoFase.setObservaciones("observaciones modificado");

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyectoFaseOriginal));
    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: update ProyectoFase
        () -> service.update(proyectoFase))
        // then: throw exception as Proyecto is not found
        .isInstanceOf(ProyectoNotFoundException.class);
  }

  @Test
  public void update_WithoutModeloEjecucion_ThrowsIllegalArgumentException() {
    // given: ProyectoFase con Proyecto sin Modelo de Ejecucion
    ProyectoFase proyectoFaseOriginal = generarMockProyectoFase(1L);
    ProyectoFase proyectoFase = generarMockProyectoFase(1L);
    proyectoFase.setObservaciones("observaciones modificado");
    proyectoFase.getProyecto().setModeloEjecucion(null);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyectoFaseOriginal));
    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);

    Assertions.assertThatThrownBy(
        // when: update ProyectoFase
        () -> service.update(proyectoFase))
        // then: throw exception as ModeloEjecucion not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoFase '%s' no disponible para el ModeloEjecucion '%s'", proyectoFase.getTipoFase().getNombre(),
            "Proyecto sin modelo asignado");
  }

  @Test
  public void update_WithoutModeloTipoFase_ThrowsIllegalArgumentException() {
    // given: ProyectoFase con TipoFase no asignado al Modelo de Ejecucion de la
    // proyecto
    ProyectoFase proyectoFaseOriginal = generarMockProyectoFase(1L);
    ProyectoFase proyectoFase = generarMockProyectoFase(1L);
    proyectoFase.setObservaciones("observaciones modificado");

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyectoFaseOriginal));
    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update ProyectoFase
        () -> service.update(proyectoFase))
        // then: throw exception as ModeloTipoFase not found
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoFase '%s' no disponible para el ModeloEjecucion '%s'", proyectoFase.getTipoFase().getNombre(),
            "Proyecto sin modelo asignado");
  }

  @Test
  public void update_WithDisabledModeloTipoFase_ThrowsIllegalArgumentException() {
    // given: ProyectoFase con la asignación de TipoFase al Modelo de Ejecucion
    // de la proyecto inactiva
    ProyectoFase proyectoFaseOriginal = generarMockProyectoFase(1L);
    ProyectoFase proyectoFase = generarMockProyectoFase(1L);
    proyectoFase.setObservaciones("observaciones modificado");

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyectoFaseOriginal));
    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoFase.getProyecto().getModeloEjecucion()));
    BDDMockito
        .given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockModeloTipoFase(1L, proyectoFase, Boolean.FALSE)));

    Assertions.assertThatThrownBy(
        // when: update ProyectoFase
        () -> service.update(proyectoFase))
        // then: throw exception as ModeloTipoFase is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("ModeloTipoFase '%s' no está activo para el ModeloEjecucion '%s'",
            proyectoFase.getTipoFase().getNombre(), proyectoFase.getProyecto().getModeloEjecucion().getNombre());
  }

  @Test
  public void update_WithDisabledTipoFase_ThrowsIllegalArgumentException() {
    // given: ProyectoFase TipoFase disabled
    ProyectoFase proyectoFaseOriginal = generarMockProyectoFase(1L);
    ProyectoFase proyectoFase = generarMockProyectoFase(1L);
    proyectoFase.setObservaciones("observaciones modificado");
    proyectoFase.getTipoFase().setActivo(Boolean.FALSE);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyectoFaseOriginal));
    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoFase.getProyecto().getModeloEjecucion()));
    BDDMockito
        .given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockModeloTipoFase(1L, proyectoFase, Boolean.TRUE)));

    Assertions.assertThatThrownBy(
        // when: update ProyectoFase
        () -> service.update(proyectoFase))
        // then: throw exception as TipoFase is disabled
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("TipoFase '%s' no está activo", proyectoFase.getTipoFase().getNombre());
  }

  @Test
  public void update_WithExistingDateOverlap_ThrowsIllegalArgumentException() {
    // given: a existing ProyectoFase with date ranges overlapping
    ProyectoFase proyectoFaseExistente = generarMockProyectoFase(2L);
    proyectoFaseExistente.setFechaInicio(LocalDateTime.of(2020, 10, 18, 0, 0, 0));
    proyectoFaseExistente.setFechaFin(LocalDateTime.of(2020, 10, 22, 23, 59, 59));

    ProyectoFase proyectoFaseOriginal = generarMockProyectoFase(1L);
    ProyectoFase proyectoFase = generarMockProyectoFase(1L);
    proyectoFase.setObservaciones("observaciones modificado");

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyectoFaseOriginal));
    BDDMockito.given(proyectoRepository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.given(proyectoRepository.getModeloEjecucion(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyectoFase.getProyecto().getModeloEjecucion()));
    BDDMockito
        .given(modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockModeloTipoFase(1L, proyectoFase, Boolean.TRUE)));
    BDDMockito
        .given(
            repository.findAll(ArgumentMatchers.<Specification<ProyectoFase>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          Page<ProyectoFase> page = new PageImpl<>(Arrays.asList(proyectoFaseExistente), pageable, 0);
          return page;
        });

    Assertions.assertThatThrownBy(
        // when: update ProyectoFase
        () -> service.update(proyectoFase))
        // then: throw exception as date overlaps
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un registro para la misma Fase en ese rango de fechas");
  }

  @Test
  public void delete_WithExistingId_NoReturnsAnyException() {
    // given: existing ProyectoFase
    Long id = 1L;

    BDDMockito.given(repository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(repository).deleteById(ArgumentMatchers.<Long>any());

    Assertions.assertThatCode(
        // when: delete by existing id
        () -> service.delete(id))
        // then: no exception is thrown
        .doesNotThrowAnyException();
  }

  @Test
  public void delete_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    Long id = 1L;

    BDDMockito.given(repository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: NotFoundException is thrown
        .isInstanceOf(ProyectoFaseNotFoundException.class);
  }

  @Test
  public void existsById_WithExistingId_ReturnsTRUE() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(repository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.TRUE);

    // when: exists by id
    boolean responseData = service.existsById(id);

    // then: returns TRUE
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData).isTrue();
  }

  @Test
  public void existsById_WithNoExistingId_ReturnsFALSE() throws Exception {
    // given: no existing id
    Long id = 1L;
    BDDMockito.given(repository.existsById(ArgumentMatchers.<Long>any())).willReturn(Boolean.FALSE);

    // when: exists by id
    boolean responseData = service.existsById(id);

    // then: returns TRUE
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData).isFalse();
  }

  @Test
  public void findById_ReturnsProyectoFase() {
    // given: Un ProyectoFase con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.of(generarMockProyectoFase(idBuscado)));

    // when: Buscamos el ProyectoFase por su id
    ProyectoFase proyectoFase = service.findById(idBuscado);

    // then: el ProyectoFase
    Assertions.assertThat(proyectoFase).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoFase.getId()).as("getId()").isEqualTo(idBuscado);
  }

  @Test
  public void findById_WithIdNotExist_ThrowsProyectoFaseNotFoundException() throws Exception {
    // given: Ningun ProyectoFase con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el ProyectoFase por su id
    // then: lanza un ProyectoFaseNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado)).isInstanceOf(ProyectoFaseNotFoundException.class);
  }

  @Test
  public void findAllByProyecto_ReturnsPage() {
    // given: Una lista con 37 ProyectoFase para la Proyecto
    Long proyectoId = 1L;
    List<ProyectoFase> proyectosEntidadesConvocantes = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectosEntidadesConvocantes.add(generarMockProyectoFase(Long.valueOf(i)));
    }

    BDDMockito
        .given(
            repository.findAll(ArgumentMatchers.<Specification<ProyectoFase>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > proyectosEntidadesConvocantes.size() ? proyectosEntidadesConvocantes.size() : toIndex;
          List<ProyectoFase> content = proyectosEntidadesConvocantes.subList(fromIndex, toIndex);
          Page<ProyectoFase> pageResponse = new PageImpl<>(content, pageable, proyectosEntidadesConvocantes.size());
          return pageResponse;

        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ProyectoFase> page = service.findAllByProyecto(proyectoId, null, paging);

    // then: Devuelve la pagina 3 con los ProyectoFase del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ProyectoFase proyectoFase = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(proyectoFase.getId()).isEqualTo(Long.valueOf(i));
    }
  }

  /**
   * Función que devuelve un objeto Proyecto
   * 
   * @param id id del Proyecto
   * @return el objeto Proyecto
   */
  private Proyecto generarMockProyecto(Long id) {
    EstadoProyecto estadoProyecto = generarMockEstadoProyecto(1L);

    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);
    modeloEjecucion.setNombre("nombre-modelo-ejecucion");

    TipoFinalidad tipoFinalidad = new TipoFinalidad();
    tipoFinalidad.setId(1L);

    TipoAmbitoGeografico tipoAmbitoGeografico = new TipoAmbitoGeografico();
    tipoAmbitoGeografico.setId(1L);

    Proyecto proyecto = new Proyecto();
    proyecto.setId(id);
    proyecto.setTitulo("PRO" + (id != null ? id : 1));
    proyecto.setCodigoExterno("cod-externo-" + (id != null ? String.format("%03d", id) : "001"));
    proyecto.setObservaciones("observaciones-proyecto-" + String.format("%03d", id));
    proyecto.setUnidadGestionRef("OPE");
    proyecto.setFechaInicio(LocalDate.now());
    proyecto.setFechaFin(LocalDate.now());
    proyecto.setModeloEjecucion(modeloEjecucion);
    proyecto.setFinalidad(tipoFinalidad);
    proyecto.setAmbitoGeografico(tipoAmbitoGeografico);
    proyecto.setConfidencial(Boolean.FALSE);
    proyecto.setActivo(true);

    if (id != null) {
      proyecto.setEstado(estadoProyecto);
    }

    return proyecto;
  }

  /**
   * Función que devuelve un objeto EstadoProyecto
   * 
   * @param id id del EstadoProyecto
   * @return el objeto EstadoProyecto
   */
  private EstadoProyecto generarMockEstadoProyecto(Long id) {
    EstadoProyecto estadoProyecto = new EstadoProyecto();
    estadoProyecto.setId(id);
    estadoProyecto.setComentario("estado-proyecto-" + String.format("%03d", id));
    estadoProyecto.setEstado(EstadoProyecto.Estado.BORRADOR);
    estadoProyecto.setFechaEstado(LocalDateTime.now());
    estadoProyecto.setIdProyecto(1L);

    return estadoProyecto;
  }

  /**
   * Función que devuelve un objeto TipoFase
   * 
   * @param id     id del TipoFase
   * @param activo
   * @return el objeto TipoFase
   */
  private TipoFase generarMockTipoFase(Long id, Boolean activo) {

    TipoFase tipoFase = new TipoFase();
    tipoFase.setId(id);
    tipoFase.setNombre("nombre-fase-" + String.format("%03d", id));
    tipoFase.setDescripcion("descripcion-fase-" + String.format("%03d", id));
    tipoFase.setActivo(activo);

    return tipoFase;
  }

  /**
   * Función que genera ModeloTipoFase a partir de un objeto ProyectoFase
   * 
   * @param id
   * @param proyectoFase
   * @param activo
   * @return
   */
  private ModeloTipoFase generarMockModeloTipoFase(Long id, ProyectoFase proyectoFase, Boolean activo) {

    return ModeloTipoFase.builder()//
        .id(id)//
        .modeloEjecucion(proyectoFase.getProyecto().getModeloEjecucion())//
        .tipoFase(proyectoFase.getTipoFase())//
        .activo(activo)//
        .build();
  }

  /**
   * Función que devuelve un objeto ProyectoFase
   * 
   * @param id id del ProyectoFase
   * @return el objeto ProyectoFase
   */
  private ProyectoFase generarMockProyectoFase(Long id) {

    return ProyectoFase.builder()//
        .id(id)//
        .proyecto(generarMockProyecto(1L))//
        .fechaInicio(LocalDateTime.of(2020, 10, 19, 0, 0, 0))//
        .fechaFin(LocalDateTime.of(2020, 10, 20, 23, 59, 59))//
        .observaciones("observaciones-proyecto-fase-" + (id == null ? "" : String.format("%03d", id)))//
        .generaAviso(Boolean.TRUE)//
        .tipoFase(generarMockTipoFase(1L, Boolean.TRUE))//
        .build();
  }
}
