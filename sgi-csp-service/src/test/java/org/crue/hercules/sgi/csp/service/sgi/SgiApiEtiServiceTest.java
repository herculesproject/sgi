package org.crue.hercules.sgi.csp.service.sgi;

import java.util.Arrays;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.config.RestApiProperties;
import org.crue.hercules.sgi.csp.dto.eti.EstadoFinanciacionDto;
import org.crue.hercules.sgi.csp.dto.eti.PeticionEvaluacionEstadoFinanciacionInput;
import org.crue.hercules.sgi.csp.dto.eti.PeticionEvaluacionOutput;
import org.crue.hercules.sgi.csp.dto.eti.PeticionEvaluacionSolicitudCspInput;
import org.crue.hercules.sgi.csp.service.BaseServiceTest;
import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * SgiApiEtiServiceTest
 */
class SgiApiEtiServiceTest extends BaseServiceTest {

  private static final String ETI_URL = "http://sgi-eti-service:4280";

  @Mock
  private RestTemplate restTemplate;
  @Mock
  private RestApiProperties restApiProperties;
  @Captor
  private ArgumentCaptor<HttpEntity<PeticionEvaluacionSolicitudCspInput>> solicitudCspRequestCaptor;
  @Captor
  private ArgumentCaptor<HttpEntity<PeticionEvaluacionEstadoFinanciacionInput>> estadoFinanciacionRequestCaptor;

  private SgiApiEtiService sgiApiEtiService;

  @BeforeEach
  void setup() {
    BDDMockito.given(restApiProperties.getEtiUrl()).willReturn(ETI_URL);
    sgiApiEtiService = new SgiApiEtiService(restApiProperties, restTemplate);
  }

  @Test
  void newPeticionEvaluacion_ReturnsPeticionEvaluacion() {
    // given: una petición de evaluación a crear y la respuesta de ETI
    PeticionEvaluacionSolicitudCspInput peticionEvaluacion = PeticionEvaluacionSolicitudCspInput.builder()
        .titulo(Arrays.asList(new I18nFieldValueDto(Language.ES, "Titulo")))
        .personaRef("usr-001")
        .existeFinanciacion(Boolean.FALSE)
        .solicitudConvocatoriaRef("1")
        .checklistId(1L)
        .build();
    PeticionEvaluacionOutput created = PeticionEvaluacionOutput.builder().id(1L).codigo("2026/001").build();
    BDDMockito
        .given(restTemplate.exchange(ArgumentMatchers.<String>any(), ArgumentMatchers.<HttpMethod>any(),
            ArgumentMatchers.<HttpEntity<PeticionEvaluacionSolicitudCspInput>>any(),
            ArgumentMatchers.<ParameterizedTypeReference<PeticionEvaluacionOutput>>any()))
        .willReturn(ResponseEntity.ok(created));

    // when: se crea la petición de evaluación
    PeticionEvaluacionOutput response = sgiApiEtiService.newPeticionEvaluacion(peticionEvaluacion);

    // then: se llama por POST al endpoint de creación desde CSP y se devuelve la
    // petición creada
    BDDMockito.then(restTemplate).should().exchange(ArgumentMatchers.eq(ETI_URL + "/peticionevaluaciones/solicitud-csp"),
        ArgumentMatchers.eq(HttpMethod.POST), solicitudCspRequestCaptor.capture(),
        ArgumentMatchers.<ParameterizedTypeReference<PeticionEvaluacionOutput>>any());
    Assertions.assertThat(solicitudCspRequestCaptor.getValue().getBody()).isEqualTo(peticionEvaluacion);
    Assertions.assertThat(response.getId()).isEqualTo(1L);
    Assertions.assertThat(response.getCodigo()).isEqualTo("2026/001");
  }

  @Test
  void updateEstadoFinanciacionPeticionEvaluacion_CallsPatch() {
    // given: una petición de evaluación existente y un nuevo estado de
    // financiación
    String peticionEvaluacionRef = "1";
    BDDMockito
        .given(restTemplate.exchange(ArgumentMatchers.<String>any(), ArgumentMatchers.<HttpMethod>any(),
            ArgumentMatchers.<HttpEntity<PeticionEvaluacionEstadoFinanciacionInput>>any(),
            ArgumentMatchers.<ParameterizedTypeReference<Void>>any(), ArgumentMatchers.<Object>any()))
        .willReturn(ResponseEntity.ok().build());

    // when: se actualiza el estado de financiación
    sgiApiEtiService.updateEstadoFinanciacionPeticionEvaluacion(peticionEvaluacionRef, EstadoFinanciacionDto.CONCEDIDO);

    // then: se llama por PATCH al endpoint del estado de financiación con el
    // nuevo estado
    BDDMockito.then(restTemplate).should().exchange(
        ArgumentMatchers.eq(ETI_URL + "/peticionevaluaciones/{peticionEvaluacionRef}/estado-financiacion"),
        ArgumentMatchers.eq(HttpMethod.PATCH), estadoFinanciacionRequestCaptor.capture(),
        ArgumentMatchers.<ParameterizedTypeReference<Void>>any(), ArgumentMatchers.eq(peticionEvaluacionRef));
    Assertions.assertThat(estadoFinanciacionRequestCaptor.getValue().getBody().getEstadoFinanciacion())
        .isEqualTo(EstadoFinanciacionDto.CONCEDIDO);
  }

}
