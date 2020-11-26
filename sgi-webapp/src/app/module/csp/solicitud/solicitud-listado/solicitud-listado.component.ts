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
import { switchMap, map } from 'rxjs/operators';
import { IPersona } from '@core/models/sgp/persona';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';
import { TipoEstadoSolicitud } from '@core/models/csp/estado-solicitud';
import { DialogService } from '@core/services/dialog.service';

const MSG_BUTTON_NEW = marker('footer.csp.solicitud.crear');
const MSG_ERROR = marker('csp.solicitud.listado.error');
const MSG_DEACTIVATE = marker('csp.solicitud.listado.desactivar');
const MSG_SUCCESS_DEACTIVATE = marker('csp.solicitud.listado.desactivar.correcto');
const MSG_ERROR_DEACTIVATE = marker('csp.solicitud.listado.desactivar.error');
const MSG_REACTIVATE = marker('csp.solicitud.listado.reactivar');
const MSG_SUCCESS_REACTIVATE = marker('csp.solicitud.listado.reactivar.correcto');
const MSG_ERROR_REACTIVATE = marker('csp.solicitud.listado.reactivar.error');


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

  constructor(
    protected readonly logger: NGXLogger,
    private readonly dialogService: DialogService,
    protected readonly snackBarService: SnackBarService,
    private readonly solicitudService: SolicitudService,
    private readonly personaFisicaService: PersonaFisicaService
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
    });

    this.loadTiposEstadoSolicitud();

    this.logger.debug(SolicitudListadoComponent.name, 'ngOnInit()', 'end');
  }

  protected createObservable(): Observable<SgiRestListResult<ISolicitud>> {
    this.logger.debug(SolicitudListadoComponent.name, `${this.createObservable.name}()`, 'start');
    const observable$ = this.solicitudService.findAllTodos(this.getFindOptions()).pipe(
      switchMap((response) => {
        if (response.total === 0) {
          return of(response);
        }

        const solicitudes = response.items;

        const personaRefsSolicitantes = solicitudes.map((solicitud: ISolicitud) => solicitud.solicitante.personaRef);
        const solicitudesWithDatosSolicitante$ = this.personaFisicaService.findByPersonasRefs([...personaRefsSolicitantes]).pipe(
          map((result: SgiRestListResult<IPersona>) => {
            const personas = result.items;

            solicitudes.forEach((solicitud: ISolicitud) => {
              solicitud.solicitante = personas.find((persona: IPersona) =>
                solicitud.solicitante.personaRef === persona.personaRef);
            });

            return response;
          })
        );

        return solicitudesWithDatosSolicitante$;
      })
    );

    this.logger.debug(SolicitudListadoComponent.name, `${this.createObservable.name}()`, 'end');
    return observable$;
  }

  protected initColumns(): void {
    this.logger.debug(SolicitudListadoComponent.name, `${this.initColumns.name}()`, 'start');
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
    this.logger.debug(SolicitudListadoComponent.name, `${this.initColumns.name}()`, 'end');
  }

  protected loadTable(reset?: boolean): void {
    this.logger.debug(SolicitudListadoComponent.name, `${this.loadTable.name}(${reset})`, 'start');
    this.solicitudes$ = this.getObservableLoadTable(reset);
    this.logger.debug(SolicitudListadoComponent.name, `${this.loadTable.name}(${reset})`, 'end');
  }

  protected createFilters(): SgiRestFilter[] {
    this.logger.debug(SolicitudListadoComponent.name, `${this.createFilters.name}()`, 'start');
    const filtros = [];

    this.addFiltro(filtros, 'referenciaConvocatoria', SgiRestFilterType.LIKE, this.formGroup.controls.referenciaConvocatoria.value);
    this.addFiltro(filtros, 'estado.estado', SgiRestFilterType.EQUALS, Object.keys(TipoEstadoSolicitud)
      .filter(key => TipoEstadoSolicitud[key] === this.formGroup.controls.estadoSolicitud.value)[0]);
    this.logger.debug(SolicitudListadoComponent.name, `${this.createFilters.name}()`, 'end');
    return filtros;
  }


  loadTiposEstadoSolicitud() {
    this.logger.debug(SolicitudListadoComponent.name, 'loadTiposJustificacion()', 'start');
    this.tiposEstadoSolicitud = Object.keys(TipoEstadoSolicitud).map(key => TipoEstadoSolicitud[key]);
    this.logger.debug(SolicitudListadoComponent.name, 'loadTiposJustificacion()', 'end');
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
      })).subscribe(
        () => {
          this.snackBarService.showSuccess(MSG_SUCCESS_REACTIVATE);
          this.loadTable();
          this.logger.debug(SolicitudListadoComponent.name,
            `activateSolicitud(solicitud: ${solicitud})`, 'end');
        },
        () => {
          this.snackBarService.showError(MSG_ERROR_REACTIVATE);
          this.logger.error(SolicitudListadoComponent.name,
            `activateSolicitud(solicitud: ${solicitud})`, 'error');
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
      })).subscribe(
        () => {
          this.snackBarService.showSuccess(MSG_SUCCESS_DEACTIVATE);
          this.loadTable();
          this.logger.debug(SolicitudListadoComponent.name,
            `deactivateSolicitud(solicitud: ${solicitud})`, 'end');
        },
        () => {
          this.snackBarService.showError(MSG_ERROR_DEACTIVATE);
          this.logger.error(SolicitudListadoComponent.name,
            `deactivateSolicitud(solicitud: ${solicitud})`, 'error');
        }
      );
    this.suscripciones.push(subcription);
  }

}
