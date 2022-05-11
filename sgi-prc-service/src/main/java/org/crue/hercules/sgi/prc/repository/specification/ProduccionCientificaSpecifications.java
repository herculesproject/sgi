package org.crue.hercules.sgi.prc.repository.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.prc.enums.EpigrafeCVN;
import org.crue.hercules.sgi.prc.model.Autor;
import org.crue.hercules.sgi.prc.model.AutorGrupo;
import org.crue.hercules.sgi.prc.model.AutorGrupo_;
import org.crue.hercules.sgi.prc.model.Autor_;
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

  /**
   * Lista de {@link ProduccionCientifica} que contenga los grupoRef indicados.
   * 
   * @param gruposRef Lista de grupoRef.
   * @return specification para obtener los {@link ProduccionCientifica} que
   *         contenga los grupoRef indicados.
   */
  public static Specification<ProduccionCientifica> byExistsSubqueryGrupoRefIn(List<Long> gruposRef) {
    return (root, query, cb) -> {
      List<Predicate> predicatesSubquery = new ArrayList<>();

      Subquery<Long> queryAutorGrupo = query.subquery(Long.class);
      Root<AutorGrupo> subqRoot = queryAutorGrupo.from(AutorGrupo.class);

      Join<AutorGrupo, Autor> joinAutor = subqRoot.join(AutorGrupo_.autor);

      predicatesSubquery
          .add(cb.and(cb.equal(joinAutor.get(Autor_.produccionCientificaId), root.get(ProduccionCientifica_.id))));
      predicatesSubquery
          .add(cb.and(subqRoot.get(AutorGrupo_.grupoRef).in(gruposRef)));

      queryAutorGrupo.select(subqRoot.get(AutorGrupo_.id))
          .where(predicatesSubquery.toArray(new Predicate[] {}));

      return cb.and(cb.exists(queryAutorGrupo));
    };
  }

}
