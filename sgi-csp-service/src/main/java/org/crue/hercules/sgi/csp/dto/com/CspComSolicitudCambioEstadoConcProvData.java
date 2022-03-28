package org.crue.hercules.sgi.csp.dto.com;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

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
public class CspComSolicitudCambioEstadoConcProvData implements Serializable {
  /** Serial version */
  private static final long serialVersionUID = 1L;

  private String tituloConvocatoria;
  private Instant fechaProvisionalConvocatoria;
  private List<Enlace> enlaces;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  @JsonInclude(Include.NON_NULL)
  public static class Enlace implements Serializable {

    /** Serial version */
    private static final long serialVersionUID = 1L;
    private String descripcion;
    private String url;
    private String tipoEnlace;
  }
}
