package org.crue.hercules.sgi.csp.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.ProyectoSocioNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoSocioPeriodoJustificacionNotFoundException;
import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoSocioRepository;
import org.crue.hercules.sgi.csp.repository.SocioPeriodoJustificacionDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoSocioPeriodoJustificacionSpecifications;
import org.crue.hercules.sgi.csp.service.ProyectoSocioPeriodoJustificacionService;
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
 * {@link ProyectoSocioPeriodoJustificacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ProyectoSocioPeriodoJustificacionServiceImpl implements ProyectoSocioPeriodoJustificacionService {

  private final ProyectoSocioPeriodoJustificacionRepository repository;
  private final ProyectoSocioRepository proyectoSocioRepository;

  private final SocioPeriodoJustificacionDocumentoRepository socioPeriodoJustificacionDocumentoRepository;

  /**
   * {@link ProyectoSocioPeriodoJustificacionServiceImpl}.
   * 
   * @param proyectoSocioPeriodoJustificacionRepository  {@link ProyectoSocioPeriodoJustificacionRepository}.
   * @param proyectoSocioRepository                      {@link ProyectoSocioRepository}.
   * @param socioPeriodoJustificacionDocumentoRepository {@link SocioPeriodoJustificacionDocumentoRepository}.
   */
  public ProyectoSocioPeriodoJustificacionServiceImpl(
      ProyectoSocioPeriodoJustificacionRepository proyectoSocioPeriodoJustificacionRepository,
      ProyectoSocioRepository proyectoSocioRepository,
      SocioPeriodoJustificacionDocumentoRepository socioPeriodoJustificacionDocumentoRepository) {
    this.repository = proyectoSocioPeriodoJustificacionRepository;
    this.proyectoSocioRepository = proyectoSocioRepository;
    this.socioPeriodoJustificacionDocumentoRepository = socioPeriodoJustificacionDocumentoRepository;
  }

  /**
   * Actualiza el listado de {@link ProyectoSocioPeriodoJustificacion} de la
   * {@link ProyectoSocio} con el listado proyectoSocioPeriodoJustificaciones
   * eliminando los elementos segun proceda.
   *
   * @param proyectoSocioId                     Id de la {@link ProyectoSocio}.
   * @param proyectoSocioPeriodoJustificaciones lista con los nuevos
   *                                            {@link ProyectoSocioPeriodoJustificacion}
   *                                            a guardar.
   * @return la entidad {@link ProyectoSocioPeriodoJustificacion} persistida.
   */
  @Override
  @Transactional
  public List<ProyectoSocioPeriodoJustificacion> delete(Long proyectoSocioId,
      List<ProyectoSocioPeriodoJustificacion> proyectoSocioPeriodoJustificaciones) {
    log.debug(
        "delete(Long proyectoSocioId, List<ProyectoSocioPeriodoJustificacion> proyectoSocioPeriodoJustificaciones) - start");

    proyectoSocioRepository.findById(proyectoSocioId)
        .orElseThrow(() -> new ProyectoSocioNotFoundException(proyectoSocioId));

    List<ProyectoSocioPeriodoJustificacion> proyectoSocioPeriodoJustificacionesBD = repository
        .findAllByProyectoSocioId(proyectoSocioId);

    // Periodos eliminados
    List<ProyectoSocioPeriodoJustificacion> periodoJustificacionesEliminar = proyectoSocioPeriodoJustificacionesBD
        .stream().filter(periodo -> !proyectoSocioPeriodoJustificaciones.stream()
            .map(ProyectoSocioPeriodoJustificacion::getId).anyMatch(id -> id == periodo.getId()))
        .collect(Collectors.toList());

    if (!periodoJustificacionesEliminar.isEmpty()) {

      List<Long> periodoJustificacionId = periodoJustificacionesEliminar.stream()
          .map(ProyectoSocioPeriodoJustificacion::getId).collect(Collectors.toList());

      socioPeriodoJustificacionDocumentoRepository
          .deleteByProyectoSocioPeriodoJustificacionIdIn(periodoJustificacionId);
      repository.deleteAll(periodoJustificacionesEliminar);
    }

    log.debug(
        "delete(Long proyectoSocioId, List<ProyectoSocioPeriodoJustificacion> proyectoSocioPeriodoJustificaciones) - end");

    return proyectoSocioPeriodoJustificaciones;
  }

  /**
   * Actualiza el {@link ProyectoSocioPeriodoJustificacion}
   *
   * @param proyectoSocioPeriodoJustificacion {@link ProyectoSocioPeriodoJustificacion}
   *                                          a actualizar.
   * @return la entidad {@link ProyectoSocioPeriodoJustificacion} persistida.
   */
  @Override
  @Transactional
  public ProyectoSocioPeriodoJustificacion update(ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion,
      Long proyectoSocioPeriodoJustificacionId) {
    log.debug("update(ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificaciones) - start");

    Assert.notNull(proyectoSocioPeriodoJustificacionId, "El id de proeycto socio no puede ser null");

    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacionExistente = repository
        .findById(proyectoSocioPeriodoJustificacionId)
        .orElseThrow(() -> new ProyectoSocioPeriodoJustificacionNotFoundException(proyectoSocioPeriodoJustificacionId));

    Specification<ProyectoSocioPeriodoJustificacion> specByIdNotEqual = ProyectoSocioPeriodoJustificacionSpecifications
        .byIdNotEqual(proyectoSocioPeriodoJustificacionId);

    Specification<ProyectoSocioPeriodoJustificacion> specs = Specification.where(specByIdNotEqual);
    List<ProyectoSocioPeriodoJustificacion> proyectoSocioPeriodoJustificacionesBD = repository.findAll(specs);

    proyectoSocioPeriodoJustificacion.setId(proyectoSocioPeriodoJustificacionId);
    proyectoSocioPeriodoJustificacionesBD.add(proyectoSocioPeriodoJustificacion);

    Assert.isTrue(
        proyectoSocioPeriodoJustificacionExistente.getProyectoSocio().getId() == proyectoSocioPeriodoJustificacion
            .getProyectoSocio().getId(),
        "No se puede modificar el proyecto socio del ProyectoSocioPeriodoJustificacion");

    validateProyectoSocioPeriodoJustificacion(proyectoSocioPeriodoJustificacion);

    AtomicInteger numPeriodo = new AtomicInteger(0);

    // Ordena los periodos por fechaInicio
    proyectoSocioPeriodoJustificacionesBD.sort(Comparator.comparing(ProyectoSocioPeriodoJustificacion::getFechaInicio));

    for (ProyectoSocioPeriodoJustificacion periodoJustificacion : proyectoSocioPeriodoJustificacionesBD) {
      periodoJustificacion.setNumPeriodo(numPeriodo.incrementAndGet());
    }

    repository.saveAll(proyectoSocioPeriodoJustificacionesBD);

    log.debug("update(ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificaciones) - end");

    return proyectoSocioPeriodoJustificacion;
  }

  /**
   * Crea el {@link ProyectoSocioPeriodoJustificacion}
   *
   * @param proyectoSocioPeriodoJustificacion {@link ProyectoSocioPeriodoJustificacion}
   *                                          a actualizar.
   * @return la entidad {@link ProyectoSocioPeriodoJustificacion} persistida.
   */
  @Override
  @Transactional
  public ProyectoSocioPeriodoJustificacion create(ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion) {
    log.debug("create(ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificaciones) - start");

    Assert.isNull(proyectoSocioPeriodoJustificacion.getId(),
        "El id de proyecto socio periodo justificación debe ser null");

    // Validaciones
    validateProyectoSocioPeriodoJustificacion(proyectoSocioPeriodoJustificacion);

    List<ProyectoSocioPeriodoJustificacion> proyectoSocioPeriodoJustificacionesBD = repository
        .findAllByProyectoSocioId(proyectoSocioPeriodoJustificacion.getProyectoSocio().getId());

    proyectoSocioPeriodoJustificacionesBD.add(proyectoSocioPeriodoJustificacion);

    AtomicInteger numPeriodo = new AtomicInteger(0);

    // Ordena los periodos por fechaInicio
    proyectoSocioPeriodoJustificacionesBD.sort(Comparator.comparing(ProyectoSocioPeriodoJustificacion::getFechaInicio));

    for (ProyectoSocioPeriodoJustificacion periodoJustificacion : proyectoSocioPeriodoJustificacionesBD) {
      periodoJustificacion.setNumPeriodo(numPeriodo.incrementAndGet());
    }

    proyectoSocioPeriodoJustificacionesBD = repository.saveAll(proyectoSocioPeriodoJustificacionesBD);

    log.debug("update(ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificaciones) - end");

    return proyectoSocioPeriodoJustificacion;

  }

  /**
   * Obtiene {@link ProyectoSocioPeriodoJustificacion} por su id.
   *
   * @param id el id de la entidad {@link ProyectoSocioPeriodoJustificacion}.
   * @return la entidad {@link ProyectoSocioPeriodoJustificacion}.
   */
  @Override
  public ProyectoSocioPeriodoJustificacion findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ProyectoSocioPeriodoJustificacion returnValue = repository.findById(id)
        .orElseThrow(() -> new ProyectoSocioPeriodoJustificacionNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link ProyectoSocioPeriodoJustificacion} para una
   * {@link ProyectoSocio}.
   *
   * @param proyectoSocioId el id de la {@link ProyectoSocio}.
   * @param query           la información del filtro.
   * @param pageable        la información de la paginación.
   * @return la lista de entidades {@link ProyectoSocioPeriodoJustificacion} de la
   *         {@link ProyectoSocio} paginadas.
   */
  public Page<ProyectoSocioPeriodoJustificacion> findAllByProyectoSocio(Long proyectoSocioId, String query,
      Pageable pageable) {
    log.debug("findAllByProyectoSocio(Long proyectoSocioId, String query, Pageable pageable) - start");
    Specification<ProyectoSocioPeriodoJustificacion> specs = ProyectoSocioPeriodoJustificacionSpecifications
        .byProyectoSocioId(proyectoSocioId).and(SgiRSQLJPASupport.toSpecification(query));

    Page<ProyectoSocioPeriodoJustificacion> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByProyectoSocio(Long proyectoSocioId, String query, Pageable pageable) - end");
    return returnValue;
  }

  private void validateProyectoSocioPeriodoJustificacion(
      ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion) {

    Assert.notNull(proyectoSocioPeriodoJustificacion.getProyectoSocio().getId(),
        "El id de proyecto socio no puede ser null");

    if (!proyectoSocioRepository.existsById(proyectoSocioPeriodoJustificacion.getProyectoSocio().getId())) {
      throw new ProyectoSocioNotFoundException(proyectoSocioPeriodoJustificacion.getProyectoSocio().getId());
    }

    Assert.isTrue(
        proyectoSocioPeriodoJustificacion.getFechaInicio().isBefore(proyectoSocioPeriodoJustificacion.getFechaFin()),
        "La fecha final tiene que ser posterior a la fecha inicial");

    if (proyectoSocioPeriodoJustificacion.getProyectoSocio().getProyecto().getEstado()
        .getEstado() == EstadoProyecto.Estado.ABIERTO) {
      Assert.isTrue(
          proyectoSocioPeriodoJustificacion.getFechaInicioPresentacion() != null
              && proyectoSocioPeriodoJustificacion.getFechaFinPresentacion() != null,
          "Las fechas de presentación no pueden ser null cuando el estado del proyecto es Abierto");
    }

    if (proyectoSocioPeriodoJustificacion.getFechaInicioPresentacion() != null
        && proyectoSocioPeriodoJustificacion.getFechaFinPresentacion() != null) {
      Assert.isTrue(
          proyectoSocioPeriodoJustificacion.getFechaInicioPresentacion()
              .isBefore(proyectoSocioPeriodoJustificacion.getFechaFinPresentacion()),
          "La fecha de fin de presentación tiene que ser posterior a la fecha de inicio de presentación");
    }

    Assert.isTrue(
        proyectoSocioPeriodoJustificacion.getProyectoSocio().getFechaFin() == null || proyectoSocioPeriodoJustificacion
            .getFechaFin().isBefore(proyectoSocioPeriodoJustificacion.getProyectoSocio().getFechaFin()),
        "La fecha fin no puede ser superior a la fecha fin indicada en Proyecto socio");

    Assert.isTrue(!isRangoFechasSolapado(proyectoSocioPeriodoJustificacion), "El periodo se solapa con otro existente");

  }

  /**
   * Comprueba si el rango de fechas del socio se solapa con alguno de los rangos
   * de ese mismo socio en el proyecto.
   * 
   * @param proyectoSocioPeriodoJustificacion un {@link ProyectoSocio}.
   * @return true si se solapa o false si no hay solapamiento.
   */
  private boolean isRangoFechasSolapado(ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion) {
    log.debug("isRangoFechasSolapado(ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion) - start");

    Specification<ProyectoSocioPeriodoJustificacion> specByIdNotEqual = ProyectoSocioPeriodoJustificacionSpecifications
        .byIdNotEqual(proyectoSocioPeriodoJustificacion.getId());

    Specification<ProyectoSocioPeriodoJustificacion> specProyectoSocioId = ProyectoSocioPeriodoJustificacionSpecifications
        .byProyectoSocioId(proyectoSocioPeriodoJustificacion.getProyectoSocio().getId());

    Specification<ProyectoSocioPeriodoJustificacion> specByRangoFechaSolapados = ProyectoSocioPeriodoJustificacionSpecifications
        .byRangoFechaSolapados(proyectoSocioPeriodoJustificacion.getFechaInicio(),
            proyectoSocioPeriodoJustificacion.getFechaFin());

    Specification<ProyectoSocioPeriodoJustificacion> specs = Specification.where(specByIdNotEqual)
        .and(specByRangoFechaSolapados).and(specProyectoSocioId);
    boolean returnValue = repository.count(specs) > 0 ? true : false;
    log.debug("isRangoFechasSolapado(ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion) - end");
    return returnValue;
  }

}
