import { Fragment } from '@core/services/action-service';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { tap, delay, map } from 'rxjs/operators';
import { IEnlace } from '@core/models/csp/enlace';


export class ConvocatoriaEnlaceFragment extends Fragment {
  enlace$: BehaviorSubject<StatusWrapper<IEnlace>[]>;

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private readonly convocatoriaService: ConvocatoriaService
  ) {
    super(key);
    this.logger.debug(ConvocatoriaEnlaceFragment.name, 'constructor()', 'start');
    this.enlace$ = new BehaviorSubject<StatusWrapper<IEnlace>[]>([]);
    this.logger.debug(ConvocatoriaEnlaceFragment.name, 'constructor()', 'end');
  }

  protected onInitialize(): void {
    this.logger.debug(ConvocatoriaEnlaceFragment.name, 'onInitialize()', 'start');
    if (this.getKey()) {
      this.convocatoriaService.getEnlaces(this.getKey() as number).pipe(
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
      ).subscribe((enlace) => {
        this.enlace$.next(enlace.map(
          enlaces => new StatusWrapper<IEnlace>(enlaces))
        );
        this.logger.debug(ConvocatoriaEnlaceFragment.name, 'onInitialize()', 'end');
      });
    }
  }

  public addEnlace(enlace: IEnlace): void {
    this.logger.debug(ConvocatoriaEnlaceFragment.name, `addEnlace(enlace: ${enlace})`, 'start');
    const wrapped = new StatusWrapper<IEnlace>(enlace);
    wrapped.setCreated();
    const current = this.enlace$.value;
    current.push(wrapped);
    this.enlace$.next(current);
    this.setChanges(true);
    this.setErrors(false);
    this.logger.debug(ConvocatoriaEnlaceFragment.name, `addEnlace(enlace: ${enlace})`, 'end');
  }

  saveOrUpdate(): Observable<string | number | void> {
    this.logger.debug(ConvocatoriaEnlaceFragment.name, 'saveOrUpdate()', 'start');
    return of(void 0).pipe(
      tap(() => this.logger.debug(ConvocatoriaEnlaceFragment.name, 'saveOrUpdate()', 'end'))
    );
  }

}
