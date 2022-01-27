package org.crue.hercules.sgi.csp.repository.custom;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.csp.dto.ProyectoPresupuestoTotales;
import org.crue.hercules.sgi.csp.model.AnualidadGasto;
import org.crue.hercules.sgi.csp.model.AnualidadGasto_;
import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.csp.model.Autorizacion_;
import org.crue.hercules.sgi.csp.model.ConceptoGasto_;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoAnualidad_;
import org.crue.hercules.sgi.csp.model.ProyectoPaqueteTrabajo;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocio_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link Autorizacion}.
 */
@Slf4j
@Component
public class CustomAutorizacionRepositoryImpl implements CustomAutorizacionRepository {

  /**
   * The entity manager.
   */
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public List<Long> findIds(Specification<Autorizacion> specification) {
    log.debug("List<Long> findIds(Specification<Autorizacion> specification) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<Autorizacion> root = cq.from(Autorizacion.class);

    cq.select(root.get(Autorizacion_.id)).distinct(true).where(specification.toPredicate(root, cq, cb));

    log.debug("List<Long> findIds(Specification<Autorizacion> specification) - end");

    return entityManager.createQuery(cq).getResultList();
  }
}
