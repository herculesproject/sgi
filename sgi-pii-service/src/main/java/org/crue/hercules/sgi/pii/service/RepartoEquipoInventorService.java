package org.crue.hercules.sgi.pii.service;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.exceptions.RepartoEquipoInventorNotFoundException;
import org.crue.hercules.sgi.pii.model.Reparto;
import org.crue.hercules.sgi.pii.model.RepartoEquipoInventor;
import org.crue.hercules.sgi.pii.repository.RepartoEquipoInventorRepository;
import org.crue.hercules.sgi.pii.repository.specification.RepartoEquipoInventorSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Negocio de la entidad RepartoEquipoInventor.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class RepartoEquipoInventorService {
  private final RepartoEquipoInventorRepository repository;

  public RepartoEquipoInventorService(RepartoEquipoInventorRepository repartoEquipoInventorRepository) {
    this.repository = repartoEquipoInventorRepository;
  }

  /**
   * Obtiene los {@link RepartoEquipoInventor} para una entidad {@link Reparto}
   * paginadas y/o filtradas.
   * 
   * @param repartoId el id de la entidad {@link Reparto}.
   * @param query     la información del filtro.
   * @param pageable  la información de la paginación.
   * @return la lista de {@link RepartoEquipoInventor} de la entidad
   *         {@link Reparto} paginadas y/o filtradas.
   */
  public Page<RepartoEquipoInventor> findByRepartoId(Long repartoId, String query, Pageable pageable) {
    log.debug("findByRepartoId(Long repartoId, String query, Pageable pageable) - start");

    Specification<RepartoEquipoInventor> specs = RepartoEquipoInventorSpecifications.byRepartoId(repartoId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<RepartoEquipoInventor> returnValue = repository.findAll(specs, pageable);
    log.debug("findByRepartoId(Long repartoId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link RepartoEquipoInventor} por su id.
   *
   * @param id el id de la entidad {@link RepartoEquipoInventor}.
   * @return la entidad {@link RepartoEquipoInventor}.
   */
  public RepartoEquipoInventor findById(Long id) {
    log.debug("findById(Long id)  - start");
    final RepartoEquipoInventor returnValue = repository.findById(id)
        .orElseThrow(() -> new RepartoEquipoInventorNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Guardar un nuevo {@link RepartoEquipoInventor}.
   *
   * @param repartoEquipoInventor la entidad {@link RepartoEquipoInventor} a
   *                              guardar.
   * @return la entidad {@link RepartoEquipoInventor} persistida.
   */
  @Transactional
  public RepartoEquipoInventor create(RepartoEquipoInventor repartoEquipoInventor) {
    log.debug("create(RepartoEquipoInventor repartoEquipoInventor) - start");

    Assert.isNull(repartoEquipoInventor.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "isNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(RepartoEquipoInventor.class)).build());

    RepartoEquipoInventor returnValue = repository.save(repartoEquipoInventor);

    log.debug("create(RepartoEquipoInventor repartoEquipoInventor) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link RepartoEquipoInventor}.
   *
   * @param repartoEquipoInventor la entidad {@link RepartoEquipoInventor} a
   *                              actualizar.
   * @return la entidad {@link RepartoEquipoInventor} persistida.
   */
  @Transactional
  public RepartoEquipoInventor update(RepartoEquipoInventor repartoEquipoInventor) {
    log.debug("update(RepartoEquipoInventor repartoEquipoInventor) - start");

    Assert.notNull(repartoEquipoInventor.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(RepartoEquipoInventor.class)).build());

    return repository.findById(repartoEquipoInventor.getId()).map(repartoEquipoInventorExistente -> {

      // Establecemos los campos actualizables con los recibidos
      repartoEquipoInventorExistente.setProyectoRef(repartoEquipoInventor.getProyectoRef());
      repartoEquipoInventorExistente.setImporteNomina(repartoEquipoInventor.getImporteNomina());
      repartoEquipoInventorExistente.setImporteOtros(repartoEquipoInventor.getImporteOtros());
      repartoEquipoInventorExistente.setImporteProyecto(repartoEquipoInventor.getImporteProyecto());

      // Actualizamos la entidad
      RepartoEquipoInventor returnValue = repository.save(repartoEquipoInventorExistente);
      log.debug("update(RepartoEquipoInventor repartoEquipoInventor) - end");
      return returnValue;
    }).orElseThrow(() -> new RepartoEquipoInventorNotFoundException(repartoEquipoInventor.getId()));
  }

  /**
   * Elimina la entidad {@link RepartoEquipoInventor} con el id indicado.
   * 
   * @param id el id de la entidad {@link RepartoEquipoInventor}.
   */
  @Transactional
  public void deleteById(Long id) {
    log.debug("deleteById(Long id) - start");
    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(RepartoEquipoInventor.class)).build());
    if (!repository.existsById(id)) {
      throw new RepartoEquipoInventorNotFoundException(id);
    }
    repository.deleteById(id);
    log.debug("deleteById(Long id) - end");
  }
}
