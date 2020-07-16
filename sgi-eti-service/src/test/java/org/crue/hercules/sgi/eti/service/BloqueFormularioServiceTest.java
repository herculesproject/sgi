package org.crue.hercules.sgi.eti.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.BloqueFormularioNotFoundException;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.BloqueFormulario;
import org.crue.hercules.sgi.eti.repository.BloqueFormularioRepository;
import org.crue.hercules.sgi.eti.service.impl.BloqueFormularioServiceImpl;
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
 * BloqueFormularioServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class BloqueFormularioServiceTest {

  @Mock
  private BloqueFormularioRepository bloqueFormularioRepository;

  private BloqueFormularioService bloqueFormularioService;

  @BeforeEach
  public void setUp() throws Exception {
    bloqueFormularioService = new BloqueFormularioServiceImpl(bloqueFormularioRepository);
  }

  @Test
  public void find_WithId_ReturnsBloqueFormulario() {
    BDDMockito.given(bloqueFormularioRepository.findById(1L))
        .willReturn(Optional.of(generarMockBloqueFormulario(1L, "BloqueFormulario1")));

    BloqueFormulario bloqueFormulario = bloqueFormularioService.findById(1L);

    Assertions.assertThat(bloqueFormulario.getId()).isEqualTo(1L);

    Assertions.assertThat(bloqueFormulario.getNombre()).isEqualTo("BloqueFormulario1");

  }

  @Test
  public void find_NotFound_ThrowsBloqueFormularioNotFoundException() throws Exception {
    BDDMockito.given(bloqueFormularioRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> bloqueFormularioService.findById(1L))
        .isInstanceOf(BloqueFormularioNotFoundException.class);
  }

  @Test
  public void create_ReturnsBloqueFormulario() {
    // given: Un nuevo BloqueFormulario
    BloqueFormulario bloqueFormularioNew = generarMockBloqueFormulario(null, "BloqueFormularioNew");

    BloqueFormulario bloqueFormulario = generarMockBloqueFormulario(1L, "BloqueFormularioNew");

    BDDMockito.given(bloqueFormularioRepository.save(bloqueFormularioNew)).willReturn(bloqueFormulario);

    // when: Creamos el BloqueFormulario
    BloqueFormulario bloqueFormularioCreado = bloqueFormularioService.create(bloqueFormularioNew);

    // then: El BloqueFormulario se crea correctamente
    Assertions.assertThat(bloqueFormularioCreado).isNotNull();
    Assertions.assertThat(bloqueFormularioCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(bloqueFormularioCreado.getNombre()).isEqualTo("BloqueFormularioNew");
  }

  @Test
  public void create_BloqueFormularioWithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo bloqueFormulario que ya tiene id
    BloqueFormulario bloqueFormularioNew = generarMockBloqueFormulario(1L, "BloqueFormularioNew");
    // when: Creamos el bloqueFormulario
    // then: Lanza una excepcion porque el bloqueFormulario ya tiene id
    Assertions.assertThatThrownBy(() -> bloqueFormularioService.create(bloqueFormularioNew))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsBloqueFormulario() {
    // given: Un nuevo bloqueFormulario con el servicio actualizado
    BloqueFormulario bloqueFormularioServicioActualizado = generarMockBloqueFormulario(1L,
        "BloqueFormulario1 actualizada");

    BloqueFormulario bloqueFormulario = generarMockBloqueFormulario(1L, "BloqueFormulario1");

    BDDMockito.given(bloqueFormularioRepository.findById(1L)).willReturn(Optional.of(bloqueFormulario));
    BDDMockito.given(bloqueFormularioRepository.save(bloqueFormulario)).willReturn(bloqueFormularioServicioActualizado);

    // when: Actualizamos el bloqueFormulario
    BloqueFormulario bloqueFormularioActualizado = bloqueFormularioService.update(bloqueFormulario);

    // then: El bloqueFormulario se actualiza correctamente.
    Assertions.assertThat(bloqueFormularioActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(bloqueFormularioActualizado.getNombre()).isEqualTo("BloqueFormulario1 actualizada");

  }

  @Test
  public void update_ThrowsBloqueFormularioNotFoundException() {
    // given: Un nuevo bloqueFormulario a actualizar
    BloqueFormulario bloqueFormulario = generarMockBloqueFormulario(1L, "BloqueFormulario");

    // then: Lanza una excepcion porque el bloqueFormulario no existe
    Assertions.assertThatThrownBy(() -> bloqueFormularioService.update(bloqueFormulario))
        .isInstanceOf(BloqueFormularioNotFoundException.class);

  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {

    // given: Un BloqueFormulario que venga sin id
    BloqueFormulario bloqueFormulario = generarMockBloqueFormulario(null, "BloqueFormulario");

    Assertions.assertThatThrownBy(
        // when: update BloqueFormulario
        () -> bloqueFormularioService.update(bloqueFormulario))
        // then: Lanza una excepción, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> bloqueFormularioService.delete(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsBloqueFormularioNotFoundException() {
    // given: Id no existe
    BDDMockito.given(bloqueFormularioRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> bloqueFormularioService.delete(1L))
        // then: Lanza BloqueFormularioNotFoundException
        .isInstanceOf(BloqueFormularioNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesBloqueFormulario() {
    // given: Id existente
    BDDMockito.given(bloqueFormularioRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(bloqueFormularioRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> bloqueFormularioService.delete(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void findAll_Unlimited_ReturnsFullBloqueFormularioList() {
    // given: One hundred BloqueFormulario
    List<BloqueFormulario> bloqueFormularios = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      bloqueFormularios
          .add(generarMockBloqueFormulario(Long.valueOf(i), "BloqueFormulario" + String.format("%03d", i)));
    }

    BDDMockito.given(bloqueFormularioRepository.findAll(ArgumentMatchers.<Specification<BloqueFormulario>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(bloqueFormularios));

    // when: find unlimited
    Page<BloqueFormulario> page = bloqueFormularioService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred BloqueFormularios
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred BloqueFormularios
    List<BloqueFormulario> bloqueFormularios = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      bloqueFormularios
          .add(generarMockBloqueFormulario(Long.valueOf(i), "BloqueFormulario" + String.format("%03d", i)));
    }

    BDDMockito.given(bloqueFormularioRepository.findAll(ArgumentMatchers.<Specification<BloqueFormulario>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<BloqueFormulario>>() {
          @Override
          public Page<BloqueFormulario> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<BloqueFormulario> content = bloqueFormularios.subList(fromIndex, toIndex);
            Page<BloqueFormulario> page = new PageImpl<>(content, pageable, bloqueFormularios.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<BloqueFormulario> page = bloqueFormularioService.findAll(null, paging);

    // then: A Page with ten BloqueFormularios are returned containing
    // nombre='BloqueFormulario031' to 'BloqueFormulario040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      BloqueFormulario bloqueFormulario = page.getContent().get(i);
      Assertions.assertThat(bloqueFormulario.getNombre()).isEqualTo("BloqueFormulario" + String.format("%03d", j));
    }
  }

  /**
   * Función que devuelve un objeto BloqueFormulario
   * 
   * @param id     id del BloqueFormulario
   * @param nombre el nombre de BloqueFormulario
   * @return el objeto BloqueFormulario
   */

  public BloqueFormulario generarMockBloqueFormulario(Long id, String nombre) {

    Formulario formulario = new Formulario();
    formulario.setId(1L);
    formulario.setNombre("Formulario1");
    formulario.setDescripcion("Descripcion formulario 1");
    formulario.setActivo(Boolean.TRUE);

    BloqueFormulario bloqueFormulario = new BloqueFormulario();
    bloqueFormulario.setId(id);
    bloqueFormulario.setFormulario(formulario);
    bloqueFormulario.setNombre(nombre);
    bloqueFormulario.setOrden(1);
    bloqueFormulario.setActivo(Boolean.TRUE);

    return bloqueFormulario;
  }
}