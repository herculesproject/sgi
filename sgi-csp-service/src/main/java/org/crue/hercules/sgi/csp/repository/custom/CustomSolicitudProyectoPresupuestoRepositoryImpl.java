package org.crue.hercules.sgi.csp.repository.custom;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.csp.dto.SolicitudProyectoPresupuestoTotalConceptoGasto;
import org.crue.hercules.sgi.csp.dto.SolicitudProyectoPresupuestoTotales;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto_;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPresupuesto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPresupuesto_;
import org.crue.hercules.sgi.csp.model.Solicitud_;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link Proyecto}.
 */
@Slf4j
@Component
public class CustomSolicitudProyectoPresupuestoRepositoryImpl implements CustomSolicitudProyectoPresupuestoRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Obtiene el {@link SolicitudProyectoPresupuestoTotales} de la
   * {@link Solicitud}.
   * 
   * @param solicitudId Id de la {@link Solicitud}.
   * @return {@link SolicitudProyectoPresupuestoTotales}.
   */
  @Override
  public SolicitudProyectoPresupuestoTotales getTotales(Long solicitudId) {
    log.debug("SolicitudProyectoPresupuestoTotales getTotales(Long solicitudId) - start");
    // Crete query
    final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    final CriteriaQuery<SolicitudProyectoPresupuestoTotales> cq = cb
        .createQuery(SolicitudProyectoPresupuestoTotales.class);

    // Define FROM Solicitud clause
    Root<Solicitud> root = cq.from(Solicitud.class);

    // Total convocatoria
    Subquery<BigDecimal> sqTotalConvocatoria = cq.subquery(BigDecimal.class);
    Root<SolicitudProyectoPresupuesto> rootTotalConvocatoria = sqTotalConvocatoria
        .from(SolicitudProyectoPresupuesto.class);
    Join<SolicitudProyectoPresupuesto, SolicitudProyecto> joinTotalConvocatoriaSolicitudProyecto = rootTotalConvocatoria
        .join(SolicitudProyectoPresupuesto_.solicitudProyecto);
    joinTotalConvocatoriaSolicitudProyecto.join(SolicitudProyecto_.solicitud);

    sqTotalConvocatoria.select(cb.sum(rootTotalConvocatoria.get(SolicitudProyectoPresupuesto_.importeSolicitado)));
    sqTotalConvocatoria
        .where(cb.and(cb.isTrue(rootTotalConvocatoria.get(SolicitudProyectoPresupuesto_.financiacionAjena)).not(),
            cb.equal(rootTotalConvocatoria.get(SolicitudProyectoPresupuesto_.solicitudProyecto)
                .get(SolicitudProyecto_.solicitud).get(Solicitud_.id), root.get(Solicitud_.id))));

    // Total ajeno
    Subquery<BigDecimal> sqTotalAjeno = cq.subquery(BigDecimal.class);
    Root<SolicitudProyectoPresupuesto> rootTotalAjeno = sqTotalAjeno.from(SolicitudProyectoPresupuesto.class);
    Join<SolicitudProyectoPresupuesto, SolicitudProyecto> joinTotalAjenoSolicitudProyecto = rootTotalAjeno
        .join(SolicitudProyectoPresupuesto_.solicitudProyecto);
    joinTotalAjenoSolicitudProyecto.join(SolicitudProyecto_.solicitud);

    sqTotalAjeno.select(cb.sum(rootTotalAjeno.get(SolicitudProyectoPresupuesto_.importeSolicitado)));
    sqTotalAjeno.where(cb.and(cb.isTrue(rootTotalAjeno.get(SolicitudProyectoPresupuesto_.financiacionAjena)),
        cb.equal(rootTotalAjeno.get(SolicitudProyectoPresupuesto_.solicitudProyecto)
            .get(SolicitudProyecto_.solicitud).get(Solicitud_.id), root.get(Solicitud_.id))));

    cq.where(cb.equal(root.get(Solicitud_.id), solicitudId));

    // Define DTO projection
    cq.multiselect(
        // total convocatoria
        cb.coalesce(sqTotalConvocatoria.getSelection(), new BigDecimal(0)),
        // total ajeno
        cb.coalesce(sqTotalAjeno.getSelection(), new BigDecimal(0)),
        // total
        cb.sum(cb.coalesce(sqTotalConvocatoria.getSelection(), new BigDecimal(0)),
            cb.coalesce(sqTotalAjeno.getSelection(), new BigDecimal(0))));

    // Execute query
    final TypedQuery<SolicitudProyectoPresupuestoTotales> q = entityManager.createQuery(cq);

    final SolicitudProyectoPresupuestoTotales result = q.getSingleResult();

    log.debug("SolicitudProyectoPresupuestoTotales getTotales(Long solicitudId) - end");
    return result;
  }

  /**
   * Obtiene los {@link SolicitudProyectoPresupuestoTotalConceptoGasto} de la
   * {@link Solicitud}.
   * 
   * @param solicitudId Id de la {@link Solicitud}.
   * @return lista de {@link SolicitudProyectoPresupuestoTotalConceptoGasto}.
   */
  @Override
  public List<SolicitudProyectoPresupuestoTotalConceptoGasto> getSolicitudProyectoPresupuestoTotalConceptoGastos(
      Long solicitudId) {
    log.debug(
        "SolicitudProyectoPresupuestoTotales getSolicitudProyectoPresupuestoTotalConceptoGastos(Long solicitudId) - start");

    // Crete query
    final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    final CriteriaQuery<SolicitudProyectoPresupuestoTotalConceptoGasto> cq = cb
        .createQuery(SolicitudProyectoPresupuestoTotalConceptoGasto.class);

    // Define FROM SolicitudProyectoPresupuesto clause
    Root<SolicitudProyectoPresupuesto> root = cq.from(SolicitudProyectoPresupuesto.class);
    Join<SolicitudProyectoPresupuesto, SolicitudProyecto> joinSolicitudProyecto = root
        .join(SolicitudProyectoPresupuesto_.solicitudProyecto);
    joinSolicitudProyecto.join(SolicitudProyecto_.solicitud);

    cq.where(cb.equal(root.get(SolicitudProyectoPresupuesto_.solicitudProyecto).get(SolicitudProyecto_.solicitud)
        .get(Solicitud_.id), solicitudId));
    cq.groupBy(root.get(SolicitudProyectoPresupuesto_.conceptoGasto));

    // Define DTO projection
    cq.multiselect(
        // total convocatoria
        root.get(SolicitudProyectoPresupuesto_.conceptoGasto),
        // total
        cb.sum(root.get(SolicitudProyectoPresupuesto_.importeSolicitado)));

    // Execute query
    final TypedQuery<SolicitudProyectoPresupuestoTotalConceptoGasto> q = entityManager.createQuery(cq);

    final List<SolicitudProyectoPresupuestoTotalConceptoGasto> result = q.getResultList();

    log.debug(
        "SolicitudProyectoPresupuestoTotales getSolicitudProyectoPresupuestoTotalConceptoGastos(Long solicitudId) - start");
    return result;
  }

}
