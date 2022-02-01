package org.crue.hercules.sgi.csp.repository.custom;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.csp.model.Autorizacion_;
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
