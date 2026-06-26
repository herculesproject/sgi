package org.crue.hercules.sgi.csp.service.sgi;

import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.config.RestApiProperties;
import org.crue.hercules.sgi.csp.dto.rel.RelacionOutput;
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

class SgiApiRelServiceTest extends BaseServiceTest {

  @Mock
  private RestTemplate restTemplate;
  @Mock
  private RestApiProperties restApiProperties;

  private SgiApiRelService sgiApiRelService;

  @BeforeEach
  void setup() {
    BDDMockito.given(restApiProperties.getRelUrl()).willReturn("http://localhost");
    this.sgiApiRelService = new SgiApiRelService(restApiProperties, restTemplate);
  }

  @Test
  void findRelacionesProyecto_ReturnsRelacionOutputList() {
    // given: REL devuelve una relacion para el proyecto
    RelacionOutput relacion = RelacionOutput.builder()
        .id(1L)
        .tipoEntidadOrigen(RelacionOutput.TipoEntidad.PROYECTO)
        .tipoEntidadDestino(RelacionOutput.TipoEntidad.INVENCION)
        .entidadOrigenRef("5")
        .entidadDestinoRef("10")
        .build();

    BDDMockito
        .given(this.restTemplate.exchange(ArgumentMatchers.<String>any(), ArgumentMatchers.<HttpMethod>any(),
            ArgumentMatchers.<HttpEntity<Object>>any(),
            ArgumentMatchers.<ParameterizedTypeReference<List<RelacionOutput>>>any()))
        .willReturn(ResponseEntity.ok(Collections.singletonList(relacion)));

    // when: se recuperan las relaciones del proyecto
    List<RelacionOutput> result = this.sgiApiRelService.findRelacionesProyecto(5L);

    // then: se devuelve la lista de relaciones
    Assertions.assertThat(result).hasSize(1);
    Assertions.assertThat(result.get(0).getId()).isEqualTo(1L);
  }

  @Test
  void findRelacionesProyecto_WithNullBody_ReturnsEmptyList() {
    // given: REL no devuelve relaciones
    BDDMockito
        .given(this.restTemplate.exchange(ArgumentMatchers.<String>any(), ArgumentMatchers.<HttpMethod>any(),
            ArgumentMatchers.<HttpEntity<Object>>any(),
            ArgumentMatchers.<ParameterizedTypeReference<List<RelacionOutput>>>any()))
        .willReturn(ResponseEntity.ok(null));

    // when: se recuperan las relaciones del proyecto
    List<RelacionOutput> result = this.sgiApiRelService.findRelacionesProyecto(5L);

    // then: se devuelve una lista vacia
    Assertions.assertThat(result).isNotNull().isEmpty();
  }

  @Test
  void findRelacionesProyecto_WhenRelReturnsControlledException_RethrowsProblemException() {
    // given: REL devuelve una excepcion controlada (ProblemException)
    ProblemException controlada = new ProblemException(Problem.builder().build());
    BDDMockito
        .given(this.restTemplate.exchange(ArgumentMatchers.<String>any(), ArgumentMatchers.<HttpMethod>any(),
            ArgumentMatchers.<HttpEntity<Object>>any(),
            ArgumentMatchers.<ParameterizedTypeReference<List<RelacionOutput>>>any()))
        .willThrow(controlada);

    // when/then: se propaga la misma excepcion controlada tal cual
    Assertions.assertThatThrownBy(() -> this.sgiApiRelService.findRelacionesProyecto(5L))
        .isSameAs(controlada);
  }

  @Test
  void findRelacionesProyecto_WhenRelReturnsUncontrolledError_ThrowsGetRelacionesException() {
    // given: REL falla con un error no controlado
    BDDMockito
        .given(this.restTemplate.exchange(ArgumentMatchers.<String>any(), ArgumentMatchers.<HttpMethod>any(),
            ArgumentMatchers.<HttpEntity<Object>>any(),
            ArgumentMatchers.<ParameterizedTypeReference<List<RelacionOutput>>>any()))
        .willThrow(new RuntimeException("connection refused"));

    // when/then: se lanza la excepcion generica de relaciones
    Assertions.assertThatThrownBy(() -> this.sgiApiRelService.findRelacionesProyecto(5L))
        .isInstanceOf(GetRelacionesException.class);
  }

}
