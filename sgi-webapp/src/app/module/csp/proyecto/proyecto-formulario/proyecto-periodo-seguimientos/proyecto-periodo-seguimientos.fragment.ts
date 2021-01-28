import { Fragment } from '@core/services/action-service';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { BehaviorSubject, from, merge, Observable, of, Subscription } from 'rxjs';
import { NGXLogger } from 'ngx-logger';
import { ProyectoPeriodoSeguimientoService } from '@core/services/csp/proyecto-periodo-seguimiento.service';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoPeriodoSeguimiento } from '@core/models/csp/proyecto-periodo-seguimiento';
import { OnDestroy } from '@angular/core';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { ProyectoActionService } from '../../proyecto.action.service';
import { TipoEstadoProyecto } from '@core/models/csp/estado-proyecto';

export class ProyectoPeriodoSeguimientosFragment extends Fragment implements OnDestroy {
  periodoSeguimientos$ = new BehaviorSubject<StatusWrapper<IProyectoPeriodoSeguimiento>[]>([]);
  private periodoSeguimientosEliminados: StatusWrapper<IProyectoPeriodoSeguimiento>[] = [];
  private subscriptions: Subscription[] = [];

  constructor(
    private logger: NGXLogger,
    key: number,
    private proyectoService: ProyectoService,
    private proyectoPeriodoSeguimientoService: ProyectoPeriodoSeguimientoService,
    private documentoService: DocumentoService,
    private proyecto: IProyecto
  ) {
    super(key);
    this.logger.debug(ProyectoPeriodoSeguimientosFragment.name, 'constructor()', 'start');
    this.setComplete(true);
    this.logger.debug(ProyectoPeriodoSeguimientosFragment.name, 'constructor()', 'start');
  }

