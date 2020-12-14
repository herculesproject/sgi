import { Component, OnInit } from '@angular/core';
import { ISolicitud } from '@core/models/csp/solicitud';
import { ROUTE_NAMES } from '@core/route.names';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { Observable, of } from 'rxjs';
import { FormGroup, FormControl } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiRestListResult, SgiRestFilterType, SgiRestFilter } from '@sgi/framework/http';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { NGXLogger } from 'ngx-logger';
import { SnackBarService } from '@core/services/snack-bar.service';
import { switchMap, map, startWith } from 'rxjs/operators';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';
import { TipoEstadoSolicitud } from '@core/models/csp/estado-solicitud';
import { DialogService } from '@core/services/dialog.service';
import { IFuenteFinanciacion } from '@core/models/csp/fuente-financiacion';
import { IPrograma } from '@core/models/csp/programa';
import { IEmpresaEconomica } from '@core/models/sgp/empresa-economica';
import { FuenteFinanciacionService } from '@core/services/csp/fuente-financiacion.service';
import { ProgramaService } from '@core/services/csp/programa.service';
import { DateUtils } from '@core/utils/date-utils';

const MSG_BUTTON_NEW = marker('footer.csp.solicitud.crear');
const MSG_ERROR = marker('csp.solicitud.listado.error');
const MSG_DEACTIVATE = marker('csp.solicitud.listado.desactivar');
const MSG_SUCCESS_DEACTIVATE = marker('csp.solicitud.listado.desactivar.correcto');
const MSG_ERROR_DEACTIVATE = marker('csp.solicitud.listado.desactivar.error');
const MSG_REACTIVATE = marker('csp.solicitud.listado.reactivar');
const MSG_SUCCESS_REACTIVATE = marker('csp.solicitud.listado.reactivar.correcto');
const MSG_ERROR_REACTIVATE = marker('csp.solicitud.listado.reactivar.error');
const MSG_ERROR_FUENTE_FINANCIACION_INIT = marker('csp.solicitud.listado.fuente.financiacion.error');
const MSG_ERROR_PLAN_INVESTIGACION_INIT = marker('csp.solicitud.listado.plan.investigacion.error');

const LABEL_EMPRESA_CONVOCANTE = marker('csp.solicitud.entidad.convocante');
const LABEL_FINANCIADORA = marker('csp.solicitud.entidad.financiadora');

@Component({
  selector: 'sgi-solicitud-listado',
  templateUrl: './solicitud-listado.component.html',
  styleUrls: ['./solicitud-listado.component.scss']
})
export class SolicitudListadoComponent extends AbstractTablePaginationComponent<ISolicitud> implements OnInit {
  ROUTE_NAMES = ROUTE_NAMES;
  TipoEstadoSolicitud = TipoEstadoSolicitud;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  solicitudes$: Observable<ISolicitud[]>;
  textoCrear = MSG_BUTTON_NEW;

  tiposEstadoSolicitud: TipoEstadoSolicitud[];
  busquedaAvanzada = false;
  private fuenteFinanciacionFiltered: IFuenteFinanciacion[] = [];
  fuenteFinanciacion$: Observable<IFuenteFinanciacion[]>;
  private planInvestigacionFiltered: IPrograma[] = [];
  planInvestigaciones$: Observable<IPrograma[]>;
  LABEL_EMPRESA_CONVOCANTE = LABEL_EMPRESA_CONVOCANTE;
  LABEL_FINANCIADORA = LABEL_FINANCIADORA;
  empresaConvocanteText = '';
  empresaFinanciadoraText = '';

