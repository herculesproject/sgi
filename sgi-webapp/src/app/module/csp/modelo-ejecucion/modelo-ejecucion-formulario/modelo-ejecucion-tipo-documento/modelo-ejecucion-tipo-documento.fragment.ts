import { IModeloTipoDocumento } from '@core/models/csp/modelo-tipo-documento';
import { IModeloEjecucion } from '@core/models/csp/tipos-configuracion';
import { Fragment } from '@core/services/action-service';
import { ModeloEjecucionService } from '@core/services/csp/modelo-ejecucion.service';
import { ModeloTipoDocumentoService } from '@core/services/csp/modelo-tipo-documento.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, takeLast, tap } from 'rxjs/operators';
import { ModeloEjecucionActionService } from '../../modelo-ejecucion.action.service';

export class ModeloEjecucionTipoDocumentoFragment extends Fragment {
  modeloTipoDocumento$ = new BehaviorSubject<StatusWrapper<IModeloTipoDocumento>[]>([]);
  modeloTipoDocumentoEliminados: StatusWrapper<IModeloTipoDocumento>[] = [];

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private modeloEjecucionService: ModeloEjecucionService,
    private modeloTipoDocumentoService: ModeloTipoDocumentoService,
    private actionService: ModeloEjecucionActionService
  ) {
    super(key);
    this.logger.debug(ModeloEjecucionTipoDocumentoFragment.name, 'constructor()', 'start');
    this.setComplete(true);
    this.logger.debug(ModeloEjecucionTipoDocumentoFragment.name, 'constructor()', 'end');
  }

  protected onInitialize(): void {
    this.logger.debug(ModeloEjecucionTipoDocumentoFragment.name, `${this.onInitialize.name}()`, 'start');
    if (this.getKey()) {
      this.modeloEjecucionService.findModeloTipoDocumento(this.getKey() as number).pipe(
        map((response: SgiRestListResult<IModeloTipoDocumento>) => response.items)
      ).subscribe(
        (modelosTipoDocumento: IModeloTipoDocumento[]) => {
          this.modeloTipoDocumento$.next(
            modelosTipoDocumento.map(modeloTipoDocumento => new StatusWrapper<IModeloTipoDocumento>(modeloTipoDocumento))
          );
        }
      );
    }
    this.logger.debug(ModeloEjecucionTipoDocumentoFragment.name, `${this.onInitialize.name}()`, 'end');
  }

  public addModeloTipoDocumento(modeloTipoDocumento: IModeloTipoDocumento) {
    this.logger.debug(ModeloEjecucionTipoDocumentoFragment.name,
      `${this.addModeloTipoDocumento.name}(modeloTipoDocumento: ${modeloTipoDocumento})`, 'start');
    const wrapped = new StatusWrapper<IModeloTipoDocumento>(modeloTipoDocumento);
    wrapped.setCreated();
    const current = this.modeloTipoDocumento$.value;
    current.push(wrapped);
    this.modeloTipoDocumento$.next(current);
    this.setChanges(true);
    this.logger.debug(ModeloEjecucionTipoDocumentoFragment.name,
      `${this.addModeloTipoDocumento.name}(modeloTipoDocumento: ${modeloTipoDocumento})`, 'end');
  }

  public deleteModeloTipoDocumento(wrapper: StatusWrapper<IModeloTipoDocumento>) {
    this.logger.debug(ModeloEjecucionTipoDocumentoFragment.name,
      `${this.deleteModeloTipoDocumento.name}(wrapper: ${wrapper})`, 'start');
    const current = this.modeloTipoDocumento$.value;
    const index = current.findIndex(
      (value: StatusWrapper<IModeloTipoDocumento>) => value === wrapper
    );
    if (index >= 0) {
      if (!wrapper.created) {
        this.modeloTipoDocumentoEliminados.push(current[index]);
      }
      current.splice(index, 1);
      this.modeloTipoDocumento$.next(current);
      this.setChanges(true);
    }
    this.logger.debug(ModeloEjecucionTipoDocumentoFragment.name,
      `${this.deleteModeloTipoDocumento.name}(wrapper: ${wrapper})`, 'end');
  }

  saveOrUpdate(): Observable<void> {
    this.logger.debug(ModeloEjecucionTipoDocumentoFragment.name, `${this.saveOrUpdate.name}()`, 'start');
    return merge(
      this.deleteModeloTipoDocumentos(),
      this.createModeloTipoDocumentos()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      }),
      tap(() => this.logger.debug(ModeloEjecucionTipoDocumentoFragment.name, `${this.saveOrUpdate.name}()`, 'end'))
    );
  }

  private deleteModeloTipoDocumentos(): Observable<void> {
    this.logger.debug(ModeloEjecucionTipoDocumentoFragment.name, `${this.deleteModeloTipoDocumentos.name}()`, 'start');
    if (this.modeloTipoDocumentoEliminados.length === 0) {
      this.logger.debug(ModeloEjecucionTipoDocumentoFragment.name, `${this.deleteModeloTipoDocumentos.name}()`, 'end');
      return of(void 0);
    }
    return from(this.modeloTipoDocumentoEliminados).pipe(
      mergeMap((wrapped) => {
        return this.modeloTipoDocumentoService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.modeloTipoDocumentoEliminados = this.modeloTipoDocumentoEliminados.filter(deletedModelo =>
                deletedModelo.value.id !== wrapped.value.id);
            }),
            tap(() => this.logger.debug(ModeloEjecucionTipoDocumentoFragment.name,
              `${this.deleteModeloTipoDocumentos.name}()`, 'end'))
          );
      }));
  }

  private createModeloTipoDocumentos(): Observable<void> {
    this.logger.debug(ModeloEjecucionTipoDocumentoFragment.name, `${this.createModeloTipoDocumentos.name}()`, 'start');
    const createdModelos = this.modeloTipoDocumento$.value.filter((modeloTipoDocumento) => modeloTipoDocumento.created);
    if (createdModelos.length === 0) {
      this.logger.debug(ModeloEjecucionTipoDocumentoFragment.name, `${this.createModeloTipoDocumentos.name}()`, 'end');
      return of(void 0);
    }
    createdModelos.forEach(
      (wrapper: StatusWrapper<IModeloTipoDocumento>) => wrapper.value.modeloEjecucion = {
        id: this.getKey(),
        activo: true
      } as IModeloEjecucion
    );
    return from(createdModelos).pipe(
      mergeMap((wrappedTarea) => {
        return this.modeloTipoDocumentoService.create(wrappedTarea.value).pipe(
          map((updatedTarea) => {
            const index = this.modeloTipoDocumento$.value.findIndex((currentTarea) => currentTarea === wrappedTarea);
            this.modeloTipoDocumento$[index] = new StatusWrapper<IModeloTipoDocumento>(updatedTarea);
          }),
          tap(() => this.logger.debug(ModeloEjecucionTipoDocumentoFragment.name,
            `${this.createModeloTipoDocumentos.name}()`, 'end'))
        );
      }));
  }

  private isSaveOrUpdateComplete(): boolean {
    this.logger.debug(ModeloEjecucionTipoDocumentoFragment.name, `${this.isSaveOrUpdateComplete.name}()`, 'start');
    const touched: boolean = this.modeloTipoDocumento$.value.some((wrapper) => wrapper.touched);
    this.logger.debug(ModeloEjecucionTipoDocumentoFragment.name, `${this.isSaveOrUpdateComplete.name}()`, 'end');
    return (this.modeloTipoDocumentoEliminados.length > 0 || touched);
  }

}
