package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.ListadoAreaTematicaNotFoundException;
import org.crue.hercules.sgi.csp.model.ListadoAreaTematica;
import org.crue.hercules.sgi.csp.repository.ListadoAreaTematicaRepository;
import org.crue.hercules.sgi.csp.service.impl.ListadoAreaTematicaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class ListadoAreaTematicaServiceTest {

  @Mock
  private ListadoAreaTematicaRepository repository;

  private ListadoAreaTematicaService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new ListadoAreaTematicaServiceImpl(repository);
  }

  @Test
  public void create_ReturnsListadoAreaTematica() {
    // given: new ListadoAreaTematica
    ListadoAreaTematica listadoAreaTematica = generarMockListadoAreaTematica(null, Boolean.TRUE);

    BDDMockito.given(repository.save(ArgumentMatchers.<ListadoAreaTematica>any()))
        .willAnswer(new Answer<ListadoAreaTematica>() {
          @Override
          public ListadoAreaTematica answer(InvocationOnMock invocation) throws Throwable {
            ListadoAreaTematica givenData = invocation.getArgument(0, ListadoAreaTematica.class);
            ListadoAreaTematica newData = new ListadoAreaTematica();
            BeanUtils.copyProperties(givenData, newData);
            newData.setId(1L);
            return newData;
          }
        });

    // when: create ListadoAreaTematica
    ListadoAreaTematica created = service.create(listadoAreaTematica);

    // then: new ListadoAreaTematica is created
    Assertions.assertThat(created).isNotNull();
    Assertions.assertThat(created.getId()).isNotNull();
    Assertions.assertThat(created.getNombre()).isEqualTo(listadoAreaTematica.getNombre());
    Assertions.assertThat(created.getDescripcion()).isEqualTo(listadoAreaTematica.getDescripcion());
    Assertions.assertThat(created.getActivo()).isEqualTo(listadoAreaTematica.getActivo());
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: a ListadoAreaTematica with id filled
    ListadoAreaTematica listadoAreaTematica = generarMockListadoAreaTematica(1L, Boolean.TRUE);

    Assertions.assertThatThrownBy(
        // when: create ListadoAreaTematica
        () -> service.create(listadoAreaTematica))
        // then: throw exception as id can't be provided
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void create_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: a ListadoAreaTematica with duplicated nombre

    ListadoAreaTematica givenListadoAreaTematica = generarMockListadoAreaTematica(1L, Boolean.TRUE);
    ListadoAreaTematica newListadoAreaTematica = new ListadoAreaTematica();
    BeanUtils.copyProperties(givenListadoAreaTematica, newListadoAreaTematica);
    newListadoAreaTematica.setId(null);

    BDDMockito.given(repository.findByNombre(ArgumentMatchers.anyString()))
        .willReturn(Optional.of(givenListadoAreaTematica));

    Assertions.assertThatThrownBy(
        // when: create ListadoAreaTematica
        () -> service.create(newListadoAreaTematica))
        // then: throw exception as Nombre already exists
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_WithExistingId_ReturnsListadoAreaTematica() {
    // given: existing ListadoAreaTematica
    ListadoAreaTematica listadoAreaTematica = generarMockListadoAreaTematica(1L, Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(listadoAreaTematica));

    BDDMockito.given(repository.save(ArgumentMatchers.<ListadoAreaTematica>any()))
        .willAnswer(new Answer<ListadoAreaTematica>() {
          @Override
          public ListadoAreaTematica answer(InvocationOnMock invocation) throws Throwable {
            ListadoAreaTematica givenData = invocation.getArgument(0, ListadoAreaTematica.class);
            givenData.setNombre("Nombre-Modificado");
            return givenData;
          }
        });

    // when: update ListadoAreaTematica
    ListadoAreaTematica updatedListadoAreaTematica = service.update(listadoAreaTematica);

    // then: ListadoAreaTematica is updated
    Assertions.assertThat(updatedListadoAreaTematica).isNotNull();
    Assertions.assertThat(updatedListadoAreaTematica.getId()).isNotNull();
    Assertions.assertThat(updatedListadoAreaTematica.getId()).isEqualTo(listadoAreaTematica.getId());
    Assertions.assertThat(updatedListadoAreaTematica.getNombre()).isEqualTo("Nombre-Modificado");
    Assertions.assertThat(updatedListadoAreaTematica.getDescripcion()).isEqualTo(listadoAreaTematica.getDescripcion());
    Assertions.assertThat(updatedListadoAreaTematica.getActivo()).isEqualTo(listadoAreaTematica.getActivo());
  }

  @Test
  public void update_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    ListadoAreaTematica listadoAreaTematica = generarMockListadoAreaTematica(1L, Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update non existing ListadoAreaTematica
        () -> service.update(listadoAreaTematica))
        // then: NotFoundException is thrown
        .isInstanceOf(ListadoAreaTematicaNotFoundException.class);
  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {
    // given: a ListadoAreaTematica without id filled
    ListadoAreaTematica listadoAreaTematica = generarMockListadoAreaTematica(null, Boolean.TRUE);

    Assertions.assertThatThrownBy(
        // when: update ListadoAreaTematica
        () -> service.update(listadoAreaTematica))
        // then: throw exception as id must be provided
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: a ListadoAreaTematica with duplicated nombre
    ListadoAreaTematica givenListadoAreaTematica = generarMockListadoAreaTematica(1L, Boolean.TRUE);
    ListadoAreaTematica listadoAreaTematica = new ListadoAreaTematica();
    BeanUtils.copyProperties(givenListadoAreaTematica, listadoAreaTematica);
    listadoAreaTematica.setId(2L);

    BDDMockito.given(repository.findByNombre(ArgumentMatchers.anyString()))
        .willReturn(Optional.of(givenListadoAreaTematica));

    Assertions.assertThatThrownBy(
        // when: update ListadoAreaTematica
        () -> service.update(listadoAreaTematica))
        // then: throw exception as Nombre already exists
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void disable_WithExistingId_ReturnsListadoAreaTematica() {
    // given: existing ListadoAreaTematica
    ListadoAreaTematica listadoAreaTematica = generarMockListadoAreaTematica(1L, Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(listadoAreaTematica));

    BDDMockito.given(repository.save(ArgumentMatchers.<ListadoAreaTematica>any()))
        .willAnswer(new Answer<ListadoAreaTematica>() {
          @Override
          public ListadoAreaTematica answer(InvocationOnMock invocation) throws Throwable {
            ListadoAreaTematica givenData = invocation.getArgument(0, ListadoAreaTematica.class);
            givenData.setActivo(Boolean.FALSE);
            return givenData;
          }
        });

    // when: disable ListadoAreaTematica
    ListadoAreaTematica disabledListadoAreaTematica = service.disable(listadoAreaTematica.getId());

    // then: ListadoAreaTematica is disabled
    Assertions.assertThat(disabledListadoAreaTematica).isNotNull();
    Assertions.assertThat(disabledListadoAreaTematica.getId()).isNotNull();
    Assertions.assertThat(disabledListadoAreaTematica.getId()).isEqualTo(listadoAreaTematica.getId());
    Assertions.assertThat(disabledListadoAreaTematica.getNombre()).isEqualTo(listadoAreaTematica.getNombre());
    Assertions.assertThat(disabledListadoAreaTematica.getDescripcion()).isEqualTo(listadoAreaTematica.getDescripcion());
    Assertions.assertThat(disabledListadoAreaTematica.getActivo()).isEqualTo(Boolean.FALSE);
  }

  @Test
  public void disable_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    ListadoAreaTematica listadoAreaTematica = generarMockListadoAreaTematica(1L, Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update non existing ListadoAreaTematica
        () -> service.disable(listadoAreaTematica.getId()))
        // then: NotFoundException is thrown
        .isInstanceOf(ListadoAreaTematicaNotFoundException.class);
  }

  @Test
  public void findById_WithExistingId_ReturnsListadoAreaTematica() throws Exception {
    // given: existing ListadoAreaTematica
    ListadoAreaTematica givenListadoAreaTematica = generarMockListadoAreaTematica(1L, Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(givenListadoAreaTematica));

    // when: find by id ListadoAreaTematica
    ListadoAreaTematica listadoAreaTematicaFound = service.findById(givenListadoAreaTematica.getId());

    // then: returns ListadoAreaTematica
    Assertions.assertThat(listadoAreaTematicaFound).isNotNull();
    Assertions.assertThat(listadoAreaTematicaFound.getId()).isNotNull();
    Assertions.assertThat(listadoAreaTematicaFound.getId()).isEqualTo(listadoAreaTematicaFound.getId());
    Assertions.assertThat(listadoAreaTematicaFound.getNombre()).isEqualTo(listadoAreaTematicaFound.getNombre());
    Assertions.assertThat(listadoAreaTematicaFound.getDescripcion())
        .isEqualTo(listadoAreaTematicaFound.getDescripcion());
    Assertions.assertThat(listadoAreaTematicaFound.getActivo()).isEqualTo(listadoAreaTematicaFound.getActivo());
  }

  @Test
  public void findById_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: find by non existing id
        () -> service.findById(1L))
        // then: NotFoundException is thrown
        .isInstanceOf(ListadoAreaTematicaNotFoundException.class);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred ListadoAreaTematica
    List<ListadoAreaTematica> listadosAreasTematicas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      listadosAreasTematicas.add(generarMockListadoAreaTematica(Long.valueOf(i), Boolean.TRUE));
    }

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ListadoAreaTematica>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<ListadoAreaTematica>>() {
          @Override
          public Page<ListadoAreaTematica> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<ListadoAreaTematica> content = listadosAreasTematicas.subList(fromIndex, toIndex);
            Page<ListadoAreaTematica> page = new PageImpl<>(content, pageable, listadosAreasTematicas.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ListadoAreaTematica> page = service.findAll(null, paging);

    // then: A Page with ten ListadoAreaTematica are returned containing
    // Nombre='nombre-31' to
    // 'nombre-40'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      ListadoAreaTematica item = page.getContent().get(i);
      Assertions.assertThat(item.getNombre()).isEqualTo("nombre-" + j);
    }
  }

  /**
   * Función que devuelve un objeto ListadoAreaTematica
   * 
   * @param id
   * @param activo
   * @return ListadoAreaTematica
   */
  private ListadoAreaTematica generarMockListadoAreaTematica(Long id, Boolean activo) {
    return ListadoAreaTematica.builder().id(id).nombre("nombre-" + id).descripcion("descripcion-" + id).activo(activo)
        .build();
  }

}
