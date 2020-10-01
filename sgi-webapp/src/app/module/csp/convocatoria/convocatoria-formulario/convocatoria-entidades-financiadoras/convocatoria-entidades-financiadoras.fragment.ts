import { IEntidadFinanciadora } from '@core/models/csp/entidad-financiadora';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { delay, map, tap } from 'rxjs/operators';

export class ConvocatoriaEntidadesFinanciadorasFragment extends Fragment {
  entidadesFinanciadoras$: BehaviorSubject<StatusWrapper<IEntidadFinanciadora>[]>;

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private readonly convocatoriaService: ConvocatoriaService
  ) {
    super(key);
    this.logger.debug(ConvocatoriaEntidadesFinanciadorasFragment.name, 'constructor()', 'start');
    this.entidadesFinanciadoras$ = new BehaviorSubject<StatusWrapper<IEntidadFinanciadora>[]>([]);
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
          entidadesFinanciadora => new StatusWrapper<IEntidadFinanciadora>(entidadesFinanciadora))
        );
        this.logger.debug(ConvocatoriaEntidadesFinanciadorasFragment.name, 'onInitialize()', 'end');
      });
    }
  }

  public addEntidadFinanciadora(entidadFinanciadora: IEntidadFinanciadora) {
    this.logger.debug(ConvocatoriaEntidadesFinanciadorasFragment.name,
      `addEntidadFinanciadora(entidadFinanciadora: ${entidadFinanciadora})`, 'start');
    const wrapped = new StatusWrapper<IEntidadFinanciadora>(entidadFinanciadora);
    wrapped.setCreated();
    const current = this.entidadesFinanciadoras$.value;
    current.push(wrapped);
    this.entidadesFinanciadoras$.next(current);
    this.setChanges(true);
    this.setErrors(false);
    this.logger.debug(ConvocatoriaEntidadesFinanciadorasFragment.name,
      `addEntidadFinanciadora(entidadFinanciadora: ${entidadFinanciadora})`, 'end');
  }

  saveOrUpdate(): Observable<string | number | void> {
    this.logger.debug(ConvocatoriaEntidadesFinanciadorasFragment.name, 'saveOrUpdate()', 'start');
    return of(void 0).pipe(
      tap(() => this.logger.debug(ConvocatoriaEntidadesFinanciadorasFragment.name, 'saveOrUpdate()', 'end'))
    );
  }

}