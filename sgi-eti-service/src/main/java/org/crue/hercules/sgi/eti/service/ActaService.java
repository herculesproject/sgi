package org.crue.hercules.sgi.eti.service;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.ActaNotFoundException;
import org.crue.hercules.sgi.eti.model.Acta;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link Acta}.
 */
public interface ActaService {

  /**
   * Guardar {@link Acta}.
   *
   * @param acta la entidad {@link Acta} a guardar.
   * @return la entidad {@link Acta} persistida.
   */
  Acta create(Acta acta);

  /**
   * Actualizar {@link Acta}.
   *
   * @param acta la entidad {@link Acta} a actualizar.
   * @return la entidad {@link Acta} persistida.
   */
  Acta update(Acta acta);

  /**
   * Obtener todas las entidades {@link Acta} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Acta} paginadas y/o filtradas.
   */
  Page<Acta> findAll(List<QueryCriteria> query, Pageable pageable);

  /**
   * Obtiene {@link Acta} por id.
   *
   * @param id el id de la entidad {@link Acta}.
   * @return la entidad {@link Acta}.
   */
  Acta findById(Long id);

  /**
   * Elimina el {@link Acta} por id.
   *
   * @param id el id de la entidad {@link Acta}.
   */
  void delete(Long id) throws ActaNotFoundException;

}