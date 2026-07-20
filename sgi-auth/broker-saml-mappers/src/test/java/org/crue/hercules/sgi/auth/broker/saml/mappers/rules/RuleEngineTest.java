package org.crue.hercules.sgi.auth.broker.saml.mappers.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

class RuleEngineTest {

  @Test
  void apply_WithAnyOfMatch_GrantsGroup() {
    // given: una regla con anyOf sobre el atributo 'grupos'
    RuleSet rs = parse("""
        {
          "rules": [
            {
              "name": "r",
              "grant": ["GESTOR-CSP"],
              "when": {
                "attribute": "grupos",
                "anyOf": ["gestor-csp"]
              }
            }
          ]
        }
        """);

    // when: se evalúa con un valor que coincide
    Set<String> result = apply(rs, attrs("grupos", "gestor-csp"));

    // then: se concede el grupo de la regla
    assertEquals(Collections.singleton("GESTOR-CSP"), result);
  }

  @Test
  void apply_WithAnyOfNoMatch_GrantsNothing() {
    // given: una regla con anyOf sobre el atributo 'grupos'
    RuleSet rs = parse("""
        {
          "rules": [
            {
              "name": "r",
              "grant": ["GESTOR-CSP"],
              "when": {
                "attribute": "grupos",
                "anyOf": ["gestor-csp"]
              }
            }
          ]
        }
        """);

    // when: se evalúa con un valor que no coincide
    Set<String> result = apply(rs, attrs("grupos", "visor-csp"));

    // then: no se concede nada
    assertTrue(result.isEmpty());
  }

  @Test
  void apply_WithAnyOfMultipleValuesOneMatching_GrantsGroup() {
    // given: una regla con anyOf de varios valores admitidos
    RuleSet rs = parse("""
        {
          "rules": [
            {
              "name": "r",
              "grant": ["TECNICO-ETICA"],
              "when": {
                "attribute": "grupos",
                "anyOf": ["tecnico-etica", "tecnico-bioseguridad"]
              }
            }
          ]
        }
        """);

    // when: se evalúa con un conjunto donde uno de los valores coincide
    Set<String> result = apply(rs, attrs("grupos", "otros", "tecnico-bioseguridad"));

    // then: se asigna el grupo porque uno de los valores coincide
    assertEquals(Collections.singleton("TECNICO-ETICA"), result);
  }

  @Test
  void apply_WithAnyOfAttributeAbsent_GrantsNothing() {
    // given: una regla con anyOf sobre el atributo 'grupos'
    RuleSet rs = parse("""
        {
          "rules": [
            {
              "name": "r",
              "grant": ["GESTOR-CSP"],
              "when": {
                "attribute": "grupos",
                "anyOf": ["gestor-csp"]
              }
            }
          ]
        }
        """);

    // when: se evalúa sin ningún atributo presente
    Set<String> result = apply(rs, Collections.emptyMap());

    // then: no se concede nada
    assertTrue(result.isEmpty());
  }

  @Test
  void apply_WithMultipleMatchingRules_GrantsAllGroups() {
    // given: dos reglas que coinciden con distintos valores del mismo atributo
    RuleSet rs = parse("""
        {
          "rules": [
            {
              "name": "r1",
              "grant": ["GESTOR-CSP"],
              "when": {
                "attribute": "g",
                "anyOf": ["gestor"]
              }
            },
            {
              "name": "r2",
              "grant": ["VISOR-CSP"],
              "when": {
                "attribute": "g",
                "anyOf": ["visor"]
              }
            }
          ]
        }
        """);

    // when: el atributo contiene los valores que coinciden con ambas reglas
    Map<String, List<Object>> a = new HashMap<>();
    a.put("g", Arrays.asList("gestor", "visor"));
    Set<String> result = apply(rs, a);

    // then: ambas reglas contribuyen al conjunto de grupos concedidos
    assertTrue(result.contains("GESTOR-CSP"));
    assertTrue(result.contains("VISOR-CSP"));
    assertEquals(2, result.size());
  }

