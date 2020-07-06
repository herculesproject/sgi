package org.crue.hercules.sgi.framework.data.search;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum QueryOperation {
  EQUALS(":"), NOT_EQUALS("!:"), LIKE("~"), NOT_LIKE("!~"), GREATER(">"), GREATER_OR_EQUAL(">:"), LOWER("<"),
  LOWER_OR_EQUAL("<:");

  private final String asString;

  public static QueryOperation fromString(String input) {
    for (QueryOperation operation : QueryOperation.values()) {
      if (operation.asString.equalsIgnoreCase(input)) {
        return operation;
      }
    }
    return null;
  }
}