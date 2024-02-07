package org.crue.hercules.sgi.eti.repository.custom;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.eti.dto.FormlyOutput;
import org.crue.hercules.sgi.eti.enums.Language;
import org.crue.hercules.sgi.eti.model.Formly;
import org.crue.hercules.sgi.eti.model.FormlyNombre;
import org.crue.hercules.sgi.eti.model.FormlyNombre_;
import org.crue.hercules.sgi.eti.model.Formly_;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Custom repository para {@link Formly}.
 */
@Slf4j
@Component
public class CustomFormlyRepositoryImpl implements CustomFormlyRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Devuelve una lista de {@link Formly}
   * 
   * 
   * @param idFormly Id de {@link Formly}.
   * @return lista de {@link Formly}
   */
  @Override
  public List<FormlyOutput> findByFormlyId(Long idFormly) {

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    // Count query
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<FormlyNombre> rootCount = countQuery.from(FormlyNombre.class);

    CriteriaQuery<FormlyOutput> cq = cb.createQuery(FormlyOutput.class);

    // Define FROM clause
    Root<FormlyNombre> root = cq.from(FormlyNombre.class);

    Join<FormlyNombre, Formly> joinFormly = root.join(FormlyNombre_.formly, JoinType.LEFT);
    Join<FormlyNombre, Formly> joinCountFormly = rootCount.join(FormlyNombre_.formly, JoinType.LEFT);
    countQuery.select(cb.count(joinCountFormly));

    cq.multiselect(joinFormly.get(Formly_.id), root.get(FormlyNombre_.nombre),
        joinFormly.get(Formly_.version), root.get(FormlyNombre_.esquema), root.get(FormlyNombre_.lang));
    // Where
    cq.where(
        cb.equal(joinFormly.get(Formly_.id), idFormly));

    countQuery.where(
        cb.equal(joinFormly.get(Formly_.id), idFormly));

    TypedQuery<FormlyOutput> typedQuery = entityManager.createQuery(cq);

    List<FormlyOutput> result = typedQuery.getResultList();

    log.debug("findByFormlyIdAndPadreId : {} - end");
    return result;
  }

  /**
   * Devuelve una lista de {@link Formly}
   * 
   * 
   * @param nombre nombre de {@link Formly}.
   * @return lista de {@link Formly}
   */
  @Override
  public FormlyOutput findByNombreOrderByVersionDesc(String nombre, String lang) {

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    CriteriaQuery<FormlyOutput> cq = cb.createQuery(FormlyOutput.class);

    // Define FROM clause
    Root<FormlyNombre> root = cq.from(FormlyNombre.class);

    Join<FormlyNombre, Formly> joinFormly = root.join(FormlyNombre_.formly, JoinType.LEFT);

    cq.multiselect(joinFormly.get(Formly_.id), root.get(FormlyNombre_.nombre),
        joinFormly.get(Formly_.version), root.get(FormlyNombre_.esquema), root.get(FormlyNombre_.lang));
    // Where
    cq.where(
        cb.and(cb.equal(root.get(FormlyNombre_.lang), Language.fromCode(lang)),
            cb.equal(root.get(FormlyNombre_.nombre), nombre)));
    cq.orderBy(cb.desc(joinFormly.get(Formly_.version)));
    TypedQuery<FormlyOutput> typedQuery = entityManager.createQuery(cq);

    FormlyOutput result = typedQuery.getSingleResult();

    log.debug("findByFormlyIdAndPadreId : {} - end");
    return result;
  }
}