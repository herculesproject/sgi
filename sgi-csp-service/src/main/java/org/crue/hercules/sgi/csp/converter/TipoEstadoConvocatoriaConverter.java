package org.crue.hercules.sgi.csp.converter;

import java.util.stream.Stream;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.crue.hercules.sgi.csp.enums.TipoEstadoConvocatoriaEnum;

@Converter(autoApply = true)
public class TipoEstadoConvocatoriaConverter implements AttributeConverter<TipoEstadoConvocatoriaEnum, String> {

  @Override
  public String convertToDatabaseColumn(TipoEstadoConvocatoriaEnum attribute) {
    if (attribute == null) {
      return null;
    }
    return attribute.getValue();
  }

  @Override
  public TipoEstadoConvocatoriaEnum convertToEntityAttribute(String dbData) {
    if (dbData == null) {
      return null;
    }
    return Stream.of(TipoEstadoConvocatoriaEnum.values()).filter(c -> c.getValue().equals(dbData)).findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }

}
