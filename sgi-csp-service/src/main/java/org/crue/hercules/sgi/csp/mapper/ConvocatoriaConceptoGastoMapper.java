package org.crue.hercules.sgi.csp.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.dto.ConvocatoriaConceptoGastoWithEnableAccion;
import org.crue.hercules.sgi.csp.enums.TipoEstadoConvocatoriaEnum;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
import org.springframework.stereotype.Service;

@Service
public class ConvocatoriaConceptoGastoMapper {

  /**
   * Transforma el objeto evaluación al dto
   * ConvocatoriaConceptoGastoWithEnableAccion
   * 
   * @param convocatoriaConceptoGasto el objeto ConvocatoriaConceptoGasto
   * @return el dto ConvocatoriaConceptoGastoWithEnableAccion
   */
  public ConvocatoriaConceptoGastoWithEnableAccion convocatoriaConceptoGastoToConvocatoriaConceptoGastoWithEnableAccion(
      ConvocatoriaConceptoGasto convocatoriaConceptoGasto) {
    if (convocatoriaConceptoGasto == null) {
      return null;
    } else {
      ConvocatoriaConceptoGastoWithEnableAccion convocatoriaConceptoGastoWithEnableAccion = new ConvocatoriaConceptoGastoWithEnableAccion();
      convocatoriaConceptoGastoWithEnableAccion.setConceptoGasto(convocatoriaConceptoGasto.getConceptoGasto());
      convocatoriaConceptoGastoWithEnableAccion.setConvocatoria(convocatoriaConceptoGasto.getConvocatoria());
      convocatoriaConceptoGastoWithEnableAccion.setId(convocatoriaConceptoGasto.getId());
      convocatoriaConceptoGastoWithEnableAccion.setImporteMaximo(convocatoriaConceptoGasto.getImporteMaximo());
      convocatoriaConceptoGastoWithEnableAccion.setNumMeses(convocatoriaConceptoGasto.getNumMeses());
      convocatoriaConceptoGastoWithEnableAccion.setObservaciones(convocatoriaConceptoGasto.getObservaciones());
      convocatoriaConceptoGastoWithEnableAccion.setPermitido(convocatoriaConceptoGasto.getPermitido());
      convocatoriaConceptoGastoWithEnableAccion
          .setPorcentajeCosteIndirecto(convocatoriaConceptoGasto.getPorcentajeCosteIndirecto());
      convocatoriaConceptoGastoWithEnableAccion.setEnableAccion(isEnable(convocatoriaConceptoGasto));
      return convocatoriaConceptoGastoWithEnableAccion;
    }
  }

  /**
   * Transforma el listado de objetos evaluación al listado de dtos
   * ConvocatoriaConceptoGastoWithEnableAccion
   * 
   * @param convocatoriaConceptoGastos el listado de objetos
   *                                   ConvocatoriaConceptoGasto
   * @return el listado de dtos ConvocatoriaConceptoGastoWithEnableAccion
   */
  public List<ConvocatoriaConceptoGastoWithEnableAccion> convocatoriaConceptoGastosToConvocatoriaConceptoGastoesWithEnableAccion(
      List<ConvocatoriaConceptoGasto> convocatoriaConceptoGastos) {
    if (convocatoriaConceptoGastos == null) {
      return new ArrayList<>();
    } else {
      return convocatoriaConceptoGastos.stream().filter(Objects::nonNull)
          .map(this::convocatoriaConceptoGastoToConvocatoriaConceptoGastoWithEnableAccion).collect(Collectors.toList());
    }
  }

  /**
   * Comprueba si se puede realizar cualquier acción (creación,modificación y
   * eliminación) sobre los gastos
   * 
   * @param convocatoriaConceptoGasto el objeto ConvocatoriaConceptoGasto
   * @return true or false
   */
  public Boolean isEnable(ConvocatoriaConceptoGasto convocatoriaConceptoGasto) {

    // No se puede añadir, modificar ni eliminar el gasto si el estado de la
    // convocatoria es distinto a borrador y registrada
    if (!convocatoriaConceptoGasto.getConvocatoria().getEstadoActual().getValue()
        .equals(TipoEstadoConvocatoriaEnum.BORRADOR.getValue())
        && !convocatoriaConceptoGasto.getConvocatoria().getEstadoActual().getValue()
            .equals(TipoEstadoConvocatoriaEnum.REGISTRADA.getValue())) {
      return false;
    }

    return true;
  }

}
