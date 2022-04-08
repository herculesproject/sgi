package org.crue.hercules.sgi.csp.repository.predicate;

import java.time.Instant;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.model.ProyectoConceptoGastoCodigoEc;
import org.crue.hercules.sgi.csp.model.ProyectoConceptoGastoCodigoEc_;
import org.crue.hercules.sgi.csp.model.ProyectoConceptoGasto_;
import org.crue.hercules.sgi.csp.util.PredicateResolverUtil;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLPredicateResolver;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import io.github.perplexhub.rsql.RSQLOperators;

public class ProyectoConceptoGastoCodigoEcPredicateResolver
    implements SgiRSQLPredicateResolver<ProyectoConceptoGastoCodigoEc> {
  private enum Property {
    RANGO_PROYECTO_ANUALIDAD_INICIO("inRangoProyectoAnualidadInicio"),
    RANGO_PROYECTO_ANUALIDAD_FIN("inRangoProyectoAnualidadFin");

    private String code;

    private Property(String code) {
      this.code = code;
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

  private static ProyectoConceptoGastoCodigoEcPredicateResolver instance;

  private ProyectoConceptoGastoCodigoEcPredicateResolver() {
    // Do nothing. Hide external instanciation
  }

  public static ProyectoConceptoGastoCodigoEcPredicateResolver getInstance() {
    if (instance == null) {
      instance = new ProyectoConceptoGastoCodigoEcPredicateResolver();
    }
    return instance;
  }

  private static Predicate buildByFechaInicio(ComparisonNode node, Root<ProyectoConceptoGastoCodigoEc> root,
      CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.GREATER_THAN_OR_EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    Instant fechaInicioAnualidad = Instant.parse(node.getArguments().get(0));

    return cb.or(
        cb.isNull(
            root.get(ProyectoConceptoGastoCodigoEc_.proyectoConceptoGasto).get(ProyectoConceptoGasto_.fechaInicio)),
        cb.greaterThanOrEqualTo(
            root.get(ProyectoConceptoGastoCodigoEc_.proyectoConceptoGasto).get(ProyectoConceptoGasto_.fechaInicio),
            fechaInicioAnualidad));
  }

  private static Predicate buildByFechaFin(ComparisonNode node, Root<ProyectoConceptoGastoCodigoEc> root,
      CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.LESS_THAN_OR_EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    Instant fechaFinAnualidad = Instant.parse(node.getArguments().get(0));

    return cb.or(
        cb.isNull(root.get(ProyectoConceptoGastoCodigoEc_.proyectoConceptoGasto).get(ProyectoConceptoGasto_.fechaFin)),
        cb.lessThanOrEqualTo(
            root.get(ProyectoConceptoGastoCodigoEc_.proyectoConceptoGasto).get(ProyectoConceptoGasto_.fechaFin),
            fechaFinAnualidad));
  }

  @Override
  public boolean isManaged(ComparisonNode node) {
    Property property = Property.fromCode(node.getSelector());
    return property != null;
  }

  @Override
  public Predicate toPredicate(ComparisonNode node, Root<ProyectoConceptoGastoCodigoEc> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    Property property = Property.fromCode(node.getSelector());
    if (property == null) {
      return null;
    }

    switch (property) {
      case RANGO_PROYECTO_ANUALIDAD_INICIO:
        return buildByFechaInicio(node, root, criteriaBuilder);
      case RANGO_PROYECTO_ANUALIDAD_FIN:
        return buildByFechaFin(node, root, criteriaBuilder);

      default:
        return null;
    }
  }
}
