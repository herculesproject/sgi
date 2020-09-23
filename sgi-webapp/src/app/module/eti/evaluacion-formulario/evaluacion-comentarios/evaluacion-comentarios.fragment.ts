import { Fragment } from '@core/services/action-service';
import { Observable, of, BehaviorSubject, merge } from 'rxjs';
import { map, endWith, tap, takeLast } from 'rxjs/operators';
import { Comentario } from '@core/models/eti/comentario';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';



export class EvaluacionComentarioFragment extends Fragment {

  comentarios$: BehaviorSubject<StatusWrapper<Comentario>[]> = new BehaviorSubject<StatusWrapper<Comentario>[]>([]);
  comentariosEliminados: StatusWrapper<Comentario>[] = [];

  constructor(key: number, private service: EvaluacionService) {
    super(key);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      this.service.getComentarios(this.getKey() as number).pipe(
        map((response) => {
          if (response.items) {
            return response.items;
          }
          else {
            return [];
          }
        })
      ).subscribe((comentarios) => {
        this.comentarios$.next(comentarios.map(comentario => new StatusWrapper<Comentario>(comentario)));
      });
    }
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.deleteComentarios(),
      this.updateComentarios(),
      this.createComentarios()
    ).pipe(
      takeLast(1),
      tap(() => this.setChanges(false))
    );
  }

  public addComentario(comentario: Comentario) {
    const wrapped = new StatusWrapper<Comentario>(comentario);
    wrapped.setCreated();
    const current = this.comentarios$.value;
    current.push(wrapped);
    this.comentarios$.next(current);
    this.setChanges(true);
  }

  public deleteComentario(comentario: StatusWrapper<Comentario>) {
    const current = this.comentarios$.value;
    const index = current.findIndex((value) => value === comentario);
    if (index >= 0) {
      if (!comentario.created) {
        this.comentariosEliminados.push(current[index]);
      }
      current.splice(index, 1);
      this.comentarios$.next(current);
      this.setChanges(true);
    }
  }

  private deleteComentarios(): Observable<void> {
    if (this.comentariosEliminados.length === 0) {
      return of(void 0);
    }
    return this.service.deleteComentarios(
      this.getKey() as number,
      this.comentariosEliminados.map((comentarioWrapped) => comentarioWrapped.value.id)
    ).pipe(
      map(() => {
        this.comentariosEliminados = [];
      })
    );
  }

  private updateComentarios(): Observable<void> {
    const comentariosEditados = this.comentarios$.value.filter((comentario) => comentario.edited);
    if (comentariosEditados.length === 0) {
      return of(void 0);
    }
    return this.service.updateComentarios(
      this.getKey() as number,
      comentariosEditados.map((comentarioWrapped) => comentarioWrapped.value)
    ).pipe(
      map((comentarios) => {
        const updatedComentarios = this.comentarios$.value.filter((comentario) => !comentario.edited);
        updatedComentarios.push(...comentarios.map((comentario) => new StatusWrapper<Comentario>(comentario)));
        this.comentarios$.next(updatedComentarios);
      }),
      endWith()
    );
  }

  private createComentarios(): Observable<void> {
    const comentariosCreados = this.comentarios$.value.filter((comentario) => comentario.created);
    if (comentariosCreados.length === 0) {
      return of(void 0);
    }
    return this.service.createComentarios(
      this.getKey() as number,
      comentariosCreados.map((comentarioWrapped) => comentarioWrapped.value)
    ).pipe(
      map((comentarios) => {
        const updatedComentarios = this.comentarios$.value.filter((comentario) => !comentario.created);
        updatedComentarios.push(...comentarios.map((comentario) => new StatusWrapper<Comentario>(comentario)));
        this.comentarios$.next(updatedComentarios);
      }),
      endWith()
    );
  }
}
