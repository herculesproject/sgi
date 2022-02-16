package org.crue.hercules.sgi.csp.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.dto.GrupoEquipoOutput;
import org.crue.hercules.sgi.csp.dto.GrupoInput;
import org.crue.hercules.sgi.csp.dto.GrupoOutput;
import org.crue.hercules.sgi.csp.model.FuenteFinanciacion;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoEquipo;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.service.GrupoEquipoService;
import org.crue.hercules.sgi.csp.service.GrupoService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * GrupoController
 */
@RestController
@RequestMapping(GrupoController.REQUEST_MAPPING)
@Slf4j
public class GrupoController {

  public static final String REQUEST_MAPPING = "/grupos";
  public static final String PATH_ID = "/{id}";
  public static final String PATH_ACTIVAR = PATH_ID + "/activar";
  public static final String PATH_DESACTIVAR = PATH_ID + "/desactivar";
  public static final String PATH_GRUPO_EQUIPO = PATH_ID + "/miembrosequipo";
  public static final String PATH_INVESTIGADORES_PRINCIPALES = PATH_ID + "/investigadoresprincipales";

  private ModelMapper modelMapper;

  private final GrupoService service;
  private final GrupoEquipoService grupoEquipoService;

  public GrupoController(ModelMapper modelMapper, GrupoService service, GrupoEquipoService grupoEquipoService) {
    this.modelMapper = modelMapper;
    this.service = service;
    this.grupoEquipoService = grupoEquipoService;
  }

