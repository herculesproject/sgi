import { Fragment } from '@core/services/action-service';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { BehaviorSubject, from, merge, Observable, of, Subscription } from 'rxjs';
import { NGXLogger } from 'ngx-logger';
import { ProyectoProrrogaService } from '@core/services/csp/proyecto-prorroga.service';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoProrroga } from '@core/models/csp/proyecto-prorroga';
import { OnDestroy } from '@angular/core';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { ProyectoActionService } from '../../proyecto.action.service';

export class ProyectoProrrogasFragment extends Fragment implements OnDestroy {
  prorrogas$ = new BehaviorSubject<StatusWrapper<IProyectoProrroga>[]>([]);
  private prorrogasEliminados: StatusWrapper<IProyectoProrroga>[] = [];
  private subscriptions: Subscription[] = [];

  constructor(
    private logger: NGXLogger,
    key: number,
    private proyectoService: ProyectoService,
    private proyectoProrrogaService: ProyectoProrrogaService,
    private documentoService: DocumentoService,
    public actionService: ProyectoActionService
  ) {
    super(key);
    this.logger.debug(ProyectoProrrogasFragment.name, 'constructor()', 'start');
    this.setComplete(true);
    this.logger.debug(ProyectoProrrogasFragment.name, 'constructor()', 'start');
  }

