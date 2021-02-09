package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoCodigoEc;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
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
   * Obtiene las {@link ConvocatoriaConceptoGastoCodigoEc} para una
   * {@link ConvocatoriaConceptoGasto}
   *
   * @param convocatoriaConceptoGastoId el id de la
   *                                    {@link ConvocatoriaConceptoGasto}.
   * @param pageable                    la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaConceptoGastoCodigoEc} de la
   *         {@link ConvocatoriaConceptoGasto} paginadas.
   */
  Page<ConvocatoriaConceptoGastoCodigoEc> findAllByConvocatoriaConceptoGasto(Long convocatoriaConceptoGastoId,
      Pageable pageable);

  /**
   * Obtiene las {@link ConvocatoriaConceptoGastoCodigoEc} permitidos para una
   * {@link Convocatoria}
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaConceptoGastoCodigoEc} de la
   *         {@link Convocatoria} paginadas.
   */
  Page<ConvocatoriaConceptoGastoCodigoEc> findAllByConvocatoriaAndPermitidoTrue(Long convocatoriaId, Pageable pageable);

  /**
   * Obtiene las {@link ConvocatoriaConceptoGastoCodigoEc} NO permitidos para una
   * {@link Convocatoria}
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaConceptoGastoCodigoEc} de la
   *         {@link Convocatoria} paginadas.
   */
  Page<ConvocatoriaConceptoGastoCodigoEc> findAllByConvocatoriaAndPermitidoFalse(Long convocatoriaId,
      Pageable pageable);

  /**
   * Actualiza el listado de {@link ConvocatoriaConceptoGastoCodigoEc} de la
   * {@link ConvocatoriaConceptoGasto} con el listado
   * convocatoriaPeriodoJustificaciones añadiendo, editando o eliminando los
   * elementos segun proceda.
   *
   * @param convocatoriaConceptoGastoId        Id de la
   *                                           {@link ConvocatoriaConceptoGasto}.
   * @param convocatoriaConceptoGastoCodigoEcs lista con los nuevos
   *                                           {@link ConvocatoriaConceptoGastoCodigoEc}
   *                                           a guardar.
   * @return la entidad {@link ConvocatoriaConceptoGastoCodigoEc} persistida.
   */
  List<ConvocatoriaConceptoGastoCodigoEc> update(Long convocatoriaConceptoGastoId,
      List<ConvocatoriaConceptoGastoCodigoEc> convocatoriaConceptoGastoCodigoEcs);

  /**
   * Comprueba la existencia del {@link ConvocatoriaConceptoGastoCodigoEc} por id
   * de {@link ConvocatoriaConceptoGasto}
   *
   * @param id el id de la entidad {@link ConvocatoriaConceptoGasto}.
   * @return true si existe y false en caso contrario.
   */
  boolean existsByConvocatoriaConceptoGasto(final Long id);

}
