package org.crue.hercules.sgi.auth.broker.saml.mappers.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import org.junit.jupiter.api.Test;

class RuleSetParseTest {

  @Test
  void parse_WithMinimalValidRuleset_ReturnsRuleSet() throws IOException {
    // given: un ruleset mínimo válido con una única regla
    String json = """
        {
          "rules": [{
            "name": "r",
            "grant": ["G"],
            "when": { "attribute": "a", "anyOf": ["v"] }
          }]
        }
        """;

    // when: se parsea el ruleset
    RuleSet rs = RuleSet.parse(json);

    // then: se obtiene la regla y los campos opcionales quedan sin definir
    assertNotNull(rs);
    assertEquals(1, rs.rules.size());
    assertEquals("r", rs.rules.get(0).name);
    assertNull(rs.fallback);
    assertNull(rs.splitters);
  }

  @Test
  void parse_WithSplitters_ReturnsRuleSetWithSplitters() throws IOException {
    // given: un ruleset con un splitter declarado
    String json = """
        {
          "splitters": { "groups": "," },
          "rules": [{ "name": "r", "grant": ["G"],
            "when": { "attribute": "groups", "anyOf": ["x"] } }]
        }
        """;

    // when: se parsea el ruleset
    RuleSet rs = RuleSet.parse(json);

    // then: el splitter se parsea con su separador
    assertNotNull(rs.splitters);
    assertEquals(",", rs.splitters.get("groups"));
  }

  @Test
  void parse_WithFallback_ReturnsRuleSetWithFallback() throws IOException {
    // given: un ruleset con un fallback declarado
    String json = """
        {
          "rules": [{ "name": "r", "grant": ["G"],
            "when": { "attribute": "a", "anyOf": ["v"] } }],
          "fallback": { "whenNoRulesMatched": ["INVESTIGADOR"] }
        }
        """;

    // when: se parsea el ruleset
    RuleSet rs = RuleSet.parse(json);

    // then: el fallback se parsea con sus grupos
    assertNotNull(rs.fallback);
    assertEquals(Collections.singletonList("INVESTIGADOR"), rs.fallback.whenNoRulesMatched);
  }

  @Test
  void parse_WithAllCombinator_ReturnsRuleSetWithNestedConditions() throws IOException {
    // given: un ruleset con una condición que usa el combinador 'all'
    String json = """
        {
          "rules": [{
            "name": "r",
            "grant": ["G"],
            "when": {
              "all": [
                { "attribute": "a", "anyOf": ["1"] },
                { "attribute": "a", "startsWithAny": ["prefix:"] }
              ]
            }
          }]
        }
        """;

    // when: se parsea el ruleset
    RuleSet rs = RuleSet.parse(json);

    // then: el combinador 'all' contiene las dos condiciones anidadas
    assertNotNull(rs.rules.get(0).when.all);
    assertEquals(2, rs.rules.get(0).when.all.size());
  }

  @Test
  void parse_WithUnknownJsonFields_ReturnsRuleSet() throws IOException {
    // given: un ruleset con un campo desconocido a nivel superior
    String json = """
        {
          "futureField": "whatever",
          "rules": [{ "name": "r", "grant": ["G"],
            "when": { "attribute": "a", "anyOf": ["v"] } }]
        }
        """;

    // when: se parsea el ruleset
    RuleSet rs = RuleSet.parse(json);

    // then: el campo desconocido se ignora y el parseo tiene éxito
    assertNotNull(rs);
  }

  @Test
  void parse_WithExampleRuleset_ReturnsRuleSet() throws IOException {
    // given: el ruleset de ejemplo disponible en el classpath de test
    RuleSet rs;
    try (InputStream in = getClass().getResourceAsStream("/rulesets/ruleset-example.json")) {
      assertNotNull(in, "example ruleset must be on the test classpath");

      // when: se parsea el ruleset de ejemplo
      rs = RuleSet.parse(new String(in.readAllBytes(), StandardCharsets.UTF_8));
    }

    // then: el ruleset se parsea y contiene al menos una regla
    assertNotNull(rs);
    assertFalse(rs.rules.isEmpty(), "Example ruleset must contain at least one rule");
  }

  @Test
  void parse_WithEmptyRulesArray_ThrowsIllegalArgumentException() {
    // given: un ruleset con el array de reglas vacío
    String json = """
        { "rules": [] }
        """;

    // when: se parsea el ruleset
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> RuleSet.parse(json));

