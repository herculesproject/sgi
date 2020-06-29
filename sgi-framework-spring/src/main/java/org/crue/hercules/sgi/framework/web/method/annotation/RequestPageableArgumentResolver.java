package org.crue.hercules.sgi.framework.web.method.annotation;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.crue.hercules.sgi.framework.core.convert.converter.SortCriteriaConverter;
import org.crue.hercules.sgi.framework.data.sort.SortCriteria;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.multipart.support.MultipartResolutionDelegate;

public class RequestPageableArgumentResolver implements HandlerMethodArgumentResolver {

  private SortCriteriaConverter converter;

  public RequestPageableArgumentResolver(SortCriteriaConverter converter) {
    this.converter = converter;
  }

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return (parameter.hasParameterAnnotation(RequestPageable.class)
        && Pageable.class.isAssignableFrom(parameter.getNestedParameterType()));
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
    MethodParameter nestedParameter = parameter.nestedIfOptional();
    RequestPageable requestPageable = parameter.getParameterAnnotation(RequestPageable.class);
    String xPage = webRequest.getHeader(requestPageable.pageHeader());
    String xPageSize = webRequest.getHeader(requestPageable.pageSizeHeader());
    String sortParamName = requestPageable.sort();
    Sort sort = Sort.unsorted();

    if (sortParamName != null && !"".equals(sortParamName)) {
      Object arg = resolveName(sortParamName.toString(), nestedParameter, webRequest);
      if (arg != null) {
        List<SortCriteria> sortCriterias = converter.convert(arg.toString());
        List<Sort> sortList = generateSortList(sortCriterias);
        sort = andSort(sortList);
      }
    }

    if (xPageSize == null) {
      return new UnpagedPageable(sort);
    }
    if (xPage == null) {
      xPage = "0";
    }

    // Use provided page size and short info
    return PageRequest.of(Integer.parseInt(xPage), Integer.parseInt(xPageSize), sort);
  }

  protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
    HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);

    if (servletRequest != null) {
      Object mpArg = MultipartResolutionDelegate.resolveMultipartArgument(name, parameter, servletRequest);
      if (mpArg != MultipartResolutionDelegate.UNRESOLVABLE) {
        return mpArg;
      }
    }

    Object arg = null;
    MultipartRequest multipartRequest = request.getNativeRequest(MultipartRequest.class);
    if (multipartRequest != null) {
      List<MultipartFile> files = multipartRequest.getFiles(name);
      if (!files.isEmpty()) {
        arg = (files.size() == 1 ? files.get(0) : files);
      }
    }
    if (arg == null) {
      String[] paramValues = request.getParameterValues(name);
      if (paramValues != null) {
        arg = (paramValues.length == 1 ? paramValues[0] : paramValues);
      }
    }
    return arg;
  }

  private List<Sort> generateSortList(List<SortCriteria> criteria) {
    return criteria.stream().map((criterion) -> {
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

  private Sort andSort(List<Sort> criteria) {

    Iterator<Sort> itr = criteria.iterator();
    if (itr.hasNext()) {
      Sort sort = (itr.next());
      while (itr.hasNext()) {
        sort = sort.and(itr.next());
      }
      return sort;
    }
    return null;
  }

  public class UnpagedPageable implements Pageable {
    private Sort sort;

    public UnpagedPageable(Sort sort) {
      this.sort = sort;
    }

    @Override
    public boolean isPaged() {
      return false;
    }

    @Override
    public Pageable previousOrFirst() {
      return this;
    }

    @Override
    public Pageable next() {
      return this;
    }

    @Override
    public boolean hasPrevious() {
      return false;
    }

    @Override
    public Sort getSort() {
      return sort;
    }

    @Override
    @JsonIgnore
    public int getPageSize() {
      throw new UnsupportedOperationException();
    }

    @Override
    @JsonIgnore
    public int getPageNumber() {
      throw new UnsupportedOperationException();
    }

    @Override
    @JsonIgnore
    public long getOffset() {
      throw new UnsupportedOperationException();
    }

    @Override
    public Pageable first() {
      return this;
    }

  }

}