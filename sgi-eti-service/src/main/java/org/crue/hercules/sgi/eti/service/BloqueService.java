package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.dto.BloqueOutput;
import org.crue.hercules.sgi.eti.exceptions.BloqueNotFoundException;
import org.crue.hercules.sgi.eti.model.Bloque;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.framework.i18n.Language;
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
   * Obtiene {@link Bloque} por id y idioma.
   *
   * @param id   el id de la entidad {@link Bloque}.
   * @param lang El {@link Language} sobre el que buscar.
   * @return la entidad {@link Bloque}.
   * @throws BloqueNotFoundException Si no existe ningún {@link BloqueOutput} con
   *                                 ese
   *                                 id.
   */
  BloqueOutput findByIdAndLanguage(Long id, Language lang) throws BloqueNotFoundException;

  /**
   * Obtener todas las entidades {@link Bloque} paginadas de una
   * {@link Formulario} en el idioma solicitado.
   * 
   * @param id       Id del formulario
   * @param lang     El {@link Language} sobre el que buscar.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link BloqueOutput} paginadas y/o filtradas.
   */
  Page<BloqueOutput> findByFormularioId(Long id, Language lang, Pageable pageable);

  /**
   * Obtiene el {@link Bloque} de comentarios generales en el idioma solicitado.
   * 
   * @param lang El {@link Language} sobre el que buscar.
   * @return el {@link BloqueOutput}.
   */
  BloqueOutput getBloqueComentariosGenerales(Language lang);

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