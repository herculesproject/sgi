package org.crue.hercules.sgi.eti.dto.sgo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Unidad de vinculación del SGO.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnidadVinculacionOutput implements Serializable {
  /** Serial version */
  private static final long serialVersionUID = 1L;

  /** Id */
  private String id;
  /** Nombre */
  private String nombre;
  /** Código */
  private String codigo;
  /** Id de la unidad predecesora, null en las unidades raíz (tipos de unidad) */
  private String predecesorId;
  /** Activo */
  private Boolean activo;
}