  constructor(
    protected logger: NGXLogger,
    private dialogService: DialogService,
    protected snackBarService: SnackBarService,
    private solicitudService: SolicitudService,
    private personaFisicaService: PersonaFisicaService,
    private fuenteFinanciacionService: FuenteFinanciacionService,
    private programaService: ProgramaService
  ) {
    super(logger, snackBarService, MSG_ERROR);
    this.logger.debug(SolicitudListadoComponent.name, 'constructor()', 'start');
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(17%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
    this.logger.debug(SolicitudListadoComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(SolicitudListadoComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();

    this.formGroup = new FormGroup({
      referenciaConvocatoria: new FormControl(''),
      estadoSolicitud: new FormControl(''),

      plazoAbierto: new FormControl(false),
      fechaInicioDesde: new FormControl(undefined),
      fechaInicioHasta: new FormControl(undefined),
      fechaFinDesde: new FormControl(undefined),
      fechaFinHasta: new FormControl(undefined),
      solicitante: new FormControl(undefined),
      activo: new FormControl(undefined),
      añoConvocatoria: new FormControl(undefined),
      tituloConvocatoria: new FormControl(undefined),
      entidadConvocante: new FormControl(undefined),
      planInvestigacion: new FormControl(undefined),
      entidadFinanciadora: new FormControl(undefined),
      fuenteFinanciacion: new FormControl(undefined)
    });

    this.loadTiposEstadoSolicitud();
    this.getFuentesFinanciacion();
    this.getPlanesInvestigacion();
    this.logger.debug(SolicitudListadoComponent.name, 'ngOnInit()', 'end');
  }

  protected createObservable(): Observable<SgiRestListResult<ISolicitud>> {
    this.logger.debug(SolicitudListadoComponent.name, `createObservable()`, 'start');
    const observable$ = this.solicitudService.findAllTodos(this.getFindOptions()).pipe(
      switchMap((response) => {
        if (response.total === 0) {
          return of(response);
        }

        const solicitudes = response.items;

        const personaRefsSolicitantes = solicitudes.map((solicitud) => solicitud.solicitante.personaRef);
        const solicitudesWithDatosSolicitante$ = this.personaFisicaService.findByPersonasRefs([...personaRefsSolicitantes]).pipe(
          map((result) => {
            const personas = result.items;

            solicitudes.forEach((solicitud) => {
              solicitud.solicitante = personas.find((persona) =>
                solicitud.solicitante.personaRef === persona.personaRef);
            });

            return response;
          })
        );

        return solicitudesWithDatosSolicitante$;
      })
    );

    this.logger.debug(SolicitudListadoComponent.name, `createObservable()`, 'end');
    return observable$;
  }

  protected initColumns(): void {
    this.logger.debug(SolicitudListadoComponent.name, `initColumns()`, 'start');
    this.columnas = [
      'referencia',
      'convocatoria.titulo',
      'codigoRegistroInterno',
      'solicitante',
      'estado.estado',
      'estado.fechaEstado',
      'activo',
      'acciones'
    ];
    this.logger.debug(SolicitudListadoComponent.name, `initColumns()`, 'end');
  }

  protected loadTable(reset?: boolean): void {
    this.logger.debug(SolicitudListadoComponent.name, `loadTable(${reset})`, 'start');
    this.solicitudes$ = this.getObservableLoadTable(reset);
    this.logger.debug(SolicitudListadoComponent.name, `loadTable(${reset})`, 'end');
  }

  protected createFilters(): SgiRestFilter[] {
    this.logger.debug(SolicitudListadoComponent.name, `createFilters()`, 'start');
    let filtros: SgiRestFilter[] = [];
    this.addFiltro(filtros, 'referenciaConvocatoria', SgiRestFilterType.LIKE, this.formGroup.controls.referenciaConvocatoria.value);
    this.addFiltro(filtros, 'estado.estado', SgiRestFilterType.EQUALS, Object.keys(TipoEstadoSolicitud)
      .filter(key => TipoEstadoSolicitud[key] === this.formGroup.controls.estadoSolicitud.value)[0]);
    if (this.busquedaAvanzada) {
      filtros = this.createFiltersAvanzados(filtros);
    }
    this.logger.debug(SolicitudListadoComponent.name, `createFilters()`, 'end');
    return filtros;
  }

  private createFiltersAvanzados(filtros: SgiRestFilter[]): SgiRestFilter[] {
    const controls = this.formGroup.controls;
    if (controls.plazoAbierto.value) {
      this.addFiltro(filtros, 'convocatoria.configuracionSolicitud.fasePresentacionSolicitudes.fechaInicio.desde',
        SgiRestFilterType.GREATHER_OR_EQUAL, DateUtils.formatFechaAsISODate(controls.fechaInicioDesde.value));
      this.addFiltro(filtros, 'convocatoria.configuracionSolicitud.fasePresentacionSolicitudes.fechaInicio.hasta',
        SgiRestFilterType.LOWER_OR_EQUAL, DateUtils.formatFechaAsISODate(controls.fechaInicioHasta.value));
      this.addFiltro(filtros, 'convocatoria.configuracionSolicitud.fasePresentacionSolicitudes.fechaFin.desde',
        SgiRestFilterType.GREATHER_OR_EQUAL, DateUtils.formatFechaAsISODate(controls.fechaFinDesde.value));
      this.addFiltro(filtros, 'convocatoria.configuracionSolicitud.fasePresentacionSolicitudes.fechaFin.hasta',
        SgiRestFilterType.LOWER_OR_EQUAL, DateUtils.formatFechaAsISODate(controls.fechaFinHasta.value));
    }
    this.addFiltro(filtros, 'solicitanteRef', SgiRestFilterType.LIKE, controls.solicitante.value?.personaRef);
    this.addFiltro(filtros, 'activo', SgiRestFilterType.EQUALS, controls.activo.value);
    this.addFiltro(filtros, 'convocatoria.anio', SgiRestFilterType.EQUALS, controls.añoConvocatoria.value);
    this.addFiltro(filtros, 'convocatoria.titulo', SgiRestFilterType.LIKE, controls.tituloConvocatoria.value);
    this.addFiltro(filtros, 'convocatoria.entidadConvocante.entidadRef', SgiRestFilterType.EQUALS,
      controls.entidadConvocante.value?.personaRef);
    this.addFiltro(filtros, 'convocatoria.entidadConvocante.programa.id', SgiRestFilterType.EQUALS,
      controls.planInvestigacion.value?.id);
    this.addFiltro(filtros, 'convocatoria.entidadFinanciadora.entidadRef', SgiRestFilterType.EQUALS,
      controls.entidadFinanciadora.value?.personaRef);
    this.addFiltro(filtros, 'convocatoria.entidadFinanciadora.fuenteFinanciacion.id', SgiRestFilterType.EQUALS,
      controls.fuenteFinanciacion.value?.id);
    return filtros;
  }

  loadTiposEstadoSolicitud(): void {
    this.logger.debug(SolicitudListadoComponent.name, 'loadTiposEstadoSolicitud()', 'start');
    this.tiposEstadoSolicitud = Object.keys(TipoEstadoSolicitud).map(key => TipoEstadoSolicitud[key]);
    this.logger.debug(SolicitudListadoComponent.name, 'loadTiposEstadoSolicitud()', 'end');
  }

  /**
   * Activar solicitud
   * @param solicitud una solicitud
   */
  activateSolicitud(solicitud: ISolicitud): void {
    this.logger.debug(SolicitudListadoComponent.name, `activateSolicitud(solicitud: ${solicitud})`, 'start');
    const subcription = this.dialogService.showConfirmation(MSG_REACTIVATE).pipe(
      switchMap((accept) => {
        if (accept) {
          return this.solicitudService.reactivar(solicitud.id);
        } else {
          return of();
        }
      })
    ).subscribe(
      () => {
        this.snackBarService.showSuccess(MSG_SUCCESS_REACTIVATE);
        this.loadTable();
        this.logger.debug(SolicitudListadoComponent.name,
          `activateSolicitud(solicitud: ${solicitud})`, 'end');
      },
      (error) => {
        this.snackBarService.showError(MSG_ERROR_REACTIVATE);
        this.logger.error(SolicitudListadoComponent.name,
          `activateSolicitud(solicitud: ${solicitud})`, error);
      }
    );
    this.suscripciones.push(subcription);
  }

  /**
   * Desactivar solicitud
   * @param solicitud una solicitud
   */
  deactivateSolicitud(solicitud: ISolicitud): void {
    this.logger.debug(SolicitudListadoComponent.name, `deactivateSolicitud(solicitud: ${solicitud})`, 'start');
    const subcription = this.dialogService.showConfirmation(MSG_DEACTIVATE).pipe(
      switchMap((accept) => {
        if (accept) {
          return this.solicitudService.desactivar(solicitud.id);
        } else {
          return of();
        }
      })
    ).subscribe(
      () => {
        this.snackBarService.showSuccess(MSG_SUCCESS_DEACTIVATE);
        this.loadTable();
        this.logger.debug(SolicitudListadoComponent.name,
          `deactivateSolicitud(solicitud: ${solicitud})`, 'end');
      },
      (error) => {
        this.snackBarService.showError(MSG_ERROR_DEACTIVATE);
        this.logger.error(SolicitudListadoComponent.name,
          `deactivateSolicitud(solicitud: ${solicitud})`, error);
      }
    );
    this.suscripciones.push(subcription);
  }

  toggleBusquedaAvanzada(): void {
    this.logger.debug(SolicitudListadoComponent.name, `toggleBusquedaAvanzada()`, 'start');
    this.busquedaAvanzada = !this.busquedaAvanzada;
    this.cleanBusquedaAvanzado();
    this.logger.debug(SolicitudListadoComponent.name, `toggleBusquedaAvanzada()`, 'end');
  }

  onClearFilters(): void {
    this.logger.debug(SolicitudListadoComponent.name, `onClearFilters()`, 'start');
    super.onClearFilters();
    this.cleanBusquedaAvanzado();
    this.logger.debug(SolicitudListadoComponent.name, `onClearFilters()`, 'end');
  }

  private cleanBusquedaAvanzado(): void {
    this.logger.debug(SolicitudListadoComponent.name, `cleanBusquedaAvanzado()`, 'start');
    this.formGroup.controls.plazoAbierto.setValue(false);
    this.formGroup.controls.fechaInicioDesde.setValue(undefined);
    this.formGroup.controls.fechaInicioHasta.setValue(undefined);
    this.formGroup.controls.fechaFinDesde.setValue(undefined);
    this.formGroup.controls.fechaFinHasta.setValue(undefined);
    this.formGroup.controls.solicitante.setValue(undefined);
    this.formGroup.controls.activo.setValue(undefined);
    this.formGroup.controls.añoConvocatoria.setValue(undefined);
    this.formGroup.controls.entidadConvocante.setValue(undefined);
    this.formGroup.controls.entidadFinanciadora.setValue(undefined);
    this.empresaConvocanteText = '';
    this.empresaFinanciadoraText = '';
    this.logger.debug(SolicitudListadoComponent.name, `cleanBusquedaAvanzado()`, 'end');
  }

  setEmpresaConvocante(empresa: IEmpresaEconomica): void {
    this.logger.debug(SolicitudListadoComponent.name,
      `setEmpresaConvocante(empresa: ${empresa})`, 'start');
    this.formGroup.controls.entidadConvocante.setValue(empresa);
    this.empresaConvocanteText = empresa.razonSocial;
    this.logger.debug(SolicitudListadoComponent.name,
      `setEmpresaConvocante(empresa: ${empresa})`, 'end');
  }

  setEmpresaFinanciadora(empresa: IEmpresaEconomica): void {
    this.logger.debug(SolicitudListadoComponent.name,
      `setEmpresaFinanciadora(empresa: ${empresa})`, 'start');
    this.formGroup.controls.entidadFinanciadora.setValue(empresa);
    this.empresaFinanciadoraText = empresa.razonSocial;
    this.logger.debug(SolicitudListadoComponent.name,
      `setEmpresaFinanciadora(empresa: ${empresa})`, 'end');
  }

  private getFuentesFinanciacion(): void {
    this.logger.debug(SolicitudListadoComponent.name, `getFuentesFinanciacion()`, 'start');
    this.suscripciones.push(
      this.fuenteFinanciacionService.findAll().subscribe(
        (res) => {
          this.fuenteFinanciacionFiltered = res.items;
          this.fuenteFinanciacion$ = this.formGroup.controls.fuenteFinanciacion.valueChanges
            .pipe(
              startWith(''),
              map(value => this.filtroFuenteFinanciacion(value))
            );
          this.logger.debug(SolicitudListadoComponent.name, `getFuentesFinanciacion()`, 'end');
        },
        (error) => {
          this.snackBarService.showError(MSG_ERROR_FUENTE_FINANCIACION_INIT);
          this.logger.error(SolicitudListadoComponent.name, `getFuentesFinanciacion()`, error);
        }
      )
    );
  }

  private filtroFuenteFinanciacion(value: string): IFuenteFinanciacion[] {
    const filterValue = value?.toString().toLowerCase();
    return this.fuenteFinanciacionFiltered.filter(fuente => fuente.nombre.toLowerCase().includes(filterValue));
  }

  getFuenteFinanciacion(fuente?: IFuenteFinanciacion): string | undefined {
    return typeof fuente === 'string' ? fuente : fuente?.nombre;
  }

  private getPlanesInvestigacion(): void {
    this.logger.debug(SolicitudListadoComponent.name, `getPlanesInvestigacion()`, 'start');
    this.suscripciones.push(
      this.programaService.findAllPlan().subscribe(
        (res) => {
          this.planInvestigacionFiltered = res.items;
          this.planInvestigaciones$ = this.formGroup.controls.planInvestigacion.valueChanges
            .pipe(
              startWith(''),
              map(value => this.filtroPlanInvestigacion(value))
            );
          this.logger.debug(SolicitudListadoComponent.name, `getPlanesInvestigacion()`, 'end');
        },
        (error) => {
          this.snackBarService.showError(MSG_ERROR_PLAN_INVESTIGACION_INIT);
          this.logger.error(SolicitudListadoComponent.name, `getPlanesInvestigacion()`, error);
        }
      )
    );
  }

  private filtroPlanInvestigacion(value: string): IPrograma[] {
    const filterValue = value?.toString().toLowerCase();
    return this.planInvestigacionFiltered.filter(fuente => fuente.nombre.toLowerCase().includes(filterValue));
  }

  getPlanInvestigacion(programa?: IPrograma): string | undefined {
    return typeof programa === 'string' ? programa : programa?.nombre;
  }
}
