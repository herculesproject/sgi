package org.crue.hercules.sgi.eti.repository.custom;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.eti.dto.EvaluacionWithNumComentario;
import org.crue.hercules.sgi.eti.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomEvaluacionRepositoryImpl implements CustomEvaluacionRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Obtener todas las entidades {@link EvaluacionWithNumComentario} paginadas
   * asociadas a una memoria y anteriores a la evaluación recibida.
   *
   * @param idMemoria    id de la memoria.
   * @param idEvaluacion id de la evaluación.
   * @param pageable     la información de la paginación.
   * 
   * @return la lista de entidades {@link EvaluacionWithNumComentario} paginadas
   *         y/o filtradas.
   */
  public Page<EvaluacionWithNumComentario> findEvaluacionesAnterioresByMemoria(Long idMemoria, Long idEvaluacion,
      Pageable pageable) {

    log.debug("findEvaluacionesAnterioresByMemoria : {} - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    CriteriaQuery<EvaluacionWithNumComentario> cq = cb.createQuery(EvaluacionWithNumComentario.class);

    // Define FROM clause
    Root<Evaluacion> root = cq.from(Evaluacion.class);

    cq.multiselect(root.alias("evaluacion"), getNumComentarios(root, cb, cq, idEvaluacion).alias("numComentarios"));

    cq.where(cb.equal(root.get(Evaluacion_.memoria).get(Memoria_.id), idMemoria),
        cb.notEqual(root.get(Evaluacion_.id), idEvaluacion));

    TypedQuery<EvaluacionWithNumComentario> typedQuery = entityManager.createQuery(cq);
    if (pageable != null && pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }

    List<EvaluacionWithNumComentario> result = typedQuery.getResultList();

    Page<EvaluacionWithNumComentario> returnValue = new PageImpl<EvaluacionWithNumComentario>(result, pageable,
        result.size());

    log.debug("findEvaluacionesAnterioresByMemoria : {} - end");
    return returnValue;
  }

  private Subquery<Long> getNumComentarios(Root<Evaluacion> root, CriteriaBuilder cb,
      CriteriaQuery<EvaluacionWithNumComentario> cq, Long idEvaluacion) {

    log.debug("getNumComentarios : {} - start");

    Subquery<Long> queryNumComentarios = cq.subquery(Long.class);
    Root<Comentario> subqRoot = queryNumComentarios.from(Comentario.class);
    queryNumComentarios.select(cb.count(subqRoot.get(Comentario_.id)))
        .where(cb.equal(subqRoot.get(Comentario_.evaluacion).get(Evaluacion_.id), root.get(Evaluacion_.id)));

    log.debug("getNumComentarios : {} - end");
    return queryNumComentarios;

  }

}
