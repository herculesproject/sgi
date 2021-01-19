import { Fragment } from '@core/services/action-service';
import { map, mergeMap, takeLast, tap } from 'rxjs/operators';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { BehaviorSubject, from, merge, Observable, of, Subscription } from 'rxjs';
import { NGXLogger } from 'ngx-logger';
import { ProyectoHitoService } from '@core/services/csp/proyecto-hito.service';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoHito } from '@core/models/csp/proyecto-hito';
import { OnDestroy } from '@angular/core';

export class ProyectoHitosFragment extends Fragment implements OnDestroy {
  hitos$ = new BehaviorSubject<StatusWrapper<IProyectoHito>[]>([]);
  private hitosEliminados: StatusWrapper<IProyectoHito>[] = [];
  private subscriptions: Subscription[] = [];

  constructor(
    private logger: NGXLogger,
    key: number,
    private proyectoService: ProyectoService,
    private proyectoHitoService: ProyectoHitoService,
    public readonly: boolean
  ) {
    super(key);
    this.logger.debug(ProyectoHitosFragment.name, 'constructor()', 'start');
    this.setComplete(true);
    this.logger.debug(ProyectoHitosFragment.name, 'constructor()', 'start');
  }

  ngOnDestroy(): void {
    this.logger.debug(ProyectoHitosFragment.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(ProyectoHitosFragment.name, 'ngOnDestroy()', 'end');
  }

  protected onInitialize(): void {
    this.logger.debug(ProyectoHitosFragment.name, 'onInitialize()', 'start');
    if (this.getKey()) {
      this.proyectoService.findHitosProyecto(this.getKey() as number).pipe(
        map((response) => response.items)
      ).subscribe((hitos) => {
        this.hitos$.next(hitos.map(
          hito => new StatusWrapper<IProyectoHito>(hito))
        );
        this.logger.debug(ProyectoHitosFragment.name, 'onInitialize()', 'end');
      });
    }
  }

  public addHito(hito: IProyectoHito) {
    this.logger.debug(ProyectoHitosFragment.name,
      `addHito(addHito: ${hito})`, 'start');
    const wrapped = new StatusWrapper<IProyectoHito>(hito);
    wrapped.setCreated();
    const current = this.hitos$.value;
    current.push(wrapped);
    this.hitos$.next(current);
    this.setChanges(true);
    this.logger.debug(ProyectoHitosFragment.name,
      `addHito(addHito: ${hito})`, 'end');
  }

  public deleteHito(wrapper: StatusWrapper<IProyectoHito>) {
    this.logger.debug(ProyectoHitosFragment.name,
      `deleteHito(wrapper: ${wrapper})`, 'start');
    const current = this.hitos$.value;
    const index = current.findIndex(
      (value) => value === wrapper
    );
    if (index >= 0) {
      if (!wrapper.created) {
        this.hitosEliminados.push(current[index]);
      }
      current.splice(index, 1);
      this.hitos$.next(current);
      this.setChanges(true);
    }
    this.logger.debug(ProyectoHitosFragment.name,
      `deleteHito(wrapper: ${wrapper})`, 'end');
  }

  saveOrUpdate(): Observable<void> {
    this.logger.debug(ProyectoHitosFragment.name, `saveOrUpdate()`, 'start');
    return merge(
      this.deleteHitos(),
      this.updateHitos(),
      this.createHitos()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      }),
      tap(() => this.logger.debug(ProyectoHitosFragment.name, `saveOrUpdate()`, 'end'))
    );
  }

  private deleteHitos(): Observable<void> {
    this.logger.debug(ProyectoHitosFragment.name, `deleteHitos()`, 'start');
    if (this.hitosEliminados.length === 0) {
      this.logger.debug(ProyectoHitosFragment.name, `deleteHitos()`, 'end');
      return of(void 0);
    }
    return from(this.hitosEliminados).pipe(
      mergeMap((wrapped) => {
        return this.proyectoHitoService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.hitosEliminados = this.hitosEliminados.filter(deletedHito =>
                deletedHito.value.id !== wrapped.value.id);
            }),
            tap(() => this.logger.debug(ProyectoHitosFragment.name,
              `deleteHitos()`, 'end'))
          );
      })
    );
  }

  private createHitos(): Observable<void> {
    this.logger.debug(ProyectoHitosFragment.name, `createHitos()`, 'start');
    const createdHitos = this.hitos$.value.filter((proyectoHito) => proyectoHito.created);
    if (createdHitos.length === 0) {
      this.logger.debug(ProyectoHitosFragment.name, `createHitos()`, 'end');
      return of(void 0);
    }
    createdHitos.forEach(
      (wrapper) => wrapper.value.proyecto = {
        id: this.getKey(),
        activo: true
      } as IProyecto
    );
    return from(createdHitos).pipe(
      mergeMap((wrappedHitos) => {
        return this.proyectoHitoService.create(wrappedHitos.value).pipe(
          map((updatedHitos) => {
            const index = this.hitos$.value.findIndex((currenthitos) => currenthitos === wrappedHitos);
            this.hitos$.value[index] = new StatusWrapper<IProyectoHito>(updatedHitos);
          }),
          tap(() => this.logger.debug(ProyectoHitosFragment.name,
            `createHitos()`, 'end'))
        );
      })
    );
  }

  private updateHitos(): Observable<void> {
    this.logger.debug(ProyectoHitosFragment.name, `updateHitos()`, 'start');
    const updateHitos = this.hitos$.value.filter((proyectoHito) => proyectoHito.edited);
    if (updateHitos.length === 0) {
      this.logger.debug(ProyectoHitosFragment.name, `updateHitos()`, 'end');
      return of(void 0);
    }
    return from(updateHitos).pipe(
      mergeMap((wrappedHitos) => {
        return this.proyectoHitoService.update(wrappedHitos.value.id, wrappedHitos.value).pipe(
          map((updatedHitos) => {
            const index = this.hitos$.value.findIndex((currenthitos) => currenthitos === wrappedHitos);
            this.hitos$.value[index] = new StatusWrapper<IProyectoHito>(updatedHitos);
          }),
          tap(() => this.logger.debug(ProyectoHitosFragment.name,
            `updateHitos()`, 'end'))
        );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    this.logger.debug(ProyectoHitosFragment.name, `isSaveOrUpdateComplete()`, 'start');
    const touched: boolean = this.hitos$.value.some((wrapper) => wrapper.touched);
    this.logger.debug(ProyectoHitosFragment.name, `isSaveOrUpdateComplete()`, 'end');
    return (this.hitosEliminados.length > 0 || touched);
  }

}
