package org.crue.hercules.sgi.usr.model;

import java.io.Serializable;

/**
 * Base Entity.
 */
public class BaseEntity implements Serializable {

  /** Serial version. */
  private static final long serialVersionUID = 1L;

  /**
   * Interfaz para marcar validaciones en los create.
   */
  public interface Create {
  }

  /**
   * Interfaz para marcar validaciones en los update.
   */
  public interface Update {
  }

  /**
   * Interfaz para marcar validaciones en los create cuando se recibe del client.
   */
  public interface CreateClient {
  }

  /**
   * Interfaz para marcar validaciones en los update cuando se recibe del client.
   */
  public interface UpdateClient {
  }
}
