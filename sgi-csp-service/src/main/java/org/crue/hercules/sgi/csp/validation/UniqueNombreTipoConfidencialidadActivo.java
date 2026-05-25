package org.crue.hercules.sgi.csp.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.crue.hercules.sgi.csp.model.TipoConfidencialidad;

/**
 * Restricción de integridad para {@link TipoConfidencialidad} que exige que no
 * exista otro {@link TipoConfidencialidad} activo con el mismo nombre en alguno
 * de los idiomas.
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueNombreTipoConfidencialidadActivoValidator.class)
public @interface UniqueNombreTipoConfidencialidadActivo {
  String message() default "{org.crue.hercules.sgi.csp.validation.UniqueNombreTipoConfidencialidadActivo.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
