package org.crue.hercules.sgi.eti.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.ComponenteFormularioNotFoundException;
import org.crue.hercules.sgi.eti.model.ComponenteFormulario;
import org.crue.hercules.sgi.eti.repository.ComponenteFormularioRepository;
import org.crue.hercules.sgi.eti.service.impl.ComponenteFormularioServiceImpl;
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
 * ComponenteFormularioServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class ComponenteFormularioServiceTest {

  @Mock
  private ComponenteFormularioRepository repository;

  private ComponenteFormularioService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ComponenteFormularioServiceImpl(repository);
  }

  @Test
  public void create_ReturnsComponenteFormulario() {

    // given: Nueva entidad sin Id
    ComponenteFormulario response = getMockData(1L);
    response.setId(null);

    BDDMockito.given(repository.save(response)).willReturn(response);

    // when: Se crea la entidad
    ComponenteFormulario result = service.create(response);

    // then: La entidad se crea correctamente
    Assertions.assertThat(result).isEqualTo(response);
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {

    // given: Nueva entidad con Id
    ComponenteFormulario response = getMockData(1L);
    // when: Se crea la entidad
    // then: Se produce error porque ya tiene Id
    Assertions.assertThatThrownBy(() -> service.create(response)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_WithExistingId_ReturnsComponenteFormulario() {

    // given: Entidad existente que se va a actualizar
    ComponenteFormulario componenteFormulario = getMockData(1L);
    ComponenteFormulario componenteFormularioActualizada = getMockData(2L);
    componenteFormularioActualizada.setId(componenteFormulario.getId());

    BDDMockito.given(repository.findById(componenteFormulario.getId())).willReturn(Optional.of(componenteFormulario));
    BDDMockito.given(repository.save(componenteFormularioActualizada)).willReturn(componenteFormularioActualizada);

    // when: Se actualiza la entidad
    ComponenteFormulario result = service.update(componenteFormularioActualizada);

    // then: Los datos se actualizan correctamente
    Assertions.assertThat(result).isEqualTo(componenteFormularioActualizada);
  }

  @Test
  public void update_WithNoExistingId_ThrowsNotFoundException() {

    // given: Entidad a actualizar que no existe
    ComponenteFormulario componenteFormularioActualizada = getMockData(1L);

    // when: Se actualiza la entidad
    // then: Se produce error porque no encuentra la entidad a actualizar
    Assertions.assertThatThrownBy(() -> service.update(componenteFormularioActualizada))
        .isInstanceOf(ComponenteFormularioNotFoundException.class);
  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {

    // given: Entidad a actualizar sin Id
    ComponenteFormulario componenteFormularioActualizada = getMockData(1L);
    componenteFormularioActualizada.setId(null);

    // when: Se actualiza la entidad
    // then: Se produce error porque no tiene Id
    Assertions.assertThatThrownBy(() -> service.update(componenteFormularioActualizada))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesComponenteFormulario() {

    // given: Entidad existente
    ComponenteFormulario response = getMockData(1L);

    BDDMockito.given(repository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(repository).deleteById(response.getId());

    // when: Se elimina la entidad
    service.delete(response.getId());

    // then: La entidad se elimina correctamente
    BDDMockito.given(repository.findById(response.getId())).willReturn(Optional.empty());
    Assertions.assertThatThrownBy(() -> service.findById(response.getId()))
        .isInstanceOf(ComponenteFormularioNotFoundException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsNotFoundException() {

    // given: Id de una entidad que no existe
    Long id = 1L;

    // when: Se elimina la entidad
    // then: Se produce error porque no encuentra la entidad a actualizar
    Assertions.assertThatThrownBy(() -> service.delete(id)).isInstanceOf(ComponenteFormularioNotFoundException.class);
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
  public void find_WithExistingId_ReturnsComponenteFormulario() {

    // given: Entidad con un determinado Id
    ComponenteFormulario response = getMockData(1L);
    BDDMockito.given(repository.findById(response.getId())).willReturn(Optional.of(response));

    // when: Se busca la entidad por ese Id
    ComponenteFormulario result = service.findById(response.getId());

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
    Assertions.assertThatThrownBy(() -> service.findById(id)).isInstanceOf(ComponenteFormularioNotFoundException.class);
  }

  @Test
  public void findAll_Unlimited_ReturnsFullComponenteFormularioList() {

    // given: Datos existentes
    List<ComponenteFormulario> response = new LinkedList<ComponenteFormulario>();
    response.add(getMockData(1L));
    response.add(getMockData(2L));

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ComponenteFormulario>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(response));

    // when: Se buscan todos las datos
    Page<ComponenteFormulario> result = service.findAll(null, Pageable.unpaged());

    // then: Se recuperan todos los datos
    Assertions.assertThat(result.getContent()).isEqualTo(response);
    Assertions.assertThat(result.getNumber()).isEqualTo(0);
    Assertions.assertThat(result.getSize()).isEqualTo(response.size());
    Assertions.assertThat(result.getTotalElements()).isEqualTo(response.size());
  }

  @Test
  public void findAll_Unlimited_ReturnEmptyPage() {

    // given: No hay datos
    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ComponenteFormulario>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(new LinkedList<ComponenteFormulario>()));

    // when: Se buscan todos las datos
    Page<ComponenteFormulario> result = service.findAll(null, Pageable.unpaged());

    // then: Se recupera lista vacía
    Assertions.assertThat(result.isEmpty());
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {

    // given: Datos existentes
    List<ComponenteFormulario> response = new LinkedList<ComponenteFormulario>();
    response.add(getMockData(1L));
    response.add(getMockData(2L));
    response.add(getMockData(3L));

    // página 1 con 2 elementos por página
    Pageable pageable = PageRequest.of(1, 2);
    Page<ComponenteFormulario> pageResponse = new PageImpl<>(response.subList(2, 3), pageable, response.size());

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ComponenteFormulario>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(pageResponse);

    // when: Se buscan los datos paginados
    Page<ComponenteFormulario> result = service.findAll(null, pageable);

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
    List<ComponenteFormulario> response = new LinkedList<ComponenteFormulario>();
    Pageable pageable = PageRequest.of(1, 2);
    Page<ComponenteFormulario> pageResponse = new PageImpl<>(response, pageable, response.size());

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ComponenteFormulario>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(pageResponse);

    // when: Se buscan los datos paginados
    Page<ComponenteFormulario> result = service.findAll(null, pageable);

    // then: Se recupera lista de datos paginados vacía
    Assertions.assertThat(result).isEmpty();
  }

  /**
   * Genera un objeto {@link ComponenteFormulario}
   * 
   * @param id
   * @return ComponenteFormulario
   */
  private ComponenteFormulario getMockData(Long id) {

    String txt = (id % 2 == 0) ? String.valueOf(id) : "0" + String.valueOf(id);

    final ComponenteFormulario data = new ComponenteFormulario();
    data.setId(id);
    data.setEsquema("EsquemaComponenteFormulario" + txt);

    return data;
  }

}
