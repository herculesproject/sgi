package org.crue.hercules.sgi.framework.web.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class ValidationError {
  private String object;
  private String field;
  private Object rejectedValue;
  private String message;

  ValidationError(String object, String message) {
    this.object = object;
    this.message = message;
  }
}