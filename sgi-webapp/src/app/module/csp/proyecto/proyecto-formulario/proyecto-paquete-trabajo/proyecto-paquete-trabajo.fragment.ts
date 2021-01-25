import { OnDestroy } from "@angular/core";
import { IProyecto } from "@core/models/csp/proyecto";
import { IProyectoPaqueteTrabajo } from "@core/models/csp/proyecto-paquete-trabajo";
import { Fragment } from "@core/services/action-service";
import { ProyectoPaqueteTrabajoService } from "@core/services/csp/proyecto-paquete-trabajo.service";
import { ProyectoService } from "@core/services/csp/proyecto.service";
import { StatusWrapper } from "@core/utils/status-wrapper";
import { NGXLogger } from "ngx-logger";
import { BehaviorSubject, from, merge, Observable, of, Subscription } from "rxjs";
import { map, mergeMap, takeLast, tap } from "rxjs/operators";

export class ProyectoPaqueteTrabajoFragment extends Fragment implements OnDestroy {
  paquetesTrabajo$ = new BehaviorSubject<StatusWrapper<IProyectoPaqueteTrabajo>[]>([]);
  private paquetesTrabajoEliminados: StatusWrapper<IProyectoPaqueteTrabajo>[] = [];
  private subscriptions: Subscription[] = [];

  constructor(
    private logger: NGXLogger,
    key: number,
    private proyectoService: ProyectoService,
    private proyectoPaqueteTrabajoService: ProyectoPaqueteTrabajoService,
    public readonly: boolean
  ) {
    super(key);
    this.logger.debug(ProyectoPaqueteTrabajoFragment.name, 'constructor()', 'start');
    this.setComplete(true);
    this.logger.debug(ProyectoPaqueteTrabajoFragment.name, 'constructor()', 'start');
  }

