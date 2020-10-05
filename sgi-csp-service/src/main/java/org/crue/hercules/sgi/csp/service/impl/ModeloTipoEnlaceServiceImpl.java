package org.crue.hercules.sgi.csp.service.impl;

import java.util.List;

import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoEnlace;
import org.crue.hercules.sgi.csp.model.TipoEnlace;
import org.crue.hercules.sgi.csp.repository.ModeloTipoEnlaceRepository;
import org.crue.hercules.sgi.csp.repository.specification.ModeloTipoEnlaceSpecifications;
import org.crue.hercules.sgi.csp.service.ModeloTipoEnlaceService;
import org.crue.hercules.sgi.framework.data.jpa.domain.QuerySpecification;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link ModeloTipoEnlace}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ModeloTipoEnlaceServiceImpl implements ModeloTipoEnlaceService {

  private final ModeloTipoEnlaceRepository modeloTipoEnlaceRepository;

  public ModeloTipoEnlaceServiceImpl(ModeloTipoEnlaceRepository modeloTipoEnlaceRepository) {
    this.modeloTipoEnlaceRepository = modeloTipoEnlaceRepository;
  }

  /**
   * Obtiene los {@link ModeloTipoEnlace} activos para un {@link ModeloEjecucion}.
   *
   * @param idModeloEjecucion el id de la entidad {@link ModeloEjecucion}.
   * @param query             la información del filtro.
   * @param pageable          la información de la paginación.
   * @return la lista de entidades {@link TipoEnlace} del {@link ModeloEjecucion}
   *         paginadas.
   */
  public Page<ModeloTipoEnlace> findAllByModeloEjecucion(Long idModeloEjecucion, List<QueryCriteria> query,
      Pageable pageable) {
    log.debug("findAllByModeloEjecucion(Long idModeloEjecucion, List<QueryCriteria> query, Pageable pageable) - start");
    Specification<ModeloTipoEnlace> specByQuery = new QuerySpecification<ModeloTipoEnlace>(query);
    Specification<ModeloTipoEnlace> specActivos = ModeloTipoEnlaceSpecifications.activos();
    Specification<ModeloTipoEnlace> specByModeloEjecucion = ModeloTipoEnlaceSpecifications
        .byModeloEjecucionId(idModeloEjecucion);

    Specification<ModeloTipoEnlace> specs = Specification.where(specActivos).and(specByModeloEjecucion)
        .and(specByQuery);

    Page<ModeloTipoEnlace> returnValue = modeloTipoEnlaceRepository.findAll(specs, pageable);
    log.debug("findAllByModeloEjecucion(Long idModeloEjecucion, List<QueryCriteria> query, Pageable pageable) - end");
    return returnValue;
  }

}
