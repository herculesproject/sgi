package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Configuracion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.crue.hercules.sgi.framework.security.web.SgiAuthenticationEntryPoint;
import org.crue.hercules.sgi.framework.security.web.access.SgiAccessDeniedHandler;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test de integracion de Configuracion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("SECURITY_MOCK")
public class ConfiguracionIT {

  @Autowired
  private TestRestTemplate restTemplate;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONFIGURACION_CONTROLLER_BASE_PATH = "/configuraciones";

  @Profile("SECURITY_MOCK") // If we use the SECURITY_MOCK profile, we use this bean!
  @TestConfiguration // Unlike a nested @Configuration class, which would be used instead of your
                     // application’s primary configuration, a nested @TestConfiguration class is
                     // used in addition to your application’s primary configuration.
  static class TestSecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
      PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
      auth.inMemoryAuthentication().passwordEncoder(encoder).withUser("user").password(encoder.encode("secret"))
          .authorities("ETI-CONFIGURACION-EDITAR", "ETI-CONFIGURACION-VER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http.cors().and().csrf().disable().authorizeRequests().antMatchers("/error").permitAll().antMatchers("/**")
          .authenticated().anyRequest().denyAll().and().exceptionHandling().accessDeniedHandler(accessDeniedHandler)
          .authenticationEntryPoint(authenticationEntryPoint).and().httpBasic();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(ObjectMapper mapper) {
      return new SgiAccessDeniedHandler(mapper);
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(ObjectMapper mapper) {
      return new SgiAuthenticationEntryPoint(mapper);
    }
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void getConfiguracion_WithId_ReturnsConfiguracion() throws Exception {
    final ResponseEntity<Configuracion> response = restTemplate.withBasicAuth("user", "secret")
        .getForEntity(CONFIGURACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, Configuracion.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Configuracion configuracion = response.getBody();

    Assertions.assertThat(configuracion.getId()).isEqualTo(1L);
    Assertions.assertThat(configuracion.getClave()).isEqualTo("Configuracion1");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addConfiguracion_ReturnsConfiguracion() throws Exception {

    Configuracion nuevoConfiguracion = new Configuracion();
    nuevoConfiguracion.setClave("Configuracion1");

    restTemplate.withBasicAuth("user", "secret").postForEntity(CONFIGURACION_CONTROLLER_BASE_PATH, nuevoConfiguracion,
        Configuracion.class);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeConfiguracion_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<Configuracion> response = restTemplate.withBasicAuth("user", "secret").exchange(
        CONFIGURACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, null, Configuracion.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeConfiguracion_DoNotGetConfiguracion() throws Exception {
    restTemplate.delete(CONFIGURACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L);

    final ResponseEntity<Configuracion> response = restTemplate.withBasicAuth("user", "secret")
        .getForEntity(CONFIGURACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, Configuracion.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceConfiguracion_ReturnsConfiguracion() throws Exception {

    Configuracion replaceConfiguracion = generarMockConfiguracion(1L, "Configuracion1");

    final HttpEntity<Configuracion> requestEntity = new HttpEntity<Configuracion>(replaceConfiguracion,
        new HttpHeaders());

    final ResponseEntity<Configuracion> response = restTemplate.withBasicAuth("user", "secret").exchange(
        CONFIGURACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, requestEntity, Configuracion.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Configuracion configuracion = response.getBody();

    Assertions.assertThat(configuracion.getId()).isNotNull();
    Assertions.assertThat(configuracion.getClave()).isEqualTo("Configuracion1");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsConfiguracionSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    URI uri = UriComponentsBuilder.fromUriString(CONFIGURACION_CONTROLLER_BASE_PATH).build(false).toUri();

    final ResponseEntity<List<Configuracion>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<Configuracion>>() {
        });

    // then: Respuesta OK, Configuraciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Configuracion> configuraciones = response.getBody();
    Assertions.assertThat(configuraciones.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("8");

    // Contiene de clave='Configuracion6' a 'Configuracion8'
    Assertions.assertThat(configuraciones.get(0).getClave()).isEqualTo("Configuracion6");
    Assertions.assertThat(configuraciones.get(1).getClave()).isEqualTo("Configuracion7");
    Assertions.assertThat(configuraciones.get(2).getClave()).isEqualTo("Configuracion8");

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredConfiguracionList() throws Exception {
    // when: Búsqueda por clave like e id equals
    Long id = 5L;
    String query = "clave~Configuracion%,id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(CONFIGURACION_CONTROLLER_BASE_PATH).queryParam("q", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Configuracion>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, null, new ParameterizedTypeReference<List<Configuracion>>() {
        });

    // then: Respuesta OK, Configuraciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Configuracion> configuraciones = response.getBody();
    Assertions.assertThat(configuraciones.size()).isEqualTo(1);
    Assertions.assertThat(configuraciones.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(configuraciones.get(0).getClave()).startsWith("Configuracion");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedConfiguracionList() throws Exception {
    // when: Ordenación por id desc
    String query = "id-";

    URI uri = UriComponentsBuilder.fromUriString(CONFIGURACION_CONTROLLER_BASE_PATH).queryParam("s", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Configuracion>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, null, new ParameterizedTypeReference<List<Configuracion>>() {
        });

    // then: Respuesta OK, Configuraciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Configuracion> configuraciones = response.getBody();
    Assertions.assertThat(configuraciones.size()).isEqualTo(8);
    for (int i = 0; i < 8; i++) {
      Configuracion configuracion = configuraciones.get(i);
      Assertions.assertThat(configuracion.getId()).isEqualTo(8 - i);
      Assertions.assertThat(configuracion.getClave()).isEqualTo("Configuracion" + String.format("%03d", 8 - i));
    }
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsConfiguracionSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por clave desc
    String sort = "clave-";
    // when: Filtra por clave like
    String filter = "clave~%00%";

    URI uri = UriComponentsBuilder.fromUriString(CONFIGURACION_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<Configuracion>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<Configuracion>>() {
        });

    // then: Respuesta OK, Configuraciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Configuracion> configuraciones = response.getBody();
    Assertions.assertThat(configuraciones.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("3");

    // Contiene clave='Configuracion001', 'Configuracion002','Configuracion003'
    Assertions.assertThat(configuraciones.get(0).getClave()).isEqualTo("Configuracion" + String.format("%03d", 3));
    Assertions.assertThat(configuraciones.get(1).getClave()).isEqualTo("Configuracion" + String.format("%03d", 2));
    Assertions.assertThat(configuraciones.get(2).getClave()).isEqualTo("Configuracion" + String.format("%03d", 1));

  }

  /**
   * Función que devuelve un objeto Configuracion
   * 
   * @param id    id del Configuracion
   * @param clave la clave de la Configuracion
   * @return el objeto Configuracion
   */

  public Configuracion generarMockConfiguracion(Long id, String clave) {

    Configuracion configuracion = new Configuracion();
    configuracion.setId(id);
    configuracion.setClave(clave);
    configuracion.setDescripcion("Descripcion" + id);
    configuracion.setValor("Valor" + id);

    return configuracion;
  }

}