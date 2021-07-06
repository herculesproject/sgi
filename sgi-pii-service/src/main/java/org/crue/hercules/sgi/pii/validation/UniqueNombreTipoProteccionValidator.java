package org.crue.hercules.sgi.pii.validation;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.model.TipoProteccion;
import org.crue.hercules.sgi.pii.model.TipoProteccion_;
import org.crue.hercules.sgi.pii.repository.TipoProteccionRepository;
import org.crue.hercules.sgi.pii.repository.specification.TipoProteccionSpecifications;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class UniqueNombreTipoProteccionValidator
    implements ConstraintValidator<UniqueNombreTipoProteccion, TipoProteccion> {
  private TipoProteccionRepository repository;

  public UniqueNombreTipoProteccionValidator(TipoProteccionRepository repository) {
    this.repository = repository;
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(TipoProteccion value, ConstraintValidatorContext context) {
    if (value == null || value.getNombre() == null) {
      return false;
    }

    Specification<TipoProteccion> specs = TipoProteccionSpecifications.activos();
    specs = specs.and((root, query, cb) -> {
      return (value.getPadre() != null) ? cb.isNotNull(root.get(TipoProteccion_.padre))
          : cb.isNull(root.get(TipoProteccion_.padre));
    });
    specs = specs.and((root, query, cb) -> {
      return cb.equal(root.get(TipoProteccion_.nombre), value.getNombre());
    });

    List<TipoProteccion> tipoProteccion = repository.findAll(specs);
    boolean returnValue = !tipoProteccion.stream().anyMatch(tipo -> tipo.getId() != value.getId());

    if (!returnValue) {
      addEntityMessageParameter(context);
    }
    return returnValue;
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context) {
    // Add "entity" message parameter this the message-revolved entity name so it
    // can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter("entity", ApplicationContextSupport.getMessage(TipoProteccion.class));
  }
}
