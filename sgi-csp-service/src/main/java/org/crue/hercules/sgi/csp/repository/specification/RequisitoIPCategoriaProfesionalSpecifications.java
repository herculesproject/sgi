package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.RequisitoIP;
import org.crue.hercules.sgi.csp.model.RequisitoIPCategoriaProfesional;
import org.crue.hercules.sgi.csp.model.RequisitoIPCategoriaProfesional_;
import org.springframework.data.jpa.domain.Specification;

public class RequisitoIPCategoriaProfesionalSpecifications {

  /**
   * {@link RequisitoIPCategoriaProfesional} del {@link RequisitoIP} con el id
   * indicado.
   * 
   * @param id identificador del {@link RequisitoIP}.
   * @return specification para obtener las
   *         {@link RequisitoIPCategoriaProfesional} del {@link RequisitoIP} con
   *         el id indicado.
   */
  public static Specification<RequisitoIPCategoriaProfesional> byRequisitoIPId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(RequisitoIPCategoriaProfesional_.requisitoIPId), id);
    };
  }

}
