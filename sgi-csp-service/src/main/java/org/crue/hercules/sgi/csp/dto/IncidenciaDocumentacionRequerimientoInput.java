package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.model.BaseEntity;
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
public class IncidenciaDocumentacionRequerimientoInput implements Serializable {
  @NotNull
  private Long requerimientoJustificacionId;
  @NotEmpty
  private List<I18nFieldValueDto> nombreDocumento;
  @Size(max = BaseEntity.DEFAULT_LONG_TEXT_LENGTH)
  private String incidencia;
}
