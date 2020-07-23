package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.TipoEstadoActa;
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
 * Test de integracion de TipoEstadoActa.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("SECURITY_MOCK")
public class TipoEstadoActaIT {

  @Autowired
  private TestRestTemplate restTemplate;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String TIPO_ESTADO_ACTA_CONTROLLER_BASE_PATH = "/tipoestadoactas";

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
          .authorities("ETI-TIPOESTADOACTA-EDITAR", "ETI-TIPOESTADOACTA-VER");
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
  public void getTipoEstadoActa_WithId_ReturnsTipoEstadoActa() throws Exception {
    final ResponseEntity<TipoEstadoActa> response = restTemplate.withBasicAuth("user", "secret")
        .getForEntity(TIPO_ESTADO_ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, TipoEstadoActa.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final TipoEstadoActa tipoEstadoActa = response.getBody();

    Assertions.assertThat(tipoEstadoActa.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoEstadoActa.getNombre()).isEqualTo("En elaboración");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addTipoEstadoActa_ReturnsTipoEstadoActa() throws Exception {

    TipoEstadoActa nuevoTipoEstadoActa = new TipoEstadoActa();
    nuevoTipoEstadoActa.setNombre("TipoEstadoActa1");
    nuevoTipoEstadoActa.setActivo(Boolean.TRUE);

    restTemplate.withBasicAuth("user", "secret").postForEntity(TIPO_ESTADO_ACTA_CONTROLLER_BASE_PATH,
        nuevoTipoEstadoActa, TipoEstadoActa.class);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeTipoEstadoActa_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<TipoEstadoActa> response = restTemplate.withBasicAuth("user", "secret").exchange(
        TIPO_ESTADO_ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, null, TipoEstadoActa.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeTipoEstadoActa_DoNotGetTipoEstadoActa() throws Exception {
    restTemplate.withBasicAuth("user", "secret").delete(TIPO_ESTADO_ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L);

    final ResponseEntity<TipoEstadoActa> response = restTemplate.withBasicAuth("user", "secret")
        .getForEntity(TIPO_ESTADO_ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, TipoEstadoActa.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceTipoEstadoActa_ReturnsTipoEstadoActa() throws Exception {

    TipoEstadoActa replaceTipoEstadoActa = generarMockTipoEstadoActa(1L, "TipoEstadoActa1");

    final HttpEntity<TipoEstadoActa> requestEntity = new HttpEntity<TipoEstadoActa>(replaceTipoEstadoActa,
        new HttpHeaders());

    final ResponseEntity<TipoEstadoActa> response = restTemplate.withBasicAuth("user", "secret").exchange(

        TIPO_ESTADO_ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, requestEntity, TipoEstadoActa.class,
        1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final TipoEstadoActa tipoEstadoActa = response.getBody();

    Assertions.assertThat(tipoEstadoActa.getId()).isNotNull();
    Assertions.assertThat(tipoEstadoActa.getNombre()).isEqualTo(replaceTipoEstadoActa.getNombre());
    Assertions.assertThat(tipoEstadoActa.getActivo()).isEqualTo(replaceTipoEstadoActa.getActivo());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsTipoEstadoActaSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "1");

    URI uri = UriComponentsBuilder.fromUriString(TIPO_ESTADO_ACTA_CONTROLLER_BASE_PATH).build(false).toUri();

    final ResponseEntity<List<TipoEstadoActa>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<TipoEstadoActa>>() {
        });

    // then: Respuesta OK, TipoEstadoActas retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoEstadoActa> tipoEstadoActas = response.getBody();
    Assertions.assertThat(tipoEstadoActas.size()).isEqualTo(1);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("2");

    // Contiene de nombre='Finalizada'
    Assertions.assertThat(tipoEstadoActas.get(0).getNombre()).isEqualTo("Finalizada");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredTipoEstadoActaList() throws Exception {
    // when: Búsqueda por nombre like e id equals
    Long id = 1L;
    String query = "nombre~En%,id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(TIPO_ESTADO_ACTA_CONTROLLER_BASE_PATH).queryParam("q", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoEstadoActa>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, null, new ParameterizedTypeReference<List<TipoEstadoActa>>() {
        });

    // then: Respuesta OK, TipoEstadoActas retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoEstadoActa> tipoEstadoActas = response.getBody();
    Assertions.assertThat(tipoEstadoActas.size()).isEqualTo(1);
    Assertions.assertThat(tipoEstadoActas.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(tipoEstadoActas.get(0).getNombre()).startsWith("En elaboración");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedTipoEstadoActaList() throws Exception {
    // when: Ordenación por nombre desc
    String query = "nombre-";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_ESTADO_ACTA_CONTROLLER_BASE_PATH).queryParam("s", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoEstadoActa>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, null, new ParameterizedTypeReference<List<TipoEstadoActa>>() {
        });

    // then: Respuesta OK, TipoEstadoActas retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoEstadoActa> tipoEstadoActas = response.getBody();
    Assertions.assertThat(tipoEstadoActas.size()).isEqualTo(2);
    Assertions.assertThat(tipoEstadoActas.get(0).getId()).isEqualTo(2);
    Assertions.assertThat(tipoEstadoActas.get(0).getNombre()).isEqualTo("Finalizada");

    Assertions.assertThat(tipoEstadoActas.get(1).getId()).isEqualTo(1);
    Assertions.assertThat(tipoEstadoActas.get(1).getNombre()).isEqualTo("En elaboración");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsTipoEstadoActaSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por nombre desc
    String sort = "nombre-";
    // when: Filtra por nombre like
    String filter = "nombre~%finalizada%";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_ESTADO_ACTA_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<TipoEstadoActa>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<TipoEstadoActa>>() {
        });

    // then: Respuesta OK, TipoEstadoActas retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoEstadoActa> tipoEstadoActas = response.getBody();
    Assertions.assertThat(tipoEstadoActas.size()).isEqualTo(1);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("1");

    // Contiene nombre='Finalizada'
    Assertions.assertThat(tipoEstadoActas.get(0).getNombre()).isEqualTo("Finalizada");

  }

  /**
   * Función que devuelve un objeto TipoEstadoActa
   * 
   * @param id     id del TipoEstadoActa
   * @param nombre la descripción del TipoEstadoActa
   * @return el objeto tipoEstadoActa
   */

  public TipoEstadoActa generarMockTipoEstadoActa(Long id, String nombre) {

    TipoEstadoActa tipoEstadoActa = new TipoEstadoActa();
    tipoEstadoActa.setId(id);
    tipoEstadoActa.setNombre(nombre);
    tipoEstadoActa.setActivo(Boolean.TRUE);

    return tipoEstadoActa;
  }

}