package org.crue.hercules.sgi.csp.util;

import java.util.Arrays;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.dto.com.Recipient;
import org.junit.jupiter.api.Test;

/**
 * ComRecipientHelperTest
 */
class ComRecipientHelperTest {

  private static final Recipient UNO = Recipient.builder().name("uno").address("uno@test.com").build();
  private static final Recipient DOS = Recipient.builder().name("dos").address("dos@test.com").build();

  @Test
  void haveSameRecipients_WithSameRecipientsInDifferentOrder_ReturnsTrue() {
    // given: dos listados con los mismos destinatarios en distinto orden
    // when: se comparan
    // then: se consideran iguales
    Assertions.assertThat(ComRecipientHelper.haveSameRecipients(Arrays.asList(UNO, DOS), Arrays.asList(DOS, UNO)))
        .isTrue();
  }

  @Test
  void haveSameRecipients_WithDifferentRecipients_ReturnsFalse() {
    // given: dos listados con destinatarios distintos
    // when: se comparan
    // then: no se consideran iguales
    Assertions.assertThat(ComRecipientHelper.haveSameRecipients(Arrays.asList(UNO), Arrays.asList(DOS))).isFalse();
    Assertions.assertThat(ComRecipientHelper.haveSameRecipients(Arrays.asList(UNO), Arrays.asList(UNO, DOS))).isFalse();
  }

  @Test
  void haveSameRecipients_WithNullList_TreatsItAsEmpty() {
    // given: un listado nulo y otro vacio
    // when: se comparan
    // then: se consideran iguales, y un nulo nunca equivale a un listado con datos
    Assertions.assertThat(ComRecipientHelper.haveSameRecipients(null, Collections.emptyList())).isTrue();
    Assertions.assertThat(ComRecipientHelper.haveSameRecipients(null, null)).isTrue();
    Assertions.assertThat(ComRecipientHelper.haveSameRecipients(null, Arrays.asList(UNO))).isFalse();
  }

  @Test
  void haveSameRecipients_IgnoresDuplicates() {
    // given: un listado con un destinatario repetido y otro sin repetir
    // when: se comparan
    // then: se consideran iguales
    Assertions.assertThat(ComRecipientHelper.haveSameRecipients(Arrays.asList(UNO, UNO), Arrays.asList(UNO))).isTrue();
  }
}
