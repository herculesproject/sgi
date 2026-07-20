package org.crue.hercules.sgi.auth.broker.saml.mappers.rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Evaluador sin estado que transforma un conjunto de atributos SAML más un
 * {@link RuleSet} en el conjunto de rutas de grupo a conceder.
 *
 * <p>
 * Orden de evaluación:
 * <ol>
 * <li>Los {@link RuleSet#splitters} expanden atributos de valor único en
 * atributos multivaluados.</li>
 * <li>Cada {@link Rule} se evalúa de forma independiente. Todas las reglas que
 * se cumplen contribuyen al conjunto final; las reglas no se cortocircuitan
 * entre sí.</li>
 * <li>Si ninguna regla se cumple y hay un {@link Fallback} declarado, se añaden
 * los grupos del fallback.</li>
 * </ol>
 *
 * <p>
 * El resultado preserva el orden de inserción (orden de declaración de las
 * reglas, y luego el orden de concesión dentro de cada regla) para obtener
 * aserciones estables y comparables.
 */
public final class RuleEngine {

  private RuleEngine() {
  }

  /**
   * Evalúa el ruleset contra un mapa de atributos.
   *
   * @param ruleSet    el ruleset parseado y validado
   * @param attributes nombre de atributo y lista de valores, tal como se
   *                   extraen de un {@code AttributeStatement} SAML. El mapa no
   *                   se muta.
   * @return conjunto ordenado de rutas de grupo a conceder
   */
  public static Set<String> apply(RuleSet ruleSet, Map<String, List<Object>> attributes) {
    Map<String, List<Object>> effective = applySplitters(attributes, ruleSet.splitters);

    Set<String> granted = new LinkedHashSet<>();
    boolean anyMatched = false;
    for (Rule rule : ruleSet.rules) {
      if (rule.when.evaluate(effective)) {
        anyMatched = true;
        granted.addAll(rule.grant);
      }
    }

    if (!anyMatched && ruleSet.fallback != null && ruleSet.fallback.whenNoRulesMatched != null) {
      granted.addAll(ruleSet.fallback.whenNoRulesMatched);
    }

    return granted;
  }

  private static Map<String, List<Object>> applySplitters(
      Map<String, List<Object>> attributes,
      Map<String, String> splitters) {
    if (splitters == null || splitters.isEmpty()) {
      return attributes;
    }

    Map<String, List<Object>> copy = new HashMap<>(attributes);
    for (Map.Entry<String, String> e : splitters.entrySet()) {
      String attrName = e.getKey();
      String separator = e.getValue();
      List<Object> values = copy.get(attrName);
      if (values == null || separator == null || separator.isEmpty()) {
        continue;
      }

      Pattern split = Pattern.compile(Pattern.quote(separator));
      List<Object> expanded = new ArrayList<>();
      for (Object v : values) {
        if (v instanceof String) {
          for (String part : split.split((String) v)) {
            expanded.add(part);
          }
        } else {
          expanded.add(v);
        }
      }
      copy.put(attrName, expanded);
    }

    return copy;
  }

}
