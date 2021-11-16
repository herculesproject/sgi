package org.crue.hercules.sgi.pii.service;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.exceptions.RepartoGastoNotFoundException;
import org.crue.hercules.sgi.pii.model.Reparto;
import org.crue.hercules.sgi.pii.model.RepartoGasto;
import org.crue.hercules.sgi.pii.repository.RepartoGastoRepository;
import org.crue.hercules.sgi.pii.repository.specification.RepartoGastoSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Negocio de la entidad RepartoGasto.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class RepartoGastoService {

  private final RepartoGastoRepository repository;

  public RepartoGastoService(RepartoGastoRepository repartoIngresoRepository) {
    this.repository = repartoIngresoRepository;
  }

  /**
   * Obtiene los {@link RepartoGasto} para una entidad {@link Reparto} paginadas
   * y/o filtradas.
   * 
   * @param repartoId el id de la entidad {@link Reparto}.
   * @param query     la información del filtro.
   * @param pageable  la información de la paginación.
   * @return la lista de {@link RepartoGasto} de la entidad {@link Reparto}
   *         paginadas y/o filtradas.
   */
  public Page<RepartoGasto> findByRepartoId(Long repartoId, String query, Pageable pageable) {
    log.debug("findByRepartoId(Long repartoId, String query, Pageable pageable) - start");

    Specification<RepartoGasto> specs = RepartoGastoSpecifications.byRepartoId(repartoId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<RepartoGasto> returnValue = repository.findAll(specs, pageable);
    log.debug("findByRepartoId(Long repartoId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link RepartoGasto} por su id.
   *
   * @param id el id de la entidad {@link RepartoGasto}.
   * @return la entidad {@link RepartoGasto}.
   */
  public RepartoGasto findById(Long id) {
    log.debug("findById(Long id)  - start");
    final RepartoGasto returnValue = repository.findById(id).orElseThrow(() -> new RepartoGastoNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Guardar un nuevo {@link RepartoGasto}.
   *
   * @param repartoGasto la entidad {@link RepartoGasto} a guardar.
   * @return la entidad {@link RepartoGasto} persistida.
   */
  @Transactional
  public RepartoGasto create(RepartoGasto repartoGasto) {
    log.debug("create(RepartoGasto repartoGasto) - start");

    Assert.isNull(repartoGasto.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "isNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(RepartoGasto.class)).build());

    RepartoGasto returnValue = repository.save(repartoGasto);

    log.debug("create(RepartoGasto repartoGasto) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link RepartoGasto}.
   *
   * @param repartoGasto la entidad {@link RepartoGasto} a actualizar.
   * @return la entidad {@link RepartoGasto} persistida.
   */
  @Transactional
  public RepartoGasto update(RepartoGasto repartoGasto) {
    log.debug("update(RepartoGasto repartoGasto) - start");

    Assert.notNull(repartoGasto.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(RepartoGasto.class)).build());

    return repository.findById(repartoGasto.getId()).map(repartoGastoExistente -> {

      // Establecemos los campos actualizables con los recibidos
      repartoGastoExistente.setImporteADeducir(repartoGasto.getImporteADeducir());

      // Actualizamos la entidad
      RepartoGasto returnValue = repository.save(repartoGastoExistente);
      log.debug("update(RepartoGasto repartoGasto) - end");
      return returnValue;
    }).orElseThrow(() -> new RepartoGastoNotFoundException(repartoGasto.getId()));
  }

  /**
   * Elimina la entidad {@link RepartoGasto} con el id indicado.
   * 
   * @param id el id de la entidad {@link RepartoGasto}.
   */
  @Transactional
  public void deleteById(Long id) {
    log.debug("deleteById(Long id) - start");
    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(RepartoGasto.class)).build());
    if (!repository.existsById(id)) {
      throw new RepartoGastoNotFoundException(id);
    }
    repository.deleteById(id);
    log.debug("deleteById(Long id) - end");
  }
}
