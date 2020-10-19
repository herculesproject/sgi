package org.crue.hercules.sgdoc.config;

import org.crue.hercules.sgi.framework.web.config.SgiWebSecurityConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
public class WebSecurityConfig extends SgiWebSecurityConfig {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // Permite el acceso a todos los endpoints
    http.cors().and().csrf().disable();
    http.authorizeRequests().antMatchers("/").permitAll();
  }
}
