package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.TipoConfidencialidadNotFoundException;
import org.crue.hercules.sgi.csp.model.TipoConfidencialidad;
import org.crue.hercules.sgi.csp.model.TipoConfidencialidadNombre;
import org.crue.hercules.sgi.csp.repository.TipoConfidencialidadRepository;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Import({ TipoConfidencialidadService.class })
class TipoConfidencialidadServiceTest extends BaseServiceTest {

  @MockBean
  private TipoConfidencialidadRepository repository;

  @MockBean
  private EntityManager entityManager;

  @MockBean
  private EntityManagerFactory entityManagerFactory;

  @MockBean
  private PersistenceUnitUtil persistenceUnitUtil;

  @Autowired
  private TipoConfidencialidadService service;

  @BeforeEach
  void setUp() {
    BDDMockito.given(entityManagerFactory.getPersistenceUnitUtil()).willReturn(persistenceUnitUtil);
    BDDMockito.given(entityManager.getEntityManagerFactory()).willReturn(entityManagerFactory);
  }

  @Test
  void create_ReturnsTipoConfidencialidad() {
    // given: nuevo TipoConfidencialidad
    TipoConfidencialidad data = generarMockTipoConfidencialidad(null, Boolean.TRUE);

    BDDMockito.given(repository.save(ArgumentMatchers.<TipoConfidencialidad>any()))
        .willAnswer(invocation -> {
          TipoConfidencialidad entity = invocation.getArgument(0, TipoConfidencialidad.class);
          entity.setId(1L);
          return entity;
        });

    // when: se crea el TipoConfidencialidad
    TipoConfidencialidad created = service.create(data);

    // then: el nuevo TipoConfidencialidad es creado
    Assertions.assertThat(created).isNotNull();
    Assertions.assertThat(created.getId()).isNotNull();
    Assertions.assertThat(created.getNombre()).isEqualTo(data.getNombre());
    Assertions.assertThat(created.getActivo()).isTrue();
  }

  @Test
  void create_WithId_ThrowsIllegalArgumentException() {
    // given: un TipoConfidencialidad con id informado
    TipoConfidencialidad data = generarMockTipoConfidencialidad(1L, Boolean.TRUE);

    Assertions.assertThatThrownBy(
        // when: se crea el TipoConfidencialidad
        () -> service.create(data))
        // then: se lanza excepción porque el id no puede ser informado
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Identificador de Tipo de Confidencialidad debe ser nulo");
  }

  @Test
  void create_WithDuplicatedNombre_ThrowsConstraintViolationException() {
    // given: un TipoConfidencialidad con nombre duplicado
    TipoConfidencialidad givenData = generarMockTipoConfidencialidad(1L, Boolean.TRUE);
    TipoConfidencialidad newData = TipoConfidencialidad.builder()
        .nombre(new HashSet<>(givenData.getNombre()))
        .activo(givenData.getActivo())
        .build();

    BDDMockito
        .given(repository.findByNombreLangAndNombreValueAndActivoIsTrue(
            ArgumentMatchers.<Language>any(), ArgumentMatchers.anyString()))
        .willReturn(Optional.of(givenData));

    Assertions.assertThatThrownBy(
        // when: se crea el TipoConfidencialidad
        () -> service.create(newData))
        // then: se lanza excepción porque el nombre ya existe
        .isInstanceOf(ConstraintViolationException.class)
        .hasMessageContaining("Ya existe un tipo de confidencialidad con el nombre 'nombre-1'");
  }

  @Test
  void update_WithExistingId_ReturnsTipoConfidencialidad() {
    // given: un TipoConfidencialidad existente
    TipoConfidencialidad data = generarMockTipoConfidencialidad(1L, Boolean.TRUE);

    mockActivableIsActivo(TipoConfidencialidad.class, data);
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(data));

    BDDMockito.given(repository.save(ArgumentMatchers.<TipoConfidencialidad>any()))
        .willAnswer(new Answer<TipoConfidencialidad>() {
          @Override
          public TipoConfidencialidad answer(InvocationOnMock invocation) throws Throwable {
            TipoConfidencialidad givenData = invocation.getArgument(0, TipoConfidencialidad.class);
            Set<TipoConfidencialidadNombre> nombre = new HashSet<>();
            nombre.add(new TipoConfidencialidadNombre(Language.ES, "Nombre-Modificado"));
            givenData.setNombre(nombre);
            return givenData;
          }
        });

