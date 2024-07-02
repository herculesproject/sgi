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

import org.crue.hercules.sgi.eti.dto.BloqueOutput;
import org.crue.hercules.sgi.eti.model.Bloque;
import org.crue.hercules.sgi.eti.model.BloqueNombre;
import org.crue.hercules.sgi.eti.model.BloqueNombre_;
import org.crue.hercules.sgi.eti.model.Bloque_;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Formulario_;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Custom repository para {@link Bloque}.
 */
@Slf4j
@Component
public class CustomBloqueRepositoryImpl implements CustomBloqueRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Devuelve una lista paginada de {@link Bloque} para un determinado formulario
   * e idioma
   * 
   * @param idFormulario Id de {@link Formulario}.
   * @param lang         El {@link Language} sobre el que buscar.
   * @param pageable     datos de paginación
   * @return lista de tareas con la informacion de si son eliminables.
   */
  @Override
  public Page<BloqueOutput> findByFormularioIdAndLanguage(Long idFormulario, Language lang, Pageable pageable) {
    log.debug("findAllByPeticionEvaluacionId : {} - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    // Count query
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<BloqueNombre> rootCount = countQuery.from(BloqueNombre.class);

    CriteriaQuery<BloqueOutput> cq = cb.createQuery(BloqueOutput.class);

    // Define FROM clause
    Root<BloqueNombre> root = cq.from(BloqueNombre.class);

    Join<BloqueNombre, Bloque> joinBloque = root.join(BloqueNombre_.bloque, JoinType.LEFT);
    Join<BloqueNombre, Bloque> joinCountBloque = rootCount.join(BloqueNombre_.bloque, JoinType.LEFT);
    countQuery.select(cb.count(joinCountBloque));

    cq.multiselect(joinBloque.get(Bloque_.id), joinBloque.get(Bloque_.formulario), root.get(BloqueNombre_.nombre),
        joinBloque.get(Bloque_.orden), root.get(BloqueNombre_.lang));

    // Where
    cq.where(cb.equal(root.get(BloqueNombre_.lang), lang),
        cb.equal(joinBloque.get(Bloque_.formulario).get(Formulario_.id), idFormulario));

    countQuery.where(cb.equal(rootCount.get(BloqueNombre_.lang), lang),
        cb.equal(joinCountBloque.get(Bloque_.formulario).get(Formulario_.id), idFormulario));

    Long count = entityManager.createQuery(countQuery).getSingleResult();

    TypedQuery<BloqueOutput> typedQuery = entityManager.createQuery(cq);
    List<BloqueOutput> result = typedQuery.getResultList();

    if (!ObjectUtils.isEmpty(pageable) && pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    } else {
      pageable = PageRequest.of(0, result.size());
    }

    Page<BloqueOutput> returnValue = new PageImpl<>(result, pageable, count);

    log.debug("findAllByPeticionEvaluacionId : {} - end");
    return returnValue;
  }

  /**
   * /**
   * Devuelve el {@link Bloque} general para un determinado idioma
   * 
   * @param lang El {@link Language} sobre el que obtener el bloque.
   * @return el bloque general
   */
  @Override
  public BloqueOutput getBloqueComentarioGenerales(Language lang) {
    log.debug("getBloqueComentarioGenerales : {} - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    CriteriaQuery<BloqueOutput> cq = cb.createQuery(BloqueOutput.class);

    // Define FROM clause
    Root<BloqueNombre> root = cq.from(BloqueNombre.class);

    Join<BloqueNombre, Bloque> joinBloque = root.join(BloqueNombre_.bloque, JoinType.LEFT);

    cq.multiselect(joinBloque.get(Bloque_.id), root.get(BloqueNombre_.nombre),
        joinBloque.get(Bloque_.orden), root.get(BloqueNombre_.lang));

    // Where
    cq.where(cb.equal(root.get(BloqueNombre_.lang), lang),
        cb.isNull(joinBloque.get(Bloque_.formulario).get(Formulario_.id)),
        cb.equal(joinBloque.get(Bloque_.ORDEN), "0"));

    TypedQuery<BloqueOutput> typedQuery = entityManager.createQuery(cq);

    BloqueOutput result = typedQuery.getSingleResult();

    BloqueOutput returnValue = result;

    log.debug("getBloqueComentarioGenerales : {} - end");
    return returnValue;
  }

  @Override
  public BloqueOutput findByBloqueIdAndLanguage(Long idBloque, Language lang) {
    log.debug("getBloqueComentarioGenerales : {} - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    CriteriaQuery<BloqueOutput> cq = cb.createQuery(BloqueOutput.class);

    // Define FROM clause
    Root<BloqueNombre> root = cq.from(BloqueNombre.class);

    Join<BloqueNombre, Bloque> joinBloque = root.join(BloqueNombre_.bloque, JoinType.LEFT);

    cq.multiselect(joinBloque.get(Bloque_.id), root.get(BloqueNombre_.nombre),
        joinBloque.get(Bloque_.orden), root.get(BloqueNombre_.lang));

    // Where
    cq.where(cb.equal(root.get(BloqueNombre_.lang), lang),
        cb.equal(joinBloque.get(Bloque_.id), idBloque));

    TypedQuery<BloqueOutput> typedQuery = entityManager.createQuery(cq);

    BloqueOutput result = typedQuery.getSingleResult();

    BloqueOutput returnValue = result;

    log.debug("getBloqueComentarioGenerales : {} - end");
    return returnValue;
  }
}