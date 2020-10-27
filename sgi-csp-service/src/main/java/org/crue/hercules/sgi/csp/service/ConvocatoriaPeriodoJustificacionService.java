package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPeriodoJustificacion;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ConvocatoriaPeriodoJustificacion}.
 */
public interface ConvocatoriaPeriodoJustificacionService {

  /**
   * Guardar un nuevo {@link ConvocatoriaPeriodoJustificacion}.
   *
   * @param convocatoriaPeriodoJustificacion la entidad
   *                                         {@link ConvocatoriaPeriodoJustificacion}
   *                                         a guardar.
   * @return la entidad {@link ConvocatoriaPeriodoJustificacion} persistida.
   */
  ConvocatoriaPeriodoJustificacion create(ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacion);

  /**
   * Actualizar {@link ConvocatoriaPeriodoJustificacion}.
   *
   * @param convocatoriaPeriodoJustificacionActualizar la entidad
   *                                                   {@link ConvocatoriaPeriodoJustificacion}
   *                                                   a actualizar.
   * @return la entidad {@link ConvocatoriaPeriodoJustificacion} persistida.
   */
  ConvocatoriaPeriodoJustificacion update(ConvocatoriaPeriodoJustificacion convocatoriaPeriodoJustificacionActualizar);

  /**
   * Elimina el {@link ConvocatoriaPeriodoJustificacion}.
   *
   * @param id Id del {@link ConvocatoriaPeriodoJustificacion}.
   */
  void delete(Long id);

  /**
   * Obtiene {@link ConvocatoriaPeriodoJustificacion} por su id.
   *
   * @param id el id de la entidad {@link ConvocatoriaPeriodoJustificacion}.
   * @return la entidad {@link ConvocatoriaPeriodoJustificacion}.
   */
  ConvocatoriaPeriodoJustificacion findById(Long id);

  /**
   * Obtiene las {@link ConvocatoriaPeriodoJustificacion} para una
   * {@link Convocatoria}.
   *
   * @param idConvocatoria el id de la {@link Convocatoria}.
   * @param query          la información del filtro.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaPeriodoJustificacion} de la
   *         {@link Convocatoria} paginadas.
   */
  Page<ConvocatoriaPeriodoJustificacion> findAllByConvocatoria(Long idConvocatoria, List<QueryCriteria> query,
      Pageable pageable);

}