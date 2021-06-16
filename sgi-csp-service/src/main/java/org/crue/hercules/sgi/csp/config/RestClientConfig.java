package org.crue.hercules.sgi.csp.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Creación de los clientes de acceso al API rest de otros módulos.
 */
@Configuration
public class RestClientConfig {
  /**
   * RestTemplate usado para el acceso al API rest de otros módulos.
   * 
   * @param restTemplateBuilder
   * @return
   */
  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
    return restTemplateBuilder.build();
  }
}
