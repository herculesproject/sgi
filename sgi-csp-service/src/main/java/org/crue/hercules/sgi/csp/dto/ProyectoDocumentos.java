package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.util.List;

import org.crue.hercules.sgi.csp.model.ProrrogaDocumento;
import org.crue.hercules.sgi.csp.model.ProyectoDocumento;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimientoDocumento;
import org.crue.hercules.sgi.csp.model.SocioPeriodoJustificacionDocumento;

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
public class ProyectoDocumentos implements Serializable {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Proyecto documentos */
  private List<ProyectoDocumento> proyectoDocumentos;

  /** Socio periodo justificación documento */
  private List<SocioPeriodoJustificacionDocumento> socioPeriodoJustificacionDocumentos;

  /** Proyecto periodo seguimiento documento */
  private List<ProyectoPeriodoSeguimientoDocumento> proyectoPeriodoSeguimientoDocumentos;

  /** Prorroga documento */
  private List<ProrrogaDocumento> prorrogaDocumentos;

}
