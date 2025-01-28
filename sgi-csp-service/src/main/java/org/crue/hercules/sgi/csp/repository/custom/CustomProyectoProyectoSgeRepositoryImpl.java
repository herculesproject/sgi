package org.crue.hercules.sgi.csp.repository.custom;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;

import org.crue.hercules.sgi.csp.dto.ProyectoSeguimientoEjecucionEconomica;
import org.crue.hercules.sgi.csp.dto.RelacionEjecucionEconomica;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaTitulo;
import org.crue.hercules.sgi.csp.model.Convocatoria_;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.crue.hercules.sgi.csp.repository.predicate.ProyectoProyectoSgePredicateResolver;
import org.crue.hercules.sgi.csp.util.CriteriaQueryUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Custom repository para {@link ProyectoProyectoSge}.
 */
@Slf4j
@Component
public class CustomProyectoProyectoSgeRepositoryImpl implements CustomProyectoProyectoSgeRepository {

  /**
   * The entity manager.
   */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Obtiene datos economicos de los {@link ProyectoProyectoSge}
   * 
   * @param specification condiciones que deben cumplir.
   * @param pageable      paginación.
   * @return el listado de entidades {@link RelacionEjecucionEconomica} paginadas
   *         y filtradas.
   */
  @Override
  public Page<RelacionEjecucionEconomica> findRelacionesEjecucionEconomica(
      Specification<ProyectoProyectoSge> specification,
      Pageable pageable) {
    log.debug(
        "findRelacionesEjecucionEconomica(Specification<ProyectoProyectoSge> specification, Pageable pageable) - start");

    // Find query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<RelacionEjecucionEconomica> cq = cb.createQuery(RelacionEjecucionEconomica.class);
    Root<ProyectoProyectoSge> root = cq.from(ProyectoProyectoSge.class);
    List<Predicate> listPredicates = new ArrayList<>();

    // Count query
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<ProyectoProyectoSge> rootCount = countQuery.from(ProyectoProyectoSge.class);
    List<Predicate> listPredicatesCount = new ArrayList<>();

    // Select
    String[] selectionNames = new String[] {
        ProyectoProyectoSgePredicateResolver.Property.NOMBRE_PROYECTO.getCode(),
        ProyectoProyectoSgePredicateResolver.Property.FECHA_INICIO_PROYECTO.getCode(),
        ProyectoProyectoSgePredicateResolver.Property.FECHA_FIN_PROYECTO.getCode(),
        ProyectoProyectoSgePredicateResolver.Property.CODIGO_EXTERNO.getCode(),
        ProyectoProyectoSgePredicateResolver.Property.CODIGO_INTERNO.getCode()
    };

    cq.multiselect(root.get(ProyectoProyectoSge_.proyecto).get(Proyecto_.id).alias(Proyecto_.ID),
        root.get(ProyectoProyectoSge_.proyecto).get(Proyecto_.titulo).alias(
            ProyectoProyectoSgePredicateResolver.Property.NOMBRE_PROYECTO.getCode()),
        root.get(ProyectoProyectoSge_.proyecto).get(Proyecto_.codigoExterno).alias(
            ProyectoProyectoSgePredicateResolver.Property.CODIGO_EXTERNO.getCode()),
        root.get(ProyectoProyectoSge_.proyecto).get(Proyecto_.codigoInterno).alias(
            ProyectoProyectoSgePredicateResolver.Property.CODIGO_INTERNO.getCode()),
        root.get(ProyectoProyectoSge_.proyecto).get(Proyecto_.fechaInicio).alias(
            ProyectoProyectoSgePredicateResolver.Property.FECHA_INICIO_PROYECTO.getCode()),
        root.get(ProyectoProyectoSge_.proyecto).get(Proyecto_.fechaFin).alias(
            ProyectoProyectoSgePredicateResolver.Property.FECHA_FIN_PROYECTO.getCode()),
        root.get(ProyectoProyectoSge_.proyectoSgeRef),
        cb.literal(RelacionEjecucionEconomica.TipoEntidad.PROYECTO.toString()),
        root.get(ProyectoProyectoSge_.proyecto).get(Proyecto_.fechaFinDefinitiva).alias(
            ProyectoProyectoSgePredicateResolver.Property.FECHA_FIN_DEFINITIVA_PROYECTO.getCode()));

    countQuery.select(cb.count(rootCount));

    // Where
    if (specification != null) {
      listPredicates.add(specification.toPredicate(root, cq, cb));
      listPredicatesCount.add(specification.toPredicate(rootCount, countQuery, cb));
    }

    cq.where(listPredicatesCount.toArray(new Predicate[] {}));
    countQuery.where(listPredicatesCount.toArray(new Predicate[] {}));

    // Order
    cq.orderBy(CriteriaQueryUtils.toOrders(pageable.getSort(), root, cb, cq, selectionNames));

    // Número de registros totales para la paginación
    Long count = entityManager.createQuery(countQuery).getSingleResult();

    TypedQuery<RelacionEjecucionEconomica> typedQuery = entityManager.createQuery(cq);
    if (pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }

    List<RelacionEjecucionEconomica> results = typedQuery.getResultList();
    Page<RelacionEjecucionEconomica> returnValue = new PageImpl<>(results, pageable, count);

    log.debug(
        "findRelacionesEjecucionEconomica(Specification<ProyectoProyectoSge> specification, Pageable pageable) - end");
    return returnValue;
  }

