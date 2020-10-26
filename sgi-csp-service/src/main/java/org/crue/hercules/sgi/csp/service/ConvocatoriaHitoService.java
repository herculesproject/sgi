package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaHito;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ConvocatoriaHito}.
 */

public interface ConvocatoriaHitoService {

  /**
   * Guarda la entidad {@link ConvocatoriaHito}.
   * 
   * @param convocatoriaHito la entidad {@link ConvocatoriaHito} a guardar.
   * @return ConvocatoriaHito la entidad {@link ConvocatoriaHito} persistida.
   */
  ConvocatoriaHito create(ConvocatoriaHito convocatoriaHito);

  /**
   * Actualiza la entidad {@link ConvocatoriaHito}.
   * 
   * @param convocatoriaHitoActualizar la entidad {@link ConvocatoriaHito} a
   *                                   guardar.
   * @return ConvocatoriaHito la entidad {@link ConvocatoriaHito} persistida.
   */
  ConvocatoriaHito update(ConvocatoriaHito convocatoriaHitoActualizar);

  /**
   * Elimina la {@link ConvocatoriaHito}.
   *
   * @param id Id del {@link ConvocatoriaHito}.
   */
  void delete(Long id);

  /**
   * Obtiene {@link ConvocatoriaHito} por su id.
   *
   * @param id el id de la entidad {@link ConvocatoriaHito}.
   * @return la entidad {@link ConvocatoriaHito}.
   */
  ConvocatoriaHito findById(Long id);

  /**
   * Obtiene las {@link ConvocatoriaHito} para una {@link Convocatoria}.
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param query          la información del filtro.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaHito} de la
   *         {@link Convocatoria} paginadas.
   */
  Page<ConvocatoriaHito> findAllByConvocatoria(Long convocatoriaId, List<QueryCriteria> query, Pageable pageable);

}
