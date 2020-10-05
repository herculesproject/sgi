package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoHito;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.crue.hercules.sgi.csp.repository.ModeloTipoHitoRepository;
import org.crue.hercules.sgi.csp.service.impl.ModeloTipoHitoServiceImpl;
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
 * ModeloTipoHitoServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class ModeloTipoHitoServiceTest {

  @Mock
  private ModeloTipoHitoRepository modeloTipoHitoRepository;

  private ModeloTipoHitoService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ModeloTipoHitoServiceImpl(modeloTipoHitoRepository);
  }

  @Test
  public void findAllByModeloEjecucion_ReturnsPage() {
    // given: Una lista con 37 ModeloTipoHito para el ModeloEjecucion
    Long idModeloEjecucion = 1L;
    List<ModeloTipoHito> modeloTipoHitos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      modeloTipoHitos.add(generarModeloTipoHito(i));
    }

    BDDMockito.given(modeloTipoHitoRepository.findAll(ArgumentMatchers.<Specification<ModeloTipoHito>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<ModeloTipoHito>>() {
          @Override
          public Page<ModeloTipoHito> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > modeloTipoHitos.size() ? modeloTipoHitos.size() : toIndex;
            List<ModeloTipoHito> content = modeloTipoHitos.subList(fromIndex, toIndex);
            Page<ModeloTipoHito> page = new PageImpl<>(content, pageable, modeloTipoHitos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ModeloTipoHito> page = service.findAllByModeloEjecucion(idModeloEjecucion, null, paging);

    // then: Devuelve la pagina 3 con los ModeloTipoHito del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ModeloTipoHito modeloTipoHito = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(modeloTipoHito.getTipoHito().getNombre()).isEqualTo("TipoHito" + String.format("%03d", i));
    }
  }

  @Test
  public void findAllByModeloEjecucionActivosConvocatoria_ReturnsPage() {
    // given: Una lista con 37 ModeloTipoHito activos para convocatorias para el
    // ModeloEjecucion
    Long idModeloEjecucion = 1L;
    List<ModeloTipoHito> modeloTipoHitos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      modeloTipoHitos.add(generarModeloTipoHito(i));
    }

    BDDMockito.given(modeloTipoHitoRepository.findAll(ArgumentMatchers.<Specification<ModeloTipoHito>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<ModeloTipoHito>>() {
          @Override
          public Page<ModeloTipoHito> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > modeloTipoHitos.size() ? modeloTipoHitos.size() : toIndex;
            List<ModeloTipoHito> content = modeloTipoHitos.subList(fromIndex, toIndex);
            Page<ModeloTipoHito> page = new PageImpl<>(content, pageable, modeloTipoHitos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ModeloTipoHito> page = service.findAllByModeloEjecucionActivosConvocatoria(idModeloEjecucion, null, paging);

    // then: Devuelve la pagina 3 con los ModeloTipoHito del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ModeloTipoHito modeloTipoHito = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(modeloTipoHito.getTipoHito().getNombre()).isEqualTo("TipoHito" + String.format("%03d", i));
    }
  }

  @Test
  public void findAllByModeloEjecucionActivosProyecto_ReturnsPage() {
    // given: Una lista con 37 ModeloTipoHito activos para convocatorias para el
    // ModeloEjecucion
    Long idModeloEjecucion = 1L;
    List<ModeloTipoHito> modeloTipoHitos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      modeloTipoHitos.add(generarModeloTipoHito(i));
    }

    BDDMockito.given(modeloTipoHitoRepository.findAll(ArgumentMatchers.<Specification<ModeloTipoHito>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<ModeloTipoHito>>() {
          @Override
          public Page<ModeloTipoHito> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > modeloTipoHitos.size() ? modeloTipoHitos.size() : toIndex;
            List<ModeloTipoHito> content = modeloTipoHitos.subList(fromIndex, toIndex);
            Page<ModeloTipoHito> page = new PageImpl<>(content, pageable, modeloTipoHitos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ModeloTipoHito> page = service.findAllByModeloEjecucionActivosProyecto(idModeloEjecucion, null, paging);

    // then: Devuelve la pagina 3 con los ModeloTipoHito del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ModeloTipoHito modeloTipoHito = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(modeloTipoHito.getTipoHito().getNombre()).isEqualTo("TipoHito" + String.format("%03d", i));
    }
  }

  @Test
  public void findAllByModeloEjecucionActivosSolicitud_ReturnsPage() {
    // given: Una lista con 37 ModeloTipoHito activos para convocatorias para el
    // ModeloEjecucion
    Long idModeloEjecucion = 1L;
    List<ModeloTipoHito> modeloTipoHitos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      modeloTipoHitos.add(generarModeloTipoHito(i));
    }

    BDDMockito.given(modeloTipoHitoRepository.findAll(ArgumentMatchers.<Specification<ModeloTipoHito>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<ModeloTipoHito>>() {
          @Override
          public Page<ModeloTipoHito> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > modeloTipoHitos.size() ? modeloTipoHitos.size() : toIndex;
            List<ModeloTipoHito> content = modeloTipoHitos.subList(fromIndex, toIndex);
            Page<ModeloTipoHito> page = new PageImpl<>(content, pageable, modeloTipoHitos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ModeloTipoHito> page = service.findAllByModeloEjecucionActivosSolicitud(idModeloEjecucion, null, paging);

    // then: Devuelve la pagina 3 con los ModeloTipoHito del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ModeloTipoHito modeloTipoHito = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(modeloTipoHito.getTipoHito().getNombre()).isEqualTo("TipoHito" + String.format("%03d", i));
    }
  }

  /**
   * Función que devuelve un objeto TipoHito
   * 
   * @param id id del TipoHito
   * @return el objeto TipoHito
   */
  public TipoHito generarMockTipoHito(Long id) {
    return generarMockTipoHito(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto TipoHito
   * 
   * @param id id del TipoHito
   * @return el objeto TipoHito
   */
  public TipoHito generarMockTipoHito(Long id, String nombre) {
    TipoHito tipoHito = new TipoHito();
    tipoHito.setId(id);
    tipoHito.setNombre(nombre);
    tipoHito.setDescripcion("descripcion-" + id);
    tipoHito.setActivo(Boolean.TRUE);
    return tipoHito;
  }

  /**
   * Función que devuelve un objeto ModeloTipoHito
   * 
   * @param id id del ModeloTipoHito
   * @return el objeto ModeloTipoHito
   */
  private ModeloTipoHito generarModeloTipoHito(Long id) {
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);

    ModeloTipoHito modeloTipoHito = new ModeloTipoHito();
    modeloTipoHito.setId(id);
    modeloTipoHito.setModeloEjecucion(modeloEjecucion);
    modeloTipoHito.setTipoHito(generarMockTipoHito(id, "TipoHito" + String.format("%03d", id)));
    modeloTipoHito.setActivoConvocatoria(true);
    modeloTipoHito.setActivoProyecto(true);
    modeloTipoHito.setActivoSolicitud(false);

    return modeloTipoHito;
  }

}
