package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.SocioPeriodoJustificacionDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SocioPeriodoJustificacionDocumentoRepository
    extends JpaRepository<SocioPeriodoJustificacionDocumento, Long>,
    JpaSpecificationExecutor<SocioPeriodoJustificacionDocumento> {

  /**
   * Recupera todos los {@link SocioPeriodoJustificacionDocumento} asociados a un
   * {@link ProyectoSocioPeriodoJustificacion}.
   * 
   * @param proyectoSocioPeriodoJustificacionId Identificador de
   *                                            {@link ProyectoSocioPeriodoJustificacion}.
   * @return listado de {@link SocioPeriodoJustificacionDocumento}
   */
  List<SocioPeriodoJustificacionDocumento> findAllByProyectoSocioPeriodoJustificacionId(
      Long proyectoSocioPeriodoJustificacionId);

  /**
   * Elimina los {@link SocioPeriodoJustificacionDocumento} asociados a los
   * {@link ProyectoSocioPeriodoJustificacion}.
   * 
   * @param periodoJustificacionId Lista de identificadores de
   *                               {@link ProyectoSocioPeriodoJustificacion}.
   */
  void deleteByProyectoSocioPeriodoJustificacionIdIn(List<Long> periodoJustificacionId);

  /**
   * Elimina todos los {@link SocioPeriodoJustificacionDocumento} asociados a un
   * {@link ProyectoSocio}.
   * 
   * @param id Identificador de {@link ProyectoSocio}.
   */
  void deleteByProyectoSocioPeriodoJustificacionProyectoSocioId(Long id);
}
