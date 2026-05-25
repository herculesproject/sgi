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
import org.crue.hercules.sgi.csp.exceptions.TipoDescriptorGrupoNotFoundException;
import org.crue.hercules.sgi.csp.model.TipoDescriptorGrupo;
import org.crue.hercules.sgi.csp.model.TipoDescriptorGrupoNombre;
import org.crue.hercules.sgi.csp.repository.TipoDescriptorGrupoRepository;
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

@Import({ TipoDescriptorGrupoService.class })
class TipoDescriptorGrupoServiceTest extends BaseServiceTest {

  @MockBean
  private TipoDescriptorGrupoRepository repository;

  @MockBean
  private EntityManager entityManager;

  @MockBean
  private EntityManagerFactory entityManagerFactory;

  @MockBean
  private PersistenceUnitUtil persistenceUnitUtil;

  @Autowired
  private TipoDescriptorGrupoService service;

  @BeforeEach
  void setUp() {
    BDDMockito.given(entityManagerFactory.getPersistenceUnitUtil()).willReturn(persistenceUnitUtil);
    BDDMockito.given(entityManager.getEntityManagerFactory()).willReturn(entityManagerFactory);
  }

  @Test
  void create_ReturnsTipoDescriptorGrupo() {
    // given: nuevo TipoDescriptorGrupo
    TipoDescriptorGrupo data = generarMockTipoDescriptorGrupo(null, Boolean.TRUE);

    BDDMockito.given(repository.save(ArgumentMatchers.<TipoDescriptorGrupo>any()))
        .willAnswer(invocation -> {
          TipoDescriptorGrupo entity = invocation.getArgument(0, TipoDescriptorGrupo.class);
          entity.setId(1L);
          return entity;
        });

    // when: se crea el TipoDescriptorGrupo
    TipoDescriptorGrupo created = service.create(data);

    // then: el nuevo TipoDescriptorGrupo es creado
    Assertions.assertThat(created).isNotNull();
    Assertions.assertThat(created.getId()).isNotNull();
    Assertions.assertThat(created.getNombre()).isEqualTo(data.getNombre());
    Assertions.assertThat(created.getActivo()).isTrue();
  }

  @Test
  void create_WithId_ThrowsIllegalArgumentException() {
    // given: un TipoDescriptorGrupo con id informado
    TipoDescriptorGrupo data = generarMockTipoDescriptorGrupo(1L, Boolean.TRUE);

    Assertions.assertThatThrownBy(
        // when: se crea el TipoDescriptorGrupo
        () -> service.create(data))
        // then: se lanza excepción porque el id no puede ser informado
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Identificador de Tipo Descriptor de Grupo debe ser nulo");
  }

  @Test
  void create_WithDuplicatedNombre_ThrowsConstraintViolationException() {
    // given: un TipoDescriptorGrupo con nombre duplicado
    TipoDescriptorGrupo givenData = generarMockTipoDescriptorGrupo(1L, Boolean.TRUE);
    TipoDescriptorGrupo newData = TipoDescriptorGrupo.builder()
        .nombre(new HashSet<>(givenData.getNombre()))
        .activo(givenData.getActivo())
        .build();

    BDDMockito
        .given(repository.findByNombreLangAndNombreValueAndActivoIsTrue(
            ArgumentMatchers.<Language>any(), ArgumentMatchers.anyString()))
        .willReturn(Optional.of(givenData));

    Assertions.assertThatThrownBy(
        // when: se crea el TipoDescriptorGrupo
        () -> service.create(newData))
        // then: se lanza excepción porque el nombre ya existe
        .isInstanceOf(ConstraintViolationException.class)
        .hasMessageContaining("Ya existe un tipo de descriptor de grupo con el nombre 'nombre-1'");
  }

  @Test
  void update_WithExistingId_ReturnsTipoDescriptorGrupo() {
    // given: un TipoDescriptorGrupo existente
    TipoDescriptorGrupo data = generarMockTipoDescriptorGrupo(1L, Boolean.TRUE);

    mockActivableIsActivo(TipoDescriptorGrupo.class, data);
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(data));

    BDDMockito.given(repository.save(ArgumentMatchers.<TipoDescriptorGrupo>any()))
        .willAnswer(new Answer<TipoDescriptorGrupo>() {
          @Override
          public TipoDescriptorGrupo answer(InvocationOnMock invocation) throws Throwable {
            TipoDescriptorGrupo givenData = invocation.getArgument(0, TipoDescriptorGrupo.class);
            Set<TipoDescriptorGrupoNombre> nombre = new HashSet<>();
            nombre.add(new TipoDescriptorGrupoNombre(Language.ES, "Nombre-Modificado"));
            givenData.setNombre(nombre);
            return givenData;
          }
        });

