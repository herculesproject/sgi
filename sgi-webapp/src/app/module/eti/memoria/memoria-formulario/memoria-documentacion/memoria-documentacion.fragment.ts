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


  private deletedDocumentacion: StatusWrapper<IDocumentacionMemoria>[] = [];
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
    } else {
      this.loadDocumentacionInicial();
    }

  }


  loadDocumentacionInicial() {

    this.tipoDocumentoService.findTiposDocumentoIniciales().pipe(
      map((res: SgiRestListResult<ITipoDocumento>) => {
        return res.items.map((tipoDocumentoRecuperado) =>
          new StatusWrapper<IDocumentacionMemoria>(
            {
              id: null, documentoRef: 'DocRef001', memoria: null, aportado: false,
              tipoDocumento: tipoDocumentoRecuperado
            } as IDocumentacionMemoria));

      })
    ).subscribe((documentacionMemoria) => {
      this.setChanges(true);
      this.setComplete(true);

      documentacionMemoria.map((wrappedDocumentacion) => wrappedDocumentacion.created);
      this.documentacionesMemoria$.next(documentacionMemoria);
    });
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
      this.createDocumentacionSeguimientoAnual()
    ).pipe(
      takeLast(1),
      tap(() => this.setChanges(false))
    );
  }


  updateDocumentacionInicial(): Observable<void> {
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


  deleteDocumentacionInicial(wrappedDocumentacion: StatusWrapper<IDocumentacionMemoria>) {
    this.logger.debug(MemoriaDocumentacionFragment.name, 'deleteDocumentacion(wrappedDocumentacion: StatusWrapper<IDocumentacionMemoria>)', 'start');
    const current = this.documentacionesMemoria$.value;
    const index = current.findIndex((value) => value === wrappedDocumentacion);
    if (index >= 0) {
      if (!wrappedDocumentacion.created) {
        current[index].setDeleted();
        this.deletedDocumentacion.push(current[index]);
      }

      this.setChanges(true);
    }

    this.logger.debug(MemoriaDocumentacionFragment.name, 'deleteDocumentacion(wrappedDocumentacion: StatusWrapper<IDocumentacionMemoria>)', 'end');
  }


  addDocumentacionSeguimiento(documentacion: IDocumentacionMemoria, tipoSeguimiento: string) {
    this.logger.debug(MemoriaDocumentacionFragment.name,
      `addDocumentacionSeguimiento()`, 'start');
    const wrapped = new StatusWrapper<IDocumentacionMemoria>(documentacion);
    wrapped.setCreated();

    if (tipoSeguimiento === 'anual') {
      const current = this.documentacionesSeguimientoAnual$.value;
      current.push(wrapped);
      this.documentacionesSeguimientoAnual$.next(current);
    } else if (tipoSeguimiento === 'final') {
      const current = this.documentacionesSeguimientoFinal$.value;
      current.push(wrapped);
      this.documentacionesSeguimientoFinal$.next(current);
    } else if (tipoSeguimiento === 'retrospectiva') {
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
   * Elimina documentación seguimiento final/anual/retrospectiva.
   *
   * @param wrapperDocumentacion un equipoTrabajo.
   */
  deletedDocumentacionSeguimiento(tipoSeguimiento: string, wrapperDocumentacion: StatusWrapper<IDocumentacionMemoria>): void {
    this.logger.debug(MemoriaDocumentacionFragment.name, 'deletedDocumentacionSeguimiento(tipoSeguimiento: string, wrapperDocumentacion: StatusWrapper<IDocumentacionMemoria>)', 'start');

    let current: StatusWrapper<IDocumentacionMemoria>[];
    if (tipoSeguimiento === 'anual') {
      current = this.documentacionesSeguimientoAnual$.value;
    } else if (tipoSeguimiento === 'final') {
      current = this.documentacionesSeguimientoFinal$.value;
    }
    else if (tipoSeguimiento === 'retrospectiva') {
      current = this.documentacionesRetrospectiva$.value;
    }

    const index = current.findIndex((value) => value === wrapperDocumentacion);
    if (index >= 0) {
      if (!wrapperDocumentacion.created) {
        current[index].setDeleted();
        if (tipoSeguimiento === 'anual') {
          this.deletedDocumentacionSeguimientoAnual.push(current[index]);
        } else if (tipoSeguimiento === 'final') {
          this.deletedDocumentacionSeguimientoFinal.push(current[index]);
        }
        else if (tipoSeguimiento === 'retrospectiva') {
          this.deletedDocumentacionRetrospectiva.push(current[index]);
        }

        current.splice(index, 1);

        if (tipoSeguimiento === 'anual') {
          this.documentacionesSeguimientoAnual$.next(current);
        } else if (tipoSeguimiento === 'final') {
          this.documentacionesSeguimientoFinal$.next(current);
        }
        else if (tipoSeguimiento === 'retrospectiva') {
          this.documentacionesRetrospectiva$.next(current);
        }
        this.setChanges(true);
      }

      this.logger.debug(MemoriaDocumentacionFragment.name, 'deleteTareasEquipoTrabajo(tipoSeguimiento: string, wrapperDocumentacion: StatusWrapper<IDocumentacionMemoria>)', 'end');
    }
  }

}
