package org.crue.hercules.sgi.csp.service.impl;

import java.util.List;

import org.crue.hercules.sgi.csp.exceptions.RolProyectoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoDatosNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoEquipoNotFoundException;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoDatos;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipo;
import org.crue.hercules.sgi.csp.repository.RolProyectoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoDatosRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoEquipoRepository;
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

  private final SolicitudProyectoDatosRepository solicitudProyectoDatosRepository;

  private final RolProyectoRepository rolProyectoRepository;

  private final SolicitudService solicitudService;

  public SolicitudProyectoEquipoServiceImpl(SolicitudProyectoEquipoRepository repository,
      SolicitudProyectoDatosRepository solicitudProyectoDatosRepository, RolProyectoRepository rolProyectoRepository,
      SolicitudService solicitudService) {
    this.repository = repository;
    this.solicitudProyectoDatosRepository = solicitudProyectoDatosRepository;
    this.rolProyectoRepository = rolProyectoRepository;
    this.solicitudService = solicitudService;
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
    Assert.isTrue(
        solicitudService.modificable(solicitudProyectoEquipo.getSolicitudProyectoDatos().getSolicitud().getId()),
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
   * {@link SolicitudProyectoDatos}.
   *
   * @param solicitudDatosProyectoId el id del {@link SolicitudProyectoDatos}.
   * @param query                    la información del filtro.
   * @param paging                   la información de la paginación.
   * @return la lista de entidades {@link SolicitudProyectoEquipo} del
   *         {@link SolicitudProyectoDatos} paginadas.
   */
  @Override
  public Page<SolicitudProyectoEquipo> findAllBySolicitud(Long solicitudDatosProyectoId, String query,
      Pageable paging) {
    log.debug("findAllBySolicitudProyectoDatos(Long solicitudDatosProyectoId, String query, Pageable paging) - start");

    Specification<SolicitudProyectoEquipo> specs = SolicitudProyectoEquipoSpecifications
        .bySolicitudId(solicitudDatosProyectoId).and(SgiRSQLJPASupport.toSpecification(query));

    Page<SolicitudProyectoEquipo> returnValue = repository.findAll(specs, paging);
    log.debug("findAllBySolicitudProyectoDatos(Long solicitudDatosProyectoId, String query, Pageable paging) - end");
    return returnValue;
  }

  private void validateSolicitudProyectoEquipo(SolicitudProyectoEquipo solicitudProyectoEquipo) {

    Assert.isTrue(
        solicitudProyectoEquipo.getSolicitudProyectoDatos() != null
            && solicitudProyectoEquipo.getSolicitudProyectoDatos().getId() != null,
        "Los datos de proyecto no puede ser null para realizar la acción sobre el SolicitudProyectoEquipo");
    Assert.isTrue(
        solicitudProyectoEquipo.getRolProyecto() != null && solicitudProyectoEquipo.getRolProyecto().getId() != null,
        "El rol de proyecto no puede ser null para realizar la acción sobre el SolicitudProyectoEquipo");
    Assert.notNull(solicitudProyectoEquipo.getPersonaRef(),
        "La persona ref no puede ser null para realizar la acción sobre el SolicitudProyectoEquipo");

    solicitudProyectoDatosRepository.findById(solicitudProyectoEquipo.getSolicitudProyectoDatos().getId())
        .map(solicitudDatosProyecto -> {

          Specification<SolicitudProyectoEquipo> specBySolicitud = SolicitudProyectoEquipoSpecifications
              .bySolicitudId(solicitudDatosProyecto.getSolicitud().getId());

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

          Specification<SolicitudProyectoEquipo> specBySolicitante = SolicitudProyectoEquipoSpecifications
              .bySolicitanteRef(solicitudDatosProyecto.getSolicitud().getSolicitanteRef());

          Specification<SolicitudProyectoEquipo> specsSolicitante = Specification.where(specBySolicitud)
              .and(specBySolicitante);

          List<SolicitudProyectoEquipo> listSolicitudProyectoEquipoSolicitante = repository.findAll(specsSolicitante);

          Assert.isTrue(
              !CollectionUtils.isEmpty(listSolicitudProyectoEquipoSolicitante) || solicitudProyectoEquipo
                  .getPersonaRef().equals(solicitudDatosProyecto.getSolicitud().getSolicitanteRef()),
              "El solicitante de la solicitud debe ser miembro del equipo");

          return solicitudDatosProyecto;
        }).orElseThrow(() -> new SolicitudProyectoDatosNotFoundException(
            solicitudProyectoEquipo.getSolicitudProyectoDatos().getId()));

    if (!rolProyectoRepository.existsById(solicitudProyectoEquipo.getRolProyecto().getId())) {
      throw new RolProyectoNotFoundException(solicitudProyectoEquipo.getRolProyecto().getId());
    }
  }

}