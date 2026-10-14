import { IPeticionEvaluacionUnidadVinculacion } from '@core/models/eti/peticion-evaluacion-unidad-vinculacion';
import { IUnidadVinculacion } from '@core/models/sgo/unidad-vinculacion';
import { Fragment } from '@core/services/action-service';
import { PeticionEvaluacionService } from '@core/services/eti/peticion-evaluacion.service';
import { UnidadVinculacionService } from '@core/services/sgo/unidad-vinculacion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';

export interface IPeticionEvaluacionUnidadVinculacionListado {
  id: number;
  peticionEvaluacionId: number;
  unidadVinculacion: IUnidadVinculacion;
  tipoUnidad: IUnidadVinculacion;
}

export class PeticionEvaluacionUnidadesVinculacionFragment extends Fragment {
  unidadesVinculacion$ = new BehaviorSubject<StatusWrapper<IPeticionEvaluacionUnidadVinculacionListado>[]>([]);

  constructor(
    key: number,
    private readonly peticionEvaluacionService: PeticionEvaluacionService,
    private readonly unidadVinculacionService: UnidadVinculacionService,
    public readonly readonly: boolean
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (!this.getKey()) {
      return;
    }

    const peticionEvaluacionId = this.getKey() as number;

    this.subscriptions.push(
      this.peticionEvaluacionService.findUnidadesVinculacion(peticionEvaluacionId).pipe(
        map(response => response.items),
        switchMap(items => this.populateUnidadesVinculacionConTipo(items))
      ).subscribe(items => {
        this.unidadesVinculacion$.next(items.map(item => {
          item.peticionEvaluacionId = peticionEvaluacionId;
          return new StatusWrapper<IPeticionEvaluacionUnidadVinculacionListado>(item);
        }));
      })
    );
  }

  addUnidadVinculacion(element: IPeticionEvaluacionUnidadVinculacionListado): void {
    const wrapper = new StatusWrapper<IPeticionEvaluacionUnidadVinculacionListado>(element);
    wrapper.setCreated();
    const current = this.unidadesVinculacion$.value;
    current.push(wrapper);
    this.unidadesVinculacion$.next(current);
    this.setChanges(true);
  }

  deleteUnidadVinculacion(wrapper: StatusWrapper<IPeticionEvaluacionUnidadVinculacionListado>): void {
    const current = this.unidadesVinculacion$.value;
    const index = current.findIndex(value => value === wrapper);
    if (index < 0) {
      return;
    }

    current.splice(index, 1);
    this.unidadesVinculacion$.next(current);
    this.setChanges(true);
  }

  saveOrUpdate(): Observable<void> {
    const peticionEvaluacionId = this.getKey() as number;
    const unidadesVinculacion = this.unidadesVinculacion$.value.map(wrapper => wrapper.value);

    return this.peticionEvaluacionService.updateUnidadesVinculacion(peticionEvaluacionId, unidadesVinculacion).pipe(
      tap(() => {
        this.unidadesVinculacion$.next(
          this.unidadesVinculacion$.value.map(wrapper =>
            new StatusWrapper<IPeticionEvaluacionUnidadVinculacionListado>(wrapper.value)
          )
        );
        this.setChanges(false);
      }),
      map(() => void 0)
    );
  }

  /**
   * Construye el listado de unidades de vinculación de la petición de evaluación resolviendo, en lote contra el SGO,
   * tanto el detalle de cada unidad asignada (`unidadVinculacion`) como su elemento raíz en la jerarquía (`tipoUnidad`).
   * Se resuelven también las unidades inactivas para poder mostrar las ya vinculadas.
   */
  private populateUnidadesVinculacionConTipo(
    items: IPeticionEvaluacionUnidadVinculacion[]
  ): Observable<IPeticionEvaluacionUnidadVinculacionListado[]> {
    if (!items?.length) {
      return of([]);
    }

    const listado: IPeticionEvaluacionUnidadVinculacionListado[] = items.map(item => ({
      id: item.id,
      peticionEvaluacionId: item.peticionEvaluacionId,
      unidadVinculacion: item.unidadVinculacion,
      tipoUnidad: undefined
    }));

    const cache = new Map<string, IUnidadVinculacion>();
    const ids = [...new Set(listado.map(item => item.unidadVinculacion?.id).filter(id => !!id))];

    return this.unidadVinculacionService.findAllByIdIn(ids, false).pipe(
      switchMap(response => {
        response.items.forEach(unidad => cache.set(unidad.id, unidad));
        listado.forEach(item => {
          if (item.unidadVinculacion?.id) {
            item.unidadVinculacion = cache.get(item.unidadVinculacion.id) ?? item.unidadVinculacion;
          }
        });

        return this.populateTipoUnidad(listado, cache);
      })
    );
  }

  /**
   * Asigna a cada item su `tipoUnidad` (la unidad raíz de la jerarquía) subiendo por la cadena de `predecesorId`.
   *
   * @param items listado al que asignar el `tipoUnidad`.
   * @param cache unidades de vinculación ya resueltas, indexadas por id; se amplía en cada pasada.
   * @param requested ids ya solicitados al SGO; evita volver a pedir predecesores inexistentes.
   */
  private populateTipoUnidad(
    items: IPeticionEvaluacionUnidadVinculacionListado[],
    cache: Map<string, IUnidadVinculacion>,
    requested: Set<string> = new Set()
  ): Observable<IPeticionEvaluacionUnidadVinculacionListado[]> {
    const predecesorIds = [...new Set(
      [...cache.values()]
        .map(unidad => unidad.predecesorId)
        .filter(id => !!id && !cache.has(id) && !requested.has(id))
    )];

    if (!predecesorIds.length) {
      items.forEach(item => {
        item.tipoUnidad = this.getRootUnidadVinculacion(item.unidadVinculacion, cache);
      });
      return of(items);
    }

    predecesorIds.forEach(id => requested.add(id));

    return this.unidadVinculacionService.findAllByIdIn(predecesorIds, false).pipe(
      switchMap(response => {
        response.items.forEach(unidad => cache.set(unidad.id, unidad));
        return this.populateTipoUnidad(items, cache, requested);
      })
    );
  }

  private getRootUnidadVinculacion(unidad: IUnidadVinculacion, cache: Map<string, IUnidadVinculacion>): IUnidadVinculacion {
    let current = unidad;
    while (current?.predecesorId && cache.has(current.predecesorId)) {
      current = cache.get(current.predecesorId);
    }

    return current;
  }

}
