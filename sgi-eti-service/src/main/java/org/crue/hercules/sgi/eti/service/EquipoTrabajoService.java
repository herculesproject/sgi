package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.model.EquipoTrabajo;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.EquipoTrabajoNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link EquipoTrabajo}.
 */
public interface EquipoTrabajoService {
  /**
   * Guardar {@link EquipoTrabajo}.
   *
   * @param equipoTrabajo la entidad {@link EquipoTrabajo} a guardar.
   * @return la entidad {@link EquipoTrabajo} persistida.
   */
  EquipoTrabajo create(EquipoTrabajo equipoTrabajo);

  /**
   * Actualizar {@link EquipoTrabajo}.
   *
   * @param equipoTrabajo la entidad {@link EquipoTrabajo} a actualizar.
   * @return la entidad {@link EquipoTrabajo} persistida.
   */
  EquipoTrabajo update(EquipoTrabajo equipoTrabajo);

  /**
   * Obtener todas las entidades {@link EquipoTrabajo} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link EquipoTrabajo} paginadas y/o filtradas.
   */
  Page<EquipoTrabajo> findAll(List<QueryCriteria> query, Pageable pageable);

  /**
   * Obtiene {@link EquipoTrabajo} por id.
   *
   * @param id el id de la entidad {@link EquipoTrabajo}.
   * @return la entidad {@link EquipoTrabajo}.
   */
  EquipoTrabajo findById(Long id);

  /**
   * Elimina el {@link EquipoTrabajo} por id.
   *
   * @param id el id de la entidad {@link EquipoTrabajo}.
   */
  void delete(Long id) throws EquipoTrabajoNotFoundException;

  /**
   * Obtener todas las entidades paginadas {@link EquipoTrabajo} activas para una
   * determinada {@link PeticionEvaluacion}.
   *
   * @param id       Id de {@link PeticionEvaluacion}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link EquipoTrabajo} paginadas.
   */
  Page<EquipoTrabajo> findAllByPeticionEvaluacionId(Long id, Pageable pageable);

}