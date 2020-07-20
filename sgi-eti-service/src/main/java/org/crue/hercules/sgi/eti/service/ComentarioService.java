package org.crue.hercules.sgi.eti.service;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.ComentarioNotFoundException;
import org.crue.hercules.sgi.eti.model.Comentario;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link Comentario}.
 */
public interface ComentarioService {

  /**
   * Guardar {@link Comentario}.
   *
   * @param comentario la entidad {@link Comentario} a guardar.
   * @return la entidad {@link Comentario} persistida.
   */
  Comentario create(Comentario comentario);

  /**
   * Actualizar {@link Comentario}.
   *
   * @param comentario la entidad {@link Comentario} a actualizar.
   * @return la entidad {@link Comentario} persistida.
   */
  Comentario update(Comentario comentario);

  /**
   * Obtener todas las entidades {@link Comentario} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Comentario} paginadas y/o filtradas.
   */
  Page<Comentario> findAll(List<QueryCriteria> query, Pageable pageable);

  /**
   * Obtiene {@link Comentario} por id.
   *
   * @param id el id de la entidad {@link Comentario}.
   * @return la entidad {@link Comentario}.
   */
  Comentario findById(Long id);

  /**
   * Elimina el {@link Comentario} por id.
   *
   * @param id el id de la entidad {@link Comentario}.
   */
  void delete(Long id) throws ComentarioNotFoundException;

}