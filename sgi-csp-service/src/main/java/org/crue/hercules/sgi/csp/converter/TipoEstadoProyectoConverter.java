
package org.crue.hercules.sgi.csp.converter;

import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.crue.hercules.sgi.csp.enums.TipoEstadoProyectoEnum;

@Converter(autoApply = true)
public class TipoEstadoProyectoConverter implements AttributeConverter<TipoEstadoProyectoEnum, String> {

  @Override
  public String convertToDatabaseColumn(TipoEstadoProyectoEnum tipoEstadoProyectoEnum) {
    if (tipoEstadoProyectoEnum == null) {
      return null;
    }
    return tipoEstadoProyectoEnum.getValue();
  }

  @Override
  public TipoEstadoProyectoEnum convertToEntityAttribute(String estadoProyecto) {
    if (estadoProyecto == null) {
      return null;
    }
    return Stream.of(TipoEstadoProyectoEnum.values()).filter(c -> c.getValue().equals(estadoProyecto)).findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }

}