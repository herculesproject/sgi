package org.crue.hercules.sgi.framework.web.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.crue.hercules.sgi.framework.web.error.Error;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${server.error.path:${error.path:/error}}")
public class SgiErrorController implements ErrorController {

  @Override
  public String getErrorPath() {
    return null;
  }

  @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public Error errorJson(HttpServletRequest request, HttpServletResponse response) {
    HttpStatus status = getStatus(request);

    Throwable ex = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
    String message = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

    Error error = new Error(status, message, ex);

    return error;
  }

  protected HttpStatus getStatus(HttpServletRequest request) {
    Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
    if (statusCode == null) {
      return HttpStatus.INTERNAL_SERVER_ERROR;
    }
    try {
      return HttpStatus.valueOf(statusCode);
    } catch (Exception ex) {
      return HttpStatus.INTERNAL_SERVER_ERROR;
    }
  }
}