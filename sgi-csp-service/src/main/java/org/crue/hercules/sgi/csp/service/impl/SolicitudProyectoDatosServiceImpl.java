package org.crue.hercules.sgi.csp.service.impl;

import java.util.Optional;

import org.crue.hercules.sgi.csp.exceptions.SolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoDatosNotFoundException;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoDatos;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoDatosRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudProyectoDatosSpecifications;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoDatosService;
import org.crue.hercules.sgi.csp.service.SolicitudService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link SolicitudProyectoDatos}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class SolicitudProyectoDatosServiceImpl implements SolicitudProyectoDatosService {

  private final SolicitudProyectoDatosRepository repository;

  private final SolicitudRepository solicitudRepository;

  private final SolicitudService solicitudService;

  public SolicitudProyectoDatosServiceImpl(SolicitudProyectoDatosRepository repository,
      SolicitudRepository solicitudRepository, SolicitudService solicitudService) {
    this.repository = repository;
    this.solicitudRepository = solicitudRepository;
    this.solicitudService = solicitudService;
  }

  /**
   * Guarda la entidad {@link SolicitudProyectoDatos}.
   * 
   * @param solicitudProyectoDatos la entidad {@link SolicitudProyectoDatos} a
   *                               guardar.
   * @return SolicitudProyectoDatos la entidad {@link SolicitudProyectoDatos}
   *         persistida.
   */
  @Override
  @Transactional
  public SolicitudProyectoDatos create(SolicitudProyectoDatos solicitudProyectoDatos) {
    log.debug("create(SolicitudProyectoDatos solicitudProyectoDatos) - start");

    Assert.isNull(solicitudProyectoDatos.getId(), "Id tiene que ser null para crear la SolicitudProyectoDatos");

    validateSolicitudProyectoDatos(solicitudProyectoDatos);

    SolicitudProyectoDatos returnValue = repository.save(solicitudProyectoDatos);

    log.debug("create(SolicitudProyectoDatos solicitudProyectoDatos) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link SolicitudProyectoDatos}.
   * 
   * @param solicitudProyectoDatos rolSocioActualizar
   *                               {@link SolicitudProyectoDatos} con los datos
   *                               actualizados.
   * @return {@link SolicitudProyectoDatos} actualizado.
   */
  @Override
  @Transactional
  public SolicitudProyectoDatos update(SolicitudProyectoDatos solicitudProyectoDatos) {
    log.debug("update(SolicitudProyectoDatos solicitudProyectoDatos) - start");

    Assert.notNull(solicitudProyectoDatos.getId(), "Id no puede ser null para actualizar SolicitudProyectoDatos");
    validateSolicitudProyectoDatos(solicitudProyectoDatos);

    // TODO validación de SolicitudProyectoPresupuesto

    // comprobar si la solicitud es modificable
    Assert.isTrue(solicitudService.modificable(solicitudProyectoDatos.getSolicitud().getId()),
        "No se puede modificar SolicitudProyectoDatos");

    return repository.findById(solicitudProyectoDatos.getId()).map((solicitudProyectoDatosExistente) -> {

      solicitudProyectoDatosExistente.setTitulo(solicitudProyectoDatos.getTitulo());
      solicitudProyectoDatosExistente.setAcronimo(solicitudProyectoDatos.getAcronimo());
      solicitudProyectoDatosExistente.setDuracion(solicitudProyectoDatos.getDuracion());
      solicitudProyectoDatosExistente.setColaborativo(solicitudProyectoDatos.getColaborativo());
      solicitudProyectoDatosExistente.setCoordinadorExterno(solicitudProyectoDatos.getCoordinadorExterno());
      solicitudProyectoDatosExistente.setUniversidadSubcontratada(solicitudProyectoDatos.getUniversidadSubcontratada());
      solicitudProyectoDatosExistente.setObjetivos(solicitudProyectoDatos.getObjetivos());
      solicitudProyectoDatosExistente.setIntereses(solicitudProyectoDatos.getIntereses());
      solicitudProyectoDatosExistente.setResultadosPrevistos(solicitudProyectoDatos.getResultadosPrevistos());
      solicitudProyectoDatosExistente.setAreaTematica(solicitudProyectoDatos.getAreaTematica());
      solicitudProyectoDatosExistente.setCheckListRef(solicitudProyectoDatos.getCheckListRef());
      solicitudProyectoDatosExistente.setEnvioEtica(solicitudProyectoDatos.getEnvioEtica());
      solicitudProyectoDatosExistente.setPresupuestoPorEntidades(solicitudProyectoDatos.getPresupuestoPorEntidades());
      SolicitudProyectoDatos returnValue = repository.save(solicitudProyectoDatosExistente);

      log.debug("update(SolicitudProyectoDatos solicitudProyectoDatos) - end");
      return returnValue;
    }).orElseThrow(() -> new SolicitudProyectoDatosNotFoundException(solicitudProyectoDatos.getId()));
  }

  /**
   * Comprueba la existencia del {@link SolicitudProyectoDatos} por id.
   *
   * @param id el id de la entidad {@link SolicitudProyectoDatos}.
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
   * Obtiene una entidad {@link SolicitudProyectoDatos} por id.
   * 
   * @param id Identificador de la entidad {@link SolicitudProyectoDatos}.
   * @return SolicitudProyectoDatos la entidad {@link SolicitudProyectoDatos}.
   */
  @Override
  public SolicitudProyectoDatos findById(Long id) {
    log.debug("findById(Long id) - start");
    final SolicitudProyectoDatos returnValue = repository.findById(id)
        .orElseThrow(() -> new SolicitudProyectoDatosNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina la {@link SolicitudProyectoDatos}.
   *
   * @param id Id del {@link SolicitudProyectoDatos}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id, "SolicitudProyectoDatos id no puede ser null para eliminar un SolicitudProyectoDatos");
    if (!repository.existsById(id)) {
      throw new SolicitudProyectoDatosNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");

  }

  /**
   * Obtiene la {@link SolicitudProyectoDatos} para una {@link Solicitud}.
   *
   * @param solicitudId el id de la {@link Solicitud}.
   * @return la lista de entidades {@link SolicitudProyectoDatos} de la
   *         {@link Solicitud} paginadas.
   */
  @Override
  public SolicitudProyectoDatos findBySolicitud(Long solicitudId) {
    log.debug("findBySolicitud(Long solicitudId) - start");

    if (solicitudRepository.existsById(solicitudId)) {
      final Optional<SolicitudProyectoDatos> returnValue = repository.findBySolicitudId(solicitudId);
      log.debug("findBySolicitud(Long solicitudId) - end");
      return (returnValue.isPresent()) ? returnValue.get() : null;
    } else {
      throw new SolicitudNotFoundException(solicitudId);
    }

  }

  /**
   * Comprueba si existe una solicitud proyecto datos
   * 
   * @param id Identificador de la {@link Solicitud}
   * @return Indicador de si existe o no solicitud datos proyecto.
   */
  @Override
  public boolean existsBySolicitudId(Long id) {

    return repository.existsBySolicitudId(id);
  }

  /**
   * Comprueba si tiene presupuesto por entidades.
   * 
   * @param solicitudId Identificador de la {@link Solicitud}
   * @return Indicador de si tiene o no presupuesto por entidades.
   */
  @Override
  public boolean hasPresupuestoPorEntidades(Long solicitudId) {
    log.debug("hasPresupuestoPorEntidades(Long solicitudId) - start");

    Specification<SolicitudProyectoDatos> specByProyecto = SolicitudProyectoDatosSpecifications
        .bySolicitudId(solicitudId);
    Specification<SolicitudProyectoDatos> specPresupuestoPorEntidades = SolicitudProyectoDatosSpecifications
        .presupuestoPorEntidades();

    Specification<SolicitudProyectoDatos> specs = Specification.where(specByProyecto).and(specPresupuestoPorEntidades);
    boolean returnValue = repository.count(specs) > 0 ? true : false;
    log.debug("hasPresupuestoPorEntidades(Long solicitudId) - end");
    return returnValue;
  }

  /**
   * Realiza las validaciones comunes para las operaciones de creación y
   * acutalización de solicitud proyecto datos.
   * 
   * @param solicitudProyectoDatos {@link SolicitudNotFoundException}
   */
  private void validateSolicitudProyectoDatos(SolicitudProyectoDatos solicitudProyectoDatos) {
    log.debug("validateSolicitudProyectoDatos(SolicitudProyectoDatos solicitudProyectoDatos) - start");

    Assert.notNull(solicitudProyectoDatos.getSolicitud(),
        "La solicitud no puede ser null para realizar la acción sobre SolicitudProyectoDatos");

    Assert.notNull(solicitudProyectoDatos.getTitulo(),
        "El título no puede ser null para realizar la acción sobre SolicitudProyectoDatos");

    Assert.notNull(solicitudProyectoDatos.getColaborativo(),
        "Colaborativo no puede ser null para realizar la acción sobre SolicitudProyectoDatos");

    Assert.notNull(solicitudProyectoDatos.getPresupuestoPorEntidades(),
        "Presupuesto por entidades no puede ser null para realizar la acción sobre SolicitudProyectoDatos");

    if (!solicitudRepository.existsById(solicitudProyectoDatos.getSolicitud().getId())) {
      throw new SolicitudNotFoundException(solicitudProyectoDatos.getSolicitud().getId());
    }

    log.debug("validateSolicitudProyectoDatos(SolicitudProyectoDatos solicitudProyectoDatos) - end");
  }

}