package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.ProrrogaDocumento;
import org.crue.hercules.sgi.csp.model.ProrrogaDocumento_;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoProrroga;
import org.crue.hercules.sgi.csp.model.ProyectoProrroga_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProrrogaDocumentoSpecifications {

  /**
   * {@link ProrrogaDocumento} de la {@link ProyectoProrroga} con el id indicado.
   * 
   * @param id identificador de la {@link ProyectoProrroga}.
   * @return specification para obtener los {@link ProrrogaDocumento} de la
   *         {@link ProyectoProrroga} con el id indicado.
   */
  public static Specification<ProrrogaDocumento> byProyectoProrrogaId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(ProrrogaDocumento_.proyectoProrroga).get(ProyectoProrroga_.id), id);
  }

  /**
   * {@link ProrrogaDocumento} de la {@link Proyecto} con el id indicado.
   * 
   * @param id identificador de la {@link Proyecto}.
   * @return specification para obtener los {@link ProrrogaDocumento} de la
   *         {@link Proyecto} con el id indicado.
   */
  public static Specification<ProrrogaDocumento> byProyectoId(Long id) {
    return (root, query, cb) -> cb.equal(
        root.get(ProrrogaDocumento_.proyectoProrroga).get(ProyectoProrroga_.proyecto).get(Proyecto_.id),
        id);
  }

  /**
   * {@link ProrrogaDocumento} visibles.
   *
   * @return specification para obtener los {@link ProrrogaDocumento} visibles.
   */
  public static Specification<ProrrogaDocumento> onlyVisibles() {
    return (root, query, cb) -> cb.isTrue(root.get(ProrrogaDocumento_.visible));
  }

}
