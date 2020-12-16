
package org.crue.hercules.sgi.csp.converter;

import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.crue.hercules.sgi.csp.enums.TipoHorasAnualesEnum;

@Converter(autoApply = true)
public class TipoHorasAnualesConverter implements AttributeConverter<TipoHorasAnualesEnum, String> {

  @Override
  public String convertToDatabaseColumn(TipoHorasAnualesEnum tipoHorasAnualesEnum) {
    if (tipoHorasAnualesEnum == null) {
      return null;
    }
    return tipoHorasAnualesEnum.getValue();
  }

  @Override
  public TipoHorasAnualesEnum convertToEntityAttribute(String horasAnuales) {
    if (horasAnuales == null) {
      return null;
    }
    return Stream.of(TipoHorasAnualesEnum.values()).filter(c -> c.getValue().equals(horasAnuales)).findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }

}