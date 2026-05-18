package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.GrupoDescriptorNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.TipoDescriptorGrupoNotFoundException;
import org.crue.hercules.sgi.csp.model.GrupoDescriptor;
import org.crue.hercules.sgi.csp.model.GrupoDescriptorTexto;
import org.crue.hercules.sgi.csp.model.TipoDescriptorGrupo;
import org.crue.hercules.sgi.csp.model.TipoDescriptorGrupoNombre;
import org.crue.hercules.sgi.csp.repository.GrupoDescriptorRepository;
import org.crue.hercules.sgi.csp.repository.TipoDescriptorGrupoRepository;
import org.crue.hercules.sgi.framework.i18n.Language;
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

@Import({ GrupoDescriptorService.class })
class GrupoDescriptorServiceTest extends BaseServiceTest {

  @MockBean
  private GrupoDescriptorRepository repository;

  @MockBean
  private TipoDescriptorGrupoRepository tipoDescriptorGrupoRepository;

  @Autowired
  private GrupoDescriptorService service;

  @Test
  void create_ReturnsGrupoDescriptor() {
    // given: nuevo GrupoDescriptor con un TipoDescriptorGrupo activo
    TipoDescriptorGrupo tipoDescriptorGrupo = generarMockTipoDescriptorGrupo(1L, Boolean.TRUE);
    GrupoDescriptor data = generarMockGrupoDescriptor(null, 1L, tipoDescriptorGrupo.getId());

    BDDMockito.given(tipoDescriptorGrupoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(tipoDescriptorGrupo));
    BDDMockito.given(repository.save(ArgumentMatchers.<GrupoDescriptor>any()))
        .willAnswer(new Answer<GrupoDescriptor>() {
          @Override
          public GrupoDescriptor answer(InvocationOnMock invocation) throws Throwable {
            GrupoDescriptor entity = invocation.getArgument(0, GrupoDescriptor.class);
            entity.setId(1L);
            return entity;
          }
        });

    // when: se crea el GrupoDescriptor
    GrupoDescriptor created = service.create(data);

    // then: el nuevo GrupoDescriptor es creado
    Assertions.assertThat(created).isNotNull();
    Assertions.assertThat(created.getId()).isNotNull();
    Assertions.assertThat(created.getGrupoId()).isEqualTo(data.getGrupoId());
    Assertions.assertThat(created.getTipoDescriptorGrupoId()).isEqualTo(data.getTipoDescriptorGrupoId());
  }

  @Test
  void create_WithId_ThrowsIllegalArgumentException() {
    // given: un GrupoDescriptor con id informado
    GrupoDescriptor data = generarMockGrupoDescriptor(1L, 1L, 1L);

    Assertions.assertThatThrownBy(
        // when: se crea el GrupoDescriptor
        () -> service.create(data))
        // then: se lanza excepción porque el id no puede ser informado
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Identificador de Descriptor de Grupo debe ser nulo");
  }

  @Test
  void create_WithInactiveTipoDescriptorGrupo_ThrowsIllegalArgumentException() {
    // given: un GrupoDescriptor con un TipoDescriptorGrupo inactivo
    TipoDescriptorGrupo tipoDescriptorGrupo = generarMockTipoDescriptorGrupo(1L, Boolean.FALSE);
    GrupoDescriptor data = generarMockGrupoDescriptor(null, 1L, tipoDescriptorGrupo.getId());

    BDDMockito.given(tipoDescriptorGrupoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(tipoDescriptorGrupo));

    Assertions.assertThatThrownBy(
        // when: se crea el GrupoDescriptor
        () -> service.create(data))
        // then: se lanza excepción porque el TipoDescriptorGrupo está inactivo
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void create_WithNonExistingTipoDescriptorGrupo_ThrowsNotFoundException() {
    // given: un GrupoDescriptor con un TipoDescriptorGrupo inexistente
    GrupoDescriptor data = generarMockGrupoDescriptor(null, 1L, 99L);

    BDDMockito.given(tipoDescriptorGrupoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: se crea el GrupoDescriptor
        () -> service.create(data))
        // then: se lanza NotFoundException
        .isInstanceOf(TipoDescriptorGrupoNotFoundException.class);
  }

  @Test
  void update_WithExistingId_ReturnsGrupoDescriptor() {
    // given: un GrupoDescriptor existente
    TipoDescriptorGrupo tipoDescriptorGrupo = generarMockTipoDescriptorGrupo(1L, Boolean.TRUE);
    GrupoDescriptor data = generarMockGrupoDescriptor(1L, 1L, tipoDescriptorGrupo.getId());

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(data));
    BDDMockito.given(repository.save(ArgumentMatchers.<GrupoDescriptor>any()))
        .willAnswer(new Answer<GrupoDescriptor>() {
          @Override
          public GrupoDescriptor answer(InvocationOnMock invocation) throws Throwable {
            return invocation.getArgument(0, GrupoDescriptor.class);
          }
        });

    // when: se actualiza el GrupoDescriptor
    GrupoDescriptor updated = service.update(data);

    // then: el GrupoDescriptor es actualizado
    Assertions.assertThat(updated).isNotNull();
    Assertions.assertThat(updated.getId()).isEqualTo(data.getId());
    Assertions.assertThat(updated.getGrupoId()).isEqualTo(data.getGrupoId());
    Assertions.assertThat(updated.getTipoDescriptorGrupoId()).isEqualTo(data.getTipoDescriptorGrupoId());
  }

