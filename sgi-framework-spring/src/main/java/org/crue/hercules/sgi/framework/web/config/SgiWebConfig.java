package org.crue.hercules.sgi.framework.web.config;

import java.util.List;

import org.crue.hercules.sgi.framework.core.convert.converter.QueryCriteriaConverter;
import org.crue.hercules.sgi.framework.core.convert.converter.SortCriteriaConverter;
import org.crue.hercules.sgi.framework.http.converter.json.PageMappingJackson2HttpMessageConverter;
import org.crue.hercules.sgi.framework.web.controller.SgiErrorController;
import org.crue.hercules.sgi.framework.web.method.annotation.RequestPageableArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SgiWebConfig implements WebMvcConfigurer {
  private static QueryCriteriaConverter queryOperationConverter = new QueryCriteriaConverter();
  private static SortCriteriaConverter sortOperationConverter = new SortCriteriaConverter();
  private static RequestPageableArgumentResolver requestPageableArgumentResolver = new RequestPageableArgumentResolver(
      sortOperationConverter);

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**").allowedMethods("GET", "POST", "PUT", "DELETE").exposedHeaders(
        PageMappingJackson2HttpMessageConverter.DEFAULT_PAGE_HEADER,
        PageMappingJackson2HttpMessageConverter.DEFAIÑT_PAGE_SIZE_HEADER,
        PageMappingJackson2HttpMessageConverter.DEFAULT_PAGE_COUNT_HEADER,
        PageMappingJackson2HttpMessageConverter.DEFAULT_PAGE_TOTAL_COUNT_HEADER,
        PageMappingJackson2HttpMessageConverter.DEFAULT_TOTAL_COUNT_HEADER);
  }

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

  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addConverter(queryOperationConverter);
    registry.addConverter(sortOperationConverter);
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(requestPageableArgumentResolver);
  }

  @Bean
  public SgiErrorController sgiErrorController() {
    return new SgiErrorController();
  }

}