package org.crue.hercules.sgi.pii.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;
import org.crue.hercules.sgi.pii.enums.TipoPropiedad;
import org.crue.hercules.sgi.pii.model.TipoProteccion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoProteccionInput implements Serializable {

  @NotEmpty
  private List<I18nFieldValueDto> nombre;

  @NotEmpty
  @Size(max = TipoProteccion.DESCRIPCION_LENGTH)
  private String descripcion;

  @NotNull
  private TipoPropiedad tipoPropiedad;

  private Long padreId;

}
