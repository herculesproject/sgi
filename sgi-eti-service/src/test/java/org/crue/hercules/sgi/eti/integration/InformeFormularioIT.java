package org.crue.hercules.sgi.eti.integration;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.FormularioMemoria;
import org.crue.hercules.sgi.eti.model.InformeFormulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
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
 * Test de integracion de InformeFormulario.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("SECURITY_MOCK")
public class InformeFormularioIT {

  @Autowired
  private TestRestTemplate restTemplate;

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String INFORME_FORMULARIO_CONTROLLER_BASE_PATH = "/informeformularios";

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
          .authorities("ETI-INFORMEFORMULARIO-EDITAR", "ETI-INFORMEFORMULARIO-VER");
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
  public void getInformeFormulario_WithId_ReturnsInformeFormulario() throws Exception {
    final ResponseEntity<InformeFormulario> response = restTemplate.withBasicAuth("user", "secret")
        .getForEntity(INFORME_FORMULARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, InformeFormulario.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final InformeFormulario informeFormulario = response.getBody();

    Assertions.assertThat(informeFormulario.getId()).isEqualTo(1L);
    Assertions.assertThat(informeFormulario.getDocumentoRef()).isEqualTo("DocumentoFormulario1");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void addInformeFormulario_ReturnsInformeFormulario() throws Exception {

    InformeFormulario nuevoInformeFormulario = new InformeFormulario();
    nuevoInformeFormulario.setDocumentoRef("DocumentoFormulario1");

    restTemplate.withBasicAuth("user", "secret").postForEntity(INFORME_FORMULARIO_CONTROLLER_BASE_PATH,
        nuevoInformeFormulario, InformeFormulario.class);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeInformeFormulario_Success() throws Exception {

    // when: Delete con id existente
    long id = 1L;
    final ResponseEntity<InformeFormulario> response = restTemplate.withBasicAuth("user", "secret").exchange(
        INFORME_FORMULARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.DELETE, null, InformeFormulario.class,
        id);

    // then: 200
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void removeInformeFormulario_DoNotGetInformeFormulario() throws Exception {
    restTemplate.withBasicAuth("user", "secret").delete(INFORME_FORMULARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        1L);

    final ResponseEntity<InformeFormulario> response = restTemplate.withBasicAuth("user", "secret")
        .getForEntity(INFORME_FORMULARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, InformeFormulario.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void replaceInformeFormulario_ReturnsInformeFormulario() throws Exception {

    InformeFormulario replaceInformeFormulario = generarMockInformeFormulario(1L, "DocumentoFormulario1");

    final HttpEntity<InformeFormulario> requestEntity = new HttpEntity<InformeFormulario>(replaceInformeFormulario,
        new HttpHeaders());

    final ResponseEntity<InformeFormulario> response = restTemplate.withBasicAuth("user", "secret").exchange(

        INFORME_FORMULARIO_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, requestEntity,
        InformeFormulario.class, 1L);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final InformeFormulario informeFormulario = response.getBody();

    Assertions.assertThat(informeFormulario.getId()).isNotNull();
    Assertions.assertThat(informeFormulario.getDocumentoRef()).isEqualTo(replaceInformeFormulario.getDocumentoRef());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPaging_ReturnsInformeFormularioSubList() throws Exception {
    // when: Obtiene la page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "1");
    headers.add("X-Page-Size", "5");

    URI uri = UriComponentsBuilder.fromUriString(INFORME_FORMULARIO_CONTROLLER_BASE_PATH).build(false).toUri();

    final ResponseEntity<List<InformeFormulario>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<InformeFormulario>>() {
        });

    // then: Respuesta OK, InformeFormularios retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<InformeFormulario> informeFormularios = response.getBody();
    Assertions.assertThat(informeFormularios.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("1");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("8");

    // Contiene de documentoRef='DocumentoFormulario6' a 'DocumentoFormulario8'
    Assertions.assertThat(informeFormularios.get(0).getDocumentoRef()).isEqualTo("DocumentoFormulario6");
    Assertions.assertThat(informeFormularios.get(1).getDocumentoRef()).isEqualTo("DocumentoFormulario7");
    Assertions.assertThat(informeFormularios.get(2).getDocumentoRef()).isEqualTo("DocumentoFormulario8");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSearchQuery_ReturnsFilteredInformeFormularioList() throws Exception {
    // when: Búsqueda por documentoRef like e id equals
    Long id = 5L;
    String query = "documentoRef~DocumentoFormulario%,id:" + id;

    URI uri = UriComponentsBuilder.fromUriString(INFORME_FORMULARIO_CONTROLLER_BASE_PATH).queryParam("q", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<InformeFormulario>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, null, new ParameterizedTypeReference<List<InformeFormulario>>() {
        });

    // then: Respuesta OK, DocumentoFormularios retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<InformeFormulario> informeFormularios = response.getBody();
    Assertions.assertThat(informeFormularios.size()).isEqualTo(1);
    Assertions.assertThat(informeFormularios.get(0).getId()).isEqualTo(id);
    Assertions.assertThat(informeFormularios.get(0).getDocumentoRef()).startsWith("DocumentoFormulario");
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithSortQuery_ReturnsOrderedInformeFormularioList() throws Exception {
    // when: Ordenación por documentoRef desc
    String query = "documentoRef-";

    URI uri = UriComponentsBuilder.fromUriString(INFORME_FORMULARIO_CONTROLLER_BASE_PATH).queryParam("s", query)
        .build(false).toUri();

    // when: Búsqueda por query
    final ResponseEntity<List<InformeFormulario>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, null, new ParameterizedTypeReference<List<InformeFormulario>>() {
        });

    // then: Respuesta OK, DocumentoFormularios retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<InformeFormulario> informeFormularios = response.getBody();
    Assertions.assertThat(informeFormularios.size()).isEqualTo(8);
    for (int i = 0; i < 8; i++) {
      InformeFormulario informeFormulario = informeFormularios.get(i);
      Assertions.assertThat(informeFormulario.getId()).isEqualTo(8 - i);
      Assertions.assertThat(informeFormulario.getDocumentoRef())
          .isEqualTo("DocumentoFormulario" + String.format("%03d", 8 - i));
    }
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsInformeFormularioSubList() throws Exception {
    // when: Obtiene page=3 con pagesize=10
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    // when: Ordena por documentoRef desc
    String sort = "documentoRef-";
    // when: Filtra por documentoRef like e id equals
    String filter = "documentoRef~%00%";

    URI uri = UriComponentsBuilder.fromUriString(INFORME_FORMULARIO_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<InformeFormulario>> response = restTemplate.withBasicAuth("user", "secret").exchange(uri,
        HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<InformeFormulario>>() {
        });

    // then: Respuesta OK, InformeFormularios retorna la información de la página
    // correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<InformeFormulario> informeFormularios = response.getBody();
    Assertions.assertThat(informeFormularios.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("3");

    // Contiene documentoRef='DocumentoFormulario001', 'DocumentoFormulario002',
    // 'DocumentoFormulario003'
    Assertions.assertThat(informeFormularios.get(0).getDocumentoRef())
        .isEqualTo("DocumentoFormulario" + String.format("%03d", 3));
    Assertions.assertThat(informeFormularios.get(1).getDocumentoRef())
        .isEqualTo("DocumentoFormulario" + String.format("%03d", 2));
    Assertions.assertThat(informeFormularios.get(2).getDocumentoRef())
        .isEqualTo("DocumentoFormulario" + String.format("%03d", 1));

  }

  /**
   * Función que devuelve un objeto InformeFormulario
   * 
   * @param id           id del InformeFormulario
   * @param documentoRef la referencia del documento
   * @return el objeto InformeFormulario
   */

  public InformeFormulario generarMockInformeFormulario(Long id, String documentoRef) {
    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(1L);
    tipoActividad.setNombre("TipoActividad1");
    tipoActividad.setActivo(Boolean.TRUE);

    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(id);
    peticionEvaluacion.setCodigo("Codigo1");
    peticionEvaluacion.setDisMetodologico("DiseñoMetodologico1");
    peticionEvaluacion.setExterno(Boolean.FALSE);
    peticionEvaluacion.setFechaFin(LocalDate.now());
    peticionEvaluacion.setFechaInicio(LocalDate.now());
    peticionEvaluacion.setFuenteFinanciacionRef("Referencia fuente financiacion");
    peticionEvaluacion.setObjetivos("Objetivos1");
    peticionEvaluacion.setOtroValorSocial("Otro valor social1");
    peticionEvaluacion.setResumen("Resumen");
    peticionEvaluacion.setSolicitudConvocatoriaRef("Referencia solicitud convocatoria");
    peticionEvaluacion.setTieneFondosPropios(Boolean.FALSE);
    peticionEvaluacion.setTipoActividad(tipoActividad);
    peticionEvaluacion.setTitulo("PeticionEvaluacion1");
    peticionEvaluacion.setUsuarioRef("user-001");
    peticionEvaluacion.setValorSocial(3);
    peticionEvaluacion.setActivo(Boolean.TRUE);

    Comite comite = new Comite(1L, "Comite1", Boolean.TRUE);

    TipoMemoria tipoMemoria = new TipoMemoria();
    tipoMemoria.setId(id);
    tipoMemoria.setNombre("TipoMemoria1");
    tipoMemoria.setActivo(Boolean.TRUE);

    Memoria memoria = new Memoria(1L, "numRef-001", peticionEvaluacion, comite, "Memoria1", "user-001", tipoMemoria,
        LocalDate.now(), Boolean.FALSE, LocalDate.now(), 3, new TipoEstadoMemoria(1L, "En elaboración", Boolean.TRUE));
    Formulario formulario = new Formulario(1L, "FORM-1", "Formulario1", Boolean.TRUE);
    FormularioMemoria formularioMemoria = new FormularioMemoria(1L, memoria, formulario, Boolean.TRUE);

    InformeFormulario informeFormulario = new InformeFormulario();
    informeFormulario.setId(id);
    informeFormulario.setDocumentoRef(documentoRef);
    informeFormulario.setFormularioMemoria(formularioMemoria);
    informeFormulario.setVersion(3);

    return informeFormulario;
  }

}