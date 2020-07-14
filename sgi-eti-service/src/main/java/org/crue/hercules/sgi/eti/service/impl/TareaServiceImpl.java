package org.crue.hercules.sgi.eti.service.impl;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.TareaNotFoundException;
import org.crue.hercules.sgi.eti.model.Tarea;
import org.crue.hercules.sgi.eti.repository.TareaRepository;
import org.crue.hercules.sgi.eti.service.TareaService;
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
 * Service Implementation para la gestión de {@link Tarea}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class TareaServiceImpl implements TareaService {
  private final TareaRepository tareaRepository;

  public TareaServiceImpl(TareaRepository tareaRepository) {
    this.tareaRepository = tareaRepository;
  }

  /**
   * Guarda la entidad {@link Tarea}.
   *
   * @param tarea la entidad {@link Tarea} a guardar.
   * @return la entidad {@link Tarea} persistida.
   */
  @Transactional
  public Tarea create(Tarea tarea) {
    log.debug("Petición a create Tarea : {} - start", tarea);
    Assert.isNull(tarea.getId(), "Tarea id tiene que ser null para crear una nueva tarea");

    return tareaRepository.save(tarea);
  }

  /**
   * Obtiene todas las entidades {@link Tarea} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link Tarea} paginadas y filtradas.
   */
  public Page<Tarea> findAll(List<QueryCriteria> query, Pageable paging) {
    log.debug("findAllTarea(List<QueryCriteria> query, Pageable paging) - start");
    Specification<Tarea> spec = new QuerySpecification<Tarea>(query);

    Page<Tarea> returnValue = tareaRepository.findAll(spec, paging);
    log.debug("findAllTarea(List<QueryCriteria> query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link Tarea} por id.
   *
   * @param id el id de la entidad {@link Tarea}.
   * @return la entidad {@link Tarea}.
   * @throws TareaNotFoundException Si no existe ningún {@link Tarea} con ese id.
   */
  public Tarea findById(final Long id) throws TareaNotFoundException {
    log.debug("Petición a get Tarea : {}  - start", id);
    final Tarea tarea = tareaRepository.findById(id).orElseThrow(() -> new TareaNotFoundException(id));
    log.debug("Petición a get Tarea : {}  - end", id);
    return tarea;

  }

  /**
   * Elimina una entidad {@link Tarea} por id.
   *
   * @param id el id de la entidad {@link Tarea}.
   */
  @Transactional
  public void delete(Long id) throws TareaNotFoundException {
    log.debug("Petición a delete Tarea : {}  - start", id);
    Assert.notNull(id, "El id de Tarea no puede ser null.");
    if (!tareaRepository.existsById(id)) {
      throw new TareaNotFoundException(id);
    }
    tareaRepository.deleteById(id);
    log.debug("Petición a delete Tarea : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link Tarea}.
   */
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de Tarea: {} - start");
    tareaRepository.deleteAll();
    log.debug("Petición a deleteAll de Tarea: {} - end");

  }

  /**
   * Actualiza los datos del {@link Tarea}.
   * 
   * @param tareaActualizar {@link Tarea} con los datos actualizados.
   * @return El {@link Tarea} actualizado.
   * @throws TareaNotFoundException   Si no existe ningún {@link Tarea} con ese
   *                                  id.
   * @throws IllegalArgumentException Si el {@link Tarea} no tiene id.
   */
  @Transactional
  public Tarea update(final Tarea tareaActualizar) {
    log.debug("update(Tarea tareaActualizar) - start");

    Assert.notNull(tareaActualizar.getId(), "Tarea id no puede ser null para actualizar una tarea");

    return tareaRepository.findById(tareaActualizar.getId()).map(tarea -> {
      tarea.setEquipoTrabajo(tareaActualizar.getEquipoTrabajo());
      tarea.setMemoria(tareaActualizar.getMemoria());
      tarea.setTarea(tareaActualizar.getTarea());
      tarea.setFormacion(tareaActualizar.getFormacion());
      tarea.setFormacionEspecifica(tareaActualizar.getFormacionEspecifica());
      tarea.setOrganismo(tareaActualizar.getOrganismo());
      tarea.setAnio(tareaActualizar.getAnio());

      Tarea returnValue = tareaRepository.save(tarea);
      log.debug("update(Tarea tareaActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new TareaNotFoundException(tareaActualizar.getId()));
  }

}
