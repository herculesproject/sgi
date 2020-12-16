
package org.crue.hercules.sgi.csp.converter;

import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.crue.hercules.sgi.csp.enums.TipoPlantillaJustificacionEnum;

@Converter(autoApply = true)
public class TipoPlantillaJustificacionConverter implements AttributeConverter<TipoPlantillaJustificacionEnum, String> {

  @Override
  public String convertToDatabaseColumn(TipoPlantillaJustificacionEnum tipoPlantillaJustificacionEnum) {
    if (tipoPlantillaJustificacionEnum == null) {
      return null;
    }
    return tipoPlantillaJustificacionEnum.getValue();
  }

  @Override
  public TipoPlantillaJustificacionEnum convertToEntityAttribute(String plantillaJustificacion) {
    if (plantillaJustificacion == null) {
      return null;
    }
    return Stream.of(TipoPlantillaJustificacionEnum.values()).filter(c -> c.getValue().equals(plantillaJustificacion))
        .findFirst().orElseThrow(IllegalArgumentException::new);
  }

}