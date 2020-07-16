package org.crue.hercules.sgi.framework.keycloak.adapters.springsecurity.config;

import org.crue.hercules.sgi.framework.keycloak.adapters.SgiKeycloakSpringBootConfigResolver;
import org.crue.hercules.sgi.framework.keycloak.representations.adpaters.config.SgiKeycloakSpringBootProperties;
import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

public class SgiKeycloakWebSecurityConfigurerAdapter extends KeycloakWebSecurityConfigurerAdapter {

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(keycloakAuthenticationProvider());
  }

  @Override
  protected KeycloakAuthenticationProvider keycloakAuthenticationProvider() {
    KeycloakAuthenticationProvider keycloakAuthenticationProvider = super.keycloakAuthenticationProvider();
    keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(grantedAuthoritiesMapper());
    return keycloakAuthenticationProvider;
  }

  protected GrantedAuthoritiesMapper grantedAuthoritiesMapper() {
    SimpleAuthorityMapper simpleAuthorityMapper = new SimpleAuthorityMapper();
    simpleAuthorityMapper.setConvertToUpperCase(true);
    simpleAuthorityMapper.setPrefix("");
    return simpleAuthorityMapper;
  }

  @Bean
  public KeycloakConfigResolver keycloakConfigResolver(SgiKeycloakSpringBootProperties properties) {
    return new SgiKeycloakSpringBootConfigResolver(properties);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    super.configure(http);
    // Uncomment .logout().disable() to disable logout endpoint (/sso/logout)
    http.cors().and()// .logout().disable()
        .csrf().disable().authorizeRequests().antMatchers("/error").permitAll().antMatchers("/**").authenticated()
        .anyRequest().denyAll();
  }

  @Override
  protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
    return new NullAuthenticatedSessionStrategy();
  }
}
