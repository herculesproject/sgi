import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoEnlace } from '@core/models/csp/grupo-enlace';
import { ITipoEnlace } from '@core/models/csp/tipos-configuracion';
import { Fragment } from '@core/services/action-service';
import { GrupoEnlaceService } from '@core/services/csp/grupo-enlace/grupo-enlace.service';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { TipoEnlaceService } from '@core/services/csp/tipo-enlace.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';


export class GrupoEnlaceFragment extends Fragment {
  enlaces$ = new BehaviorSubject<StatusWrapper<IGrupoEnlace>[]>([]);
  private gruposEnlaceEliminados: StatusWrapper<IGrupoEnlace>[] = [];

  constructor(
    key: number,
    private readonly grupoService: GrupoService,
    private readonly grupoEnlaceService: GrupoEnlaceService,
    private readonly tipoEnlaceService: TipoEnlaceService,
    public readonly readonly: boolean,
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const id = this.getKey() as number;
      this.subscriptions.push(
        this.grupoService.findEnlaces(id).pipe(
          map((response) => response.items),
          switchMap((enlaces) => this.populateTiposEnlace(enlaces)),
        ).subscribe((enlaces) => {
          this.enlaces$.next(enlaces.map(
            enlace => {
              enlace.grupo = { id: this.getKey() } as IGrupo;
              return new StatusWrapper<IGrupoEnlace>(enlace);
            })
          );
        }));
    }
  }

  addGrupoEnlace(element: IGrupoEnlace) {
    const wrapper = new StatusWrapper<IGrupoEnlace>(element);
    wrapper.setCreated();
    const current = this.enlaces$.value;
    current.push(wrapper);
    this.enlaces$.next(current);
    this.setChanges(true);
    return element;
  }

  updateGrupoEnlace(wrapper: StatusWrapper<IGrupoEnlace>): void {
    const current = this.enlaces$.value;
    const index = current.findIndex(value => value.value.id === wrapper.value.id);
    if (index >= 0) {
      wrapper.setEdited();
      this.enlaces$.value[index] = wrapper;
      this.setChanges(true);
    }
  }

  deleteGrupoEnlace(wrapper: StatusWrapper<IGrupoEnlace>) {
    const current = this.enlaces$.value;
    const index = current.findIndex((value) => value === wrapper);

    if (index >= 0) {
      if (!wrapper.created) {
        this.gruposEnlaceEliminados.push(current[index]);
      }
      current.splice(index, 1);
      this.enlaces$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.deleteGrupoEnlaces(),
      this.updateGrupoEnlaces(),
      this.createGrupoEnlaces()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private deleteGrupoEnlaces(): Observable<void> {
    if (this.gruposEnlaceEliminados.length === 0) {
      return of(void 0);
    }
    return from(this.gruposEnlaceEliminados).pipe(
      mergeMap((wrapped) => {
        return this.grupoEnlaceService.deleteById(wrapped.value.id).pipe(
          tap(() => {
            this.gruposEnlaceEliminados = this.gruposEnlaceEliminados.filter(deleted =>
              deleted.value.id !== wrapped.value.id);
          })
        );
      })
    );
  }

  private updateGrupoEnlaces(): Observable<void> {
    const editedEntidades = this.enlaces$.value.filter((value) => value.edited);
    if (editedEntidades.length === 0) {
      return of(void 0);
    }
    return from(editedEntidades).pipe(
      mergeMap((data) => {
        return this.grupoEnlaceService.update(
          data.value.id, data.value).pipe(
            map((updated) => {
              updated.tipoEnlace = data.value.tipoEnlace;
              const index = this.enlaces$.value.findIndex((current) => current === data);
              this.enlaces$.value[index] = new StatusWrapper<IGrupoEnlace>(updated);
              this.enlaces$.next(this.enlaces$.value);
            })
          );
      })
    );
  }

  private createGrupoEnlaces(): Observable<void> {
    const createdEntidades = this.enlaces$.value.filter((value) => value.created);
    if (createdEntidades.length === 0) {
      return of(void 0);
    }
    createdEntidades.forEach(
      (wrapper) => wrapper.value.grupo.id = this.getKey() as number
    );
    return from(createdEntidades).pipe(
      mergeMap((data) => {
        return this.grupoEnlaceService.create(data.value).pipe(
          map((createdEntidad) => {
            createdEntidad.tipoEnlace = data.value.tipoEnlace;
            const index = this.enlaces$.value.findIndex((current) => current === data);
            this.enlaces$.value[index] = new StatusWrapper<IGrupoEnlace>(createdEntidad);
            this.enlaces$.next(this.enlaces$.value);
          })
        );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const hasTouched = this.enlaces$.value.some((wrapper) => wrapper.touched);
    return !hasTouched;
  }

  private populateTiposEnlace(enlaces: IGrupoEnlace[]): Observable<IGrupoEnlace[]> {
    const ids = [...new Set(enlaces.map(e => e.tipoEnlace?.id).filter(id => !!id))];
    if (!ids?.length) {
      return of(enlaces);
    }

    return this.tipoEnlaceService.findAllByIdIn(ids).pipe(
      map((response) => {
        const tiposEnlaceById = new Map<number, ITipoEnlace>(response.items.map(t => [t.id, t]));
        enlaces.forEach(enlace => {
          if (enlace.tipoEnlace?.id) {
            enlace.tipoEnlace = tiposEnlaceById.get(enlace.tipoEnlace.id) ?? enlace.tipoEnlace;
          }
        });

        return enlaces;
      })
    );
  }

}