  @Test
  void apply_WithStartsWithAnyMatch_GrantsGroup() {
    // given: una regla con startsWithAny sobre un prefijo
    RuleSet rs = parse("""
        {
          "rules": [
            {
              "name": "r",
              "grant": ["VISOR-PRC"],
              "when": {
                "attribute": "code",
                "startsWithAny": ["urn:schac:personalUniqueCode:es:employeeID:a003"]
              }
            }
          ]
        }
        """);

    // when: se evalúa con un valor que empieza por el prefijo
    Set<String> result = apply(rs,
        attrs("code", "urn:schac:personalUniqueCode:es:employeeID:a003b025"));

    // then: se concede el grupo de la regla
    assertEquals(Collections.singleton("VISOR-PRC"), result);
  }

  @Test
  void apply_WithStartsWithAnyNoMatch_GrantsNothing() {
    // given: una regla con startsWithAny sobre un prefijo
    RuleSet rs = parse("""
        {
          "rules": [
            {
              "name": "r",
              "grant": ["VISOR-PRC"],
              "when": {
                "attribute": "code",
                "startsWithAny": ["urn:schac:personalUniqueCode:es:employeeID:a003"]
              }
            }
          ]
        }
        """);

    // when: se evalúa con un valor que no empieza por el prefijo
    Set<String> result = apply(rs,
        attrs("code", "urn:schac:personalUniqueCode:es:employeeID:a999x000"));

    // then: no se concede nada
    assertTrue(result.isEmpty());
  }

  @Test
  void apply_WithRegexMatch_GrantsGroup() {
    // given: una regla con una expresión regular
    RuleSet rs = parse("""
        {
          "rules": [
            {
              "name": "r",
              "grant": ["INVESTIGADOR"],
              "when": {
                "attribute": "uid",
                "regex": "inv-\\\\d+"
              }
            }
          ]
        }
        """);

    // when: se evalúa con un valor que coincide con la regex
    Set<String> result = apply(rs, attrs("uid", "inv-12345"));

    // then: se concede el grupo de la regla
    assertEquals(Collections.singleton("INVESTIGADOR"), result);
  }

  @Test
  void apply_WithRegexNoMatch_GrantsNothing() {
    // given: una regla con una expresión regular
    RuleSet rs = parse("""
        {
          "rules": [
            {
              "name": "r",
              "grant": ["INVESTIGADOR"],
              "when": {
                "attribute": "uid",
                "regex": "inv-\\\\d+"
              }
            }
          ]
        }
        """);

    // when: se evalúa con un valor que no coincide con la regex
    Set<String> result = apply(rs, attrs("uid", "adm-007"));

    // then: no se concede nada
    assertTrue(result.isEmpty());
  }

  @Test
  void apply_WithAllCombinator_GrantsOnlyWhenBothConditionsMatch() {
    // given: una regla con 'all' que exige dos condiciones
    RuleSet rs = parse("""
        {
          "rules": [
            {
              "name": "r",
              "grant": ["GESTOR-CSP-OPE"],
              "when": {
                "all": [
                  {
                    "attribute": "classif",
                    "anyOf": ["FILIACION:a003"]
                  },
                  {
                    "attribute": "classif",
                    "startsWithAny": ["UNIDADES:a001b085c005"]
                  }
                ]
              }
            }
          ]
        }
        """);

    // when: ambas condiciones están presentes
    Set<String> both = apply(rs,
        attrs("classif", "FILIACION:a003", "UNIDADES:a001b085c005z"));
    // when: solo una condición está presente
    Set<String> onlyOne = apply(rs, attrs("classif", "FILIACION:a003"));

    // then: solo coincide cuando se cumplen las dos condiciones
    assertEquals(Collections.singleton("GESTOR-CSP-OPE"), both);
    assertTrue(onlyOne.isEmpty());
  }

