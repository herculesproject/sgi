package org.crue.hercules.sgi.csp.service;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.csp.exceptions.TipoConfidencialidadNotFoundException;
import org.crue.hercules.sgi.csp.model.BaseActivableEntity;
import org.crue.hercules.sgi.csp.model.BaseEntity;
import org.crue.hercules.sgi.csp.model.TipoConfidencialidad;
import org.crue.hercules.sgi.csp.repository.TipoConfidencialidadRepository;
import org.crue.hercules.sgi.csp.repository.specification.TipoConfidencialidadSpecifications;
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
 * Service para la gestión de {@link TipoConfidencialidad}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class TipoConfidencialidadService {

  private final Validator validator;
  private final TipoConfidencialidadRepository repository;

  /**
   * Crea la entidad {@link TipoConfidencialidad}.
   *
   * @param tipoConfidencialidad la entidad {@link TipoConfidencialidad} a
   *                             guardar.
   * @return la entidad {@link TipoConfidencialidad} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public TipoConfidencialidad create(@Valid TipoConfidencialidad tipoConfidencialidad) {
    log.debug("create - data: {}", tipoConfidencialidad);

    AssertHelper.idIsNull(tipoConfidencialidad.getId(), TipoConfidencialidad.class);

    tipoConfidencialidad.setActivo(Boolean.TRUE);
    TipoConfidencialidad saved = repository.save(tipoConfidencialidad);

    log.debug("create - id: {}", saved.getId());
    return saved;
  }

  /**
   * Actualiza los datos del {@link TipoConfidencialidad}.
   *
   * @param tipoConfidencialidad la entidad {@link TipoConfidencialidad} con los
   *                             datos actualizados.
   * @return la entidad {@link TipoConfidencialidad} actualizada.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public TipoConfidencialidad update(@Valid TipoConfidencialidad tipoConfidencialidad) {
    log.debug("update - id: {}, data: {}", tipoConfidencialidad.getId(), tipoConfidencialidad);

    AssertHelper.idNotNull(tipoConfidencialidad.getId(), TipoConfidencialidad.class);

    return repository.findById(tipoConfidencialidad.getId()).map(data -> {
      data.setNombre(tipoConfidencialidad.getNombre());

      return repository.save(data);
    }).orElseThrow(() -> new TipoConfidencialidadNotFoundException(tipoConfidencialidad.getId()));
  }

  /**
   * Reactiva el {@link TipoConfidencialidad} con el id indicado.
   *
   * @param id identificador del {@link TipoConfidencialidad} a reactivar.
   * @return el {@link TipoConfidencialidad} reactivado.
   */
  @Transactional
  public TipoConfidencialidad enable(Long id) {
    log.debug("enable - id: {}", id);

    AssertHelper.idNotNull(id, TipoConfidencialidad.class);

    return repository.findById(id).map(tipoConfidencialidad -> {
      if (Boolean.TRUE.equals(tipoConfidencialidad.getActivo())) {
        return tipoConfidencialidad;
      }

      Set<ConstraintViolation<TipoConfidencialidad>> result = validator.validate(tipoConfidencialidad,
          BaseActivableEntity.OnActivar.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      tipoConfidencialidad.setActivo(true);
      return repository.save(tipoConfidencialidad);
    }).orElseThrow(() -> new TipoConfidencialidadNotFoundException(id));
  }

  /**
   * Desactiva el {@link TipoConfidencialidad} con el id indicado.
   *
   * @param id identificador del {@link TipoConfidencialidad} a desactivar.
   * @return el {@link TipoConfidencialidad} desactivado.
   */
  @Transactional
  public TipoConfidencialidad disable(Long id) {
    log.debug("disable - id: {}", id);

    AssertHelper.idNotNull(id, TipoConfidencialidad.class);

    return repository.findById(id).map(tipoConfidencialidad -> {
      if (Boolean.FALSE.equals(tipoConfidencialidad.getActivo())) {
        return tipoConfidencialidad;
      }

      tipoConfidencialidad.setActivo(false);
      return repository.save(tipoConfidencialidad);
    }).orElseThrow(() -> new TipoConfidencialidadNotFoundException(id));
  }

  /**
   * Obtiene todos los {@link TipoConfidencialidad} activos.
   *
   * @param query  filtro de búsqueda.
   * @param paging paginación.
   * @return página de {@link TipoConfidencialidad} activos.
   */
  public Page<TipoConfidencialidad> findAll(String query, Pageable paging) {
    log.debug("findAll - query: {}, paging: {}", query, SgiLogUtils.pageable(paging));
    Specification<TipoConfidencialidad> specs = TipoConfidencialidadSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<TipoConfidencialidad> page = repository.findAll(specs, paging);
    log.debug("findAll - response: {}", SgiLogUtils.page(page));
    return page;
  }

  /**
   * Obtiene todos los {@link TipoConfidencialidad}.
   *
   * @param query  filtro de búsqueda.
   * @param paging paginación.
   * @return página de {@link TipoConfidencialidad}.
   */
  public Page<TipoConfidencialidad> findAllTodos(String query, Pageable paging) {
    log.debug("findAllTodos - query: {}, paging: {}", query, SgiLogUtils.pageable(paging));
    Specification<TipoConfidencialidad> specs = SgiRSQLJPASupport.toSpecification(query);
    Page<TipoConfidencialidad> page = repository.findAll(specs, paging);
    log.debug("findAllTodos - response: {}", SgiLogUtils.page(page));
    return page;
  }

  /**
   * Obtiene el {@link TipoConfidencialidad} por id.
   *
   * @param id identificador del {@link TipoConfidencialidad}.
   * @return el {@link TipoConfidencialidad} con el id indicado.
   */
  public TipoConfidencialidad findById(Long id) {
    log.debug("findById - id: {}", id);
    return repository.findById(id)
        .orElseThrow(() -> new TipoConfidencialidadNotFoundException(id));
  }

}
