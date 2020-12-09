import { FormGroup } from '@angular/forms';
import { ISolicitud } from '@core/models/csp/solicitud';
import { FormFragment, Fragment } from '@core/services/action-service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { NGXLogger } from 'ngx-logger';
import { Observable, BehaviorSubject, merge, of, from, } from 'rxjs';
import { map, tap, mergeMap, takeLast, switchMap } from 'rxjs/operators';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { SgiAuthService } from '@sgi/framework/auth';
import { ISolicitudHito } from '@core/models/csp/solicitud-hito';
import { OnDestroy } from '@angular/core';
import { SolicitudHitoService } from '@core/services/csp/solicitud-hito.service';




export class SolicitudHitosFragment extends Fragment implements OnDestroy {


  hitos$ = new BehaviorSubject<StatusWrapper<ISolicitudHito>[]>([]);
  private hitosEliminados: StatusWrapper<ISolicitudHito>[] = [];

  solicitud: ISolicitud;

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private service: SolicitudHitoService,
    private solicitudService: SolicitudService,
    private sgiAuthService: SgiAuthService,
  ) {
    super(key);
    this.setComplete(true);
    this.logger.debug(SolicitudHitosFragment.name, 'constructor()', 'start');
    this.logger.debug(SolicitudHitosFragment.name, 'constructor()', 'end');
  }


  protected onInitialize(): void {
    this.logger.debug(SolicitudHitosFragment.name, 'onInitialize()', 'start');
    if (this.getKey()) {
      this.solicitudService.findById(this.getKey() as number).pipe(
        switchMap((solicitud) => {
          this.solicitud = solicitud as ISolicitud;
          return this.solicitudService.findHitosSolicitud(this.solicitud.id);
        })
      ).subscribe((hitos) => {
        this.hitos$.next(hitos.items.map(
          listaHitos => new StatusWrapper<ISolicitudHito>(listaHitos))
        );


        this.logger.debug(SolicitudHitosFragment.name, 'onInitialize()', 'end');
      });
    }
  }

  public addHito(hito: ISolicitudHito) {
    this.logger.debug(SolicitudHitosFragment.name,
      `addHito(addHito: ${hito})`, 'start');
    const wrapped = new StatusWrapper<ISolicitudHito>(hito);
    wrapped.setCreated();
    const current = this.hitos$.value;
    current.push(wrapped);
    this.hitos$.next(current);
    this.setChanges(true);
    this.logger.debug(SolicitudHitosFragment.name,
      `addHito(addHito: ${hito})`, 'end');
  }




  public deleteHito(wrapper: StatusWrapper<ISolicitudHito>) {
    this.logger.debug(SolicitudHitosFragment.name,
      `deleteHito(wrapper: ${wrapper})`, 'start');
    const current = this.hitos$.value;
    const index = current.findIndex(
      (value) => value === wrapper
    );
    if (index >= 0) {
      if (!wrapper.created) {
        this.hitosEliminados.push(current[index]);
      }
      current.splice(index, 1);
      this.hitos$.next(current);
      this.setChanges(true);
    }
    this.logger.debug(SolicitudHitosFragment.name,
      `deleteHito(wrapper: ${wrapper})`, 'end');
  }


  saveOrUpdate(): Observable<void> {
    this.logger.debug(SolicitudHitosFragment.name,
      `saveOrUpdate()`, 'start');

    return merge(
      this.deleteHitos(),
      this.updateHitos(),
      this.createHitos()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      }),
      tap(() => this.logger.debug(SolicitudHitosFragment.name,
        `saveOrUpdate})`, 'end'))
    );

  }

  private deleteHitos(): Observable<void> {
    this.logger.debug(SolicitudHitosFragment.name, `deleteHitos()`, 'start');
    if (this.hitosEliminados.length === 0) {
      this.logger.debug(SolicitudHitosFragment.name, `deleteHitos()`, 'end');
      return of(void 0);
    }
    return from(this.hitosEliminados).pipe(
      mergeMap((wrapped) => {
        return this.service.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.hitosEliminados = this.hitosEliminados.filter(deletedHito =>
                deletedHito.value.id !== wrapped.value.id);
            }),
            tap(() => this.logger.debug(SolicitudHitosFragment.name,
              `deleteHitos()`, 'end'))
          );
      })
    );
  }

  private updateHitos(): Observable<void> {
    this.logger.debug(SolicitudHitosFragment.name, `deleteHitos()`, 'start');
    const updateHitos = this.hitos$.value.filter((convocatoriaHito) => convocatoriaHito.edited);
    if (updateHitos.length === 0) {
      this.logger.debug(SolicitudHitosFragment.name, `updateHitos()`, 'end');
      return of(void 0);
    }
    return from(updateHitos).pipe(
      mergeMap((wrappedHitos) => {
        return this.service.update(wrappedHitos.value.id, wrappedHitos.value).pipe(
          map((updateHito) => {
            const index = this.hitos$.value.findIndex((currenthitos) => currenthitos === wrappedHitos);
            this.hitos$.value[index] = new StatusWrapper<ISolicitudHito>(updateHito);
          }),
          tap(() => this.logger.debug(SolicitudHitosFragment.name,
            `updateHitos()`, 'end'))
        );
      })
    );
  }

  private createHitos(): Observable<void> {
    this.logger.debug(SolicitudHitosFragment.name, `deleteHitos()`, 'start');
    const createdHitos = this.hitos$.value.filter((convocatoriaHito) => convocatoriaHito.created);
    if (createdHitos.length === 0) {
      this.logger.debug(SolicitudHitosFragment.name, `createHitos()`, 'end');
      return of(void 0);
    }
    createdHitos.forEach(
      (wrapper) => wrapper.value.solicitud = {
        id: this.getKey(),
        activo: true
      } as ISolicitud
    );
    return from(createdHitos).pipe(
      mergeMap((wrappedHitos) => {
        return this.service.create(wrappedHitos.value).pipe(
          map((createHito) => {
            const index = this.hitos$.value.findIndex((currenthitos) => currenthitos === wrappedHitos);
            this.hitos$.value[index] = new StatusWrapper<ISolicitudHito>(createHito);
          }),
          tap(() => this.logger.debug(SolicitudHitosFragment.name,
            `createHitos()`, 'end'))
        );
      })
    );
  }


  private isSaveOrUpdateComplete(): boolean {
    this.logger.debug(SolicitudHitosFragment.name, `isSaveOrUpdateComplete()`, 'start');
    const touched: boolean = this.hitos$.value.some((wrapper) => wrapper.touched);
    this.logger.debug(SolicitudHitosFragment.name, `isSaveOrUpdateComplete()`, 'end');
    return (this.hitosEliminados.length > 0 || touched);
  }

  ngOnDestroy(): void {
    this.logger.debug(SolicitudHitosFragment.name, `ngOnDestroy()`, 'start');
    this.logger.debug(SolicitudHitosFragment.name, `ngOnDestroy()`, 'end');
  }
}
