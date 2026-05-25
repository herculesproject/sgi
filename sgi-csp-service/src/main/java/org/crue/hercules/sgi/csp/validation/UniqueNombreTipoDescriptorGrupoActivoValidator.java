package org.crue.hercules.sgi.csp.validation;

import java.util.Optional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.csp.model.TipoDescriptorGrupo;
import org.crue.hercules.sgi.csp.model.TipoDescriptorGrupoNombre;
import org.crue.hercules.sgi.csp.repository.TipoDescriptorGrupoRepository;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * Validador de la restricción {@link UniqueNombreTipoDescriptorGrupoActivo}.
 * Comprueba contra la base de datos que no exista otro
 * {@link TipoDescriptorGrupo} activo con el mismo nombre en alguno de los
 * idiomas informados.
 */
@RequiredArgsConstructor
public class UniqueNombreTipoDescriptorGrupoActivoValidator
    implements ConstraintValidator<UniqueNombreTipoDescriptorGrupoActivo, TipoDescriptorGrupo> {

  private final TipoDescriptorGrupoRepository repository;

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(TipoDescriptorGrupo value, ConstraintValidatorContext context) {
    if (value == null || value.getNombre() == null) {
      return false;
    }
    for (TipoDescriptorGrupoNombre nombreI18n : value.getNombre()) {
      Optional<TipoDescriptorGrupo> existing = repository
          .findByNombreLangAndNombreValueAndActivoIsTrue(nombreI18n.getLang(), nombreI18n.getValue());
      if (existing.isPresent() && !existing.get().getId().equals(value.getId())) {
        addMessageParameters(context, nombreI18n.getValue());
        return false;
      }
    }
    return true;
  }

  private void addMessageParameters(ConstraintValidatorContext context, String duplicatedValue) {
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter("value", duplicatedValue);
  }

}
