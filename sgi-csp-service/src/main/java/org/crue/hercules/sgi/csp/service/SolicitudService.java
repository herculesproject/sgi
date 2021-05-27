package org.crue.hercules.sgi.csp.service;

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
   * @param solicitud la entidad {@link Solicitud} a guardar.
   * @return solicitud la entidad {@link Solicitud} persistida.
   */
  Solicitud create(Solicitud solicitud);

  /**
   * Actualiza los datos del {@link Solicitud}.
   * 
   * @param solicitud {@link Solicitud} con los datos actualizados.
   * @return solicitud {@link Solicitud} actualizado.
   */
  Solicitud update(final Solicitud solicitud);

  /**
   * Reactiva el {@link Solicitud}.
   *
   * @param id Id del {@link Solicitud}.
   * @return la entidad {@link Solicitud} persistida.
   */
  Solicitud enable(Long id);

  /**
   * Desactiva el {@link Solicitud}.
   *
   * @param id Id del {@link Solicitud}.
   * @return la entidad {@link Solicitud} persistida.
   */
  Solicitud disable(Long id);

  /**
   * Obtiene una entidad {@link Solicitud} por id.
   * 
   * @param id Identificador de la entidad {@link Solicitud}.
   * @return Solicitud la entidad {@link Solicitud}.
   */
  Solicitud findById(final Long id);

  /**
   * Obtiene todas las entidades {@link Solicitud} activas paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link Solicitud} activas paginadas y
   *         filtradas.
   */
  Page<Solicitud> findAllRestringidos(String query, Pageable paging);

  /**
   * Obtiene todas las entidades {@link Solicitud} paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link Solicitud} paginadas y filtradas.
   */
  Page<Solicitud> findAllTodosRestringidos(String query, Pageable paging);

  /**
   * Obtiene todas las entidades {@link Solicitud} que puede visualizar un
   * investigador paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link Solicitud} que puede visualizar un
   *         investigador paginadas y filtradas.
   */
  Page<Solicitud> findAllInvestigador(String query, Pageable paging);

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
  boolean modificable(Long id);

  /**
   * Hace las comprobaciones necesarias para determinar si se puede crear un
   * {@link Proyecto} a partir de la {@link Solicitud}
   * 
   * @param id Id de la {@link Solicitud}.
   * @return true si se permite la creación / false si no se permite creación
   */
  boolean isPosibleCrearProyecto(Long id);

  /**
   * Se hace el cambio de estado de una {@link Solicitud}.
   * 
   * @param id              Identificador de {@link Solicitud}.
   * @param estadoSolicitud Estado al que se cambiará la solicitud.
   * @return {@link Solicitud} actualizado.
   */
  Solicitud cambiarEstado(Long id, EstadoSolicitud estadoSolicitud);
}
