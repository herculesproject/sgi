package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoSeguimientoCientifico;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar
 * {@link ConvocatoriaPeriodoSeguimientoCientifico}.
 */

public interface ConvocatoriaPeriodoSeguimientoCientificoService {

  /**
   * Guarda la entidad {@link ConvocatoriaPeriodoSeguimientoCientifico}.
   * 
   * @param convocatoriaPeriodoSeguimientoCientifico la entidad
   *                                                 {@link ConvocatoriaPeriodoSeguimientoCientifico}
   *                                                 a guardar.
   * @return ConvocatoriaPeriodoSeguimientoCientifico la entidad
   *         {@link ConvocatoriaPeriodoSeguimientoCientifico} persistida.
   */
  ConvocatoriaPeriodoSeguimientoCientifico create(
      ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientifico);

  /**
   * Actualiza los datos del {@link ConvocatoriaPeriodoSeguimientoCientifico}.
   * 
   * @param convocatoriaPeriodoSeguimientoCientificoActualizar {@link ConvocatoriaPeriodoSeguimientoCientifico}
   *                                                           con los datos
   *                                                           actualizados.
   * @return ConvocatoriaPeriodoSeguimientoCientifico
   *         {@link ConvocatoriaPeriodoSeguimientoCientifico} actualizado.
   */
  ConvocatoriaPeriodoSeguimientoCientifico update(
      final ConvocatoriaPeriodoSeguimientoCientifico convocatoriaPeriodoSeguimientoCientificoActualizar);

  /**
   * Elimina el {@link ConvocatoriaPeriodoSeguimientoCientifico}.
   *
   * @param id Id del {@link ConvocatoriaPeriodoSeguimientoCientifico}.
   */
  void delete(Long id);

  /**
   * Obtiene una entidad {@link ConvocatoriaPeriodoSeguimientoCientifico} por id.
   * 
   * @param id Identificador de la entidad
   *           {@link ConvocatoriaPeriodoSeguimientoCientifico}.
   * @return ConvocatoriaPeriodoSeguimientoCientifico la entidad
   *         {@link ConvocatoriaPeriodoSeguimientoCientifico}.
   */
  ConvocatoriaPeriodoSeguimientoCientifico findById(final Long id);

  /**
   * Obtiene todas las entidades {@link ConvocatoriaPeriodoSeguimientoCientifico}
   * para una {@link Convocatoria}.
   * 
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param query          información del filtro.
   * @param paging         información de paginación.
   * @return el listado de entidades
   *         {@link ConvocatoriaPeriodoSeguimientoCientifico} paginadas y
   *         filtradas.
   */
  Page<ConvocatoriaPeriodoSeguimientoCientifico> findAllByConvocatoria(Long convocatoriaId, List<QueryCriteria> query,
      Pageable paging);
}
