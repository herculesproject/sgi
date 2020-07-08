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
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class SgiResponseEntityExceptionHandler
    extends org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler {

  @ExceptionHandler({ ConstraintViolationException.class, EntityNotFoundException.class,
      DataIntegrityViolationException.class, MethodArgumentTypeMismatchException.class, IllegalArgumentException.class,
      NotFoundException.class, IllegalSpecificationArgumentException.class,
      UnsupportedSpecificationOperationException.class })
  @Nullable
  public final ResponseEntity<Object> handleCustomExceptions(Exception ex, WebRequest request) throws Exception {
    HttpHeaders headers = new HttpHeaders();

    if (ex instanceof ConstraintViolationException) {
      HttpStatus status = HttpStatus.BAD_REQUEST;
      return handleConstraintViolation((ConstraintViolationException) ex, headers, status, request);
    } else if (ex instanceof EntityNotFoundException) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      return handleEntityNotFound((EntityNotFoundException) ex, headers, status, request);
    } else if (ex instanceof DataIntegrityViolationException) {
      HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
      return handleDataIntegrityViolation((DataIntegrityViolationException) ex, headers, status, request);
    } else if (ex instanceof MethodArgumentTypeMismatchException) {
      HttpStatus status = HttpStatus.BAD_REQUEST;
      return handleMethodArgumentTypeMismatch((MethodArgumentTypeMismatchException) ex, headers, status, request);
    } else if (ex instanceof IllegalArgumentException) {
      HttpStatus status = HttpStatus.BAD_REQUEST;
      return handleIllegalArgument((IllegalArgumentException) ex, headers, status, request);
    } else if (ex instanceof NotFoundException) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      return handleNotFound((NotFoundException) ex, headers, status, request);
    } else if (ex instanceof IllegalSpecificationArgumentException) {
      HttpStatus status = HttpStatus.BAD_REQUEST;
      return handleIllegalSpecificationArgument((IllegalSpecificationArgumentException) ex, headers, status, request);
    } else if (ex instanceof UnsupportedSpecificationOperationException) {
      HttpStatus status = HttpStatus.BAD_REQUEST;
      return handleUnsupportedSpecificationOperation((UnsupportedSpecificationOperationException) ex, headers, status,
          request);
    } else {
      // Unknown exception, typically a wrapper with a common MVC exception as cause
      // (since @ExceptionHandler type declarations also match first-level causes):
      // We only deal with top-level MVC exceptions here, so let's rethrow the given
      // exception for further processing through the HandlerExceptionResolver chain.
      throw ex;
    }
  }

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    if (body == null) {
      body = new Error(status, ex.getMessage(), ex);
    }
    return super.handleExceptionInternal(ex, body, headers, status, request);
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    Error error = new Error(HttpStatus.BAD_REQUEST, ex.getParameterName() + " parameter is missing", ex);
    return handleExceptionInternal(ex, error, headers, error.getStatus(), request);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    StringBuilder builder = new StringBuilder();
    builder.append(ex.getContentType());
    builder.append(" media type is not supported. Supported media types are ");
    ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));
    Error error = new Error(HttpStatus.UNSUPPORTED_MEDIA_TYPE, builder.substring(0, builder.length() - 2), ex);
    return handleExceptionInternal(ex, error, headers, error.getStatus(), request);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    Error error = new Error(HttpStatus.BAD_REQUEST, "Validation error", ex);
    error.addValidationErrors(ex.getBindingResult().getFieldErrors());
    error.addValidationError(ex.getBindingResult().getGlobalErrors());
    return handleExceptionInternal(ex, error, headers, error.getStatus(), request);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    ServletWebRequest servletWebRequest = (ServletWebRequest) request;
    log.info("{} to {}", servletWebRequest.getHttpMethod(), servletWebRequest.getRequest().getServletPath());
    Error error = new Error(HttpStatus.BAD_REQUEST, "Malformed JSON request", ex);
    return handleExceptionInternal(ex, error, headers, error.getStatus(), request);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    Error error = new Error(HttpStatus.INTERNAL_SERVER_ERROR, "Error writing JSON output", ex);
    return handleExceptionInternal(ex, error, headers, error.getStatus(), request);
  }

  @Override
  protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    Error error = new Error(HttpStatus.BAD_REQUEST,
        String.format("Could not find the %s method for URL %s", ex.getHttpMethod(), ex.getRequestURL()), ex);
    return handleExceptionInternal(ex, error, headers, error.getStatus(), request);
  }

  protected ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    Error error = new Error(status, "Validation error", ex);
    error.addValidationErrors(ex.getConstraintViolations());
    return handleExceptionInternal(ex, error, headers, status, request);
  }

  protected ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    return handleExceptionInternal(ex, null, headers, status, request);
  }

  protected ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    if (ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
      Error error = new Error(HttpStatus.CONFLICT, "Database error", ex.getCause());
      return handleExceptionInternal(ex, error, headers, status, request);
    }
    Error error = new Error(status, "Database error", ex);
    return handleExceptionInternal(ex, error, headers, status, request);
  }

  protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    Error error = new Error(status,
        String.format("The parameter '%s' of value '%s' could not be converted to type '%s'", ex.getName(),
            ex.getValue(), ex.getRequiredType().getSimpleName()),
        ex);
    return handleExceptionInternal(ex, error, headers, status, request);
  }

  public final ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) throws Exception {
    Error error = new Error(HttpStatus.BAD_REQUEST, "Malformed request", ex);
    return handleExceptionInternal(ex, error, headers, status, request);
  }

  protected ResponseEntity<Object> handleNotFound(NotFoundException ex, HttpHeaders headers, HttpStatus status,
      WebRequest request) {
    Error error = new Error(status, "Data not found", ex);
    return handleExceptionInternal(ex, error, headers, status, request);
  }

  protected ResponseEntity<Object> handleIllegalSpecificationArgument(IllegalSpecificationArgumentException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    Error error = new Error(status, "Malformed query request", ex);
    return handleExceptionInternal(ex, error, headers, status, request);
  }

  protected ResponseEntity<Object> handleUnsupportedSpecificationOperation(
      UnsupportedSpecificationOperationException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    Error error = new Error(status, "Unsupported query request", ex);
    return handleExceptionInternal(ex, error, headers, status, request);
  }

}