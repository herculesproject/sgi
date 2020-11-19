package org.crue.hercules.sgi.csp.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.dto.ConvocatoriaConceptoGastoCodigoEcWithEnableAccion;
import org.crue.hercules.sgi.csp.enums.TipoEstadoConvocatoriaEnum;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoCodigoEc;
import org.springframework.stereotype.Service;

@Service
public class ConvocatoriaConceptoGastoCodigoEcMapper {

  /**
   * Transforma el objeto evaluación al dto
   * ConvocatoriaConceptoGastoCodigoEcWithEnableAccion
   * 
   * @param convocatoriaConceptoGastoCodigoEc el objeto
   *                                          ConvocatoriaConceptoGastoCodigoEc
   * @return el dto ConvocatoriaConceptoGastoCodigoEcWithEnableAccion
   */
  public ConvocatoriaConceptoGastoCodigoEcWithEnableAccion convocatoriaConceptoGastoCodigoEcToConvocatoriaConceptoGastoCodigoEcWithEnableAccion(
      ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc) {
    if (convocatoriaConceptoGastoCodigoEc == null) {
      return null;
    } else {
      ConvocatoriaConceptoGastoCodigoEcWithEnableAccion convocatoriaConceptoGastoWithEnableAccion = new ConvocatoriaConceptoGastoCodigoEcWithEnableAccion();
      convocatoriaConceptoGastoWithEnableAccion
          .setConvocatoriaConceptoGasto(convocatoriaConceptoGastoCodigoEc.getConvocatoriaConceptoGasto());
      convocatoriaConceptoGastoWithEnableAccion.setId(convocatoriaConceptoGastoCodigoEc.getId());
      convocatoriaConceptoGastoWithEnableAccion
          .setCodigoEconomicoRef(convocatoriaConceptoGastoCodigoEc.getCodigoEconomicoRef());
      convocatoriaConceptoGastoWithEnableAccion.setFechaInicio(convocatoriaConceptoGastoCodigoEc.getFechaInicio());
      convocatoriaConceptoGastoWithEnableAccion.setFechaFin(convocatoriaConceptoGastoCodigoEc.getFechaFin());
      convocatoriaConceptoGastoWithEnableAccion.setEnableAccion(isEnable(convocatoriaConceptoGastoCodigoEc));
      convocatoriaConceptoGastoWithEnableAccion.setObservaciones(convocatoriaConceptoGastoCodigoEc.getObservaciones());
      return convocatoriaConceptoGastoWithEnableAccion;
    }
  }

  /**
   * Transforma el listado de objetos evaluación al listado de dtos
   * ConvocatoriaConceptoGastoCodigoEcWithEnableAccion
   * 
   * @param convocatoriaConceptoGastoCodigoEcs el listado de objetos
   *                                           ConvocatoriaConceptoGastoCodigoEc
   * @return el listado de dtos ConvocatoriaConceptoGastoCodigoEcWithEnableAccion
   */
  public List<ConvocatoriaConceptoGastoCodigoEcWithEnableAccion> convocatoriaConceptoGastoCodigoEcsToConvocatoriaConceptoGastoCodigoEcsWithEnableAccion(
      List<ConvocatoriaConceptoGastoCodigoEc> convocatoriaConceptoGastoCodigoEcs) {
    if (convocatoriaConceptoGastoCodigoEcs == null) {
      return new ArrayList<>();
    } else {
      return convocatoriaConceptoGastoCodigoEcs.stream().filter(Objects::nonNull)
          .map(this::convocatoriaConceptoGastoCodigoEcToConvocatoriaConceptoGastoCodigoEcWithEnableAccion)
          .collect(Collectors.toList());
    }
  }

  /**
   * Comprueba si se puede realizar cualquier acción (creación,modificación y
   * eliminación) sobre los gastos
   * 
   * @param convocatoriaConceptoGastoCodigoEc el objeto
   *                                          ConvocatoriaConceptoGastoCodigoEc
   * @return true or false
   */
  public Boolean isEnable(ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc) {

    // No se puede añadir, modificar ni eliminar el gasto si el estado de la
    // convocatoria es distinto a borrador y registrada
    if (!convocatoriaConceptoGastoCodigoEc.getConvocatoriaConceptoGasto().getConvocatoria().getEstadoActual().getValue()
        .equals(TipoEstadoConvocatoriaEnum.BORRADOR.getValue())
        && !convocatoriaConceptoGastoCodigoEc.getConvocatoriaConceptoGasto().getConvocatoria().getEstadoActual()
            .getValue().equals(TipoEstadoConvocatoriaEnum.REGISTRADA.getValue())) {
      return false;
    }

    return true;
  }

}
