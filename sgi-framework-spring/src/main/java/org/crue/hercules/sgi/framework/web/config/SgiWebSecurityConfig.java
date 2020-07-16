package org.crue.hercules.sgi.framework.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.crue.hercules.sgi.framework.keycloak.adapters.springsecurity.config.SgiKeycloakWebSecurityConfigurerAdapter;
import org.crue.hercules.sgi.framework.security.web.access.SgiAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
// If you add @EnableWebSecurity Spring Boot's autoconfiguration is disabled
public class SgiWebSecurityConfig extends SgiKeycloakWebSecurityConfigurerAdapter {
  @Autowired
  private AccessDeniedHandler accessDeniedHandler;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    super.configure(http);
    http.exceptionHandling().accessDeniedHandler(accessDeniedHandler);
  }

  @Bean
  public AccessDeniedHandler accessDeniedHandler(ObjectMapper mapper) {
    return new SgiAccessDeniedHandler(mapper);
  }
}