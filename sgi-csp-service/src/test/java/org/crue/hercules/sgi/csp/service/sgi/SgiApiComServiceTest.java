package org.crue.hercules.sgi.csp.service.sgi;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.config.RestApiProperties;
import org.crue.hercules.sgi.csp.dto.com.CspComInicioPresentacionGastoData;
import org.crue.hercules.sgi.csp.dto.com.EmailInput;
import org.crue.hercules.sgi.csp.dto.com.EmailInput.Deferrable;
import org.crue.hercules.sgi.csp.dto.com.EmailOutput;
import org.crue.hercules.sgi.csp.dto.com.Recipient;
import org.crue.hercules.sgi.csp.service.BaseServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

class SgiApiComServiceTest extends BaseServiceTest {

  @Mock
  private RestTemplate restTemplate;
  @Mock
  private RestApiProperties restApiProperties;
  @Mock
  private ObjectMapper objectMapper;

  private SgiApiComService emailService;

  @BeforeEach
  void setup() {
    BDDMockito.given(this.restApiProperties.getComUrl()).willReturn("smtp.gmail.com");
    this.emailService = new SgiApiComService(this.restApiProperties, this.restTemplate, this.objectMapper);
  }

  @Test
  void createGenericEmailText_ReturnsEmailOutput() {
    String subject = "Asunto";
    String content = "Mensaje email test";
    List<Recipient> recipients = this.buildMockRecipients();
    Deferrable deferrableRecipients = Deferrable.builder().build();
    EmailOutput emailOutput = this.buildMockEmailOutput(1L);

    BDDMockito
        .given(this.restTemplate.exchange(ArgumentMatchers
            .<String>any(), ArgumentMatchers.<HttpMethod>any(),
            ArgumentMatchers.<HttpEntity<Object>>any(),
            ArgumentMatchers.<ParameterizedTypeReference<EmailOutput>>any(),
            ArgumentMatchers.<Object>any()))
        .willReturn(ResponseEntity.ok(emailOutput));

    EmailOutput response = this.emailService.createGenericEmailText(subject, content, recipients,
        deferrableRecipients);

    Assertions.assertThat(response).isNotNull();
  }

  @Test
  void updateGenericEmailText_ReturnsEmailOutput() {
    String subject = "Asunto test";
    String content = "Mensaje email test";
    List<Recipient> recipients = this.buildMockRecipients();
    Deferrable deferrableRecipients = Deferrable.builder().build();
    EmailOutput emailOutput = this.buildMockEmailOutput(1L);

    BDDMockito
        .given(this.restTemplate.exchange(ArgumentMatchers
            .<String>any(), ArgumentMatchers.<HttpMethod>any(),
            ArgumentMatchers.<HttpEntity<Object>>any(),
            ArgumentMatchers.<ParameterizedTypeReference<EmailOutput>>any(),
            ArgumentMatchers.<Object>any()))
        .willReturn(ResponseEntity.ok(emailOutput));

    EmailOutput response = this.emailService.updateGenericEmailText(emailOutput.getId(), subject, content, recipients,
        deferrableRecipients);

    Assertions.assertThat(response).isNotNull();
  }

  @Test
  void deleteEmail_VerifyCallDeleteApiService() {

    this.emailService.deleteEmail(1L);

    verify(this.restTemplate, times(1)).exchange(ArgumentMatchers
        .<String>any(), ArgumentMatchers.<HttpMethod>any(),
        ArgumentMatchers.<HttpEntity<Object>>any(),
        ArgumentMatchers.<ParameterizedTypeReference<Void>>any(),
        ArgumentMatchers.<Object>any());
  }

