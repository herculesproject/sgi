package org.crue.hercules.sgi.auth.broker.saml.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.crue.hercules.sgi.auth.broker.saml.mappers.rules.RuleEngine;
import org.crue.hercules.sgi.auth.broker.saml.mappers.rules.RuleSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Validación end-to-end del ruleset de ejemplo distribuido
 * ({@code src/test/resources/rulesets/ruleset-example.json}) con diferentes
 * combinaciones de atributos
 */
class ExampleRulesetIntegrationTest {

  private static RuleSet example;

  @BeforeAll
  static void loadRuleset() throws IOException {
    example = loadClasspath("/rulesets/ruleset-example.json");
  }

  private static RuleSet loadClasspath(String resource) throws IOException {
    try (InputStream in = ExampleRulesetIntegrationTest.class.getResourceAsStream(resource)) {
      if (in == null) {
        throw new IOException("Cannot locate classpath resource " + resource);
      }
      return RuleSet.parse(new String(in.readAllBytes(), StandardCharsets.UTF_8));
    }
  }

  private static Map<String, List<Object>> attrs(String key, Object... values) {
    Map<String, List<Object>> map = new HashMap<>();
    map.put(key, Arrays.asList(values));
    return map;
  }

  @Test
  void apply_WithAdminGroup_GrantsAdminBundle() {
    // given: 'memberOf' es un único valor separado por comas
    Map<String, List<Object>> attributes = attrs("memberOf", "team-x,sgi-admin,team-y");

    // when: se evalúa el ruleset
    Set<String> granted = RuleEngine.apply(example, attributes);

    // then: se concede el bundle completo de administración
    assertEquals(Set.of("SYSADM-CSP", "ADMINISTRADOR-SGI"), granted);
  }

  @Test
  void apply_WithSplitter_DoesNotLeakUnrelatedTokens() {
    // given: un 'memberOf' con un token de grupo no mapeado
    Map<String, List<Object>> attributes = attrs("memberOf", "sgi-admin,random-team");

    // when: se evalúa el ruleset
    Set<String> granted = RuleEngine.apply(example, attributes);

    // then: los tokens no mapeados no se convierten en grupos concedidos
    assertFalse(granted.contains("random-team"), "Unmapped tokens must not become grants");
  }

  @Test
  void apply_WithResearchAffiliationPrefix_GrantsInvestigador() {
    // given: una afiliación cuyo valor empieza por el prefijo de investigación
    Map<String, List<Object>> attributes = attrs("eduPersonAffiliation", "staff:research:biology");

    // when: se evalúa el ruleset
    Set<String> granted = RuleEngine.apply(example, attributes);

    // then: se concede el grupo de investigador
    assertEquals(Set.of("INVESTIGADOR"), granted);
  }

  @Test
  void apply_WithUnitAndRole_GrantsManagerBundle() {
    // given: los atributos incluyen tanto la unidad como el rol requeridos
    Map<String, List<Object>> both = new HashMap<>();
    both.put("unitCode", List.of("U-RESEARCH"));
    both.put("role", List.of("manager"));

    // when: se evalúa el ruleset
    Set<String> granted = RuleEngine.apply(example, both);

    // then: se concede el bundle de gestor de unidad
    assertTrue(granted.contains("GESTOR-CSP-UGI"));
    assertTrue(granted.contains("GESTOR-PRC"));
  }

  @Test
  void apply_WithUnitOnly_DoesNotGrantManagerBundle() {
    // given: solo está presente la unidad, sin el rol
    Map<String, List<Object>> attributes = attrs("unitCode", "U-RESEARCH");

    // when: se evalúa el ruleset
    Set<String> granted = RuleEngine.apply(example, attributes);

    // then: no se concede porque coincide una sola condición
    assertFalse(granted.contains("GESTOR-CSP-UGI"), "'all' must not match with only one condition");
  }

  @Test
  void apply_WithEmployeeIdMatchingRegex_GrantsVisor() {
    // given: un identificador de empleado que coincide con la regex
    Map<String, List<Object>> attributes = attrs("employeeId", "EMP-1234");

    // when: se evalúa el ruleset
    Set<String> granted = RuleEngine.apply(example, attributes);

    // then: se concede el grupo de visor
    assertEquals(Set.of("VISOR-CSP"), granted);
  }

  @Test
  void apply_WithEmployeeIdNotMatchingRegex_AppliesFallback() {
    // given: un identificador de empleado que no coincide con la regex
    Map<String, List<Object>> attributes = attrs("employeeId", "EMP-12");

    // when: se evalúa el ruleset
    Set<String> granted = RuleEngine.apply(example, attributes);

    // then: al no coincidir ninguna regla se aplica el fallback
    assertEquals(Set.of("VISITANTE"), granted, "Bad id matches no rule -> fallback");
  }

  @Test
  void apply_WithDirectorRole_GrantsEthicsManager() {
    // given: un usuario con rol director
    Map<String, List<Object>> attributes = attrs("role", "director");

    // when: se evalúa el ruleset
    Set<String> granted = RuleEngine.apply(example, attributes);

    // then: se concede el grupo de gestor de ética
    assertEquals(Set.of("GESTOR-ETICA"), granted);
  }

  @Test
  void apply_WithReadonlyMember_ExcludesEthicsManager() {
    // given: un director que además pertenece al grupo de solo lectura
    Map<String, List<Object>> attributes = new HashMap<>();
    attributes.put("role", List.of("director"));
    attributes.put("memberOf", List.of("sgi-readonly"));

    // when: se evalúa el ruleset
    Set<String> granted = RuleEngine.apply(example, attributes);

    // then: el 'not' excluye a los miembros de solo lectura y se aplica el fallback
    assertFalse(granted.contains("GESTOR-ETICA"), "'not' must exclude read-only members");
    assertEquals(Set.of("VISITANTE"), granted, "Excluded + no other rule -> fallback");
  }

  @Test
  void apply_WithoutAttributes_AppliesFallback() {
    // given: un conjunto de atributos vacío
    Map<String, List<Object>> attributes = new HashMap<>();

    // when: se evalúa el ruleset
    Set<String> granted = RuleEngine.apply(example, attributes);

    // then: al no coincidir ninguna regla se aplica el fallback
    assertEquals(Set.of("VISITANTE"), granted);
  }

  @Test
  void apply_WhenRuleMatched_DoesNotApplyFallback() {
    // given: un 'memberOf' que coincide con la regla de administradores
    Map<String, List<Object>> attributes = attrs("memberOf", "sgi-admin");

    // when: se evalúa el ruleset
    Set<String> granted = RuleEngine.apply(example, attributes);

    // then: el fallback no se dispara cuando una regla coincide
    assertFalse(granted.contains("VISITANTE"), "Fallback must not fire when a rule matched");
  }
}
