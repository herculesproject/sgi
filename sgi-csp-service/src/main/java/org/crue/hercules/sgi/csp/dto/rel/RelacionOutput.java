package org.crue.hercules.sgi.csp.dto.rel;

import java.io.Serializable;
import java.util.List;

import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Representación de una relación del módulo REL recuperada a través de CSP.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Builder
public class RelacionOutput implements Serializable {

  public enum TipoEntidad {
    /** Proyecto */
    PROYECTO,
    /** Convocatoria */
    CONVOCATORIA,
    /** Invencion */
    INVENCION,
    /** Grupo */
    GRUPO
  }

  private Long id;
  private TipoEntidad tipoEntidadOrigen;
  private TipoEntidad tipoEntidadDestino;
  private String entidadOrigenRef;
  private String entidadDestinoRef;
  private List<I18nFieldValueDto> observaciones;
}
