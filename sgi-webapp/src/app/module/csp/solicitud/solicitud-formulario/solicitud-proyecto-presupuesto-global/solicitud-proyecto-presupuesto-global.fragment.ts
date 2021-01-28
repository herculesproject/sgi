import { OnDestroy } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { ISolicitudProyectoDatos } from '@core/models/csp/solicitud-proyecto-datos';
import { ISolicitudProyectoPresupuesto } from '@core/models/csp/solicitud-proyecto-presupuesto';
import { FormFragment, Fragment } from '@core/services/action-service';
import { SolicitudProyectoPresupuestoService } from '@core/services/csp/solicitud-proyecto-presupuesto.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { EmpresaEconomicaService } from '@core/services/sgp/empresa-economica.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, Subscription, from, of, Observable, merge } from 'rxjs';
import { switchMap, mergeMap, tap, catchError, takeLast, map, mergeAll } from 'rxjs/operators';


export class SolicitudProyectoPresupuestoGlobalFragment extends Fragment implements OnDestroy {
  formGroup: FormGroup;
  partidasGastos$ = new BehaviorSubject<StatusWrapper<ISolicitudProyectoPresupuesto>[]>([]);
  private partidasGastosEliminadas: StatusWrapper<ISolicitudProyectoPresupuesto>[] = [];
  private subscriptions: Subscription[] = [];

  existsDatosProyecto = false;

  constructor(
    private logger: NGXLogger,
    key: number,
    private solicitudService: SolicitudService,
    private solicitudProyectoPresupuestoService: SolicitudProyectoPresupuestoService,
    private empresaEconomicaService: EmpresaEconomicaService,
    public readonly: boolean
  ) {
    super(key);
    this.logger.debug(SolicitudProyectoPresupuestoGlobalFragment.name, 'constructor()', 'start');
    this.setComplete(true);
    this.logger.debug(SolicitudProyectoPresupuestoGlobalFragment.name, 'constructor()', 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(SolicitudProyectoPresupuestoGlobalFragment.name, `ngOnDestroy()`, 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(SolicitudProyectoPresupuestoGlobalFragment.name, `ngOnDestroy()`, 'end');
  }

  protected onInitialize(): void {
    this.logger.debug(SolicitudProyectoPresupuestoGlobalFragment.name, 'onInitialize()', 'start');

    this.formGroup = new FormGroup({
      totalPresupuesto: new FormControl({ value: '', disabled: true })
    });

    const solicitudId = this.getKey() as number;
    if (solicitudId) {
      const subscription = this.solicitudService.findAllSolicitudProyectoPresupuesto(solicitudId).pipe(
        switchMap((solicitudProyectoPresupuestos) =>
          from(solicitudProyectoPresupuestos)
            .pipe(
              map((solicitudProyectoPresupuesto) => {
                if (solicitudProyectoPresupuesto.empresa.personaRef) {
                  return this.empresaEconomicaService.findById(solicitudProyectoPresupuesto.empresa.personaRef)
                    .pipe(
                      tap(empresaEconomica => solicitudProyectoPresupuesto.empresa = empresaEconomica),
                      catchError(() => of(null))
                    )
                } else {
                  return of(solicitudProyectoPresupuesto);
                }
              }),
              mergeAll(),
              map(() => {
                return solicitudProyectoPresupuestos
                  .map(element => new StatusWrapper<ISolicitudProyectoPresupuesto>(element));
              })
            )
        ),
        takeLast(1)
      ).subscribe(
        (solicitudProyectoPresupuestos) => {
          this.partidasGastos$.next(solicitudProyectoPresupuestos);
          this.updateTotalPresupuesto();
          this.logger.debug(SolicitudProyectoPresupuestoGlobalFragment.name, 'onInitialize()', 'end');
        }
      );
      this.subscriptions.push(subscription);
    } else {
      this.logger.debug(SolicitudProyectoPresupuestoGlobalFragment.name, 'onInitialize()', 'end');
    }
  }

  saveOrUpdate(): Observable<void> {
    this.logger.debug(SolicitudProyectoPresupuestoGlobalFragment.name, `saveOrUpdate()`, 'start');
    return merge(
      this.deleteSolicitudProyectoPresupuestos(),
      this.updateSolicitudProyectoPresupuestos(),
      this.createSolicitudProyectoPresupuestos()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      }),
      tap(() => this.logger.debug(SolicitudProyectoPresupuestoGlobalFragment.name, `saveOrUpdate()`, 'end'))
    );
  }

