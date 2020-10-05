package org.crue.hercules.sgi.csp.service.impl;

import java.util.List;

import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.repository.ModeloTipoFaseRepository;
import org.crue.hercules.sgi.csp.repository.specification.ModeloTipoFaseSpecifications;
import org.crue.hercules.sgi.csp.service.ModeloTipoFaseService;
import org.crue.hercules.sgi.framework.data.jpa.domain.QuerySpecification;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link ModeloTipoFase}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ModeloTipoFaseServiceImpl implements ModeloTipoFaseService {

  private final ModeloTipoFaseRepository modeloTipoFaseRepository;

  public ModeloTipoFaseServiceImpl(ModeloTipoFaseRepository modeloTipoFaseRepository) {
    this.modeloTipoFaseRepository = modeloTipoFaseRepository;
  }

  /**
   * Obtiene los {@link ModeloTipoFase} activos para un {@link ModeloEjecucion}.
   *
   * @param idModeloEjecucion el id de la entidad {@link ModeloEjecucion}.
   * @param query             la información del filtro.
   * @param pageable          la información de la paginación.
   * @return la lista de entidades {@link ModeloTipoFase} del
   *         {@link ModeloEjecucion} paginadas.
   */
  public Page<ModeloTipoFase> findAllByModeloEjecucion(Long idModeloEjecucion, List<QueryCriteria> query,
      Pageable pageable) {
    log.debug(
        "findAllByModeloEjecucion(Long idModeloEjecucion,  List<QueryCriteria> query, Pageable pageable) - start");
    Specification<ModeloTipoFase> specByQuery = new QuerySpecification<ModeloTipoFase>(query);
    Specification<ModeloTipoFase> specByModeloEjecucion = ModeloTipoFaseSpecifications
        .byModeloEjecucionId(idModeloEjecucion).and(specByQuery);

    Specification<ModeloTipoFase> specs = Specification.where(specByModeloEjecucion).and(specByQuery);

    Page<ModeloTipoFase> returnValue = modeloTipoFaseRepository.findAll(specs, pageable);
    log.debug("findAllByModeloEjecucion(Long idModeloEjecucion,  List<QueryCriteria> query, Pageable pageable) - end");
    return returnValue;
  }

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
  @Override
  public Page<ModeloTipoFase> findAllByModeloEjecucionActivosConvocatoria(Long idModeloEjecucion,
      List<QueryCriteria> query, Pageable pageable) {
    log.debug(
        "findAllByModeloEjecucionActivosConvocatoria(Long idModeloEjecucion, List<QueryCriteria> query, Pageable pageable) - start");
    Specification<ModeloTipoFase> specByQuery = new QuerySpecification<ModeloTipoFase>(query);
    Specification<ModeloTipoFase> specByModeloEjecucion = ModeloTipoFaseSpecifications
        .byModeloEjecucionId(idModeloEjecucion).and(specByQuery);
    Specification<ModeloTipoFase> specActivosConvocatoria = ModeloTipoFaseSpecifications.activosConvocatoria();

    Specification<ModeloTipoFase> specs = Specification.where(specByModeloEjecucion).and(specActivosConvocatoria)
        .and(specByQuery);

    Page<ModeloTipoFase> returnValue = modeloTipoFaseRepository.findAll(specs, pageable);
    log.debug(
        "findAllByModeloEjecucionActivosConvocatoria(Long idModeloEjecucion, List<QueryCriteria> query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link TipoFase} activos para proyectos para un
   * {@link ModeloEjecucion}.
   *
   * @param idModeloEjecucion el id de la entidad {@link ModeloEjecucion}.
   * @param query             la información del filtro.
   * @param pageable          la información de la paginación.
   * @return la lista de entidades {@link TipoFase} del {@link ModeloEjecucion}
   *         paginadas.
   */
  @Override
  public Page<ModeloTipoFase> findAllByModeloEjecucionActivosProyecto(Long idModeloEjecucion, List<QueryCriteria> query,
      Pageable pageable) {
    log.debug(
        "findAllByModeloEjecucionActivosProyecto(Long idModeloEjecucion, List<QueryCriteria> query, Pageable pageable) - start");
    Specification<ModeloTipoFase> specByQuery = new QuerySpecification<ModeloTipoFase>(query);
    Specification<ModeloTipoFase> specByModeloEjecucion = ModeloTipoFaseSpecifications
        .byModeloEjecucionId(idModeloEjecucion).and(specByQuery);
    Specification<ModeloTipoFase> specActivosProyecto = ModeloTipoFaseSpecifications.activosProyecto();

    Specification<ModeloTipoFase> specs = Specification.where(specByModeloEjecucion).and(specActivosProyecto)
        .and(specByQuery);

    Page<ModeloTipoFase> returnValue = modeloTipoFaseRepository.findAll(specs, pageable);
    log.debug(
        "findAllByModeloEjecucionActivosProyecto(Long idModeloEjecucion, List<QueryCriteria> query, Pageable pageable) - end");
    return returnValue;
  }

}
