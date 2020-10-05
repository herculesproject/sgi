package org.crue.hercules.sgi.csp.service.impl;

import java.util.List;

import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoHito;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.crue.hercules.sgi.csp.repository.ModeloTipoHitoRepository;
import org.crue.hercules.sgi.csp.repository.specification.ModeloTipoHitoSpecifications;
import org.crue.hercules.sgi.csp.service.ModeloTipoHitoService;
import org.crue.hercules.sgi.framework.data.jpa.domain.QuerySpecification;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link ModeloTipoHito}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ModeloTipoHitoServiceImpl implements ModeloTipoHitoService {

  private final ModeloTipoHitoRepository modeloTipoHitoRepository;

  public ModeloTipoHitoServiceImpl(ModeloTipoHitoRepository modeloTipoHitoRepository) {
    this.modeloTipoHitoRepository = modeloTipoHitoRepository;
  }

  /**
   * Obtiene los {@link ModeloTipoHito} para un {@link ModeloEjecucion}.
   *
   * @param idModeloEjecucion el id de la entidad {@link ModeloEjecucion}.
   * @param query             la información del filtro.
   * @param pageable          la información de la paginación.
   * @return la lista de entidades {@link TipoHito} del {@link ModeloEjecucion}
   *         paginadas.
   */
  public Page<ModeloTipoHito> findAllByModeloEjecucion(Long idModeloEjecucion, List<QueryCriteria> query,
      Pageable pageable) {
    log.debug("findAllByModeloEjecucion(Long idModeloEjecucion, List<QueryCriteria> query, Pageable pageable) - start");
    Specification<ModeloTipoHito> specByQuery = new QuerySpecification<ModeloTipoHito>(query);
    Specification<ModeloTipoHito> specByModeloEjecucion = ModeloTipoHitoSpecifications
        .byModeloEjecucionId(idModeloEjecucion).and(specByQuery);

    Specification<ModeloTipoHito> specs = Specification.where(specByModeloEjecucion).and(specByQuery);

    Page<ModeloTipoHito> returnValue = modeloTipoHitoRepository.findAll(specs, pageable);
    log.debug("findAllByModeloEjecucion(Long idModeloEjecucion, List<QueryCriteria> query, Pageable pageable) - end");
    return returnValue;
  }

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
  @Override
  public Page<ModeloTipoHito> findAllByModeloEjecucionActivosConvocatoria(Long idModeloEjecucion,
      List<QueryCriteria> query, Pageable pageable) {
    log.debug(
        "findAllByModeloEjecucionActivosConvocatoria(Long idModeloEjecucion, List<QueryCriteria> query, Pageable pageable) - start");
    Specification<ModeloTipoHito> specByQuery = new QuerySpecification<ModeloTipoHito>(query);
    Specification<ModeloTipoHito> specByModeloEjecucion = ModeloTipoHitoSpecifications
        .byModeloEjecucionId(idModeloEjecucion).and(specByQuery);
    Specification<ModeloTipoHito> specActivosConvocatoria = ModeloTipoHitoSpecifications.activosConvocatoria();

    Specification<ModeloTipoHito> specs = Specification.where(specByModeloEjecucion).and(specActivosConvocatoria)
        .and(specByQuery);

    Page<ModeloTipoHito> returnValue = modeloTipoHitoRepository.findAll(specs, pageable);
    log.debug(
        "findAllByModeloEjecucionActivosConvocatoria(Long idModeloEjecucion, List<QueryCriteria> query, Pageable pageable) - end");
    return returnValue;
  }

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
  @Override
  public Page<ModeloTipoHito> findAllByModeloEjecucionActivosProyecto(Long idModeloEjecucion, List<QueryCriteria> query,
      Pageable pageable) {
    log.debug(
        "findAllByModeloEjecucionActivosProyecto(Long idModeloEjecucion, List<QueryCriteria> query, Pageable pageable) - start");
    Specification<ModeloTipoHito> specByQuery = new QuerySpecification<ModeloTipoHito>(query);
    Specification<ModeloTipoHito> specByModeloEjecucion = ModeloTipoHitoSpecifications
        .byModeloEjecucionId(idModeloEjecucion).and(specByQuery);
    Specification<ModeloTipoHito> specActivosProyecto = ModeloTipoHitoSpecifications.activosProyecto();

    Specification<ModeloTipoHito> specs = Specification.where(specByModeloEjecucion).and(specActivosProyecto)
        .and(specByQuery);

    Page<ModeloTipoHito> returnValue = modeloTipoHitoRepository.findAll(specs, pageable);
    log.debug(
        "findAllByModeloEjecucionActivosProyecto(Long idModeloEjecucion, List<QueryCriteria> query, Pageable pageable) - end");
    return returnValue;
  }

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
  @Override
  public Page<ModeloTipoHito> findAllByModeloEjecucionActivosSolicitud(Long idModeloEjecucion,
      List<QueryCriteria> query, Pageable pageable) {
    log.debug(
        "findAllByModeloEjecucionActivosProyecto(Long idModeloEjecucion, List<QueryCriteria> query, Pageable pageable) - start");
    Specification<ModeloTipoHito> specByQuery = new QuerySpecification<ModeloTipoHito>(query);
    Specification<ModeloTipoHito> specByModeloEjecucion = ModeloTipoHitoSpecifications
        .byModeloEjecucionId(idModeloEjecucion).and(specByQuery);
    Specification<ModeloTipoHito> specActivosSolicitud = ModeloTipoHitoSpecifications.activosSolcitud();

    Specification<ModeloTipoHito> specs = Specification.where(specByModeloEjecucion).and(specActivosSolicitud)
        .and(specByQuery);

    Page<ModeloTipoHito> returnValue = modeloTipoHitoRepository.findAll(specs, pageable);
    log.debug(
        "findAllByModeloEjecucionActivosProyecto(Long idModeloEjecucion, List<QueryCriteria> query, Pageable pageable) - end");
    return returnValue;
  }

}
