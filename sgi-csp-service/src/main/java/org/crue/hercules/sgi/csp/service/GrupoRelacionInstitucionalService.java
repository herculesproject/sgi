package org.crue.hercules.sgi.csp.service;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.exceptions.GrupoRelacionInstitucionalNotFoundException;
import org.crue.hercules.sgi.csp.model.BaseEntity;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoRelacionInstitucional;
import org.crue.hercules.sgi.csp.repository.GrupoRelacionInstitucionalRepository;
import org.crue.hercules.sgi.csp.repository.specification.GrupoRelacionInstitucionalSpecifications;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.csp.util.GrupoAuthorityHelper;
import org.crue.hercules.sgi.csp.util.SgiLogUtils;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service para la gestión de {@link GrupoRelacionInstitucional}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class GrupoRelacionInstitucionalService {

  private final GrupoRelacionInstitucionalRepository repository;
  private final GrupoAuthorityHelper authorityHelper;

  /**
   * Guarda la entidad {@link GrupoRelacionInstitucional}.
   *
   * @param grupoRelacionInstitucional la entidad
   *                                   {@link GrupoRelacionInstitucional} a
   *                                   guardar.
   * @return la entidad {@link GrupoRelacionInstitucional} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public GrupoRelacionInstitucional create(@Valid GrupoRelacionInstitucional grupoRelacionInstitucional) {
    log.debug("create - data: {}", grupoRelacionInstitucional);

    AssertHelper.idIsNull(grupoRelacionInstitucional.getId(), GrupoRelacionInstitucional.class);
    authorityHelper.checkUserHasAuthorityViewGrupo(grupoRelacionInstitucional.getGrupoId());

    GrupoRelacionInstitucional saved = repository.save(grupoRelacionInstitucional);
    log.debug("create - id: {}", saved.getId());
    return saved;
  }

  /**
   * Actualiza los datos del {@link GrupoRelacionInstitucional}.
   *
   * @param grupoRelacionInstitucionalActualizar {@link GrupoRelacionInstitucional}
   *                                             con los datos actualizados.
   * @return {@link GrupoRelacionInstitucional} actualizado.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public GrupoRelacionInstitucional update(@Valid GrupoRelacionInstitucional grupoRelacionInstitucionalActualizar) {
    log.debug("update - id: {}, data: {}",
        grupoRelacionInstitucionalActualizar.getId(), grupoRelacionInstitucionalActualizar);

    AssertHelper.idNotNull(grupoRelacionInstitucionalActualizar.getId(), GrupoRelacionInstitucional.class);
    authorityHelper.checkUserHasAuthorityViewGrupo(grupoRelacionInstitucionalActualizar.getGrupoId());

    return repository.findById(grupoRelacionInstitucionalActualizar.getId()).map(data -> {
      data.setEntidadRef(grupoRelacionInstitucionalActualizar.getEntidadRef());
      data.setInstitucion(grupoRelacionInstitucionalActualizar.getInstitucion());
      return repository.save(data);
    }).orElseThrow(() -> new GrupoRelacionInstitucionalNotFoundException(grupoRelacionInstitucionalActualizar.getId()));
  }

  /**
   * Obtiene una entidad {@link GrupoRelacionInstitucional} por id.
   *
   * @param id Identificador de la entidad {@link GrupoRelacionInstitucional}.
   * @return la entidad {@link GrupoRelacionInstitucional}.
   */
  public GrupoRelacionInstitucional findById(Long id) {
    log.debug("findById - id: {}", id);

    AssertHelper.idNotNull(id, GrupoRelacionInstitucional.class);
    GrupoRelacionInstitucional grupoRelacionInstitucional = repository.findById(id)
        .orElseThrow(() -> new GrupoRelacionInstitucionalNotFoundException(id));

    authorityHelper.checkUserHasAuthorityViewGrupo(grupoRelacionInstitucional.getGrupoId());

    return grupoRelacionInstitucional;
  }

  /**
   * Elimina el {@link GrupoRelacionInstitucional}.
   *
   * @param id Id del {@link GrupoRelacionInstitucional}.
   */
  @Transactional
  public void deleteById(Long id) {
    log.debug("deleteById - id: {}", id);

    AssertHelper.idNotNull(id, GrupoRelacionInstitucional.class);

    GrupoRelacionInstitucional grupoRelacionInstitucional = repository.findById(id)
        .orElseThrow(() -> new GrupoRelacionInstitucionalNotFoundException(id));
    authorityHelper.checkUserHasAuthorityViewGrupo(grupoRelacionInstitucional.getGrupoId());

    repository.deleteById(id);
  }

  /**
   * Obtener todas las entidades {@link GrupoRelacionInstitucional} paginadas y/o
   * filtradas del {@link Grupo}.
   *
   * @param grupoId Identificador de la entidad {@link Grupo}.
   * @param paging  la información de la paginación.
   * @param query   la información del filtro.
   * @return la lista de entidades {@link GrupoRelacionInstitucional} paginadas
   *         y/o filtradas.
   */
  public Page<GrupoRelacionInstitucional> findAllByGrupo(Long grupoId, String query, Pageable paging) {
    log.debug("findAllByGrupo - grupoId: {}, query: {}, paging: {}",
        grupoId, query, SgiLogUtils.pageable(paging));

    AssertHelper.idNotNull(grupoId, Grupo.class);
    authorityHelper.checkUserHasAuthorityViewGrupo(grupoId);

    Specification<GrupoRelacionInstitucional> specs = GrupoRelacionInstitucionalSpecifications.byGrupoId(grupoId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<GrupoRelacionInstitucional> page = repository.findAll(specs, paging);
    log.debug("findAllByGrupo - response: {}", SgiLogUtils.page(page));
    return page;
  }

}
