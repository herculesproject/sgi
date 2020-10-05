package org.crue.hercules.sgi.csp.service.impl;

import java.util.List;

import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloUnidad;
import org.crue.hercules.sgi.csp.repository.ModeloUnidadRepository;
import org.crue.hercules.sgi.csp.repository.specification.ModeloUnidadSpecifications;
import org.crue.hercules.sgi.csp.service.ModeloUnidadService;
import org.crue.hercules.sgi.framework.data.jpa.domain.QuerySpecification;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link ModeloUnidad}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ModeloUnidadServiceImpl implements ModeloUnidadService {

  private final ModeloUnidadRepository modeloUnidadRepository;

  public ModeloUnidadServiceImpl(ModeloUnidadRepository modeloUnidadRepository) {
    this.modeloUnidadRepository = modeloUnidadRepository;
  }

  /**
   * Obtiene los {@link ModeloUnidad} activos para un {@link ModeloEjecucion}.
   *
   * @param idModeloEjecucion el id de la entidad {@link ModeloEjecucion}.
   * @param query             la información del filtro.
   * @param pageable          la información de la paginación.
   * @return la lista de entidades {@link ModeloUnidad} del
   *         {@link ModeloEjecucion} paginadas.
   */
  public Page<ModeloUnidad> findAllByModeloEjecucion(Long idModeloEjecucion, List<QueryCriteria> query,
      Pageable pageable) {
    log.debug("findAllByModeloEjecucion(Long idModeloEjecucion, List<QueryCriteria> query, Pageable pageable) - start");
    Specification<ModeloUnidad> specByQuery = new QuerySpecification<ModeloUnidad>(query);
    Specification<ModeloUnidad> specActivos = ModeloUnidadSpecifications.activos();
    Specification<ModeloUnidad> specByModeloEjecucion = ModeloUnidadSpecifications
        .byModeloEjecucionId(idModeloEjecucion);

    Specification<ModeloUnidad> specs = Specification.where(specActivos).and(specByModeloEjecucion).and(specByQuery);

    Page<ModeloUnidad> returnValue = modeloUnidadRepository.findAll(specs, pageable);
    log.debug("findAllByModeloEjecucion(Long idModeloEjecucion, List<QueryCriteria> query, Pageable pageable) - end");
    return returnValue;
  }

}
