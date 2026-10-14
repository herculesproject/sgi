package org.crue.hercules.sgi.eti.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.PeticionEvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionUnidadVinculacion;
import org.crue.hercules.sgi.eti.repository.PeticionEvaluacionRepository;
import org.crue.hercules.sgi.eti.repository.PeticionEvaluacionUnidadVinculacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * PeticionEvaluacionUnidadVinculacionServiceTest
 */
class PeticionEvaluacionUnidadVinculacionServiceTest extends BaseServiceTest {

  @Mock
  private PeticionEvaluacionUnidadVinculacionRepository repository;

  @Mock
  private PeticionEvaluacionRepository peticionEvaluacionRepository;

  private PeticionEvaluacionUnidadVinculacionService service;

  @BeforeEach
  void setUp() {
    service = new PeticionEvaluacionUnidadVinculacionService(repository, peticionEvaluacionRepository);
  }

  @Test
  void findAllByPeticionEvaluacionId_ReturnsPage() {
    // given: una petición con una unidad de vinculación
    Long peticionEvaluacionId = 1L;
    Pageable paging = PageRequest.of(0, 10);
    BDDMockito.given(peticionEvaluacionRepository.existsById(peticionEvaluacionId)).willReturn(true);
    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<PeticionEvaluacionUnidadVinculacion>>any(),
        ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(Arrays.asList(generarMock(1L, peticionEvaluacionId, "UV-1")), paging, 1));

    // when: se buscan las unidades de la petición
    Page<PeticionEvaluacionUnidadVinculacion> page = service.findAllByPeticionEvaluacionId(peticionEvaluacionId,
        null, paging);

