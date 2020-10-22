package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.model.InformeFormulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.InformeFormularioNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link InformeFormulario}.
 */
public interface InformeFormularioService {
  /**
   * Guardar {@link InformeFormulario}.
   *
   * @param informeFormulario la entidad {@link InformeFormulario} a guardar.
   * @return la entidad {@link InformeFormulario} persistida.
   */
  InformeFormulario create(InformeFormulario informeFormulario);

  /**
   * Actualizar {@link InformeFormulario}.
   *
   * @param informeFormulario la entidad {@link InformeFormulario} a actualizar.
   * @return la entidad {@link InformeFormulario} persistida.
   */
  InformeFormulario update(InformeFormulario informeFormulario);

  /**
   * Obtener todas las entidades {@link InformeFormulario} paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link InformeFormulario} paginadas y/o
   *         filtradas.
   */
  Page<InformeFormulario> findAll(List<QueryCriteria> query, Pageable pageable);

  /**
   * Obtiene {@link InformeFormulario} por id.
   *
   * @param id el id de la entidad {@link InformeFormulario}.
   * @return la entidad {@link InformeFormulario}.
   */
  InformeFormulario findById(Long id);

  /**
   * Elimina el {@link InformeFormulario} por id.
   *
   * @param id el id de la entidad {@link InformeFormulario}.
   */
  void delete(Long id) throws InformeFormularioNotFoundException;

  /**
   * Elimina todos los {@link InformeFormulario}.
   */
  void deleteAll();

  /**
   * Elimina el informe relacionado a una memoria
   * 
   * @param idMemoria identificador de la {@link Memoria}
   */
  void deleteInformeMemoria(Long idMemoria);

  /**
   * Devuelve un listado paginado de {@link InformeFormulario} filtrado por la
   * {@link Memoria}
   * 
   * @param id       identificador de la {@link Memoria}
   * @param pageable paginación
   * @return el listado paginado de {@link InformeFormulario}
   */
  Page<InformeFormulario> findByMemoria(Long id, Pageable pageable);

}