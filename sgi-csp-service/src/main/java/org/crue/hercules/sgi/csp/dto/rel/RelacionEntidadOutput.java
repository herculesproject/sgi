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
 * Representación mínima de la entidad relacionada (convocatoria, proyecto,
 * grupo
 * o invención) para mostrarla en el listado de relaciones.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Builder
public class RelacionEntidadOutput implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;
  private List<I18nFieldValueDto> titulo;
  /** Código externo del proyecto relacionado ({@code null} para otros tipos). */
  private String codigoExterno;
  /**
   * Códigos SGE del proyecto o grupo relacionado ({@code null} para otros tipos).
   */
  private String codigosSge;
}
