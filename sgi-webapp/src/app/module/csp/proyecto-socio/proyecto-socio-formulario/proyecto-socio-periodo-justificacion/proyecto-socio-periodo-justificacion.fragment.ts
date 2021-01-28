import { Fragment } from '@core/services/action-service';
import { OnDestroy } from '@angular/core';
import { Subscription, BehaviorSubject, Observable } from 'rxjs';
import { IProyectoSocioPeriodoJustificacion } from '@core/models/csp/proyecto-socio-periodo-justificacion';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { ProyectoSocioService } from '@core/services/csp/proyecto-socio.service';
import { NGXLogger } from 'ngx-logger';
import { map, takeLast, tap } from 'rxjs/operators';
import { ProyectoSocioPeriodoJustificacionService } from '@core/services/csp/proyecto-socio-periodo-justificacion.service';
import { SgiRestFindOptions, SgiRestSort, SgiRestSortDirection } from '@sgi/framework/http';

export class ProyectoSocioPeriodoJustificacionFragment extends Fragment implements OnDestroy {
  private subscriptions: Subscription[] = [];
  periodoJustificaciones$ = new BehaviorSubject<StatusWrapper<IProyectoSocioPeriodoJustificacion>[]>([]);

  constructor(
    private logger: NGXLogger,
    key: number,
    private proyectoSocioService: ProyectoSocioService,
    private proyectoSocioPeriodoJustificacionService: ProyectoSocioPeriodoJustificacionService
  ) {
    super(key);
    this.logger.debug(ProyectoSocioPeriodoJustificacionFragment.name, 'constructor()', 'start');
    this.setComplete(true);
    this.logger.debug(ProyectoSocioPeriodoJustificacionFragment.name, 'constructor()', 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(ProyectoSocioPeriodoJustificacionFragment.name, `ngOnDestroy()`, 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(ProyectoSocioPeriodoJustificacionFragment.name, `ngOnDestroy()`, 'end');
  }

  protected onInitialize(): void {
    this.logger.debug(ProyectoSocioPeriodoJustificacionFragment.name, 'onInitialize()', 'start');
    if (this.getKey()) {
      const id = this.getKey() as number;
      const sort: SgiRestSort = {
        direction: SgiRestSortDirection.ASC,
        field: 'numPeriodo'
      };
      const options: SgiRestFindOptions = {
        sort
      };
      this.subscriptions.push(
        this.proyectoSocioService.findAllProyectoSocioPeriodoJustificacion(id, options).pipe(
          map(response => response.items),
        ).subscribe(
          result => {
            this.periodoJustificaciones$.next(
              result.map(value => new StatusWrapper<IProyectoSocioPeriodoJustificacion>(value))
            );
            this.logger.debug(ProyectoSocioPeriodoJustificacionFragment.name, 'onInitialize()', 'end');
          },
          error => {
            this.logger.error(ProyectoSocioPeriodoJustificacionFragment.name, 'onInitialize()', error);
          }
        )
      );
    }
  }

  deletePeriodoJustificacion(wrapper: StatusWrapper<IProyectoSocioPeriodoJustificacion>): void {
    this.logger.debug(ProyectoSocioPeriodoJustificacionFragment.name,
      `deletePeriodoJustificacion(wrapper: ${wrapper})`, 'start');
    const current = this.periodoJustificaciones$.value;
    const index = current.findIndex((value) => value === wrapper);
    if (index >= 0) {
      current.splice(index, 1);
      this.recalcularNumPeriodos(current);
      this.setChanges(true);
    }
    this.logger.debug(ProyectoSocioPeriodoJustificacionFragment.name,
      `deletePeriodoJustificacion(wrapper: ${wrapper})`, 'end');
  }

  saveOrUpdate(): Observable<string | number | void> {
    this.logger.debug(ProyectoSocioPeriodoJustificacionFragment.name, `saveOrUpdate()`, 'start');
    const values = this.periodoJustificaciones$.value.map(wrapper => wrapper.value);
    const id = this.getKey() as number;
    return this.proyectoSocioPeriodoJustificacionService.updateList(id, values).pipe(
      takeLast(1),
      map((results) => {
        this.periodoJustificaciones$.next(
          results.map(value => new StatusWrapper<IProyectoSocioPeriodoJustificacion>(value)));
      }),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
        this.logger.debug(ProyectoSocioPeriodoJustificacionFragment.name, `saveOrUpdate()`, 'end');
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    this.logger.debug(ProyectoSocioPeriodoJustificacionFragment.name, `isSaveOrUpdateComplete()`, 'start');
    const hasTouched = this.periodoJustificaciones$.value.some((wrapper) => wrapper.touched);
    this.logger.debug(ProyectoSocioPeriodoJustificacionFragment.name, `isSaveOrUpdateComplete()`, 'end');
    return !hasTouched;
  }

  private recalcularNumPeriodos(current: StatusWrapper<IProyectoSocioPeriodoJustificacion>[]): void {
    let numPeriodo = 1;
    current.sort(
      (a, b) => {
        const dateA = new Date(a.value.fechaInicio);
        const dateB = new Date(b.value.fechaInicio);
        return (dateA > dateB) ? 1 : ((dateB > dateA) ? -1 : 0);
      }
    );
    current.forEach(element => element.value.numPeriodo = numPeriodo++);
    this.periodoJustificaciones$.next(current);
  }
}
