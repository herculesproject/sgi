package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoUnidadVinculacion;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoUnidadVinculacionRepository;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudProyectoUnidadVinculacionSpecifications;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.csp.util.SgiLogUtils;
import org.crue.hercules.sgi.csp.util.SolicitudAuthorityHelper;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service para gestionar {@link SolicitudProyectoUnidadVinculacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SolicitudProyectoUnidadVinculacionService {

  private final SolicitudProyectoUnidadVinculacionRepository repository;
  private final SolicitudAuthorityHelper authorityHelper;

  /**
   * Obtiene las {@link SolicitudProyectoUnidadVinculacion} para un
   * {@link SolicitudProyecto} paginadas y/o filtradas.
   *
   * @param solicitudProyectoId el id de la {@link SolicitudProyecto}.
   * @param query               la información del filtro.
   * @param pageable            la información de la paginación.
   * @return la lista de {@link SolicitudProyectoUnidadVinculacion} del
   *         {@link SolicitudProyecto}
   *         paginadas y/o filtradas.
   */
  public Page<SolicitudProyectoUnidadVinculacion> findBySolicitudProyectoId(Long solicitudProyectoId, String query,
      Pageable pageable) {
    log.debug("findBySolicitudProyectoId - solicitudProyectoId: {}, query: {}, paging: {}",
        solicitudProyectoId, query, SgiLogUtils.pageable(pageable));

    AssertHelper.idNotNull(solicitudProyectoId, SolicitudProyecto.class);
    authorityHelper.checkUserHasAuthorityViewSolicitud(solicitudProyectoId);

    Specification<SolicitudProyectoUnidadVinculacion> specs = SolicitudProyectoUnidadVinculacionSpecifications
        .bySolicitudProyectoId(solicitudProyectoId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<SolicitudProyectoUnidadVinculacion> page = repository.findAll(specs, pageable);
    log.debug("findBySolicitudProyectoId - response: {}", SgiLogUtils.page(page));
    return page;
  }

  /**
   * Actualiza la lista de {@link SolicitudProyectoUnidadVinculacion} del
   * {@link SolicitudProyecto}.
   * Elimina las que ya no están en la nueva lista y añade las que son nuevas.
   *
   * @param solicitudProyectoId el id del {@link SolicitudProyecto}.
   * @param unidadesVinculacion la lista con las nuevas unidades de vinculación.
   * @return La lista actualizada de {@link SolicitudProyectoUnidadVinculacion}.
   */
  @Transactional
  public List<SolicitudProyectoUnidadVinculacion> updateUnidadesVinculacion(Long solicitudProyectoId,
      List<SolicitudProyectoUnidadVinculacion> unidadesVinculacion) {
    log.debug("updateUnidadesVinculacion - solicitudProyectoId: {}, unidadesVinculacion: {}", solicitudProyectoId,
        unidadesVinculacion);

    AssertHelper.idNotNull(solicitudProyectoId, SolicitudProyecto.class);
    authorityHelper.checkUserHasAuthorityModifySolicitud(solicitudProyectoId);

    List<SolicitudProyectoUnidadVinculacion> existing = repository.findAll(
        SolicitudProyectoUnidadVinculacionSpecifications.bySolicitudProyectoId(solicitudProyectoId));

    Set<String> existingRefs = existing.stream()
        .map(SolicitudProyectoUnidadVinculacion::getUnidadVinculacionRef)
        .collect(Collectors.toSet());

    Set<String> newRefs = new LinkedHashSet<>();
    unidadesVinculacion.forEach(u -> newRefs.add(u.getUnidadVinculacionRef()));

    List<SolicitudProyectoUnidadVinculacion> toDelete = existing.stream()
        .filter(u -> !newRefs.contains(u.getUnidadVinculacionRef()))
        .toList();
    if (!toDelete.isEmpty()) {
      repository.deleteAll(toDelete);
    }

    List<SolicitudProyectoUnidadVinculacion> toAdd = newRefs.stream()
        .filter(ref -> !existingRefs.contains(ref))
        .map(ref -> SolicitudProyectoUnidadVinculacion.builder()
            .solicitudProyectoId(solicitudProyectoId)
            .unidadVinculacionRef(ref)
            .build())
        .toList();

    List<SolicitudProyectoUnidadVinculacion> saved = toAdd.isEmpty()
        ? Collections.emptyList()
        : repository.saveAll(toAdd);

    List<SolicitudProyectoUnidadVinculacion> result = new ArrayList<>(existing.size());
    existing.stream()
        .filter(u -> newRefs.contains(u.getUnidadVinculacionRef()))
        .forEach(result::add);
    result.addAll(saved);

    log.debug("updateUnidadesVinculacion - response: {}", result);
    return result;
  }

}
