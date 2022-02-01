package org.crue.hercules.sgi.csp.dto;

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
public class NotificacionCVNEntidadFinanciadoraInput {

  private String datosEntidadFinanciadora;

  private String entidadFinanciadoraRef;

  private Long notificacionProyectoExternoCvnId;
}
