import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { IConvocatoriaEntidadFinanciadora } from '@core/models/csp/convocatoria-entidad-financiadora';
import { IFuenteFinanciacion } from '@core/models/csp/fuente-financiacion';
import { ITipoFinalidad, ITipoFinanciacion } from '@core/models/csp/tipos-configuracion';
import { IEmpresaEconomica } from '@core/models/sgp/empresa-economica';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { FuenteFinanciacionService } from '@core/services/csp/fuente-financiacion.service';
import { TipoFinanciacionService } from '@core/services/csp/tipo-financiacion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export interface ConvocatoriaEntidadFinanciadoraDataModal {
  entidad: IConvocatoriaEntidadFinanciadora;
  empresa: IEmpresaEconomica;
  selectedEmpresas: IEmpresaEconomica[];
  readonly: boolean;
}

const MSG_ANADIR = marker('botones.aniadir');
const MSG_ACEPTAR = marker('botones.aceptar');
@Component({
  templateUrl: './convocatoria-entidad-financiadora-modal.component.html',
  styleUrls: ['./convocatoria-entidad-financiadora-modal.component.scss']
})
export class ConvocatoriaEntidadFinanciadoraModalComponent extends
  BaseModalComponent<IConvocatoriaEntidadFinanciadora, ConvocatoriaEntidadFinanciadoraModalComponent> implements OnInit {

  fuentesFinanciacion$: Observable<IFuenteFinanciacion[]>;
  tiposFinanciacion$: Observable<ITipoFinalidad[]>;
  textSaveOrUpdate: string;

  constructor(
    protected logger: NGXLogger,
    protected snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<ConvocatoriaEntidadFinanciadoraModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ConvocatoriaEntidadFinanciadoraDataModal,
    private tipoFinanciacionService: TipoFinanciacionService,
    private fuenteFinanciacionService: FuenteFinanciacionService
  ) {
    super(logger, snackBarService, matDialogRef, data.entidad);
    this.logger.debug(ConvocatoriaEntidadFinanciadoraModalComponent.name, 'constructor()', 'start');
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.layoutAlign = 'row';
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties.order = '2';
    this.logger.debug(ConvocatoriaEntidadFinanciadoraModalComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(ConvocatoriaEntidadFinanciadoraModalComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();
    this.loadFuentesFinanciacion();
    this.loadTiposFinanciacion();
    this.textSaveOrUpdate = this.data.empresa ? MSG_ACEPTAR : MSG_ANADIR;
    this.logger.debug(ConvocatoriaEntidadFinanciadoraModalComponent.name, 'ngOnInit()', 'end');
  }

  protected getDatosForm(): IConvocatoriaEntidadFinanciadora {
    this.logger.debug(ConvocatoriaEntidadFinanciadoraModalComponent.name, `getDatosForm()`, 'start');
    const entidad = this.data.entidad;
    entidad.entidadRef = this.formGroup.get('empresaEconomica').value?.personaRef;
    entidad.fuenteFinanciacion = this.formGroup.get('fuenteFinanciacion').value;
    entidad.tipoFinanciacion = this.formGroup.get('tipoFinanciacion').value;
    entidad.porcentajeFinanciacion = this.formGroup.get('porcentajeFinanciacion').value;
    this.logger.debug(ConvocatoriaEntidadFinanciadoraModalComponent.name, `getDatosForm()`, 'end');
    return entidad;
  }

  protected getFormGroup(): FormGroup {
    this.logger.debug(ConvocatoriaEntidadFinanciadoraModalComponent.name, `getFormGroup()`, 'start');
    const formGroup = new FormGroup({
      empresaEconomica: new FormControl(this.data.empresa),
      fuenteFinanciacion: new FormControl(this.data.entidad.fuenteFinanciacion),
      tipoFinanciacion: new FormControl(this.data.entidad.tipoFinanciacion),
      porcentajeFinanciacion: new FormControl(this.data.entidad.porcentajeFinanciacion)
    });
    if (this.data.readonly) {
      formGroup.disable();
    }
    this.logger.debug(ConvocatoriaEntidadFinanciadoraModalComponent.name, `getFormGroup()`, 'end');
    return formGroup;
  }

  /**
   * Carga todas las fuentes de financiación
   */
  private loadFuentesFinanciacion(): void {
    this.logger.debug(ConvocatoriaEntidadFinanciadoraModalComponent.name, `loadFuentesFinanciacion()`, 'start');
    this.fuentesFinanciacion$ = this.fuenteFinanciacionService.findAll().pipe(
      map((fuenteFinanciones) => fuenteFinanciones.items)
    );
    this.logger.debug(ConvocatoriaEntidadFinanciadoraModalComponent.name, `loadFuentesFinanciacion()`, 'end');
  }

  /**
   * Carga todos los tipos de financiación
   */
  private loadTiposFinanciacion(): void {
    this.logger.debug(ConvocatoriaEntidadFinanciadoraModalComponent.name, `loadTiposFinanciacion()`, 'start');
    this.tiposFinanciacion$ = this.tipoFinanciacionService.findAll().pipe(
      map((tipoFinanciaciones) => tipoFinanciaciones.items)
    );
    this.logger.debug(ConvocatoriaEntidadFinanciadoraModalComponent.name, `loadTiposFinanciacion()`, 'end');
  }

  getNombreFuenteFinanciacion(fuenteFinanciacion: IFuenteFinanciacion): string {
    return fuenteFinanciacion?.nombre;
  }

  getNombreTipoFinanciacion(tipoFinanciacion: ITipoFinanciacion): string {
    return tipoFinanciacion?.nombre;
  }
}
