package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloUnidad;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ModeloUnidad}.
 */
public interface ModeloUnidadService {

  /**
   * Obtiene los {@link ModeloUnidad} activos para un {@link ModeloEjecucion}.
   *
   * @param idModeloEjecucion el id de la entidad {@link ModeloEjecucion}.
   * @param query             la información del filtro.
   * @param pageable          la información de la paginación.
   * @return la lista de entidades {@link ModeloUnidad} del
   *         {@link ModeloEjecucion} paginadas.
   */
  Page<ModeloUnidad> findAllByModeloEjecucion(Long idModeloEjecucion, List<QueryCriteria> query, Pageable pageable);

}