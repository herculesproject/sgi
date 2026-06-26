package org.crue.hercules.sgi.csp.util;

import java.time.Instant;
import java.util.List;

import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToAccessProyectoException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToCreateProyectoException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToModifyProyectoException;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.repository.ProyectoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoResponsableEconomicoRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoEquipoSpecifications;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoResponsableEconomicoSpecifications;
import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Primary
@Component
@RequiredArgsConstructor
public class ProyectoHelper extends AuthorityHelper {
  public static final String CSP_PRO_B = "CSP-PRO-B";
  public static final String CSP_PRO_C = "CSP-PRO-C";
  public static final String CSP_PRO_E = "CSP-PRO-E";
  public static final String CSP_PRO_INV_VR = "CSP-PRO-INV-VR";
  public static final String CSP_PRO_MOD_V = "CSP-PRO-MOD-V";
  public static final String CSP_PRO_R = "CSP-PRO-R";
  public static final String CSP_PRO_V = "CSP-PRO-V";

  private final ProyectoRepository repository;
  private final ProyectoEquipoRepository proyectoEquipoRepository;
  private final ProyectoResponsableEconomicoRepository proyectoResponsableEconomicoRepository;

  /**
   * Restricción de acceso aplicada a los investigadores sobre un
   * {@link Proyecto}.
   */
  public enum InvestigadorAccessConstraint {
    /** Sin restricción adicional: cualquier investigador con autoridad de vista. */
    NONE,
    /** Investigador con rol principal y participación vigente en el equipo. */
    ROL_PRINCIPAL_ACTUAL,
    /**
     * Investigador con rol principal y participación vigente en el equipo, para la
     * vista ampliada.
     */
    ROL_PRINCIPAL_ACTUAL_VISTA_AMPLIADA
  }

  /**
   * Comprueba que el usuario actual puede acceder al {@link Proyecto} como
   * usuario de gestión o como investigador.
   *
   * @param proyecto         el {@link Proyecto} sobre el que realizar las
   *                         comprobaciones
   * @param accessConstraint restricción de acceso aplicada a los investigadores
   */
  public void checkCanAccessProyecto(Proyecto proyecto, InvestigadorAccessConstraint accessConstraint) {
    if (!hasUserAuthorityViewMod()
        && !hasUserAuthorityViewUO(proyecto)
        && !hasUserAccessAsInvestigador(proyecto.getId(), accessConstraint)) {
      throw new UserNotAuthorizedToAccessProyectoException();
    }
  }

  /**
   * Comprueba si el usuario actual tiene acceso al {@link Proyecto} como
   * investigador, según la restricción de acceso indicada.
   *
   * @param proyectoId       identificador del {@link Proyecto}
   * @param accessConstraint restricción de acceso aplicada a los investigadores
   * @return {@code true} si el investigador tiene acceso con esa restricción
   */
  private boolean hasUserAccessAsInvestigador(Long proyectoId, InvestigadorAccessConstraint accessConstraint) {
    switch (accessConstraint) {
      case NONE:
        return hasUserAuthorityViewInvestigador();
      case ROL_PRINCIPAL_ACTUAL:
        return isInvestigadorPrincipalActual(proyectoId);
      case ROL_PRINCIPAL_ACTUAL_VISTA_AMPLIADA:
        return hasUserAuthorityViewInvestigador()
            && (checkUserPresentInEquipos(proyectoId) || checkUserIsResponsableEconomico(proyectoId));
      default:
        return false;
    }
  }

  protected boolean hasUserAuthorityViewInvestigador() {
    return SgiSecurityContextHolder.hasAuthorityForAnyUO(CSP_PRO_INV_VR);
  }

  public boolean hasUserAuthorityViewUO(Proyecto proyecto) {
    return SgiSecurityContextHolder.hasAnyAuthorityForUO(
        new String[] {
            CSP_PRO_V,
            CSP_PRO_E,
            CSP_PRO_MOD_V
        },
        proyecto.getUnidadGestionRef());
  }

