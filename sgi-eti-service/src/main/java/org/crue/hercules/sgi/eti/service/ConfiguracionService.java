package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.model.Configuracion;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;

import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.ConfiguracionNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link Configuracion}.
 */
public interface ConfiguracionService {
  /**
   * Guardar {@link Configuracion}.
   *
   * @param configuracion la entidad {@link Configuracion} a guardar.
   * @return la entidad {@link Configuracion} persistida.
   */
  Configuracion create(Configuracion configuracion);

  /**
   * Actualizar {@link Configuracion}.
   *
   * @param configuracion la entidad {@link Configuracion} a actualizar.
   * @return la entidad {@link Configuracion} persistida.
   */
  Configuracion update(Configuracion configuracion);

  /**
   * Obtener todas las entidades {@link Configuracion} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Configuracion} paginadas y/o filtradas.
   */
  Page<Configuracion> findAll(List<QueryCriteria> query, Pageable pageable);

  /**
   * Obtiene la {@link Configuracion} por id.
   *
   * @param id el id de la entidad {@link Configuracion}.
   * @return la entidad {@link Configuracion}.
   */
  Configuracion findById(Long id);

  /**
   * Elimina la {@link Configuracion} por id.
   *
   * @param id el id de la entidad {@link Configuracion}.
   */
  void delete(Long id) throws ConfiguracionNotFoundException;

  /**
   * Elimina todas las entidades {@link Configuracion}.
   */
  void deleteAll();

}