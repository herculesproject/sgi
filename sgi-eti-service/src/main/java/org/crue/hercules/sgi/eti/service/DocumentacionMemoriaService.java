package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.model.DocumentacionMemoria;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.DocumentacionMemoriaNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link DocumentacionMemoria}.
 */
public interface DocumentacionMemoriaService {
  /**
   * Guardar {@link DocumentacionMemoria}.
   *
   * @param DocumentacionMemoria la entidad {@link DocumentacionMemoria} a
   *                             guardar.
   * @return la entidad {@link DocumentacionMemoria} persistida.
   */
  DocumentacionMemoria create(DocumentacionMemoria DocumentacionMemoria);

  /**
   * Actualizar {@link DocumentacionMemoria}.
   *
   * @param DocumentacionMemoria la entidad {@link DocumentacionMemoria} a
   *                             actualizar.
   * @return la entidad {@link DocumentacionMemoria} persistida.
   */
  DocumentacionMemoria update(DocumentacionMemoria DocumentacionMemoria);

  /**
   * Obtener todas las entidades {@link DocumentacionMemoria} paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link DocumentacionMemoria} paginadas y/o
   *         filtradas.
   */
  Page<DocumentacionMemoria> findAll(List<QueryCriteria> query, Pageable pageable);

  /**
   * Obtiene {@link DocumentacionMemoria} por id.
   *
   * @param id el id de la entidad {@link DocumentacionMemoria}.
   * @return la entidad {@link DocumentacionMemoria}.
   */
  DocumentacionMemoria findById(Long id);

  /**
   * Elimina el {@link DocumentacionMemoria} por id.
   *
   * @param id el id de la entidad {@link DocumentacionMemoria}.
   */
  void delete(Long id) throws DocumentacionMemoriaNotFoundException;

  /**
   * Elimina todos los {@link DocumentacionMemoria}.
   */
  void deleteAll();

  /**
   * Obtener todas las entidades paginadas {@link DocumentacionMemoria} para una
   * determinada {@link Memoria}.
   *
   * @param id       Id de {@link Memoria}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link DocumentacionMemoria} paginadas.
   */
  Page<DocumentacionMemoria> findByMemoriaId(Long id, Pageable pageable);

  /**
   * Obtener todas las entidades paginadas {@link DocumentacionMemoria} por
   * {@link TipoEvaluacion} para una determinada {@link Memoria}.
   *
   * @param id               Id de {@link Memoria}.
   * @param idTipoEvaluacion Id de {@link TipoEvaluacion}
   * @param pageable         la información de la paginación.
   * @return la lista de entidades {@link DocumentacionMemoria} paginadas.
   */
  Page<DocumentacionMemoria> findByMemoriaIdAndTipoEvaluacion(Long id, Long idTipoEvaluacion, Pageable pageable);
}