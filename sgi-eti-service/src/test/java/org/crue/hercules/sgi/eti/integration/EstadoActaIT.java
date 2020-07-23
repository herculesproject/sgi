package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Acta;
import org.crue.hercules.sgi.eti.model.EstadoActa;
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
 * Test de integracion de EstadoActa.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("SECURITY_MOCK")
public class EstadoActaIT {

  @Autowired
  private TestRestTemplate restTemplate;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String ESTADO_ACTA_CONTROLLER_BASE_PATH = "/estadoactas";

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
          .authorities("ETI-ESTADOACTA-EDITAR", "ETI-ESTADOACTA-VER");
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
  public void getEstadoActa_WithId_ReturnsEstadoActa() throws Exception {
    final ResponseEntity<EstadoActa> response = restTemplate.withBasicAuth("user", "secret")
        .getForEntity(ESTADO_ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, EstadoActa.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final EstadoActa estadoActa = response.getBody();

    Assertions.assertThat(estadoActa.getId()).as("id").isEqualTo(1L);
    Assertions.assertThat(estadoActa.getActa()).as("acta").isNotNull();
    Assertions.assertThat(estadoActa.getActa().getId()).as("acta.id").isEqualTo(100L);
    Assertions.assertThat(estadoActa.getTipoEstadoActa()).as("tipoEstadoActa").isNotNull();
    Assertions.assertThat(estadoActa.getTipoEstadoActa().getId()).as("tipoEstadoActa.id").isEqualTo(200L);
    Assertions.assertThat(estadoActa.getFechaEstado()).as("fechaEstado")
        .isEqualTo(LocalDateTime.of(2020, 07, 14, 19, 30, 0));
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addEstadoActa_ReturnsEstadoActa() throws Exception {

    EstadoActa nuevoEstadoActa = generarMockEstadoActa(null);

    final ResponseEntity<EstadoActa> response = restTemplate.withBasicAuth("user", "secret")
        .postForEntity(ESTADO_ACTA_CONTROLLER_BASE_PATH, nuevoEstadoActa, EstadoActa.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    final EstadoActa estadoActa = response.getBody();

    Assertions.assertThat(estadoActa.getId()).as("id").isEqualTo(1L);
    Assertions.assertThat(estadoActa.getActa()).as("acta").isNotNull();
    Assertions.assertThat(estadoActa.getActa().getId()).as("acta.id").isEqualTo(100L);
    Assertions.assertThat(estadoActa.getTipoEstadoActa()).as("tipoEstadoActa").isNotNull();
    Assertions.assertThat(estadoActa.getTipoEstadoActa().getId()).as("tipoEstadoActa.id").isEqualTo(200L);
    Assertions.assertThat(estadoActa.getFechaEstado()).as("fechaEstado")
        .isEqualTo(LocalDateTime.of(2020, 07, 14, 19, 30, 0));
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeEstadoActa_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<EstadoActa> response = restTemplate.withBasicAuth("user", "secret")
        .exchange(ESTADO_ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, null, EstadoActa.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeEstadoActa_DoNotGetEstadoActa() throws Exception {
    restTemplate.withBasicAuth("user", "secret").delete(ESTADO_ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L);

    final ResponseEntity<EstadoActa> response = restTemplate.withBasicAuth("user", "secret")
        .getForEntity(ESTADO_ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, EstadoActa.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceEstadoActa_ReturnsEstadoActa() throws Exception {

    EstadoActa replaceEstadoActa = generarMockEstadoActa(1L);

    final HttpEntity<EstadoActa> requestEntity = new HttpEntity<EstadoActa>(replaceEstadoActa, new HttpHeaders());

    final ResponseEntity<EstadoActa> response = restTemplate.withBasicAuth("user", "secret").exchange(
        ESTADO_ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, requestEntity, EstadoActa.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final EstadoActa estadoActa = response.getBody();

    Assertions.assertThat(estadoActa.getId()).as("id").isEqualTo(1L);
    Assertions.assertThat(estadoActa.getActa()).as("acta").isNotNull();
    Assertions.assertThat(estadoActa.getActa().getId()).as("acta.id").isEqualTo(100L);
    Assertions.assertThat(estadoActa.getTipoEstadoActa()).as("tipoEstadoActa").isNotNull();
    Assertions.assertThat(estadoActa.getTipoEstadoActa().getId()).as("tipoEstadoActa.id").isEqualTo(200L);
    Assertions.assertThat(estadoActa.getFechaEstado()).as("fechaEstado")
        .isEqualTo(LocalDateTime.of(2020, 07, 14, 19, 30, 0));
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsEstadoActaSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=5
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    URI uri = UriComponentsBuilder.fromUriString(ESTADO_ACTA_CONTROLLER_BASE_PATH).build(false).toUri();

    final ResponseEntity<List<EstadoActa>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<EstadoActa>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<EstadoActa> estadosActas = response.getBody();
    Assertions.assertThat(estadosActas.size()).as("size").isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).as("x-page").isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).as("x-page-size").isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).as("x-total-count").isEqualTo("8");

    // Contiene de id=6 a 8
    Assertions.assertThat(estadosActas.get(0).getId()).as("0.id").isEqualTo(6);
    Assertions.assertThat(estadosActas.get(1).getId()).as("1.id").isEqualTo(7);
    Assertions.assertThat(estadosActas.get(2).getId()).as("2.id").isEqualTo(8);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredEstadoActaList() throws Exception {
    // when: Búsqueda por estado acta id equals
    Long id = 5L;
    String query = "id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(ESTADO_ACTA_CONTROLLER_BASE_PATH).queryParam("q", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<EstadoActa>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, null, new ParameterizedTypeReference<List<EstadoActa>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<EstadoActa> estadosActas = response.getBody();
    Assertions.assertThat(estadosActas.size()).as("size").isEqualTo(1);
    Assertions.assertThat(estadosActas.get(0).getId()).as("id").isEqualTo(id);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedEstadoActaList() throws Exception {
    // when: Ordenación por id desc
    String sort = "id-";

    URI uri = UriComponentsBuilder.fromUriString(ESTADO_ACTA_CONTROLLER_BASE_PATH).queryParam("s", sort).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<EstadoActa>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, null, new ParameterizedTypeReference<List<EstadoActa>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<EstadoActa> estadosActas = response.getBody();
    Assertions.assertThat(estadosActas.size()).as("size").isEqualTo(8);
    for (int i = 0; i < 8; i++) {
      EstadoActa estadoActa = estadosActas.get(i);
      Assertions.assertThat(estadoActa.getId()).as((8 - i) + ".id").isEqualTo(8 - i);
    }
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsEstadoActaSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=3
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por id desc
    String sort = "id-";
    // when: Filtra por id menor
    String filter = "id<4";

    URI uri = UriComponentsBuilder.fromUriString(ESTADO_ACTA_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<EstadoActa>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<EstadoActa>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<EstadoActa> estadosActas = response.getBody();
    Assertions.assertThat(estadosActas.size()).as("size").isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("x-page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("x-page-size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("x-total-count").isEqualTo("3");

    // Contiene id=3, 2, 1
    Assertions.assertThat(estadosActas.get(0).getId()).as("0.id").isEqualTo(3);
    Assertions.assertThat(estadosActas.get(1).getId()).as("1.id").isEqualTo(2);
    Assertions.assertThat(estadosActas.get(2).getId()).as("2.id").isEqualTo(1);
  }

  /**
   * Función que devuelve un objeto EstadoActa
   * 
   * @param id id del estado acta
   * @return el objeto EstadoActa
   */
  public EstadoActa generarMockEstadoActa(Long id) {
    Acta acta = new Acta();
    acta.setId(100L);

    TipoEstadoActa tipoEstadoActa = new TipoEstadoActa();
    tipoEstadoActa.setId(200L);

    EstadoActa estadoActa = new EstadoActa();
    estadoActa.setId(id);
    estadoActa.setActa(acta);
    estadoActa.setTipoEstadoActa(tipoEstadoActa);
    estadoActa.setFechaEstado(LocalDateTime.of(2020, 07, 14, 19, 30, 0));

    return estadoActa;
  }

}