package org.crue.hercules.sgi.eti.service;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.crue.hercules.sgi.eti.exceptions.PeticionEvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionUnidadVinculacion;
import org.crue.hercules.sgi.eti.repository.PeticionEvaluacionRepository;
import org.crue.hercules.sgi.eti.repository.PeticionEvaluacionUnidadVinculacionRepository;
import org.crue.hercules.sgi.eti.repository.specification.PeticionEvaluacionUnidadVinculacionSpecifications;
import org.crue.hercules.sgi.eti.util.SgiLogUtils;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.util.AssertHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service para gestionar {@link PeticionEvaluacionUnidadVinculacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PeticionEvaluacionUnidadVinculacionService {

  private final PeticionEvaluacionUnidadVinculacionRepository repository;
  private final PeticionEvaluacionRepository peticionEvaluacionRepository;

  /**
   * Actualiza la lista de {@link PeticionEvaluacionUnidadVinculacion} de la
   * {@link PeticionEvaluacion}. Elimina las que ya no están en la nueva lista,
   * añade las nuevas y conserva las que se mantienen. Las referencias
   * duplicadas se tratan como una sola.
   *
   * @param peticionEvaluacionId el id de la {@link PeticionEvaluacion}.
   * @param unidadesVinculacion  la lista con las nuevas unidades de vinculación.
   * @return la lista actualizada de {@link PeticionEvaluacionUnidadVinculacion}.
   * @throws PeticionEvaluacionNotFoundException si no existe la
   *                                             {@link PeticionEvaluacion}.
   */
  @Transactional
  public List<PeticionEvaluacionUnidadVinculacion> updateUnidadesVinculacion(Long peticionEvaluacionId,
      List<PeticionEvaluacionUnidadVinculacion> unidadesVinculacion) {
    log.debug("updateUnidadesVinculacion - peticionEvaluacionId: {}, unidadesVinculacion: {}", peticionEvaluacionId,
        unidadesVinculacion);
    assertPeticionEvaluacionExists(peticionEvaluacionId);

    List<PeticionEvaluacionUnidadVinculacion> existing = repository
        .findAllByPeticionEvaluacionId(peticionEvaluacionId);

    Set<String> existingRefs = existing.stream()
        .map(PeticionEvaluacionUnidadVinculacion::getUnidadVinculacionRef)
        .collect(Collectors.toSet());

    Set<String> newRefs = unidadesVinculacion.stream()
        .map(PeticionEvaluacionUnidadVinculacion::getUnidadVinculacionRef)
        .collect(Collectors.toCollection(LinkedHashSet::new));

    Map<Boolean, List<PeticionEvaluacionUnidadVinculacion>> existingByKept = existing.stream()
        .collect(Collectors.partitioningBy(unidad -> newRefs.contains(unidad.getUnidadVinculacionRef())));
    List<PeticionEvaluacionUnidadVinculacion> kept = existingByKept.get(true);
    List<PeticionEvaluacionUnidadVinculacion> toDelete = existingByKept.get(false);
    if (!toDelete.isEmpty()) {
      repository.deleteAll(toDelete);
    }

    List<PeticionEvaluacionUnidadVinculacion> toAdd = newRefs.stream()
        .filter(ref -> !existingRefs.contains(ref))
        .map(ref -> PeticionEvaluacionUnidadVinculacion.builder()
            .peticionEvaluacionId(peticionEvaluacionId)
            .unidadVinculacionRef(ref)
            .build())
        .toList();

    List<PeticionEvaluacionUnidadVinculacion> saved = toAdd.isEmpty()
        ? Collections.emptyList()
        : repository.saveAll(toAdd);

    List<PeticionEvaluacionUnidadVinculacion> result = Stream.concat(kept.stream(), saved.stream()).toList();

    log.debug("updateUnidadesVinculacion - response: {}", SgiLogUtils.collection(result));
    return result;
  }

  /**
   * Obtiene las {@link PeticionEvaluacionUnidadVinculacion} de una
   * {@link PeticionEvaluacion} paginadas y/o filtradas.
   *
   * @param peticionEvaluacionId el id de la {@link PeticionEvaluacion}.
   * @param query                la información del filtro.
   * @param paging               la información de la paginación.
   * @return la página de {@link PeticionEvaluacionUnidadVinculacion} de la
   *         {@link PeticionEvaluacion}.
   * @throws PeticionEvaluacionNotFoundException si no existe la
   *                                             {@link PeticionEvaluacion}.
   */
  public Page<PeticionEvaluacionUnidadVinculacion> findAllByPeticionEvaluacionId(Long peticionEvaluacionId,
      String query, Pageable paging) {
    log.debug("findAllByPeticionEvaluacionId - peticionEvaluacionId: {}, query: {}, paging: {}",
        peticionEvaluacionId, query, SgiLogUtils.pageable(paging));
    assertPeticionEvaluacionExists(peticionEvaluacionId);

    Specification<PeticionEvaluacionUnidadVinculacion> specs = PeticionEvaluacionUnidadVinculacionSpecifications
        .byPeticionEvaluacionId(peticionEvaluacionId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<PeticionEvaluacionUnidadVinculacion> page = repository.findAll(specs, paging);
    log.debug("findAllByPeticionEvaluacionId - response: {}", SgiLogUtils.page(page));
    return page;
  }

  /**
   * Comprueba que existe la {@link PeticionEvaluacion} con el id indicado.
   *
   * @param peticionEvaluacionId el id de la {@link PeticionEvaluacion}.
   * @throws PeticionEvaluacionNotFoundException si no existe.
   */
  private void assertPeticionEvaluacionExists(Long peticionEvaluacionId) {
    AssertHelper.idNotNull(peticionEvaluacionId, PeticionEvaluacion.class);
    if (!peticionEvaluacionRepository.existsById(peticionEvaluacionId)) {
      throw new PeticionEvaluacionNotFoundException(peticionEvaluacionId);
    }
  }

}
