package org.crue.hercules.sgi.csp.service.impl;

import java.util.List;

import org.crue.hercules.sgi.csp.exceptions.RolProyectoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoEquipoNotFoundException;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipo;
import org.crue.hercules.sgi.csp.repository.RolProyectoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudProyectoEquipoSpecifications;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoEquipoService;
import org.crue.hercules.sgi.csp.service.SolicitudService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link SolicitudProyectoEquipo}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class SolicitudProyectoEquipoServiceImpl implements SolicitudProyectoEquipoService {

  private final SolicitudProyectoEquipoRepository repository;

  private final SolicitudProyectoRepository solicitudProyectoRepository;

  private final RolProyectoRepository rolProyectoRepository;

  private final SolicitudService solicitudService;

  private final SolicitudRepository solicitudRepository;

  public SolicitudProyectoEquipoServiceImpl(SolicitudProyectoEquipoRepository repository,
      SolicitudProyectoRepository solicitudProyectoRepository, RolProyectoRepository rolProyectoRepository,
      SolicitudService solicitudService, SolicitudRepository solicitudRepository) {
    this.repository = repository;
    this.solicitudProyectoRepository = solicitudProyectoRepository;
    this.rolProyectoRepository = rolProyectoRepository;
    this.solicitudService = solicitudService;
    this.solicitudRepository = solicitudRepository;
  }

  /**
   * Guarda la entidad {@link SolicitudProyectoEquipo}.
   * 
   * @param solicitudProyectoEquipo la entidad {@link SolicitudProyectoEquipo} a
   *                                guardar.
   * @return SolicitudProyectoEquipo la entidad {@link SolicitudProyectoEquipo}
   *         persistida.
   */
  @Override
  @Transactional
  public SolicitudProyectoEquipo create(SolicitudProyectoEquipo solicitudProyectoEquipo) {
    log.debug("create(SolicitudProyectoEquipo solicitudProyectoEquipo) - start");

    Assert.isNull(solicitudProyectoEquipo.getId(),
        "Id tiene que ser null para realizar la acción sobre el SolicitudProyectoEquipo");

    validateSolicitudProyectoEquipo(solicitudProyectoEquipo);

    SolicitudProyectoEquipo returnValue = repository.save(solicitudProyectoEquipo);

    log.debug("create(SolicitudProyectoEquipo solicitudProyectoEquipo) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link SolicitudProyectoEquipo}.
   * 
   * @param solicitudProyectoEquipo rolSocioActualizar
   *                                {@link SolicitudProyectoEquipo} con los datos
   *                                actualizados.
   * @return {@link SolicitudProyectoEquipo} actualizado.
   */
  @Override
  @Transactional
  public SolicitudProyectoEquipo update(SolicitudProyectoEquipo solicitudProyectoEquipo) {
    log.debug("update(SolicitudProyectoEquipo solicitudProyectoEquipo) - start");

    validateSolicitudProyectoEquipo(solicitudProyectoEquipo);

    // comprobar si la solicitud es modificable
    SolicitudProyecto solicitudProyecto = solicitudProyectoRepository
        .findById(solicitudProyectoEquipo.getSolicitudProyectoId())
        .orElseThrow(() -> new SolicitudProyectoNotFoundException(solicitudProyectoEquipo.getSolicitudProyectoId()));
    Assert.isTrue(solicitudService.modificable(solicitudProyecto.getId()),
        "No se puede modificar SolicitudProyectoEquipo");

    return repository.findById(solicitudProyectoEquipo.getId()).map((solicitudProyectoEquipoExistente) -> {

      solicitudProyectoEquipoExistente.setRolProyecto(solicitudProyectoEquipo.getRolProyecto());
      solicitudProyectoEquipoExistente.setPersonaRef(solicitudProyectoEquipo.getPersonaRef());
      solicitudProyectoEquipoExistente.setMesInicio(solicitudProyectoEquipo.getMesInicio());
      solicitudProyectoEquipoExistente.setMesFin(solicitudProyectoEquipo.getMesFin());
      SolicitudProyectoEquipo returnValue = repository.save(solicitudProyectoEquipoExistente);

      log.debug("update(SolicitudProyectoEquipo solicitudProyectoEquipo) - end");
      return returnValue;
    }).orElseThrow(() -> new SolicitudProyectoEquipoNotFoundException(solicitudProyectoEquipo.getId()));
  }

  /**
   * Comprueba la existencia del {@link SolicitudProyectoEquipo} por id.
   *
   * @param id el id de la entidad {@link SolicitudProyectoEquipo}.
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
   * Obtiene una entidad {@link SolicitudProyectoEquipo} por id.
   * 
   * @param id Identificador de la entidad {@link SolicitudProyectoEquipo}.
   * @return SolicitudProyectoEquipo la entidad {@link SolicitudProyectoEquipo}.
   */
  @Override
  public SolicitudProyectoEquipo findById(Long id) {
    log.debug("findById(Long id) - start");
    final SolicitudProyectoEquipo returnValue = repository.findById(id)
        .orElseThrow(() -> new SolicitudProyectoEquipoNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina la {@link SolicitudProyectoEquipo}.
   *
   * @param id Id del {@link SolicitudProyectoEquipo}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id, "SolicitudProyectoEquipo id no puede ser null para eliminar un SolicitudProyectoEquipo");
    if (!repository.existsById(id)) {
      throw new SolicitudProyectoEquipoNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");

  }

  /**
   * Obtiene las {@link SolicitudProyectoEquipo} para un
   * {@link SolicitudProyecto}.
   *
   * @param solicitudProyectoId el id del {@link SolicitudProyecto}.
   * @param query               la información del filtro.
   * @param paging              la información de la paginación.
   * @return la lista de entidades {@link SolicitudProyectoEquipo} del
   *         {@link SolicitudProyecto} paginadas.
   */
  @Override
  public Page<SolicitudProyectoEquipo> findAllBySolicitud(Long solicitudProyectoId, String query, Pageable paging) {
    log.debug("findAllBySolicitudProyecto(Long solicitudProyectoId, String query, Pageable paging) - start");

    Specification<SolicitudProyectoEquipo> specs = SolicitudProyectoEquipoSpecifications
        .bySolicitudId(solicitudProyectoId).and(SgiRSQLJPASupport.toSpecification(query));

    Page<SolicitudProyectoEquipo> returnValue = repository.findAll(specs, paging);
    log.debug("findAllBySolicitudProyecto(Long solicitudProyectoId, String query, Pageable paging) - end");
    return returnValue;
  }

  private void validateSolicitudProyectoEquipo(SolicitudProyectoEquipo solicitudProyectoEquipo) {

    Assert.isTrue(solicitudProyectoEquipo.getSolicitudProyectoId() != null,
        "Los datos de proyecto no puede ser null para realizar la acción sobre el SolicitudProyectoEquipo");
    Assert.isTrue(
        solicitudProyectoEquipo.getRolProyecto() != null && solicitudProyectoEquipo.getRolProyecto().getId() != null,
        "El rol de proyecto no puede ser null para realizar la acción sobre el SolicitudProyectoEquipo");
    Assert.notNull(solicitudProyectoEquipo.getPersonaRef(),
        "La persona ref no puede ser null para realizar la acción sobre el SolicitudProyectoEquipo");

    solicitudProyectoRepository.findById(solicitudProyectoEquipo.getSolicitudProyectoId()).map(solicitudProyecto -> {

      Specification<SolicitudProyectoEquipo> specBySolicitud = SolicitudProyectoEquipoSpecifications
          .bySolicitudId(solicitudProyecto.getId());

      Specification<SolicitudProyectoEquipo> specByPersonaRef = SolicitudProyectoEquipoSpecifications
          .byPersonaRef(solicitudProyectoEquipo.getPersonaRef());

      Specification<SolicitudProyectoEquipo> specBySolapamientoMeses = SolicitudProyectoEquipoSpecifications
          .byRangoMesesSolapados(solicitudProyectoEquipo.getMesInicio(), solicitudProyectoEquipo.getMesFin());

      Specification<SolicitudProyectoEquipo> specs = Specification.where(specBySolicitud).and(specByPersonaRef)
          .and(specBySolapamientoMeses);

      if (solicitudProyectoEquipo.getId() != null) {
        Specification<SolicitudProyectoEquipo> specsIdNotEqual = SolicitudProyectoEquipoSpecifications
            .byIdNotEqual(solicitudProyectoEquipo.getId());
        specs = specs.and(specsIdNotEqual);
      }

      List<SolicitudProyectoEquipo> listMiembrosEquipoEnRango = repository.findAll(specs);

      Assert.isTrue(CollectionUtils.isEmpty(listMiembrosEquipoEnRango),
          "El miembro del equipo ya existe en el mismo rango de fechas");

      Solicitud solicitud = solicitudRepository.findById(solicitudProyecto.getId())
          .orElseThrow(() -> new SolicitudNotFoundException(solicitudProyecto.getId()));
      Specification<SolicitudProyectoEquipo> specBySolicitante = SolicitudProyectoEquipoSpecifications
          .bySolicitanteRef(solicitud.getSolicitanteRef());

      Specification<SolicitudProyectoEquipo> specsSolicitante = Specification.where(specBySolicitud)
          .and(specBySolicitante);

      List<SolicitudProyectoEquipo> listSolicitudProyectoEquipoSolicitante = repository.findAll(specsSolicitante);

      Assert.isTrue(
          !CollectionUtils.isEmpty(listSolicitudProyectoEquipoSolicitante)
              || solicitudProyectoEquipo.getPersonaRef().equals(solicitud.getSolicitanteRef()),
          "El solicitante de la solicitud debe ser miembro del equipo");

      return solicitudProyecto;
    }).orElseThrow(() -> new SolicitudProyectoNotFoundException(solicitudProyectoEquipo.getSolicitudProyectoId()));

    if (!rolProyectoRepository.existsById(solicitudProyectoEquipo.getRolProyecto().getId())) {
      throw new RolProyectoNotFoundException(solicitudProyectoEquipo.getRolProyecto().getId());
    }
  }

}