package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.SocioPeriodoJustificacionDocumento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link SocioPeriodoJustificacionDocumento}.
 */
public interface SocioPeriodoJustificacionDocumentoService {

  /**
   * Actualiza el listado de {@link SocioPeriodoJustificacionDocumento} dela
   * {@link ProyectoSocioPeriodoJustificacion} con el listado
   * pIdroyectoSocioPeriodoJustificaciones añadiendo, editando o eliminando los
   * elementos segun proceda.
   *
   * @param proyectoSocioPeriodoJustificacionId  Id de la
   *                                             {@link ProyectoSocioPeriodoJustificacion}.
   * @param socioPeriodoJustificacionDocumentoes lista con los nuevos
   *                                             {@link SocioPeriodoJustificacionDocumento}
   *                                             a guardar.
   * @return la entidad {@link SocioPeriodoJustificacionDocumento} persistida.
   */
  List<SocioPeriodoJustificacionDocumento> update(Long proyectoSocioPeriodoJustificacionId,
      List<SocioPeriodoJustificacionDocumento> socioPeriodoJustificacionDocumentoes);

  /**
   * Obtiene {@link SocioPeriodoJustificacionDocumento} por su id.
   *
   * @param id el id de la entidad {@link SocioPeriodoJustificacionDocumento}.
   * @return la entidad {@link SocioPeriodoJustificacionDocumento}.
   */
  SocioPeriodoJustificacionDocumento findById(Long id);

  /**
   * Obtiene las {@link SocioPeriodoJustificacionDocumento} para una
   * {@link ProyectoSocioPeriodoJustificacion}.
   *
   * @param idProyectoSocioPeriodoJustificacion el id de la
   *                                            {@link ProyectoSocioPeriodoJustificacion}.
   * @param query                               la información del filtro.
   * @param pageable                            la información de la paginación.
   * @return la lista de entidades {@link SocioPeriodoJustificacionDocumento} de
   *         la {@link ProyectoSocioPeriodoJustificacion} paginadas.
   */
  Page<SocioPeriodoJustificacionDocumento> findAllByProyectoSocioPeriodoJustificacion(
      Long idProyectoSocioPeriodoJustificacion, String query, Pageable pageable);

  /**
   * Obtiene las {@link SocioPeriodoJustificacionDocumento} para una
   * {@link Proyecto}.
   *
   * @param idProyecto el id de la {@link Proyecto}.
   * @return la lista de entidades {@link SocioPeriodoJustificacionDocumento} de
   *         la {@link Proyecto}.
   */
  List<SocioPeriodoJustificacionDocumento> findAllByProyecto(Long idProyecto);

}