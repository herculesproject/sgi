import { Fragment } from '@core/services/action-service';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { IPeriodosJustificacion } from '@core/models/csp/periodo-justificacion';
import { tap, delay, map } from 'rxjs/operators';


export class ConvocatoriaPeriodosJustificacionFragment extends Fragment {
  periodosJustificacion$: BehaviorSubject<StatusWrapper<IPeriodosJustificacion>[]>;

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private readonly convocatoriaService: ConvocatoriaService
  ) {
    super(key);
    this.logger.debug(ConvocatoriaPeriodosJustificacionFragment.name, 'constructor()', 'start');
    this.periodosJustificacion$ = new BehaviorSubject<StatusWrapper<IPeriodosJustificacion>[]>([]);
    this.logger.debug(ConvocatoriaPeriodosJustificacionFragment.name, 'constructor()', 'end');
  }

  protected onInitialize(): void {
    this.logger.debug(ConvocatoriaPeriodosJustificacionFragment.name, 'onInitialize()', 'start');
    // if (this.getKey()) {
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
        entidadConvocantes => new StatusWrapper<IPeriodosJustificacion>(entidadConvocantes))
      );
      this.logger.debug(ConvocatoriaPeriodosJustificacionFragment.name, 'onInitialize()', 'end');
    });
    // }
  }

  saveOrUpdate(): Observable<string | number | void> {
    this.logger.debug(ConvocatoriaPeriodosJustificacionFragment.name, 'saveOrUpdate()', 'start');
    return of(void 0).pipe(
      tap(() => this.logger.debug(ConvocatoriaPeriodosJustificacionFragment.name, 'saveOrUpdate()', 'end'))
    );
  }

}
