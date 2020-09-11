package org.crue.hercules.sgi.eti.repository.custom;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion_;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva_;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Memoria_;
import org.crue.hercules.sgi.eti.model.Retrospectiva_;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion_;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria_;
import org.crue.hercules.sgi.eti.util.Constantes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Custom repository para {@link Memoria}.
 */
@Slf4j
@Component
public class CustomMemoriaRepositoryImpl implements CustomMemoriaRepository {

  /** The entity manager. */
  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Si la convocatoria es de tipo "Seguimiento" devuelve las memorias en estado
   * "En secretaría seguimiento anual" y "En secretaría seguimiento final" con la
   * fecha de envío es igual o menor a la fecha límite de la convocatoria de
   * reunión.
   * 
   * Si la convocatoria es de tipo "Ordinaria" o "Extraordinaria" devuelve las
   * memorias en estado "En secretaria" con la fecha de envío es igual o menor a
   * la fecha límite de la convocatoria de reunión y las que tengan una
   * retrospectiva en estado "En secretaría".
   * 
   * @param idConvocatoriaReunion Identificador del {@link ConvocatoriaReunion}
   * @param pageable              la información de paginación.
   * @return lista de memorias asignables a la convocatoria.
   */
  @Override
  public Page<Memoria> findAllMemoriasAsignablesConvocatoria(Long idConvocatoriaReunion, Pageable pageable) {
    log.debug("findAllMemoriasAsignablesConvocatoria(Long idConvocatoriaReunion, Pageable pageable) - start");
    final List<Predicate> predicates = new ArrayList<>();

    // Crete query
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Memoria> cq = cb.createQuery(Memoria.class);

    // Fecha limite convocatoria
    Subquery<LocalDate> sqFechaLimiteConvocatoria = cq.subquery(LocalDate.class);
    Root<ConvocatoriaReunion> convocatoriaFechaLimiteRoot = sqFechaLimiteConvocatoria.from(ConvocatoriaReunion.class);
    sqFechaLimiteConvocatoria.select(convocatoriaFechaLimiteRoot.get(ConvocatoriaReunion_.fechaLimite));
    sqFechaLimiteConvocatoria
        .where(cb.equal(convocatoriaFechaLimiteRoot.get(ConvocatoriaReunion_.id), idConvocatoriaReunion));

    // Comite de la convocatoria si la convocatoria es de tipo ordinaria o
    // extraordinaria
    Subquery<Long> sqComiteConvocatoriaOrdinariaExtraordinaria = cq.subquery(Long.class);
    Root<ConvocatoriaReunion> convocatoriasOrdExtraordRoot = sqComiteConvocatoriaOrdinariaExtraordinaria
        .from(ConvocatoriaReunion.class);
    Join<ConvocatoriaReunion, Comite> joinConvocatoriaReunionOrdExtraordComite = convocatoriasOrdExtraordRoot
        .join(ConvocatoriaReunion_.comite);
    Join<ConvocatoriaReunion, TipoConvocatoriaReunion> joinConvocatoriaReunionOrdExtraordTipo = convocatoriasOrdExtraordRoot
        .join(ConvocatoriaReunion_.tipoConvocatoriaReunion);

    sqComiteConvocatoriaOrdinariaExtraordinaria.select(joinConvocatoriaReunionOrdExtraordComite.get(Comite_.id));
    sqComiteConvocatoriaOrdinariaExtraordinaria
        .where(
            cb.and(cb.equal(convocatoriasOrdExtraordRoot.get(ConvocatoriaReunion_.id), idConvocatoriaReunion),
                joinConvocatoriaReunionOrdExtraordTipo.get(TipoConvocatoriaReunion_.id)
                    .in(Arrays.asList(Constantes.TIPO_CONVOCATORIA_REUNION_ORDINARIA,
                        Constantes.TIPO_CONVOCATORIA_REUNION_EXTRAORDINARIA))));

    // Comite de la convocatoria si la convocatoria es de tipo seguimiento
    Subquery<Long> sqComiteConvocatoriaSeguimiento = cq.subquery(Long.class);
    Root<ConvocatoriaReunion> convocatoriaSeguimientoRoot = sqComiteConvocatoriaSeguimiento
        .from(ConvocatoriaReunion.class);
    Join<ConvocatoriaReunion, Comite> joinConvocatoriaSeguimientoComite = convocatoriaSeguimientoRoot
        .join(ConvocatoriaReunion_.comite);
    Join<ConvocatoriaReunion, TipoConvocatoriaReunion> joinConvocatoriaSeguimientoTipo = convocatoriaSeguimientoRoot
        .join(ConvocatoriaReunion_.tipoConvocatoriaReunion);

    sqComiteConvocatoriaSeguimiento.select(joinConvocatoriaSeguimientoComite.get(Comite_.id));
    sqComiteConvocatoriaSeguimiento
        .where(cb.and(cb.equal(convocatoriaSeguimientoRoot.get(ConvocatoriaReunion_.id), idConvocatoriaReunion),
            cb.equal(joinConvocatoriaSeguimientoTipo.get(TipoConvocatoriaReunion_.id),
                Constantes.TIPO_CONVOCATORIA_REUNION_SEGUIMIENTO)));

    // Define FROM clause
    Root<Memoria> root = cq.from(Memoria.class);
    Join<Memoria, Comite> joinMemoriaComite = root.join(Memoria_.comite);
    Join<Memoria, TipoEstadoMemoria> joinMemoriaTipoEstado = root.join(Memoria_.estadoActual);

    // Memorias convocatoria ordinaria o extraordinaria
    Predicate comiteConvocatoriaReunionOrdExtraord = cb.equal(joinMemoriaComite.get(Comite_.id),
        sqComiteConvocatoriaOrdinariaExtraordinaria);
    Predicate memoriasSecretaria = cb.equal(joinMemoriaTipoEstado.get(TipoEstadoMemoria_.id),
        Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA);
    Predicate fechaEnvioMenorFechaLimite = cb.lessThanOrEqualTo(root.get(Memoria_.fechaEnvioSecretaria),
        sqFechaLimiteConvocatoria);
    Predicate retrospectivaSecretaria = cb.equal(
        root.get(Memoria_.retrospectiva).get(Retrospectiva_.estadoRetrospectiva).get(EstadoRetrospectiva_.id),
        Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA);

    Predicate memoriasConvocatoriaOrdinariaExtraordinaria = cb.and(comiteConvocatoriaReunionOrdExtraord,
        cb.or(cb.and(memoriasSecretaria, fechaEnvioMenorFechaLimite), retrospectivaSecretaria));

    // Memorias convocatoria seguimiento
    Predicate comiteConvocatoriaReunionSeguimiento = cb.equal(joinMemoriaComite.get(Comite_.id),
        sqComiteConvocatoriaSeguimiento);
    Predicate memoriasSecretariaSeguimientoAnualYFinal = joinMemoriaTipoEstado.get(TipoEstadoMemoria_.id)
        .in(Arrays.asList(Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA_SEGUIMIENTO_ANUAL,
            Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA_SEGUIMIENTO_FINAL));
    Predicate memoriasConvocatoriaSeguimiento = cb.and(comiteConvocatoriaReunionSeguimiento,
        cb.and(memoriasSecretariaSeguimientoAnualYFinal, fechaEnvioMenorFechaLimite));

    // Memorias activos
    Predicate memoriasActivas = cb.equal(root.get(Memoria_.activo), Boolean.TRUE);

    // WHERE
    predicates.add(memoriasActivas);
    predicates.add(cb.or(memoriasConvocatoriaOrdinariaExtraordinaria, memoriasConvocatoriaSeguimiento));

    // Join all restrictions
    cq.where(cb.and(predicates.toArray(new Predicate[] {})));

    // Execute query

    List<Order> orders = QueryUtils.toOrders(pageable.getSort(), root, cb);
    cq.orderBy(orders);

    TypedQuery<Memoria> typedQuery = entityManager.createQuery(cq);
    if (pageable != null && pageable.isPaged()) {
      typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
      typedQuery.setMaxResults(pageable.getPageSize());
    }

    List<Memoria> result = typedQuery.getResultList();
    Page<Memoria> returnValue = new PageImpl<Memoria>(result, pageable, result.size());

    log.debug("findAllMemoriasAsignablesConvocatoria(Long idConvocatoriaReunion, Pageable pageable) - end");

    return returnValue;
  }

}