package org.crue.hercules.sgi.framework.web.error;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.crue.hercules.sgi.framework.jackson.databind.jsontype.LowerCaseClassNameResolver;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.CUSTOM, property = "error", visible = true)
@JsonTypeIdResolver(LowerCaseClassNameResolver.class)
@Slf4j
public class Error {

  private HttpStatus status;
  private ZonedDateTime timestamp;
  private String message;
  private String debugMessage;
  private List<ValidationError> validationErrors;

  public Error(HttpStatus status) {
    this(status, null);
    log.debug("Error(HttpStatus status) - start");
    log.debug("Error(HttpStatus status) - end");
  }

  public Error(HttpStatus status, Throwable ex) {
    this(status, "Unexpected error", ex);
    log.debug("Error(HttpStatus status, Throwable ex) - start");
    log.debug("Error(HttpStatus status, Throwable ex) - end");
  }

  public Error(HttpStatus status, String message, Throwable ex) {
    log.debug("Error(HttpStatus status, String message, Throwable ex) - start");
    timestamp = ZonedDateTime.now();
    this.status = status;
    this.message = message;
    if (ex != null) {
      this.debugMessage = ExceptionUtils.getStackTrace(ex);
    }
    log.debug("Error(HttpStatus status, String message, Throwable ex) - end");
  }

  /**
   * Utility method for adding error.
   *
   * @param validationError the {@link ValidationError}
   */
  private void addValidationError(ValidationError validationError) {
    log.debug("addValidationError(ValidationError validationError) - start");
    if (validationErrors == null) {
      validationErrors = new ArrayList<>();
    }

    validationErrors.add(validationError);
    log.debug("addValidationError(ValidationError validationError) - end");
  }

  /**
   * Utility method for adding error.
   *
   * @param object        the object with the error
   * @param field         the field name with the error
   * @param rejectedValue the rejected value
   * @param message       the error message
   */
  private void addValidationError(String object, String field, Object rejectedValue, String message) {
    addValidationError(new ValidationError(object, field, rejectedValue, message));
  }

  /**
   * Utility method for adding error.
   *
   * @param object  the object with the error
   * @param message the error message
   */
  private void addValidationError(String object, String message) {
    log.debug("addValidationError(String object, String message) - start");
    addValidationError(new ValidationError(object, message));
    log.debug("addValidationError(String object, String message) - end");
  }

  /**
   * Utility method for adding error. Usually when a @Validated validation fails.
   *
   * @param fieldError the FieldError
   */
  private void addValidationError(FieldError fieldError) {
    log.debug("addValidationError(FieldError fieldError) - start");
    this.addValidationError(fieldError.getObjectName(), fieldError.getField(), fieldError.getRejectedValue(),
        fieldError.getDefaultMessage());
    log.debug("addValidationError(FieldError fieldError) - end");
  }

  /**
   * Utility method for adding errors. Usually when a @Validated validation fails.
   *
   * @param fieldErrors the FieldErrors
   */
  public void addValidationErrors(List<FieldError> fieldErrors) {
    log.debug("addValidationErrors(List<FieldError> fieldErrors) - start");
    fieldErrors.forEach(this::addValidationError);
    log.debug("addValidationErrors(List<FieldError> fieldErrors) - end");
  }

  /**
   * Utility method for adding error. Usually when a @Validated validation fails.
   *
   * @param objectError the ObjectError
   */
  private void addValidationError(ObjectError objectError) {
    log.debug("addValidationError(ObjectError objectError) - start");
    this.addValidationError(objectError.getObjectName(), objectError.getDefaultMessage());
    log.debug("addValidationError(ObjectError objectError) - end");
  }

  /**
   * Utility method for adding errors. Usually when a @Validated validation fails.
   *
   * @param globalErrors the ObjectErrors
   */
  public void addValidationError(List<ObjectError> globalErrors) {
    log.debug("addValidationError(List<ObjectError> globalErrors) - start");
    globalErrors.forEach(this::addValidationError);
    log.debug("addValidationError(List<ObjectError> globalErrors) - end");
  }

  /**
   * Utility method for adding error of ConstraintViolation. Usually when
   * a @Validated validation fails.
   *
   * @param cv the ConstraintViolation
   */
  private void addValidationError(ConstraintViolation<?> cv) {
    log.debug("addValidationError(ConstraintViolation<?> cv) - start");
    this.addValidationError(cv.getRootBeanClass().getSimpleName(),
        ((PathImpl) cv.getPropertyPath()).getLeafNode().asString(), cv.getInvalidValue(), cv.getMessage());
    log.debug("addValidationError(ConstraintViolation<?> cv) - end");
  }

  /**
   * Utility method for adding errors of ConstraintViolations. Usually when
   * a @Validated validation fails.
   *
   * @param constraintViolations the ConstraintViolations
   */
  public void addValidationErrors(Set<ConstraintViolation<?>> constraintViolations) {
    log.debug("addValidationErrors(Set<ConstraintViolation<?>> constraintViolations) - start");
    constraintViolations.forEach(this::addValidationError);
    log.debug("addValidationErrors(Set<ConstraintViolation<?>> constraintViolations) - end");
  }

}
