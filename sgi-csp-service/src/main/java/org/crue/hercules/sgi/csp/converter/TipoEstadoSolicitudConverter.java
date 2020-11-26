package org.crue.hercules.sgi.csp.converter;

import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.crue.hercules.sgi.csp.enums.TipoEstadoSolicitudEnum;

@Converter(autoApply = true)
public class TipoEstadoSolicitudConverter implements AttributeConverter<TipoEstadoSolicitudEnum, String> {

  @Override
  public String convertToDatabaseColumn(TipoEstadoSolicitudEnum attribute) {
    if (attribute == null) {
      return null;
    }
    return attribute.getValue();
  }

  @Override
  public TipoEstadoSolicitudEnum convertToEntityAttribute(String dbData) {
    if (dbData == null) {
      return null;
    }
    return Stream.of(TipoEstadoSolicitudEnum.values()).filter(c -> c.getValue().equals(dbData)).findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }

}
