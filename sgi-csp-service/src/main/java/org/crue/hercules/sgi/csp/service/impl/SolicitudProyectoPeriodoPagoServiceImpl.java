package org.crue.hercules.sgi.csp.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoPeriodoPagoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoSocioNotFoundException;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPeriodoPago;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoPeriodoPagoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioRepository;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudProyectoPeriodoPagoSpecifications;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoPeriodoPagoService;
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
 * Service Implementation para gestion {@link SolicitudProyectoPeriodoPago}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class SolicitudProyectoPeriodoPagoServiceImpl implements SolicitudProyectoPeriodoPagoService {

  private final SolicitudProyectoPeriodoPagoRepository repository;

  private final SolicitudProyectoSocioRepository solicitudProyectoSocioRepository;

  private final SolicitudService solicitudService;

  /**
   * {@link SolicitudProyectoPeriodoPagoServiceImpl}
   * 
   * @param repository                       {@link SolicitudProyectoPeriodoPagoRepository}
   * @param solicitudProyectoSocioRepository {@link SolicitudProyectoSocioRepository}
   * @param solicitudService                 {@link SolicitudService}
   */
  public SolicitudProyectoPeriodoPagoServiceImpl(SolicitudProyectoPeriodoPagoRepository repository,
      SolicitudProyectoSocioRepository solicitudProyectoSocioRepository, SolicitudService solicitudService) {
    this.repository = repository;
    this.solicitudProyectoSocioRepository = solicitudProyectoSocioRepository;
    this.solicitudService = solicitudService;
  }

  /**
   * Actualiza el listado de {@link SolicitudProyectoPeriodoPago} de la
   * {@link SolicitudProyectoSocio} con el listado solicitudPeriodoPagos
   * añadiendo, editando o eliminando los elementos segun proceda.
   *
   * @param solicitudProyectoSocioId Id de la {@link SolicitudProyectoSocio}.
   * @param solicitudPeriodoPagos    lista con los nuevos
   *                                 {@link SolicitudProyectoPeriodoPago} a
   *                                 guardar.
   * @return la entidad {@link SolicitudProyectoPeriodoPago} persistida.
   */
  @Override
  @Transactional
  public List<SolicitudProyectoPeriodoPago> update(Long solicitudProyectoSocioId,
      List<SolicitudProyectoPeriodoPago> solicitudPeriodoPagos) {
    log.debug(
        "updateConvocatoriaPeriodoJustificacionesConvocatoria(Long solicitudProyectoSocioId, List<SolicitudProyectoPeriodoPago> solicitudPeriodoPagos) - start");

    if (solicitudPeriodoPagos.isEmpty()) {
      return new ArrayList<>();
    }

    SolicitudProyectoSocio solicitudProyectoSocio = solicitudProyectoSocioRepository.findById(solicitudProyectoSocioId)
        .orElseThrow(() -> new SolicitudProyectoSocioNotFoundException(solicitudProyectoSocioId));

    // comprobar si la solicitud es modificable
    Assert.isTrue(
        solicitudService.modificable(solicitudProyectoSocio.getSolicitudProyectoDatos().getSolicitud().getId()),
        "No se puede modificar SolicitudProyectoPeriodoPago");

    List<SolicitudProyectoPeriodoPago> solicitudProyectoPeriodoPagosBD = repository
        .findAllBySolicitudProyectoSocioId(solicitudProyectoSocioId);

    // Periodos pago eliminados
    List<SolicitudProyectoPeriodoPago> solicitudProyectoPeriodoPagoEliminar = solicitudProyectoPeriodoPagosBD.stream()
        .filter(periodo -> !solicitudPeriodoPagos.stream().map(SolicitudProyectoPeriodoPago::getId)
            .anyMatch(id -> id == periodo.getId()))
        .collect(Collectors.toList());

    if (!solicitudProyectoPeriodoPagoEliminar.isEmpty()) {
      repository.deleteAll(solicitudProyectoPeriodoPagoEliminar);
    }

    // Ordena los periodos por mesInicial
    solicitudPeriodoPagos.sort(Comparator.comparing(SolicitudProyectoPeriodoPago::getMes));

    AtomicInteger numPeriodo = new AtomicInteger(0);

    SolicitudProyectoPeriodoPago solicitudProyectoPeriodoPagoAnterior = null;
    for (SolicitudProyectoPeriodoPago solicitudProyectoPeriodoPago : solicitudPeriodoPagos) {
      // Actualiza el numero de periodo
      solicitudProyectoPeriodoPago.setNumPeriodo(numPeriodo.incrementAndGet());

      // Si tiene id se valida que exista y que tenga la solicitudProyectoSocio de la
      // que se
      // estan actualizando los periodos
      if (solicitudProyectoPeriodoPago.getId() != null) {
        SolicitudProyectoPeriodoPago solicitudProyectoPeriodoPagoBD = solicitudProyectoPeriodoPagosBD.stream()
            .filter(periodo -> periodo.getId() == solicitudProyectoPeriodoPago.getId()).findFirst()
            .orElseThrow(() -> new SolicitudProyectoPeriodoPagoNotFoundException(solicitudProyectoPeriodoPago.getId()));

        Assert.isTrue(
            solicitudProyectoPeriodoPagoBD.getSolicitudProyectoSocio().getId() == solicitudProyectoPeriodoPago
                .getSolicitudProyectoSocio().getId(),
            "No se puede modificar la solicitud proyecto socio del SolicitudProyectoPeriodoPago");
      }

      // Setea la solicitudProyectoSocio recuperada del solicitudProyectoSocioId
      solicitudProyectoPeriodoPago.setSolicitudProyectoSocio(solicitudProyectoSocio);

      // Validaciones

      Assert.notNull(solicitudProyectoPeriodoPago.getMes(),
          "Mes no puede ser null para realizar la acción sobre SolicitudProyectoPeriodoPago");

      Assert.isTrue(
          solicitudProyectoPeriodoPago.getSolicitudProyectoSocio().getSolicitudProyectoDatos().getDuracion() == null
              || solicitudProyectoPeriodoPago.getMes() <= solicitudProyectoPeriodoPago.getSolicitudProyectoSocio()
                  .getSolicitudProyectoDatos().getDuracion(),
          "El mes no puede ser superior a la duración en meses indicada en la Convocatoria");

      Assert.isTrue(
          solicitudProyectoPeriodoPagoAnterior == null || (solicitudProyectoPeriodoPagoAnterior != null
              && !solicitudProyectoPeriodoPagoAnterior.getMes().equals(solicitudProyectoPeriodoPago.getMes())),
          "El periodo se solapa con otro existente");

      solicitudProyectoPeriodoPagoAnterior = solicitudProyectoPeriodoPago;
    }

    List<SolicitudProyectoPeriodoPago> returnValue = repository.saveAll(solicitudPeriodoPagos);
    log.debug(
        "updateConvocatoriaPeriodoJustificacionesConvocatoria(Long solicitudProyectoSocioId, List<SolicitudProyectoPeriodoPago> solicitudPeriodoPagos) - end");

    return returnValue;
  }

  /**
   * Comprueba la existencia del {@link SolicitudProyectoPeriodoPago} por id.
   *
   * @param id el id de la entidad {@link SolicitudProyectoPeriodoPago}.
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
   * Obtiene una entidad {@link SolicitudProyectoPeriodoPago} por id.
   * 
   * @param id Identificador de la entidad {@link SolicitudProyectoPeriodoPago}.
   * @return SolicitudProyectoPeriodoPago la entidad
   *         {@link SolicitudProyectoPeriodoPago}.
   */
  @Override
  public SolicitudProyectoPeriodoPago findById(Long id) {
    log.debug("findById(Long id) - start");
    final SolicitudProyectoPeriodoPago returnValue = repository.findById(id)
        .orElseThrow(() -> new SolicitudProyectoPeriodoPagoNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link SolicitudProyectoPeriodoPago} para un
   * {@link SolicitudProyectoSocio}.
   *
   * @param idSolicitudProyectoSocio el id del {@link SolicitudProyectoSocio}.
   * @param query                    la información del filtro.
   * @param paging                   la información de la paginación.
   * @return la lista de entidades {@link SolicitudProyectoPeriodoPago} del
   *         {@link Solicitud} paginadas.
   */
  @Override
  public Page<SolicitudProyectoPeriodoPago> findAllBySolicitudProyectoSocio(Long idSolicitudProyectoSocio, String query,
      Pageable paging) {
    log.debug("findAllBySolicitudProyectoDatos(Long solicitudDatosProyectoId, String query, Pageable paging) - start");

    Specification<SolicitudProyectoPeriodoPago> specs = SolicitudProyectoPeriodoPagoSpecifications
        .bySolicitudProyectoSocioId(idSolicitudProyectoSocio).and(SgiRSQLJPASupport.toSpecification(query));

    Page<SolicitudProyectoPeriodoPago> returnValue = repository.findAll(specs, paging);
    log.debug("findAllBySolicitudProyectoDatos(Long solicitudDatosProyectoId, String query, Pageable paging) - end");
    return returnValue;
  }

}