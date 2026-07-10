package org.crue.hercules.sgi.auth.broker.saml.mappers.rules;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Configuración de nivel superior parseada desde JSON.
 *
 * <pre>
 * {
 *   "splitters":  { "&lt;attribute&gt;": "&lt;separator&gt;", ... },
 *   "rules":      [ ... ],
 *   "fallback":   { "whenNoRulesMatched": [ ... ] }
 * }
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RuleSet {

  /** Preprocesadores opcionales: nombre de atributo y separador. */
  public Map<String, String> splitters;

  /**
   * Reglas evaluadas en orden de declaración. Todas las reglas que se cumplen
   * contribuyen.
   */
  public List<Rule> rules;

  /** Grupos por defecto opcionales cuando ninguna regla se cumple. */
  public Fallback fallback;

  /**
   * Parsea y valida un ruleset JSON.
   *
   * @param json el texto JSON
   * @return un {@link RuleSet} validado
   * @throws IOException              cuando el JSON está malformado
   * @throws IllegalArgumentException cuando el documento es estructuralmente
   *                                  inválido
   */
  public static RuleSet parse(String json) throws IOException {
    JsonFactory factory = JsonFactory.builder()
        .enable(JsonReadFeature.ALLOW_JAVA_COMMENTS)
        .enable(JsonReadFeature.ALLOW_TRAILING_COMMA)
        .build();
    ObjectMapper mapper = new ObjectMapper(factory);
    RuleSet rs = mapper.readValue(json, RuleSet.class);
    rs.validate();
    return rs;
  }

  /**
   * Valida la estructura del ruleset.
   *
   * @throws IllegalArgumentException cuando el documento es estructuralmente
   *                                  inválido
   */
  public void validate() {
    if (rules == null || rules.isEmpty()) {
      throw new IllegalArgumentException("ruleset has no rules");
    }

    for (int i = 0; i < rules.size(); i++) {
      rules.get(i).validate("rules[" + i + "]");
    }
  }

}
