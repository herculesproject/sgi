package org.crue.hercules.sgi.csp.converter;

import java.util.stream.Stream;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.crue.hercules.sgi.csp.enums.TipoDestinatarioEnum;

@Converter(autoApply = true)
public class TipoDestinatarioConverter implements AttributeConverter<TipoDestinatarioEnum, String> {

  @Override
  public String convertToDatabaseColumn(TipoDestinatarioEnum attribute) {
    if (attribute == null) {
      return null;
    }
    return attribute.getValue();
  }

  @Override
  public TipoDestinatarioEnum convertToEntityAttribute(String dbData) {
    if (dbData == null) {
      return null;
    }
    return Stream.of(TipoDestinatarioEnum.values()).filter(c -> c.getValue().equals(dbData)).findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }

}
