package org.crue.hercules.sgi.prc.repository.custom;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaResumen;
import org.crue.hercules.sgi.prc.dto.PublicacionResumen;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica.CodigoCVN;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica_;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica_;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica_;
import org.crue.hercules.sgi.prc.model.ValorCampo;
import org.crue.hercules.sgi.prc.model.ValorCampo_;
import org.crue.hercules.sgi.prc.repository.predicate.ProduccionCientificaPredicateResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data JPA repository para {@link ProduccionCientifica}.
 */
@Slf4j
@Component
public class CustomProduccionCientificaRepositoryImpl implements CustomProduccionCientificaRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Elimina el estado de {@link ProduccionCientifica} cuyo id coincide con el
   * indicado.
   * 
   * @param produccionCientificaId el identificador de la
   *                               {@link ProduccionCientifica}
   * @return el número de registros eliminados
   */
  @Override
  public int updateEstadoNull(long produccionCientificaId) {
    log.debug("updateEstadoNull(produccionCientificaId) : {} - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaUpdate<ProduccionCientifica> update = cb.createCriteriaUpdate(ProduccionCientifica.class);

    // Define FROM IndiceImpacto clause
    Root<ProduccionCientifica> root = update.from(ProduccionCientifica.class);

    update.set(ProduccionCientifica_.ESTADO, null);

    // Set WHERE restrictions
    update.where(cb.equal(root.get(ProduccionCientifica_.id), produccionCientificaId));

    // Execute query
    int result = entityManager.createQuery(update).executeUpdate();

    log.debug("updateEstadoNull(produccionCientificaId) : {} - start");

    return result;
  }

  /**
   * Devuelve el identificador CVN y el estado (Validado O Rechazado) de aquellos
   * items almacenados en producción científica que han cambiado al estado
   * Validado o Rechazado en una fecha igual o superior a la fecha de estado
   * pasada por parámetro
   * 
   * @param specification filtro fechaEstado
   * @return lista de {@link ProduccionCientificaResumen}
   */
  @Override
  public List<ProduccionCientificaResumen> findByEstadoValidadoOrRechazadoByFechaModificacion(
      Specification<ProduccionCientifica> specification) {
    log.debug("findByEstadoValidadoOrRechazadoByFechaModificacion(specification) : {} - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    CriteriaQuery<ProduccionCientificaResumen> cq = cb.createQuery(ProduccionCientificaResumen.class);

    Root<ProduccionCientifica> root = cq.from(ProduccionCientifica.class);

    cq.multiselect(root.get(ProduccionCientifica_.id).alias("id"),
        root.get(ProduccionCientifica_.produccionCientificaRef).alias(
            ProduccionCientifica_.PRODUCCION_CIENTIFICA_REF),
        root.get(ProduccionCientifica_.estado).get(EstadoProduccionCientifica_.estado)
            .alias(ProduccionCientifica_.ESTADO),
        root.get(ProduccionCientifica_.epigrafeCVN).alias(ProduccionCientifica_.EPIGRAFE_CV_N))
        .distinct(true)
        .where(specification.toPredicate(root, cq, cb));

    log.debug("findByEstadoValidadoOrRechazadoByFechaModificacion(specification) : {} - end");
    return entityManager.createQuery(cq).getResultList();
  }

  /**
   * Recupera todas las {@link PublicacionResumen} con su título, fecha y tipo de
   * producción
   * 
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return Listado paginado de {@link PublicacionResumen}
   */
  public Page<PublicacionResumen> findAllPublicaciones(String query, Pageable pageable) {
    log.debug("findAllPublicaciones(String query, Pageable pageable) - start");

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<PublicacionResumen> cq = cb.createQuery(PublicacionResumen.class);
    Root<ProduccionCientifica> root = cq.from(ProduccionCientifica.class);

    Join<ProduccionCientifica, EstadoProduccionCientifica> joinEstado = root.join(
        ProduccionCientifica_.estado, JoinType.INNER);

    Join<ProduccionCientifica, CampoProduccionCientifica> joinCamposTituloPublicacion = root.join(
        ProduccionCientifica_.campos, JoinType.LEFT);
    Join<CampoProduccionCientifica, ValorCampo> joinValoresTituloPublicacion = joinCamposTituloPublicacion.join(
        CampoProduccionCientifica_.valoresCampos, JoinType.LEFT);

    Join<ProduccionCientifica, CampoProduccionCientifica> joinCamposTipoProduccion = root.join(
        ProduccionCientifica_.campos, JoinType.LEFT);
    Join<CampoProduccionCientifica, ValorCampo> joinValoresTipoProduccion = joinCamposTipoProduccion.join(
        CampoProduccionCientifica_.valoresCampos, JoinType.LEFT);

    Join<ProduccionCientifica, CampoProduccionCientifica> joinCamposFechaPublicacion = root.join(
        ProduccionCientifica_.campos, JoinType.LEFT);
    Join<CampoProduccionCientifica, ValorCampo> joinValoresFechaPublicacion = joinCamposFechaPublicacion.join(
        CampoProduccionCientifica_.valoresCampos, JoinType.LEFT);

    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<ProduccionCientifica> rootCount = countQuery.from(ProduccionCientifica.class);

    countQuery.select(cb.count(rootCount));

    rootCount.join(ProduccionCientifica_.estado, JoinType.INNER);

    Join<ProduccionCientifica, CampoProduccionCientifica> joinCamposTituloPublicacionCount = rootCount.join(
        ProduccionCientifica_.campos, JoinType.LEFT);
    joinCamposTituloPublicacionCount.join(CampoProduccionCientifica_.valoresCampos, JoinType.LEFT);

    Join<ProduccionCientifica, CampoProduccionCientifica> joinCamposTipoProduccionCount = rootCount.join(
        ProduccionCientifica_.campos, JoinType.LEFT);
    joinCamposTipoProduccionCount.join(CampoProduccionCientifica_.valoresCampos, JoinType.LEFT);

    Join<ProduccionCientifica, CampoProduccionCientifica> joinCamposFechaPublicacionCount = rootCount.join(
        ProduccionCientifica_.campos, JoinType.LEFT);
    joinCamposFechaPublicacionCount.join(CampoProduccionCientifica_.valoresCampos, JoinType.LEFT);

    List<Predicate> listPredicates = new ArrayList<>();
    List<Predicate> listPredicatesCount = new ArrayList<>();

    listPredicates.add(cb.and(
        cb.equal(joinCamposTituloPublicacion.get(CampoProduccionCientifica_.codigoCVN), CodigoCVN.E060_010_010_030)));
    listPredicates.add(cb.and(
        cb.equal(joinCamposTipoProduccion.get(CampoProduccionCientifica_.codigoCVN), CodigoCVN.E060_010_010_010)));
    listPredicates.add(cb.and(
        cb.equal(joinCamposFechaPublicacion.get(CampoProduccionCientifica_.codigoCVN), CodigoCVN.E060_010_010_140)));

    listPredicatesCount.add(cb.and(
        cb.equal(joinCamposTituloPublicacionCount.get(CampoProduccionCientifica_.codigoCVN),
            CodigoCVN.E060_010_010_030)));
    listPredicatesCount.add(cb.and(
        cb.equal(joinCamposTipoProduccionCount.get(CampoProduccionCientifica_.codigoCVN), CodigoCVN.E060_010_010_010)));
    listPredicatesCount.add(cb.and(
        cb.equal(joinCamposFechaPublicacionCount.get(CampoProduccionCientifica_.codigoCVN),
            CodigoCVN.E060_010_010_140)));

    if (StringUtils.hasText(query)) {
      Specification<ProduccionCientifica> spec = SgiRSQLJPASupport.toSpecification(query,
          ProduccionCientificaPredicateResolver.getInstance());
      listPredicates.add(spec.toPredicate(root, cq, cb));
      listPredicatesCount.add(spec.toPredicate(rootCount, countQuery, cb));
    }

    Path<Long> pathProduccionCientificaId = root.get(ProduccionCientifica_.id);
    cq.where(listPredicates.toArray(new Predicate[] {}));

    cq.multiselect(pathProduccionCientificaId.alias("id"),
        root.get(ProduccionCientifica_.produccionCientificaRef).alias("produccionCientificaRef"),
        joinEstado.get(EstadoProduccionCientifica_.estado).alias("estado"),
        root.get(ProduccionCientifica_.epigrafeCVN).alias("epigrafe"),
        joinValoresTituloPublicacion.get(ValorCampo_.valor)
            .alias(ProduccionCientificaPredicateResolver.Property.TITULO_PUBLICACION.getCode()),
        joinValoresTipoProduccion.get(ValorCampo_.valor)
            .alias(ProduccionCientificaPredicateResolver.Property.TIPO_PRODUCCION.getCode()),
        joinValoresFechaPublicacion.get(ValorCampo_.valor)
            .alias(ProduccionCientificaPredicateResolver.Property.FECHA_PUBLICACION.getCode()));

    String[] selectionNames = new String[] {
        ProduccionCientificaPredicateResolver.Property.TITULO_PUBLICACION.getCode(),
        ProduccionCientificaPredicateResolver.Property.TIPO_PRODUCCION.getCode(),
        ProduccionCientificaPredicateResolver.Property.FECHA_PUBLICACION.getCode() };

    Optional<Integer> selectionIndex = getIndexOrderBySelectionName(selectionNames, pageable.getSort(), cq);
    if (selectionIndex.isPresent()) {
      cq.orderBy(toOrdersByPosition(selectionIndex.get() + 1, pageable.getSort(), cb));
    } else {
      List<Order> orders = QueryUtils.toOrders(pageable.getSort(), root, cb);
      cq.orderBy(orders);
    }

    countQuery.where(listPredicatesCount.toArray(new Predicate[] {}));
    Long count = entityManager.createQuery(countQuery).getSingleResult();

    TypedQuery<PublicacionResumen> typedQuery = entityManager.createQuery(cq);
    if (pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }

    List<PublicacionResumen> result = typedQuery.getResultList();
    Page<PublicacionResumen> returnValue = new PageImpl<>(result, pageable, count);

    log.debug("findAllPublicaciones(String query, Pageable pageable) - end");

    return returnValue;
  }

  private List<Order> toOrdersByPosition(int position, Sort sort, CriteriaBuilder cb) {
    List<Order> orders = new ArrayList<>();
    if (sort.isUnsorted()) {
      return orders;
    }

    sort.forEach(order -> {
      Expression<Integer> orderByLiteral = cb.literal(position);
      Order orderByPosition = cb.desc(orderByLiteral);
      if (order.isAscending()) {
        orderByPosition = cb.asc(orderByLiteral);
      }
      orders.add(orderByPosition);
    });

    return orders;
  }

  private Optional<Integer> getIndexOrderBySelectionName(String[] selectionNames, Sort sort,
      CriteriaQuery<PublicacionResumen> cq) {
    return Stream.of(selectionNames)
        .filter(
            selectionName -> !sort.filter(order -> order.getProperty().equals(selectionName)).isEmpty())
        .findFirst()
        .map(
            selectionName -> IntStream.range(0, cq.getSelection().getCompoundSelectionItems().size())
                .filter(
                    index -> cq.getSelection().getCompoundSelectionItems().get(index).getAlias().equals(selectionName))
                .findFirst()
                .getAsInt());
  }
}