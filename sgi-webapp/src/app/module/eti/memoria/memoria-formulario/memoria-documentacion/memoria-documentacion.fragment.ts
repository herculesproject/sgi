import { Fragment } from '@core/services/action-service';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { Observable, BehaviorSubject, of, from, merge, fromEvent } from 'rxjs';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { IDocumentacionMemoria } from '@core/models/eti/documentacion-memoria';
import { map, mergeMap, endWith, tap, takeLast } from 'rxjs/operators';
import { TipoDocumentoService } from '@core/services/eti/tipo-documento.service';
import { NGXLogger } from 'ngx-logger';
import { SgiRestListResult } from '@sgi/framework/http/types';
import { ITipoDocumento } from '@core/models/eti/tipo-documento';

export class MemoriaDocumentacionFragment extends Fragment {

  documentacionesMemoria$:
    BehaviorSubject<StatusWrapper<IDocumentacionMemoria>[]> = new BehaviorSubject<StatusWrapper<IDocumentacionMemoria>[]>([]);
  documentacionesSeguimientoAnual$:
    BehaviorSubject<StatusWrapper<IDocumentacionMemoria>[]> = new BehaviorSubject<StatusWrapper<IDocumentacionMemoria>[]>([]);
  documentacionesSeguimientoFinal$:
    BehaviorSubject<StatusWrapper<IDocumentacionMemoria>[]> = new BehaviorSubject<StatusWrapper<IDocumentacionMemoria>[]>([]);
  documentacionesRetrospectiva$:
    BehaviorSubject<StatusWrapper<IDocumentacionMemoria>[]> = new BehaviorSubject<StatusWrapper<IDocumentacionMemoria>[]>([]);


  private deletedDocumentacionSeguimientoAnual: StatusWrapper<IDocumentacionMemoria>[] = [];
  private deletedDocumentacionSeguimientoFinal: StatusWrapper<IDocumentacionMemoria>[] = [];
  private deletedDocumentacionRetrospectiva: StatusWrapper<IDocumentacionMemoria>[] = [];

  constructor(
    private logger: NGXLogger,
    key: number,
    private service: MemoriaService,
    private readonly tipoDocumentoService: TipoDocumentoService
  ) {
    super(key);
  }


  onInitialize(): void {
    if (this.getKey()) {
      this.loadDocumentosMemoria(this.getKey() as number);
      this.loadDocumentosSeguimientoAnual(this.getKey() as number);
      this.loadDocumentosSeguimientoFinal(this.getKey() as number);
      this.loadDocumentosRetrospectiva(this.getKey() as number);
    }

  }


  loadDocumentosMemoria(idMemoria: number): void {

    if (!this.isInitialized()) {
      this.service.findDocumentacionFormulario(idMemoria).pipe(
        map((response) => {
          if (response.items) {

            return response.items.map((documentacion) => new StatusWrapper<IDocumentacionMemoria>(documentacion));
          }
          else {
            return [];
          }
        })
      ).subscribe((documentacionMemoria) => {
        this.setChanges(true);
        this.setComplete(true);

        this.documentacionesMemoria$.next(documentacionMemoria);
      });
    }

  }

  loadDocumentosSeguimientoAnual(idMemoria: number): void {
    if (!this.isInitialized()) {
      this.service.findDocumentacionSeguimientoAnual(idMemoria).pipe(
        map((response) => {
          if (response.items) {
            return response.items.map((documentacion) => new StatusWrapper<IDocumentacionMemoria>(documentacion));
          }
          else {
            return [];
          }
        })
      ).subscribe((documentacionMemoria) => {
        this.setChanges(true);
        this.setComplete(true);

        this.documentacionesSeguimientoAnual$.next(documentacionMemoria);
      });
    }
  }

  loadDocumentosSeguimientoFinal(idMemoria: number): void {
    if (!this.isInitialized()) {
      this.service.findDocumentacionSeguimientoFinal(idMemoria).pipe(
        map((response) => {
          if (response.items) {
            return response.items.map((documentacion) => new StatusWrapper<IDocumentacionMemoria>(documentacion));
          }
          else {
            return [];
          }
        })
      ).subscribe((documentacionMemoria) => {
        this.setChanges(true);
        this.setComplete(true);

        this.documentacionesSeguimientoFinal$.next(documentacionMemoria);
      });
    }
  }