  /**
   * Elimina la partida de gasto y la marca como eliminada si ya existia previamente.
   *
   * @param wrapper ISolicitudProyectoPresupuesto
   */
  public deletePartidaGasto(wrapper: StatusWrapper<ISolicitudProyectoPresupuesto>): void {
    this.logger.debug(SolicitudProyectoPresupuestoGlobalFragment.name, `deletePartidaGasto(wrapper: ${wrapper})`, 'start');

    const current = this.partidasGastos$.value;
    const index = current.findIndex((value) => value === wrapper);
    if (index >= 0) {
      if (!wrapper.created) {
        this.partidasGastosEliminadas.push(current[index]);
      }
      current.splice(index, 1);
      this.partidasGastos$.next(current);
      this.setChanges(true);
      this.updateTotalPresupuesto();
    }

    this.logger.debug(SolicitudProyectoPresupuestoGlobalFragment.name, `deletePartidaGasto(wrapper: ${wrapper})`, 'end');
  }

  public addPartidaGasto(partidaGasto: ISolicitudProyectoPresupuesto) {
    this.logger.debug(SolicitudProyectoPresupuestoGlobalFragment.name,
      `addPartidaGasto(partidaGasto: ${partidaGasto})`, 'start');

    const wrapped = new StatusWrapper<ISolicitudProyectoPresupuesto>(partidaGasto);
    wrapped.setCreated();
    const current = this.partidasGastos$.value;
    current.push(wrapped);
    this.partidasGastos$.next(current);
    this.setChanges(true);
    this.updateTotalPresupuesto();
    this.logger.debug(SolicitudProyectoPresupuestoGlobalFragment.name,
      `addPartidaGasto(partidaGasto: ${partidaGasto})`, 'end');
  }

  public updatePartidaGasto(wrapper: StatusWrapper<ISolicitudProyectoPresupuesto>) {
    this.logger.debug(SolicitudProyectoPresupuestoGlobalFragment.name,
      `updatePartidaGasto(wrapper: ${wrapper})`, 'start');
    const current = this.partidasGastos$.value;
    const index = current.findIndex(value => value.value.id === wrapper.value.id);
    if (index >= 0) {
      wrapper.setEdited();
      this.partidasGastos$.value[index] = wrapper;
      this.setChanges(true);
      this.updateTotalPresupuesto();
    }
    this.logger.debug(SolicitudProyectoPresupuestoGlobalFragment.name,
      `updatePartidaGasto(wrapper: ${wrapper})`, 'end');
  }

