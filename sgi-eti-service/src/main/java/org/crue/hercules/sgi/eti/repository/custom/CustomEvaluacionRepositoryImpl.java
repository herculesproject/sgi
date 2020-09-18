package org.crue.hercules.sgi.eti.repository.custom;

import lombok.extern.slf4j.Slf4j;
import org.crue.hercules.sgi.eti.dto.EvaluacionWithNumComentario;
import org.crue.hercules.sgi.eti.model.*;
import org.crue.hercules.sgi.framework.data.jpa.domain.QuerySpecification;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluacion_;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Memoria_;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.Retrospectiva_;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria_;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion_;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva_;

/**
 * Spring Data JPA repository para {@link Evaluacion}.
 */
@Component
@Slf4j
public class CustomEvaluacionRepositoryImpl implements CustomEvaluacionRepository {

  /**
   * The entity manager.
   */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Obtener todas las entidades {@link EvaluacionWithNumComentario} paginadas
   * asociadas a una memoria y anteriores a la evaluación recibida.
   *
   * @param idMemoria    id de la memoria.
   * @param idEvaluacion id de la evaluación.
   * @param pageable     la información de la paginación.
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

    cq.multiselect(root.alias("evaluacion"), getNumComentarios(root, cb, cq).alias("numComentarios"));

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

  /**
   * Obtener todas las entidades {@link Evaluacion} paginadas y/o filtradas.
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Evaluacion} paginadas y/o filtradas.
   */
  public Page<Evaluacion> findAllByMemoriaAndRetrospectivaEnEvaluacion(List<QueryCriteria> query, Pageable pageable) {

    log.debug("findAllByMemoriaAndRetrospectivaEnEvaluacion : {} - start");

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Evaluacion> cq = cb.createQuery(Evaluacion.class);

    // Define FROM clause
    Root<Evaluacion> root = cq.from(Evaluacion.class);

    List<Predicate> listPredicates = new ArrayList<Predicate>();

    listPredicates.add(cb.or(
        cb.and(cb.equal(root.get(Evaluacion_.tipoEvaluacion).get(TipoEvaluacion_.id), 1L),
            cb.in(root.get(Evaluacion_.memoria).get(Memoria_.id)).value(getIdsMemoriasRestropectivas(cb, cq, root))),
        cb.and(cb.equal(root.get(Evaluacion_.tipoEvaluacion).get(TipoEvaluacion_.id), 2L),
            cb.in(root.get(Evaluacion_.memoria).get(Memoria_.id)).value(getIdsMemoriasEstadoActual(cb, cq, root)))));

    listPredicates.add(cb.and(cb.equal(root.get(Evaluacion_.activo), Boolean.TRUE)));

    // Where
    if (query != null) {
      Specification<Evaluacion> spec = new QuerySpecification<Evaluacion>(query);
      listPredicates.add(spec.toPredicate(root, cq, cb));
    }

    // Filtros
    cq.where(listPredicates.toArray(new Predicate[] {}));

    // Ordenación
    List<Order> orders = QueryUtils.toOrders(pageable.getSort(), root, cb);
    cq.orderBy(orders);

    // Paginación
    TypedQuery<Evaluacion> typedQuery = entityManager.createQuery(cq);
    if (pageable != null && pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }

    List<Evaluacion> result = typedQuery.getResultList();
    Page<Evaluacion> returnValue = new PageImpl<Evaluacion>(result, pageable, result.size());

    log.debug("findAllByMemoriaAndRetrospectivaEnEvaluacion : {} - end");

    return returnValue;

  }

