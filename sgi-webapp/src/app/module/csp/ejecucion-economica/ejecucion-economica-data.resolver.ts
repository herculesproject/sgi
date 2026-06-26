import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiError } from '@core/errors/sgi-error';
import { sortGrupoEquipoByPersonaNombre, sortGrupoEquipoByRolProyectoOrden } from '@core/models/csp/grupo-equipo';
import { sortProyectoEquipoByPersonaNombre, sortProyectoEquipoByRolProyectoOrden } from '@core/models/csp/proyecto-equipo';
import { TipoEntidad } from '@core/models/csp/relacion-ejecucion-economica';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { IPersona } from '@core/models/sgp/persona';
import { Module } from '@core/module';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { ConfigService } from '@core/services/csp/configuracion/config.service';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { RelacionEjecucionEconomicaService } from '@core/services/csp/relacion-ejecucion-economica/relacion-ejecucion-economica.service';
import { ProyectoSgeService } from '@core/services/sge/proyecto-sge.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { EMPTY, forkJoin, from, Observable, of, throwError } from 'rxjs';
import { catchError, filter, map, mergeMap, switchMap, toArray } from 'rxjs/operators';
import { EJECUCION_ECONOMICA_ROUTE_PARAMS } from './ejecucion-economica-route-params';
import { IEjecucionEconomicaData } from './ejecucion-economica.action.service';

const MSG_NOT_FOUND = marker('error.load');
const MSG_PROYECTO_SGE_NOT_FOUND = marker('error.sge.proyecto.not-found');

export const EJECUCION_ECONOMICA_DATA_KEY = 'ejecucionEconomicaData';

@Injectable()
export class EjecucionEconomicaDataResolver extends SgiResolverResolver<IEjecucionEconomicaData> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private readonly relacionEjecucionEconomicaService: RelacionEjecucionEconomicaService,
    private readonly grupoService: GrupoService,
    private readonly personaService: PersonaService,
    private readonly proyectoService: ProyectoService,
    private readonly proyectoSgeService: ProyectoSgeService,
    private readonly configuracionService: ConfigService
  ) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IEjecucionEconomicaData> {

    const proyectoSgeRef = route.paramMap.get(EJECUCION_ECONOMICA_ROUTE_PARAMS.ID);
    const isInvestigador = route.data.module === Module.INV;

    return forkJoin({
      configuracion: this.configuracionService.getConfiguracion(),
      relaciones: isInvestigador
        ? this.relacionEjecucionEconomicaService.findRelacionesProyectoSgeRefInvestigador(proyectoSgeRef)
        : this.relacionEjecucionEconomicaService.findRelacionesProyectoSgeRef(proyectoSgeRef)
    }).pipe(
      map(({ configuracion, relaciones }) => {
        return {
          configuracion,
          proyectoSge: { id: proyectoSgeRef } as IProyectoSge,
          readonly: isInvestigador,
          relaciones
        } as IEjecucionEconomicaData;
      }),
      switchMap(data =>
        this.proyectoSgeService.findById(data.proyectoSge.id).pipe(
          map(proyectoSge => {
            if (!proyectoSge) {
              throw new SgiError(MSG_PROYECTO_SGE_NOT_FOUND);
            }

            data.proyectoSge = proyectoSge;
            return data;
          }),
          catchError(error => {
            if (error instanceof SgiError) {
              this.setErrorMessage(`${error.title}${!!error.message ? ': ' + error.message : ''}`);
            }
            return throwError(error);
          })
        )
      ),
      switchMap(response =>
        from(response.relaciones).pipe(
          mergeMap(relacion => {
            if (isInvestigador && !relacion.accesibleByInvestigador) {
              return of(relacion);
            }

            let responsables$: Observable<IPersona[]>;

            switch (relacion.tipoEntidad) {
              case TipoEntidad.GRUPO:
                responsables$ = this.grupoService.findInvestigadoresPrincipales(relacion.id).pipe(
                  filter(responsables => !!responsables?.length),
                  switchMap(responsables => this.personaService.findAllByIdIn(responsables.map(responsable => responsable.persona.id)).pipe(
                    map(personas => {
                      responsables.forEach(responsable => {
                        responsable.persona = personas.items.find(persona => persona.id === responsable.persona.id);
                      });

                      responsables.sort((a, b) => {
                        return sortGrupoEquipoByRolProyectoOrden(a, b)
                          || sortGrupoEquipoByPersonaNombre(a, b);
                      });

                      return responsables.map(responsable => responsable.persona);
                    }),
                    catchError((error) => {
                      this.logger.error(error);
                      return EMPTY;
                    })
                  ))
                );

                break;
              case TipoEntidad.PROYECTO:
                responsables$ = this.proyectoService.findInvestigadoresPrincipalesActuales(relacion.id).pipe(
                  filter(responsables => !!responsables?.length),
                  switchMap(responsables => this.personaService.findAllByIdIn(responsables.map(responsable => responsable.persona.id)).pipe(
                    map(personas => {
                      responsables.forEach(responsable => {
                        responsable.persona = personas.items.find(persona => persona.id === responsable.persona.id);
                      });

                      responsables.sort((a, b) => {
                        return sortProyectoEquipoByRolProyectoOrden(a, b)
                          || sortProyectoEquipoByPersonaNombre(a, b);
                      });

                      return responsables.map(responsable => responsable.persona);
                    }),
                    catchError((error) => {
                      this.logger.error(error);
                      return EMPTY;
                    })
                  ))
                );

                break;
              default:
                throw Error(`Invalid tipoEntidad "${relacion.tipoEntidad}"`);
            }

            return responsables$.pipe(
              map(responsables => {
                relacion.responsables = responsables;
                return relacion;
              }),
              catchError(error => {
                this.logger.error(error);
                return EMPTY;
              })
            );
          }),
          toArray(),
          map(() => {
            return response;
          })
        )
      )
    );
  }

}
