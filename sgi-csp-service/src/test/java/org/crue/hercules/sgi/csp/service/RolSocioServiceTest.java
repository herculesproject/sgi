package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.RolSocioNotFoundException;
import org.crue.hercules.sgi.csp.model.RolSocio;
import org.crue.hercules.sgi.csp.repository.RolSocioRepository;
import org.crue.hercules.sgi.csp.service.impl.RolSocioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public class RolSocioServiceTest extends BaseServiceTest {

  @Mock
  private RolSocioRepository repository;
  private RolSocioService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new RolSocioServiceImpl(repository);
  }

  @Test
  public void create_ReturnsRolSocio() {
    // given: new RolSocio
    RolSocio rolSocio = generarMockRolSocio(1L);
    rolSocio.setId(null);

    BDDMockito.given(repository.save(ArgumentMatchers.<RolSocio>any())).willAnswer(new Answer<RolSocio>() {
      @Override
      public RolSocio answer(InvocationOnMock invocation) throws Throwable {
        RolSocio givenData = invocation.getArgument(0, RolSocio.class);
        RolSocio newData = new RolSocio();
        BeanUtils.copyProperties(givenData, newData);
        newData.setId(1L);
        return newData;
      }
    });

    // when: create RolSocio
    RolSocio responseData = service.create(rolSocio);

    // then: new RolSocio is created
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getAbreviatura()).as("getAbreviatura()").isEqualTo(rolSocio.getAbreviatura());
    Assertions.assertThat(responseData.getNombre()).as("getNombre()").isEqualTo(rolSocio.getNombre());
    Assertions.assertThat(responseData.getDescripcion()).as("getDescripcion()").isEqualTo(rolSocio.getDescripcion());
    Assertions.assertThat(responseData.getCoordinador()).as("getCoordinador()").isEqualTo(rolSocio.getCoordinador());
    Assertions.assertThat(responseData.getActivo()).isEqualTo(Boolean.TRUE);
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: a RolSocio with id filled
    RolSocio rolSocio = generarMockRolSocio(1L);

    Assertions.assertThatThrownBy(
        // when: create RolSocio
        () -> service.create(rolSocio))
        // then: throw exception as id can't be provided
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Id tiene que ser null para crear la RolSocio");
  }

  @Test
  public void create_WithoutAbreviatura_ThrowsIllegalArgumentException() {
    // given: a RolSocio without Abreviatura
    RolSocio rolSocio = generarMockRolSocio(1L);
    rolSocio.setId(null);
    rolSocio.setAbreviatura(null);

    Assertions.assertThatThrownBy(
        // when: create RolSocio
        () -> service.create(rolSocio))
        // then: throw exception as Abreviatura is not provided
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Abreviatura es un campo obligatorio para RolSocio");
  }

  @Test
  public void create_WithoutNombre_ThrowsIllegalArgumentException() {
    // given: a RolSocio without Nombre
    RolSocio rolSocio = generarMockRolSocio(1L);
    rolSocio.setId(null);
    rolSocio.setNombre(null);

    Assertions.assertThatThrownBy(
        // when: create RolSocio
        () -> service.create(rolSocio))
        // then: throw exception as Nombre is not provided
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Nombre es un campo obligatorio para RolSocio");
  }

  @Test
  public void create_WithDuplicatedAbreviatura_ThrowsIllegalArgumentException() {
    // given: a RolSocio with duplicated Abreviatura
    RolSocio rolSocioExistente = generarMockRolSocio(1L);
    RolSocio rolSocio = generarMockRolSocio(1L);
    rolSocio.setId(null);

    BDDMockito.given(repository.findByAbreviaturaAndActivoIsTrue(ArgumentMatchers.<String>any()))
        .willReturn(Optional.of(rolSocioExistente));

    Assertions.assertThatThrownBy(
        // when: create RolSocio
        () -> service.create(rolSocio))
        // then: throw exception as Abreviatura is duplicated
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un RolSocio activo con la abreviatura '%s'", rolSocio.getAbreviatura());
  }

  @Test
  public void create_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: a RolSocio with duplicated Nombre
    RolSocio rolSocioExistente = generarMockRolSocio(1L);
    RolSocio rolSocio = generarMockRolSocio(1L);
    rolSocio.setId(null);

    BDDMockito.given(repository.findByNombreAndActivoIsTrue(ArgumentMatchers.<String>any()))
        .willReturn(Optional.of(rolSocioExistente));

    Assertions.assertThatThrownBy(
        // when: create RolSocio
        () -> service.create(rolSocio))
        // then: throw exception as Nombre is duplicated
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un RolSocio activo con el nombre '%s'", rolSocio.getNombre());
  }

  @Test
  public void update_WithExistingId_ReturnsRolSocio() {
    // given: existing RolSocio
    RolSocio rolSocio = generarMockRolSocio(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(rolSocio));

    BDDMockito.given(repository.save(ArgumentMatchers.<RolSocio>any())).willAnswer(new Answer<RolSocio>() {
      @Override
      public RolSocio answer(InvocationOnMock invocation) throws Throwable {
        RolSocio givenData = invocation.getArgument(0, RolSocio.class);
        givenData.setDescripcion("descripcion-modificada");
        return givenData;
      }
    });

    // when: update RolSocio
    RolSocio responseData = service.update(rolSocio);

    // then: RolSocio is updated
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(rolSocio.getId());
    Assertions.assertThat(responseData.getAbreviatura()).as("getAbreviatura()").isEqualTo(rolSocio.getAbreviatura());
    Assertions.assertThat(responseData.getNombre()).as("getNombre()").isEqualTo(rolSocio.getNombre());
    Assertions.assertThat(responseData.getDescripcion()).as("getDescripcion()").isEqualTo("descripcion-modificada");
    Assertions.assertThat(responseData.getCoordinador()).as("getCoordinador()").isEqualTo(rolSocio.getCoordinador());
    Assertions.assertThat(responseData.getActivo()).as("getActivo()").isEqualTo(rolSocio.getActivo());
  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {
    // given: a RolSocio without id filled
    RolSocio rolSocio = generarMockRolSocio(1L);
    rolSocio.setId(null);

    Assertions.assertThatThrownBy(
        // when: update RolSocio
        () -> service.update(rolSocio))
        // then: throw exception as id must be provided
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Id no puede ser null para actualizar RolSocio");
  }

  @Test
  public void update_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    RolSocio rolSocio = generarMockRolSocio(1L);
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update non existing RolSocio
        () -> service.update(rolSocio))
        // then: NotFoundException is thrown
        .isInstanceOf(RolSocioNotFoundException.class);
  }

  @Test
  public void update_WithoutAbreviatura_ThrowsIllegalArgumentException() {
    // given: a RolSocio without Abreviatura
    RolSocio rolSocio = generarMockRolSocio(1L);
    rolSocio.setAbreviatura(null);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(rolSocio));

    Assertions.assertThatThrownBy(
        // when: update RolSocio
        () -> service.update(rolSocio))
        // then: throw exception as Abreviatura is not provided
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Abreviatura es un campo obligatorio para RolSocio");
  }

  @Test
  public void update_WithoutNombre_ThrowsIllegalArgumentException() {
    // given: a RolSocio without Nombre
    RolSocio rolSocio = generarMockRolSocio(1L);
    rolSocio.setNombre(null);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(rolSocio));

    Assertions.assertThatThrownBy(
        // when: update RolSocio
        () -> service.update(rolSocio))
        // then: throw exception as Nombre is not provided
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Nombre es un campo obligatorio para RolSocio");
  }

  @Test
  public void update_WithDuplicatedAbreviatura_ThrowsIllegalArgumentException() {
    // given: a RolSocio with duplicated Abreviatura
    RolSocio rolSocioExistente = generarMockRolSocio(2L);
    rolSocioExistente.setAbreviatura("ABR");
    RolSocio rolSocio = generarMockRolSocio(1L);
    rolSocio.setAbreviatura("ABR");

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(rolSocio));

    BDDMockito.given(repository.findByAbreviaturaAndActivoIsTrue(ArgumentMatchers.<String>any()))
        .willReturn(Optional.of(rolSocioExistente));

    Assertions.assertThatThrownBy(
        // when: update RolSocio
        () -> service.update(rolSocio))
        // then: throw exception as Abreviatura is duplicated
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un RolSocio activo con la abreviatura '%s'", rolSocio.getAbreviatura());
  }

  @Test
  public void update_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: a RolSocio with duplicated Nombre
    RolSocio rolSocioExistente = generarMockRolSocio(2L);
    rolSocioExistente.setNombre("nombre");
    RolSocio rolSocio = generarMockRolSocio(1L);
    rolSocio.setNombre("nombre");

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(rolSocio));

    BDDMockito.given(repository.findByNombreAndActivoIsTrue(ArgumentMatchers.<String>any()))
        .willReturn(Optional.of(rolSocioExistente));

    Assertions.assertThatThrownBy(
        // when: update RolSocio
        () -> service.update(rolSocio))
        // then: throw exception as Nombre is duplicated
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un RolSocio activo con el nombre '%s'", rolSocio.getNombre());
  }

  @Test
  public void enable_WithExistingId_ReturnsRolSocio() {
    // given: existing RolSocio
    RolSocio rolSocio = generarMockRolSocio(1L, Boolean.FALSE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(rolSocio));

    BDDMockito.given(repository.save(ArgumentMatchers.<RolSocio>any())).willAnswer(new Answer<RolSocio>() {
      @Override
      public RolSocio answer(InvocationOnMock invocation) throws Throwable {
        RolSocio givenData = invocation.getArgument(0, RolSocio.class);
        givenData.setActivo(Boolean.TRUE);
        return givenData;
      }
    });

    // when: enable RolSocio
    RolSocio responseData = service.enable(rolSocio.getId());

    // then: RolSocio is enabled
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(rolSocio.getId());
    Assertions.assertThat(responseData.getAbreviatura()).as("getAbreviatura()").isEqualTo(rolSocio.getAbreviatura());
    Assertions.assertThat(responseData.getNombre()).as("getNombre()").isEqualTo(rolSocio.getNombre());
    Assertions.assertThat(responseData.getDescripcion()).as("getDescripcion()").isEqualTo(rolSocio.getDescripcion());
    Assertions.assertThat(responseData.getCoordinador()).as("getCoordinador()").isEqualTo(rolSocio.getCoordinador());
    Assertions.assertThat(responseData.getActivo()).isEqualTo(Boolean.TRUE);
  }

  @Test
  public void enable_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    RolSocio rolSocio = generarMockRolSocio(1L, Boolean.FALSE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: enable non existing RolSocio
        () -> service.disable(rolSocio.getId()))
        // then: NotFoundException is thrown
        .isInstanceOf(RolSocioNotFoundException.class);
  }

  @Test
  public void enable_WithDuplicatedAbreviatura_ThrowsIllegalArgumentException() throws Exception {
    // given: a RolSocio with duplicated Abreviatura
    RolSocio rolSocioExistente = generarMockRolSocio(2L);
    rolSocioExistente.setAbreviatura("ABR");
    RolSocio rolSocio = generarMockRolSocio(1L);
    rolSocio.setActivo(Boolean.FALSE);
    rolSocio.setAbreviatura("ABR");

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(rolSocio));

    BDDMockito.given(repository.findByAbreviaturaAndActivoIsTrue(ArgumentMatchers.<String>any()))
        .willReturn(Optional.of(rolSocioExistente));

    Assertions.assertThatThrownBy(
        // when: enable RolSocio
        () -> service.enable(rolSocio.getId()))
        // then: throw exception as Abreviatura is duplicated
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un RolSocio activo con la abreviatura '%s'", rolSocio.getAbreviatura());
  }

  @Test
  public void enable_WithDuplicatedNombre_ThrowsIllegalArgumentException() throws Exception {
    // given: a RolSocio with duplicated Nombre
    RolSocio rolSocioExistente = generarMockRolSocio(2L);
    rolSocioExistente.setNombre("nombre");
    RolSocio rolSocio = generarMockRolSocio(1L, Boolean.FALSE);
    rolSocio.setNombre("nombre");

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(rolSocio));

    BDDMockito.given(repository.findByNombreAndActivoIsTrue(ArgumentMatchers.<String>any()))
        .willReturn(Optional.of(rolSocioExistente));

    Assertions.assertThatThrownBy(
        // when: enable RolSocio
        () -> service.enable(rolSocio.getId()))
        // then: throw exception as Nombre is duplicated
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un RolSocio activo con el nombre '%s'", rolSocio.getNombre());
  }

  @Test
  public void disable_WithExistingId_ReturnsRolSocio() {
    // given: existing RolSocio
    RolSocio rolSocio = generarMockRolSocio(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(rolSocio));

    BDDMockito.given(repository.save(ArgumentMatchers.<RolSocio>any())).willAnswer(new Answer<RolSocio>() {
      @Override
      public RolSocio answer(InvocationOnMock invocation) throws Throwable {
        RolSocio givenData = invocation.getArgument(0, RolSocio.class);
        givenData.setActivo(Boolean.FALSE);
        return givenData;
      }
    });

    // when: disable RolSocio
    RolSocio responseData = service.disable(rolSocio.getId());

    // then: RolSocio is disabled
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(rolSocio.getId());
    Assertions.assertThat(responseData.getAbreviatura()).as("getAbreviatura()").isEqualTo(rolSocio.getAbreviatura());
    Assertions.assertThat(responseData.getNombre()).as("getNombre()").isEqualTo(rolSocio.getNombre());
    Assertions.assertThat(responseData.getDescripcion()).as("getDescripcion()").isEqualTo(rolSocio.getDescripcion());
    Assertions.assertThat(responseData.getCoordinador()).as("getCoordinador()").isEqualTo(rolSocio.getCoordinador());
    Assertions.assertThat(responseData.getActivo()).isEqualTo(Boolean.FALSE);
  }

  @Test
  public void disable_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    RolSocio rolSocio = generarMockRolSocio(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: disable non existing RolSocio
        () -> service.disable(rolSocio.getId()))
        // then: NotFoundException is thrown
        .isInstanceOf(RolSocioNotFoundException.class);
  }

  @Test
  public void existsById_WithExistingId_ReturnsTRUE() throws Exception {
    // given: existing id
    Long id = 1L;
    BDDMockito.given(repository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: exists by id
    boolean responseData = service.existsById(id);

    // then: returns TRUE
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData).isTrue();
  }

  @Test
  public void existsById_WithNoExistingId_ReturnsFALSE() throws Exception {
    // given: no existing id
    Long id = 1L;
    BDDMockito.given(repository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    // when: exists by id
    boolean responseData = service.existsById(id);

    // then: returns TRUE
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData).isFalse();
  }

  @Test
  public void findById_WithExistingId_ReturnsRolSocio() throws Exception {
    // given: existing RolSocio
    RolSocio rolSocioExistente = generarMockRolSocio(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(rolSocioExistente));

    // when: find by id RolSocio
    RolSocio responseData = service.findById(rolSocioExistente.getId());

    // then: returns RolSocio
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(responseData.getId());
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getAbreviatura()).as("getAbreviatura()")
        .isEqualTo(responseData.getAbreviatura());
    Assertions.assertThat(responseData.getNombre()).as("getNombre()").isEqualTo(responseData.getNombre());
    Assertions.assertThat(responseData.getDescripcion()).as("getDescripcion()")
        .isEqualTo(responseData.getDescripcion());
    Assertions.assertThat(responseData.getCoordinador()).as("getCoordinador()")
        .isEqualTo(responseData.getCoordinador());
    Assertions.assertThat(responseData.getActivo()).as("getActivo()").isEqualTo(rolSocioExistente.getActivo());
  }

  @Test
  public void findById_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: find by non existing id
        () -> service.findById(1L))
        // then: NotFoundException is thrown
        .isInstanceOf(RolSocioNotFoundException.class);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred RolSocio
    List<RolSocio> rolSocios = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      if (i % 2 == 0) {
        rolSocios.add(generarMockRolSocio(Long.valueOf(i)));
      }
    }

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<RolSocio>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<RolSocio>>() {
          @Override
          public Page<RolSocio> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<RolSocio> content = rolSocios.subList(fromIndex, toIndex);
            Page<RolSocio> page = new PageImpl<>(content, pageable, rolSocios.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<RolSocio> page = service.findAll(null, paging);

    // then: A Page with ten RolSocio are returned
    // containing Abreviatura='062' to '080'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(50);

    for (int i = 0, j = 62; i < 10; i++, j += 2) {
      RolSocio item = page.getContent().get(i);
      Assertions.assertThat(item.getAbreviatura()).isEqualTo(String.format("%03d", j));
      Assertions.assertThat(item.getActivo()).isEqualTo(Boolean.TRUE);
    }
  }

  @Test
  public void findAllTodos_WithPaging_ReturnsPage() {
    // given: One hundred RolSocio
    List<RolSocio> rolSocios = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      rolSocios.add(generarMockRolSocio(Long.valueOf(i), (i % 2 == 0) ? Boolean.TRUE : Boolean.FALSE));
    }

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<RolSocio>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<RolSocio>>() {
          @Override
          public Page<RolSocio> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<RolSocio> content = rolSocios.subList(fromIndex, toIndex);
            Page<RolSocio> page = new PageImpl<>(content, pageable, rolSocios.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<RolSocio> page = service.findAll(null, paging);

    // then: A Page with ten RolSocio are returned containing
    // Nombre='nombre-31' to
    // 'nombre-40'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      RolSocio item = page.getContent().get(i);
      Assertions.assertThat(item.getAbreviatura()).isEqualTo(String.format("%03d", j));
      Assertions.assertThat(item.getActivo()).isEqualTo((j % 2 == 0 ? Boolean.TRUE : Boolean.FALSE));
    }
  }

  /**
   * Función que genera RolSocio
   * 
   * @param rolSocioId
   * @return el rolSocio
   */
  private RolSocio generarMockRolSocio(Long rolSocioId) {

    String suffix = String.format("%03d", rolSocioId);

    // @formatter:off
    RolSocio rolSocio = RolSocio.builder()
        .id(rolSocioId)
        .abreviatura(suffix)
        .nombre("nombre-" + suffix)
        .descripcion("descripcion-" + suffix)
        .coordinador(Boolean.FALSE)
        .activo(Boolean.TRUE)
        .build();
    // @formatter:on

    return rolSocio;
  }

  /**
   * Función que genera RolSocio con el estado indicado
   * 
   * @param rolSocioId
   * @param activo
   * @return el rolSocio
   */
  private RolSocio generarMockRolSocio(Long rolSocioId, Boolean activo) {

    RolSocio rolSocio = generarMockRolSocio(rolSocioId);
    rolSocio.setActivo(activo);

    return rolSocio;
  }

}
