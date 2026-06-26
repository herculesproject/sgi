package org.crue.hercules.sgi.csp.repository.specification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo_;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.RolProyecto_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProyectoProyectoSgeSpecifications {

  /**
   * {@link ProyectoProyectoSge} con id distinto
   * 
   * @param id identificador del {@link ProyectoProyectoSge}.
   * @return specification para obtener los {@link ProyectoProyectoSge} con id
   *         distinto.
   */
  public static Specification<ProyectoProyectoSge> notId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(ProyectoProyectoSge_.id), id).not();
  }

  /**
   * {@link ProyectoProyectoSge} del {@link Proyecto} con el id indicado.
   * 
   * @param proyectoId identificador del {@link Proyecto}.
   * @return specification para obtener los {@link ProyectoProyectoSge} del
   *         {@link Proyecto} con el id indicado.
   */
  public static Specification<ProyectoProyectoSge> byProyectoId(Long proyectoId) {
    return (root, query, cb) -> cb.equal(root.get(ProyectoProyectoSge_.proyecto).get(Proyecto_.id), proyectoId);

  }

  /**
   * {@link ProyectoProyectoSge} con un { @link Proyecto} con un unidadGestionRef
   * incluido en la lista.
   * 
   * @param unidadGestionRefs lista de unidadGestionRefs
   * @return specification para obtener los {@link ProyectoProyectoSge} cuyo
   *         unidadGestionRef se encuentre entre los recibidos.
   */
  public static Specification<ProyectoProyectoSge> unidadGestionRefIn(List<String> unidadGestionRefs) {
    return (root, query, cb) -> root.get(ProyectoProyectoSge_.proyecto).get(Proyecto_.unidadGestionRef)
        .in(unidadGestionRefs);
  }

  /**
   * {@link ProyectoProyectoSge} cuyo {@link Proyecto} tenga un id incluido en la
   * lista.
   *
   * @param proyectoIds lista de identificadores de {@link Proyecto}.
   * @return specification para obtener los {@link ProyectoProyectoSge} cuyo
   *         {@link Proyecto} se encuentre entre los recibidos.
   */
  public static Specification<ProyectoProyectoSge> byProyectoIdIn(List<Long> proyectoIds) {
    return (root, query, cb) -> root.get(ProyectoProyectoSge_.proyecto).get(Proyecto_.id).in(proyectoIds);
  }

  /**
   * {@link ProyectoProyectoSge} asociados a {@link Proyecto} activos.
   * 
   * @return specification para obtener los ProyectoProyectoSge} asociados a
   * 
   *         {@link Proyecto} activos
   */
  public static Specification<ProyectoProyectoSge> activos() {
    return (root, query, cb) -> cb.isTrue(root.get(ProyectoProyectoSge_.proyecto).get(Proyecto_.activo));
  }

  /**
   * {@link ProyectoProyectoSge} del ProyectoSGE con el id indicado.
   * 
   * @param proyectoSgeRef identificador del ProyectoSGE.
   * @return specification para obtener los {@link ProyectoProyectoSge} del
   *         ProyectoSGE con el id indicado.
   */
  public static Specification<ProyectoProyectoSge> byProyectoSgeRef(String proyectoSgeRef) {
    return (root, query, cb) -> cb.equal(root.get(ProyectoProyectoSge_.proyectoSgeRef), proyectoSgeRef);

  }

  /**
   * {@link ProyectoProyectoSge} cuyo {@link Proyecto} tiene en su equipo a la
   * persona indicada con un {@link RolProyecto} con el flag rolPrincipal a
   * <code>true</code>.
   * <p>
   * Si se indica una fecha, ademas se exige que la participacion de la persona
   * como investigador principal este vigente en esa fecha. Si la fecha es
   * <code>null</code> se considera la participacion en cualquier fecha.
   *
   * @param personaRef identificador de la persona.
   * @param fecha      fecha en la que debe estar vigente la participacion, o
   *                   <code>null</code> para cualquier fecha.
   * @return specification para obtener los {@link ProyectoProyectoSge} de
   *         {@link Proyecto} en los que la persona es investigador principal.
   */
  public static Specification<ProyectoProyectoSge> byPersonaInProyectoEquipoRolPrincipal(String personaRef,
      Instant fecha) {
    return (root, query, cb) -> {
      Subquery<Long> subquery = query.subquery(Long.class);
      Root<ProyectoEquipo> subRoot = subquery.from(ProyectoEquipo.class);

      List<Predicate> predicates = new ArrayList<>();
      predicates.add(cb.equal(subRoot.get(ProyectoEquipo_.proyecto).get(Proyecto_.id),
          root.get(ProyectoProyectoSge_.proyecto).get(Proyecto_.id)));
      predicates.add(cb.equal(subRoot.get(ProyectoEquipo_.personaRef), personaRef));
      predicates.add(cb.isTrue(subRoot.get(ProyectoEquipo_.rolProyecto).get(RolProyecto_.rolPrincipal)));

      if (fecha != null) {
        predicates.add(cb.or(cb.isNull(subRoot.get(ProyectoEquipo_.fechaInicio)),
            cb.lessThanOrEqualTo(subRoot.get(ProyectoEquipo_.fechaInicio), fecha)));
        predicates.add(cb.or(cb.isNull(subRoot.get(ProyectoEquipo_.fechaFin)),
            cb.greaterThanOrEqualTo(subRoot.get(ProyectoEquipo_.fechaFin), fecha)));
      }

      subquery.select(subRoot.get(ProyectoEquipo_.proyecto).get(Proyecto_.id))
          .where(predicates.toArray(new Predicate[0]));

      return root.get(ProyectoProyectoSge_.proyecto).get(Proyecto_.id).in(subquery);
    };
  }

}