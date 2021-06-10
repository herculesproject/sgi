import { IProyectoPartida } from '@core/models/csp/proyecto-partida';
import { Fragment } from '@core/services/action-service';
import { ProyectoPartidaService } from '@core/services/csp/proyecto-partida.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, concat, from, Observable, of } from 'rxjs';
import { map, mergeMap, takeLast, tap } from 'rxjs/operators';

export class ProyectoPartidasPresupuestariasFragment extends Fragment {
  partidasPresupuestarias$ = new BehaviorSubject<StatusWrapper<IProyectoPartida>[]>([]);
  private partidasPresupuestariasEliminadas: StatusWrapper<IProyectoPartida>[] = [];

  constructor(
    key: number,
    private proyectoService: ProyectoService,
    private proyectoPartidaService: ProyectoPartidaService,
    public readonly: boolean
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const id = this.getKey() as number;
      this.subscriptions.push(
        this.proyectoService.findAllProyectoPartidas(id).pipe(
          map(response => response.items)
        ).subscribe(
          result => {
            this.partidasPresupuestarias$.next(
              result.map(partidaPresupuestaria =>
                new StatusWrapper<IProyectoPartida>(partidaPresupuestaria)
              )
            );
          }
        )
      );
    }
  }

  addPartidaPresupuestaria(partidaPresupuestaria: IProyectoPartida) {
    const wrapped = new StatusWrapper<IProyectoPartida>(partidaPresupuestaria);
    wrapped.setCreated();
    const current = this.partidasPresupuestarias$.value;
    current.push(wrapped);
    this.partidasPresupuestarias$.next(current);
    this.setChanges(true);
  }

  updatePartidaPresupuestaria(wrapper: StatusWrapper<IProyectoPartida>): void {
    const current = this.partidasPresupuestarias$.value;
    const index = current.findIndex(value => value.value.id === wrapper.value.id);
    if (index >= 0) {
      wrapper.setEdited();
      this.partidasPresupuestarias$.value[index] = wrapper;
      this.setChanges(true);
    }
  }

  deletePartidaPresupuestaria(wrapper: StatusWrapper<IProyectoPartida>) {
    const current = this.partidasPresupuestarias$.value;
    const index = current.findIndex(
      (value) => value === wrapper
    );
    if (index >= 0) {
      if (!wrapper.created) {
        this.partidasPresupuestariasEliminadas.push(current[index]);
      }
      current.splice(index, 1);
      this.partidasPresupuestarias$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    return concat(
      this.deletePartidasPresupuestarias(),
      this.updatePartidasPresupuestarias(),
      this.createPartidasPresupuestarias()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private deletePartidasPresupuestarias(): Observable<void> {
    if (this.partidasPresupuestariasEliminadas.length === 0) {
      return of(void 0);
    }
    return from(this.partidasPresupuestariasEliminadas).pipe(
      mergeMap((wrapped) => {
        return this.proyectoPartidaService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.partidasPresupuestariasEliminadas = this.partidasPresupuestariasEliminadas
                .filter(deletedPartidaPresupuestaria => deletedPartidaPresupuestaria.value.id !== wrapped.value.id);
            })
          );
      })
    );
  }

  private createPartidasPresupuestarias(): Observable<void> {
    const createdPartidasPresupuestarias = this.partidasPresupuestarias$.value
      .filter((partidaPresupuestaria) => partidaPresupuestaria.created);
    if (createdPartidasPresupuestarias.length === 0) {
      return of(void 0);
    }
    createdPartidasPresupuestarias.forEach(
      (wrapper) => wrapper.value.proyectoId = this.getKey() as number
    );
    return from(createdPartidasPresupuestarias).pipe(
      mergeMap((wrappedPartidaPresupuestaria) => {
        return this.proyectoPartidaService.create(wrappedPartidaPresupuestaria.value).pipe(
          map((createdPartidaPresupuestaria) => {
            const index = this.partidasPresupuestarias$.value
              .findIndex((currentPartidaPresupuestaria) => currentPartidaPresupuestaria === wrappedPartidaPresupuestaria);
            this.partidasPresupuestarias$.value[index] = new StatusWrapper<IProyectoPartida>(createdPartidaPresupuestaria);
          })
        );
      })
    );
  }

  private updatePartidasPresupuestarias(): Observable<void> {
    const updatedPartidasPresupuestarias = this.partidasPresupuestarias$.value.filter((proyectoHito) => proyectoHito.edited);
    if (updatedPartidasPresupuestarias.length === 0) {
      return of(void 0);
    }
    return from(updatedPartidasPresupuestarias).pipe(
      mergeMap((wrappedPartidaPresupuestaria) => {
        return this.proyectoPartidaService.update(wrappedPartidaPresupuestaria.value.id, wrappedPartidaPresupuestaria.value).pipe(
          map((updatedPartidaPresupuestaria) => {
            const index = this.partidasPresupuestarias$.value
              .findIndex((currentPartidaPresupuestaria) => currentPartidaPresupuestaria === wrappedPartidaPresupuestaria);
            this.partidasPresupuestarias$.value[index] = new StatusWrapper<IProyectoPartida>(updatedPartidaPresupuestaria);
          })
        );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.partidasPresupuestarias$.value.some((wrapper) => wrapper.touched);
    return !(this.partidasPresupuestariasEliminadas.length > 0 || touched);
  }
}
