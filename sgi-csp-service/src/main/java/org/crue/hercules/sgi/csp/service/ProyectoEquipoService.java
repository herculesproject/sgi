package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Servicio interfaz para la gestión de {@link ProyectoSocioService}.
 */
public interface ProyectoEquipoService {

  /**
   * Actualiza el listado de {@link ProyectoEquipo} de la {@link Proyecto} con el
   * listado convocatoriaPeriodoJustificaciones añadiendo, editando o eliminando
   * los elementos segun proceda.
   *
   * @param proyectoId      Id de la {@link Proyecto}.
   * @param proyectoEquipos lista con los nuevos {@link ProyectoEquipo} a guardar.
   * @return la entidad {@link ProyectoEquipo} persistida.
   */
  List<ProyectoEquipo> update(Long proyectoId, List<ProyectoEquipo> proyectoEquipos);

  /**
   * Obtiene una entidad {@link ProyectoEquipo} por id.
   * 
   * @param id Identificador de la entidad {@link ProyectoEquipo}.
   * @return la entidad {@link ProyectoEquipo}.
   */
  ProyectoEquipo findById(final Long id);

  /**
   * Obtiene todas las entidades {@link ProyectoEquipo} paginadas y filtradas para
   * un {@link Proyecto}.
   *
   * @param proyectoId el id de la {@link Proyecto}.
   * @param query      información del filtro.
   * @param paging     información de paginación.
   * @return el listado de entidades {@link ProyectoEquipo} del {@link Proyecto}
   *         paginadas y filtradas.
   */
  Page<ProyectoEquipo> findAllByProyecto(Long proyectoId, String query, Pageable paging);

}
