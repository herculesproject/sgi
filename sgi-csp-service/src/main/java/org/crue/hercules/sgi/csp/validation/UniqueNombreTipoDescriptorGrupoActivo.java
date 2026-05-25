package org.crue.hercules.sgi.csp.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.crue.hercules.sgi.csp.model.TipoDescriptorGrupo;

/**
 * Restricción de integridad para {@link TipoDescriptorGrupo} que exige que no
 * exista otro {@link TipoDescriptorGrupo} activo con el mismo nombre en alguno
 * de los idiomas.
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueNombreTipoDescriptorGrupoActivoValidator.class)
public @interface UniqueNombreTipoDescriptorGrupoActivo {
  String message() default "{org.crue.hercules.sgi.csp.validation.UniqueNombreTipoDescriptorGrupoActivo.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
