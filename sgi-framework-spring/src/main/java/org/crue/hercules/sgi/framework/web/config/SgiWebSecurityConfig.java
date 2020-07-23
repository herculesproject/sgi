package org.crue.hercules.sgi.framework.web.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.crue.hercules.sgi.framework.security.oauth2.client.oicd.userinfo.KeycloakOidcUserService;
import org.crue.hercules.sgi.framework.security.web.SgiAuthenticationEntryPoint;
import org.crue.hercules.sgi.framework.security.web.access.SgiAccessDeniedHandler;
import org.crue.hercules.sgi.framework.security.web.authentication.logout.KeycloakLogoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class SgiWebSecurityConfig extends WebSecurityConfigurerAdapter {
  @Autowired
  private ObjectMapper mapper;

  @Autowired
  private JwtDecoder jwtDecoder;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // @formatter:off
    http
        // CSRF protection by cookie
        .csrf()
        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
      .and()
        // Translate exceptions to JSON
        .exceptionHandling()
        .accessDeniedHandler(accessDeniedHandler())
      .and()
        // Configure session management as a basis for a classic, server side rendered application
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
      .and()
        // Require authentication for all requests except for error
        .authorizeRequests()
        .antMatchers("/error").permitAll()
        .antMatchers("/**").authenticated()
      .and()
        // Propagate logouts via /logout to Keycloak
        .logout()
        .addLogoutHandler(keycloakLogoutHandler())
      .and()
        // This is the point where OAuth2 login of Spring 5 gets enabled
        .oauth2Login()
          .userInfoEndpoint()
          .oidcUserService(keycloakOidcUserService())
        .and()
      .and()
        // Validate tokens through configured OpenID Provider
        .oauth2ResourceServer()
          .jwt()
          .jwtAuthenticationConverter(jwtAuthenticationConverter())
        .and()
      .and();
    // @formatter:on
  }

  protected OAuth2UserService<OidcUserRequest, OidcUser> keycloakOidcUserService() {
    return new KeycloakOidcUserService(jwtDecoder, jwtAuthenticationConverter());
  }

  protected JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    // Convert realm_access.roles claims to granted authorities, for use in access
    // decisions
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter());
    return jwtAuthenticationConverter;
  }

  protected Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter() {
    return new Converter<Jwt, Collection<GrantedAuthority>>() {
      @Override
      @SuppressWarnings("unchecked")
      public Collection<GrantedAuthority> convert(final Jwt jwt) {
        final Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");
        return ((List<String>) realmAccess.get("roles")).stream().map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
      }
    };
  }

  protected AccessDeniedHandler accessDeniedHandler() {
    return new SgiAccessDeniedHandler(mapper);
  }

  protected AuthenticationEntryPoint authenticationEntryPoint() {
    return new SgiAuthenticationEntryPoint(mapper);
  }

  protected KeycloakLogoutHandler keycloakLogoutHandler() {
    return new KeycloakLogoutHandler(new RestTemplate());
  }

}
