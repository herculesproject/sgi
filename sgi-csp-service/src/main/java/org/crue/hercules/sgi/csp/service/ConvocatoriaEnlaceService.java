package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.ConvocatoriaEnlace;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ConvocatoriaEnlace}.
 */
public interface ConvocatoriaEnlaceService {

  /**
   * Guardar un nuevo {@link ConvocatoriaEnlace}.
   *
   * @param convocatoriaEnlace la entidad {@link ConvocatoriaEnlace} a guardar.
   * @return la entidad {@link ConvocatoriaEnlace} persistida.
   */
  ConvocatoriaEnlace create(ConvocatoriaEnlace convocatoriaEnlace);

  /**
   * Actualizar {@link ConvocatoriaEnlace}.
   *
   * @param convocatoriaEnlaceActualizar la entidad {@link ConvocatoriaEnlace} a
   *                                     actualizar.
   * @return la entidad {@link ConvocatoriaEnlace} persistida.
   */
  ConvocatoriaEnlace update(ConvocatoriaEnlace convocatoriaEnlaceActualizar);

  /**
   * Elimina el {@link ConvocatoriaEnlace}.
   *
   * @param id Id del {@link ConvocatoriaEnlace}.
   */
  void delete(Long id);

  /**
   * Obtiene {@link ConvocatoriaEnlace} por su id.
   *
   * @param id el id de la entidad {@link ConvocatoriaEnlace}.
   * @return la entidad {@link ConvocatoriaEnlace}.
   */
  ConvocatoriaEnlace findById(Long id);

  /**
   * Obtener todas las entidades {@link ConvocatoriaEnlace} paginadas y/o
   * filtradas.
   * 
   * @param id     id de la convocatoria
   * @param query  la información del filtro.
   * @param paging la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaEnlace} paginadas y/o
   *         filtradas.
   */

  Page<ConvocatoriaEnlace> findAllByConvocatoria(Long id, List<QueryCriteria> query, Pageable paging);

}
