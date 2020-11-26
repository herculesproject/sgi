import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { IDocumentoRequerido } from '@core/models/csp/documentos-requeridos-solicitud';
import { ITipoDocumento } from '@core/models/csp/tipos-configuracion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ModeloEjecucionService } from '@core/services/csp/modelo-ejecucion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { SgiRestFindOptions, SgiRestFilterType, SgiRestFilter } from '@sgi/framework/http';

const MSG_ERROR_INIT = marker('csp.convocatoria.plazos.enlace.error.cargar');
const MSG_ANADIR = marker('botones.aniadir');
const MSG_ACEPTAR = marker('botones.aceptar');
@Component({
  templateUrl: './convocatoria-configuracion-solicitudes-modal.component.html',
  styleUrls: ['./convocatoria-configuracion-solicitudes-modal.component.scss']
})
export class ConvocatoriaConfiguracionSolicitudesModalComponent extends
  BaseModalComponent<IDocumentoRequerido, ConvocatoriaConfiguracionSolicitudesModalComponent> implements OnInit {
  fxLayoutProperties: FxLayoutProperties;
  fxFlexProperties: FxFlexProperties;

  documentoRequeridoFiltered: ITipoDocumento[];
  documentoRequerido$: Observable<ITipoDocumento[]>;
  textSaveOrUpdate: string;

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<ConvocatoriaConfiguracionSolicitudesModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: IDocumentoRequerido,
    private modeloEjecucionService: ModeloEjecucionService
  ) {
    super(logger, snackBarService, matDialogRef, data);
    this.logger.debug(ConvocatoriaConfiguracionSolicitudesModalComponent.name, 'constructor()', 'start');
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.layoutAlign = 'row';
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties.order = '2';
    this.logger.debug(ConvocatoriaConfiguracionSolicitudesModalComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(ConvocatoriaConfiguracionSolicitudesModalComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();
    this.loadTipoDocumento();
    this.textSaveOrUpdate = this.data.tipoDocumento ? MSG_ACEPTAR : MSG_ANADIR;
    this.logger.debug(ConvocatoriaConfiguracionSolicitudesModalComponent.name, 'ngOnInit()', 'end');
  }

  /**
   * Carga todos los tipos de documento
   */
  loadTipoDocumento() {
    this.logger.debug(ConvocatoriaConfiguracionSolicitudesModalComponent.name,
      `${this.loadTipoDocumento.name}()`, 'start');
    const id = this.data.configuracionSolicitud.convocatoria.modeloEjecucion.id;
    const idTipoFase = this.data.configuracionSolicitud.fasePresentacionSolicitudes?.tipoFase.id;
    const options = {
      filters: [
        {
          field: 'modeloTipoFase.tipoFase.id',
          type: SgiRestFilterType.EQUALS,
          value: idTipoFase ? idTipoFase.toString() : null
        } as SgiRestFilter
      ]
    } as SgiRestFindOptions;
    const subscription = this.modeloEjecucionService.findModeloTipoDocumento(id, options).pipe(
      map((result => {
        const listado: ITipoDocumento[] = [];
        result.items.forEach(x => listado.push(x.tipoDocumento));
        return listado;
      }))
    ).subscribe(
      (res) => {
        this.documentoRequeridoFiltered = res;
        this.documentoRequerido$ = this.formGroup.controls.tipoDocumento.valueChanges
          .pipe(
            startWith(''),
            map(value => this.filtroTipoDocumento(value))
          );
        this.logger.debug(ConvocatoriaConfiguracionSolicitudesModalComponent.name,
          `${this.loadTipoDocumento.name}()`, 'end');
      },
      (error) => {
        this.snackBarService.showError(MSG_ERROR_INIT);
        this.logger.error(ConvocatoriaConfiguracionSolicitudesModalComponent.name,
          `${this.loadTipoDocumento.name}()`, error);
      }
    );
    this.subscriptions.push(subscription);
  }

  /**
   * Filtra la lista devuelta por el servicio.
   *
   * @param value del input para autocompletar
   */
  filtroTipoDocumento(value: string): ITipoDocumento[] {
    const filterValue = value.toString().toLowerCase();
    return this.documentoRequeridoFiltered.filter(docRequerido => docRequerido.nombre.toLowerCase().includes(filterValue));
  }

  /**
   * Devuelve el nombre
   * @param docRequerido array de documentos requeridos
   */
  getNombreTipoDocumento(docRequerido?: ITipoDocumento): string | undefined {
    return typeof docRequerido === 'string' ? docRequerido : docRequerido?.nombre;
  }

  protected getDatosForm(): IDocumentoRequerido {
    this.logger.debug(ConvocatoriaConfiguracionSolicitudesModalComponent.name, `${this.getDatosForm.name}()`, 'start');
    this.data.tipoDocumento = this.formGroup.controls.tipoDocumento.value;
    this.data.observaciones = this.formGroup.controls.observaciones.value;
    this.logger.debug(ConvocatoriaConfiguracionSolicitudesModalComponent.name, `${this.getDatosForm.name}()`, 'end');
    return this.data;
  }

  protected getFormGroup(): FormGroup {
    this.logger.debug(ConvocatoriaConfiguracionSolicitudesModalComponent.name, `${this.getFormGroup.name}()`, 'start');
    const formGroup = new FormGroup({
      tipoDocumento: new FormControl(this.data.tipoDocumento),
      observaciones: new FormControl(this.data.observaciones),
    });
    this.logger.debug(ConvocatoriaConfiguracionSolicitudesModalComponent.name, `${this.getFormGroup.name}()`, 'end');
    return formGroup;
  }
}
