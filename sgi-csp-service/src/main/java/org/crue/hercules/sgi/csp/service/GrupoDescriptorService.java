package org.crue.hercules.sgi.csp.service;

import java.util.Objects;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.exceptions.GrupoDescriptorNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.TipoDescriptorGrupoNotFoundException;
import org.crue.hercules.sgi.csp.model.BaseEntity;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoDescriptor;
import org.crue.hercules.sgi.csp.model.TipoDescriptorGrupo;
import org.crue.hercules.sgi.csp.repository.GrupoDescriptorRepository;
import org.crue.hercules.sgi.csp.repository.TipoDescriptorGrupoRepository;
import org.crue.hercules.sgi.csp.repository.specification.GrupoDescriptorSpecifications;
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
 * Service para la gestión de {@link GrupoDescriptor}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class GrupoDescriptorService {

  private final GrupoDescriptorRepository repository;
  private final TipoDescriptorGrupoRepository tipoDescriptorGrupoRepository;
  private final GrupoAuthorityHelper authorityHelper;

  /**
   * Guarda la entidad {@link GrupoDescriptor}.
   *
   * @param grupoDescriptor la entidad {@link GrupoDescriptor} a guardar.
   * @return la entidad {@link GrupoDescriptor} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public GrupoDescriptor create(@Valid GrupoDescriptor grupoDescriptor) {
    log.debug("create - data: {}", grupoDescriptor);

    AssertHelper.idIsNull(grupoDescriptor.getId(), GrupoDescriptor.class);
    authorityHelper.checkUserHasAuthorityViewGrupo(grupoDescriptor.getGrupoId());
    checkTipoDescriptorGrupoActivo(grupoDescriptor.getTipoDescriptorGrupoId());

    GrupoDescriptor saved = repository.save(grupoDescriptor);

    log.debug("create - id: {}", saved.getId());
    return saved;
  }

  /**
   * Actualiza los datos del {@link GrupoDescriptor}.
   *
   * @param grupoDescriptorActualizar la entidad {@link GrupoDescriptor} con los
   *                                  datos actualizados.
   * @return la entidad {@link GrupoDescriptor} actualizada.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public GrupoDescriptor update(@Valid GrupoDescriptor grupoDescriptorActualizar) {
    log.debug("update - id: {}, data: {}", grupoDescriptorActualizar.getId(), grupoDescriptorActualizar);

    AssertHelper.idNotNull(grupoDescriptorActualizar.getId(), GrupoDescriptor.class);
    authorityHelper.checkUserHasAuthorityViewGrupo(grupoDescriptorActualizar.getGrupoId());

    return repository.findById(grupoDescriptorActualizar.getId()).map(data -> {
      if (!Objects.equals(data.getTipoDescriptorGrupoId(), grupoDescriptorActualizar.getTipoDescriptorGrupoId())) {
        checkTipoDescriptorGrupoActivo(grupoDescriptorActualizar.getTipoDescriptorGrupoId());
      }

      data.setTipoDescriptorGrupoId(grupoDescriptorActualizar.getTipoDescriptorGrupoId());
      data.setTexto(grupoDescriptorActualizar.getTexto());

      return repository.save(data);
    }).orElseThrow(() -> new GrupoDescriptorNotFoundException(grupoDescriptorActualizar.getId()));
  }

  /**
   * Elimina el {@link GrupoDescriptor} con el id indicado.
   *
   * @param id identificador del {@link GrupoDescriptor} a eliminar.
   */
  @Transactional
  public void deleteById(Long id) {
    log.debug("deleteById - id: {}", id);

    AssertHelper.idNotNull(id, GrupoDescriptor.class);

    GrupoDescriptor grupoDescriptor = repository.findById(id)
        .orElseThrow(() -> new GrupoDescriptorNotFoundException(id));
    authorityHelper.checkUserHasAuthorityViewGrupo(grupoDescriptor.getGrupoId());

    repository.deleteById(id);
  }

  /**
   * Obtiene el {@link GrupoDescriptor} con el id indicado.
   *
   * @param id identificador del {@link GrupoDescriptor} a recuperar.
   * @return el {@link GrupoDescriptor} encontrado.
   */
  public GrupoDescriptor findById(Long id) {
    log.debug("findById - id: {}", id);

    AssertHelper.idNotNull(id, GrupoDescriptor.class);
    GrupoDescriptor grupoDescriptor = repository.findById(id)
        .orElseThrow(() -> new GrupoDescriptorNotFoundException(id));
    authorityHelper.checkUserHasAuthorityViewGrupo(grupoDescriptor.getGrupoId());
    return grupoDescriptor;
  }

  /**
   * Obtiene todos los {@link GrupoDescriptor} de un {@link Grupo}.
   *
   * @param grupoId identificador del {@link Grupo}.
   * @param query   filtro de búsqueda.
   * @param paging  paginación.
   * @return página de {@link GrupoDescriptor}.
   */
  public Page<GrupoDescriptor> findAllByGrupo(Long grupoId, String query, Pageable paging) {
    log.debug("findAllByGrupo - grupoId: {}, query: {}, paging: {}", grupoId, query, SgiLogUtils.pageable(paging));
    AssertHelper.idNotNull(grupoId, Grupo.class);
    authorityHelper.checkUserHasAuthorityViewGrupo(grupoId);

    Specification<GrupoDescriptor> specs = GrupoDescriptorSpecifications.byGrupoId(grupoId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<GrupoDescriptor> page = repository.findAll(specs, paging);
    log.debug("findAllByGrupo - response: {}", SgiLogUtils.page(page));
    return page;
  }

  /**
   * Comprueba que el {@link TipoDescriptorGrupo} existe y está activo.
   *
   * @param tipoDescriptorGrupoId Identificador del {@link TipoDescriptorGrupo}.
   * @throws TipoDescriptorGrupoNotFoundException si no existe un
   *                                              {@link TipoDescriptorGrupo} con
   *                                              el id indicado.
   */
  private void checkTipoDescriptorGrupoActivo(Long tipoDescriptorGrupoId) {
    if (tipoDescriptorGrupoId == null) {
      return;
    }

    TipoDescriptorGrupo tipoDescriptorGrupo = tipoDescriptorGrupoRepository.findById(tipoDescriptorGrupoId)
        .orElseThrow(() -> new TipoDescriptorGrupoNotFoundException(tipoDescriptorGrupoId));

    AssertHelper.entityActivo(Boolean.TRUE.equals(tipoDescriptorGrupo.getActivo()), TipoDescriptorGrupo.class,
        String.valueOf(tipoDescriptorGrupo.getId()));
  }

}
