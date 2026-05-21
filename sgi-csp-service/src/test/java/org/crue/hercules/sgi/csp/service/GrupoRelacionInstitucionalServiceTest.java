package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.ConstraintViolationException;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.GrupoRelacionInstitucionalNotFoundException;
import org.crue.hercules.sgi.csp.model.GrupoRelacionInstitucional;
import org.crue.hercules.sgi.csp.repository.GrupoRelacionInstitucionalRepository;
import org.crue.hercules.sgi.csp.util.GrupoAuthorityHelper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Import({ GrupoRelacionInstitucionalService.class })
class GrupoRelacionInstitucionalServiceTest extends BaseServiceTest {

  @MockBean
  private GrupoRelacionInstitucionalRepository repository;

  @MockBean
  private GrupoAuthorityHelper authorityHelper;

  @Autowired
  private GrupoRelacionInstitucionalService service;

  @Test
  void create_WithEntidadRef_ReturnsGrupoRelacionInstitucional() {
    // given: nuevo GrupoRelacionInstitucional con entidadRef
    GrupoRelacionInstitucional data = generarMockConEntidadRef(null, 1L, "ENT-001");

    BDDMockito.given(repository.save(ArgumentMatchers.<GrupoRelacionInstitucional>any()))
        .willAnswer(new Answer<GrupoRelacionInstitucional>() {
          @Override
          public GrupoRelacionInstitucional answer(InvocationOnMock invocation) throws Throwable {
            GrupoRelacionInstitucional entity = invocation.getArgument(0, GrupoRelacionInstitucional.class);
            entity.setId(1L);
            return entity;
          }
        });

    // when: se crea
    GrupoRelacionInstitucional created = service.create(data);

    // then: se devuelve el nuevo GrupoRelacionInstitucional con id
    Assertions.assertThat(created).isNotNull();
    Assertions.assertThat(created.getId()).isEqualTo(1L);
    Assertions.assertThat(created.getGrupoId()).isEqualTo(data.getGrupoId());
    Assertions.assertThat(created.getEntidadRef()).isEqualTo("ENT-001");
    Assertions.assertThat(created.getInstitucion()).isNull();
  }

  @Test
  void create_WithInstitucion_ReturnsGrupoRelacionInstitucional() {
    // given: nuevo GrupoRelacionInstitucional con institucion manual
    GrupoRelacionInstitucional data = generarMockConInstitucion(null, 1L, "Universidad de Ejemplo");

    BDDMockito.given(repository.save(ArgumentMatchers.<GrupoRelacionInstitucional>any()))
        .willAnswer(new Answer<GrupoRelacionInstitucional>() {
          @Override
          public GrupoRelacionInstitucional answer(InvocationOnMock invocation) throws Throwable {
            GrupoRelacionInstitucional entity = invocation.getArgument(0, GrupoRelacionInstitucional.class);
            entity.setId(2L);
            return entity;
          }
        });

    // when: se crea
    GrupoRelacionInstitucional created = service.create(data);

    // then: se devuelve el nuevo GrupoRelacionInstitucional con institucion
    Assertions.assertThat(created).isNotNull();
    Assertions.assertThat(created.getId()).isEqualTo(2L);
    Assertions.assertThat(created.getInstitucion()).isEqualTo("Universidad de Ejemplo");
    Assertions.assertThat(created.getEntidadRef()).isNull();
  }

  @Test
  void create_WithId_ThrowsIllegalArgumentException() {
    // given: GrupoRelacionInstitucional con id informado
    GrupoRelacionInstitucional data = generarMockConEntidadRef(1L, 1L, "ENT-001");

    Assertions.assertThatThrownBy(
        // when: se intenta crear
        () -> service.create(data))
        // then: se lanza excepción porque el id debe ser nulo
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Identificador de Relación institucional debe ser nulo");
  }

  @Test
  void create_WithBothEntidadRefAndInstitucion_ThrowsConstraintViolationException() {
    // given: GrupoRelacionInstitucional con ambos campos informados
    GrupoRelacionInstitucional data = GrupoRelacionInstitucional.builder()
        .grupoId(1L)
        .entidadRef("ENT-001")
        .institucion("Otra")
        .build();

    Assertions.assertThatThrownBy(
        // when: se intenta crear
        () -> service.create(data))
        // then: se lanza excepción por la restricción XOR de la entidad
        .isInstanceOf(ConstraintViolationException.class)
        .hasMessageContaining(
            "La relación institucional debe especificar exactamente uno de los campos 'entidadRef' o 'institucion'");
  }

  @Test
  void create_WithExistingEntidadRefInSameGrupo_ThrowsConstraintViolationException() {
    // given: ya existe otra relación del mismo grupo con la misma entidadRef
    GrupoRelacionInstitucional data = generarMockConEntidadRef(null, 1L, "ENT-001");
    GrupoRelacionInstitucional existing = generarMockConEntidadRef(99L, 1L, "ENT-001");
    BDDMockito.given(repository.findFirstByGrupoIdAndEntidadRef(1L, "ENT-001"))
        .willReturn(Optional.of(existing));

    Assertions.assertThatThrownBy(
        // when: se intenta crear
        () -> service.create(data))
        // then: se lanza excepción por duplicado
        .isInstanceOf(ConstraintViolationException.class)
        .hasMessageContaining("'ENT-001'");
  }

