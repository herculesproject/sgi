package org.crue.hercules.sgi.csp.converter;

import java.util.stream.Stream;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.crue.hercules.sgi.csp.enums.ClasificacionCVNEnum;

@Converter(autoApply = true)
public class ClasificacionCVNConverter implements AttributeConverter<ClasificacionCVNEnum, String> {

  @Override
  public String convertToDatabaseColumn(ClasificacionCVNEnum attribute) {
    if (attribute == null) {
      return null;
    }
    return attribute.getValue();
  }

  @Override
  public ClasificacionCVNEnum convertToEntityAttribute(String dbData) {
    if (dbData == null) {
      return null;
    }
    return Stream.of(ClasificacionCVNEnum.values()).filter(c -> c.getValue().equals(dbData)).findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }

}
