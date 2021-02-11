package org.crue.hercules.sgi.csp.repository.custom;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.crue.hercules.sgi.csp.model.ProyectoPaqueteTrabajo;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link Proyecto}.
 */
@Slf4j
@Component
public class CustomProyectoRepositoryImpl implements CustomProyectoRepository {

  /**
   * The entity manager.
   */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Obtiene el {@link ModeloEjecucion} asignada a la {@link Proyecto}.
   *
   * @param id Id de la {@link Proyecto}.
   * @return {@link ModeloEjecucion} asignado
   */
  public Optional<ModeloEjecucion> getModeloEjecucion(Long id) {
    log.debug("getModeloEjecucion(Long id) - start");

    Optional<ModeloEjecucion> returnValue = Optional.empty();

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<ModeloEjecucion> cq = cb.createQuery(ModeloEjecucion.class);
    Root<Proyecto> root = cq.from(Proyecto.class);

    Predicate finalPredicate = cb.equal(root.get(Proyecto_.id), id);
    cq.select(root.get(Proyecto_.modeloEjecucion)).where(finalPredicate);

    returnValue = entityManager.createQuery(cq).getResultList().stream().findFirst();

    log.debug("getModeloEjecucion(Long id) - end");
    return returnValue;
  }

  /**
   * Indica si en el {@link Proyecto} se permiten {@link ProyectoPaqueteTrabajo}.
   *
   * @param id Id de la {@link Proyecto}.
   * @return true si se permiten {@link ProyectoPaqueteTrabajo}, false si no se
   *         permiten {@link ProyectoPaqueteTrabajo}
   */
  public Optional<Boolean> getPaquetesTrabajo(Long id) {
    log.debug("getpaquetesTrabajo(Long id) - start");
    Optional<Boolean> returnValue = Optional.empty();

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Boolean> cq = cb.createQuery(Boolean.class);
    Root<Proyecto> root = cq.from(Proyecto.class);

    Predicate finalPredicate = cb.equal(root.get(Proyecto_.id), id);
    cq.select(root.get(Proyecto_.paquetesTrabajo)).where(finalPredicate);

    try {
      returnValue = entityManager.createQuery(cq).getResultList().stream().findFirst();
    } catch (NullPointerException e) {
    }

    log.debug("getpaquetesTrabajo(Long id) - stop");
    return returnValue;
  }
}
