package org.crue.hercules.sgi.csp.validation;

import java.util.Optional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.csp.model.GrupoRelacionInstitucional;
import org.crue.hercules.sgi.csp.repository.GrupoRelacionInstitucionalRepository;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;

/**
 * Validador de la restricción {@link UniqueGrupoRelacionInstitucional}.
 * Comprueba contra la base de datos que no exista otra
 * {@link GrupoRelacionInstitucional} del mismo grupo con el mismo
 * {@code entidadRef} (cuando se informa) o con la misma {@code institucion}
 * (cuando se informa).
 */
@RequiredArgsConstructor
public class UniqueGrupoRelacionInstitucionalValidator
    implements ConstraintValidator<UniqueGrupoRelacionInstitucional, GrupoRelacionInstitucional> {

  private final GrupoRelacionInstitucionalRepository repository;

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(GrupoRelacionInstitucional value, ConstraintValidatorContext context) {
    if (value == null || value.getGrupoId() == null) {
      return true;
    }

    if (StringUtils.hasText(value.getEntidadRef())) {
      Optional<GrupoRelacionInstitucional> existing = repository
          .findFirstByGrupoIdAndEntidadRef(value.getGrupoId(), value.getEntidadRef());
      if (existing.isPresent() && !existing.get().getId().equals(value.getId())) {
        addMessageParameters(context, value.getEntidadRef());
        return false;
      }
    }

    if (StringUtils.hasText(value.getInstitucion())) {
      Optional<GrupoRelacionInstitucional> existing = repository
          .findFirstByGrupoIdAndInstitucion(value.getGrupoId(), value.getInstitucion());
      if (existing.isPresent() && !existing.get().getId().equals(value.getId())) {
        addMessageParameters(context, value.getInstitucion());
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
