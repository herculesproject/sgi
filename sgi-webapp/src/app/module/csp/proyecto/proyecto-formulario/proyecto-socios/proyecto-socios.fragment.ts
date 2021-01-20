import { OnDestroy } from '@angular/core';
import { IProyectoSocio } from '@core/models/csp/proyecto-socio';
import { Fragment } from '@core/services/action-service';
import { ProyectoSocioService } from '@core/services/csp/proyecto-socio.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { EmpresaEconomicaService } from '@core/services/sgp/empresa-economica.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, merge, Observable, of, Subscription } from 'rxjs';
import { catchError, map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';
import { ProyectoActionService } from '../../proyecto.action.service';

export class ProyectoSociosFragment extends Fragment implements OnDestroy {
  proyectoSocios$ = new BehaviorSubject<StatusWrapper<IProyectoSocio>[]>([]);
  private proyectoSocioEliminados: StatusWrapper<IProyectoSocio>[] = [];
  private subscriptions: Subscription[] = [];

  constructor(
    private logger: NGXLogger,
    key: number,
    private empresaEconomicaService: EmpresaEconomicaService,
    private proyectoService: ProyectoService,
    private proyectoSocioService: ProyectoSocioService,
    private actionService: ProyectoActionService
  ) {
    super(key);
    this.logger.debug(ProyectoSociosFragment.name, 'constructor()', 'start');
    this.setComplete(true);
    this.logger.debug(ProyectoSociosFragment.name, 'constructor()', 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(ProyectoSociosFragment.name, `ngOnDestroy()`, 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(ProyectoSociosFragment.name, `ngOnDestroy()`, 'end');
  }

  protected onInitialize(): void {
    this.logger.debug(ProyectoSociosFragment.name, 'onInitialize()', 'start');
    const id = this.getKey() as number;
    if (id) {
      this.actionService.checkProyectoColavorativo();
      const subscription = this.proyectoService.findAllProyectoSocioProyecto(id)
        .pipe(
          switchMap((proyectoSocios) =>
            from(proyectoSocios).pipe(
              mergeMap((proyectoSocio) =>
                this.empresaEconomicaService.findById(proyectoSocio.empresa.personaRef).pipe(
                  tap(empresaEconomica => proyectoSocio.empresa = empresaEconomica),
                  catchError(() => of([]))
                )
              ),
              map(() => proyectoSocios)
            )
          ),
          map(results => results.map(proyectoSocio => new StatusWrapper<IProyectoSocio>(proyectoSocio))),
        ).subscribe(
          (result) => {
            this.proyectoSocios$.next(result);
            this.logger.debug(ProyectoSociosFragment.name, 'onInitialize()', 'end');
          }
        );
      this.subscriptions.push(subscription);
    } else {
      this.logger.debug(ProyectoSociosFragment.name, 'onInitialize()', 'end');
    }
  }

  public deleteProyectoSocio(wrapper: StatusWrapper<IProyectoSocio>) {
    this.logger.debug(ProyectoSociosFragment.name,
      `deleteProyectoSocio(wrapper: ${wrapper})`, 'start');
    const current = this.proyectoSocios$.value;
    const index = current.findIndex((value) => value === wrapper);
    if (index >= 0) {
      if (!wrapper.created) {
        this.proyectoSocioEliminados.push(current[index]);
      }
      current.splice(index, 1);
      this.proyectoSocios$.next(current);
      this.setChanges(true);
    }
    this.logger.debug(ProyectoSociosFragment.name,
      `deleteProyectoSocio(wrapper: ${wrapper})`, 'end');
  }


  saveOrUpdate(): Observable<void> {
    this.logger.debug(ProyectoSociosFragment.name, `saveOrUpdate()`, 'start');
    return merge(
      this.deleteProyectoSocios()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.proyectoSocioEliminados.length === 0) {
          this.setChanges(false);
        }
      }),
      tap(() => this.logger.debug(ProyectoSociosFragment.name, `saveOrUpdate()`, 'end'))
    );
  }

  private deleteProyectoSocios(): Observable<void> {
    this.logger.debug(ProyectoSociosFragment.name, `deleteProyectoSocios()`, 'start');
    if (this.proyectoSocioEliminados.length === 0) {
      this.logger.debug(ProyectoSociosFragment.name, `deleteProyectoSocios()`, 'end');
      return of(void 0);
    }
    return from(this.proyectoSocioEliminados).pipe(
      mergeMap((wrapped) => {
        return this.proyectoSocioService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.proyectoSocioEliminados = this.proyectoSocioEliminados.filter(deletedProyectoSocio =>
                deletedProyectoSocio.value.id !== wrapped.value.id);
            }),
            tap(() => this.logger.debug(ProyectoSociosFragment.name,
              `deleteProyectoSocios()`, 'end'))
          );
      })
    );
  }

}
