package org.crue.hercules.sgi.csp.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoEquipoSocioNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoSocioNotFoundException;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipoSocio;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoEquipoSocioRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioRepository;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudProyectoEquipoSocioSpecifications;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoEquipoSocioService;
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
 * Service Implementation para gestion {@link SolicitudProyectoEquipoSocio}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class SolicitudProyectoEquipoSocioServiceImpl implements SolicitudProyectoEquipoSocioService {

  /** Solicitud service */
  private final SolicitudService solicitudService;

  /** Solicitud proyecto equipo socio repository */
  private final SolicitudProyectoEquipoSocioRepository repository;

  /** Solicitud proyecto socio repository */
  private final SolicitudProyectoSocioRepository solicitudProyectoSocioRepository;

  public SolicitudProyectoEquipoSocioServiceImpl(SolicitudProyectoEquipoSocioRepository repository,
      SolicitudService solicitudService, SolicitudProyectoSocioRepository solicitudProyectoSocioRepository) {
    this.repository = repository;
    this.solicitudService = solicitudService;
    this.solicitudProyectoSocioRepository = solicitudProyectoSocioRepository;
  }

  /**
   * Actualiza los datos del {@link SolicitudProyectoEquipoSocio}.
   * 
   * @param solicitudProyectoSocioId      Id de la {@link SolicitudProyectoSocio}.
   * @param solicitudProyectoEquipoSocios lista con los nuevos
   *                                      {@link SolicitudProyectoEquipoSocio} a
   *                                      guardar.
   * @return SolicitudProyectoEquipoSocio {@link SolicitudProyectoEquipoSocio}
   *         actualizado.
   */
  @Override
  @Transactional
  public List<SolicitudProyectoEquipoSocio> update(Long solicitudProyectoSocioId,
      List<SolicitudProyectoEquipoSocio> solicitudProyectoEquipoSocios) {
    log.debug(
        "update(Long solicitudProyectoSocioId,  List<SolicitudProyectoEquipoSocio> solicitudProyectoEquipoSocios) - start");

    SolicitudProyectoSocio solicitudProyectoSocio = solicitudProyectoSocioRepository.findById(solicitudProyectoSocioId)
        .orElseThrow(() -> new SolicitudProyectoSocioNotFoundException(solicitudProyectoSocioId));

    // comprobar si la solicitud es modificable
    Assert.isTrue(
        solicitudService.modificable(solicitudProyectoSocio.getSolicitudProyectoDatos().getSolicitud().getId()),
        "No se puede modificar SolicitudProyectoEquipoSocio");

    List<SolicitudProyectoEquipoSocio> existentes = repository
        .findAllBySolicitudProyectoSocioId(solicitudProyectoSocioId);

    // Periodos eliminados
    List<SolicitudProyectoEquipoSocio> solicitudProyectoEquipoEliminar = existentes.stream()
        .filter(periodo -> !solicitudProyectoEquipoSocios.stream().map(SolicitudProyectoEquipoSocio::getId)
            .anyMatch(id -> id == periodo.getId()))
        .collect(Collectors.toList());

    if (!solicitudProyectoEquipoEliminar.isEmpty()) {
      repository.deleteAll(solicitudProyectoEquipoEliminar);
    }

    if (solicitudProyectoEquipoSocios.isEmpty()) {
      return new ArrayList<>();
    }

    List<SolicitudProyectoEquipoSocio> solicitudProyectoEquipoMesInicioNull = solicitudProyectoEquipoSocios.stream()
        .filter(periodo -> periodo.getMesInicio() == null).collect(Collectors.toList());

    List<SolicitudProyectoEquipoSocio> solicitudProyectoEquipoConMesInicio = solicitudProyectoEquipoSocios.stream()
        .filter(periodo -> periodo.getMesInicio() != null).collect(Collectors.toList());

    solicitudProyectoEquipoMesInicioNull.sort(Comparator.comparing(SolicitudProyectoEquipoSocio::getPersonaRef));

    solicitudProyectoEquipoConMesInicio.sort(Comparator.comparing(SolicitudProyectoEquipoSocio::getMesInicio)
        .thenComparing(Comparator.comparing(SolicitudProyectoEquipoSocio::getPersonaRef)));

    List<SolicitudProyectoEquipoSocio> solicitudProyectoEquipoAll = new ArrayList<SolicitudProyectoEquipoSocio>();
    solicitudProyectoEquipoAll.addAll(solicitudProyectoEquipoMesInicioNull);
    solicitudProyectoEquipoAll.addAll(solicitudProyectoEquipoConMesInicio);

    SolicitudProyectoEquipoSocio solicitudProyectoEquipoSocioAnterior = null;
    for (SolicitudProyectoEquipoSocio solicitudProyectoEquipoSocio : solicitudProyectoEquipoAll) {

      // Si tiene id se valida que exista y que tenga la solicitud proyecto equipo de
      // la que se estan actualizando los periodos
      if (solicitudProyectoEquipoSocio.getId() != null) {
        SolicitudProyectoEquipoSocio existente = existentes.stream()
            .filter(equipoSocio -> equipoSocio.getId() == solicitudProyectoEquipoSocio.getId()).findFirst()
            .orElseThrow(() -> new SolicitudProyectoEquipoSocioNotFoundException(solicitudProyectoEquipoSocio.getId()));

        Assert
            .isTrue(
                existente.getSolicitudProyectoSocio().getId() == solicitudProyectoEquipoSocio
                    .getSolicitudProyectoSocio().getId(),
                "No se puede modificar la solicitud proyecto socio del SolicitudProyectoEquipoSocio");
      }

      // Setea la solicitud proyecto socio recuperada del solicitudProyectoSocioId
      solicitudProyectoEquipoSocio.setSolicitudProyectoSocio(solicitudProyectoSocio);

      // Validaciones
      Assert.notNull(solicitudProyectoEquipoSocio.getRolProyecto(),
          "El rol de participación no puede ser null para realizar la acción sobre SolicitudProyectoEquipoSocio");

      Assert.notNull(solicitudProyectoEquipoSocio.getPersonaRef(),
          "La persona ref no puede ser null para realizar la acción sobre SolicitudProyectoEquipoSocio");

      Assert.isTrue(solicitudProyectoEquipoSocioAnterior == null || (solicitudProyectoEquipoSocioAnterior != null
          && (!solicitudProyectoEquipoSocioAnterior.getPersonaRef().equals(solicitudProyectoEquipoSocio.getPersonaRef())
              || (solicitudProyectoEquipoSocioAnterior.getPersonaRef()
                  .equals(solicitudProyectoEquipoSocio.getPersonaRef())
                  && (solicitudProyectoEquipoSocio.getMesInicio() > solicitudProyectoEquipoSocioAnterior
                      .getMesFin())))),
          "El periodo se solapa con otro existente");

      solicitudProyectoEquipoSocioAnterior = solicitudProyectoEquipoSocio;
    }

    List<SolicitudProyectoEquipoSocio> returnValue = repository.saveAll(solicitudProyectoEquipoSocios);
    log.debug(
        "update(Long solicitudProyectoSocioId,  List<SolicitudProyectoEquipoSocio> solicitudProyectoEquipoSocios) - end");

    return returnValue;
  }

  /**
   * Comprueba la existencia del {@link SolicitudProyectoEquipoSocio} por id.
   *
   * @param id el id de la entidad {@link SolicitudProyectoEquipoSocio}.
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
   * Obtiene una entidad {@link SolicitudProyectoEquipoSocio} por id.
   * 
   * @param id Identificador de la entidad {@link SolicitudProyectoEquipoSocio}.
   * @return SolicitudProyectoEquipoSocio la entidad
   *         {@link SolicitudProyectoEquipoSocio}.
   */
  @Override
  public SolicitudProyectoEquipoSocio findById(Long id) {
    log.debug("findById(Long id) - start");
    final SolicitudProyectoEquipoSocio returnValue = repository.findById(id)
        .orElseThrow(() -> new SolicitudProyectoEquipoSocioNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene la {@link SolicitudProyectoEquipoSocio} para una
   * {@link SolicitudProyectoSocio}.
   *
   * @param solicitudProyectoSocioId el id de la {@link SolicitudProyectoSocio}.
   * @return la lista de entidades {@link SolicitudProyectoEquipoSocio} de la
   *         {@link SolicitudProyectoSocio} paginadas.
   */
  @Override
  public Page<SolicitudProyectoEquipoSocio> findAllBySolicitudProyectoSocio(Long solicitudProyectoSocioId, String query,
      Pageable paging) {
    log.debug("findAllBySolicitudProyectoSocio(Long solicitudProyectoSocioId, String query, Pageable paging) - start");

    Specification<SolicitudProyectoEquipoSocio> specs = SolicitudProyectoEquipoSocioSpecifications
        .bySolicitudProyectoSocio(solicitudProyectoSocioId).and(SgiRSQLJPASupport.toSpecification(query));

    Page<SolicitudProyectoEquipoSocio> returnValue = repository.findAll(specs, paging);
    log.debug("findAllBySolicitudProyectoSocio(Long solicitudProyectoSocioId, String query, Pageable paging) - end");
    return returnValue;

  }

}