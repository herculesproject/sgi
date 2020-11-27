package org.crue.hercules.sgi.eti.repository.custom;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Predicate;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Memoria_;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion_;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Custom repository para {@link PeticionEvaluacion}.
 */
@Slf4j
@Component
public class CustomPeticionEvaluacionRepositoryImpl implements CustomPeticionEvaluacionRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public Page<PeticionEvaluacion> findAllPeticionEvaluacionMemoria(Specification<Memoria> specsMem, Pageable pageable,
      String personaRefConsulta) {
    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<PeticionEvaluacion> cq = cb.createQuery(PeticionEvaluacion.class);
    Root<PeticionEvaluacion> root = cq.from(PeticionEvaluacion.class);

    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<PeticionEvaluacion> rootCount = countQuery.from(PeticionEvaluacion.class);
    countQuery.select(cb.count(rootCount));

    Predicate predicateMemoria = cb.in(root.get(PeticionEvaluacion_.id))
        .value(getIdsPeticionEvaluacionMemoria(root, cb, cq, specsMem, personaRefConsulta));
    Predicate predicateMemoriaCount = cb.in(rootCount.get(PeticionEvaluacion_.id))
        .value(getIdsPeticionEvaluacionMemoria(rootCount, cb, cq, specsMem, personaRefConsulta));

    Predicate predicatePersonaRef = null;
    Predicate predicatePersonaRefCount = null;
    if (personaRefConsulta != null) {
      predicatePersonaRef = cb.equal(root.get(PeticionEvaluacion_.personaRef), personaRefConsulta);
      predicatePersonaRefCount = cb.equal(rootCount.get(PeticionEvaluacion_.personaRef), personaRefConsulta);
    }

    List<Predicate> predicates = new ArrayList<Predicate>();
    List<Predicate> predicatesCount = new ArrayList<Predicate>();
    // Where
    if (specsMem != null) {
      predicates.add(predicateMemoria);
      predicatesCount.add(predicateMemoriaCount);
    } else {
      if (predicatePersonaRef != null) {
        predicates
            .add(cb.or(predicateMemoria, cb.and(predicatePersonaRef, cb.isTrue(root.get(PeticionEvaluacion_.activo)))));
        predicatesCount.add(cb.or(predicateMemoriaCount,
            cb.and(predicatePersonaRefCount, cb.isTrue(rootCount.get(PeticionEvaluacion_.activo)))));
      } else {
        predicates.add(cb.or(predicateMemoria, cb.isTrue(root.get(PeticionEvaluacion_.activo))));
        predicatesCount.add(cb.or(predicateMemoriaCount, cb.isTrue(rootCount.get(PeticionEvaluacion_.activo))));
      }
    }

    cq.where(predicates.toArray(new Predicate[] {}));
    countQuery.where(predicatesCount.toArray(new Predicate[] {}));

    List<Order> orders = QueryUtils.toOrders(pageable.getSort(), root, cb);
    cq.orderBy(orders);

    Long count = entityManager.createQuery(countQuery).getSingleResult();
    TypedQuery<PeticionEvaluacion> typedQuery = entityManager.createQuery(cq);
    if (pageable != null && pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }

    List<PeticionEvaluacion> result = typedQuery.getResultList();
    Page<PeticionEvaluacion> returnValue = new PageImpl<PeticionEvaluacion>(result, pageable, count);
    return returnValue;
  }

  /**
   * Obtiene las peticiones de evaluación filtradas por memoria
   * 
   * @param root
   * @param cb
   * @param cq
   * @param specsMem
   * @param personaRef
   * @return
   */
  private Subquery<Long> getIdsPeticionEvaluacionMemoria(Root<PeticionEvaluacion> root, CriteriaBuilder cb,
      CriteriaQuery<PeticionEvaluacion> cq, Specification<Memoria> specsMem, String personaRef) {

    log.debug(
        "getActaConvocatoria(Root<ConvocatoriaReunion> root, CriteriaBuilder cb, CriteriaQuery<ConvocatoriaReunionDatosGenerales> cq, Long idConvocatoria) - start");

    Subquery<Long> queryGetIdPeticionEvaluacion = cq.subquery(Long.class);
    Root<Memoria> subqRoot = queryGetIdPeticionEvaluacion.from(Memoria.class);

    List<Predicate> predicates = new ArrayList<Predicate>();
    predicates.add(cb.isTrue(subqRoot.get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.activo)));
    predicates.add(cb.isTrue(subqRoot.get(Memoria_.activo)));

    if (specsMem != null) {
      if (personaRef != null) {
        predicates.add(cb.or(cb.equal(subqRoot.get(Memoria_.personaRef), personaRef),
            cb.equal(subqRoot.get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.personaRef), personaRef)));
      }
      predicates.add(specsMem.toPredicate(subqRoot, cq, cb));
    } else {
      if (personaRef != null) {
        predicates.add(cb.equal(subqRoot.get(Memoria_.personaRef), personaRef));
      }
    }

    queryGetIdPeticionEvaluacion.select(subqRoot.get(Memoria_.peticionEvaluacion).get(PeticionEvaluacion_.id))
        .where(predicates.toArray(new Predicate[] {}));
    log.debug(
        "getActaConvocatoria(Root<ConvocatoriaReunion> root, CriteriaBuilder cb, CriteriaQuery<ConvocatoriaReunionDatosGenerales> cq, Long idConvocatoria) - end");

    return queryGetIdPeticionEvaluacion;
  }

}