  @Test
  void createConvocatoriaHitoEmail_ReturnsLong() {
    String subject = "Asunto test";
    String content = "Mensaje email test";
    List<Recipient> recipients = this.buildMockRecipients();

    EmailOutput emailOutput = this.buildMockEmailOutput(1L);

    BDDMockito
        .given(this.restTemplate.exchange(ArgumentMatchers
            .<String>any(), ArgumentMatchers.<HttpMethod>any(),
            ArgumentMatchers.<HttpEntity<Object>>any(),
            ArgumentMatchers.<ParameterizedTypeReference<EmailOutput>>any(),
            ArgumentMatchers.<Object>any()))
        .willReturn(ResponseEntity.ok(emailOutput));

    Long response = this.emailService.createConvocatoriaHitoEmail(1L, subject, content, recipients);

    Assertions.assertThat(response).isEqualTo(1L);
  }

  @Test
  void updateConvocatoriaHitoEmail_VerifyCallApiRestUpdate() {
    String subject = "Asunto test";
    String content = "Mensaje email test";
    List<Recipient> recipients = this.buildMockRecipients();
    EmailOutput emailOutput = this.buildMockEmailOutput(1L);

    BDDMockito
        .given(this.restTemplate.exchange(ArgumentMatchers
            .<String>any(), ArgumentMatchers.<HttpMethod>any(),
            ArgumentMatchers.<HttpEntity<Object>>any(),
            ArgumentMatchers.<ParameterizedTypeReference<EmailOutput>>any(),
            ArgumentMatchers.<Object>any()))
        .willReturn(ResponseEntity.ok(emailOutput));

    this.emailService.updateConvocatoriaHitoEmail(1L, 1L, subject, content, recipients);

    verify(this.restTemplate, times(1)).exchange(ArgumentMatchers
        .<String>any(), ArgumentMatchers.<HttpMethod>any(),
        ArgumentMatchers.<HttpEntity<Object>>any(),
        ArgumentMatchers.<ParameterizedTypeReference<EmailOutput>>any(),
        ArgumentMatchers.<Object>any());
  }

  @Test
  void createComunicadoInicioPresentacionJustificacionGastosEmail_ReturnsEmailOutput() throws JsonProcessingException {
    CspComInicioPresentacionGastoData data = CspComInicioPresentacionGastoData.builder()
        .fecha(LocalDate.now())
        .build();
    List<Recipient> recipients = this.buildMockRecipients();
    EmailOutput emailOutput = this.buildMockEmailOutput(1L);

    BDDMockito.given(this.objectMapper.writeValueAsString(data)).willReturn("/test");

    BDDMockito
        .given(this.restTemplate.exchange(ArgumentMatchers
            .<String>any(), ArgumentMatchers.<HttpMethod>any(),
            ArgumentMatchers.<HttpEntity<Object>>any(),
            ArgumentMatchers.<ParameterizedTypeReference<EmailOutput>>any(),
            ArgumentMatchers.<Object>any()))
        .willReturn(ResponseEntity.ok(emailOutput));

    EmailOutput response = this.emailService.createComunicadoInicioPresentacionJustificacionGastosEmail(data,
        recipients);

    Assertions.assertThat(response).isNotNull();
  }

  @Test
  void updateConvocatoriaFaseEmail_SendsConvocatoriaFaseDeferrableRecipientsUrl() {
    // given: un email de aviso de una fase de convocatoria
    mockEmailOutputResponse();

    // when: se actualiza el email de la fase de convocatoria 5
    this.emailService.updateConvocatoriaFaseEmail(1L, 5L, "Asunto test", "Mensaje email test",
        this.buildMockRecipients());

    // then: el resolutor de destinatarios apunta a la fase de convocatoria
    Assertions.assertThat(captureDeferrableRecipientsUrl())
        .isEqualTo("/convocatoriafases/5/deferrable-recipients");
  }

  @Test
  void updateProyectoFaseEmail_SendsProyectoFaseDeferrableRecipientsUrl() {
    // given: un email de aviso de una fase de proyecto
    mockEmailOutputResponse();

    // when: se actualiza el email de la fase de proyecto 5
    this.emailService.updateProyectoFaseEmail(1L, 5L, "Asunto test", "Mensaje email test",
        this.buildMockRecipients());

    // then: el resolutor de destinatarios apunta a la fase de proyecto
    Assertions.assertThat(captureDeferrableRecipientsUrl())
        .isEqualTo("/proyectofases/5/deferrable-recipients");
  }

