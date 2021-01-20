import { OnDestroy } from '@angular/core';
import { Fragment } from '@core/services/action-service';
import { ISolicitudProyectoPeriodoPago } from '@core/models/csp/solicitud-proyecto-periodo-pago';
import { Subscription, BehaviorSubject, Observable } from 'rxjs';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { SolicitudProyectoPeriodoPagoService } from '@core/services/csp/solicitud-proyecto-periodo-pago.service';
import { SolicitudProyectoSocioService } from '@core/services/csp/solicitud-proyecto-socio.service';
import { map, tap, takeLast } from 'rxjs/operators';

export class SolicitudProyectoSocioPeriodoPagoFragment extends Fragment implements OnDestroy {
  private subscriptions: Subscription[] = [];
  periodoPagos$ = new BehaviorSubject<StatusWrapper<ISolicitudProyectoPeriodoPago>[]>([]);

  constructor(
    private logger: NGXLogger,
    key: number,
    private solicitudProyectoSocioService: SolicitudProyectoSocioService,
    private solicitudProyectoPeriodoPagoService: SolicitudProyectoPeriodoPagoService
  ) {
    super(key);
    this.logger.debug(SolicitudProyectoSocioPeriodoPagoFragment.name, 'constructor()', 'start');
    this.setComplete(true);
    this.logger.debug(SolicitudProyectoSocioPeriodoPagoFragment.name, 'constructor()', 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(SolicitudProyectoSocioPeriodoPagoFragment.name, `ngOnDestroy()`, 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(SolicitudProyectoSocioPeriodoPagoFragment.name, `ngOnDestroy()`, 'end');
  }

  protected onInitialize(): void {
    this.logger.debug(SolicitudProyectoSocioPeriodoPagoFragment.name, 'onInitialize()', 'start');
    if (this.getKey()) {
      const id = this.getKey() as number;
      this.subscriptions.push(
        this.solicitudProyectoSocioService.findAllSolicitudProyectoPeriodoPago(id).pipe(
          map(response => response.items)
        ).subscribe(
          result => {
            this.periodoPagos$.next(
              result.map(value => new StatusWrapper<ISolicitudProyectoPeriodoPago>(value))
            );
            this.logger.debug(SolicitudProyectoSocioPeriodoPagoFragment.name, 'onInitialize()', 'end');
          },
          error => {
            this.logger.error(SolicitudProyectoSocioPeriodoPagoFragment.name, 'onInitialize()', error);
          }
        )
      );
    }
  }

  addPeriodoPago(element: ISolicitudProyectoPeriodoPago) {
    this.logger.debug(SolicitudProyectoSocioPeriodoPagoFragment.name,
      `addPeriodoPago(wrapper: ${element})`, 'start');
    const wrapped = new StatusWrapper<ISolicitudProyectoPeriodoPago>(element);
    wrapped.setCreated();
    const current = this.periodoPagos$.value;
    current.push(wrapped);
    this.recalcularNumPeriodos(current);
    this.setChanges(true);
    this.logger.debug(SolicitudProyectoSocioPeriodoPagoFragment.name,
      `addPeriodoPago(wrapper: ${element})`, 'end');
  }

  deletePeriodoPago(wrapper: StatusWrapper<ISolicitudProyectoPeriodoPago>) {
    this.logger.debug(SolicitudProyectoSocioPeriodoPagoFragment.name,
      `deletePeriodoPago(wrapper: ${wrapper})`, 'start');
    const current = this.periodoPagos$.value;
    const index = current.findIndex((value) => value === wrapper);
    if (index >= 0) {
      current.splice(index, 1);
      this.recalcularNumPeriodos(current);
      this.setChanges(true);
    }
    this.logger.debug(SolicitudProyectoSocioPeriodoPagoFragment.name,
      `deletePeriodoPago(wrapper: ${wrapper})`, 'end');
  }

  saveOrUpdate(): Observable<void> {
    this.logger.debug(SolicitudProyectoSocioPeriodoPagoFragment.name, `saveOrUpdate()`, 'start');
    const values = this.periodoPagos$.value.map(wrapper => wrapper.value);
    const id = this.getKey() as number;
    return this.solicitudProyectoPeriodoPagoService.updateList(id, values).pipe(
      takeLast(1),
      map((results) => {
        this.periodoPagos$.next(
          results.map(value => new StatusWrapper<ISolicitudProyectoPeriodoPago>(value)));
      }),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
        this.logger.debug(SolicitudProyectoSocioPeriodoPagoFragment.name, `saveOrUpdate()`, 'end');
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    this.logger.debug(SolicitudProyectoSocioPeriodoPagoFragment.name, `isSaveOrUpdateComplete()`, 'start');
    const hasTouched = this.periodoPagos$.value.some((wrapper) => wrapper.touched);
    this.logger.debug(SolicitudProyectoSocioPeriodoPagoFragment.name, `isSaveOrUpdateComplete()`, 'end');
    return !hasTouched;
  }

  private recalcularNumPeriodos(current: StatusWrapper<ISolicitudProyectoPeriodoPago>[]): void {
    let numPeriodo = 1;
    current.sort((a, b) =>
      (a.value.mes > b.value.mes) ? 1 : ((b.value.mes > a.value.mes) ? -1 : 0));
    current.forEach(element => element.value.numPeriodo = numPeriodo++);
    this.periodoPagos$.next(current);
  }
}
