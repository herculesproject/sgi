package org.crue.hercules.sgi.csp.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.TipoEstadoProyectoEnum;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.service.impl.ProyectoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * ProyectoServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class ProyectoServiceTest {

  @Mock
  private ProyectoRepository repository;

  private ProyectoService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ProyectoServiceImpl(repository);
  }

  // TODO implementar tests de creación y actualización cuando se implemente su
  // lógica en el servicio

  @Test
  public void enable_ReturnsProyecto() {
    // given: Un nuevo Proyecto inactivo
    Proyecto proyecto = generarMockProyecto(1L);
    proyecto.setActivo(false);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyecto));

    BDDMockito.given(repository.save(ArgumentMatchers.<Proyecto>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Activamos el Proyecto
    Proyecto programaActualizado = service.enable(proyecto.getId(), Arrays.asList(proyecto.getUnidadGestionRef()));

    // then: El Proyecto se activa correctamente.
    Assertions.assertThat(programaActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(programaActualizado.getId()).as("getId()").isEqualTo(proyecto.getId());
    Assertions.assertThat(programaActualizado.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Test
  public void enable_WithIdNotExist_ThrowsProyectoNotFoundException() {
    // given: Un id de un Proyecto que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: activamos el Proyecto
    // then: Lanza una excepcion porque el Proyecto no existe
    Assertions.assertThatThrownBy(() -> service.enable(idNoExiste, Arrays.asList("OPE")))
        .isInstanceOf(ProyectoNotFoundException.class);
  }

  @Test
  public void disable_ReturnsProyecto() {
    // given: Un Proyecto activo
    Proyecto proyecto = generarMockProyecto(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.of(proyecto));

    BDDMockito.given(repository.save(ArgumentMatchers.<Proyecto>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    // when: Desactivamos el Proyecto
    Proyecto proyectoActualizada = service.disable(proyecto.getId(), Arrays.asList(proyecto.getUnidadGestionRef()));

    // then: El Proyecto se desactivan correctamente
    Assertions.assertThat(proyectoActualizada).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyectoActualizada.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(proyectoActualizada.getActivo()).as("getActivo()").isEqualTo(false);
  }

  @Test
  public void disable_WithIdNotExist_ThrowsProyectoNotFoundException() {
    // given: Un id de un Proyecto que no existe
    Long idNoExiste = 1L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());
    // when: desactivamos el Proyecto
    // then: Lanza una excepcion porque el Proyecto no existe
    Assertions.assertThatThrownBy(() -> service.disable(idNoExiste, Arrays.asList("OPE")))
        .isInstanceOf(ProyectoNotFoundException.class);
  }

  @Test
  public void findById_ReturnsProyecto() {
    // given: Un Proyecto con el id buscado
    Long idBuscado = 1L;
    Proyecto proyectoBuscada = generarMockProyecto(idBuscado);
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.of(proyectoBuscada));

    // when: Buscamos el Proyecto por su id
    Proyecto proyecto = service.findById(idBuscado, Arrays.asList(proyectoBuscada.getUnidadGestionRef()));

    // then: el Proyecto
    Assertions.assertThat(proyecto).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyecto.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(proyecto.getEstado().getId()).as("getEstado().getId()").isEqualTo(1);
    Assertions.assertThat(proyecto.getObservaciones()).as("getObservaciones()").isEqualTo("observaciones-001");
    Assertions.assertThat(proyecto.getUnidadGestionRef()).as("getUnidadGestionRef()").isEqualTo("OPE");
    Assertions.assertThat(proyecto.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Test
  public void findById_WithIdNotExist_ThrowsProyectoNotFoundException() throws Exception {
    // given: Ningun Proyecto con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el Proyecto por su id
    // then: lanza un ProyectoNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado, Arrays.asList("OPE")))
        .isInstanceOf(ProyectoNotFoundException.class);
  }

  @Test
  public void findAll_ReturnsPage() {
    // given: Una lista con 37 Proyecto
    List<Proyecto> proyectos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectos.add(generarMockProyecto(i));
    }

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<Proyecto>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Proyecto>>() {
          @Override
          public Page<Proyecto> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > proyectos.size() ? proyectos.size() : toIndex;
            List<Proyecto> content = proyectos.subList(fromIndex, toIndex);
            Page<Proyecto> page = new PageImpl<>(content, pageable, proyectos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Proyecto> page = service.findAllRestringidos(null, paging, Arrays.asList("OPE"));

    // then: Devuelve la pagina 3 con los Programa del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      Proyecto proyecto = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(proyecto.getObservaciones()).isEqualTo("observaciones-" + String.format("%03d", i));
    }
  }

  @Test
  public void findAllTodos_ReturnsPage() {
    // given: Una lista con 37 Proyecto
    List<Proyecto> proyectos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectos.add(generarMockProyecto(i));
    }

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<Proyecto>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Proyecto>>() {
          @Override
          public Page<Proyecto> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > proyectos.size() ? proyectos.size() : toIndex;
            List<Proyecto> content = proyectos.subList(fromIndex, toIndex);
            Page<Proyecto> page = new PageImpl<>(content, pageable, proyectos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Proyecto> page = service.findAllTodosRestringidos(null, paging, Arrays.asList("OPE"));

    // then: Devuelve la pagina 3 con los Programa del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      Proyecto proyecto = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(proyecto.getObservaciones()).isEqualTo("observaciones-" + String.format("%03d", i));
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

    TipoFinalidad tipoFinalidad = new TipoFinalidad();
    tipoFinalidad.setId(1L);

    TipoAmbitoGeografico tipoAmbitoGeografico = new TipoAmbitoGeografico();
    tipoAmbitoGeografico.setId(1L);

    Proyecto proyecto = new Proyecto();
    proyecto.setId(id);
    proyecto.setTitulo("PRO" + (id != null ? id : 1));
    proyecto.setCodigoExterno("cod-externo-" + (id != null ? String.format("%03d", id) : "001"));
    proyecto.setObservaciones("observaciones-" + String.format("%03d", id));
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
    estadoProyecto.setComentario("Estado-" + id);
    estadoProyecto.setEstado(TipoEstadoProyectoEnum.BORRADOR);
    estadoProyecto.setFechaEstado(LocalDateTime.now());
    estadoProyecto.setIdProyecto(1L);

    return estadoProyecto;
  }

}
