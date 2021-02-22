package org.crue.hercules.sgi.csp.repository.specification;

import java.util.List;

import javax.persistence.criteria.Subquery;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.FuenteFinanciacion_;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadConvocante;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadConvocante_;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadFinanciadora_;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo_;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocio_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.crue.hercules.sgi.csp.model.RolProyecto_;
import org.springframework.data.jpa.domain.Specification;

public class ProyectoSpecifications {

  /**
   * {@link Proyecto} con Activo a True
   * 
   * @return specification para obtener las {@link Proyecto} activas
   */
  public static Specification<Proyecto> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Proyecto_.activo), Boolean.TRUE);
    };
  }

  /**
   * {@link Proyecto} con un unidadGestionRef incluido en la lista.
   * 
   * @param unidadGestionRefs lista de unidadGestionRefs
   * @return specification para obtener los {@link Convocatoria} cuyo
   *         unidadGestionRef se encuentre entre los recibidos.
   */
  public static Specification<Proyecto> unidadGestionRefIn(List<String> unidadGestionRefs) {
    return (root, query, cb) -> {
      return root.get(Proyecto_.unidadGestionRef).in(unidadGestionRefs);
    };
  }

  public static Specification<Proyecto> byReferenciaMiembroEquipo(String miembroEquipoRef) {
    return (root, query, cb) -> {
      Subquery<Long> queryMiembroEquipo = query.subquery(Long.class);
      Root<ProyectoEquipo> rootProyectoEquipo = queryMiembroEquipo.from(ProyectoEquipo.class);
      queryMiembroEquipo.select(rootProyectoEquipo.get(ProyectoEquipo_.proyecto).get(Proyecto_.id))
          .where(cb.equal(rootProyectoEquipo.get(ProyectoEquipo_.personaRef), miembroEquipoRef));
      return root.get(Proyecto_.id).in(queryMiembroEquipo);
    };
  }

  public static Specification<Proyecto> byReferenciaResponsableProyecto(String responsableProyecto) {
    return (root, query, cb) -> {
      Subquery<Long> queryResponsableProyecto = query.subquery(Long.class);
      Root<ProyectoEquipo> rootProyectoEquipo = queryResponsableProyecto.from(ProyectoEquipo.class);
      queryResponsableProyecto.select(rootProyectoEquipo.get(ProyectoEquipo_.proyecto).get(Proyecto_.id)).where(
          cb.and(cb.equal(rootProyectoEquipo.get(ProyectoEquipo_.personaRef), responsableProyecto)),
          cb.equal(rootProyectoEquipo.get(ProyectoEquipo_.rolProyecto).get(RolProyecto_.rolPrincipal), true));
      return root.get(Proyecto_.id).in(queryResponsableProyecto);
    };
  }

  public static Specification<Proyecto> byReferenciaSocioColaborador(String socioColaboradorRef) {
    return (root, query, cb) -> {
      Subquery<Long> querySocioColaborador = query.subquery(Long.class);
      Root<ProyectoSocio> rootProyectoSocio = querySocioColaborador.from(ProyectoSocio.class);
      querySocioColaborador.select(rootProyectoSocio.get(ProyectoSocio_.proyecto).get(Proyecto_.id))
          .where(cb.equal(rootProyectoSocio.get(ProyectoSocio_.empresaRef), socioColaboradorRef));
      return root.get(Proyecto_.id).in(querySocioColaborador);
    };
  }

  public static Specification<Proyecto> byReferenciaEntidadFinanciadora(String entidadFinanciadoraRef) {
    return (root, query, cb) -> {
      Subquery<Long> queryEntidadFinanciadora = query.subquery(Long.class);
      Root<ProyectoEntidadFinanciadora> rootProyectoEntidadFinanciadora = queryEntidadFinanciadora
          .from(ProyectoEntidadFinanciadora.class);
      queryEntidadFinanciadora.select(rootProyectoEntidadFinanciadora.get(ProyectoEntidadFinanciadora_.proyectoId))
          .where(cb.equal(rootProyectoEntidadFinanciadora.get(ProyectoEntidadFinanciadora_.entidadRef),
              entidadFinanciadoraRef));
      return root.get(Proyecto_.id).in(queryEntidadFinanciadora);
    };
  }

  public static Specification<Proyecto> byReferenciaEntidadConvocante(String entidadConvocanteRef) {
    return (root, query, cb) -> {
      Subquery<Long> queryEntidadConvocante = query.subquery(Long.class);
      Root<ProyectoEntidadConvocante> rootProyectoEntidadConvocante = queryEntidadConvocante
          .from(ProyectoEntidadConvocante.class);
      queryEntidadConvocante.select(rootProyectoEntidadConvocante.get(ProyectoEntidadConvocante_.proyectoId)).where(
          cb.equal(rootProyectoEntidadConvocante.get(ProyectoEntidadConvocante_.entidadRef), entidadConvocanteRef));
      return root.get(Proyecto_.id).in(queryEntidadConvocante);
    };
  }

  public static Specification<Proyecto> byReferenciaFuenteFinanciacion(String fuenteFinanciacionRef) {
    return (root, query, cb) -> {
      Subquery<Long> queryFuenteFinanciacion = query.subquery(Long.class);
      Root<ProyectoEntidadFinanciadora> rootProyectoEntidadFinanciadora = queryFuenteFinanciacion
          .from(ProyectoEntidadFinanciadora.class);
      queryFuenteFinanciacion.select(rootProyectoEntidadFinanciadora.get(ProyectoEntidadFinanciadora_.proyectoId))
          .where(cb.equal(rootProyectoEntidadFinanciadora.get(ProyectoEntidadFinanciadora_.fuenteFinanciacion)
              .get(FuenteFinanciacion_.id), fuenteFinanciacionRef));
      return root.get(Proyecto_.id).in(queryFuenteFinanciacion);
    };
  }

  public static Specification<Proyecto> byReferenciaPlanInvestigacion(List<Programa> programasHijos) {
    return (root, query, cb) -> {
      Subquery<Long> queryPlanInvestigacion = query.subquery(Long.class);
      Root<ProyectoEntidadConvocante> rootProyectoEntidadConvocante = queryPlanInvestigacion
          .from(ProyectoEntidadConvocante.class);
      queryPlanInvestigacion.select(rootProyectoEntidadConvocante.get(ProyectoEntidadConvocante_.proyectoId))
          .where(cb.or(rootProyectoEntidadConvocante.get(ProyectoEntidadConvocante_.programa).in(programasHijos),
              rootProyectoEntidadConvocante.get(ProyectoEntidadConvocante_.programaConvocatoria).in(programasHijos)));
      return root.get(Proyecto_.id).in(queryPlanInvestigacion);
    };
  }

}
