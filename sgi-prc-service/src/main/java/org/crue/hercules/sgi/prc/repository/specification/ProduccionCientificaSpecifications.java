package org.crue.hercules.sgi.prc.repository.specification;

import java.util.List;

import org.crue.hercules.sgi.prc.enums.EpigrafeCVN;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica_;
import org.springframework.data.jpa.domain.Specification;

public class ProduccionCientificaSpecifications {

  private ProduccionCientificaSpecifications() {
  }

  /**
   * {@link ProduccionCientifica} de la {@link ConvocatoriaBaremacion} con
   * el id indicado.
   * 
   * @param convocatoriaBaremacionId identificador del
   *                                 {@link ConvocatoriaBaremacion}.
   * @return specification para obtener los {@link ProduccionCientifica} de
   *         la {@link ConvocatoriaBaremacion} con el id indicado.
   */
  public static Specification<ProduccionCientifica> byConvocatoriaBaremacionId(Long convocatoriaBaremacionId) {
    return (root, query, cb) -> cb.equal(root.get(ProduccionCientifica_.convocatoriaBaremacionId),
        convocatoriaBaremacionId);
  }

  /**
   * {@link ProduccionCientifica} con el {@link EpigrafeCVN} indicado.
   * 
   * @param epigrafeCVN {@link EpigrafeCVN}.
   * @return specification para obtener los {@link ProduccionCientifica} con
   *         el {@link EpigrafeCVN} indicado.
   */
  public static Specification<ProduccionCientifica> byEpigrafeCVN(EpigrafeCVN epigrafeCVN) {
    return (root, query, cb) -> cb.equal(root.get(ProduccionCientifica_.epigrafeCVN), epigrafeCVN);
  }

  /**
   * Lista de {@link ProduccionCientifica} que no contenga los
   * {@link EpigrafeCVN} indicados.
   * 
   * @param epigrafes Lista de {@link EpigrafeCVN}.
   * @return specification para obtener los {@link ProduccionCientifica} que
   *         contenga los {@link EpigrafeCVN} indicados.
   */
  public static Specification<ProduccionCientifica> byEpigrafeCVNIn(List<EpigrafeCVN> epigrafes) {
    return (root, query, cb) -> root.get(ProduccionCientifica_.epigrafeCVN).in(epigrafes);
  }

}
