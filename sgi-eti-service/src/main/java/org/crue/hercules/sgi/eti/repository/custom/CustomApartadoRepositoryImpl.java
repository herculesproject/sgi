package org.crue.hercules.sgi.eti.repository.custom;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.eti.dto.ApartadoOutput;
import org.crue.hercules.sgi.eti.model.Apartado;
import org.crue.hercules.sgi.eti.model.ApartadoNombre;
import org.crue.hercules.sgi.eti.model.ApartadoNombre_;
import org.crue.hercules.sgi.eti.model.Apartado_;
import org.crue.hercules.sgi.eti.model.Bloque;
import org.crue.hercules.sgi.eti.model.Bloque_;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Custom repository para {@link Apartado}.
 */
@Slf4j
@Component
public class CustomApartadoRepositoryImpl implements CustomApartadoRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Devuelve una lista paginada de {@link Apartado} sin apartados padres para un
   * determinado
   * {@link Bloque} e idioma
   * 
   * @param idBloque Id de {@link Bloque}.
   * @param lang     El {@link Language} sobre el que buscar.
   * @param pageable datos de paginación
   * @return lista de {@link Apartado} paginados
   */
  @Override
  public Page<ApartadoOutput> findByBloqueIdAndPadreIsNullAndLanguage(Long idBloque, Language lang, Pageable pageable) {
    log.debug("findByBloqueIdAndPadreIsNullAndLanguage : {} - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    // Count query
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<ApartadoNombre> rootCount = countQuery.from(ApartadoNombre.class);

    CriteriaQuery<ApartadoOutput> cq = cb.createQuery(ApartadoOutput.class);

    // Define FROM clause
    Root<ApartadoNombre> root = cq.from(ApartadoNombre.class);

    Join<ApartadoNombre, Apartado> joinApartado = root.join(ApartadoNombre_.apartado, JoinType.LEFT);
    Join<ApartadoNombre, Apartado> joinCountApartado = rootCount.join(ApartadoNombre_.apartado, JoinType.LEFT);
    countQuery.select(cb.count(joinCountApartado));

    cq.multiselect(joinApartado.get(Apartado_.id), root.get(ApartadoNombre_.nombre),
        joinApartado.get(Apartado_.bloque), joinApartado.get(Apartado_.orden), root.get(ApartadoNombre_.esquema),
        root.get(ApartadoNombre_.lang));

    // Where
    cq.where(cb.equal(root.get(ApartadoNombre_.lang), lang),
        cb.equal(joinApartado.get(Apartado_.bloque).get(Bloque_.id), idBloque),
        cb.isNull(joinApartado.get(Apartado_.padre).get(Apartado_.id)));

    countQuery.where(cb.equal(rootCount.get(ApartadoNombre_.lang), lang),
        cb.equal(joinCountApartado.get(Apartado_.bloque).get(Bloque_.id), idBloque),
        cb.isNull(joinCountApartado.get(Apartado_.padre).get(Apartado_.id)));

    Long count = entityManager.createQuery(countQuery).getSingleResult();

    TypedQuery<ApartadoOutput> typedQuery = entityManager.createQuery(cq);
    if (pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }

    List<ApartadoOutput> result = typedQuery.getResultList();

    Page<ApartadoOutput> returnValue = new PageImpl<>(result, pageable, count);

    log.debug("findByBloqueIdAndPadreIsNullAndLanguage : {} - end");
    return returnValue;
  }

  @Override
  public Page<ApartadoOutput> findByPadreIdAndLanguage(Long idPadre, Language lang, Pageable pageable) {
    log.debug("findByBloqueIdAndPadreIsNullAndLanguage : {} - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    // Count query
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<ApartadoNombre> rootCount = countQuery.from(ApartadoNombre.class);

    CriteriaQuery<ApartadoOutput> cq = cb.createQuery(ApartadoOutput.class);

    // Define FROM clause
    Root<ApartadoNombre> root = cq.from(ApartadoNombre.class);

    Join<ApartadoNombre, Apartado> joinApartado = root.join(ApartadoNombre_.apartado, JoinType.LEFT);
    Join<ApartadoNombre, Apartado> joinCountApartado = rootCount.join(ApartadoNombre_.apartado, JoinType.LEFT);
    countQuery.select(cb.count(joinCountApartado));

    cq.multiselect(joinApartado.get(Apartado_.id), root.get(ApartadoNombre_.nombre),
        joinApartado.get(Apartado_.bloque), joinApartado.get(Apartado_.padre).get(Apartado_.id),
        joinApartado.get(Apartado_.padre).get(Apartado_.orden), joinApartado.get(Apartado_.orden),
        root.get(ApartadoNombre_.esquema), root.get(ApartadoNombre_.lang));
    // Where
    cq.where(cb.equal(root.get(ApartadoNombre_.lang), lang),
        cb.equal(joinApartado.get(Apartado_.padre).get(Apartado_.id), idPadre));

    countQuery.where(cb.equal(rootCount.get(ApartadoNombre_.lang), lang),
        cb.equal(joinCountApartado.get(Apartado_.padre).get(Apartado_.id), idPadre));

    Long count = entityManager.createQuery(countQuery).getSingleResult();

    TypedQuery<ApartadoOutput> typedQuery = entityManager.createQuery(cq);
    if (pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }

    List<ApartadoOutput> result = typedQuery.getResultList();

    Page<ApartadoOutput> returnValue = new PageImpl<>(result, pageable, count);

    log.debug("findByBloqueIdAndPadreIsNullAndLanguage : {} - end");
    return returnValue;
  }

  /**
   * Devuelve un objeto {@link Apartado} para una determinado
   * {@link Apartado} e idioma
   * 
   * 
   * @param idApartado Id de {@link Apartado}.
   * @param lang       El {@link Language} sobre el que buscar.
   * @return lista de {@link Apartado}
   */
  @Override
  public ApartadoOutput findByApartadoIdAndLanguage(Long idApartado, Language lang) {
    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    CriteriaQuery<ApartadoOutput> cq = cb.createQuery(ApartadoOutput.class);

    // Define FROM clause
    Root<ApartadoNombre> root = cq.from(ApartadoNombre.class);

    Join<ApartadoNombre, Apartado> joinApartado = root.join(ApartadoNombre_.apartado, JoinType.LEFT);

    cq.multiselect(joinApartado.get(Apartado_.id), root.get(ApartadoNombre_.nombre),
        joinApartado.get(Apartado_.bloque), joinApartado.get(Apartado_.orden),
        root.get(ApartadoNombre_.esquema), root.get(ApartadoNombre_.lang));
    // Where
    cq.where(
        cb.equal(joinApartado.get(Apartado_.id), idApartado),
        cb.equal(root.get(ApartadoNombre_.lang), lang),
        cb.isNull(joinApartado.get(Apartado_.padre)));

    TypedQuery<ApartadoOutput> typedQuery = entityManager.createQuery(cq);

    ApartadoOutput result = typedQuery.getSingleResult();

    log.debug("findByApartadoId : {} - end");
    return result;
  }

  /**
   * Devuelve un objeto {@link Apartado} para una determinado
   * {@link Apartado} e idioma
   * 
   * 
   * @param idApartado Id de {@link Apartado}.
   * @param idPadre    id {@link Apartado} padre
   * @param lang       El {@link Language} sobre el que buscar.
   * @return lista de {@link Apartado}
   */
  @Override
  public ApartadoOutput findByApartadoIdAndPadreIdAndLanguage(Long idApartado, Long idPadre, Language lang) {
    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    CriteriaQuery<ApartadoOutput> cq = cb.createQuery(ApartadoOutput.class);

    // Define FROM clause
    Root<ApartadoNombre> root = cq.from(ApartadoNombre.class);

    Join<ApartadoNombre, Apartado> joinApartado = root.join(ApartadoNombre_.apartado, JoinType.LEFT);

    cq.multiselect(joinApartado.get(Apartado_.id), root.get(ApartadoNombre_.nombre),
        joinApartado.get(Apartado_.bloque), joinApartado.get(Apartado_.padre).get(Apartado_.id),
        joinApartado.get(Apartado_.padre).get(Apartado_.orden), joinApartado.get(Apartado_.orden),
        root.get(ApartadoNombre_.esquema), root.get(ApartadoNombre_.lang));
    // Where
    cq.where(
        cb.equal(joinApartado.get(Apartado_.id), idApartado),
        cb.equal(root.get(ApartadoNombre_.lang), lang),
        cb.equal(joinApartado.get(Apartado_.padre).get(Apartado_.id), idPadre));

    TypedQuery<ApartadoOutput> typedQuery = entityManager.createQuery(cq);

    ApartadoOutput result = typedQuery.getSingleResult();
    return result;
  }

  @Override
  public List<ApartadoOutput> findByPadreIdAndLanguageOrderByOrdenDesc(Long idPadre, Language lang) {
    log.debug("findByPadreIdAndLanguageOrderByOrdenDesc : {} - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    CriteriaQuery<ApartadoOutput> cq = cb.createQuery(ApartadoOutput.class);

    // Define FROM clause
    Root<ApartadoNombre> root = cq.from(ApartadoNombre.class);

    Join<ApartadoNombre, Apartado> joinApartado = root.join(ApartadoNombre_.apartado, JoinType.LEFT);

    cq.multiselect(joinApartado.get(Apartado_.id), root.get(ApartadoNombre_.nombre),
        joinApartado.get(Apartado_.bloque), joinApartado.get(Apartado_.padre).get(Apartado_.id),
        joinApartado.get(Apartado_.padre).get(Apartado_.orden), joinApartado.get(Apartado_.orden),
        root.get(ApartadoNombre_.esquema), root.get(ApartadoNombre_.lang));
    // Where
    cq.where(cb.equal(root.get(ApartadoNombre_.lang), lang),
        cb.equal(joinApartado.get(Apartado_.padre).get(Apartado_.id), idPadre));

    List<Order> orders = QueryUtils.toOrders(Sort.by(Sort.Direction.DESC, Apartado_.ORDEN), joinApartado,
        cb);
    cq.orderBy(orders);

    TypedQuery<ApartadoOutput> typedQuery = entityManager.createQuery(cq);

    List<ApartadoOutput> result = typedQuery.getResultList();

    log.debug("findByPadreIdAndLanguageOrderByOrdenDesc : {} - end");
    return result;
  }
}