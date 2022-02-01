package org.crue.hercules.sgi.prc.integration;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.prc.controller.ProduccionCientificaApiController;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiCreateInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiCreateInput.TipoEstadoProduccionCientifica;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput.AcreditacionInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput.AutorInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput.CampoProduccionCientificaInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput.IndiceImpactoInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput.ProyectoInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiFullOutput;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.IndiceImpacto.TipoFuenteImpacto;
import org.crue.hercules.sgi.prc.model.IndiceImpacto.TipoRanking;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica.EpigrafeCVN;
import org.crue.hercules.sgi.prc.repository.AcreditacionRepository;
import org.crue.hercules.sgi.prc.repository.AutorRepository;
import org.crue.hercules.sgi.prc.repository.CampoProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.IndiceImpactoRepository;
import org.crue.hercules.sgi.prc.repository.ProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.ProyectoRepository;
import org.crue.hercules.sgi.prc.repository.ValorCampoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

/**
 * Test de integracion de ProduccionCientifica.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProduccionCientificaApiIT extends BaseIT {

  @Autowired
  private CampoProduccionCientificaRepository campoProduccionCientificaRepository;

  @Autowired
  private ProduccionCientificaRepository produccionCientificaRepository;

  @Autowired
  private ValorCampoRepository valorCampoRepository;

  @Autowired
  private AutorRepository autorRepository;

  @Autowired
  private IndiceImpactoRepository indiceImpactoRepository;

  @Autowired
  private AcreditacionRepository acreditacionRepository;

  @Autowired
  private ProyectoRepository proyectoRepository;

  private static final String PATH_PARAMETER_PRODUCCION_CIENTIFICA_REF = "/{produccionCientificaRef}";
  private static final String CONTROLLER_BASE_PATH = ProduccionCientificaApiController.MAPPING;

  private static final String PRODUCCION_CIENTIFICA_REF_VALUE = "produccion-cientifica-ref-";

  private HttpEntity<ProduccionCientificaApiInput> buildRequest(HttpHeaders headers,
      ProduccionCientificaApiInput entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s",
            tokenBuilder.buildToken("user", "AUTH")));

    HttpEntity<ProduccionCientificaApiInput> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsProduccionCientifica() throws Exception {
    ProduccionCientificaApiCreateInput produccionCientifica = generarMockProduccionCientificaApiInput();

    final ResponseEntity<ProduccionCientificaApiFullOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, produccionCientifica), ProduccionCientificaApiFullOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    ProduccionCientificaApiFullOutput produccionCientificaCreado = response.getBody();
    Assertions.assertThat(produccionCientificaCreado.getProduccionCientificaRef()).as("getProduccionCientificaRef()")
        .isEqualTo(produccionCientifica.getProduccionCientificaRef());

    Long produccionCientificaId = produccionCientificaRepository
        .findByProduccionCientificaRef(produccionCientificaCreado.getProduccionCientificaRef()).get().getId();
    List<CampoProduccionCientifica> campos = campoProduccionCientificaRepository
        .findAllByProduccionCientificaId(produccionCientificaId);
    Assertions.assertThat(campos.size()).as("number of campos created").isEqualTo(3);

    campos.stream()
        .forEach(campo -> Assertions.assertThat(valorCampoRepository.findAllByCampoProduccionCientificaId(campo.getId())
            .size()).as(String.format("number of valores created of campo [%s]", campo.getCodigoCVN().getInternValue()))
            .isEqualTo(2));
    Assertions.assertThat(autorRepository.findAllByProduccionCientificaId(produccionCientificaId)
        .size()).as("number of autores created").isEqualTo(3);
    Assertions.assertThat(indiceImpactoRepository.findAllByProduccionCientificaId(produccionCientificaId)
        .size()).as("number of indicesImpacto created").isEqualTo(3);
    Assertions.assertThat(acreditacionRepository.findAllByProduccionCientificaId(produccionCientificaId)
        .size()).as("number of acreditaciones created").isEqualTo(3);
    Assertions.assertThat(proyectoRepository.findAllByProduccionCientificaId(produccionCientificaId)
        .size()).as("number of proyectos created").isEqualTo(3);
  }

  @Test
  public void create_ReturnsValidationExceptionProduccionCientificaRefEmpty() throws Exception {
    ProduccionCientificaApiCreateInput produccionCientifica = generarMockProduccionCientificaApiInput();
    produccionCientifica.setProduccionCientificaRef(null);

    callCreateAndValidateResponse(produccionCientifica, HttpStatus.BAD_REQUEST);
  }

  @Test
  public void create_ReturnsValidationExceptionCampoEmpty() throws Exception {
    ProduccionCientificaApiCreateInput produccionCientifica = generarMockProduccionCientificaApiInput();
    produccionCientifica.setCampos(null);

    callCreateAndValidateResponse(produccionCientifica, HttpStatus.BAD_REQUEST);
  }

  @Test
  public void create_ReturnsValidationExceptionValorCampoEmpty() throws Exception {
    ProduccionCientificaApiCreateInput produccionCientifica = generarMockProduccionCientificaApiInput();
    produccionCientifica.getCampos().forEach(campo -> campo.setValores(null));

    callCreateAndValidateResponse(produccionCientifica, HttpStatus.BAD_REQUEST);
  }

  @Test
  public void create_ReturnsValidationExceptionCampoDuplicated() throws Exception {
    ProduccionCientificaApiInput produccionCientifica = generarMockProduccionCientificaApiInput();
    produccionCientifica.getCampos().add(produccionCientifica.getCampos().get(0));

    callCreateAndValidateResponse(produccionCientifica, HttpStatus.BAD_REQUEST);
  }

  @Test
  public void create_ReturnsValidationExceptionAutorDuplicated() throws Exception {
    ProduccionCientificaApiInput produccionCientifica = generarMockProduccionCientificaApiInput();
    produccionCientifica.getAutores().add(produccionCientifica.getAutores().get(0));

    callCreateAndValidateResponse(produccionCientifica, HttpStatus.BAD_REQUEST);
  }

  @Test
  public void create_ReturnsValidationExceptionAutorFirmaEmpty() throws Exception {
    ProduccionCientificaApiInput produccionCientifica = generarMockProduccionCientificaApiInput();
    produccionCientifica.getAutores().get(0).setFirma(null);

    callCreateAndValidateResponse(produccionCientifica, HttpStatus.BAD_REQUEST);
  }

  @Test
  public void create_ReturnsValidationExceptionAutorPersonaRefEmpty() throws Exception {
    ProduccionCientificaApiInput produccionCientifica = generarMockProduccionCientificaApiInput();
    produccionCientifica.getAutores().get(1).setPersonaRef(null);

    callCreateAndValidateResponse(produccionCientifica, HttpStatus.BAD_REQUEST);
  }

  @Test
  public void create_ReturnsValidationExceptionAutorNombreEmpty() throws Exception {
    ProduccionCientificaApiInput produccionCientifica = generarMockProduccionCientificaApiInput();
    produccionCientifica.getAutores().get(2).setNombre(null);

    callCreateAndValidateResponse(produccionCientifica, HttpStatus.BAD_REQUEST);
  }

  @Test
  public void create_ReturnsValidationExceptionAutorFirmaDuplicated() throws Exception {
    ProduccionCientificaApiInput produccionCientifica = generarMockProduccionCientificaApiInput();
    produccionCientifica.getAutores().get(1).setPersonaRef(null);
    produccionCientifica.getAutores().get(1).setFirma(produccionCientifica.getAutores().get(0).getFirma());

    callCreateAndValidateResponse(produccionCientifica, HttpStatus.BAD_REQUEST);
  }

  @Test
  public void create_ReturnsValidationExceptionAutorPersonaRefDuplicated() throws Exception {
    ProduccionCientificaApiInput produccionCientifica = generarMockProduccionCientificaApiInput();
    produccionCientifica.getAutores().get(0).setFirma(null);
    produccionCientifica.getAutores().get(0).setPersonaRef(produccionCientifica.getAutores().get(1).getPersonaRef());

    callCreateAndValidateResponse(produccionCientifica, HttpStatus.BAD_REQUEST);
  }

  @Test
  public void create_ReturnsValidationExceptionIndiceImpactoTipoFuenteEmpty() throws Exception {
    ProduccionCientificaApiInput produccionCientifica = generarMockProduccionCientificaApiInput();
    produccionCientifica.getIndicesImpacto().get(0).setTipoFuenteImpacto(null);

    callCreateAndValidateResponse(produccionCientifica, HttpStatus.BAD_REQUEST);
  }

  @Test
  public void create_ReturnsValidationExceptionIndiceImpactoTipoFuenteDuplicated() throws Exception {
    ProduccionCientificaApiInput produccionCientifica = generarMockProduccionCientificaApiInput();
    produccionCientifica.getIndicesImpacto().add(produccionCientifica.getIndicesImpacto().get(0));
    callCreateAndValidateResponse(produccionCientifica, HttpStatus.BAD_REQUEST);
  }

  private void callCreateAndValidateResponse(ProduccionCientificaApiInput produccionCientifica, HttpStatus httpStatus)
      throws Exception {
    final ResponseEntity<ProduccionCientificaApiFullOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, produccionCientifica),
        ProduccionCientificaApiFullOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(httpStatus);
  }

  @Test
  public void create_ReturnsValidationExceptionAcreditacionUrlEmpty() throws Exception {
    ProduccionCientificaApiInput produccionCientifica = generarMockProduccionCientificaApiInput();
    produccionCientifica.getAcreditaciones().get(0).setUrl(null);

    callCreateAndValidateResponse(produccionCientifica, HttpStatus.BAD_REQUEST);
  }

  @Test
  public void create_ReturnsValidationExceptionAcreditacionDuplicated() throws Exception {
    ProduccionCientificaApiInput produccionCientifica = generarMockProduccionCientificaApiInput();
    produccionCientifica.getAcreditaciones().add(produccionCientifica.getAcreditaciones().get(0));
    callCreateAndValidateResponse(produccionCientifica, HttpStatus.BAD_REQUEST);
  }

  @Test
  public void create_ReturnsValidationExceptionAcreditacionDuplicated2() throws Exception {
    ProduccionCientificaApiInput produccionCientifica = generarMockProduccionCientificaApiInput();
    produccionCientifica.getAcreditaciones().get(0).setUrl(null);
    produccionCientifica.getAcreditaciones().get(0).setDocumentoRef(
        produccionCientifica.getAcreditaciones().get(1).getDocumentoRef());

    callCreateAndValidateResponse(produccionCientifica, HttpStatus.BAD_REQUEST);
  }

  @Test
  public void create_ReturnsValidationExceptionProyectoRefEmpty() throws Exception {
    ProduccionCientificaApiInput produccionCientifica = generarMockProduccionCientificaApiInput();
    produccionCientifica.getProyectos().get(0).setProyectoRef(null);

    callCreateAndValidateResponse(produccionCientifica, HttpStatus.BAD_REQUEST);
  }

  @Test
  public void create_ReturnsValidationExceptionProyectoDuplicated() throws Exception {
    ProduccionCientificaApiInput produccionCientifica = generarMockProduccionCientificaApiInput();
    produccionCientifica.getProyectos().add(produccionCientifica.getProyectos().get(0));
    callCreateAndValidateResponse(produccionCientifica, HttpStatus.BAD_REQUEST);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql"
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsUniqueExceptionProduccionCientificaRef() throws Exception {
    ProduccionCientificaApiCreateInput produccionCientifica = generarMockProduccionCientificaApiInput();
    produccionCientifica.setProduccionCientificaRef(PRODUCCION_CIENTIFICA_REF_VALUE + "001");

    callCreateAndValidateResponse(produccionCientifica, HttpStatus.BAD_REQUEST);
  }

  @Test
  public void update_ReturnsProduccionCientificaRefException() throws Exception {
    ProduccionCientificaApiCreateInput produccionCientifica = generarMockProduccionCientificaApiInput();
    callUpdateAndValidateResponse(produccionCientifica, produccionCientifica.getProduccionCientificaRef(),
        HttpStatus.NOT_FOUND);
  }

  private void callUpdateAndValidateResponse(ProduccionCientificaApiCreateInput produccionCientifica,
      String produccionCientificaRef,
      HttpStatus httpStatus) throws Exception {
    final ResponseEntity<ProduccionCientificaApiFullOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_PRODUCCION_CIENTIFICA_REF, HttpMethod.PUT,
        buildRequest(null, produccionCientifica),
        ProduccionCientificaApiFullOutput.class, produccionCientificaRef);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(httpStatus);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsEstadoException() throws Exception {
    ProduccionCientificaApiCreateInput produccionCientifica = generarMockProduccionCientificaApiInput();

    callUpdateAndValidateResponse(produccionCientifica, PRODUCCION_CIENTIFICA_REF_VALUE + "001",
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void deleteByProduccionCientificaRef_Return204() throws Exception {
    String produccionCientificaRef = PRODUCCION_CIENTIFICA_REF_VALUE + "001";
    // when: exists by produccionCientificaRef
    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_PRODUCCION_CIENTIFICA_REF,
        HttpMethod.DELETE, buildRequest(null, null), Void.class, produccionCientificaRef);
    // then: 204 NO CONTENT
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  public void delete_ReturnProduccionCientificaRefNotFoundException() throws Exception {
    String produccionCientificaRef = "ANY";

    // when: exists by produccionCientificaRef
    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_PRODUCCION_CIENTIFICA_REF,
        HttpMethod.DELETE, buildRequest(null, null), Void.class, produccionCientificaRef);
    // then: 204 NO CONTENT
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  private ProduccionCientificaApiCreateInput generarMockProduccionCientificaApiInput() {
    ProduccionCientificaApiCreateInput produccionCientifica = new ProduccionCientificaApiCreateInput();
    produccionCientifica.setProduccionCientificaRef(PRODUCCION_CIENTIFICA_REF_VALUE + "001");
    produccionCientifica.setEpigrafe(EpigrafeCVN.E060_010_010_000.getInternValue());
    produccionCientifica.setEstado(TipoEstadoProduccionCientifica.PENDIENTE);

    produccionCientifica.setCampos(new ArrayList<>());
    String codigoCVN1 = CampoProduccionCientifica.CodigoCVN.E060_010_010_010.getInternValue();
    produccionCientifica.getCampos().add(addValores(codigoCVN1));
    String codigoCVN2 = CampoProduccionCientifica.CodigoCVN.E060_010_010_030.getInternValue();
    produccionCientifica.getCampos().add(addValores(codigoCVN2));
    String codigoCVN3 = CampoProduccionCientifica.CodigoCVN.E060_010_010_090.getInternValue();
    produccionCientifica.getCampos().add(addValores(codigoCVN3));

    produccionCientifica.setAutores(new ArrayList<>());
    AutorInput autor = new AutorInput();
    autor.setFirma("firma");
    autor.setOrcidId("orcidId");
    autor.setOrden(1);
    autor.setIp(Boolean.FALSE);
    produccionCientifica.getAutores().add(autor);
    autor = new AutorInput();
    autor.setPersonaRef("personaRef");
    autor.setOrcidId("orcidId");
    autor.setOrden(1);
    autor.setIp(Boolean.FALSE);
    produccionCientifica.getAutores().add(autor);
    autor = new AutorInput();
    autor.setNombre("nombre");
    autor.setApellidos("apellidos");
    autor.setOrcidId("orcidId");
    autor.setOrden(1);
    autor.setIp(Boolean.FALSE);
    produccionCientifica.getAutores().add(autor);

    produccionCientifica.setIndicesImpacto(new ArrayList<>());
    IndiceImpactoInput indiceImpacto = new IndiceImpactoInput();
    indiceImpacto.setRanking(TipoRanking.CLASE1);
    indiceImpacto.setTipoFuenteImpacto(TipoFuenteImpacto.BCI.getInternValue());
    indiceImpacto.setAnio(2022);
    indiceImpacto.setNumeroRevistas(BigDecimal.ZERO);
    indiceImpacto.setPosicionPublicacion(new BigDecimal(1));
    indiceImpacto.setOtraFuenteImpacto("otraFuenteImpacto");
    indiceImpacto.setRevista25(Boolean.TRUE);
    produccionCientifica.getIndicesImpacto().add(indiceImpacto);
    indiceImpacto = new IndiceImpactoInput();
    indiceImpacto.setRanking(TipoRanking.CLASE2);
    indiceImpacto.setTipoFuenteImpacto(TipoFuenteImpacto.CORE.getInternValue());
    indiceImpacto.setAnio(2022);
    indiceImpacto.setNumeroRevistas(BigDecimal.ZERO);
    indiceImpacto.setPosicionPublicacion(new BigDecimal(3));
    indiceImpacto.setOtraFuenteImpacto("otraFuenteImpacto2");
    indiceImpacto.setRevista25(Boolean.TRUE);
    produccionCientifica.getIndicesImpacto().add(indiceImpacto);
    indiceImpacto = new IndiceImpactoInput();
    indiceImpacto.setRanking(TipoRanking.CLASE3);
    indiceImpacto.setTipoFuenteImpacto(TipoFuenteImpacto.DIALNET.getInternValue());
    indiceImpacto.setAnio(2022);
    indiceImpacto.setNumeroRevistas(BigDecimal.ZERO);
    indiceImpacto.setPosicionPublicacion(new BigDecimal(2));
    indiceImpacto.setOtraFuenteImpacto("otraFuenteImpacto3");
    indiceImpacto.setRevista25(Boolean.FALSE);
    produccionCientifica.getIndicesImpacto().add(indiceImpacto);

    produccionCientifica.setAcreditaciones(new ArrayList<>());
    produccionCientifica.getAcreditaciones().add(AcreditacionInput.builder().url("url").build());
    produccionCientifica.getAcreditaciones().add(AcreditacionInput.builder().documentoRef("documentoRef").build());
    produccionCientifica.getAcreditaciones().add(AcreditacionInput.builder().documentoRef("documentoRef2").build());

    produccionCientifica.setProyectos(new ArrayList<>());
    produccionCientifica.getProyectos().add(ProyectoInput.builder().proyectoRef(1L).build());
    produccionCientifica.getProyectos().add(ProyectoInput.builder().proyectoRef(2L).build());
    produccionCientifica.getProyectos().add(ProyectoInput.builder().proyectoRef(3L).build());

    return produccionCientifica;
  }

  private CampoProduccionCientificaInput addValores(String codigoCVN1) {
    CampoProduccionCientificaInput campo = new CampoProduccionCientificaInput();
    campo.setCodigo(codigoCVN1);
    campo.setValores(Stream.of(codigoCVN1 + "_0", codigoCVN1 + "_1").collect(Collectors.toList()));
    return campo;
  }

}
