package org.crue.hercules.sgi.csp.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.GrupoUnidadVinculacion;
import org.crue.hercules.sgi.csp.repository.GrupoUnidadVinculacionRepository;
import org.crue.hercules.sgi.csp.util.GrupoAuthorityHelper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Import({ GrupoUnidadVinculacionService.class })
class GrupoUnidadServiceTest extends BaseServiceTest {

  @MockBean
  private GrupoUnidadVinculacionRepository repository;

  @MockBean
  private GrupoAuthorityHelper authorityHelper;

  @Autowired
  private GrupoUnidadVinculacionService service;

  @Test
  void findByGrupoId_ReturnsPage() {
    // given: un Grupo con una GrupoUnidadVinculacion
    Long grupoId = 1L;
    Pageable pageable = PageRequest.of(0, 10);
    GrupoUnidadVinculacion entity = GrupoUnidadVinculacion.builder().id(1L).grupoId(grupoId)
        .unidadVinculacionRef("unidad-01")
        .build();
    Page<GrupoUnidadVinculacion> page = new PageImpl<>(Arrays.asList(entity));

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<GrupoUnidadVinculacion>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(page);

    // when: se buscan las GrupoUnidadVinculacion del Grupo
    Page<GrupoUnidadVinculacion> result = service.findByGrupoId(grupoId, null, pageable);

    // then: se obtiene la Page con la GrupoUnidadVinculacion y se verifica el
    // permiso
    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getContent()).hasSize(1);
    Assertions.assertThat(result.getContent().get(0).getUnidadVinculacionRef()).isEqualTo("unidad-01");
    BDDMockito.verify(authorityHelper).checkUserHasAuthorityViewGrupo(grupoId);
  }

  @Test
  void updateUnidades_WithEmptyList_DeletesAllExisting() {
    // given: un Grupo con unidades existentes y una lista nueva vacía
    Long grupoId = 1L;
    List<GrupoUnidadVinculacion> existing = Arrays.asList(
        GrupoUnidadVinculacion.builder().id(1L).grupoId(grupoId).unidadVinculacionRef("u-01").build(),
        GrupoUnidadVinculacion.builder().id(2L).grupoId(grupoId).unidadVinculacionRef("u-02").build());

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<GrupoUnidadVinculacion>>any()))
        .willReturn(existing);

    // when: se actualizan con lista vacía
    List<GrupoUnidadVinculacion> result = service.updateUnidadesVinculacion(grupoId, Collections.emptyList());

    // then: se eliminan todas las existentes y no se persiste ninguna nueva
    Assertions.assertThat(result).isEmpty();
    BDDMockito.verify(authorityHelper).checkUserHasAuthorityViewGrupo(grupoId);
    BDDMockito.verify(repository).deleteAll(existing);
    BDDMockito.verify(repository, BDDMockito.never()).saveAll(ArgumentMatchers.anyList());
  }

  @Test
  void updateUnidades_WithSameItems_PreservesExistingWithoutDbChanges() {
    // given: un Grupo con unidades existentes y se envía la misma lista
    Long grupoId = 1L;
    GrupoUnidadVinculacion unidadVinculacionExisting1 = GrupoUnidadVinculacion.builder()
        .id(10L)
        .grupoId(grupoId)
        .unidadVinculacionRef("u-01")
        .build();
    GrupoUnidadVinculacion unidadVinculacionExisting2 = GrupoUnidadVinculacion.builder()
        .id(11L)
        .grupoId(grupoId)
        .unidadVinculacionRef("u-02")
        .build();
    List<GrupoUnidadVinculacion> existing = Arrays.asList(unidadVinculacionExisting1, unidadVinculacionExisting2);

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<GrupoUnidadVinculacion>>any()))
        .willReturn(existing);

    GrupoUnidadVinculacion unidadVinculacionNew1 = GrupoUnidadVinculacion.builder()
        .unidadVinculacionRef("u-01")
        .build();
    GrupoUnidadVinculacion unidadVinculacionNew2 = GrupoUnidadVinculacion.builder()
        .unidadVinculacionRef("u-02")
        .build();

    // when: se actualizan con los mismos items
    List<GrupoUnidadVinculacion> result = service.updateUnidadesVinculacion(grupoId,
        Arrays.asList(unidadVinculacionNew1, unidadVinculacionNew2));

    // then: se devuelven los mismos items con sus IDs originales sin tocar la BD
    Assertions.assertThat(result)
        .hasSize(2)
        .extracting(GrupoUnidadVinculacion::getId)
        .containsExactlyInAnyOrder(10L, 11L);
    BDDMockito.verify(repository, BDDMockito.never()).deleteAll(ArgumentMatchers.anyList());
    BDDMockito.verify(repository, BDDMockito.never()).saveAll(ArgumentMatchers.anyList());
  }

  @Test
  @SuppressWarnings("unchecked")
  void updateUnidades_WithNewAndDuplicateItems_SavesDeduplicatedNew() {
    // given: sin existentes y una lista nueva con una referencia duplicada
    Long grupoId = 1L;
    GrupoUnidadVinculacion unidadVinculacion1 = GrupoUnidadVinculacion.builder().unidadVinculacionRef("u-01").build();
    GrupoUnidadVinculacion unidadVinculacion2 = GrupoUnidadVinculacion.builder().unidadVinculacionRef("u-02").build();
    GrupoUnidadVinculacion unidadVinculacion1Duplitaded = GrupoUnidadVinculacion.builder().unidadVinculacionRef("u-01")
        .build();

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<GrupoUnidadVinculacion>>any()))
        .willReturn(Collections.emptyList());
    BDDMockito.given(repository.saveAll(ArgumentMatchers.<List<GrupoUnidadVinculacion>>any()))
        .willAnswer(invocation -> invocation.getArgument(0));

    // when: se actualizan con la lista duplicada
    List<GrupoUnidadVinculacion> result = service.updateUnidadesVinculacion(grupoId,
        Arrays.asList(unidadVinculacion1, unidadVinculacion2, unidadVinculacion1Duplitaded));

    // then: u-01 aparece dos veces pero se persiste una sola vez
    Assertions.assertThat(result)
        .hasSize(2)
        .allMatch(u -> grupoId.equals(u.getGrupoId()));
    ArgumentCaptor<List<GrupoUnidadVinculacion>> captor = ArgumentCaptor.forClass(List.class);
    BDDMockito.verify(repository).saveAll(captor.capture());
    Assertions.assertThat(captor.getValue())
        .hasSize(2)
        .extracting(GrupoUnidadVinculacion::getUnidadVinculacionRef)
        .containsExactlyInAnyOrder("u-01", "u-02");
    BDDMockito.verify(repository, BDDMockito.never()).deleteAll(ArgumentMatchers.anyList());
  }

  @Test
  @SuppressWarnings("unchecked")
  void updateUnidades_WithMixedChanges_AddsNewAndRemovesOld() {
    // given: existentes [u-1, u-2] y nueva lista [u-2, u-3]
    Long grupoId = 1L;
    GrupoUnidadVinculacion unidadVinculacionExisting1 = GrupoUnidadVinculacion.builder()
        .id(10L)
        .grupoId(grupoId)
        .unidadVinculacionRef("u-1")
        .build();
    GrupoUnidadVinculacion unidadVinculacionExisting2 = GrupoUnidadVinculacion.builder()
        .id(11L)
        .grupoId(grupoId)
        .unidadVinculacionRef("u-2")
        .build();
    List<GrupoUnidadVinculacion> existing = Arrays.asList(unidadVinculacionExisting1, unidadVinculacionExisting2);

    GrupoUnidadVinculacion unidadVinculacionNew1 = GrupoUnidadVinculacion.builder()
        .unidadVinculacionRef("u-2")
        .build();
    GrupoUnidadVinculacion unidadVinculacionNew2 = GrupoUnidadVinculacion.builder()
        .unidadVinculacionRef("u-3")
        .build();

    GrupoUnidadVinculacion unidadVinculacionNew2Saved = GrupoUnidadVinculacion.builder()
        .id(12L)
        .grupoId(grupoId)
        .unidadVinculacionRef("u-3")
        .build();

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<GrupoUnidadVinculacion>>any()))
        .willReturn(existing);
    BDDMockito.given(repository.saveAll(ArgumentMatchers.<List<GrupoUnidadVinculacion>>any()))
        .willReturn(Arrays.asList(unidadVinculacionNew2Saved));

    // when: se actualizan eliminando u-1 y añadiendo u-3
    List<GrupoUnidadVinculacion> result = service.updateUnidadesVinculacion(grupoId,
        Arrays.asList(unidadVinculacionNew1, unidadVinculacionNew2));

    // then: u-2 se mantiene con su ID original, u-3 se añade, u-1 se elimina
    Assertions.assertThat(result)
        .hasSize(2)
        .extracting(GrupoUnidadVinculacion::getUnidadVinculacionRef)
        .containsExactlyInAnyOrder("u-2", "u-3");
    Assertions.assertThat(result.stream()
        .filter(u -> "u-2".equals(u.getUnidadVinculacionRef()))
        .findFirst().get().getId()).isEqualTo(11L);

    ArgumentCaptor<List<GrupoUnidadVinculacion>> deleteCaptor = ArgumentCaptor.forClass(List.class);
    BDDMockito.verify(repository).deleteAll(deleteCaptor.capture());
    Assertions.assertThat(deleteCaptor.getValue())
        .hasSize(1)
        .extracting(GrupoUnidadVinculacion::getUnidadVinculacionRef)
        .containsExactly("u-1");

    ArgumentCaptor<List<GrupoUnidadVinculacion>> saveCaptor = ArgumentCaptor.forClass(List.class);
    BDDMockito.verify(repository).saveAll(saveCaptor.capture());
    Assertions.assertThat(saveCaptor.getValue())
        .hasSize(1)
        .extracting(GrupoUnidadVinculacion::getUnidadVinculacionRef)
        .containsExactly("u-3");
  }

}
