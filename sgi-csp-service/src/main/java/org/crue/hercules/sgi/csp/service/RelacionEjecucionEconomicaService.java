package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;

import org.crue.hercules.sgi.csp.dto.RelacionEjecucionEconomica;
import org.crue.hercules.sgi.csp.util.EjecucionEconomicaInvestigadorAuthorityHelper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service para la obtención de {@link RelacionEjecucionEconomica}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RelacionEjecucionEconomicaService {

  private final ProyectoService proyectoService;
  private final GrupoService grupoService;
  private final EjecucionEconomicaInvestigadorAuthorityHelper ejecucionEconomicaInvestigadorAuthorityHelper;

  /**
   * Obtiene las {@link RelacionEjecucionEconomica} (proyectos y grupos) asociadas
   * al {@code proyectoSgeRef} para el acceso con perfil de investigador.
   *
   * @param proyectoSgeRef identificador del proyecto en el SGE.
   * @return el listado de {@link RelacionEjecucionEconomica} de la referencia.
   */
  public List<RelacionEjecucionEconomica> findByProyectoSgeRefInvestigador(String proyectoSgeRef) {
    log.debug("findByProyectoSgeRefInvestigador - proyectoSgeRef: {}", proyectoSgeRef);

    ejecucionEconomicaInvestigadorAuthorityHelper.checkAccessProyectoSgeRef(proyectoSgeRef);

    String query = "proyectoSgeRef==" + proyectoSgeRef;
    List<RelacionEjecucionEconomica> relaciones = new ArrayList<>();
    relaciones
        .addAll(proyectoService.findRelacionesEjecucionEconomicaProyectos(query, Pageable.unpaged()).getContent());
    relaciones.addAll(grupoService.findRelacionesEjecucionEconomicaGrupos(query, Pageable.unpaged()).getContent());

    relaciones.forEach(relacion -> relacion.setAccesibleByInvestigador(
        relacion.getTipoEntidad() == RelacionEjecucionEconomica.TipoEntidad.PROYECTO
            ? ejecucionEconomicaInvestigadorAuthorityHelper.isInvestigadorPrincipalProyecto(relacion.getId())
            : ejecucionEconomicaInvestigadorAuthorityHelper.isResponsableGrupo(relacion.getId())));

    log.debug("findByProyectoSgeRefInvestigador - relaciones: {}", relaciones.size());
    return relaciones;
  }

}
