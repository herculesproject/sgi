package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;

import org.crue.hercules.sgi.csp.model.FuenteFinanciacion;

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
public class FuenteFinanciacionInput implements Serializable {
  @NotEmpty
  @Size(max = FuenteFinanciacion.NOMBRE_LENGTH)
  private List<I18nFieldValueDto> nombre;

  @Size(max = FuenteFinanciacion.DESCRIPCION_LENGTH)
  private String descripcion;

  @NotNull
  private Boolean fondoEstructural;

  @NotNull
  private Long tipoAmbitoGeograficoId;

  @NotNull
  private Long tipoOrigenFuenteFinanciacionId;
}
