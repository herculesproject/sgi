
package org.crue.hercules.sgi.csp.converter;

import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.crue.hercules.sgi.csp.enums.TipoJustificacionEnum;

@Converter(autoApply = true)
public class TipoJustificacionConverter implements AttributeConverter<TipoJustificacionEnum, String> {

  @Override
  public String convertToDatabaseColumn(TipoJustificacionEnum tipoJustificacionEnum) {
    if (tipoJustificacionEnum == null) {
      return null;
    }
    return tipoJustificacionEnum.getValue();
  }

  @Override
  public TipoJustificacionEnum convertToEntityAttribute(String tipoJustificacion) {
    if (tipoJustificacion == null) {
      return null;
    }
    return Stream.of(TipoJustificacionEnum.values()).filter(c -> c.getValue().equals(tipoJustificacion)).findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }

}