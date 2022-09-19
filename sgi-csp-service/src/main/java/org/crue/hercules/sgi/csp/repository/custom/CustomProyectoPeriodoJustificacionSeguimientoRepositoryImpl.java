package org.crue.hercules.sgi.csp.repository.custom;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.dto.SeguimientoJustificacionAnualidad;
import org.crue.hercules.sgi.csp.model.ProyectoAnualidad;
import org.crue.hercules.sgi.csp.model.ProyectoAnualidad_;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacionSeguimiento;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacionSeguimiento_;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacion_;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge_;
import org.crue.hercules.sgi.csp.model.RequerimientoJustificacion;
import org.crue.hercules.sgi.csp.model.RequerimientoJustificacion_;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA custom repository para
 * {@link ProyectoPeriodoJustificacionSeguimiento}.
 */
@Slf4j
@Component
public class CustomProyectoPeriodoJustificacionSeguimientoRepositoryImpl implements
    CustomProyectoPeriodoJustificacionSeguimientoRepository {

  /**
   * The entity manager.
   */
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public List<SeguimientoJustificacionAnualidad> findSeguimientosJustificacionAnualidadByProyectoSgeRef(
      String proyectoSgeRef) {
    log.debug("findSeguimientosJustificacionProyectoAnualidadByProyectoSgeRef(String proyectoSgeRef) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<SeguimientoJustificacionAnualidad> cq = cb
        .createQuery(SeguimientoJustificacionAnualidad.class);
    Root<ProyectoPeriodoJustificacion> root = cq.from(ProyectoPeriodoJustificacion.class);

    Join<ProyectoPeriodoJustificacion, RequerimientoJustificacion> joinRequerimiento = root.join(
        ProyectoPeriodoJustificacion_.requerimientosJustificacion, JoinType.INNER);
    Join<RequerimientoJustificacion, ProyectoProyectoSge> joinProyecto = joinRequerimiento.join(
        RequerimientoJustificacion_.proyectoProyectoSge, JoinType.INNER);
    Join<ProyectoPeriodoJustificacion, ProyectoPeriodoJustificacionSeguimiento> joinSeguimiento = root.join(
        ProyectoPeriodoJustificacion_.proyectoPeriodoJustificacionSeguimiento, JoinType.LEFT);
    Join<ProyectoPeriodoJustificacionSeguimiento, ProyectoAnualidad> joinAnualidad = joinSeguimiento.join(
        ProyectoPeriodoJustificacionSeguimiento_.proyectoAnualidad, JoinType.LEFT);

    cq.multiselect(
        root.get(ProyectoPeriodoJustificacion_.id),
        joinSeguimiento.get(ProyectoPeriodoJustificacionSeguimiento_.id),
        joinProyecto.get(ProyectoProyectoSge_.proyectoId),
        root.get(ProyectoPeriodoJustificacion_.identificadorJustificacion),
        root.get(ProyectoPeriodoJustificacion_.fechaPresentacionJustificacion),
        joinAnualidad.get(ProyectoAnualidad_.anio)).distinct(Boolean.TRUE);

    cq.where(cb.equal(joinProyecto.get(ProyectoProyectoSge_.proyectoSgeRef), proyectoSgeRef));

    cq.orderBy(cb.desc(joinProyecto.get(ProyectoProyectoSge_.proyectoId)));

    TypedQuery<SeguimientoJustificacionAnualidad> typedQuery = entityManager.createQuery(cq);
    List<SeguimientoJustificacionAnualidad> result = typedQuery.getResultList();

    log.debug("findSeguimientosJustificacionProyectoAnualidadByProyectoSgeRef(String proyectoSgeRef) - end");
    return result;
  }

}