    // then: se rechaza indicando que no hay reglas
    assertTrue(ex.getMessage() != null && ex.getMessage().contains("ruleset has no rules"));
  }

  @Test
  void parse_WithMissingRules_ThrowsIllegalArgumentException() {
    // given: un ruleset sin el campo 'rules'
    String json = """
        { "fallback": { "whenNoRulesMatched": ["G"] } }
        """;

    // when: se parsea el ruleset
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> RuleSet.parse(json));

    // then: se rechaza indicando que no hay reglas
    assertTrue(ex.getMessage() != null && ex.getMessage().contains("ruleset has no rules"));
  }

  @Test
  void parse_WithLeafWithoutAttribute_ThrowsIllegalArgumentException() {
    // given: una condición hoja sin 'attribute'
    String json = """
        {
          "rules": [{ "name": "r", "grant": ["G"],
            "when": { "anyOf": ["v"] } }]
        }
        """;

    // when: se parsea el ruleset
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> RuleSet.parse(json));

    // then: se rechaza indicando que la hoja requiere 'attribute'
    assertTrue(ex.getMessage() != null
        && ex.getMessage().contains("leaf condition requires 'attribute'"));
  }

  @Test
  void parse_WithLeafWithTwoOperators_ThrowsIllegalArgumentException() {
    // given: una condición hoja con dos operadores (anyOf y regex)
    String json = """
        {
          "rules": [{ "name": "r", "grant": ["G"],
            "when": { "attribute": "a", "anyOf": ["v"], "regex": "x" } }]
        }
        """;

    // when: se parsea el ruleset
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> RuleSet.parse(json));

    // then: se rechaza indicando que solo se admite un operador de hoja
    assertTrue(ex.getMessage() != null
        && ex.getMessage().contains("leaf condition requires exactly one of anyOf/startsWithAny/regex"));
  }

  @Test
  void parse_WithCombinatorMixedWithLeaf_ThrowsIllegalArgumentException() {
    // given: una condición que mezcla un combinador con operadores de hoja
    String json = """
        {
          "rules": [{ "name": "r", "grant": ["G"],
            "when": { "all": [{ "attribute": "a", "anyOf": ["v"] }],
                      "attribute": "b", "anyOf": ["w"] } }]
        }
        """;

    // when: se parsea el ruleset
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> RuleSet.parse(json));

    // then: se rechaza indicando que no se pueden mezclar combinadores con
    // operadores de hoja
    assertTrue(ex.getMessage() != null
        && ex.getMessage().contains("combinators cannot be mixed with leaf operators"));
  }

  @Test
  void parse_WithTwoCombinatorsInSameNode_ThrowsIllegalArgumentException() {
    // given: una condición con dos combinadores ('all' y 'any') en el mismo nodo
    String json = """
        {
          "rules": [{ "name": "r", "grant": ["G"],
            "when": { "all": [{ "attribute": "a", "anyOf": ["v"] }],
                      "any": [{ "attribute": "b", "anyOf": ["w"] }] } }]
        }
        """;

    // when: se parsea el ruleset
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> RuleSet.parse(json));

    // then: se rechaza indicando que solo se admite uno de all/any/not
    assertTrue(ex.getMessage() != null
        && ex.getMessage().contains("only one of all/any/not is allowed"));
  }

  @Test
  void parse_WithMalformedJson_ThrowsIOException() {
    // given: un texto JSON malformado
    String json = "{ not valid json ";

    // when: se parsea el ruleset
    IOException ex = assertThrows(IOException.class, () -> RuleSet.parse(json));

    // then: se lanza una IOException con mensaje descriptivo del carácter
    // inesperado
    assertTrue(ex.getMessage() != null && ex.getMessage().contains("Unexpected character"));
  }

  @Test
  void parse_WithRuleWithoutGrant_ThrowsIllegalArgumentException() {
    // given: una regla sin el campo 'grant'
    String json = """
        {
          "rules": [{ "name": "r",
            "when": { "attribute": "a", "anyOf": ["v"] } }]
        }
        """;

    // when: se parsea el ruleset
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> RuleSet.parse(json));

    // then: se rechaza indicando que 'grant' no puede estar vacío
    assertTrue(ex.getMessage() != null
        && ex.getMessage().contains("'grant' must not be empty"));
  }

  @Test
  void parse_WithRuleWithEmptyGrant_ThrowsIllegalArgumentException() {
    // given: una regla con el campo 'grant' vacío
    String json = """
        {
          "rules": [{ "name": "r", "grant": [],
            "when": { "attribute": "a", "anyOf": ["v"] } }]
        }
        """;

    // when: se parsea el ruleset
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> RuleSet.parse(json));

    // then: se rechaza indicando que 'grant' no puede estar vacío
    assertTrue(ex.getMessage() != null
        && ex.getMessage().contains("'grant' must not be empty"));
  }

  @Test
  void parse_WithRuleWithoutWhen_ThrowsIllegalArgumentException() {
    // given: una regla sin el campo 'when'
    String json = """
        {
          "rules": [{ "name": "r", "grant": ["G"] }]
        }
        """;

    // when: se parsea el ruleset
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> RuleSet.parse(json));

    // then: se rechaza indicando que 'when' es obligatorio
    assertTrue(ex.getMessage() != null && ex.getMessage().contains("'when' is required"));
  }

}
