package org.crue.hercules.sgi.csp.util;

import java.util.Arrays;
import java.util.List;

import org.crue.hercules.sgi.csp.dto.com.EmailOutput;
import org.crue.hercules.sgi.csp.dto.com.EmailParam;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utilidades para los parámetros de la plantilla de email genérico del modulo
 * COM.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ComGenericEmailTextHelper {

  private static final String PARAM_CONTENT = "GENERIC_CONTENT_TEXT";
  private static final String PARAM_SUBJECT = "GENERIC_SUBJECT";

  /**
   * Construye los parámetros de un email genérico
   *
   * @param subject Asunto del email
   * @param content Contenido del email
   * @return Listado de parámetros del email
   */
  public static List<EmailParam> buildParams(String subject, String content) {
    return Arrays.asList(
        new EmailParam(PARAM_CONTENT, content),
        new EmailParam(PARAM_SUBJECT, subject));
  }

  /**
   * Obtiene el asunto de un email genérico
   *
   * @param email Email del que obtener el asunto
   * @return Asunto del email o <code>null</code> si no lo tiene
   */
  public static String getSubject(EmailOutput email) {
    return findParamValue(email, PARAM_SUBJECT);
  }

  /**
   * Obtiene el contenido de un email genérico
   *
   * @param email Email del que obtener el contenido
   * @return Contenido del email o <code>null</code> si no lo tiene
   */
  public static String getContent(EmailOutput email) {
    return findParamValue(email, PARAM_CONTENT);
  }

  /**
   * Busca entre los parámetros de un email el valor del que tenga el nombre
   * indicado
   *
   * @param email     Email en el que buscar
   * @param paramName Nombre del parámetro a buscar
   * @return Valor del parámetro o <code>null</code> si el email no lo tiene
   */
  private static String findParamValue(EmailOutput email, String paramName) {
    if (email == null || email.getParams() == null) {
      return null;
    }

    return email.getParams().stream()
        .filter(param -> paramName.equals(param.getName()))
        .map(EmailParam::getValue)
        .findFirst().orElse(null);
  }

}
