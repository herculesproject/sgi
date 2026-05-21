package org.crue.hercules.sgi.csp.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.csp.model.GrupoRelacionInstitucional;
import org.springframework.util.StringUtils;

import lombok.NoArgsConstructor;

/**
 * Validador de la restricción
 * {@link GrupoRelacionInstitucionalEntidadRefOrInstitucion}. Comprueba que la
 * institución del {@link GrupoRelacionInstitucional} se ha indicado por una
 * sola vía: o bien una referencia a entidad del SGE ({@code entidadRef}) o
 * bien un nombre libre ({@code institucion}), pero nunca ambas a la vez ni
 * ninguna.
 */
@NoArgsConstructor
public class GrupoRelacionInstitucionalEntidadRefOrInstitucionValidator
    implements ConstraintValidator<GrupoRelacionInstitucionalEntidadRefOrInstitucion, GrupoRelacionInstitucional> {

  /**
   * Comprueba la restricción XOR sobre los campos {@code entidadRef} e
   * {@code institucion}.
   *
   * @param value   {@link GrupoRelacionInstitucional} a validar.
   * @param context contexto de la validación.
   * @return {@code true} si exactamente uno de los dos campos tiene valor y
   *         {@code false} si están ambos informados, ninguno informado, o el
   *         {@code value} es {@code null}.
   */
  @Override
  public boolean isValid(GrupoRelacionInstitucional value, ConstraintValidatorContext context) {
    if (value == null) {
      return false;
    }

    boolean hasEntidadRef = StringUtils.hasText(value.getEntidadRef());
    boolean hasInstitucion = StringUtils.hasText(value.getInstitucion());
    return hasEntidadRef ^ hasInstitucion;
  }

}
