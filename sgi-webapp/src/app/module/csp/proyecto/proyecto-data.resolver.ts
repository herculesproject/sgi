import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IProyecto } from '@core/models/csp/proyecto';
import { Module } from '@core/module';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { ConfigService } from '@core/services/csp/configuracion/config.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { RolSocioService } from '@core/services/csp/rol-socio/rol-socio.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthService } from '@herculesproject/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { forkJoin, Observable, of, throwError } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { PROYECTO_ROUTE_PARAMS } from './proyecto-route-params';
import { IProyectoData } from './proyecto.action.service';

const MSG_NOT_FOUND = marker('csp.proyecto.editar.no-encontrado');

export const PROYECTO_DATA_KEY = 'proyectoData';

@Injectable()
export class ProyectoDataResolver extends SgiResolverResolver<IProyectoData> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private service: ProyectoService,
    private rolSocioService: RolSocioService,
    private solicitudService: SolicitudService,
    private authService: SgiAuthService,
    private configService: ConfigService
  ) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IProyectoData> {
    return this.service.findById(Number(route.paramMap.get(PROYECTO_ROUTE_PARAMS.ID))).pipe(
      map((proyecto) => {
        return {
          proyecto,
          solicitanteRefSolicitud: null,
          solicitudFormularioSolicitud: null,
          disableRolUniversidad: false,
          hasAnyProyectoSocioCoordinador: false,
          isVisor: this.hasVisorAuthority(proyecto) && route.data.module === Module.CSP,
          isInvestigador: this.hasViewAuthorityInv() && route.data.module === Module.INV
        } as IProyectoData;
      }),
      switchMap(data => {
        if (!data.isInvestigador && !data.isVisor && !this.hasViewAuthorityUO(data.proyecto)) {
          return throwError('NOT_FOUND');
        }

        return of(data);
      }),
      switchMap(data => forkJoin({
        rolUniversidad: data.proyecto.rolUniversidad
          ? this.rolSocioService.findById(data.proyecto.rolUniversidad.id)
          : of(null),
        hasPeriodosPago: this.service.hasPeriodosPago(data.proyecto.id),
        hasPeriodosJustificacion: this.service.hasPeriodosJustificacion(data.proyecto.id),
        modificable: data?.proyecto?.id ? this.service.modificable(data.proyecto.id) : of(true),
        hasCoordinador: data?.proyecto?.id
          ? this.service.hasAnyProyectoSocioWithRolCoordinador(data.proyecto.id)
          : of(false),
        solicitud: !data.isInvestigador && data.proyecto?.solicitudId
          ? this.solicitudService.findById(data.proyecto.solicitudId)
          : of(null),
        isProyectoUnidadesVinculacionEnabled: this.configService.isProyectoUnidadesVinculacionEnabled(),
        isProyectoAreasConocimientoEnabled: this.configService.isProyectoAreasConocimientoEnabled(),
      }).pipe(
        map(({ rolUniversidad, hasPeriodosPago, hasPeriodosJustificacion, modificable, hasCoordinador, solicitud,
          isProyectoUnidadesVinculacionEnabled, isProyectoAreasConocimientoEnabled }) => {

          data.disableRolUniversidad = hasPeriodosPago || hasPeriodosJustificacion;
          data.hasAnyProyectoSocioCoordinador = hasCoordinador;
          data.isProyectoAreasConocimientoEnabled = isProyectoAreasConocimientoEnabled;
          data.isProyectoUnidadesVinculacionEnabled = isProyectoUnidadesVinculacionEnabled;
          data.readonly = !modificable;

          if (rolUniversidad) {
            data.proyecto.rolUniversidad = rolUniversidad;
          }

          if (solicitud) {
            data.solicitanteRefSolicitud = solicitud.solicitante?.id;
            data.solicitudFormularioSolicitud = solicitud.formularioSolicitud;
          }

          return data;
        })
      ))
    );
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
