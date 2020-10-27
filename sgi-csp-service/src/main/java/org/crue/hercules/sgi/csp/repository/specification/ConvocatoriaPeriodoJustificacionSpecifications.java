package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoJustificacion_;
import org.crue.hercules.sgi.csp.model.Convocatoria_;
import org.springframework.data.jpa.domain.Specification;

public class ConvocatoriaPeriodoJustificacionSpecifications {

  /**
   * {@link ConvocatoriaPeriodoJustificacion} de la {@link Convocatoria} con el id
   * indicado.
   * 
   * @param id identificador de la {@link Convocatoria}.
   * @return specification para obtener los
   *         {@link ConvocatoriaPeriodoJustificacion} de la {@link Convocatoria}
   *         con el id indicado.
   */
  public static Specification<ConvocatoriaPeriodoJustificacion> byConvocatoriaId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ConvocatoriaPeriodoJustificacion_.convocatoria).get(Convocatoria_.id), id);
    };
  }

  /**
   * {@link ConvocatoriaPeriodoJustificacion} de la {@link Convocatoria} con el
   * idConvocatoria indicado que tienen un rango de fechas solapados excluyendo al
   * {@link ConvocatoriaPeriodoJustificacion} con el id indiciado
   * 
   * @param id             identificador de la
   *                       {@link ConvocatoriaPeriodoJustificacion}.
   * @param idConvocatoria identificador de la {@link Convocatoria}.
   * @param mesInicial     mes inicial
   * @param mesFinal       mes final
   * @return specification para obtener los
   *         {@link ConvocatoriaPeriodoJustificacion} de la {@link Convocatoria}
   *         con el idConvocatoria indicado que tienen un rango de fechas
   *         solapados excluyendo al {@link ConvocatoriaPeriodoJustificacion} con
   *         el id indiciado
   */
  public static Specification<ConvocatoriaPeriodoJustificacion> byConvocatoriaAndRangoMesesSolapados(Long id,
      Long idConvocatoria, Integer mesInicial, Integer mesFinal) {
    return Specification.where(byConvocatoriaId(idConvocatoria)).and(byIdNotEqual(id))
        .and(byRangoMesesSolapados(mesInicial, mesFinal));
  }

  /**
   * {@link ConvocatoriaPeriodoJustificacion} de la {@link Convocatoria} con meses
   * solapados
   * 
   * @param id identificador de la {@link Convocatoria}.
   * @return specification para obtener los
   *         {@link ConvocatoriaPeriodoJustificacion} de la {@link Convocatoria}
   *         con el id indicado.
   */
  private static Specification<ConvocatoriaPeriodoJustificacion> byRangoMesesSolapados(Integer mesInicial,
      Integer mesFinal) {
    return (root, query, cb) -> {
      return cb.and(cb.lessThanOrEqualTo(root.get(ConvocatoriaPeriodoJustificacion_.mesInicial), mesFinal),
          cb.greaterThanOrEqualTo(root.get(ConvocatoriaPeriodoJustificacion_.mesFinal), mesInicial));
    };
  }

  /**
   * {@link ConvocatoriaPeriodoJustificacion} con id diferente del indicado.
   * 
   * @param id identificador de la {@link ConvocatoriaPeriodoJustificacion}.
   * @return specification para obtener los
   *         {@link ConvocatoriaPeriodoJustificacion} con id diferente del
   *         indicado.
   */
  private static Specification<ConvocatoriaPeriodoJustificacion> byIdNotEqual(Long id) {
    return (root, query, cb) -> {
      if (id == null) {
        return cb.isTrue(cb.literal(true)); // always true = no filtering
      }
      return cb.equal(root.get(ConvocatoriaPeriodoJustificacion_.id), id).not();
    };
  }

}
