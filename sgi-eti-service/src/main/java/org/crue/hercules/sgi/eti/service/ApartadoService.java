package org.crue.hercules.sgi.eti.service;

import java.util.List;

import org.crue.hercules.sgi.eti.dto.ApartadoOutput;
import org.crue.hercules.sgi.eti.dto.ApartadoTreeOutput;
import org.crue.hercules.sgi.eti.exceptions.ApartadoNotFoundException;
import org.crue.hercules.sgi.eti.model.Apartado;
import org.crue.hercules.sgi.eti.model.Bloque;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link Apartado}.
 */
public interface ApartadoService {

  /**
   * Obtiene las entidades {@link Apartado} filtradas y paginadas según los
   * criterios de búsqueda.
   *
   * @param query  filtro de búsqueda.
   * @param paging pageable
   * @return el listado de entidades {@link Apartado} paginadas y filtradas.
   */
  Page<Apartado> findAll(String query, Pageable paging);

  /**
   * Obtiene {@link Apartado} por id.
   *
   * @param id   El id de la entidad {@link Apartado}.
   * @param lang code language
   * @return La entidad {@link Apartado}.
   * @throws ApartadoNotFoundException Si no existe ninguna entidad
   *                                   {@link Apartado} con ese id.
   * @throws IllegalArgumentException  Si no se informa Id.
   */
  ApartadoOutput findByIdAndLanguage(Long id, String lang) throws ApartadoNotFoundException, IllegalArgumentException;

  /**
   * Obtiene {@link Apartado} por id.
   *
   * @param id      El id de la entidad {@link Apartado}.
   * @param idPadre id del {@link Apartado} padre
   * @param lang    code language
   * @return La entidad {@link Apartado}.
   * @throws ApartadoNotFoundException Si no existe ninguna entidad
   *                                   {@link Apartado} con ese id.
   * @throws IllegalArgumentException  Si no se informa Id.
   */
  ApartadoOutput findByIdAndPadreIdAndLanguage(Long id, Long idPadre, String lang)
      throws ApartadoNotFoundException, IllegalArgumentException;

  /**
   * Obtiene las entidades {@link Apartado} filtradas y paginadas según por el id
   * de su {@link Bloque}. Solamente se devuelven los Apartados de primer nivel
   * (sin padre).
   *
   * @param id       id del {@link Bloque}.
   * @param lang     code language
   * @param pageable pageable
   * @return el listado de entidades {@link Apartado} paginadas y filtradas.
   */
  Page<ApartadoOutput> findByBloqueId(Long id, String lang, Pageable pageable);

  /**
   * Obtiene las entidades {@link Apartado} filtradas y paginadas según por el id
   * de su padre {@link Apartado}.
   *
   * @param id       id del {@link Apartado} padre.
   * @param lang     code language
   * @param pageable pageable
   * @return el listado de entidades {@link Apartado} paginadas y filtradas.
   */
  Page<ApartadoOutput> findByPadreId(Long id, String lang, Pageable pageable);

  /**
   * Obtiene las entidades {@link ApartadoTreeOutput} paginadas por el id
   * de su {@link Bloque}. Se devuelven los Apartados de primer nivel
   * (sin padre) con sus arboles de apartados hijos.
   *
   * @param id       id del {@link Bloque}.
   * @param lang     code language
   * @param pageable pageable
   * @return el listado de entidades {@link ApartadoTreeOutput} paginadas.
   */
  Page<ApartadoTreeOutput> findApartadosTreeByBloqueId(Long id, String lang, Pageable pageable);

  /**
   * Obtiene las entidades {@link Apartado} en todos los idiomas
   *
   * @param id id del {@link Apartado}.
   * @return el listado de entidades {@link Apartado} paginadas y filtradas.
   */
  Apartado findByApartadoAllLanguages(Long id);

  /**
   * Obtiene las entidades {@link Apartado} filtrados por el padreen todos los
   * idiomas
   *
   * @param id      id del {@link Apartado}.
   * @param idPadre id padre del {@link Apartado}.
   * @return el listado de entidades {@link Apartado} paginadas y filtradas.
   */
  List<Apartado> findByApartadoAndPadreAllLanguages(Long id, Long idPadre);

  /**
   * Obtiene las entidades {@link Apartado} filtradas y paginadas según por el id
   * de su padre {@link Apartado}.
   *
   * @param id       id del {@link Apartado} padre.
   * @param pageable pageable
   * @return el listado de entidades {@link Apartado} paginadas y filtradas.
   */
  Page<Apartado> findByPadreIdAllLanguages(Long id, Pageable pageable);

  /**
   * Obtiene las entidades {@link Apartado} filtradas y paginadas según por el id
   * de su {@link Bloque}. Solamente se devuelven los Apartados de primer nivel
   * (sin padre).
   *
   * @param id       id del {@link Bloque}.
   * @param pageable pageable
   * @return el listado de entidades {@link Apartado} paginadas y filtradas.
   */
  Page<Apartado> findByBloqueIdAllLanguages(Long id, Pageable pageable);

}
