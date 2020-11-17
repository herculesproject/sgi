package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.TipoRegimenConcurrenciaNotFoundException;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.crue.hercules.sgi.csp.repository.TipoRegimenConcurrenciaRepository;
import org.crue.hercules.sgi.csp.service.impl.TipoRegimenConcurrenciaServiceImpl;
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
public class TipoRegimenConcurrenciaServiceTest extends BaseServiceTest {

  @Mock
  private TipoRegimenConcurrenciaRepository repository;

  private TipoRegimenConcurrenciaService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new TipoRegimenConcurrenciaServiceImpl(repository);
  }

  @Test
  public void create_ReturnsTipoRegimenConcurrencia() {
    // given: new TipoRegimenConcurrencia
    TipoRegimenConcurrencia data = generarMockTipoRegimenConcurrencia(null, Boolean.TRUE);

    BDDMockito.given(repository.save(ArgumentMatchers.<TipoRegimenConcurrencia>any()))
        .willAnswer(new Answer<TipoRegimenConcurrencia>() {
          @Override
          public TipoRegimenConcurrencia answer(InvocationOnMock invocation) throws Throwable {
            TipoRegimenConcurrencia givenData = invocation.getArgument(0, TipoRegimenConcurrencia.class);
            TipoRegimenConcurrencia newData = new TipoRegimenConcurrencia();
            BeanUtils.copyProperties(givenData, newData);
            newData.setId(1L);
            return newData;
          }
        });

    // when: create TipoRegimenConcurrencia
    TipoRegimenConcurrencia created = service.create(data);

    // then: new TipoRegimenConcurrencia is created
    Assertions.assertThat(created).isNotNull();
    Assertions.assertThat(created.getId()).isNotNull();
    Assertions.assertThat(created.getNombre()).isEqualTo(data.getNombre());
    Assertions.assertThat(created.getActivo()).isEqualTo(data.getActivo());
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: a TipoRegimenConcurrencia with id filled
    TipoRegimenConcurrencia data = generarMockTipoRegimenConcurrencia(1L, Boolean.TRUE);

    Assertions.assertThatThrownBy(
        // when: create TipoRegimenConcurrencia
        () -> service.create(data))
        // then: throw exception as id can't be provided
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void create_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: a TipoRegimenConcurrencia with duplicated nombre

    TipoRegimenConcurrencia givenData = generarMockTipoRegimenConcurrencia(1L, Boolean.TRUE);
    TipoRegimenConcurrencia newData = new TipoRegimenConcurrencia();
    BeanUtils.copyProperties(givenData, newData);
    newData.setId(null);

    BDDMockito.given(repository.findByNombre(ArgumentMatchers.anyString())).willReturn(Optional.of(givenData));

    Assertions.assertThatThrownBy(
        // when: create TipoRegimenConcurrencia
        () -> service.create(newData))
        // then: throw exception as Nombre already exists
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_WithExistingId_ReturnsTipoRegimenConcurrencia() {
    // given: existing TipoRegimenConcurrencia
    TipoRegimenConcurrencia data = generarMockTipoRegimenConcurrencia(1L, Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(data));

    BDDMockito.given(repository.save(ArgumentMatchers.<TipoRegimenConcurrencia>any()))
        .willAnswer(new Answer<TipoRegimenConcurrencia>() {
          @Override
          public TipoRegimenConcurrencia answer(InvocationOnMock invocation) throws Throwable {
            TipoRegimenConcurrencia givenData = invocation.getArgument(0, TipoRegimenConcurrencia.class);
            givenData.setNombre("Nombre-Modificado");
            return givenData;
          }
        });

    // when: update TipoRegimenConcurrencia
    TipoRegimenConcurrencia updated = service.update(data);

    // then: TipoRegimenConcurrencia is updated
    Assertions.assertThat(updated).isNotNull();
    Assertions.assertThat(updated.getId()).isNotNull();
    Assertions.assertThat(updated.getId()).isEqualTo(data.getId());
    Assertions.assertThat(updated.getNombre()).isEqualTo("Nombre-Modificado");
    Assertions.assertThat(updated.getActivo()).isEqualTo(data.getActivo());
  }

  @Test
  public void update_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    TipoRegimenConcurrencia data = generarMockTipoRegimenConcurrencia(1L, Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update non existing TipoRegimenConcurrencia
        () -> service.update(data))
        // then: NotFoundException is thrown
        .isInstanceOf(TipoRegimenConcurrenciaNotFoundException.class);
  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {
    // given: a TipoRegimenConcurrencia without id filled
    TipoRegimenConcurrencia data = generarMockTipoRegimenConcurrencia(null, Boolean.TRUE);

    Assertions.assertThatThrownBy(
        // when: update TipoRegimenConcurrencia
        () -> service.update(data))
        // then: throw exception as id must be provided
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: a TipoRegimenConcurrencia with duplicated nombre
    TipoRegimenConcurrencia givenData = generarMockTipoRegimenConcurrencia(1L, Boolean.TRUE);
    TipoRegimenConcurrencia data = new TipoRegimenConcurrencia();
    BeanUtils.copyProperties(givenData, data);
    data.setId(2L);

    BDDMockito.given(repository.findByNombre(ArgumentMatchers.anyString())).willReturn(Optional.of(givenData));

    Assertions.assertThatThrownBy(
        // when: update TipoRegimenConcurrencia
        () -> service.update(data))
        // then: throw exception as Nombre already exists
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void disable_WithExistingId_ReturnsTipoRegimenConcurrencia() {
    // given: existing TipoRegimenConcurrencia
    TipoRegimenConcurrencia data = generarMockTipoRegimenConcurrencia(1L, Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(data));

    BDDMockito.given(repository.save(ArgumentMatchers.<TipoRegimenConcurrencia>any()))
        .willAnswer(new Answer<TipoRegimenConcurrencia>() {
          @Override
          public TipoRegimenConcurrencia answer(InvocationOnMock invocation) throws Throwable {
            TipoRegimenConcurrencia givenData = invocation.getArgument(0, TipoRegimenConcurrencia.class);
            givenData.setActivo(Boolean.FALSE);
            return givenData;
          }
        });

    // when: disable TipoRegimenConcurrencia
    TipoRegimenConcurrencia disabledData = service.disable(data.getId());

    // then: TipoRegimenConcurrencia is disabled
    Assertions.assertThat(disabledData).isNotNull();
    Assertions.assertThat(disabledData.getId()).isNotNull();
    Assertions.assertThat(disabledData.getId()).isEqualTo(data.getId());
    Assertions.assertThat(disabledData.getNombre()).isEqualTo(data.getNombre());
    Assertions.assertThat(disabledData.getActivo()).isEqualTo(Boolean.FALSE);
  }

  @Test
  public void disable_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    TipoRegimenConcurrencia data = generarMockTipoRegimenConcurrencia(1L, Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update non existing TipoRegimenConcurrencia
        () -> service.disable(data.getId()))
        // then: NotFoundException is thrown
        .isInstanceOf(TipoRegimenConcurrenciaNotFoundException.class);
  }

  @Test
  public void findById_WithExistingId_ReturnsTipoRegimenConcurrencia() throws Exception {
    // given: existing TipoRegimenConcurrencia
    TipoRegimenConcurrencia givenData = generarMockTipoRegimenConcurrencia(1L, Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(givenData));

    // when: find by id TipoRegimenConcurrencia
    TipoRegimenConcurrencia data = service.findById(givenData.getId());

    // then: returns TipoRegimenConcurrencia
    Assertions.assertThat(data).isNotNull();
    Assertions.assertThat(data.getId()).isNotNull();
    Assertions.assertThat(data.getId()).isEqualTo(data.getId());
    Assertions.assertThat(data.getNombre()).isEqualTo(data.getNombre());
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
        .isInstanceOf(TipoRegimenConcurrenciaNotFoundException.class);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred TipoRegimenConcurrencia
    List<TipoRegimenConcurrencia> data = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      data.add(generarMockTipoRegimenConcurrencia(Long.valueOf(i), Boolean.TRUE));
    }

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<TipoRegimenConcurrencia>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<TipoRegimenConcurrencia>>() {
          @Override
          public Page<TipoRegimenConcurrencia> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoRegimenConcurrencia> content = data.subList(fromIndex, toIndex);
            Page<TipoRegimenConcurrencia> page = new PageImpl<>(content, pageable, data.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<TipoRegimenConcurrencia> page = service.findAll(null, paging);

    // then: A Page with ten TipoRegimenConcurrencia are returned containing
    // Nombre='nombre-31' to
    // 'nombre-40'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoRegimenConcurrencia item = page.getContent().get(i);
      Assertions.assertThat(item.getNombre()).isEqualTo("nombre-" + j);
    }
  }

  @Test
  public void findAllTodos_WithPaging_ReturnsPage() {
    // given: One hundred TipoRegimenConcurrencia
    List<TipoRegimenConcurrencia> data = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      data.add(generarMockTipoRegimenConcurrencia(Long.valueOf(i), Boolean.TRUE));
    }

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<TipoRegimenConcurrencia>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<TipoRegimenConcurrencia>>() {
          @Override
          public Page<TipoRegimenConcurrencia> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoRegimenConcurrencia> content = data.subList(fromIndex, toIndex);
            Page<TipoRegimenConcurrencia> page = new PageImpl<>(content, pageable, data.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<TipoRegimenConcurrencia> page = service.findAllTodos(null, paging);

    // then: A Page with ten TipoRegimenConcurrencia are returned containing
    // Nombre='nombre-31' to
    // 'nombre-40'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoRegimenConcurrencia item = page.getContent().get(i);
      Assertions.assertThat(item.getNombre()).isEqualTo("nombre-" + j);
    }
  }

  /**
   * Función que devuelve un objeto TipoRegimenConcurrencia
   * 
   * @param id
   * @param activo
   * @return TipoRegimenConcurrencia
   */
  private TipoRegimenConcurrencia generarMockTipoRegimenConcurrencia(Long id, Boolean activo) {
    return TipoRegimenConcurrencia.builder().id(id).nombre("nombre-" + id).activo(activo).build();
  }

}
