package org.crue.hercules.sgi.auth.broker.saml.mappers.rules;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Pertenencia a grupos concedida por defecto cuando ninguna regla se cumple.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Fallback {

  /** Grupos a conceder cuando ninguna regla se cumple. */
  public List<String> whenNoRulesMatched;
}
