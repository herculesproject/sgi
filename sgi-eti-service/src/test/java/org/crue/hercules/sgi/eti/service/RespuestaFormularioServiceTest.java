package org.crue.hercules.sgi.eti.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.RespuestaFormularioNotFoundException;
import org.crue.hercules.sgi.eti.model.ComponenteFormulario;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.FormularioMemoria;
import org.crue.hercules.sgi.eti.model.RespuestaFormulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.repository.RespuestaFormularioRepository;
import org.crue.hercules.sgi.eti.service.impl.RespuestaFormularioServiceImpl;
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
 * RespuestaFormularioServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class RespuestaFormularioServiceTest {

  @Mock
  private RespuestaFormularioRepository respuestaFormularioRepository;

  private RespuestaFormularioService respuestaFormularioService;

  @BeforeEach
  public void setUp() throws Exception {
    respuestaFormularioService = new RespuestaFormularioServiceImpl(respuestaFormularioRepository);
  }

  @Test
  public void find_WithId_ReturnsRespuestaFormulario() {
    BDDMockito.given(respuestaFormularioRepository.findById(1L))
        .willReturn(Optional.of(generarMockRespuestaFormulario(1L)));

    RespuestaFormulario respuestaFormulario = respuestaFormularioService.findById(1L);

    Assertions.assertThat(respuestaFormulario.getId()).isEqualTo(1L);
    Assertions.assertThat(respuestaFormulario.getFormularioMemoria().getId()).isEqualTo(1L);
    Assertions.assertThat(respuestaFormulario.getComponenteFormulario().getEsquema()).isEqualTo("Esquema1");
    Assertions.assertThat(respuestaFormulario.getValor()).isEqualTo("Valor1");

  }

  @Test
  public void find_NotFound_ThrowsRespuestaFormularioNotFoundException() throws Exception {
    BDDMockito.given(respuestaFormularioRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> respuestaFormularioService.findById(1L))
        .isInstanceOf(RespuestaFormularioNotFoundException.class);
  }

  @Test
  public void create_ReturnsRespuestaFormulario() {
    // given: Un nuevo RespuestaFormulario
    RespuestaFormulario respuestaFormularioNew = generarMockRespuestaFormulario(null);

    RespuestaFormulario respuestaFormulario = generarMockRespuestaFormulario(1L);
    respuestaFormulario.setValor("ValorNew");

    BDDMockito.given(respuestaFormularioRepository.save(respuestaFormularioNew)).willReturn(respuestaFormulario);

    // when: Creamos el RespuestaFormulario
    RespuestaFormulario respuestaFormularioCreado = respuestaFormularioService.create(respuestaFormularioNew);

    // then: El RespuestaFormulario se crea correctamente
    Assertions.assertThat(respuestaFormularioCreado).isNotNull();
    Assertions.assertThat(respuestaFormularioCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(respuestaFormularioCreado.getValor()).isEqualTo("ValorNew");
  }

  @Test
  public void create_RespuestaFormularioWithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo RespuestaFormulario que ya tiene id
    RespuestaFormulario respuestaFormularioNew = generarMockRespuestaFormulario(1L);
    // when: Creamos el RespuestaFormulario
    // then: Lanza una excepcion porque el RespuestaFormulario ya tiene id
    Assertions.assertThatThrownBy(() -> respuestaFormularioService.create(respuestaFormularioNew))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsRespuestaFormulario() {
    // given: Un nuevo RespuestaFormulario con el servicio actualizado
    RespuestaFormulario respuestaFormularioServicioActualizado = generarMockRespuestaFormulario(1L);
    respuestaFormularioServicioActualizado.setValor("Valor actualizado");

    RespuestaFormulario respuestaFormulario = generarMockRespuestaFormulario(1L);

    BDDMockito.given(respuestaFormularioRepository.findById(1L)).willReturn(Optional.of(respuestaFormulario));
    BDDMockito.given(respuestaFormularioRepository.save(respuestaFormulario))
        .willReturn(respuestaFormularioServicioActualizado);

    // when: Actualizamos el RespuestaFormulario
    RespuestaFormulario RespuestaFormularioActualizado = respuestaFormularioService.update(respuestaFormulario);

    // then: El RespuestaFormulario se actualiza correctamente.
    Assertions.assertThat(RespuestaFormularioActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(RespuestaFormularioActualizado.getValor()).isEqualTo("Valor actualizado");

  }

  @Test
  public void update_ThrowsRespuestaFormularioNotFoundException() {
    // given: Un nuevo RespuestaFormulario a actualizar
    RespuestaFormulario respuestaFormulario = generarMockRespuestaFormulario(1L);

    // then: Lanza una excepcion porque el RespuestaFormulario no existe
    Assertions.assertThatThrownBy(() -> respuestaFormularioService.update(respuestaFormulario))
        .isInstanceOf(RespuestaFormularioNotFoundException.class);

  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {

    // given: Un RespuestaFormulario que venga sin id
    RespuestaFormulario respuestaFormulario = generarMockRespuestaFormulario(null);

    Assertions.assertThatThrownBy(
        // when: update RespuestaFormulario
        () -> respuestaFormularioService.update(respuestaFormulario))
        // then: Lanza una excepción, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> respuestaFormularioService.delete(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsRespuestaFormularioNotFoundException() {
    // given: Id no existe
    BDDMockito.given(respuestaFormularioRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> respuestaFormularioService.delete(1L))
        // then: Lanza RespuestaFormularioNotFoundException
        .isInstanceOf(RespuestaFormularioNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesRespuestaFormulario() {
    // given: Id existente
    BDDMockito.given(respuestaFormularioRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(respuestaFormularioRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> respuestaFormularioService.delete(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void findAll_Unlimited_ReturnsFullRespuestaFormularioList() {
    // given: One hundred RespuestaFormulario
    List<RespuestaFormulario> respuestaFormularios = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      respuestaFormularios.add(generarMockRespuestaFormulario(Long.valueOf(i)));
    }

    BDDMockito.given(respuestaFormularioRepository.findAll(ArgumentMatchers.<Specification<RespuestaFormulario>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(respuestaFormularios));

    // when: find unlimited
    Page<RespuestaFormulario> page = respuestaFormularioService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred RespuestaFormularios
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred RespuestaFormularios
    List<RespuestaFormulario> respuestaFormularios = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      respuestaFormularios.add(generarMockRespuestaFormulario(Long.valueOf(i)));
    }

    BDDMockito.given(respuestaFormularioRepository.findAll(ArgumentMatchers.<Specification<RespuestaFormulario>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<RespuestaFormulario>>() {
          @Override
          public Page<RespuestaFormulario> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<RespuestaFormulario> content = respuestaFormularios.subList(fromIndex, toIndex);
            Page<RespuestaFormulario> page = new PageImpl<>(content, pageable, respuestaFormularios.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<RespuestaFormulario> page = respuestaFormularioService.findAll(null, paging);

    // then: A Page with ten RespuestaFormularios are returned containing
    // descripcion='RespuestaFormulario031' to 'RespuestaFormulario040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      RespuestaFormulario RespuestaFormulario = page.getContent().get(i);
      Assertions.assertThat(RespuestaFormulario.getId()).isEqualTo(j);
    }
  }

  /**
   * Función que devuelve un objeto RespuestaFormulario
   * 
   * @param id id del RespuestaFormulario
   * @return el objeto RespuestaFormulario
   */

  public RespuestaFormulario generarMockRespuestaFormulario(Long id) {
    Memoria memoria = new Memoria();
    memoria.setId(id);

    Formulario formulario = new Formulario();
    formulario.setId(id);

    FormularioMemoria formularioMemoria = new FormularioMemoria();
    formularioMemoria.setId(id);
    formularioMemoria.setMemoria(memoria);
    formularioMemoria.setFormulario(formulario);
    formularioMemoria.setActivo(true);

    ComponenteFormulario componenteFormulario = new ComponenteFormulario();
    componenteFormulario.setId(id);
    componenteFormulario.setEsquema("Esquema" + id);

    RespuestaFormulario respuestaFormulario = new RespuestaFormulario();
    respuestaFormulario.setId(id);
    respuestaFormulario.setFormularioMemoria(formularioMemoria);
    respuestaFormulario.setComponenteFormulario(componenteFormulario);
    respuestaFormulario.setValor("Valor" + id);

    return respuestaFormulario;
  }
}