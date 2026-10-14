package org.crue.hercules.sgi.eti.service.sgi;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.config.RestApiProperties;
import org.crue.hercules.sgi.eti.dto.sgp.VinculacionOutput;
import org.crue.hercules.sgi.eti.exceptions.rep.MicroserviceCallException;
import org.crue.hercules.sgi.eti.service.BaseServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * SgiApiSgpServiceTest
 */
class SgiApiSgpServiceTest extends BaseServiceTest {

  private static final String SGP_URL = "http://sgp";

  @Mock
  private RestApiProperties restApiProperties;

  @Mock
  private RestTemplate restTemplate;

  private SgiApiSgpService sgiApiSgpService;

  @BeforeEach
  void setUp() {
    sgiApiSgpService = new SgiApiSgpService(restApiProperties, restTemplate);
  }

  @Test
  void findVinculacionByPersonaId_WithVinculacion_ReturnsVinculacion() {
    // given: el SGP devuelve la vinculación de la persona
    BDDMockito.given(restApiProperties.getSgpUrl()).willReturn(SGP_URL);
    VinculacionOutput vinculacion = VinculacionOutput.builder().id("V1")
        .departamento(VinculacionOutput.DepartamentoOutput.builder().id("DEP-1").nombre("Departamento").build())
        .build();
    mockExchange(new ResponseEntity<>(vinculacion, HttpStatus.OK));

    // when: se busca la vinculación de la persona
    Optional<VinculacionOutput> result = sgiApiSgpService.findVinculacionByPersonaId("persona-1");

    // then: se devuelve la vinculación con su departamento
    Assertions.assertThat(result).isPresent();
    Assertions.assertThat(result.get().getDepartamento().getId()).isEqualTo("DEP-1");
    BDDMockito.then(restTemplate).should().exchange(
        ArgumentMatchers.eq(SGP_URL + "/vinculaciones/persona/{id}"), ArgumentMatchers.eq(HttpMethod.GET),
        ArgumentMatchers.<HttpEntity<Void>>any(),
        ArgumentMatchers.<ParameterizedTypeReference<VinculacionOutput>>any(),
        ArgumentMatchers.eq("persona-1"));
  }

  @Test
  void findVinculacionByPersonaId_WithNoContent_ReturnsEmpty() {
    // given: la persona no tiene vinculación
    BDDMockito.given(restApiProperties.getSgpUrl()).willReturn(SGP_URL);
    mockExchange(new ResponseEntity<>(HttpStatus.NO_CONTENT));

    // when: se busca la vinculación de la persona
    Optional<VinculacionOutput> result = sgiApiSgpService.findVinculacionByPersonaId("persona-1");

    // then: no se devuelve vinculación
    Assertions.assertThat(result).isEmpty();
  }

  @Test
  void findVinculacionByPersonaId_WithError_ThrowsMicroserviceCallException() {
    // given: la llamada al SGP falla
    BDDMockito.given(restApiProperties.getSgpUrl()).willReturn(SGP_URL);
    BDDMockito.given(restTemplate.exchange(ArgumentMatchers.anyString(), ArgumentMatchers.eq(HttpMethod.GET),
        ArgumentMatchers.<HttpEntity<Void>>any(),
        ArgumentMatchers.<ParameterizedTypeReference<VinculacionOutput>>any(),
        ArgumentMatchers.<Object>any())).willThrow(new RestClientException("error"));

    Assertions.assertThatThrownBy(
        // when: se busca la vinculación de la persona
        () -> sgiApiSgpService.findVinculacionByPersonaId("persona-1"))
        // then: se lanza la excepción de llamada a microservicio
        .isInstanceOf(MicroserviceCallException.class);
  }

  private void mockExchange(ResponseEntity<VinculacionOutput> response) {
    BDDMockito.given(restTemplate.exchange(ArgumentMatchers.anyString(), ArgumentMatchers.eq(HttpMethod.GET),
        ArgumentMatchers.<HttpEntity<Void>>any(),
        ArgumentMatchers.<ParameterizedTypeReference<VinculacionOutput>>any(),
        ArgumentMatchers.<Object>any())).willReturn(response);
  }

}
