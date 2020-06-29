package org.crue.hercules.sgi.framework.data.jpa.domain;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.framework.data.search.QueryCriteria;

import org.springframework.data.jpa.domain.Specification;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuerySpecification<T> implements Specification<T> {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  private QueryCriteria criteria;

  @Override
  public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
    if (criteria == null || criteria.getOperation() == null) {
      return null;
    }
    switch (criteria.getOperation()) {
      case EQUALS:
        return criteriaBuilder.equal(root.get(criteria.getKey()), criteria.getValue());
      case NOT_EQUALS:
        return criteriaBuilder.notEqual(root.get(criteria.getKey()), criteria.getValue());
      case LIKE:
        return criteriaBuilder.like(root.get(criteria.getKey()), criteria.getValue().toString());
      case NOT_LIKE:
        return criteriaBuilder.notLike(root.get(criteria.getKey()), criteria.getValue().toString());
      case GREATER:
        return criteriaBuilder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString());
      case GREATER_OR_EQUAL:
        return criteriaBuilder.greaterThanOrEqualTo(root.get(criteria.getKey()), criteria.getValue().toString());
      case LOWER:
        return criteriaBuilder.lessThan(root.get(criteria.getKey()), criteria.getValue().toString());
      case LOWER_OR_EQUAL:
        return criteriaBuilder.lessThanOrEqualTo(root.get(criteria.getKey()), criteria.getValue().toString());
      default:
        return null;
    }
  }

}