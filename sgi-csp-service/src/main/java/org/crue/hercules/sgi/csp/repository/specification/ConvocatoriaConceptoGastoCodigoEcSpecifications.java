package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConceptoGasto_;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoCodigoEc;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoCodigoEc_;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto_;
import org.crue.hercules.sgi.csp.model.Convocatoria_;
import org.springframework.data.jpa.domain.Specification;

public class ConvocatoriaConceptoGastoCodigoEcSpecifications {

  /**
   * {@link ConvocatoriaConceptoGastoCodigoEc} conceptoGastos activos.
   * 
   * @return specification para obtener los {@link ConceptoGasto} activos.
   */
  public static Specification<ConvocatoriaConceptoGastoCodigoEc> byConceptoGastoActivo() {
    return (root, query, cb) -> {
      return cb.equal(root.get(ConvocatoriaConceptoGastoCodigoEc_.convocatoriaConceptoGasto)
          .get(ConvocatoriaConceptoGasto_.conceptoGasto).get(ConceptoGasto_.activo), Boolean.TRUE);
    };
  }

  /**
   * {@link ConvocatoriaConceptoGastoCodigoEc} ConvocatoriaConceptoGasto
   * permitido.
   * 
   * @param permitido boolean indica si el gasto es permitido o no
   * @return specification para obtener los {@link ConvocatoriaConceptoGasto}
   *         permitidos o no.
   */
  public static Specification<ConvocatoriaConceptoGastoCodigoEc> byConvocatoriaConceptoGastoPermitido(
      Boolean permitido) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ConvocatoriaConceptoGastoCodigoEc_.convocatoriaConceptoGasto)
          .get(ConvocatoriaConceptoGasto_.permitido), permitido);
    };
  }

  /**
   * Se obtienen los {@link ConvocatoriaConceptoGastoCodigoEc} por convocatoria
   * 
   * @param idConvocatoria identificador de la {@link Convocatoria}
   * @return specification para obtener los
   *         {@link ConvocatoriaConceptoGastoCodigoEc} por convocatoria
   */
  public static Specification<ConvocatoriaConceptoGastoCodigoEc> byConvocatoria(Long idConvocatoria) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ConvocatoriaConceptoGastoCodigoEc_.convocatoriaConceptoGasto)
          .get(ConvocatoriaConceptoGasto_.convocatoria).get(Convocatoria_.id), idConvocatoria);
    };
  }

}