import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { MSG_PARAMS } from '@core/i18n';
import { INotificacionProyectoExternoCVN } from '@core/models/csp/notificacion-proyecto-externo-cvn';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { NotificacionProyectoExternoCvnService } from '@core/services/csp/notificacion-proyecto-externo-cvn/notificacion-proyecto-externo-cvn.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { SgiRestListResult, SgiRestFilter, RSQLSgiRestFilter, SgiRestFilterOperator } from '@sgi/framework/http';
import { from, Observable, of } from 'rxjs';
import { filter, map, mergeMap, switchMap, toArray } from 'rxjs/operators';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';
import { NotificacionCvnAsociarAutorizacionModalComponent } from '../notificacion-cvn-asociar-autorizacion-modal/notificacion-cvn-asociar-autorizacion-modal.component';

const MSG_ERROR_LOAD = marker('error.load');
const MSG_ASOCIAR_SUCCESS = marker('msg.asociar.entity.success');
const AUTORIZACION_KEY = marker('csp.autorizacion');

@Component({
  selector: 'sgi-notificacion-cvn-listado',
  templateUrl: './notificacion-cvn-listado.component.html',
  styleUrls: ['./notificacion-cvn-listado.component.scss']
})
export class NotificacionCvnListadoComponent extends AbstractTablePaginationComponent<INotificacionProyectoExternoCVN> implements OnInit {
  fxLayoutProperties: FxLayoutProperties;

  notificaciones$: Observable<INotificacionProyectoExternoCVN[]>;

  textoAsociarSuccess: string;
  msgParamAutorizacionEntity = {};

  TIPO_COLECTIVO = TipoColectivo;

  constructor(
    protected readonly snackBarService: SnackBarService,
    private readonly notificacionProyectoExternoCvnService: NotificacionProyectoExternoCvnService,
    private readonly personaService: PersonaService,
    private matDialog: MatDialog,
    private readonly empresaService: EmpresaService,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, MSG_ERROR_LOAD);
    this.initFlexProperties();
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.initFormGroup();
  }


  private setupI18N(): void {
    this.translate.get(
      AUTORIZACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ASOCIAR_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoAsociarSuccess = value);

    this.translate.get(
      AUTORIZACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamAutorizacionEntity = { entity: value });

  }

  private initFormGroup(): void {
    this.formGroup = new FormGroup({
      investigador: new FormControl(null),
      titulo: new FormControl(''),
      fechaInicioProyectoDesde: new FormControl(null),
      fechaInicioProyectoHasta: new FormControl(null),
      fechaFinProyectoDesde: new FormControl(null),
      fechaFinProyectoHasta: new FormControl(null),
      entidadParticipacion: new FormControl(null)
    });
  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<INotificacionProyectoExternoCVN>> {
    return this.notificacionProyectoExternoCvnService.findAll(this.getFindOptions(reset)).pipe(
      switchMap(notificaciones => from(notificaciones.items).pipe(
        mergeMap(notificacion => this.fillNotificacionSolicitante(notificacion)),
        mergeMap(notificacion => this.fillNotificacionEntidadParticipacion(notificacion)),
        mergeMap(notificacion => this.fillNotificacionInvestigadorPrincipal(notificacion)),
        toArray(),
        map(items => {
          notificaciones.items = items;
          return notificaciones;
        })
      ))
    );
  }

  private fillNotificacionSolicitante(notificacion: INotificacionProyectoExternoCVN): Observable<INotificacionProyectoExternoCVN> {
    if (!notificacion.solicitante?.id) {
      return of(notificacion);
    }
    return this.personaService.findById(notificacion.solicitante.id).pipe(
      map(solicitante => {
        notificacion.solicitante = solicitante;
        return notificacion;
      })
    );
  }

  private fillNotificacionEntidadParticipacion(notificacion: INotificacionProyectoExternoCVN): Observable<INotificacionProyectoExternoCVN> {
    if (!notificacion.entidadParticipacion?.id) {
      return of(notificacion);
    }
    return this.empresaService.findById(notificacion.entidadParticipacion.id).pipe(
      map(entidadParticipacion => {
        notificacion.entidadParticipacion = entidadParticipacion;
        return notificacion;
      })
    );
  }

  private fillNotificacionInvestigadorPrincipal(
    notificacion: INotificacionProyectoExternoCVN
  ): Observable<INotificacionProyectoExternoCVN> {
    if (!notificacion.responsable?.id) {
      return of(notificacion);
    }
    return this.personaService.findById(notificacion.responsable.id).pipe(
      map(responsable => {
        notificacion.responsable = responsable;
        return notificacion;
      })
    );
  }


  protected initColumns(): void {
    this.columnas = ['investigador', 'titulo', 'entidadParticipacion', 'ip', 'fechaInicio', 'fechaFin', 'acciones'];
  }

  protected loadTable(reset?: boolean): void {
    this.notificaciones$ = this.getObservableLoadTable(reset);
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    return new RSQLSgiRestFilter(
      'solicitanteRef', SgiRestFilterOperator.EQUALS, controls.investigador.value?.id)
      .and('titulo', SgiRestFilterOperator.LIKE_ICASE, controls.titulo.value)
      .and('fechaInicio', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaInicioProyectoDesde.value))
      .and('fechaInicio', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaInicioProyectoHasta.value))
      .and('fechaFin', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaFinProyectoDesde.value))
      .and('fechaFin', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaFinProyectoHasta.value))
      .and('entidadParticipacionRef', SgiRestFilterOperator.EQUALS, controls.entidadParticipacion.value?.id);
  }

  onClearFilters() {
    super.onClearFilters();
    this.formGroup.controls.fechaInicioProyectoDesde.setValue(null);
    this.formGroup.controls.fechaInicioProyectoHasta.setValue(null);
    this.formGroup.controls.fechaFinProyectoDesde.setValue(null);
    this.formGroup.controls.fechaFinProyectoHasta.setValue(null);
    this.onSearch();
  }

  private initFlexProperties(): void {
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '1%';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  /**
   * Abre un modal para añadir o actualizar
   *
   * @param data notificacionProyectoExternoCvn
   */
  openModal(data: INotificacionProyectoExternoCVN): void {
    const config: MatDialogConfig<INotificacionProyectoExternoCVN> = {
      panelClass: 'sgi-dialog-container',
      data
    };
    const dialogRef = this.matDialog.open(NotificacionCvnAsociarAutorizacionModalComponent, config);
    dialogRef.afterClosed().pipe(
      filter(result => !!result),
      switchMap((result: INotificacionProyectoExternoCVN) =>
        this.notificacionProyectoExternoCvnService.update(result.id, result).pipe(
          map(() => result)
        ))
    ).subscribe(result => {
      this.snackBarService.showSuccess(data ? this.textoAsociarSuccess : null);
    });
  }
}
