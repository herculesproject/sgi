package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.dto.BloqueOutput;
import org.crue.hercules.sgi.eti.exceptions.BloqueNotFoundException;
import org.crue.hercules.sgi.eti.model.Bloque;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link Bloque}.
 */
public interface BloqueService {

  /**
   * Obtener todas las entidades {@link Bloque} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Bloque} paginadas y/o filtradas.
   */
  Page<Bloque> findAll(String query, Pageable pageable);

  /**
   * Obtiene {@link Bloque} por id.
   *
   * @param id   el id de la entidad {@link Bloque}.
   * @param lang code language
   * @return la entidad {@link Bloque}.
   * @throws BloqueNotFoundException Si no existe ningún {@link Bloque} con ese
   *                                 id.
   */
  BloqueOutput findByIdAndLanguage(Long id, String lang) throws BloqueNotFoundException;

  /**
   * Obtener todas las entidades {@link Bloque} paginadas de una
   * {@link Formulario}.
   * 
   * @param id       Id del formulario
   * @param lang     code language
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Bloque} paginadas y/o filtradas.
   */
  Page<BloqueOutput> findByFormularioId(Long id, String lang, Pageable pageable);

  /**
   * Obtiene el {@link Bloque} de comentarios generales.
   * 
   * @param lang code language
   * @return el {@link Bloque}.
   */
  BloqueOutput getBloqueComentariosGenerales(String lang);

  /**
   * Obtener todas las entidades {@link Bloque} paginadas de una
   * {@link Formulario}.
   * 
   * @param id       Id del formulario
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Bloque} paginadas y/o filtradas.
   */
  Page<Bloque> findByFormularioIdAllLanguages(Long id, Pageable pageable);

  /**
   * Obtiene el {@link Bloque} de comentarios generales.
   *
   * @return el {@link Bloque}.
   */
  Bloque getBloqueComentariosGeneralesAllLanguages();

}