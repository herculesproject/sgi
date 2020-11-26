package org.crue.hercules.sgi.csp.repository.specification;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.Convocatoria_;
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

}
