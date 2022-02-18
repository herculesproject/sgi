package org.crue.hercules.sgi.csp.service.com;

import java.util.ArrayList;
import java.util.List;

import org.crue.hercules.sgi.csp.config.RestApiProperties;
import org.crue.hercules.sgi.csp.dto.com.EmailInput;
import org.crue.hercules.sgi.csp.dto.com.EmailInput.Deferrable;
import org.crue.hercules.sgi.csp.dto.com.EmailOutput;
import org.crue.hercules.sgi.csp.dto.com.EmailParam;
import org.crue.hercules.sgi.csp.dto.com.Recipient;
import org.crue.hercules.sgi.csp.dto.com.ServiceType;
import org.crue.hercules.sgi.framework.http.HttpEntityBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailService {

  private static final String TEMPLATE_GENERIC_EMAIL_TEXT_NAME = "GENERIC_EMAIL_TEXT";
  private static final String TEMPLATE_GENERIC_EMAIL_TEXT_PARAM_CONTENT = "GENERIC_CONTENT_TEXT";
  private static final String TEMPLATE_GENERIC_EMAIL_TEXT_PARAM_SUBJECT = "GENERIC_SUBJECT";

  private static final String CONVOCATORIA_HITO_DEFERRABLE_RECIPIENTS_URI_FORMAT = "/convocatoriahitos/%s/deferrable-recipients";

  private final RestTemplate restTemplate;
  private final String baseUrl;

  public EmailService(RestTemplate restTemplate, RestApiProperties restApiProperties) {
    this.restTemplate = restTemplate;
    this.baseUrl = restApiProperties.getComUrl() + "/emails";
  }

  /**
   * Crea un email genérico en el modulo COM
   * 
   * @param subject              Asunto del email
   * @param content              Contenido del email
   * @param recipients           Destinatarios del email
   * @param deferrableRecipients Resolutor de los destinatarios adicionales a
   *                             resolver en el momento del envio
   * @return Email creado
   */
  public EmailOutput createGenericEmailText(String subject, String content, List<Recipient> recipients,
      Deferrable deferrableRecipients) {
    log.debug("createGenericEmailText({}, {}, {}, {}) - start", subject, content, recipients, deferrableRecipients);

    Assert.notNull(subject, "Subject is required");
    Assert.notNull(content, "Content is required");
    Assert.notEmpty(recipients, "At least one Recipient is required");
    Assert.noNullElements(recipients, "The Recipients list must not contain null elements");

    EmailInput emailRequest = EmailInput.builder().template(TEMPLATE_GENERIC_EMAIL_TEXT_NAME).recipients(recipients)
        .deferrableRecipients(deferrableRecipients).build();
    emailRequest.setParams(new ArrayList<>());
    emailRequest.getParams()
        .add(new EmailParam(TEMPLATE_GENERIC_EMAIL_TEXT_PARAM_CONTENT, content));
    emailRequest.getParams()
        .add(new EmailParam(TEMPLATE_GENERIC_EMAIL_TEXT_PARAM_SUBJECT, subject));

    ResponseEntity<EmailOutput> emailResponse = restTemplate.exchange(
        baseUrl, HttpMethod.POST,
        new HttpEntityBuilder<>(
            emailRequest).withCurrentUserAuthorization().build(),
        EmailOutput.class);
    log.debug("createGenericEmailText({}, {}, {}, {}) - end", subject, content, recipients, deferrableRecipients);
    return emailResponse.getBody();
  }

  /**
   * Actualiza un email genérico en el modulo COM
   * 
   * @param id                   Identificador del email
   * @param subject              Asunto del email
   * @param content              Contenido del email
   * @param recipients           Destinatarios del email
   * @param deferrableRecipients Resolutor de los destinatarios adicionales a
   *                             resolver en el momento del envio
   * @return Email creado
   */
  public EmailOutput updateGenericEmailText(Long id, String subject, String content, List<Recipient> recipients,
      Deferrable deferrableRecipients) {
    log.debug("updateGenericEmailText({}, {}, {}, {}, {}) - start", id, subject, content, recipients,
        deferrableRecipients);

    Assert.notNull(id, "ID is required");
    Assert.notNull(subject, "Subject is required");
    Assert.notNull(content, "Content is required");
    Assert.notEmpty(recipients, "At least one Recipient is required");
    Assert.noNullElements(recipients, "The Recipients list must not contain null elements");

    EmailInput emailRequest = EmailInput.builder().template(TEMPLATE_GENERIC_EMAIL_TEXT_NAME).recipients(recipients)
        .deferrableRecipients(deferrableRecipients).build();
    emailRequest.setParams(new ArrayList<>());
    emailRequest.getParams()
        .add(new EmailParam(TEMPLATE_GENERIC_EMAIL_TEXT_PARAM_CONTENT, content));
    emailRequest.getParams()
        .add(new EmailParam(TEMPLATE_GENERIC_EMAIL_TEXT_PARAM_SUBJECT, subject));

    ResponseEntity<EmailOutput> emailResponse = restTemplate.exchange(
        baseUrl + "/" + id, HttpMethod.PUT,
        new HttpEntityBuilder<>(
            emailRequest).withCurrentUserAuthorization().build(),
        EmailOutput.class);

    log.debug("updateGenericEmailText({}, {}, {}, {}, {}) - end", id, subject, content, recipients,
        deferrableRecipients);
    return emailResponse.getBody();
  }

  /**
   * Elimina un email del modulo COM
   * 
   * @param id Identificador del email
   */
  public void deleteEmail(Long id) {
    log.debug("deleteEmail({}) - start", id);

    Assert.notNull(id, "ID is required");

    restTemplate.exchange(
        baseUrl + "/" + id,
        HttpMethod.DELETE,
        new HttpEntityBuilder<>().withCurrentUserAuthorization().build(),
        Void.class);
    log.debug("deleteEmail({}) - end", id);
  }

  /**
   * Crea un email en el modulo COM para el aviso de un hito de convocatoria
   * 
   * @param convocatoriaHitoId Identificador del hito de convocatoria
   * @param subject            Asunto del email
   * @param content            Contenido del email
   * @param recipients         Destinatarios del email
   * @return Identificador del email creado
   */
  public Long createConvocatoriaHitoEmail(Long convocatoriaHitoId, String subject, String content,
      List<Recipient> recipients) {
    log.debug("createConvocatoriaHitoEmail({}, {}, {}, {}) - start", convocatoriaHitoId, subject, content, recipients);

    Assert.notNull(convocatoriaHitoId, "ConvocatoriaHito ID is required");
    Assert.notNull(subject, "Subject is required");
    Assert.notNull(content, "Content is required");
    Assert.notEmpty(recipients, "At least one Recipient is required");
    Assert.noNullElements(recipients, "The Recipients list must not contain null elements");

    Long id = this.createGenericEmailText(subject, content, recipients, new Deferrable(
        ServiceType.CSP,
        String.format(CONVOCATORIA_HITO_DEFERRABLE_RECIPIENTS_URI_FORMAT, convocatoriaHitoId), HttpMethod.GET))
        .getId();
    log.debug("createConvocatoriaHitoEmail({}, {}, {}, {}) - end", convocatoriaHitoId, subject, content, recipients);
    return id;
  }

  /**
   * Actualiza un email en el modulo COM para el aviso de un hito de convocatoria
   * 
   * @param id                 Identificador el email
   * @param convocatoriaHitoId Identificador del hito de convocatoria
   * @param subject            Asunto del email
   * @param content            Contenido del email
   * @param recipients         Destinatarios del email
   */
  public void updateConvocatoriaHitoEmail(Long id, Long convocatoriaHitoId, String subject, String content,
      List<Recipient> recipients) {
    log.debug("updateConvocatoriaHitoEmail({}, {}, {}, {}) - start", id, convocatoriaHitoId, subject, content,
        recipients);

    Assert.notNull(id, "ID is required");
    Assert.notNull(convocatoriaHitoId, "ConvocatoriaHito ID is required");
    Assert.notNull(subject, "Subject is required");
    Assert.notNull(content, "Content is required");
    Assert.notEmpty(recipients, "At least one Recipient is required");
    Assert.noNullElements(recipients, "The Recipients list must not contain null elements");

    this.updateGenericEmailText(id, subject, content, recipients, new Deferrable(
        ServiceType.CSP,
        String.format(CONVOCATORIA_HITO_DEFERRABLE_RECIPIENTS_URI_FORMAT, convocatoriaHitoId), HttpMethod.GET));
    log.debug("updateConvocatoriaHitoEmail({}, {}, {}, {}) - end", id, convocatoriaHitoId, subject, content,
        recipients);
  }
}
