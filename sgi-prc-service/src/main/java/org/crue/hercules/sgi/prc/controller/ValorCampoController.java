package org.crue.hercules.sgi.prc.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.crue.hercules.sgi.prc.dto.ValorCampoOutput;
import org.crue.hercules.sgi.prc.model.ValorCampo;
import org.crue.hercules.sgi.prc.service.ValorCampoService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ValorCampoController
 */
@RestController
@RequestMapping(ValorCampoController.MAPPING)
@Slf4j
public class ValorCampoController {
  public static final String MAPPING = "/valores-campos";

  private ModelMapper modelMapper;

  /** ValorCampo service */
  private final ValorCampoService service;

  /**
   * Instancia un nuevo ValorCampoController.
   * 
   * @param modelMapper {@link ModelMapper}
   * @param service     {@link ValorCampoService}
   */
  public ValorCampoController(ModelMapper modelMapper, ValorCampoService service) {
    this.modelMapper = modelMapper;
    this.service = service;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link ValorCampo}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link ValorCampo}
   *         paginadas y filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthority('PRC-VAL-V', 'PRC-VAL-E')")
  public ResponseEntity<Page<ValorCampoOutput>> findAll(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<ValorCampo> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(convert(page), HttpStatus.OK);
  }

  private Page<ValorCampoOutput> convert(Page<ValorCampo> page) {
    List<ValorCampoOutput> content = page.getContent().stream()
        .map(valorCampo -> convert(valorCampo)).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private ValorCampoOutput convert(ValorCampo valorCampo) {
    return modelMapper.map(valorCampo, ValorCampoOutput.class);
  }
}
