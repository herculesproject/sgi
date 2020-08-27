package org.crue.hercules.sgi.eti.service;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.ComentarioNotFoundException;
import org.crue.hercules.sgi.eti.model.Comentario;
import org.crue.hercules.sgi.eti.model.Evaluacion;
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
   * Guardar un listado de {@link Comentario} de una {@link Evaluacion}.
   *
   * @param evaluacionId Id de la evaluación
   * @param comentarios  lista de entidades {@link Comentario} a guardar.
   * @return lista de entidades {@link Comentario} persistida.
   */
  List<Comentario> createAll(Long evaluacionId, List<Comentario> comentarios);

  /**
   * Actualizar {@link Comentario}.
   *
   * @param comentario la entidad {@link Comentario} a actualizar.
   * @return la entidad {@link Comentario} persistida.
   */
  Comentario update(Comentario comentario);

  /**
   * Actualizar un listado {@link Comentario} de una {@link Evaluacion}.
   *
   * @param evaluacionId Id de la evaluación
   * @param comentarios  listado de entidades {@link Comentario} a actualizar.
   * @return listado de entidades {@link Comentario} persistida.
   */
  List<Comentario> updateAll(Long evaluacionId, List<Comentario> comentarios);

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
   * Obtiene {@link Comentario} por el id de su evaluación.
   *
   * @param id       el id de la entidad {@link Comentario}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Comentario} paginadas.
   */
  Page<Comentario> findByEvaluacionId(Long id, Pageable pageable);

  /**
   * Elimina el {@link Comentario} por id.
   *
   * @param id el id de la entidad {@link Comentario}.
   */
  void delete(Long id) throws ComentarioNotFoundException;

  /**
   * Elimina un listado de {@link Comentario} de una {@link Evaluacion}.
   *
   * @param evaluacionId Id de la evaluación
   * @param ids          listado de id de la entidad {@link Comentario}.
   */
  void deleteAll(Long evaluacionId, List<Long> ids) throws ComentarioNotFoundException;

  /**
   * Obtiene el número total de {@link Comentario} para un determinado
   * {@link Evaluacion}.
   * 
   * @param id Id de {@link Evaluacion}.
   * @return número de entidades {@link Comentario}
   */
  int countByEvaluacionId(Long id);
}