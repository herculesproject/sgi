package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.TipoComentario;
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
 * Test de integracion de TipoComentario.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("SECURITY_MOCK")

public class TipoComentarioIT {

  @Autowired
  private TestRestTemplate restTemplate;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String TIPO_COMENTARIO_CONTROLLER_BASE_PATH = "/tipocomentarios";

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
          .authorities("ETI-TIPOCOMENTARIO-EDITAR", "ETI-TIPOCOMENTARIO-VER");
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
  public void getTipoComentario_WithId_ReturnsTipoComentario() throws Exception {
    final ResponseEntity<TipoComentario> response = restTemplate.withBasicAuth("user", "secret")
        .getForEntity(TIPO_COMENTARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, TipoComentario.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final TipoComentario tipoComentario = response.getBody();

    Assertions.assertThat(tipoComentario.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoComentario.getNombre()).isEqualTo("GESTOR");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addTipoComentario_ReturnsTipoComentario() throws Exception {

    TipoComentario nuevoTipoComentario = new TipoComentario();
    nuevoTipoComentario.setNombre("TipoComentario1");
    nuevoTipoComentario.setActivo(Boolean.TRUE);

    restTemplate.withBasicAuth("user", "secret").postForEntity(TIPO_COMENTARIO_CONTROLLER_BASE_PATH,
        nuevoTipoComentario, TipoComentario.class);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeTipoComentario_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<TipoComentario> response = restTemplate.withBasicAuth("user", "secret").exchange(
        TIPO_COMENTARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, null, TipoComentario.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeTipoComentario_DoNotGetTipoComentario() throws Exception {
    restTemplate.withBasicAuth("user", "secret").withBasicAuth("user", "secret")
        .delete(TIPO_COMENTARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L);

    final ResponseEntity<TipoComentario> response = restTemplate.withBasicAuth("user", "secret")
        .getForEntity(TIPO_COMENTARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, TipoComentario.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceTipoComentario_ReturnsTipoComentario() throws Exception {

    TipoComentario replaceTipoComentario = generarMockTipoComentario(1L, "TipoComentario1");

    final HttpEntity<TipoComentario> requestEntity = new HttpEntity<TipoComentario>(replaceTipoComentario,
        new HttpHeaders());

    final ResponseEntity<TipoComentario> response = restTemplate.withBasicAuth("user", "secret").exchange(

        TIPO_COMENTARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, requestEntity, TipoComentario.class,
        1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final TipoComentario tipoComentario = response.getBody();

    Assertions.assertThat(tipoComentario.getId()).isNotNull();
    Assertions.assertThat(tipoComentario.getNombre()).isEqualTo(replaceTipoComentario.getNombre());
    Assertions.assertThat(tipoComentario.getActivo()).isEqualTo(replaceTipoComentario.getActivo());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsTipoComentarioSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "1");

    URI uri = UriComponentsBuilder.fromUriString(TIPO_COMENTARIO_CONTROLLER_BASE_PATH).build(false).toUri();

    final ResponseEntity<List<TipoComentario>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<TipoComentario>>() {
        });

    // then: Respuesta OK, TipoComentarios retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoComentario> tipoComentarios = response.getBody();
    Assertions.assertThat(tipoComentarios.size()).isEqualTo(1);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("2");

    // Contiene de nombre='EVALUADOR'
    Assertions.assertThat(tipoComentarios.get(0).getNombre()).isEqualTo("EVALUADOR");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredTipoComentarioList() throws Exception {
    // when: Búsqueda por nombre like e id equals
    Long id = 1L;
    String query = "nombre~gestor%,id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(TIPO_COMENTARIO_CONTROLLER_BASE_PATH).queryParam("q", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoComentario>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, null, new ParameterizedTypeReference<List<TipoComentario>>() {
        });

    // then: Respuesta OK, TipoComentarios retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoComentario> tipoComentarios = response.getBody();
    Assertions.assertThat(tipoComentarios.size()).isEqualTo(1);
    Assertions.assertThat(tipoComentarios.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(tipoComentarios.get(0).getNombre()).startsWith("GESTOR");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedTipoComentarioList() throws Exception {
    // when: Ordenación por nombre desc
    String query = "nombre-";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_COMENTARIO_CONTROLLER_BASE_PATH).queryParam("s", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoComentario>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, null, new ParameterizedTypeReference<List<TipoComentario>>() {
        });

    // then: Respuesta OK, TipoComentarios retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoComentario> tipoComentarios = response.getBody();
    Assertions.assertThat(tipoComentarios.size()).isEqualTo(2);
    Assertions.assertThat(tipoComentarios.get(0).getId()).isEqualTo(1);
    Assertions.assertThat(tipoComentarios.get(0).getNombre()).isEqualTo("GESTOR");
    Assertions.assertThat(tipoComentarios.get(1).getId()).isEqualTo(2);
    Assertions.assertThat(tipoComentarios.get(1).getNombre()).isEqualTo("EVALUADOR");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsTipoComentarioSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por nombre desc
    String sort = "nombre-";
    // when: Filtra por nombre like
    String filter = "nombre~%gest%";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_COMENTARIO_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<TipoComentario>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<TipoComentario>>() {
        });

    // then: Respuesta OK, TipoComentarios retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoComentario> tipoComentarios = response.getBody();
    Assertions.assertThat(tipoComentarios.size()).isEqualTo(1);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("1");

    // Contiene nombre='GESTOR'
    Assertions.assertThat(tipoComentarios.get(0).getNombre()).isEqualTo("GESTOR");

  }

  /**
   * Función que devuelve un objeto TipoComentario
   * 
   * @param id     id del TipoComentario
   * @param nombre la descripción del TipoComentario
   * @return el objeto TipoComentario
   */

  public TipoComentario generarMockTipoComentario(Long id, String nombre) {

    TipoComentario tipoComentario = new TipoComentario();
    tipoComentario.setId(id);
    tipoComentario.setNombre(nombre);
    tipoComentario.setActivo(Boolean.TRUE);

    return tipoComentario;
  }

}