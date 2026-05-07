package org.crue.hercules.sgi.csp.validation;

import java.util.Optional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.csp.model.TipoGrupo;
import org.crue.hercules.sgi.csp.model.TipoGrupoNombre;
import org.crue.hercules.sgi.csp.repository.TipoGrupoRepository;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.messageinterpolation.ExpressionLanguageFeatureLevel;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class UniqueNombreTipoGrupoActivoValidator
    implements ConstraintValidator<UniqueNombreTipoGrupoActivo, TipoGrupo> {

  private TipoGrupoRepository repository;
  private String field;

  public UniqueNombreTipoGrupoActivoValidator(TipoGrupoRepository repository) {
    this.repository = repository;
  }

  @Override
  public void initialize(UniqueNombreTipoGrupoActivo constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    field = constraintAnnotation.field();
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(TipoGrupo value, ConstraintValidatorContext context) {
    if (value == null || value.getNombre() == null) {
      return false;
    }
    for (TipoGrupoNombre tipoGrupoNombre : value.getNombre()) {
      Optional<TipoGrupo> tipoGrupo = repository
          .findByNombreLangAndNombreValueAndActivoIsTrue(tipoGrupoNombre.getLang(), tipoGrupoNombre.getValue());
      boolean returnValue = (!tipoGrupo.isPresent() || tipoGrupo.get().getId().equals(value.getId()));
      if (!returnValue) {
        addEntityMessageParameter(context, tipoGrupoNombre);
        return false;
      }
    }
    return true;
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context, TipoGrupoNombre tipoGrupoNombre) {
    // Add "entity" message parameter this the message-revolved entity name so it
    // can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter("entity", ApplicationContextSupport.getMessage(TipoGrupo.class));
    hibernateContext.addMessageParameter("nombre", tipoGrupoNombre.getValue());

    // Disable default message to allow binding the message to a property
    hibernateContext.disableDefaultConstraintViolation();
    // Build a custom message for a property using the default message
    hibernateContext.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
        .enableExpressionLanguage(ExpressionLanguageFeatureLevel.BEAN_PROPERTIES)
        .addPropertyNode(ApplicationContextSupport.getMessage(field)).addConstraintViolation();
  }
}
