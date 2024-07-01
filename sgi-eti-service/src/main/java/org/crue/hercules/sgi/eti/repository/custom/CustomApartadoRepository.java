package org.crue.hercules.sgi.eti.repository.custom;

import java.util.List;

import org.crue.hercules.sgi.eti.dto.ApartadoOutput;
import org.crue.hercules.sgi.eti.model.Apartado;
import org.crue.hercules.sgi.eti.model.Bloque;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * Custom repository para {@link Apartado}.
 */
@Component
public interface CustomApartadoRepository {

  /**
   * Devuelve una lista paginada de {@link Apartado} para una determinada
   * {@link Apartado} con la informacion de si es eliminable o no.
   * 
   * No son eliminables las {@link Apartado} que estan asociadas a una
   * {@link Memoria} que no esta en alguno de los siguiente estados: En
   * elaboración, Completada, Favorable, Pendiente de Modificaciones Mínimas,
   * Pendiente de correcciones y No procede evaluar.
   * 
   * @param idBloque Id de {@link Bloque}.
   * @param lang     El {@link Language} sobre el que buscar.
   * @param pageable pageable
   * @return lista de {@link Apartado}
   */
  Page<ApartadoOutput> findByBloqueIdAndPadreIsNullAndLanguage(Long idBloque, Language lang, Pageable pageable);

  /**
   * Devuelve una lista paginada de {@link Apartado} para una determinada
   * {@link Apartado}
   * 
   * @param idPadre  Id del padre {@link Apartado}.
   * @param lang     El {@link Language} sobre el que buscar.
   * @param pageable pageable
   * @return lista de {@link Apartado}
   */
  Page<ApartadoOutput> findByPadreIdAndLanguage(Long idPadre, Language lang, Pageable pageable);

  /**
   * Devuelve una lista paginada de {@link Apartado} para un determinada
   * {@link Apartado} y language
   * 
   * @param idApartado Id de {@link Apartado}.
   * @param lang       El {@link Language} sobre el que buscar.
   * @return lista de {@link Apartado}
   */
  ApartadoOutput findByApartadoIdAndLanguage(Long idApartado, Language lang);

  /**
   * Devuelve una lista paginada de {@link Apartado} para un determinada
   * {@link Apartado} y language
   * 
   * @param idApartado Id de {@link Apartado}.
   * @param idPadre    id {@link Apartado} padre
   * @param lang       El {@link Language} sobre el que buscar.
   * @return lista de {@link Apartado}
   */
  ApartadoOutput findByApartadoIdAndPadreIdAndLanguage(Long idApartado, Long idPadre, Language lang);

  /**
   * Obtiene las entidades {@link Apartado} filtradaspor el id
   * de su padre {@link Apartado}.
   *
   * @param id   id del {@link Apartado}.
   * @param lang El {@link Language} sobre el que buscar.
   * @return el listado de entidades {@link Apartado} hijas.
   */
  List<ApartadoOutput> findByPadreIdAndLanguageOrderByOrdenDesc(Long id, Language lang);

}
