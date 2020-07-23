package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
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
 * Test de integracion de TipoEstadoMemoria.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("SECURITY_MOCK")

public class TipoEstadoMemoriaIT {

  @Autowired
  private TestRestTemplate restTemplate;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String TIPO_ESTADO_MEMORIA_CONTROLLER_BASE_PATH = "/tipoestadomemorias";

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
          .authorities("ETI-TIPOESTADOMEMORIA-EDITAR", "ETI-TIPOESTADOMEMORIA-VER");
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
  public void getTipoEstadoMemoria_WithId_ReturnsTipoEstadoMemoria() throws Exception {
    final ResponseEntity<TipoEstadoMemoria> response = restTemplate.withBasicAuth("user", "secret")
        .getForEntity(TIPO_ESTADO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, TipoEstadoMemoria.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final TipoEstadoMemoria tipoEstadoMemoria = response.getBody();

    Assertions.assertThat(tipoEstadoMemoria.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoEstadoMemoria.getNombre()).isEqualTo("En elaboración");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addTipoEstadoMemoria_ReturnsTipoEstadoMemoria() throws Exception {

    TipoEstadoMemoria nuevoTipoEstadoMemoria = new TipoEstadoMemoria();
    nuevoTipoEstadoMemoria.setNombre("TipoEstadoMemoria1");
    nuevoTipoEstadoMemoria.setActivo(Boolean.TRUE);

    restTemplate.withBasicAuth("user", "secret").postForEntity(TIPO_ESTADO_MEMORIA_CONTROLLER_BASE_PATH,
        nuevoTipoEstadoMemoria, TipoEstadoMemoria.class);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeTipoEstadoMemoria_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<TipoEstadoMemoria> response = restTemplate.withBasicAuth("user", "secret").exchange(
        TIPO_ESTADO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, null, TipoEstadoMemoria.class,
        id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeTipoEstadoMemoria_DoNotGetTipoEstadoMemoria() throws Exception {
    restTemplate.withBasicAuth("user", "secret").delete(TIPO_ESTADO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        1L);

    final ResponseEntity<TipoEstadoMemoria> response = restTemplate.withBasicAuth("user", "secret")
        .getForEntity(TIPO_ESTADO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, TipoEstadoMemoria.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceTipoEstadoMemoria_ReturnsTipoEstadoMemoria() throws Exception {

    TipoEstadoMemoria replaceTipoEstadoMemoria = generarMockTipoEstadoMemoria(1L, "TipoEstadoMemoria1");

    final HttpEntity<TipoEstadoMemoria> requestEntity = new HttpEntity<TipoEstadoMemoria>(replaceTipoEstadoMemoria,
        new HttpHeaders());

    final ResponseEntity<TipoEstadoMemoria> response = restTemplate.withBasicAuth("user", "secret").exchange(

        TIPO_ESTADO_MEMORIA_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, requestEntity,
        TipoEstadoMemoria.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final TipoEstadoMemoria tipoEstadoMemoria = response.getBody();

    Assertions.assertThat(tipoEstadoMemoria.getId()).isNotNull();
    Assertions.assertThat(tipoEstadoMemoria.getNombre()).isEqualTo(replaceTipoEstadoMemoria.getNombre());
    Assertions.assertThat(tipoEstadoMemoria.getActivo()).isEqualTo(replaceTipoEstadoMemoria.getActivo());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsTipoEstadoMemoriaSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    URI uri = UriComponentsBuilder.fromUriString(TIPO_ESTADO_MEMORIA_CONTROLLER_BASE_PATH).build(false).toUri();

    final ResponseEntity<List<TipoEstadoMemoria>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<TipoEstadoMemoria>>() {
        });

    // then: Respuesta OK, TipoEstadoMemorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoEstadoMemoria> tipoEstadoMemorias = response.getBody();
    Assertions.assertThat(tipoEstadoMemorias.size()).isEqualTo(5);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("10");

    // Contiene de nombre='Favorable Pendiente de Modificaciones Mínimas', 'No
    // procede evaluar', 'Fin evaluación' y
    // 'Archivado'
    Assertions.assertThat(tipoEstadoMemorias.get(0).getNombre())
        .isEqualTo("Favorable Pendiente de Modificaciones Mínimas");
    Assertions.assertThat(tipoEstadoMemorias.get(1).getNombre()).isEqualTo("Pendiente de correcciones");
    Assertions.assertThat(tipoEstadoMemorias.get(2).getNombre()).isEqualTo("No procede evaluar");
    Assertions.assertThat(tipoEstadoMemorias.get(3).getNombre()).isEqualTo("Fin evaluación");
    Assertions.assertThat(tipoEstadoMemorias.get(4).getNombre()).isEqualTo("Archivado");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredTipoEstadoMemoriaList() throws Exception {
    // when: Búsqueda por nombre like e id equals
    Long id = 5L;
    String query = "nombre~en%,id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(TIPO_ESTADO_MEMORIA_CONTROLLER_BASE_PATH).queryParam("q", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoEstadoMemoria>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, null, new ParameterizedTypeReference<List<TipoEstadoMemoria>>() {
        });

    // then: Respuesta OK, TipoEstadoMemorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoEstadoMemoria> tipoEstadoMemorias = response.getBody();
    Assertions.assertThat(tipoEstadoMemorias.size()).isEqualTo(1);
    Assertions.assertThat(tipoEstadoMemorias.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(tipoEstadoMemorias.get(0).getNombre()).startsWith("En evaluación");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedTipoEstadoMemoriaList() throws Exception {
    // when: Ordenación por nombre desc
    String query = "nombre-";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_ESTADO_MEMORIA_CONTROLLER_BASE_PATH).queryParam("s", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoEstadoMemoria>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, null, new ParameterizedTypeReference<List<TipoEstadoMemoria>>() {
        });

    // then: Respuesta OK, TipoEstadoMemorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoEstadoMemoria> tipoEstadoMemorias = response.getBody();
    Assertions.assertThat(tipoEstadoMemorias.size()).isEqualTo(10);

    Assertions.assertThat(tipoEstadoMemorias.get(0).getId()).isEqualTo(7);
    Assertions.assertThat(tipoEstadoMemorias.get(0).getNombre()).isEqualTo("Pendiente de correcciones");
    Assertions.assertThat(tipoEstadoMemorias.get(1).getId()).isEqualTo(8);
    Assertions.assertThat(tipoEstadoMemorias.get(1).getNombre()).isEqualTo("No procede evaluar");
    Assertions.assertThat(tipoEstadoMemorias.get(2).getId()).isEqualTo(9);
    Assertions.assertThat(tipoEstadoMemorias.get(2).getNombre()).isEqualTo("Fin evaluación");
    Assertions.assertThat(tipoEstadoMemorias.get(3).getId()).isEqualTo(6);
    Assertions.assertThat(tipoEstadoMemorias.get(3).getNombre())
        .isEqualTo("Favorable Pendiente de Modificaciones Mínimas");
    Assertions.assertThat(tipoEstadoMemorias.get(4).getId()).isEqualTo(4);
    Assertions.assertThat(tipoEstadoMemorias.get(4).getNombre()).isEqualTo("En secretaría revisión mínima");
    Assertions.assertThat(tipoEstadoMemorias.get(5).getId()).isEqualTo(3);
    Assertions.assertThat(tipoEstadoMemorias.get(5).getNombre()).isEqualTo("En secretaría");
    Assertions.assertThat(tipoEstadoMemorias.get(6).getId()).isEqualTo(5);
    Assertions.assertThat(tipoEstadoMemorias.get(6).getNombre()).isEqualTo("En evaluación");
    Assertions.assertThat(tipoEstadoMemorias.get(7).getId()).isEqualTo(1);
    Assertions.assertThat(tipoEstadoMemorias.get(7).getNombre()).isEqualTo("En elaboración");
    Assertions.assertThat(tipoEstadoMemorias.get(8).getId()).isEqualTo(2);
    Assertions.assertThat(tipoEstadoMemorias.get(8).getNombre()).isEqualTo("Completada");
    Assertions.assertThat(tipoEstadoMemorias.get(9).getId()).isEqualTo(10);
    Assertions.assertThat(tipoEstadoMemorias.get(9).getNombre()).isEqualTo("Archivado");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsTipoEstadoMemoriaSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por nombre desc
    String sort = "nombre-";
    // when: Filtra por nombre like
    String filter = "nombre~%en%";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_ESTADO_MEMORIA_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<TipoEstadoMemoria>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<TipoEstadoMemoria>>() {
        });

    // then: Respuesta OK, TipoEstadoMemorias retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoEstadoMemoria> tipoEstadoMemorias = response.getBody();
    Assertions.assertThat(tipoEstadoMemorias.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("6");

    // Contiene nombre='Pendiente de correcciones', 'Favorable Pendiente de
    // Modificaciones Mínimas',
    // 'En secretaría revisión mínima'
    Assertions.assertThat(tipoEstadoMemorias.get(0).getNombre()).isEqualTo("Pendiente de correcciones");
    Assertions.assertThat(tipoEstadoMemorias.get(1).getNombre())
        .isEqualTo("Favorable Pendiente de Modificaciones Mínimas");
    Assertions.assertThat(tipoEstadoMemorias.get(2).getNombre()).isEqualTo("En secretaría revisión mínima");

  }

  /**
   * Función que devuelve un objeto TipoEstadoMemoria
   * 
   * @param id     id del TipoEstadoMemoria
   * @param nombre nombre del TipoEstadoMemoria
   * @return el objeto TipoEstadoMemoria
   */

  public TipoEstadoMemoria generarMockTipoEstadoMemoria(Long id, String nombre) {

    TipoEstadoMemoria tipoEstadoMemoria = new TipoEstadoMemoria();
    tipoEstadoMemoria.setId(id);
    tipoEstadoMemoria.setNombre(nombre);
    tipoEstadoMemoria.setActivo(Boolean.TRUE);

    return tipoEstadoMemoria;
  }

}