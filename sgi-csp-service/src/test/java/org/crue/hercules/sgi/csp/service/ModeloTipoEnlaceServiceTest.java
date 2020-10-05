package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoEnlace;
import org.crue.hercules.sgi.csp.model.TipoEnlace;
import org.crue.hercules.sgi.csp.repository.ModeloTipoEnlaceRepository;
import org.crue.hercules.sgi.csp.service.impl.ModeloTipoEnlaceServiceImpl;
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
public class ModeloTipoEnlaceServiceTest {

  @Mock
  private ModeloTipoEnlaceRepository modeloTipoEnlaceRepository;

  private ModeloTipoEnlaceService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ModeloTipoEnlaceServiceImpl(modeloTipoEnlaceRepository);
  }

  @Test
  public void findAllByModeloEjecucion_ReturnsPage() {
    // given: Una lista con 37 ModeloTipoEnlace para el ModeloEjecucion
    Long idModeloEjecucion = 1L;
    List<ModeloTipoEnlace> modeloTipoEnlaces = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      modeloTipoEnlaces.add(generarModeloTipoEnlace(i));
    }

    BDDMockito.given(modeloTipoEnlaceRepository.findAll(ArgumentMatchers.<Specification<ModeloTipoEnlace>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<ModeloTipoEnlace>>() {
          @Override
          public Page<ModeloTipoEnlace> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > modeloTipoEnlaces.size() ? modeloTipoEnlaces.size() : toIndex;
            List<ModeloTipoEnlace> content = modeloTipoEnlaces.subList(fromIndex, toIndex);
            Page<ModeloTipoEnlace> page = new PageImpl<>(content, pageable, modeloTipoEnlaces.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ModeloTipoEnlace> page = service.findAllByModeloEjecucion(idModeloEjecucion, null, paging);

    // then: Devuelve la pagina 3 con los TipoEnlace del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ModeloTipoEnlace modeloTipoEnlace = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(modeloTipoEnlace.getTipoEnlace().getNombre()).isEqualTo("nombre-" + i);
    }
  }

  /**
   * Función que devuelve un objeto TipoEnlace
   * 
   * @param id
   * @param activo
   * @return TipoEnlace
   */
  private TipoEnlace generarMockTipoEnlace(Long id, Boolean activo) {
    return TipoEnlace.builder().id(id).nombre("nombre-" + id).descripcion("descripcion-" + id).activo(activo).build();
  }

  /**
   * Función que devuelve un objeto ModeloTipoEnlace
   * 
   * @param id id del ModeloTipoEnlace
   * @return el objeto ModeloTipoEnlace
   */
  private ModeloTipoEnlace generarModeloTipoEnlace(Long id) {
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);

    ModeloTipoEnlace modeloTipoFinalidad = new ModeloTipoEnlace();
    modeloTipoFinalidad.setId(id);
    modeloTipoFinalidad.setModeloEjecucion(modeloEjecucion);
    modeloTipoFinalidad.setTipoEnlace(generarMockTipoEnlace(id, true));
    modeloTipoFinalidad.setActivo(true);

    return modeloTipoFinalidad;
  }

}
