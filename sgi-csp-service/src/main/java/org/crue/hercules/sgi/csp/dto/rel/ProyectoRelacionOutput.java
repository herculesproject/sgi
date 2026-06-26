package org.crue.hercules.sgi.csp.dto.rel;

import java.io.Serializable;
import java.util.List;

import org.crue.hercules.sgi.csp.dto.rel.RelacionOutput.TipoEntidad;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Relación de un {@link Proyecto} enriquecida con los datos mínimos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Builder
public class ProyectoRelacionOutput implements Serializable {

  private static final long serialVersionUID = 1L;

  /** Identificador de la relación en el módulo REL. */
  private Long id;
  private TipoEntidad tipoEntidadRelacionada;
  private RelacionEntidadOutput entidadRelacionada;
  private List<I18nFieldValueDto> observaciones;
}
