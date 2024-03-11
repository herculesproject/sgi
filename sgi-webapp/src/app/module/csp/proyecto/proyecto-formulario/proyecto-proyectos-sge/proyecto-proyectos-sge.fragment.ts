import { CardinalidadRelacionSgiSge } from '@core/models/csp/configuracion';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoProyectoSge } from '@core/models/csp/proyecto-proyecto-sge';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { Fragment } from '@core/services/action-service';
import { ConfigService } from '@core/services/csp/config.service';
import { ProyectoProyectoSgeService } from '@core/services/csp/proyecto-proyecto-sge.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { ProyectoSgeService } from '@core/services/sge/proyecto-sge.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, concat, forkJoin, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export class ProyectoProyectosSgeFragment extends Fragment {
  proyectosSge$ = new BehaviorSubject<StatusWrapper<IProyectoProyectoSge>[]>([]);

  private _cardinalidadRelacionSgiSge: CardinalidadRelacionSgiSge;
  private _disableAddIdentificadorSge: boolean;
  private _isModificacionProyectoSgeEnabled: boolean;

  get disableAddIdentificadorSge(): boolean {
    return this._disableAddIdentificadorSge;
  }

  get isModificacionProyectoSgeEnabled(): boolean {
    return this._isModificacionProyectoSgeEnabled;
  }

  constructor(
    key: number,
    private service: ProyectoProyectoSgeService,
    private proyectoService: ProyectoService,
    private proyectoSgeService: ProyectoSgeService,
    private configService: ConfigService,
    public readonly: boolean,
    public isVisor: boolean
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {

      this.subscriptions.push(
        forkJoin({
          proyectosSge: this.proyectoService.findAllProyectosSgeProyecto(this.getKey() as number).pipe(
            map(response => response.items.map(proyectoProyectoSge => new StatusWrapper<IProyectoProyectoSge>(proyectoProyectoSge))),
            switchMap(response => {
              const requestsProyectoSge: Observable<StatusWrapper<IProyectoProyectoSge>>[] = [];
              response.forEach(proyectoProyectoSge => {
                requestsProyectoSge.push(this.proyectoSgeService.findById(proyectoProyectoSge.value.proyectoSge.id).pipe(
                  map((proyectoSge) => {
                    proyectoProyectoSge.value.proyectoSge = proyectoSge;
                    return proyectoProyectoSge;
                  })
                ));
              });
              return of(response).pipe(
                tap(() => merge(...requestsProyectoSge).subscribe())
              );
            })
          ),
          cardinalidadRelacionSgiSge: this.configService.getCardinalidadRelacionSgiSge(),
          isModificacionProyectoSgeEnabled: this.configService.isModificacionProyectoSgeEnabled()
        }).subscribe(({ cardinalidadRelacionSgiSge, isModificacionProyectoSgeEnabled, proyectosSge }) => {
          this._cardinalidadRelacionSgiSge = cardinalidadRelacionSgiSge;
          this._isModificacionProyectoSgeEnabled = isModificacionProyectoSgeEnabled;
          this.proyectosSge$.next(proyectosSge);
        })
      );

      this.subscriptions.push(
        this.proyectosSge$.subscribe(proyectosSge => {
          this._disableAddIdentificadorSge = (proyectosSge?.length ?? 0) > 0
            && (this._cardinalidadRelacionSgiSge === CardinalidadRelacionSgiSge.SGI_1_SGE_1
              || this._cardinalidadRelacionSgiSge === CardinalidadRelacionSgiSge.SGI_N_SGE_1);
        })
      )
    }
  }

  public addProyectoSge(proyectoSge: IProyectoSge) {
    const proyectoProyectoSge: IProyectoProyectoSge = {
      id: undefined,
      proyecto: { id: this.getKey() as number } as IProyecto,
      proyectoSge
    };

    const wrapped = new StatusWrapper<IProyectoProyectoSge>(proyectoProyectoSge);
    wrapped.setCreated();
    const current = this.proyectosSge$.value;
    current.push(wrapped);
    this.proyectosSge$.next(current);
    this.setChanges(true);
  }

  saveOrUpdate(): Observable<void> {
    return concat(
      this.createProyectosSge()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private createProyectosSge(): Observable<void> {
    const createdProyectosSge = this.proyectosSge$.value.filter((proyectoSge) => proyectoSge.created);
    if (createdProyectosSge.length === 0) {
      return of(void 0);
    }

    return from(createdProyectosSge).pipe(
      mergeMap((wrappedProyectoSge) => {
        return this.service.create(wrappedProyectoSge.value).pipe(
          map((createdProyectoSge) => {
            const index = this.proyectosSge$.value.findIndex((currentProyectoSge) =>
              currentProyectoSge === wrappedProyectoSge);
            const proyectoSge = wrappedProyectoSge.value;
            proyectoSge.id = createdProyectoSge.id;
            this.proyectosSge$.value[index] = new StatusWrapper<IProyectoProyectoSge>(proyectoSge);
            this.proyectosSge$.next(this.proyectosSge$.value);
          })
        );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.proyectosSge$.value.some((wrapper) => wrapper.touched);
    return !(touched);
  }

}
