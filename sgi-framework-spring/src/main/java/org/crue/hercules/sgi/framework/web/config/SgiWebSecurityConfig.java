package org.crue.hercules.sgi.framework.web.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.crue.hercules.sgi.framework.security.web.SgiAuthenticationEntryPoint;
import org.crue.hercules.sgi.framework.security.web.access.SgiAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.stereotype.Component;

@Component
public class SgiWebSecurityConfig extends WebSecurityConfigurerAdapter {
  @Autowired
  private ObjectMapper mapper;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.exceptionHandling().accessDeniedHandler(accessDeniedHandler())
        .authenticationEntryPoint(authenticationEntryPoint()).and()
        // CSRF protection by cookie
        .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and()
        // Require authentication for all requests except for error
        .authorizeRequests().antMatchers("/error").permitAll().antMatchers("/**").authenticated().and()
        // Validate tokens through configured OpenID Provider
        .oauth2ResourceServer().jwt().jwtAuthenticationConverter(jwtAuthenticationConverter());
  }

  protected JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    // Convert realm_access.roles claims to granted authorities, for use in access
    // decisions
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new Converter<Jwt, Collection<GrantedAuthority>>() {
      @Override
      @SuppressWarnings("unchecked")
      public Collection<GrantedAuthority> convert(final Jwt jwt) {
        final Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");
        return ((List<String>) realmAccess.get("roles")).stream().map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
      }
    });
    return jwtAuthenticationConverter;
  }

  protected AccessDeniedHandler accessDeniedHandler() {
    return new SgiAccessDeniedHandler(mapper);
  }

  protected AuthenticationEntryPoint authenticationEntryPoint() {
    return new SgiAuthenticationEntryPoint(mapper);
  }
}
