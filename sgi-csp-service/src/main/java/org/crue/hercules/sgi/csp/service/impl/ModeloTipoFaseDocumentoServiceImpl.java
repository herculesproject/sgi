package org.crue.hercules.sgi.csp.service.impl;

import java.util.List;

import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFaseDocumento;
import org.crue.hercules.sgi.csp.repository.ModeloTipoFaseDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.specification.ModeloTipoFaseDocumentoSpecifications;
import org.crue.hercules.sgi.csp.service.ModeloTipoFaseDocumentoService;
import org.crue.hercules.sgi.framework.data.jpa.domain.QuerySpecification;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link ModeloTipoFaseDocumento}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ModeloTipoFaseDocumentoServiceImpl implements ModeloTipoFaseDocumentoService {

  private final ModeloTipoFaseDocumentoRepository modeloTipoFaseDocumentoRepository;

  public ModeloTipoFaseDocumentoServiceImpl(ModeloTipoFaseDocumentoRepository modeloTipoFaseDocumentoRepository) {
    this.modeloTipoFaseDocumentoRepository = modeloTipoFaseDocumentoRepository;
  }

  /**
   * Obtiene los {@link ModeloTipoFaseDocumento} activos para un
   * {@link ModeloEjecucion}.
   *
   * @param idModeloEjecucion el id de la entidad {@link ModeloEjecucion}.
   * @param query             la información del filtro.
   * @param pageable          la información de la paginación.
   * @return la lista de entidades {@link ModeloTipoFaseDocumento} del
   *         {@link ModeloEjecucion} paginadas.
   */
  @Override
  public Page<ModeloTipoFaseDocumento> findAllByModeloEjecucion(Long idModeloEjecucion, List<QueryCriteria> query,
      Pageable pageable) {
    log.debug("findAllByModeloEjecucion(Long idModeloEjecucion, List<QueryCriteria> query, Pageable pageable) - start");
    Specification<ModeloTipoFaseDocumento> specByQuery = new QuerySpecification<ModeloTipoFaseDocumento>(query);
    Specification<ModeloTipoFaseDocumento> specActivos = ModeloTipoFaseDocumentoSpecifications.activos();
    Specification<ModeloTipoFaseDocumento> specByModeloEjecucion = ModeloTipoFaseDocumentoSpecifications
        .byModeloEjecucionId(idModeloEjecucion);

    Specification<ModeloTipoFaseDocumento> specs = Specification.where(specActivos).and(specByModeloEjecucion)
        .and(specByQuery);

    Page<ModeloTipoFaseDocumento> returnValue = modeloTipoFaseDocumentoRepository.findAll(specs, pageable);
    log.debug("findAllByModeloEjecucion(Long idModeloEjecucion, List<QueryCriteria> query, Pageable pageable) - end");
    return returnValue;
  }

}