  @Test
  void update_WithNoExistingId_ThrowsNotFoundException() {
    // given: un id inexistente
    GrupoDescriptor data = generarMockGrupoDescriptor(1L, 1L, 1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: se actualiza un GrupoDescriptor inexistente
        () -> service.update(data))
        // then: se lanza NotFoundException
        .isInstanceOf(GrupoDescriptorNotFoundException.class);
  }

  @Test
  void update_WithoutId_ThrowsIllegalArgumentException() {
    // given: un GrupoDescriptor sin id
    GrupoDescriptor data = generarMockGrupoDescriptor(null, 1L, 1L);

    Assertions.assertThatThrownBy(
        // when: se actualiza el GrupoDescriptor
        () -> service.update(data))
        // then: se lanza excepción porque el id debe ser informado
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Identificador de Descriptor de Grupo no puede ser nulo");
  }

  @Test
  void update_WithChangedInactiveTipoDescriptorGrupo_ThrowsIllegalArgumentException() {
    // given: GrupoDescriptor existente, actualizando a un TipoDescriptorGrupo
    // inactivo
    TipoDescriptorGrupo tipoInactivo = generarMockTipoDescriptorGrupo(2L, Boolean.FALSE);
    GrupoDescriptor existing = generarMockGrupoDescriptor(1L, 1L, 1L);
    GrupoDescriptor toUpdate = generarMockGrupoDescriptor(1L, 1L, tipoInactivo.getId());

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(existing));
    BDDMockito.given(tipoDescriptorGrupoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(tipoInactivo));

    Assertions.assertThatThrownBy(
        // when: se actualiza el GrupoDescriptor
        () -> service.update(toUpdate))
        // then: se lanza excepción porque el TipoDescriptorGrupo está inactivo
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void deleteById_WithExistingId_DeletesGrupoDescriptor() {
    // given: un id existente
    Long id = 1L;
    BDDMockito.given(repository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.willDoNothing().given(repository).deleteById(ArgumentMatchers.anyLong());

    // when: se elimina por id
    service.deleteById(id);

    // then: se invoca repository.deleteById
    BDDMockito.then(repository).should().deleteById(id);
  }

  @Test
  void deleteById_WithNoExistingId_ThrowsNotFoundException() {
    // given: un id inexistente
    Long id = 1L;
    BDDMockito.given(repository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: se elimina un GrupoDescriptor inexistente
        () -> service.deleteById(id))
        // then: se lanza NotFoundException
        .isInstanceOf(GrupoDescriptorNotFoundException.class);
  }

  @Test
  void findById_WithExistingId_ReturnsGrupoDescriptor() {
    // given: un GrupoDescriptor existente
    GrupoDescriptor givenData = generarMockGrupoDescriptor(1L, 1L, 1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(givenData));

    // when: se busca por id
    GrupoDescriptor data = service.findById(givenData.getId());

    // then: devuelve el GrupoDescriptor
    Assertions.assertThat(data).isNotNull();
    Assertions.assertThat(data.getId()).isEqualTo(givenData.getId());
    Assertions.assertThat(data.getGrupoId()).isEqualTo(givenData.getGrupoId());
    Assertions.assertThat(data.getTipoDescriptorGrupoId()).isEqualTo(givenData.getTipoDescriptorGrupoId());
  }

  @Test
  void findById_WithNoExistingId_ThrowsNotFoundException() {
    // given: un id inexistente
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: se busca por un id inexistente
        () -> service.findById(1L))
        // then: se lanza NotFoundException
        .isInstanceOf(GrupoDescriptorNotFoundException.class);
  }

  @Test
  void findAllByGrupo_WithPaging_ReturnsPage() {
    // given: diez GrupoDescriptor para un grupo
    Long grupoId = 1L;
    List<GrupoDescriptor> data = new ArrayList<>();
    for (int i = 1; i <= 10; i++) {
      data.add(generarMockGrupoDescriptor(Long.valueOf(i), grupoId, 1L));
    }

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<GrupoDescriptor>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<GrupoDescriptor>>() {
          @Override
          public Page<GrupoDescriptor> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            return new PageImpl<>(data, pageable, data.size());
          }
        });

    // when: se obtienen los GrupoDescriptor del grupo
    Pageable paging = PageRequest.of(0, 10);
    Page<GrupoDescriptor> page = service.findAllByGrupo(grupoId, null, paging);

    // then: se devuelve una página con diez GrupoDescriptor
    Assertions.assertThat(page.getContent()).hasSize(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(10);
  }

  /**
   * Genera un mock de {@link GrupoDescriptor}.
   *
   * @param id                    identificador.
   * @param grupoId               identificador del grupo.
   * @param tipoDescriptorGrupoId identificador del tipo descriptor de grupo.
   * @return {@link GrupoDescriptor} mock.
   */
  private GrupoDescriptor generarMockGrupoDescriptor(Long id, Long grupoId, Long tipoDescriptorGrupoId) {
    Set<GrupoDescriptorTexto> texto = new HashSet<>();
    texto.add(new GrupoDescriptorTexto(Language.ES, "texto-" + id));

    return GrupoDescriptor.builder()
        .id(id)
        .grupoId(grupoId)
        .tipoDescriptorGrupoId(tipoDescriptorGrupoId)
        .texto(texto)
        .build();
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
        .id(id).nombre(nombre)
        .activo(activo)
        .build();
  }

}
