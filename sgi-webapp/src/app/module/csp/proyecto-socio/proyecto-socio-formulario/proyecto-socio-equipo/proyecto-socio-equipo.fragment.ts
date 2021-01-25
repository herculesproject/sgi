import { Fragment } from '@core/services/action-service';
import { OnDestroy } from '@angular/core';
import { Subscription, BehaviorSubject, Observable, from } from 'rxjs';
import { IProyectoSocioEquipo } from '@core/models/csp/proyecto-socio-equipo';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { tap, map, takeLast, mergeMap, switchMap } from 'rxjs/operators';
import { ProyectoSocioService } from '@core/services/csp/proyecto-socio.service';
import { ProyectoSocioEquipoService } from '@core/services/csp/proyecto-socio-equipo.service';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';

export class ProyectoSocioEquipoFragment extends Fragment implements OnDestroy {
  private subscriptions: Subscription[] = [];
  proyectoEquipoSocios$ = new BehaviorSubject<StatusWrapper<IProyectoSocioEquipo>[]>([]);

  constructor(
    private logger: NGXLogger,
    key: number,
    private proyectoSocioService: ProyectoSocioService,
    private proyectoEquipoSocioService: ProyectoSocioEquipoService,
    private personaFisicaService: PersonaFisicaService
  ) {
    super(key);
    this.logger.debug(ProyectoSocioEquipoFragment.name, 'constructor()', 'start');
    this.setComplete(true);
    this.logger.debug(ProyectoSocioEquipoFragment.name, 'constructor()', 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(ProyectoSocioEquipoFragment.name, `ngOnDestroy()`, 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(ProyectoSocioEquipoFragment.name, `ngOnDestroy()`, 'end');
  }

  protected onInitialize(): void {
    this.logger.debug(ProyectoSocioEquipoFragment.name, 'onInitialize()', 'start');
    if (this.getKey()) {
      const id = this.getKey() as number;
      this.subscriptions.push(
        this.proyectoSocioService.findAllProyectoEquipoSocio(id).pipe(
          switchMap(result => {
            return from(result.items).pipe(
              mergeMap(element => {
                const personaRef = element.persona.personaRef;
                return this.personaFisicaService.getInformacionBasica(personaRef).pipe(
                  map(persona => {
                    element.persona = persona;
                    return element;
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
                new StatusWrapper<IProyectoSocioEquipo>(solicitudProyectoEquipoSocio)
              )
            );
            this.logger.debug(ProyectoSocioEquipoFragment.name, 'onInitialize()', 'end');
          },
          error => {
            this.logger.error(ProyectoSocioEquipoFragment.name, 'onInitialize()', error);
          }
        )
      );
    }
  }

  addProyectoSocioEquipo(element: IProyectoSocioEquipo) {
    this.logger.debug(ProyectoSocioEquipoFragment.name,
      `addProyectoEquipoSocio(wrapper: ${element})`, 'start');
    const wrapped = new StatusWrapper<IProyectoSocioEquipo>(element);
    wrapped.setCreated();
    const current = this.proyectoEquipoSocios$.value;
    current.push(wrapped);
    this.proyectoEquipoSocios$.next(current);
    this.setChanges(true);
    this.logger.debug(ProyectoSocioEquipoFragment.name,
      `addProyectoEquipoSocio(wrapper: ${element})`, 'end');
  }

  deleteProyectoSocioEquipo(wrapper: StatusWrapper<IProyectoSocioEquipo>) {
    this.logger.debug(ProyectoSocioEquipoFragment.name,
      `deleteProyectoEquipoSocio(wrapper: ${wrapper})`, 'start');
    const current = this.proyectoEquipoSocios$.value;
    const index = current.findIndex((value) => value === wrapper);
    if (index >= 0) {
      current.splice(index, 1);
      this.proyectoEquipoSocios$.next(current);
      this.setChanges(true);
    }
    this.logger.debug(ProyectoSocioEquipoFragment.name,
      `deleteProyectoEquipoSocio(wrapper: ${wrapper})`, 'end');
  }

  saveOrUpdate(): Observable<void> {
    this.logger.debug(ProyectoSocioEquipoFragment.name, `saveOrUpdate()`, 'start');
    const values = this.proyectoEquipoSocios$.value.map(wrapper => wrapper.value);
    const id = this.getKey() as number;
    return this.proyectoEquipoSocioService.updateList(id, values)
      .pipe(
        takeLast(1),
        map((results) => {
          this.proyectoEquipoSocios$.next(
            results.map(value => new StatusWrapper<IProyectoSocioEquipo>(value)));
        }),
        tap(() => {
          if (this.isSaveOrUpdateComplete()) {
            this.setChanges(false);
          }
          this.logger.debug(ProyectoSocioEquipoFragment.name, `saveOrUpdate()`, 'end');
        })
      );
  }

  private isSaveOrUpdateComplete(): boolean {
    this.logger.debug(ProyectoSocioEquipoFragment.name, `isSaveOrUpdateComplete()`, 'start');
    const hasTouched = this.proyectoEquipoSocios$.value.some((wrapper) => wrapper.touched);
    this.logger.debug(ProyectoSocioEquipoFragment.name, `isSaveOrUpdateComplete()`, 'end');
    return !hasTouched;
  }
}
