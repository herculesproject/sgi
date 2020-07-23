package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.CargoComite;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.EvaluadorEvaluacion;
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
 * Test de integracion de EvaluadorEvaluacion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("SECURITY_MOCK")
public class EvaluadorEvaluacionIT {

  @Autowired
  private TestRestTemplate restTemplate;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String EVALUADOR_EVALUACION_CONTROLLER_BASE_PATH = "/evaluadorevaluaciones";

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
          .authorities("ETI-EVALUADOREVALUACION-EDITAR", "ETI-EVALUADOREVALUACION-VER");
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
  public void getEvaluadorEvaluacion_WithId_ReturnsEvaluadorEvaluacion() throws Exception {
    final ResponseEntity<EvaluadorEvaluacion> response = restTemplate.withBasicAuth("user", "secret")
        .getForEntity(EVALUADOR_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, EvaluadorEvaluacion.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final EvaluadorEvaluacion EvaluadorEvaluacion = response.getBody();

    Assertions.assertThat(EvaluadorEvaluacion.getId()).isEqualTo(1L);
    Assertions.assertThat(EvaluadorEvaluacion.getEvaluador().getResumen()).isEqualTo("Evaluador1");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addEvaluadorEvaluacion_ReturnsEvaluadorEvaluacion() throws Exception {

    EvaluadorEvaluacion nuevoEvaluadorEvaluacion = generarMockEvaluadorEvaluacion(null);

    restTemplate.postForEntity(EVALUADOR_EVALUACION_CONTROLLER_BASE_PATH, nuevoEvaluadorEvaluacion,
        EvaluadorEvaluacion.class);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeEvaluadorEvaluacion_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<EvaluadorEvaluacion> response = restTemplate.withBasicAuth("user", "secret").exchange(
        EVALUADOR_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, null,
        EvaluadorEvaluacion.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeEvaluadorEvaluacion_DoNotGetEvaluadorEvaluacion() throws Exception {
    restTemplate.delete(EVALUADOR_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L);

    final ResponseEntity<EvaluadorEvaluacion> response = restTemplate.withBasicAuth("user", "secret")
        .getForEntity(EVALUADOR_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, EvaluadorEvaluacion.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceEvaluadorEvaluacion_ReturnsEvaluadorEvaluacion() throws Exception {

    EvaluadorEvaluacion replaceEvaluadorEvaluacion = generarMockEvaluadorEvaluacion(1L);

    final HttpEntity<EvaluadorEvaluacion> requestEntity = new HttpEntity<EvaluadorEvaluacion>(
        replaceEvaluadorEvaluacion, new HttpHeaders());

    final ResponseEntity<EvaluadorEvaluacion> response = restTemplate.withBasicAuth("user", "secret").exchange(

        EVALUADOR_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, requestEntity,
        EvaluadorEvaluacion.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final EvaluadorEvaluacion evaluadorEvaluacion = response.getBody();

    Assertions.assertThat(evaluadorEvaluacion.getId()).isNotNull();
    Assertions.assertThat(evaluadorEvaluacion.getEvaluador()).isEqualTo(replaceEvaluadorEvaluacion.getEvaluador());
    Assertions.assertThat(evaluadorEvaluacion.getEvaluacion()).isEqualTo(replaceEvaluadorEvaluacion.getEvaluacion());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsEvaluadorEvaluacionSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    URI uri = UriComponentsBuilder.fromUriString(EVALUADOR_EVALUACION_CONTROLLER_BASE_PATH).build(false).toUri();

    final ResponseEntity<List<EvaluadorEvaluacion>> response = restTemplate.withBasicAuth("user", "secret").exchange(
        uri, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<EvaluadorEvaluacion>>() {
        });

    // then: Respuesta OK, EvaluadorEvaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<EvaluadorEvaluacion> EvaluadorEvaluaciones = response.getBody();
    Assertions.assertThat(EvaluadorEvaluaciones.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("8");

    // Contiene de id='6' a '8'
    Assertions.assertThat(EvaluadorEvaluaciones.get(0).getId()).isEqualTo(6);
    Assertions.assertThat(EvaluadorEvaluaciones.get(1).getId()).isEqualTo(7);
    Assertions.assertThat(EvaluadorEvaluaciones.get(2).getId()).isEqualTo(8);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredEvaluadorEvaluacionList() throws Exception {
    // when: Búsqueda por resumen like e id equals
    Long id = 5L;
    String query = "id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(EVALUADOR_EVALUACION_CONTROLLER_BASE_PATH).queryParam("q", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<EvaluadorEvaluacion>> response = restTemplate.withBasicAuth("user", "secret")
        .exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<List<EvaluadorEvaluacion>>() {
        });

    // then: Respuesta OK, EvaluadorEvaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<EvaluadorEvaluacion> EvaluadorEvaluaciones = response.getBody();
    Assertions.assertThat(EvaluadorEvaluaciones.size()).isEqualTo(1);
    Assertions.assertThat(EvaluadorEvaluaciones.get(0).getId()).isEqualTo(id);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedEvaluadorEvaluacionList() throws Exception {
    // when: Ordenación por id desc
    String query = "id-";

    URI uri = UriComponentsBuilder.fromUriString(EVALUADOR_EVALUACION_CONTROLLER_BASE_PATH).queryParam("s", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<EvaluadorEvaluacion>> response = restTemplate.withBasicAuth("user", "secret")
        .exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<List<EvaluadorEvaluacion>>() {
        });

    // then: Respuesta OK, EvaluadorEvaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<EvaluadorEvaluacion> EvaluadorEvaluaciones = response.getBody();
    Assertions.assertThat(EvaluadorEvaluaciones.size()).isEqualTo(8);
    for (int i = 0; i < 8; i++) {
      EvaluadorEvaluacion evaluadorEvaluacion = EvaluadorEvaluaciones.get(i);
      Assertions.assertThat(evaluadorEvaluacion.getId()).isEqualTo(8 - i);
    }
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsEvaluadorEvaluacionSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "1");
    // when: Ordena por resumen desc
    String sort = "id-";
    // when: Filtra por id equals
    Long id = 5L;
    String filter = "id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(EVALUADOR_EVALUACION_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<EvaluadorEvaluacion>> response = restTemplate.withBasicAuth("user", "secret").exchange(
        uri, HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<EvaluadorEvaluacion>>() {
        });

    // then: Respuesta OK, EvaluadorEvaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<EvaluadorEvaluacion> EvaluadorEvaluaciones = response.getBody();
    Assertions.assertThat(EvaluadorEvaluaciones.size()).isEqualTo(1);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("1");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("1");

    // Contiene id='1'
    Assertions.assertThat(EvaluadorEvaluaciones.get(0).getId()).isEqualTo(5);
  }

  /**
   * Función que devuelve un objeto EvaluadorEvaluacion
   * 
   * @param id id del EvaluadorEvaluacion
   * @return el objeto EvaluadorEvaluacion
   */

  public EvaluadorEvaluacion generarMockEvaluadorEvaluacion(Long id) {
    CargoComite cargoComite = new CargoComite();
    cargoComite.setId(1L);
    cargoComite.setNombre("CargoComite1");
    cargoComite.setActivo(Boolean.TRUE);

    Comite comite = new Comite(1L, "Comite1", Boolean.TRUE);

    Evaluador evaluador = new Evaluador();
    evaluador.setId(1L);
    evaluador.setCargoComite(cargoComite);
    evaluador.setComite(comite);
    evaluador.setFechaAlta(LocalDate.now());
    evaluador.setFechaBaja(LocalDate.now());
    evaluador.setResumen("Evaluador");
    evaluador.setUsuarioRef("user-001");
    evaluador.setActivo(Boolean.TRUE);

    Evaluacion evaluacion = new Evaluacion();
    evaluacion.setId(id);

    EvaluadorEvaluacion evaluadorEvaluacion = new EvaluadorEvaluacion(id, evaluador, evaluacion);

    return evaluadorEvaluacion;
  }

}