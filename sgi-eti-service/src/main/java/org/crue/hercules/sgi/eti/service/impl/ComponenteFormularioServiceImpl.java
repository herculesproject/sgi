package org.crue.hercules.sgi.eti.service.impl;

import java.util.List;
import org.crue.hercules.sgi.eti.exceptions.ComponenteFormularioNotFoundException;
import org.crue.hercules.sgi.eti.model.ComponenteFormulario;
import org.crue.hercules.sgi.eti.repository.ComponenteFormularioRepository;
import org.crue.hercules.sgi.eti.service.ComponenteFormularioService;
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
 * Service Implementation para gestion {@link ComponenteFormulario}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ComponenteFormularioServiceImpl implements ComponenteFormularioService {

  private final ComponenteFormularioRepository repository;

  public ComponenteFormularioServiceImpl(ComponenteFormularioRepository repository) {
    this.repository = repository;
  }

  /**
   * Crea {@link ComponenteFormulario}.
   *
   * @param componenteFormulario La entidad {@link ComponenteFormulario} a crear.
   * @return La entidad {@link ComponenteFormulario} creada.
   * @throws IllegalArgumentException Si la entidad {@link ComponenteFormulario}
   *                                  tiene id.
   */
  @Override
  @Transactional
  public ComponenteFormulario create(ComponenteFormulario componenteFormulario) {
    log.debug("create(ComponenteFormulario componenteFormulario) - start");
    Assert.isNull(componenteFormulario.getId(),
        "ComponenteFormulario id debe ser null para crear un nuevo ComponenteFormulario");
    ComponenteFormulario returnValue = repository.save(componenteFormulario);
    log.debug("create(ComponenteFormulario componenteFormulario) - end");
    return returnValue;
  }

  /**
   * Actualiza {@link ComponenteFormulario}.
   *
   * @param componenteFormularioActualizar La entidad {@link ComponenteFormulario}
   *                                       a actualizar.
   * @return La entidad {@link ComponenteFormulario} actualizada.
   * @throws ComponenteFormularioNotFoundException Si no existe ninguna entidad
   *                                               {@link ComponenteFormulario}
   *                                               con ese id.
   * @throws IllegalArgumentException              Si la entidad
   *                                               {@link ComponenteFormulario}
   *                                               entidad no tiene id.
   */
  @Override
  @Transactional
  public ComponenteFormulario update(final ComponenteFormulario componenteFormularioActualizar) {
    log.debug("update(ComponenteFormulario componenteFormularioActualizar) - start");

    Assert.notNull(componenteFormularioActualizar.getId(),
        "ComponenteFormulario id no puede ser null para actualizar un componenteFormulario");

    return repository.findById(componenteFormularioActualizar.getId()).map(componenteFormulario -> {
      componenteFormulario.setEsquema(componenteFormularioActualizar.getEsquema());

      ComponenteFormulario returnValue = repository.save(componenteFormulario);
      log.debug("update(ComponenteFormulario componenteFormularioActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ComponenteFormularioNotFoundException(componenteFormularioActualizar.getId()));
  }

  /**
   * Elimina todas las entidades {@link ComponenteFormulario}.
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
   * Elimina {@link ComponenteFormulario} por id.
   *
   * @param id El id de la entidad {@link ComponenteFormulario}.
   * @throws ComponenteFormularioNotFoundException Si no existe ninguna entidad
   *                                               {@link ComponenteFormulario}
   *                                               con ese id.
   * @throws IllegalArgumentException              Si no se informa Id.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");
    Assert.notNull(id, "ComponenteFormulario id no puede ser null para eliminar una componenteFormulario");
    if (!repository.existsById(id)) {
      throw new ComponenteFormularioNotFoundException(id);
    }
    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene las entidades {@link ComponenteFormulario} filtradas y paginadas
   * según los criterios de búsqueda.
   *
   * @param query  filtro de {@link QueryCriteria}.
   * @param paging pageable
   * @return el listado de entidades {@link ComponenteFormulario} paginadas y
   *         filtradas.
   */
  @Override
  public Page<ComponenteFormulario> findAll(List<QueryCriteria> query, Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query, Pageable paging) - start");

    Specification<ComponenteFormulario> spec = new QuerySpecification<ComponenteFormulario>(query);
    Page<ComponenteFormulario> returnValue = repository.findAll(spec, paging);

    log.debug("findAll(List<QueryCriteria> query, Pageable paging) - end");

    return returnValue;
  }

  /**
   * Obtiene {@link ComponenteFormulario} por id.
   *
   * @param id El id de la entidad {@link ComponenteFormulario}.
   * @return La entidad {@link ComponenteFormulario}.
   * @throws ComponenteFormularioNotFoundException Si no existe ninguna entidad
   *                                               {@link ComponenteFormulario}
   *                                               con ese id.
   * @throws IllegalArgumentException              Si no se informa Id.
   */
  @Override
  public ComponenteFormulario findById(final Long id) {
    log.debug("findById(final Long id) - start");
    Assert.notNull(id, "ComponenteFormulario id no puede ser null para buscar una componenteFormulario por Id");
    final ComponenteFormulario componenteFormulario = repository.findById(id)
        .orElseThrow(() -> new ComponenteFormularioNotFoundException(id));
    log.debug("findById(final Long id) - end");
    return componenteFormulario;
  }

}
