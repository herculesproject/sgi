package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.csp.model.NotificacionProyectoExternoCVN;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.repository.NotificacionProyectoExternoCVNRepository;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de
 * {@link NotificacionProyectoExternoCVN}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class NotificacionProyectoExternoCVNService {

  private final NotificacionProyectoExternoCVNRepository repository;

  public NotificacionProyectoExternoCVNService(
      NotificacionProyectoExternoCVNRepository notificacionProyectoExternoCVNRepository) {
    this.repository = notificacionProyectoExternoCVNRepository;
  }

  /**
   * Obtener todas las entidades {@link NotificacionProyectoExternoCVN} paginadas
   * y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link NotificacionProyectoExternoCVN}
   *         paginadas y/o filtradas.
   */
  public Page<NotificacionProyectoExternoCVN> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<NotificacionProyectoExternoCVN> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<NotificacionProyectoExternoCVN> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;

  }

  /**
   * Guarda la entidad {@link NotificacionProyectoExternoCVN}.
   *
   * @param notificacionProyectoExternoCVN la entidad
   *                                       {@link NotificacionProyectoExternoCVN}
   *                                       a guardar.
   * @return proyecto la entidad {@link NotificacionProyectoExternoCVN}
   *         persistida.
   */
  @Transactional
  public NotificacionProyectoExternoCVN create(NotificacionProyectoExternoCVN notificacionProyectoExternoCVN) {
    log.debug("create(NotificacionProyectoExternoCVN notificacionProyectoExternoCVN) - start");

    // Crea la notificacion proyecto externo CVN
    NotificacionProyectoExternoCVN returnValue = repository.save(notificacionProyectoExternoCVN);

    log.debug("create(NotificacionProyectoExternoCVN notificacionProyectoExternoCVN) - end");
    return returnValue;
  }

  /**
   * Devuelve la {@link NotificacionProyectoExternoCVN} asociada a la
   * {@link Autorizacion}.
   * 
   * @param id id del {@link Autorizacion}.
   * @return el {@link NotificacionProyectoExternoCVN}.
   */
  public NotificacionProyectoExternoCVN findByAutorizacionId(Long id) {
    log.debug("findByAutorizacionId(Long id) - start");
    NotificacionProyectoExternoCVN returnValue = repository.findByAutorizacionId(id).orElse(null);
    log.debug("findByAutorizacionId(Long id) - end");
    return returnValue;
  }

  /**
   * Comprueba si existen datos vinculados a {@link Autorizacion} de
   * {@link NotificacionProyectoExternoCVN}
   *
   * @param autorizacionId Id del {@link Autorizacion}.
   * @return si existe o no el Autorizacion
   */
  public boolean existsByAutorizacionId(Long autorizacionId) {
    return repository.existsByAutorizacionId(autorizacionId);
  }

  /**
   * 
   * Recupera una lista de objetos {@link NotificacionProyectoExternoCVN} de un
   * {@link Proyecto}
   * 
   * @param proyectoId Identificador del {@link Proyecto}
   * @return lista de {@link NotificacionProyectoExternoCVN}
   */
  public List<NotificacionProyectoExternoCVN> findByProyectoId(Long proyectoId) {
    return this.repository.findByProyectoId(proyectoId);
  }

}
