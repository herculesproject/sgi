package org.crue.hercules.sgi.csp.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.ProyectoSocioPeriodoJustificacionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SocioPeriodoJustificacionDocumentoNotFoundException;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.SocioPeriodoJustificacionDocumento;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.SocioPeriodoJustificacionDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.specification.SocioPeriodoJustificacionDocumentoSpecifications;
import org.crue.hercules.sgi.csp.service.SocioPeriodoJustificacionDocumentoService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de
 * {@link SocioPeriodoJustificacionDocumento}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class SocioPeriodoJustificacionDocumentoServiceImpl implements SocioPeriodoJustificacionDocumentoService {

  private final SocioPeriodoJustificacionDocumentoRepository repository;
  private final ProyectoSocioPeriodoJustificacionRepository proyectoSocioRepository;

  /**
   * {@link SocioPeriodoJustificacionDocumentoServiceImpl}.
   * 
   * @param proyectoSocioPeriodoJustificacionRepository {@link SocioPeriodoJustificacionDocumentoRepository}.
   * @param proyectoSocioRepository                     {@link ProyectoSocioPeriodoJustificacionRepository}.
   */
  public SocioPeriodoJustificacionDocumentoServiceImpl(
      SocioPeriodoJustificacionDocumentoRepository proyectoSocioPeriodoJustificacionRepository,
      ProyectoSocioPeriodoJustificacionRepository proyectoSocioRepository) {
    this.repository = proyectoSocioPeriodoJustificacionRepository;
    this.proyectoSocioRepository = proyectoSocioRepository;
  }

  /**
   * Actualiza el listado de {@link SocioPeriodoJustificacionDocumento} de la
   * {@link ProyectoSocioPeriodoJustificacion} con el listado
   * proyectoSocioPeriodoJustificaciones añadiendo, editando o eliminando los
   * elementos segun proceda.
   *
   * @param proyectoSocioId                     Id de la
   *                                            {@link ProyectoSocioPeriodoJustificacion}.
   * @param proyectoSocioPeriodoJustificaciones lista con los nuevos
   *                                            {@link SocioPeriodoJustificacionDocumento}
   *                                            a guardar.
   * @return la entidad {@link SocioPeriodoJustificacionDocumento} persistida.
   */
  @Override
  @Transactional
  public List<SocioPeriodoJustificacionDocumento> update(Long proyectoSocioId,
      List<SocioPeriodoJustificacionDocumento> proyectoSocioPeriodoJustificaciones) {
    log.debug(
        "update(Long proyectoSocioId, List<SocioPeriodoJustificacionDocumento> proyectoSocioPeriodoJustificaciones) - start");

    ProyectoSocioPeriodoJustificacion proyectoSocio = proyectoSocioRepository.findById(proyectoSocioId)
        .orElseThrow(() -> new ProyectoSocioPeriodoJustificacionNotFoundException(proyectoSocioId));

    List<SocioPeriodoJustificacionDocumento> proyectoSocioPeriodoJustificacionesBD = repository
        .findAllByProyectoSocioPeriodoJustificacionId(proyectoSocioId);

    // Periodos eliminados
    List<SocioPeriodoJustificacionDocumento> periodoJustificacionesEliminar = proyectoSocioPeriodoJustificacionesBD
        .stream().filter(periodo -> !proyectoSocioPeriodoJustificaciones.stream()
            .map(SocioPeriodoJustificacionDocumento::getId).anyMatch(id -> id == periodo.getId()))
        .collect(Collectors.toList());

    if (!periodoJustificacionesEliminar.isEmpty()) {
      repository.deleteAll(periodoJustificacionesEliminar);
    }

    for (SocioPeriodoJustificacionDocumento socioPeriodoJustificacionDocumento : proyectoSocioPeriodoJustificaciones) {

      // Si tiene id se valida que exista y que tenga el proyecto socio de la que se
      // estan actualizando los periodos
      if (socioPeriodoJustificacionDocumento.getId() != null) {
        SocioPeriodoJustificacionDocumento socioPeriodoJustificacionDocumentoBD = proyectoSocioPeriodoJustificacionesBD
            .stream().filter(periodo -> periodo.getId() == socioPeriodoJustificacionDocumento.getId()).findFirst()
            .orElseThrow(() -> new SocioPeriodoJustificacionDocumentoNotFoundException(
                socioPeriodoJustificacionDocumento.getId()));

        Assert.isTrue(
            socioPeriodoJustificacionDocumentoBD.getProyectoSocioPeriodoJustificacion()
                .getId() == socioPeriodoJustificacionDocumento.getProyectoSocioPeriodoJustificacion().getId(),
            "No se puede modificar el proyecto socio del SocioPeriodoJustificacionDocumento");
      }

      // Setea el proyecto socio recuperado del proyectoSocioId
      socioPeriodoJustificacionDocumento.setProyectoSocioPeriodoJustificacion(proyectoSocio);

    }

    List<SocioPeriodoJustificacionDocumento> returnValue = repository.saveAll(proyectoSocioPeriodoJustificaciones);
    log.debug(
        "update(Long proyectoSocioId, List<SocioPeriodoJustificacionDocumento> proyectoSocioPeriodoJustificaciones) - end");

    return returnValue;
  }

  /**
   * Obtiene {@link SocioPeriodoJustificacionDocumento} por su id.
   *
   * @param id el id de la entidad {@link SocioPeriodoJustificacionDocumento}.
   * @return la entidad {@link SocioPeriodoJustificacionDocumento}.
   */
  @Override
  public SocioPeriodoJustificacionDocumento findById(Long id) {
    log.debug("findById(Long id)  - start");
    final SocioPeriodoJustificacionDocumento returnValue = repository.findById(id)
        .orElseThrow(() -> new SocioPeriodoJustificacionDocumentoNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link SocioPeriodoJustificacionDocumento} para una
   * {@link ProyectoSocioPeriodoJustificacion}.
   *
   * @param proyectoSocioId el id de la {@link ProyectoSocioPeriodoJustificacion}.
   * @param query           la información del filtro.
   * @param pageable        la información de la paginación.
   * @return la lista de entidades {@link SocioPeriodoJustificacionDocumento} de
   *         la {@link ProyectoSocioPeriodoJustificacion} paginadas.
   */
  public Page<SocioPeriodoJustificacionDocumento> findAllByProyectoSocioPeriodoJustificacion(Long proyectoSocioId,
      String query, Pageable pageable) {
    log.debug(
        "findAllByProyectoSocioPeriodoJustificacion(Long proyectoSocioId, String query, Pageable pageable) - start");
    Specification<SocioPeriodoJustificacionDocumento> specs = SocioPeriodoJustificacionDocumentoSpecifications
        .byProyectoSocioPeriodoJustificacionId(proyectoSocioId).and(SgiRSQLJPASupport.toSpecification(query));

    Page<SocioPeriodoJustificacionDocumento> returnValue = repository.findAll(specs, pageable);
    log.debug(
        "findAllByProyectoSocioPeriodoJustificacion(Long proyectoSocioId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link SocioPeriodoJustificacionDocumento} para una
   * {@link Proyecto}.
   *
   * @param idProyecto el id de la {@link Proyecto}.
   * @return la lista de entidades {@link SocioPeriodoJustificacionDocumento} de
   *         la {@link Proyecto} paginadas.
   */
  @Override
  public List<SocioPeriodoJustificacionDocumento> findAllByProyecto(Long idProyecto) {
    log.debug("findAllByProyecto(Long idProyecto) - start");

    Specification<SocioPeriodoJustificacionDocumento> specByProyecto = SocioPeriodoJustificacionDocumentoSpecifications
        .byProyectoSocioPeriodoJustificacionId(idProyecto);

    Specification<SocioPeriodoJustificacionDocumento> specs = Specification.where(specByProyecto);

    List<SocioPeriodoJustificacionDocumento> returnValue = repository.findAll(specs);
    log.debug("findAllByProyecto(Long idProyecto) - end");
    return returnValue;
  }

}
