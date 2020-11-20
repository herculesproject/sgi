package org.crue.hercules.sgi.csp.converter;

import java.util.stream.Stream;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.crue.hercules.sgi.csp.enums.TipoFormularioSolicitudEnum;

@Converter(autoApply = true)
public class TipoFormularioSolicitudConverter implements AttributeConverter<TipoFormularioSolicitudEnum, String> {

  @Override
  public String convertToDatabaseColumn(TipoFormularioSolicitudEnum attribute) {
    if (attribute == null) {
      return null;
    }
    return attribute.getValue();
  }

  @Override
  public TipoFormularioSolicitudEnum convertToEntityAttribute(String dbData) {
    if (dbData == null) {
      return null;
    }
    return Stream.of(TipoFormularioSolicitudEnum.values()).filter(c -> c.getValue().equals(dbData)).findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }

}
