package org.crue.hercules.sgi.framework.web.servlet.mvc.method.annotation;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;

import org.crue.hercules.sgi.framework.exception.IllegalSpecificationArgumentException;
import org.crue.hercules.sgi.framework.exception.NotFoundException;
import org.crue.hercules.sgi.framework.exception.UnsupportedSpecificationOperationException;
import org.crue.hercules.sgi.framework.web.error.Error;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import lombok.extern.slf4j.Slf4j;

/**
 * A convenient base class for {@link ControllerAdvice @ControllerAdvice}
 * classes that wish to provide centralized exception handling across all
 * {@code @RequestMapping} methods through {@code @ExceptionHandler} methods.
 *
 * <p>
 * This base class provides an {@code @ExceptionHandler} method for handling
 * internal Spring MVC exceptions. This method returns a {@code ResponseEntity}
 * for writing to the response with a {@link HttpMessageConverter message
 * converter}, in contrast to
 * {@link org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver
 * DefaultHandlerExceptionResolver} which returns a
 * {@link org.springframework.web.servlet.ModelAndView ModelAndView}.
 *
 * <p>
 * If there is no need to write error content to the response body, or when
 * using view resolution (e.g., via {@code ContentNegotiatingViewResolver}),
 * then {@code DefaultHandlerExceptionResolver} is good enough.
 *
 * <p>
 * Note that in order for an {@code @ControllerAdvice} subclass to be detected,
 * {@link ExceptionHandlerExceptionResolver} must be configured.
 *
 * @see #handleException(Exception, WebRequest)
 * @see org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver
 */
