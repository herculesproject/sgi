package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.repository.ModeloTipoFaseRepository;
import org.crue.hercules.sgi.csp.service.impl.ModeloTipoFaseServiceImpl;
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
 * ModeloTipoFaseServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class ModeloTipoFaseServiceTest {

  @Mock
  private ModeloTipoFaseRepository modeloTipoFaseRepository;

  private ModeloTipoFaseService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ModeloTipoFaseServiceImpl(modeloTipoFaseRepository);
  }

  @Test
  public void findAllByModeloEjecucion_ReturnsPage() {
    // given: Una lista con 37 ModeloTipoFase para el ModeloEjecucion
    Long idModeloEjecucion = 1L;
    List<ModeloTipoFase> modeloTipoFases = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      modeloTipoFases.add(generarModeloTipoFase(i));
    }

    BDDMockito.given(modeloTipoFaseRepository.findAll(ArgumentMatchers.<Specification<ModeloTipoFase>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<ModeloTipoFase>>() {
          @Override
          public Page<ModeloTipoFase> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > modeloTipoFases.size() ? modeloTipoFases.size() : toIndex;
            List<ModeloTipoFase> content = modeloTipoFases.subList(fromIndex, toIndex);
            Page<ModeloTipoFase> page = new PageImpl<>(content, pageable, modeloTipoFases.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ModeloTipoFase> page = service.findAllByModeloEjecucion(idModeloEjecucion, null, paging);

    // then: Devuelve la pagina 3 con los ModeloTipoFase del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ModeloTipoFase modeloTipoFase = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(modeloTipoFase.getTipoFase().getNombre()).isEqualTo("TipoFase" + String.format("%03d", i));
    }
  }

  @Test
  public void findAllByModeloEjecucionActivosConvocatoria_ReturnsPage() {
    // given: Una lista con 37 ModeloTipoFase activos para convocatorias para el
    // ModeloEjecucion
    Long idModeloEjecucion = 1L;
    List<ModeloTipoFase> tiposFaseModeloEjecucion = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      tiposFaseModeloEjecucion.add(generarModeloTipoFase(i));
    }

    BDDMockito.given(modeloTipoFaseRepository.findAll(ArgumentMatchers.<Specification<ModeloTipoFase>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<ModeloTipoFase>>() {
          @Override
          public Page<ModeloTipoFase> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > tiposFaseModeloEjecucion.size() ? tiposFaseModeloEjecucion.size() : toIndex;
            List<ModeloTipoFase> content = tiposFaseModeloEjecucion.subList(fromIndex, toIndex);
            Page<ModeloTipoFase> page = new PageImpl<>(content, pageable, tiposFaseModeloEjecucion.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ModeloTipoFase> page = service.findAllByModeloEjecucionActivosConvocatoria(idModeloEjecucion, null, paging);

    // then: Devuelve la pagina 3 con los ModeloTipoFase del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ModeloTipoFase modeloTipoFase = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(modeloTipoFase.getTipoFase().getNombre()).isEqualTo("TipoFase" + String.format("%03d", i));
    }
  }

  @Test
  public void findAllByModeloEjecucionActivosProyecto_ReturnsPage() {
    // given: Una lista con 37 ModeloTipoFase activos para convocatorias para el
    // ModeloEjecucion
    Long idModeloEjecucion = 1L;
    List<ModeloTipoFase> tiposFaseModeloEjecucion = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      tiposFaseModeloEjecucion.add(generarModeloTipoFase(i));
    }

    BDDMockito.given(modeloTipoFaseRepository.findAll(ArgumentMatchers.<Specification<ModeloTipoFase>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<ModeloTipoFase>>() {
          @Override
          public Page<ModeloTipoFase> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > tiposFaseModeloEjecucion.size() ? tiposFaseModeloEjecucion.size() : toIndex;
            List<ModeloTipoFase> content = tiposFaseModeloEjecucion.subList(fromIndex, toIndex);
            Page<ModeloTipoFase> page = new PageImpl<>(content, pageable, tiposFaseModeloEjecucion.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ModeloTipoFase> page = service.findAllByModeloEjecucionActivosProyecto(idModeloEjecucion, null, paging);

    // then: Devuelve la pagina 3 con los ModeloTipoFase del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ModeloTipoFase modeloTipoFase = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(modeloTipoFase.getTipoFase().getNombre()).isEqualTo("TipoFase" + String.format("%03d", i));
    }
  }

  /**
   * Función que devuelve un objeto TipoFase
   * 
   * @param id id del TipoDocumento
   * @return el objeto TipoDocumento
   */
  private TipoFase generarMockTipoFase(Long id, String nombre) {

    TipoFase tipoFase = new TipoFase();
    tipoFase.setId(id);
    tipoFase.setNombre(nombre);
    tipoFase.setDescripcion("descripcion-" + id);
    tipoFase.setActivo(Boolean.TRUE);

    return tipoFase;
  }

  /**
   * Función que devuelve un objeto ModeloTipoFase
   * 
   * @param id id del ModeloTipoFase
   * @return el objeto ModeloTipoFase
   */
  private ModeloTipoFase generarModeloTipoFase(Long id) {
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);

    ModeloTipoFase modeloTipoFase = new ModeloTipoFase();
    modeloTipoFase.setId(id);
    modeloTipoFase.setModeloEjecucion(modeloEjecucion);
    modeloTipoFase.setTipoFase(generarMockTipoFase(id, "TipoFase" + String.format("%03d", id)));
    modeloTipoFase.setActivoConvocatoria(true);
    modeloTipoFase.setActivoProyecto(true);
    modeloTipoFase.setActivoSolicitud(false);

    return modeloTipoFase;
  }

}
