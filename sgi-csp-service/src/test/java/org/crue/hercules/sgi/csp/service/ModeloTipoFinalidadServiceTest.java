package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.repository.ModeloTipoFinalidadRepository;
import org.crue.hercules.sgi.csp.service.impl.ModeloTipoFinalidadServiceImpl;
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

@ExtendWith(MockitoExtension.class)
public class ModeloTipoFinalidadServiceTest {

  @Mock
  private ModeloTipoFinalidadRepository modeloTipoFinalidadRepository;

  private ModeloTipoFinalidadService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ModeloTipoFinalidadServiceImpl(modeloTipoFinalidadRepository);
  }

  @Test
  public void findAllByModeloEjecucion_ReturnsPage() {
    // given: Una lista con 37 ModeloTipoFinalidad para el ModeloEjecucion
    Long idModeloEjecucion = 1L;
    List<ModeloTipoFinalidad> modeloTipoFinalidades = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      modeloTipoFinalidades.add(generarModeloTipoFinalidad(i));
    }

    BDDMockito.given(modeloTipoFinalidadRepository.findAll(ArgumentMatchers.<Specification<ModeloTipoFinalidad>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<ModeloTipoFinalidad>>() {
          @Override
          public Page<ModeloTipoFinalidad> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > modeloTipoFinalidades.size() ? modeloTipoFinalidades.size() : toIndex;
            List<ModeloTipoFinalidad> content = modeloTipoFinalidades.subList(fromIndex, toIndex);
            Page<ModeloTipoFinalidad> page = new PageImpl<>(content, pageable, modeloTipoFinalidades.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ModeloTipoFinalidad> page = service.findAllByModeloEjecucion(idModeloEjecucion, null, paging);

    // then: Devuelve la pagina 3 con los ModeloTipoFinalidad del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ModeloTipoFinalidad modeloTipoFinalidad = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(modeloTipoFinalidad.getTipoFinalidad().getNombre()).isEqualTo("nombre-" + i);
    }
  }

  /**
   * Función que devuelve un objeto TipoFinalidad
   * 
   * @param id
   * @param activo
   * @return TipoFinalidad
   */
  private TipoFinalidad generarMockTipoFinalidad(Long id, Boolean activo) {
    return TipoFinalidad.builder().id(id).nombre("nombre-" + id).descripcion("descripcion-" + id).activo(activo)
        .build();
  }

  /**
   * Función que devuelve un objeto ModeloTipoFinalidad
   * 
   * @param id id del ModeloTipoFinalidad
   * @return el objeto ModeloTipoFinalidad
   */
  private ModeloTipoFinalidad generarModeloTipoFinalidad(Long id) {
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);

    ModeloTipoFinalidad modeloTipoFinalidad = new ModeloTipoFinalidad();
    modeloTipoFinalidad.setId(id);
    modeloTipoFinalidad.setModeloEjecucion(modeloEjecucion);
    modeloTipoFinalidad.setTipoFinalidad(generarMockTipoFinalidad(id, true));
    modeloTipoFinalidad.setActivo(true);

    return modeloTipoFinalidad;
  }

}
