package org.crue.hercules.sgi.rep.dto.eti;

import java.time.Instant;

import org.crue.hercules.sgi.rep.dto.BaseRestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MemoriaDto extends BaseRestDto {

  public enum Tipo {
    /** Nueva */
    NUEVA,
    /** Modificacion */
    MODIFICACION,
    /** Ratificacion */
    RATIFICACION;
  }

  private String numReferencia;
  private PeticionEvaluacionDto peticionEvaluacion;
  private ComiteDto comite;
  private FormularioDto formulario;
  private FormularioDto formularioSeguimientoAnual;
  private FormularioDto formularioSeguimientoFinal;
  private FormularioDto formularioRetrospectiva;
  private String titulo;
  private String personaRef;
  private Tipo tipo;
  private TipoEstadoMemoriaDto estadoActual;
  private Instant fechaEnvioSecretaria;
  private Boolean requiereRetrospectiva;
  private RetrospectivaDto retrospectiva;
  private Integer version;
  private Boolean activo;
  private MemoriaDto memoriaOriginal;

}