  @Test
  void create_WithExistingInstitucionInSameGrupo_ThrowsConstraintViolationException() {
    // given: ya existe otra relación del mismo grupo con la misma institucion
    GrupoRelacionInstitucional data = generarMockConInstitucion(null, 1L, "Universidad X");
    GrupoRelacionInstitucional existing = generarMockConInstitucion(99L, 1L, "Universidad X");
    BDDMockito.given(repository.findFirstByGrupoIdAndInstitucion(1L, "Universidad X"))
        .willReturn(Optional.of(existing));

    Assertions.assertThatThrownBy(
        // when: se intenta crear
        () -> service.create(data))
        // then: se lanza excepción por duplicado
        .isInstanceOf(ConstraintViolationException.class)
        .hasMessageContaining("'Universidad X'");
  }

  @Test
  void update_WithExistingEntidadRefInSameGrupo_ThrowsConstraintViolationException() {
    // given: otra relación distinta del mismo grupo ya tiene la entidadRef
    GrupoRelacionInstitucional data = generarMockConEntidadRef(1L, 1L, "ENT-001");
    GrupoRelacionInstitucional other = generarMockConEntidadRef(2L, 1L, "ENT-001");
    BDDMockito.given(repository.findFirstByGrupoIdAndEntidadRef(1L, "ENT-001"))
        .willReturn(Optional.of(other));

    Assertions.assertThatThrownBy(
        // when: se intenta actualizar
        () -> service.update(data))
        // then: se lanza excepción por duplicado
        .isInstanceOf(ConstraintViolationException.class)
        .hasMessageContaining("'ENT-001'");
  }

  @Test
  void update_WithSameEntidadRefAsSelf_DoesNotThrow() {
    // given: la entidadRef ya existe pero es la propia relación que se actualiza
    GrupoRelacionInstitucional toUpdate = generarMockConEntidadRef(1L, 1L, "ENT-001");
    BDDMockito.given(repository.findFirstByGrupoIdAndEntidadRef(1L, "ENT-001"))
        .willReturn(Optional.of(toUpdate));
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(toUpdate));
    BDDMockito.given(repository.save(ArgumentMatchers.<GrupoRelacionInstitucional>any()))
        .willAnswer(invocation -> invocation.getArgument(0, GrupoRelacionInstitucional.class));

    // when: se actualiza con la misma entidadRef
    GrupoRelacionInstitucional updated = service.update(toUpdate);

