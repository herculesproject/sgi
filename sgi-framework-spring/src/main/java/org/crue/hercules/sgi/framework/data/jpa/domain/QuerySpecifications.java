package org.crue.hercules.sgi.framework.data.jpa.domain;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.framework.data.search.QueryCriteria;

import org.springframework.data.jpa.domain.Specification;

public class QuerySpecifications {
  public static <T> Specification<T> byQueryCriteriaList(List<QueryCriteria> query) {
    if (query == null || query.isEmpty()) {
      return null;
    }
    List<QuerySpecification<T>> specList = query.stream().map(queryCriteria -> new QuerySpecification<T>(queryCriteria))
        .collect(Collectors.toList());
    Iterator<QuerySpecification<T>> itr = specList.iterator();
    if (itr.hasNext()) {
      Specification<T> spec = Specification.where(itr.next());
      while (itr.hasNext()) {
        spec = spec.and(itr.next());
      }
      return spec;
    }
    return null;
  }
}