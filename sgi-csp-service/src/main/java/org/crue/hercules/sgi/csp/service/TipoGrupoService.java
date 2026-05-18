package org.crue.hercules.sgi.csp.service;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.csp.exceptions.TipoGrupoNotFoundException;
import org.crue.hercules.sgi.csp.model.BaseActivableEntity;
import org.crue.hercules.sgi.csp.model.BaseEntity;
import org.crue.hercules.sgi.csp.model.TipoGrupo;
import org.crue.hercules.sgi.csp.repository.TipoGrupoRepository;
import org.crue.hercules.sgi.csp.repository.specification.TipoGrupoSpecifications;
import org.crue.hercules.sgi.csp.util.AssertHelper;
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
 * Service para la gestión de {@link TipoGrupo}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class TipoGrupoService {

  private final Validator validator;
  private final TipoGrupoRepository repository;

  /**
   * Guarda la entidad {@link TipoGrupo}.
   * 
   * @param tipoGrupo la entidad {@link TipoGrupo} a guardar.
   * @return la entidad {@link TipoGrupo} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public TipoGrupo create(@Valid TipoGrupo tipoGrupo) {
    log.debug("create({}) - start", tipoGrupo);

    AssertHelper.idIsNull(tipoGrupo.getId(), TipoGrupo.class);

    tipoGrupo.setActivo(Boolean.TRUE);
    TipoGrupo returnValue = repository.save(tipoGrupo);

    log.debug("create({}) - end", returnValue);
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link TipoGrupo}.
   *
   * @param tipoGrupo {@link TipoGrupo} con los datos actualizados.
   * @return {@link TipoGrupo} actualizado.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public TipoGrupo update(@Valid TipoGrupo tipoGrupo) {
    log.debug("update({}) - start", tipoGrupo);

    AssertHelper.idNotNull(tipoGrupo.getId(), TipoGrupo.class);

    return repository.findById(tipoGrupo.getId()).map(currentTipoGrupo -> {
      currentTipoGrupo.setNombre(tipoGrupo.getNombre());
      currentTipoGrupo.setDescripcion(tipoGrupo.getDescripcion());

      TipoGrupo returnValue = repository.save(currentTipoGrupo);
      log.debug("update({}) - end", returnValue);
      return returnValue;
    }).orElseThrow(() -> new TipoGrupoNotFoundException(tipoGrupo.getId()));
  }

  /**
   * Reactiva el {@link TipoGrupo}.
   *
   * @param id Id del {@link TipoGrupo}.
   * @return la entidad {@link TipoGrupo} persistida.
   */
  @Transactional
  public TipoGrupo enable(Long id) {
    log.debug("enable({}) - start", id);

    AssertHelper.idNotNull(id, TipoGrupo.class);

    return repository.findById(id).map(tipoGrupo -> {
      if (Boolean.TRUE.equals(tipoGrupo.getActivo())) {
        return tipoGrupo;
      }

      // Invocar validaciones asociadas a OnActivar
      Set<ConstraintViolation<TipoGrupo>> result = validator.validate(
          tipoGrupo,
          BaseActivableEntity.OnActivar.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      tipoGrupo.setActivo(true);
      TipoGrupo returnValue = repository.save(tipoGrupo);
      log.debug("enable({}) - end", id);
      return returnValue;
    }).orElseThrow(() -> new TipoGrupoNotFoundException(id));
  }

  /**
   * Desactiva el {@link TipoGrupo}.
   *
   * @param id Id del {@link TipoGrupo}.
   * @return la entidad {@link TipoGrupo} persistida.
   */
  @Transactional
  public TipoGrupo disable(Long id) {
    log.debug("disable({}) - start", id);

    AssertHelper.idNotNull(id, TipoGrupo.class);

    return repository.findById(id).map(tipoGrupo -> {
      if (Boolean.FALSE.equals(tipoGrupo.getActivo())) {
        return tipoGrupo;
      }

      tipoGrupo.setActivo(false);
      TipoGrupo returnValue = repository.save(tipoGrupo);
      log.debug("disable({}) - end", id);
      return returnValue;
    }).orElseThrow(() -> new TipoGrupoNotFoundException(id));
  }

  /**
   * Obtiene todas las entidades {@link TipoGrupo} activas paginadas y
   * filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link TipoGrupo} paginadas y filtradas.
   */
  public Page<TipoGrupo> findAll(String query, Pageable paging) {
    log.debug("findAll({}, {}) - start", query, paging);
    Specification<TipoGrupo> specs = TipoGrupoSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<TipoGrupo> returnValue = repository.findAll(specs, paging);
    log.debug("findAll({}, {}) - end", query, paging);
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link TipoGrupo} paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link TipoGrupo} paginadas y filtradas.
   */
  public Page<TipoGrupo> findAllTodos(String query, Pageable paging) {
    log.debug("findAllTodos({}, {}) - start", query, paging);
    Specification<TipoGrupo> specs = SgiRSQLJPASupport.toSpecification(query);
    Page<TipoGrupo> returnValue = repository.findAll(specs, paging);
    log.debug("findAllTodos({}, {}) - end", query, paging);
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link TipoGrupo} por id.
   * 
   * @param id Identificador de la entidad {@link TipoGrupo}.
   * @return la entidad {@link TipoGrupo}.
   */
  public TipoGrupo findById(Long id) {
    log.debug("findById({}) - start", id);
    final TipoGrupo returnValue = repository.findById(id)
        .orElseThrow(() -> new TipoGrupoNotFoundException(id));
    log.debug("findById({}) - end", id);
    return returnValue;
  }

}
