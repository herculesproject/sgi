package org.crue.hercules.sgi.prc.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.crue.hercules.sgi.prc.dto.ConvocatoriaBaremacionOutput;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.crue.hercules.sgi.prc.service.ConvocatoriaBaremacionService;
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
public class ConvocatoriaBaremacionController {

  public static final String REQUEST_MAPPING = "/convocatoriabaremacion";

  private final ConvocatoriaBaremacionService convocatoriaBaremacionService;

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
    log.debug("findAllTodos(String query, Pageable paging) - start");
    Page<ConvocatoriaBaremacion> page = convocatoriaBaremacionService.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllTodos(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllTodos(String query, Pageable paging) - end");
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

  private Page<ConvocatoriaBaremacionOutput> convert(Page<ConvocatoriaBaremacion> page) {
    List<ConvocatoriaBaremacionOutput> content = page.getContent().stream().map(this::convert)
        .collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private ConvocatoriaBaremacionOutput convert(ConvocatoriaBaremacion convocatoriaBaremacion) {
    return modelMapper.map(convocatoriaBaremacion, ConvocatoriaBaremacionOutput.class);
  }
}
