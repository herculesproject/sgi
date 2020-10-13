package org.crue.hercules.sgi.csp.service.impl;

import java.util.List;

import org.crue.hercules.sgi.csp.exceptions.ListadoAreaTematicaNotFoundException;
import org.crue.hercules.sgi.csp.model.ListadoAreaTematica;
import org.crue.hercules.sgi.csp.repository.ListadoAreaTematicaRepository;
import org.crue.hercules.sgi.csp.service.ListadoAreaTematicaService;
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
 * Service Implementation para gestion {@link ListadoAreaTematica}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ListadoAreaTematicaServiceImpl implements ListadoAreaTematicaService {

  private final ListadoAreaTematicaRepository repository;

  public ListadoAreaTematicaServiceImpl(ListadoAreaTematicaRepository repository) {
    this.repository = repository;
  }

  /**
   * Guarda la entidad {@link ListadoAreaTematica}.
   * 
   * @param listadoAreaTematica la entidad {@link ListadoAreaTematica} a guardar.
   * @return ListadoAreaTematica la entidad {@link ListadoAreaTematica} persistida.
   */
  @Override
  @Transactional
  public ListadoAreaTematica create(ListadoAreaTematica listadoAreaTematica) {
    log.debug("create(ListadoAreaTematica listadoAreaTematica) - start");

    Assert.isNull(listadoAreaTematica.getId(), "Id tiene que ser null para crear ListadoAreaTematica");
    Assert.isTrue(!(repository.findByNombre(listadoAreaTematica.getNombre()).isPresent()),
        "Ya existe ListadoAreaTematica con el nombre " + listadoAreaTematica.getNombre());

    listadoAreaTematica.setActivo(Boolean.TRUE);
    ListadoAreaTematica returnValue = repository.save(listadoAreaTematica);

    log.debug("create(ListadoAreaTematica listadoAreaTematica) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link ListadoAreaTematica}.
   * 
   * @param listadoAreaTematica listadoAreaTematicaActualizar {@link ListadoAreaTematica} con los
   *                      datos actualizados.
   * @return {@link ListadoAreaTematica} actualizado.
   */
  @Override
  @Transactional
  public ListadoAreaTematica update(ListadoAreaTematica listadoAreaTematica) {
    log.debug("update(ListadoAreaTematica listadoAreaTematica) - start");

    Assert.notNull(listadoAreaTematica.getId(), "Id no puede ser null para actualizar ListadoAreaTematica");
    repository.findByNombre(listadoAreaTematica.getNombre()).ifPresent((listadoAreaTematicaExistente) -> {
      Assert.isTrue(listadoAreaTematica.getId() == listadoAreaTematicaExistente.getId(),
          "Ya existe un ListadoAreaTematica con el nombre " + listadoAreaTematicaExistente.getNombre());
    });

    return repository.findById(listadoAreaTematica.getId()).map((data) -> {
      data.setNombre(listadoAreaTematica.getNombre());
      data.setDescripcion(listadoAreaTematica.getDescripcion());
      data.setActivo(listadoAreaTematica.getActivo());

      ListadoAreaTematica returnValue = repository.save(data);
      log.debug("update(ListadoAreaTematica listadoAreaTematica) - end");
      return returnValue;
    }).orElseThrow(() -> new ListadoAreaTematicaNotFoundException(listadoAreaTematica.getId()));
  }

  /**
   * Desactiva el {@link ListadoAreaTematica}.
   *
   * @param id Id del {@link ListadoAreaTematica}.
   * @return la entidad {@link ListadoAreaTematica} persistida.
   */
  @Override
  @Transactional
  public ListadoAreaTematica disable(Long id) {
    log.debug("disable(Long id) - start");

    Assert.notNull(id, "ListadoAreaTematica id no puede ser null para desactivar un ListadoAreaTematica");

    return repository.findById(id).map(listadoAreaTematica -> {
      listadoAreaTematica.setActivo(false);

      ListadoAreaTematica returnValue = repository.save(listadoAreaTematica);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new ListadoAreaTematicaNotFoundException(id));
  }

  /**
   * Obtiene todas las entidades {@link ListadoAreaTematica} paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link ListadoAreaTematica} paginadas y filtradas.
   */
  @Override
  public Page<ListadoAreaTematica> findAll(List<QueryCriteria> query, Pageable paging) {
    log.debug("findAll(List<QueryCriteria> query, Pageable paging) - start");
    Specification<ListadoAreaTematica> spec = new QuerySpecification<ListadoAreaTematica>(query);
    Page<ListadoAreaTematica> returnValue = repository.findAll(spec, paging);
    log.debug("findAll(List<QueryCriteria> query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link ListadoAreaTematica} por id.
   * 
   * @param id Identificador de la entidad {@link ListadoAreaTematica}.
   * @return ListadoAreaTematica la entidad {@link ListadoAreaTematica}.
   */
  @Override
  public ListadoAreaTematica findById(Long id) {
    log.debug("findById(Long id) - start");
    final ListadoAreaTematica returnValue = repository.findById(id).orElseThrow(() -> new ListadoAreaTematicaNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

}
