import { IComentario } from '@core/models/eti/comentario';
import { Fragment } from '@core/services/action-service';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, merge, Observable, of } from 'rxjs';
import { endWith, map, takeLast, tap } from 'rxjs/operators';



export class EvaluacionComentarioFragment extends Fragment {

  comentarios$: BehaviorSubject<StatusWrapper<IComentario>[]> = new BehaviorSubject<StatusWrapper<IComentario>[]>([]);
  comentariosEliminados: StatusWrapper<IComentario>[] = [];

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private service: EvaluacionService
  ) {
    super(key);
    this.logger.debug(EvaluacionComentarioFragment.name, 'constructor()', 'start');
    this.logger.debug(EvaluacionComentarioFragment.name, 'constructor()', 'end');
  }

  protected onInitialize(): void {
    this.logger.debug(EvaluacionComentarioFragment.name, 'onInitialize()', 'start');
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
        this.comentarios$.next(comentarios.map(comentario => new StatusWrapper<IComentario>(comentario)));
        this.logger.debug(EvaluacionComentarioFragment.name, 'onInitialize()', 'end');
      });
    }
  }

  saveOrUpdate(): Observable<void> {
    this.logger.debug(EvaluacionComentarioFragment.name, 'saveOrUpdate()', 'start');
    return merge(
      this.deleteComentarios(),
      this.updateComentarios(),
      this.createComentarios()
    ).pipe(
      takeLast(1),
      tap(() => this.setChanges(false)),
      tap(() => this.logger.debug(EvaluacionComentarioFragment.name, 'saveOrUpdate()', 'end'))
    );
  }

  public addComentario(comentario: IComentario) {
    this.logger.debug(EvaluacionComentarioFragment.name, `addComentario(comentario: ${comentario})`, 'start');
    const wrapped = new StatusWrapper<IComentario>(comentario);
    wrapped.setCreated();
    const current = this.comentarios$.value;
    current.push(wrapped);
    this.comentarios$.next(current);
    this.setChanges(true);
    this.logger.debug(EvaluacionComentarioFragment.name, `addComentario(comentario: ${comentario})`, 'end');
  }

  public deleteComentario(comentario: StatusWrapper<IComentario>) {
    this.logger.debug(EvaluacionComentarioFragment.name, `deleteComentario(comentario: ${comentario})`, 'start');
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
    this.logger.debug(EvaluacionComentarioFragment.name, `deleteComentario(comentario: ${comentario})`, 'end');
  }

  private deleteComentarios(): Observable<void> {
    this.logger.debug(EvaluacionComentarioFragment.name, 'deleteComentarios()', 'start');
    if (this.comentariosEliminados.length === 0) {
      this.logger.debug(EvaluacionComentarioFragment.name, 'deleteComentarios()', 'end');
      return of(void 0);
    }
    return this.service.deleteComentarios(
      this.getKey() as number,
      this.comentariosEliminados.map((comentarioWrapped) => comentarioWrapped.value.id)
    ).pipe(
      map(() => {
        this.comentariosEliminados = [];
      }),
      tap(() => this.logger.debug(EvaluacionComentarioFragment.name, 'deleteComentarios()', 'end'))
    );
  }

  private updateComentarios(): Observable<void> {
    this.logger.debug(EvaluacionComentarioFragment.name, 'updateComentarios()', 'start');
    const comentariosEditados = this.comentarios$.value.filter((comentario) => comentario.edited);
    if (comentariosEditados.length === 0) {
      this.logger.debug(EvaluacionComentarioFragment.name, 'updateComentarios()', 'end');
      return of(void 0);
    }
    return this.service.updateComentarios(
      this.getKey() as number,
      comentariosEditados.map((comentarioWrapped) => comentarioWrapped.value)
    ).pipe(
      map((comentarios) => {
        const updatedComentarios = this.comentarios$.value.filter((comentario) => !comentario.edited);
        updatedComentarios.push(...comentarios.map((comentario) => new StatusWrapper<IComentario>(comentario)));
        this.comentarios$.next(updatedComentarios);
      }),
      endWith(),
      tap(() => this.logger.debug(EvaluacionComentarioFragment.name, 'updateComentarios()', 'end'))
    );
  }

  private createComentarios(): Observable<void> {
    this.logger.debug(EvaluacionComentarioFragment.name, 'createComentarios()', 'start');
    const comentariosCreados = this.comentarios$.value.filter((comentario) => comentario.created);
    if (comentariosCreados.length === 0) {
      this.logger.debug(EvaluacionComentarioFragment.name, 'createComentarios()', 'end');
      return of(void 0);
    }
    return this.service.createComentarios(
      this.getKey() as number,
      comentariosCreados.map((comentarioWrapped) => comentarioWrapped.value)
    ).pipe(
      map((comentarios) => {
        const updatedComentarios = this.comentarios$.value.filter((comentario) => !comentario.created);
        updatedComentarios.push(...comentarios.map((comentario) => new StatusWrapper<IComentario>(comentario)));
        this.comentarios$.next(updatedComentarios);
      }),
      endWith(),
      tap(() => this.logger.debug(EvaluacionComentarioFragment.name, 'createComentarios()', 'end'))
    );
  }
}
