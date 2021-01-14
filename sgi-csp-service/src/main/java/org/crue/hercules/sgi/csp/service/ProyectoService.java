package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Servicio interfaz para la gestión de {@link Proyecto}.
 */
public interface ProyectoService {

  /**
   * Guarda la entidad {@link Proyecto}.
   * 
   * @param proyecto          la entidad {@link Proyecto} a guardar.
   * @param unidadGestionRefs lista de referencias de las unidades de gestion
   *                          permitidas para el usuario.
   * @return proyecto la entidad {@link Proyecto} persistida.
   */
  Proyecto create(Proyecto proyecto, List<String> unidadGestionRefs);

  /**
   * Actualiza los datos del {@link Proyecto}.
   * 
   * @param proyecto          {@link Proyecto} con los datos actualizados.
   * @param unidadGestionRefs lista de referencias de las unidades de gestion
   *                          permitidas para el usuario.
   * @return proyecto {@link Proyecto} actualizado.
   */
  Proyecto update(final Proyecto proyecto, List<String> unidadGestionRefs);

  /**
   * Reactiva el {@link Proyecto}.
   *
   * @param id                Id del {@link Proyecto}.
   * @param unidadGestionRefs lista de referencias de las unidades de gestion
   *                          permitidas para el usuario.
   * @return la entidad {@link Proyecto} persistida.
   */
  Proyecto enable(Long id, List<String> unidadGestionRefs);

  /**
   * Desactiva el {@link Proyecto}.
   *
   * @param id                Id del {@link Proyecto}.
   * @param unidadGestionRefs lista de referencias de las unidades de gestion
   *                          permitidas para el usuario.
   * @return la entidad {@link Proyecto} persistida.
   */
  Proyecto disable(Long id, List<String> unidadGestionRefs);

  /**
   * Comprueba la existencia del {@link Proyecto} por id.
   *
   * @param id el id de la entidad {@link Proyecto}.
   * @return true si existe y false en caso contrario.
   */
  boolean existsById(Long id);

  /**
   * Obtiene el {@link ModeloEjecucion} asignado al {@link Proyecto}.
   * 
   * @param id Id del {@link Proyecto}.
   * @return {@link ModeloEjecucion} asignado
   */
  ModeloEjecucion getModeloEjecucion(Long id);

  /**
   * Obtiene una entidad {@link Proyecto} por id.
   * 
   * @param id                Identificador de la entidad {@link Proyecto}.
   * @param unidadGestionRefs lista de referencias de las unidades de gestion
   *                          permitidas para el usuario.
   * @return Proyecto la entidad {@link Proyecto}.
   */
  Proyecto findById(final Long id, List<String> unidadGestionRefs);

  /**
   * Obtiene todas las entidades {@link Proyecto} activas paginadas y filtradas.
   *
   * @param query             información del filtro.
   * @param paging            información de paginación.
   * @param unidadGestionRefs lista de referencias de las unidades de gestion.
   * @return el listado de entidades {@link Proyecto} activas paginadas y
   *         filtradas.
   */
  Page<Proyecto> findAllRestringidos(List<QueryCriteria> query, Pageable paging, List<String> unidadGestionRefs);

  /**
   * Obtiene todas las entidades {@link Proyecto} paginadas y filtradas.
   *
   * @param query             información del filtro.
   * @param paging            información de paginación.
   * @param unidadGestionRefs lista de referencias de las unidades de gestion.
   * @return el listado de entidades {@link Proyecto} paginadas y filtradas.
   */
  Page<Proyecto> findAllTodosRestringidos(List<QueryCriteria> query, Pageable paging, List<String> unidadGestionRefs);

}
