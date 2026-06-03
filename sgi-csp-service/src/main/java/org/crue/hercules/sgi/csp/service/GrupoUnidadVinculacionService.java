package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoUnidadVinculacion;
import org.crue.hercules.sgi.csp.repository.GrupoUnidadVinculacionRepository;
import org.crue.hercules.sgi.csp.repository.specification.GrupoUnidadVinculacionSpecifications;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.csp.util.GrupoAuthorityHelper;
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
 * Service para gestionar {@link GrupoUnidadVinculacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GrupoUnidadVinculacionService {

  private final GrupoUnidadVinculacionRepository repository;
  private final GrupoAuthorityHelper authorityHelper;

  /**
   * Obtiene las {@link GrupoUnidadVinculacion} para un {@link Grupo} paginadas
   * y/o filtradas.
   *
   * @param grupoId  el id de la {@link Grupo}.
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de {@link GrupoUnidadVinculacion} del {@link Grupo}
   *         paginadas y/o filtradas.
   */
  public Page<GrupoUnidadVinculacion> findByGrupoId(Long grupoId, String query, Pageable pageable) {
    log.debug("findByGrupoId - grupoId: {}, query: {}, paging: {}",
        grupoId, query, SgiLogUtils.pageable(pageable));

    AssertHelper.idNotNull(grupoId, Grupo.class);
    authorityHelper.checkUserHasAuthorityViewGrupo(grupoId);

    Specification<GrupoUnidadVinculacion> specs = GrupoUnidadVinculacionSpecifications.byGrupoId(grupoId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<GrupoUnidadVinculacion> page = repository.findAll(specs, pageable);
    log.debug("findByGrupoId - response: {}", SgiLogUtils.page(page));
    return page;
  }

  /**
   * Actualiza la lista de {@link GrupoUnidadVinculacion} del {@link Grupo}.
   * Elimina las que ya no están en la nueva lista y añade las que son nuevas,
   * preservando los datos de auditoría de las no modificadas.
   *
   * @param grupoId             el id del {@link Grupo}.
   * @param unidadesVinculacion la lista con las nuevas unidades de vinculación.
   * @return La lista actualizada de {@link GrupoUnidadVinculacion}.
   */
  @Transactional
  public List<GrupoUnidadVinculacion> updateUnidadesVinculacion(Long grupoId,
      List<GrupoUnidadVinculacion> unidadesVinculacion) {
    log.debug("updateUnidadesVinculacion - grupoId: {}, unidadesVinculacion: {}", grupoId, unidadesVinculacion);

    AssertHelper.idNotNull(grupoId, Grupo.class);
    authorityHelper.checkUserHasAuthorityViewGrupo(grupoId);

    List<GrupoUnidadVinculacion> existing = repository.findAll(
        GrupoUnidadVinculacionSpecifications.byGrupoId(grupoId));

    Set<String> existingRefs = existing.stream()
        .map(GrupoUnidadVinculacion::getUnidadVinculacionRef)
        .collect(Collectors.toSet());

    Set<String> newRefs = new LinkedHashSet<>();
    unidadesVinculacion.forEach(u -> newRefs.add(u.getUnidadVinculacionRef()));

    List<GrupoUnidadVinculacion> toDelete = existing.stream()
        .filter(u -> !newRefs.contains(u.getUnidadVinculacionRef()))
        .toList();
    if (!toDelete.isEmpty()) {
      repository.deleteAll(toDelete);
    }

    List<GrupoUnidadVinculacion> toAdd = newRefs.stream()
        .filter(ref -> !existingRefs.contains(ref))
        .map(ref -> GrupoUnidadVinculacion.builder()
            .grupoId(grupoId)
            .unidadVinculacionRef(ref)
            .build())
        .toList();
    List<GrupoUnidadVinculacion> saved = toAdd.isEmpty()
        ? Collections.emptyList()
        : repository.saveAll(toAdd);

    List<GrupoUnidadVinculacion> result = new ArrayList<>(existing.size());
    existing.stream()
        .filter(u -> newRefs.contains(u.getUnidadVinculacionRef()))
        .forEach(result::add);
    result.addAll(saved);

    log.debug("updateUnidadesVinculacion - response: {}", result);
    return result;
  }

}
