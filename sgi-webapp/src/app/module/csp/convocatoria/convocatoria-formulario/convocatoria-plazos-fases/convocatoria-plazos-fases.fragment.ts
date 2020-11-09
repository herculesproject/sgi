import { Fragment } from '@core/services/action-service';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { tap, delay, map } from 'rxjs/operators';
import { IPlazosFases } from '@core/models/csp/plazos-fases';


export class ConvocatoriaPlazosFasesFragment extends Fragment {
  plazosFase$: BehaviorSubject<StatusWrapper<IPlazosFases>[]>;

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private readonly convocatoriaService: ConvocatoriaService
  ) {
    super(key);
    this.logger.debug(ConvocatoriaPlazosFasesFragment.name, 'constructor()', 'start');
    this.setComplete(true);
    this.plazosFase$ = new BehaviorSubject<StatusWrapper<IPlazosFases>[]>([]);
    this.logger.debug(ConvocatoriaPlazosFasesFragment.name, 'constructor()', 'end');
  }

  protected onInitialize(): void {
    this.logger.debug(ConvocatoriaPlazosFasesFragment.name, 'onInitialize()', 'start');
    if (this.getKey()) {
      this.convocatoriaService.getPlazosFases(this.getKey() as number).pipe(
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
      ).subscribe((plazosFases) => {
        this.plazosFase$.next(plazosFases.map(
          plazosFase => new StatusWrapper<IPlazosFases>(plazosFase))
        );
        this.logger.debug(ConvocatoriaPlazosFasesFragment.name, 'onInitialize()', 'end');
      });
    }
  }

  /**
   * Insertamos plazos fase
   *
   * @param plazoFase PlazoFase
   */
  public addPlazosFases(plazoFase: IPlazosFases) {
    this.logger.debug(ConvocatoriaPlazosFasesFragment.name,
      `addEntidadFinanciadora(comentario: ${plazoFase})`, 'start');
    const wrapped = new StatusWrapper<IPlazosFases>(plazoFase);
    wrapped.setCreated();
    const current = this.plazosFase$.value;
    current.push(wrapped);
    this.plazosFase$.next(current);
    this.setChanges(true);
    this.setErrors(false);
    this.logger.debug(ConvocatoriaPlazosFasesFragment.name,
      `addEntidadFinanciadora(comentario: ${plazoFase})`, 'end');
  }

  saveOrUpdate(): Observable<string | number | void> {
    this.logger.debug(ConvocatoriaPlazosFasesFragment.name, 'saveOrUpdate()', 'start');
    return of(void 0).pipe(
      tap(() => this.logger.debug(ConvocatoriaPlazosFasesFragment.name, 'saveOrUpdate()', 'end'))
    );
  }

}
