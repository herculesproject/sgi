package org.crue.hercules.sgi.csp.service;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.csp.exceptions.TipoDescriptorGrupoNotFoundException;
import org.crue.hercules.sgi.csp.model.BaseActivableEntity;
import org.crue.hercules.sgi.csp.model.BaseEntity;
import org.crue.hercules.sgi.csp.model.TipoDescriptorGrupo;
import org.crue.hercules.sgi.csp.repository.TipoDescriptorGrupoRepository;
import org.crue.hercules.sgi.csp.repository.specification.TipoDescriptorGrupoSpecifications;
import org.crue.hercules.sgi.csp.util.AssertHelper;
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
 * Service para la gestión de {@link TipoDescriptorGrupo}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class TipoDescriptorGrupoService {

  private final Validator validator;
  private final TipoDescriptorGrupoRepository repository;

  /**
   * Guarda la entidad {@link TipoDescriptorGrupo}.
   *
   * @param tipoDescriptorGrupo la entidad {@link TipoDescriptorGrupo} a guardar.
   * @return la entidad {@link TipoDescriptorGrupo} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public TipoDescriptorGrupo create(@Valid TipoDescriptorGrupo tipoDescriptorGrupo) {
    log.debug("create - data: {}", tipoDescriptorGrupo);

    AssertHelper.idIsNull(tipoDescriptorGrupo.getId(), TipoDescriptorGrupo.class);

    tipoDescriptorGrupo.setActivo(Boolean.TRUE);
    TipoDescriptorGrupo saved = repository.save(tipoDescriptorGrupo);

    log.debug("create - id: {}", saved.getId());
    return saved;
  }

  /**
   * Actualiza los datos del {@link TipoDescriptorGrupo}.
   *
   * @param tipoDescriptorGrupo la entidad {@link TipoDescriptorGrupo} con los
   *                            datos actualizados.
   * @return la entidad {@link TipoDescriptorGrupo} actualizada.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public TipoDescriptorGrupo update(@Valid TipoDescriptorGrupo tipoDescriptorGrupo) {
    log.debug("update - id: {}, data: {}", tipoDescriptorGrupo.getId(), tipoDescriptorGrupo);

    AssertHelper.idNotNull(tipoDescriptorGrupo.getId(), TipoDescriptorGrupo.class);

    return repository.findById(tipoDescriptorGrupo.getId()).map(data -> {
      data.setNombre(tipoDescriptorGrupo.getNombre());

      return repository.save(data);
    }).orElseThrow(() -> new TipoDescriptorGrupoNotFoundException(tipoDescriptorGrupo.getId()));
  }

  /**
   * Reactiva el {@link TipoDescriptorGrupo} con el id indicado.
   *
   * @param id identificador del {@link TipoDescriptorGrupo} a reactivar.
   * @return el {@link TipoDescriptorGrupo} reactivado.
   */
  @Transactional
  public TipoDescriptorGrupo enable(Long id) {
    log.debug("enable - id: {}", id);

    AssertHelper.idNotNull(id, TipoDescriptorGrupo.class);

    return repository.findById(id).map(tipoDescriptorGrupo -> {
      if (Boolean.TRUE.equals(tipoDescriptorGrupo.getActivo())) {
        return tipoDescriptorGrupo;
      }

      Set<ConstraintViolation<TipoDescriptorGrupo>> result = validator.validate(tipoDescriptorGrupo,
          BaseActivableEntity.OnActivar.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      tipoDescriptorGrupo.setActivo(true);
      return repository.save(tipoDescriptorGrupo);
    }).orElseThrow(() -> new TipoDescriptorGrupoNotFoundException(id));
  }

  /**
   * Desactiva el {@link TipoDescriptorGrupo} con el id indicado.
   *
   * @param id identificador del {@link TipoDescriptorGrupo} a desactivar.
   * @return el {@link TipoDescriptorGrupo} desactivado.
   */
  @Transactional
  public TipoDescriptorGrupo disable(Long id) {
    log.debug("disable - id: {}", id);

    AssertHelper.idNotNull(id, TipoDescriptorGrupo.class);

    return repository.findById(id).map(tipoDescriptorGrupo -> {
      if (Boolean.FALSE.equals(tipoDescriptorGrupo.getActivo())) {
        return tipoDescriptorGrupo;
      }

      tipoDescriptorGrupo.setActivo(false);
      return repository.save(tipoDescriptorGrupo);
    }).orElseThrow(() -> new TipoDescriptorGrupoNotFoundException(id));
  }

  /**
   * Obtiene todos los {@link TipoDescriptorGrupo} activos.
   *
   * @param query  filtro de búsqueda.
   * @param paging paginación.
   * @return página de {@link TipoDescriptorGrupo} activos.
   */
  public Page<TipoDescriptorGrupo> findAll(String query, Pageable paging) {
    log.debug("findAll - query: {}, paging: {}", query, SgiLogUtils.pageable(paging));
    Specification<TipoDescriptorGrupo> specs = TipoDescriptorGrupoSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<TipoDescriptorGrupo> page = repository.findAll(specs, paging);
    log.debug("findAll - response: {}", SgiLogUtils.page(page));
    return page;
  }

  /**
   * Obtiene todos los {@link TipoDescriptorGrupo} independientemente de su
   * estado.
   *
   * @param query  filtro de búsqueda.
   * @param paging paginación.
   * @return página de {@link TipoDescriptorGrupo}.
   */
  public Page<TipoDescriptorGrupo> findAllTodos(String query, Pageable paging) {
    log.debug("findAllTodos - query: {}, paging: {}", query, SgiLogUtils.pageable(paging));
    Specification<TipoDescriptorGrupo> specs = SgiRSQLJPASupport.toSpecification(query);
    Page<TipoDescriptorGrupo> page = repository.findAll(specs, paging);
    log.debug("findAllTodos - response: {}", SgiLogUtils.page(page));
    return page;
  }

  /**
   * Obtiene el {@link TipoDescriptorGrupo} con el id indicado.
   *
   * @param id identificador del {@link TipoDescriptorGrupo} a recuperar.
   * @return el {@link TipoDescriptorGrupo} encontrado.
   */
  public TipoDescriptorGrupo findById(Long id) {
    log.debug("findById - id: {}", id);
    return repository.findById(id)
        .orElseThrow(() -> new TipoDescriptorGrupoNotFoundException(id));
  }

}
