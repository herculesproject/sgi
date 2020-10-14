package org.crue.hercules.sgi.usr.exceptions;

import org.crue.hercules.sgi.framework.exception.NotFoundException;

/**
 * UspNotFoundException
 */
public class UsrNotFoundException extends NotFoundException {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  public UsrNotFoundException(String message) {
    super(message);
  }

}
