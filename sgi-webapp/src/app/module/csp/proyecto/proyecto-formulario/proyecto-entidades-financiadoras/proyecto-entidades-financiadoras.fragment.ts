import { OnDestroy } from '@angular/core';
import { IProyectoEntidadFinanciadora } from '@core/models/csp/proyecto-entidad-financiadora';
import { Fragment } from '@core/services/action-service';
import { ProyectoEntidadFinanciadoraService } from '@core/services/csp/proyecto-entidad-financiadora.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { EmpresaEconomicaService } from '@core/services/sgp/empresa-economica.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, merge, Observable, of, Subscription } from 'rxjs';
import { map, mergeMap, takeLast, tap } from 'rxjs/operators';

export class ProyectoEntidadesFinanciadorasFragment extends Fragment implements OnDestroy {
  entidadesPropias$ = new BehaviorSubject<StatusWrapper<IProyectoEntidadFinanciadora>[]>([]);
  entidadesAjenas$ = new BehaviorSubject<StatusWrapper<IProyectoEntidadFinanciadora>[]>([]);
  private entidadesEliminadas: StatusWrapper<IProyectoEntidadFinanciadora>[] = [];
  private subscriptions: Subscription[] = [];

  constructor(
    private logger: NGXLogger,
    key: number,
    private proyectoService: ProyectoService,
    private proyectoEntidadFinanciadoraService: ProyectoEntidadFinanciadoraService,
    private empresaEconomicaService: EmpresaEconomicaService,
    public readonly: boolean
  ) {
    super(key);
    this.logger.debug(ProyectoEntidadesFinanciadorasFragment.name, 'constructor()', 'start');
    this.setComplete(true);
    this.logger.debug(ProyectoEntidadesFinanciadorasFragment.name, 'constructor()', 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(ProyectoEntidadesFinanciadorasFragment.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(ProyectoEntidadesFinanciadorasFragment.name, 'ngOnDestroy()', 'end');
  }

  protected onInitialize(): void {
    this.logger.debug(ProyectoEntidadesFinanciadorasFragment.name, `${this.onInitialize.name}()`, 'start');
    if (this.getKey()) {
      const subscription =
        merge(
          this.proyectoService.findEntidadesFinanciadorasPropias(this.getKey() as number).pipe(
            map(response => {
              return response.items.map(entidad => new StatusWrapper<IProyectoEntidadFinanciadora>(entidad));
            }),
            tap((value) => {
              this.entidadesPropias$.next(value);
            }),
            mergeMap(entidades => this.fillEmpresaEconomica(entidades)),
          ),
          this.proyectoService.findEntidadesFinanciadorasAjenas(this.getKey() as number).pipe(
            map(response => {
              return response.items.map(entidad => new StatusWrapper<IProyectoEntidadFinanciadora>(entidad));
            }),
            tap((value) => {
              this.entidadesAjenas$.next(value);
            }),
            mergeMap(entidades => this.fillEmpresaEconomica(entidades)),
          ),
        ).subscribe();
      this.subscriptions.push(subscription);
    }
  }

  private fillEmpresaEconomica(entidades: StatusWrapper<IProyectoEntidadFinanciadora>[]):
    Observable<StatusWrapper<IProyectoEntidadFinanciadora>> {
    return from(entidades).pipe(
      mergeMap(entidad => {
        return this.empresaEconomicaService.findById(entidad.value.empresa.personaRef).pipe(
          map((empresa) => {
            entidad.value.empresa = empresa;
            return entidad;
          })
        );
      }),
    );
  }

  public deleteEntidadFinanciadora(wrapper: StatusWrapper<IProyectoEntidadFinanciadora>, targetPropias: boolean) {
    this.logger.debug(ProyectoEntidadesFinanciadorasFragment.name,
      `${this.deleteEntidadFinanciadora.name}(wrapper: ${wrapper})`, 'start');
    const current = targetPropias ? this.entidadesPropias$.value : this.entidadesAjenas$.value;
    const index = current.findIndex(value => value.value.id === wrapper.value.id);
    if (index >= 0) {
      if (!wrapper.created) {
        this.entidadesEliminadas.push(current[index]);
      }
      current.splice(index, 1);
      targetPropias ? this.entidadesPropias$.next(current) : this.entidadesAjenas$.next(current);
      this.setChanges(true);
    }
    this.logger.debug(ProyectoEntidadesFinanciadorasFragment.name,
      `${this.deleteEntidadFinanciadora.name}(wrapper: ${wrapper})`, 'end');
  }

  public updateEntidadFinanciadora(wrapper: StatusWrapper<IProyectoEntidadFinanciadora>, targetPropias: boolean) {
    this.logger.debug(ProyectoEntidadesFinanciadorasFragment.name,
      `${this.updateEntidadFinanciadora.name}(wrapper: ${wrapper})`, 'start');
    const current = targetPropias ? this.entidadesPropias$.value : this.entidadesAjenas$.value;
    const index = current.findIndex(value => value.value.id === wrapper.value.id);
    if (index >= 0) {
      wrapper.setEdited();
      targetPropias ?
        this.entidadesPropias$.value[index] = wrapper :
        this.entidadesAjenas$.value[index] = wrapper;
      this.setChanges(true);
    }
    this.logger.debug(ProyectoEntidadesFinanciadorasFragment.name,
      `${this.updateEntidadFinanciadora.name}(wrapper: ${wrapper})`, 'end');
  }

  public addEntidadFinanciadora(entidadFinanciadora: IProyectoEntidadFinanciadora, targetPropias: boolean) {
    this.logger.debug(ProyectoEntidadesFinanciadorasFragment.name,
      `${this.addEntidadFinanciadora.name}(entidadFinanciadora: ${entidadFinanciadora})`, 'start');
    const wrapped = new StatusWrapper<IProyectoEntidadFinanciadora>(entidadFinanciadora);
    wrapped.value.ajena = !targetPropias;
    wrapped.setCreated();
    if (targetPropias) {
      const current = this.entidadesPropias$.value;
      current.push(wrapped);
      this.entidadesPropias$.next(current);
    }
    else {
      const current = this.entidadesAjenas$.value;
      current.push(wrapped);
      this.entidadesAjenas$.next(current);
    }
    this.setChanges(true);
    this.logger.debug(ProyectoEntidadesFinanciadorasFragment.name,
      `${this.addEntidadFinanciadora.name}(entidadFinanciadora: ${entidadFinanciadora})`, 'end');
  }

  saveOrUpdate(): Observable<void> {
    this.logger.debug(ProyectoEntidadesFinanciadorasFragment.name, `${this.saveOrUpdate.name}()`, 'start');
    return merge(
      this.deleteProyectoEntidadFinanciadoras(),
      this.updateProyectoEntidadFinanciadoras(this.entidadesPropias$),
      this.updateProyectoEntidadFinanciadoras(this.entidadesAjenas$),
      this.createProyectoEntidadFinanciadoras(this.entidadesPropias$),
      this.createProyectoEntidadFinanciadoras(this.entidadesAjenas$),
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      }),
      tap(() => this.logger.debug(ProyectoEntidadesFinanciadorasFragment.name, `${this.saveOrUpdate.name}()`, 'end'))
    );
  }

