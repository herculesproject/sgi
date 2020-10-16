package org.crue.hercules.sgi.eti.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.ComiteFormularioNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.ComiteNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ComiteFormulario;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.repository.ComiteFormularioRepository;
import org.crue.hercules.sgi.eti.repository.ComiteRepository;
import org.crue.hercules.sgi.eti.service.impl.ComiteFormularioServiceImpl;
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
 * ComiteFormularioServiceTest
 */
@ExtendWith(MockitoExtension.class)

public class ComiteFormularioServiceTest {

  @Mock
  private ComiteFormularioRepository comiteFormularioRepository;

  @Mock
  private ComiteRepository comiteRepository;

  private ComiteFormularioService comiteFormularioService;

  @BeforeEach
  public void setUp() throws Exception {
    comiteFormularioService = new ComiteFormularioServiceImpl(comiteFormularioRepository, comiteRepository);
  }

  @Test
  public void find_WithId_ReturnsComiteFormulario() {

    Comite comite = new Comite(1L, "Comite1", Boolean.TRUE);
    Formulario formulario = new Formulario(1L, "M10", "Descripcion", Boolean.TRUE);

    BDDMockito.given(comiteFormularioRepository.findById(1L))
        .willReturn(Optional.of(generarMockComiteFormulario(1L, comite, formulario)));

    ComiteFormulario comiteFormulario = comiteFormularioService.findById(1L);

    Assertions.assertThat(comiteFormulario.getId()).isEqualTo(1L);
    Assertions.assertThat(comiteFormulario.getComite()).isEqualTo(comite);
    Assertions.assertThat(comiteFormulario.getFormulario()).isEqualTo(formulario);

  }

  @Test
  public void find_NotFound_ThrowsComiteFormularioNotFoundException() throws Exception {
    BDDMockito.given(comiteFormularioRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> comiteFormularioService.findById(1L))
        .isInstanceOf(ComiteFormularioNotFoundException.class);
  }

