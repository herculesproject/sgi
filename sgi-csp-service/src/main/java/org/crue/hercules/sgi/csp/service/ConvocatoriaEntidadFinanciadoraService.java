package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadFinanciadora;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ConvocatoriaEntidadFinanciadora}.
 */
public interface ConvocatoriaEntidadFinanciadoraService {

  /**
   * Guardar un nuevo {@link ConvocatoriaEntidadFinanciadora}.
   *
   * @param convocatoriaEntidadFinanciadora la entidad
   *                                        {@link ConvocatoriaEntidadFinanciadora}
   *                                        a guardar.
   * @return la entidad {@link ConvocatoriaEntidadFinanciadora} persistida.
   */
  ConvocatoriaEntidadFinanciadora create(ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadora);

  /**
   * Actualizar {@link ConvocatoriaEntidadFinanciadora}.
   *
   * @param convocatoriaEntidadFinanciadoraActualizar la entidad
   *                                                  {@link ConvocatoriaEntidadFinanciadora}
   *                                                  a actualizar.
   * @return la entidad {@link ConvocatoriaEntidadFinanciadora} persistida.
   */
  ConvocatoriaEntidadFinanciadora update(ConvocatoriaEntidadFinanciadora convocatoriaEntidadFinanciadoraActualizar);

  /**
   * Elimina el {@link ConvocatoriaEntidadFinanciadora}.
   *
   * @param id Id del {@link ConvocatoriaEntidadFinanciadora}.
   */
  void delete(Long id);

  /**
   * Obtiene {@link ConvocatoriaEntidadFinanciadora} por su id.
   *
   * @param id el id de la entidad {@link ConvocatoriaEntidadFinanciadora}.
   * @return la entidad {@link ConvocatoriaEntidadFinanciadora}.
   */
  ConvocatoriaEntidadFinanciadora findById(Long id);

  /**
   * Obtiene las {@link ConvocatoriaEntidadFinanciadora} para una
   * {@link Convocatoria}.
   *
   * @param idConvocatoria el id de la {@link Convocatoria}.
   * @param query          la información del filtro.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaEntidadFinanciadora} de la
   *         {@link Convocatoria} paginadas.
   */
  Page<ConvocatoriaEntidadFinanciadora> findAllByConvocatoria(Long idConvocatoria, String query, Pageable pageable);

  /**
   * Comprueba si existen datos vinculados a la {@link Convocatoria} de
   * {@link ConvocatoriaEntidadFinanciadora} con el fin de permitir la edición de
   * los campo Desglose de presupuesto
   *
   * @param id Id del {@link ConvocatoriaEntidadFinanciadora}.
   * @return true existen datos vinculados/false no existen datos vinculados.
   */
  Boolean hasConvocatoriaEntidad(Long id);

}