  @Test
  void apply_WithAnyCombinator_GrantsWhenOneConditionMatches() {
    // given: una regla con 'any' con dos condiciones
    RuleSet rs = parse("""
        {
          "rules": [
            {
              "name": "r",
              "grant": ["ADMIN"],
              "when": {
                "any": [
                  {
                    "attribute": "role",
                    "anyOf": ["admin-a"]
                  },
                  {
                    "attribute": "role",
                    "anyOf": ["admin-b"]
                  }
                ]
              }
            }
          ]
        }
        """);

    // when: se evalúa con un valor que coincide una de las condiciones y con otro
    // que no coincide ninguna
    Set<String> matching = apply(rs, attrs("role", "admin-b"));
    Set<String> nonMatching = apply(rs, attrs("role", "user"));

    // then: basta con que se cumpla una condición para conceder el grupo
    assertEquals(Collections.singleton("ADMIN"), matching);
    assertTrue(nonMatching.isEmpty());
  }

  @Test
  void apply_WithNotCombinator_InvertsCondition() {
    // given: una regla con 'not' sobre una condición
    RuleSet rs = parse("""
        {
          "rules": [
            {
              "name": "r",
              "grant": ["PUBLICO"],
              "when": {
                "not": {
                  "attribute": "role",
                  "anyOf": ["blocked"]
                }
              }
            }
          ]
        }
        """);

    // when: se evalúa con el valor bloqueado presente y con un valor distinto
    Set<String> blocked = apply(rs, attrs("role", "blocked"));
    Set<String> other = apply(rs, attrs("role", "visitor"));

    // then: 'blocked' presente hace que 'not' coincida y no se asigne y con un
    // valor distinto sí asigna el grupo
    assertTrue(blocked.isEmpty());
    assertEquals(Collections.singleton("PUBLICO"), other);
  }

  @Test
  void apply_WithNotAndAbsentAttribute_GrantsGroup() {
    // given: una regla con el 'not' sobre una condición
    RuleSet rs = parse("""
        {
          "rules": [
            {
              "name": "r",
              "grant": ["PUBLICO"],
              "when": {
                "not": {
                  "attribute": "role",
                  "anyOf": ["blocked"]
                }
              }
            }
          ]
        }
        """);

    // when: se evalúa sin el atributo presente
    Set<String> result = apply(rs, Collections.emptyMap());

    // then: se concede porque el atributo no está presente
    assertEquals(Collections.singleton("PUBLICO"), result);
  }

  @Test
  void apply_WithSplitter_ExpandsCsvBeforeEvaluation() {
    // given: un splitter por comas y una regla sobre uno de los tokens
    RuleSet rs = parse("""
        {
          "splitters": {
            "groups": ","
          },
          "rules": [
            {
              "name": "r",
              "grant": ["ADMINISTRADOR-CSP"],
              "when": {
                "attribute": "groups",
                "anyOf": ["SGI.administrador-csp"]
              }
            }
          ]
        }
        """);

    // when: el atributo llega como una única cadena CSV
    Set<String> result = apply(rs,
        attrs("groups", "SGI.visor-csp,SGI.administrador-csp,SGI.gestor-pii"));

    // then: el CSV se expande y la regla coincide con el token correspondiente
    assertEquals(Collections.singleton("ADMINISTRADOR-CSP"), result);
  }

  @Test
  void apply_WithSplitterAndCsvNotContainingValue_GrantsNothing() {
    // given: un splitter por comas y una regla sobre un token concreto
    RuleSet rs = parse("""
        {
          "splitters": {
            "groups": ","
          },
          "rules": [
            {
              "name": "r",
              "grant": ["ADMINISTRADOR-CSP"],
              "when": {
                "attribute": "groups",
                "anyOf": ["SGI.administrador-csp"]
              }
            }
          ]
        }
        """);

    // when: el CSV no contiene el token requerido
    Set<String> result = apply(rs, attrs("groups", "SGI.visor-csp,SGI.gestor-pii"));

    // then: no se concede nada
    assertTrue(result.isEmpty());
  }

