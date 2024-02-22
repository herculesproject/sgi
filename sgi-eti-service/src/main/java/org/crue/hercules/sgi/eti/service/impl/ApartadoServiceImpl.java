package org.crue.hercules.sgi.eti.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.eti.converter.ApartadoTreeConverter;
import org.crue.hercules.sgi.eti.dto.ApartadoOutput;
import org.crue.hercules.sgi.eti.dto.ApartadoTreeOutput;
import org.crue.hercules.sgi.eti.exceptions.ApartadoNotFoundException;
import org.crue.hercules.sgi.eti.model.Apartado;
import org.crue.hercules.sgi.eti.model.Bloque;
import org.crue.hercules.sgi.eti.repository.ApartadoRepository;
import org.crue.hercules.sgi.eti.service.ApartadoService;
import org.crue.hercules.sgi.eti.util.AssertHelper;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link Apartado}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ApartadoServiceImpl implements ApartadoService {

  private final ApartadoRepository repository;
  private final ApartadoTreeConverter apartadoTreeConverter;

  public ApartadoServiceImpl(ApartadoRepository repository, ApartadoTreeConverter apartadoTreeConverter) {
    this.repository = repository;
    this.apartadoTreeConverter = apartadoTreeConverter;
  }

  /**
   * Obtiene las entidades {@link Apartado} filtradas y paginadas según los
   * criterios de búsqueda.
   *
   * @param query  filtro de búsqueda.
   * @param paging pageable
   * @return el listado de entidades {@link Apartado} paginadas y filtradas.
   */
  @Override
  public Page<Apartado> findAll(String query, Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");

    Specification<Apartado> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<Apartado> returnValue = repository.findAll(specs, paging);

    log.debug("findAll(String query, Pageable paging) - end");

    return returnValue;
  }

  /**
   * Obtiene {@link Apartado} por id.
   *
   * @param id El id de la entidad {@link Apartado}.
   * @return La entidad {@link Apartado}.
   * @throws ApartadoNotFoundException Si no existe ninguna entidad
   *                                   {@link Apartado} con ese id.
   * @throws IllegalArgumentException  Si no se informa Id.
   */
  @Override
  public ApartadoOutput findByIdAndLanguage(final Long id, String lang) {
    log.debug("findById(final Long id) - start");
    AssertHelper.idNotNull(id, Apartado.class);
    repository.findById(id).orElseThrow(() -> new ApartadoNotFoundException(id));
    final ApartadoOutput apartado = repository.findByApartadoIdAndLanguage(id, lang);
    log.debug("findById(final Long id) - end");
    return apartado;
  }

  /**
   * Obtiene {@link Apartado} por id.
   *
   * @param id El id de la entidad {@link Apartado}.
   * @return La entidad {@link Apartado}.
   * @throws ApartadoNotFoundException Si no existe ninguna entidad
   *                                   {@link Apartado} con ese id.
   * @throws IllegalArgumentException  Si no se informa Id.
   */
  @Override
  public ApartadoOutput findByIdAndPadreIdAndLanguage(final Long id, Long idPadre, String lang) {
    log.debug("findById(final Long id) - start");
    AssertHelper.idNotNull(id, Apartado.class);
    final ApartadoOutput apartado = repository.findByApartadoIdAndPadreIdAndLanguage(id, idPadre, lang);
    log.debug("findById(final Long id) - end");
    return apartado;
  }

  /**
   * Obtiene las entidades {@link Apartado} filtradas y paginadas según por el id
   * de su {@link Bloque}. Solamente se devuelven los Apartados de primer nivel
   * (sin padre).
   *
   * @param id       id del {@link Bloque}.
   * @param pageable pageable
   * @return el listado de entidades {@link Apartado} paginadas y filtradas.
   */
  @Override
  public Page<ApartadoOutput> findByBloqueId(Long id, String lang, Pageable pageable) {
    log.debug("findByBloqueId(Long id, Pageable pageable) - start");
    AssertHelper.idNotNull(id, Bloque.class);
    final Page<ApartadoOutput> apartado = repository.findByBloqueIdAndPadreIsNullAndLanguage(id, lang, pageable);
    log.debug("findByBloqueId(Long id, Pageable pageable) - end");
    return apartado;
  }

  /**
   * Obtiene las entidades {@link Apartado} filtradas y paginadas según por el id
   * de su padre {@link Apartado}.
   *
   * @param id       id del {@link Apartado}.
   * @param lang     code language
   * @param pageable pageable
   * @return el listado de entidades {@link Apartado} paginadas y filtradas.
   */
  @Override
  public Page<ApartadoOutput> findByPadreId(Long id, String lang, Pageable pageable) {
    log.debug("findByPadreId(Long id, Pageable pageable) - start");
    AssertHelper.idNotNull(id, Apartado.class);
    final Page<ApartadoOutput> apartado = repository.findByPadreIdAndLanguage(id, lang, pageable);
    log.debug("findByPadreId(Long id, Pageable pageable) - end");
    return apartado;
  }

  /**
   * Obtiene las entidades {@link ApartadoTreeOutput} paginadas por el id
   * de su {@link Bloque}. Se devuelven los Apartados de primer nivel
   * (sin padre) con sus arboles de apartados hijos.
   *
   * @param id       id del {@link Bloque}.
   * @param pageable pageable
   * @return el listado de entidades {@link ApartadoTreeOutput} paginadas.
   */
  public Page<ApartadoTreeOutput> findApartadosTreeByBloqueId(Long id, String lang, Pageable pageable) {
    log.debug("findByPadreId(Long id, Pageable pageable) - start");
    AssertHelper.idNotNull(id, Bloque.class);
    final Page<ApartadoTreeOutput> apartados = repository.findByBloqueIdAndPadreIsNullAndLanguage(id, lang, pageable)
        .map(apartadoTreeConverter::convert)
        .map(this::fillApartadoTreeHijosRecursive);

    log.debug("findByPadreId(Long id, Pageable pageable) - end");
    return apartados;
  }

  private ApartadoTreeOutput fillApartadoTreeHijosRecursive(ApartadoTreeOutput apartado) {
    List<ApartadoTreeOutput> hijos = apartadoTreeConverter.convertList(
        repository.findByPadreIdAndLanguageOrderByOrdenDesc(apartado.getId(), apartado.getLang()))
        .stream()
        .map(this::fillApartadoTreeHijosRecursive)
        .collect(Collectors.toList());
    apartado.setHijos(hijos);

    return apartado;
  }

  /**
   * Obtiene las entidades {@link Apartado} en todos los idiomas
   *
   * @param id id del {@link Apartado}.
   * @return el listado de entidades {@link Apartado} paginadas y filtradas.
   */
  @Override
  public Apartado findByApartadoAllLanguages(Long id) {
    log.debug("findByApartadoAllLanguages(Long id) - start");
    AssertHelper.idNotNull(id, Apartado.class);
    final Apartado apartado = repository.findById(id).orElseThrow(() -> new ApartadoNotFoundException(id));
    log.debug("findByApartadoAllLanguages(Long id) - end");
    return apartado;
  }

  /**
   * Obtiene las entidades {@link Apartado} filtrados por el padreen todos los
   * idiomas
   *
   * @param id      id del {@link Apartado}.
   * @param idPadre id padre del {@link Apartado}.
   * @return el listado de entidades {@link Apartado} paginadas y filtradas.
   */
  @Override
  public List<Apartado> findByApartadoAndPadreAllLanguages(Long id, Long idPadre) {
    log.debug("findByApartadoAndPadreAllLanguages(Long id, Long idPadre) - start");
    AssertHelper.idNotNull(id, Apartado.class);
    final List<Apartado> apartados = repository.findByIdAndPadreId(id, idPadre);
    log.debug("findByApartadoAndPadreAllLanguages(Long id, Long idPadre) - end");
    return apartados;
  }

  /**
   * Obtiene las entidades {@link Apartado} filtradas y paginadas según por el id
   * de su padre {@link Apartado}.
   *
   * @param id       id del {@link Apartado}.
   * @param pageable pageable
   * @return el listado de entidades {@link Apartado} paginadas y filtradas.
   */
  @Override
  public Page<Apartado> findByPadreIdAllLanguages(Long id, Pageable pageable) {
    log.debug("findByPadreId(Long id, Pageable pageable) - start");
    AssertHelper.idNotNull(id, Apartado.class);
    final Page<Apartado> apartado = repository.findByPadreId(id, pageable);
    log.debug("findByPadreId(Long id, Pageable pageable) - end");
    return apartado;
  }

  /**
   * Obtiene las entidades {@link Apartado} filtradas y paginadas según por el id
   * de su {@link Bloque}. Solamente se devuelven los Apartados de primer nivel
   * (sin padre).
   *
   * @param id       id del {@link Bloque}.
   * @param pageable pageable
   * @return el listado de entidades {@link Apartado} paginadas y filtradas.
   */
  @Override
  public Page<Apartado> findByBloqueIdAllLanguages(Long id, Pageable pageable) {
    log.debug("findByBloqueId(Long id, Pageable pageable) - start");
    AssertHelper.idNotNull(id, Bloque.class);
    final Page<Apartado> apartado = repository.findByBloqueIdAndPadreIsNull(id, pageable);
    log.debug("findByBloqueId(Long id, Pageable pageable) - end");
    return apartado;
  }

}
