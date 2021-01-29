package org.crue.hercules.sgi.csp.converter;

import java.util.stream.Stream;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.crue.hercules.sgi.csp.enums.TipoProrrogaEnum;

@Converter(autoApply = true)
public class TipoProrrogaConverter implements AttributeConverter<TipoProrrogaEnum, String> {

  @Override
  public String convertToDatabaseColumn(TipoProrrogaEnum attribute) {
    if (attribute == null) {
      return null;
    }
    return attribute.getValue();
  }

  @Override
  public TipoProrrogaEnum convertToEntityAttribute(String dbData) {
    if (dbData == null) {
      return null;
    }
    return Stream.of(TipoProrrogaEnum.values()).filter(c -> c.getValue().equals(dbData)).findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }

}
