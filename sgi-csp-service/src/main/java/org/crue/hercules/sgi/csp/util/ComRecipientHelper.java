package org.crue.hercules.sgi.csp.util;

import java.util.HashSet;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.crue.hercules.sgi.csp.dto.com.Recipient;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utilidades sobre los destinatarios ({@link Recipient}) de los emails del
 * modulo COM.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ComRecipientHelper {

  /**
   * Comprueba si dos listados de destinatarios contienen los mismos
   * destinatarios, sin tener en cuenta el orden ni las repeticiones.
   *
   * @param recipients      Listado de destinatarios a comparar
   * @param otherRecipients Listado de destinatarios con el que comparar
   * @return <code>true</code> si ambos listados contienen los mismos
   *         destinatarios
   */
  public static boolean haveSameRecipients(List<Recipient> recipients, List<Recipient> otherRecipients) {
    return new HashSet<>(CollectionUtils.emptyIfNull(recipients))
        .equals(new HashSet<>(CollectionUtils.emptyIfNull(otherRecipients)));
  }
}
