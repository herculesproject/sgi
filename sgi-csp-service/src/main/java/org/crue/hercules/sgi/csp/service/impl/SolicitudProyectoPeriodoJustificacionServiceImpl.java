package org.crue.hercules.sgi.csp.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoPeriodoJustificacionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoSocioNotFoundException;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioRepository;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudProyectoPeriodoJustificacionSpecifications;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoPeriodoJustificacionService;
import org.crue.hercules.sgi.csp.service.SolicitudService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion
 * {@link SolicitudProyectoPeriodoJustificacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class SolicitudProyectoPeriodoJustificacionServiceImpl implements SolicitudProyectoPeriodoJustificacionService {

  private final SolicitudProyectoPeriodoJustificacionRepository repository;

  private final SolicitudProyectoSocioRepository solicitudProyectoSocioRepository;

  private final SolicitudService solicitudService;

  public SolicitudProyectoPeriodoJustificacionServiceImpl(SolicitudProyectoPeriodoJustificacionRepository repository,
      SolicitudProyectoSocioRepository solicitudProyectoSocioRepository, SolicitudService solicitudService) {
    this.repository = repository;
    this.solicitudProyectoSocioRepository = solicitudProyectoSocioRepository;
    this.solicitudService = solicitudService;
  }

  /**
   * Actualiza los datos del {@link SolicitudProyectoPeriodoJustificacion}.
   * 
   * @param solicitudProyectoSocioId        Id de la
   *                                        {@link SolicitudProyectoSocio}.
   * @param solicitudPeriodoJustificaciones lista con los nuevos
   *                                        {@link SolicitudProyectoPeriodoJustificacion}
   *                                        a guardar.
   * @return {@link SolicitudProyectoPeriodoJustificacion} actualizado.
   */
  @Override
  @Transactional
  public List<SolicitudProyectoPeriodoJustificacion> update(Long solicitudProyectoSocioId,
      List<SolicitudProyectoPeriodoJustificacion> solicitudPeriodoJustificaciones) {

    log.debug(
        "update(Long solicitudProyectoSocioId, List<SolicitudProyectoPeriodoJustificacion> solicitudPeriodoJustificaciones) - start");

    SolicitudProyectoSocio solicitudProyectoSocio = solicitudProyectoSocioRepository.findById(solicitudProyectoSocioId)
        .orElseThrow(() -> new SolicitudProyectoSocioNotFoundException(solicitudProyectoSocioId));

    // comprobar si la solicitud es modificable
    Assert.isTrue(
        solicitudService.modificable(solicitudProyectoSocio.getSolicitudProyectoDatos().getSolicitud().getId()),
        "No se puede modificar SolicitudProyectoPeriodoJustificacion");

    List<SolicitudProyectoPeriodoJustificacion> solicitudProyectoPeriodoJustificacionesBD = repository
        .findAllBySolicitudProyectoSocioId(solicitudProyectoSocioId);

    // Periodos eliminados
    List<SolicitudProyectoPeriodoJustificacion> periodoJustificacionesEliminar = solicitudProyectoPeriodoJustificacionesBD
        .stream().filter(periodo -> !solicitudPeriodoJustificaciones.stream()
            .map(SolicitudProyectoPeriodoJustificacion::getId).anyMatch(id -> id == periodo.getId()))
        .collect(Collectors.toList());

    if (!periodoJustificacionesEliminar.isEmpty()) {
      repository.deleteAll(periodoJustificacionesEliminar);
    }

    if (solicitudPeriodoJustificaciones.isEmpty()) {
      return new ArrayList<>();
    }

    // Ordena los periodos por mesInicial
    solicitudPeriodoJustificaciones.sort(Comparator.comparing(SolicitudProyectoPeriodoJustificacion::getMesInicial));

    AtomicInteger numPeriodo = new AtomicInteger(0);

    SolicitudProyectoPeriodoJustificacion periodoJustificacionAnterior = null;
    for (SolicitudProyectoPeriodoJustificacion periodoJustificacion : solicitudPeriodoJustificaciones) {
      // Actualiza el numero de periodo
      periodoJustificacion.setNumPeriodo(numPeriodo.incrementAndGet());

      // Si tiene id se valida que exista y que tenga la solicitud proyecto socio de
      // la que se
      // estan actualizando los periodos
      if (periodoJustificacion.getId() != null) {
        SolicitudProyectoPeriodoJustificacion periodoJustificacionBD = solicitudProyectoPeriodoJustificacionesBD
            .stream().filter(periodo -> periodo.getId() == periodoJustificacion.getId()).findFirst().orElseThrow(
                () -> new SolicitudProyectoPeriodoJustificacionNotFoundException(periodoJustificacion.getId()));

        Assert.isTrue(
            periodoJustificacionBD.getSolicitudProyectoSocio().getId() == periodoJustificacion
                .getSolicitudProyectoSocio().getId(),
            "No se puede modificar la solicitud proyecto socio del SolicitudProyectoPeriodoJustificacion");
      }

      // Setea la convocatoria recuperada del convocatoriaId
      periodoJustificacion.setSolicitudProyectoSocio(solicitudProyectoSocio);

      // Validaciones
      Assert.isTrue(periodoJustificacion.getMesInicial() < periodoJustificacion.getMesFinal(),
          "El mes final tiene que ser posterior al mes inicial");

      if (periodoJustificacion.getFechaInicio() != null && periodoJustificacion.getFechaFin() != null) {
        Assert.isTrue(periodoJustificacion.getFechaInicio().isBefore(periodoJustificacion.getFechaFin()),
            "La fecha de fin tiene que ser posterior a la fecha de inicio");
      }

      Assert.isTrue(
          periodoJustificacion.getSolicitudProyectoSocio().getSolicitudProyectoDatos().getDuracion() == null
              || periodoJustificacion.getMesFinal() <= periodoJustificacion.getSolicitudProyectoSocio()
                  .getSolicitudProyectoDatos().getDuracion(),
          "El mes final no puede ser superior a la duración en meses indicada en la solicitud proyecto datos");

      Assert.isTrue(
          periodoJustificacionAnterior == null || (periodoJustificacionAnterior != null
              && periodoJustificacionAnterior.getMesFinal() < periodoJustificacion.getMesInicial()),
          "El periodo se solapa con otro existente");

      periodoJustificacionAnterior = periodoJustificacion;
    }

    List<SolicitudProyectoPeriodoJustificacion> returnValue = repository.saveAll(solicitudPeriodoJustificaciones);

    log.debug(
        "update(Long solicitudProyectoSocioId,  List<SolicitudProyectoPeriodoJustificacion> solicitudPeriodoJustificaciones) - end");

    return returnValue;

  }

  /**
   * Comprueba la existencia del {@link SolicitudProyectoPeriodoJustificacion} por
   * id.
   *
   * @param id el id de la entidad {@link SolicitudProyectoPeriodoJustificacion}.
   * @return true si existe y false en caso contrario.
   */
  @Override
  public boolean existsById(final Long id) {
    log.debug("existsById(final Long id)  - start", id);
    final boolean existe = repository.existsById(id);
    log.debug("existsById(final Long id)  - end", id);
    return existe;
  }

  /**
   * Obtiene una entidad {@link SolicitudProyectoPeriodoJustificacion} por id.
   * 
   * @param id Identificador de la entidad
   *           {@link SolicitudProyectoPeriodoJustificacion}.
   * @return SolicitudProyectoPeriodoJustificacion la entidad
   *         {@link SolicitudProyectoPeriodoJustificacion}.
   */
  @Override
  public SolicitudProyectoPeriodoJustificacion findById(Long id) {
    log.debug("findById(Long id) - start");
    final SolicitudProyectoPeriodoJustificacion returnValue = repository.findById(id)
        .orElseThrow(() -> new SolicitudProyectoPeriodoJustificacionNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina la {@link SolicitudProyectoPeriodoJustificacion}.
   *
   * @param id Id del {@link SolicitudProyectoPeriodoJustificacion}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id,
        "SolicitudProyectoPeriodoJustificacion id no puede ser null para eliminar un SolicitudProyectoPeriodoJustificacion");
    if (!repository.existsById(id)) {
      throw new SolicitudProyectoPeriodoJustificacionNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");

  }

  /**
   * Obtiene las {@link SolicitudProyectoPeriodoJustificacion} para un
   * {@link Solicitud}.
   *
   * @param solicitudDatosProyectoSocioId el id del
   *                                      {@link SolicitudProyectoSocio}.
   * @param query                         la información del filtro.
   * @param paging                        la información de la paginación.
   * @return la lista de entidades {@link SolicitudProyectoPeriodoJustificacion}
   *         del {@link SolicitudProyectoSocio} paginadas.
   */
  @Override
  public Page<SolicitudProyectoPeriodoJustificacion> findAllBySolicitudProyectoSocio(Long solicitudDatosProyectoSocioId,
      String query, Pageable paging) {
    log.debug(
        "findAllBySolicitudProyectoSocio(Long solicitudDatosProyectoSocioId, String query, Pageable paging) - start");

    Specification<SolicitudProyectoPeriodoJustificacion> specs = SolicitudProyectoPeriodoJustificacionSpecifications
        .bySolicitudId(solicitudDatosProyectoSocioId).and(SgiRSQLJPASupport.toSpecification(query));

    Page<SolicitudProyectoPeriodoJustificacion> returnValue = repository.findAll(specs, paging);
    log.debug(
        "findAllBySolicitudProyectoSocio(Long solicitudDatosProyectoSocioId, String query, Pageable paging) - end");
    return returnValue;
  }

}