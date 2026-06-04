import { IProyectoUnidadVinculacion } from '@core/models/csp/proyecto-unidad-vinculacion';
import { IUnidadVinculacion } from '@core/models/sgo/unidad-vinculacion';
import { Fragment } from '@core/services/action-service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { UnidadVinculacionService } from '@core/services/sgo/unidad-vinculacion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';

export interface IProyectoUnidadVinculacionListado {
  id: number;
  proyectoId: number;
  unidadVinculacion: IUnidadVinculacion;
  tipoUnidad: IUnidadVinculacion;
}

export class ProyectoUnidadesVinculacionFragment extends Fragment {
  unidadesVinculacion$ = new BehaviorSubject<StatusWrapper<IProyectoUnidadVinculacionListado>[]>([]);

  constructor(
    key: number,
    private readonly proyectoService: ProyectoService,
    private readonly unidadService: UnidadVinculacionService,
    public readonly readonly: boolean,
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (!this.getKey()) {
      return;
    }

    const proyectoId = this.getKey() as number;

    this.subscriptions.push(
      this.proyectoService.findUnidadesVinculacion(proyectoId).pipe(
        map(response => response.items),
        switchMap(items => this.populateUnidadesVinculacionConTipo(items))
      ).subscribe(items => {
        this.unidadesVinculacion$.next(items.map(item => {
          item.proyectoId = proyectoId;
          return new StatusWrapper<IProyectoUnidadVinculacionListado>(item);
        }));
      })
    );
  }

  addProyectoUnidad(element: IProyectoUnidadVinculacionListado): void {
    const wrapper = new StatusWrapper<IProyectoUnidadVinculacionListado>(element);
    wrapper.setCreated();
    const current = this.unidadesVinculacion$.value;
    current.push(wrapper);
    this.unidadesVinculacion$.next(current);
    this.setChanges(true);
  }

  deleteProyectoUnidad(wrapper: StatusWrapper<IProyectoUnidadVinculacionListado>): void {
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
    const proyectoId = this.getKey() as number;
    const unidadesVinculacion = this.unidadesVinculacion$.value.map(wrapper => wrapper.value);

    return this.proyectoService.updateUnidadesVinculacion(proyectoId, unidadesVinculacion).pipe(
      tap(() => {
        this.unidadesVinculacion$.next(
          this.unidadesVinculacion$.value.map(wrapper => new StatusWrapper<IProyectoUnidadVinculacionListado>(wrapper.value))
        );
        this.setChanges(false);
      }),
      map(() => void 0)
    );
  }

  /**
   * Construye el listado de unidades de vinculación del proyecto resolviendo, en lote contra el SGO, tanto el detalle
   * de cada unidad asignada (`unidadVinculacion`) como su elemento raíz en la jerarquía (`tipoUnidad`).
   */
  private populateUnidadesVinculacionConTipo(
    items: IProyectoUnidadVinculacion[]
  ): Observable<IProyectoUnidadVinculacionListado[]> {
    if (!items?.length) {
      return of([]);
    }

    const listado: IProyectoUnidadVinculacionListado[] = items.map(item => ({
      id: item.id,
      proyectoId: item.proyectoId,
      unidadVinculacion: item.unidadVinculacion,
      tipoUnidad: undefined
    }));

    const cache = new Map<string, IUnidadVinculacion>();
    const ids = [...new Set(listado.map(item => item.unidadVinculacion?.id).filter(id => !!id))];

    return this.unidadService.findAllByIdIn(ids, false).pipe(
      switchMap(response => {
        response.items.forEach(u => cache.set(u.id, u));
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
   * En cada iteración recolecta los predecesores que aún no estén en la caché y los recupera en lote.
   * Cuando no quedan predecesores, fija el `tipoUnidad` de cada item.
   *
   * @param items listado al que asignar el `tipoUnidad`.
   * @param cache unidades de vinculación ya resueltas, indexadas por id; se amplía en cada pasada.
   * @param requested ids ya solicitados al SGO; evita volver a pedir predecesores inexistentes (datos inconsistentes).
   */
  private populateTipoUnidad(
    items: IProyectoUnidadVinculacionListado[],
    cache: Map<string, IUnidadVinculacion>,
    requested: Set<string> = new Set()
  ): Observable<IProyectoUnidadVinculacionListado[]> {
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

    return this.unidadService.findAllByIdIn(predecesorIds, false).pipe(
      switchMap(response => {
        response.items.forEach(u => cache.set(u.id, u));
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