  @Test
  void apply_WhenNoRuleMatched_AppliesFallback() {
    // given: una regla y un fallback declarado
    RuleSet rs = parse("""
        {
          "rules": [
            {
              "name": "r",
              "grant": ["ADMIN"],
              "when": {
                "attribute": "role",
                "anyOf": ["admin"]
              }
            }
          ],
          "fallback": {
            "whenNoRulesMatched": ["INVESTIGADOR"]
          }
        }
        """);

    // when: ninguna regla coincide
    Set<String> result = apply(rs, attrs("role", "other"));

    // then: se aplica el fallback
    assertEquals(Collections.singleton("INVESTIGADOR"), result);
  }

  @Test
  void apply_WhenAtLeastOneRuleMatched_DoesNotApplyFallback() {
    // given: una regla y un fallback declarado
    RuleSet rs = parse("""
        {
          "rules": [
            {
              "name": "r",
              "grant": ["ADMIN"],
              "when": {
                "attribute": "role",
                "anyOf": ["admin"]
              }
            }
          ],
          "fallback": {
            "whenNoRulesMatched": ["INVESTIGADOR"]
          }
        }
        """);

    // when: al menos una regla coincide
    Set<String> result = apply(rs, attrs("role", "admin"));

    // then: no se aplica el fallback
    assertEquals(Collections.singleton("ADMIN"), result);
  }

  @Test
  void apply_WithBundleRule_GrantsAllListedGroups() {
    // given: una regla que concede varios grupos a la vez
    RuleSet rs = parse("""
        {
          "rules": [
            {
              "name": "sysadm",
              "grant": ["SYSADM-CSP", "ADMINISTRADOR-SGI"],
              "when": {
                "attribute": "grupos",
                "anyOf": ["sysadm"]
              }
            }
          ]
        }
        """);

    // when: la regla coincide
    Set<String> result = apply(rs, attrs("grupos", "sysadm"));

    // then: se conceden todos los grupos listados
    assertTrue(result.contains("SYSADM-CSP"));
    assertTrue(result.contains("ADMINISTRADOR-SGI"));
    assertEquals(2, result.size());
  }

  @Test
  void apply_WithMultipleRules_PreservesDeclarationOrder() {
    // given: tres reglas que coinciden con el mismo valor, en orden de declaración
    RuleSet rs = parse("""
        {
          "rules": [
            {
              "name": "r1",
              "grant": ["A"],
              "when": {
                "attribute": "x",
                "anyOf": ["1"]
              }
            },
            {
              "name": "r2",
              "grant": ["B"],
              "when": {
                "attribute": "x",
                "anyOf": ["1"]
              }
            },
            {
              "name": "r3",
              "grant": ["C"],
              "when": {
                "attribute": "x",
                "anyOf": ["1"]
              }
            }
          ]
        }
        """);

    // when: todas las reglas coinciden
    Set<String> result = apply(rs, attrs("x", "1"));

    // then: el conjunto preserva el orden de declaración
    assertIterableEquals(Arrays.asList("A", "B", "C"), result);
  }

  @Test
  void apply_WithJsonCommentsAndTrailingCommas_GrantsGroup() {
    // given: un JSON con comentarios y comas finales
    RuleSet rs = parse("""
        {
          // comentario de nivel superior
          "rules": [
            {
              "name": "r", // comentario en línea
              "grant": ["G"],
              "when": {
                "attribute": "a",
                "anyOf": ["v"],
              },
            },
          ],
        }
        """);

    // when: se evalúa con el valor que coincide
    Set<String> result = apply(rs, attrs("a", "v"));

    // then: el JSON se parsea correctamente y la regla concede su grupo
    assertEquals(Collections.singleton("G"), result);
  }

  private static Map<String, List<Object>> attrs(String key, Object... values) {
    Map<String, List<Object>> map = new HashMap<>();
    map.put(key, Arrays.asList(values));
    return map;
  }

  private static RuleSet parse(String json) {
    try {
      return RuleSet.parse(json);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static Set<String> apply(RuleSet rs, Map<String, List<Object>> attributes) {
    return RuleEngine.apply(rs, attributes);
  }

}