  ngOnDestroy(): void {
    this.logger.debug(ProyectoPeriodoSeguimientosFragment.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(ProyectoPeriodoSeguimientosFragment.name, 'ngOnDestroy()', 'end');
  }

  protected onInitialize(): void {
    this.logger.debug(ProyectoPeriodoSeguimientosFragment.name, 'onInitialize()', 'start');
    if (this.getKey()) {
      this.proyectoService.findAllProyectoPeriodoSeguimientoProyecto(this.getKey() as number).pipe(
        map((response) => response.items)
      ).subscribe((periodoSeguimientos) => {
        this.periodoSeguimientos$.next(periodoSeguimientos.map(
          periodoSeguimiento => new StatusWrapper<IProyectoPeriodoSeguimiento>(periodoSeguimiento))
        );
        this.logger.debug(ProyectoPeriodoSeguimientosFragment.name, 'onInitialize()', 'end');
      });
    }
  }

  public addPeriodoSeguimiento(periodoSeguimiento: IProyectoPeriodoSeguimiento) {
    this.logger.debug(ProyectoPeriodoSeguimientosFragment.name,
      `addPeriodoSeguimiento(addPeriodoSeguimiento: ${periodoSeguimiento})`, 'start');
    const wrapped = new StatusWrapper<IProyectoPeriodoSeguimiento>(periodoSeguimiento);
    wrapped.setCreated();
    const current = this.periodoSeguimientos$.value;
    current.push(wrapped);
    this.periodoSeguimientos$.next(current);
    this.setChanges(true);
    this.logger.debug(ProyectoPeriodoSeguimientosFragment.name,
      `addPeriodoSeguimiento(addPeriodoSeguimiento: ${periodoSeguimiento})`, 'end');
  }

  public deletePeriodoSeguimiento(wrapper: StatusWrapper<IProyectoPeriodoSeguimiento>) {
    this.logger.debug(ProyectoPeriodoSeguimientosFragment.name,
      `deletePeriodoSeguimiento(wrapper: ${wrapper})`, 'start');
    const current = this.periodoSeguimientos$.value;
    const index = current.findIndex(
      (value) => value === wrapper
    );
    if (index >= 0) {
      if (!wrapper.created) {
        this.periodoSeguimientosEliminados.push(current[index]);
      }
      current.splice(index, 1);
      this.periodoSeguimientos$.next(current);
      this.setChanges(true);
      this.recalcularNumPeriodos();
    }
    this.logger.debug(ProyectoPeriodoSeguimientosFragment.name,
      `deletePeriodoSeguimiento(wrapper: ${wrapper})`, 'end');
  }

  saveOrUpdate(): Observable<void> {
    this.logger.debug(ProyectoPeriodoSeguimientosFragment.name, `saveOrUpdate()`, 'start');
    return merge(
      this.deletePeriodoSeguimientos(),
      this.updatePeriodoSeguimientos(),
      this.createPeriodoSeguimientos()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      }),
      tap(() => this.logger.debug(ProyectoPeriodoSeguimientosFragment.name, `saveOrUpdate()`, 'end'))
    );
  }

  private deletePeriodoSeguimientos(): Observable<void> {
    this.logger.debug(ProyectoPeriodoSeguimientosFragment.name, `deletePeriodoSeguimientos()`, 'start');
    if (this.periodoSeguimientosEliminados.length === 0) {
      this.logger.debug(ProyectoPeriodoSeguimientosFragment.name, `deletePeriodoSeguimientos()`, 'end');
      return of(void 0);
    }
    return from(this.periodoSeguimientosEliminados).pipe(
      mergeMap((wrapped) => {
        return this.proyectoPeriodoSeguimientoService.findDocumentos(wrapped.value.id).pipe(
          switchMap((documentos) => {
            return this.proyectoPeriodoSeguimientoService.deleteById(wrapped.value.id)
              .pipe(
                tap(() => {
                  this.periodoSeguimientosEliminados = this.periodoSeguimientosEliminados.filter(deletedPeriodoSeguimiento =>
                    deletedPeriodoSeguimiento.value.id !== wrapped.value.id),
                    map(() => {
                      return from(documentos.items).pipe(
                        mergeMap(documento => {
                          return this.documentoService.eliminarFichero(documento.documentoRef).pipe(
                            tap(() => this.logger.debug(ProyectoPeriodoSeguimientosFragment.name,
                              `${this.documentoService.eliminarFichero.name}()`, 'end'))
                          );
                        })
                      )
                    });
                }),
                takeLast(1),
                tap(() => this.logger.debug(ProyectoPeriodoSeguimientosFragment.name,
                  `deletePeriodoSeguimientos()`, 'end'))
              );
          })
        )
      })
    );
  }

  private createPeriodoSeguimientos(): Observable<void> {
    this.logger.debug(ProyectoPeriodoSeguimientosFragment.name, `createPeriodoSeguimientos()`, 'start');
    const createdPeriodoSeguimientos = this.periodoSeguimientos$.value.filter((proyectoPeriodoSeguimiento) => proyectoPeriodoSeguimiento.created);
    if (createdPeriodoSeguimientos.length === 0) {
      this.logger.debug(ProyectoPeriodoSeguimientosFragment.name, `createPeriodoSeguimientos()`, 'end');
      return of(void 0);
    }
    createdPeriodoSeguimientos.forEach(
      (wrapper) => wrapper.value.proyecto = {
        id: this.getKey(),
        activo: true
      } as IProyecto
    );
    return from(createdPeriodoSeguimientos).pipe(
      mergeMap((wrappedPeriodoSeguimientos) => {
        return this.proyectoPeriodoSeguimientoService.create(wrappedPeriodoSeguimientos.value).pipe(
          map((updatedPeriodoSeguimientos) => {
            const index = this.periodoSeguimientos$.value.findIndex((currentperiodoSeguimientos) => currentperiodoSeguimientos === wrappedPeriodoSeguimientos);
            this.periodoSeguimientos$.value[index] = new StatusWrapper<IProyectoPeriodoSeguimiento>(updatedPeriodoSeguimientos);
          }),
          tap(() => this.logger.debug(ProyectoPeriodoSeguimientosFragment.name,
            `createPeriodoSeguimientos()`, 'end'))
        );
      })
    );
  }

  private updatePeriodoSeguimientos(): Observable<void> {
    this.logger.debug(ProyectoPeriodoSeguimientosFragment.name, `updatePeriodoSeguimientos()`, 'start');
    const updatePeriodoSeguimientos = this.periodoSeguimientos$.value.filter((proyectoPeriodoSeguimiento) => proyectoPeriodoSeguimiento.edited);
    if (updatePeriodoSeguimientos.length === 0) {
      this.logger.debug(ProyectoPeriodoSeguimientosFragment.name, `updatePeriodoSeguimientos()`, 'end');
      return of(void 0);
    }
    return from(updatePeriodoSeguimientos).pipe(
      mergeMap((wrappedPeriodoSeguimientos) => {
        return this.proyectoPeriodoSeguimientoService.update(wrappedPeriodoSeguimientos.value.id, wrappedPeriodoSeguimientos.value).pipe(
          map((updatedPeriodoSeguimientos) => {
            const index = this.periodoSeguimientos$.value.findIndex((currentperiodoSeguimientos) => currentperiodoSeguimientos === wrappedPeriodoSeguimientos);
            this.periodoSeguimientos$.value[index] = new StatusWrapper<IProyectoPeriodoSeguimiento>(updatedPeriodoSeguimientos);
          }),
          tap(() => this.logger.debug(ProyectoPeriodoSeguimientosFragment.name,
            `updatePeriodoSeguimientos()`, 'end'))
        );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    this.logger.debug(ProyectoPeriodoSeguimientosFragment.name, `isSaveOrUpdateComplete()`, 'start');
    const touched: boolean = this.periodoSeguimientos$.value.some((wrapper) => wrapper.touched);
    this.logger.debug(ProyectoPeriodoSeguimientosFragment.name, `isSaveOrUpdateComplete()`, 'end');
    return (this.periodoSeguimientosEliminados.length > 0 || touched);
  }

  /**
  * Recalcula los numeros de los periodos de todos los periodos de seguimiento de la tabla en funcion de su fecha de inicio.
  */
  private recalcularNumPeriodos(): void {
    let numPeriodo = 1;
    this.periodoSeguimientos$.value
      .sort((a, b) => (a.value.fechaInicio > b.value.fechaInicio) ? 1 : ((b.value.fechaInicio > a.value.fechaInicio) ? -1 : 0));

    this.periodoSeguimientos$.value.forEach(c => {
      c.value.numPeriodo = numPeriodo++;
    });

    this.periodoSeguimientos$.next(this.periodoSeguimientos$.value);
  }


  /**
   * Indica si el proyecto cumple con las condiciones para que se puedan añadir/modificar los periodos
   */
  get readOnly(): boolean {
    if (!this.proyecto?.unidadGestion) {
      return true;
    }
    if (!this.proyecto?.activo) {
      return true;
    }
    if (this.proyecto?.estado?.estado === TipoEstadoProyecto.CANCELADO || this.proyecto?.estado?.estado === TipoEstadoProyecto.FINALIZADO) {
      return true;
    }
    return false;
  }

}
