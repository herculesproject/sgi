package org.crue.hercules.sgi.framework.web.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@Slf4j
public class ValidationError {
  private String object;
  private String field;
  private Object rejectedValue;
  private String message;

  ValidationError(String object, String message) {
    log.debug("ValidationError(String object, String message) - start");
    this.object = object;
    this.message = message;
    log.debug("ValidationError(String object, String message) - end");
  }
}