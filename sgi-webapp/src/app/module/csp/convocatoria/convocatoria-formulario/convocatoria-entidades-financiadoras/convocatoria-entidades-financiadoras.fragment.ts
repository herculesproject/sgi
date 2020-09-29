import { IEntidadesFinanciadoras } from '@core/models/csp/entidades-financiadoras';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { delay, map, tap } from 'rxjs/operators';

export class ConvocatoriaEntidadesFinanciadorasFragment extends Fragment {
  entidadesFinanciadoras$: BehaviorSubject<StatusWrapper<IEntidadesFinanciadoras>[]>;

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private readonly convocatoriaService: ConvocatoriaService
  ) {
    super(key);
    this.logger.debug(ConvocatoriaEntidadesFinanciadorasFragment.name, 'constructor()', 'start');
    this.entidadesFinanciadoras$ = new BehaviorSubject<StatusWrapper<IEntidadesFinanciadoras>[]>([]);
    this.logger.debug(ConvocatoriaEntidadesFinanciadorasFragment.name, 'constructor()', 'end');
  }

  protected onInitialize(): void {
    this.logger.debug(ConvocatoriaEntidadesFinanciadorasFragment.name, 'onInitialize()', 'start');
    if (this.getKey()) {
      this.convocatoriaService.getEntidadesFinanciadoras(this.getKey() as number).pipe(
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
      ).subscribe((entidadesFinanciadoras) => {
        this.entidadesFinanciadoras$.next(entidadesFinanciadoras.map(
          entidadesFinanciadora => new StatusWrapper<IEntidadesFinanciadoras>(entidadesFinanciadora))
        );
        this.logger.debug(ConvocatoriaEntidadesFinanciadorasFragment.name, 'onInitialize()', 'end');
      });
    }

  }

  saveOrUpdate(): Observable<string | number | void> {
    this.logger.debug(ConvocatoriaEntidadesFinanciadorasFragment.name, 'saveOrUpdate()', 'start');
    return of(void 0).pipe(
      tap(() => this.logger.debug(ConvocatoriaEntidadesFinanciadorasFragment.name, 'saveOrUpdate()', 'end'))
    );
  }

}