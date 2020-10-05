package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ModeloTipoFase}.
 */

public interface ModeloTipoFaseService {

  /**
   * Obtiene los {@link ModeloTipoFase} para un {@link ModeloEjecucion}.
   *
   * @param idModeloEjecucion el id de la entidad {@link ModeloEjecucion}.
   * @param query             la información del filtro.
   * @param pageable          la información de la paginación.
   * @return la lista de entidades {@link ModeloTipoFase} del
   *         {@link ModeloEjecucion} paginadas.
   */
  Page<ModeloTipoFase> findAllByModeloEjecucion(Long idModeloEjecucion, List<QueryCriteria> query, Pageable pageable);

  /**
   * Obtiene los {@link ModeloTipoFase} activos para convocatorias para un
   * {@link ModeloEjecucion}.
   *
   * @param idModeloEjecucion el id de la entidad {@link ModeloEjecucion}.
   * @param query             la información del filtro.
   * @param pageable          la información de la paginación.
   * @return la lista de entidades {@link ModeloTipoFase} del
   *         {@link ModeloEjecucion} paginadas.
   */
  Page<ModeloTipoFase> findAllByModeloEjecucionActivosConvocatoria(Long idModeloEjecucion, List<QueryCriteria> query,
      Pageable pageable);

  /**
   * Obtiene los {@link ModeloTipoFase} activos para proyectos para un
   * {@link ModeloEjecucion}.
   *
   * @param idModeloEjecucion el id de la entidad {@link ModeloEjecucion}.
   * @param query             la información del filtro.
   * @param pageable          la información de la paginación.
   * @return la lista de entidades {@link ModeloTipoFase} del
   *         {@link ModeloEjecucion} paginadas.
   */
  Page<ModeloTipoFase> findAllByModeloEjecucionActivosProyecto(Long idModeloEjecucion, List<QueryCriteria> query,
      Pageable pageable);

}
