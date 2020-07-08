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

@Data
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.CUSTOM, property = "error", visible = true)
@JsonTypeIdResolver(LowerCaseClassNameResolver.class)
public class Error {

  private HttpStatus status;
  private ZonedDateTime timestamp;
  private String message;
  private String debugMessage;
  private List<ValidationError> validationErrors;

  public Error(HttpStatus status) {
    this(status, null);
  }

  public Error(HttpStatus status, Throwable ex) {
    this(status, "Unexpected error", ex);
  }

  public Error(HttpStatus status, String message, Throwable ex) {
    timestamp = ZonedDateTime.now();
    this.status = status;
    this.message = message;
    if (ex != null) {
      this.debugMessage = ExceptionUtils.getStackTrace(ex);
    }
  }

  private void addValidationError(ValidationError validationError) {
    if (validationErrors == null) {
      validationErrors = new ArrayList<>();
    }
    validationErrors.add(validationError);
  }

  private void addValidationError(String object, String field, Object rejectedValue, String message) {
    addValidationError(new ValidationError(object, field, rejectedValue, message));
  }

  private void addValidationError(String object, String message) {
    addValidationError(new ValidationError(object, message));
  }

  private void addValidationError(FieldError fieldError) {
    this.addValidationError(fieldError.getObjectName(), fieldError.getField(), fieldError.getRejectedValue(),
        fieldError.getDefaultMessage());
  }

  public void addValidationErrors(List<FieldError> fieldErrors) {
    fieldErrors.forEach(this::addValidationError);
  }

  private void addValidationError(ObjectError objectError) {
    this.addValidationError(objectError.getObjectName(), objectError.getDefaultMessage());
  }

  public void addValidationError(List<ObjectError> globalErrors) {
    globalErrors.forEach(this::addValidationError);
  }

  /**
   * Utility method for adding error of ConstraintViolation. Usually when
   * a @Validated validation fails.
   *
   * @param cv the ConstraintViolation
   */
  private void addValidationError(ConstraintViolation<?> cv) {
    this.addValidationError(cv.getRootBeanClass().getSimpleName(),
        ((PathImpl) cv.getPropertyPath()).getLeafNode().asString(), cv.getInvalidValue(), cv.getMessage());
  }

  public void addValidationErrors(Set<ConstraintViolation<?>> constraintViolations) {
    constraintViolations.forEach(this::addValidationError);
  }

}
