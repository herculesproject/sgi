package org.crue.hercules.sgi.framework.web.config;

import java.util.List;

import org.crue.hercules.sgi.framework.core.convert.converter.QueryCriteriaConverter;
import org.crue.hercules.sgi.framework.core.convert.converter.SortCriteriaConverter;
import org.crue.hercules.sgi.framework.http.converter.json.PageMappingJackson2HttpMessageConverter;
import org.crue.hercules.sgi.framework.web.controller.SgiErrorController;
import org.crue.hercules.sgi.framework.web.method.annotation.RequestPageableArgumentResolver;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

/**
 * Defines callback methods to customize the Java-based configuration for Spring
 * MVC enabled via {@code @EnableWebMvc}.
 */
@Configuration
public class SgiWebConfig implements WebMvcConfigurer {
  private static QueryCriteriaConverter queryOperationConverter = new QueryCriteriaConverter();
  private static SortCriteriaConverter sortOperationConverter = new SortCriteriaConverter();
  private static RequestPageableArgumentResolver requestPageableArgumentResolver = new RequestPageableArgumentResolver(
      sortOperationConverter);

  /**
   * Configure cross origin requests processing.
   * 
   * @param registry {@link CorsRegistry}
   */
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**").allowedMethods("GET", "POST", "PUT", "DELETE").exposedHeaders(
        PageMappingJackson2HttpMessageConverter.DEFAULT_PAGE_HEADER,
        PageMappingJackson2HttpMessageConverter.DEFAIÑT_PAGE_SIZE_HEADER,
        PageMappingJackson2HttpMessageConverter.DEFAULT_PAGE_COUNT_HEADER,
        PageMappingJackson2HttpMessageConverter.DEFAULT_PAGE_TOTAL_COUNT_HEADER,
        PageMappingJackson2HttpMessageConverter.DEFAULT_TOTAL_COUNT_HEADER);
  }

  /**
   * Configure the {@link HttpMessageConverter HttpMessageConverters} to use for
   * reading or writing to the body of the request or response. If no converters
   * are added, a default list of converters is registered.
   * <p>
   * <strong>Note</strong> that adding converters to the list, turns off default
   * converter registration. To simply add a converter without impacting default
   * registration, consider using the method
   * {@link #extendMessageConverters(java.util.List)} instead.
   * 
   * @param converters initially an empty list of converters
   */
  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    for (HttpMessageConverter<?> httpMessageConverter : converters) {
      if (httpMessageConverter instanceof MappingJackson2HttpMessageConverter) {
        // Override all MappingJackson2HttpMessageConverter with custom
        // PageMappingJackson2HttpMessageConverter
        // One is created by WebMvcConfigurationSupport
        // One is created by AllEncompassingFormHttpMessageConverter
        MappingJackson2HttpMessageConverter converter = (MappingJackson2HttpMessageConverter) httpMessageConverter;
        PageMappingJackson2HttpMessageConverter newConverter = new PageMappingJackson2HttpMessageConverter(
            converter.getObjectMapper());
        converters.set(converters.indexOf(converter), newConverter);
      }
    }
  }

  /**
   * Add {@link Converter Converters} and {@link Formatter Formatters} in addition
   * to the ones registered by default.
   *
   * @param registry {@link FormatterRegistry}
   */
  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addConverter(queryOperationConverter);
    registry.addConverter(sortOperationConverter);
  }

  /**
   * Add resolvers to support custom controller method argument types.
   * <p>
   * This does not override the built-in support for resolving handler method
   * arguments. To customize the built-in support for argument resolution,
   * configure {@link RequestMappingHandlerAdapter} directly.
   * 
   * @param resolvers initially an empty list
   */
  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(requestPageableArgumentResolver);
  }

  /**
   * Registers a custom JSON {@link ErrorController}
   * 
   * @return SgiErrorController {@link SgiErrorController}
   */
  @Bean
  public SgiErrorController sgiErrorController() {
    return new SgiErrorController();
  }

}