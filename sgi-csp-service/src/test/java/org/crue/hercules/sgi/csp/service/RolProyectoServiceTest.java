package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.RolProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.repository.RolProyectoRepository;
import org.crue.hercules.sgi.csp.service.impl.RolProyectoServiceImpl;
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

public class RolProyectoServiceTest extends BaseServiceTest {

  @Mock
  private RolProyectoRepository repository;
  private RolProyectoService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new RolProyectoServiceImpl(repository);
  }

  @Test
  public void create_ReturnsRolProyecto() {
    // given: new RolProyecto
    RolProyecto rolProyecto = generarMockRolProyecto(1L);
    rolProyecto.setId(null);

    BDDMockito.given(repository.save(ArgumentMatchers.<RolProyecto>any())).willAnswer(new Answer<RolProyecto>() {
      @Override
      public RolProyecto answer(InvocationOnMock invocation) throws Throwable {
        RolProyecto givenData = invocation.getArgument(0, RolProyecto.class);
        RolProyecto newData = new RolProyecto();
        BeanUtils.copyProperties(givenData, newData);
        newData.setId(1L);
        return newData;
      }
    });

    // when: create RolProyecto
    RolProyecto responseData = service.create(rolProyecto);

    // then: new RolProyecto is created
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getAbreviatura()).as("getAbreviatura()").isEqualTo(rolProyecto.getAbreviatura());
    Assertions.assertThat(responseData.getNombre()).as("getNombre()").isEqualTo(rolProyecto.getNombre());
    Assertions.assertThat(responseData.getDescripcion()).as("getDescripcion()").isEqualTo(rolProyecto.getDescripcion());
    Assertions.assertThat(responseData.getRolPrincipal()).as("getRolPrincipal()")
        .isEqualTo(rolProyecto.getRolPrincipal());
    Assertions.assertThat(responseData.getResponsableEconomico()).as("getResponsableEconomico()")
        .isEqualTo(rolProyecto.getResponsableEconomico());
    Assertions.assertThat(responseData.getEquipo()).as("getEquipo()").isEqualTo(rolProyecto.getEquipo());
    Assertions.assertThat(responseData.getActivo()).isEqualTo(Boolean.TRUE);
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: a RolProyecto with id filled
    RolProyecto rolProyecto = generarMockRolProyecto(1L);

    Assertions.assertThatThrownBy(
        // when: create RolProyecto
        () -> service.create(rolProyecto))
        // then: throw exception as id can't be provided
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Id tiene que ser null para crear la RolProyecto");
  }

  @Test
  public void create_WithoutAbreviatura_ThrowsIllegalArgumentException() {
    // given: a RolProyecto without Abreviatura
    RolProyecto rolProyecto = generarMockRolProyecto(1L);
    rolProyecto.setId(null);
    rolProyecto.setAbreviatura(null);

    Assertions.assertThatThrownBy(
        // when: create RolProyecto
        () -> service.create(rolProyecto))
        // then: throw exception as Abreviatura is not provided
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Abreviatura es un campo obligatorio para RolProyecto");
  }

  @Test
  public void create_WithoutNombre_ThrowsIllegalArgumentException() {
    // given: a RolProyecto without Nombre
    RolProyecto rolProyecto = generarMockRolProyecto(1L);
    rolProyecto.setId(null);
    rolProyecto.setNombre(null);

    Assertions.assertThatThrownBy(
        // when: create RolProyecto
        () -> service.create(rolProyecto))
        // then: throw exception as Nombre is not provided
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Nombre es un campo obligatorio para RolProyecto");
  }

  @Test
  public void create_WithoutEquipo_ThrowsIllegalArgumentException() {
    // given: a RolProyecto without Equipo
    RolProyecto rolProyecto = generarMockRolProyecto(1L);
    rolProyecto.setId(null);
    rolProyecto.setEquipo(null);

    Assertions.assertThatThrownBy(
        // when: create RolProyecto
        () -> service.create(rolProyecto))
        // then: throw exception as Equipo is not provided
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Equipo es un campo obligatorio para RolProyecto");
  }

  @Test
  public void create_WithDuplicatedAbreviatura_ThrowsIllegalArgumentException() {
    // given: a RolProyecto with duplicated Abreviatura
    RolProyecto rolProyectoExistente = generarMockRolProyecto(1L);
    RolProyecto rolProyecto = generarMockRolProyecto(1L);
    rolProyecto.setId(null);

    BDDMockito.given(repository.findByAbreviaturaAndActivoIsTrue(ArgumentMatchers.<String>any()))
        .willReturn(Optional.of(rolProyectoExistente));

    Assertions.assertThatThrownBy(
        // when: create RolProyecto
        () -> service.create(rolProyecto))
        // then: throw exception as Abreviatura is duplicated
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un RolProyecto activo con la abreviatura '%s'", rolProyecto.getAbreviatura());
  }

  @Test
  public void create_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: a RolProyecto with duplicated Nombre
    RolProyecto rolProyectoExistente = generarMockRolProyecto(1L);
    RolProyecto rolProyecto = generarMockRolProyecto(1L);
    rolProyecto.setId(null);

    BDDMockito.given(repository.findByNombreAndActivoIsTrue(ArgumentMatchers.<String>any()))
        .willReturn(Optional.of(rolProyectoExistente));

    Assertions.assertThatThrownBy(
        // when: create RolProyecto
        () -> service.create(rolProyecto))
        // then: throw exception as Nombre is duplicated
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un RolProyecto activo con el nombre '%s'", rolProyecto.getNombre());
  }

  @Test
  public void update_WithExistingId_ReturnsRolProyecto() {
    // given: existing RolProyecto
    RolProyecto rolProyecto = generarMockRolProyecto(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(rolProyecto));

    BDDMockito.given(repository.save(ArgumentMatchers.<RolProyecto>any())).willAnswer(new Answer<RolProyecto>() {
      @Override
      public RolProyecto answer(InvocationOnMock invocation) throws Throwable {
        RolProyecto givenData = invocation.getArgument(0, RolProyecto.class);
        givenData.setDescripcion("descripcion-modificada");
        return givenData;
      }
    });

    // when: update RolProyecto
    RolProyecto responseData = service.update(rolProyecto);

    // then: RolProyecto is updated
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(rolProyecto.getId());
    Assertions.assertThat(responseData.getAbreviatura()).as("getAbreviatura()").isEqualTo(rolProyecto.getAbreviatura());
    Assertions.assertThat(responseData.getNombre()).as("getNombre()").isEqualTo(rolProyecto.getNombre());
    Assertions.assertThat(responseData.getDescripcion()).as("getDescripcion()").isEqualTo("descripcion-modificada");
    Assertions.assertThat(responseData.getRolPrincipal()).as("getRolPrincipal()")
        .isEqualTo(rolProyecto.getRolPrincipal());
    Assertions.assertThat(responseData.getResponsableEconomico()).as("getResponsableEconomico()")
        .isEqualTo(rolProyecto.getResponsableEconomico());
    Assertions.assertThat(responseData.getEquipo()).as("getEquipo()").isEqualTo(rolProyecto.getEquipo());
    Assertions.assertThat(responseData.getActivo()).as("getActivo()").isEqualTo(rolProyecto.getActivo());
  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {
    // given: a RolProyecto without id filled
    RolProyecto rolProyecto = generarMockRolProyecto(1L);
    rolProyecto.setId(null);

    Assertions.assertThatThrownBy(
        // when: update RolProyecto
        () -> service.update(rolProyecto))
        // then: throw exception as id must be provided
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Id no puede ser null para actualizar RolProyecto");
  }

  @Test
  public void update_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    RolProyecto rolProyecto = generarMockRolProyecto(1L);
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update non existing RolProyecto
        () -> service.update(rolProyecto))
        // then: NotFoundException is thrown
        .isInstanceOf(RolProyectoNotFoundException.class);
  }

  @Test
  public void update_WithoutAbreviatura_ThrowsIllegalArgumentException() {
    // given: a RolProyecto without Abreviatura
    RolProyecto rolProyecto = generarMockRolProyecto(1L);
    rolProyecto.setAbreviatura(null);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(rolProyecto));

    Assertions.assertThatThrownBy(
        // when: update RolProyecto
        () -> service.update(rolProyecto))
        // then: throw exception as Abreviatura is not provided
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Abreviatura es un campo obligatorio para RolProyecto");
  }

  @Test
  public void update_WithoutNombre_ThrowsIllegalArgumentException() {
    // given: a RolProyecto without Nombre
    RolProyecto rolProyecto = generarMockRolProyecto(1L);
    rolProyecto.setNombre(null);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(rolProyecto));

    Assertions.assertThatThrownBy(
        // when: update RolProyecto
        () -> service.update(rolProyecto))
        // then: throw exception as Nombre is not provided
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Nombre es un campo obligatorio para RolProyecto");
  }

  @Test
  public void update_WithoutEquipo_ThrowsIllegalArgumentException() {
    // given: a RolProyecto without Equipo
    RolProyecto rolProyecto = generarMockRolProyecto(1L);
    rolProyecto.setEquipo(null);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(rolProyecto));

    Assertions.assertThatThrownBy(
        // when: update RolProyecto
        () -> service.update(rolProyecto))
        // then: throw exception as Equipo is not provided
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Equipo es un campo obligatorio para RolProyecto");
  }

  @Test
  public void update_WithDuplicatedAbreviatura_ThrowsIllegalArgumentException() {
    // given: a RolProyecto with duplicated Abreviatura
    RolProyecto rolProyectoExistente = generarMockRolProyecto(2L);
    rolProyectoExistente.setAbreviatura("ABR");
    RolProyecto rolProyecto = generarMockRolProyecto(1L);
    rolProyecto.setAbreviatura("ABR");

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(rolProyecto));

    BDDMockito.given(repository.findByAbreviaturaAndActivoIsTrue(ArgumentMatchers.<String>any()))
        .willReturn(Optional.of(rolProyectoExistente));

    Assertions.assertThatThrownBy(
        // when: update RolProyecto
        () -> service.update(rolProyecto))
        // then: throw exception as Abreviatura is duplicated
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un RolProyecto activo con la abreviatura '%s'", rolProyecto.getAbreviatura());
  }

  @Test
  public void update_WithDuplicatedNombre_ThrowsIllegalArgumentException() {
    // given: a RolProyecto with duplicated Nombre
    RolProyecto rolProyectoExistente = generarMockRolProyecto(2L);
    rolProyectoExistente.setNombre("nombre");
    RolProyecto rolProyecto = generarMockRolProyecto(1L);
    rolProyecto.setNombre("nombre");

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(rolProyecto));

    BDDMockito.given(repository.findByNombreAndActivoIsTrue(ArgumentMatchers.<String>any()))
        .willReturn(Optional.of(rolProyectoExistente));

    Assertions.assertThatThrownBy(
        // when: update RolProyecto
        () -> service.update(rolProyecto))
        // then: throw exception as Nombre is duplicated
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un RolProyecto activo con el nombre '%s'", rolProyecto.getNombre());
  }

  @Test
  public void enable_WithExistingId_ReturnsRolProyecto() {
    // given: existing RolProyecto
    RolProyecto rolProyecto = generarMockRolProyecto(1L, Boolean.FALSE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(rolProyecto));

    BDDMockito.given(repository.save(ArgumentMatchers.<RolProyecto>any())).willAnswer(new Answer<RolProyecto>() {
      @Override
      public RolProyecto answer(InvocationOnMock invocation) throws Throwable {
        RolProyecto givenData = invocation.getArgument(0, RolProyecto.class);
        givenData.setActivo(Boolean.TRUE);
        return givenData;
      }
    });

    // when: enable RolProyecto
    RolProyecto responseData = service.enable(rolProyecto.getId());

    // then: RolProyecto is enabled
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(rolProyecto.getId());
    Assertions.assertThat(responseData.getAbreviatura()).as("getAbreviatura()").isEqualTo(rolProyecto.getAbreviatura());
    Assertions.assertThat(responseData.getNombre()).as("getNombre()").isEqualTo(rolProyecto.getNombre());
    Assertions.assertThat(responseData.getDescripcion()).as("getDescripcion()").isEqualTo(rolProyecto.getDescripcion());
    Assertions.assertThat(responseData.getRolPrincipal()).as("getRolPrincipal()")
        .isEqualTo(rolProyecto.getRolPrincipal());
    Assertions.assertThat(responseData.getResponsableEconomico()).as("getResponsableEconomico()")
        .isEqualTo(rolProyecto.getResponsableEconomico());
    Assertions.assertThat(responseData.getEquipo()).as("getEquipo()").isEqualTo(rolProyecto.getEquipo());
    Assertions.assertThat(responseData.getActivo()).isEqualTo(Boolean.TRUE);
  }

  @Test
  public void enable_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    RolProyecto rolProyecto = generarMockRolProyecto(1L, Boolean.FALSE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: enable non existing RolProyecto
        () -> service.disable(rolProyecto.getId()))
        // then: NotFoundException is thrown
        .isInstanceOf(RolProyectoNotFoundException.class);
  }

  @Test
  public void enable_WithDuplicatedAbreviatura_ThrowsIllegalArgumentException() throws Exception {
    // given: a RolProyecto with duplicated Abreviatura
    RolProyecto rolProyectoExistente = generarMockRolProyecto(2L);
    rolProyectoExistente.setAbreviatura("ABR");
    RolProyecto rolProyecto = generarMockRolProyecto(1L);
    rolProyecto.setActivo(Boolean.FALSE);
    rolProyecto.setAbreviatura("ABR");

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(rolProyecto));

    BDDMockito.given(repository.findByAbreviaturaAndActivoIsTrue(ArgumentMatchers.<String>any()))
        .willReturn(Optional.of(rolProyectoExistente));

    Assertions.assertThatThrownBy(
        // when: enable RolProyecto
        () -> service.enable(rolProyecto.getId()))
        // then: throw exception as Abreviatura is duplicated
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un RolProyecto activo con la abreviatura '%s'", rolProyecto.getAbreviatura());
  }

  @Test
  public void enable_WithDuplicatedNombre_ThrowsIllegalArgumentException() throws Exception {
    // given: a RolProyecto with duplicated Nombre
    RolProyecto rolProyectoExistente = generarMockRolProyecto(2L);
    rolProyectoExistente.setNombre("nombre");
    RolProyecto rolProyecto = generarMockRolProyecto(1L, Boolean.FALSE);
    rolProyecto.setNombre("nombre");

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(rolProyecto));

    BDDMockito.given(repository.findByNombreAndActivoIsTrue(ArgumentMatchers.<String>any()))
        .willReturn(Optional.of(rolProyectoExistente));

    Assertions.assertThatThrownBy(
        // when: enable RolProyecto
        () -> service.enable(rolProyecto.getId()))
        // then: throw exception as Nombre is duplicated
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Ya existe un RolProyecto activo con el nombre '%s'", rolProyecto.getNombre());
  }

  @Test
  public void disable_WithExistingId_ReturnsRolProyecto() {
    // given: existing RolProyecto
    RolProyecto rolProyecto = generarMockRolProyecto(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(rolProyecto));

    BDDMockito.given(repository.save(ArgumentMatchers.<RolProyecto>any())).willAnswer(new Answer<RolProyecto>() {
      @Override
      public RolProyecto answer(InvocationOnMock invocation) throws Throwable {
        RolProyecto givenData = invocation.getArgument(0, RolProyecto.class);
        givenData.setActivo(Boolean.FALSE);
        return givenData;
      }
    });

    // when: disable RolProyecto
    RolProyecto responseData = service.disable(rolProyecto.getId());

    // then: RolProyecto is disabled
    Assertions.assertThat(responseData).isNotNull();
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(rolProyecto.getId());
    Assertions.assertThat(responseData.getAbreviatura()).as("getAbreviatura()").isEqualTo(rolProyecto.getAbreviatura());
    Assertions.assertThat(responseData.getNombre()).as("getNombre()").isEqualTo(rolProyecto.getNombre());
    Assertions.assertThat(responseData.getDescripcion()).as("getDescripcion()").isEqualTo(rolProyecto.getDescripcion());
    Assertions.assertThat(responseData.getRolPrincipal()).as("getRolPrincipal()")
        .isEqualTo(rolProyecto.getRolPrincipal());
    Assertions.assertThat(responseData.getResponsableEconomico()).as("getResponsableEconomico()")
        .isEqualTo(rolProyecto.getResponsableEconomico());
    Assertions.assertThat(responseData.getEquipo()).as("getEquipo()").isEqualTo(rolProyecto.getEquipo());
    Assertions.assertThat(responseData.getActivo()).isEqualTo(Boolean.FALSE);
  }

  @Test
  public void disable_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    RolProyecto rolProyecto = generarMockRolProyecto(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: disable non existing RolProyecto
        () -> service.disable(rolProyecto.getId()))
        // then: NotFoundException is thrown
        .isInstanceOf(RolProyectoNotFoundException.class);
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
  public void findById_WithExistingId_ReturnsRolProyecto() throws Exception {
    // given: existing RolProyecto
    RolProyecto rolProyectoExistente = generarMockRolProyecto(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(rolProyectoExistente));

    // when: find by id RolProyecto
    RolProyecto responseData = service.findById(rolProyectoExistente.getId());

    // then: returns RolProyecto
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(responseData.getId());
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getAbreviatura()).as("getAbreviatura()")
        .isEqualTo(responseData.getAbreviatura());
    Assertions.assertThat(responseData.getNombre()).as("getNombre()").isEqualTo(responseData.getNombre());
    Assertions.assertThat(responseData.getDescripcion()).as("getDescripcion()")
        .isEqualTo(responseData.getDescripcion());
    Assertions.assertThat(responseData.getRolPrincipal()).as("getRolPrincipal()")
        .isEqualTo(responseData.getRolPrincipal());
    Assertions.assertThat(responseData.getResponsableEconomico()).as("getResponsableEconomico()")
        .isEqualTo(responseData.getResponsableEconomico());
    Assertions.assertThat(responseData.getEquipo()).as("getEquipo()").isEqualTo(responseData.getEquipo());
    Assertions.assertThat(responseData.getActivo()).as("getActivo()").isEqualTo(rolProyectoExistente.getActivo());
  }

  @Test
  public void findById_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: find by non existing id
        () -> service.findById(1L))
        // then: NotFoundException is thrown
        .isInstanceOf(RolProyectoNotFoundException.class);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred RolProyecto
    List<RolProyecto> rolProyectos = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      if (i % 2 == 0) {
        rolProyectos.add(generarMockRolProyecto(Long.valueOf(i)));
      }
    }

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<RolProyecto>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<RolProyecto>>() {
          @Override
          public Page<RolProyecto> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<RolProyecto> content = rolProyectos.subList(fromIndex, toIndex);
            Page<RolProyecto> page = new PageImpl<>(content, pageable, rolProyectos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<RolProyecto> page = service.findAll(null, paging);

    // then: A Page with ten RolProyecto are returned
    // containing Abreviatura='062' to '080'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(50);

    for (int i = 0, j = 62; i < 10; i++, j += 2) {
      RolProyecto item = page.getContent().get(i);
      Assertions.assertThat(item.getAbreviatura()).isEqualTo(String.format("%03d", j));
      Assertions.assertThat(item.getActivo()).isEqualTo(Boolean.TRUE);
    }
  }

  @Test
  public void findAllTodos_WithPaging_ReturnsPage() {
    // given: One hundred RolProyecto
    List<RolProyecto> rolProyectos = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      rolProyectos.add(generarMockRolProyecto(Long.valueOf(i), (i % 2 == 0) ? Boolean.TRUE : Boolean.FALSE));
    }

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<RolProyecto>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<RolProyecto>>() {
          @Override
          public Page<RolProyecto> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<RolProyecto> content = rolProyectos.subList(fromIndex, toIndex);
            Page<RolProyecto> page = new PageImpl<>(content, pageable, rolProyectos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<RolProyecto> page = service.findAll(null, paging);

    // then: A Page with ten RolProyecto are returned containing
    // Nombre='nombre-31' to
    // 'nombre-40'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      RolProyecto item = page.getContent().get(i);
      Assertions.assertThat(item.getAbreviatura()).isEqualTo(String.format("%03d", j));
      Assertions.assertThat(item.getActivo()).isEqualTo((j % 2 == 0 ? Boolean.TRUE : Boolean.FALSE));
    }
  }

  /**
   * Función que genera RolProyecto
   * 
   * @param rolProyectoId
   * @return el rolProyecto
   */
  private RolProyecto generarMockRolProyecto(Long rolProyectoId) {

    String suffix = String.format("%03d", rolProyectoId);

    // @formatter:off
    RolProyecto rolProyecto = RolProyecto.builder()
        .id(rolProyectoId)
        .abreviatura(suffix)
        .nombre("nombre-" + suffix)
        .descripcion("descripcion-" + suffix)
        .rolPrincipal(Boolean.FALSE)
        .responsableEconomico(Boolean.FALSE)
        .equipo(RolProyecto.Equipo.INVESTIGACION)
        .activo(Boolean.TRUE)
        .build();
    // @formatter:on

    return rolProyecto;
  }

  /**
   * Función que genera RolProyecto con el estado indicado
   * 
   * @param rolProyectoId
   * @param activo
   * @return el rolProyecto
   */
  private RolProyecto generarMockRolProyecto(Long rolProyectoId, Boolean activo) {

    RolProyecto rolProyecto = generarMockRolProyecto(rolProyectoId);
    rolProyecto.setActivo(activo);

    return rolProyecto;
  }

}
