package org.crue.hercules.sgi.eti.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.TipoTarea;
import org.crue.hercules.sgi.eti.repository.TipoTareaRepository;
import org.crue.hercules.sgi.eti.service.impl.TipoTareaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;

/**
 * TipoTareaServiceTest
 */
public class TipoTareaServiceTest extends BaseServiceTest {

  @Mock
  private TipoTareaRepository tipoTareaRepository;

  private TipoTareaService tipoTareaService;

  @BeforeEach
  public void setUp() throws Exception {
    tipoTareaService = new TipoTareaServiceImpl(tipoTareaRepository);
  }

  @Test
  public void find_WithId_ReturnsTipoTarea() {
    BDDMockito.given(tipoTareaRepository.findById(1L)).willReturn(Optional.of(generarMockTipoTarea(1L, "TipoTarea1")));

    TipoTarea tipoTarea = tipoTareaService.findById(1L);

    Assertions.assertThat(tipoTarea.getId()).isEqualTo(1L);

    Assertions.assertThat(tipoTarea.getNombre()).isEqualTo("TipoTarea1");

  }

  @Test
  public void create_ReturnsTipoTarea() {
    // given: Un nuevo TipoTarea
    TipoTarea tipoTareaNew = generarMockTipoTarea(1L, "TipoTareaNew");

    TipoTarea tipoTarea = generarMockTipoTarea(1L, "TipoTareaNew");

    BDDMockito.given(tipoTareaRepository.save(tipoTareaNew)).willReturn(tipoTarea);

    // when: Creamos el tipo Tarea
    TipoTarea tipoTareaCreado = tipoTareaService.create(tipoTareaNew);

    // then: El tipo Tarea se crea correctamente
    Assertions.assertThat(tipoTareaCreado).isNotNull();
    Assertions.assertThat(tipoTareaCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoTareaCreado.getNombre()).isEqualTo("TipoTareaNew");
  }

  /**
   * Función que devuelve un objeto TipoTarea
   * 
   * @param id     id del tipoTarea
   * @param nombre nombre del tipo de Tarea
   * @return el objeto tipo Tarea
   */

  public TipoTarea generarMockTipoTarea(Long id, String nombre) {

    TipoTarea tipoTarea = new TipoTarea();
    tipoTarea.setId(id);
    tipoTarea.setNombre(nombre);
    tipoTarea.setActivo(Boolean.TRUE);

    return tipoTarea;
  }
}