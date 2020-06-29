package org.crue.hercules.sgi.framework.http.converter.json;

import java.io.IOException;
import java.lang.reflect.Type;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

public class PageMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
  public static String DEFAULT_PAGE_HEADER = "X-Page";
  public static String DEFAIÑT_PAGE_SIZE_HEADER = "X-Page-Size";
  public static String DEFAULT_PAGE_COUNT_HEADER = "X-Page-Count";
  public static String DEFAULT_PAGE_TOTAL_COUNT_HEADER = "X-Page-Total-Count";
  public static String DEFAULT_TOTAL_COUNT_HEADER = "X-Total-Count";

  private String pageHeader;

  private String pageSizeHeader;

  private String pageCountHeader;

  private String pageTotalCountHeader;

  private String totalCountHeader;

  /**
   * Construct a new {@link MappingJackson2HttpMessageConverter} using default
   * configuration provided by {@link Jackson2ObjectMapperBuilder}.
   */
  public PageMappingJackson2HttpMessageConverter() {
    this(Jackson2ObjectMapperBuilder.json().build());
  }

  /**
   * Construct a new {@link MappingJackson2HttpMessageConverter} with a custom
   * {@link ObjectMapper}. You can use {@link Jackson2ObjectMapperBuilder} to
   * build it easily.
   * 
   * @see Jackson2ObjectMapperBuilder#json()
   */
  public PageMappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
    super(objectMapper);
  }

  @Override
  protected void addDefaultHeaders(HttpHeaders headers, Object t, MediaType contentType) throws IOException {
    super.addDefaultHeaders(headers, t, contentType);
    if (t instanceof Page) {
      Page<?> page = (Page<?>) t;
      // Page index
      headers.add(getPageHeader(), String.valueOf(page.getNumber()));
      // Elements per page
      headers.add(getPageSizeHeader(), String.valueOf(page.getSize()));
      // Elements in this page
      headers.add(getPageTotalCountHeader(), String.valueOf(page.getNumberOfElements()));
      // Total number of pages
      headers.add(getPageCountHeader(), String.valueOf(page.getTotalPages()));
      // Total amount of elements
      headers.add(getTotalCountHeader(), String.valueOf(page.getTotalElements()));
    }
  }

  @Override
  protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage)
      throws IOException, HttpMessageNotWritableException {
    if (object instanceof Page) {
      // Extract page content
      Page<?> page = (Page<?>) object;
      object = page.getContent();
    }
    super.writeInternal(object, type, outputMessage);
  }

  public String getPageHeader() {
    if (pageHeader != null) {
      return pageHeader;
    }
    return DEFAULT_PAGE_HEADER;
  }

  public String getPageSizeHeader() {
    if (pageSizeHeader != null) {
      return pageSizeHeader;
    }
    return DEFAIÑT_PAGE_SIZE_HEADER;
  }

  public String getPageCountHeader() {
    if (pageCountHeader != null) {
      return pageCountHeader;
    }
    return DEFAULT_PAGE_COUNT_HEADER;
  }

  public String getPageTotalCountHeader() {
    if (pageTotalCountHeader != null) {
      return pageTotalCountHeader;
    }
    return DEFAULT_PAGE_TOTAL_COUNT_HEADER;
  }

  public String getTotalCountHeader() {
    if (totalCountHeader != null) {
      return totalCountHeader;
    }
    return DEFAULT_TOTAL_COUNT_HEADER;
  }

  public void setPageHeader(String pageHeader) {
    this.pageHeader = pageHeader;
  }

  public void setPageSizeHeader(String pageSizeHeader) {
    this.pageSizeHeader = pageSizeHeader;
  }

  public void setPageCountHeader(String pageCountHeader) {
    this.pageCountHeader = pageCountHeader;
  }

  public void setPageTotalCountHeader(String pageTotalCountHeader) {
    this.pageTotalCountHeader = pageTotalCountHeader;
  }

  public void setTotalCountHeader(String totalCountHeader) {
    this.totalCountHeader = totalCountHeader;
  }

}