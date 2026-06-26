package org.crue.hercules.sgi.csp.service;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPartida;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

/**
 * Service Interface para gestionar {@link ConvocatoriaPartida}.
 */
@Validated
public interface ConvocatoriaPartidaService {

  /**
   * Guardar un nuevo {@link ConvocatoriaPartida}.
   *
   * @param convocatoriaPartida la partida {@link ConvocatoriaPartida} a guardar.
   * @return la partida {@link ConvocatoriaPartida} persistida.
   */
  ConvocatoriaPartida create(@Valid ConvocatoriaPartida convocatoriaPartida);

  /**
   * Actualizar {@link ConvocatoriaPartida}.
   *
   * @param convocatoriaPartidaActualizar la partida {@link ConvocatoriaPartida} a
   *                                      actualizar.
   * @return la partida {@link ConvocatoriaPartida} persistida.
   */
  ConvocatoriaPartida update(@Valid ConvocatoriaPartida convocatoriaPartidaActualizar);

  /**
   * Elimina el {@link ConvocatoriaPartida}.
   *
   * @param id Id del {@link ConvocatoriaPartida}.
   */
  void delete(Long id);

  /**
   * Obtiene {@link ConvocatoriaPartida} por su id.
   *
   * @param id el id de la partida {@link ConvocatoriaPartida}.
   * @return la partida {@link ConvocatoriaPartida}.
   */
  ConvocatoriaPartida findById(Long id);

  /**
   * Obtiene las {@link ConvocatoriaPartida} para una {@link Convocatoria}.
   *
   * @param idConvocatoria el id de la {@link Convocatoria}.
   * @param query          la información del filtro.
   * @param pageable       la información de la paginación.
   * @return la lista de partidas {@link ConvocatoriaPartida} de la
   *         {@link Convocatoria} paginadas.
   */
  Page<ConvocatoriaPartida> findAllByConvocatoria(Long idConvocatoria, String query, Pageable pageable);

  /**
   * Obtiene las {@link ConvocatoriaPartida} de la {@link Convocatoria} asociada
   * al {@link Proyecto}, controlando el acceso a nivel de {@link Proyecto}.
   *
   * @param proyectoId el id del {@link Proyecto}.
   * @param query      la información del filtro.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaPartida} paginadas.
   */
  Page<ConvocatoriaPartida> findAllByProyectoId(Long proyectoId, String query, Pageable pageable);

  /**
   * Hace las comprobaciones necesarias para determinar si la
   * {@link ConvocatoriaPartida} puede ser modificada. También se utilizará para
   * permitir la creación, modificación o eliminación de ciertas entidades
   * relacionadas con la propia {@link ConvocatoriaPartida}.
   *
   * @param id        Id de la {@link ConvocatoriaPartida}.
   * @param authority Authority a validar
   * @return true si puede ser modificada / false si no puede ser modificada
   */
  boolean modificable(Long id, String authority);

}