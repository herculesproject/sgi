package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.TipoEnlaceNotFoundException;
import org.crue.hercules.sgi.csp.model.TipoEnlace;
import org.crue.hercules.sgi.csp.repository.TipoEnlaceRepository;
import org.crue.hercules.sgi.csp.service.impl.TipoEnlaceServiceImpl;
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
public class TipoEnlaceServiceTest extends BaseServiceTest {

  @Mock
  private TipoEnlaceRepository repository;

  private TipoEnlaceService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new TipoEnlaceServiceImpl(repository);
  }

  @Test
  public void create_ReturnsTipoEnlace() {
    // given: new TipoEnlace
    TipoEnlace data = generarMockTipoEnlace(null, Boolean.TRUE);

    BDDMockito.given(repository.save(ArgumentMatchers.<TipoEnlace>any())).willAnswer(new Answer<TipoEnlace>() {
      @Override
      public TipoEnlace answer(InvocationOnMock invocation) throws Throwable {
        TipoEnlace givenData = invocation.getArgument(0, TipoEnlace.class);
        TipoEnlace newData = new TipoEnlace();
        BeanUtils.copyProperties(givenData, newData);
        newData.setId(1L);
        return newData;
      }
    });

    // when: create TipoEnlace
    TipoEnlace created = service.create(data);

    // then: new TipoEnlace is created
    Assertions.assertThat(created).isNotNull();
    Assertions.assertThat(created.getId()).isNotNull();
    Assertions.assertThat(created.getNombre()).isEqualTo(data.getNombre());
    Assertions.assertThat(created.getDescripcion()).isEqualTo(data.getDescripcion());
    Assertions.assertThat(created.getActivo()).isEqualTo(data.getActivo());
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: a TipoEnlace with id filled
    TipoEnlace data = generarMockTipoEnlace(1L, Boolean.TRUE);

    Assertions.assertThatThrownBy(
        // when: create TipoEnlace
        () -> service.create(data))
        // then: throw exception as id can't be provided
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void create_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: a TipoEnlace with duplicated nombre

    TipoEnlace givenData = generarMockTipoEnlace(1L, Boolean.TRUE);
    TipoEnlace newData = new TipoEnlace();
    BeanUtils.copyProperties(givenData, newData);
    newData.setId(null);

    BDDMockito.given(repository.findByNombre(ArgumentMatchers.anyString())).willReturn(Optional.of(givenData));

    Assertions.assertThatThrownBy(
        // when: create TipoEnlace
        () -> service.create(newData))
        // then: throw exception as Nombre already exists
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_WithExistingId_ReturnsTipoEnlace() {
    // given: existing TipoEnlace
    TipoEnlace data = generarMockTipoEnlace(1L, Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(data));

    BDDMockito.given(repository.save(ArgumentMatchers.<TipoEnlace>any())).willAnswer(new Answer<TipoEnlace>() {
      @Override
      public TipoEnlace answer(InvocationOnMock invocation) throws Throwable {
        TipoEnlace givenData = invocation.getArgument(0, TipoEnlace.class);
        givenData.setNombre("Nombre-Modificado");
        return givenData;
      }
    });

    // when: update TipoEnlace
    TipoEnlace updated = service.update(data);

    // then: TipoEnlace is updated
    Assertions.assertThat(updated).isNotNull();
    Assertions.assertThat(updated.getId()).isNotNull();
    Assertions.assertThat(updated.getId()).isEqualTo(data.getId());
    Assertions.assertThat(updated.getNombre()).isEqualTo("Nombre-Modificado");
    Assertions.assertThat(updated.getDescripcion()).isEqualTo(data.getDescripcion());
    Assertions.assertThat(updated.getActivo()).isEqualTo(data.getActivo());
  }

  @Test
  public void update_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    TipoEnlace data = generarMockTipoEnlace(1L, Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update non existing TipoEnlace
        () -> service.update(data))
        // then: NotFoundException is thrown
        .isInstanceOf(TipoEnlaceNotFoundException.class);
  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {
    // given: a TipoEnlace without id filled
    TipoEnlace data = generarMockTipoEnlace(null, Boolean.TRUE);

    Assertions.assertThatThrownBy(
        // when: update TipoEnlace
        () -> service.update(data))
        // then: throw exception as id must be provided
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: a TipoEnlace with duplicated nombre
    TipoEnlace givenData = generarMockTipoEnlace(1L, Boolean.TRUE);
    TipoEnlace data = new TipoEnlace();
    BeanUtils.copyProperties(givenData, data);
    data.setId(2L);

    BDDMockito.given(repository.findByNombre(ArgumentMatchers.anyString())).willReturn(Optional.of(givenData));

    Assertions.assertThatThrownBy(
        // when: update TipoEnlace
        () -> service.update(data))
        // then: throw exception as Nombre already exists
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void disable_WithExistingId_ReturnsTipoEnlace() {
    // given: existing TipoEnlace
    TipoEnlace data = generarMockTipoEnlace(1L, Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(data));

    BDDMockito.given(repository.save(ArgumentMatchers.<TipoEnlace>any())).willAnswer(new Answer<TipoEnlace>() {
      @Override
      public TipoEnlace answer(InvocationOnMock invocation) throws Throwable {
        TipoEnlace givenData = invocation.getArgument(0, TipoEnlace.class);
        givenData.setActivo(Boolean.FALSE);
        return givenData;
      }
    });

    // when: disable TipoEnlace
    TipoEnlace disabledData = service.disable(data.getId());

    // then: TipoEnlace is disabled
    Assertions.assertThat(disabledData).isNotNull();
    Assertions.assertThat(disabledData.getId()).isNotNull();
    Assertions.assertThat(disabledData.getId()).isEqualTo(data.getId());
    Assertions.assertThat(disabledData.getNombre()).isEqualTo(data.getNombre());
    Assertions.assertThat(disabledData.getDescripcion()).isEqualTo(data.getDescripcion());
    Assertions.assertThat(disabledData.getActivo()).isEqualTo(Boolean.FALSE);
  }

  @Test
  public void disable_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    TipoEnlace data = generarMockTipoEnlace(1L, Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update non existing TipoEnlace
        () -> service.disable(data.getId()))
        // then: NotFoundException is thrown
        .isInstanceOf(TipoEnlaceNotFoundException.class);
  }

  @Test
  public void findById_WithExistingId_ReturnsTipoEnlace() throws Exception {
    // given: existing TipoEnlace
    TipoEnlace givenData = generarMockTipoEnlace(1L, Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(givenData));

    // when: find by id TipoEnlace
    TipoEnlace data = service.findById(givenData.getId());

    // then: returns TipoEnlace
    Assertions.assertThat(data).isNotNull();
    Assertions.assertThat(data.getId()).isNotNull();
    Assertions.assertThat(data.getId()).isEqualTo(data.getId());
    Assertions.assertThat(data.getNombre()).isEqualTo(data.getNombre());
    Assertions.assertThat(data.getDescripcion()).isEqualTo(data.getDescripcion());
    Assertions.assertThat(data.getActivo()).isEqualTo(data.getActivo());
  }

  @Test
  public void findById_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: find by non existing id
        () -> service.findById(1L))
        // then: NotFoundException is thrown
        .isInstanceOf(TipoEnlaceNotFoundException.class);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred TipoEnlace
    List<TipoEnlace> data = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      data.add(generarMockTipoEnlace(Long.valueOf(i), Boolean.TRUE));
    }

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<TipoEnlace>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoEnlace>>() {
          @Override
          public Page<TipoEnlace> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoEnlace> content = data.subList(fromIndex, toIndex);
            Page<TipoEnlace> page = new PageImpl<>(content, pageable, data.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<TipoEnlace> page = service.findAll(null, paging);

    // then: A Page with ten TipoEnlace are returned containing
    // Nombre='nombre-31' to
    // 'nombre-40'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoEnlace item = page.getContent().get(i);
      Assertions.assertThat(item.getNombre()).isEqualTo("nombre-" + j);
    }
  }

  @Test
  public void findAllTodos_WithPaging_ReturnsPage() {
    // given: One hundred TipoEnlace
    List<TipoEnlace> data = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      data.add(generarMockTipoEnlace(Long.valueOf(i), Boolean.TRUE));
    }

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<TipoEnlace>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoEnlace>>() {
          @Override
          public Page<TipoEnlace> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoEnlace> content = data.subList(fromIndex, toIndex);
            Page<TipoEnlace> page = new PageImpl<>(content, pageable, data.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<TipoEnlace> page = service.findAllTodos(null, paging);

    // then: A Page with ten TipoEnlace are returned containing
    // Nombre='nombre-31' to
    // 'nombre-40'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoEnlace item = page.getContent().get(i);
      Assertions.assertThat(item.getNombre()).isEqualTo("nombre-" + j);
    }
  }

  /**
   * Función que devuelve un objeto TipoEnlace
   * 
   * @param id
   * @param activo
   * @return TipoEnlace
   */
  private TipoEnlace generarMockTipoEnlace(Long id, Boolean activo) {
    return TipoEnlace.builder().id(id).nombre("nombre-" + id).descripcion("descripcion-" + id).activo(activo).build();
  }

}
