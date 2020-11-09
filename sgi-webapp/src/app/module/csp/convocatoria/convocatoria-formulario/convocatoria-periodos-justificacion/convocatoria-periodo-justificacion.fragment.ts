import { Fragment } from '@core/services/action-service';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { IPeriodoJustificacion } from '@core/models/csp/periodo-justificacion';
import { tap, delay, map } from 'rxjs/operators';


export class ConvocatoriaPeriodosJustificacionFragment extends Fragment {
  periodosJustificacion$: BehaviorSubject<StatusWrapper<IPeriodoJustificacion>[]>;

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private readonly convocatoriaService: ConvocatoriaService
  ) {
    super(key);
    this.logger.debug(ConvocatoriaPeriodosJustificacionFragment.name, 'constructor()', 'start');
    this.setComplete(true);
    this.periodosJustificacion$ = new BehaviorSubject<StatusWrapper<IPeriodoJustificacion>[]>([]);
    this.logger.debug(ConvocatoriaPeriodosJustificacionFragment.name, 'constructor()', 'end');
  }

  protected onInitialize(): void {
    this.logger.debug(ConvocatoriaPeriodosJustificacionFragment.name, 'onInitialize()', 'start');
    if (this.getKey()) {
      this.convocatoriaService.getPeriodosJustificacion(this.getKey() as number).pipe(
        // TODO eliminar delay cuando se quite el mock
        delay(0),
        map((response) => {
          if (response.items) {
            return response.items;
          }
          else {
            return [];
          }
        })
      ).subscribe((periodosJustificacion) => {
        this.periodosJustificacion$.next(periodosJustificacion.map(
          periodoJustificacion => new StatusWrapper<IPeriodoJustificacion>(periodoJustificacion))
        );
        this.logger.debug(ConvocatoriaPeriodosJustificacionFragment.name, 'onInitialize()', 'end');
      });
    }
  }

  /**
   * Insertamos periodo justificacion
   *
   * @param periodoJustificacion Periodo de justificación
   */
  public addPeriodoJustificacion(periodoJustificacion: IPeriodoJustificacion) {
    this.logger.debug(ConvocatoriaPeriodosJustificacionFragment.name,
      `addPeriodoJustificacion(periodoJustificacion: ${periodoJustificacion})`, 'start');
    const wrapped = new StatusWrapper<IPeriodoJustificacion>(periodoJustificacion);
    wrapped.setCreated();
    const current = this.periodosJustificacion$.value;
    current.push(wrapped);
    this.periodosJustificacion$.next(current);
    this.setChanges(true);
    this.setErrors(false);
    this.logger.debug(ConvocatoriaPeriodosJustificacionFragment.name,
      `addPeriodoJustificacion(periodoJustificacion: ${periodoJustificacion})`, 'end');
  }


  saveOrUpdate(): Observable<string | number | void> {
    this.logger.debug(ConvocatoriaPeriodosJustificacionFragment.name, 'saveOrUpdate()', 'start');
    return of(void 0).pipe(
      tap(() => this.logger.debug(ConvocatoriaPeriodosJustificacionFragment.name, 'saveOrUpdate()', 'end'))
    );
  }

}
