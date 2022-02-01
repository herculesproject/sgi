package org.crue.hercules.sgi.prc.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaOutput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaOutput.EstadoProduccionCientifica;
import org.crue.hercules.sgi.prc.dto.PublicacionOutput;
import org.crue.hercules.sgi.prc.dto.PublicacionResumen;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.service.ProduccionCientificaService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
 * ProduccionCientificaController
 */
@RestController
@RequestMapping(ProduccionCientificaController.MAPPING)
@Slf4j
public class ProduccionCientificaController {
  public static final String MAPPING = "/producciones-cientificas";

  private ModelMapper modelMapper;

  /** ProduccionCientifica service */
  private final ProduccionCientificaService service;

  /**
   * Instancia un nuevo ProduccionCientificaController.
   * 
   * @param modelMapper {@link ModelMapper}
   * @param service     {@link ProduccionCientificaService}
   */
  public ProduccionCientificaController(ModelMapper modelMapper, ProduccionCientificaService service) {
    this.modelMapper = modelMapper;
    this.service = service;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link PublicacionOutput}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link PublicacionOutput} paginadas y
   *         filtradas.
   */
  @GetMapping("/publicaciones")
  // TODO poner permisos necesarios en todos los métodos
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Page<PublicacionOutput>> findAllPublicaciones(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllPublicaciones(String query, Pageable paging) - start");
    Page<PublicacionResumen> page = service.findAllPublicaciones(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllPublicaciones(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllPublicaciones(String query, Pageable paging) - end");
    return new ResponseEntity<>(convertPublicacionResumen(page), HttpStatus.OK);
  }

  /**
   * Devuelve el {@link ProduccionCientificaOutput} con el id indicado.
   * 
   * @param id Identificador de {@link ProduccionCientifica}.
   * @return {@link ProduccionCientificaOutput} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public ProduccionCientificaOutput findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ProduccionCientifica returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return convert(returnValue);
  }

  private ProduccionCientificaOutput convert(ProduccionCientifica produccionCientifica) {
    return modelMapper.map(produccionCientifica, ProduccionCientificaOutput.class);
  }

  private PublicacionOutput convertPublicacionResumen(PublicacionResumen publicacionResumen) {
    PublicacionOutput output = modelMapper.map(publicacionResumen, PublicacionOutput.class);
    output.setEstado(new EstadoProduccionCientifica());
    output.getEstado().setEstado(publicacionResumen.getEstado());
    return output;
  }

  private Page<PublicacionOutput> convertPublicacionResumen(Page<PublicacionResumen> page) {
    List<PublicacionOutput> content = page.getContent().stream()
        .map(produccionCientifica -> convertPublicacionResumen(produccionCientifica)).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }
}