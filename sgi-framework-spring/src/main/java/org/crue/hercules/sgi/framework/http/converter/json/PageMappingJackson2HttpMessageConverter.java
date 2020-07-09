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

/**
 * Implementation of
 * {@link org.springframework.http.converter.HttpMessageConverter} that can read
 * and write JSON using <a href="https://github.com/FasterXML/jackson">Jackson
 * 2.x's</a> {@link ObjectMapper}.
 *
 * <p>
 * This converter can be used to bind to typed beans, or untyped {@code HashMap}
 * instances.
 *
 * <p>
 * By default, this converter supports {@code application/json} and
 * {@code application/*+json} with {@code UTF-8} character set. This can be
 * overridden by setting the {@link #setSupportedMediaTypes supportedMediaTypes}
 * property.
 *
 * <p>
 * The default constructor uses the default configuration provided by
 * {@link Jackson2ObjectMapperBuilder}.
 *
 * <p>
 * Compatible with Jackson 2.9 and higher, as of Spring 5.0.
 */
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
   * 
   * @param objectMapper the ObjectMapper
   */
  public PageMappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
    super(objectMapper);
  }

  /**
   * Add default headers to the output message.
   * <p>
   * This implementation delegates to {@link #getDefaultContentType(Object)} if a
   * content type was not provided, set if necessary the default character set,
   * calls {@link #getContentLength}, and sets the corresponding headers.
   * 
   * @param headers     the HttpHeaders
   * @param t           the Object
   * @param contentType the MediaType
   * @throws IOException if a problem occurs
   */
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

  /**
   * Method that writes the actual body. Invoked from {@link #write}.
   * 
   * @param object        the object to write to the output message
   * @param type          the type of object to write (may be {@code null})
   * @param outputMessage the HTTP output message to write to
   * @throws IOException                     in case of I/O errors
   * @throws HttpMessageNotWritableException in case of conversion errors
   */
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

  /**
   * Get the header name used for page number.
   * 
   * @return String the header name used for page number
   */
  public String getPageHeader() {
    if (pageHeader != null) {
      return pageHeader;
    }
    return DEFAULT_PAGE_HEADER;
  }

  /**
   * Get the header name used for page size.
   * 
   * @return String the header name used for page size
   */
  public String getPageSizeHeader() {
    if (pageSizeHeader != null) {
      return pageSizeHeader;
    }
    return DEFAIÑT_PAGE_SIZE_HEADER;
  }

  /**
   * Get the header name used for page count.
   * 
   * @return String the header name used for page count
   */
  public String getPageCountHeader() {
    if (pageCountHeader != null) {
      return pageCountHeader;
    }
    return DEFAULT_PAGE_COUNT_HEADER;
  }

  /**
   * Get the header name used for total page count.
   * 
   * @return String the header name used for total page count
   */
  public String getPageTotalCountHeader() {
    if (pageTotalCountHeader != null) {
      return pageTotalCountHeader;
    }
    return DEFAULT_PAGE_TOTAL_COUNT_HEADER;
  }

  /**
   * Get the header name used for total count.
   * 
   * @return String the header name used for total count
   */
  public String getTotalCountHeader() {
    if (totalCountHeader != null) {
      return totalCountHeader;
    }
    return DEFAULT_TOTAL_COUNT_HEADER;
  }

  /**
   * Set the header name used for page number.
   * 
   * @param pageHeader the header name used for page number
   */
  public void setPageHeader(String pageHeader) {
    this.pageHeader = pageHeader;
  }

  /**
   * Set the header name used for page size.
   * 
   * @param pageSizeHeader the header name used for page size
   */
  public void setPageSizeHeader(String pageSizeHeader) {
    this.pageSizeHeader = pageSizeHeader;
  }

  /**
   * Set the header name used for page count.
   * 
   * @param pageCountHeader the header name used for page count
   */
  public void setPageCountHeader(String pageCountHeader) {
    this.pageCountHeader = pageCountHeader;
  }

  /**
   * Set the header name used for total page count.
   * 
   * @param pageTotalCountHeader the header name used for total page count
   */
  public void setPageTotalCountHeader(String pageTotalCountHeader) {
    this.pageTotalCountHeader = pageTotalCountHeader;
  }

  /**
   * Set the header name used for total count.
   * 
   * @param totalCountHeader the header name used for total count
   */
  public void setTotalCountHeader(String totalCountHeader) {
    this.totalCountHeader = totalCountHeader;
  }

}