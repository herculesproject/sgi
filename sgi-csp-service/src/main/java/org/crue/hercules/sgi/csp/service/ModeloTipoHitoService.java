package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoHito;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ModeloTipoHito}.
 */
public interface ModeloTipoHitoService {

  /**
   * Obtiene los {@link ModeloTipoHito} para un {@link ModeloEjecucion}.
   *
   * @param idModeloEjecucion el id de la entidad {@link ModeloEjecucion}.
   * @param query             la información del filtro.
   * @param pageable          la información de la paginación.
   * @return la lista de entidades {@link TipoHito} del {@link ModeloEjecucion}
   *         paginadas.
   */
  Page<ModeloTipoHito> findAllByModeloEjecucion(Long idModeloEjecucion, List<QueryCriteria> query, Pageable pageable);

  /**
   * Obtiene los {@link ModeloTipoHito} activos para convocatorias para un
   * {@link ModeloEjecucion}.
   *
   * @param idModeloEjecucion el id de la entidad {@link ModeloEjecucion}.
   * @param query             la información del filtro.
   * @param pageable          la información de la paginación.
   * @return la lista de entidades {@link ModeloTipoHito} del
   *         {@link ModeloEjecucion} paginadas.
   */
  Page<ModeloTipoHito> findAllByModeloEjecucionActivosConvocatoria(Long idModeloEjecucion, List<QueryCriteria> query,
      Pageable pageable);

  /**
   * Obtiene los {@link ModeloTipoHito} activos para proyectos para un
   * {@link ModeloEjecucion}.
   *
   * @param idModeloEjecucion el id de la entidad {@link ModeloEjecucion}.
   * @param query             la información del filtro.
   * @param pageable          la información de la paginación.
   * @return la lista de entidades {@link ModeloTipoHito} del
   *         {@link ModeloEjecucion} paginadas.
   */
  Page<ModeloTipoHito> findAllByModeloEjecucionActivosProyecto(Long idModeloEjecucion, List<QueryCriteria> query,
      Pageable pageable);

  /**
   * Obtiene los {@link ModeloTipoHito} activos para solicitudes para un
   * {@link ModeloEjecucion}.
   *
   * @param idModeloEjecucion el id de la entidad {@link ModeloEjecucion}.
   * @param query             la información del filtro.
   * @param pageable          la información de la paginación.
   * @return la lista de entidades {@link ModeloTipoHito} del
   *         {@link ModeloEjecucion} paginadas.
   */
  Page<ModeloTipoHito> findAllByModeloEjecucionActivosSolicitud(Long idModeloEjecucion, List<QueryCriteria> query,
      Pageable pageable);

}
