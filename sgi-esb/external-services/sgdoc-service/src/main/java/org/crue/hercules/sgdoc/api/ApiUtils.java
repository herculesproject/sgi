package org.crue.hercules.sgdoc.api;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.framework.core.convert.converter.QueryCriteriaConverter;
import org.crue.hercules.sgi.framework.core.convert.converter.SortCriteriaConverter;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.crue.hercules.sgi.framework.data.sort.SortCriteria;
import org.crue.hercules.sgi.framework.http.converter.json.PageMappingJackson2HttpMessageConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class ApiUtils {

  private QueryCriteriaConverter queryOperationConverter = new QueryCriteriaConverter();
  private SortCriteriaConverter sortCriteriaConverter = new SortCriteriaConverter();

  public ApiUtils() {
  }

  /**
   * Obiene la lista de {@link QueryCriteria} correspondiente a la query
   * 
   * @param query query criteria en formato texto.
   * @return la lista de {@link QueryCriteria}.
   */
  public List<QueryCriteria> getQueryCriteria(String query) {
    return queryOperationConverter.convert(query);
  }

  /**
   * Obtiene el {@link Pageable} correspondiente a los parametros indicados.
   * 
   * @param page         indice de la pagina empezando en 0.
   * @param size         tamaño de la pagina.
   * @param sortCriteria ordenacion de la paginacion.
   * @return el {@link Pageable} correspondiente.
   */
  public Pageable getPageable(Integer page, Integer pageSize, String sortCriteria) {
    List<SortCriteria> sortCriteriaList = sortCriteriaConverter.convert(sortCriteria);
    Sort sort = sortCriteriaListToSort(sortCriteriaList);

    if (page == null && pageSize == null && sortCriteria == null) {
      return Pageable.unpaged();
    }

    if (pageSize == null) {
      return new UnpagedSorted(sort);
    }

    if (page == null) {
      page = 0;
    }

    return PageRequest.of(page, pageSize, sort);
  }

  /**
   * Obtiene las cabeceras de la paginacion.
   * 
   * @param page una {@link Page}.
   * @return las cabeceras con la informacion de la paginacion.
   */
  public HttpHeaders getPaginationHeaders(Page<?> page) {
    HttpHeaders paginationHeaders = new HttpHeaders();
    // Page index
    paginationHeaders.add(PageMappingJackson2HttpMessageConverter.DEFAULT_PAGE_HEADER,
        String.valueOf(page.getNumber()));
    // Elements per page
    paginationHeaders.add(PageMappingJackson2HttpMessageConverter.DEFAULT_PAGE_SIZE_HEADER,
        String.valueOf(page.getSize()));
    // Elements in this page
    paginationHeaders.add(PageMappingJackson2HttpMessageConverter.DEFAULT_PAGE_COUNT_HEADER,
        String.valueOf(page.getNumberOfElements()));
    // Total number of pages
    paginationHeaders.add(PageMappingJackson2HttpMessageConverter.DEFAULT_PAGE_TOTAL_COUNT_HEADER,
        String.valueOf(page.getTotalPages()));
    // Total amount of elements
    paginationHeaders.add(PageMappingJackson2HttpMessageConverter.DEFAULT_TOTAL_COUNT_HEADER,
        String.valueOf(page.getTotalElements()));

    return paginationHeaders;
  }

  /**
   * Convierte una lista de {@link SortCriteria} en un {@link Sort}.
   * 
   * @param sortCriteriaList lista de {@link SortCriteria}.
   * @return un {@link Sort} con las ordenaciones de la lista
   */
  private Sort sortCriteriaListToSort(List<SortCriteria> sortCriteriaList) {
    List<Sort> sortList = generateSortList(sortCriteriaList);

    return andSort(sortList);
  }

  /**
   * Convierte una lista de {@link SortCriteria} en una lista de {@link Sort}.
   * 
   * @param sortCriteriaList una lista de {@link SortCriteria}.
   * @return la lista de {@link Sort}.
   */
  private List<Sort> generateSortList(List<SortCriteria> sortCriteriaList) {
    return sortCriteriaList.stream().map((criterion) -> {
      switch (criterion.getOperation()) {
        case ASC:
          return Sort.by(Order.asc(criterion.getKey()));
        case DESC:
          return Sort.by(Order.desc(criterion.getKey()));
        default:
          return null;
      }
    }).filter((sort) -> sort != null).collect(Collectors.toList());
  }

  /**
   * Convierte una lista de {@link Sort} en un {@link Sort}.
   * 
   * @param sortList lista de {@link Sort}.
   * @return un objeto sort con todas las ordenaciones de la lista.
   */
  private Sort andSort(List<Sort> sortList) {
    Iterator<Sort> itr = sortList.iterator();
    if (itr.hasNext()) {
      Sort sort = (itr.next());
      while (itr.hasNext()) {
        sort = sort.and(itr.next());
      }
      return sort;
    }

    return Sort.unsorted();
  }

}