package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Acta;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.TipoEstadoActa;
import org.crue.hercules.sgi.framework.security.web.SgiAuthenticationEntryPoint;
import org.crue.hercules.sgi.framework.security.web.access.SgiAccessDeniedHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Test de integracion de Acta.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("SECURITY_MOCK")

public class ActaIT {

  @Autowired
  private TestRestTemplate restTemplate;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String ACTA_CONTROLLER_BASE_PATH = "/actas";

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
          .authorities("ETI-ACTA-EDITAR", "ETI-ACTA-VER");
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
  public void getActa_WithId_ReturnsActa() throws Exception {
    final ResponseEntity<Acta> response = restTemplate.withBasicAuth("user", "secret")
        .getForEntity(ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, Acta.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Acta acta = response.getBody();

    Assertions.assertThat(acta.getId()).as("id").isEqualTo(1L);
    Assertions.assertThat(acta.getConvocatoriaReunion().getId()).as("convocatoriaReunion.id").isEqualTo(100L);
    Assertions.assertThat(acta.getHoraInicio()).as("horaInicio").isEqualTo(10);
    Assertions.assertThat(acta.getMinutoInicio()).as("minutoInicio").isEqualTo(15);
    Assertions.assertThat(acta.getHoraFin()).as("horaFin").isEqualTo(12);
    Assertions.assertThat(acta.getMinutoFin()).as("minutoFin").isEqualTo(0);
    Assertions.assertThat(acta.getResumen()).as("resumen").isEqualTo("Resumen123");
    Assertions.assertThat(acta.getNumero()).as("numero").isEqualTo(123);
    Assertions.assertThat(acta.getEstadoActual().getId()).as("estadoActual.id").isEqualTo(1L);
    Assertions.assertThat(acta.getInactiva()).as("inactiva").isEqualTo(true);
    Assertions.assertThat(acta.getActivo()).as("activo").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addActa_ReturnsActa() throws Exception {

    Acta nuevoActa = generarMockActa(null, 123);

    final ResponseEntity<Acta> response = restTemplate.withBasicAuth("user", "secret")
        .postForEntity(ACTA_CONTROLLER_BASE_PATH, nuevoActa, Acta.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    final Acta acta = response.getBody();

    Assertions.assertThat(acta.getId()).as("id").isEqualTo(1L);
    Assertions.assertThat(acta.getConvocatoriaReunion().getId()).as("convocatoriaReunion.id").isEqualTo(100L);
    Assertions.assertThat(acta.getHoraInicio()).as("horaInicio").isEqualTo(10);
    Assertions.assertThat(acta.getMinutoInicio()).as("minutoInicio").isEqualTo(15);
    Assertions.assertThat(acta.getHoraFin()).as("horaFin").isEqualTo(12);
    Assertions.assertThat(acta.getMinutoFin()).as("minutoFin").isEqualTo(0);
    Assertions.assertThat(acta.getResumen()).as("resumen").isEqualTo("Resumen123");
    Assertions.assertThat(acta.getNumero()).as("numero").isEqualTo(123);
    Assertions.assertThat(acta.getEstadoActual().getId()).as("estadoActual.id").isEqualTo(1L);
    Assertions.assertThat(acta.getInactiva()).as("inactiva").isEqualTo(true);
    Assertions.assertThat(acta.getActivo()).as("activo").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeActa_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<Acta> response = restTemplate.withBasicAuth("user", "secret")
        .exchange(ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, null, Acta.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeActa_DoNotGetActa() throws Exception {
    restTemplate.withBasicAuth("user", "secret").delete(ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L);

    final ResponseEntity<Acta> response = restTemplate.withBasicAuth("user", "secret")
        .getForEntity(ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, Acta.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceActa_ReturnsActa() throws Exception {

    Acta replaceActa = generarMockActa(1L, 456);

    final HttpEntity<Acta> requestEntity = new HttpEntity<Acta>(replaceActa, new HttpHeaders());

    final ResponseEntity<Acta> response = restTemplate.withBasicAuth("user", "secret")
        .exchange(ACTA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, requestEntity, Acta.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Acta acta = response.getBody();

    Assertions.assertThat(acta.getId()).as("id").isEqualTo(1L);
    Assertions.assertThat(acta.getConvocatoriaReunion().getId()).as("convocatoriaReunion.id").isEqualTo(100L);
    Assertions.assertThat(acta.getHoraInicio()).as("horaInicio").isEqualTo(10);
    Assertions.assertThat(acta.getMinutoInicio()).as("minutoInicio").isEqualTo(15);
    Assertions.assertThat(acta.getHoraFin()).as("horaFin").isEqualTo(12);
    Assertions.assertThat(acta.getMinutoFin()).as("minutoFin").isEqualTo(0);
    Assertions.assertThat(acta.getResumen()).as("resumen").isEqualTo("Resumen456");
    Assertions.assertThat(acta.getNumero()).as("numero").isEqualTo(456);
    Assertions.assertThat(acta.getEstadoActual().getId()).as("estadoActual.id").isEqualTo(1L);
    Assertions.assertThat(acta.getInactiva()).as("inactiva").isEqualTo(true);
    Assertions.assertThat(acta.getActivo()).as("activo").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsActaSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=5
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    URI uri = UriComponentsBuilder.fromUriString(ACTA_CONTROLLER_BASE_PATH).build(false).toUri();

    final ResponseEntity<List<Acta>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<Acta>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Acta> actas = response.getBody();
    Assertions.assertThat(actas.size()).as("size").isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).as("x-page").isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).as("x-page-size").isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).as("x-total-count").isEqualTo("8");

    // Contiene de id=6 a 8
    Assertions.assertThat(actas.get(0).getId()).as("0.id").isEqualTo(6);
    Assertions.assertThat(actas.get(1).getId()).as("1.id").isEqualTo(7);
    Assertions.assertThat(actas.get(2).getId()).as("2.id").isEqualTo(8);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredActaList() throws Exception {
    // when: Búsqueda por acta id equals
    Long id = 5L;
    String query = "id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(ACTA_CONTROLLER_BASE_PATH).queryParam("q", query).build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Acta>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, null, new ParameterizedTypeReference<List<Acta>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Acta> actas = response.getBody();
    Assertions.assertThat(actas.size()).as("size").isEqualTo(1);
    Assertions.assertThat(actas.get(0).getId()).as("id").isEqualTo(id);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedActaList() throws Exception {
    // when: Ordenación por id desc
    String sort = "id-";

    URI uri = UriComponentsBuilder.fromUriString(ACTA_CONTROLLER_BASE_PATH).queryParam("s", sort).build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Acta>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, null, new ParameterizedTypeReference<List<Acta>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Acta> actas = response.getBody();
    Assertions.assertThat(actas.size()).as("size").isEqualTo(8);
    for (int i = 0; i < 8; i++) {
      Acta acta = actas.get(i);
      Assertions.assertThat(acta.getId()).as((8 - i) + ".id").isEqualTo(8 - i);
    }
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsActaSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=3
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por id desc
    String sort = "id-";
    // when: Filtra por id menor
    String filter = "id<4";

    URI uri = UriComponentsBuilder.fromUriString(ACTA_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<Acta>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<Acta>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Acta> actas = response.getBody();
    Assertions.assertThat(actas.size()).as("size").isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("x-page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("x-page-size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("x-total-count").isEqualTo("3");

    // Contiene id=3, 2, 1
    Assertions.assertThat(actas.get(0).getId()).as("0.id").isEqualTo(3);
    Assertions.assertThat(actas.get(1).getId()).as("1.id").isEqualTo(2);
    Assertions.assertThat(actas.get(2).getId()).as("2.id").isEqualTo(1);
  }

  /**
   * Función que devuelve un objeto Acta
   * 
   * @param id     id del acta
   * @param numero numero del acta
   * @return el objeto Acta
   */
  public Acta generarMockActa(Long id, Integer numero) {
    ConvocatoriaReunion convocatoriaReunion = new ConvocatoriaReunion();
    convocatoriaReunion.setId(100L);

    TipoEstadoActa tipoEstadoActa = new TipoEstadoActa();
    tipoEstadoActa.setId(1L);
    tipoEstadoActa.setNombre("En elaboración");
    tipoEstadoActa.setActivo(Boolean.TRUE);

    Acta acta = new Acta();
    acta.setId(id);
    acta.setConvocatoriaReunion(convocatoriaReunion);
    acta.setHoraInicio(10);
    acta.setMinutoInicio(15);
    acta.setHoraFin(12);
    acta.setMinutoFin(0);
    acta.setResumen("Resumen" + numero);
    acta.setNumero(numero);
    acta.setEstadoActual(tipoEstadoActa);
    acta.setInactiva(true);
    acta.setActivo(true);

    return acta;
  }

}