package org.crue.hercules.sgi.csp.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ProyectoSocioNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoSocioPeriodoJustificacionNotFoundException;
import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioRepository;
import org.crue.hercules.sgi.csp.repository.SocioPeriodoJustificacionDocumentoRepository;
import org.crue.hercules.sgi.csp.service.impl.ProyectoSocioPeriodoJustificacionServiceImpl;
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
 * ProyectoSocioPeriodoJustificacionServiceTest
 */
public class ProyectoSocioPeriodoJustificacionServiceTest extends BaseServiceTest {

  @Mock
  private ProyectoSocioPeriodoJustificacionRepository repository;
  @Mock
  private ProyectoSocioRepository proyectoSocioRepository;

  @Mock
  private SocioPeriodoJustificacionDocumentoRepository socioPeriodoJustificacionDocumentoRepository;

  private ProyectoSocioPeriodoJustificacionService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ProyectoSocioPeriodoJustificacionServiceImpl(repository, proyectoSocioRepository,
        socioPeriodoJustificacionDocumentoRepository);
  }

  @Test
  public void update_ReturnsProyectoSocioPeriodoJustificacion() {
    // given: un ProyectoSocioPeriodoJustificacion actualizado,

    ProyectoSocioPeriodoJustificacion updateProyectoSocioPeriodoJustificacion1 = generarMockProyectoSocioPeriodoJustificacion(
        4L);
    updateProyectoSocioPeriodoJustificacion1.setFechaInicio(LocalDate.of(2021, 1, 19));
    updateProyectoSocioPeriodoJustificacion1.setFechaFin(LocalDate.of(2021, 1, 26));

    BDDMockito.given(proyectoSocioRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockProyectoSocioPeriodoJustificacion(4L)));

    BDDMockito.given(repository.saveAll(ArgumentMatchers.<ProyectoSocioPeriodoJustificacion>anyList()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacionActualizado = service
        .update(updateProyectoSocioPeriodoJustificacion1, 4L);

    // then: Se actualiza el ProyectoSocioPeriodoJustificacion
    Assertions.assertThat(proyectoSocioPeriodoJustificacionActualizado.getId()).as(".getId()").isEqualTo(4L);
    Assertions.assertThat(proyectoSocioPeriodoJustificacionActualizado.getProyectoSocio().getId())
        .as(".getProyectoSocio().getId()").isEqualTo(4L);
    Assertions.assertThat(proyectoSocioPeriodoJustificacionActualizado.getFechaInicio()).as(".getFechaInicio()")
        .isEqualTo(updateProyectoSocioPeriodoJustificacion1.getFechaInicio());
    Assertions.assertThat(proyectoSocioPeriodoJustificacionActualizado.getFechaFin()).as("getFechaFin()")
        .isEqualTo(updateProyectoSocioPeriodoJustificacion1.getFechaFin());
    Assertions.assertThat(proyectoSocioPeriodoJustificacionActualizado.getFechaInicioPresentacion())
        .as("get(0).getFechaInicioPresentacion()")
        .isEqualTo(updateProyectoSocioPeriodoJustificacion1.getFechaInicioPresentacion());
    Assertions.assertThat(proyectoSocioPeriodoJustificacionActualizado.getFechaFinPresentacion())
        .as("get(0).getFechaFinPresentacion()")
        .isEqualTo(updateProyectoSocioPeriodoJustificacion1.getFechaFinPresentacion());
    Assertions.assertThat(proyectoSocioPeriodoJustificacionActualizado.getNumPeriodo()).as("getNumPeriodo()")
        .isEqualTo(1);
    Assertions.assertThat(proyectoSocioPeriodoJustificacionActualizado.getObservaciones()).as("getObservaciones()")
        .isEqualTo(updateProyectoSocioPeriodoJustificacion1.getObservaciones());

  }

  @Test
  public void update_WithNoExistingProyectoSocio_ThrowsProyectoSocioNotFoundException() {
    // given: a ProyectoSocioEntidadGestora with non existing ProyectoSocio
    Long proyectoSocioPeriodoJustificacionId = 1L;
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = generarMockProyectoSocioPeriodoJustificacion(
        1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoSocioPeriodoJustificacion));
    BDDMockito.given(proyectoSocioRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: update ProyectoSocioEntidadGestora
        () -> service.update(proyectoSocioPeriodoJustificacion, proyectoSocioPeriodoJustificacionId))
        // then: throw exception as ProyectoSocio is not found
        .isInstanceOf(ProyectoSocioNotFoundException.class);
  }

  @Test
  public void update_WithIdNotExist_ThrowsProyectoSocioPeriodoJustificacionNotFoundException() {
    // given: Un ProyectoSocioPeriodoJustificacion a actualizar con un id que no
    // existe
    Long proyectoSocioPeriodoJustificacionId = 1L;
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = generarMockProyectoSocioPeriodoJustificacion(
        1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    // when:update
    // then: Lanza una excepcion porque el ProyectoSocioPeriodoJustificacion no
    // existe
    Assertions
        .assertThatThrownBy(
            () -> service.update(proyectoSocioPeriodoJustificacion, proyectoSocioPeriodoJustificacionId))
        .isInstanceOf(ProyectoSocioPeriodoJustificacionNotFoundException.class);
  }

  @Test
  public void update_WithProyectoSocioChange_ThrowsIllegalArgumentException() {
    // given: a ProyectoSocioPeriodoJustificacion with proyecto socio modificado
    Long proyectoSocioPeriodoJustificacionId = 1L;
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = generarMockProyectoSocioPeriodoJustificacion(
        1L);

    proyectoSocioPeriodoJustificacion.getProyectoSocio().setId(3L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockProyectoSocioPeriodoJustificacion(1L)));

    Assertions.assertThatThrownBy(
        // when: update
        () -> service.update(proyectoSocioPeriodoJustificacion, proyectoSocioPeriodoJustificacionId))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("No se puede modificar el proyecto socio del ProyectoSocioPeriodoJustificacion");
  }

  @Test
  public void update_WithFechaFinBeforeThanFechaInicio_ThrowsIllegalArgumentException() {
    // given: a ProyectoSocioPeriodoJustificacion with fecha fin before
    // fecha inicio
    Long proyectoSocioPeriodoJustificacionId = 1L;
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = generarMockProyectoSocioPeriodoJustificacion(
        1L);

    proyectoSocioPeriodoJustificacion.setFechaInicio(proyectoSocioPeriodoJustificacion.getFechaFin().plusDays(1));

    BDDMockito.given(proyectoSocioRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoSocioPeriodoJustificacion));

    Assertions.assertThatThrownBy(
        // when: update
        () -> service.update(proyectoSocioPeriodoJustificacion, proyectoSocioPeriodoJustificacionId))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La fecha final tiene que ser posterior a la fecha inicial");
  }

  @Test
  public void update_WithOutFechasPresentacionAndEstadoProyectoAbierto_ThrowsIllegalArgumentException() {
    // given: a ProyectoSocioPeriodoJustificacion without fechas presentacion and
    // estado proyecto abierto
    Long proyectoSocioPeriodoJustificacionId = 1L;
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = generarMockProyectoSocioPeriodoJustificacion(
        1L);

    proyectoSocioPeriodoJustificacion.setFechaInicioPresentacion(null);
    proyectoSocioPeriodoJustificacion.setFechaFinPresentacion(null);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoSocioPeriodoJustificacion));

    BDDMockito.given(proyectoSocioRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    Assertions.assertThatThrownBy(
        // when: update
        () -> service.update(proyectoSocioPeriodoJustificacion, proyectoSocioPeriodoJustificacionId))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Las fechas de presentación no pueden ser null cuando el estado del proyecto es Abierto");
  }

  @Test
  public void update_WithFechaFinPresentacionBeforeFechaInicioPresentacion_ThrowsIllegalArgumentException() {
    // given: a ProyectoSocioPeriodoJustificacion with FechaFinPresentacion before
    // FechaInicioPresentacion
    Long proyectoSocioPeriodoJustificacionId = 1L;
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = generarMockProyectoSocioPeriodoJustificacion(
        1L);
    proyectoSocioPeriodoJustificacion
        .setFechaInicioPresentacion(proyectoSocioPeriodoJustificacion.getFechaFinPresentacion().plusDays(1));

    BDDMockito.given(proyectoSocioRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoSocioPeriodoJustificacion));

    Assertions.assertThatThrownBy(
        // when: update
        () -> service.update(proyectoSocioPeriodoJustificacion, proyectoSocioPeriodoJustificacionId))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La fecha de fin de presentación tiene que ser posterior a la fecha de inicio de presentación");
  }

  @Test
  public void update_WithFechaFinAfterProyectoFechaFin_ThrowsIllegalArgumentException() {
    // given: a ProyectoSocioPeriodoJustificacion with Fecha fin after fecha fin
    // proyecto
    Long proyectoSocioPeriodoJustificacionId = 1L;
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = generarMockProyectoSocioPeriodoJustificacion(
        1L);
    proyectoSocioPeriodoJustificacion
        .setFechaFin(proyectoSocioPeriodoJustificacion.getProyectoSocio().getFechaFin().plusDays(1));

    BDDMockito.given(proyectoSocioRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoSocioPeriodoJustificacion));

    Assertions.assertThatThrownBy(
        // when: update
        () -> service.update(proyectoSocioPeriodoJustificacion, proyectoSocioPeriodoJustificacionId))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La fecha fin no puede ser superior a la fecha fin indicada en Proyecto socio");
  }

  @Test
  public void create_ReturnsProyectoSocioPeriodoJustificacion() {
    // given: un nuevo ProyectoSocioPeriodoJustificacion ,

    ProyectoSocioPeriodoJustificacion updateProyectoSocioPeriodoJustificacion1 = generarMockProyectoSocioPeriodoJustificacion(
        null);
    updateProyectoSocioPeriodoJustificacion1.setFechaInicio(LocalDate.of(2021, 1, 19));
    updateProyectoSocioPeriodoJustificacion1.setFechaFin(LocalDate.of(2021, 1, 26));

    BDDMockito.given(proyectoSocioRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    BDDMockito.given(repository.saveAll(ArgumentMatchers.<ProyectoSocioPeriodoJustificacion>anyList()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: update
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacionActualizado = service
        .create(updateProyectoSocioPeriodoJustificacion1);

    // then: Se crea el nuevo ProyectoSocioPeriodoJustificacion,
    Assertions.assertThat(proyectoSocioPeriodoJustificacionActualizado.getProyectoSocio().getId())
        .as(".getProyectoSocio().getId()").isEqualTo(1L);
    Assertions.assertThat(proyectoSocioPeriodoJustificacionActualizado.getFechaInicio()).as(".getFechaInicio()")
        .isEqualTo(updateProyectoSocioPeriodoJustificacion1.getFechaInicio());
    Assertions.assertThat(proyectoSocioPeriodoJustificacionActualizado.getFechaFin()).as("getFechaFin()")
        .isEqualTo(updateProyectoSocioPeriodoJustificacion1.getFechaFin());
    Assertions.assertThat(proyectoSocioPeriodoJustificacionActualizado.getFechaInicioPresentacion())
        .as("get(0).getFechaInicioPresentacion()")
        .isEqualTo(updateProyectoSocioPeriodoJustificacion1.getFechaInicioPresentacion());
    Assertions.assertThat(proyectoSocioPeriodoJustificacionActualizado.getFechaFinPresentacion())
        .as("get(0).getFechaFinPresentacion()")
        .isEqualTo(updateProyectoSocioPeriodoJustificacion1.getFechaFinPresentacion());
    Assertions.assertThat(proyectoSocioPeriodoJustificacionActualizado.getNumPeriodo()).as("getNumPeriodo()")
        .isEqualTo(1);
    Assertions.assertThat(proyectoSocioPeriodoJustificacionActualizado.getObservaciones()).as("getObservaciones()")
        .isEqualTo(updateProyectoSocioPeriodoJustificacion1.getObservaciones());

  }

  @Test
  public void create_WithNoExistingProyectoSocio_ThrowsProyectoSocioNotFoundException() {
    // given: a ProyectoSocioEntidadGestora with non existing ProyectoSocio
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = generarMockProyectoSocioPeriodoJustificacion(
        null);

    BDDMockito.given(proyectoSocioRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: create ProyectoSocioEntidadGestora
        () -> service.create(proyectoSocioPeriodoJustificacion))
        // then: throw exception as ProyectoSocio is not found
        .isInstanceOf(ProyectoSocioNotFoundException.class);
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un ProyectoSocioPeriodoJustificacion a actualizar con un id que no
    // existe
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = generarMockProyectoSocioPeriodoJustificacion(
        3L);

    // when:create
    // then: Lanza una excepcion porque el ProyectoSocioPeriodoJustificacion no
    // puede tener id
    Assertions.assertThatThrownBy(
        // when: create
        () -> service.create(proyectoSocioPeriodoJustificacion))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El id de proyecto socio periodo justificación debe ser null");
  }

  @Test
  public void create_WithFechaFinBeforeThanFechaInicio_ThrowsIllegalArgumentException() {
    // given: a ProyectoSocioPeriodoJustificacion with fecha fin before
    // fecha inicio
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = generarMockProyectoSocioPeriodoJustificacion(
        null);

    proyectoSocioPeriodoJustificacion.setFechaInicio(proyectoSocioPeriodoJustificacion.getFechaFin().plusDays(1));

    BDDMockito.given(proyectoSocioRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    Assertions.assertThatThrownBy(
        // when: create
        () -> service.create(proyectoSocioPeriodoJustificacion))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La fecha final tiene que ser posterior a la fecha inicial");
  }

  @Test
  public void create_WithOutFechasPresentacionAndEstadoProyectoAbierto_ThrowsIllegalArgumentException() {
    // given: a ProyectoSocioPeriodoJustificacion without fechas presentacion and
    // estado proyecto abierto
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = generarMockProyectoSocioPeriodoJustificacion(
        null);

    proyectoSocioPeriodoJustificacion.setFechaInicioPresentacion(null);
    proyectoSocioPeriodoJustificacion.setFechaFinPresentacion(null);

    BDDMockito.given(proyectoSocioRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    Assertions.assertThatThrownBy(
        // when: create
        () -> service.create(proyectoSocioPeriodoJustificacion))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Las fechas de presentación no pueden ser null cuando el estado del proyecto es Abierto");
  }

  @Test
  public void create_WithFechaFinPresentacionBeforeFechaInicioPresentacion_ThrowsIllegalArgumentException() {
    // given: a ProyectoSocioPeriodoJustificacion with FechaFinPresentacion before
    // FechaInicioPresentacion
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = generarMockProyectoSocioPeriodoJustificacion(
        null);
    proyectoSocioPeriodoJustificacion
        .setFechaInicioPresentacion(proyectoSocioPeriodoJustificacion.getFechaFinPresentacion().plusDays(1));

    BDDMockito.given(proyectoSocioRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    Assertions.assertThatThrownBy(
        // when: create
        () -> service.create(proyectoSocioPeriodoJustificacion))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La fecha de fin de presentación tiene que ser posterior a la fecha de inicio de presentación");
  }

  @Test
  public void create_WithFechaFinAfterProyectoFechaFin_ThrowsIllegalArgumentException() {
    // given: a ProyectoSocioPeriodoJustificacion with Fecha fin after fecha fin
    // proyecto
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = generarMockProyectoSocioPeriodoJustificacion(
        null);
    proyectoSocioPeriodoJustificacion
        .setFechaFin(proyectoSocioPeriodoJustificacion.getProyectoSocio().getFechaFin().plusDays(1));

    BDDMockito.given(proyectoSocioRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    Assertions.assertThatThrownBy(
        // when: create
        () -> service.create(proyectoSocioPeriodoJustificacion))
        // then: throw exception
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La fecha fin no puede ser superior a la fecha fin indicada en Proyecto socio");
  }

  @Test
  public void delete_WithExistingId_NoReturnsAnyException() {
    // given: existing SolicitudProyectoSocio
    Long id = 1L;

    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = generarMockProyectoSocioPeriodoJustificacion(
        4L);
    List<ProyectoSocioPeriodoJustificacion> proyectoSocioPeriodoJustificacionEliminar = new ArrayList<>();
    proyectoSocioPeriodoJustificacionEliminar.add(proyectoSocioPeriodoJustificacion);

    BDDMockito.given(proyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoSocioPeriodoJustificacion.getProyectoSocio()));

    Assertions.assertThatCode(
        // when: delete by existing id
        () -> service.delete(id, proyectoSocioPeriodoJustificacionEliminar))
        // then: no exception is thrown
        .doesNotThrowAnyException();
  }

  @Test
  public void delete_WithNoExistingProyectoSocio_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    Long id = 1L;

    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = generarMockProyectoSocioPeriodoJustificacion(
        4L);
    List<ProyectoSocioPeriodoJustificacion> proyectoSocioPeriodoJustificacionEliminar = new ArrayList<>();
    proyectoSocioPeriodoJustificacionEliminar.add(proyectoSocioPeriodoJustificacion);

    BDDMockito.given(proyectoSocioRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id, proyectoSocioPeriodoJustificacionEliminar))
        // then: NotFoundException is thrown
        .isInstanceOf(ProyectoSocioNotFoundException.class);
  }

  @Test
  public void findAllByProyectoSocio_ReturnsPage() {
    // given: Una lista con 37 ProyectoSocioEntidadGestora para la ProyectoSocio
    Long proyectoSocioId = 1L;
    List<ProyectoSocioPeriodoJustificacion> proyectoSociosEntidadesConvocantes = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectoSociosEntidadesConvocantes.add(generarMockProyectoSocioPeriodoJustificacion(i));
    }

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ProyectoSocioPeriodoJustificacion>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > proyectoSociosEntidadesConvocantes.size() ? proyectoSociosEntidadesConvocantes.size()
              : toIndex;
          List<ProyectoSocioPeriodoJustificacion> content = proyectoSociosEntidadesConvocantes.subList(fromIndex,
              toIndex);
          Page<ProyectoSocioPeriodoJustificacion> pageResponse = new PageImpl<>(content, pageable,
              proyectoSociosEntidadesConvocantes.size());
          return pageResponse;

        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ProyectoSocioPeriodoJustificacion> page = service.findAllByProyectoSocio(proyectoSocioId, null, paging);

    // then: Devuelve la pagina 3 con los ProyectoSocioPeriodoJustificacion del 31
    // al
    // 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(proyectoSocioPeriodoJustificacion.getId()).isEqualTo(Long.valueOf(i));
      Assertions.assertThat(proyectoSocioPeriodoJustificacion.getObservaciones()).isEqualTo("observaciones-" + i);
    }
  }

  @Test
  public void findById_ReturnsProyectoSocioPeriodoJustificacion() {
    // given: Un ProyectoSocioPeriodoJustificacion con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado))
        .willReturn(Optional.of(generarMockProyectoSocioPeriodoJustificacion(idBuscado)));

    // when: Buscamos el ProyectoSocioPeriodoJustificacion por su id
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = service.findById(idBuscado);

    // then: el ProyectoSocioPeriodoJustificacion
    Assertions.assertThat(proyectoSocioPeriodoJustificacion).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoSocioPeriodoJustificacion.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(proyectoSocioPeriodoJustificacion.getFechaInicio()).as("getFechaInicio()")
        .isEqualTo(LocalDate.of(2020, 9, 10));
    Assertions.assertThat(proyectoSocioPeriodoJustificacion.getFechaFin()).as("getFechaFin()")
        .isEqualTo(LocalDate.of(2020, 12, 20));
    Assertions.assertThat(proyectoSocioPeriodoJustificacion.getFechaInicioPresentacion())
        .as("getFechaInicioPresentacion()").isEqualTo(LocalDateTime.of(2020, 10, 10, 0, 0));
    Assertions.assertThat(proyectoSocioPeriodoJustificacion.getFechaFinPresentacion()).as("getFechaFinPresentacion()")
        .isEqualTo(LocalDateTime.of(2020, 11, 20, 0, 0));
    Assertions.assertThat(proyectoSocioPeriodoJustificacion.getNumPeriodo()).as("getNumPeriodo()").isEqualTo(1);
    Assertions.assertThat(proyectoSocioPeriodoJustificacion.getObservaciones()).as("getObservaciones()")
        .isEqualTo("observaciones-1");
  }

  @Test
  public void findById_WithIdNotExist_ThrowsProyectoSocioPeriodoJustificacionNotFoundException() throws Exception {
    // given: Ningun ProyectoSocioPeriodoJustificacion con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el ProyectoSocioPeriodoJustificacion por su id
    // then: lanza un ProyectoSocioPeriodoJustificacionNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(ProyectoSocioPeriodoJustificacionNotFoundException.class);
  }

  /**
   * Función que devuelve un objeto ProyectoSocioPeriodoJustificacion.
   * 
   * @param id identificador
   * @return el objeto ProyectoSocioPeriodoJustificacion
   */
  private ProyectoSocioPeriodoJustificacion generarMockProyectoSocioPeriodoJustificacion(Long id) {
    ProyectoSocio proyectoSocio = new ProyectoSocio();
    proyectoSocio.setId(id == null ? 1 : id);
    proyectoSocio.setProyecto(new Proyecto());
    proyectoSocio.getProyecto().setEstado(new EstadoProyecto());
    proyectoSocio.getProyecto().getEstado().setEstado(EstadoProyecto.Estado.ABIERTO);
    proyectoSocio.setFechaFin(LocalDate.of(2022, 12, 23));

    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion = new ProyectoSocioPeriodoJustificacion();
    proyectoSocioPeriodoJustificacion.setId(id);
    proyectoSocioPeriodoJustificacion.setProyectoSocio(proyectoSocio);
    proyectoSocioPeriodoJustificacion.setNumPeriodo(1);
    proyectoSocioPeriodoJustificacion.setFechaInicio(LocalDate.of(2020, 9, 10));
    proyectoSocioPeriodoJustificacion.setFechaFin(LocalDate.of(2020, 12, 20));
    proyectoSocioPeriodoJustificacion.setFechaInicioPresentacion(LocalDateTime.of(2020, 10, 10, 0, 0, 0));
    proyectoSocioPeriodoJustificacion.setFechaFinPresentacion(LocalDateTime.of(2020, 11, 20, 0, 0, 0));
    proyectoSocioPeriodoJustificacion.setObservaciones("observaciones-" + id);

    return proyectoSocioPeriodoJustificacion;
  }

}
