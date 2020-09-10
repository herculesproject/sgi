package org.crue.hercules.sgi.eti.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.FormularioMemoriaNotFoundException;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.FormularioMemoria;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.repository.FormularioMemoriaRepository;
import org.crue.hercules.sgi.eti.service.impl.FormularioMemoriaServiceImpl;
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
 * FormularioMemoriaServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class FormularioMemoriaServiceTest {

  @Mock
  private FormularioMemoriaRepository formularioMemoriaRepository;

  private FormularioMemoriaService formularioMemoriaService;

  @BeforeEach
  public void setUp() throws Exception {
    formularioMemoriaService = new FormularioMemoriaServiceImpl(formularioMemoriaRepository);
  }

  @Test
  public void find_WithId_ReturnsFormularioMemoria() {
    BDDMockito.given(formularioMemoriaRepository.findById(1L))
        .willReturn(Optional.of(generarMockFormularioMemoria(1L)));

    FormularioMemoria formularioMemoria = formularioMemoriaService.findById(1L);

    Assertions.assertThat(formularioMemoria.getId()).as("id").isEqualTo(1L);
    Assertions.assertThat(formularioMemoria.getMemoria()).as("memoria").isNotNull();
    Assertions.assertThat(formularioMemoria.getMemoria().getId()).as("memoria.id").isEqualTo(100L);
    Assertions.assertThat(formularioMemoria.getFormulario()).as("formulario").isNotNull();
    Assertions.assertThat(formularioMemoria.getFormulario().getId()).as("formulario.id").isEqualTo(200L);
    Assertions.assertThat(formularioMemoria.getActivo()).as("activo").isEqualTo(true);
  }

  @Test
  public void find_NotFound_ThrowsFormularioMemoriaNotFoundException() throws Exception {
    BDDMockito.given(formularioMemoriaRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> formularioMemoriaService.findById(1L))
        .isInstanceOf(FormularioMemoriaNotFoundException.class);
  }

  @Test
  public void create_ReturnsFormularioMemoria() {
    // given: Un nuevo formulario memoria
    FormularioMemoria formularioMemoriaNew = generarMockFormularioMemoria(null);

    FormularioMemoria formularioMemoria = generarMockFormularioMemoria(1L);

    BDDMockito.given(formularioMemoriaRepository.save(formularioMemoriaNew)).willReturn(formularioMemoria);

    // when: Creamos el formulario memoria
    FormularioMemoria formularioMemoriaCreado = formularioMemoriaService.create(formularioMemoriaNew);

    // then: el formulario memoria se crea correctamente
    Assertions.assertThat(formularioMemoriaCreado).isNotNull();
    Assertions.assertThat(formularioMemoriaCreado.getId()).as("id").isEqualTo(1L);
    Assertions.assertThat(formularioMemoriaCreado.getMemoria()).as("memoria").isNotNull();
    Assertions.assertThat(formularioMemoriaCreado.getMemoria().getId()).as("memoria.id").isEqualTo(100L);
    Assertions.assertThat(formularioMemoriaCreado.getFormulario()).as("formulario").isNotNull();
    Assertions.assertThat(formularioMemoriaCreado.getFormulario().getId()).as("formulario.id").isEqualTo(200L);
    Assertions.assertThat(formularioMemoriaCreado.getActivo()).as("activo").isEqualTo(true);
  }

  @Test
  public void create_FormularioMemoriaWithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo formulario memoria que ya tiene id
    FormularioMemoria formularioMemoriaNew = generarMockFormularioMemoria(1L);
    // when: Creamos el formulario memoria
    // then: Lanza una excepcion porque el formulario memoria ya tiene id
    Assertions.assertThatThrownBy(() -> formularioMemoriaService.create(formularioMemoriaNew))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsFormularioMemoria() {
    // given: Un nuevo formulario memoria con el formulario actualizado
    FormularioMemoria formularioMemoriaFormularioActualizado = generarMockFormularioMemoria(1L);
    formularioMemoriaFormularioActualizado.setFormulario(new Formulario(201L, null, null, null));

    FormularioMemoria formularioMemoria = generarMockFormularioMemoria(1L);

    BDDMockito.given(formularioMemoriaRepository.findById(1L)).willReturn(Optional.of(formularioMemoria));
    BDDMockito.given(formularioMemoriaRepository.save(formularioMemoriaFormularioActualizado))
        .willReturn(formularioMemoriaFormularioActualizado);

    // when: Actualizamos el formulario memoria
    FormularioMemoria formularioMemoriaActualizado = formularioMemoriaService
        .update(formularioMemoriaFormularioActualizado);

    // then: El formulariom emoria se actualiza correctamente.
    Assertions.assertThat(formularioMemoriaActualizado).isNotNull();
    Assertions.assertThat(formularioMemoriaActualizado.getId()).as("id").isEqualTo(1L);
    Assertions.assertThat(formularioMemoriaActualizado.getMemoria()).as("memoria").isNotNull();
    Assertions.assertThat(formularioMemoriaActualizado.getMemoria().getId()).as("memoria.id").isEqualTo(100L);
    Assertions.assertThat(formularioMemoriaActualizado.getFormulario()).as("formulario").isNotNull();
    Assertions.assertThat(formularioMemoriaActualizado.getFormulario().getId()).as("formulario.id").isEqualTo(201L);
    Assertions.assertThat(formularioMemoriaActualizado.getActivo()).as("activo").isEqualTo(true);
  }

  @Test
  public void update_ThrowsFormularioMemoriaNotFoundException() {
    // given: Un formulario memoria a actualizar
    FormularioMemoria formularioMemoriaResumenActualizado = generarMockFormularioMemoria(1L);

    // then: Lanza una excepcion porque el formulario memoria no existe
    Assertions.assertThatThrownBy(() -> formularioMemoriaService.update(formularioMemoriaResumenActualizado))
        .isInstanceOf(FormularioMemoriaNotFoundException.class);
  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {
    // given: Un formulario memoria que venga sin id
    FormularioMemoria formularioMemoriaActualizado = generarMockFormularioMemoria(null);

    Assertions.assertThatThrownBy(
        // when: update formularioMemoria
        () -> formularioMemoriaService.update(formularioMemoriaActualizado))
        // then: Lanza una excepción, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> formularioMemoriaService.delete(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsFormularioMemoriaNotFoundException() {
    // given: Id no existe
    BDDMockito.given(formularioMemoriaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> formularioMemoriaService.delete(1L))
        // then: Lanza FormularioMemoriaNotFoundException
        .isInstanceOf(FormularioMemoriaNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesFormularioMemoria() {
    // given: Id existente
    BDDMockito.given(formularioMemoriaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(formularioMemoriaRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> formularioMemoriaService.delete(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void findAll_Unlimited_ReturnsFullFormularioMemoriaList() {
    // given: One hundred formularios memorias
    List<FormularioMemoria> formulariosMemorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      formulariosMemorias.add(generarMockFormularioMemoria(Long.valueOf(i)));
    }

    BDDMockito.given(formularioMemoriaRepository.findAll(ArgumentMatchers.<Specification<FormularioMemoria>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(formulariosMemorias));

    // when: find unlimited
    Page<FormularioMemoria> page = formularioMemoriaService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred formularios memorias
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred formularios memorias
    List<FormularioMemoria> formulariosMemorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      formulariosMemorias.add(generarMockFormularioMemoria(Long.valueOf(i)));
    }

    BDDMockito.given(formularioMemoriaRepository.findAll(ArgumentMatchers.<Specification<FormularioMemoria>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<FormularioMemoria>>() {
          @Override
          public Page<FormularioMemoria> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<FormularioMemoria> content = formulariosMemorias.subList(fromIndex, toIndex);
            Page<FormularioMemoria> page = new PageImpl<>(content, pageable, formulariosMemorias.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<FormularioMemoria> page = formularioMemoriaService.findAll(null, paging);

    // then: A Page with ten formularios memorias are returned containing id='31' to
    // '40'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      FormularioMemoria formularioMemoria = page.getContent().get(i);
      Assertions.assertThat(formularioMemoria.getId()).isEqualTo(j);
    }
  }

  /**
   * Función que devuelve un objeto FormularioMemoria
   * 
   * @param id id del formulario memoria
   * @return el objeto FormularioMemoria
   */
  public FormularioMemoria generarMockFormularioMemoria(Long id) {
    Memoria memoria = new Memoria();
    memoria.setId(100L);

    Formulario formulario = new Formulario();
    formulario.setId(200L);

    FormularioMemoria formularioMemoria = new FormularioMemoria();
    formularioMemoria.setId(id);
    formularioMemoria.setMemoria(memoria);
    formularioMemoria.setFormulario(formulario);
    formularioMemoria.setActivo(true);

    return formularioMemoria;
  }

}