  @Test
  public void create_ReturnsComiteFormulario() {

    Comite comite = new Comite(1L, "Comite1", Boolean.TRUE);
    Formulario formulario = new Formulario(1L, "M10", "Descripcion", Boolean.TRUE);

    // given: Un nuevo ComiteFormulario
    ComiteFormulario comiteFormularioNew = generarMockComiteFormulario(null, comite, formulario);

    ComiteFormulario comiteFormulario = generarMockComiteFormulario(1L, comite, formulario);

    BDDMockito.given(comiteFormularioRepository.save(comiteFormularioNew)).willReturn(comiteFormulario);

    // when: Creamos el ComiteFormulario
    ComiteFormulario comiteFormularioCreado = comiteFormularioService.create(comiteFormularioNew);

    // then: El ComiteFormulario se crea correctamente
    Assertions.assertThat(comiteFormularioCreado).isNotNull();
    Assertions.assertThat(comiteFormularioCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(comiteFormularioCreado.getComite()).isEqualTo(comite);
    Assertions.assertThat(comiteFormularioCreado.getFormulario()).isEqualTo(formulario);
  }

  @Test
  public void create_ComiteFormularioWithId_ThrowsIllegalArgumentException() {

    Comite comite = new Comite(1L, "Comite1", Boolean.TRUE);
    Formulario formulario = new Formulario(1L, "M10", "Descripcion", Boolean.TRUE);

    // given: Un nuevo ComiteFormulario que ya tiene id
    ComiteFormulario comiteFormularioNew = generarMockComiteFormulario(1L, comite, formulario);
    // when: Creamos el ComiteFormulario
    // then: Lanza una excepcion porque el ComiteFormulario ya tiene id
    Assertions.assertThatThrownBy(() -> comiteFormularioService.create(comiteFormularioNew))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsComiteFormulario() {

    Comite comite = new Comite(1L, "Comite1", Boolean.TRUE);
    Comite comite2 = new Comite(1L, "Comite2", Boolean.TRUE);
    Formulario formulario = new Formulario(1L, "M10", "Descripcion", Boolean.TRUE);
    Formulario formulario2 = new Formulario(1L, "M20", "Descripcion", Boolean.TRUE);

    // given: Un nuevo ComiteFormulario con el servicio actualizado
    ComiteFormulario comiteFormularioServicioActualizado = generarMockComiteFormulario(1L, comite2, formulario2);

    ComiteFormulario comiteFormulario = generarMockComiteFormulario(1L, comite, formulario);

    BDDMockito.given(comiteFormularioRepository.findById(1L)).willReturn(Optional.of(comiteFormulario));
    BDDMockito.given(comiteFormularioRepository.save(comiteFormulario)).willReturn(comiteFormularioServicioActualizado);

    // when: Actualizamos el ComiteFormulario
    ComiteFormulario comiteFormularioActualizado = comiteFormularioService.update(comiteFormulario);

    // then: El ComiteFormulario se actualiza correctamente.
    Assertions.assertThat(comiteFormularioActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(comiteFormularioActualizado.getComite()).isEqualTo(comite2);
    Assertions.assertThat(comiteFormularioActualizado.getFormulario()).isEqualTo(formulario2);

  }

  @Test
  public void update_ThrowsComiteFormularioNotFoundException() {
    Comite comite = new Comite(1L, "Comite1", Boolean.TRUE);
    Formulario formulario = new Formulario(1L, "M10", "Descripcion", Boolean.TRUE);

    // given: Un nuevo ComiteFormulario a actualizar
    ComiteFormulario comiteFormulario = generarMockComiteFormulario(1L, comite, formulario);

    // then: Lanza una excepcion porque el ComiteFormulario no existe
    Assertions.assertThatThrownBy(() -> comiteFormularioService.update(comiteFormulario))
        .isInstanceOf(ComiteFormularioNotFoundException.class);

  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {

    Comite comite = new Comite(1L, "Comite1", Boolean.TRUE);
    Formulario formulario = new Formulario(1L, "M10", "Descripcion", Boolean.TRUE);

    // given: Un ComiteFormulario que venga sin id
    ComiteFormulario ComiteFormulario = generarMockComiteFormulario(null, comite, formulario);

    Assertions.assertThatThrownBy(
        // when: update ComiteFormulario
        () -> comiteFormularioService.update(ComiteFormulario))
        // then: Lanza una excepción, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> comiteFormularioService.deleteById(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsComiteFormularioNotFoundException() {
    // given: Id no existe
    BDDMockito.given(comiteFormularioRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> comiteFormularioService.deleteById(1L))
        // then: Lanza ComiteFormularioNotFoundException
        .isInstanceOf(ComiteFormularioNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesComiteFormulario() {
    // given: Id existente
    BDDMockito.given(comiteFormularioRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(comiteFormularioRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> comiteFormularioService.deleteById(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void findAll_Unlimited_ReturnsFullComiteFormularioList() {

    Comite comite = new Comite(1L, "Comite1", Boolean.TRUE);
    Formulario formulario = new Formulario(1L, "M10", "Descripcion", Boolean.TRUE);

    // given: One hundred ComiteFormulario
    List<ComiteFormulario> comiteFormularios = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      comiteFormularios.add(generarMockComiteFormulario(Long.valueOf(i), comite, formulario));
    }

    BDDMockito.given(comiteFormularioRepository.findAll(ArgumentMatchers.<Specification<ComiteFormulario>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(comiteFormularios));

    // when: find unlimited
    Page<ComiteFormulario> page = comiteFormularioService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred ComiteFormularios
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {

    Comite comite = new Comite(1L, "Comite", Boolean.TRUE);
    Formulario formulario = new Formulario(1L, "M10", "Descripcion", Boolean.TRUE);

    // given: One hundred ComiteFormularios
    List<ComiteFormulario> ComiteFormularios = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      comite.setComite("Comite" + (String.format("%03d", i)));
      ComiteFormulario ComiteFormulario = new ComiteFormulario();
      ComiteFormulario.setId(Long.valueOf(i));
      ComiteFormulario.setComite(comite);
      ComiteFormulario.setFormulario(formulario);

      ComiteFormularios.add(ComiteFormulario);
    }

    BDDMockito.given(comiteFormularioRepository.findAll(ArgumentMatchers.<Specification<ComiteFormulario>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<ComiteFormulario>>() {
          @Override
          public Page<ComiteFormulario> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<ComiteFormulario> content = ComiteFormularios.subList(fromIndex, toIndex);
            Page<ComiteFormulario> page = new PageImpl<>(content, pageable, ComiteFormularios.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ComiteFormulario> page = comiteFormularioService.findAll(null, paging);

    // then: A Page with ten ComiteFormularios are returned containing
    // id='31' to '40'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      ComiteFormulario comiteFormulario = page.getContent().get(i);
      Assertions.assertThat(comiteFormulario.getId()).isEqualTo(j);
    }
  }

  @Test
  public void findComiteFormularioTipoM_IdValid() {
    // given: EL id del comité es valido
    Long comiteId = 1L;

    Comite comite = new Comite(comiteId, "Comite1", Boolean.TRUE);
    Formulario formulario = new Formulario(1L, "M10", "Descripcion", Boolean.TRUE);
    ComiteFormulario comiteFormulario = generarMockComiteFormulario(2L, comite, formulario);

    BDDMockito.given(comiteRepository.findByIdAndActivoTrue(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(comite));

    BDDMockito.given(comiteFormularioRepository
        .findByComiteIdAndComiteActivoTrueAndFormularioIdInAndFormularioActivoTrue(comiteId, Arrays.asList(1L, 2L, 3L)))
        .willReturn(Optional.of(comiteFormulario));

    Formulario formularioRecuperado = comiteFormularioService.findComiteFormularioTipoM(comiteId);

    // then: Se recuperan los datos correctamente
    Assertions.assertThat(formularioRecuperado.getId()).isEqualTo(1L);

  }

  @Test
  public void findComiteFormularioTipoM_ComiteIdNull() {
    // given: EL id del comité sea null
    Long comiteId = null;
    try {
      // when: se recupera el formulario a partir del comite id
      comiteFormularioService.findComiteFormularioTipoM(comiteId);
      Assertions.fail("Comité id no puede ser null para recuperar un ComiteFormulario asociado a este");
      // then: se debe lanzar una excepción
    } catch (IllegalArgumentException e) {
      Assertions.assertThat(e.getMessage())
          .isEqualTo("Comité id no puede ser null para recuperar un ComiteFormulario asociado a este");
    }
  }

  @Test
  public void findComiteFormularioTipo_ThrowsComiteFormularioNotFoundException() throws Exception {
    // given: EL id del comité es valido
    Long comiteId = 1L;

    Comite comite = new Comite(comiteId, "Comite1", Boolean.TRUE);

    BDDMockito.given(comiteRepository.findByIdAndActivoTrue(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(comite));

    BDDMockito.given(comiteFormularioRepository
        .findByComiteIdAndComiteActivoTrueAndFormularioIdInAndFormularioActivoTrue(comiteId, Arrays.asList(1L, 2L, 3L)))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> comiteFormularioService.findComiteFormularioTipoM(comiteId))
        .isInstanceOf(ComiteFormularioNotFoundException.class);
  }

  @Test
  public void findComiteFormularioTipo_ThrowsComiteNotFoundException() throws Exception {
    // given: EL id del comité es valido
    Long comiteId = 1L;

    BDDMockito.given(comiteRepository.findByIdAndActivoTrue(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> comiteFormularioService.findComiteFormularioTipoM(comiteId))
        .isInstanceOf(ComiteNotFoundException.class);
  }

  /**
   * Función que devuelve un objeto ComiteFormulario
   * 
   * @param id         id del ComiteFormulario
   * @param comite     el Comite de ComiteFormulario
   * @param formulario el Formulario de ComiteFormulario
   * @return el objeto ComiteFormulario
   */

  private ComiteFormulario generarMockComiteFormulario(Long id, Comite comite, Formulario formulario) {

    ComiteFormulario comiteFormulario = new ComiteFormulario();
    comiteFormulario.setId(id);
    comiteFormulario.setComite(comite);
    comiteFormulario.setFormulario(formulario);

    return comiteFormulario;
  }
}