package org.crue.hercules.sgi.csp.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * UniqueNombreTipoDescriptorGrupoActivo
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueNombreTipoDescriptorGrupoActivoValidator.class)
public @interface UniqueNombreTipoDescriptorGrupoActivo {
  String message() default "{org.crue.hercules.sgi.csp.validation.UniqueNombreTipoDescriptorGrupoActivo.message}";

  String field() default "name";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
