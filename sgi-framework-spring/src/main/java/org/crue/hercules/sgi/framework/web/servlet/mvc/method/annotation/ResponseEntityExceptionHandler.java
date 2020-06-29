package org.crue.hercules.sgi.framework.web.servlet.mvc.method.annotation;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

// TODO see: https://www.toptal.com/java/spring-boot-rest-api-error-handling
public abstract class ResponseEntityExceptionHandler
    extends org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler {

  @ExceptionHandler({ IllegalArgumentException.class })
  public final ResponseEntity<Object> handleIllegalArgumentException(Exception ex, WebRequest request)
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    HttpStatus status = HttpStatus.BAD_REQUEST;
    return handleExceptionInternal(ex, null, headers, status, request);
  }

}