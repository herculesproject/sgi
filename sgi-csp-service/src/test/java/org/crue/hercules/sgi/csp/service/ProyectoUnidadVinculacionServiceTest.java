package org.crue.hercules.sgi.csp.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToAccessProyectoException;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoUnidadVinculacion;
import org.crue.hercules.sgi.csp.repository.ProyectoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoResponsableEconomicoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoUnidadVinculacionRepository;
import org.crue.hercules.sgi.csp.util.ProyectoHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * Tests unitarios para {@link ProyectoUnidadVinculacionService}.
 */
class ProyectoUnidadVinculacionServiceTest extends BaseServiceTest {

  @Mock
  private ProyectoUnidadVinculacionRepository repository;
  @Mock
  private ProyectoEquipoRepository proyectoEquipoRepository;
  @Mock
  private ProyectoResponsableEconomicoRepository proyectoResponsableEconomicoRepository;
  @Mock
  private ProyectoRepository proyectoRepository;

  private ProyectoHelper proyectoHelper;
  private ProyectoUnidadVinculacionService service;

  @BeforeEach
  void setUp() {
    this.proyectoHelper = new ProyectoHelper(proyectoRepository, proyectoEquipoRepository,
        proyectoResponsableEconomicoRepository);
    this.service = new ProyectoUnidadVinculacionService(repository, proyectoHelper);
  }

  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  @Test
  void findByProyectoId_ReturnsPaginatedList() {
    // given: un Proyecto con dos ProyectoUnidadVinculacion
    Long proyectoId = 1L;
    Pageable pageable = PageRequest.of(0, 10);
    Proyecto proyecto = Proyecto.builder().id(proyectoId).unidadGestionRef("UGI001").build();
    List<ProyectoUnidadVinculacion> unidades = Arrays.asList(
        buildUnidad(1L, proyectoId, "ref-01"),
        buildUnidad(2L, proyectoId, "ref-02"));

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto));
    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ProyectoUnidadVinculacion>>any(),
        ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(unidades, pageable, unidades.size()));

    // when: se buscan las unidades de vinculacion del proyecto
    Page<ProyectoUnidadVinculacion> result = service.findByProyectoId(proyectoId, null, pageable);

    // then: se devuelve la lista paginada
    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getTotalElements()).isEqualTo(2);
    Assertions.assertThat(result.getContent().get(0).getUnidadVinculacionRef()).isEqualTo("ref-01");
    Assertions.assertThat(result.getContent().get(1).getUnidadVinculacionRef()).isEqualTo("ref-02");
  }

  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  @Test
  void findByProyectoId_WithoutAuthorization_ThrowsException() {
    // given: un usuario sin autorización sobre el proyecto
    Long proyectoId = 1L;
    Pageable pageable = PageRequest.of(0, 10);
    Proyecto proyecto = Proyecto.builder().id(proyectoId).unidadGestionRef("UGI001").build();

    BDDMockito.given(proyectoRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(proyecto));

    // when: se intenta buscar sin autorización
    // then: se lanza UserNotAuthorizedToAccessProyectoException
    Assertions.assertThatThrownBy(() -> service.findByProyectoId(proyectoId, null, pageable))
        .isInstanceOf(UserNotAuthorizedToAccessProyectoException.class);
  }

  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  @Test
  void updateUnidadesVinculacion_WithNewRefs_ReturnsUpdatedList() {
    // given: un Proyecto con unidades previas y una lista con nuevas referencias
    Long proyectoId = 1L;

    List<ProyectoUnidadVinculacion> existing = Arrays.asList(
        buildUnidad(1L, proyectoId, "old-ref-01"),
        buildUnidad(2L, proyectoId, "old-ref-02"));

    List<ProyectoUnidadVinculacion> newUnidades = Arrays.asList(
        buildUnidadSinId(proyectoId, "new-ref-01"),
        buildUnidadSinId(proyectoId, "new-ref-02"));

    List<ProyectoUnidadVinculacion> saved = Arrays.asList(
        buildUnidad(3L, proyectoId, "new-ref-01"),
        buildUnidad(4L, proyectoId, "new-ref-02"));

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ProyectoUnidadVinculacion>>any()))
        .willReturn(existing);
    BDDMockito.given(repository.saveAll(ArgumentMatchers.<List<ProyectoUnidadVinculacion>>any()))
        .willReturn(saved);

    // when: se actualizan con nuevas referencias
    List<ProyectoUnidadVinculacion> result = service.updateUnidadesVinculacion(proyectoId, newUnidades);

    // then: se devuelven solo las nuevas
    Assertions.assertThat(result).hasSize(2);
    Assertions.assertThat(result.get(0).getUnidadVinculacionRef()).isEqualTo("new-ref-01");
    Assertions.assertThat(result.get(1).getUnidadVinculacionRef()).isEqualTo("new-ref-02");
  }

  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  @Test
  void updateUnidadesVinculacion_WithSameRefs_PreservesExisting() {
    // given: un Proyecto con unidades previas y se envía la misma lista
    Long proyectoId = 1L;

    List<ProyectoUnidadVinculacion> existing = Arrays.asList(
        buildUnidad(1L, proyectoId, "ref-01"),
        buildUnidad(2L, proyectoId, "ref-02"));

    List<ProyectoUnidadVinculacion> input = Arrays.asList(
        buildUnidadSinId(proyectoId, "ref-01"),
        buildUnidadSinId(proyectoId, "ref-02"));

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ProyectoUnidadVinculacion>>any()))
        .willReturn(existing);

    // when: se actualizan con la misma lista
    List<ProyectoUnidadVinculacion> result = service.updateUnidadesVinculacion(proyectoId, input);

    // then: se preservan los existentes (sin llamar a saveAll)
    Assertions.assertThat(result).hasSize(2);
    Assertions.assertThat(result.get(0).getId()).isEqualTo(1L);
    Assertions.assertThat(result.get(1).getId()).isEqualTo(2L);
    BDDMockito.then(repository).should(BDDMockito.never())
        .saveAll(ArgumentMatchers.<List<ProyectoUnidadVinculacion>>any());
  }

  @WithMockUser(username = "user", authorities = { "CSP-PRO-E" })
  @Test
  void updateUnidadesVinculacion_WithEmptyList_DeletesAll() {
    // given: un Proyecto con unidades previas y se envía lista vacía
    Long proyectoId = 1L;

    List<ProyectoUnidadVinculacion> existing = Arrays.asList(
        buildUnidad(1L, proyectoId, "ref-01"),
        buildUnidad(2L, proyectoId, "ref-02"));

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<ProyectoUnidadVinculacion>>any()))
        .willReturn(existing);

    // when: se actualiza con lista vacía
    List<ProyectoUnidadVinculacion> result = service.updateUnidadesVinculacion(proyectoId,
        Collections.emptyList());

    // then: resultado vacío y se eliminan todos
    Assertions.assertThat(result).isEmpty();
    BDDMockito.then(repository).should().deleteAll(existing);
  }

  private ProyectoUnidadVinculacion buildUnidad(Long id, Long proyectoId, String ref) {
    return ProyectoUnidadVinculacion.builder()
        .id(id)
        .proyectoId(proyectoId)
        .unidadVinculacionRef(ref)
        .build();
  }

  private ProyectoUnidadVinculacion buildUnidadSinId(Long proyectoId, String ref) {
    return ProyectoUnidadVinculacion.builder()
        .proyectoId(proyectoId)
        .unidadVinculacionRef(ref)
        .build();
  }

}
