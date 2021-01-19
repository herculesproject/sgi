import { OnDestroy } from '@angular/core';
import { ISolicitudProyectoSocio } from '@core/models/csp/solicitud-proyecto-socio';
import { Fragment } from '@core/services/action-service';
import { SolicitudProyectoSocioService } from '@core/services/csp/solicitud-proyecto-socio.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, Observable, of, Subscription, from } from 'rxjs';
import { map, tap, switchMap, mergeMap, catchError } from 'rxjs/operators';
import { EmpresaEconomicaService } from '@core/services/sgp/empresa-economica.service';

export class SolicitudSociosColaboradoresFragment extends Fragment implements OnDestroy {
  proyectoSocios$ = new BehaviorSubject<StatusWrapper<ISolicitudProyectoSocio>[]>([]);
  private sociosEliminados: StatusWrapper<ISolicitudProyectoSocio>[] = [];
  private subscriptions: Subscription[] = [];

  isSociosColaboradores = false;
  enableAddSocioColaborador = false;

  constructor(
    private logger: NGXLogger,
    key: number,
    private solicitudService: SolicitudService,
    private solicitudProyectoSocioService: SolicitudProyectoSocioService,
    private empresaEconomicaService: EmpresaEconomicaService
  ) {
    super(key);
    this.logger.debug(SolicitudSociosColaboradoresFragment.name, 'constructor()', 'start');
    this.setComplete(true);
    this.logger.debug(SolicitudSociosColaboradoresFragment.name, 'constructor()', 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(SolicitudSociosColaboradoresFragment.name, `ngOnDestroy()`, 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(SolicitudSociosColaboradoresFragment.name, `ngOnDestroy()`, 'end');
  }

  protected onInitialize(): void {
    this.logger.debug(SolicitudSociosColaboradoresFragment.name, 'onInitialize()', 'start');
    const id = this.getKey() as number;
    if (id) {
      const subscription = this.solicitudService.findAllSolicitudProyectoSocio(id).pipe(
        switchMap((proyectoSocios) =>
          from(proyectoSocios).pipe(
            mergeMap((proyectoSocio) =>
              this.empresaEconomicaService.findById(proyectoSocio.empresa.personaRef).pipe(
                tap(empresaEconomica => proyectoSocio.empresa = empresaEconomica),
                catchError(() => of())
              )
            ),
            map(() => proyectoSocios)
          )
        ),
        map(results => results.map(
          proyectoSocio => new StatusWrapper<ISolicitudProyectoSocio>(proyectoSocio))),
      ).subscribe(
        (result) => {
          this.proyectoSocios$.next(result);
          this.logger.debug(SolicitudSociosColaboradoresFragment.name, 'onInitialize()', 'end');
        }
      );
      this.subscriptions.push(subscription);
    } else {
      this.logger.debug(SolicitudSociosColaboradoresFragment.name, 'onInitialize()', 'end');
    }
  }

  public deleteProyectoSocio(wrapper: StatusWrapper<ISolicitudProyectoSocio>) {
    this.logger.debug(SolicitudSociosColaboradoresFragment.name,
      `deleteProyectoSocio(wrapper: ${wrapper})`, 'start');
    const current = this.proyectoSocios$.value;
    const index = current.findIndex((value) => value === wrapper);
    if (index >= 0) {
      if (!wrapper.created) {
        this.sociosEliminados.push(current[index]);
      }
      current.splice(index, 1);
      this.proyectoSocios$.next(current);
      this.setChanges(true);
    }
    this.logger.debug(SolicitudSociosColaboradoresFragment.name,
      `deleteProyectoSocio(wrapper: ${wrapper})`, 'end');
  }

  saveOrUpdate(): Observable<void> {
    this.logger.debug(SolicitudSociosColaboradoresFragment.name, `saveOrUpdate()`, 'start');
    return this.deleteProyectoSocios().pipe(
      tap(() => this.logger.debug(SolicitudSociosColaboradoresFragment.name, `saveOrUpdate()`, 'end'))
    );
  }

  private deleteProyectoSocios(): Observable<void> {
    this.logger.debug(SolicitudSociosColaboradoresFragment.name, `deleteProyectoSocios()`, 'start');
    if (this.sociosEliminados.length === 0) {
      this.logger.debug(SolicitudSociosColaboradoresFragment.name, `deleteProyectoSocios()`, 'end');
      return of(void 0);
    }
    return from(this.sociosEliminados).pipe(
      mergeMap((wrapped) => {
        return this.solicitudProyectoSocioService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.sociosEliminados = this.sociosEliminados.filter(deletedEnlace =>
                deletedEnlace.value.id !== wrapped.value.id);
            }),
            tap(() => this.logger.debug(SolicitudSociosColaboradoresFragment.name,
              `deleteProyectoSocios()`, 'end'))
          );
      })
    );
  }
}
