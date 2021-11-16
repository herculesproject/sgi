package org.crue.hercules.sgi.pii.service;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.exceptions.RepartoIngresoNotFoundException;
import org.crue.hercules.sgi.pii.model.Reparto;
import org.crue.hercules.sgi.pii.model.RepartoIngreso;
import org.crue.hercules.sgi.pii.repository.RepartoIngresoRepository;
import org.crue.hercules.sgi.pii.repository.specification.RepartoIngresoSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Negocio de la entidad RepartoIngreso.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class RepartoIngresoService {

  private final RepartoIngresoRepository repository;

  public RepartoIngresoService(RepartoIngresoRepository repartoIngresoRepository) {
    this.repository = repartoIngresoRepository;
  }

  /**
   * Obtiene los {@link RepartoIngreso} para una entidad {@link Reparto} paginadas
   * y/o filtradas.
   * 
   * @param repartoId el id de la entidad {@link Reparto}.
   * @param query     la información del filtro.
   * @param pageable  la información de la paginación.
   * @return la lista de {@link RepartoIngreso} de la entidad {@link Reparto}
   *         paginadas y/o filtradas.
   */
  public Page<RepartoIngreso> findByRepartoId(Long repartoId, String query, Pageable pageable) {
    log.debug("findByRepartoId(Long repartoId, String query, Pageable pageable) - start");

    Specification<RepartoIngreso> specs = RepartoIngresoSpecifications.byRepartoId(repartoId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<RepartoIngreso> returnValue = repository.findAll(specs, pageable);
    log.debug("findByRepartoId(Long repartoId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link RepartoIngreso} por su id.
   *
   * @param id el id de la entidad {@link RepartoIngreso}.
   * @return la entidad {@link RepartoIngreso}.
   */
  public RepartoIngreso findById(Long id) {
    log.debug("findById(Long id)  - start");
    final RepartoIngreso returnValue = repository.findById(id)
        .orElseThrow(() -> new RepartoIngresoNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Guardar un nuevo {@link RepartoIngreso}.
   *
   * @param repartoIngreso la entidad {@link RepartoIngreso} a guardar.
   * @return la entidad {@link RepartoIngreso} persistida.
   */
  @Transactional
  public RepartoIngreso create(RepartoIngreso repartoIngreso) {
    log.debug("create(RepartoIngreso repartoIngreso) - start");

    Assert.isNull(repartoIngreso.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "isNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(RepartoIngreso.class)).build());

    RepartoIngreso returnValue = repository.save(repartoIngreso);

    log.debug("create(RepartoIngreso repartoIngreso) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link RepartoIngreso}.
   *
   * @param repartoIngreso la entidad {@link RepartoIngreso} a actualizar.
   * @return la entidad {@link RepartoIngreso} persistida.
   */
  @Transactional
  public RepartoIngreso update(RepartoIngreso repartoIngreso) {
    log.debug("update(RepartoIngreso repartoIngreso) - start");

    Assert.notNull(repartoIngreso.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(RepartoIngreso.class)).build());

    return repository.findById(repartoIngreso.getId()).map(repartoIngresoExistente -> {

      // Establecemos los campos actualizables con los recibidos
      repartoIngresoExistente.setImporteARepartir(repartoIngreso.getImporteARepartir());

      // Actualizamos la entidad
      RepartoIngreso returnValue = repository.save(repartoIngresoExistente);
      log.debug("update(RepartoIngreso repartoIngreso) - end");
      return returnValue;
    }).orElseThrow(() -> new RepartoIngresoNotFoundException(repartoIngreso.getId()));
  }

  /**
   * Elimina la entidad {@link RepartoIngreso} con el id indicado.
   * 
   * @param id el id de la entidad {@link RepartoIngreso}.
   */
  @Transactional
  public void deleteById(Long id) {
    log.debug("deleteById(Long id) - start");
    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(RepartoIngreso.class)).build());
    if (!repository.existsById(id)) {
      throw new RepartoIngresoNotFoundException(id);
    }
    repository.deleteById(id);
    log.debug("deleteById(Long id) - end");
  }
}
