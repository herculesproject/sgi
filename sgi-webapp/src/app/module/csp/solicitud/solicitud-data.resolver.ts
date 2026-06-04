import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormularioSolicitud } from '@core/enums/formulario-solicitud';
import { Estado } from '@core/models/csp/estado-solicitud';
import { ISolicitud } from '@core/models/csp/solicitud';
import { ISolicitudRrhhTutor } from '@core/models/csp/solicitud-rrhh-tutor';
import { Module } from '@core/module';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { ConfigService } from '@core/services/csp/configuracion/config.service';
import { RolSocioService } from '@core/services/csp/rol-socio/rol-socio.service';
import { SolicitudProyectoService } from '@core/services/csp/solicitud-proyecto.service';
import { SolicitudRrhhService } from '@core/services/csp/solicitud-rrhh/solicitud-rrhh.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthService } from '@herculesproject/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { forkJoin, Observable, of, throwError } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { SOLICITUD_ROUTE_PARAMS } from './solicitud-route-params';
import { ISolicitudData } from './solicitud.action.service';

const MSG_NOT_FOUND = marker('error.load');

export const SOLICITUD_DATA_KEY = 'solicitudData';

const ALLOWED_PROYECTO_LINK_ESTADOS = new Set([
  Estado.CONCEDIDA,
  Estado.CONCEDIDA_PROVISIONAL,
  Estado.CONCEDIDA_PROVISIONAL_ALEGADA,
  Estado.CONCEDIDA_PROVISIONAL_NO_ALEGADA
]);

@Injectable()
export class SolicitudDataResolver extends SgiResolverResolver<ISolicitudData> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private readonly service: SolicitudService,
    private readonly authService: SgiAuthService,
    private readonly configService: ConfigService,
    private readonly solicitudProyectoService: SolicitudProyectoService,
    private readonly solicitudRrhhService: SolicitudRrhhService,
    private readonly rolSocioService: RolSocioService
  ) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<ISolicitudData> {
    const isInvestigador = route.data.module === Module.INV && this.hasViewAuthorityInv();
    const currentUser = this.authService.authStatus$?.getValue()?.userRefId;


    return this.service.findById(Number(route.paramMap.get(SOLICITUD_ROUTE_PARAMS.ID))).pipe(
      map(solicitud => {
        return {
          solicitud
        } as ISolicitudData;
      }),
      switchMap(data => forkJoin({
        hasSolicitudProyecto: this.service.existsSolictudProyecto(data.solicitud.id),
        modificable: isInvestigador
          ? this.service.modificableByInvestigador(data.solicitud.id)
          : this.service.modificableByUO(data.solicitud.id),
      }).pipe(
        map(({ hasSolicitudProyecto, modificable }) => {
          data.hasSolicitudProyecto = hasSolicitudProyecto;
          data.readonly = !modificable;
          return data;
        })
      )),
      switchMap(data => {
        data.isInvestigador = isInvestigador;

        if (!isInvestigador) {
          data.estadoAndDocumentosReadonly = data.readonly;

          if (route.data.module === Module.CSP && this.hasViewAuthorityUO(data.solicitud)) {
            return of(data);
          }

          return throwError('NOT_FOUND');
        }

        return forkJoin(
          {
            modificableEstadoAndDocumentos: this.service.modificableEstadoAndDocumentosByInvestigador(data.solicitud.id),
            modificableEstadoAsTutor: data.solicitud.formularioSolicitud === FormularioSolicitud.RRHH
              ? this.service.modificableEstadoAsTutor(data.solicitud.id) : of(false),
            tutorRrhh: data.solicitud.formularioSolicitud === FormularioSolicitud.RRHH
              ? this.solicitudRrhhService.findTutor(data.solicitud.id) : of({} as ISolicitudRrhhTutor)
          }
        ).pipe(
          switchMap(({ modificableEstadoAndDocumentos, modificableEstadoAsTutor, tutorRrhh }) => {
            data.estadoAndDocumentosReadonly = !modificableEstadoAndDocumentos;
            data.modificableEstadoAsTutor = modificableEstadoAsTutor;
            data.isTutor = tutorRrhh?.tutor?.id === currentUser;

            if (data.isTutor || data.solicitud.solicitante?.id === currentUser) {
              return of(data);
            }

            return throwError('NOT_FOUND');
          }),

        );
      }),
      switchMap(data => forkJoin({
        solicitudProyecto: data.hasSolicitudProyecto
          ? this.service.findSolicitudProyecto(data.solicitud.id)
          : of(null),
        proyectosIds: ALLOWED_PROYECTO_LINK_ESTADOS.has(data.solicitud?.estado?.estado)
          ? this.service.findIdsProyectosBySolicitudId(data.solicitud.id)
          : of(null),
        isProyectoAreasConocimientoEnabled: this.configService.isProyectoAreasConocimientoEnabled(),
        isProyectoUnidadesVinculacionEnabled: this.configService.isProyectoUnidadesVinculacionEnabled(),
      }).pipe(
        map(({ solicitudProyecto, proyectosIds, isProyectoAreasConocimientoEnabled, isProyectoUnidadesVinculacionEnabled }) => {
          if (solicitudProyecto) {
            data.solicitudProyecto = solicitudProyecto;
          }
          if (proyectosIds) {
            data.proyectosIds = proyectosIds;
          }
          data.isProyectoAreasConocimientoEnabled = isProyectoAreasConocimientoEnabled;
          data.isProyectoUnidadesVinculacionEnabled = isProyectoUnidadesVinculacionEnabled;
          return data;
        })
      )),
      switchMap(data => forkJoin({
        rolUniversidad: data.solicitudProyecto?.rolUniversidad
          ? this.rolSocioService.findById(data.solicitudProyecto.rolUniversidad.id)
          : of(null),
        hasCoordinador: data.solicitudProyecto
          ? this.solicitudProyectoService.hasAnySolicitudProyectoSocioWithRolCoordinador(data.solicitudProyecto.id)
          : of(false),
        hasPeriodosPago: data.solicitudProyecto?.id
          ? this.solicitudProyectoService.hasPeriodosPago(data.solicitudProyecto.id)
          : of(false),
        hasPeriodosJustificacion: data.solicitudProyecto?.id
          ? this.solicitudProyectoService.hasPeriodosJustificacion(data.solicitudProyecto.id)
          : of(false),
      }).pipe(
        map(({ rolUniversidad, hasCoordinador, hasPeriodosPago, hasPeriodosJustificacion }) => {
          if (rolUniversidad) {
            data.solicitudProyecto.rolUniversidad = rolUniversidad;
          }
          data.hasAnySolicitudProyectoSocioWithRolCoordinador = hasCoordinador;
          data.hasPopulatedPeriodosSocios = hasPeriodosPago || hasPeriodosJustificacion;
          return data;
        })
      ))
    ) as Observable<ISolicitudData>;
  }

  private hasViewAuthorityInv(): boolean {
    return this.authService.hasAuthority('CSP-SOL-INV-ER');
  }

  private hasViewAuthorityUO(solicitud: ISolicitud): boolean {
    return this.authService.hasAnyAuthority(
      [
        'CSP-SOL-E',
        'CSP-SOL-E_' + solicitud.unidadGestion.id,
        'CSP-SOL-V',
        'CSP-SOL-V_' + solicitud.unidadGestion.id
      ]
    );
  }

}
