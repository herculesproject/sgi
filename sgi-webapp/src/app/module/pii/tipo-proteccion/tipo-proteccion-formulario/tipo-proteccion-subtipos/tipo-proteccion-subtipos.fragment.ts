import { ITipoProteccion } from '@core/models/pii/tipo-proteccion';
import { Fragment } from '@core/services/action-service';
import { TipoProteccionService } from '@core/services/pii/tipo-proteccion/tipo-proteccion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { concatMap, filter, map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export class TipoProteccionSubtiposFragment extends Fragment {

  tipoProteccionPadre: ITipoProteccion;
  subtiposProteccion$ = new BehaviorSubject<StatusWrapper<ITipoProteccion>[]>([]);

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private tipoProteccionService: TipoProteccionService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const cargarPadre$ = this.tipoProteccionService.findById(this.getKey() as number);
      const cargarSubtipos$ = this.tipoProteccionService
        .findSubtipos(this.getKey() as number).pipe(
          switchMap((sgiResultSubtipos) => {
            return of(sgiResultSubtipos.items.map(item => new StatusWrapper<ITipoProteccion>(item)));
          }));

      this.subscriptions.push(
        cargarPadre$.subscribe((tipoProteccionPadre) => { this.tipoProteccionPadre = tipoProteccionPadre; }),
        cargarSubtipos$.subscribe(subtipos => this.subtiposProteccion$.next(subtipos)));
    }
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.subtiposProteccionEliminar(),
      this.subtiposProteccionCrear(),
      this.subtiposProteccionActualizar(),
      this.subtiposProteccionRecuperar()
    ).pipe(
      takeLast(1),
      tap(() => {
        this.setChanges(false);
      })
    );
  }

  private subtiposProteccionCrear(): Observable<void> {
    const subtiposProteccionCreados = this.subtiposProteccion$.value.filter((subtipoProteccion) => subtipoProteccion.created);
    if (subtiposProteccionCreados.length === 0) {
      return of(void 0);
    }
    return from(subtiposProteccionCreados).pipe(
      mergeMap(wrappedSubtiposProteccion => {
        return this.tipoProteccionService.create(wrappedSubtiposProteccion.value).pipe(
          map((subtiposProteccionCreado) => {
            const index = this.subtiposProteccion$.value
              .findIndex((currentSubtipoProteccion) => currentSubtipoProteccion === wrappedSubtiposProteccion);
            this.subtiposProteccion$.value[index] = new StatusWrapper<ITipoProteccion>(subtiposProteccionCreado);
          })
        );
      })
    );
  }

  private subtiposProteccionEliminar(): Observable<void> {
    return from(this.subtiposProteccion$.value).pipe(
      filter(subtipo => subtipo.deleted && subtipo.value.activo === false),
      mergeMap((wrapped) => {
        return this.tipoProteccionService.deactivate(wrapped.value.id);
      }),
    );
  }

  private subtiposProteccionRecuperar(): Observable<any> {
    return from(this.subtiposProteccion$.value).pipe(
      filter(subtipo => subtipo.deleted && subtipo.value.activo === true),
      concatMap((wrapped) => {
        return this.tipoProteccionService.activate(wrapped.value.id);
      }),
    );
  }

  private subtiposProteccionActualizar(): Observable<void> {
    const subtiposProteccionActualizar = this.subtiposProteccion$.value.filter((subtipoProteccion) => subtipoProteccion.edited);
    if (subtiposProteccionActualizar.length === 0) {
      return of(void 0);
    }
    return from(subtiposProteccionActualizar).pipe(
      mergeMap(wrappedSubtiposProteccion => {
        return this.tipoProteccionService.update(wrappedSubtiposProteccion.value.id, wrappedSubtiposProteccion.value).pipe(
          map((subtipoProteccionActualizado) => {
            const index = this.subtiposProteccion$.value
              .findIndex((currentSubtipoProteccion) => currentSubtipoProteccion === wrappedSubtiposProteccion);
            this.subtiposProteccion$.value[index] = new StatusWrapper<ITipoProteccion>(subtipoProteccionActualizado);
          })
        );
      })
    );
  }

  public agregarSubtiposProteccion(tiposProteccion: StatusWrapper<ITipoProteccion>[]) {
    if (tiposProteccion && tiposProteccion.length > 0) {
      const current = this.subtiposProteccion$.value;
      tiposProteccion.forEach((tipoProteccion) => {
        tipoProteccion.value.padre = { id: this.getKey() as number } as ITipoProteccion;
        tipoProteccion.value.tipoPropiedad = this.tipoProteccionPadre.tipoPropiedad;
        tipoProteccion.setCreated();
        current.push(tipoProteccion);
      });
      this.subtiposProteccion$.next(current);
      this.setChanges(true);
    }
  }

  public desactivarSubtipoProteccion(wrapper: StatusWrapper<ITipoProteccion>) {
    const current = this.subtiposProteccion$.value;
    const index = current.findIndex(
      (value) => value === wrapper
    );
    if (index >= 0) {
      if (current[index].value.id !== (void 0)) {
        current[index].setDeleted();
        current[index].value.activo = false;
        this.setChanges(true);
      } else {
        current.splice(index, 1);
        this.subtiposProteccion$.next(current);
        this.setChanges(true);
      }

    }
  }

  public activarSubtipoProteccion(wrapper: StatusWrapper<ITipoProteccion>) {
    const current = this.subtiposProteccion$.value;
    const index = current.findIndex(
      (value) => value === wrapper
    );
    if (index >= 0) {
      current[index].value.activo = true;
      current[index].setDeleted();
      this.setChanges(true);
    }
  }

}
