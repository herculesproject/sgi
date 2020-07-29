package org.crue.hercules.sgi.eti.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.TipoEvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.eti.repository.TipoEvaluacionRepository;
import org.crue.hercules.sgi.eti.service.impl.TipoEvaluacionServiceImpl;
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
 * TipoEvaluacionServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class TipoEvaluacionServiceTest {

  @Mock
  private TipoEvaluacionRepository tipoEvaluacionRepository;

  private TipoEvaluacionService tipoEvaluacionService;

  @BeforeEach
  public void setUp() throws Exception {
    tipoEvaluacionService = new TipoEvaluacionServiceImpl(tipoEvaluacionRepository);
  }

  @Test
  public void find_WithId_ReturnsTipoEvaluacion() {
    BDDMockito.given(tipoEvaluacionRepository.findById(1L))
        .willReturn(Optional.of(generarMockTipoEvaluacion(1L, "TipoEvaluacion1")));

    TipoEvaluacion tipoEvaluacion = tipoEvaluacionService.findById(1L);

    Assertions.assertThat(tipoEvaluacion.getId()).isEqualTo(1L);

    Assertions.assertThat(tipoEvaluacion.getNombre()).isEqualTo("TipoEvaluacion1");

  }

  @Test
  public void find_NotFound_ThrowsTipoEvaluacionNotFoundException() throws Exception {
    BDDMockito.given(tipoEvaluacionRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> tipoEvaluacionService.findById(1L))
        .isInstanceOf(TipoEvaluacionNotFoundException.class);
  }

  @Test
  public void create_ReturnsTipoEvaluacion() {
    // given: Un nuevo TipoEvaluacion
    TipoEvaluacion tipoEvaluacionNew = generarMockTipoEvaluacion(1L, "TipoEvaluacion2");

    TipoEvaluacion tipoEvaluacion = generarMockTipoEvaluacion(1L, "TipoEvaluacion2");

    BDDMockito.given(tipoEvaluacionRepository.save(tipoEvaluacionNew)).willReturn(tipoEvaluacion);

    // when: Creamos el tipo Evaluacion
    TipoEvaluacion tipoEvaluacionCreado = tipoEvaluacionService.create(tipoEvaluacionNew);

    // then: El tipo Evaluacion se crea correctamente
    Assertions.assertThat(tipoEvaluacionCreado).isNotNull();
    Assertions.assertThat(tipoEvaluacionCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoEvaluacionCreado.getNombre()).isEqualTo("TipoEvaluacion2");
  }

  @Test
  public void create_TipoEvaluacionWithoutId_ThrowsIllegalArgumentException() {
    // given: Un nuevo tipo de Evaluacion sin id
    TipoEvaluacion tipoEvaluacionNew = generarMockTipoEvaluacion(null, "TipoEvaluacion2");
    // when: Creamos el tipo de Evaluacion
    // then: Lanza una excepcion porque el tipo Evaluacion ya tiene id
    Assertions.assertThatThrownBy(() -> tipoEvaluacionService.create(tipoEvaluacionNew))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsTipoEvaluacion() {
    // given: Un nuevo tipo Evaluacion con el servicio actualizado
    TipoEvaluacion tipoEvaluacionServicioActualizado = generarMockTipoEvaluacion(1L, "TipoEvaluacion1 actualizada");

    TipoEvaluacion tipoEvaluacion = generarMockTipoEvaluacion(1L, "TipoEvaluacion1");

    BDDMockito.given(tipoEvaluacionRepository.findById(1L)).willReturn(Optional.of(tipoEvaluacion));
    BDDMockito.given(tipoEvaluacionRepository.save(tipoEvaluacion)).willReturn(tipoEvaluacionServicioActualizado);

    // when: Actualizamos el tipo Evaluacion
    TipoEvaluacion tipoEvaluacionActualizado = tipoEvaluacionService.update(tipoEvaluacion);

    // then: El tipo Evaluacion se actualiza correctamente.
    Assertions.assertThat(tipoEvaluacionActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoEvaluacionActualizado.getNombre()).isEqualTo("TipoEvaluacion1 actualizada");

  }

  @Test
  public void update_ThrowsTipoEvaluacionNotFoundException() {
    // given: Un nuevo tipo Evaluacion a actualizar
    TipoEvaluacion tipoEvaluacion = generarMockTipoEvaluacion(1L, "TipoEvaluacion");

    // then: Lanza una excepcion porque el tipo Evaluacion no existe
    Assertions.assertThatThrownBy(() -> tipoEvaluacionService.update(tipoEvaluacion))
        .isInstanceOf(TipoEvaluacionNotFoundException.class);

  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {

    // given: Un TipoEvaluacion que venga sin id
    TipoEvaluacion tipoEvaluacion = generarMockTipoEvaluacion(null, "TipoEvaluacion");

    Assertions.assertThatThrownBy(
        // when: update TipoEvaluacion
        () -> tipoEvaluacionService.update(tipoEvaluacion))
        // then: Lanza una excepción, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> tipoEvaluacionService.delete(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsTipoEvaluacionNotFoundException() {
    // given: Id no existe
    BDDMockito.given(tipoEvaluacionRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> tipoEvaluacionService.delete(1L))
        // then: Lanza TipoEvaluacionNotFoundException
        .isInstanceOf(TipoEvaluacionNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesTipoEvaluacion() {
    // given: Id existente
    BDDMockito.given(tipoEvaluacionRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(tipoEvaluacionRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> tipoEvaluacionService.delete(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void findAll_Unlimited_ReturnsFullTipoEvaluacionList() {
    // given: One hundred TipoEvaluacion
    List<TipoEvaluacion> tipoEvaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoEvaluaciones.add(generarMockTipoEvaluacion(Long.valueOf(i), "TipoEvaluacion" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoEvaluacionRepository.findAll(ArgumentMatchers.<Specification<TipoEvaluacion>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(tipoEvaluaciones));

    // when: find unlimited
    Page<TipoEvaluacion> page = tipoEvaluacionService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred TipoEvaluaciones
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred TipoEvaluaciones
    List<TipoEvaluacion> tipoEvaluaciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoEvaluaciones.add(generarMockTipoEvaluacion(Long.valueOf(i), "TipoEvaluacion" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoEvaluacionRepository.findAll(ArgumentMatchers.<Specification<TipoEvaluacion>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<TipoEvaluacion>>() {
          @Override
          public Page<TipoEvaluacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoEvaluacion> content = tipoEvaluaciones.subList(fromIndex, toIndex);
            Page<TipoEvaluacion> page = new PageImpl<>(content, pageable, tipoEvaluaciones.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<TipoEvaluacion> page = tipoEvaluacionService.findAll(null, paging);

    // then: A Page with ten TipoEvaluaciones are returned containing
    // descripcion='TipoEvaluacion031' to 'TipoEvaluacion040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoEvaluacion tipoEvaluacion = page.getContent().get(i);
      Assertions.assertThat(tipoEvaluacion.getNombre()).isEqualTo("TipoEvaluacion" + String.format("%03d", j));
    }
  }

  /**
   * Función que devuelve un objeto TipoEvaluacion
   * 
   * @param id     id del TipoEvaluacion
   * @param nombre nombre del TipoEvaluacion
   * @return el objeto TipoEvaluacion
   */

  public TipoEvaluacion generarMockTipoEvaluacion(Long id, String nombre) {

    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion();
    tipoEvaluacion.setId(id);
    tipoEvaluacion.setNombre(nombre);
    tipoEvaluacion.setActivo(Boolean.TRUE);

    return tipoEvaluacion;
  }
}