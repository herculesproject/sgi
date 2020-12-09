package org.crue.hercules.sgi.csp.service.impl;

import java.util.List;

import org.crue.hercules.sgi.csp.enums.TipoEstadoSolicitudEnum;
import org.crue.hercules.sgi.csp.exceptions.SolicitudDocumentoNotFoundException;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudDocumento;
import org.crue.hercules.sgi.csp.repository.SolicitudDocumentoRepository;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudDocumentoSpecifications;
import org.crue.hercules.sgi.csp.service.SolicitudDocumentoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import org.crue.hercules.sgi.framework.data.jpa.domain.QuerySpecification;
import org.crue.hercules.sgi.framework.data.search.QueryCriteria;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link SolicitudDocumento}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class SolicitudDocumentoServiceImpl implements SolicitudDocumentoService {

  private final SolicitudDocumentoRepository repository;

  public SolicitudDocumentoServiceImpl(SolicitudDocumentoRepository repository) {
    this.repository = repository;
  }

  /**
   * Guarda la entidad {@link SolicitudDocumento}.
   * 
   * @param solicitudDocumento la entidad {@link SolicitudDocumento} a guardar.
   * @return SolicitudDocumento la entidad {@link SolicitudDocumento} persistida.
   */
  @Override
  @Transactional
  public SolicitudDocumento create(SolicitudDocumento solicitudDocumento) {
    log.debug("create(SolicitudDocumento solicitudDocumento) - start");

    Assert.isNull(solicitudDocumento.getId(), "Id tiene que ser null para crear la SolicitudDocumento");

    Assert.notNull(solicitudDocumento.getSolicitud().getId(),
        "Solicitud id no puede ser null para crear una SolicitudModalidad");

    Assert.notNull(solicitudDocumento.getNombre(),
        "Nombre documento no puede ser null para crear la SolicitudDocumento");
    Assert.notNull(solicitudDocumento.getDocumentoRef(),
        "La referencia del documento no puede ser null para crear la SolicitudDocumento");

    SolicitudDocumento returnValue = repository.save(solicitudDocumento);

    log.debug("create(SolicitudDocumento solicitudDocumento) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link SolicitudDocumento}.
   * 
   * @param solicitudDocumento      rolSocioActualizar {@link SolicitudDocumento}
   *                                con los datos actualizados.
   * @param isAdministradorOrGestor Indiciador de si el usuario es administrador o
   *                                gestor.
   * @return {@link SolicitudDocumento} actualizado.
   */
  @Override
  @Transactional
  public SolicitudDocumento update(SolicitudDocumento solicitudDocumento, Boolean isAdministradorOrGestor) {
    log.debug("update(SolicitudDocumento solicitudDocumento) - start");

    Assert.notNull(solicitudDocumento.getId(), "Id no puede ser null para actualizar SolicitudDocumento");

    Assert.notNull(solicitudDocumento.getSolicitud().getId(),
        "Solicitud id no puede ser null para crear una SolicitudModalidad");

    Assert.notNull(solicitudDocumento.getNombre(),
        "Nombre documento no puede ser null para actualizar la SolicitudDocumento");
    Assert.notNull(solicitudDocumento.getDocumentoRef(),
        "La referencia del documento no puede ser null para actualizar la SolicitudDocumento");
    if (isAdministradorOrGestor) {

      Assert.isTrue(
          solicitudDocumento.getSolicitud().getEstado().getEstado().getValue()
              .equals(TipoEstadoSolicitudEnum.BORRADOR.getValue())
              || solicitudDocumento.getSolicitud().getEstado().getEstado().getValue()
                  .equals(TipoEstadoSolicitudEnum.PRESENTADA.getValue())
              || solicitudDocumento.getSolicitud().getEstado().getEstado().getValue()
                  .equals(TipoEstadoSolicitudEnum.ADMITIDA_PROVISIONAL.getValue())
              || solicitudDocumento.getSolicitud().getEstado().getEstado().getValue()
                  .equals(TipoEstadoSolicitudEnum.ALEGADA_ADMISION.getValue())
              || solicitudDocumento.getSolicitud().getEstado().getEstado().getValue()
                  .equals(TipoEstadoSolicitudEnum.ADMITIDA_DEFINITIVA.getValue())
              || solicitudDocumento.getSolicitud().getEstado().getEstado().getValue()
                  .equals(TipoEstadoSolicitudEnum.CONCECIDA_PROVISIONAL.getValue())
              || solicitudDocumento.getSolicitud().getEstado().getEstado().getValue()
                  .equals(TipoEstadoSolicitudEnum.ALEGADA_CONCESION.getValue()),
          "La solicitud asociada no se encuentra en un estado adecuado para ser actualizada");
    } else {
      Assert.isTrue(
          solicitudDocumento.getSolicitud().getEstado().getEstado().getValue()
              .equals(TipoEstadoSolicitudEnum.BORRADOR.getValue())
              || solicitudDocumento.getSolicitud().getEstado().getEstado().getValue()
                  .equals(TipoEstadoSolicitudEnum.EXCLUIDA_PROVISIONAL.getValue())
              || solicitudDocumento.getSolicitud().getEstado().getEstado().getValue()
                  .equals(TipoEstadoSolicitudEnum.DENEGADA_PROVISIONAL.getValue()),
          "La solicitud asociada no se encuentra en un estado adecuado para ser actualizada");
    }

    return repository.findById(solicitudDocumento.getId()).map((solicitudDocumentoExistente) -> {

      solicitudDocumentoExistente.setComentario(solicitudDocumento.getComentario());
      solicitudDocumentoExistente.setDocumentoRef(solicitudDocumento.getDocumentoRef());
      solicitudDocumentoExistente.setTipoDocumento(solicitudDocumento.getTipoDocumento());
      solicitudDocumentoExistente.setNombre(solicitudDocumento.getNombre());

      SolicitudDocumento returnValue = repository.save(solicitudDocumentoExistente);

      log.debug("update(SolicitudDocumento solicitudDocumento) - end");
      return returnValue;
    }).orElseThrow(() -> new SolicitudDocumentoNotFoundException(solicitudDocumento.getId()));
  }

  /**
   * Comprueba la existencia del {@link SolicitudDocumento} por id.
   *
   * @param id el id de la entidad {@link SolicitudDocumento}.
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
   * Obtiene una entidad {@link SolicitudDocumento} por id.
   * 
   * @param id Identificador de la entidad {@link SolicitudDocumento}.
   * @return SolicitudDocumento la entidad {@link SolicitudDocumento}.
   */
  @Override
  public SolicitudDocumento findById(Long id) {
    log.debug("findById(Long id) - start");
    final SolicitudDocumento returnValue = repository.findById(id)
        .orElseThrow(() -> new SolicitudDocumentoNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina la {@link SolicitudDocumento}.
   *
   * @param id Id del {@link SolicitudDocumento}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id, "SolicitudDocumento id no puede ser null para eliminar un SolicitudDocumento");
    if (!repository.existsById(id)) {
      throw new SolicitudDocumentoNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");

  }

  /**
   * Obtiene las {@link SolicitudDocumento} para una {@link Solicitud}.
   *
   * @param solicitudId el id de la {@link Solicitud}.
   * @param query       la información del filtro.
   * @param paging      la información de la paginación.
   * @return la lista de entidades {@link SolicitudDocumento} de la
   *         {@link Solicitud} paginadas.
   */
  @Override
  public Page<SolicitudDocumento> findAllBySolicitud(Long solicitudId, List<QueryCriteria> query, Pageable paging) {
    log.debug("findAllBySolicitud(Long solicitudId, List<QueryCriteria> query, Pageable paging) - start");

    Specification<SolicitudDocumento> specByQuery = new QuerySpecification<SolicitudDocumento>(query);
    Specification<SolicitudDocumento> specBySolicitud = SolicitudDocumentoSpecifications.bySolicitudId(solicitudId);
    Specification<SolicitudDocumento> specs = Specification.where(specByQuery).and(specBySolicitud);

    Page<SolicitudDocumento> returnValue = repository.findAll(specs, paging);
    log.debug("findAllBySolicitud(Long solicitudId, List<QueryCriteria> query, Pageable paging) - end");
    return returnValue;
  }

}
