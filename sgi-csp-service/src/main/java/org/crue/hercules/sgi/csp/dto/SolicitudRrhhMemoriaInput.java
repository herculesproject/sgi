package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.model.SolicitudRrhh;
import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;

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
public class SolicitudRrhhMemoriaInput implements Serializable {

  @NotEmpty
  private List<I18nFieldValueDto> tituloTrabajo;

  @NotEmpty
  private List<I18nFieldValueDto> resumen;

  @Size(max = SolicitudRrhh.OBSERVACIONES_LENGTH)
  private String observaciones;

}
