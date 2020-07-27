package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
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
 * Test de integracion de Retrospectiva.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("SECURITY_MOCK")

public class RetrospectivaIT {

  @Autowired
  private TestRestTemplate restTemplate;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String RETROSPECTIVA_CONTROLLER_BASE_PATH = "/retrospectivas";

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
          .authorities("ETI-RETROSPECTIVA-EDITAR", "ETI-RETROSPECTIVA-VER");
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
  public void create_ReturnsRetrospectiva() throws Exception {

    // given: Nueva entidad
    final Retrospectiva newRetrospectiva = getMockData(1L);
    newRetrospectiva.setId(null);

    final String url = new StringBuilder(RETROSPECTIVA_CONTROLLER_BASE_PATH).toString();

    // when: Se crea la entidad
    final ResponseEntity<Retrospectiva> response = restTemplate.withBasicAuth("user", "secret").postForEntity(url,
        newRetrospectiva, Retrospectiva.class);

    // then: La entidad se crea correctamente
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    final Retrospectiva retrospectiva = response.getBody();
    Assertions.assertThat(retrospectiva.getId()).isNotNull();
    newRetrospectiva.setId(retrospectiva.getId());
    Assertions.assertThat(retrospectiva).isEqualTo(newRetrospectiva);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_WithExistingId_ReturnsRetrospectiva() throws Exception {

    // given: Entidad existente que se va a actualizar
    final Retrospectiva updatedRetrospectiva = getMockData(2L);
    updatedRetrospectiva.setId(1L);

    final String url = new StringBuilder(RETROSPECTIVA_CONTROLLER_BASE_PATH)//
        .append(PATH_PARAMETER_ID)//
        .toString();

    HttpEntity<Retrospectiva> request = new HttpEntity<>(updatedRetrospectiva);

    // when: Se actualiza la entidad
    final ResponseEntity<Retrospectiva> response = restTemplate.withBasicAuth("user", "secret").exchange(url,
        HttpMethod.PUT, request, Retrospectiva.class, updatedRetrospectiva.getId());

    // then: Los datos se actualizan correctamente
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isEqualTo(updatedRetrospectiva);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void delete_WithExistingId_Return204() throws Exception {

    // given: Entidad existente
    Long id = 1L;

    final String url = new StringBuilder(RETROSPECTIVA_CONTROLLER_BASE_PATH)//
        .append(PATH_PARAMETER_ID)//
        .toString();

    ResponseEntity<Retrospectiva> response = restTemplate.withBasicAuth("user", "secret").getForEntity(url,
        Retrospectiva.class, id);
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    // when: Se elimina la entidad
    response = restTemplate.withBasicAuth("user", "secret").exchange(url, HttpMethod.DELETE, null, Retrospectiva.class,
        id);

    // then: La entidad se elimina correctamente
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    response = restTemplate.withBasicAuth("user", "secret").getForEntity(url, Retrospectiva.class, id);
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_WithExistingId_ReturnsRetrospectiva() throws Exception {

    // given: Entidad con un determinado Id
    final Retrospectiva retrospectiva = getMockData(1L);

    final String url = new StringBuilder(RETROSPECTIVA_CONTROLLER_BASE_PATH)//
        .append(PATH_PARAMETER_ID)//
        .toString();

    // when: Se busca la entidad por ese Id
    ResponseEntity<Retrospectiva> response = restTemplate.withBasicAuth("user", "secret").getForEntity(url,
        Retrospectiva.class, retrospectiva.getId());

    // then: Se recupera la entidad con el Id
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isEqualTo(retrospectiva);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_WithNotExistingId_Returns404() throws Exception {

    // given: No existe entidad con el id indicado
    Long id = 1L;
    final String url = new StringBuilder(RETROSPECTIVA_CONTROLLER_BASE_PATH)//
        .append(PATH_PARAMETER_ID)//
        .toString();

    // when: Se busca la entidad por ese Id
    ResponseEntity<Retrospectiva> response = restTemplate.withBasicAuth("user", "secret").getForEntity(url,
        Retrospectiva.class, id);

    // then: Se produce error porque no encuentra la entidad con ese Id
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_Unlimited_ReturnsFullRetrospectivaList() throws Exception {

    // given: Datos existentes
    List<Retrospectiva> response = new LinkedList<>();
    response.add(getMockData(1L));
    response.add(getMockData(2L));

    final String url = new StringBuilder(RETROSPECTIVA_CONTROLLER_BASE_PATH).toString();

    // when: Se buscan todos los datos
    final ResponseEntity<List<Retrospectiva>> result = restTemplate.withBasicAuth("user", "secret").exchange(url,
        HttpMethod.GET, null, new ParameterizedTypeReference<List<Retrospectiva>>() {
        });

    // then: Se recuperan todos los datos
    Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(result.getBody()).isEqualTo(response);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsRetrospectivaSubList() throws Exception {

    // given: Datos existentes
    List<Retrospectiva> response = new LinkedList<>();
    response.add(getMockData(5L));

    // página 2 con 2 elementos por página
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "2");
    headers.add("X-Page-Size", "2");

    final String url = new StringBuilder(RETROSPECTIVA_CONTROLLER_BASE_PATH).toString();

    // when: Se buscan los datos paginados
    final ResponseEntity<List<Retrospectiva>> result = restTemplate.withBasicAuth("user", "secret").exchange(url,
        HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<Retrospectiva>>() {
        });

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(result.getBody().size()).isEqualTo(1);
    Assertions.assertThat(result.getBody()).isEqualTo(response);
    Assertions.assertThat(result.getHeaders().getFirst("X-Page")).isEqualTo("2");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Size")).isEqualTo("2");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Total-Count")).isEqualTo("1");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Count")).isEqualTo("3");
    Assertions.assertThat(result.getHeaders().getFirst("X-Total-Count")).isEqualTo("5");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredRetrospectivaList() throws Exception {

    // given: Datos existentes
    List<Retrospectiva> response = new LinkedList<>();
    response.add(getMockData(3L));

    // search by codigo like, id equals
    Long id = 3L;
    String query = "fechaRetrospectiva<=2020-07-03,id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(RETROSPECTIVA_CONTROLLER_BASE_PATH).queryParam("q", query).build(false)
        .toUri();

    // when: Se buscan los datos con el filtro indicado
    final ResponseEntity<List<Retrospectiva>> result = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, null, new ParameterizedTypeReference<List<Retrospectiva>>() {
        });

    // then: Se recuperan los datos filtrados
    Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(result.getBody()).isEqualTo(response);
    Assertions.assertThat(result.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Size")).isEqualTo("1");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Total-Count")).isEqualTo("1");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Count")).isEqualTo("1");
    Assertions.assertThat(result.getHeaders().getFirst("X-Total-Count")).isEqualTo("1");

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedRetrospectivaList() throws Exception {

    // given: Datos existentes

    // sort by id desc
    String sort = "id-";

    URI uri = UriComponentsBuilder.fromUriString(RETROSPECTIVA_CONTROLLER_BASE_PATH).queryParam("s", sort).build(false)
        .toUri();

    // when: Se buscan los datos con la ordenación indicada
    final ResponseEntity<List<Retrospectiva>> result = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, null, new ParameterizedTypeReference<List<Retrospectiva>>() {
        });

    // then: Se recuperan los datos filtrados, ordenados y paginados
    Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(result.getBody().get(0).getId()).isEqualTo(5L);
    Assertions.assertThat(result.getBody().get(4).getId()).isEqualTo(1L);
    Assertions.assertThat(result.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Total-Count")).isEqualTo("5");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Count")).isEqualTo("1");
    Assertions.assertThat(result.getHeaders().getFirst("X-Total-Count")).isEqualTo("5");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsRetrospectivaSubList() throws Exception {

    // given: Datos existentes
    List<Retrospectiva> response = new LinkedList<>();
    response.add(getMockData(1L));

    // página 1 con 2 elementos por página
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "2");

    // sort
    String sort = "id-";

    // search
    String query = "estadoRetrospectiva.nombre~Retrospectiva0%";

    URI uri = UriComponentsBuilder.fromUriString(RETROSPECTIVA_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", query).build(false).toUri();

    // when: Se buscan los datos paginados con el filtro y orden indicados
    final ResponseEntity<List<Retrospectiva>> result = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<Retrospectiva>>() {
        });

    // then: Se recuperan los datos filtrados, ordenados y paginados
    Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(result.getBody().size()).isEqualTo(1);
    Assertions.assertThat(result.getBody()).isEqualTo(response);
    Assertions.assertThat(result.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Size")).isEqualTo("2");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Total-Count")).isEqualTo("1");
    Assertions.assertThat(result.getHeaders().getFirst("X-Page-Count")).isEqualTo("2");
    Assertions.assertThat(result.getHeaders().getFirst("X-Total-Count")).isEqualTo("3");
  }

  /**
   * Genera un objeto {@link Retrospectiva}
   * 
   * @param id
   * @return Retrospectiva
   */
  private Retrospectiva getMockData(Long id) {

    final Retrospectiva data = new Retrospectiva();
    data.setId(id);
    data.setEstadoRetrospectiva(getMockDataEstadoRetrospectiva((id % 2 == 0) ? 2L : 1L));
    data.setFechaRetrospectiva(LocalDate.of(2020, 7, id.intValue()));

    return data;
  }

  /**
   * Genera un objeto {@link EstadoRetrospectiva}
   * 
   * @param id
   * @return EstadoRetrospectiva
   */
  private EstadoRetrospectiva getMockDataEstadoRetrospectiva(Long id) {

    String txt = (id % 2 == 0) ? String.valueOf(id) : "0" + String.valueOf(id);

    final EstadoRetrospectiva data = new EstadoRetrospectiva();
    data.setId(id);
    data.setNombre("EstadoRetrospectiva" + txt);
    data.setActivo(Boolean.TRUE);

    return data;
  }
}
