import { ISeguimientoCientifico } from '@core/models/csp/seguimiento-cientifico';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { delay, map, tap } from 'rxjs/operators';

export class ConvocatoriaSeguimientoCientificoFragment extends Fragment {
  seguimientosCientificos$: BehaviorSubject<StatusWrapper<ISeguimientoCientifico>[]>;

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private readonly convocatoriaService: ConvocatoriaService
  ) {
    super(key);
    this.logger.debug(ConvocatoriaSeguimientoCientificoFragment.name, 'constructor()', 'start');
    this.seguimientosCientificos$ = new BehaviorSubject<StatusWrapper<ISeguimientoCientifico>[]>([]);
    this.logger.debug(ConvocatoriaSeguimientoCientificoFragment.name, 'constructor()', 'end');
  }

  protected onInitialize(): void {
    this.logger.debug(ConvocatoriaSeguimientoCientificoFragment.name, 'onInitialize()', 'start');
    if (this.getKey()) {
      this.convocatoriaService.getSeguimientosCientificos(this.getKey() as number).pipe(
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
      ).subscribe((seguimientosCientificos) => {
        this.seguimientosCientificos$.next(seguimientosCientificos.map(
          entidadConvocantes => new StatusWrapper<ISeguimientoCientifico>(entidadConvocantes))
        );
        this.logger.debug(ConvocatoriaSeguimientoCientificoFragment.name, 'onInitialize()', 'end');
      });
    }
  }

  saveOrUpdate(): Observable<string | number | void> {
    this.logger.debug(ConvocatoriaSeguimientoCientificoFragment.name, 'saveOrUpdate()', 'start');
    return of(void 0).pipe(
      tap(() => this.logger.debug(ConvocatoriaSeguimientoCientificoFragment.name, 'saveOrUpdate()', 'end'))
    );
  }

}