  loadDocumentosRetrospectiva(idMemoria: number): void {
    if (!this.isInitialized()) {
      this.service.findDocumentacionRetrospectiva(idMemoria).pipe(
        map((response) => {
          if (response.items) {
            return response.items.map((documentacion) => new StatusWrapper<IDocumentacionMemoria>(documentacion));
          }
          else {
            return [];
          }
        })
      ).subscribe((documentacionMemoria) => {
        this.setChanges(true);
        this.setComplete(true);

        this.documentacionesRetrospectiva$.next(documentacionMemoria);
      });
    }
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.updateDocumentacionInicial(),
      this.createDocumentacionInicial(),
      this.createDocumentacionSeguimientoAnual(),
      this.deleteDocumentacionSeguimientoAnual(),
      this.createDocumentacionSeguimientoFinal(),
      this.deleteDocumentacionSeguimientoFinal(),
      this.createDocumentacionRetrospectiva(),
      this.deleteDocumentacionRetrospectiva()
    ).pipe(
      takeLast(1),
      tap(() => this.setChanges(false))
    );
  }

  /**
   * Actualiza la documentación inicial de una memoria.
   */
  private updateDocumentacionInicial(): Observable<void> {
    this.logger.debug(MemoriaDocumentacionFragment.name, 'updateDocumentacionInicial()', 'start');
    const editedDocumentacion = this.documentacionesMemoria$.value.filter((documentacion) => documentacion.edited);
    if (editedDocumentacion.length === 0) {
      this.logger.debug(MemoriaDocumentacionFragment.name, 'updateDocumentacionInicial()', 'end');
      return of(void 0);
    }


    return from(editedDocumentacion).pipe(
      mergeMap((wrappedDocumentacion) => {
        return this.service.updateDocumentacion(this.getKey() as number, wrappedDocumentacion.value, wrappedDocumentacion.value.id).pipe(
          map((updatedDocumentacion) => {
            const index =
              this.documentacionesMemoria$.value.findIndex((currentDocumentacion) => currentDocumentacion === wrappedDocumentacion);
            this.documentacionesMemoria$[index] =
              new StatusWrapper<IDocumentacionMemoria>(updatedDocumentacion);
          })
        );
      }));

  }

  /**
   * Crea la documentación inicial de una memoria.
   */
  private createDocumentacionInicial(): Observable<void> {
    const documentacionCreada = this.documentacionesMemoria$.value.filter((documentacion) => documentacion.created);
    if (documentacionCreada.length === 0) {
      return of(void 0);
    }

    return from(documentacionCreada).pipe(
      mergeMap((wrappedDocumentacion) => {

        return this.service.createDocumentacionInicial(this.getKey() as number, wrappedDocumentacion.value).pipe(
          map((savedDocumentacion) => {
            const index = this.documentacionesMemoria$.value.findIndex((currentComentario) => currentComentario === wrappedDocumentacion);
            this.documentacionesMemoria$[index] = new StatusWrapper<IDocumentacionMemoria>(savedDocumentacion);
          })
        );
      }),
      endWith()
    );
  }

  /**
   * Crea la documentación de seguimiento anual de una memoria
   */
  private createDocumentacionSeguimientoAnual(): Observable<void> {
    const documentacionCreada = this.documentacionesSeguimientoAnual$.value.filter((documentacion) => documentacion.created);
    if (documentacionCreada.length === 0) {
      return of(void 0);
    }

    return from(documentacionCreada).pipe(
      mergeMap((wrappedDocumentacion) => {

        return this.service.createDocumentacionSeguimientoAnual(this.getKey() as number, wrappedDocumentacion.value).pipe(
          map((savedDocumentacion) => {
            const index = this.documentacionesSeguimientoAnual$.value.findIndex((currentComentario) => currentComentario
              === wrappedDocumentacion);
            this.documentacionesSeguimientoAnual$[index] = new StatusWrapper<IDocumentacionMemoria>(savedDocumentacion);
          })
        );
      }),
      endWith()
    );
  }

  /**
   * Crea la documentación de seguimiento final de una memoria
   */
  private createDocumentacionSeguimientoFinal(): Observable<void> {
    const documentacionCreada = this.documentacionesSeguimientoFinal$.value.filter((documentacion) => documentacion.created);
    if (documentacionCreada.length === 0) {
      return of(void 0);
    }

    return from(documentacionCreada).pipe(
      mergeMap((wrappedDocumentacion) => {

        return this.service.createDocumentacionSeguimientoFinal(this.getKey() as number, wrappedDocumentacion.value).pipe(
          map((savedDocumentacion) => {
            const index = this.documentacionesSeguimientoFinal$.value.findIndex((currentComentario) => currentComentario
              === wrappedDocumentacion);
            this.documentacionesSeguimientoFinal$[index] = new StatusWrapper<IDocumentacionMemoria>(savedDocumentacion);
          })
        );
      }),
      endWith()
    );
  }


  /**
   * Crea la documentación de retrospectiva de una memoria
   */
  private createDocumentacionRetrospectiva(): Observable<void> {
    const documentacionCreada = this.documentacionesRetrospectiva$.value.filter((documentacion) => documentacion.created);
    if (documentacionCreada.length === 0) {
      return of(void 0);
    }

    return from(documentacionCreada).pipe(
      mergeMap((wrappedDocumentacion) => {

        return this.service.createDocumentacionRetrospectiva(this.getKey() as number, wrappedDocumentacion.value).pipe(
          map((savedDocumentacion) => {
            const index = this.documentacionesRetrospectiva$.value.findIndex((currentComentario) => currentComentario
              === wrappedDocumentacion);
            this.documentacionesRetrospectiva$[index] = new StatusWrapper<IDocumentacionMemoria>(savedDocumentacion);
          })
        );
      }),
      endWith()
    );
  }



  /**
   * Elimina la documentación de seguimiento anual de una memoria
   */
  private deleteDocumentacionSeguimientoAnual(): Observable<void> {
    this.logger.debug(MemoriaDocumentacionFragment.name, 'deleteDocumentacionSeguimientoAnual()', 'start');
    if (this.deletedDocumentacionSeguimientoAnual.length === 0) {
      this.logger.debug(MemoriaDocumentacionFragment.name, 'deleteDocumentacionSeguimientoAnual()', 'end');
      return of(void 0);
    }
    return from(this.deletedDocumentacionSeguimientoAnual).pipe(
      mergeMap((wrappedDocumentacion) => {
        return this.service
          .deleteDocumentacionSeguimientoAnual(wrappedDocumentacion.value.memoria.id, wrappedDocumentacion.value.id)
          .pipe(
            tap(_ => {
              this.deletedDocumentacionSeguimientoAnual =
                this.deletedDocumentacionSeguimientoAnual.filter(deletedDocumentacionSeguimientoAnual =>
                  deletedDocumentacionSeguimientoAnual.value.id !== wrappedDocumentacion.value.id);
            })
          );
      }));
  }


  /**
   * Elimina la documentación de seguimiento final de una memoria
   */
  private deleteDocumentacionSeguimientoFinal(): Observable<void> {
    this.logger.debug(MemoriaDocumentacionFragment.name, 'deleteDocumentacionSeguimientoFinall()', 'start');
    if (this.deletedDocumentacionSeguimientoFinal.length === 0) {
      this.logger.debug(MemoriaDocumentacionFragment.name, 'deleteDocumentacionSeguimientoFinall()', 'end');
      return of(void 0);
    }
    return from(this.deletedDocumentacionSeguimientoFinal).pipe(
      mergeMap((wrappedDocumentacion) => {
        return this.service
          .deleteDocumentacionSeguimientoFinal(wrappedDocumentacion.value.memoria.id, wrappedDocumentacion.value.id)
          .pipe(
            tap(_ => {
              this.deletedDocumentacionSeguimientoFinal =
                this.deletedDocumentacionSeguimientoFinal.filter(deletedDocumentacionSeguimientoFinal =>
                  deletedDocumentacionSeguimientoFinal.value.id !== wrappedDocumentacion.value.id);
            })
          );
      }));
  }


  /**
   * Elimina la documentación de retrospectiva de una memoria
   */
  private deleteDocumentacionRetrospectiva(): Observable<void> {
    this.logger.debug(MemoriaDocumentacionFragment.name, 'deleteDocumentacionRetrospectiva()', 'start');
    if (this.deletedDocumentacionRetrospectiva.length === 0) {
      this.logger.debug(MemoriaDocumentacionFragment.name, 'deleteDocumentacionRetrospectiva()', 'end');
      return of(void 0);
    }
    return from(this.deletedDocumentacionRetrospectiva).pipe(
      mergeMap((wrappedDocumentacion) => {
        return this.service
          .deleteDocumentacionRetrospectiva(wrappedDocumentacion.value.memoria.id, wrappedDocumentacion.value.id)
          .pipe(
            tap(_ => {
              this.deletedDocumentacionSeguimientoFinal =
                this.deletedDocumentacionSeguimientoFinal.filter(deletedDocumentacionSeguimientoFinal =>
                  deletedDocumentacionSeguimientoFinal.value.id !== wrappedDocumentacion.value.id);
            })
          );
      }));
  }

  /**
   * Añade documentación del tipo recibido por parámetro al listado de documentación correspondiente.
   * @param documentacion documentación a añadir.
   * @param tipoDocumentacion tipo de documentación a añadir.
   */
  addDocumentacion(documentacion: IDocumentacionMemoria) {
    this.logger.debug(MemoriaDocumentacionFragment.name,
      `addDocumentacion()`, 'start');
    const wrapped = new StatusWrapper<IDocumentacionMemoria>(documentacion);
    wrapped.setCreated();


    const current = this.documentacionesMemoria$.value;
    current.push(wrapped);
    this.documentacionesMemoria$.next(current);


    this.setChanges(true);
    this.setErrors(false);
    this.logger.debug(MemoriaDocumentacionFragment.name,
      `addDocumentacion()`, 'end');
  }


  /**
   * Añade documentación del tipo recibido por parámetro al listado de documentación correspondiente.
   * @param documentacion documentación a añadir.
   * @param tipoDocumentacion tipo de documentación a añadir.
   */
  addDocumentacionSeguimiento(documentacion: IDocumentacionMemoria, tipoDocumentacion: number) {
    this.logger.debug(MemoriaDocumentacionFragment.name,
      `addDocumentacionSeguimiento()`, 'start');
    const wrapped = new StatusWrapper<IDocumentacionMemoria>(documentacion);
    wrapped.setCreated();

    if (tipoDocumentacion === 1) {
      const current = this.documentacionesSeguimientoAnual$.value;
      current.push(wrapped);
      this.documentacionesSeguimientoAnual$.next(current);
    } else if (tipoDocumentacion === 2) {
      const current = this.documentacionesSeguimientoFinal$.value;
      current.push(wrapped);
      this.documentacionesSeguimientoFinal$.next(current);
    } else if (tipoDocumentacion === 3) {
      const current = this.documentacionesRetrospectiva$.value;
      current.push(wrapped);
      this.documentacionesRetrospectiva$.next(current);
    }

    this.setChanges(true);
    this.setErrors(false);
    this.logger.debug(MemoriaDocumentacionFragment.name,
      `addDocumentacionSeguimiento()`, 'end');
  }


  /**
   * Elimina documentación del tipo recibido por parámentro de la lista correspondiente.
   *
   * @param tipoDocumentacion tipo de la documentación a eliminar.
   * @param wrapperDocumentacion documentación a eliminar.
   */
  deletedDocumentacionSeguimiento(tipoDocumentacion: number, wrapperDocumentacion: StatusWrapper<IDocumentacionMemoria>): void {
    this.logger.debug(MemoriaDocumentacionFragment.name, 'deletedDocumentacionSeguimiento(tipoSeguimiento: string, wrapperDocumentacion: StatusWrapper<IDocumentacionMemoria>)', 'start');

    let current: StatusWrapper<IDocumentacionMemoria>[];
    if (tipoDocumentacion === 1) {
      current = this.documentacionesSeguimientoAnual$.value;
    } else if (tipoDocumentacion === 2) {
      current = this.documentacionesSeguimientoFinal$.value;
    }
    else if (tipoDocumentacion === 3) {
      current = this.documentacionesRetrospectiva$.value;
    }

    const index = current.findIndex((value) => value === wrapperDocumentacion);
    if (index >= 0) {
      if (!wrapperDocumentacion.created) {
        current[index].setDeleted();
        if (tipoDocumentacion === 1) {
          this.deletedDocumentacionSeguimientoAnual.push(current[index]);
        } else if (tipoDocumentacion === 2) {
          this.deletedDocumentacionSeguimientoFinal.push(current[index]);
        }
        else if (tipoDocumentacion === 3) {
          this.deletedDocumentacionRetrospectiva.push(current[index]);
        }

        current.splice(index, 1);

        if (tipoDocumentacion === 1) {
          this.documentacionesSeguimientoAnual$.next(current);
        } else if (tipoDocumentacion === 2) {
          this.documentacionesSeguimientoFinal$.next(current);
        }
        else if (tipoDocumentacion === 3) {
          this.documentacionesRetrospectiva$.next(current);
        }
        this.setChanges(true);
      }

      this.logger.debug(MemoriaDocumentacionFragment.name, 'deleteTareasEquipoTrabajo(tipoSeguimiento: string, wrapperDocumentacion: StatusWrapper<IDocumentacionMemoria>)', 'end');
    }
  }

}
