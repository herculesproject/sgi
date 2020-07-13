package org.crue.hercules.sgi.framework.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.crue.hercules.sgi.framework.security.web.SgiAuthenticationEntryPoint;
import org.crue.hercules.sgi.framework.security.web.access.SgiAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@EnableWebSecurity
public class SgiWebSecurityConfig extends WebSecurityConfigurerAdapter {
  @Autowired
  AccessDeniedHandler accessDeniedHandler;

  @Autowired
  AuthenticationEntryPoint authenticationEntryPoint;

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