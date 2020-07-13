package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.MemoriaNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link Memoria}.
 */
public interface MemoriaService {
  /**
   * Guardar {@link Memoria}.
   *
   * @param memoria la entidad {@link Memoria} a guardar.
   * @return la entidad {@link Memoria} persistida.
   */
  Memoria create(Memoria memoria);

  /**
   * Actualizar {@link Memoria}.
   *
   * @param memoria la entidad {@link Memoria} a actualizar.
   * @return la entidad {@link Memoria} persistida.
   */
  Memoria update(Memoria memoria);

  /**
   * Obtener todas las entidades {@link Memoria} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Memoria} paginadas y/o filtradas.
   */
  Page<Memoria> findAll(List<QueryCriteria> query, Pageable pageable);

  /**
   * Obtiene {@link Memoria} por id.
   *
   * @param id el id de la entidad {@link Memoria}.
   * @return la entidad {@link Memoria}.
   */
  Memoria findById(Long id);

  /**
   * Elimina el {@link Memoria} por id.
   *
   * @param id el id de la entidad {@link Memoria}.
   */
  void delete(Long id) throws MemoriaNotFoundException;

  /**
   * Elimina todas las {@link Memoria}.
   */
  void deleteAll();

}