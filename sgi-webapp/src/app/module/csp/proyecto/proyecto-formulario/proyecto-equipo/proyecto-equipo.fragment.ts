import { Fragment } from '@core/services/action-service';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { BehaviorSubject, from, Observable, Subscription } from 'rxjs';
import { NGXLogger } from 'ngx-logger';
import { OnDestroy } from '@angular/core';
import { IProyectoEquipo } from '@core/models/csp/proyecto-equipo';
import { ProyectoEquipoService } from '@core/services/csp/proyecto-equipo.service';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';

export class ProyectoEquipoFragment extends Fragment implements OnDestroy {
  private subscriptions: Subscription[] = [];
  equipos$ = new BehaviorSubject<StatusWrapper<IProyectoEquipo>[]>([]);

  constructor(
    private logger: NGXLogger,
    key: number,
    private proyectoService: ProyectoService,
    private proyectoEquipoService: ProyectoEquipoService,
    private personaFisicaService: PersonaFisicaService
  ) {
    super(key);
    this.logger.debug(ProyectoEquipoFragment.name, 'constructor()', 'start');
    this.setComplete(true);
    this.logger.debug(ProyectoEquipoFragment.name, 'constructor()', 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(ProyectoEquipoFragment.name, `ngOnDestroy()`, 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(ProyectoEquipoFragment.name, `ngOnDestroy()`, 'end');
  }

  protected onInitialize(): void {
    this.logger.debug(ProyectoEquipoFragment.name, 'onInitialize()', 'start');
    if (this.getKey()) {
      const id = this.getKey() as number;
      this.subscriptions.push(
        this.proyectoService.findAllProyectoEquipo(id).pipe(
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
            this.equipos$.next(
              result.map(solicitudProyectoEquipo =>
                new StatusWrapper<IProyectoEquipo>(solicitudProyectoEquipo)
              )
            );
            this.logger.debug(ProyectoEquipoFragment.name, 'onInitialize()', 'end');
          },
          error => {
            this.logger.error(ProyectoEquipoFragment.name, 'onInitialize()', error);
          }
        )
      );
    }
  }

  addProyectoEquipo(element: IProyectoEquipo) {
    this.logger.debug(ProyectoEquipoFragment.name,
      `addProyectoEquipo(wrapper: ${element})`, 'start');
    const wrapped = new StatusWrapper<IProyectoEquipo>(element);
    wrapped.setCreated();
    const current = this.equipos$.value;
    current.push(wrapped);
    this.equipos$.next(current);
    this.setChanges(true);
    this.logger.debug(ProyectoEquipoFragment.name,
      `addProyectoEquipo(wrapper: ${element})`, 'end');
  }

  deleteProyectoEquipo(wrapper: StatusWrapper<IProyectoEquipo>) {
    this.logger.debug(ProyectoEquipoFragment.name,
      `deleteProyectoEquipo(wrapper: ${wrapper})`, 'start');
    const current = this.equipos$.value;
    const index = current.findIndex((value) => value === wrapper);
    if (index >= 0) {
      current.splice(index, 1);
      this.equipos$.next(current);
      this.setChanges(true);
    }
    this.logger.debug(ProyectoEquipoFragment.name,
      `deleteProyectoEquipo(wrapper: ${wrapper})`, 'end');
  }

  saveOrUpdate(): Observable<void> {
    this.logger.debug(ProyectoEquipoFragment.name, `saveOrUpdate()`, 'start');
    const values = this.equipos$.value.map(wrapper => wrapper.value);
    const id = this.getKey() as number;
    return this.proyectoEquipoService.updateList(id, values)
      .pipe(
        takeLast(1),
        map((results) => {
          this.equipos$.next(
            results.map(value => new StatusWrapper<IProyectoEquipo>(value)));
        }),
        tap(() => {
          if (this.isSaveOrUpdateComplete()) {
            this.setChanges(false);
          }
          this.logger.debug(ProyectoEquipoFragment.name, `saveOrUpdate()`, 'end');
        })
      );
  }

  private isSaveOrUpdateComplete(): boolean {
    this.logger.debug(ProyectoEquipoFragment.name, `isSaveOrUpdateComplete()`, 'start');
    const hasTouched = this.equipos$.value.some((wrapper) => wrapper.touched);
    this.logger.debug(ProyectoEquipoFragment.name, `isSaveOrUpdateComplete()`, 'end');
    return !hasTouched;
  }
}
