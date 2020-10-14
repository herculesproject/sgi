package org.crue.hercules.sgi.eti.repository.specification;

import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.EstadoMemoria;
import org.crue.hercules.sgi.eti.model.EstadoMemoria_;
import org.crue.hercules.sgi.eti.model.Memoria_;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specifications de {@link EstadoMemoria}.
 */
public class EstadoMemoriaSpecifications {

  /**
   * Evaluaciones de una {@link EstadoMemoria}.
   * 
   * @param idMemoria identificador de la {@link ConvocatoriaReunion}.
   * @return specification para obtener las evaluaciones asociadas a la
   *         convocatoria.
   */
  public static Specification<EstadoMemoria> byMemoriaId(Long idMemoria) {
    return (root, query, cb) -> {
      cb.desc(root.get(EstadoMemoria_.fechaEstado));
      return cb.equal(root.get(EstadoMemoria_.memoria).get(Memoria_.id), idMemoria);
    };
  }

}
