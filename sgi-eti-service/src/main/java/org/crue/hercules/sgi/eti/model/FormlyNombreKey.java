package org.crue.hercules.sgi.eti.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;

import org.crue.hercules.sgi.eti.converter.LanguageConverter;
import org.crue.hercules.sgi.eti.enums.Language;

import lombok.EqualsAndHashCode;

/**
 * Formly Language
 */
@EqualsAndHashCode
public class FormlyNombreKey implements Serializable {
  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  @Column(name = "formly_id", nullable = false)
  private Long formlyId;

  @Column(name = "lang", nullable = false, length = 2)
  @Convert(converter = LanguageConverter.class)
  private Language lang;
}