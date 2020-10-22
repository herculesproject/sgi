package org.crue.hercules.sgi.eti.repository.custom;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.eti.model.FormularioMemoria_;
import org.crue.hercules.sgi.eti.model.InformeFormulario;
import org.crue.hercules.sgi.eti.model.InformeFormulario_;
import org.crue.hercules.sgi.eti.model.Memoria_;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;

public class CustomInformeFormularioRepositoryImpl implements CustomInformeFormularioRepository {

  /**
   * The entity manager.
   */
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public Page<InformeFormulario> findByMemoria(Long idMemoria, Pageable pageable) {
    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<InformeFormulario> cq = cb.createQuery(InformeFormulario.class);

    // Define FROM clause
    Root<InformeFormulario> root = cq.from(InformeFormulario.class);

    List<Predicate> listPredicates = new ArrayList<>();

    Predicate predicate = cb.equal(
        root.get(InformeFormulario_.formularioMemoria).get(FormularioMemoria_.memoria).get(Memoria_.id), idMemoria);

    listPredicates.add(predicate);

    TypedQuery<InformeFormulario> typedQuery = entityManager.createQuery(cq);

    if (pageable != null && pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }

    List<InformeFormulario> result = typedQuery.getResultList();
    Page<InformeFormulario> returnValue = new PageImpl<InformeFormulario>(result, pageable, result.size());

    return returnValue;

  }

}
