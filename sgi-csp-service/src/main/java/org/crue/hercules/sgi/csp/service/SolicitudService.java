package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link Solicitud}.
 */
public interface SolicitudService {

  /**
   * Guarda la entidad {@link Solicitud}.
   * 
   * @param solicitud         la entidad {@link Solicitud} a guardar.
   * @param unidadGestionRefs lista de referencias de las unidades de gestion
   *                          permitidas para el usuario.
   * @return solicitud la entidad {@link Solicitud} persistida.
   */
  Solicitud create(Solicitud solicitud, List<String> unidadGestionRefs);

  /**
   * Actualiza los datos del {@link Solicitud}.
   * 
   * @param solicitud               {@link Solicitud} con los datos actualizados.
   * @param unidadGestionRefs       lista de referencias de las unidades de
   *                                gestion permitidas para el usuario.
   * @param isAdministradorOrGestor Indicador de si el usuario que realiza la
   *                                acutalización es administrador o gestor.
   * @return solicitud {@link Solicitud} actualizado.
   */
  Solicitud update(final Solicitud solicitud, List<String> unidadGestionRefs, Boolean isAdministradorOrGestor);

  /**
   * Reactiva el {@link Solicitud}.
   *
   * @param id                Id del {@link Solicitud}.
   * @param unidadGestionRefs lista de referencias de las unidades de gestion
   *                          permitidas para el usuario.
   * @return la entidad {@link Solicitud} persistida.
   */
  Solicitud enable(Long id, List<String> unidadGestionRefs);

  /**
   * Desactiva el {@link Solicitud}.
   *
   * @param id                Id del {@link Solicitud}.
   * @param unidadGestionRefs lista de referencias de las unidades de gestion
   *                          permitidas para el usuario.
   * @return la entidad {@link Solicitud} persistida.
   */
  Solicitud disable(Long id, List<String> unidadGestionRefs);

  /**
   * Obtiene una entidad {@link Solicitud} por id.
   * 
   * @param id                Identificador de la entidad {@link Solicitud}.
   * @param unidadGestionRefs lista de referencias de las unidades de gestion
   *                          permitidas para el usuario.
   * @return Solicitud la entidad {@link Solicitud}.
   */
  Solicitud findById(final Long id, List<String> unidadGestionRefs);

  /**
   * Obtiene todas las entidades {@link Solicitud} activas paginadas y filtradas.
   *
   * @param query             información del filtro.
   * @param paging            información de paginación.
   * @param unidadGestionRefs lista de referencias de las unidades de gestion.
   * @return el listado de entidades {@link Solicitud} activas paginadas y
   *         filtradas.
   */
  Page<Solicitud> findAllRestringidos(List<QueryCriteria> query, Pageable paging, List<String> unidadGestionRefs);

  /**
   * Obtiene todas las entidades {@link Solicitud} paginadas y filtradas.
   *
   * @param query             información del filtro.
   * @param paging            información de paginación.
   * @param unidadGestionRefs lista de referencias de las unidades de gestion.
   * @return el listado de entidades {@link Solicitud} paginadas y filtradas.
   */
  Page<Solicitud> findAllTodosRestringidos(List<QueryCriteria> query, Pageable paging, List<String> unidadGestionRefs);

}
