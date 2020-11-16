package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link Convocatoria}.
 */

public interface ConvocatoriaService {

  /**
   * Guarda la entidad {@link Convocatoria}.
   * 
   * @param convocatoria           la entidad {@link Convocatoria} a guardar.
   * @param acronimosUnidadGestion listado de acrónimos asociados a las unidades
   *                               de gestión del usuario logueado.
   * @return Convocatoria la entidad {@link Convocatoria} persistida.
   */
  Convocatoria create(Convocatoria convocatoria, List<String> acronimosUnidadGestion);

  /**
   * Actualiza los datos del {@link Convocatoria}.
   * 
   * @param convocatoria           {@link Convocatoria} con los datos
   *                               actualizados.
   * @param acronimosUnidadGestion
   * @return Convocatoria {@link Convocatoria} actualizado.
   */
  Convocatoria update(final Convocatoria convocatoria, List<String> acronimosUnidadGestion);

  /**
   * Registra una {@link Convocatoria} actualizando su estado de 'Borrador' a
   * 'Registrada'
   * 
   * @param id Identificador de la {@link Convocatoria}.
   * @return Convocatoria {@link Convocatoria} actualizada.
   */
  Convocatoria registrar(final Long id);

  /**
   * Reactiva el {@link Convocatoria}.
   *
   * @param id Id del {@link Convocatoria}.
   * @return la entidad {@link Convocatoria} persistida.
   */
  Convocatoria enable(Long id);

  /**
   * Desactiva el {@link Convocatoria}.
   *
   * @param id Id del {@link Convocatoria}.
   * @return la entidad {@link Convocatoria} persistida.
   */
  Convocatoria disable(Long id);

  /**
   * Obtiene una entidad {@link Convocatoria} por id.
   * 
   * @param id Identificador de la entidad {@link Convocatoria}.
   * @return Convocatoria la entidad {@link Convocatoria}.
   */
  Convocatoria findById(final Long id);

  /**
   * Obtiene todas las entidades {@link Convocatoria} activas paginadas y
   * filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link Convocatoria} activas paginadas y
   *         filtradas.
   */
  Page<Convocatoria> findAll(List<QueryCriteria> query, Pageable paging);

  /**
   * Obtiene todas las entidades {@link Convocatoria} paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link Convocatoria} paginadas y filtradas.
   */
  Page<Convocatoria> findAllTodos(List<QueryCriteria> query, Pageable paging);

  /**
   * Devuelve todas las convocatorias activas que se encuentren dentro de la
   * unidad de gestión del usuario logueado.
   */
  Page<Convocatoria> findAllTodosRestringidos(List<QueryCriteria> query, Pageable paging,
      List<String> acronimosUnidadGestion);

}
