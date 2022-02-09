package org.crue.hercules.sgi.csp.service;

import static org.mockito.ArgumentMatchers.anyString;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.config.RestApiProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

class ReportServiceTest extends BaseServiceTest {

  @Mock
  private RestApiProperties restApiProperties;
  @Mock
  private RestTemplate restTemplate;

  private ReportService reportService;

  @BeforeEach
  public void setup() {
    this.reportService = new ReportService(restApiProperties, restTemplate);
  }

  @Test
  void getInformeAutorizacion_ReturnsResource() {
    Long idAutorizacion = 1L;
    final ResponseEntity<Resource> expectedResource = ResponseEntity.ok(new ClassPathResource("application.yml"));

    BDDMockito.given(this.restTemplate.exchange(anyString(), ArgumentMatchers.<HttpMethod>any(), ArgumentMatchers.<HttpEntity<Object>>any(), ArgumentMatchers.<Class<Resource>>any())).willReturn(expectedResource);

    Resource resource = reportService.getInformeAutorizacion(idAutorizacion);

    Assertions.assertThat(resource).isNotNull().isEqualTo(expectedResource.getBody());
  }
}