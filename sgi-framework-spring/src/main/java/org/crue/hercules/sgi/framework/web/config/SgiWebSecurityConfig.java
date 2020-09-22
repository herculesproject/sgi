package org.crue.hercules.sgi.framework.web.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.crue.hercules.sgi.framework.security.oauth2.client.oicd.userinfo.KeycloakOidcUserService;
import org.crue.hercules.sgi.framework.security.oauth2.server.resource.authentication.SgiJwtAuthenticationConverter;
import org.crue.hercules.sgi.framework.security.web.SgiAuthenticationEntryPoint;
import org.crue.hercules.sgi.framework.security.web.access.SgiAccessDeniedHandler;
import org.crue.hercules.sgi.framework.security.web.authentication.logout.KeycloakLogoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
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
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SgiWebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Value("${spring.security.oauth2.enable-login:false}")
  private boolean loginEnabled;

  @Value("${spring.security.oauth2.resourceserver.jwt.user-name-claim:sub}")
  private String userNameClaim;

  @Value("${spring.security.csrf.enable:true}")
  private boolean csrfEnabled;

  @Value("${spring.security.frameoptions.enable:true}")
  private boolean frameoptionsEnabled;

  @Autowired
  private ObjectMapper mapper;

  @Autowired
  private JwtDecoder jwtDecoder;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    log.debug("configure(HttpSecurity http) - start");
    // @formatter:off
    http
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
        // Validate tokens through configured OpenID Provider
        .oauth2ResourceServer()
          .jwt()
          .jwtAuthenticationConverter(jwtAuthenticationConverter())
        .and()
      .and();

    // @formatter:on
    if (csrfEnabled) {
      // @formatter:off
      http
        // CSRF protection by cookie
        .csrf()
        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
    // @formatter:on
    } else {
      // @formatter:off
      http
        // CSRF protection disabled
        .csrf()
        .disable();
    }

    // @formatter:on
    if (!frameoptionsEnabled) {
      // @formatter:off
      http
        .headers()
        // Disable X-Frame-Options
        .frameOptions().disable();
    }

    // @formatter:on
    if (loginEnabled) {
      // @formatter:off
      http
          // This is the point where OAuth2 login of Spring 5 gets enabled
          .oauth2Login()
            .userInfoEndpoint()
            .oidcUserService(keycloakOidcUserService())
          .and()
        .and()
          // Propagate logouts via /logout to Keycloak
          .logout()
          .addLogoutHandler(keycloakLogoutHandler());
      // @formatter:on
    } else {
      // @formatter:off
      http
          // Translate exceptions to JSON
          .exceptionHandling()
          .authenticationEntryPoint(authenticationEntryPoint());
    }
    log.debug("configure(HttpSecurity http) - end");
  }

  protected OAuth2UserService<OidcUserRequest, OidcUser> keycloakOidcUserService() {
    log.debug("keycloakOidcUserService() - start");
    OAuth2UserService<OidcUserRequest, OidcUser> returnValue = new KeycloakOidcUserService(jwtDecoder,
        jwtAuthenticationConverter());
    log.debug("keycloakOidcUserService() - start");
    return returnValue;
  }

  protected Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
    log.debug("jwtAuthenticationConverter() - start");
    SgiJwtAuthenticationConverter sgiJwtAuthenticationConverter = new SgiJwtAuthenticationConverter();
    // Convert realm_access.roles claims to granted authorities, for use in access
    // decisions
    sgiJwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter());
    // Specify the JWT claim to be used as username
    sgiJwtAuthenticationConverter.setUserNameClaim(userNameClaim);
    log.debug("jwtAuthenticationConverter() - end");
    return sgiJwtAuthenticationConverter;
  }

  protected Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter() {
    log.debug("jwtGrantedAuthoritiesConverter() - start");
    Converter<Jwt, Collection<GrantedAuthority>> returnValue = new Converter<Jwt, Collection<GrantedAuthority>>() {
      private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Converter.class);

      @Override
      @SuppressWarnings("unchecked")
      public Collection<GrantedAuthority> convert(final Jwt jwt) {
        log.debug("convert(final Jwt jwt) - start");
        final Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");
        if (realmAccess == null) {
          Collection<GrantedAuthority> returnValue = Arrays
              .asList(new GrantedAuthority[] { new SimpleGrantedAuthority("ROLE_USER") });
          log.warn("No realm_acces found in token");
          log.debug("convert(final Jwt jwt) - end");
          return returnValue;
        }
        Collection<GrantedAuthority> returnValue = ((List<String>) realmAccess.get("roles")).stream()
            .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        log.debug("convert(final Jwt jwt) - end");
        return returnValue;
      }
    };
    log.debug("jwtGrantedAuthoritiesConverter() - end");
    return returnValue;
  }

  protected AccessDeniedHandler accessDeniedHandler() {
    log.debug("accessDeniedHandler() - start");
    AccessDeniedHandler returnValue = new SgiAccessDeniedHandler(mapper);
    log.debug("accessDeniedHandler() - end");
    return returnValue;
  }

  protected AuthenticationEntryPoint authenticationEntryPoint() {
    log.debug("authenticationEntryPoint() - start");
    AuthenticationEntryPoint returnValue = new SgiAuthenticationEntryPoint(mapper);
    log.debug("authenticationEntryPoint() - end");
    return returnValue;
  }

  protected KeycloakLogoutHandler keycloakLogoutHandler() {
    log.debug("keycloakLogoutHandler() - start");
    KeycloakLogoutHandler returnValue = new KeycloakLogoutHandler(new RestTemplate());
    log.debug("keycloakLogoutHandler() - end");
    return returnValue;
  }

}
