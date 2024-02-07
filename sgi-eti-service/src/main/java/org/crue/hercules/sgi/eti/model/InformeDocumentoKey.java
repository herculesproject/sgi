package org.crue.hercules.sgi.eti.model;

import java.io.Serializable;

import org.crue.hercules.sgi.eti.enums.Language;

import lombok.EqualsAndHashCode;

/**
 * Informe
 */
@EqualsAndHashCode
public class InformeDocumentoKey implements Serializable {
  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  private Long informeId;
  private Language lang;

}