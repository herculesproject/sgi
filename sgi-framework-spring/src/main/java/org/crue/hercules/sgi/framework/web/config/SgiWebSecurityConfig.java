package org.crue.hercules.sgi.framework.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.crue.hercules.sgi.framework.security.web.SgiAuthenticationEntryPoint;
import org.crue.hercules.sgi.framework.security.web.access.SgiAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
// If you add @EnableWebSecurity Spring Boot's autoconfiguration is disabled
public class SgiWebSecurityConfig extends WebSecurityConfigurerAdapter {
  @Autowired
  private AccessDeniedHandler accessDeniedHandler;

  @Autowired
  private AuthenticationEntryPoint authenticationEntryPoint;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors().and().csrf().disable().authorizeRequests().antMatchers("/error").permitAll().antMatchers("/**")
        .authenticated().anyRequest().denyAll().and().exceptionHandling().accessDeniedHandler(accessDeniedHandler)
        .authenticationEntryPoint(authenticationEntryPoint);
  }

  @Bean
  public AuthenticationEntryPoint authenticationEntryPoint(ObjectMapper mapper) {
    return new SgiAuthenticationEntryPoint(mapper);
  }

  @Bean
  public AccessDeniedHandler accessDeniedHandler(ObjectMapper mapper) {
    return new SgiAccessDeniedHandler(mapper);
  }
}