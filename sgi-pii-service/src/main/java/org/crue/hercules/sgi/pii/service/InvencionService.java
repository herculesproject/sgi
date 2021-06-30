package org.crue.hercules.sgi.pii.service;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.exceptions.InvencionNotFoundException;
import org.crue.hercules.sgi.pii.model.Invencion;
import org.crue.hercules.sgi.pii.model.SectorAplicacion;
import org.crue.hercules.sgi.pii.model.TipoProteccion;
import org.crue.hercules.sgi.pii.repository.InvencionRepository;
import org.crue.hercules.sgi.pii.repository.specification.InvencionSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service para gestionar {@link SectorAplicacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class InvencionService {

  private final InvencionRepository repository;

  public InvencionService(InvencionRepository invencionRepository) {
    this.repository = invencionRepository;
  }

  /**
   * Obtener todas las entidades {@link Invencion} activos paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Invencion} paginadas y/o filtradas.
   */
  public Page<Invencion> findActivos(String query, Pageable pageable) {
    log.debug("findActivos(String query, Pageable pageable) - start");
    Specification<Invencion> specs = InvencionSpecifications.activos().and(SgiRSQLJPASupport.toSpecification(query));

    Page<Invencion> returnValue = repository.findAll(specs, pageable);
    log.debug("findActivos(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link Invencion} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Invencion} paginadas y/o filtradas.
   */
  public Page<Invencion> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<Invencion> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<Invencion> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link Invencion} por su id.
   *
   * @param id el id de la entidad {@link Invencion}.
   * @return la entidad {@link Invencion}.
   */
  public Invencion findById(Long id) {
    log.debug("findById(Long id)  - start");
    final Invencion returnValue = repository.findById(id).orElseThrow(() -> new InvencionNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Guardar un nuevo {@link Invencion}.
   *
   * @param invencion la entidad {@link Invencion} a guardar.
   * @return la entidad {@link Invencion} persistida.
   */
  @Transactional
  public Invencion create(Invencion invencion) {
    log.debug("create(Invencion invencion) - start");

    Assert.isNull(invencion.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "isNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(Invencion.class)).build());
    Assert.notNull(invencion.getTipoProteccion(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage(TipoProteccion.class))
            .parameter("entity", ApplicationContextSupport.getMessage(Invencion.class)).build());
    Assert.notNull(invencion.getTipoProteccion().getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(TipoProteccion.class)).build());

    invencion.setActivo(true);
    Invencion returnValue = repository.save(invencion);

    log.debug("create(Invencion invencion) - end");
    return returnValue;
  }
}
