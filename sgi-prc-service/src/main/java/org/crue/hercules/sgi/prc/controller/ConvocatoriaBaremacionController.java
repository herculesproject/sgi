package org.crue.hercules.sgi.prc.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.crue.hercules.sgi.prc.dto.BaremoInput;
import org.crue.hercules.sgi.prc.dto.BaremoOutput;
import org.crue.hercules.sgi.prc.dto.ConvocatoriaBaremacionInput;
import org.crue.hercules.sgi.prc.dto.ConvocatoriaBaremacionOutput;
import org.crue.hercules.sgi.prc.exceptions.NoRelatedEntitiesException;
import org.crue.hercules.sgi.prc.model.Baremo;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.crue.hercules.sgi.prc.service.BaremoService;
import org.crue.hercules.sgi.prc.service.ConvocatoriaBaremacionService;
import org.crue.hercules.sgi.prc.validation.BaremosNoRepetido;
import org.crue.hercules.sgi.prc.validation.BaremosPesoTotal;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ConvocatoriaBaremacionController
 */
@RestController
@RequestMapping(ConvocatoriaBaremacionController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
@Validated
public class ConvocatoriaBaremacionController {

  public static final String REQUEST_MAPPING = "/convocatoriasbaremacion";
  public static final String PATH_BAREMOS = "/{id}/baremos";

  private final ConvocatoriaBaremacionService convocatoriaBaremacionService;

  private final BaremoService baremoService;

  private final ModelMapper modelMapper;

  /**
   * Devuelve una lista paginada y filtrada {@link ConvocatoriaBaremacion}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return la lista de entidades {@link ConvocatoriaBaremacion} paginadas y/o
   *         filtradas.
   */
  @GetMapping("/todos")
  @PreAuthorize("hasAnyAuthority('PRC-CON-V', 'PRC-CON-E', 'PRC-CON-B', 'PRC-CON-R')")
  ResponseEntity<Page<ConvocatoriaBaremacionOutput>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<ConvocatoriaBaremacion> page = convocatoriaBaremacionService.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  /**
   * Devuelve la {@link ConvocatoriaBaremacion} con el id indicado.
   * 
   * @param id Identificador de {@link ConvocatoriaBaremacion}.
   * @return {@link ConvocatoriaBaremacion} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('PRC-CON-V', 'PRC-CON-E')")
  ConvocatoriaBaremacionOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ConvocatoriaBaremacion returnValue = convocatoriaBaremacionService.findById(id);
    log.debug("findById(Long id) - end");
    return convert(returnValue);
  }

  /**
   * Activa la {@link ConvocatoriaBaremacion} con id indicado.
   * 
   * @param id Identificador de {@link ConvocatoriaBaremacion}.
   * @return {@link ConvocatoriaBaremacion} actualizada.
   */
  @PatchMapping("/{id}/activar")
  @PreAuthorize("hasAuthority('PRC-CON-R')")
  ConvocatoriaBaremacionOutput activar(@PathVariable Long id) {
    log.debug("activar(Long id) - start");
    ConvocatoriaBaremacion returnValue = convocatoriaBaremacionService.activar(id);
    log.debug("activar(Long id) - end");
    return convert(returnValue);
  }

  /**
   * Desactiva la {@link ConvocatoriaBaremacion} con id indicado.
   * 
   * @param id Identificador de {@link ConvocatoriaBaremacion}.
   */
  @PatchMapping("/{id}/desactivar")
  @PreAuthorize("hasAuthority('PRC-CON-B')")
  ConvocatoriaBaremacionOutput desactivar(@PathVariable Long id) {
    log.debug("desactivar(Long id) - start");
    ConvocatoriaBaremacion returnValue = convocatoriaBaremacionService.desactivar(id);
    log.debug("desactivar(Long id) - end");
    return convert(returnValue);
  }

  /**
   * Crea un nuevo {@link ConvocatoriaBaremacion}.
   * 
   * @param convocatoriaBaremacion {@link ConvocatoriaBaremacion} que se quiere
   *                               crear.
   * @return Nuevo {@link ConvocatoriaBaremacion} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthority('PRC-CON-C')")
  ResponseEntity<ConvocatoriaBaremacionOutput> create(
      @Valid @RequestBody ConvocatoriaBaremacionInput convocatoriaBaremacion) {
    log.debug("create(ConvocatoriaBaremacion convocatoriaBaremacion) - start");
    ConvocatoriaBaremacion returnValue = convocatoriaBaremacionService.create(convert(convocatoriaBaremacion));
    log.debug("create(ConvocatoriaBaremacion convocatoriaBaremacion) - end");
    return new ResponseEntity<>(convert(returnValue), HttpStatus.CREATED);
  }

  /**
   * Actualiza la {@link ConvocatoriaBaremacion} con el id indicado.
   * 
   * @param convocatoriaBaremacion {@link ConvocatoriaBaremacion} a actualizar.
   * @param id                     id {@link ConvocatoriaBaremacion} a actualizar.
   * @return {@link ConvocatoriaBaremacion} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('PRC-CON-E')")
  ConvocatoriaBaremacionOutput update(@Valid @RequestBody ConvocatoriaBaremacionInput convocatoriaBaremacion,
      @PathVariable Long id) {
    log.debug("update(ConvocatoriaBaremacion convocatoriaBaremacion, Long id) - start");
    ConvocatoriaBaremacion returnValue = convocatoriaBaremacionService.update(convert(id, convocatoriaBaremacion));
    log.debug("update(ConvocatoriaBaremacion convocatoriaBaremacion, Long id) - end");
    return convert(returnValue);
  }

  /**
   * Obtiene los años en los que hay alguna {@link ConvocatoriaBaremacion}
   * 
   * @return lista de años en los hay alguna {@link ConvocatoriaBaremacion}
   */
  @GetMapping("/anios")
  @PreAuthorize("hasAuthority('PRC-INF-G')")
  ResponseEntity<List<Integer>> findAniosWithConvocatoriasBaremacion() {
    log.debug("findAniosWithConvocatoriasBaremacion() - start");
    List<Integer> anios = convocatoriaBaremacionService.findAniosWithConvocatoriasBaremacion();
    log.debug("findAniosWithConvocatoriasBaremacion() - end");
    return anios.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(anios, HttpStatus.OK);
  }

  /**
   * Clona la convocatoria cuya fuente es la correspondiente al id pasado por el
   * path
   * 
   * @param id id de la {@link ConvocatoriaBaremacion}
   * @return Convocatoria devuelve una {@link ConvocatoriaBaremacion}
   */
  @PostMapping("/{id}/clone")
  @PreAuthorize("hasAuthorityForAnyUO('PRC-CON-C')")
  public ResponseEntity<Long> clone(@PathVariable Long id) {
    return new ResponseEntity<>(convocatoriaBaremacionService.clone(id, "Clonada - ", 1).getId(), HttpStatus.CREATED);
  }

  private ConvocatoriaBaremacion convert(ConvocatoriaBaremacionInput convocatoriaBaremacionInput) {
    return convert(null, convocatoriaBaremacionInput);
  }

  private ConvocatoriaBaremacion convert(Long id, ConvocatoriaBaremacionInput convocatoriaBaremacionInput) {
    ConvocatoriaBaremacion convocatoriaBaremacion = modelMapper.map(convocatoriaBaremacionInput,
        ConvocatoriaBaremacion.class);
    convocatoriaBaremacion.setId(id);
    return convocatoriaBaremacion;
  }

  private Page<ConvocatoriaBaremacionOutput> convert(Page<ConvocatoriaBaremacion> page) {
    List<ConvocatoriaBaremacionOutput> content = page.getContent().stream().map(this::convert)
        .collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private ConvocatoriaBaremacionOutput convert(ConvocatoriaBaremacion convocatoriaBaremacion) {
    return modelMapper.map(convocatoriaBaremacion, ConvocatoriaBaremacionOutput.class);
  }

  /* BAREMO */

  /**
   * Devuelve los {@link Baremo} asociados a la {@link ConvocatoriaBaremacion} con
   * el id indicado
   * 
   * @param id     Identificador de {@link ConvocatoriaBaremacion}
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return {@link Baremo} correspondientes al id de la
   *         {@link ConvocatoriaBaremacion}
   */
  @GetMapping(PATH_BAREMOS)
  @PreAuthorize("hasAnyAuthority('PRC-CON-V', 'PRC-CON-C', 'PRC-CON-E')")
  public Page<BaremoOutput> findBaremos(@PathVariable Long id, @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findBaremos(@PathVariable Long id, String query, Pageable paging) - start");
    Page<BaremoOutput> returnValue = convertBaremos(
        baremoService.findByConvocatoriaBaremacionId(id, query, paging));
    log.debug("findBaremos(@PathVariable Long id, String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Actualiza la lista de {@link Baremo} asociadas a la
   * {@link ConvocatoriaBaremacion} con el id indicado
   * 
   * @param id      identificador de la {@link ConvocatoriaBaremacion}
   * @param baremos nueva lista de {@link Baremo} de
   *                la {@link ConvocatoriaBaremacion}
   * @return la nueva lista de {@link Baremo} asociadas a la
   *         {@link ConvocatoriaBaremacion}
   */
  @PatchMapping(PATH_BAREMOS)
  @PreAuthorize("hasAnyAuthority('PRC-CON-C', 'PRC-CON-E')")
  public ResponseEntity<List<BaremoOutput>> updateBaremos(@PathVariable Long id,
      @RequestBody @NotEmpty @BaremosNoRepetido @BaremosPesoTotal List<@Valid BaremoInput> baremos) {
    log.debug("updateBaremos(Long id, List<BaremoInput> baremos) - start");

    convocatoriaBaremacionService.checkConvocatoriaBaremacionUpdatable(id);
    baremos.stream().forEach(baremo -> {
      if (!baremo.getConvocatoriaBaremacionId().equals(id)) {
        throw new NoRelatedEntitiesException(Baremo.class, ConvocatoriaBaremacion.class);
      }
    });

    List<BaremoOutput> returnValue = convertBaremos(
        baremoService.updateBaremos(id,
            convertBaremosInputs(id, baremos)));
    log.debug("updateBaremos(Long id, List<BaremoInput> baremos) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  private Page<BaremoOutput> convertBaremos(Page<Baremo> page) {
    List<BaremoOutput> content = page.getContent().stream()
        .map(this::convert).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private List<BaremoOutput> convertBaremos(List<Baremo> list) {
    return list.stream()
        .map(this::convert)
        .collect(Collectors.toList());
  }

  private BaremoOutput convert(Baremo entity) {
    return modelMapper.map(entity, BaremoOutput.class);
  }

  private List<Baremo> convertBaremosInputs(Long convocatoriaBaremacionId,
      List<BaremoInput> inputs) {
    return inputs.stream().map(input -> convert(convocatoriaBaremacionId, input)).collect(Collectors.toList());
  }

  private Baremo convert(Long convocatoriaBaremacionId, BaremoInput input) {
    Baremo entity = modelMapper.map(input, Baremo.class);
    entity.setConvocatoriaBaremacionId(convocatoriaBaremacionId);
    return entity;
  }
}
