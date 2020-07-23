package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Asistentes;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Evaluador;
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
 * Test de integracion de Asistentes.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("SECURITY_MOCK")
public class AsistentesIT {

  @Autowired
  private TestRestTemplate restTemplate;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String ASISTENTE_CONTROLLER_BASE_PATH = "/asistentes";

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
          .authorities("ETI-ASISTENTES-EDITAR", "ETI-ASISTENTES-VER");
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
  public void getAsistentes_WithId_ReturnsAsistentes() throws Exception {
    final ResponseEntity<Asistentes> response = restTemplate.withBasicAuth("user", "secret")
        .getForEntity(ASISTENTE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, Asistentes.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Asistentes asistente = response.getBody();

    Assertions.assertThat(asistente.getId()).isEqualTo(1L);
    Assertions.assertThat(asistente.getMotivo()).isEqualTo("Motivo1");
    Assertions.assertThat(asistente.getAsistencia()).isTrue();
    Assertions.assertThat(asistente.getConvocatoriaReunion().getId()).isEqualTo(1L);
    Assertions.assertThat(asistente.getEvaluador().getId()).isEqualTo(1L);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addAsistentes_ReturnsAsistentes() throws Exception {

    Asistentes nuevoAsistente = new Asistentes();
    nuevoAsistente.setMotivo("Motivo 1");
    nuevoAsistente.setAsistencia(Boolean.TRUE);
    nuevoAsistente.setConvocatoriaReunion(new ConvocatoriaReunion());
    nuevoAsistente.getConvocatoriaReunion().setId(1L);
    nuevoAsistente.setEvaluador(new Evaluador());
    nuevoAsistente.getEvaluador().setId(1L);

    restTemplate.withBasicAuth("user", "secret").postForEntity(ASISTENTE_CONTROLLER_BASE_PATH, nuevoAsistente,
        Asistentes.class);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeAsistentes_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<Asistentes> response = restTemplate.withBasicAuth("user", "secret")
        .exchange(ASISTENTE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, null, Asistentes.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeAsistentes_DoNotGetAsistentes() throws Exception {
    restTemplate.withBasicAuth("user", "secret").delete(ASISTENTE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L);

    final ResponseEntity<Asistentes> response = restTemplate.withBasicAuth("user", "secret")
        .getForEntity(ASISTENTE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, Asistentes.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceAsistentes_ReturnsAsistentes() throws Exception {

    Asistentes replaceAsistente = new Asistentes();
    replaceAsistente.setMotivo("Motivo 1");
    replaceAsistente.setAsistencia(Boolean.TRUE);
    replaceAsistente.setConvocatoriaReunion(new ConvocatoriaReunion());
    replaceAsistente.getConvocatoriaReunion().setId(1L);
    replaceAsistente.setEvaluador(new Evaluador());
    replaceAsistente.getEvaluador().setId(1L);

    final HttpEntity<Asistentes> requestEntity = new HttpEntity<Asistentes>(replaceAsistente, new HttpHeaders());

    final ResponseEntity<Asistentes> response = restTemplate.withBasicAuth("user", "secret").exchange(

        ASISTENTE_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, requestEntity, Asistentes.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final Asistentes asistente = response.getBody();

    Assertions.assertThat(asistente.getId()).isNotNull();
    Assertions.assertThat(asistente.getId()).isEqualTo(1L);
    Assertions.assertThat(asistente.getMotivo()).isEqualTo("Motivo 1");
    Assertions.assertThat(asistente.getAsistencia()).isTrue();
    Assertions.assertThat(asistente.getConvocatoriaReunion().getId()).isEqualTo(1L);
    Assertions.assertThat(asistente.getEvaluador().getId()).isEqualTo(1L);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsAsistentesSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "3");

    URI uri = UriComponentsBuilder.fromUriString(ASISTENTE_CONTROLLER_BASE_PATH).build(false).toUri();

    final ResponseEntity<List<Asistentes>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<Asistentes>>() {
        });

    // then: Respuesta OK, Asistentes retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Asistentes> asistentes = response.getBody();
    Assertions.assertThat(asistentes.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("6");

    // Contiene de motivo='Motivo4' a
    // 'Motivo6'
    Assertions.assertThat(asistentes.get(0).getMotivo()).isEqualTo("Motivo4");
    Assertions.assertThat(asistentes.get(1).getMotivo()).isEqualTo("Motivo5");
    Assertions.assertThat(asistentes.get(2).getMotivo()).isEqualTo("Motivo6");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredAsistentesList() throws Exception {
    // when: Búsqueda por motivo like e id equals
    Long id = 5L;
    String query = "motivo~motivo%,id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(ASISTENTE_CONTROLLER_BASE_PATH).queryParam("q", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Asistentes>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, null, new ParameterizedTypeReference<List<Asistentes>>() {
        });

    // then: Respuesta OK, Asistentes retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Asistentes> asistentes = response.getBody();
    Assertions.assertThat(asistentes.size()).isEqualTo(1);
    Assertions.assertThat(asistentes.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(asistentes.get(0).getMotivo()).startsWith("Motivo5");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedAsistentesList() throws Exception {
    // when: Ordenación por motivo desc
    String query = "motivo-";

    URI uri = UriComponentsBuilder.fromUriString(ASISTENTE_CONTROLLER_BASE_PATH).queryParam("s", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<Asistentes>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, null, new ParameterizedTypeReference<List<Asistentes>>() {
        });

    // then: Respuesta OK, Asistentes retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Asistentes> asistentes = response.getBody();
    Assertions.assertThat(asistentes.size()).isEqualTo(6);
    for (int i = 0; i < 6; i++) {
      Asistentes asistente = asistentes.get(i);
      Assertions.assertThat(asistente.getId()).isEqualTo(6 - i);
      Assertions.assertThat(asistente.getMotivo()).isEqualTo("Motivo" + String.valueOf(6 - i));
    }
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsAsistentesSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por nombre desc
    String sort = "motivo-";
    // when: Filtra por motivo like
    String filter = "motivo~%motivo%";

    URI uri = UriComponentsBuilder.fromUriString(ASISTENTE_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<Asistentes>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<Asistentes>>() {
        });

    // then: Respuesta OK, Asistentes retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<Asistentes> asistentes = response.getBody();
    Assertions.assertThat(asistentes.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("6");

    // Contiene de motivo='Motivo6' a
    // 'Motivo4'
    Assertions.assertThat(asistentes.get(0).getMotivo()).isEqualTo("Motivo6");
    Assertions.assertThat(asistentes.get(1).getMotivo()).isEqualTo("Motivo5");
    Assertions.assertThat(asistentes.get(2).getMotivo()).isEqualTo("Motivo4");

  }

}