package org.crue.hercules.sgi.csp.validation;

import java.util.Optional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.csp.model.TipoDescriptorGrupo;
import org.crue.hercules.sgi.csp.model.TipoDescriptorGrupoNombre;
import org.crue.hercules.sgi.csp.repository.TipoDescriptorGrupoRepository;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.messageinterpolation.ExpressionLanguageFeatureLevel;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * UniqueNombreTipoDescriptorGrupoActivoValidator
 */
public class UniqueNombreTipoDescriptorGrupoActivoValidator
    implements ConstraintValidator<UniqueNombreTipoDescriptorGrupoActivo, TipoDescriptorGrupo> {

  private TipoDescriptorGrupoRepository repository;
  private String field;

  public UniqueNombreTipoDescriptorGrupoActivoValidator(TipoDescriptorGrupoRepository repository) {
    this.repository = repository;
  }

  @Override
  public void initialize(UniqueNombreTipoDescriptorGrupoActivo constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    field = constraintAnnotation.field();
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(TipoDescriptorGrupo value, ConstraintValidatorContext context) {
    if (value == null || value.getNombre() == null) {
      return false;
    }
    for (TipoDescriptorGrupoNombre nombreI18n : value.getNombre()) {
      Optional<TipoDescriptorGrupo> tipoDescriptorGrupo = repository
          .findByNombreLangAndNombreValueAndActivoIsTrue(nombreI18n.getLang(), nombreI18n.getValue());
      boolean returnValue = (!tipoDescriptorGrupo.isPresent()
          || tipoDescriptorGrupo.get().getId().equals(value.getId()));
      if (!returnValue) {
        addEntityMessageParameter(context, nombreI18n);
        return false;
      }
    }
    return true;
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context, TipoDescriptorGrupoNombre nombreI18n) {
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter("entity", ApplicationContextSupport.getMessage(TipoDescriptorGrupo.class));
    hibernateContext.addMessageParameter("nombre", nombreI18n.getValue());

    hibernateContext.disableDefaultConstraintViolation();
    hibernateContext.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
        .enableExpressionLanguage(ExpressionLanguageFeatureLevel.BEAN_PROPERTIES)
        .addPropertyNode(ApplicationContextSupport.getMessage(field)).addConstraintViolation();
  }

}
