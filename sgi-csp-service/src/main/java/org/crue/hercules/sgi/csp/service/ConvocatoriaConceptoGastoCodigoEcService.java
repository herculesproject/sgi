package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.dto.ConvocatoriaConceptoGastoCodigoEcWithEnableAccion;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoCodigoEc;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ConvocatoriaConceptoGastoCodigoEc}.
 */

public interface ConvocatoriaConceptoGastoCodigoEcService {

  /**
   * Guarda la entidad {@link ConvocatoriaConceptoGastoCodigoEc}.
   * 
   * @param convocatoriaConceptoGastoCodigoEc la entidad
   *                                          {@link ConvocatoriaConceptoGastoCodigoEc}
   *                                          a guardar.
   * @return ConvocatoriaConceptoGastoCodigoEc la entidad
   *         {@link ConvocatoriaConceptoGastoCodigoEc} persistida.
   */
  ConvocatoriaConceptoGastoCodigoEc create(ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEc);

  /**
   * Actualiza la entidad {@link ConvocatoriaConceptoGastoCodigoEc}.
   * 
   * @param convocatoriaConceptoGastoCodigoEcActualizar la entidad
   *                                                    {@link ConvocatoriaConceptoGastoCodigoEc}
   *                                                    a guardar.
   * @return ConvocatoriaConceptoGastoCodigoEc la entidad
   *         {@link ConvocatoriaConceptoGastoCodigoEc} persistida.
   */
  ConvocatoriaConceptoGastoCodigoEc update(
      ConvocatoriaConceptoGastoCodigoEc convocatoriaConceptoGastoCodigoEcActualizar);

  /**
   * Elimina la {@link ConvocatoriaConceptoGastoCodigoEc}.
   *
   * @param id Id del {@link ConvocatoriaConceptoGastoCodigoEc}.
   */
  void delete(Long id);

  /**
   * Obtiene {@link ConvocatoriaConceptoGastoCodigoEc} por su id.
   *
   * @param id el id de la entidad {@link ConvocatoriaConceptoGastoCodigoEc}.
   * @return la entidad {@link ConvocatoriaConceptoGastoCodigoEc}.
   */
  ConvocatoriaConceptoGastoCodigoEc findById(Long id);

  /**
   * Obtiene las {@link ConvocatoriaConceptoGastoCodigoEcWithEnableAccion}
   * permitidos para una {@link Convocatoria}
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaConceptoGastoCodigoEc} de la
   *         {@link Convocatoria} paginadas.
   */
  Page<ConvocatoriaConceptoGastoCodigoEcWithEnableAccion> findAllByConvocatoriaAndPermitidoTrue(Long convocatoriaId,
      Pageable pageable);

  /**
   * Obtiene las {@link ConvocatoriaConceptoGastoCodigoEcWithEnableAccion} NO
   * permitidos para una {@link Convocatoria}
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaConceptoGastoCodigoEc} de la
   *         {@link Convocatoria} paginadas.
   */
  Page<ConvocatoriaConceptoGastoCodigoEcWithEnableAccion> findAllByConvocatoriaAndPermitidoFalse(Long convocatoriaId,
      Pageable pageable);

}
