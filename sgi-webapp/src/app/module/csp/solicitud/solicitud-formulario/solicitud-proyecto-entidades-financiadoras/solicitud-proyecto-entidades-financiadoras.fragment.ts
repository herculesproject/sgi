import { OnDestroy } from '@angular/core';
import { ISolicitudProyectoDatos } from '@core/models/csp/solicitud-proyecto-datos';
import { ISolicitudProyectoEntidadFinanciadoraAjena } from '@core/models/csp/solicitud-proyecto-entidad-financiadora-ajena';
import { Fragment } from '@core/services/action-service';
import { SolicitudProyectoEntidadFinanciadoraAjenaService } from '@core/services/csp/solicitud-proyecto-entidad-financiadora-ajena.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { EmpresaEconomicaService } from '@core/services/sgp/empresa-economica.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, merge, Observable, of, Subscription } from 'rxjs';
import { map, mergeAll, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export class SolicitudProyectoEntidadesFinanciadorasFragment extends Fragment implements OnDestroy {
  entidadesFinanciadoras$ = new BehaviorSubject<StatusWrapper<ISolicitudProyectoEntidadFinanciadoraAjena>[]>([]);
  private entidadesFinanciadorasEliminadas: StatusWrapper<ISolicitudProyectoEntidadFinanciadoraAjena>[] = [];
  private subscriptions: Subscription[] = [];

  existsDatosProyecto = false;
  solicitantePersonaRef: string;

  constructor(
    private logger: NGXLogger,
    key: number,
    private solicitudService: SolicitudService,
    private solicitudProyectoEntidadFinanciadoraService: SolicitudProyectoEntidadFinanciadoraAjenaService,
    private empresaEconomicaService: EmpresaEconomicaService,
    public readonly: boolean
  ) {
    super(key);
    this.logger.debug(SolicitudProyectoEntidadesFinanciadorasFragment.name, 'constructor()', 'start');
    this.setComplete(true);
    this.logger.debug(SolicitudProyectoEntidadesFinanciadorasFragment.name, 'constructor()', 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(SolicitudProyectoEntidadesFinanciadorasFragment.name, `ngOnDestroy()`, 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(SolicitudProyectoEntidadesFinanciadorasFragment.name, `ngOnDestroy()`, 'end');
  }

  protected onInitialize(): void {
    this.logger.debug(SolicitudProyectoEntidadesFinanciadorasFragment.name, 'onInitialize()', 'start');
    const id = this.getKey() as number;

    if (id) {
      const subscription = this.solicitudService.findAllSolicitudProyectoEntidadFinanciadora(id)
        .pipe(
          map(result => {
            return result.items.map((entidadFinanciadora) => {
              return new StatusWrapper<ISolicitudProyectoEntidadFinanciadoraAjena>(entidadFinanciadora)
            });
          }),
          switchMap((entidadesFinanciadoras) => {
            return from(entidadesFinanciadoras)
              .pipe(
                map((entidadesFinanciadora) => {
                  return this.empresaEconomicaService.findById(entidadesFinanciadora.value.empresa.personaRef)
                    .pipe(
                      map(empresaEconomica => {
                        entidadesFinanciadora.value.empresa = empresaEconomica;
                        return entidadesFinanciadora;
                      }),
                    );

                }),
                mergeAll(),
                map(() => {
                  return entidadesFinanciadoras;
                })
              );
          }),
          takeLast(1)
        ).subscribe(
          (result) => {
            this.entidadesFinanciadoras$.next(result);
            this.logger.debug(SolicitudProyectoEntidadesFinanciadorasFragment.name, 'onInitialize()', 'end');
          }
        );

      this.subscriptions.push(subscription);
    } else {
      this.logger.debug(SolicitudProyectoEntidadesFinanciadorasFragment.name, 'onInitialize()', 'end');
    }
  }

  saveOrUpdate(): Observable<void> {
    this.logger.debug(SolicitudProyectoEntidadesFinanciadorasFragment.name, `saveOrUpdate()`, 'start');
    return merge(
      this.deleteSolicitudProyectoEntidadFinanciadoras(),
      this.updateSolicitudProyectoEntidadFinanciadoras(),
      this.createSolicitudProyectoEntidadFinanciadoras()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      }),
      tap(() => this.logger.debug(SolicitudProyectoEntidadesFinanciadorasFragment.name, `saveOrUpdate()`, 'end'))
    );
  }

  public deleteSolicitudProyectoEntidadFinanciadora(wrapper: StatusWrapper<ISolicitudProyectoEntidadFinanciadoraAjena>) {
    this.logger.debug(SolicitudProyectoEntidadesFinanciadorasFragment.name,
      `deleteSolicitudProyectoEntidadFinanciadora(wrapper: ${wrapper})`, 'start');

    const current = this.entidadesFinanciadoras$.value;
    const index = current.findIndex(
      (value) => value === wrapper
    );
    if (index >= 0) {
      if (!wrapper.created) {
        this.entidadesFinanciadorasEliminadas.push(current[index]);
      }
      current.splice(index, 1);
      this.entidadesFinanciadoras$.next(current);
      this.setChanges(true);
    }

    this.logger.debug(SolicitudProyectoEntidadesFinanciadorasFragment.name,
      `deleteSolicitudProyectoEntidadFinanciadora(wrapper: ${wrapper})`, 'end');
  }

  public addSolicitudProyectoEntidadFinanciadora(entidadFinanciadora: ISolicitudProyectoEntidadFinanciadoraAjena) {
    this.logger.debug(SolicitudProyectoEntidadesFinanciadorasFragment.name,
      `addSolicitudProyectoEntidadFinanciadora(entidadFinanciadora: ${entidadFinanciadora})`, 'start');

    const wrapped = new StatusWrapper<ISolicitudProyectoEntidadFinanciadoraAjena>(entidadFinanciadora);
    wrapped.setCreated();
    const current = this.entidadesFinanciadoras$.value;
    current.push(wrapped);
    this.entidadesFinanciadoras$.next(current);
    this.setChanges(true);

    this.logger.debug(SolicitudProyectoEntidadesFinanciadorasFragment.name,
      `addSolicitudProyectoEntidadFinanciadora(entidadFinanciadora: ${entidadFinanciadora})`, 'end');
  }

  public updateSolicitudProyectoEntidadFinanciadora(wrapper: StatusWrapper<ISolicitudProyectoEntidadFinanciadoraAjena>) {
    this.logger.debug(SolicitudProyectoEntidadesFinanciadorasFragment.name,
      `updateSolicitudProyectoEntidadFinanciadora(wrapper: ${wrapper})`, 'start');
    const current = this.entidadesFinanciadoras$.value;
    const index = current.findIndex(value => value.value.id === wrapper.value.id);
    if (index >= 0) {
      wrapper.setEdited();
      this.entidadesFinanciadoras$.value[index] = wrapper;
      this.setChanges(true);
    }
    this.logger.debug(SolicitudProyectoEntidadesFinanciadorasFragment.name,
      `updateSolicitudProyectoEntidadFinanciadora(wrapper: ${wrapper})`, 'end');
  }

  /**
   * Elimina las SolicitudProyectoEntidadFinanciadoraAjena de la lista de entidades a eliminar
   */
  private deleteSolicitudProyectoEntidadFinanciadoras(): Observable<void> {
    this.logger.debug(SolicitudProyectoEntidadesFinanciadorasFragment.name, `deleteSolicitudProyectoEntidadFinanciadoras()`, 'start');

    if (this.entidadesFinanciadorasEliminadas.length === 0) {
      this.logger.debug(SolicitudProyectoEntidadesFinanciadorasFragment.name, `deleteSolicitudProyectoEntidadFinanciadoras()`, 'end');
      return of(void 0);
    }

    return from(this.entidadesFinanciadorasEliminadas).pipe(
      mergeMap((wrapped) => {
        return this.solicitudProyectoEntidadFinanciadoraService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.entidadesFinanciadorasEliminadas = this.entidadesFinanciadorasEliminadas.filter(deletedEntidadFinanciadora =>
                deletedEntidadFinanciadora.value.id !== wrapped.value.id);
            }),
            tap(() => this.logger.debug(SolicitudProyectoEntidadesFinanciadorasFragment.name,
              `deleteSolicitudProyectoEntidadFinanciadoras()`, 'end'))
          );
      })
    );
  }

  /**
   * Actualiza las SolicitudProyectoEntidadFinanciadoraAjena modificadas.
   */
  private updateSolicitudProyectoEntidadFinanciadoras(): Observable<void> {
    this.logger.debug(SolicitudProyectoEntidadesFinanciadorasFragment.name, `updateSolicitudProyectoEntidadFinanciadoras()`, 'start');

    const updatedEntidadesFinanciadoras = this.entidadesFinanciadoras$.value.filter((entidadFinanciadora) => entidadFinanciadora.edited);
    if (updatedEntidadesFinanciadoras.length === 0) {
      this.logger.debug(SolicitudProyectoEntidadesFinanciadorasFragment.name, `updateSolicitudProyectoEntidadFinanciadoras()`, 'end');
      return of(void 0);
    }

    return from(updatedEntidadesFinanciadoras).pipe(
      mergeMap((wrapped) => {
        const entidadFinanciadora = wrapped.value;
        return this.solicitudProyectoEntidadFinanciadoraService.update(entidadFinanciadora.id, entidadFinanciadora).pipe(
          map((updated) => {
            const index = this.entidadesFinanciadoras$.value.findIndex((current) => current === wrapped);
            this.entidadesFinanciadoras$.value[index] = new StatusWrapper<ISolicitudProyectoEntidadFinanciadoraAjena>(updated);
          }),
          tap(() => this.logger.debug(SolicitudProyectoEntidadesFinanciadorasFragment.name,
            `updateSolicitudProyectoEntidadFinanciadoras()`, 'end'))
        );
      })
    );
  }

  private createSolicitudProyectoEntidadFinanciadoras(): Observable<void> {
    this.logger.debug(SolicitudProyectoEntidadesFinanciadorasFragment.name, `createSolicitudProyectoEntidadFinanciadoras()`, 'start');
    const createdEntidadesFinanciadoras = this.entidadesFinanciadoras$.value.filter((entidadFinanciadora) => entidadFinanciadora.created);
    if (createdEntidadesFinanciadoras.length === 0) {
      this.logger.debug(SolicitudProyectoEntidadesFinanciadorasFragment.name, `createSolicitudProyectoEntidadFinanciadoras()`, 'end');
      return of(void 0);
    }
    const id = this.getKey() as number;

    return this.solicitudService.findSolicitudProyectoDatos(id).pipe(
      switchMap(solicitudProyectoDatos => {
        return from(createdEntidadesFinanciadoras).pipe(
          mergeMap((wrapped) => {
            const entidadFinanciadora = wrapped.value;
            entidadFinanciadora.solicitudProyectoDatos = { id: solicitudProyectoDatos.id } as ISolicitudProyectoDatos;
            return this.solicitudProyectoEntidadFinanciadoraService.create(entidadFinanciadora).pipe(
              map((updated) => {
                const index = this.entidadesFinanciadoras$.value.findIndex((current) => current === wrapped);
                this.entidadesFinanciadoras$.value[index] = new StatusWrapper<ISolicitudProyectoEntidadFinanciadoraAjena>(updated);
              })
            );
          }),
          takeLast(1),
          tap(() => this.logger.debug(SolicitudProyectoEntidadesFinanciadorasFragment.name,
            `createSolicitudProyectoEntidadFinanciadoras()`, 'end'))
        );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    this.logger.debug(SolicitudProyectoEntidadesFinanciadorasFragment.name, `isSaveOrUpdateComplete()`, 'start');
    const touched: boolean = this.entidadesFinanciadoras$.value.some((wrapper) => wrapper.touched);
    this.logger.debug(SolicitudProyectoEntidadesFinanciadorasFragment.name, `isSaveOrUpdateComplete()`, 'end');
    return (this.entidadesFinanciadorasEliminadas.length > 0 || touched);
  }


}