  /**
   * Variante de
   * {@link #checkCanAccessProyecto(Proyecto, InvestigadorAccessConstraint)}
   * que recupera el {@link Proyecto} a partir de su identificador.
   *
   * @param proyectoId       identificador del {@link Proyecto}
   * @param accessConstraint restricción de acceso aplicada a los investigadores
   */
  public void checkCanAccessProyecto(Long proyectoId, InvestigadorAccessConstraint accessConstraint) {
    checkCanAccessProyecto(
        repository.findById(proyectoId).orElseThrow(() -> new ProyectoNotFoundException(proyectoId)), accessConstraint);
  }

  public void checkCanCreateProyecto(Proyecto proyecto) {
    if (!SgiSecurityContextHolder.hasAuthorityForUO(CSP_PRO_C, proyecto.getUnidadGestionRef())) {
      throw new UserNotAuthorizedToCreateProyectoException();
    }
  }

  public void checkUserHasAuthorityModifyProyecto(Long proyectoId) {
    checkUserHasAuthorityModifyProyecto(
        repository.findById(proyectoId).orElseThrow(() -> new ProyectoNotFoundException(proyectoId)));
  }

  public void checkUserHasAuthorityModifyProyecto(Proyecto proyecto) {
    if (!SgiSecurityContextHolder.hasAuthorityForUO(CSP_PRO_E, proyecto.getUnidadGestionRef())) {
      throw new UserNotAuthorizedToModifyProyectoException();
    }
  }

  public boolean hasUserAuthorityModifyProyecto(Long proyectoId) {
    return hasUserAuthorityModifyProyecto(
        repository.findById(proyectoId).orElseThrow(() -> new ProyectoNotFoundException(proyectoId)));
  }

  public boolean checkUserIsResponsableEconomico(Long proyectoId) {
    return this.proyectoResponsableEconomicoRepository
        .count(ProyectoResponsableEconomicoSpecifications.byProyectoId(proyectoId)
            .and(ProyectoResponsableEconomicoSpecifications.byPersonaRef(getAuthenticationPersonaRef()))) > 0;
  }

  protected boolean checkUserPresentInEquipos(Long proyectoId) {
    return this.proyectoEquipoRepository
        .count(ProyectoEquipoSpecifications.byProyectoId(proyectoId)
            .and(ProyectoEquipoSpecifications.byPersonaRef(getAuthenticationPersonaRef())
                .and(ProyectoEquipoSpecifications.byRolPrincipal(true)))) > 0;
  }

  public boolean checkIfUserIsInvestigadorPrincipal(Long proyectoId) {
    return this.proyectoEquipoRepository.existsByProyectoIdAndPersonaRefAndRolProyectoRolPrincipalTrue(proyectoId,
        getAuthenticationPersonaRef());
  }

  private boolean isInvestigadorPrincipalActual(Long proyectoId) {
    if (!hasUserAuthorityViewInvestigador()) {
      return false;
    }
    return proyectoEquipoRepository.count(
        ProyectoEquipoSpecifications.byProyectoId(proyectoId)
            .and(ProyectoEquipoSpecifications.byPersonaRef(getAuthenticationPersonaRef()))
            .and(ProyectoEquipoSpecifications.byRolPrincipal(true))
            .and(ProyectoEquipoSpecifications.byFechaActual(Instant.now()))) > 0;
  }

  /**
   * Lista de unidades de gestion para las que el usuario tienen algun permiso de
   * PROYECTO (CSP_PRO_)
   * 
   * @return la lista de unidades de gestion
   */
  public List<String> getUserUOsProyecto() {
    return SgiSecurityContextHolder.getUOsForAnyAuthority(
        new String[] { CSP_PRO_B, CSP_PRO_C, CSP_PRO_E, CSP_PRO_R, CSP_PRO_V });
  }

  private boolean hasUserAuthorityModifyProyecto(Proyecto proyecto) {
    return SgiSecurityContextHolder.hasAuthorityForUO(CSP_PRO_E, proyecto.getUnidadGestionRef());
  }

  private boolean hasUserAuthorityViewMod() {
    return SgiSecurityContextHolder.hasAuthority(CSP_PRO_MOD_V);
  }

}
