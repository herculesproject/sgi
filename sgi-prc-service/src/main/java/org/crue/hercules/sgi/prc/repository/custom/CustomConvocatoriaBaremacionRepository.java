package org.crue.hercules.sgi.prc.repository.custom;

import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;

public interface CustomConvocatoriaBaremacionRepository {
  /**
   * Obtiene la suma de puntos de cada tipo de una {@link ConvocatoriaBaremacion}
   * cuyo id
   * coincide con el indicado.
   * 
   * @param id el identificador de la {@link ConvocatoriaBaremacion}
   * @return suma de puntos de cada tipo
   */
  ConvocatoriaBaremacion findSumPuntosById(Long id);
}
