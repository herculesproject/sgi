package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFaseDocumento;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.repository.ModeloTipoFaseDocumentoRepository;
import org.crue.hercules.sgi.csp.service.impl.ModeloTipoFaseDocumentoServiceImpl;
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
 * ModeloTipoFaseDocumentoServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class ModeloTipoFaseDocumentoServiceTest {

  @Mock
  private ModeloTipoFaseDocumentoRepository modeloTipoFaseDocumentoRepository;

  private ModeloTipoFaseDocumentoService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ModeloTipoFaseDocumentoServiceImpl(modeloTipoFaseDocumentoRepository);
  }

  @Test
  public void findAllByModeloEjecucion_ReturnsPage() {
    // given: Una lista con 37 ModeloTipoFaseDocumento para el ModeloEjecucion
    Long idModeloEjecucion = 1L;
    List<ModeloTipoFaseDocumento> modeloTipoFaseDocumentos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      modeloTipoFaseDocumentos.add(generarModeloTipoFaseDocumento(i));
    }

    BDDMockito.given(modeloTipoFaseDocumentoRepository
        .findAll(ArgumentMatchers.<Specification<ModeloTipoFaseDocumento>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ModeloTipoFaseDocumento>>() {
          @Override
          public Page<ModeloTipoFaseDocumento> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > modeloTipoFaseDocumentos.size() ? modeloTipoFaseDocumentos.size() : toIndex;
            List<ModeloTipoFaseDocumento> content = modeloTipoFaseDocumentos.subList(fromIndex, toIndex);
            Page<ModeloTipoFaseDocumento> page = new PageImpl<>(content, pageable, modeloTipoFaseDocumentos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ModeloTipoFaseDocumento> page = service.findAllByModeloEjecucion(idModeloEjecucion, null, paging);

    // then: Devuelve la pagina 3 con los ModeloTipoFaseDocumento del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ModeloTipoFaseDocumento modeloTipoFaseDocumento = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(modeloTipoFaseDocumento.getTipoDocumento().getNombre())
          .isEqualTo("TipoDocumento" + String.format("%03d", i));
    }
  }

  /**
   * Función que devuelve un objeto TipoDocumento
   * 
   * @param id id del TipoDocumento
   * @return el objeto TipoDocumento
   */
  private TipoDocumento generarMockTipoDocumento(Long id, String nombre) {

    TipoDocumento tipoDocumento = new TipoDocumento();
    tipoDocumento.setId(id);
    tipoDocumento.setNombre(nombre);
    tipoDocumento.setDescripcion("descripcion-" + id);
    tipoDocumento.setActivo(Boolean.TRUE);

    return tipoDocumento;
  }

  /**
   * Función que devuelve un objeto ModeloTipoFaseDocumento
   * 
   * @param id id del ModeloTipoFaseDocumento
   * @return el objeto ModeloTipoFaseDocumento
   */
  private ModeloTipoFaseDocumento generarModeloTipoFaseDocumento(Long id) {
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(1L);

    ModeloTipoFaseDocumento modeloTipoFaseDocumento = new ModeloTipoFaseDocumento();
    modeloTipoFaseDocumento.setId(id);
    modeloTipoFaseDocumento.setModeloEjecucion(modeloEjecucion);
    modeloTipoFaseDocumento.setTipoDocumento(generarMockTipoDocumento(id, "TipoDocumento" + String.format("%03d", id)));
    modeloTipoFaseDocumento.setModeloTipoFase(null);
    modeloTipoFaseDocumento.setActivo(true);

    return modeloTipoFaseDocumento;
  }

}