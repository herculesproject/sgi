package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.dto.ConvocatoriaConceptoGastoWithEnableAccion;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ConvocatoriaConceptoGasto}.
 */

public interface ConvocatoriaConceptoGastoService {

  /**
   * Guarda la entidad {@link ConvocatoriaConceptoGasto}.
   * 
   * @param convocatoriaConceptoGasto la entidad {@link ConvocatoriaConceptoGasto}
   *                                  a guardar.
   * @return ConvocatoriaConceptoGasto la entidad
   *         {@link ConvocatoriaConceptoGasto} persistida.
   */
  ConvocatoriaConceptoGasto create(ConvocatoriaConceptoGasto convocatoriaConceptoGasto);

  /**
   * Actualiza la entidad {@link ConvocatoriaConceptoGasto}.
   * 
   * @param convocatoriaConceptoGastoActualizar la entidad
   *                                            {@link ConvocatoriaConceptoGasto}
   *                                            a guardar.
   * @return ConvocatoriaConceptoGasto la entidad
   *         {@link ConvocatoriaConceptoGasto} persistida.
   */
  ConvocatoriaConceptoGasto update(ConvocatoriaConceptoGasto convocatoriaConceptoGastoActualizar);

  /**
   * Elimina la {@link ConvocatoriaConceptoGasto}.
   *
   * @param id Id del {@link ConvocatoriaConceptoGasto}.
   */
  void delete(Long id);

  /**
   * Obtiene {@link ConvocatoriaConceptoGasto} por su id.
   *
   * @param id el id de la entidad {@link ConvocatoriaConceptoGasto}.
   * @return la entidad {@link ConvocatoriaConceptoGasto}.
   */
  ConvocatoriaConceptoGasto findById(Long id);

  /**
   * Obtiene las {@link ConvocatoriaConceptoGastoWithEnableAccion} permitidos para
   * una {@link Convocatoria}
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaConceptoGasto} de la
   *         {@link Convocatoria} paginadas.
   */
  Page<ConvocatoriaConceptoGastoWithEnableAccion> findAllByConvocatoriaAndPermitidoTrue(Long convocatoriaId,
      Pageable pageable);

  /**
   * Obtiene las {@link ConvocatoriaConceptoGastoWithEnableAccion} NO permitidos
   * para una {@link Convocatoria}
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaConceptoGasto} de la
   *         {@link Convocatoria} paginadas.
   */
  Page<ConvocatoriaConceptoGastoWithEnableAccion> findAllByConvocatoriaAndPermitidoFalse(Long convocatoriaId,
      Pageable pageable);
}
