package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.TipoDocumento;
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
 * Test de integracion de TipoDocumento.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("SECURITY_MOCK")

public class TipoDocumentoIT {

  @Autowired
  private TestRestTemplate restTemplate;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String TIPO_DOCUMENTO_CONTROLLER_BASE_PATH = "/tipodocumentos";

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
          .authorities("ETI-TIPODOCUMENTO-EDITAR", "ETI-TIPODOCUMENTO-VER");
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
  public void getTipoDocumento_WithId_ReturnsTipoDocumento() throws Exception {

    Formulario formulario = new Formulario();
    formulario.setId(1L);
    formulario.setNombre("M10");
    formulario.setDescripcion("Formulario M10");
    formulario.setActivo(Boolean.TRUE);

    final ResponseEntity<TipoDocumento> response = restTemplate.withBasicAuth("user", "secret")
        .getForEntity(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, TipoDocumento.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final TipoDocumento tipoDocumento = response.getBody();

    Assertions.assertThat(tipoDocumento.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoDocumento.getNombre()).isEqualTo("TipoDocumento1");
    Assertions.assertThat(tipoDocumento.getFormulario()).isEqualTo(formulario);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addTipoDocumento_ReturnsTipoDocumento() throws Exception {

    TipoDocumento nuevoTipoDocumento = generarMockTipoDocumento(1L, "TipoDocumento1");

    restTemplate.withBasicAuth("user", "secret").postForEntity(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH, nuevoTipoDocumento,
        TipoDocumento.class);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeTipoDocumento_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<TipoDocumento> response = restTemplate.withBasicAuth("user", "secret").exchange(
        TIPO_DOCUMENTO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, null, TipoDocumento.class, id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeTipoDocumento_DoNotGetTipoDocumento() throws Exception {
    restTemplate.withBasicAuth("user", "secret").delete(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, 1L);

    final ResponseEntity<TipoDocumento> response = restTemplate.withBasicAuth("user", "secret")
        .getForEntity(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, TipoDocumento.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceTipoDocumento_ReturnsTipoDocumento() throws Exception {

    Formulario formulario = new Formulario();
    formulario.setId(1L);
    formulario.setNombre("M10");
    formulario.setDescripcion("Formulario M10");
    formulario.setActivo(Boolean.TRUE);

    TipoDocumento replaceTipoDocumento = generarMockTipoDocumento(1L, "TipoDocumento1");

    final HttpEntity<TipoDocumento> requestEntity = new HttpEntity<TipoDocumento>(replaceTipoDocumento,
        new HttpHeaders());

    final ResponseEntity<TipoDocumento> response = restTemplate.withBasicAuth("user", "secret").exchange(

        TIPO_DOCUMENTO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, requestEntity, TipoDocumento.class,
        1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final TipoDocumento tipoDocumento = response.getBody();

    Assertions.assertThat(tipoDocumento.getId()).isNotNull();
    Assertions.assertThat(tipoDocumento.getNombre()).isEqualTo(replaceTipoDocumento.getNombre());
    Assertions.assertThat(tipoDocumento.getFormulario()).isEqualTo(replaceTipoDocumento.getFormulario());
    Assertions.assertThat(tipoDocumento.getActivo()).isEqualTo(replaceTipoDocumento.getActivo());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsTipoDocumentoSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    URI uri = UriComponentsBuilder.fromUriString(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH).build(false).toUri();

    final ResponseEntity<List<TipoDocumento>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<TipoDocumento>>() {
        });

    // then: Respuesta OK, TipoDocumentos retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoDocumento> tipoDocumentos = response.getBody();
    Assertions.assertThat(tipoDocumentos.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("8");

    // Contiene de nombre='TipoDocumento6' a 'TipoDocumento8'
    Assertions.assertThat(tipoDocumentos.get(0).getNombre()).isEqualTo("TipoDocumento6");
    Assertions.assertThat(tipoDocumentos.get(1).getNombre()).isEqualTo("TipoDocumento7");
    Assertions.assertThat(tipoDocumentos.get(2).getNombre()).isEqualTo("TipoDocumento8");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredTipoDocumentoList() throws Exception {
    // when: Búsqueda por nombre like e id equals
    Long id = 5L;
    String query = "nombre~TipoDocumento%,id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH).queryParam("q", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoDocumento>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, null, new ParameterizedTypeReference<List<TipoDocumento>>() {
        });

    // then: Respuesta OK, TipoDocumentos retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoDocumento> tipoDocumentos = response.getBody();
    Assertions.assertThat(tipoDocumentos.size()).isEqualTo(1);
    Assertions.assertThat(tipoDocumentos.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(tipoDocumentos.get(0).getNombre()).startsWith("TipoDocumento");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedTipoDocumentoList() throws Exception {
    // when: Ordenación por nombre desc
    String query = "nombre-";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH).queryParam("s", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<TipoDocumento>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, null, new ParameterizedTypeReference<List<TipoDocumento>>() {
        });

    // then: Respuesta OK, TipoDocumentos retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoDocumento> tipoDocumentos = response.getBody();
    Assertions.assertThat(tipoDocumentos.size()).isEqualTo(8);
    for (int i = 0; i < 8; i++) {
      TipoDocumento tipoDocumento = tipoDocumentos.get(i);
      Assertions.assertThat(tipoDocumento.getId()).isEqualTo(8 - i);
      Assertions.assertThat(tipoDocumento.getNombre()).isEqualTo("TipoDocumento" + String.format("%03d", 8 - i));
    }
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsTipoDocumentoSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por nombre desc
    String sort = "nombre-";
    // when: Filtra por nombre like e id equals
    String filter = "nombre~%00%";

    URI uri = UriComponentsBuilder.fromUriString(TIPO_DOCUMENTO_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<TipoDocumento>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<TipoDocumento>>() {
        });

    // then: Respuesta OK, TipoDocumentos retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<TipoDocumento> tipoDocumentos = response.getBody();
    Assertions.assertThat(tipoDocumentos.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("3");

    // Contiene nombre='TipoDocumento001', 'TipoDocumento002',
    // 'TipoDocumento003'
    Assertions.assertThat(tipoDocumentos.get(0).getNombre()).isEqualTo("TipoDocumento" + String.format("%03d", 3));
    Assertions.assertThat(tipoDocumentos.get(1).getNombre()).isEqualTo("TipoDocumento" + String.format("%03d", 2));
    Assertions.assertThat(tipoDocumentos.get(2).getNombre()).isEqualTo("TipoDocumento" + String.format("%03d", 1));

  }

  /**
   * Función que devuelve un objeto TipoDocumento
   * 
   * @param id     id del tipoDocumento
   * @param nombre la descripción del tipoDocumento
   * @return el objeto tipoDocumento
   */

  public TipoDocumento generarMockTipoDocumento(Long id, String nombre) {

    Formulario formulario = new Formulario();
    formulario.setId(1L);
    formulario.setNombre("M10");
    formulario.setDescripcion("Formulario M10");
    formulario.setActivo(Boolean.TRUE);

    TipoDocumento tipoDocumento = new TipoDocumento();
    tipoDocumento.setId(id);
    tipoDocumento.setNombre(nombre);
    tipoDocumento.setFormulario(formulario);
    tipoDocumento.setActivo(Boolean.TRUE);

    return tipoDocumento;
  }

}