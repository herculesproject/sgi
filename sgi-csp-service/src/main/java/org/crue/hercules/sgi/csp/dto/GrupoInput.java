package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.model.Grupo;
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
public class GrupoInput implements Serializable {

  @Size(max = Grupo.ACRONIMO_LENGTH)
  private String acronimo;

  @NotEmpty
  private List<I18nFieldValueDto> nombre;

  @Size(max = Grupo.DIRECCION_LENGTH)
  private String direccion;

  @Size(max = Grupo.EMAIL_LENGTH)
  private String email;

  @NotNull
  private Instant fechaInicio;

  private Instant fechaFin;

  @Size(max = Grupo.IMAGEN_REF_LENGTH)
  private String imagenRef;

  @Size(max = Grupo.PROYECTO_SGE_REF_LENGTH)
  private String proyectoSgeRef;

  private Long solicitudId;

  @Size(max = Grupo.CODIGO_LENGTH)
  private String codigo;

  private Long tipoGrupoId;

  private Boolean especialInvestigacion;

  private List<I18nFieldValueDto> resumen;

  private String departamentoOrigenRef;

}
