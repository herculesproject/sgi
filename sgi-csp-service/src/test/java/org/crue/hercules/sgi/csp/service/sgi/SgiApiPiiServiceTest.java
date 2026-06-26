package org.crue.hercules.sgi.csp.service.sgi;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.config.RestApiProperties;
import org.crue.hercules.sgi.csp.dto.pii.InvencionOutput;
import org.crue.hercules.sgi.csp.exceptions.rel.GetRelacionesException;
import org.crue.hercules.sgi.csp.service.BaseServiceTest;
import org.crue.hercules.sgi.framework.problem.Problem;
import org.crue.hercules.sgi.framework.problem.exception.ProblemException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

class SgiApiPiiServiceTest extends BaseServiceTest {

  @Mock
  private RestTemplate restTemplate;
  @Mock
  private RestApiProperties restApiProperties;

  private SgiApiPiiService sgiApiPiiService;

  @BeforeEach
  void setup() {
    BDDMockito.given(restApiProperties.getPiiUrl()).willReturn("http://localhost");
    this.sgiApiPiiService = new SgiApiPiiService(restApiProperties, restTemplate);
  }

  @Test
  void findInvencionById_ReturnsInvencionOutput() {
    // given: PII devuelve la invencion
    InvencionOutput invencion = InvencionOutput.builder().id(10L).build();
    BDDMockito
        .given(this.restTemplate.exchange(ArgumentMatchers.<String>any(), ArgumentMatchers.<HttpMethod>any(),
            ArgumentMatchers.<HttpEntity<Object>>any(),
            ArgumentMatchers.<ParameterizedTypeReference<InvencionOutput>>any(), ArgumentMatchers.<Object>any()))
        .willReturn(ResponseEntity.ok(invencion));

    // when: se recupera la invencion
    InvencionOutput result = this.sgiApiPiiService.findInvencionById(10L);

    // then: se devuelve la invencion
    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getId()).isEqualTo(10L);
  }

  @Test
  void findInvencionById_WhenPiiReturnsControlledException_RethrowsProblemException() {
    // given: PII devuelve una excepcion controlada (ProblemException)
    ProblemException controlada = new ProblemException(Problem.builder().build());
    BDDMockito
        .given(this.restTemplate.exchange(ArgumentMatchers.<String>any(), ArgumentMatchers.<HttpMethod>any(),
            ArgumentMatchers.<HttpEntity<Object>>any(),
            ArgumentMatchers.<ParameterizedTypeReference<InvencionOutput>>any(), ArgumentMatchers.<Object>any()))
        .willThrow(controlada);

    // when/then: se propaga la misma excepcion controlada tal cual
    Assertions.assertThatThrownBy(() -> this.sgiApiPiiService.findInvencionById(10L))
        .isSameAs(controlada);
  }

  @Test
  void findInvencionById_WhenPiiReturnsUncontrolledError_ThrowsGetRelacionesException() {
    // given: PII falla con un error no controlado
    BDDMockito
        .given(this.restTemplate.exchange(ArgumentMatchers.<String>any(), ArgumentMatchers.<HttpMethod>any(),
            ArgumentMatchers.<HttpEntity<Object>>any(),
            ArgumentMatchers.<ParameterizedTypeReference<InvencionOutput>>any(), ArgumentMatchers.<Object>any()))
        .willThrow(new RuntimeException("connection refused"));

    // when/then: se lanza la excepcion generica de relaciones
    Assertions.assertThatThrownBy(() -> this.sgiApiPiiService.findInvencionById(10L))
        .isInstanceOf(GetRelacionesException.class);
  }

}
