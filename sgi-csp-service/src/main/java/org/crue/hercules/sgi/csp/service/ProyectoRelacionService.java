package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.dto.rel.ProyectoRelacionOutput;
import org.crue.hercules.sgi.csp.dto.rel.RelacionEntidadOutput;
import org.crue.hercules.sgi.csp.dto.rel.RelacionOutput;
import org.crue.hercules.sgi.csp.dto.rel.RelacionOutput.TipoEntidad;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiRelService;
import org.crue.hercules.sgi.csp.util.ProyectoHelper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Pasarela de acceso a las relaciones de un {@link Proyecto} almacenadas en el
 * módulo REL.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProyectoRelacionService {

  private final ProyectoHelper proyectoHelper;
  private final SgiApiRelService sgiApiRelService;
  private final RelacionEntidadResolver relacionEntidadResolver;

  /**
   * Devuelve las relaciones del {@link Proyecto} indicado con los datos mínimos
   * de la entidad relacionada, comprobando previamente que el usuario actual
   * tiene acceso al proyecto.
   *
   * @param proyectoId identificador del proyecto
   * @return la lista de relaciones del proyecto enriquecidas
   */
  public List<ProyectoRelacionOutput> findRelacionesProyecto(Long proyectoId) {
    log.debug("findRelacionesProyecto - proyectoId: {}", proyectoId);
    proyectoHelper.checkCanAccessProyecto(proyectoId,
        ProyectoHelper.InvestigadorAccessConstraint.ROL_PRINCIPAL_ACTUAL_VISTA_AMPLIADA);

    return sgiApiRelService.findRelacionesProyecto(proyectoId).stream()
        .map(relacion -> toProyectoRelacionOutput(relacion, proyectoId))
        .toList();
  }

  /**
   * Orienta la relación respecto al proyecto actual (determinando cuál de los dos
   * extremos es la entidad relacionada) y resuelve sus datos mínimos.
   *
   * @param relacion   relación recuperada de REL
   * @param proyectoId identificador del proyecto actual
   * @return la relación enriquecida
   */
  private ProyectoRelacionOutput toProyectoRelacionOutput(RelacionOutput relacion, Long proyectoId) {
    boolean origenIsProyectoActual = relacion.getTipoEntidadOrigen() == TipoEntidad.PROYECTO
        && String.valueOf(proyectoId).equals(relacion.getEntidadOrigenRef());

    TipoEntidad tipoEntidadRelacionada = origenIsProyectoActual ? relacion.getTipoEntidadDestino()
        : relacion.getTipoEntidadOrigen();
    String entidadRelacionadaRef = origenIsProyectoActual ? relacion.getEntidadDestinoRef()
        : relacion.getEntidadOrigenRef();

    Long entidadRelacionadaId = Long.valueOf(entidadRelacionadaRef);

    return ProyectoRelacionOutput.builder()
        .id(relacion.getId())
        .tipoEntidadRelacionada(tipoEntidadRelacionada)
        .entidadRelacionada(resolveEntidadRelacionada(tipoEntidadRelacionada, entidadRelacionadaId))
        .observaciones(relacion.getObservaciones())
        .build();
  }

  /**
   * Resuelve los datos mínimos de la entidad relacionada. Si el resolver falla
   * (p.ej. el módulo de origen no está disponible), el resultado contiene
   * únicamente su identificador para no impedir la recuperación del resto de
   * relaciones.
   *
   * @param tipoEntidadRelacionada tipo de la entidad relacionada
   * @param entidadRelacionadaId   identificador de la entidad relacionada
   * @return la entidad relacionada enriquecida o, en caso de error, reducida a su
   *         identificador
   */
  private RelacionEntidadOutput resolveEntidadRelacionada(TipoEntidad tipoEntidadRelacionada,
      Long entidadRelacionadaId) {
    try {
      return relacionEntidadResolver.resolve(tipoEntidadRelacionada, entidadRelacionadaId);
    } catch (Exception e) {
      log.warn("No se pudo resolver la entidad relacionada {} con id {}: {}", tipoEntidadRelacionada,
          entidadRelacionadaId, e.getMessage());

      return RelacionEntidadOutput.builder()
          .id(entidadRelacionadaId)
          .build();
    }
  }

}