  @Override
  public Page<ProyectoSeguimientoEjecucionEconomica> findProyectosSeguimientoEjecucionEconomica(
      Specification<ProyectoProyectoSge> specification, Pageable pageable) {
    log.debug(
        "findProyectosSeguimientoEjecucionEconomica(Specification<ProyectoProyectoSge> specification, Pageable pageable) - start");

    // Find query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
    Root<ProyectoProyectoSge> root = cq.from(ProyectoProyectoSge.class);
    List<Predicate> listPredicates = new ArrayList<>();

    // Count query
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<ProyectoProyectoSge> rootCount = countQuery.from(ProyectoProyectoSge.class);
    List<Predicate> listPredicatesCount = new ArrayList<>();

    // Select
    String[] selectionNames = new String[] {
        ProyectoProyectoSgePredicateResolver.Property.NOMBRE_PROYECTO.getCode(),
        ProyectoProyectoSgePredicateResolver.Property.FECHA_INICIO_PROYECTO.getCode(),
        ProyectoProyectoSgePredicateResolver.Property.FECHA_FIN_PROYECTO.getCode(),
        ProyectoProyectoSgePredicateResolver.Property.FECHA_FIN_DEFINITIVA_PROYECTO.getCode(),
        ProyectoProyectoSgePredicateResolver.Property.CODIGO_EXTERNO.getCode(),
        ProyectoProyectoSgePredicateResolver.Property.TITULO_CONVOCATORIA.getCode(),
        ProyectoProyectoSgePredicateResolver.Property.IMPORTE_CONCEDIDO.getCode(),
        ProyectoProyectoSgePredicateResolver.Property.IMPORTE_CONCEDIDO_COSTES_INDIRECTOS.getCode(),
    };

    Join<ProyectoProyectoSge, Proyecto> joinProyecto = root.join(ProyectoProyectoSge_.proyecto);
    Join<Proyecto, Convocatoria> joinConvocatoria = joinProyecto.join(Proyecto_.convocatoria, JoinType.LEFT);
    SetJoin<Convocatoria, ConvocatoriaTitulo> joinConvocatoriaTitulo = joinConvocatoria.join(Convocatoria_.titulo,
        JoinType.LEFT);

    cq.multiselect(
        root.get(ProyectoProyectoSge_.id).alias(ProyectoProyectoSge_.ID),
        root.get(ProyectoProyectoSge_.proyectoId).alias(ProyectoProyectoSge_.PROYECTO_ID),
        root.get(ProyectoProyectoSge_.proyectoSgeRef).alias(ProyectoProyectoSge_.PROYECTO_SGE_REF),
        joinProyecto.get(Proyecto_.titulo).alias(
            ProyectoProyectoSgePredicateResolver.Property.NOMBRE_PROYECTO.getCode()),
        joinProyecto.get(Proyecto_.codigoExterno).alias(
            ProyectoProyectoSgePredicateResolver.Property.CODIGO_EXTERNO.getCode()),
        joinProyecto.get(Proyecto_.fechaInicio).alias(
            ProyectoProyectoSgePredicateResolver.Property.FECHA_INICIO_PROYECTO.getCode()),
        joinProyecto.get(Proyecto_.fechaFin).alias(
            ProyectoProyectoSgePredicateResolver.Property.FECHA_FIN_PROYECTO.getCode()),
        joinProyecto.get(Proyecto_.fechaFinDefinitiva).alias(
            ProyectoProyectoSgePredicateResolver.Property.FECHA_FIN_DEFINITIVA_PROYECTO.getCode()),
        joinConvocatoriaTitulo.alias(ProyectoProyectoSgePredicateResolver.Property.TITULO_CONVOCATORIA.getCode()),
        joinProyecto.get(Proyecto_.importeConcedido).alias(
            ProyectoProyectoSgePredicateResolver.Property.IMPORTE_CONCEDIDO.getCode()),
        joinProyecto.get(Proyecto_.importeConcedidoCostesIndirectos).alias(
            ProyectoProyectoSgePredicateResolver.Property.IMPORTE_CONCEDIDO_COSTES_INDIRECTOS.getCode()));

    countQuery.select(cb.count(rootCount));

    // Where
    if (specification != null) {
      listPredicates.add(specification.toPredicate(root, cq, cb));
      listPredicatesCount.add(specification.toPredicate(rootCount, countQuery, cb));
    }

    cq.where(listPredicates.toArray(new Predicate[] {}));
    countQuery.where(listPredicatesCount.toArray(new Predicate[] {}));

    // Order
    cq.orderBy(CriteriaQueryUtils.toOrders(pageable.getSort(), root, cb, cq, selectionNames));

    // Número de registros totales para la paginación
    Long count = entityManager.createQuery(countQuery).getSingleResult();

    TypedQuery<Tuple> typedQuery = entityManager.createQuery(cq);
    if (pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }

    List<Tuple> results = typedQuery.getResultList();

    Map<Long, ProyectoSeguimientoEjecucionEconomica> tuplesMap = new HashMap<>();
    results.forEach(tuple -> {
      Long proyectoProyectoSgeId = tuple.get(ProyectoProyectoSge_.ID, Long.class);

      ProyectoSeguimientoEjecucionEconomica proyectoSeguimiento = tuplesMap.computeIfAbsent(proyectoProyectoSgeId,
          key -> ProyectoSeguimientoEjecucionEconomica.builder()
              .id(proyectoProyectoSgeId)
              .proyectoId(tuple.get(ProyectoProyectoSge_.PROYECTO_ID, Long.class))
              .proyectoSgeRef(tuple.get(ProyectoProyectoSge_.PROYECTO_SGE_REF, String.class))
              .nombre(tuple.get(ProyectoProyectoSgePredicateResolver.Property.NOMBRE_PROYECTO.getCode(), String.class))
              .codigoExterno(
                  tuple.get(ProyectoProyectoSgePredicateResolver.Property.CODIGO_EXTERNO.getCode(), String.class))
              .fechaInicio(tuple.get(ProyectoProyectoSgePredicateResolver.Property.FECHA_INICIO_PROYECTO.getCode(),
                  Instant.class))
              .fechaFin(
                  tuple.get(ProyectoProyectoSgePredicateResolver.Property.FECHA_FIN_PROYECTO.getCode(), Instant.class))
              .fechaFinDefinitiva(tuple.get(
                  ProyectoProyectoSgePredicateResolver.Property.FECHA_FIN_DEFINITIVA_PROYECTO.getCode(), Instant.class))
              .importeConcedido(tuple.get(ProyectoProyectoSgePredicateResolver.Property.IMPORTE_CONCEDIDO.getCode(),
                  BigDecimal.class))
              .importeConcedidoCostesIndirectos(
                  tuple.get(ProyectoProyectoSgePredicateResolver.Property.IMPORTE_CONCEDIDO_COSTES_INDIRECTOS.getCode(),
                      BigDecimal.class))
              .tituloConvocatoria(new HashSet<>())
              .build());

      // Añade el titulo de la convocatoria de la tupla actual
      ConvocatoriaTitulo tituloConvocatoria = tuple.get(
          ProyectoProyectoSgePredicateResolver.Property.TITULO_CONVOCATORIA.getCode(), ConvocatoriaTitulo.class);
      if (tituloConvocatoria != null) {
        proyectoSeguimiento.getTituloConvocatoria().add(tituloConvocatoria);
      }
    });

    Page<ProyectoSeguimientoEjecucionEconomica> returnValue = new PageImpl<>(tuplesMap.values().stream().toList(),
        pageable, count);

    log.debug(
        "findProyectosSeguimientoEjecucionEconomica(Specification<ProyectoProyectoSge> specification, Pageable pageable) - end");
    return returnValue;
  }

}
