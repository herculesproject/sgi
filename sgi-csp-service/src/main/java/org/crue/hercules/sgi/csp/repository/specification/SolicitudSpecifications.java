package org.crue.hercules.sgi.csp.repository.specification;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante_;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadFinanciadora_;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase_;
import org.crue.hercules.sgi.csp.model.Convocatoria_;
import org.crue.hercules.sgi.csp.model.FuenteFinanciacion_;
import org.crue.hercules.sgi.csp.model.Programa_;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.Solicitud_;
import org.springframework.data.jpa.domain.Specification;

public class SolicitudSpecifications {

  /**
   * {@link Solicitud} con Activo a True
   * 
   * @return specification para obtener las {@link Solicitud} activas
   */
  public static Specification<Solicitud> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Solicitud_.activo), Boolean.TRUE);
    };
  }

  /**
   * {@link Solicitud} con un unidadGestionRef incluido en la lista.
   * 
   * @param unidadGestionRefs lista de unidadGestionRefs
   * @return specification para obtener los {@link Convocatoria} cuyo
   *         unidadGestionRef se encuentre entre los recibidos.
   */
  public static Specification<Solicitud> unidadGestionRefIn(List<String> unidadGestionRefs) {
    return (root, query, cb) -> {
      return root.get(Solicitud_.unidadGestionRef).in(unidadGestionRefs);
    };
  }

  /**
   * {@link Solicitud} con codigo convocatoria como referenciaConvocatoria si
   * tiene una {@link Convocatoria} asociada o con convocatoriaExterna como
   * referenciaConvocatoria si no tiene {@link Convocatoria} asociada.
   * 
   * @param referenciaConvocatoria codigo de referencia
   * 
   * @return specification para obtener las {@link Solicitud} con la referencia
   *         buscada.
   */
  public static Specification<Solicitud> byReferenciaConvocatoria(String referenciaConvocatoria) {
    return (root, query, cb) -> {
      String referenciaConvocatoriaLike = "%" + referenciaConvocatoria + "%";

      root.join(Solicitud_.convocatoria, JoinType.LEFT);

      return cb.or(
          cb.and(cb.isNotNull(root.get(Solicitud_.convocatoria).get(Convocatoria_.id)),
              cb.like(root.get(Solicitud_.convocatoria).get(Convocatoria_.codigo), referenciaConvocatoriaLike)),
          cb.and(cb.and(cb.isNull(root.get(Solicitud_.convocatoria).get(Convocatoria_.id)),
              cb.like(root.get(Solicitud_.convocatoriaExterna), referenciaConvocatoriaLike))));
    };
  }

  public static Specification<Solicitud> byFechaInicioDesdeConfiguracionSolicitud(LocalDate fechaInicio) {
    return (root, query, cb) -> {

      Subquery<Long> queryConvocatoriaFase = query.subquery(Long.class);
      Root<ConvocatoriaFase> subqRoot = queryConvocatoriaFase.from(ConvocatoriaFase.class);
      queryConvocatoriaFase.select(subqRoot.get(ConvocatoriaFase_.convocatoria).get(Convocatoria_.id))
          .where(cb.greaterThanOrEqualTo(subqRoot.get(ConvocatoriaFase_.fechaInicio), fechaInicio));

      return cb.or(root.get(Solicitud_.convocatoria).get(Convocatoria_.id).in(queryConvocatoriaFase));
    };
  }

  public static Specification<Solicitud> byFechaInicioHastaConfiguracionSolicitud(LocalDate fechaInicio) {
    return (root, query, cb) -> {

      Subquery<Long> queryConvocatoriaFase = query.subquery(Long.class);
      Root<ConvocatoriaFase> subqRoot = queryConvocatoriaFase.from(ConvocatoriaFase.class);
      queryConvocatoriaFase.select(subqRoot.get(ConvocatoriaFase_.convocatoria).get(Convocatoria_.id))
          .where(cb.lessThanOrEqualTo(subqRoot.get(ConvocatoriaFase_.fechaInicio), fechaInicio));

      return cb.or(root.get(Solicitud_.convocatoria).get(Convocatoria_.id).in(queryConvocatoriaFase));
    };
  }

  public static Specification<Solicitud> byFechaFinDesdeConfiguracionSolicitud(LocalDate fechaFin) {
    return (root, query, cb) -> {

      Subquery<Long> queryConvocatoriaFase = query.subquery(Long.class);
      Root<ConvocatoriaFase> subqRoot = queryConvocatoriaFase.from(ConvocatoriaFase.class);
      queryConvocatoriaFase.select(subqRoot.get(ConvocatoriaFase_.convocatoria).get(Convocatoria_.id))
          .where(cb.greaterThanOrEqualTo(subqRoot.get(ConvocatoriaFase_.fechaFin), fechaFin));

      return cb.or(root.get(Solicitud_.convocatoria).get(Convocatoria_.id).in(queryConvocatoriaFase));
    };
  }

  public static Specification<Solicitud> byFechaFinHastaConfiguracionSolicitud(LocalDate fechaFin) {
    return (root, query, cb) -> {

      Subquery<Long> queryConvocatoriaFase = query.subquery(Long.class);
      Root<ConvocatoriaFase> subqRoot = queryConvocatoriaFase.from(ConvocatoriaFase.class);
      queryConvocatoriaFase.select(subqRoot.get(ConvocatoriaFase_.convocatoria).get(Convocatoria_.id))
          .where(cb.lessThanOrEqualTo(subqRoot.get(ConvocatoriaFase_.fechaFin), fechaFin));

      return cb.or(root.get(Solicitud_.convocatoria).get(Convocatoria_.id).in(queryConvocatoriaFase));
    };
  }

  public static Specification<Solicitud> byConvocatoriaEntidadFinanciadora(String entidadRef) {
    return (root, query, cb) -> {

      Subquery<Long> queryConvocatoriaEntidadFinanciadora = query.subquery(Long.class);
      Root<ConvocatoriaEntidadFinanciadora> subqRoot = queryConvocatoriaEntidadFinanciadora
          .from(ConvocatoriaEntidadFinanciadora.class);
      queryConvocatoriaEntidadFinanciadora
          .select(subqRoot.get(ConvocatoriaEntidadFinanciadora_.convocatoria).get(Convocatoria_.id))
          .where(cb.equal(subqRoot.get(ConvocatoriaEntidadFinanciadora_.entidadRef), entidadRef));

      return cb.or(root.get(Solicitud_.convocatoria).get(Convocatoria_.id).in(queryConvocatoriaEntidadFinanciadora));
    };
  }

  public static Specification<Solicitud> byConvocatoriaEntidadFinanciadoraFinanciacionId(Long fuenteFinanciacionId) {
    return (root, query, cb) -> {

      Subquery<Long> queryConvocatoriaEntidadConvocanteProgramaId = query.subquery(Long.class);
      Root<ConvocatoriaEntidadFinanciadora> subqRoot = queryConvocatoriaEntidadConvocanteProgramaId
          .from(ConvocatoriaEntidadFinanciadora.class);
      queryConvocatoriaEntidadConvocanteProgramaId
          .select(subqRoot.get(ConvocatoriaEntidadFinanciadora_.convocatoria).get(Convocatoria_.id))
          .where(cb.equal(subqRoot.get(ConvocatoriaEntidadFinanciadora_.fuenteFinanciacion).get(FuenteFinanciacion_.id),
              fuenteFinanciacionId));

      return cb
          .or(root.get(Solicitud_.convocatoria).get(Convocatoria_.id).in(queryConvocatoriaEntidadConvocanteProgramaId));
    };
  }

  public static Specification<Solicitud> byConvocatoriaEntidadConvocante(String entidadRef) {
    return (root, query, cb) -> {

      Subquery<Long> queryConvocatoriaEntidadConvocante = query.subquery(Long.class);
      Root<ConvocatoriaEntidadConvocante> subqRoot = queryConvocatoriaEntidadConvocante
          .from(ConvocatoriaEntidadConvocante.class);
      queryConvocatoriaEntidadConvocante
          .select(subqRoot.get(ConvocatoriaEntidadConvocante_.convocatoria).get(Convocatoria_.id))
          .where(cb.equal(subqRoot.get(ConvocatoriaEntidadConvocante_.entidadRef), entidadRef));

      return cb.or(root.get(Solicitud_.convocatoria).get(Convocatoria_.id).in(queryConvocatoriaEntidadConvocante));
    };
  }

  public static Specification<Solicitud> byConvocatoriaEntidadConvocanteProgramaId(Long programaId) {
    return (root, query, cb) -> {

      Subquery<Long> queryConvocatoriaEntidadConvocanteProgramaId = query.subquery(Long.class);
      Root<ConvocatoriaEntidadConvocante> subqRoot = queryConvocatoriaEntidadConvocanteProgramaId
          .from(ConvocatoriaEntidadConvocante.class);
      queryConvocatoriaEntidadConvocanteProgramaId
          .select(subqRoot.get(ConvocatoriaEntidadConvocante_.convocatoria).get(Convocatoria_.id))
          .where(cb.equal(subqRoot.get(ConvocatoriaEntidadConvocante_.programa).get(Programa_.id), programaId));

      return cb
          .or(root.get(Solicitud_.convocatoria).get(Convocatoria_.id).in(queryConvocatoriaEntidadConvocanteProgramaId));
    };
  }

}
