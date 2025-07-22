package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.exceptions.TipoDocumentoNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.repository.TipoDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.specification.TipoDocumentoSpecifications;
import org.crue.hercules.sgi.csp.service.TipoDocumentoService;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link TipoDocumento}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class TipoDocumentoServiceImpl implements TipoDocumentoService {
  private static final String MSG_KEY_ENTITY = "entity";
  private static final String MSG_KEY_FIELD = "field";
  private static final String MSG_MODEL_TIPO_DOCUMENTO = "org.crue.hercules.sgi.csp.model.TipoDocumento.message";
  private static final String MSG_ENTITY_EXISTS = "org.springframework.util.Assert.entity.exists.message";

  private final TipoDocumentoRepository tipoDocumentoRepository;

  public TipoDocumentoServiceImpl(TipoDocumentoRepository tipoDocumentoRepository) {
    this.tipoDocumentoRepository = tipoDocumentoRepository;
  }

  /**
   * Guardar un nuevo {@link TipoDocumento}.
   *
   * @param tipoDocumento la entidad {@link TipoDocumento} a guardar.
   * @return la entidad {@link TipoDocumento} persistida.
   */
  @Override
  @Transactional
  public TipoDocumento create(TipoDocumento tipoDocumento) {
    log.debug("create(TipoDocumento tipoDocumento) - start");

    AssertHelper.idIsNull(tipoDocumento.getId(), TipoDocumento.class);
    Assert.isTrue(!(tipoDocumentoRepository.findByNombreAndActivoIsTrue(tipoDocumento.getNombre()).isPresent()),
        () -> ProblemMessage.builder()
            .key(MSG_ENTITY_EXISTS)
            .parameter(MSG_KEY_ENTITY,
                ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_DOCUMENTO))
            .parameter(MSG_KEY_FIELD, tipoDocumento.getNombre())
            .build());

    tipoDocumento.setActivo(Boolean.TRUE);
    TipoDocumento returnValue = tipoDocumentoRepository.save(tipoDocumento);

    log.debug("create(TipoDocumento tipoDocumento) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link TipoDocumento}.
   *
   * @param tipoDocumentoActualizar la entidad {@link TipoDocumento} a actualizar.
   * @return la entidad {@link TipoDocumento} persistida.
   */
  @Override
  @Transactional
  public TipoDocumento update(TipoDocumento tipoDocumentoActualizar) {
    log.debug("update(TipoDocumento tipoDocumento) - start");

    AssertHelper.idNotNull(tipoDocumentoActualizar.getId(), TipoDocumento.class);
    tipoDocumentoRepository.findByNombreAndActivoIsTrue(tipoDocumentoActualizar.getNombre())
        .ifPresent(tipoDocumentoExistente -> AssertHelper.entityExists(
            Objects.equals(tipoDocumentoActualizar.getId(), tipoDocumentoExistente.getId()),
            TipoDocumento.class, TipoDocumento.class));

    return tipoDocumentoRepository.findById(tipoDocumentoActualizar.getId()).map(tipoDocumento -> {
      tipoDocumento.setNombre(tipoDocumentoActualizar.getNombre());
      tipoDocumento.setDescripcion(tipoDocumentoActualizar.getDescripcion());

      TipoDocumento returnValue = tipoDocumentoRepository.save(tipoDocumento);
      log.debug("update(TipoDocumento tipoDocumento) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoDocumentoNotFoundException(tipoDocumentoActualizar.getId()));
  }

  /**
   * Reactiva el {@link TipoDocumento}.
   *
   * @param id Id del {@link TipoDocumento}.
   * @return la entidad {@link TipoDocumento} persistida.
   */
  @Override
  @Transactional
  public TipoDocumento enable(Long id) {
    log.debug("enable(Long id) - start");

    AssertHelper.idNotNull(id, TipoDocumento.class);

    return tipoDocumentoRepository.findById(id).map(tipoDocumento -> {
      if (Boolean.TRUE.equals(tipoDocumento.getActivo())) {
        return tipoDocumento;
      }

      Assert.isTrue(!(tipoDocumentoRepository.findByNombreAndActivoIsTrue(tipoDocumento.getNombre()).isPresent()),
          () -> ProblemMessage.builder()
              .key(MSG_ENTITY_EXISTS)
              .parameter(MSG_KEY_ENTITY,
                  ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_DOCUMENTO))
              .parameter(MSG_KEY_FIELD, tipoDocumento.getNombre())
              .build());

      tipoDocumento.setActivo(true);
      TipoDocumento returnValue = tipoDocumentoRepository.save(tipoDocumento);
      log.debug("enable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoDocumentoNotFoundException(id));
  }

  /**
   * Desactiva el {@link TipoDocumento}.
   *
   * @param id Id del {@link TipoDocumento}.
   * @return la entidad {@link TipoDocumento} persistida.
   */
  @Override
  @Transactional
  public TipoDocumento disable(Long id) {
    log.debug("disable(Long id) - start");

    AssertHelper.idNotNull(id, TipoDocumento.class);

    return tipoDocumentoRepository.findById(id).map(tipoDocumento -> {
      if (Boolean.FALSE.equals(tipoDocumento.getActivo())) {
        return tipoDocumento;
      }

      tipoDocumento.setActivo(false);
      TipoDocumento returnValue = tipoDocumentoRepository.save(tipoDocumento);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoDocumentoNotFoundException(id));
  }

  /**
   * Obtener todas las entidades {@link TipoDocumento} activas paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TipoDocumento} paginadas y/o filtradas.
   */
  @Override
  public Page<TipoDocumento> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<TipoDocumento> specs = TipoDocumentoSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<TipoDocumento> returnValue = tipoDocumentoRepository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link TipoDocumento} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TipoDocumento} paginadas y/o filtradas.
   */
  @Override
  public Page<TipoDocumento> findAllTodos(String query, Pageable pageable) {
    log.debug("findAllTodos(String query, Pageable pageable) - start");
    Specification<TipoDocumento> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<TipoDocumento> returnValue = tipoDocumentoRepository.findAll(specs, pageable);
    log.debug("findAllTodos(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link TipoDocumento} por su id.
   *
   * @param id el id de la entidad {@link TipoDocumento}.
   * @return la entidad {@link TipoDocumento}.
   */
  @Override
  public TipoDocumento findById(Long id) {
    log.debug("findById(Long id)  - start");
    final TipoDocumento returnValue = tipoDocumentoRepository.findById(id)
        .orElseThrow(() -> new TipoDocumentoNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link TipoDocumento} asociados a la fase de
   * presentación de solicitudes de la {@link Convocatoria}.
   *
   * @param convocatoriaId identificador de la {@link Convocatoria}.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link TipoDocumento} paginadas y/o filtradas.
   */
  @Override
  public Page<TipoDocumento> findAllTipoDocumentosFasePresentacionConvocatoria(Long convocatoriaId, Pageable pageable) {
    log.debug("findAllTipoDocumentosFasePresentacionConvocatoria(Long convocatoriaId, Pageable pageable) - start");
    Specification<TipoDocumento> specActivos = TipoDocumentoSpecifications.activos();
    Specification<TipoDocumento> specTipoDocumentosFasePresentacionConvocatoria = TipoDocumentoSpecifications
        .tipoDocumentosFasePresentacionConvocatoria(convocatoriaId);

    Specification<TipoDocumento> specs = Specification.where(specActivos)
        .and(specTipoDocumentosFasePresentacionConvocatoria);

    Page<TipoDocumento> returnValue = tipoDocumentoRepository.findAll(specs, pageable);
    log.debug("findAllTipoDocumentosFasePresentacionConvocatoria(Long convocatoriaId, Pageable pageable) - end");
    return returnValue;
  }

}
