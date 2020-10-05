package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion_;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase_;
import org.springframework.data.jpa.domain.Specification;

public class ModeloTipoFaseSpecifications {

  /**
   * {@link ModeloTipoFase} con activoConvocatoria a true.
   * 
   * @return specification para obtener los {@link ModeloTipoFase} con
   *         activoConvocatoria a true.
   */
  public static Specification<ModeloTipoFase> activosConvocatoria() {
    return (root, query, cb) -> {
      return cb.equal(root.get(ModeloTipoFase_.activoConvocatoria), Boolean.TRUE);
    };
  }

  /**
   * {@link ModeloTipoFase} con activoProyecto a true.
   * 
   * @return specification para obtener los {@link ModeloTipoFase} con
   *         activoProyecto a true.
   */
  public static Specification<ModeloTipoFase> activosProyecto() {
    return (root, query, cb) -> {
      return cb.equal(root.get(ModeloTipoFase_.activoProyecto), Boolean.TRUE);
    };
  }

  /**
   * {@link ModeloTipoFase} con activoSolicitud a true.
   * 
   * @return specification para obtener los {@link ModeloTipoFase} con
   *         activoSolicitud a true.
   */
  public static Specification<ModeloTipoFase> activosSolcitud() {
    return (root, query, cb) -> {
      return cb.equal(root.get(ModeloTipoFase_.activoSolicitud), Boolean.TRUE);
    };
  }

  /**
   * {@link ModeloTipoFase} del {@link ModeloEjecucion} con el id indicado.
   * 
   * @param id identificador del {@link ModeloEjecucion}.
   * @return specification para obtener los {@link ModeloTipoFase} del
   *         {@link ModeloEjecucion} con el id indicado.
   */
  public static Specification<ModeloTipoFase> byModeloEjecucionId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ModeloTipoFase_.modeloEjecucion).get(ModeloEjecucion_.id), id);
    };
  }

}