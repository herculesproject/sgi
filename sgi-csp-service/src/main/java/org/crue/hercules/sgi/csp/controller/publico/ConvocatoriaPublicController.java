package org.crue.hercules.sgi.csp.controller.publico;

import org.crue.hercules.sgi.csp.converter.ConvocatoriaFaseConverter;
import org.crue.hercules.sgi.csp.dto.ConvocatoriaFaseOutput;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEntidadConvocanteService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEntidadFinanciadoraService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaFaseService;
import org.crue.hercules.sgi.csp.service.ConvocatoriaService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ConvocatoriaController
 */
@RestController
@RequestMapping(ConvocatoriaPublicController.REQUEST_MAPPING)
@Slf4j
public class ConvocatoriaPublicController {

  public static final String PATH_DELIMITER = "/";
  public static final String PATH_PUBLIC = PATH_DELIMITER + "public";
  public static final String REQUEST_MAPPING = PATH_PUBLIC + PATH_DELIMITER + "convocatorias";

  public static final String PATH_INVESTIGADOR = PATH_DELIMITER + "investigador";

  public static final String PATH_ID = PATH_DELIMITER + "{id}";

  public static final String PATH_ENTIDADES_CONVOCANTES = PATH_ID + PATH_DELIMITER + "convocatoriaentidadconvocantes";
  public static final String PATH_ENTIDADES_FINANCIADORAS = PATH_ID + PATH_DELIMITER
      + "convocatoriaentidadfinanciadoras";
  public static final String PATH_FASES = PATH_ID + PATH_DELIMITER + "convocatoriafases";
  public static final String PATH_TRAMITABLE = PATH_ID + PATH_DELIMITER + "tramitable";

  private final ConvocatoriaService service;
  private final ConvocatoriaEntidadFinanciadoraService convocatoriaEntidadFinanciadoraService;
  private final ConvocatoriaFaseService convocatoriaFaseService;
  private final ConvocatoriaEntidadConvocanteService convocatoriaEntidadConvocanteService;
  private final ConvocatoriaFaseConverter convocatoriaFaseConverter;

  /**
   * Instancia un nuevo ConvocatoriaController.
   *
   * @param convocatoriaService                    {@link ConvocatoriaService}.
   * @param convocatoriaEntidadConvocanteService   {@link ConvocatoriaEntidadConvocanteService}.
   * @param convocatoriaEntidadFinanciadoraService {@link ConvocatoriaEntidadFinanciadoraService}.
   * @param convocatoriaFaseService                {@link ConvocatoriaFaseService}.
   * @param convocatoriaFaseConverter              {@link ConvocatoriaFaseConverter}.
   */
  public ConvocatoriaPublicController(ConvocatoriaService convocatoriaService,
      ConvocatoriaEntidadConvocanteService convocatoriaEntidadConvocanteService,
      ConvocatoriaEntidadFinanciadoraService convocatoriaEntidadFinanciadoraService,
      ConvocatoriaFaseService convocatoriaFaseService,
      ConvocatoriaFaseConverter convocatoriaFaseConverter) {
    this.service = convocatoriaService;
    this.convocatoriaEntidadConvocanteService = convocatoriaEntidadConvocanteService;
    this.convocatoriaEntidadFinanciadoraService = convocatoriaEntidadFinanciadoraService;
    this.convocatoriaFaseService = convocatoriaFaseService;
    this.convocatoriaFaseConverter = convocatoriaFaseConverter;
  }

  /**
   * Comprueba la existencia del {@link Convocatoria} con el id indicado.
   *
   * @param id Identificador de {@link Convocatoria}.
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = PATH_ID, method = RequestMethod.HEAD)
  public ResponseEntity<Void> exists(@PathVariable Long id) {
    log.debug("exists(Long id) - start");
    if (service.existsById(id)) {
      log.debug("exists(Long id) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("exists(Long id) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Convocatoria} activas.
   *
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link Convocatoria} activas paginadas y
   *         filtradas.
   */
  @GetMapping(PATH_INVESTIGADOR)
  public ResponseEntity<Page<Convocatoria>> findAllPublicas(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllPublicas(String query,Pageable paging) - start");
    Page<Convocatoria> page = service.findAllPublicas(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllPublicas(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAllPublicas(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada de
   * {@link ConvocatoriaEntidadFinanciadora} de la {@link Convocatoria}.
   *
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link ConvocatoriaEntidadFinanciadora}
   *         paginadas y filtradas de la {@link Convocatoria}.
   */
  @GetMapping(PATH_ENTIDADES_FINANCIADORAS)
  public ResponseEntity<Page<ConvocatoriaEntidadFinanciadora>> findAllConvocatoriaEntidadFinanciadora(
      @PathVariable Long id, @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaEntidadFinanciadora(Long id, String query, Pageable paging) - start");
    Page<ConvocatoriaEntidadFinanciadora> page = convocatoriaEntidadFinanciadoraService.findAllByConvocatoria(id, query,
        paging);

    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaEntidadFinanciadora(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaEntidadFinanciadora(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);

  }

  /**
   * Devuelve una lista paginada y filtrada de {@link ConvocatoriaFase} de la
   * {@link Convocatoria}.
   *
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link ConvocatoriaFase}
   *         paginadas y filtradas de la {@link Convocatoria}.
   */
  @GetMapping(PATH_FASES)
  public ResponseEntity<Page<ConvocatoriaFaseOutput>> findAllConvocatoriaFases(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaFase(Long id, String query, Pageable paging) - start");
    Page<ConvocatoriaFaseOutput> page = this.convocatoriaFaseConverter
        .convert(convocatoriaFaseService.findAllByConvocatoria(id, query, paging));

    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaFase(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaFase(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada de
   * {@link ConvocatoriaEntidadConvocante} de la {@link Convocatoria}.
   *
   * @param id     Identificador de {@link Convocatoria}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link ConvocatoriaEntidadConvocante}
   *         paginadas y filtradas de la {@link Convocatoria}.
   */
  @GetMapping(PATH_ENTIDADES_CONVOCANTES)
  public ResponseEntity<Page<ConvocatoriaEntidadConvocante>> findAllConvocatoriaEntidadConvocantes(
      @PathVariable Long id, @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllConvocatoriaEntidadConvocantes(Long id, String query, Pageable paging) - start");
    Page<ConvocatoriaEntidadConvocante> page = convocatoriaEntidadConvocanteService.findAllByConvocatoria(id, query,
        paging);

    if (page.isEmpty()) {
      log.debug("findAllConvocatoriaEntidadConvocantes(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllConvocatoriaEntidadConvocantes(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Hace las comprobaciones necesarias para determinar si la {@link Convocatoria}
   * puede tramitarse.
   *
   * @param id Id del {@link Convocatoria}.
   * @return HTTP-200 si puede ser tramitada / HTTP-204 si no puede ser tramitada
   */
  @RequestMapping(path = PATH_TRAMITABLE, method = RequestMethod.HEAD)
  public ResponseEntity<Void> tramitable(@PathVariable Long id) {
    log.debug("registrable(Long id) - start");
    boolean returnValue = service.tramitable(id);
    log.debug("registrable(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
