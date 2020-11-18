package org.crue.hercules.sgi.csp.repository.specification;

import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.csp.model.ConvocatoriaAreaTematica;
import org.crue.hercules.sgi.csp.model.ConvocatoriaAreaTematica_;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante_;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadFinanciadora_;
import org.crue.hercules.sgi.csp.model.AreaTematica;
import org.crue.hercules.sgi.csp.model.AreaTematica_;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.Convocatoria_;
import org.crue.hercules.sgi.csp.model.FuenteFinanciacion;
import org.crue.hercules.sgi.csp.model.FuenteFinanciacion_;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class ConvocatoriaSpecifications {

  /**
   * {@link Convocatoria} con Activo a True
   * 
   * @return specification para obtener las {@link Convocatoria} activas
   */
  public static Specification<Convocatoria> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Convocatoria_.activo), Boolean.TRUE);
    };
  }

  /**
   * {@link Convocatoria} unidadGestionRef.
   * 
   * @return specification para obtener los {@link Convocatoria} cuyo
   *         unidadGestionRef se encuentre entre los recibidos.
   */
  public static Specification<Convocatoria> acronimosIn(List<String> acronimos) {
    return (root, query, cb) -> {
      return root.get(Convocatoria_.unidadGestionRef).in(acronimos);

    };
  }

  /**
   * {@link Convocatoria} asociada al area temática recibida.
   * 
   * @param idAreaTematica Identificador de {@link AreaTematica}
   * 
   * @return specification para obtener las {@link Convocatoria} activas
   */

  public static Specification<Convocatoria> byAreaTematicaId(Long idAreaTematica) {
    return (root, query, cb) -> {

      Subquery<Long> queryConvocatoriaAreaTematica = query.subquery(Long.class);
      Root<ConvocatoriaAreaTematica> subqRoot = queryConvocatoriaAreaTematica.from(ConvocatoriaAreaTematica.class);
      queryConvocatoriaAreaTematica.select(subqRoot.get(ConvocatoriaAreaTematica_.convocatoria).get(Convocatoria_.id))
          .where(cb.equal(subqRoot.get(ConvocatoriaAreaTematica_.areaTematica).get(AreaTematica_.id), idAreaTematica));

      return root.get(Convocatoria_.id).in(queryConvocatoriaAreaTematica);
    };
  }

  /**
   * {@link Convocatoria} asociada a la entidad convocante recibida.
   * 
   * @param entidadRef Referencia de la entidad convocante.
   * 
   * @return specification para obtener las {@link Convocatoria} activas
   */
  public static Specification<Convocatoria> byEntidadConvocanteRef(String entidadRef) {
    return (root, query, cb) -> {

      Subquery<Long> queryConvocatoriaEntidadConvocante = query.subquery(Long.class);
      Root<ConvocatoriaEntidadConvocante> subqRoot = queryConvocatoriaEntidadConvocante
          .from(ConvocatoriaEntidadConvocante.class);
      queryConvocatoriaEntidadConvocante
          .select(subqRoot.get(ConvocatoriaEntidadConvocante_.convocatoria).get(Convocatoria_.id))
          .where(cb.equal(subqRoot.get(ConvocatoriaEntidadConvocante_.entidadRef), entidadRef));

      return root.get(Convocatoria_.id).in(queryConvocatoriaEntidadConvocante);
    };
  }

  /**
   * {@link Convocatoria} asociada a la entidad financiera recibida.
   * 
   * @param entidadRef Referencia de la entidad financiera.
   * 
   * @return specification para obtener las {@link Convocatoria} activas
   */
  public static Specification<Convocatoria> byEntidadFinancieraRef(String entidadRef) {
    return (root, query, cb) -> {

      Subquery<Long> queryConvocatoriaEntidadFinanciera = query.subquery(Long.class);
      Root<ConvocatoriaEntidadFinanciadora> subqRoot = queryConvocatoriaEntidadFinanciera
          .from(ConvocatoriaEntidadFinanciadora.class);
      queryConvocatoriaEntidadFinanciera
          .select(subqRoot.get(ConvocatoriaEntidadFinanciadora_.convocatoria).get(Convocatoria_.id))
          .where(cb.equal(subqRoot.get(ConvocatoriaEntidadFinanciadora_.entidadRef), entidadRef));

      return root.get(Convocatoria_.id).in(queryConvocatoriaEntidadFinanciera);
    };
  }

  /**
   * {@link Convocatoria} asociada a la fuente de financiación recibida.
   * 
   * @param fuenteFinanciacionId Identificador {@link FuenteFinanciacion}
   * 
   * @return specification para obtener las {@link Convocatoria} asociadas a la
   *         fuente de financiación.
   */
  public static Specification<Convocatoria> byFuenteFinanciacionId(Long fuenteFinanciacionId) {
    return (root, query, cb) -> {

      Subquery<Long> queryConvocatoriaEntidadFinanciera = query.subquery(Long.class);
      Root<ConvocatoriaEntidadFinanciadora> subqRoot = queryConvocatoriaEntidadFinanciera
          .from(ConvocatoriaEntidadFinanciadora.class);
      queryConvocatoriaEntidadFinanciera
          .select(subqRoot.get(ConvocatoriaEntidadFinanciadora_.convocatoria).get(Convocatoria_.id))
          .where(cb.equal(subqRoot.get(ConvocatoriaEntidadFinanciadora_.fuenteFinanciacion).get(FuenteFinanciacion_.id),
              fuenteFinanciacionId));

      return root.get(Convocatoria_.id).in(queryConvocatoriaEntidadFinanciera);
    };
  }

}
