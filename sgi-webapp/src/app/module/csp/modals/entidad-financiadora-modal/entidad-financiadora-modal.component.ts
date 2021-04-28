import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IEntidadFinanciadora } from '@core/models/csp/entidad-financiadora';
import { IFuenteFinanciacion } from '@core/models/csp/fuente-financiacion';
import { ITipoFinalidad, ITipoFinanciacion } from '@core/models/csp/tipos-configuracion';
import { IEmpresaEconomica } from '@core/models/sgp/empresa-economica';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { FuenteFinanciacionService } from '@core/services/csp/fuente-financiacion.service';
import { TipoFinanciacionService } from '@core/services/csp/tipo-financiacion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export interface EntidadFinanciadoraDataModal {
  title: string;
  entidad: IEntidadFinanciadora;
  selectedEmpresas: IEmpresaEconomica[];
  readonly: boolean;
}

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const ENTIDAD_FINANCIADORA_KEY = marker('csp.entidad-financiadora');
const ENTIDAD_FINANCIADORA_PORCENTAJE_FINANCIACION_KEY = marker('csp.entidad-financiadora.porcentaje-financiacion');

@Component({
  templateUrl: './entidad-financiadora-modal.component.html',
  styleUrls: ['./entidad-financiadora-modal.component.scss']
})
export class EntidadFinanciadoraModalComponent extends
  BaseModalComponent<IEntidadFinanciadora, EntidadFinanciadoraModalComponent> implements OnInit {

  fuentesFinanciacion$: Observable<IFuenteFinanciacion[]>;
  tiposFinanciacion$: Observable<ITipoFinalidad[]>;
  textSaveOrUpdate: string;
  title: string;

  msgParamPorcentajeFinanciacionoEntity = {};
  msgParamEmpresaEconomicaEntity = {};

  constructor(
    protected snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<EntidadFinanciadoraModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: EntidadFinanciadoraDataModal,
    private tipoFinanciacionService: TipoFinanciacionService,
    private fuenteFinanciacionService: FuenteFinanciacionService,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, data.entidad);
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.layoutAlign = 'row';
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.loadFuentesFinanciacion();
    this.loadTiposFinanciacion();
    this.textSaveOrUpdate = this.data.entidad?.empresa ? MSG_ACEPTAR : MSG_ANADIR;
    this.title = this.data.title;
  }

  private setupI18N(): void {
    this.translate.get(
      ENTIDAD_FINANCIADORA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEmpresaEconomicaEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });

    this.translate.get(
      ENTIDAD_FINANCIADORA_PORCENTAJE_FINANCIACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamPorcentajeFinanciacionoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });
  }

  protected getDatosForm(): IEntidadFinanciadora {
    const fuenteFinanciacion = this.formGroup.get('fuenteFinanciacion').value;
    const tipoFinanciacion = this.formGroup.get('tipoFinanciacion').value;

    const entidad = this.data.entidad;
    entidad.empresa = this.formGroup.get('empresaEconomica').value;
    entidad.fuenteFinanciacion = typeof fuenteFinanciacion === 'string' ? undefined : fuenteFinanciacion;
    entidad.tipoFinanciacion = typeof tipoFinanciacion === 'string' ? undefined : tipoFinanciacion;
    entidad.porcentajeFinanciacion = this.formGroup.get('porcentajeFinanciacion').value;
    return entidad;
  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      empresaEconomica: new FormControl(
        {
          value: this.data.entidad.empresa,
          disabled: this.data.entidad.empresa || this.data.readonly
        },
        [Validators.required]),
      fuenteFinanciacion: new FormControl(this.data.entidad.fuenteFinanciacion),
      tipoFinanciacion: new FormControl(this.data.entidad.tipoFinanciacion),
      porcentajeFinanciacion: new FormControl(this.data.entidad.porcentajeFinanciacion, [
        Validators.min(0),
        Validators.max(100)
      ])
    });
    if (this.data.readonly) {
      formGroup.disable();
    }
    return formGroup;
  }

  /**
   * Carga todas las fuentes de financiación
   */
  private loadFuentesFinanciacion(): void {
    this.fuentesFinanciacion$ = this.fuenteFinanciacionService.findAll().pipe(
      map((fuenteFinanciones) => fuenteFinanciones.items)
    );
  }

  /**
   * Carga todos los tipos de financiación
   */
  private loadTiposFinanciacion(): void {
    this.tiposFinanciacion$ = this.tipoFinanciacionService.findAll().pipe(
      map((tipoFinanciaciones) => tipoFinanciaciones.items)
    );
  }

  getNombreFuenteFinanciacion(fuenteFinanciacion: IFuenteFinanciacion): string {
    return fuenteFinanciacion?.nombre;
  }

  getNombreTipoFinanciacion(tipoFinanciacion: ITipoFinanciacion): string {
    return tipoFinanciacion?.nombre;
  }
}