    // when: se actualiza el TipoConfidencialidad
    TipoConfidencialidad updated = service.update(data);

    // then: el TipoConfidencialidad es actualizado
    Assertions.assertThat(updated).isNotNull();
    Assertions.assertThat(updated.getId()).isEqualTo(data.getId());
    Assertions.assertThat(updated.getActivo()).isEqualTo(data.getActivo());
  }

  @Test
  void update_WithNoExistingId_ThrowsConstraintViolationException() {
    // given: un TipoConfidencialidad con un id que no existe en BD
    TipoConfidencialidad data = generarMockTipoConfidencialidad(1L, Boolean.TRUE);

    mockActivableIsActivo(TipoConfidencialidad.class, null);

    Assertions.assertThatThrownBy(
        // when: se actualiza un TipoConfidencialidad inexistente
        () -> service.update(data))
        // then: @ActivableIsActivo lanza ConstraintViolationException
        .isInstanceOf(ConstraintViolationException.class);
  }

  @Test
  void update_WithoutId_ThrowsValidationException() {
    // given: un TipoConfidencialidad sin id informado
    TipoConfidencialidad data = generarMockTipoConfidencialidad(null, Boolean.TRUE);

    mockActivableIsActivo(TipoConfidencialidad.class, null);

    Assertions.assertThatThrownBy(
        // when: se actualiza el TipoConfidencialidad
        () -> service.update(data))
        // then: ValidationException envuelve la IllegalArgumentException del
        // ActivableIsActivoValidator
        .isInstanceOf(ValidationException.class);
  }

  @Test
  void findById_WithExistingId_ReturnsTipoConfidencialidad() {
    // given: un TipoConfidencialidad existente
    TipoConfidencialidad data = generarMockTipoConfidencialidad(1L, Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(data));

    // when: se busca el TipoConfidencialidad por id
    TipoConfidencialidad found = service.findById(1L);

    // then: se devuelve el TipoConfidencialidad con el id indicado
    Assertions.assertThat(found).isNotNull();
    Assertions.assertThat(found.getId()).isEqualTo(1L);
    Assertions.assertThat(found.getNombre()).isEqualTo(data.getNombre());
    Assertions.assertThat(found.getActivo()).isEqualTo(data.getActivo());
  }

  @Test
  void findById_WithNoExistingId_ThrowsNotFoundException() {
    // given: un id inexistente
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: se busca el TipoConfidencialidad por id
        () -> service.findById(1L))
        // then: se lanza NotFoundException
        .isInstanceOf(TipoConfidencialidadNotFoundException.class);
  }

  @Test
  void enable_ReturnsTipoConfidencialidad() {
    // given: un TipoConfidencialidad inactivo
    TipoConfidencialidad tipoConfidencialidad = generarMockTipoConfidencialidad(1L, Boolean.FALSE);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(tipoConfidencialidad));
    BDDMockito.given(repository.save(ArgumentMatchers.<TipoConfidencialidad>any()))
        .willAnswer(new Answer<TipoConfidencialidad>() {
          @Override
          public TipoConfidencialidad answer(InvocationOnMock invocation) throws Throwable {
            TipoConfidencialidad givenData = invocation.getArgument(0, TipoConfidencialidad.class);
            givenData.setActivo(Boolean.TRUE);
            return givenData;
          }
        });

    // when: se activa el TipoConfidencialidad
    TipoConfidencialidad updated = service.enable(tipoConfidencialidad.getId());

    // then: el TipoConfidencialidad está activo
    Assertions.assertThat(updated).isNotNull();
    Assertions.assertThat(updated.getId()).isEqualTo(1L);
    Assertions.assertThat(updated.getActivo()).isTrue();
  }

  @Test
  void enable_WithNoExistingId_ThrowsNotFoundException() {
    // given: un id inexistente
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: se activa un TipoConfidencialidad inexistente
        () -> service.enable(1L))
        // then: se lanza NotFoundException
        .isInstanceOf(TipoConfidencialidadNotFoundException.class);
  }

  @Test
  void enable_WithDuplicatedNombre_ThrowsConstraintViolationException() {
    // given: un TipoConfidencialidad inactivo con nombre ya usado por otro activo
    TipoConfidencialidad tipoExistente = generarMockTipoConfidencialidad(2L, Boolean.TRUE);
    TipoConfidencialidad tipoConfidencialidad = generarMockTipoConfidencialidad(1L, Boolean.FALSE);
    Long id = tipoConfidencialidad.getId();

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(tipoConfidencialidad));
    BDDMockito.given(repository.findByNombreLangAndNombreValueAndActivoIsTrue(
        ArgumentMatchers.<Language>any(), ArgumentMatchers.anyString()))
        .willReturn(Optional.of(tipoExistente));

    Assertions.assertThatThrownBy(
        // when: se activa el TipoConfidencialidad
        () -> service.enable(id))
        // then: se lanza ConstraintViolationException
        .isInstanceOf(ConstraintViolationException.class)
        .hasMessageContaining("Ya existe un tipo de confidencialidad con el nombre 'nombre-1'");
  }

  @Test
  void disable_ReturnsTipoConfidencialidad() {
    // given: un TipoConfidencialidad activo
    TipoConfidencialidad tipoConfidencialidad = generarMockTipoConfidencialidad(1L, Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(tipoConfidencialidad));
    BDDMockito.given(repository.save(ArgumentMatchers.<TipoConfidencialidad>any()))
        .willAnswer(new Answer<TipoConfidencialidad>() {
          @Override
          public TipoConfidencialidad answer(InvocationOnMock invocation) throws Throwable {
            TipoConfidencialidad givenData = invocation.getArgument(0, TipoConfidencialidad.class);
            givenData.setActivo(Boolean.FALSE);
            return givenData;
          }
        });

    // when: se desactiva el TipoConfidencialidad
    TipoConfidencialidad updated = service.disable(tipoConfidencialidad.getId());

    // then: el TipoConfidencialidad está inactivo
    Assertions.assertThat(updated).isNotNull();
    Assertions.assertThat(updated.getId()).isEqualTo(1L);
    Assertions.assertThat(updated.getActivo()).isFalse();
  }

  @Test
  void disable_WithNoExistingId_ThrowsNotFoundException() {
    // given: un id inexistente
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: se desactiva un TipoConfidencialidad inexistente
        () -> service.disable(1L))
        // then: se lanza NotFoundException
        .isInstanceOf(TipoConfidencialidadNotFoundException.class);
  }

  @Test
  void findAll_WithPaging_ReturnsPage() {
    // given: cien TipoConfidencialidad
    List<TipoConfidencialidad> data = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      data.add(generarMockTipoConfidencialidad(Long.valueOf(i), Boolean.TRUE));
    }

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<TipoConfidencialidad>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoConfidencialidad>>() {
          @Override
          public Page<TipoConfidencialidad> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoConfidencialidad> content = data.subList(fromIndex, toIndex);
            return new PageImpl<>(content, pageable, data.size());
          }
        });

    // when: se obtiene la página 3 con tamaño 10
    Pageable paging = PageRequest.of(3, 10);
    Page<TipoConfidencialidad> page = service.findAll(null, paging);

    // then: se devuelve una página con diez TipoConfidencialidad
    Assertions.assertThat(page.getContent()).hasSize(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  /**
   * Genera un mock de {@link TipoConfidencialidad}.
   *
   * @param id     identificador.
   * @param activo estado activo.
   * @return {@link TipoConfidencialidad} mock.
   */
  private TipoConfidencialidad generarMockTipoConfidencialidad(Long id, Boolean activo) {
    Set<TipoConfidencialidadNombre> nombre = new HashSet<>();
    nombre.add(new TipoConfidencialidadNombre(Language.ES, "nombre-" + id));

    return TipoConfidencialidad.builder()
        .id(id)
        .nombre(nombre)
        .activo(activo)
        .build();
  }

  /**
   * Simula las llamadas JPA internas del validador {@code @ActivableIsActivo}.
   *
   * @param clazz  clase de la entidad validada.
   * @param object entidad que se valida.
   */
  private <T> void mockActivableIsActivo(Class<T> clazz, T object) {
    BDDMockito.given(persistenceUnitUtil.getIdentifier(ArgumentMatchers.any(clazz)))
        .willAnswer((InvocationOnMock invocation) -> {
          Object arg0 = invocation.getArgument(0);
          if (arg0 == null) {
            return null;
          }
          BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(arg0);
          return wrapper.getPropertyValue("id");
        });
    BDDMockito.given(entityManager.find(ArgumentMatchers.eq(clazz), ArgumentMatchers.anyLong())).willReturn(object);
  }

}
