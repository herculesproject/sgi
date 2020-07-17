package org.crue.hercules.sgi.eti.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.ApartadoFormularioNotFoundException;
import org.crue.hercules.sgi.eti.model.ApartadoFormulario;
import org.crue.hercules.sgi.eti.model.BloqueFormulario;
import org.crue.hercules.sgi.eti.model.ComponenteFormulario;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.repository.ApartadoFormularioRepository;
import org.crue.hercules.sgi.eti.service.impl.ApartadoFormularioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * ApartadoFormularioServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class ApartadoFormularioServiceTest {

  @Mock
  private ApartadoFormularioRepository repository;

  private ApartadoFormularioService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ApartadoFormularioServiceImpl(repository);
  }

  @Test
  public void create_ReturnsApartadoFormulario() {

    // given: Nueva entidad sin Id
    ApartadoFormulario response = getMockData(1L, 1L, 1L, null);
    response.setId(null);

    BDDMockito.given(repository.save(response)).willReturn(response);

    // when: Se crea la entidad
    ApartadoFormulario result = service.create(response);

    // then: La entidad se crea correctamente
    Assertions.assertThat(result).isEqualTo(response);
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {

    // given: Nueva entidad con Id
    ApartadoFormulario response = getMockData(1L, 1L, 1L, null);
    // when: Se crea la entidad
    // then: Se produce error porque ya tiene Id
    Assertions.assertThatThrownBy(() -> service.create(response)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_WithExistingId_ReturnsApartadoFormulario() {

    // given: Entidad existente que se va a actualizar
    ApartadoFormulario apartadoFormulario = getMockData(1L, 1L, 1L, null);
    ApartadoFormulario apartadoFormularioActualizada = getMockData(2L, 1L, 1L, 1L);
    apartadoFormularioActualizada.setId(apartadoFormulario.getId());

    BDDMockito.given(repository.findById(apartadoFormulario.getId())).willReturn(Optional.of(apartadoFormulario));
    BDDMockito.given(repository.save(apartadoFormularioActualizada)).willReturn(apartadoFormularioActualizada);

    // when: Se actualiza la entidad
    ApartadoFormulario result = service.update(apartadoFormularioActualizada);

    // then: Los datos se actualizan correctamente
    Assertions.assertThat(result).isEqualTo(apartadoFormularioActualizada);
  }

  @Test
  public void update_WithNoExistingId_ThrowsNotFoundException() {

    // given: Entidad a actualizar que no existe
    ApartadoFormulario apartadoFormularioActualizada = getMockData(1L, 1L, 1L, null);

    // when: Se actualiza la entidad
    // then: Se produce error porque no encuentra la entidad a actualizar
    Assertions.assertThatThrownBy(() -> service.update(apartadoFormularioActualizada))
        .isInstanceOf(ApartadoFormularioNotFoundException.class);
  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {

    // given: Entidad a actualizar sin Id
    ApartadoFormulario apartadoFormularioActualizada = getMockData(1L, 1L, 1L, null);
    apartadoFormularioActualizada.setId(null);

    // when: Se actualiza la entidad
    // then: Se produce error porque no tiene Id
    Assertions.assertThatThrownBy(() -> service.update(apartadoFormularioActualizada))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesApartadoFormulario() {

    // given: Entidad existente
    ApartadoFormulario response = getMockData(1L, 1L, 1L, null);

    BDDMockito.given(repository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(repository).deleteById(response.getId());

    // when: Se elimina la entidad
    service.delete(response.getId());

    // then: La entidad se elimina correctamente
    BDDMockito.given(repository.findById(response.getId())).willReturn(Optional.empty());
    Assertions.assertThatThrownBy(() -> service.findById(response.getId()))
        .isInstanceOf(ApartadoFormularioNotFoundException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsNotFoundException() {

    // given: Id de una entidad que no existe
    Long id = 1L;

    // when: Se elimina la entidad
    // then: Se produce error porque no encuentra la entidad a actualizar
    Assertions.assertThatThrownBy(() -> service.delete(id)).isInstanceOf(ApartadoFormularioNotFoundException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {

    // given: Id de una entidad vacía
    Long id = null;

    // when: Se elimina la entidad
    // then: Se produce error porque no encuentra la entidad a actualizar
    Assertions.assertThatThrownBy(() -> service.delete(id)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void find_WithExistingId_ReturnsApartadoFormulario() {

    // given: Entidad con un determinado Id
    ApartadoFormulario response = getMockData(1L, 1L, 1L, null);
    BDDMockito.given(repository.findById(response.getId())).willReturn(Optional.of(response));

    // when: Se busca la entidad por ese Id
    ApartadoFormulario result = service.findById(response.getId());

    // then: Se recupera la entidad con el Id
    Assertions.assertThat(result).isEqualTo(response);
  }

  @Test
  public void find_WithNoExistingId_ThrowsNotFoundException() throws Exception {

    // given: No existe entidad con el id indicado
    Long id = 1L;
    BDDMockito.given(repository.findById(id)).willReturn(Optional.empty());

    // when: Se busca entidad con ese id
    // then: Se produce error porque no encuentra la entidad con ese Id
    Assertions.assertThatThrownBy(() -> service.findById(id)).isInstanceOf(ApartadoFormularioNotFoundException.class);
  }

  @Test
  public void findAll_Unlimited_ReturnsFullApartadoFormularioList() {

    // given: Datos existentes
    List<ApartadoFormulario> response = new LinkedList<ApartadoFormulario>();
    response.add(getMockData(1L, 1L, 1L, null));
    response.add(getMockData(2L, 1L, 1L, 1L));

    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<ApartadoFormulario>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(response));

    // when: Se buscan todos las datos
    Page<ApartadoFormulario> result = service.findAll(null, Pageable.unpaged());

    // then: Se recuperan todos los datos
    Assertions.assertThat(result.getContent()).isEqualTo(response);
    Assertions.assertThat(result.getNumber()).isEqualTo(0);
    Assertions.assertThat(result.getSize()).isEqualTo(response.size());
    Assertions.assertThat(result.getTotalElements()).isEqualTo(response.size());
  }

  @Test
  public void findAll_Unlimited_ReturnEmptyPage() {

    // given: No hay datos
    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<ApartadoFormulario>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(new LinkedList<ApartadoFormulario>()));

    // when: Se buscan todos las datos
    Page<ApartadoFormulario> result = service.findAll(null, Pageable.unpaged());

    // then: Se recupera lista vacía
    Assertions.assertThat(result.isEmpty());
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {

    // given: Datos existentes
    List<ApartadoFormulario> response = new LinkedList<ApartadoFormulario>();
    response.add(getMockData(1L, 1L, 1L, null));
    response.add(getMockData(2L, 1L, 1L, 1L));
    response.add(getMockData(3L, 1L, 1L, 1L));

    // página 1 con 2 elementos por página
    Pageable pageable = PageRequest.of(1, 2);
    Page<ApartadoFormulario> pageResponse = new PageImpl<>(response.subList(2, 3), pageable, response.size());

    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<ApartadoFormulario>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(pageResponse);

    // when: Se buscan los datos paginados
    Page<ApartadoFormulario> result = service.findAll(null, pageable);

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(result).isEqualTo(pageResponse);
    Assertions.assertThat(result.getContent()).isEqualTo(response.subList(2, 3));
    Assertions.assertThat(result.getNumber()).isEqualTo(pageable.getPageNumber());
    Assertions.assertThat(result.getSize()).isEqualTo(pageable.getPageSize());
    Assertions.assertThat(result.getTotalElements()).isEqualTo(response.size());
  }

  @Test
  public void findAll_WithPaging_ReturnEmptyPage() {

    // given: No hay datos
    List<ApartadoFormulario> response = new LinkedList<ApartadoFormulario>();
    Pageable pageable = PageRequest.of(1, 2);
    Page<ApartadoFormulario> pageResponse = new PageImpl<>(response, pageable, response.size());

    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<ApartadoFormulario>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(pageResponse);

    // when: Se buscan los datos paginados
    Page<ApartadoFormulario> result = service.findAll(null, pageable);

    // then: Se recupera lista de datos paginados vacía
    Assertions.assertThat(result).isEmpty();
  }

  /**
   * Genera un objeto {@link ApartadoFormulario}
   * 
   * @param id
   * @param bloqueFormularioId
   * @param componenteFormularioId
   * @param apartadoFormularioPadreId
   * @return ApartadoFormulario
   */
  private ApartadoFormulario getMockData(Long id, Long bloqueFormularioId, Long componenteFormularioId,
      Long apartadoFormularioPadreId) {

    Formulario formulario = new Formulario(1L, "M10", "Descripcion1", Boolean.TRUE);
    BloqueFormulario bloqueFormulario = new BloqueFormulario(bloqueFormularioId, formulario,
        "Bloque Formulario " + bloqueFormularioId, bloqueFormularioId.intValue(), Boolean.TRUE);
    ComponenteFormulario componenteFormulario = new ComponenteFormulario(componenteFormularioId,
        "EsquemaComponenteFormulario" + componenteFormularioId);

    ApartadoFormulario apartadoFormularioPadre = (apartadoFormularioPadreId != null)
        ? getMockData(apartadoFormularioPadreId, bloqueFormularioId, componenteFormularioId, null)
        : null;

    String txt = (id % 2 == 0) ? String.valueOf(id) : "0" + String.valueOf(id);

    final ApartadoFormulario data = new ApartadoFormulario();
    data.setId(id);
    data.setBloqueFormulario(bloqueFormulario);
    data.setNombre("ApartadoFormulario" + txt);
    data.setApartadoFormularioPadre(apartadoFormularioPadre);
    data.setOrden(id.intValue());
    data.setComponenteFormulario(componenteFormulario);
    data.setActivo(Boolean.TRUE);

    return data;
  }

}
