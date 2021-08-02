package org.crue.hercules.sgi.sgdoc.config;

import org.crue.hercules.sgi.framework.web.config.SgiWebConfig;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SgiWebConfig Fw
 * 
 * Framework Web configuration.
 */
@Configuration
public class WebConfig extends SgiWebConfig {

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }

}
