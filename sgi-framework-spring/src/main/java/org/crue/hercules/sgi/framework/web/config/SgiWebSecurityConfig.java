package org.crue.hercules.sgi.framework.web.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.framework.security.oauth2.client.oicd.userinfo.KeycloakOidcUserService;
import org.crue.hercules.sgi.framework.security.oauth2.server.resource.authentication.SgiJwtAuthenticationConverter;
import org.crue.hercules.sgi.framework.security.web.authentication.logout.KeycloakLogoutHandler;
import org.crue.hercules.sgi.framework.security.web.exception.handler.WebSecurityExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * A Configuration bean that configures {@link HttpSecurity}
 * based on configuration properties.
 */
@Configuration
@Order(SecurityProperties.BASIC_AUTH_ORDER)
@Slf4j
public class SgiWebSecurityConfig {
  @Value("${spring.security.oauth2.enable-login:false}")
  private boolean loginEnabled;

  @Value("${spring.security.oauth2.resourceserver.jwt.user-name-claim:sub}")
  private String userNameClaim;

  @Value("${spring.security.csrf.enable:true}")
  private boolean csrfEnabled;

  @Value("${spring.security.csrf.cookie-path:/}")
  private String csrfCookiePath;

  @Value("${spring.security.frameoptions.enable:true}")
  private boolean frameoptionsEnabled;

  private JwtDecoder jwtDecoder;

  private WebSecurityExceptionHandler webSecurityExceptionHandler;

  @Autowired
  public void setJwtDecoder(JwtDecoder jwtDecoder) {
    this.jwtDecoder = jwtDecoder;
  }

  @Autowired(required = false)
  public void setWebSecurityExceptionHandler(WebSecurityExceptionHandler webSecurityExceptionHandler) {
    this.webSecurityExceptionHandler = webSecurityExceptionHandler;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    log.debug("configure(HttpSecurity http) - start");
    // @formatter:off
    http
      // Configure session management as a basis for a classic, server side rendered application
      .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
      // Require authentication for all requests except for error, health checks and public endpoints
      .authorizeHttpRequests(authz -> 
        authz.antMatchers("/error", "/actuator/health/liveness", "/actuator/health/readiness").permitAll()
        .antMatchers("/public/**", "/config/time-zone").permitAll()
        .antMatchers("/**").authenticated()
      )
      // Validate tokens through configured OpenID Provider
      .oauth2ResourceServer(ors -> 
        ors.jwt().jwtAuthenticationConverter(jwtAuthenticationConverter())
      );
    // @formatter:on

    if (csrfEnabled) {
      // @formatter:off
      http
        // CSRF protection by cookie
        .csrf(csrf -> csrf.csrfTokenRepository(csrfTokenRepository()));
      // @formatter:on
    } else {
      // @formatter:off
      http
        // CSRF protection disabled
        .csrf(csrf -> csrf.disable());
      // @formatter:on
    }

    if (!frameoptionsEnabled) {
      // @formatter:off
      http
        // Disable X-Frame-Options
        .headers( h -> h.frameOptions().disable());
      // @formatter:on
    }

    if (loginEnabled) {
      // @formatter:off
      http
        // This is the point where OAuth2 login of Spring 5 gets enabled
        .oauth2Login(ol -> ol.userInfoEndpoint().oidcUserService(keycloakOidcUserService()))
        // Propagate logouts via /logout to Keycloak
        .logout(l -> l.addLogoutHandler(keycloakLogoutHandler()));
      // @formatter:on
    }

    if (webSecurityExceptionHandler != null) {
      if (loginEnabled) {
        http
            // Handle Spring Security exceptions
            .exceptionHandling(ah -> ah.accessDeniedHandler(webSecurityExceptionHandler));
      } else {
        http
            // Handle Spring Security exceptions
            .exceptionHandling(ah -> ah.accessDeniedHandler(webSecurityExceptionHandler)
                .authenticationEntryPoint(webSecurityExceptionHandler));
      }
    }
    log.debug("configure(HttpSecurity http) - end");
    return http.build();
  }

  /**
   * Creates the {@link CsrfTokenRepository} that stores the CSRF token in a
   * cookie readable by JavaScript (so the SPA can echo it back in the
   * {@code X-XSRF-TOKEN} header).
   * <p>
   * The cookie path is forced to the value of the
   * {@code spring.security.csrf.cookie-path} property (defaults to {@code /})
   * instead of letting {@link CookieCsrfTokenRepository} derive it from the
   * servlet context path. When several services are served under different path
   * prefixes on the same host (e.g. {@code /api/csp} behind an ingress that does
   * not rewrite the prefix, forcing a {@code server.servlet.context-path}),
   * deriving the path from the context produces {@code XSRF-TOKEN} cookies scoped
   * to different paths. The browser then keeps several cookies with the same name
   * and may send a different one than the server validates against, resulting in
   * spurious {@code 403 Invalid CSRF Token} errors. A fixed path collapses them
   * into a single cookie. It remains configurable in case a deployment needs a
   * different scope.
   *
   * @return the {@link CsrfTokenRepository}
   */
  protected CsrfTokenRepository csrfTokenRepository() {
    log.debug("csrfTokenRepository() - start");
    CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
    csrfTokenRepository.setCookiePath(csrfCookiePath);
    log.debug("csrfTokenRepository() - end");
    return csrfTokenRepository;
  }

  /**
   * Creates new {@link KeycloakOidcUserService}.
   *
   * @return the {@link KeycloakOidcUserService}
   */
  protected OAuth2UserService<OidcUserRequest, OidcUser> keycloakOidcUserService() {
    log.debug("keycloakOidcUserService() - start");
    OAuth2UserService<OidcUserRequest, OidcUser> returnValue = new KeycloakOidcUserService(jwtDecoder,
        jwtAuthenticationConverter());
    log.debug("keycloakOidcUserService() - start");
    return returnValue;
  }

  /**
   * Creates a new {@link SgiJwtAuthenticationConverter}
   * 
   * @return the {@link SgiJwtAuthenticationConverter}
   */
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

  /**
   * Creates a {@link Converter} to create a {@link GrantedAuthority} collection
   * from a JSON Web Token.
   * 
   * @return the {@link Converter}
   */
  protected Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter() {
    log.debug("jwtGrantedAuthoritiesConverter() - start");
    Converter<Jwt, Collection<GrantedAuthority>> returnValue = new Converter<Jwt, Collection<GrantedAuthority>>() {
      private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());

      @Override
      @SuppressWarnings("unchecked")
      public Collection<GrantedAuthority> convert(final Jwt jwt) {
        log.debug("convert(final Jwt jwt) - start");
        final Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");
        if (realmAccess == null) {
          Collection<GrantedAuthority> returnValue = new ArrayList<>(
              Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
          String scopes = (String) jwt.getClaims().get("scope");
          if (scopes != null) {
            List<String> scopeList = Arrays.asList(scopes.split(" "));
            returnValue.addAll(scopeList.stream().map(scope -> new SimpleGrantedAuthority("SCOPE_" + scope))
                .collect(Collectors.toList()));
          }

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

  /**
   * Custom {@link KeycloakLogoutHandler} to propagete logout from application to
   * Keycloak.
   * 
   * @return the {@link KeycloakLogoutHandler}
   */
  protected KeycloakLogoutHandler keycloakLogoutHandler() {
    log.debug("keycloakLogoutHandler() - start");
    KeycloakLogoutHandler returnValue = new KeycloakLogoutHandler(new RestTemplate());
    log.debug("keycloakLogoutHandler() - end");
    return returnValue;
  }
}
