
package org.crue.hercules.sgi.csp.converter;

import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.crue.hercules.sgi.csp.enums.TipoHojaFirmaEnum;

@Converter(autoApply = true)
public class TipoHojaFirmaConverter implements AttributeConverter<TipoHojaFirmaEnum, String> {

  @Override
  public String convertToDatabaseColumn(TipoHojaFirmaEnum tipoPlantillaJustificacionEnum) {
    if (tipoPlantillaJustificacionEnum == null) {
      return null;
    }
    return tipoPlantillaJustificacionEnum.getValue();
  }

  @Override
  public TipoHojaFirmaEnum convertToEntityAttribute(String plantillaJustificacion) {
    if (plantillaJustificacion == null) {
      return null;
    }
    return Stream.of(TipoHojaFirmaEnum.values()).filter(c -> c.getValue().equals(plantillaJustificacion)).findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }

}