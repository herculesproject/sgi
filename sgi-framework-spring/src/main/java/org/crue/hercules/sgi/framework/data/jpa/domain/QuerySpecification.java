package org.crue.hercules.sgi.framework.data.jpa.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.crue.hercules.sgi.framework.data.search.QueryOperation;
import org.crue.hercules.sgi.framework.exception.IllegalSpecificationArgumentException;
import org.crue.hercules.sgi.framework.exception.UnsupportedSpecificationOperationException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class QuerySpecification<T> extends StringDynamicSpecification<T> {
  private static final long serialVersionUID = 1L;

  private final List<QueryCriteria> query;

  /**
   * Creates a WHERE clause for a query of the referenced entity in form of a
   * {@link Predicate} for the given {@link Root} and {@link CriteriaQuery}.
   *
   * @param root must not be {@literal null}.
   * @param cq   must not be {@literal null}.
   * @param cb   must not be {@literal null}.
   * @return a {@link Predicate}, may be {@literal null}.
   */
  @Override
  public Predicate toPredicate(Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
    if (query == null || query.isEmpty()) {
      return null;
    }
    return cb.and(getPredicates(cb, root, query).toArray(new Predicate[] {}));
  }

  /**
   * Builds the predicates to use in the specification from the list of query
   * criterias.
   * 
   * @param cb        The JPA criteria builder
   * @param root      The root of the entity
   * @param criterias The list of query criterias to convert
   * 
   * @return The list of builded predicates
   * 
   * @throws IllegalSpecificationArgumentException      If the values can't be
   *                                                    converted or fields don't
   *                                                    exists
   * @throws UnsupportedSpecificationOperationException If the operation can't be
   *                                                    builded or isn't supported
   */
  private List<Predicate> getPredicates(CriteriaBuilder cb, Root<T> root, List<QueryCriteria> criterias)
      throws IllegalSpecificationArgumentException, UnsupportedSpecificationOperationException {
    List<Predicate> predicates = new ArrayList<>();
    for (QueryCriteria criteria : criterias) {
      Path<?> path = getPath(root, criteria.getKey());
      Object value = stringToFieldClass(path.getJavaType(), criteria.getValue());
      Predicate predicate = getPredicate(cb, path, value, criteria.getOperation());
      if (predicate != null) {
        predicates.add(predicate);
      }
    }
    return predicates;
  }

  /**
   * Builds a predicate
   * 
   * @param cb    The JPA criteria builder to use
   * @param field The field to use
   * @param value The value to use, it must be converted to the field type.
   * @param op    The operation of the predicate
   * 
   * @return The predicate
   * 
   * @throws UnsupportedSpecificationOperationException If the predicate cannot be
   *                                                    builded
   */
  private Predicate getPredicate(CriteriaBuilder cb, Expression<?> field, Object value, QueryOperation op)
      throws UnsupportedSpecificationOperationException {
    switch (op) {
      case EQUALS:
        return buildEqual(cb, field, value);
      case NOT_EQUALS:
        return buildNotEqual(cb, field, value);
      case LOWER_OR_EQUAL:
        return buildLessThanOrEqual(cb, field, value);
      case GREATER_OR_EQUAL:
        return buildGreaterThanOrEqual(cb, field, value);
      case LIKE:
        return buildContains(cb, field, value);
      case NOT_LIKE:
        return buildNotContains(cb, field, value);
      case LOWER:
        return buildLessThan(cb, field, value);
      case GREATER:
        return buildGreaterThan(cb, field, value);
      default:
        return null;
    }
  }

}