    // when: se actualiza el TipoDescriptorGrupo
    TipoDescriptorGrupo updated = service.update(data);

    // then: el TipoDescriptorGrupo es actualizado
    Assertions.assertThat(updated).isNotNull();
    Assertions.assertThat(updated.getId()).isEqualTo(data.getId());
    Assertions.assertThat(updated.getActivo()).isEqualTo(data.getActivo());
  }

  @Test
  void update_WithNoExistingId_ThrowsConstraintViolationException() {
    // given: un TipoDescriptorGrupo con un id que no existe en BD
    TipoDescriptorGrupo data = generarMockTipoDescriptorGrupo(1L, Boolean.TRUE);

    // @ActivableIsActivo valida antes del cuerpo del servicio: entidad no
    // encontrada → constraint violation
    mockActivableIsActivo(TipoDescriptorGrupo.class, null);

    Assertions.assertThatThrownBy(
        // when: se actualiza un TipoDescriptorGrupo inexistente
        () -> service.update(data))
        // then: @ActivableIsActivo lanza ConstraintViolationException antes de llegar
        // al cuerpo del servicio
        .isInstanceOf(ConstraintViolationException.class);
  }

  @Test
  void update_WithoutId_ThrowsValidationException() {
    // given: un TipoDescriptorGrupo sin id informado
    TipoDescriptorGrupo data = generarMockTipoDescriptorGrupo(null, Boolean.TRUE);

    // @ActivableIsActivo se ejecuta antes del cuerpo del servicio: un id nulo causa
    // estado ilegal en el validador
    mockActivableIsActivo(TipoDescriptorGrupo.class, null);

    Assertions.assertThatThrownBy(
        // when: se actualiza el TipoDescriptorGrupo
        () -> service.update(data))
        // then: ValidationException envuelve la IllegalArgumentException del
        // ActivableIsActivoValidator
        .isInstanceOf(ValidationException.class);
  }

  @Test
  void enable_ReturnsTipoDescriptorGrupo() {
    // given: un TipoDescriptorGrupo inactivo
    TipoDescriptorGrupo tipoDescriptorGrupo = generarMockTipoDescriptorGrupo(1L, Boolean.FALSE);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(tipoDescriptorGrupo));
    BDDMockito.given(repository.save(ArgumentMatchers.<TipoDescriptorGrupo>any()))
        .willAnswer(new Answer<TipoDescriptorGrupo>() {
          @Override
          public TipoDescriptorGrupo answer(InvocationOnMock invocation) throws Throwable {
            TipoDescriptorGrupo givenData = invocation.getArgument(0, TipoDescriptorGrupo.class);
            givenData.setActivo(Boolean.TRUE);
            return givenData;
          }
        });

    // when: se activa el TipoDescriptorGrupo
    TipoDescriptorGrupo updated = service.enable(tipoDescriptorGrupo.getId());

    // then: el TipoDescriptorGrupo está activo
    Assertions.assertThat(updated).isNotNull();
    Assertions.assertThat(updated.getId()).isEqualTo(1L);
    Assertions.assertThat(updated.getActivo()).isTrue();
  }

  @Test
  void enable_WithNoExistingId_ThrowsNotFoundException() {
    // given: un id inexistente
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: se activa un TipoDescriptorGrupo inexistente
        () -> service.enable(1L))
        // then: se lanza NotFoundException
        .isInstanceOf(TipoDescriptorGrupoNotFoundException.class);
  }

  @Test
  void enable_WithDuplicatedNombre_ThrowsConstraintViolationException() {
    // given: un TipoDescriptorGrupo inactivo con un nombre ya usado por otro activo
    TipoDescriptorGrupo tipoDescriptorGrupoExistente = generarMockTipoDescriptorGrupo(2L, Boolean.TRUE);
    TipoDescriptorGrupo tipoDescriptorGrupo = generarMockTipoDescriptorGrupo(1L, Boolean.FALSE);
    Long id = tipoDescriptorGrupo.getId();

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(tipoDescriptorGrupo));
    BDDMockito.given(repository.findByNombreLangAndNombreValueAndActivoIsTrue(
        ArgumentMatchers.<Language>any(), ArgumentMatchers.anyString()))
        .willReturn(Optional.of(tipoDescriptorGrupoExistente));

    Assertions.assertThatThrownBy(
        // when: se activa el TipoDescriptorGrupo
        () -> service.enable(id))
        // then: se lanza ConstraintViolationException
        .isInstanceOf(ConstraintViolationException.class)
        .hasMessageContaining("Ya existe un tipo de descriptor de grupo con el nombre 'nombre-1'");
  }

  @Test
  void disable_ReturnsTipoDescriptorGrupo() {
    // given: un TipoDescriptorGrupo activo
    TipoDescriptorGrupo tipoDescriptorGrupo = generarMockTipoDescriptorGrupo(1L, Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(tipoDescriptorGrupo));
    BDDMockito.given(repository.save(ArgumentMatchers.<TipoDescriptorGrupo>any()))
        .willAnswer(new Answer<TipoDescriptorGrupo>() {
          @Override
          public TipoDescriptorGrupo answer(InvocationOnMock invocation) throws Throwable {
            TipoDescriptorGrupo givenData = invocation.getArgument(0, TipoDescriptorGrupo.class);
            givenData.setActivo(Boolean.FALSE);
            return givenData;
          }
        });

    // when: se desactiva el TipoDescriptorGrupo
    TipoDescriptorGrupo updated = service.disable(tipoDescriptorGrupo.getId());

    // then: el TipoDescriptorGrupo está inactivo
    Assertions.assertThat(updated).isNotNull();
    Assertions.assertThat(updated.getId()).isEqualTo(1L);
    Assertions.assertThat(updated.getActivo()).isFalse();
  }

  @Test
  void disable_WithNoExistingId_ThrowsNotFoundException() {
    // given: un id inexistente
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: se desactiva un TipoDescriptorGrupo inexistente
        () -> service.disable(1L))
        // then: se lanza NotFoundException
        .isInstanceOf(TipoDescriptorGrupoNotFoundException.class);
  }

  @Test
  void findById_WithExistingId_ReturnsTipoDescriptorGrupo() {
    // given: un TipoDescriptorGrupo existente
    TipoDescriptorGrupo givenData = generarMockTipoDescriptorGrupo(1L, Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(givenData));

    // when: se busca por id
    TipoDescriptorGrupo data = service.findById(givenData.getId());

    // then: devuelve el TipoDescriptorGrupo
    Assertions.assertThat(data).isNotNull();
    Assertions.assertThat(data.getId()).isEqualTo(givenData.getId());
    Assertions.assertThat(data.getNombre()).isEqualTo(givenData.getNombre());
    Assertions.assertThat(data.getActivo()).isEqualTo(givenData.getActivo());
  }

  @Test
  void findById_WithNoExistingId_ThrowsNotFoundException() {
    // given: un id inexistente
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: se busca por un id inexistente
        () -> service.findById(1L))
        // then: se lanza NotFoundException
        .isInstanceOf(TipoDescriptorGrupoNotFoundException.class);
  }

  @Test
  void findAll_WithPaging_ReturnsPage() {
    // given: cien TipoDescriptorGrupo
    List<TipoDescriptorGrupo> data = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      data.add(generarMockTipoDescriptorGrupo(Long.valueOf(i), Boolean.TRUE));
    }

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<TipoDescriptorGrupo>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoDescriptorGrupo>>() {
          @Override
          public Page<TipoDescriptorGrupo> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoDescriptorGrupo> content = data.subList(fromIndex, toIndex);
            return new PageImpl<>(content, pageable, data.size());
          }
        });

    // when: se obtiene la página 3 con tamaño 10
    Pageable paging = PageRequest.of(3, 10);
    Page<TipoDescriptorGrupo> page = service.findAll(null, paging);

    // then: se devuelve una página con diez TipoDescriptorGrupo
    Assertions.assertThat(page.getContent()).hasSize(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  /**
   * Genera un mock de {@link TipoDescriptorGrupo}.
   *
   * @param id     identificador.
   * @param activo estado activo.
   * @return {@link TipoDescriptorGrupo} mock.
   */
  private TipoDescriptorGrupo generarMockTipoDescriptorGrupo(Long id, Boolean activo) {
    Set<TipoDescriptorGrupoNombre> nombre = new HashSet<>();
    nombre.add(new TipoDescriptorGrupoNombre(Language.ES, "nombre-" + id));

    return TipoDescriptorGrupo.builder()
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
