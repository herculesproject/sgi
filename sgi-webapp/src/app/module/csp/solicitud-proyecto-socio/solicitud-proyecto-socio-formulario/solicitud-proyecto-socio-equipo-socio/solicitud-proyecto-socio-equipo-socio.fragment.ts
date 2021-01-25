import { OnDestroy } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { Fragment } from '@core/services/action-service';
import { Subscription, BehaviorSubject, Observable, from } from 'rxjs';
import { ISolicitudProyectoEquipoSocio } from '@core/models/csp/solicitud-proyecto-equipo-socio';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { SolicitudProyectoSocioService } from '@core/services/csp/solicitud-proyecto-socio.service';
import { map, tap, takeLast, switchMap, mergeMap } from 'rxjs/operators';
import { SolicitudProyectoEquipoSocioService } from '@core/services/csp/solicitud-proyecto-equipo-socio.service';
import { SolicitudProyectoSocioActionService } from '../../solicitud-proyecto-socio.action.service';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';

export class SolicitudProyectoSocioEquipoSocioFragment extends Fragment implements OnDestroy {
  private subscriptions: Subscription[] = [];
  proyectoEquipoSocios$ = new BehaviorSubject<StatusWrapper<ISolicitudProyectoEquipoSocio>[]>([]);

  constructor(
    private logger: NGXLogger,
    key: number,
    private solicitudProyectoSocioService: SolicitudProyectoSocioService,
    private solicitudProyectoEquipoSocioService: SolicitudProyectoEquipoSocioService,
    private personaFisicaService: PersonaFisicaService
  ) {
    super(key);
    this.logger.debug(SolicitudProyectoSocioEquipoSocioFragment.name, 'constructor()', 'start');
    this.setComplete(true);
    this.logger.debug(SolicitudProyectoSocioEquipoSocioFragment.name, 'constructor()', 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(SolicitudProyectoSocioEquipoSocioFragment.name, `ngOnDestroy()`, 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(SolicitudProyectoSocioEquipoSocioFragment.name, `ngOnDestroy()`, 'end');
  }

  protected onInitialize(): void {
    this.logger.debug(SolicitudProyectoSocioEquipoSocioFragment.name, 'onInitialize()', 'start');
    if (this.getKey()) {
      const id = this.getKey() as number;
      this.subscriptions.push(
        this.solicitudProyectoSocioService.findAllSolicitudProyectoEquipoSocio(id).pipe(
          switchMap(result => {
            return from(result.items).pipe(
              mergeMap(solicitudProyectoEquipoSocio => {
                const personaRef = solicitudProyectoEquipoSocio.persona.personaRef;
                return this.personaFisicaService.getInformacionBasica(personaRef).pipe(
                  map(persona => {
                    solicitudProyectoEquipoSocio.persona = persona;
                    return solicitudProyectoEquipoSocio;
                  })
                );
              }),
              map(() => result)
            );
          }),
          map(response => response.items)
        ).subscribe(
          result => {
            this.proyectoEquipoSocios$.next(
              result.map(solicitudProyectoEquipoSocio =>
                new StatusWrapper<ISolicitudProyectoEquipoSocio>(solicitudProyectoEquipoSocio)
              )
            );
            this.logger.debug(SolicitudProyectoSocioEquipoSocioFragment.name, 'onInitialize()', 'end');
          },
          error => {
            this.logger.error(SolicitudProyectoSocioEquipoSocioFragment.name, 'onInitialize()', error);
          }
        )
      );
    }
  }

  addProyectoEquipoSocio(element: ISolicitudProyectoEquipoSocio) {
    this.logger.debug(SolicitudProyectoSocioEquipoSocioFragment.name,
      `addProyectoEquipoSocio(wrapper: ${element})`, 'start');
    const wrapped = new StatusWrapper<ISolicitudProyectoEquipoSocio>(element);
    wrapped.setCreated();
    const current = this.proyectoEquipoSocios$.value;
    current.push(wrapped);
    this.proyectoEquipoSocios$.next(current);
    this.setChanges(true);
    this.logger.debug(SolicitudProyectoSocioEquipoSocioFragment.name,
      `addProyectoEquipoSocio(wrapper: ${element})`, 'end');
  }

  deleteProyectoEquipoSocio(wrapper: StatusWrapper<ISolicitudProyectoEquipoSocio>) {
    this.logger.debug(SolicitudProyectoSocioEquipoSocioFragment.name,
      `deleteProyectoEquipoSocio(wrapper: ${wrapper})`, 'start');
    const current = this.proyectoEquipoSocios$.value;
    const index = current.findIndex((value) => value === wrapper);
    if (index >= 0) {
      current.splice(index, 1);
      this.proyectoEquipoSocios$.next(current);
      this.setChanges(true);
    }
    this.logger.debug(SolicitudProyectoSocioEquipoSocioFragment.name,
      `deleteProyectoEquipoSocio(wrapper: ${wrapper})`, 'end');
  }

  saveOrUpdate(): Observable<void> {
    this.logger.debug(SolicitudProyectoSocioEquipoSocioFragment.name, `saveOrUpdate()`, 'start');
    const values = this.proyectoEquipoSocios$.value.map(wrapper => wrapper.value);
    const id = this.getKey() as number;
    return this.solicitudProyectoEquipoSocioService.updateList(id, values)
      .pipe(
        takeLast(1),
        map((results) => {
          this.proyectoEquipoSocios$.next(
            results.map(value => new StatusWrapper<ISolicitudProyectoEquipoSocio>(value)));
        }),
        tap(() => {
          if (this.isSaveOrUpdateComplete()) {
            this.setChanges(false);
          }
          this.logger.debug(SolicitudProyectoSocioEquipoSocioFragment.name, `saveOrUpdate()`, 'end');
        })
      );
  }

  private isSaveOrUpdateComplete(): boolean {
    this.logger.debug(SolicitudProyectoSocioEquipoSocioFragment.name, `isSaveOrUpdateComplete()`, 'start');
    const hasTouched = this.proyectoEquipoSocios$.value.some((wrapper) => wrapper.touched);
    this.logger.debug(SolicitudProyectoSocioEquipoSocioFragment.name, `isSaveOrUpdateComplete()`, 'end');
    return !hasTouched;
  }
}
