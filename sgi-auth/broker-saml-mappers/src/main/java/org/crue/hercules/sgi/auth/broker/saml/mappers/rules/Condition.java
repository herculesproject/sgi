package org.crue.hercules.sgi.auth.broker.saml.mappers.rules;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Condición declarativa evaluada contra un mapa de atributos SAML.
 *
 * <p>
 * Una {@code Condition} es una condición o un operador lógico.
 *
 * <b>Condiciones</b>
 * <ul>
 * <li>{@code attribute} + {@code anyOf}: algún valor del atributo es igual a
 * una
 * de las cadenas listadas.</li>
 * <li>{@code attribute} + {@code startsWithAny}: algún valor del atributo
 * empieza por uno de los prefijos listados.</li>
 * <li>{@code attribute} + {@code regex}: algún valor del atributo coincide con
 * la expresión regular indicada.</li>
 * </ul>
 *
 * <b>Operadores lógicos</b>
 * <ul>
 * <li>{@code all}: se satisfacen todas las condiciones anidadas.</li>
 * <li>{@code any}: se satisface al menos una condición anidada.</li>
 * <li>{@code not}: la condición anidada no se satisface.</li>
 * </ul>
 *
 * <p>
 * Si el atributo referenciado está ausente o vacío, las condiciones
 * evalúan a {@code false}; por tanto {@code not} devuelve {@code true} para un
 * atributo ausente.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Condition {

  public List<Condition> all;
  public List<Condition> any;
  public Condition not;

  public String attribute;
  public List<String> anyOf;
  public List<String> startsWithAny;
  public String regex;

  /**
   * Verifica que la condición se ajusta exactamente a una de las formas
   * soportadas.
   *
   * @param path el puntero JSON de esta condición, usado en los mensajes de
   *             error
   * @throws IllegalArgumentException cuando la forma es inválida
   */
  public void validate(String path) {
    int combinators = (all != null ? 1 : 0) + (any != null ? 1 : 0) + (not != null ? 1 : 0);
    boolean hasLeaf = attribute != null || anyOf != null || startsWithAny != null || regex != null;

    if (combinators == 0 && !hasLeaf) {
      throw new IllegalArgumentException(path + ": condition is empty");
    }

    if (combinators > 1) {
      throw new IllegalArgumentException(path + ": only one of all/any/not is allowed");
    }

    if (combinators == 1 && hasLeaf) {
      throw new IllegalArgumentException(path + ": combinators cannot be mixed with leaf operators");
    }

    if (combinators == 0) {
      if (attribute == null) {
        throw new IllegalArgumentException(path + ": leaf condition requires 'attribute'");
      }

      int leafOps = (anyOf != null ? 1 : 0) + (startsWithAny != null ? 1 : 0) + (regex != null ? 1 : 0);
      if (leafOps != 1) {
        throw new IllegalArgumentException(
            path + ": leaf condition requires exactly one of anyOf/startsWithAny/regex");
      }
    }

    if (all != null) {
      for (int i = 0; i < all.size(); i++) {
        all.get(i).validate(path + ".all[" + i + "]");
      }
    }

    if (any != null) {
      for (int i = 0; i < any.size(); i++) {
        any.get(i).validate(path + ".any[" + i + "]");
      }
    }

    if (not != null) {
      not.validate(path + ".not");
    }
  }

  /**
   * Evalúa esta condición contra el mapa de atributos proporcionado.
   *
   * @param attributes nombre de atributo y su lista de valores
   * @return {@code true} cuando la condición se cumple
   */
  public boolean evaluate(Map<String, List<Object>> attributes) {
    if (all != null) {
      for (Condition c : all) {
        if (!c.evaluate(attributes)) {
          return false;
        }
      }
      return true;
    }

    if (any != null) {
      for (Condition c : any) {
        if (c.evaluate(attributes)) {
          return true;
        }
      }

      return false;
    }

    if (not != null) {
      return !not.evaluate(attributes);
    }

    List<Object> values = attributes.get(attribute);
    if (values == null || values.isEmpty()) {
      return false;
    }

    if (anyOf != null) {
      for (Object value : values) {
        String s = stringify(value);
        if (s != null && anyOf.contains(s)) {
          return true;
        }
      }

      return false;
    }

    if (startsWithAny != null) {
      for (Object value : values) {
        String s = stringify(value);
        if (s == null) {
          continue;
        }

        for (String prefix : startsWithAny) {
          if (s.startsWith(prefix)) {
            return true;
          }
        }
      }

      return false;
    }

    if (regex != null) {
      Pattern p = Pattern.compile(regex);
      for (Object value : values) {
        String s = stringify(value);
        if (s != null && p.matcher(s).matches()) {
          return true;
        }
      }

      return false;
    }

    return false;
  }

  private static String stringify(Object value) {
    return value == null ? null : value.toString();
  }

}
