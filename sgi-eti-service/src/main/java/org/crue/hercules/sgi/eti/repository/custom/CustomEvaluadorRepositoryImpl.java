package org.crue.hercules.sgi.eti.repository.custom;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.Comite_;
import org.crue.hercules.sgi.eti.model.EquipoTrabajo;
import org.crue.hercules.sgi.eti.model.EquipoTrabajo_;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.Evaluador_;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Memoria_;
import org.crue.hercules.sgi.eti.model.Tarea;
import org.crue.hercules.sgi.eti.model.Tarea_;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Custom repository para {@link Evaluador}.
 */
@Slf4j
@Component
public class CustomEvaluadorRepositoryImpl implements CustomEvaluadorRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Devuelve los evaluadores activos del comité indicado que no entre en
   * conflicto de intereses con ningún miembro del equipo investigador de la
   * memoria.
   * 
   * @param idComite  Identificador del {@link Comite}
   * @param idMemoria Identificador de la {@link Memoria}
   * @param pageable  la información de paginación.
   * @return lista de evaluadores sin conflictos de intereses
   */
  @Override
  public Page<Evaluador> findAllByComiteSinconflictoInteresesMemoria(Long idComite, Long idMemoria, Pageable pageable) {
    log.debug("findAllByComiteSinconflictoInteresesMemoria(Long idComite, Long idMemoria, Pageable pageable) - start");
    final List<Predicate> predicates = new ArrayList<>();

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Evaluador> cq = cb.createQuery(Evaluador.class);

    // Define FROM clause
    Root<Evaluador> root = cq.from(Evaluador.class);

    // Evaluadores activos
    predicates.add(cb.equal(root.get(Evaluador_.activo), Boolean.TRUE));

    // Evaluadores del comite
    predicates.add(cb.equal(root.get(Evaluador_.comite).get(Comite_.id), idComite));

    // Evaluadores que no forman parte del equipo de trabajo de la memoria
    Subquery<String> sqEvaluadoresConflictoIntereses = cq.subquery(String.class);
    Root<Tarea> tareaRoot = sqEvaluadoresConflictoIntereses.from(Tarea.class);
    Join<Tarea, EquipoTrabajo> joinTareaEquipoTrabajo = tareaRoot.join(Tarea_.equipoTrabajo);
    sqEvaluadoresConflictoIntereses.select(joinTareaEquipoTrabajo.get(EquipoTrabajo_.personaRef));
    sqEvaluadoresConflictoIntereses.where(cb.equal(tareaRoot.get(Tarea_.memoria).get(Memoria_.id), idMemoria));

    predicates.add(cb.not(root.get(Evaluador_.personaRef).in(sqEvaluadoresConflictoIntereses)));

    // Join all restrictions
    cq.where(cb.and(predicates.toArray(new Predicate[] {})));

    // Execute query

    List<Order> orders = QueryUtils.toOrders(pageable.getSort(), root, cb);
    cq.orderBy(orders);

    TypedQuery<Evaluador> typedQuery = entityManager.createQuery(cq);
    if (pageable != null && pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }

    List<Evaluador> result = typedQuery.getResultList();
    Page<Evaluador> returnValue = new PageImpl<Evaluador>(result, pageable, result.size());

    log.debug("findAllByComiteSinconflictoInteresesMemoria(Long idComite, Long idMemoria, Pageable pageable) - end");

    return returnValue;
  }

}