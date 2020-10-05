package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloUnidad;
import org.crue.hercules.sgi.csp.repository.ModeloUnidadRepository;
import org.crue.hercules.sgi.csp.service.impl.ModeloUnidadServiceImpl;
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
 * ModeloUnidadServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class ModeloUnidadServiceTest {

  @Mock
  private ModeloUnidadRepository modeloUnidadRepository;

  private ModeloUnidadService modeloUnidadService;

  @BeforeEach
  public void setUp() throws Exception {
    modeloUnidadService = new ModeloUnidadServiceImpl(modeloUnidadRepository);
  }

  @Test
  public void findAllByModeloEjecucion_ReturnsPage() {
    // given: Una lista con 37 ModeloUnidad para el ModeloEjecucion
    Long idModeloEjecucion = 1L;
    List<ModeloUnidad> modelosUnidadModeloEjecucion = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      modelosUnidadModeloEjecucion.add(generarModeloUnidad(i));
    }

    BDDMockito.given(modeloUnidadRepository.findAll(ArgumentMatchers.<Specification<ModeloUnidad>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<ModeloUnidad>>() {
          @Override
          public Page<ModeloUnidad> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > modelosUnidadModeloEjecucion.size() ? modelosUnidadModeloEjecucion.size() : toIndex;
            List<ModeloUnidad> content = modelosUnidadModeloEjecucion.subList(fromIndex, toIndex);
            Page<ModeloUnidad> page = new PageImpl<>(content, pageable, modelosUnidadModeloEjecucion.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ModeloUnidad> page = modeloUnidadService.findAllByModeloEjecucion(idModeloEjecucion, null, paging);

    // then: Devuelve la pagina 3 con los ModeloUnidad del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ModeloUnidad modeloUnidad = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(modeloUnidad.getUnidadGestion()).isEqualTo("ModeloUnidad" + String.format("%03d", i));
    }
  }

  /**
   * Función que devuelve un objeto ModeloTipoFaseDocumento
   * 
   * @param id id del ModeloTipoFaseDocumento
   * @return el objeto ModeloTipoFaseDocumento
   */
  private ModeloUnidad generarModeloUnidad(Long id) {
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);

    ModeloUnidad modeloUnidad = new ModeloUnidad();
    modeloUnidad.setId(id);
    modeloUnidad.setModeloEjecucion(modeloEjecucion);
    modeloUnidad.setUnidadGestion("ModeloUnidad" + String.format("%03d", id));
    modeloUnidad.setActivo(true);

    return modeloUnidad;
  }

}