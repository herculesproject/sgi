package org.crue.hercules.sgi.csp.util;

import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToAccessEjecucionEconomicaException;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.repository.GrupoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoProyectoSgeRepository;
import org.crue.hercules.sgi.csp.repository.specification.GrupoSpecifications;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoProyectoSgeSpecifications;
import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * Comprobaciones de acceso de un investigador (CSP-EJEC-INV-VR) a la ejecucion
 * economica de un proyecto del SGE.
 */
@Component
@RequiredArgsConstructor
public class EjecucionEconomicaInvestigadorAuthorityHelper extends AuthorityHelper {
  public static final String CSP_EJEC_INV_VR = "CSP-EJEC-INV-VR";

  private final ProyectoProyectoSgeRepository proyectoProyectoSgeRepository;
  private final GrupoRepository grupoRepository;
  private final ProyectoHelper proyectoHelper;

  /**
   * Comprueba que el usuario actual puede acceder a la ejecucion economica del
   * {@code proyectoSgeRef} indicado como investigador.
   *
   * @param proyectoSgeRef identificador del proyecto en el SGE.
   */
  public void checkAccessProyectoSgeRef(String proyectoSgeRef) {
    if (!hasUserAuthorityViewInvestigador() || !isAccesibleByInvestigador(proyectoSgeRef)) {
      throw new UserNotAuthorizedToAccessEjecucionEconomicaException();
    }
  }

  /**
   * Comprueba si el usuario actual tiene la autorización de investigador.
   *
   * @return {@code true} si tiene el rol CSP-EJEC-INV-VR.
   */
  public boolean hasUserAuthorityViewInvestigador() {
    return SgiSecurityContextHolder.hasAuthority(CSP_EJEC_INV_VR);
  }

  /**
   * Comprueba si el usuario actual es investigador principal de algun proyecto o
   * responsable de algun grupo vinculado al {@code proyectoSgeRef} indicado, en
   * cualquier fecha.
   *
   * @param proyectoSgeRef identificador del proyecto en el SGE.
   * @return {@code true} si el usuario tiene acceso al {@code proyectoSgeRef}.
   */
  public boolean isAccesibleByInvestigador(String proyectoSgeRef) {
    String personaRef = getAuthenticationPersonaRef();

    boolean accesibleComoIpProyecto = proyectoProyectoSgeRepository.count(
        ProyectoProyectoSgeSpecifications.byProyectoSgeRef(proyectoSgeRef)
            .and(ProyectoProyectoSgeSpecifications.byPersonaInProyectoEquipoRolPrincipal(personaRef, null))) > 0;
    if (accesibleComoIpProyecto) {
      return true;
    }

    return grupoRepository.count(
        GrupoSpecifications.byProyectoSgeRef(proyectoSgeRef)
            .and(GrupoSpecifications.byResponsable(personaRef))) > 0;
  }

  /**
   * Comprueba si el usuario actual es investigador principal (rol principal) del
   * {@link Proyecto} indicado, en cualquier fecha.
   *
   * @param proyectoId identificador del {@link Proyecto}.
   * @return {@code true} si el usuario es IP del proyecto.
   */
  public boolean isInvestigadorPrincipalProyecto(Long proyectoId) {
    return proyectoHelper.checkIfUserIsInvestigadorPrincipal(proyectoId);
  }

  /**
   * Comprueba si el usuario actual es responsable (rol principal) del
   * {@link Grupo} indicado, en cualquier fecha.
   *
   * @param grupoId identificador del {@link Grupo}.
   * @return {@code true} si el usuario es responsable del grupo.
   */
  public boolean isResponsableGrupo(Long grupoId) {
    return grupoRepository.count(
        GrupoSpecifications.byId(grupoId)
            .and(GrupoSpecifications.byResponsable(getAuthenticationPersonaRef()))) > 0;
  }

}
