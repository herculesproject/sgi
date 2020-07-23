package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.TipoActividad;
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
 * Test de integracion de TipoActividad.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("SECURITY_MOCK")

public class TipoActividadIT {

  @Autowired
  private TestRestTemplate restTemplate;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH = "/tipoactividades";

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
          .authorities("ETI-TIPOACTIVIDAD-EDITAR", "ETI-TIPOACTIVIDAD-VER");
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
  public void getTipoActividad_WithId_ReturnsTipoActividad() throws Exception {
    final ResponseEntity<TipoActividad> response = restTemplate.withBasicAuth("user", "secret")
        .getForEntity(TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, TipoActividad.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final TipoActividad tipoActividad = response.getBody();

    Assertions.assertThat(tipoActividad.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoActividad.getNombre()).isEqualTo("Proyecto de investigación");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addTipoActividad_ReturnsTipoActividad() throws Exception {

    TipoActividad nuevoTipoActividad = new TipoActividad();
    nuevoTipoActividad.setNombre("TipoActividad1");
    nuevoTipoActividad.setActivo(Boolean.TRUE);

    restTemplate.withBasicAuth("user", "secret").postForEntity(TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH, nuevoTipoActividad,
        TipoActividad.class);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeTipoActividad_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<TipoActividad> response = restTemplate.withBasicAuth("user", "secret").exchange(
        TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, null, TipoActividad.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeTipoActividad_DoNotGetTipoActividad() throws Exception {
    restTemplate.withBasicAuth("user", "secret").delete(TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L);

    final ResponseEntity<TipoActividad> response = restTemplate.withBasicAuth("user", "secret")
        .getForEntity(TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, TipoActividad.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceTipoActividad_ReturnsTipoActividad() throws Exception {

    TipoActividad replaceTipoActividad = generarMockTipoActividad(1L, "TipoActividad1");

    final HttpEntity<TipoActividad> requestEntity = new HttpEntity<TipoActividad>(replaceTipoActividad,
        new HttpHeaders());

    final ResponseEntity<TipoActividad> response = restTemplate.withBasicAuth("user", "secret").exchange(

        TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, requestEntity, TipoActividad.class,
        1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final TipoActividad tipoActividad = response.getBody();

    Assertions.assertThat(tipoActividad.getId()).isNotNull();
    Assertions.assertThat(tipoActividad.getNombre()).isEqualTo(replaceTipoActividad.getNombre());
    Assertions.assertThat(tipoActividad.getActivo()).isEqualTo(replaceTipoActividad.getActivo());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsTipoActividadSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "3");

    URI uri = UriComponentsBuilder.fromUriString(TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH).build(false).toUri();

    final ResponseEntity<List<TipoActividad>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<TipoActividad>>() {
        });

    // then: Respuesta OK, TipoActividades retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoActividad> tipoActividades = response.getBody();
    Assertions.assertThat(tipoActividades.size()).isEqualTo(2);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("5");

    // Contiene de nombre='Trabajo Fin de Máster' y 'Trabajo Fin de Grado'
    Assertions.assertThat(tipoActividades.get(0).getNombre()).isEqualTo("Trabajo Fin de Máster");
    Assertions.assertThat(tipoActividades.get(1).getNombre()).isEqualTo("Trabajo Fin de Grado");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredTipoActividadList() throws Exception {
    // when: Búsqueda por nombre like e id equals
    Long id = 3L;
    String query = "nombre~Tesis%,id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH).queryParam("q", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoActividad>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, null, new ParameterizedTypeReference<List<TipoActividad>>() {
        });

    // then: Respuesta OK, TipoActividades retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoActividad> tipoActividades = response.getBody();
    Assertions.assertThat(tipoActividades.size()).isEqualTo(1);
    Assertions.assertThat(tipoActividades.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(tipoActividades.get(0).getNombre()).startsWith("Tesis doctoral");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedTipoActividadList() throws Exception {
    // when: Ordenación por nombre desc
    String query = "nombre-";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH).queryParam("s", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoActividad>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, null, new ParameterizedTypeReference<List<TipoActividad>>() {
        });

    // then: Respuesta OK, TipoActividades retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoActividad> tipoActividades = response.getBody();
    Assertions.assertThat(tipoActividades.size()).isEqualTo(5);
    Assertions.assertThat(tipoActividades.get(0).getId()).isEqualTo(4);
    Assertions.assertThat(tipoActividades.get(0).getNombre()).isEqualTo("Trabajo Fin de Máster");
    Assertions.assertThat(tipoActividades.get(4).getId()).isEqualTo(1);
    Assertions.assertThat(tipoActividades.get(4).getNombre()).isEqualTo("Proyecto de investigación");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsTipoActividadSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "4");
    // when: Ordena por nombre desc
    String sort = "nombre-";
    // when: Filtra por nombre like
    String filter = "nombre~%Trabajo%";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_ACTIVIDAD_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<TipoActividad>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<TipoActividad>>() {
        });

    // then: Respuesta OK, TipoActividades retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoActividad> tipoActividades = response.getBody();
    Assertions.assertThat(tipoActividades.size()).isEqualTo(2);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("4");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("2");

    // Contiene nombre='Trabajo Fin de Grado', 'Trabajo Fin de Máster'
    Assertions.assertThat(tipoActividades.get(0).getNombre()).isEqualTo("Trabajo Fin de Máster");
    Assertions.assertThat(tipoActividades.get(1).getNombre()).isEqualTo("Trabajo Fin de Grado");

  }

  /**
   * Función que devuelve un objeto TipoActividad
   * 
   * @param id     id del tipoActividad
   * @param nombre la descripción del tipo de actividad
   * @return el objeto tipo actividad
   */

  public TipoActividad generarMockTipoActividad(Long id, String nombre) {

    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(id);
    tipoActividad.setNombre(nombre);
    tipoActividad.setActivo(Boolean.TRUE);

    return tipoActividad;
  }

}