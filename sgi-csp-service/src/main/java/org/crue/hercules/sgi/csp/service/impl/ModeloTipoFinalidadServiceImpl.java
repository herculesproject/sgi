package org.crue.hercules.sgi.csp.service.impl;

import java.util.List;

import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoEnlace;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.repository.ModeloTipoFinalidadRepository;
import org.crue.hercules.sgi.csp.repository.specification.ModeloTipoFinalidadSpecifications;
import org.crue.hercules.sgi.csp.service.ModeloTipoFinalidadService;
import org.crue.hercules.sgi.framework.data.jpa.domain.QuerySpecification;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link ModeloTipoFinalidad}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ModeloTipoFinalidadServiceImpl implements ModeloTipoFinalidadService {

  private final ModeloTipoFinalidadRepository modeloTipoFinalidadRepository;

  public ModeloTipoFinalidadServiceImpl(ModeloTipoFinalidadRepository modeloTipoFinalidadRepository) {
    this.modeloTipoFinalidadRepository = modeloTipoFinalidadRepository;
  }

  /**
   * Obtiene los {@link ModeloTipoEnlace} activos para un {@link ModeloEjecucion}.
   *
   * @param idModeloEjecucion el id de la entidad {@link ModeloEjecucion}.
   * @param query             la información del filtro.
   * @param pageable          la información de la paginación.
   * @return la lista de entidades {@link ModeloTipoEnlace} del
   *         {@link ModeloEjecucion} paginadas.
   */
  public Page<ModeloTipoFinalidad> findAllByModeloEjecucion(Long idModeloEjecucion, List<QueryCriteria> query,
      Pageable pageable) {
    log.debug("findAllByModeloEjecucion(Long idModeloEjecucion, List<QueryCriteria> query, Pageable pageable) - start");
    Specification<ModeloTipoFinalidad> specByQuery = new QuerySpecification<ModeloTipoFinalidad>(query);
    Specification<ModeloTipoFinalidad> specActivos = ModeloTipoFinalidadSpecifications.activos();
    Specification<ModeloTipoFinalidad> specByModeloEjecucion = ModeloTipoFinalidadSpecifications
        .byModeloEjecucionId(idModeloEjecucion);

    Specification<ModeloTipoFinalidad> specs = Specification.where(specActivos).and(specByModeloEjecucion)
        .and(specByQuery);

    Page<ModeloTipoFinalidad> returnValue = modeloTipoFinalidadRepository.findAll(specs, pageable);
    log.debug("findAllByModeloEjecucion(Long idModeloEjecucion, List<QueryCriteria> query, Pageable pageable) - end");
    return returnValue;
  }

}
