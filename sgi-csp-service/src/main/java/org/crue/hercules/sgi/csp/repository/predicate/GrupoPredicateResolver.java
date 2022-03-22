package org.crue.hercules.sgi.csp.repository.predicate;

import java.time.Instant;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.config.SgiConfigProperties;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoEquipo;
import org.crue.hercules.sgi.csp.model.GrupoEquipo_;
import org.crue.hercules.sgi.csp.model.Grupo_;
import org.crue.hercules.sgi.csp.model.RolProyecto_;
import org.crue.hercules.sgi.csp.util.PredicateResolverUtil;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLPredicateResolver;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import io.github.perplexhub.rsql.RSQLOperators;

public class GrupoPredicateResolver implements SgiRSQLPredicateResolver<Grupo> {

  public enum Property {
    /* Responsable */
    RESPONSABLE("responsable");

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

  private final SgiConfigProperties sgiConfigProperties;

  private GrupoPredicateResolver(SgiConfigProperties sgiConfigProperties) {
    this.sgiConfigProperties = sgiConfigProperties;
  }

  public static GrupoPredicateResolver getInstance(SgiConfigProperties sgiConfigProperties) {
    return new GrupoPredicateResolver(sgiConfigProperties);
  }

  @Override
  public boolean isManaged(ComparisonNode node) {
    Property property = Property.fromCode(node.getSelector());
    return property != null;
  }

  @Override
  public Predicate toPredicate(ComparisonNode node, Root<Grupo> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    Property property = Property.fromCode(node.getSelector());
    if (property == null) {
      return null;
    }

    switch (property) {
      case RESPONSABLE:
        return buildByResponsable(node, root, criteriaBuilder);
      default:
        return null;
    }
  }

  private Predicate buildByResponsable(ComparisonNode node, Root<Grupo> root, CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    String personaRef = node.getArguments().get(0);
    Instant fechaActual = Instant.now().atZone(sgiConfigProperties.getTimeZone().toZoneId()).toInstant();

    Join<Grupo, GrupoEquipo> joinEquipos = root.join(Grupo_.miembrosEquipo, JoinType.LEFT);

    Predicate personaRefEquals = cb.equal(joinEquipos.get(GrupoEquipo_.personaRef), personaRef);
    Predicate rolPrincipal = cb.equal(joinEquipos.get(GrupoEquipo_.rol).get(RolProyecto_.rolPrincipal), true);
    Predicate greaterThanFechaInicio = cb.lessThanOrEqualTo(joinEquipos.get(GrupoEquipo_.fechaInicio), fechaActual);
    Predicate lowerThanFechaFin = cb.or(cb.isNull(joinEquipos.get(GrupoEquipo_.fechaFin)),
        cb.greaterThanOrEqualTo(joinEquipos.get(GrupoEquipo_.fechaFin), fechaActual));

    Predicate fechaLowerThanFechaInicioGrupo = cb.greaterThan(root.get(Grupo_.fechaInicio), fechaActual);
    Predicate fechaGreaterThanFechaFinGrupo = cb.lessThan(root.get(Grupo_.fechaFin), fechaActual);

    return cb.and(
        personaRefEquals,
        rolPrincipal,
        cb.or(fechaLowerThanFechaInicioGrupo, greaterThanFechaInicio),
        cb.or(fechaGreaterThanFechaFinGrupo, lowerThanFechaFin));
  }

}
