import { Fragment } from '@core/services/action-service';
import { OnDestroy } from '@angular/core';
import { Subscription, BehaviorSubject, Observable, of } from 'rxjs';
import { ISolicitudProyectoPeriodoJustificacion } from '@core/models/csp/solicitud-proyecto-periodo-justificacion';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { SolicitudProyectoPeriodoJustificacionService } from '@core/services/csp/solicitud-proyecto-periodo-justificacion.service';
import { NGXLogger } from 'ngx-logger';
import { SolicitudProyectoSocioService } from '@core/services/csp/solicitud-proyecto-socio.service';
import { map, takeLast, tap, switchMap } from 'rxjs/operators';
import { SolicitudProyectoSocioActionService } from '../../solicitud-proyecto-socio.action.service';
import { ISolicitudProyectoSocio } from '@core/models/csp/solicitud-proyecto-socio';
import { SolicitudService } from '@core/services/csp/solicitud.service';

export class SolicitudProyectoPeriodoJustificacionesFragment extends Fragment implements OnDestroy {
  private subscriptions: Subscription[] = [];
  periodoJustificaciones$ = new BehaviorSubject<StatusWrapper<ISolicitudProyectoPeriodoJustificacion>[]>([]);

  constructor(
    private logger: NGXLogger,
    key: number,
    private solicitudProyectoSocioService: SolicitudProyectoSocioService,
    private solicitudProyectoPeriodoJustificacionService: SolicitudProyectoPeriodoJustificacionService,
    private actionService: SolicitudProyectoSocioActionService
  ) {
    super(key);
    this.logger.debug(SolicitudProyectoPeriodoJustificacionesFragment.name, 'constructor()', 'start');
    this.setComplete(true);
    this.logger.debug(SolicitudProyectoPeriodoJustificacionesFragment.name, 'constructor()', 'end');
  }

  protected onInitialize(): void {
    this.logger.debug(SolicitudProyectoPeriodoJustificacionesFragment.name, 'onInitialize()', 'start');
    if (this.getKey()) {
      const id = this.getKey() as number;
      this.subscriptions.push(
        this.solicitudProyectoSocioService.findAllSolicitudProyectoPeriodoJustificacion(id).pipe(
          map(response => response.items),
        ).subscribe(
          result => {
            this.periodoJustificaciones$.next(
              result.map(value => new StatusWrapper<ISolicitudProyectoPeriodoJustificacion>(value))
            );
            this.logger.debug(SolicitudProyectoPeriodoJustificacionesFragment.name, 'onInitialize()', 'end');
          },
          error => {
            this.logger.error(SolicitudProyectoPeriodoJustificacionesFragment.name, 'onInitialize()', error);
          }
        )
      );
    }
  }

  addPeriodoJustificacion(element: ISolicitudProyectoPeriodoJustificacion): void {
    this.logger.debug(SolicitudProyectoPeriodoJustificacionesFragment.name,
      `addPeriodoJustificacion(wrapper: ${element})`, 'start');
    const wrapped = new StatusWrapper<ISolicitudProyectoPeriodoJustificacion>(element);
    wrapped.setCreated();
    const current = this.periodoJustificaciones$.value;
    current.push(wrapped);
    this.recalcularNumPeriodos(current);
    this.logger.debug(SolicitudProyectoPeriodoJustificacionesFragment.name,
      `addPeriodoJustificacion(wrapper: ${element})`, 'end');
  }

  deletePeriodoJustificacion(wrapper: StatusWrapper<ISolicitudProyectoPeriodoJustificacion>): void {
    this.logger.debug(SolicitudProyectoPeriodoJustificacionesFragment.name,
      `deletePeriodoJustificacion(wrapper: ${wrapper})`, 'start');
    const current = this.periodoJustificaciones$.value;
    const index = current.findIndex((value) => value === wrapper);
    if (index >= 0) {
      current.splice(index, 1);
      this.recalcularNumPeriodos(current);
    }
    this.logger.debug(SolicitudProyectoPeriodoJustificacionesFragment.name,
      `deletePeriodoJustificacion(wrapper: ${wrapper})`, 'end');
  }

  saveOrUpdate(): Observable<void> {
    this.logger.debug(SolicitudProyectoPeriodoJustificacionesFragment.name, `saveOrUpdate()`, 'start');
    const values = this.periodoJustificaciones$.value.map(wrapper => {
      wrapper.value.solicitudProyectoSocio = this.actionService.getSolicitudProyectoSocio();
      return wrapper.value;
    });
    const id = this.getKey() as number;
    return this.solicitudProyectoPeriodoJustificacionService.updateList(id, values).pipe(
      takeLast(1),
      map((results) => {
        this.periodoJustificaciones$.next(
          results.map(value => new StatusWrapper<ISolicitudProyectoPeriodoJustificacion>(value)));
      }),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
        this.logger.debug(SolicitudProyectoPeriodoJustificacionesFragment.name, `saveOrUpdate()`, 'end');
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    this.logger.debug(SolicitudProyectoPeriodoJustificacionesFragment.name, `isSaveOrUpdateComplete()`, 'start');
    const hasTouched = this.periodoJustificaciones$.value.some((wrapper) => wrapper.touched);
    this.logger.debug(SolicitudProyectoPeriodoJustificacionesFragment.name, `isSaveOrUpdateComplete()`, 'end');
    return !hasTouched;
  }

  ngOnDestroy(): void {
    this.logger.debug(SolicitudProyectoPeriodoJustificacionesFragment.name, `ngOnDestroy()`, 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(SolicitudProyectoPeriodoJustificacionesFragment.name, `ngOnDestroy()`, 'end');
  }

  private recalcularNumPeriodos(current: StatusWrapper<ISolicitudProyectoPeriodoJustificacion>[]): void {
    let numPeriodo = 1;
    current.sort((a, b) =>
      (a.value.mesInicial > b.value.mesInicial) ? 1 : ((b.value.mesInicial > a.value.mesInicial) ? -1 : 0));
    current.forEach(element => element.value.numPeriodo = numPeriodo++);
    this.periodoJustificaciones$.next(current);
    this.setChanges(true);
  }

  updatePeriodoJustificacion(wrapper: StatusWrapper<ISolicitudProyectoPeriodoJustificacion>): void {
    if (!wrapper.created) {
      wrapper.setEdited();
    }
    const current = this.periodoJustificaciones$.value;
    this.recalcularNumPeriodos(current);
  }
}
