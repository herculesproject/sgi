package org.crue.hercules.sgi.eti.service.impl;

import java.util.List;
import org.crue.hercules.sgi.eti.exceptions.ApartadoFormularioNotFoundException;
import org.crue.hercules.sgi.eti.model.ApartadoFormulario;
import org.crue.hercules.sgi.eti.repository.ApartadoFormularioRepository;
import org.crue.hercules.sgi.eti.service.ApartadoFormularioService;
import org.crue.hercules.sgi.framework.data.jpa.domain.QuerySpecification;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link ApartadoFormulario}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ApartadoFormularioServiceImpl implements ApartadoFormularioService {

  private final ApartadoFormularioRepository repository;

  public ApartadoFormularioServiceImpl(ApartadoFormularioRepository repository) {
    this.repository = repository;
  }

  /**
   * Crea {@link ApartadoFormulario}.
   *
   * @param apartadoFormulario La entidad {@link ApartadoFormulario} a crear.
   * @return La entidad {@link ApartadoFormulario} creada.
   * @throws IllegalArgumentException Si la entidad {@link ApartadoFormulario}
   *                                  tiene id.
   */
  @Override
  @Transactional
  public ApartadoFormulario create(ApartadoFormulario apartadoFormulario) {
    log.debug("create(ApartadoFormulario apartadoFormulario) - start");
    Assert.isNull(apartadoFormulario.getId(), "ApartadoFormulario id must be null to create a new ApartadoFormulario");
    ApartadoFormulario returnValue = repository.save(apartadoFormulario);
    log.debug("create(ApartadoFormulario apartadoFormulario) - end");
    return returnValue;
  }

  /**
   * Actualiza {@link ApartadoFormulario}.
   *
   * @param apartadoFormularioActualizar La entidad {@link ApartadoFormulario} a
   *                                     actualizar.
   * @return La entidad {@link ApartadoFormulario} actualizada.
   * @throws ApartadoFormularioNotFoundException Si no existe ninguna entidad
   *                                             {@link ApartadoFormulario} con
   *                                             ese id.
   * @throws IllegalArgumentException            Si la entidad
   *                                             {@link ApartadoFormulario}
   *                                             entidad no tiene id.
   */
  @Override
  @Transactional
  public ApartadoFormulario update(final ApartadoFormulario apartadoFormularioActualizar) {
    log.debug("update(ApartadoFormulario apartadoFormularioActualizar) - start");

    Assert.notNull(apartadoFormularioActualizar.getId(),
        "ApartadoFormulario id tiene que ser null para crear una nueva apartadoFormulario");

    return repository.findById(apartadoFormularioActualizar.getId()).map(apartadoFormulario -> {
      apartadoFormulario.setBloqueFormulario(apartadoFormularioActualizar.getBloqueFormulario());
      apartadoFormulario.setNombre(apartadoFormularioActualizar.getNombre());
      apartadoFormulario.setApartadoFormularioPadre(apartadoFormularioActualizar.getApartadoFormularioPadre());
      apartadoFormulario.setOrden(apartadoFormularioActualizar.getOrden());
      apartadoFormulario.setComponenteFormulario(apartadoFormularioActualizar.getComponenteFormulario());
      apartadoFormulario.setActivo(apartadoFormularioActualizar.getActivo());

      ApartadoFormulario returnValue = repository.save(apartadoFormulario);
      log.debug("update(ApartadoFormulario apartadoFormularioActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ApartadoFormularioNotFoundException(apartadoFormularioActualizar.getId()));
  }

  /**
   * Elimina todas las entidades {@link ApartadoFormulario}.
   *
   */
  @Override
  @Transactional
  public void deleteAll() {
    log.debug("deleteAll() - start");
    repository.deleteAll();
    log.debug("deleteAll() - end");
  }

  /**
   * Elimina {@link ApartadoFormulario} por id.
   *
   * @param id El id de la entidad {@link ApartadoFormulario}.
   * @throws ApartadoFormularioNotFoundException Si no existe ninguna entidad
   *                                             {@link ApartadoFormulario} con
   *                                             ese id.
   * @throws IllegalArgumentException            Si no se informa Id.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");
    Assert.notNull(id, "ApartadoFormulario id no puede ser null para eliminar una apartadoFormulario");
    if (!repository.existsById(id)) {
      throw new ApartadoFormularioNotFoundException(id);
    }
    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene las entidades {@link ApartadoFormulario} filtradas y paginadas según
   * los criterios de búsqueda.
   *
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable
   * @return el listado de entidades {@link ApartadoFormulario} paginadas y
   *         filtradas.
   */
  @Override
  public Page<ApartadoFormulario> findAll(List<QueryCriteria> query, Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query, Pageable paging) - start");

    Specification<ApartadoFormulario> spec = new QuerySpecification<ApartadoFormulario>(query);
    Page<ApartadoFormulario> returnValue = repository.findAll(spec, paging);

    log.debug("findAll(List<QueryCriteria> query, Pageable paging) - end");

    return returnValue;
  }

  /**
   * Obtiene {@link ApartadoFormulario} por id.
   *
   * @param id El id de la entidad {@link ApartadoFormulario}.
   * @return La entidad {@link ApartadoFormulario}.
   * @throws ApartadoFormularioNotFoundException Si no existe ninguna entidad
   *                                             {@link ApartadoFormulario} con
   *                                             ese id.
   * @throws IllegalArgumentException            Si no se informa Id.
   */
  @Override
  public ApartadoFormulario findById(final Long id) {
    log.debug("findById(final Long id) - start");
    Assert.notNull(id, "ApartadoFormulario id no puede ser null para buscar una apartadoFormulario por Id");
    final ApartadoFormulario apartadoFormulario = repository.findById(id)
        .orElseThrow(() -> new ApartadoFormularioNotFoundException(id));
    log.debug("findById(final Long id) - end");
    return apartadoFormulario;
  }

}
