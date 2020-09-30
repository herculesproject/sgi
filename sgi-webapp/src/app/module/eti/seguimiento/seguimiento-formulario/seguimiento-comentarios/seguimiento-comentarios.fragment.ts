import { IComentario } from '@core/models/eti/comentario';
import { Fragment } from '@core/services/action-service';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, merge, Observable, of, from } from 'rxjs';
import { endWith, map, takeLast, tap, mergeMap } from 'rxjs/operators';

export class SeguimientoComentarioFragment extends Fragment {
  comentarios$: BehaviorSubject<StatusWrapper<IComentario>[]>;
  comentariosEliminados: StatusWrapper<IComentario>[];

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private service: EvaluacionService
  ) {
    super(key);
    this.logger.debug(SeguimientoComentarioFragment.name, 'constructor()', 'start');
    this.comentarios$ = new BehaviorSubject<StatusWrapper<IComentario>[]>([]);
    this.comentariosEliminados = [];
    this.logger.debug(SeguimientoComentarioFragment.name, 'constructor()', 'end');
  }

  protected onInitialize(): void {
    this.logger.debug(SeguimientoComentarioFragment.name, 'onInitialize()', 'start');
    if (this.getKey()) {
      this.service.getComentariosEvaluador(this.getKey() as number).pipe(
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
        this.logger.debug(SeguimientoComentarioFragment.name, 'onInitialize()', 'end');
      });
    }
  }

  saveOrUpdate(): Observable<void> {
    this.logger.debug(SeguimientoComentarioFragment.name, 'saveOrUpdate()', 'start');
    return merge(
      this.deleteComentariosEvaluador(),
      this.updateComentariosEvaluador(),
      this.createComentariosEvaluador()
    ).pipe(
      takeLast(1),
      tap(() => this.setChanges(false)),
      tap(() => this.logger.debug(SeguimientoComentarioFragment.name, 'saveOrUpdate()', 'end'))
    );
  }

  public addComentario(comentario: IComentario) {
    this.logger.debug(SeguimientoComentarioFragment.name, `addComentario(comentario: ${comentario})`, 'start');
    const wrapped = new StatusWrapper<IComentario>(comentario);
    wrapped.setCreated();
    const current = this.comentarios$.value;
    current.push(wrapped);
    this.comentarios$.next(current);
    this.setChanges(true);
    this.logger.debug(SeguimientoComentarioFragment.name, `addComentario(comentario: ${comentario})`, 'end');
  }

  public deleteComentario(comentario: StatusWrapper<IComentario>) {
    this.logger.debug(SeguimientoComentarioFragment.name, `deleteComentario(comentario: ${comentario})`, 'start');
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
    this.logger.debug(SeguimientoComentarioFragment.name, `deleteComentario(comentario: ${comentario})`, 'end');
  }

  private deleteComentariosEvaluador(): Observable<void> {
    this.logger.debug(SeguimientoComentarioFragment.name, 'deleteComentarios()', 'start');
    if (this.comentariosEliminados.length === 0) {
      this.logger.debug(SeguimientoComentarioFragment.name, 'deleteComentarios()', 'end');
      return of(void 0);
    }
    return from(this.comentariosEliminados).pipe(
      mergeMap((wrappedComentario) => {

        return this.service.deleteComentarioEvaluador(this.getKey() as number, wrappedComentario.value.id);
      }),
      endWith()
    );
  }

  private updateComentariosEvaluador(): Observable<void> {
    this.logger.debug(SeguimientoComentarioFragment.name, 'updateComentarios()', 'start');
    const comentariosEditados = this.comentarios$.value.filter((comentario) => comentario.edited);
    if (comentariosEditados.length === 0) {
      this.logger.debug(SeguimientoComentarioFragment.name, 'updateComentarios()', 'end');
      return of(void 0);
    }
    return from(comentariosEditados).pipe(
      mergeMap((wrappedComentario) => {

        return this.service.updateComentarioEvaluador(this.getKey() as number, wrappedComentario.value, wrappedComentario.value.id).pipe(
          map((updatedComentario) => {
            const index = this.comentarios$.value.findIndex((currentComentario) => currentComentario === wrappedComentario);
            this.comentarios$[index] = new StatusWrapper<IComentario>(updatedComentario);
          })
        );
      }),
      endWith()
    );
  }

  private createComentariosEvaluador(): Observable<void> {
    this.logger.debug(SeguimientoComentarioFragment.name, 'createComentarios()', 'start');
    const comentariosCreados = this.comentarios$.value.filter((comentario) => comentario.created);
    if (comentariosCreados.length === 0) {
      this.logger.debug(SeguimientoComentarioFragment.name, 'createComentarios()', 'end');
      return of(void 0);
    }
    return from(comentariosCreados).pipe(
      mergeMap((wrappedComentario) => {

        return this.service.createComentarioEvaluador(this.getKey() as number, wrappedComentario.value).pipe(
          map((savedComentario) => {
            const index = this.comentarios$.value.findIndex((currentComentario) => currentComentario === wrappedComentario);
            this.comentarios$[index] = new StatusWrapper<IComentario>(savedComentario);
          })
        );
      }),
      endWith(),
      tap(() => this.logger.debug(SeguimientoComentarioFragment.name, 'createComentarios()', 'end'))
    );
  }
}