    // then: se obtiene la página con la unidad
    Assertions.assertThat(page.getContent()).hasSize(1);
    Assertions.assertThat(page.getContent().get(0).getUnidadVinculacionRef()).isEqualTo("UV-1");
  }

  @Test
  void findAllByPeticionEvaluacionId_WithNoExistingPeticion_ThrowsPeticionEvaluacionNotFoundException() {
    // given: una petición que no existe
    Long peticionEvaluacionId = 1L;
    Pageable paging = Pageable.unpaged();
    BDDMockito.given(peticionEvaluacionRepository.existsById(peticionEvaluacionId)).willReturn(false);

    Assertions.assertThatThrownBy(
        // when: se buscan las unidades de la petición
        () -> service.findAllByPeticionEvaluacionId(peticionEvaluacionId, null, paging))
        // then: se lanza la excepción de petición no encontrada
        .isInstanceOf(PeticionEvaluacionNotFoundException.class);
  }

  @Test
  void findAllByPeticionEvaluacionId_WithIdNull_ThrowsIllegalArgumentException() {
    // given: un id de petición nulo
    Pageable paging = Pageable.unpaged();

    Assertions.assertThatThrownBy(
        // when: se buscan las unidades de la petición
        () -> service.findAllByPeticionEvaluacionId(null, null, paging))
        // then: se lanza la excepción de argumento
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Identificador de Petición Evaluación no puede ser nulo");
  }

  @Test
  void updateUnidadesVinculacion_WithEmptyList_DeletesAllExisting() {
    // given: una petición con dos unidades y una lista nueva vacía
    Long peticionEvaluacionId = 1L;
    List<PeticionEvaluacionUnidadVinculacion> existing = Arrays.asList(generarMock(1L, peticionEvaluacionId, "UV-1"),
        generarMock(2L, peticionEvaluacionId, "UV-2"));
    BDDMockito.given(peticionEvaluacionRepository.existsById(peticionEvaluacionId)).willReturn(true);
    BDDMockito.given(repository.findAllByPeticionEvaluacionId(peticionEvaluacionId)).willReturn(existing);

    // when: se actualizan con la lista vacía
    List<PeticionEvaluacionUnidadVinculacion> result = service.updateUnidadesVinculacion(peticionEvaluacionId,
        Collections.emptyList());

    // then: se eliminan las existentes y no se guarda ninguna
    Assertions.assertThat(result).isEmpty();
    BDDMockito.then(repository).should().deleteAll(existing);
    BDDMockito.then(repository).should(BDDMockito.never())
        .saveAll(ArgumentMatchers.<Iterable<PeticionEvaluacionUnidadVinculacion>>any());
  }

  @Test
  void updateUnidadesVinculacion_WithSameRefs_PreservesExisting() {
    // given: una petición con dos unidades y una lista nueva con las mismas referencias
    Long peticionEvaluacionId = 1L;
    List<PeticionEvaluacionUnidadVinculacion> existing = Arrays.asList(generarMock(1L, peticionEvaluacionId, "UV-1"),
        generarMock(2L, peticionEvaluacionId, "UV-2"));
    BDDMockito.given(peticionEvaluacionRepository.existsById(peticionEvaluacionId)).willReturn(true);
    BDDMockito.given(repository.findAllByPeticionEvaluacionId(peticionEvaluacionId)).willReturn(existing);

    // when: se actualizan con las mismas referencias
    List<PeticionEvaluacionUnidadVinculacion> result = service.updateUnidadesVinculacion(peticionEvaluacionId,
        Arrays.asList(generarMock(null, null, "UV-1"), generarMock(null, null, "UV-2")));

    // then: se conservan los ids y no se borra ni guarda nada
    Assertions.assertThat(result).extracting(PeticionEvaluacionUnidadVinculacion::getId).containsExactly(1L, 2L);
    BDDMockito.then(repository).should(BDDMockito.never())
        .deleteAll(ArgumentMatchers.<Iterable<PeticionEvaluacionUnidadVinculacion>>any());
    BDDMockito.then(repository).should(BDDMockito.never())
        .saveAll(ArgumentMatchers.<Iterable<PeticionEvaluacionUnidadVinculacion>>any());
  }

  @Test
  void updateUnidadesVinculacion_WithNewAndDuplicatedRefs_SavesDeduplicated() {
    // given: una petición sin unidades y una lista con una referencia repetida
    Long peticionEvaluacionId = 1L;
    BDDMockito.given(peticionEvaluacionRepository.existsById(peticionEvaluacionId)).willReturn(true);
    BDDMockito.given(repository.findAllByPeticionEvaluacionId(peticionEvaluacionId))
        .willReturn(Collections.emptyList());
    mockSaveAll();

    // when: se actualizan con la referencia repetida y otra nueva
    List<PeticionEvaluacionUnidadVinculacion> result = service.updateUnidadesVinculacion(peticionEvaluacionId,
        Arrays.asList(generarMock(null, null, "UV-1"), generarMock(null, null, "UV-1"),
            generarMock(null, null, "UV-2")));

    // then: se guardan dos unidades asociadas a la petición
    Assertions.assertThat(result).extracting(PeticionEvaluacionUnidadVinculacion::getUnidadVinculacionRef)
        .containsExactly("UV-1", "UV-2");
    Assertions.assertThat(result).extracting(PeticionEvaluacionUnidadVinculacion::getPeticionEvaluacionId)
        .containsOnly(peticionEvaluacionId);
    Assertions.assertThat(result).extracting(PeticionEvaluacionUnidadVinculacion::getId).doesNotContainNull();
    BDDMockito.then(repository).should(BDDMockito.never())
        .deleteAll(ArgumentMatchers.<Iterable<PeticionEvaluacionUnidadVinculacion>>any());
  }

  @Test
  @SuppressWarnings("unchecked")
  void updateUnidadesVinculacion_WithMixedRefs_DeletesRemovedAndSavesNew() {
    // given: una petición con dos unidades; se mantiene una, se quita otra y se añade una nueva
    Long peticionEvaluacionId = 1L;
    PeticionEvaluacionUnidadVinculacion kept = generarMock(1L, peticionEvaluacionId, "UV-1");
    PeticionEvaluacionUnidadVinculacion removed = generarMock(2L, peticionEvaluacionId, "UV-2");
    BDDMockito.given(peticionEvaluacionRepository.existsById(peticionEvaluacionId)).willReturn(true);
    BDDMockito.given(repository.findAllByPeticionEvaluacionId(peticionEvaluacionId))
        .willReturn(Arrays.asList(kept, removed));
    mockSaveAll();

    // when: se actualizan con UV-1 y UV-3
    List<PeticionEvaluacionUnidadVinculacion> result = service.updateUnidadesVinculacion(peticionEvaluacionId,
        Arrays.asList(generarMock(null, null, "UV-1"), generarMock(null, null, "UV-3")));

    // then: se borra UV-2, se guarda UV-3 y se conserva el id de UV-1
    ArgumentCaptor<Collection<PeticionEvaluacionUnidadVinculacion>> deleted = ArgumentCaptor
        .forClass(Collection.class);
    BDDMockito.then(repository).should().deleteAll(deleted.capture());
    Assertions.assertThat(deleted.getValue()).containsExactly(removed);
    Assertions.assertThat(result).extracting(PeticionEvaluacionUnidadVinculacion::getUnidadVinculacionRef)
        .containsExactly("UV-1", "UV-3");
    Assertions.assertThat(result.get(0).getId()).isEqualTo(1L);
  }

  @Test
  void updateUnidadesVinculacion_WithNoExistingPeticion_ThrowsPeticionEvaluacionNotFoundException() {
    // given: una petición que no existe
    Long peticionEvaluacionId = 1L;
    List<PeticionEvaluacionUnidadVinculacion> unidades = Collections.emptyList();
    BDDMockito.given(peticionEvaluacionRepository.existsById(peticionEvaluacionId)).willReturn(false);

    Assertions.assertThatThrownBy(
        // when: se actualizan las unidades
        () -> service.updateUnidadesVinculacion(peticionEvaluacionId, unidades))
        // then: se lanza la excepción de petición no encontrada
        .isInstanceOf(PeticionEvaluacionNotFoundException.class);
  }

  private void mockSaveAll() {
    BDDMockito.given(repository.saveAll(ArgumentMatchers.<Iterable<PeticionEvaluacionUnidadVinculacion>>any()))
        .willAnswer(invocation -> {
          Iterable<PeticionEvaluacionUnidadVinculacion> entities = invocation.getArgument(0);
          List<PeticionEvaluacionUnidadVinculacion> saved = new ArrayList<>();
          long id = 100L;
          for (PeticionEvaluacionUnidadVinculacion entity : entities) {
            entity.setId(id++);
            saved.add(entity);
          }
          return saved;
        });
  }

  private PeticionEvaluacionUnidadVinculacion generarMock(Long id, Long peticionEvaluacionId, String ref) {
    return PeticionEvaluacionUnidadVinculacion.builder()
        .id(id)
        .peticionEvaluacionId(peticionEvaluacionId)
        .unidadVinculacionRef(ref)
        .build();
  }

}
