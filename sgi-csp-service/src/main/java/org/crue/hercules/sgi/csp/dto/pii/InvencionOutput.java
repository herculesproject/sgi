package org.crue.hercules.sgi.csp.dto.pii;

import java.io.Serializable;
import java.util.List;

import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Representación mínima de una Invención del módulo PII.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Builder
public class InvencionOutput implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;
  private List<I18nFieldValueDto> titulo;
}