  /**
   * Crea nuevo {@link Grupo}
   * 
   * @param grupo {@link Grupo} que se quiere crear.
   * @return Nuevo {@link Grupo} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-GIN-C')")
  public ResponseEntity<GrupoOutput> create(@Valid @RequestBody GrupoInput grupo) {
    log.debug("create(GrupoInput grupo) - start");
    GrupoOutput returnValue = convert(service.create(convert(grupo)));
    log.debug("create(GrupoInput grupo) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link Grupo}.
   * 
   * @param grupo {@link Grupo} a actualizar.
   * @param id    Identificador {@link Grupo} a actualizar.
   * @return {@link Grupo} actualizado
   */
  @PutMapping(PATH_ID)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-GIN-E')")
  public GrupoOutput update(@Valid @RequestBody GrupoInput grupo, @PathVariable Long id) {
    log.debug("update(GrupoInput grupo, Long id) - start");
    GrupoOutput returnValue = convert(service.update(convert(id, grupo)));
    log.debug("update(GrupoInput grupo, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link Grupo} con el id indicado.
   * 
   * @param id Identificador de {@link Grupo}.
   * @return {@link Grupo} correspondiente al id
   */
  @GetMapping(PATH_ID)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-E', 'CSP-GIN-V')")
  public GrupoOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    GrupoOutput returnValue = convert(service.findById(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Comprueba la existencia del {@link Grupo} con el id indicado.
   *
   * @param id Identificador de {@link Grupo}.
   * @return {@link HttpStatus#OK} si existe y {@link HttpStatus#NO_CONTENT} si
   *         no.
   */
  @RequestMapping(path = PATH_ID, method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-E', 'CSP-GIN-V')")
  public ResponseEntity<Void> exists(@PathVariable Long id) {
    log.debug("Convocatoria exists(Long id) - start");
    boolean exists = service.existsById(id);
    log.debug("Convocatoria exists(Long id) - end");
    return exists ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Grupo}.
   *
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link Grupo} paginadas y
   *         filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-B', 'CSP-GIN-E', 'CSP-GIN-R', 'CSP-GIN-V')")
  public ResponseEntity<Page<GrupoOutput>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<GrupoOutput> page = convert(service.findAll(query, paging));
    log.debug("findAll(String query, Pageable paging) - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Activa la {@link FuenteFinanciacion} con id indicado.
   * 
   * @param id Identificador de {@link FuenteFinanciacion}.
   * @return {@link FuenteFinanciacion} actualizado.
   */
  @PatchMapping(PATH_ACTIVAR)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-GIN-R')")
  public GrupoOutput activar(@PathVariable Long id) {
    log.debug("activar(Long id) - start");
    GrupoOutput returnValue = convert(service.activar(id));
    log.debug("activar(Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link FuenteFinanciacion} con id indicado.
   * 
   * @param id Identificador de {@link FuenteFinanciacion}.
   * @return {@link FuenteFinanciacion} desactivada.
   */
  @PatchMapping(PATH_DESACTIVAR)
  @PreAuthorize("hasAuthority('CSP-GIN-B')")
  public GrupoOutput desactivar(@PathVariable Long id) {
    log.debug("desactivar(Long id) - start");
    GrupoOutput returnValue = convert(service.desactivar(id));
    log.debug("desactivar(Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada de {@link GrupoEquipo} del
   * {@link Grupo}.
   * 
   * @param id     Identificador del {@link GrupoEquipo}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link ProyectoEquipo} paginadas y
   *         filtradas del {@link Grupo}.
   */
  @GetMapping(PATH_GRUPO_EQUIPO)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-V', 'CSP-GIN-E')")
  public ResponseEntity<Page<GrupoEquipoOutput>> findAllGrupoEquipo(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllGrupoEquipo(Long id, String query, Pageable paging) - start");
    Page<GrupoEquipoOutput> page = convertMiembrosEquipo(grupoEquipoService.findAllByGrupo(id, query, paging));
    log.debug("findAllGrupoEquipo(Long id, String query, Pageable paging) - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada DE investigadores principales del
   * {@link Grupo} en el momento actual.
   *
   * Se considera investiador principal al {@link GrupoEquipo} que a fecha actual
   * tiene el {@link RolProyecto} con el flag "principal" a
   * <code>true</code>. En caso de existir mas de un {@link GrupoEquipo}, se
   * recupera el que tenga el mayor porcentaje de dedicación al grupo (campo
   * "participación").
   * Y en caso de que varios coincidan se devuelven todos los que coincidan.
   * 
   * @param id     Identificador del {@link GrupoEquipo}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link GrupoEquipo} paginadas y
   *         filtradas del {@link Grupo}.
   */
  @GetMapping(PATH_INVESTIGADORES_PRINCIPALES)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-V', 'CSP-GIN-E')")
  public ResponseEntity<Page<GrupoEquipoOutput>> findInvestigadoresPrincipales(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findInvestigadoresPrincipales(Long id, String query, Pageable paging) - start");
    Page<GrupoEquipoOutput> page = convertMiembrosEquipo(
        grupoEquipoService.findInvestigadoresPrincipales(id, query, paging));
    log.debug("findInvestigadoresPrincipales(Long id, String query, Pageable paging) - end");
    return page.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(page, HttpStatus.OK);
  }

  private GrupoOutput convert(Grupo grupo) {
    return modelMapper.map(grupo, GrupoOutput.class);
  }

  private Grupo convert(GrupoInput grupoInput) {
    return convert(null, grupoInput);
  }

  private Grupo convert(Long id, GrupoInput grupoInput) {
    Grupo grupo = modelMapper.map(grupoInput, Grupo.class);
    grupo.setId(id);
    return grupo;
  }

  private Page<GrupoOutput> convert(Page<Grupo> page) {
    List<GrupoOutput> content = page.getContent().stream()
        .map(this::convert).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private GrupoEquipoOutput convert(GrupoEquipo grupoEquipo) {
    return modelMapper.map(grupoEquipo, GrupoEquipoOutput.class);
  }

  private Page<GrupoEquipoOutput> convertMiembrosEquipo(Page<GrupoEquipo> page) {
    List<GrupoEquipoOutput> content = page.getContent().stream()
        .map(this::convert).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

}
