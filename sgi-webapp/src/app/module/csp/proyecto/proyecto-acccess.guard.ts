import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  CanActivateChild,
  Router,
  UrlTree
} from '@angular/router';
import { IProyecto } from '@core/models/csp/proyecto';
import { Module } from '@core/module';
import { ConfigService } from '@core/services/csp/configuracion/config.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { SgiAuthService } from '@herculesproject/framework/auth';
import { forkJoin, Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { INV_ROUTE_NAMES } from '../../inv/inv-route-names';
import { CSP_ROUTE_NAMES } from '../csp-route-names';
import { PROYECTO_ROUTE_PARAMS } from './proyecto-route-params';

export enum InvestigadorAccessConstraint {
  ROL_PRINCIPAL_ACTUAL = 'ROL_PRINCIPAL_ACTUAL',
  ROL_PRINCIPAL_ACTUAL_VISTA_AMPLIADA = 'ROL_PRINCIPAL_ACTUAL_VISTA_AMPLIADA',
  NONE = 'NONE'
}

@Injectable()
export class ProyectoAccessGuard implements CanActivate, CanActivateChild {

  constructor(
    private readonly router: Router,
    private readonly proyectoService: ProyectoService,
    private readonly configService: ConfigService,
    private readonly authService: SgiAuthService
  ) { }

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.checkAccess(route);
  }

  canActivateChild(childRoute: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.checkAccess(childRoute);
  }

  private checkAccess(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const accessConstraint: InvestigadorAccessConstraint =
      this.resolveData<InvestigadorAccessConstraint>(route, 'accessConstraint');
    const currentModule: Module = this.resolveData<Module>(route, 'module');
    const proyectoId = this.resolveProyectoId(route);

    if (!proyectoId || !currentModule) {
      return of(this.router.createUrlTree(['/']));
    }

    return currentModule === Module.INV
      ? this.checkAccessModeleInv(accessConstraint, proyectoId)
      : this.checkAccessModuleCSP(proyectoId);
  }

  /**
   * Devuelve el primer valor definido de route.data[key] subiendo desde la ruta actual hasta la raíz.
   *
   * @param route la ruta
   * @param key nombre del parámetro
   * @returns el valor del parámetro
   */
  private resolveData<T>(route: ActivatedRouteSnapshot, key: string): T {
    let current: ActivatedRouteSnapshot = route;
    while (current) {
      const value = current.data?.[key];
      if (value !== undefined) {
        return value as T;
      }

      current = current.parent;
    }

    return undefined;
  }

  /**
   * Devuelve el valor del parámetro proyectoId subiendo desde la ruta actual hasta la raíz.
   *
   * @param route la ruta
   * @returns el valor del parámetro
   */
  private resolveProyectoId(route: ActivatedRouteSnapshot): number {
    let current: ActivatedRouteSnapshot = route;
    while (current) {
      const id = current.paramMap.get(PROYECTO_ROUTE_PARAMS.ID);
      if (id) {
        return Number(id);
      }

      current = current.parent;
    }

    return undefined;
  }

  /**
   * Comprueba si el usuario tiene acceso al proyecto desde el módulo CSP
   *
   * @param proyectoId Identificador del proyecto
   * @returns el valor del parámetro
   */
  private checkAccessModuleCSP(
    proyectoId: number
  ): Observable<boolean | UrlTree> {
    return this.proyectoService.findById(proyectoId).pipe(
      map((proyecto) => {
        const hasGestorAuthority = this.hasViewAuthorityUO(proyecto) || this.hasEditAuthorityUO(proyecto);
        const hasVisorAuthority = this.hasVisorAuthority(proyecto);

        if (!hasGestorAuthority && !hasVisorAuthority) {
          return this.router.createUrlTree(['/', Module.CSP.path, CSP_ROUTE_NAMES.PROYECTO]);
        }

        return true;
      })
    );
  }

  /**
   * Comprueba si el usuario tiene acceso al proyecto desde el módulo INV
   *
   * @param accessConstraint Restricciones de acceso
   * @param proyectoId Identificador del proyecto
   * @returns el valor del parámetro
   */
  private checkAccessModeleInv(
    accessConstraint: InvestigadorAccessConstraint,
    proyectoId: number
  ): Observable<boolean | UrlTree> {
    const hasInvestigadorAuthority = this.hasViewAuthorityInv();
    if (!hasInvestigadorAuthority) {
      return of(this.router.createUrlTree(['/', Module.INV.path, INV_ROUTE_NAMES.PROYECTOS]));
    }

    return forkJoin({
      isInvProyectoVistaAmpliadaIpEnabled: this.configService.isInvProyectoVistaAmpliadaIpEnabled(),
      isInvestigadorPrincipalActual: this.proyectoService.isInvestigadorPrincipalActual(proyectoId)
    }).pipe(
      map(({ isInvProyectoVistaAmpliadaIpEnabled, isInvestigadorPrincipalActual }) => {

        if (!this.evaluateAccessConstraint(accessConstraint, isInvestigadorPrincipalActual, isInvProyectoVistaAmpliadaIpEnabled)) {
          return this.router.createUrlTree(['/', Module.INV.path, INV_ROUTE_NAMES.PROYECTOS]);
        }

        return true;
      })
    );

  }

  private evaluateAccessConstraint(
    constraint: InvestigadorAccessConstraint,
    isInvestigadorPrincipalActual: boolean,
    isInvProyectoVistaAmpliadaIpEnabled: boolean
  ): boolean {
    switch (constraint) {
      case InvestigadorAccessConstraint.ROL_PRINCIPAL_ACTUAL:
        return isInvestigadorPrincipalActual;

      case InvestigadorAccessConstraint.ROL_PRINCIPAL_ACTUAL_VISTA_AMPLIADA:
        return isInvestigadorPrincipalActual && isInvProyectoVistaAmpliadaIpEnabled;

      case InvestigadorAccessConstraint.NONE:
        return true;

      default:
        return false;
    }
  }

  private hasViewAuthorityInv(): boolean {
    return this.authService.hasAuthority('CSP-PRO-INV-VR');
  }

  private hasViewAuthorityUO(proyecto: IProyecto): boolean {
    return this.authService.hasAnyAuthority(
      [
        'CSP-PRO-E',
        'CSP-PRO-E_' + proyecto.unidadGestion.id,
        'CSP-PRO-V',
        'CSP-PRO-V_' + proyecto.unidadGestion.id
      ]
    );
  }

  private hasEditAuthorityUO(proyecto: IProyecto): boolean {
    return this.authService.hasAnyAuthority(
      [
        'CSP-PRO-E',
        'CSP-PRO-E_' + proyecto.unidadGestion.id
      ]
    );
  }

  private hasVisorAuthority(proyecto: IProyecto): boolean {
    return !this.hasEditAuthorityUO(proyecto) && this.authService.hasAnyAuthority(
      [
        'CSP-PRO-V',
        'CSP-PRO-V_' + proyecto.unidadGestion.id
      ]
    );
  }

}

