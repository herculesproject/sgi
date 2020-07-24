package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.TipoTarea;
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
 * Test de integracion de TipoTarea.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("SECURITY_MOCK")
public class TipoTareaIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String TIPO_TAREA_CONTROLLER_BASE_PATH = "/tipostarea";

  @Autowired
  private TestRestTemplate restTemplate;

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
          .authorities("ETI-TIPOTAREA-EDITAR", "ETI-TIPOTAREA-VER");
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
  public void getTipoTarea_WithId_ReturnsTipoTarea() throws Exception {
    final ResponseEntity<TipoTarea> response = restTemplate.withBasicAuth("user", "secret")
        .getForEntity(TIPO_TAREA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, TipoTarea.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final TipoTarea tipoTarea = response.getBody();

    Assertions.assertThat(tipoTarea.getId()).as("id").isEqualTo(1L);
    Assertions.assertThat(tipoTarea.getNombre()).as("nombre").isEqualTo("Diseño de proyecto y procedimientos");
    Assertions.assertThat(tipoTarea.getActivo()).as("activo").isEqualTo(true);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addTipoTarea_ReturnsTipoTarea() throws Exception {

    TipoTarea nuevoTipoTarea = new TipoTarea();
    nuevoTipoTarea.setId(1L);
    nuevoTipoTarea.setNombre("TipoTarea1");
    nuevoTipoTarea.setActivo(Boolean.TRUE);

    final ResponseEntity<TipoTarea> response = restTemplate.withBasicAuth("user", "secret")
        .postForEntity(TIPO_TAREA_CONTROLLER_BASE_PATH, nuevoTipoTarea, TipoTarea.class);

    final TipoTarea tipoTarea = response.getBody();

    Assertions.assertThat(tipoTarea.getId()).as("id").isEqualTo(1L);
    Assertions.assertThat(tipoTarea.getNombre()).as("nombre").isEqualTo(nuevoTipoTarea.getNombre());
    Assertions.assertThat(tipoTarea.getActivo()).as("activo").isEqualTo(nuevoTipoTarea.getActivo());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeTipoTarea_Success() throws Exception {
    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<TipoTarea> response = restTemplate.withBasicAuth("user", "secret")
        .exchange(TIPO_TAREA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, null, TipoTarea.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeTipoTarea_DoNotGetTipoTarea() throws Exception {
    restTemplate.withBasicAuth("user", "secret").delete(TIPO_TAREA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 10L);

    final ResponseEntity<TipoTarea> response = restTemplate.withBasicAuth("user", "secret")
        .getForEntity(TIPO_TAREA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, TipoTarea.class, 10L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceTipoTarea_ReturnsTipoTarea() throws Exception {

    TipoTarea replaceTipoTarea = generarMockTipoTarea(1L, "TipoTarea1");

    final HttpEntity<TipoTarea> requestEntity = new HttpEntity<TipoTarea>(replaceTipoTarea, new HttpHeaders());

    final ResponseEntity<TipoTarea> response = restTemplate.withBasicAuth("user", "secret").exchange(
        TIPO_TAREA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, requestEntity, TipoTarea.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final TipoTarea tipoTarea = response.getBody();

    Assertions.assertThat(tipoTarea.getId()).as("id").isNotNull();
    Assertions.assertThat(tipoTarea.getNombre()).as("nombre").isEqualTo(replaceTipoTarea.getNombre());
    Assertions.assertThat(tipoTarea.getActivo()).as("activo").isEqualTo(replaceTipoTarea.getActivo());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsTipoTareaSubList() throws Exception {
    // when: Obtiene la page=0 con pagesize=2
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "2");

    URI uri = UriComponentsBuilder.fromUriString(TIPO_TAREA_CONTROLLER_BASE_PATH).build(false).toUri();

    final ResponseEntity<List<TipoTarea>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<TipoTarea>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoTarea> tiposTarea = response.getBody();
    Assertions.assertThat(tiposTarea.size()).isEqualTo(2);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("2");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("3");

    // Contiene de nombre='Diseño de proyecto y procedimientos' y 'Eutanasia'
    Assertions.assertThat(tiposTarea.get(0).getNombre()).isEqualTo("Diseño de proyecto y procedimientos");
    Assertions.assertThat(tiposTarea.get(1).getNombre()).isEqualTo("Eutanasia");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredTipoTareaList() throws Exception {
    // when: Búsqueda por nombre like e id equals
    Long id = 1L;
    String query = "nombre~%proyecto%,id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(TIPO_TAREA_CONTROLLER_BASE_PATH).queryParam("q", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoTarea>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, null, new ParameterizedTypeReference<List<TipoTarea>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoTarea> tiposTarea = response.getBody();
    Assertions.assertThat(tiposTarea.size()).isEqualTo(1);
    Assertions.assertThat(tiposTarea.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(tiposTarea.get(0).getNombre()).startsWith("Diseño de proyecto y procedimientos");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedTipoTareaList() throws Exception {
    // when: Ordenación por nombre desc
    String query = "nombre-";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_TAREA_CONTROLLER_BASE_PATH).queryParam("s", query).build(false)
        .toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoTarea>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, null, new ParameterizedTypeReference<List<TipoTarea>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoTarea> tiposTarea = response.getBody();
    Assertions.assertThat(tiposTarea.size()).isEqualTo(3);
    Assertions.assertThat(tiposTarea.get(0).getId()).as("0.id").isEqualTo(3);
    Assertions.assertThat(tiposTarea.get(0).getNombre()).as("0.nombre").isEqualTo("Manipulación de animales");
    Assertions.assertThat(tiposTarea.get(1).getId()).as("1.id").isEqualTo(2);
    Assertions.assertThat(tiposTarea.get(1).getNombre()).as("1.nombre").isEqualTo("Eutanasia");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsTipoTareaSubList() throws Exception {
    // when: Obtiene page=0 con pagesize=4
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "4");
    // when: Ordena por nombre desc
    String sort = "nombre-";
    // when: Filtra por nombre like
    String filter = "nombre~%de%";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_TAREA_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<TipoTarea>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<TipoTarea>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoTarea> tiposTarea = response.getBody();
    Assertions.assertThat(tiposTarea.size()).isEqualTo(2);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("4");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("2");

    // Contiene nombre='Diseño de proyecto y procedimientos', 'Manipulación de
    // animales'
    Assertions.assertThat(tiposTarea.get(0).getNombre()).as("0.nombre").isEqualTo("Manipulación de animales");
    Assertions.assertThat(tiposTarea.get(1).getNombre()).as("1.nombre")
        .isEqualTo("Diseño de proyecto y procedimientos");

  }

  /**
   * Función que devuelve un objeto TipoTarea
   * 
   * @param id     id del tipoTarea
   * @param nombre la descripción del tipo de tarea
   * @return el objeto tipo tarea
   */
  public TipoTarea generarMockTipoTarea(Long id, String nombre) {
    TipoTarea tipoTarea = new TipoTarea();
    tipoTarea.setId(id);
    tipoTarea.setNombre(nombre);
    tipoTarea.setActivo(Boolean.TRUE);

    return tipoTarea;
  }

}
