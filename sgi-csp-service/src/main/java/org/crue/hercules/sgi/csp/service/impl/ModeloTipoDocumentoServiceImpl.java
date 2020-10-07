package org.crue.hercules.sgi.csp.service.impl;

import java.util.List;
import java.util.Optional;

import org.crue.hercules.sgi.csp.exceptions.ModeloEjecucionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ModeloTipoDocumentoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.TipoDocumentoNotFoundException;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoDocumento;
import org.crue.hercules.sgi.csp.repository.ModeloEjecucionRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoFaseRepository;
import org.crue.hercules.sgi.csp.repository.TipoDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.specification.ModeloTipoDocumentoSpecifications;
import org.crue.hercules.sgi.csp.service.ModeloTipoDocumentoService;
import org.crue.hercules.sgi.framework.data.jpa.domain.QuerySpecification;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link ModeloTipoDocumento}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ModeloTipoDocumentoServiceImpl implements ModeloTipoDocumentoService {

  private final ModeloEjecucionRepository modeloEjecucionRepository;
  private final ModeloTipoDocumentoRepository modeloTipoDocumentoRepository;
  private final ModeloTipoFaseRepository modeloTipoFaseRepository;
  private final TipoDocumentoRepository tipoDocumentoRepository;

  public ModeloTipoDocumentoServiceImpl(ModeloEjecucionRepository modeloEjecucionRepository,
      ModeloTipoDocumentoRepository modeloTipoDocumentoRepository, ModeloTipoFaseRepository modeloTipoFaseRepository,
      TipoDocumentoRepository tipoDocumentoRepository) {
    this.modeloEjecucionRepository = modeloEjecucionRepository;
    this.modeloTipoDocumentoRepository = modeloTipoDocumentoRepository;
    this.modeloTipoFaseRepository = modeloTipoFaseRepository;
    this.tipoDocumentoRepository = tipoDocumentoRepository;
  }

  /**
   * Guarda la entidad {@link ModeloTipoDocumento}.
   * 
   * @param modeloTipoDocumento la entidad {@link ModeloTipoDocumento} a guardar.
   * @return la entidad {@link ModeloTipoDocumento} persistida.
   */
  @Transactional
  @Override
  public ModeloTipoDocumento create(ModeloTipoDocumento modeloTipoDocumento) {
    log.debug("create(ModeloTipoDocumento modeloTipoDocumento) - start");

    Assert.isNull(modeloTipoDocumento.getId(), "Id tiene que ser null para crear un modeloTipoDocumento");
    Assert.notNull(modeloTipoDocumento.getModeloEjecucion().getId(),
        "Id ModeloEjecucion no puede ser null para crear un modeloTipoDocumento");
    Assert.notNull(modeloTipoDocumento.getTipoDocumento().getId(),
        "Id TipoDocumento no puede ser null para crear un modeloTipoDocumento");

    modeloTipoDocumento
        .setModeloEjecucion(modeloEjecucionRepository.findById(modeloTipoDocumento.getModeloEjecucion().getId())
            .orElseThrow(() -> new ModeloEjecucionNotFoundException(modeloTipoDocumento.getModeloEjecucion().getId())));

    modeloTipoDocumento
        .setTipoDocumento(tipoDocumentoRepository.findById(modeloTipoDocumento.getTipoDocumento().getId())
            .orElseThrow(() -> new TipoDocumentoNotFoundException(modeloTipoDocumento.getTipoDocumento().getId())));
    Assert.isTrue(modeloTipoDocumento.getTipoDocumento().getActivo(), "El TipoDocumento debe estar Activo");

    if (modeloTipoDocumento.getModeloTipoFase() != null) {
      if (modeloTipoDocumento.getModeloTipoFase().getId() == null) {
        modeloTipoDocumento.setModeloTipoFase(null);
      } else {
        modeloTipoDocumento.setModeloTipoFase(
            modeloTipoFaseRepository.findById(modeloTipoDocumento.getModeloTipoFase().getId()).orElseThrow(
                () -> new ModeloEjecucionNotFoundException(modeloTipoDocumento.getModeloTipoFase().getId())));
        Assert.isTrue(modeloTipoDocumento.getModeloTipoFase().getActivo(), "El ModeloTipoFase debe estar Activo");
      }
    }

    List<ModeloTipoDocumento> listaMismoModeloEjecucionAndTipoDocumento = modeloTipoDocumentoRepository
        .findByModeloEjecucionIdAndTipoDocumentoId(modeloTipoDocumento.getModeloEjecucion().getId(),
            modeloTipoDocumento.getTipoDocumento().getId());

    if (!listaMismoModeloEjecucionAndTipoDocumento.isEmpty()) {

      if (modeloTipoDocumento.getModeloTipoFase() == null && listaMismoModeloEjecucionAndTipoDocumento.stream()
          .anyMatch(modeloTipoDocumentoFilter -> modeloTipoDocumentoFilter.getModeloTipoFase() != null)) {
        throw new IllegalArgumentException(
            "Ya existe una asociación activa para ese ModeloEjecucion y ese TipoDocumento con ModeloTipoFase");
      }

      if (modeloTipoDocumento.getModeloTipoFase() != null && listaMismoModeloEjecucionAndTipoDocumento.stream()
          .anyMatch(modeloTipoDocumentoFilter -> modeloTipoDocumentoFilter.getModeloTipoFase() == null)) {
        throw new IllegalArgumentException(
            "Ya existe una asociación activa para ese ModeloEjecucion y ese TipoDocumento sin ModeloTipoFase");
      }

      // Comprueba si existe ya esa misma relacion
      Optional<ModeloTipoDocumento> modeloTipoDocumentoIgual = listaMismoModeloEjecucionAndTipoDocumento.stream()
          .filter(modeloTipoDocumentoFilter -> modeloTipoDocumentoFilter.getModeloEjecucion()
              .getId() == modeloTipoDocumento.getModeloEjecucion().getId()
              && modeloTipoDocumentoFilter.getTipoDocumento().getId() == modeloTipoDocumento.getTipoDocumento().getId()
              && (modeloTipoDocumentoFilter.getModeloTipoFase() == null
                  && modeloTipoDocumento.getModeloTipoFase() == null
                  || modeloTipoDocumentoFilter.getModeloTipoFase().getId() == modeloTipoDocumento.getModeloTipoFase()
                      .getId()))
          .findFirst();

      modeloTipoDocumentoIgual.ifPresent(modeloTipoDocumentoExistente -> {
        Assert.isTrue(!modeloTipoDocumentoExistente.getActivo(),
            "Ya existe una asociación activa para ese ModeloEjecucion, TipoDocumento y ModeloTipoFase");

        // Si existe pero esta inactiva se settea el id para que se actualice la
        // relacion existente
        modeloTipoDocumento.setId(modeloTipoDocumentoExistente.getId());
      });

    }

    modeloTipoDocumento.setActivo(true);
    ModeloTipoDocumento returnValue = modeloTipoDocumentoRepository.save(modeloTipoDocumento);
    log.debug("create(ModeloTipoDocumento modeloTipoDocumento) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link ModeloTipoDocumento}.
   *
   * @param id Id del {@link ModeloTipoDocumento}.
   * @return la entidad {@link ModeloTipoDocumento} persistida.
   */
  @Transactional
  @Override
  public ModeloTipoDocumento disable(Long id) {
    log.debug("disable(Long id) - start");

    Assert.notNull(id, "ModeloTipoDocumento id no puede ser null para desactivar un ModeloTipoDocumento");

    return modeloTipoDocumentoRepository.findById(id).map(modeloTipoDocumento -> {
      modeloTipoDocumento.setActivo(false);

      ModeloTipoDocumento returnValue = modeloTipoDocumentoRepository.save(modeloTipoDocumento);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new ModeloTipoDocumentoNotFoundException(id));
  }

  /**
   * Obtiene una entidad {@link ModeloTipoDocumento} por id.
   * 
   * @param id Identificador de la entidad {@link ModeloTipoDocumento}.
   * @return la entidad {@link ModeloTipoDocumento}.
   */
  @Override
  public ModeloTipoDocumento findById(final Long id) {
    log.debug("findById(Long id) - start");
    final ModeloTipoDocumento returnValue = modeloTipoDocumentoRepository.findById(id)
        .orElseThrow(() -> new ModeloTipoDocumentoNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link ModeloTipoDocumento} activos para un
   * {@link ModeloEjecucion}.
   *
   * @param idModeloEjecucion el id de la entidad {@link ModeloEjecucion}.
   * @param query             la información del filtro.
   * @param pageable          la información de la paginación.
   * @return la lista de entidades {@link ModeloTipoDocumento} del
   *         {@link ModeloEjecucion} paginadas.
   */
  @Override
  public Page<ModeloTipoDocumento> findAllByModeloEjecucion(Long idModeloEjecucion, List<QueryCriteria> query,
      Pageable pageable) {
    log.debug("findAllByModeloEjecucion(Long idModeloEjecucion, List<QueryCriteria> query, Pageable pageable) - start");
    Specification<ModeloTipoDocumento> specByQuery = new QuerySpecification<ModeloTipoDocumento>(query);
    Specification<ModeloTipoDocumento> specActivos = ModeloTipoDocumentoSpecifications.activos();
    Specification<ModeloTipoDocumento> specByModeloEjecucion = ModeloTipoDocumentoSpecifications
        .byModeloEjecucionId(idModeloEjecucion);

    Specification<ModeloTipoDocumento> specs = Specification.where(specActivos).and(specByModeloEjecucion)
        .and(specByQuery);

    Page<ModeloTipoDocumento> returnValue = modeloTipoDocumentoRepository.findAll(specs, pageable);
    log.debug("findAllByModeloEjecucion(Long idModeloEjecucion, List<QueryCriteria> query, Pageable pageable) - end");
    return returnValue;
  }

}
