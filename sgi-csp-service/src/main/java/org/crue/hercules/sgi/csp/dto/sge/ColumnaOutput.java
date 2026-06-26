package org.crue.hercules.sgi.csp.dto.sge;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Columna de un dato económico del SGE.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColumnaOutput {
  private String id;
  private String nombre;
  private boolean acumulable;
}
