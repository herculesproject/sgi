package org.crue.hercules.sgi.prc.dto.csp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

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
public class InvencionDto implements Serializable {

  public enum ClasificacionCVN {
    /** Proyectos competitivos */
    COMPETITIVOS,
    /** Contratos, Convenios, Proyectos no competitivos */
    NO_COMPETITIVOS;
  }

  private Long id;
  private String titulo;
  private BigDecimal participacion;
  private BigDecimal cuantiaLicencias;
  private List<SolicitudProteccionDto> solicitudesProteccion;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class SolicitudProteccionDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Instant fechaConcesion;
    private ViaProteccionDto viaProteccion;

  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ViaProteccionDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String nombre;
    private String descripcion;
    Integer mesesPrioridad;
    Boolean paisEspecifico;
    Boolean extensionInternacional;
    Boolean variosPaises;

  }

}
