package org.crue.hercules.sgi.eti.controller;

import java.util.Locale;

import org.crue.hercules.sgi.eti.dto.ApartadoOutput;
import org.crue.hercules.sgi.eti.dto.ApartadoTreeOutput;
import org.crue.hercules.sgi.eti.dto.BloqueOutput;
import org.crue.hercules.sgi.eti.model.Apartado;
import org.crue.hercules.sgi.eti.model.Bloque;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.service.ApartadoService;
import org.crue.hercules.sgi.eti.service.BloqueService;
import org.crue.hercules.sgi.eti.util.SgiLocaleHelper;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * BloqueController
 */
@RestController
@RequestMapping(BloqueController.REQUEST_MAPPING)
@Slf4j
public class BloqueController {

  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "bloques";
  public static final String PATH_COMENTARIOS_GENERALES = PATH_DELIMITER + "comentarios-generales/{lang}";

  public static final String PATH_ID = PATH_DELIMITER + "{id}";
  public static final String PATH_APARTADOS = PATH_ID + PATH_DELIMITER + "apartados/{lang}";
  public static final String PATH_APARTADOS_TREE = PATH_ID + PATH_DELIMITER + "apartados-tree/{lang}";
  public static final String PATH_FORMULARIO = PATH_DELIMITER + "{idFormulario}" + PATH_DELIMITER + "formulario/{lang}";
  public static final String PATH_APARTADOS_ALL_LANGUAGES = PATH_ID + PATH_DELIMITER + "apartados/all-languages";
  public static final String PATH_COMENTARIOS_GENERALES_ALL_LANGUAGES = PATH_DELIMITER + "comentarios-generales";

  /** Bloque service */
  private final BloqueService service;

  /** Apartado service */
  private ApartadoService apartadoService;

  /**
   * Instancia un nuevo BloqueController.
   * 
   * @param service         BloqueService
   * @param apartadoService ApartadoService
   */
  public BloqueController(BloqueService service, ApartadoService apartadoService) {
    log.debug("BloqueController(BloqueService service, ApartadoService apartadoService) - start");
    this.service = service;
    this.apartadoService = apartadoService;
    log.debug("BloqueController(BloqueService service, ApartadoService apartadoService) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Bloque}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable
   * @return el listado de entidades {@link Bloque} paginadas y filtradas.
   */
  @GetMapping()
  public ResponseEntity<Page<Bloque>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<Bloque> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve el {@link Bloque} con el id indicado.
   * 
   * @param id Identificador de {@link Bloque}.
   * @return {@link Bloque} correspondiente al id.
   */
  @GetMapping(PATH_ID)
  public BloqueOutput one(@PathVariable Long id) {
    log.debug("Bloque one(Long id) - start");
    Locale locale = LocaleContextHolder.getLocale();
    BloqueOutput returnValue = service.findByIdAndLanguage(id, SgiLocaleHelper.getLang(locale));
    log.debug("Bloque one(Long id) - end");
    return returnValue;
  }

  @GetMapping(PATH_APARTADOS)
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-INV-EVALR', 'ETI-MEM-INV-ER')")
  public ResponseEntity<Page<ApartadoOutput>> getApartados(@PathVariable Long id, @PathVariable String lang,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("getApartados(Long id, Pageable paging - start");
    Page<ApartadoOutput> page = apartadoService.findByBloqueId(id, lang, paging);
    log.debug("getApartados(Long id, Pageable paging - end");
    if (page.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve los bloques en el idioma correspondiente a través del id del
   * formulario.
   * 
   * @param idFormulario Identificador de {@link Formulario}.
   * @param paging       pageable
   * @param lang         code language
   * @return el listado de entidades {@link Bloque} paginadas.
   */
  @GetMapping(PATH_FORMULARIO)
  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-eti')) or hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-PEV-INV-VR')")
  @Deprecated
  public Page<BloqueOutput> findByFormularioId(@PathVariable Long idFormulario, @PathVariable String lang,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("Bloque findByFormularioId(Long idFormulario, Pageable paging) - start");
    Page<BloqueOutput> returnValue = service.findByFormularioId(idFormulario, lang, paging);
    log.debug("Bloque findByFormularioId(Long idFormulario, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene el {@link Bloque} de comentarios generales.
   * 
   * @param lang code language
   * @return el {@link Bloque}.
   */
  @GetMapping(PATH_COMENTARIOS_GENERALES)
  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-eti'))")
  public BloqueOutput getBloqueComentariosGenerales(@PathVariable String lang) {
    log.debug("getBloqueComentariosGenerales() - start");
    BloqueOutput returnValue = service.getBloqueComentariosGenerales(lang);
    log.debug("getBloqueComentariosGenerales() - end");
    return returnValue;
  }

  /**
   * Obtiene las entidades {@link Apartado} por el id de su {@link Bloque}.
   * 
   * @param id     El id de la entidad {@link Bloque}.
   * @param paging pageable
   * @param lang   code language
   * @return el listado de entidades {@link Apartado} paginadas.
   */
  @GetMapping(PATH_APARTADOS_TREE)
  @PreAuthorize("isClient() and hasAuthority('SCOPE_sgi-eti')")
  public ResponseEntity<Page<ApartadoTreeOutput>> getApartadosTree(@PathVariable Long id, @PathVariable String lang,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug(" getApartadosTree(@PathVariable Long id, @PathVariable String lang, Pageable paging) - start");
    Page<ApartadoTreeOutput> page = apartadoService.findApartadosTreeByBloqueId(id, lang, paging);
    log.debug(" getApartadosTree(@PathVariable Long id, @PathVariable String lang, Pageable paging) - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Obtiene las entidades {@link Apartado} por el id de su {@link Bloque}.
   * 
   * @param id     El id de la entidad {@link Bloque}.
   * @param paging pageable
   * @return el listado de entidades {@link Apartado} paginadas y filtradas.
   */
  @GetMapping(PATH_APARTADOS_ALL_LANGUAGES)
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-INV-EVALR', 'ETI-MEM-INV-ER')")
  public ResponseEntity<Page<Apartado>> getApartadosAllLanguages(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("getApartadosAllLanguages(Long id, Pageable paging - start");
    Page<Apartado> page = apartadoService.findByBloqueIdAllLanguages(id, paging);
    log.debug("getApartadosAllLanguages(Long id, Pageable paging - end");
    if (page.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Obtiene el {@link Bloque} de comentarios generales.
   *
   * @return el {@link Bloque}.
   */
  @GetMapping(PATH_COMENTARIOS_GENERALES_ALL_LANGUAGES)
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-PEV-INV-C', 'ETI-PEV-INV-ER')")
  public Bloque getBloqueComentariosGeneralesAllLanguages() {
    log.debug("getBloqueComentariosGeneralesAllLanguages() - start");
    Bloque returnValue = service.getBloqueComentariosGeneralesAllLanguages();
    log.debug("getBloqueComentariosGeneralesAllLanguages() - end");
    return returnValue;
  }

}
