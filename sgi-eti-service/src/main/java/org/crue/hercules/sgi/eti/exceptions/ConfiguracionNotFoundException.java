package org.crue.hercules.sgi.eti.exceptions;

/**
 * ConfiguracionNotFoundException
 */
public class ConfiguracionNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public ConfiguracionNotFoundException(Long configuracionId) {
    super("Configuracion " + configuracionId + " does not exist.");
  }

}