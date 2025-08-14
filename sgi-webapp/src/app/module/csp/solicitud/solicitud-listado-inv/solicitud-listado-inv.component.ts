import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { Estado, ESTADO_MAP } from '@core/models/csp/estado-solicitud';
import { ISolicitud } from '@core/models/csp/solicitud';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { ConfigService } from '@core/services/csp/configuracion/config.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthService } from '@herculesproject/framework/auth';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@herculesproject/framework/http';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { forkJoin, merge, Observable, of } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';

const MSG_BUTTON_NEW = marker('btn.add.entity');
const MSG_DEACTIVATE = marker('msg.deactivate.entity');
const MSG_SUCCESS_DEACTIVATE = marker('msg.csp.deactivate.success');
const MSG_ERROR_DEACTIVATE = marker('error.csp.deactivate.entity');
const SOLICITUD_KEY = marker('csp.solicitud');

interface SolicitudListado extends ISolicitud {
  convocatoria: IConvocatoria;
  modificable: boolean;
  eliminable: boolean;
}

@Component({
  selector: 'sgi-solicitud-listado',
  templateUrl: './solicitud-listado-inv.component.html',
  styleUrls: ['./solicitud-listado-inv.component.scss']
})
export class SolicitudListadoInvComponent extends AbstractTablePaginationComponent<SolicitudListado> implements OnInit {
  ROUTE_NAMES = ROUTE_NAMES;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  solicitudes$: Observable<SolicitudListado[]>;
  textoCrear: string;
  textoDesactivar: string;
  textoReactivar: string;
  textoErrorDesactivar: string;
  textoSuccessDesactivar: string;

  isAddSolicitudEnabled = false;

  msgParamConvocatoriaExternaEntity = {};
  msgParamCodigoExternoEntity = {};
  msgParamObservacionesEntity = {};
  msgParamUnidadGestionEntity = {};

  get ESTADO_MAP() {
    return ESTADO_MAP;
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    private readonly logger: NGXLogger,
    private dialogService: DialogService,
    protected snackBarService: SnackBarService,
    private solicitudService: SolicitudService,
    private readonly translate: TranslateService,
    private sgiAuthService: SgiAuthService,
    private configService: ConfigService
  ) {
    super(translate);
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(17%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

    this.resolveSortProperty = (column: string) => {
      if (column === 'titulo') {
        return 'titulo.value';
      }
      return column;
    }
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.formGroup = new FormGroup({
      convocatoria: new FormControl(undefined),
      estadoSolicitud: new FormControl(''),
      tituloSolicitud: new FormControl([]),
    });

    this.suscripciones.push(
      this.configService.isSolicitudesSinConvocatoriaInvestigadorEnabled().subscribe(value => {
        this.isAddSolicitudEnabled = value;
      })
    );
  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<SolicitudListado>> {
    return this.solicitudService.findAllInvestigador(this.getFindOptions(reset)).pipe(
      map(response => {
        return response as SgiRestListResult<SolicitudListado>;
      }),
      switchMap(response => {
        const requestsConvocatoria: Observable<SolicitudListado>[] = [];
        response.items.forEach(solicitud => {
          if (solicitud.convocatoriaId) {
            requestsConvocatoria.push(this.solicitudService.findConvocatoria(solicitud.id).pipe(
              map(convocatoria => {
                solicitud.convocatoria = convocatoria;
                return solicitud;
              })
            ));
          }
          else {
            requestsConvocatoria.push(of(solicitud));
          }
        });
        return of(response).pipe(
          tap(() => merge(...requestsConvocatoria).subscribe())
        );
      }),
      switchMap((response) => {
        const requestsModificable: Observable<SolicitudListado>[] = [];
        response.items.forEach(solicitud => {
          requestsModificable.push(
            forkJoin({
              modificable: this.solicitudService.modificableByInvestigador(solicitud.id),
              estadoAndDocumentosReadonly: this.solicitudService.modificableEstadoAndDocumentosByInvestigador(solicitud.id)
            }).pipe(
              map(({ modificable, estadoAndDocumentosReadonly }) => {
                solicitud.modificable = modificable || estadoAndDocumentosReadonly;
                return solicitud;
              })
            )
          );
        });
        return of(response).pipe(
          tap(() => merge(...requestsModificable).subscribe())
        );
      }),
      map((response) => {
        response.items.forEach(solicitud => {
          solicitud.eliminable = solicitud.estado.estado === Estado.BORRADOR
            && solicitud.creador.id === this.sgiAuthService.authStatus$?.getValue()?.userRefId;
        });
        return response;
      })
    );
  }

  protected initColumns(): void {
    this.columnas = [
      'codigoRegistroInterno',
      'codigoExterno',
      'titulo',
      'referencia',
      'estado.estado',
      'estado.fechaEstado',
      'acciones'
    ];
  }

  protected loadTable(reset?: boolean): void {
    this.solicitudes$ = this.getObservableLoadTable(reset);
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;

    return new RSQLSgiRestFilter('convocatoria.id', SgiRestFilterOperator.EQUALS, controls.convocatoria.value?.id?.toString())
      .and('estado.estado', SgiRestFilterOperator.EQUALS, controls.estadoSolicitud.value)
      .and('titulo.value', SgiRestFilterOperator.LIKE_ICASE, controls.tituloSolicitud.value);
  }

  /**
   * Desactivar solicitud
   * @param solicitud una solicitud
   */
  deactivateSolicitud(solicitud: ISolicitud): void {
    const subcription = this.dialogService.showConfirmation(this.textoDesactivar).pipe(
      switchMap((accept) => {
        if (accept) {
          return this.solicitudService.desactivar(solicitud.id);
        } else {
          return of();
        }
      })
    ).subscribe(
      () => {
        this.snackBarService.showSuccess(this.textoSuccessDesactivar);
        this.loadTable();
      },
      (error) => {
        this.logger.error(error);
        if (error instanceof SgiError) {
          this.processError(error);
        }
        else {
          this.processError(new SgiError(this.textoErrorDesactivar));
        }
      }
    );
    this.suscripciones.push(subcription);
  }

  protected setupI18N(): void {
    this.translate.get(
      SOLICITUD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_BUTTON_NEW,
          { entity: value }
        );
      })
    ).subscribe((value) => this.textoCrear = value);

    this.translate.get(
      SOLICITUD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DEACTIVATE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoDesactivar = value);

    this.translate.get(
      SOLICITUD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR_DEACTIVATE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoErrorDesactivar = value);

    this.translate.get(
      SOLICITUD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS_DEACTIVATE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoSuccessDesactivar = value);
  }

}
