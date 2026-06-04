package org.crue.hercules.sgi.csp.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoUnidadVinculacion;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoUnidadVinculacionRepository;
import org.crue.hercules.sgi.csp.util.SolicitudAuthorityHelper;
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

@Import({ SolicitudProyectoUnidadVinculacionService.class })
class SolicitudProyectoUnidadVinculacionServiceTest extends BaseServiceTest {

  @MockBean
  private SolicitudProyectoUnidadVinculacionRepository repository;

  @MockBean
  private SolicitudAuthorityHelper authorityHelper;

  @Autowired
  private SolicitudProyectoUnidadVinculacionService service;

  @Test
  void findBySolicitudProyectoId_ReturnsPage() {
    // given: un SolicitudProyecto con una SolicitudProyectoUnidadVinculacion
    Long solicitudProyectoId = 1L;
    Pageable pageable = PageRequest.of(0, 10);
    SolicitudProyectoUnidadVinculacion entity = SolicitudProyectoUnidadVinculacion.builder().id(1L)
        .solicitudProyectoId(solicitudProyectoId)
        .unidadVinculacionRef("unidad-01")
        .build();
    Page<SolicitudProyectoUnidadVinculacion> page = new PageImpl<>(Arrays.asList(entity));

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<SolicitudProyectoUnidadVinculacion>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(page);

    // when: se buscan las SolicitudProyectoUnidadVinculacion del SolicitudProyecto
    Page<SolicitudProyectoUnidadVinculacion> result = service.findBySolicitudProyectoId(solicitudProyectoId, null,
        pageable);

    // then: se obtiene la Page con la SolicitudProyectoUnidadVinculacion y se
    // verifica el
    // permiso
    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getContent()).hasSize(1);
    Assertions.assertThat(result.getContent().get(0).getUnidadVinculacionRef()).isEqualTo("unidad-01");
    BDDMockito.verify(authorityHelper).checkUserHasAuthorityViewSolicitud(solicitudProyectoId);
  }

  @Test
  void updateUnidades_WithEmptyList_DeletesAllExisting() {
    // given: un SolicitudProyecto con unidades existentes y una lista nueva vacía
    Long solicitudProyectoId = 1L;
    List<SolicitudProyectoUnidadVinculacion> existing = Arrays.asList(
        SolicitudProyectoUnidadVinculacion.builder().id(1L).solicitudProyectoId(solicitudProyectoId)
            .unidadVinculacionRef("u-01").build(),
        SolicitudProyectoUnidadVinculacion.builder().id(2L).solicitudProyectoId(solicitudProyectoId)
            .unidadVinculacionRef("u-02").build());

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<SolicitudProyectoUnidadVinculacion>>any()))
        .willReturn(existing);

    // when: se actualizan con lista vacía
    List<SolicitudProyectoUnidadVinculacion> result = service.updateUnidadesVinculacion(solicitudProyectoId,
        Collections.emptyList());

    // then: se eliminan todas las existentes y no se persiste ninguna nueva
    Assertions.assertThat(result).isEmpty();
    BDDMockito.verify(authorityHelper).checkUserHasAuthorityModifySolicitud(solicitudProyectoId);
    BDDMockito.verify(repository).deleteAll(existing);
    BDDMockito.verify(repository, BDDMockito.never()).saveAll(ArgumentMatchers.anyList());
  }

  @Test
  void updateUnidades_WithSameItems_PreservesExistingWithoutDbChanges() {
    // given: un SolicitudProyecto con unidades existentes y se envía la misma lista
    Long solicitudProyectoId = 1L;
    SolicitudProyectoUnidadVinculacion unidadVinculacionExisting1 = SolicitudProyectoUnidadVinculacion.builder()
        .id(10L)
        .solicitudProyectoId(solicitudProyectoId)
        .unidadVinculacionRef("u-01")
        .build();
    SolicitudProyectoUnidadVinculacion unidadVinculacionExisting2 = SolicitudProyectoUnidadVinculacion.builder()
        .id(11L)
        .solicitudProyectoId(solicitudProyectoId)
        .unidadVinculacionRef("u-02")
        .build();
    List<SolicitudProyectoUnidadVinculacion> existing = Arrays.asList(unidadVinculacionExisting1,
        unidadVinculacionExisting2);

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<SolicitudProyectoUnidadVinculacion>>any()))
        .willReturn(existing);

    SolicitudProyectoUnidadVinculacion unidadVinculacionNew1 = SolicitudProyectoUnidadVinculacion.builder()
        .unidadVinculacionRef("u-01")
        .build();
    SolicitudProyectoUnidadVinculacion unidadVinculacionNew2 = SolicitudProyectoUnidadVinculacion.builder()
        .unidadVinculacionRef("u-02")
        .build();

    // when: se actualizan con los mismos items
    List<SolicitudProyectoUnidadVinculacion> result = service.updateUnidadesVinculacion(solicitudProyectoId,
        Arrays.asList(unidadVinculacionNew1, unidadVinculacionNew2));

    // then: se devuelven los mismos items con sus IDs originales sin tocar la BD
    Assertions.assertThat(result)
        .hasSize(2)
        .extracting(SolicitudProyectoUnidadVinculacion::getId)
        .containsExactlyInAnyOrder(10L, 11L);
    BDDMockito.verify(repository, BDDMockito.never()).deleteAll(ArgumentMatchers.anyList());
    BDDMockito.verify(repository, BDDMockito.never()).saveAll(ArgumentMatchers.anyList());
  }

  @Test
  @SuppressWarnings("unchecked")
  void updateUnidades_WithNewAndDuplicateItems_SavesDeduplicatedNew() {
    // given: sin existentes y una lista nueva con una referencia duplicada
    Long solicitudProyectoId = 1L;
    SolicitudProyectoUnidadVinculacion unidadVinculacion1 = SolicitudProyectoUnidadVinculacion.builder()
        .unidadVinculacionRef("u-01").build();
    SolicitudProyectoUnidadVinculacion unidadVinculacion2 = SolicitudProyectoUnidadVinculacion.builder()
        .unidadVinculacionRef("u-02").build();
    SolicitudProyectoUnidadVinculacion unidadVinculacion1Duplitaded = SolicitudProyectoUnidadVinculacion.builder()
        .unidadVinculacionRef("u-01")
        .build();

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<SolicitudProyectoUnidadVinculacion>>any()))
        .willReturn(Collections.emptyList());
    BDDMockito.given(repository.saveAll(ArgumentMatchers.<List<SolicitudProyectoUnidadVinculacion>>any()))
        .willAnswer(invocation -> invocation.getArgument(0));

    // when: se actualizan con la lista duplicada
    List<SolicitudProyectoUnidadVinculacion> result = service.updateUnidadesVinculacion(solicitudProyectoId,
        Arrays.asList(unidadVinculacion1, unidadVinculacion2, unidadVinculacion1Duplitaded));

    // then: u-01 aparece dos veces pero se persiste una sola vez
    Assertions.assertThat(result)
        .hasSize(2)
        .allMatch(u -> solicitudProyectoId.equals(u.getSolicitudProyectoId()));
    ArgumentCaptor<List<SolicitudProyectoUnidadVinculacion>> captor = ArgumentCaptor.forClass(List.class);
    BDDMockito.verify(repository).saveAll(captor.capture());
    Assertions.assertThat(captor.getValue())
        .hasSize(2)
        .extracting(SolicitudProyectoUnidadVinculacion::getUnidadVinculacionRef)
        .containsExactlyInAnyOrder("u-01", "u-02");
    BDDMockito.verify(repository, BDDMockito.never()).deleteAll(ArgumentMatchers.anyList());
  }

  @Test
  @SuppressWarnings("unchecked")
  void updateUnidades_WithMixedChanges_AddsNewAndRemovesOld() {
    // given: existentes [u-1, u-2] y nueva lista [u-2, u-3]
    Long solicitudProyectoId = 1L;
    SolicitudProyectoUnidadVinculacion unidadVinculacionExisting1 = SolicitudProyectoUnidadVinculacion.builder()
        .id(10L)
        .solicitudProyectoId(solicitudProyectoId)
        .unidadVinculacionRef("u-1")
        .build();
    SolicitudProyectoUnidadVinculacion unidadVinculacionExisting2 = SolicitudProyectoUnidadVinculacion.builder()
        .id(11L)
        .solicitudProyectoId(solicitudProyectoId)
        .unidadVinculacionRef("u-2")
        .build();
    List<SolicitudProyectoUnidadVinculacion> existing = Arrays.asList(unidadVinculacionExisting1,
        unidadVinculacionExisting2);

    SolicitudProyectoUnidadVinculacion unidadVinculacionNew1 = SolicitudProyectoUnidadVinculacion.builder()
        .unidadVinculacionRef("u-2")
        .build();
    SolicitudProyectoUnidadVinculacion unidadVinculacionNew2 = SolicitudProyectoUnidadVinculacion.builder()
        .unidadVinculacionRef("u-3")
        .build();

    SolicitudProyectoUnidadVinculacion unidadVinculacionNew2Saved = SolicitudProyectoUnidadVinculacion.builder()
        .id(12L)
        .solicitudProyectoId(solicitudProyectoId)
        .unidadVinculacionRef("u-3")
        .build();

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<SolicitudProyectoUnidadVinculacion>>any()))
        .willReturn(existing);
    BDDMockito.given(repository.saveAll(ArgumentMatchers.<List<SolicitudProyectoUnidadVinculacion>>any()))
        .willReturn(Arrays.asList(unidadVinculacionNew2Saved));

    // when: se actualizan eliminando u-1 y añadiendo u-3
    List<SolicitudProyectoUnidadVinculacion> result = service.updateUnidadesVinculacion(solicitudProyectoId,
        Arrays.asList(unidadVinculacionNew1, unidadVinculacionNew2));

    // then: u-2 se mantiene con su ID original, u-3 se añade, u-1 se elimina
    Assertions.assertThat(result)
        .hasSize(2)
        .extracting(SolicitudProyectoUnidadVinculacion::getUnidadVinculacionRef)
        .containsExactlyInAnyOrder("u-2", "u-3");
    Assertions.assertThat(result.stream()
        .filter(u -> "u-2".equals(u.getUnidadVinculacionRef()))
        .findFirst().get().getId()).isEqualTo(11L);

    ArgumentCaptor<List<SolicitudProyectoUnidadVinculacion>> deleteCaptor = ArgumentCaptor.forClass(List.class);
    BDDMockito.verify(repository).deleteAll(deleteCaptor.capture());
    Assertions.assertThat(deleteCaptor.getValue())
        .hasSize(1)
        .extracting(SolicitudProyectoUnidadVinculacion::getUnidadVinculacionRef)
        .containsExactly("u-1");

    ArgumentCaptor<List<SolicitudProyectoUnidadVinculacion>> saveCaptor = ArgumentCaptor.forClass(List.class);
    BDDMockito.verify(repository).saveAll(saveCaptor.capture());
    Assertions.assertThat(saveCaptor.getValue())
        .hasSize(1)
        .extracting(SolicitudProyectoUnidadVinculacion::getUnidadVinculacionRef)
        .containsExactly("u-3");
  }

}
