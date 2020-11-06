package org.crue.hercules.sgi.csp.service.impl;

import java.util.List;
import java.util.Optional;

import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaFaseNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaFaseRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.ModeloTipoFaseRepository;
import org.crue.hercules.sgi.csp.repository.specification.ConvocatoriaFaseSpecifications;
import org.crue.hercules.sgi.csp.service.ConvocatoriaFaseService;
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
 * Service Implementation para gestion {@link ConvocatoriaFase}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ConvocatoriaFaseServiceImpl implements ConvocatoriaFaseService {

  private final ConvocatoriaFaseRepository repository;
  private final ConvocatoriaRepository convocatoriaRepository;
  private final ModeloTipoFaseRepository modeloTipoFaseRepository;

  public ConvocatoriaFaseServiceImpl(ConvocatoriaFaseRepository repository,
      ConvocatoriaRepository convocatoriaRepository, ModeloTipoFaseRepository modeloTipoFaseRepository) {
    this.repository = repository;
    this.convocatoriaRepository = convocatoriaRepository;
    this.modeloTipoFaseRepository = modeloTipoFaseRepository;
  }

  /**
   * Guarda la entidad {@link ConvocatoriaFase}.
   * 
   * @param convocatoriaFase la entidad {@link ConvocatoriaFase} a guardar.
   * @return ConvocatoriaFase la entidad {@link ConvocatoriaFase} persistida.
   */
  @Override
  @Transactional
  public ConvocatoriaFase create(ConvocatoriaFase convocatoriaFase) {
    log.debug("create(ConvocatoriaFase convocatoriaFase) - start");

    Assert.isNull(convocatoriaFase.getId(), "Id tiene que ser null para crear ConvocatoriaFase");

    Assert.isTrue(convocatoriaFase.getConvocatoria() != null && convocatoriaFase.getConvocatoria().getId() != null,
        "Id Convocatoria no puede ser null para crear ConvocatoriaFase");

    Assert.isTrue(convocatoriaFase.getTipoFase() != null && convocatoriaFase.getTipoFase().getId() != null,
        "Id Fase no puede ser null para crear ConvocatoriaFase");

    Assert.notNull(convocatoriaFase.getFechaInicio(),
        "La fecha de inicio no puede ser null para crear ConvocatoriaFase");

    Assert.notNull(convocatoriaFase.getFechaFin(), "La fecha de fin no puede ser null para crear ConvocatoriaFase");

    Assert.isTrue(convocatoriaFase.getFechaFin().compareTo(convocatoriaFase.getFechaInicio()) >= 0,
        "La fecha de fecha de fin debe ser posterior a la fecha de inicio");

    convocatoriaFase.setConvocatoria(convocatoriaRepository.findById(convocatoriaFase.getConvocatoria().getId())
        .orElseThrow(() -> new ConvocatoriaNotFoundException(convocatoriaFase.getConvocatoria().getId())));

    // TipoFase
    Optional<ModeloTipoFase> modeloTipoFase = modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(
        convocatoriaFase.getConvocatoria().getModeloEjecucion().getId(), convocatoriaFase.getTipoFase().getId());

    // Está asignado al ModeloEjecucion
    Assert.isTrue(modeloTipoFase.isPresent(),
        "TipoFase '" + convocatoriaFase.getTipoFase().getNombre() + "' no disponible para el ModeloEjecucion '"
            + convocatoriaFase.getConvocatoria().getModeloEjecucion().getNombre() + "'");

    // La asignación al ModeloEjecucion está activa
    Assert.isTrue(modeloTipoFase.get().getActivo(), "ModeloTipoFase '" + modeloTipoFase.get().getTipoFase().getNombre()
        + "' no está activo para el ModeloEjecucion '" + modeloTipoFase.get().getModeloEjecucion().getNombre() + "'");

    // El TipoFase está activo
    Assert.isTrue(modeloTipoFase.get().getTipoFase().getActivo(),
        "TipoFase '" + modeloTipoFase.get().getTipoFase().getNombre() + "' no está activo");

    convocatoriaFase.setTipoFase(modeloTipoFase.get().getTipoFase());

    Assert.isTrue(!existsConvocatoriaFaseConFechasSolapadas(convocatoriaFase),
        "Ya existe una convocatoria en ese rango de fechas");

    ConvocatoriaFase returnValue = repository.save(convocatoriaFase);

    log.debug("create(ConvocatoriaFase convocatoriaFase) - end");
    return returnValue;
  }

  /**
   * Actualiza la entidad {@link ConvocatoriaFase}.
   * 
   * @param convocatoriaFaseActualizar la entidad {@link ConvocatoriaFase} a
   *                                   guardar.
   * @return ConvocatoriaFase la entidad {@link ConvocatoriaFase} persistida.
   */
  @Override
  @Transactional
  public ConvocatoriaFase update(ConvocatoriaFase convocatoriaFaseActualizar) {
    log.debug("update(ConvocatoriaFase convocatoriaFaseActualizar) - start");

    Assert.notNull(convocatoriaFaseActualizar.getId(),
        "ConvocatoriaFase id no puede ser null para actualizar un ConvocatoriaFase");

    Assert.isTrue(
        convocatoriaFaseActualizar.getConvocatoria() != null
            && convocatoriaFaseActualizar.getConvocatoria().getId() != null,
        "Id Convocatoria no puede ser null para actualizar ConvocatoriaFase");

    Assert.isTrue(
        convocatoriaFaseActualizar.getTipoFase() != null && convocatoriaFaseActualizar.getTipoFase().getId() != null,
        "Id Fase no puede ser null para actualizar ConvocatoriaFase");

    Assert.notNull(convocatoriaFaseActualizar.getFechaInicio(),
        "La fecha de inicio no puede ser null para actualizar ConvocatoriaFase");

    Assert.notNull(convocatoriaFaseActualizar.getFechaFin(),
        "La fecha de fin no puede ser null para crear ConvocatoriaFase");

    Assert.isTrue(convocatoriaFaseActualizar.getFechaFin().compareTo(convocatoriaFaseActualizar.getFechaInicio()) >= 0,
        "La fecha de fecha de fin debe ser posterior a la fecha de inicio");

    return repository.findById(convocatoriaFaseActualizar.getId()).map(convocatoriaFase -> {

      // TipoFase
      Optional<ModeloTipoFase> modeloTipoFase = modeloTipoFaseRepository.findByModeloEjecucionIdAndTipoFaseId(
          convocatoriaFase.getConvocatoria().getModeloEjecucion().getId(),
          convocatoriaFaseActualizar.getTipoFase().getId());

      // Está asignado al ModeloEjecucion
      Assert.isTrue(modeloTipoFase.isPresent(),
          "TipoFase '" + convocatoriaFaseActualizar.getTipoFase().getNombre()
              + "' no disponible para el ModeloEjecucion '"
              + convocatoriaFase.getConvocatoria().getModeloEjecucion().getNombre() + "'");

      // La asignación al ModeloEjecucion está activa
      Assert.isTrue(
          modeloTipoFase.get().getTipoFase().getId() == convocatoriaFase.getTipoFase().getId()
              || modeloTipoFase.get().getActivo(),
          "ModeloTipoFase '" + modeloTipoFase.get().getTipoFase().getNombre()
              + "' no está activo para el ModeloEjecucion '" + modeloTipoFase.get().getModeloEjecucion().getNombre()
              + "'");

      // El TipoFase está activo
      Assert.isTrue(
          modeloTipoFase.get().getTipoFase().getId() == convocatoriaFase.getTipoFase().getId()
              || modeloTipoFase.get().getTipoFase().getActivo(),
          "TipoFase '" + modeloTipoFase.get().getTipoFase().getNombre() + "' no está activo");

      convocatoriaFaseActualizar.setTipoFase(modeloTipoFase.get().getTipoFase());

      Assert.isTrue(!existsConvocatoriaFaseConFechasSolapadas(convocatoriaFaseActualizar),
          "Ya existe una convocatoria en ese rango de fechas");

      convocatoriaFase.setFechaInicio(convocatoriaFaseActualizar.getFechaInicio());
      convocatoriaFase.setFechaFin(convocatoriaFaseActualizar.getFechaFin());
      convocatoriaFase.setTipoFase(convocatoriaFaseActualizar.getTipoFase());
      convocatoriaFase.setObservaciones(convocatoriaFaseActualizar.getObservaciones());

      ConvocatoriaFase returnValue = repository.save(convocatoriaFase);
      log.debug("update(ConvocatoriaFase convocatoriaFaseActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ConvocatoriaFaseNotFoundException(convocatoriaFaseActualizar.getId()));

  }

  /**
   * Elimina la {@link ConvocatoriaFase}.
   *
   * @param id Id del {@link ConvocatoriaFase}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id, "ConvocatoriaFase id no puede ser null para eliminar un ConvocatoriaFase");
    if (!repository.existsById(id)) {
      throw new ConvocatoriaFaseNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene {@link ConvocatoriaFase} por su id.
   *
   * @param id el id de la entidad {@link ConvocatoriaFase}.
   * @return la entidad {@link ConvocatoriaFase}.
   */
  @Override
  public ConvocatoriaFase findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ConvocatoriaFase returnValue = repository.findById(id)
        .orElseThrow(() -> new ConvocatoriaFaseNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link ConvocatoriaFase} para una {@link Convocatoria}.
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param query          la información del filtro.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaFase} de la
   *         {@link Convocatoria} paginadas.
   */
  @Override
  public Page<ConvocatoriaFase> findAllByConvocatoria(Long convocatoriaId, List<QueryCriteria> query,
      Pageable pageable) {
    log.debug("findAllByConvocatoria(Long idConvocatoria, List<QueryCriteria> query, Pageable pageable) - start");
    Specification<ConvocatoriaFase> specByQuery = new QuerySpecification<ConvocatoriaFase>(query);
    Specification<ConvocatoriaFase> specByConvocatoria = ConvocatoriaFaseSpecifications
        .byConvocatoriaId(convocatoriaId);

    Specification<ConvocatoriaFase> specs = Specification.where(specByConvocatoria).and(specByQuery);

    Page<ConvocatoriaFase> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByConvocatoria(Long idConvocatoria, List<QueryCriteria> query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Comprueba que existen {@link ConvocatoriaFase} para una {@link Convocatoria}
   * con el mismo {@link TipoFase} y con las fechas solapadas
   *
   * @param convocatoriaFase {@link Convocatoria} a comprobar.
   * 
   * @return true si exite la coincidencia
   */

  private Boolean existsConvocatoriaFaseConFechasSolapadas(ConvocatoriaFase convocatoriaFase) {

    log.debug("existsConvocatoriaFaseConFechasSolapadas(ConvocatoriaFase convocatoriaFase) - start");
    Specification<ConvocatoriaFase> specByRangoFechaSolapados = ConvocatoriaFaseSpecifications
        .byRangoFechaSolapados(convocatoriaFase.getFechaInicio(), convocatoriaFase.getFechaFin());
    Specification<ConvocatoriaFase> specByConvocatoria = ConvocatoriaFaseSpecifications
        .byConvocatoriaId(convocatoriaFase.getConvocatoria().getId());
    Specification<ConvocatoriaFase> specByTipoFase = ConvocatoriaFaseSpecifications
        .byTipoFaseId(convocatoriaFase.getTipoFase().getId());
    Specification<ConvocatoriaFase> specByIdNotEqual = ConvocatoriaFaseSpecifications
        .byIdNotEqual(convocatoriaFase.getId());

    Specification<ConvocatoriaFase> specs = Specification.where(specByConvocatoria).and(specByRangoFechaSolapados)
        .and(specByTipoFase).and(specByIdNotEqual);

    Page<ConvocatoriaFase> convocatoriaFases = repository.findAll(specs, Pageable.unpaged());

    Boolean returnValue = !convocatoriaFases.isEmpty();
    log.debug("existsConvocatoriaFaseConFechasSolapadas(ConvocatoriaFase convocatoriaFase) - end");

    return returnValue;

  }
}