@Slf4j
public abstract class SgiResponseEntityExceptionHandler
    extends org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler {

  /**
   * Provides handling for exceptions.
   * 
   * @param ex      the target exception
   * @param request the current request
   * @return ResponseEntity
   * @throws Exception if a problem occurs
   */
  @ExceptionHandler({ ConstraintViolationException.class, EntityNotFoundException.class,
      DataIntegrityViolationException.class, MethodArgumentTypeMismatchException.class, IllegalArgumentException.class,
      NotFoundException.class, IllegalSpecificationArgumentException.class,
      UnsupportedSpecificationOperationException.class })
  @Nullable
  public final ResponseEntity<Object> handleCustomExceptions(Exception ex, WebRequest request) throws Exception {
    log.debug("handleCustomExceptions(Exception ex, WebRequest request) - start");
    HttpHeaders headers = new HttpHeaders();

    if (ex instanceof ConstraintViolationException) {
      HttpStatus status = HttpStatus.BAD_REQUEST;
      ResponseEntity<Object> returnValue = handleConstraintViolation((ConstraintViolationException) ex, headers, status,
          request);
      log.debug("handleCustomExceptions(Exception ex, WebRequest request) - end");
      return returnValue;
    } else if (ex instanceof EntityNotFoundException) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      ResponseEntity<Object> returnValue = handleEntityNotFound((EntityNotFoundException) ex, headers, status, request);
      log.debug("handleCustomExceptions(Exception ex, WebRequest request) - end");
      return returnValue;
    } else if (ex instanceof DataIntegrityViolationException) {
      HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
      ResponseEntity<Object> returnValue = handleDataIntegrityViolation((DataIntegrityViolationException) ex, headers,
          status, request);
      log.debug("handleCustomExceptions(Exception ex, WebRequest request) - end");
      return returnValue;
    } else if (ex instanceof MethodArgumentTypeMismatchException) {
      HttpStatus status = HttpStatus.BAD_REQUEST;
      ResponseEntity<Object> returnValue = handleMethodArgumentTypeMismatch((MethodArgumentTypeMismatchException) ex,
          headers, status, request);
      log.debug("handleCustomExceptions(Exception ex, WebRequest request) - end");
      return returnValue;
    } else if (ex instanceof IllegalArgumentException) {
      HttpStatus status = HttpStatus.BAD_REQUEST;
      ResponseEntity<Object> returnValue = handleIllegalArgument((IllegalArgumentException) ex, headers, status,
          request);
      log.debug("handleCustomExceptions(Exception ex, WebRequest request) - end");
      return returnValue;
    } else if (ex instanceof NotFoundException) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      ResponseEntity<Object> returnValue = handleNotFound((NotFoundException) ex, headers, status, request);
      log.debug("handleCustomExceptions(Exception ex, WebRequest request) - end");
      return returnValue;
    } else if (ex instanceof IllegalSpecificationArgumentException) {
      HttpStatus status = HttpStatus.BAD_REQUEST;
      ResponseEntity<Object> returnValue = handleIllegalSpecificationArgument(
          (IllegalSpecificationArgumentException) ex, headers, status, request);
      log.debug("handleCustomExceptions(Exception ex, WebRequest request) - end");
      return returnValue;
    } else if (ex instanceof UnsupportedSpecificationOperationException) {
      HttpStatus status = HttpStatus.BAD_REQUEST;
      ResponseEntity<Object> returnValue = handleUnsupportedSpecificationOperation(
          (UnsupportedSpecificationOperationException) ex, headers, status, request);
      log.debug("handleCustomExceptions(Exception ex, WebRequest request) - end");
      return returnValue;
    } else {
      // Unknown exception, typically a wrapper with a common MVC exception as cause
      // (since @ExceptionHandler type declarations also match first-level causes):
      // We only deal with top-level MVC exceptions here, so let's rethrow the given
      // exception for further processing through the HandlerExceptionResolver chain.
      log.error("Unknown exception", ex);
      throw ex;
    }
  }

  /**
   * @param ex      the Exception
   * @param body    the body Object
   * @param headers the HttpHeaders
   * @param status  the HttpStatus
   * @param request the WebRequest
   * @return ResponseEntity
   */
  @Override
  protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    log.debug(
        "handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    if (body == null) {
      body = new Error(status, ex.getMessage(), ex);
    }
    ResponseEntity<Object> returnValue = super.handleExceptionInternal(ex, body, headers, status, request);
    log.debug(
        "handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return returnValue;
  }

  /**
   * @param ex      the Exception
   * @param headers the HttpHeaders
   * @param status  the HttpStatus
   * @param request the WebRequest
   * @return ResponseEntity
   */
  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    log.debug(
        "handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    Error error = new Error(HttpStatus.BAD_REQUEST, ex.getParameterName() + " parameter is missing", ex);
    ResponseEntity<Object> returnValue = handleExceptionInternal(ex, error, headers, error.getStatus(), request);
    log.debug(
        "handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return returnValue;
  }

  /**
   * @param ex      the Exception
   * @param headers the HttpHeaders
   * @param status  the HttpStatus
   * @param request the WebRequest
   * @return ResponseEntity
   */
  @Override
  protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    log.debug(
        "handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    StringBuilder builder = new StringBuilder();
    builder.append(ex.getContentType());
    builder.append(" media type is not supported. Supported media types are ");
    ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));
    Error error = new Error(HttpStatus.UNSUPPORTED_MEDIA_TYPE, builder.substring(0, builder.length() - 2), ex);
    ResponseEntity<Object> returnValue = handleExceptionInternal(ex, error, headers, error.getStatus(), request);
    log.debug(
        "handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return returnValue;
  }

  /**
   * @param ex      the Exception
   * @param headers the HttpHeaders
   * @param status  the HttpStatus
   * @param request the WebRequest
   * @return ResponseEntity
   */
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    log.debug(
        "handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    Error error = new Error(HttpStatus.BAD_REQUEST, "Validation error", ex);
    error.addValidationErrors(ex.getBindingResult().getFieldErrors());
    error.addValidationError(ex.getBindingResult().getGlobalErrors());
    ResponseEntity<Object> returnValue = handleExceptionInternal(ex, error, headers, error.getStatus(), request);
    log.debug(
        "handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return returnValue;
  }

  /**
   * @param ex      the Exception
   * @param headers the HttpHeaders
   * @param status  the HttpStatus
   * @param request the WebRequest
   * @return ResponseEntity
   */
  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    log.debug(
        "handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    ServletWebRequest servletWebRequest = (ServletWebRequest) request;
    log.info("{} to {}", servletWebRequest.getHttpMethod(), servletWebRequest.getRequest().getServletPath());
    Error error = new Error(HttpStatus.BAD_REQUEST, "Malformed JSON request", ex);
    ResponseEntity<Object> returnValue = handleExceptionInternal(ex, error, headers, error.getStatus(), request);
    log.debug(
        "handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return returnValue;
  }

  /**
   * @param ex      the Exception
   * @param headers the HttpHeaders
   * @param status  the HttpStatus
   * @param request the WebRequest
   * @return ResponseEntity
   */
  @Override
  protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    log.debug(
        "handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    Error error = new Error(HttpStatus.INTERNAL_SERVER_ERROR, "Error writing JSON output", ex);
    ResponseEntity<Object> returnValue = handleExceptionInternal(ex, error, headers, error.getStatus(), request);
    log.debug(
        "handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return returnValue;
  }

  /**
   * @param ex      the Exception
   * @param headers the HttpHeaders
   * @param status  the HttpStatus
   * @param request the WebRequest
   * @return ResponseEntity
   */
  @Override
  protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    log.debug(
        "handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    Error error = new Error(HttpStatus.BAD_REQUEST,
        String.format("Could not find the %s method for URL %s", ex.getHttpMethod(), ex.getRequestURL()), ex);
    ResponseEntity<Object> returnValue = handleExceptionInternal(ex, error, headers, error.getStatus(), request);
    log.debug(
        "handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return returnValue;
  }

  /**
   * @param ex      the Exception
   * @param headers the HttpHeaders
   * @param status  the HttpStatus
   * @param request the WebRequest
   * @return ResponseEntity
   */
  protected ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    log.debug(
        "handleConstraintViolation(ConstraintViolationException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    Error error = new Error(status, "Validation error", ex);
    error.addValidationErrors(ex.getConstraintViolations());
    ResponseEntity<Object> returnValue = handleExceptionInternal(ex, error, headers, status, request);
    log.debug(
        "handleConstraintViolation(ConstraintViolationException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return returnValue;
  }

  /**
   * @param ex      the Exception
   * @param headers the HttpHeaders
   * @param status  the HttpStatus
   * @param request the WebRequest
   * @return ResponseEntity
   */
  protected ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    log.debug(
        "handleEntityNotFound(EntityNotFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    ResponseEntity<Object> returnValue = handleExceptionInternal(ex, null, headers, status, request);
    log.debug(
        "handleEntityNotFound(EntityNotFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return returnValue;
  }

  /**
   * @param ex      the Exception
   * @param headers the HttpHeaders
   * @param status  the HttpStatus
   * @param request the WebRequest
   * @return ResponseEntity
   */
  protected ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    log.debug(
        "handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    if (ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
      Error error = new Error(HttpStatus.CONFLICT, "Database error", ex.getCause());
      ResponseEntity<Object> returnValue = handleExceptionInternal(ex, error, headers, status, request);
      log.debug(
          "handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
      return returnValue;
    }
    Error error = new Error(status, "Database error", ex);
    ResponseEntity<Object> returnValue = handleExceptionInternal(ex, error, headers, status, request);
    log.debug(
        "handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return returnValue;
  }

  /**
   * @param ex      the Exception
   * @param headers the HttpHeaders
   * @param status  the HttpStatus
   * @param request the WebRequest
   * @return ResponseEntity
   */
  protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    log.debug(
        "handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    Error error = new Error(status,
        String.format("The parameter '%s' of value '%s' could not be converted to type '%s'", ex.getName(),
            ex.getValue(), ex.getRequiredType().getSimpleName()),
        ex);
    ResponseEntity<Object> returnValue = handleExceptionInternal(ex, error, headers, status, request);
    log.debug(
        "handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return returnValue;
  }

  /**
   * @param ex      the Exception
   * @param headers the HttpHeaders
   * @param status  the HttpStatus
   * @param request the WebRequest
   * @return ResponseEntity
   */
  protected final ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    log.debug(
        "handleIllegalArgument(IllegalArgumentException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    Error error = new Error(HttpStatus.BAD_REQUEST, "Malformed request", ex);
    ResponseEntity<Object> returnValue = handleExceptionInternal(ex, error, headers, status, request);
    log.debug(
        "handleIllegalArgument(IllegalArgumentException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return returnValue;
  }

  /**
   * @param ex      the Exception
   * @param headers the HttpHeaders
   * @param status  the HttpStatus
   * @param request the WebRequest
   * @return ResponseEntity
   */
  protected ResponseEntity<Object> handleNotFound(NotFoundException ex, HttpHeaders headers, HttpStatus status,
      WebRequest request) {
    log.debug(
        "handleNotFound(NotFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    Error error = new Error(status, "Data not found", ex);
    ResponseEntity<Object> returnValue = handleExceptionInternal(ex, error, headers, status, request);
    log.debug("handleNotFound(NotFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return returnValue;
  }

  /**
   * @param ex      the Exception
   * @param headers the HttpHeaders
   * @param status  the HttpStatus
   * @param request the WebRequest
   * @return ResponseEntity
   */
  protected ResponseEntity<Object> handleIllegalSpecificationArgument(IllegalSpecificationArgumentException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    log.debug(
        "handleIllegalSpecificationArgument(IllegalSpecificationArgumentException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    Error error = new Error(status, "Malformed query request", ex);
    ResponseEntity<Object> returnValue = handleExceptionInternal(ex, error, headers, status, request);
    log.debug(
        "handleIllegalSpecificationArgument(IllegalSpecificationArgumentException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return returnValue;
  }

  /**
   * @param ex      the Exception
   * @param headers the HttpHeaders
   * @param status  the HttpStatus
   * @param request the WebRequest
   * @return ResponseEntity
   */
  protected ResponseEntity<Object> handleUnsupportedSpecificationOperation(
      UnsupportedSpecificationOperationException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    log.debug(
        "handleUnsupportedSpecificationOperation(UnsupportedSpecificationOperationException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - start");
    Error error = new Error(status, "Unsupported query request", ex);
    ResponseEntity<Object> returnValue = handleExceptionInternal(ex, error, headers, status, request);
    log.debug(
        "handleUnsupportedSpecificationOperation(UnsupportedSpecificationOperationException ex, HttpHeaders headers, HttpStatus status, WebRequest request) - end");
    return returnValue;
  }

}