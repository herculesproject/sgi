package org.crue.hercules.sgi.csp.repository.custom;

import java.time.Instant;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.dto.GrupoDto;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoEspecialInvestigacion;
import org.crue.hercules.sgi.csp.model.GrupoEspecialInvestigacion_;
import org.crue.hercules.sgi.csp.model.Grupo_;
import org.crue.hercules.sgi.framework.data.jpa.domain.Activable_;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA custom repository para {@link Grupo}.
 */
@Slf4j
@Component
public class CustomGrupoRepositoryImpl implements CustomGrupoRepository {

  /**
   * The entity manager.
   */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Devuelve si grupoRef pertenece a un grupo de investigación con el campo
   * "Grupo especial de investigación" a "No" el 31 de diciembre del
   * año que se esta baremando
   *
   * @param grupoRef        grupoRef
   * @param fechaBaremacion fecha de baremación
   * @return true/false
   */
  @Override
  public Boolean isGrupoBaremable(Long grupoRef, Instant fechaBaremacion) {
    log.debug("isGrupoBaremable(grupoRef, fechaBaremacion) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<Grupo> root = cq.from(Grupo.class);

    Root<GrupoEspecialInvestigacion> rootGrupoEspecialInvestigacion = cq.from(GrupoEspecialInvestigacion.class);
    Predicate predicateIsGrupoRef = cb.equal(root.get(Grupo_.id), grupoRef);

    Predicate predicateIsBaremable = getPredicatesIsBaremable(fechaBaremacion, cb, root,
        rootGrupoEspecialInvestigacion);

    Predicate predicateFinal = cb.and(predicateIsGrupoRef, predicateIsBaremable);

    cq.select(root.get(Grupo_.id)).where(predicateFinal);

    log.debug("isGrupoBaremable(isGrupoBaremable, fechaBaremacion) - end");

    return !entityManager.createQuery(cq).getResultList().isEmpty();
  }

  /**
   * Devuelve una lista de {@link GrupoDto} pertenecientes a un determinado
   * grupo y que estén a 31 de diciembre del año de baremación
   *
   * @param fechaBaremacion fecha de baremación
   * 
   * @return Lista de {@link GrupoDto}
   */
  @Override
  public List<GrupoDto> findAllByAnio(Instant fechaBaremacion) {
    log.debug("findAllByAnio(fechaBaremacion) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<GrupoDto> cq = cb.createQuery(GrupoDto.class);

    Root<Grupo> root = cq.from(Grupo.class);

    Root<GrupoEspecialInvestigacion> rootGrupoEspecialInvestigacion = cq.from(GrupoEspecialInvestigacion.class);

    cq.multiselect(root.get(Grupo_.id),
        root.get(Grupo_.nombre),
        root.get(Grupo_.fechaInicio),
        root.get(Grupo_.fechaFin));

    Predicate predicateIsBaremable = getPredicatesIsBaremable(fechaBaremacion, cb, root,
        rootGrupoEspecialInvestigacion);

    cq.where(cb.and(predicateIsBaremable));

    TypedQuery<GrupoDto> typedQuery = entityManager.createQuery(cq);
    List<GrupoDto> result = typedQuery.getResultList();

    log.debug("findAllByAnio(fechaBaremacion) - end");

    return result;
  }

  private Predicate getPredicatesIsBaremable(Instant fechaBaremacion, CriteriaBuilder cb, Root<Grupo> root,
      Root<GrupoEspecialInvestigacion> rootGrupoEspecialInvestigacion) {
    Predicate predicateJoinGrupoEspecialInvestigacion = cb.equal(
        rootGrupoEspecialInvestigacion.get(GrupoEspecialInvestigacion_.grupoId), root.get(Grupo_.id));

    Predicate predicateGrupoIsActivo = cb.equal(root.get(Activable_.activo), Boolean.TRUE);
    Predicate predicateGrupoEspecialInvestigacionIsFalse = cb.equal(
        rootGrupoEspecialInvestigacion.get(GrupoEspecialInvestigacion_.especialInvestigacion), Boolean.FALSE);

    Predicate predicateGrupoInFechaBaremacion = cb.and(
        cb.lessThanOrEqualTo(root.get(Grupo_.fechaInicio), fechaBaremacion),
        cb.and(cb.or(cb.isNull(root.get(Grupo_.fechaFin)),
            cb.greaterThanOrEqualTo(root.get(Grupo_.fechaFin), fechaBaremacion))));

    return cb.and(
        predicateJoinGrupoEspecialInvestigacion,
        predicateGrupoIsActivo,
        predicateGrupoEspecialInvestigacionIsFalse,
        predicateGrupoInFechaBaremacion);
  }
}