  /**
   * Elimina las partidas de gasto añadidas a partidasGastosEliminadas.
   */
  private deleteSolicitudProyectoPresupuestos(): Observable<void> {
    this.logger.debug(SolicitudProyectoPresupuestoGlobalFragment.name, `deleteSolicitudProyectoPresupuestos()`, 'start');

    if (this.partidasGastosEliminadas.length === 0) {
      this.logger.debug(SolicitudProyectoPresupuestoGlobalFragment.name, `deleteSolicitudProyectoPresupuestos()`, 'end');
      return of(void 0);
    }

    return from(this.partidasGastosEliminadas).pipe(
      mergeMap((wrapped) => {
        return this.solicitudProyectoPresupuestoService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.partidasGastosEliminadas = this.partidasGastosEliminadas
                .filter(deletedEnlace => deletedEnlace.value.id !== wrapped.value.id);
            }),
            tap(() => this.logger.debug(SolicitudProyectoPresupuestoGlobalFragment.name,
              `deleteSolicitudProyectoPresupuestos()`, 'end'))
          );
      })
    );
  }

  /**
   * Actualiza las SolicitudProyectoPresupuesto modificadas.
   */
  private updateSolicitudProyectoPresupuestos(): Observable<void> {
    this.logger.debug(SolicitudProyectoPresupuestoGlobalFragment.name, `updateSolicitudProyectoPresupuestos()`, 'start');

    const updatedSolicitudProyectoPresupuestos = this.partidasGastos$.value.filter((wrapper) => wrapper.edited);
    if (updatedSolicitudProyectoPresupuestos.length === 0) {
      this.logger.debug(SolicitudProyectoPresupuestoGlobalFragment.name, `updateSolicitudProyectoPresupuestos()`, 'end');
      return of(void 0);
    }

    return from(updatedSolicitudProyectoPresupuestos).pipe(
      mergeMap((wrapped) => {
        const solicitudProyectoPresupuesto = wrapped.value;
        return this.solicitudProyectoPresupuestoService.update(solicitudProyectoPresupuesto.id, solicitudProyectoPresupuesto).pipe(
          map((updated) => {
            const index = this.partidasGastos$.value.findIndex((current) => current === wrapped);
            this.partidasGastos$.value[index] = new StatusWrapper<ISolicitudProyectoPresupuesto>(updated);
          }),
          tap(() => this.logger.debug(SolicitudProyectoPresupuestoGlobalFragment.name,
            `updateSolicitudProyectoPresupuestos()`, 'end'))
        );
      })
    );
  }

  private createSolicitudProyectoPresupuestos(): Observable<void> {
    this.logger.debug(SolicitudProyectoPresupuestoGlobalFragment.name, `createSolicitudProyectoPresupuestos()`, 'start');
    const createdSolicitudProyectoPresupuestos = this.partidasGastos$.value.filter((solicitudProyectoPresupuesto) => solicitudProyectoPresupuesto.created);
    if (createdSolicitudProyectoPresupuestos.length === 0) {
      this.logger.debug(SolicitudProyectoPresupuestoGlobalFragment.name, `createSolicitudProyectoPresupuestos()`, 'end');
      return of(void 0);
    }
    const id = this.getKey() as number;

    return this.solicitudService.findSolicitudProyectoDatos(id).pipe(
      switchMap(solicitudProyectoDatos => {
        return from(createdSolicitudProyectoPresupuestos).pipe(
          mergeMap((wrapped) => {
            const solicitudProyectoPresupuesto = wrapped.value;
            solicitudProyectoPresupuesto.solicitudProyectoDatos = { id: solicitudProyectoDatos.id } as ISolicitudProyectoDatos;
            return this.solicitudProyectoPresupuestoService.create(solicitudProyectoPresupuesto).pipe(
              map((updated) => {
                const index = this.partidasGastos$.value.findIndex((current) => current === wrapped);
                this.partidasGastos$.value[index] = new StatusWrapper<ISolicitudProyectoPresupuesto>(updated);
              })
            );
          }),
          takeLast(1),
          tap(() => this.logger.debug(SolicitudProyectoPresupuestoGlobalFragment.name,
            `createSolicitudProyectoPresupuestos()`, 'end'))
        );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    this.logger.debug(SolicitudProyectoPresupuestoGlobalFragment.name, `isSaveOrUpdateComplete()`, 'start');
    const touched: boolean = this.partidasGastos$.value.some((wrapper) => wrapper.touched);
    this.logger.debug(SolicitudProyectoPresupuestoGlobalFragment.name, `isSaveOrUpdateComplete()`, 'end');
    return (this.partidasGastosEliminadas.length > 0 || touched);
  }

  private updateTotalPresupuesto() {
    this.logger.debug(SolicitudProyectoPresupuestoGlobalFragment.name, `updateTotalPresupuesto()`, 'start');
    const totalPresupuesto = this.partidasGastos$.value.reduce((total, partidaGasto) => total + partidaGasto.value.importeSolicitado, 0);
    this.formGroup.controls.totalPresupuesto.setValue(totalPresupuesto);
    this.logger.debug(SolicitudProyectoPresupuestoGlobalFragment.name, `updateTotalPresupuesto()`, 'end');
  }


}
