package org.crue.hercules.sgi.eti.controller;

import java.util.List;
import java.util.Locale;

import org.crue.hercules.sgi.eti.dto.ApartadoOutput;
import org.crue.hercules.sgi.eti.exceptions.ApartadoNotFoundException;
import org.crue.hercules.sgi.eti.model.Apartado;
import org.crue.hercules.sgi.eti.service.ApartadoService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ApartadoController
 */
@RestController
@RequestMapping("/apartados")
@Slf4j
public class ApartadoController {

  /** Apartado service */
  private ApartadoService service;

  /**
   * Instancia un nuevo ApartadoController.
   * 
   * @param service ApartadoService.
   */
  public ApartadoController(ApartadoService service) {
    log.debug("ApartadoController(ApartadoService service) - start");
    this.service = service;
    log.debug("ApartadoController(ApartadoService service) - end");
  }

  /**
   * Obtiene las entidades {@link Apartado} filtradas y paginadas según los
   * criterios de búsqueda.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable
   * @return el listado de entidades {@link Apartado} paginadas y filtradas.
   */
  @GetMapping()
  ResponseEntity<Page<Apartado>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging - start");
    Page<Apartado> page = service.findAll(query, paging);
    log.debug("findAll(String query, Pageable paging - end");
    if (page.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Obtiene la entidad {@link Apartado} por id.
   * 
   * @param id El id de la entidad {@link Apartado}.
   * @return La entidad {@link Apartado}.
   * @throws ApartadoNotFoundException Si no existe ninguna entidad
   *                                   {@link Apartado} con ese id.
   * @throws IllegalArgumentException  Si no se informa id.
   */
  @GetMapping("/{id}")
  ApartadoOutput one(@PathVariable Long id) {
    log.debug("one(Long id) - start");
    Locale locale = LocaleContextHolder.getLocale();
    ApartadoOutput returnValue = service.findByIdAndLanguage(id, locale.getLanguage());
    log.debug("one(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene la entidad {@link Apartado} por id.
   * 
   * @param id El id de la entidad {@link Apartado}.
   * @return La entidad {@link Apartado}.
   * @throws ApartadoNotFoundException Si no existe ninguna entidad
   *                                   {@link Apartado} con ese id.
   * @throws IllegalArgumentException  Si no se informa id.
   */
  @GetMapping("/{id}/padre/{idPadre}")
  ApartadoOutput apartadoByPadre(@PathVariable Long id, @PathVariable Long idPadre) {
    log.debug("one(Long id) - start");
    Locale locale = LocaleContextHolder.getLocale();
    ApartadoOutput returnValue = service.findByIdAndPadreIdAndLanguage(id, idPadre, locale.getLanguage());
    log.debug("one(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene las entidades {@link Apartado} por el id de su padre
   * {@link Apartado}.
   * 
   * @param id     El id de la entidad {@link Apartado}.
   * @param paging pageable
   * @return el listado de entidades {@link Apartado} paginadas y filtradas.
   */
  @GetMapping("/{id}/hijos/{lang}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-INV-EVALR', 'ETI-MEM-INV-ER')")
  ResponseEntity<Page<ApartadoOutput>> getHijos(@PathVariable Long id, @PathVariable String lang,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("getHijos(Long id, Pageable paging - start");
    Page<ApartadoOutput> page = service.findByPadreId(id, lang, paging);
    log.debug("getHijos(Long id, Pageable paging - end");
    if (page.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Obtiene las entidades {@link Apartado}
   * 
   * @param id El id de la entidad {@link Apartado}.
   * @return el listado de entidades {@link Apartado} paginadas y filtradas.
   */
  @GetMapping("/{id}/all-languages")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-INV-EVALR', 'ETI-MEM-INV-ER')")
  ResponseEntity<Apartado> getApartadoAllLanguages(@PathVariable Long id) {
    log.debug("getHijos(Long id, Pageable paging - start");
    Apartado returnValue = service.findByApartadoAllLanguages(id);
    log.debug("getHijos(Long id, Pageable paging - end");
    if (ObjectUtils.isEmpty(returnValue)) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  /**
   * Obtiene las entidades {@link Apartado} por el id de su padre
   * {@link Apartado}.
   * 
   * @param id     El id de la entidad {@link Apartado}.
   * @param paging pageable
   * @return el listado de entidades {@link Apartado} paginadas y filtradas.
   */
  @GetMapping("/{id}/all-languages/{idPadre}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-INV-EVALR', 'ETI-MEM-INV-ER')")
  ResponseEntity<List<Apartado>> getApartadosPadreAllLanguages(@PathVariable Long id,
      @PathVariable Long idPadre) {
    log.debug("getHijos(Long id, Pageable paging - start");
    List<Apartado> returnValue = service.findByApartadoAndPadreAllLanguages(id, idPadre);
    log.debug("getHijos(Long id, Pageable paging - end");
    if (returnValue.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  /**
   * Obtiene las entidades {@link Apartado} por el id de su padre
   * {@link Apartado}.
   * 
   * @param id     El id de la entidad {@link Apartado}.
   * @param paging pageable
   * @return el listado de entidades {@link Apartado} paginadas y filtradas.
   */
  @GetMapping("/{id}/hijos/all-languages")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-INV-EVALR', 'ETI-MEM-INV-ER')")
  ResponseEntity<Page<Apartado>> getHijosAllLanguages(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("getHijos(Long id, Pageable paging - start");
    Page<Apartado> page = service.findByPadreIdAllLanguages(id, paging);
    log.debug("getHijos(Long id, Pageable paging - end");
    if (page.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(page, HttpStatus.OK);
  }
}
