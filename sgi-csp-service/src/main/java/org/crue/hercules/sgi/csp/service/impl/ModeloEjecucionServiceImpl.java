package org.crue.hercules.sgi.csp.service.impl;

import java.util.List;

import org.crue.hercules.sgi.csp.exceptions.ModeloEjecucionNotFoundException;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion_;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.repository.ModeloEjecucionRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.specification.ModeloEjecucionSpecifications;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoSpecifications;
import org.crue.hercules.sgi.csp.service.ModeloEjecucionService;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link ModeloEjecucion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ModeloEjecucionServiceImpl implements ModeloEjecucionService {
  private static final String MSG_KEY_ENTITY = "entity";
  private static final String MSG_KEY_FIELD = "field";
  private static final String MSG_MODEL_MODELO_EJECUCION = "org.crue.hercules.sgi.csp.model.ModeloEjecucion.message";
  private static final String MSG_ENTITY_EXISTS = "org.springframework.util.Assert.entity.exists.message";

  private final ModeloEjecucionRepository modeloEjecucionRepository;
  private final ProyectoRepository proyectoRepository;

  public ModeloEjecucionServiceImpl(ModeloEjecucionRepository modeloEjecucionRepository,
      ProyectoRepository proyectoRepository) {
    this.modeloEjecucionRepository = modeloEjecucionRepository;
    this.proyectoRepository = proyectoRepository;
  }

  /**
   * Guardar un nuevo {@link ModeloEjecucion}.
   *
   * @param modeloEjecucion la entidad {@link ModeloEjecucion} a guardar.
   * @return la entidad {@link ModeloEjecucion} persistida.
   */
  @Override
  @Transactional
  public ModeloEjecucion create(ModeloEjecucion modeloEjecucion) {
    log.debug("create(ModeloEjecucion modeloEjecucion) - start");

    AssertHelper.idIsNull(modeloEjecucion.getId(), ModeloEjecucion.class);
    Assert.isTrue(!(modeloEjecucionRepository.findByNombreAndActivoIsTrue(modeloEjecucion.getNombre()).isPresent()),
        () -> ProblemMessage.builder()
            .key(MSG_ENTITY_EXISTS)
            .parameter(MSG_KEY_ENTITY,
                ApplicationContextSupport.getMessage(MSG_MODEL_MODELO_EJECUCION))
            .parameter(MSG_KEY_FIELD, modeloEjecucion.getNombre())
            .build());

    modeloEjecucion.setActivo(true);

    ModeloEjecucion returnValue = modeloEjecucionRepository.save(modeloEjecucion);

    log.debug("create(ModeloEjecucion modeloEjecucion) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link ModeloEjecucion}.
   *
   * @param modeloEjecucionActualizar la entidad {@link ModeloEjecucion} a
   *                                  actualizar.
   * @return la entidad {@link ModeloEjecucion} persistida.
   */
  @Override
  @Transactional
  public ModeloEjecucion update(ModeloEjecucion modeloEjecucionActualizar) {
    log.debug("update(ModeloEjecucion modeloEjecucionActualizar) - start");

    AssertHelper.idNotNull(modeloEjecucionActualizar.getId(), ModeloEjecucion.class);
    modeloEjecucionRepository.findByNombreAndActivoIsTrue(modeloEjecucionActualizar.getNombre())
        .ifPresent(modeloEjecucionExistente -> Assert
            .isTrue(modeloEjecucionActualizar.getId().equals(modeloEjecucionExistente.getId()),
                () -> ProblemMessage.builder()
                    .key(MSG_ENTITY_EXISTS)
                    .parameter(MSG_KEY_ENTITY,
                        ApplicationContextSupport.getMessage(MSG_MODEL_MODELO_EJECUCION))
                    .parameter(MSG_KEY_FIELD, modeloEjecucionExistente.getNombre())
                    .build()));

    return modeloEjecucionRepository.findById(modeloEjecucionActualizar.getId()).map(modeloEjecucion -> {
      modeloEjecucion.setNombre(modeloEjecucionActualizar.getNombre());
      modeloEjecucion.setDescripcion(modeloEjecucionActualizar.getDescripcion());
      modeloEjecucion.setExterno(modeloEjecucionActualizar.getExterno());
      modeloEjecucion.setContrato(modeloEjecucionActualizar.getContrato());
      modeloEjecucion.setSolicitudSinConvocatoria(modeloEjecucionActualizar.getSolicitudSinConvocatoria());

      ModeloEjecucion returnValue = modeloEjecucionRepository.save(modeloEjecucion);
      log.debug("update(ModeloEjecucion modeloEjecucionActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ModeloEjecucionNotFoundException(modeloEjecucionActualizar.getId()));
  }

  /**
   * Reactiva el {@link ModeloEjecucion}.
   *
   * @param id Id del {@link ModeloEjecucion}.
   * @return la entidad {@link ModeloEjecucion} persistida.
   */
  @Override
  @Transactional
  public ModeloEjecucion enable(Long id) {
    log.debug("enable(Long id) - start");

    AssertHelper.idNotNull(id, ModeloEjecucion.class);

    return modeloEjecucionRepository.findById(id).map(modeloEjecucion -> {
      if (modeloEjecucion.getActivo()) {
        return modeloEjecucion;
      }

      AssertHelper.entityExists(!(modeloEjecucionRepository.findByNombreAndActivoIsTrue(modeloEjecucion.getNombre())
          .isPresent()), ModeloEjecucion.class, ModeloEjecucion.class);

      modeloEjecucion.setActivo(true);
      ModeloEjecucion returnValue = modeloEjecucionRepository.save(modeloEjecucion);
      log.debug("enable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new ModeloEjecucionNotFoundException(id));
  }

  /**
   * Desactiva el {@link ModeloEjecucion}.
   *
   * @param id Id del {@link ModeloEjecucion}.
   * @return la entidad {@link ModeloEjecucion} persistida.
   */
  @Override
  @Transactional
  public ModeloEjecucion disable(Long id) {
    log.debug("disable(Long id) - start");

    AssertHelper.idNotNull(id, ModeloEjecucion.class);

    return modeloEjecucionRepository.findById(id).map(modeloEjecucion -> {
      if (!modeloEjecucion.getActivo()) {
        return modeloEjecucion;
      }

      modeloEjecucion.setActivo(false);
      ModeloEjecucion returnValue = modeloEjecucionRepository.save(modeloEjecucion);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new ModeloEjecucionNotFoundException(id));
  }

  /**
   * Obtener todas las entidades {@link ModeloEjecucion} activas paginadas y/o
   * filtradas.
   *
   * @param query la información del filtro.
   * @return la lista de entidades {@link ModeloEjecucion} paginadas y/o
   *         filtradas.
   */
  @Override
  public List<ModeloEjecucion> findAll(String query) {
    log.debug("findAll(String query) - start");
    Specification<ModeloEjecucion> specs = ModeloEjecucionSpecifications.distinct()
        .and(ModeloEjecucionSpecifications.activos())
        .and(SgiRSQLJPASupport.toSpecification(query));

    List<ModeloEjecucion> returnValue = modeloEjecucionRepository.findAll(specs,
        Sort.by(Sort.Direction.ASC, ModeloEjecucion_.NOMBRE));
    log.debug("findAll(String query) - end");
    return returnValue;
  }

  /**
   * Comprueba si existen o no {@link ModeloEjecucion} que cumplan con el filtro.
   *
   * @param query la información del filtro.
   * @return Si existen o no {@link ModeloEjecucion} que cumplan con el filtro.
   */
  @Override
  public boolean exists(String query) {
    log.debug("exists(String query) - start");
    Specification<ModeloEjecucion> specs = ModeloEjecucionSpecifications.distinct()
        .and(ModeloEjecucionSpecifications.activos())
        .and(SgiRSQLJPASupport.toSpecification(query));

    boolean returnValue = modeloEjecucionRepository.exists(specs);
    log.debug("exists(String query) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link ModeloEjecucion} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link ModeloEjecucion} paginadas y/o
   *         filtradas.
   */
  @Override
  public Page<ModeloEjecucion> findAllTodos(String query, Pageable pageable) {
    log.debug("findAllTodos(String query, Pageable pageable) - start");
    Specification<ModeloEjecucion> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<ModeloEjecucion> returnValue = modeloEjecucionRepository.findAll(specs, pageable);
    log.debug("findAllTodos(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link ModeloEjecucion} por su id.
   *
   * @param id el id de la entidad {@link ModeloEjecucion}.
   * @return la entidad {@link ModeloEjecucion}.
   */
  @Override
  public ModeloEjecucion findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ModeloEjecucion returnValue = modeloEjecucionRepository.findById(id)
        .orElseThrow(() -> new ModeloEjecucionNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Obtiene un Booleano indicando si el modelo de ejecucion esta asociado a algun
   * proyecto
   *
   * @param id el id de la entidad {@link ModeloEjecucion}.
   * @return true si existe algun proyecto asociado y false si no tiene ninguno.
   */
  @Override
  public Boolean hasProyectosAsociados(Long id) {
    log.debug("hasProyectosAsociados(id)- start");

    Specification<Proyecto> specs = ProyectoSpecifications.byModeloEjecucionId(id);

    Boolean returnValue = proyectoRepository.count(specs) > 0 ? true : false;
    log.debug("hasProyectosAsociados(id) - end");
    return returnValue;
  }
}