  ngOnDestroy(): void {
    this.logger.debug(ProyectoProrrogasFragment.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(ProyectoProrrogasFragment.name, 'ngOnDestroy()', 'end');
  }

  protected onInitialize(): void {
    this.logger.debug(ProyectoProrrogasFragment.name, 'onInitialize()', 'start');
    if (this.getKey()) {
      this.proyectoService.findAllProyectoProrrogaProyecto(this.getKey() as number).pipe(
        map((response) => response.items)
      ).subscribe((prorrogas) => {
        this.prorrogas$.next(prorrogas.map(
          periodoSeguimiento => new StatusWrapper<IProyectoProrroga>(periodoSeguimiento))
        );
        this.logger.debug(ProyectoProrrogasFragment.name, 'onInitialize()', 'end');
      });
    }
  }

  public addProrroga(periodoSeguimiento: IProyectoProrroga) {
    this.logger.debug(ProyectoProrrogasFragment.name,
      `addProrroga(addProrroga: ${periodoSeguimiento})`, 'start');
    const wrapped = new StatusWrapper<IProyectoProrroga>(periodoSeguimiento);
    wrapped.setCreated();
    const current = this.prorrogas$.value;
    current.push(wrapped);
    this.prorrogas$.next(current);
    this.setChanges(true);
    this.logger.debug(ProyectoProrrogasFragment.name,
      `addProrroga(addProrroga: ${periodoSeguimiento})`, 'end');
  }

  public deleteProrroga(wrapper: StatusWrapper<IProyectoProrroga>) {
    this.logger.debug(ProyectoProrrogasFragment.name,
      `deleteProrroga(wrapper: ${wrapper})`, 'start');
    const current = this.prorrogas$.value;
    const index = current.findIndex(
      (value) => value === wrapper
    );
    if (index >= 0) {
      if (!wrapper.created) {
        this.prorrogasEliminados.push(current[index]);
      }
      current.splice(index, 1);
      this.prorrogas$.next(current);
      this.setChanges(true);
    }
    this.logger.debug(ProyectoProrrogasFragment.name,
      `deleteProrroga(wrapper: ${wrapper})`, 'end');
  }

  saveOrUpdate(): Observable<void> {
    this.logger.debug(ProyectoProrrogasFragment.name, `saveOrUpdate()`, 'start');
    return merge(
      this.deleteProrrogas(),
      this.updateProrrogas(),
      this.createProrrogas()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      }),
      tap(() => this.logger.debug(ProyectoProrrogasFragment.name, `saveOrUpdate()`, 'end'))
    );
  }

  private deleteProrrogas(): Observable<void> {
    this.logger.debug(ProyectoProrrogasFragment.name, `deleteProrrogas()`, 'start');
    if (this.prorrogasEliminados.length === 0) {
      this.logger.debug(ProyectoProrrogasFragment.name, `deleteProrrogas()`, 'end');
      return of(void 0);
    }
    return from(this.prorrogasEliminados).pipe(
      mergeMap((wrapped) => {
        return this.proyectoProrrogaService.findDocumentos(wrapped.value.id).pipe(
          switchMap((documentos) => {
            return this.proyectoProrrogaService.deleteById(wrapped.value.id)
              .pipe(
                tap(() => {
                  this.prorrogasEliminados = this.prorrogasEliminados.filter(deletedProrroga =>
                    deletedProrroga.value.id !== wrapped.value.id),
                    map(() => {
                      return from(documentos.items).pipe(
                        mergeMap(documento => {
                          return this.documentoService.eliminarFichero(documento.documentoRef).pipe(
                            tap(() => this.logger.debug(ProyectoProrrogasFragment.name,
                              `${this.documentoService.eliminarFichero.name}()`, 'end'))
                          );
                        })
                      )
                    });
                }),
                takeLast(1),
                tap(() => this.logger.debug(ProyectoProrrogasFragment.name,
                  `deleteProrrogas()`, 'end'))
              );
          })
        )
      })
    );
  }

  private createProrrogas(): Observable<void> {
    this.logger.debug(ProyectoProrrogasFragment.name, `createProrrogas()`, 'start');
    const createdProrrogas = this.prorrogas$.value.filter((proyectoProrroga) => proyectoProrroga.created);
    if (createdProrrogas.length === 0) {
      this.logger.debug(ProyectoProrrogasFragment.name, `createProrrogas()`, 'end');
      return of(void 0);
    }
    createdProrrogas.forEach(
      (wrapper) => wrapper.value.proyecto = {
        id: this.getKey(),
        activo: true
      } as IProyecto
    );
    return from(createdProrrogas).pipe(
      mergeMap((wrappedProrrogas) => {
        return this.proyectoProrrogaService.create(wrappedProrrogas.value).pipe(
          map((updatedProrrogas) => {
            const index = this.prorrogas$.value.findIndex((currentprorrogas) => currentprorrogas === wrappedProrrogas);
            this.prorrogas$.value[index] = new StatusWrapper<IProyectoProrroga>(updatedProrrogas);
          }),
          tap(() => this.logger.debug(ProyectoProrrogasFragment.name,
            `createProrrogas()`, 'end'))
        );
      })
    );
  }

  private updateProrrogas(): Observable<void> {
    this.logger.debug(ProyectoProrrogasFragment.name, `updateProrrogas()`, 'start');
    const updateProrrogas = this.prorrogas$.value.filter((proyectoProrroga) => proyectoProrroga.edited);
    if (updateProrrogas.length === 0) {
      this.logger.debug(ProyectoProrrogasFragment.name, `updateProrrogas()`, 'end');
      return of(void 0);
    }
    return from(updateProrrogas).pipe(
      mergeMap((wrappedProrrogas) => {
        return this.proyectoProrrogaService.update(wrappedProrrogas.value.id, wrappedProrrogas.value).pipe(
          map((updatedProrrogas) => {
            const index = this.prorrogas$.value.findIndex((currentprorrogas) => currentprorrogas === wrappedProrrogas);
            this.prorrogas$.value[index] = new StatusWrapper<IProyectoProrroga>(updatedProrrogas);
          }),
          tap(() => this.logger.debug(ProyectoProrrogasFragment.name,
            `updateProrrogas()`, 'end'))
        );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    this.logger.debug(ProyectoProrrogasFragment.name, `isSaveOrUpdateComplete()`, 'start');
    const touched: boolean = this.prorrogas$.value.some((wrapper) => wrapper.touched);
    this.logger.debug(ProyectoProrrogasFragment.name, `isSaveOrUpdateComplete()`, 'end');
    return (this.prorrogasEliminados.length > 0 || touched);
  }

}