  private deleteProyectoEntidadFinanciadoras(): Observable<void> {
    this.logger.debug(ProyectoEntidadesFinanciadorasFragment.name, `${this.deleteProyectoEntidadFinanciadoras.name}()`, 'start');
    const deleted = this.entidadesEliminadas.filter((value) => value.value.id);
    if (deleted.length === 0) {
      this.logger.debug(ProyectoEntidadesFinanciadorasFragment.name, `${this.deleteProyectoEntidadFinanciadoras.name}()`, 'end');
      return of(void 0);
    }
    return from(deleted).pipe(
      mergeMap((wrapped) => {
        return this.proyectoEntidadFinanciadoraService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.entidadesEliminadas = deleted.filter(entidad => entidad.value.id !== wrapped.value.id);
            }),
            tap(() => this.logger.debug(ProyectoEntidadesFinanciadorasFragment.name,
              `${this.deleteProyectoEntidadFinanciadoras.name}()`, 'end'))
          );
      })
    );
  }

  private updateProyectoEntidadFinanciadoras(target$: BehaviorSubject<StatusWrapper<IProyectoEntidadFinanciadora>[]>): Observable<void> {
    this.logger.debug(ProyectoEntidadesFinanciadorasFragment.name, `${this.updateProyectoEntidadFinanciadoras.name}()`, 'start');
    const edited = target$.value.filter((value) => value.edited);
    if (edited.length === 0) {
      this.logger.debug(ProyectoEntidadesFinanciadorasFragment.name, `${this.updateProyectoEntidadFinanciadoras.name}()`, 'end');
      return of(void 0);
    }
    return from(edited).pipe(
      mergeMap((wrapped) => {
        return this.proyectoEntidadFinanciadoraService.update(wrapped.value.id, wrapped.value).pipe(
          map((update) => {
            const index = target$.value.findIndex((current) => current === wrapped);
            target$.value[index] = new StatusWrapper<IProyectoEntidadFinanciadora>(update);
          }),
          tap(() => this.logger.debug(ProyectoEntidadesFinanciadorasFragment.name,
            `${this.updateProyectoEntidadFinanciadoras.name}()`, 'end')
          )
        );
      })
    );
  }

  private createProyectoEntidadFinanciadoras(target$: BehaviorSubject<StatusWrapper<IProyectoEntidadFinanciadora>[]>): Observable<void> {
    this.logger.debug(ProyectoEntidadesFinanciadorasFragment.name, `${this.createProyectoEntidadFinanciadoras.name}()`, 'start');
    const created = target$.value.filter((value) => value.created);
    if (created.length === 0) {
      this.logger.debug(ProyectoEntidadesFinanciadorasFragment.name, `${this.createProyectoEntidadFinanciadoras.name}()`, 'end');
      return of(void 0);
    }
    created.forEach(
      (wrapper) => wrapper.value.proyectoId = this.getKey() as number
    );
    return from(created).pipe(
      mergeMap((wrapped) => {
        return this.proyectoEntidadFinanciadoraService.create(wrapped.value).pipe(
          map((update) => {
            const index = target$.value.findIndex((current) => current === wrapped);
            target$[index] = new StatusWrapper<IProyectoEntidadFinanciadora>(update);
          }),
          tap(() => this.logger.debug(ProyectoEntidadesFinanciadorasFragment.name,
            `${this.createProyectoEntidadFinanciadoras.name}()`, 'end'))
        );
      }));
  }

  private isSaveOrUpdateComplete(): boolean {
    this.logger.debug(ProyectoEntidadesFinanciadorasFragment.name, `${this.isSaveOrUpdateComplete.name}()`, 'start');
    const touched = this.entidadesPropias$.value.some((wrapper) => wrapper.touched) || this.entidadesAjenas$.value.some((wrapper) => wrapper.touched);
    this.logger.debug(ProyectoEntidadesFinanciadorasFragment.name, `${this.isSaveOrUpdateComplete.name}()`, 'end');
    return (this.entidadesEliminadas.length > 0 || touched);
  }
}
