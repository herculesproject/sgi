package org.crue.hercules.sgi.framework.data.sort;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SortOperation {
  ASC("+"), DESC("-");

  private final String asString;

  public static SortOperation fromString(String input) {
    for (SortOperation operation : SortOperation.values()) {
      if (operation.asString.equalsIgnoreCase(input)) {
        return operation;
      }
    }
    return null;
  }
}