package org.crue.hercules.sgi.csp.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Restricción de integridad para
 * {@link org.crue.hercules.sgi.csp.model.GrupoRelacionInstitucional} que exige
 * que no se repita el valor dentro del mismo grupo.
 * <p>
 * Cuando se informa {@code entidadRef} no puede existir otra relación del mismo
 * grupo con la misma {@code entidadRef}; análogamente, cuando se informa
 * {@code institucion} no puede existir otra relación del mismo grupo con la
 * misma {@code institucion}. No se controla la coincidencia cruzada entre
 * {@code entidadRef} e {@code institucion}.
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueGrupoRelacionInstitucionalValidator.class)
public @interface UniqueGrupoRelacionInstitucional {
  String message() default "{org.crue.hercules.sgi.csp.validation.UniqueGrupoRelacionInstitucional.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