  @Test
  void updateProyectoHitoEmail_SendsProyectoHitoDeferrableRecipientsUrl() {
    // given: un email de aviso de un hito de proyecto
    mockEmailOutputResponse();

    // when: se actualiza el email del hito de proyecto 5
    this.emailService.updateProyectoHitoEmail(1L, 5L, "Asunto test", "Mensaje email test",
        this.buildMockRecipients());

    // then: el resolutor de destinatarios apunta al hito de proyecto
    Assertions.assertThat(captureDeferrableRecipientsUrl())
        .isEqualTo("/proyectohitos/5/deferrable-recipients");
  }

  @Test
  void createConvocatoriaFaseEmail_SendsConvocatoriaFaseDeferrableRecipientsUrl() {
    // given: una fase de convocatoria sin email de aviso
    mockEmailOutputResponse();

    // when: se crea el email de la fase de convocatoria 5
    this.emailService.createConvocatoriaFaseEmail(5L, "Asunto test", "Mensaje email test",
        this.buildMockRecipients());

    // then: el resolutor de destinatarios apunta a la fase de convocatoria
    Assertions.assertThat(captureDeferrableRecipientsUrl())
        .isEqualTo("/convocatoriafases/5/deferrable-recipients");
  }

  @Test
  void createProyectoFaseEmail_SendsProyectoFaseDeferrableRecipientsUrl() {
    // given: una fase de proyecto sin email de aviso
    mockEmailOutputResponse();

    // when: se crea el email de la fase de proyecto 5
    this.emailService.createProyectoFaseEmail(5L, "Asunto test", "Mensaje email test", this.buildMockRecipients());

    // then: el resolutor de destinatarios apunta a la fase de proyecto
    Assertions.assertThat(captureDeferrableRecipientsUrl())
        .isEqualTo("/proyectofases/5/deferrable-recipients");
  }

  @Test
  void findGenericEmailTextById_ReturnsEmailOutput() {
    // given: un email existente en el modulo COM
    mockEmailOutputResponse();

    // when: se recupera por su id
    EmailOutput response = this.emailService.findGenericEmailTextById(1L);

    // then: se devuelve el email
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getId()).isEqualTo(1L);
  }

  private void mockEmailOutputResponse() {
    BDDMockito
        .given(this.restTemplate.exchange(ArgumentMatchers
            .<String>any(), ArgumentMatchers.<HttpMethod>any(),
            ArgumentMatchers.<HttpEntity<Object>>any(),
            ArgumentMatchers.<ParameterizedTypeReference<EmailOutput>>any(),
            ArgumentMatchers.<Object>any()))
        .willReturn(ResponseEntity.ok(this.buildMockEmailOutput(1L)));
  }

  @SuppressWarnings("unchecked")
  private String captureDeferrableRecipientsUrl() {
    ArgumentCaptor<HttpEntity<EmailInput>> requestCaptor = ArgumentCaptor.forClass(HttpEntity.class);

    verify(this.restTemplate, times(1)).exchange(ArgumentMatchers.<String>any(),
        ArgumentMatchers.<HttpMethod>any(), requestCaptor.capture(),
        ArgumentMatchers.<ParameterizedTypeReference<EmailOutput>>any(),
        ArgumentMatchers.<Object>any());

    return requestCaptor.getValue().getBody().getDeferrableRecipients().getUrl();
  }

  private EmailOutput buildMockEmailOutput(Long id) {
    return EmailOutput.builder()
        .id(id)
        .build();
  }

  private List<Recipient> buildMockRecipients() {
    return Arrays.asList(Recipient.builder()
        .address("test@gmail.com")
        .name("test")
        .build());
  }
}
