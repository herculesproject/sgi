package org.crue.hercules.sgi.framework.web.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.crue.hercules.sgi.framework.web.error.Error;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

@ResponseBody
// Can't use @RestController as this is declared as a Bean in SgiWebConfig
@RequestMapping("${server.error.path:${error.path:/error}}")
@Slf4j
public class SgiErrorController implements ErrorController {

  /**
   * @return String
   */
  @Override
  public String getErrorPath() {
    log.debug("getErrorPath() - start");
    log.debug("getErrorPath() - end");
    return null;
  }

  /**
   * @param request  the HttpServletRequest
   * @param response the HttpServletResponse
   * @return Error
   */
  @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public Error errorJson(HttpServletRequest request, HttpServletResponse response) {
    log.debug("errorJson(HttpServletRequest request, HttpServletResponse response) - start");
    HttpStatus status = getStatus(request);

    Throwable ex = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
    String message = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

    Error error = new Error(status, message, ex);

    log.debug("errorJson(HttpServletRequest request, HttpServletResponse response) - end");
    return error;
  }

  /**
   * @param request the HttpServletRequest
   * @return HttpStatus
   */
  protected HttpStatus getStatus(HttpServletRequest request) {
    log.debug("getStatus(HttpServletRequest request) - start");
    Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
    if (statusCode == null) {
      log.debug("getStatus(HttpServletRequest request) - end");
      return HttpStatus.INTERNAL_SERVER_ERROR;
    }
    try {
      HttpStatus returnValue = HttpStatus.valueOf(statusCode);
      log.debug("getStatus(HttpServletRequest request) - end");
      return returnValue;
    } catch (Exception ex) {
      log.debug("getStatus(HttpServletRequest request) - end");
      return HttpStatus.INTERNAL_SERVER_ERROR;
    }
  }
}