  ngOnDestroy(): void {
    this.logger.debug(ProyectoPaqueteTrabajoFragment.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(x => x.unsubscribe());
    this.logger.debug(ProyectoPaqueteTrabajoFragment.name, 'ngOnDestroy()', 'end');
  }

  protected onInitialize(): void {
    this.logger.debug(ProyectoPaqueteTrabajoFragment.name, 'onInitialize()', 'start');
    if (this.getKey()) {
      this.proyectoService.findPaqueteTrabajoProyecto(this.getKey() as number).pipe(
        map((response) => response.items)
      ).subscribe((paquetes) => {
        this.paquetesTrabajo$.next(paquetes.map(
          listaPaquetes => new StatusWrapper<IProyectoPaqueteTrabajo>(listaPaquetes))
        );
        this.logger.debug(ProyectoPaqueteTrabajoFragment.name, 'onInitialize()', 'end');
      });
    }
  }

  public addPaqueteTrabajo(paquete: IProyectoPaqueteTrabajo) {
    this.logger.debug(ProyectoPaqueteTrabajoFragment.name,
      `addPaqueteTrabajo(addPaqueteTrabajo: ${paquete})`, 'start');
    const wrapped = new StatusWrapper<IProyectoPaqueteTrabajo>(paquete);
    wrapped.setCreated();
    const current = this.paquetesTrabajo$.value;
    current.push(wrapped);
    this.paquetesTrabajo$.next(current);
    this.setChanges(true);
    this.logger.debug(ProyectoPaqueteTrabajoFragment.name,
      `addPaqueteTrabajo(addPaqueteTrabajo: ${paquete})`, 'end');
  }

  public deletePaqueteTrabajo(wrapper: StatusWrapper<IProyectoPaqueteTrabajo>) {
    this.logger.debug(ProyectoPaqueteTrabajoFragment.name,
      `deletePaqueteTrabajo(wrapper: ${wrapper})`, 'start');
    const current = this.paquetesTrabajo$.value;
    const index = current.findIndex(
      (value) => value === wrapper
    );
    if (index >= 0) {
      if (!wrapper.created) {
        this.paquetesTrabajoEliminados.push(current[index]);
      }
      current.splice(index, 1);
      this.paquetesTrabajo$.next(current);
      this.setChanges(true);
    }
    this.logger.debug(ProyectoPaqueteTrabajoFragment.name,
      `deletePaqueteTrabajo(wrapper: ${wrapper})`, 'end');
  }

  saveOrUpdate(): Observable<void> {
    this.logger.debug(ProyectoPaqueteTrabajoFragment.name, `saveOrUpdate()`, 'start');
    return merge(
      this.deletePaqueteTrabajos(),
      this.updatePaquetesTrabajo(),
      this.createPaquetesTrabajo()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      }),
      tap(() => this.logger.debug(ProyectoPaqueteTrabajoFragment.name, `saveOrUpdate()`, 'end'))
    );
  }

  private deletePaqueteTrabajos(): Observable<void> {
    this.logger.debug(ProyectoPaqueteTrabajoFragment.name, `deletePaqueteTrabajos()`, 'start');
    if (this.paquetesTrabajoEliminados.length === 0) {
      this.logger.debug(ProyectoPaqueteTrabajoFragment.name, `deletePaqueteTrabajos()`, 'end');
      return of(void 0);
    }
    return from(this.paquetesTrabajoEliminados).pipe(
      mergeMap((wrapped) => {
        return this.proyectoPaqueteTrabajoService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.paquetesTrabajoEliminados = this.paquetesTrabajoEliminados.filter(deletedPaquete =>
                deletedPaquete.value.id !== wrapped.value.id);
            }),
            tap(() => this.logger.debug(ProyectoPaqueteTrabajoFragment.name,
              `deletePaqueteTrabajos()`, 'end'))
          );
      })
    );
  }

  private createPaquetesTrabajo(): Observable<void> {
    this.logger.debug(ProyectoPaqueteTrabajoFragment.name, `createPaquetesTrabajo()`, 'start');
    const createdPaquetes = this.paquetesTrabajo$.value.filter((proyectoPaquete) => proyectoPaquete.created);
    if (createdPaquetes.length === 0) {
      this.logger.debug(ProyectoPaqueteTrabajoFragment.name, `createPaquetesTrabajo()`, 'end');
      return of(void 0);
    }
    createdPaquetes.forEach(
      (wrapper) => wrapper.value.proyecto = {
        id: this.getKey(),
        activo: true
      } as IProyecto
    );
    return from(createdPaquetes).pipe(
      mergeMap((wrappedPaquetes) => {
        return this.proyectoPaqueteTrabajoService.create(wrappedPaquetes.value).pipe(
          map((updatedPaquetes) => {
            const index = this.paquetesTrabajo$.value.findIndex((currentPaquetes) => currentPaquetes === wrappedPaquetes);
            this.paquetesTrabajo$.value[index] = new StatusWrapper<IProyectoPaqueteTrabajo>(updatedPaquetes);
          }),
          tap(() => this.logger.debug(ProyectoPaqueteTrabajoFragment.name,
            `createPaquetesTrabajo()`, 'end'))
        );
      })
    );
  }

  private updatePaquetesTrabajo(): Observable<void> {
    this.logger.debug(ProyectoPaqueteTrabajoFragment.name, `updatePaquetesTrabajo()`, 'start');
    const updatePaquetesTrabajo = this.paquetesTrabajo$.value.filter((proyectoPaquete) => proyectoPaquete.edited);
    if (updatePaquetesTrabajo.length === 0) {
      this.logger.debug(ProyectoPaqueteTrabajoFragment.name, `updatePaquetesTrabajo()`, 'end');
      return of(void 0);
    }
    return from(updatePaquetesTrabajo).pipe(
      mergeMap((wrappedPaquetes) => {
        return this.proyectoPaqueteTrabajoService.update(wrappedPaquetes.value.id, wrappedPaquetes.value).pipe(
          map((updatedPaquetes) => {
            const index = this.paquetesTrabajo$.value.findIndex((currentPaquetes) => currentPaquetes === wrappedPaquetes);
            this.paquetesTrabajo$.value[index] = new StatusWrapper<IProyectoPaqueteTrabajo>(updatedPaquetes);
          }),
          tap(() => this.logger.debug(ProyectoPaqueteTrabajoFragment.name,
            `updatePaquetesTrabajo()`, 'end'))
        );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    this.logger.debug(ProyectoPaqueteTrabajoFragment.name, `isSaveOrUpdateComplete()`, 'start');
    const touched: boolean = this.paquetesTrabajo$.value.some((wrapper) => wrapper.touched);
    this.logger.debug(ProyectoPaqueteTrabajoFragment.name, `isSaveOrUpdateComplete()`, 'end');
    return (this.paquetesTrabajoEliminados.length > 0 || touched);
  }

}
