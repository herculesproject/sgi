package org.crue.hercules.sgi.csp.util;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.dto.com.EmailOutput;
import org.crue.hercules.sgi.csp.dto.com.EmailParam;
import org.junit.jupiter.api.Test;

/**
 * ComGenericEmailTextHelperTest
 */
class ComGenericEmailTextHelperTest {

  private static final String ASUNTO = "Asunto test";
  private static final String CONTENIDO = "Mensaje email test";

  @Test
  void buildParamsAndGet_AreSymmetric() {
    // given: los parametros construidos para un email generico
    EmailOutput email = EmailOutput.builder().id(1L).build();
    email.setParams(ComGenericEmailTextHelper.buildParams(ASUNTO, CONTENIDO));

    // when: se vuelven a leer
    // then: se recupera lo que se escribio
    Assertions.assertThat(ComGenericEmailTextHelper.getSubject(email)).isEqualTo(ASUNTO);
    Assertions.assertThat(ComGenericEmailTextHelper.getContent(email)).isEqualTo(CONTENIDO);
  }

  @Test
  void buildParams_ReturnsSubjectAndContent() {
    // given: un asunto y un contenido
    // when: se construyen los parametros
    List<EmailParam> params = ComGenericEmailTextHelper.buildParams(ASUNTO, CONTENIDO);

    // then: se devuelve un parametro por cada uno
    Assertions.assertThat(params).hasSize(2);
    Assertions.assertThat(params).extracting(EmailParam::getValue).containsExactlyInAnyOrder(ASUNTO, CONTENIDO);
  }

  @Test
  void getSubjectAndContent_WithoutParams_ReturnsNull() {
    // given: un email sin parametros
    EmailOutput email = EmailOutput.builder().id(1L).build();

    // when: se obtienen el asunto y el contenido
    // then: no se devuelve valor
    Assertions.assertThat(ComGenericEmailTextHelper.getSubject(email)).isNull();
    Assertions.assertThat(ComGenericEmailTextHelper.getContent(email)).isNull();
  }

  @Test
  void getSubjectAndContent_WithUnknownParams_ReturnsNull() {
    // given: un email cuyos parametros no son los de la plantilla generica
    EmailOutput email = EmailOutput.builder().id(1L).build();
    email.setParams(Arrays.asList(new EmailParam("OTRO_PARAM", "valor")));

    // when: se obtienen el asunto y el contenido
    // then: no se devuelve valor
    Assertions.assertThat(ComGenericEmailTextHelper.getSubject(email)).isNull();
    Assertions.assertThat(ComGenericEmailTextHelper.getContent(email)).isNull();
  }

  @Test
  void getSubject_WithNullEmail_ReturnsNull() {
    // given: ningun email
    // when: se obtiene el asunto
    // then: no se devuelve valor
    Assertions.assertThat(ComGenericEmailTextHelper.getSubject(null)).isNull();
  }
}
