package org.crue.hercules.sgi.pii.repository.predicate;

import java.util.regex.Pattern;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.crue.hercules.sgi.framework.rsql.SgiRSQLPredicateResolver;
import org.crue.hercules.sgi.pii.model.TramoReparto;
import org.crue.hercules.sgi.pii.model.TramoReparto_;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.RSQLOperators;

public class TramoRepartoPredicateResolver implements SgiRSQLPredicateResolver<TramoReparto> {
  private Pattern integerPattern = Pattern.compile("^\\d+$");

  private enum Property {
    /* Máximo valor Hasta */
    MAX_HASTA("maxHasta");

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

  private TramoRepartoPredicateResolver() {
  }

  public static TramoRepartoPredicateResolver getInstance() {
    return new TramoRepartoPredicateResolver();
  }

  private Predicate buildByMaxHasta(ComparisonNode node, Root<TramoReparto> root, CriteriaQuery<?> query,
      CriteriaBuilder cb) {
    ComparisonOperator operator = node.getOperator();
    if (!operator.equals(RSQLOperators.EQUAL)) {
      // Unsupported Operator
      throw new IllegalArgumentException("Unsupported operator: " + operator + " for " + node.getSelector());
    }
    if (node.getArguments().size() != 1) {
      // Bad number of arguments
      throw new IllegalArgumentException("Bad number of arguments for " + node.getSelector());
    }
    String selectedId = node.getArguments().get(0);
    Subquery<Integer> subquery = query.subquery(Integer.class);
    Root<TramoReparto> rootSubquery = subquery.from(TramoReparto.class);
    subquery.select(cb.max(rootSubquery.get(TramoReparto_.hasta)));
    // Si el argumento es un id (entero positivo) se excluye dicho id para obtener
    // el hasta máximo
    if (isPositiveInteger(selectedId)) {
      subquery.where(cb.notEqual(rootSubquery.get(TramoReparto_.id), Long.parseLong(selectedId)));
    }

    return cb.equal(root.get(TramoReparto_.hasta), subquery);
  }

  public boolean isPositiveInteger(String strInt) {
    if (strInt == null) {
      return false;
    }
    return integerPattern.matcher(strInt).matches();
  }

  @Override
  public boolean isManaged(ComparisonNode node) {
    Property property = Property.fromCode(node.getSelector());
    return property != null;
  }

  @Override
  public Predicate toPredicate(ComparisonNode node, Root<TramoReparto> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    switch (Property.fromCode(node.getSelector())) {
      case MAX_HASTA:
        return buildByMaxHasta(node, root, query, criteriaBuilder);
      default:
        return null;
    }
  }

}
