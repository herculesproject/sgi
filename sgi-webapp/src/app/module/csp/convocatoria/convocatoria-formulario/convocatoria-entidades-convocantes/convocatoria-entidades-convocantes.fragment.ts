import { IEntidadesConvocantes } from '@core/models/csp/entidades-convocantes';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { delay, map, tap } from 'rxjs/operators';

export class ConvocatoriaEntidadesConvocantesFragment extends Fragment {
  entidadesConvocantes$: BehaviorSubject<StatusWrapper<IEntidadesConvocantes>[]>;

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private readonly convocatoriaService: ConvocatoriaService
  ) {
    super(key);
    this.logger.debug(ConvocatoriaEntidadesConvocantesFragment.name, 'constructor()', 'start');
    this.entidadesConvocantes$ = new BehaviorSubject<StatusWrapper<IEntidadesConvocantes>[]>([]);
    this.logger.debug(ConvocatoriaEntidadesConvocantesFragment.name, 'constructor()', 'end');
  }

  protected onInitialize(): void {
    this.logger.debug(ConvocatoriaEntidadesConvocantesFragment.name, 'onInitialize()', 'start');
    if (this.getKey()) {
      this.convocatoriaService.getEntidadesConvocantes(this.getKey() as number).pipe(
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
      ).subscribe((entidadesConvocantes) => {
        this.entidadesConvocantes$.next(entidadesConvocantes.map(
          entidadConvocantes => new StatusWrapper<IEntidadesConvocantes>(entidadConvocantes))
        );
        this.logger.debug(ConvocatoriaEntidadesConvocantesFragment.name, 'onInitialize()', 'end');
      });
    }
  }

  saveOrUpdate(): Observable<string | number | void> {
    this.logger.debug(ConvocatoriaEntidadesConvocantesFragment.name, 'saveOrUpdate()', 'start');
    return of(void 0).pipe(
      tap(() => this.logger.debug(ConvocatoriaEntidadesConvocantesFragment.name, 'saveOrUpdate()', 'end'))
    );
  }

}
