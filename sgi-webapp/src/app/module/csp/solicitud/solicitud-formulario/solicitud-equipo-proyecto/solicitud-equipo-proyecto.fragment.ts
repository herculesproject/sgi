import { OnDestroy } from '@angular/core';
import { ISolicitudProyectoDatos } from '@core/models/csp/solicitud-proyecto-datos';
import { ISolicitudProyectoEquipo } from '@core/models/csp/solicitud-proyecto-equipo';
import { Fragment } from '@core/services/action-service';
import { SolicitudProyectoEquipoService } from '@core/services/csp/solicitud-proyecto-equipo.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, merge, Observable, of, Subscription } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export class SolicitudEquipoProyectoFragment extends Fragment implements OnDestroy {
  proyectoEquipos$ = new BehaviorSubject<StatusWrapper<ISolicitudProyectoEquipo>[]>([]);
  private proyectosEquipoEliminados: StatusWrapper<ISolicitudProyectoEquipo>[] = [];
  private subscriptions: Subscription[] = [];

  existsDatosProyecto = false;
  solicitantePersonaRef: string;

  constructor(
    private logger: NGXLogger,
    key: number,
    private solicitudService: SolicitudService,
    private solicitudProyectoEquipoService: SolicitudProyectoEquipoService
  ) {
    super(key);
    this.logger.debug(SolicitudEquipoProyectoFragment.name, 'constructor()', 'start');
    this.setComplete(true);
    this.logger.debug(SolicitudEquipoProyectoFragment.name, 'constructor()', 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(SolicitudEquipoProyectoFragment.name, `ngOnDestroy()`, 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(SolicitudEquipoProyectoFragment.name, `ngOnDestroy()`, 'end');
  }

  protected onInitialize(): void {
    this.logger.debug(SolicitudEquipoProyectoFragment.name, 'onInitialize()', 'start');
    const id = this.getKey() as number;
    if (id) {
      const subscription = this.solicitudService.findAllSolicitudProyectoEquipo(id).pipe(
        map(result => result.items.map(solicitudProyectoEquipo =>
          new StatusWrapper<ISolicitudProyectoEquipo>(solicitudProyectoEquipo)
        )),
      ).subscribe(
        (result) => {
          this.proyectoEquipos$.next(result);
          this.logger.debug(SolicitudEquipoProyectoFragment.name, 'onInitialize()', 'end');
        }
      );
      this.subscriptions.push(subscription);
    }
  }

  public deleteProyectoEquipo(wrapper: StatusWrapper<ISolicitudProyectoEquipo>) {
    this.logger.debug(SolicitudEquipoProyectoFragment.name,
      `deleteProyectoEquipo(wrapper: ${wrapper})`, 'start');
    const current = this.proyectoEquipos$.value;
    const index = current.findIndex(
      (value) => value === wrapper
    );
    if (index >= 0) {
      if (!wrapper.created) {
        this.proyectosEquipoEliminados.push(current[index]);
      }
      current.splice(index, 1);
      this.proyectoEquipos$.next(current);
      this.setChanges(true);
    }
    this.logger.debug(SolicitudEquipoProyectoFragment.name,
      `deleteProyectoEquipo(wrapper: ${wrapper})`, 'end');
  }

  addProyectoEquipo(equipoProyectoData: ISolicitudProyectoEquipo) {
    this.logger.debug(SolicitudEquipoProyectoFragment.name,
      `addProyectoEquipo(equipoProyectoData: ${equipoProyectoData})`, 'start');
    const wrapped = new StatusWrapper<ISolicitudProyectoEquipo>(equipoProyectoData);
    wrapped.setCreated();
    const current = this.proyectoEquipos$.value;
    current.push(wrapped);
    this.proyectoEquipos$.next(current);
    this.setChanges(true);
    this.logger.debug(SolicitudEquipoProyectoFragment.name,
      `addProyectoEquipo(equipoProyectoData: ${equipoProyectoData})`, 'end');
  }

  saveOrUpdate(): Observable<void> {
    this.logger.debug(SolicitudEquipoProyectoFragment.name, `saveOrUpdate()`, 'start');
    return merge(
      this.deleteEquipoProyecto(),
      this.updateEquipoProyecto(),
      this.createEquipoProyecto()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      }),
      tap(() => this.logger.debug(SolicitudEquipoProyectoFragment.name, `saveOrUpdate()`, 'end'))
    );
  }

  private deleteEquipoProyecto(): Observable<void> {
    this.logger.debug(SolicitudEquipoProyectoFragment.name, `deleteEquipoProyecto()`, 'start');
    if (this.proyectosEquipoEliminados.length === 0) {
      this.logger.debug(SolicitudEquipoProyectoFragment.name, `deleteEquipoProyecto()`, 'end');
      return of(void 0);
    }
    return from(this.proyectosEquipoEliminados).pipe(
      mergeMap((wrapped) => {
        return this.solicitudProyectoEquipoService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.proyectosEquipoEliminados = this.proyectosEquipoEliminados.filter(deletedHito =>
                deletedHito.value.id !== wrapped.value.id);
            }),
            tap(() => this.logger.debug(SolicitudEquipoProyectoFragment.name,
              `deleteEquipoProyecto()`, 'end'))
          );
      })
    );
  }

  private updateEquipoProyecto(): Observable<void> {
    this.logger.debug(SolicitudEquipoProyectoFragment.name, `updateEquipoProyecto()`, 'start');
    const updateEquipos = this.proyectoEquipos$.value.filter((wrapper) => wrapper.value.id);
    if (updateEquipos.length === 0) {
      this.logger.debug(SolicitudEquipoProyectoFragment.name, `updateEquipoProyecto()`, 'end');
      return of(void 0);
    }
    return from(updateEquipos).pipe(
      mergeMap((wrapped) => {
        const solicitudProyectoEquipo = wrapped.value;
        return this.solicitudProyectoEquipoService.update(solicitudProyectoEquipo.id, solicitudProyectoEquipo).pipe(
          map((updated) => {
            const index = this.proyectoEquipos$.value.findIndex((current) => current === wrapped);
            this.proyectoEquipos$.value[index] = new StatusWrapper<ISolicitudProyectoEquipo>(updated);
          }),
          tap(() => this.logger.debug(SolicitudEquipoProyectoFragment.name,
            `updateEquipoProyecto()`, 'end'))
        );
      })
    );
  }

  private createEquipoProyecto(): Observable<void> {
    this.logger.debug(SolicitudEquipoProyectoFragment.name, `createEquipoProyecto()`, 'start');
    const createdEquipos = this.proyectoEquipos$.value.filter((equipoProyecto) => !equipoProyecto.value.id);
    if (createdEquipos.length === 0) {
      this.logger.debug(SolicitudEquipoProyectoFragment.name, `createEquipoProyecto()`, 'end');
      return of(void 0);
    }
    const id = this.getKey() as number;
    return this.solicitudService.findSolicitudProyectoDatos(id).pipe(
      switchMap(solicitudProyectoDatos => {
        const index = this.getIndexSolicitante(createdEquipos);
        if (index >= 0) {
          const solicitante = createdEquipos[index].value;
          solicitante.solicitudProyectoDatos = solicitudProyectoDatos;
          return this.solicitudProyectoEquipoService.create(solicitante).pipe(
            switchMap(() => this.createEquipos(solicitudProyectoDatos, createdEquipos.filter(
              (wrapper) => wrapper.value.persona.personaRef !== this.solicitantePersonaRef))
            )
          );
        }
        return this.createEquipos(solicitudProyectoDatos, createdEquipos);
      })
    );
  }

  private createEquipos(
    solicitudProyectoDatos: ISolicitudProyectoDatos,
    createdEquipos: StatusWrapper<ISolicitudProyectoEquipo>[]
  ): Observable<void> {
    return from(createdEquipos).pipe(
      mergeMap((wrapped) => {
        const value = wrapped.value;
        value.solicitudProyectoDatos = solicitudProyectoDatos;
        return this.solicitudProyectoEquipoService.create(value).pipe(
          map((solicitudProyectoEquipo) => {
            const index = this.proyectoEquipos$.value.findIndex((currenthitos) => currenthitos === wrapped);
            this.proyectoEquipos$.value[index] = new StatusWrapper<ISolicitudProyectoEquipo>(solicitudProyectoEquipo);
          }),
          tap(() => this.logger.debug(SolicitudEquipoProyectoFragment.name,
            `createEquipoProyecto()`, 'end'))
        );
      }),
      takeLast(1)
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    this.logger.debug(SolicitudEquipoProyectoFragment.name, `isSaveOrUpdateComplete()`, 'start');
    const touched: boolean = this.proyectoEquipos$.value.some((wrapper) => wrapper.touched);
    this.logger.debug(SolicitudEquipoProyectoFragment.name, `isSaveOrUpdateComplete()`, 'end');
    return (this.proyectosEquipoEliminados.length > 0 || touched);
  }

  getIndexSolicitante(proyectoEquipos: StatusWrapper<ISolicitudProyectoEquipo>[]): number {
    return proyectoEquipos.findIndex(
      (element) => element.value.persona.personaRef === this.solicitantePersonaRef
    );
  }
}
