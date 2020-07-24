package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
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
 * Test de integracion de TipoEvaluacion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("SECURITY_MOCK")
public class TipoEvaluacionIT {

  @Autowired
  private TestRestTemplate restTemplate;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String TIPO_EVALUACION_CONTROLLER_BASE_PATH = "/tipoEvaluaciones";

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
          .authorities("ETI-TIPOEVALUACION-EDITAR", "ETI-TIPOEVALUACION-VER");
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
  public void getTipoEvaluacion_WithId_ReturnsTipoEvaluacion() throws Exception {
    final ResponseEntity<TipoEvaluacion> response = restTemplate.withBasicAuth("user", "secret")
        .getForEntity(TIPO_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, TipoEvaluacion.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final TipoEvaluacion tipoEvaluacion = response.getBody();

    Assertions.assertThat(tipoEvaluacion.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoEvaluacion.getNombre()).isEqualTo("TipoEvaluacion1");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addTipoEvaluacion_ReturnsTipoEvaluacion() throws Exception {

    TipoEvaluacion nuevoTipoEvaluacion = new TipoEvaluacion();
    nuevoTipoEvaluacion.setNombre("TipoEvaluacion1");
    nuevoTipoEvaluacion.setActivo(Boolean.TRUE);

    restTemplate.withBasicAuth("user", "secret").postForEntity(TIPO_EVALUACION_CONTROLLER_BASE_PATH,
        nuevoTipoEvaluacion, TipoEvaluacion.class);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeTipoEvaluacion_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<TipoEvaluacion> response = restTemplate.withBasicAuth("user", "secret").exchange(
        TIPO_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, null, TipoEvaluacion.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeTipoEvaluacion_DoNotGetTipoEvaluacion() throws Exception {
    restTemplate.withBasicAuth("user", "secret").delete(TIPO_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L);

    final ResponseEntity<TipoEvaluacion> response = restTemplate.withBasicAuth("user", "secret")
        .getForEntity(TIPO_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, TipoEvaluacion.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceTipoEvaluacion_ReturnsTipoEvaluacion() throws Exception {

    TipoEvaluacion replaceTipoEvaluacion = generarMockTipoEvaluacion(1L, "TipoEvaluacion1");

    final HttpEntity<TipoEvaluacion> requestEntity = new HttpEntity<TipoEvaluacion>(replaceTipoEvaluacion,
        new HttpHeaders());

    final ResponseEntity<TipoEvaluacion> response = restTemplate.withBasicAuth("user", "secret").exchange(

        TIPO_EVALUACION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, requestEntity, TipoEvaluacion.class,
        1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final TipoEvaluacion tipoEvaluacion = response.getBody();

    Assertions.assertThat(tipoEvaluacion.getId()).isNotNull();
    Assertions.assertThat(tipoEvaluacion.getNombre()).isEqualTo(replaceTipoEvaluacion.getNombre());
    Assertions.assertThat(tipoEvaluacion.getActivo()).isEqualTo(replaceTipoEvaluacion.getActivo());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsTipoEvaluacionSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    URI uri = UriComponentsBuilder.fromUriString(TIPO_EVALUACION_CONTROLLER_BASE_PATH).build(false).toUri();

    final ResponseEntity<List<TipoEvaluacion>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<TipoEvaluacion>>() {
        });

    // then: Respuesta OK, TipoEvaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoEvaluacion> tipoEvaluaciones = response.getBody();
    Assertions.assertThat(tipoEvaluaciones.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("8");

    // Contiene de nombre='TipoEvaluacion6' a 'TipoEvaluacion8'
    Assertions.assertThat(tipoEvaluaciones.get(0).getNombre()).isEqualTo("TipoEvaluacion6");
    Assertions.assertThat(tipoEvaluaciones.get(1).getNombre()).isEqualTo("TipoEvaluacion7");
    Assertions.assertThat(tipoEvaluaciones.get(2).getNombre()).isEqualTo("TipoEvaluacion8");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredTipoEvaluacionList() throws Exception {
    // when: Búsqueda por nombre like e id equals
    Long id = 5L;
    String query = "nombre~TipoEvaluacion%,id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(TIPO_EVALUACION_CONTROLLER_BASE_PATH).queryParam("q", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoEvaluacion>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, null, new ParameterizedTypeReference<List<TipoEvaluacion>>() {
        });

    // then: Respuesta OK, TipoEvaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoEvaluacion> tipoEvaluaciones = response.getBody();
    Assertions.assertThat(tipoEvaluaciones.size()).isEqualTo(1);
    Assertions.assertThat(tipoEvaluaciones.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(tipoEvaluaciones.get(0).getNombre()).startsWith("TipoEvaluacion");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedTipoEvaluacionList() throws Exception {
    // when: Ordenación por nombre desc
    String query = "nombre-";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_EVALUACION_CONTROLLER_BASE_PATH).queryParam("s", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoEvaluacion>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, null, new ParameterizedTypeReference<List<TipoEvaluacion>>() {
        });

    // then: Respuesta OK, TipoEvaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoEvaluacion> tipoEvaluaciones = response.getBody();
    Assertions.assertThat(tipoEvaluaciones.size()).isEqualTo(8);
    for (int i = 0; i < 8; i++) {
      TipoEvaluacion tipoEvaluacion = tipoEvaluaciones.get(i);
      Assertions.assertThat(tipoEvaluacion.getId()).isEqualTo(8 - i);
      Assertions.assertThat(tipoEvaluacion.getNombre()).isEqualTo("TipoEvaluacion" + String.format("%03d", 8 - i));
    }
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsTipoEvaluacionSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por nombre desc
    String sort = "nombre-";
    // when: Filtra por nombre like e id equals
    String filter = "nombre~%00%";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_EVALUACION_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<TipoEvaluacion>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<TipoEvaluacion>>() {
        });

    // then: Respuesta OK, TipoEvaluaciones retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoEvaluacion> tipoEvaluaciones = response.getBody();
    Assertions.assertThat(tipoEvaluaciones.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("3");

    // Contiene nombre='TipoEvaluacion001', 'TipoEvaluacion002',
    // 'TipoEvaluacion003'
    Assertions.assertThat(tipoEvaluaciones.get(0).getNombre()).isEqualTo("TipoEvaluacion" + String.format("%03d", 3));
    Assertions.assertThat(tipoEvaluaciones.get(1).getNombre()).isEqualTo("TipoEvaluacion" + String.format("%03d", 2));
    Assertions.assertThat(tipoEvaluaciones.get(2).getNombre()).isEqualTo("TipoEvaluacion" + String.format("%03d", 1));

  }

  /**
   * Función que devuelve un objeto TipoEvaluacion
   * 
   * @param id     id del TipoEvaluacion
   * @param nombre la descripción del TipoEvaluacion
   * @return el objeto TipoEvaluacion
   */

  public TipoEvaluacion generarMockTipoEvaluacion(Long id, String nombre) {

    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion();
    tipoEvaluacion.setId(id);
    tipoEvaluacion.setNombre(nombre);
    tipoEvaluacion.setActivo(Boolean.TRUE);

    return tipoEvaluacion;
  }

}