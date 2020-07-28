package org.crue.hercules.sgi.framework.data.search;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Getter
@Slf4j
public enum QueryOperation {
  EQUALS(":"), NOT_EQUALS("!:"), LIKE("~"), NOT_LIKE("!~"), GREATER(">"), GREATER_OR_EQUAL(">:"), LOWER("<"),
  LOWER_OR_EQUAL("<:");

  private final String asString;

  public static QueryOperation fromString(String input) {
    log.debug("fromString(String input) - start");
    for (QueryOperation operation : QueryOperation.values()) {
      if (operation.asString.equalsIgnoreCase(input)) {
        log.debug("fromString(String input) - end");
        return operation;
      }
    }
    log.warn("No valid query operation found");
    log.debug("fromString(String input) - end");
    return null;
  }
}