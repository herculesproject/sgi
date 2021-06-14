package org.crue.hercules.sgi.csp.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoResponsableEconomico;

public class OrderMesesSolicitudProyectoResponsableEconomicoValidator implements
    ConstraintValidator<OrderMesesSolicitudProyectoResponsableEconomico, SolicitudProyectoResponsableEconomico> {

  @Override
  public boolean isValid(SolicitudProyectoResponsableEconomico value, ConstraintValidatorContext context) {
    if (value == null) {
      return false;
    }

    return (value.getMesInicio() != null && value.getMesFin() != null) && value.getMesInicio() >= value.getMesFin();
  }

}