    // then: no se lanza excepción y se devuelve la entidad
    Assertions.assertThat(updated).isNotNull();
    Assertions.assertThat(updated.getEntidadRef()).isEqualTo("ENT-001");
  }

  @Test
  void create_WithNeitherEntidadRefNorInstitucion_ThrowsConstraintViolationException() {
    // given: GrupoRelacionInstitucional sin entidadRef ni institucion
    GrupoRelacionInstitucional data = GrupoRelacionInstitucional.builder()
        .grupoId(1L)
        .build();

    Assertions.assertThatThrownBy(
        // when: se intenta crear
        () -> service.create(data))
        // then: se lanza excepción por la restricción XOR de la entidad
        .isInstanceOf(ConstraintViolationException.class)
        .hasMessageContaining(
            "La relación institucional debe especificar exactamente uno de los campos 'entidadRef' o 'institucion'");
  }

  @Test
  void update_WithBothEntidadRefAndInstitucion_ThrowsConstraintViolationException() {
    // given: GrupoRelacionInstitucional con ambos campos informados
    GrupoRelacionInstitucional data = GrupoRelacionInstitucional.builder()
        .id(1L)
        .grupoId(1L)
        .entidadRef("ENT-001")
        .institucion("Otra")
        .build();

    Assertions.assertThatThrownBy(
        // when: se intenta actualizar
        () -> service.update(data))
        // then: se lanza excepción por la restricción XOR de la entidad
        .isInstanceOf(ConstraintViolationException.class)
        .hasMessageContaining(
            "La relación institucional debe especificar exactamente uno de los campos 'entidadRef' o 'institucion'");
  }

  @Test
  void update_WithNeitherEntidadRefNorInstitucion_ThrowsConstraintViolationException() {
    // given: GrupoRelacionInstitucional sin entidadRef ni institucion
    GrupoRelacionInstitucional data = GrupoRelacionInstitucional.builder()
        .id(1L)
        .grupoId(1L)
        .build();

    Assertions.assertThatThrownBy(
        // when: se intenta actualizar
        () -> service.update(data))
        // then: se lanza excepción por la restricción XOR de la entidad
        .isInstanceOf(ConstraintViolationException.class)
        .hasMessageContaining(
            "La relación institucional debe especificar exactamente uno de los campos 'entidadRef' o 'institucion'");
  }

  @Test
  void update_WithExistingId_ReturnsGrupoRelacionInstitucional() {
    // given: GrupoRelacionInstitucional existente
    GrupoRelacionInstitucional existing = generarMockConEntidadRef(1L, 1L, "ENT-001");
    GrupoRelacionInstitucional toUpdate = generarMockConInstitucion(1L, 1L, "Universidad X");

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(existing));
    BDDMockito.given(repository.save(ArgumentMatchers.<GrupoRelacionInstitucional>any()))
        .willAnswer(new Answer<GrupoRelacionInstitucional>() {
          @Override
          public GrupoRelacionInstitucional answer(InvocationOnMock invocation) throws Throwable {
            return invocation.getArgument(0, GrupoRelacionInstitucional.class);
          }
        });

    // when: se actualiza
    GrupoRelacionInstitucional updated = service.update(toUpdate);

    // then: se actualizan los campos
    Assertions.assertThat(updated).isNotNull();
    Assertions.assertThat(updated.getId()).isEqualTo(1L);
    Assertions.assertThat(updated.getEntidadRef()).isNull();
    Assertions.assertThat(updated.getInstitucion()).isEqualTo("Universidad X");
  }

  @Test
  void update_WithNoExistingId_ThrowsNotFoundException() {
    // given: id inexistente
    GrupoRelacionInstitucional toUpdate = generarMockConEntidadRef(1L, 1L, "ENT-001");
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: se intenta actualizar
        () -> service.update(toUpdate))
        // then: se lanza NotFoundException
        .isInstanceOf(GrupoRelacionInstitucionalNotFoundException.class);
  }

  @Test
  void update_WithoutId_ThrowsIllegalArgumentException() {
    // given: GrupoRelacionInstitucional sin id
    GrupoRelacionInstitucional data = generarMockConEntidadRef(null, 1L, "ENT-001");

    Assertions.assertThatThrownBy(
        // when: se intenta actualizar
        () -> service.update(data))
        // then: se lanza excepción porque el id no puede ser nulo
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Identificador de Relación institucional no puede ser nulo");
  }

  @Test
  void delete_WithExistingId_DeletesGrupoRelacionInstitucional() {
    // given: id existente
    Long id = 1L;
    GrupoRelacionInstitucional existing = generarMockConEntidadRef(id, 1L, "ENT-001");
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(existing));
    BDDMockito.willDoNothing().given(repository).deleteById(ArgumentMatchers.anyLong());

    // when: se elimina
    service.deleteById(id);

    // then: se invoca repository.deleteById
    BDDMockito.then(repository).should().deleteById(id);
  }

  @Test
  void delete_WithNoExistingId_ThrowsNotFoundException() {
    // given: id inexistente
    Long id = 1L;
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: se intenta eliminar
        () -> service.deleteById(id))
        // then: se lanza NotFoundException
        .isInstanceOf(GrupoRelacionInstitucionalNotFoundException.class);
  }

  @Test
  void findById_WithExistingId_ReturnsGrupoRelacionInstitucional() {
    // given: GrupoRelacionInstitucional existente
    GrupoRelacionInstitucional given = generarMockConEntidadRef(1L, 1L, "ENT-001");
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(given));

    // when: se busca por id
    GrupoRelacionInstitucional found = service.findById(1L);

    // then: se devuelve la entidad
    Assertions.assertThat(found).isNotNull();
    Assertions.assertThat(found.getId()).isEqualTo(1L);
    Assertions.assertThat(found.getEntidadRef()).isEqualTo("ENT-001");
  }

  @Test
  void findById_WithNoExistingId_ThrowsNotFoundException() {
    // given: id inexistente
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: se busca por id
        () -> service.findById(1L))
        // then: se lanza NotFoundException
        .isInstanceOf(GrupoRelacionInstitucionalNotFoundException.class);
  }

  @Test
  void findAllByGrupo_WithPaging_ReturnsPage() {
    // given: diez GrupoRelacionInstitucional para un grupo
    Long grupoId = 1L;
    List<GrupoRelacionInstitucional> data = new ArrayList<>();
    for (int i = 1; i <= 10; i++) {
      data.add(generarMockConEntidadRef(Long.valueOf(i), grupoId, "ENT-" + i));
    }

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<GrupoRelacionInstitucional>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<GrupoRelacionInstitucional>>() {
          @Override
          public Page<GrupoRelacionInstitucional> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            return new PageImpl<>(data, pageable, data.size());
          }
        });

    // when: se obtienen las relaciones del grupo
    Pageable paging = PageRequest.of(0, 10);
    Page<GrupoRelacionInstitucional> page = service.findAllByGrupo(grupoId, null, paging);

    // then: se devuelve una página con diez elementos
    Assertions.assertThat(page.getContent()).hasSize(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(10);
  }

  private GrupoRelacionInstitucional generarMockConEntidadRef(Long id, Long grupoId, String entidadRef) {
    return GrupoRelacionInstitucional.builder()
        .id(id)
        .grupoId(grupoId)
        .entidadRef(entidadRef)
        .build();
  }

  private GrupoRelacionInstitucional generarMockConInstitucion(Long id, Long grupoId, String institucion) {
    return GrupoRelacionInstitucional.builder()
        .id(id)
        .grupoId(grupoId)
        .institucion(institucion)
        .build();
  }

}
