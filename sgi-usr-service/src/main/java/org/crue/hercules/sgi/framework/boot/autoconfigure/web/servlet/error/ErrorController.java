package org.crue.hercules.sgi.framework.boot.autoconfigure.web.servlet.error;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.error.ErrorAttributeOptions.Include;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

// TODO see: https://www.toptal.com/java/spring-boot-rest-api-error-handling
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

  private final ErrorAttributes errorAttributes;

  private final ErrorProperties errorProperties;

  @Autowired
  public ErrorController(ErrorAttributes errorAttributes, ServerProperties serverProperties,
      List<ErrorViewResolver> errorViewResolvers) {
    this.errorAttributes = errorAttributes;
    this.errorProperties = serverProperties.getError();
  }

  @Override
  public String getErrorPath() {
    return "/error";
  }

  @GetMapping
  public Map<String, Object> handleError(HttpServletRequest request, HttpServletResponse response) {
    HttpStatus status = getStatus(request);
    Map<String, Object> model = Collections
        .unmodifiableMap(getErrorAttributes(request, getErrorAttributeOptions(request, MediaType.TEXT_HTML)));
    response.setStatus(status.value());
    return model;
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

  protected ErrorAttributeOptions getErrorAttributeOptions(HttpServletRequest request, MediaType mediaType) {
    ErrorAttributeOptions options = ErrorAttributeOptions.defaults();
    if (this.errorProperties.isIncludeException()) {
      options = options.including(Include.EXCEPTION);
    }
    if (isIncludeStackTrace(request, mediaType)) {
      options = options.including(Include.STACK_TRACE);
    }
    if (isIncludeMessage(request, mediaType)) {
      options = options.including(Include.MESSAGE);
    }
    if (isIncludeBindingErrors(request, mediaType)) {
      options = options.including(Include.BINDING_ERRORS);
    }
    return options;
  }

  /**
   * Determine if the stacktrace attribute should be included.
   * 
   * @param request  the source request
   * @param produces the media type produced (or {@code MediaType.ALL})
   * @return if the stacktrace attribute should be included
   */
  @SuppressWarnings("deprecation")
  protected boolean isIncludeStackTrace(HttpServletRequest request, MediaType produces) {
    switch (getErrorProperties().getIncludeStacktrace()) {
      case ALWAYS:
        return true;
      case ON_PARAM:
      case ON_TRACE_PARAM:
        return getTraceParameter(request);
      default:
        return false;
    }
  }

  /**
   * Determine if the message attribute should be included.
   * 
   * @param request  the source request
   * @param produces the media type produced (or {@code MediaType.ALL})
   * @return if the message attribute should be included
   */
  protected boolean isIncludeMessage(HttpServletRequest request, MediaType produces) {
    switch (getErrorProperties().getIncludeMessage()) {
      case ALWAYS:
        return true;
      case ON_PARAM:
        return getMessageParameter(request);
      default:
        return false;
    }
  }

  /**
   * Determine if the errors attribute should be included.
   * 
   * @param request  the source request
   * @param produces the media type produced (or {@code MediaType.ALL})
   * @return if the errors attribute should be included
   */
  protected boolean isIncludeBindingErrors(HttpServletRequest request, MediaType produces) {
    switch (getErrorProperties().getIncludeBindingErrors()) {
      case ALWAYS:
        return true;
      case ON_PARAM:
        return getErrorsParameter(request);
      default:
        return false;
    }
  }

  /**
   * Provide access to the error properties.
   * 
   * @return the error properties
   */
  protected ErrorProperties getErrorProperties() {
    return this.errorProperties;
  }

  protected Map<String, Object> getErrorAttributes(HttpServletRequest request, ErrorAttributeOptions options) {
    WebRequest webRequest = new ServletWebRequest(request);
    return this.errorAttributes.getErrorAttributes(webRequest, options);
  }

  protected boolean getTraceParameter(HttpServletRequest request) {
    return getBooleanParameter(request, "trace");
  }

  protected boolean getMessageParameter(HttpServletRequest request) {
    return getBooleanParameter(request, "message");
  }

  protected boolean getErrorsParameter(HttpServletRequest request) {
    return getBooleanParameter(request, "errors");
  }

  protected boolean getBooleanParameter(HttpServletRequest request, String parameterName) {
    String parameter = request.getParameter(parameterName);
    if (parameter == null) {
      return false;
    }
    return !"false".equalsIgnoreCase(parameter);
  }

}