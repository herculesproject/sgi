
package org.crue.hercules.sgi.csp.converter;

import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.crue.hercules.sgi.csp.enums.TipoPropiedadResultadosEnum;

@Converter(autoApply = true)
public class TipoPropiedadResultadosConverter implements AttributeConverter<TipoPropiedadResultadosEnum, String> {

  @Override
  public String convertToDatabaseColumn(TipoPropiedadResultadosEnum TipoPropiedadResultadosEnum) {
    if (TipoPropiedadResultadosEnum == null) {
      return null;
    }
    return TipoPropiedadResultadosEnum.getValue();
  }

  @Override
  public TipoPropiedadResultadosEnum convertToEntityAttribute(String plantillaJustificacion) {
    if (plantillaJustificacion == null) {
      return null;
    }
    return Stream.of(TipoPropiedadResultadosEnum.values()).filter(c -> c.getValue().equals(plantillaJustificacion))
        .findFirst().orElseThrow(IllegalArgumentException::new);
  }

}