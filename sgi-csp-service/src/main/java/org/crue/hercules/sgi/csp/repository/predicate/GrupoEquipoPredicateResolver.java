package org.crue.hercules.sgi.csp.repository.predicate;

import java.time.Instant;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoEquipo;
import org.crue.hercules.sgi.csp.model.GrupoEquipo_;
import org.crue.hercules.sgi.csp.model.Grupo_;
import org.crue.hercules.sgi.csp.util.PredicateResolverUtil;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLPredicateResolver;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import io.github.perplexhub.rsql.RSQLOperators;

public class GrupoEquipoPredicateResolver implements SgiRSQLPredicateResolver<GrupoEquipo> {

  public enum Property {
    /* Fecha participacion anterior a */
    FECHA_PARTICIPACION_ANTERIOR("fechaParticipacionAnterior"),
    /* Fecha participacion posterior a */
    FECHA_PARTICIPACION_POSTERIOR("fechaParticipacionPosterior");

    private String code;

    private Property(String code) {
      this.code = code;
    }

    public String getCode() {
      return code;
    }

    public static Property fromCode(String code) {
      for (Property property : Property.values()) {
        if (property.code.equals(code)) {
          return property;
        }
      }
      return null;
    }
  }

  private GrupoEquipoPredicateResolver() {
  }

  public static GrupoEquipoPredicateResolver getInstance() {
    return new GrupoEquipoPredicateResolver();
  }

  @Override
  public boolean isManaged(ComparisonNode node) {
    Property property = Property.fromCode(node.getSelector());
    return property != null;
  }

  @Override
  public Predicate toPredicate(ComparisonNode node, Root<GrupoEquipo> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    Property property = Property.fromCode(node.getSelector());
    if (property == null) {
      return null;
    }

    switch (property) {
      case FECHA_PARTICIPACION_ANTERIOR:
        return buildByFechaParticipacionAnterior(node, root, criteriaBuilder);
      case FECHA_PARTICIPACION_POSTERIOR:
        return buildByFechaParticipacionPosterior(node, root, criteriaBuilder);
      default:
        return null;
    }
  }

  private Predicate buildByFechaParticipacionAnterior(ComparisonNode node, Root<GrupoEquipo> root, CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.LESS_THAN_OR_EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    String fechaModificacionArgument = node.getArguments().get(0);
    Instant fechaFin = Instant.parse(fechaModificacionArgument);
    Join<GrupoEquipo, Grupo> joinGrupo = root.join(GrupoEquipo_.grupo, JoinType.LEFT);

    return cb.or(
        cb.lessThanOrEqualTo(root.get(GrupoEquipo_.fechaInicio), fechaFin),
        cb.and(
            cb.isNull(root.get(GrupoEquipo_.fechaInicio)),
            cb.lessThanOrEqualTo(joinGrupo.get(Grupo_.fechaInicio), fechaFin)));
  }

  private Predicate buildByFechaParticipacionPosterior(ComparisonNode node, Root<GrupoEquipo> root,
      CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.GREATER_THAN_OR_EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    String fechaModificacionArgument = node.getArguments().get(0);
    Instant fechaInicio = Instant.parse(fechaModificacionArgument);
    Join<GrupoEquipo, Grupo> joinGrupo = root.join(GrupoEquipo_.grupo, JoinType.LEFT);

    return cb.or(
        cb.greaterThanOrEqualTo(root.get(GrupoEquipo_.fechaFin), fechaInicio),
        cb.and(
            cb.isNull(root.get(GrupoEquipo_.fechaFin)),
            cb.or(
                cb.isNull(joinGrupo.get(Grupo_.fechaFin)),
                cb.greaterThanOrEqualTo(joinGrupo.get(Grupo_.fechaFin), fechaInicio))));
  }

}
