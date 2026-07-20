package org.crue.hercules.sgi.auth.broker.saml.mappers.rules;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Regla de asignación de grupos. Cuando {@link #when} evalúa a
 * {@code true} para el conjunto de atributos SAML de un usuario, cada entrada
 * de {@link #grant} se añade a los grupos del usuario.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Rule {

  /** Nombre legible de la regla */
  public String name;

  /** Lista de grupos que se conceden cuando se cumple la condicion. */
  public List<String> grant;

  /** Predicado evaluado contra el conjunto de atributos SAML. */
  public Condition when;

  void validate(String path) {
    if (grant == null || grant.isEmpty()) {
      throw new IllegalArgumentException(path + ": 'grant' must not be empty");
    }

    if (when == null) {
      throw new IllegalArgumentException(path + ": 'when' is required");
    }

    when.validate(path + ".when");
  }

}
