package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoUnidadVinculacion;
import org.crue.hercules.sgi.csp.repository.ProyectoUnidadVinculacionRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoUnidadVinculacionSpecifications;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.csp.util.ProyectoHelper;
import org.crue.hercules.sgi.csp.util.SgiLogUtils;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service para gestionar {@link ProyectoUnidadVinculacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProyectoUnidadVinculacionService {

  private final ProyectoUnidadVinculacionRepository repository;
  private final ProyectoHelper proyectoHelper;

  /**
   * Obtiene las {@link ProyectoUnidadVinculacion} para un {@link Proyecto}
   * paginadas
   * y/o filtradas.
   *
   * @param proyectoId el id del {@link Proyecto}.
   * @param query      la información del filtro.
   * @param pageable   la información de la paginación.
   * @return la lista de {@link ProyectoUnidadVinculacion} del {@link Proyecto}
   *         paginadas y/o filtradas.
   */
  public Page<ProyectoUnidadVinculacion> findByProyectoId(Long proyectoId, String query, Pageable pageable) {
    log.debug("findByProyectoId - proyectoId: {}, query: {}, paging: {}",
        proyectoId, query, SgiLogUtils.pageable(pageable));

    AssertHelper.idNotNull(proyectoId, Proyecto.class);
    proyectoHelper.checkCanAccessProyecto(proyectoId);

    Specification<ProyectoUnidadVinculacion> specs = ProyectoUnidadVinculacionSpecifications.byProyectoId(proyectoId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ProyectoUnidadVinculacion> page = repository.findAll(specs, pageable);
    log.debug("findByProyectoId - response: {}", SgiLogUtils.page(page));
    return page;
  }

  /**
   * Actualiza la lista de {@link ProyectoUnidadVinculacion} del {@link Proyecto}.
   * Elimina las que ya no están en la nueva lista y añade las que son nuevas.
   *
   * @param proyectoId          el id del {@link Proyecto}.
   * @param unidadesVinculacion la lista con las nuevas unidades de vinculación.
   * @return La lista actualizada de {@link ProyectoUnidadVinculacion}.
   */
  @Transactional
  public List<ProyectoUnidadVinculacion> updateUnidadesVinculacion(Long proyectoId,
      List<ProyectoUnidadVinculacion> unidadesVinculacion) {
    log.debug("updateUnidadesVinculacion - proyectoId: {}, unidadesVinculacion: {}",
        proyectoId, unidadesVinculacion);

    AssertHelper.idNotNull(proyectoId, Proyecto.class);

    List<ProyectoUnidadVinculacion> existing = repository.findAll(
        ProyectoUnidadVinculacionSpecifications.byProyectoId(proyectoId));

    Set<String> existingRefs = existing.stream()
        .map(ProyectoUnidadVinculacion::getUnidadVinculacionRef)
        .collect(Collectors.toSet());

    Set<String> newRefs = new LinkedHashSet<>();
    unidadesVinculacion.forEach(u -> newRefs.add(u.getUnidadVinculacionRef()));

    List<ProyectoUnidadVinculacion> toDelete = existing.stream()
        .filter(u -> !newRefs.contains(u.getUnidadVinculacionRef()))
        .toList();
    if (!toDelete.isEmpty()) {
      repository.deleteAll(toDelete);
    }

    List<ProyectoUnidadVinculacion> toAdd = newRefs.stream()
        .filter(ref -> !existingRefs.contains(ref))
        .map(ref -> ProyectoUnidadVinculacion.builder()
            .proyectoId(proyectoId)
            .unidadVinculacionRef(ref)
            .build())
        .toList();
    List<ProyectoUnidadVinculacion> saved = toAdd.isEmpty()
        ? Collections.emptyList()
        : repository.saveAll(toAdd);

    List<ProyectoUnidadVinculacion> result = new ArrayList<>(existing.size());
    existing.stream()
        .filter(u -> newRefs.contains(u.getUnidadVinculacionRef()))
        .forEach(result::add);
    result.addAll(saved);

    log.debug("updateUnidadesVinculacion - response: {}", result);
    return result;
  }

}