  /**
   * Devuelve una subconsulta con el listado de Memorias con estado 4 o 5 donde
   * además la versión de la memoria sea igual a la versión del listado de
   * evaluación.
   * 
   * @return Subquery<Long> Listado de Memorias con estado En Evaluacion o En
   *         secretaría revisión mínima
   */
  private Subquery<Long> getIdsMemoriasEstadoActual(CriteriaBuilder cb, CriteriaQuery<Evaluacion> cq,
      Root<Evaluacion> root) {

    log.debug("getIdsMemoriasEstadoActual : {} - start");

    Subquery<Long> queryMemoriasEstadoActual = cq.subquery(Long.class);
    Root<Memoria> subqRoot = queryMemoriasEstadoActual.from(Memoria.class);
    queryMemoriasEstadoActual.select(subqRoot.get(Memoria_.id))
        .where(cb.and(subqRoot.get(Memoria_.estadoActual).get(TipoEstadoMemoria_.id).in(Arrays.asList(4L, 5L)),
            cb.equal(root.get(Evaluacion_.version), subqRoot.get(Memoria_.version))));

    log.debug("getIdsMemoriasEstadoActual : {} - end");

    return queryMemoriasEstadoActual;
  }

  /**
   * Recupera las evaluaciones del tipo memoria en estado 'En evaluacion' (id = 4)
   * o 'En secretaria revisión minima'(id = 5), o tipo retrospectiva, memoria que
   * requiere retrospectiva y el estado de la RETROSPECTIVA es 'En evaluacion' (id
   * = 4).
   * 
   * @param personaRef Identificador del {@link Evaluacion}
   * @param query      filtro de {@link QueryCriteria}.
   * @param pageable   pageable
   * @return la lista de entidades {@link Evaluacion} paginadas y/o filtradas.
   */
  @Override
  public Page<Evaluacion> findByEvaluador(String personaRef, List<QueryCriteria> query, Pageable pageable) {
    log.debug("findByEvaluador : {} - start");

    // Create query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Evaluacion> cq = cb.createQuery(Evaluacion.class);

    // Define FROM clause
    Root<Evaluacion> rootEvaluacion = cq.from(Evaluacion.class);

    List<Predicate> listPredicates = new ArrayList<>();
    Predicate memoriaVersion = cb.equal(rootEvaluacion.get(Evaluacion_.version),
        rootEvaluacion.get(Evaluacion_.memoria).get(Memoria_.version));

    // Memoria en estado 'En evaluacion' (id = 4)
    // o 'En secretaria revisión minima'(id = 5)

    Predicate memoria = cb.and(cb.equal(rootEvaluacion.get(Evaluacion_.tipoEvaluacion).get(TipoEvaluacion_.id), 2L),
        cb.in(rootEvaluacion.get(Evaluacion_.memoria).get(Memoria_.id))
            .value(getIdsMemoriasEstadoActual(cb, cq, rootEvaluacion)),
        memoriaVersion);

    // Tipo retrospectiva, memoria Requiere retrospectiva y el estado de la
    // RETROSPECTIVA es 'En evaluacion' (id = 4)

    Subquery<Long> queryRetrospectiva = cq.subquery(Long.class);
    Root<Evaluacion> subqRoot = queryRetrospectiva.from(Evaluacion.class);

    Predicate requiereRetrospectiva = cb.isTrue(subqRoot.get(Evaluacion_.memoria).get(Memoria_.requiereRetrospectiva));
    Predicate estadoRetrospectiva = cb
        .equal(subqRoot.get(Evaluacion_.memoria).get(Memoria_.retrospectiva).get(Retrospectiva_.id), 4L);
    Predicate comite = cb.equal(subqRoot.get(Evaluacion_.memoria).get(Memoria_.comite).get(Comite_.id), 2L);
    Predicate tipoEvaluacion = cb.equal(subqRoot.get(Evaluacion_.tipoEvaluacion).get(TipoEvaluacion_.id), 1L);
    Predicate evaluador = cb.or(cb.equal(subqRoot.get(Evaluacion_.evaluador1).get(Evaluador_.personaRef), personaRef),
        cb.equal(subqRoot.get(Evaluacion_.evaluador2).get(Evaluador_.personaRef), personaRef));

    queryRetrospectiva.select(subqRoot.get(Evaluacion_.id))
        .where(cb.and(requiereRetrospectiva, estadoRetrospectiva, comite, tipoEvaluacion, evaluador, memoriaVersion));

    Predicate retrospectiva = cb.in(rootEvaluacion.get(Evaluacion_.memoria).get(Memoria_.id)).value(queryRetrospectiva);

    listPredicates.add(cb.or(memoria, retrospectiva));

    // Where
    if (query != null) {
      Specification<Evaluacion> spec = new QuerySpecification<Evaluacion>(query);
      listPredicates.add(spec.toPredicate(rootEvaluacion, cq, cb));
    }

    // Filtros
    cq.where(listPredicates.toArray(new Predicate[] {}));

    // Ordenación
    List<Order> orders = QueryUtils.toOrders(pageable.getSort(), rootEvaluacion, cb);
    cq.orderBy(orders);

    // Paginación
    TypedQuery<Evaluacion> typedQuery = entityManager.createQuery(cq);
    if (pageable != null && pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }
    List<Evaluacion> result = typedQuery.getResultList();
    Page<Evaluacion> returnValue = new PageImpl<Evaluacion>(result, pageable, result.size());

    log.debug("findByEvaluador : {} - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link Evaluacion}, en estado "En evaluación
   * seguimiento anual" (id = 11), "En evaluación seguimiento final" (id = 12) o
   * "En secretaría seguimiento final aclaraciones" (id = 13), paginadas asociadas
   * a un evaluador.
   * 
   * @param personaRef Persona Ref del {@link Evaluador}
   * @param query      filtro de {@link QueryCriteria}.
   * @param pageable   pageable
   * @return la lista de entidades {@link Evaluacion} paginadas y/o filtradas.
   */
  @Override
  public Page<Evaluacion> findEvaluacionesEnSeguimientosByEvaluador(String personaRef, List<QueryCriteria> query,
      Pageable pageable) {
    log.debug("findEvaluacionesEnSeguimientosByEvaluador : {} - start");

    // Create query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Evaluacion> cq = cb.createQuery(Evaluacion.class);

    // Define FROM clause
    Root<Evaluacion> rootEvaluacion = cq.from(Evaluacion.class);

    // Evaluaciones en seguimiento
    List<Predicate> listPredicates = getPredicateEvaluacionEnSeguimiento(rootEvaluacion, cb, personaRef);

    // Where
    if (query != null) {
      Specification<Evaluacion> spec = new QuerySpecification<Evaluacion>(query);
      listPredicates.add(spec.toPredicate(rootEvaluacion, cq, cb));
    }

    // Filtros
    cq.where(listPredicates.toArray(new Predicate[] {}));

    // Ordenación
    List<Order> orders = QueryUtils.toOrders(pageable.getSort(), rootEvaluacion, cb);
    cq.orderBy(orders);

    // Paginación
    TypedQuery<Evaluacion> typedQuery = entityManager.createQuery(cq);
    if (pageable != null && pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }
    List<Evaluacion> result = typedQuery.getResultList();
    Page<Evaluacion> returnValue = new PageImpl<Evaluacion>(result, pageable, result.size());

    log.debug("findEvaluacionesEnSeguimientosByEvaluador : {} - end");
    return returnValue;
  }

  /**
   * Crea el predicate necesario para recuperar las evaluaciones de tipo memoria
   * que se encuentran en seguimiento:
   * 
   * "En evaluación seguimiento anual" (id = 11), "En evaluación seguimiento
   * final" (id = 12) o "En secretaría seguimiento final aclaraciones" (id = 13)
   * 
   * @param rootEvaluacion
   * @param cb
   * @return lista con los predicates necesarios
   */
  private List<Predicate> getPredicateEvaluacionEnSeguimiento(Root<Evaluacion> rootEvaluacion, CriteriaBuilder cb,
      String personaRef) {

    log.debug("getPredicateEvaluacionEnSeguimiento : {} - start");
    List<Predicate> listPredicates = new ArrayList<Predicate>();

    listPredicates.add(rootEvaluacion.get(Evaluacion_.memoria).get(Memoria_.estadoActual).get(TipoEstadoMemoria_.id)
        .in(Arrays.asList(11L, 12L, 13L)));

    listPredicates.add(cb.isTrue(rootEvaluacion.get(Evaluacion_.activo)));
    listPredicates.add(cb.equal(rootEvaluacion.get(Evaluacion_.tipoEvaluacion).get(TipoEvaluacion_.id), 2L));
    listPredicates
        .add(cb.or(cb.equal(rootEvaluacion.get(Evaluacion_.evaluador1).get(Evaluador_.personaRef), personaRef),
            cb.equal(rootEvaluacion.get(Evaluacion_.evaluador2).get(Evaluador_.personaRef), personaRef)));

    Predicate memoriaVersion = cb.equal(rootEvaluacion.get(Evaluacion_.version),
        rootEvaluacion.get(Evaluacion_.memoria).get(Memoria_.version));
    listPredicates.add(memoriaVersion);

    log.debug("getPredicateEvaluacionEnSeguimiento : {} - end");

    return listPredicates;
  }

  private Subquery<Long> getNumComentarios(Root<Evaluacion> root, CriteriaBuilder cb,
      CriteriaQuery<EvaluacionWithNumComentario> cq) {

    log.debug("getNumComentarios : {} - start");

    Subquery<Long> queryNumComentarios = cq.subquery(Long.class);
    Root<Comentario> subqRoot = queryNumComentarios.from(Comentario.class);
    queryNumComentarios.select(cb.count(subqRoot.get(Comentario_.id)))
        .where(cb.equal(subqRoot.get(Comentario_.evaluacion).get(Evaluacion_.id), root.get(Evaluacion_.id)));

    log.debug("getNumComentarios : {} - end");
    return queryNumComentarios;
  }

  /**
   * Devuelve una subconsulta con el listado de Memorias con Retrospectivas en
   * estado 4 (En Evaluacion) donde la versión de la memoria sea igual a la
   * versión del listado de evaluación.
   * 
   * @return Subquery<Long> Listado de Memorias con Retrospectivas en estado En
   *         Evaluacion
   */
  private Subquery<Long> getIdsMemoriasRestropectivas(CriteriaBuilder cb, CriteriaQuery<Evaluacion> cq,
      Root<Evaluacion> root) {

    log.debug("getIdsMemoriasRestropectivas : {} - start");

    Subquery<Long> queryMemorias = cq.subquery(Long.class);
    Root<Memoria> subqRoot = queryMemorias.from(Memoria.class);
    queryMemorias.select(subqRoot.get(Memoria_.id)).where(
        cb.and(cb.in(subqRoot.get(Memoria_.retrospectiva).get(Retrospectiva_.id)).value(getRetrospectivas(cb, cq)),
            cb.equal(root.get(Evaluacion_.version), subqRoot.get(Memoria_.version))));

    log.debug("getIdsMemoriasRestropectivas : {} - end");

    return queryMemorias;
  }

  /**
   * Devuelve una subconsulta con el listado de retrospectivas en estado 4 (En
   * Evaluacion)
   * 
   * @return Subquery<Long> Listado de Retrospectivas en estado En Evaluacion
   */

  private Subquery<Long> getRetrospectivas(CriteriaBuilder cb, CriteriaQuery<Evaluacion> cq) {

    log.debug("getRetrospectivas : {} - start");

    Subquery<Long> queryRetrospectivas = cq.subquery(Long.class);
    Root<Retrospectiva> subqRoot = queryRetrospectivas.from(Retrospectiva.class);
    queryRetrospectivas.select(subqRoot.get(Retrospectiva_.id))
        .where(cb.equal(subqRoot.get(Retrospectiva_.estadoRetrospectiva).get(EstadoRetrospectiva_.id), 4L));

    log.debug("getRetrospectivas : {} - end");

    return queryRetrospectivas;

  }
}
