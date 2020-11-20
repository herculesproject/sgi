import { OnDestroy } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { IConfiguracionSolicitud } from '@core/models/csp/configuracion-solicitud';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IConvocatoriaFase } from '@core/models/csp/convocatoria-fase';
import { IDocumentoRequerido } from '@core/models/csp/documentos-requeridos-solicitud';
import { FormFragment } from '@core/services/action-service';
import { ConfiguracionSolicitudService } from '@core/services/csp/configuracion-solicitud.service';
import { DocumentoRequeridoService } from '@core/services/csp/documento-requerido.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, merge, Observable, of, Subscription } from 'rxjs';
import { catchError, map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';
import { ConvocatoriaActionService } from '../../convocatoria.action.service';
import { DateUtils } from '@core/utils/date-utils';
import { ConvocatoriaPlazosFasesFragment } from '../convocatoria-plazos-fases/convocatoria-plazos-fases.fragment';

export class ConvocatoriaConfiguracionSolicitudesFragment extends FormFragment<IConfiguracionSolicitud> implements OnDestroy {
  configuracionSolicitud: IConfiguracionSolicitud;
  documentosRequeridos$ = new BehaviorSubject<StatusWrapper<IDocumentoRequerido>[]>([]);
  documentosRequeridosEliminados: StatusWrapper<IDocumentoRequerido>[] = [];
  private subscriptionsFragment: Subscription[] = [];

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private configuracionSolicitudService: ConfiguracionSolicitudService,
    private documentoRequeridoService: DocumentoRequeridoService,
    private convocatoriaActionService: ConvocatoriaActionService,
    private plazosFasesFragment: ConvocatoriaPlazosFasesFragment) {
    super(key, true);
    this.logger.debug(ConvocatoriaConfiguracionSolicitudesFragment.name, 'constructor()', 'start');
    this.setComplete(true);
    this.configuracionSolicitud = {} as IConfiguracionSolicitud;
    this.logger.debug(ConvocatoriaConfiguracionSolicitudesFragment.name, 'constructor()', 'start');
  }

  ngOnDestroy(): void {
    this.logger.debug(ConvocatoriaConfiguracionSolicitudesFragment.name, 'ngOnDestroy()', 'start');
    this.subscriptionsFragment.forEach(x => x.unsubscribe());
    this.logger.debug(ConvocatoriaConfiguracionSolicitudesFragment.name, 'ngOnDestroy()', 'end');
  }

  protected buildFormGroup(): FormGroup {
    this.logger.debug(ConvocatoriaConfiguracionSolicitudesFragment.name, `buildFormGroup()`, 'start');
    const form = new FormGroup({
      tramitacionSGI: new FormControl(false),
      fasePresentacionSolicitudes: new FormControl(undefined),
      formularioSolicitud: new FormControl(undefined),
      tipoBaremacion: new FormControl(undefined),
      fechaInicioFase: new FormControl({ value: '', disabled: true }),
      fechaFinFase: new FormControl({ value: '', disabled: true }),
      importeMaximoSolicitud: new FormControl('', [Validators.maxLength(50)]),
    });
    this.logger.debug(ConvocatoriaConfiguracionSolicitudesFragment.name, `buildFormGroup()`, 'start');
    return form;
  }

  protected buildPatch(configuracionSolicitud: IConfiguracionSolicitud): { [key: string]: any; } {
    this.logger.debug(ConvocatoriaConfiguracionSolicitudesFragment.name,
      `buildPatch(configuracionSolicitud: ${configuracionSolicitud})`, 'start');
    const result = {
      tramitacionSGI: configuracionSolicitud?.tramitacionSGI,
      fasePresentacionSolicitudes: configuracionSolicitud?.fasePresentacionSolicitudes,
      formularioSolicitud: configuracionSolicitud?.formularioSolicitud,
      tipoBaremacion: configuracionSolicitud?.baremacionRef,
      fechaInicioFase: configuracionSolicitud?.fasePresentacionSolicitudes?.fechaInicio,
      fechaFinFase: configuracionSolicitud?.fasePresentacionSolicitudes?.fechaFin,
      importeMaximoSolicitud: configuracionSolicitud?.importeMaximoSolicitud,
    };
    this.logger.debug(ConvocatoriaConfiguracionSolicitudesFragment.name,
      `buildPatch(configuracionSolicitud: ${configuracionSolicitud})`, 'end');
    return result;
  }

  protected initializer(key: number): Observable<IConfiguracionSolicitud> {
    this.logger.debug(ConvocatoriaConfiguracionSolicitudesFragment.name,
      `initializer()`, 'start');
    return this.configuracionSolicitudService.findById(key).pipe(
      switchMap((configuracionSolicitud) => {
        this.configuracionSolicitud = configuracionSolicitud;
        return this.configuracionSolicitudService.findAllConvocatoriaDocumentoRequeridoSolicitud(key).pipe(
          switchMap((documentosRequeridos) => {
            const documentos = documentosRequeridos.items;
            if (documentos.length > 0) {
              this.configuracionSolicitud = documentosRequeridos.items[0].configuracionSolicitud;
              this.documentosRequeridos$.next(documentosRequeridos.items.map(
                doc => new StatusWrapper<IDocumentoRequerido>(doc))
              );
            }
            return of(this.configuracionSolicitud);
          })
        );
      }),
      tap(() => this.logger.debug(ConvocatoriaConfiguracionSolicitudesFragment.name,
        `initializer(key: ${key})`, 'end')),
      catchError(() => {
        this.logger.error(ConvocatoriaConfiguracionSolicitudesFragment.name,
          `initializer(key: ${key})`, 'error');
        return of({} as IConfiguracionSolicitud);
      })
    );
  }

  getValue(): IConfiguracionSolicitud {
    this.logger.debug(ConvocatoriaConfiguracionSolicitudesFragment.name, `getValue()`, 'start');
    const form = this.getFormGroup().value;
    if (this.configuracionSolicitud === null) {
      this.configuracionSolicitud = {} as IConfiguracionSolicitud;
    }
    if (!this.configuracionSolicitud.convocatoria) {
      this.configuracionSolicitud.convocatoria = this.convocatoriaActionService.getDatosGeneralesConvocatoria();
    }
    this.configuracionSolicitud.tramitacionSGI = form.tramitacionSGI;
    this.configuracionSolicitud.fasePresentacionSolicitudes = form.fasePresentacionSolicitudes;
    this.configuracionSolicitud.formularioSolicitud = form.formularioSolicitud;
    this.configuracionSolicitud.baremacionRef = form.tipoBaremacion;
    this.configuracionSolicitud.importeMaximoSolicitud = form.importeMaximoSolicitud;
    this.logger.debug(ConvocatoriaConfiguracionSolicitudesFragment.name, `getValue()`, 'end');
    return this.configuracionSolicitud;
  }

  public addDocumentoRequerido(docRequerido: IDocumentoRequerido): void {
    this.logger.debug(ConvocatoriaConfiguracionSolicitudesFragment.name,
      `addDocumentoRequerido(addDocumentoRequerido: ${docRequerido})`, 'start');
    const wrapped = new StatusWrapper<IDocumentoRequerido>(docRequerido);
    wrapped.setCreated();
    const current = this.documentosRequeridos$.value;
    current.push(wrapped);
    this.documentosRequeridos$.next(current);
    this.setChanges(true);
    this.logger.debug(ConvocatoriaConfiguracionSolicitudesFragment.name,
      `addDocumentoRequerido(addDocumentoRequerido: ${docRequerido})`, 'end');
  }

  public deleteDocumentoRequerido(wrapper: StatusWrapper<IDocumentoRequerido>): void {
    this.logger.debug(ConvocatoriaConfiguracionSolicitudesFragment.name,
      `deleteDocumentoRequerido(wrapper: ${wrapper})`, 'start');
    const current = this.documentosRequeridos$.value;
    const index = current.findIndex(
      (value: StatusWrapper<IDocumentoRequerido>) => value === wrapper
    );
    if (index >= 0) {
      if (!wrapper.created) {
        this.documentosRequeridosEliminados.push(current[index]);
      }
      current.splice(index, 1);
      this.documentosRequeridos$.next(current);
      this.setChanges(true);
    }
    this.logger.debug(ConvocatoriaConfiguracionSolicitudesFragment.name,
      `deleteDocumentoRequerido(wrapper: ${wrapper})`, 'end');
  }

  saveOrUpdate(): Observable<number> {
    this.logger.debug(ConvocatoriaConfiguracionSolicitudesFragment.name, `saveOrUpdate()`, 'start');
    const configuracion = this.getValue();
    const observable$ = configuracion.id ? this.update(configuracion) : this.create(configuracion);
    return observable$.pipe(
      map(value => {
        this.configuracionSolicitud = value;
        this.createDocumentoRequeridos(this.configuracionSolicitud);
        return this.configuracionSolicitud.id;
      }),
      tap(() => this.logger.debug(ConvocatoriaConfiguracionSolicitudesFragment.name, `saveOrUpdate()`, 'end'))
    );
  }

  private create(configuracion: IConfiguracionSolicitud): Observable<IConfiguracionSolicitud> {
    this.logger.debug(ConvocatoriaConfiguracionSolicitudesFragment.name,
      `create(configuracion: ${configuracion})`, 'start');
    configuracion.convocatoria = {
      id: this.getKey(),
      activo: true
    } as IConvocatoria;

    if (configuracion.fasePresentacionSolicitudes != null) {
      if (!configuracion.fasePresentacionSolicitudes.id) {
        const plazosFases = this.plazosFasesFragment.plazosFase$.value.find(plazoFase =>
          plazoFase.value.tipoFase.id === configuracion.fasePresentacionSolicitudes.tipoFase.id);
        configuracion.fasePresentacionSolicitudes.id = plazosFases.value.id;
      } else {
        configuracion.fasePresentacionSolicitudes =
          this.convocatoriaActionService.getPlazosFases().find(
            plazoFase => plazoFase.value.tipoFase.nombre === configuracion.fasePresentacionSolicitudes?.tipoFase.nombre
              && plazoFase.value.fechaInicio === configuracion.fasePresentacionSolicitudes?.fechaInicio
              && plazoFase.value.fechaFin === configuracion.fasePresentacionSolicitudes?.fechaFin
              && plazoFase.value.observaciones === configuracion.fasePresentacionSolicitudes?.observaciones)?.value;
      }
    }

    return this.configuracionSolicitudService.create(configuracion).pipe(

      tap(() => this.logger.debug(ConvocatoriaConfiguracionSolicitudesFragment.name,
        `create(configuracion: ${configuracion})`, 'end'))
    );
  }

  private update(configuracion: IConfiguracionSolicitud): Observable<IConfiguracionSolicitud> {
    this.logger.debug(ConvocatoriaConfiguracionSolicitudesFragment.name,
      `update(configuracion: ${configuracion})`, 'start');

    if (typeof configuracion.fasePresentacionSolicitudes === 'string') {
      configuracion.fasePresentacionSolicitudes = null;
    }

    if (configuracion.fasePresentacionSolicitudes !== null) {

      configuracion.fasePresentacionSolicitudes =
        this.convocatoriaActionService.getPlazosFases().find(
          plazoFase => plazoFase.value.tipoFase.nombre === configuracion.fasePresentacionSolicitudes?.tipoFase.nombre
            && plazoFase.value.fechaInicio.toString() ===
            DateUtils.formatFechaAsISODate(DateUtils.fechaToDate(configuracion.fasePresentacionSolicitudes?.fechaInicio))
            && plazoFase.value.fechaFin.toString() ===
            DateUtils.formatFechaAsISODate(DateUtils.fechaToDate(configuracion.fasePresentacionSolicitudes?.fechaFin))
            && plazoFase.value.observaciones === configuracion.fasePresentacionSolicitudes?.observaciones)?.value;
    }



    return this.configuracionSolicitudService.update(Number(this.getKey()), configuracion).pipe(
      tap(result => this.configuracionSolicitud = result),
      switchMap(result => this.saveOrUpdateDocumentoRequeridos(result)),
      tap(() => this.logger.debug(ConvocatoriaConfiguracionSolicitudesFragment.name,
        `update(configuracion: ${configuracion})`, 'end'))
    );
  }

  private saveOrUpdateDocumentoRequeridos(result: IConfiguracionSolicitud) {
    this.logger.debug(ConvocatoriaConfiguracionSolicitudesFragment.name,
      `saveOrUpdateDocumentoRequeridos()`, 'start');
    return merge(
      this.deleteDocumentoRequeridos(),
      this.updateDocumentoRequeridos(result),
      this.createDocumentoRequeridos(result)
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      }),
      switchMap(() => of(result)),
      tap(() => this.logger.debug(ConvocatoriaConfiguracionSolicitudesFragment.name,
        `saveOrUpdateDocumentoRequeridos()`, 'end'))
    );
  }

  private deleteDocumentoRequeridos(): Observable<void> {
    this.logger.debug(ConvocatoriaConfiguracionSolicitudesFragment.name,
      `deleteDocumentoRequeridos()`, 'start');
    if (this.documentosRequeridosEliminados.length === 0) {
      this.logger.debug(ConvocatoriaConfiguracionSolicitudesFragment.name,
        `deleteDocumentoRequeridos()`, 'end');
      return of(void 0);
    }
    return from(this.documentosRequeridosEliminados).pipe(
      mergeMap((wrapped) => {
        return this.documentoRequeridoService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.documentosRequeridosEliminados = this.documentosRequeridosEliminados.filter(
                deletedModelo => deletedModelo.value.id !== wrapped.value.id);
            }),
            tap(() => this.logger.debug(ConvocatoriaConfiguracionSolicitudesFragment.name,
              `deleteDocumentoRequeridos()`, 'end'))
          );
      })
    );
  }

  private updateDocumentoRequeridos(result: IConfiguracionSolicitud): Observable<void> {
    this.logger.debug(ConvocatoriaConfiguracionSolicitudesFragment.name, `updateDocumentoRequeridos()`, 'start');
    const editedDocumentos = this.documentosRequeridos$.value.filter((value) => value.value.id);
    if (editedDocumentos.length === 0) {
      this.logger.debug(ConvocatoriaConfiguracionSolicitudesFragment.name, `updateDocumentoRequeridos()`, 'end');
      return of(void 0);
    }
    editedDocumentos.forEach(documento =>
      documento.value.configuracionSolicitud = result);
    return from(editedDocumentos).pipe(
      mergeMap((wrapped) => {
        return this.documentoRequeridoService.update(wrapped.value.id, wrapped.value).pipe(
          map((updatedEntidad) => {
            const index = this.documentosRequeridos$.value.findIndex((currentEntidad) => currentEntidad === wrapped);
            this.documentosRequeridos$.value[index] = new StatusWrapper<IDocumentoRequerido>(updatedEntidad);
          }),
          tap(() => this.logger.debug(ConvocatoriaConfiguracionSolicitudesFragment.name,
            `updateDocumentoRequeridos()`, 'end')
          )
        );
      })
    );
  }

  private createDocumentoRequeridos(configuracion: IConfiguracionSolicitud): Observable<void> {
    this.logger.debug(ConvocatoriaConfiguracionSolicitudesFragment.name, `createDocumentoRequeridos()`, 'start');
    const createdDocumentos = this.documentosRequeridos$.value.filter((value) => !value.value.id);
    if (createdDocumentos.length === 0) {
      this.logger.debug(ConvocatoriaConfiguracionSolicitudesFragment.name, `createDocumentoRequeridos()`, 'end');
      return of(void 0);
    }
    createdDocumentos.forEach(documento => {
      documento.value.configuracionSolicitud = configuracion;
      documento.value.id = null;
    });
    return from(createdDocumentos).pipe(
      mergeMap((wrapped) => {
        return this.documentoRequeridoService.create(wrapped.value).pipe(
          map((createdEntidad) => {
            const index = this.documentosRequeridos$.value.findIndex((currentEntidad) => currentEntidad === wrapped);
            this.documentosRequeridos$[index] = new StatusWrapper<IDocumentoRequerido>(createdEntidad);
          }),
          tap(() => this.logger.debug(ConvocatoriaConfiguracionSolicitudesFragment.name,
            `createDocumentoRequeridos()`, 'end'))
        );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    this.logger.debug(ConvocatoriaConfiguracionSolicitudesFragment.name, `isSaveOrUpdateComplete()`, 'start');
    const touched: boolean = this.documentosRequeridos$.value.some((wrapper) => wrapper.touched);
    this.logger.debug(ConvocatoriaConfiguracionSolicitudesFragment.name, `isSaveOrUpdateComplete()`, 'end');
    return (this.documentosRequeridosEliminados.length > 0 || touched);
  }
}
