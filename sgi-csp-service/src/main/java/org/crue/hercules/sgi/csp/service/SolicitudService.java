package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.Solicitud;
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
   * @param solicitud         {@link Solicitud} con los datos actualizados.
   * @param unidadGestionRefs lista de referencias de las unidades de gestion
   *                          permitidas para el usuario.
   * @return solicitud {@link Solicitud} actualizado.
   */
  Solicitud update(final Solicitud solicitud, List<String> unidadGestionRefs);

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
  Page<Solicitud> findAllRestringidos(String query, Pageable paging, List<String> unidadGestionRefs);

  /**
   * Obtiene todas las entidades {@link Solicitud} paginadas y filtradas.
   *
   * @param query             información del filtro.
   * @param paging            información de paginación.
   * @param unidadGestionRefs lista de referencias de las unidades de gestion.
   * @return el listado de entidades {@link Solicitud} paginadas y filtradas.
   */
  Page<Solicitud> findAllTodosRestringidos(String query, Pageable paging, List<String> unidadGestionRefs);

  /**
   * Comprueba si la soliciutd está asociada a una convocatoria SGI.
   * 
   * @param id Identificador de {@link Solicitud}.
   * @return indicador de si se encuentra asociado o no la solicitud a una
   *         convocatoria SGI
   */
  boolean hasConvocatoriaSgi(Long id);

  /**
   * Hace las comprobaciones necesarias para determinar si la {@link Solicitud}
   * puede ser modificada. También se utilizará para permitir la creación,
   * modificación o eliminación de ciertas entidades relacionadas con la propia
   * {@link Solicitud}.
   *
   * @param id Id del {@link Solicitud}.
   * @return true si puede ser modificada / false si no puede ser modificada
   */
  Boolean modificable(Long id);

  /**
   * Hace las comprobaciones necesarias para determinar si se puede crear un
   * {@link Proyecto} a partir de la {@link Solicitud}
   * 
   * @param id Id de la {@link Solicitud}.
   * @return true si se permite la creación / false si no se permite creación
   */
  Boolean isPosibleCrearProyecto(Long id);

  /**
   * Se hace el cambio de estado de "Borrador" a "Presentada".
   * 
   * @param id Identificador de {@link Solicitud}.
   * @return {@link Solicitud} actualizado.
   */
  Solicitud presentarSolicitud(Long id);

  /**
   * Cambio de estado de "Presentada" a "Admitida provisionalmente".
   * 
   * @param id Identificador de {@link Solicitud}.
   * @return {@link Solicitud} actualizado.
   */
  Solicitud admitirProvisionalmente(Long id);

  /**
   * Cambio de estado de "Admitida provisionalmente" a "Admitida definitivamente".
   * 
   * @param id Identificador de {@link Solicitud}.
   * @return {@link Solicitud} actualizado.
   */
  Solicitud admitirDefinitivamente(Long id);

  /**
   * Cambio de estado de "Admitida definitivamente" a "Concedida provisional".
   * 
   * @param id Identificador de {@link Solicitud}.
   * @return {@link Solicitud} actualizado.
   */
  Solicitud concederProvisionalmente(Long id);

  /**
   * Cambio de estado de "Concedida provisional" o "Alegada concesión" a
   * "Concedida".
   * 
   * @param id Identificador de {@link Solicitud}.
   * @return {@link Solicitud} actualizado.
   */
  Solicitud conceder(Long id);

  /**
   * Cambio de estado de "Presentada" a "Excluir provisionalmente".
   * 
   * @param id         Identificador de {@link Solicitud}.
   * @param comentario Comentario de {@link EstadoSolicitud}.
   * @return {@link Solicitud} actualizado.
   */
  Solicitud exlcluirProvisionalmente(Long id, String comentario);

  /**
   * Comprueba si se cumplen todas las condiciones para que la solicitud pueda
   * pasar al estado de "Presentada."
   * 
   * @param id Id del {@link Solicitud}.
   * @return <code>true</code> Cumple condiciones para el cambio de estado.
   *         <code>false</code>No cumple condiciones.
   */
  Boolean cumpleValidacionesPresentada(Long id);

  /**
   * Cambio de estado de "Excluida provisional" a "Alegada admisión".
   * 
   * @param id         Identificador de {@link Solicitud}.
   * @param comentario Comentario de {@link EstadoSolicitud}.
   * @return {@link Solicitud} actualizado.
   */
  Solicitud alegarAdmision(Long id, String comentario);

  /**
   * Cambio de estado de "Alegada admisión" a "Excluida".
   * 
   * @param id         Identificador de {@link Solicitud}.
   * @param comentario Comentario de {@link EstadoSolicitud}.
   * @return {@link Solicitud} actualizado.
   */
  Solicitud excluir(Long id, String comentario);

  /**
   * Cambio de estado de "Admitida definitiva" a "Denegada provisional".
   * 
   * @param id         Identificador de {@link Solicitud}.
   * @param comentario Comentario de {@link EstadoSolicitud}.
   * @return {@link Solicitud} actualizado.
   */
  Solicitud denegarProvisionalmente(Long id, String comentario);

  /**
   * Cambio de estado de "Denegada provisional" a "Alegada concesión".
   * 
   * @param id         Identificador de {@link Solicitud}.
   * @param comentario Comentario de {@link EstadoSolicitud}.
   * @return {@link Solicitud} actualizado.
   */
  Solicitud alegarConcesion(Long id, String comentario);

  /**
   * Cambio de estado de "Alegada concesión" a "Denegada".
   * 
   * @param id         Identificador de {@link Solicitud}.
   * @param comentario Comentario de {@link EstadoSolicitud}.
   * @return {@link Solicitud} actualizado.
   */
  Solicitud denegar(Long id, String comentario);

  /**
   * Cambio de estado de "Presentada", "Admitida provisional", "Excluida
   * provisional", "Admitida definitiva", "Denegada provisional" o "Concedida
   * provisional" a "Desistida".
   * 
   * @param id         Identificador de {@link Solicitud}.
   * @param comentario Comentario de {@link EstadoSolicitud}.
   * @return {@link Solicitud} actualizado.
   */
  Solicitud desistir(Long id, String comentario);

}
