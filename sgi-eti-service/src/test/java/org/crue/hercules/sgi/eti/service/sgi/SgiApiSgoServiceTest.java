package org.crue.hercules.sgi.eti.service.sgi;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.config.RestApiProperties;
import org.crue.hercules.sgi.eti.dto.sgo.UnidadVinculacionOutput;
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
 * SgiApiSgoServiceTest
 */
class SgiApiSgoServiceTest extends BaseServiceTest {

  private static final String SGO_URL = "http://sgo";

  @Mock
  private RestApiProperties restApiProperties;

  @Mock
  private RestTemplate restTemplate;

  private SgiApiSgoService sgiApiSgoService;

  @BeforeEach
  void setUp() {
    sgiApiSgoService = new SgiApiSgoService(restApiProperties, restTemplate);
  }

  @Test
  void findUnidadVinculacionById_WithUnidad_ReturnsUnidad() {
    // given: el SGO devuelve la unidad buscada
    BDDMockito.given(restApiProperties.getSgoUrl()).willReturn(SGO_URL);
    UnidadVinculacionOutput unidad = UnidadVinculacionOutput.builder().id("DEP-1").predecesorId("TIPO-DEP")
        .activo(Boolean.TRUE).build();
    mockExchange(new ResponseEntity<>(Arrays.asList(unidad), HttpStatus.OK));

    // when: se busca la unidad por id
    Optional<UnidadVinculacionOutput> result = sgiApiSgoService
        .findUnidadVinculacionById("DEP-1");

    // then: se devuelve la unidad y la consulta filtra solo por id
    Assertions.assertThat(result).contains(unidad);
    BDDMockito.then(restTemplate).should().exchange(
        ArgumentMatchers.eq(SGO_URL + "/unidades-vinculacion?q={query}"), ArgumentMatchers.eq(HttpMethod.GET),
        ArgumentMatchers.<HttpEntity<Void>>any(),
        ArgumentMatchers.<ParameterizedTypeReference<List<UnidadVinculacionOutput>>>any(),
        ArgumentMatchers.eq("id==\"DEP-1\""));
  }

  @Test
  void findUnidadVinculacionById_WithNoContent_ReturnsEmpty() {
    // given: el SGO responde sin contenido
    BDDMockito.given(restApiProperties.getSgoUrl()).willReturn(SGO_URL);
    mockExchange(new ResponseEntity<>(HttpStatus.NO_CONTENT));

    // when: se busca la unidad
    Optional<UnidadVinculacionOutput> result = sgiApiSgoService
        .findUnidadVinculacionById("DEP-1");

    // then: no se devuelve ninguna unidad
    Assertions.assertThat(result).isEmpty();
  }

  @Test
  void findUnidadVinculacionById_WithError_ThrowsMicroserviceCallException() {
    // given: la llamada al SGO falla
    BDDMockito.given(restApiProperties.getSgoUrl()).willReturn(SGO_URL);
    BDDMockito.given(restTemplate.exchange(ArgumentMatchers.anyString(), ArgumentMatchers.eq(HttpMethod.GET),
        ArgumentMatchers.<HttpEntity<Void>>any(),
        ArgumentMatchers.<ParameterizedTypeReference<List<UnidadVinculacionOutput>>>any(),
        ArgumentMatchers.<Object>any())).willThrow(new RestClientException("error"));

    Assertions.assertThatThrownBy(
        // when: se busca la unidad
        () -> sgiApiSgoService.findUnidadVinculacionById("DEP-1"))
        // then: se lanza la excepción de llamada a microservicio
        .isInstanceOf(MicroserviceCallException.class);
  }

  private void mockExchange(ResponseEntity<List<UnidadVinculacionOutput>> response) {
    BDDMockito.given(restTemplate.exchange(ArgumentMatchers.anyString(), ArgumentMatchers.eq(HttpMethod.GET),
        ArgumentMatchers.<HttpEntity<Void>>any(),
        ArgumentMatchers.<ParameterizedTypeReference<List<UnidadVinculacionOutput>>>any(),
        ArgumentMatchers.<Object>any())).willReturn(response);
  }

}
