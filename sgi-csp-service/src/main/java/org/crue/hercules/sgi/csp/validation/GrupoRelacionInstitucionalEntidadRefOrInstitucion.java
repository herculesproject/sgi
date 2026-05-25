package org.crue.hercules.sgi.csp.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.crue.hercules.sgi.csp.model.GrupoRelacionInstitucional;

/**
 * Restricción de integridad para {@link GrupoRelacionInstitucional} que exige
 * indicar la institución por una sola de las dos vías disponibles.
 * <p>
 * Es válida cuando exactamente uno de los campos {@code entidadRef} o
 * {@code institucion} tiene valor. La validación falla si ambos están
 * informados o
 * si ninguno lo está.
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = GrupoRelacionInstitucionalEntidadRefOrInstitucionValidator.class)
public @interface GrupoRelacionInstitucionalEntidadRefOrInstitucion {
  String message() default "{org.crue.hercules.sgi.csp.validation.GrupoRelacionInstitucionalEntidadRefOrInstitucion.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
