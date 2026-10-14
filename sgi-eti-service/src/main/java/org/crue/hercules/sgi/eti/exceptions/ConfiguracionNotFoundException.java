package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.eti.model.Configuracion;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;

/**
 * ConfiguracionNotFoundException
 */
@SuppressWarnings("java:S110")
public class ConfiguracionNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  /**
   * Crea la excepción para cuando no existe la {@link Configuracion}.
   */
  public ConfiguracionNotFoundException() {
    super(ProblemMessage.builder().key(ConfiguracionNotFoundException.class).build());
  }

  /**
   * Crea la excepción para cuando no existe la {@link Configuracion} con el
   * identificador indicado.
   *
   * @param idConfiguracion identificador de la {@link Configuracion}.
   */
  public ConfiguracionNotFoundException(Long idConfiguracion) {
    super(idConfiguracion, Configuracion.class);
  }

}
