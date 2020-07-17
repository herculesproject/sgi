package org.crue.hercules.sgi.eti.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.ConfiguracionNotFoundException;
import org.crue.hercules.sgi.eti.model.Configuracion;
import org.crue.hercules.sgi.eti.repository.ConfiguracionRepository;
import org.crue.hercules.sgi.eti.service.impl.ConfiguracionServiceImpl;
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
 * ConfiguracionServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class ConfiguracionServiceTest {

  @Mock
  private ConfiguracionRepository configuracionRepository;

  private ConfiguracionService configuracionService;

  @BeforeEach
  public void setUp() throws Exception {
    configuracionService = new ConfiguracionServiceImpl(configuracionRepository);
  }

  @Test
  public void find_WithId_ReturnsConfiguracion() {
    BDDMockito.given(configuracionRepository.findById(1L))
        .willReturn(Optional.of(generarMockConfiguracion(1L, "Configuracion1")));

    Configuracion configuracion = configuracionService.findById(1L);

    Assertions.assertThat(configuracion.getId()).isEqualTo(1L);

    Assertions.assertThat(configuracion.getClave()).isEqualTo("Configuracion1");
  }

  @Test
  public void find_NotFound_ThrowsConfiguracionNotFoundException() throws Exception {
    BDDMockito.given(configuracionRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> configuracionService.findById(1L))
        .isInstanceOf(ConfiguracionNotFoundException.class);
  }

  @Test
  public void create_ReturnsConfiguracion() {
    // given: Una nueva Configuracion
    Configuracion configuracionNew = generarMockConfiguracion(null, "Configuracion New");

    Configuracion configuracion = generarMockConfiguracion(1L, "Configuracion New");

    BDDMockito.given(configuracionRepository.save(configuracionNew)).willReturn(configuracion);

    // when: Creamos la Configuracion
    Configuracion configuracionCreado = configuracionService.create(configuracionNew);

    // then: La Configuracion se crea correctamente
    Assertions.assertThat(configuracionCreado).isNotNull();
    Assertions.assertThat(configuracionCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(configuracion.getClave()).isEqualTo("Configuracion New");
  }

  @Test
  public void create_ConfiguracionWithId_ThrowsIllegalArgumentException() {
    // given: Una nueva configuracion que ya tiene id
    Configuracion configuracionNew = generarMockConfiguracion(1L, "Configuracion New");
    // when: Creamos la configuracion
    // then: Lanza una excepcion porque la configuracion ya tiene id
    Assertions.assertThatThrownBy(() -> configuracionService.create(configuracionNew))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsConfiguracion() {
    // given: Una nueva configuracion con el servicio actualizado
    Configuracion configuracionServicioActualizado = generarMockConfiguracion(1L, "Configuracion actualizada");

    Configuracion configuracion = generarMockConfiguracion(1L, "Configuracion1");

    BDDMockito.given(configuracionRepository.findById(1L)).willReturn(Optional.of(configuracion));
    BDDMockito.given(configuracionRepository.save(configuracion)).willReturn(configuracionServicioActualizado);

    // when: Actualizamos la configuracion
    Configuracion configuracionActualizado = configuracionService.update(configuracion);

    // then: La configuracion se actualiza correctamente.
    Assertions.assertThat(configuracionActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(configuracionActualizado.getClave()).isEqualTo("Configuracion actualizada");
  }

  @Test
  public void update_ThrowsConfiguracionNotFoundException() {
    // given: Una nueva configuracion a actualizar
    Configuracion configuracion = generarMockConfiguracion(1L, "Configuracion1");

    // then: Lanza una excepcion porque la configuracion no existe
    Assertions.assertThatThrownBy(() -> configuracionService.update(configuracion))
        .isInstanceOf(ConfiguracionNotFoundException.class);

  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {

    // given: Una Configuracion que venga sin id
    Configuracion configuracion = generarMockConfiguracion(null, "Configuracion1");

    Assertions.assertThatThrownBy(
        // when: update Configuracion
        () -> configuracionService.update(configuracion))
        // then: Lanza una excepción, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> configuracionService.delete(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsConfiguracionNotFoundException() {
    // given: Id no existe
    BDDMockito.given(configuracionRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> configuracionService.delete(1L))
        // then: Lanza ConfiguracionNotFoundException
        .isInstanceOf(ConfiguracionNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesConfiguracion() {
    // given: Id existente
    BDDMockito.given(configuracionRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(configuracionRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> configuracionService.delete(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void findAll_Unlimited_ReturnsFullConfiguracionList() {
    // given: One hundred Configuracion
    List<Configuracion> configuraciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      configuraciones.add(generarMockConfiguracion(Long.valueOf(i), "Configuracion" + String.format("%03d", i)));
    }

    BDDMockito.given(configuracionRepository.findAll(ArgumentMatchers.<Specification<Configuracion>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(configuraciones));

    // when: find unlimited
    Page<Configuracion> page = configuracionService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred Configuraciones
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred Configuraciones
    List<Configuracion> configuraciones = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      configuraciones.add(generarMockConfiguracion(Long.valueOf(i), "Configuracion" + String.format("%03d", i)));
    }

    BDDMockito.given(configuracionRepository.findAll(ArgumentMatchers.<Specification<Configuracion>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<Configuracion>>() {
          @Override
          public Page<Configuracion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Configuracion> content = configuraciones.subList(fromIndex, toIndex);
            Page<Configuracion> page = new PageImpl<>(content, pageable, configuraciones.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Configuracion> page = configuracionService.findAll(null, paging);

    // then: A Page with ten Configuraciones are returned containing
    // clave='Configuracion031' to 'Configuracion040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Configuracion configuracion = page.getContent().get(i);
      Assertions.assertThat(configuracion.getClave()).isEqualTo("Configuracion" + String.format("%03d", j));
    }
  }

  /**
   * Función que devuelve un objeto Configuracion
   * 
   * @param id    id del Configuracion
   * @param clave la clave de la Configuracion
   * @return el objeto Configuracion
   */

  public Configuracion generarMockConfiguracion(Long id, String clave) {

    Configuracion configuracion = new Configuracion();
    configuracion.setId(id);
    configuracion.setClave(clave);
    configuracion.setDescripcion("Descripcion" + id);
    configuracion.setValor("Valor" + id);

    return configuracion;
  }

}