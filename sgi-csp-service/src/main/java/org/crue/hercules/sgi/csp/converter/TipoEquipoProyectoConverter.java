package org.crue.hercules.sgi.csp.converter;

import java.util.stream.Stream;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.crue.hercules.sgi.csp.enums.TipoEquipoProyectoEnum;

@Converter(autoApply = true)
public class TipoEquipoProyectoConverter implements AttributeConverter<TipoEquipoProyectoEnum, String> {

  @Override
  public String convertToDatabaseColumn(TipoEquipoProyectoEnum attribute) {
    if (attribute == null) {
      return null;
    }
    return attribute.getValue();
  }

  @Override
  public TipoEquipoProyectoEnum convertToEntityAttribute(String dbData) {
    if (dbData == null) {
      return null;
    }
    return Stream.of(TipoEquipoProyectoEnum.values()).filter(c -> c.getValue().equals(dbData)).findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }

}
