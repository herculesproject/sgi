package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.RolSocio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link RolSocio}.
 */

public interface RolSocioService {

  /**
   * Guarda la entidad {@link RolSocio}.
   * 
   * @param rolSocio la entidad {@link RolSocio} a guardar.
   * @return RolSocio la entidad {@link RolSocio} persistida.
   */
  RolSocio create(RolSocio rolSocio);

  /**
   * Actualiza los datos del {@link RolSocio}.
   * 
   * @param rolSocio {@link RolSocio} con los datos actualizados.
   * @return RolSocio {@link RolSocio} actualizado.
   */
  RolSocio update(final RolSocio rolSocio);

  /**
   * Reactiva el {@link RolSocio}.
   *
   * @param id Id del {@link RolSocio}.
   * @return la entidad {@link RolSocio} persistida.
   */
  RolSocio enable(Long id);

  /**
   * Desactiva el {@link RolSocio}.
   *
   * @param id Id del {@link RolSocio}.
   * @return la entidad {@link RolSocio} persistida.
   */
  RolSocio disable(Long id);

  /**
   * Comprueba la existencia del {@link RolSocio} por id.
   *
   * @param id el id de la entidad {@link RolSocio}.
   * @return true si existe y false en caso contrario.
   */
  boolean existsById(Long id);

  /**
   * Obtiene una entidad {@link RolSocio} por id.
   * 
   * @param id Identificador de la entidad {@link RolSocio}.
   * @return RolSocio la entidad {@link RolSocio}.
   */
  RolSocio findById(final Long id);

  /**
   * Obtiene todas las entidades {@link RolSocio} activas paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link RolSocio} activas paginadas y
   *         filtradas.
   */
  Page<RolSocio> findAll(String query, Pageable paging);

  /**
   * Obtiene todas las entidades {@link RolSocio} paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link RolSocio} paginadas y filtradas.
   */
  Page<RolSocio> findAllTodos(String query, Pageable paging);

}
