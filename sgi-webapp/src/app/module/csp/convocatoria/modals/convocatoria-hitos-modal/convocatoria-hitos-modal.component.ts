import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatAutocompleteTrigger } from '@angular/material/autocomplete';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaHito } from '@core/models/csp/convocatoria-hito';
import { IModeloTipoHito } from '@core/models/csp/modelo-tipo-hito';
import { ITipoHito } from '@core/models/csp/tipos-configuracion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ModeloEjecucionService } from '@core/services/csp/modelo-ejecucion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { TipoHitoValidator } from '@core/validators/tipo-hito-validator';
import { TranslateService } from '@ngx-translate/core';
import { SgiRestListResult } from '@sgi/framework/http/types';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { Observable, Subscription } from 'rxjs';
import { map, startWith, switchMap } from 'rxjs/operators';

const MSG_ERROR_INIT = marker('error.load');
const MSG_ERROR_TIPOS = marker('error.csp.convocatoria-tipo-hito');
const MSG_ERROR_FORM_GROUP = marker('error.form-group');
const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const CONVOCATORIA_HITO_KEY = marker('csp.hito');
const CONVOCATORIA_HITO_COMENTARIO_KEY = marker('csp.hito.comentario');
const CONVOCATORIA_HITO_FECHA_INICIO_KEY = marker('csp.hito.fecha-inicio');
const CONVOCATORIA_HITO_TIPO_KEY = marker('csp.hito.tipo');
const TITLE_NEW_ENTITY = marker('title.new.entity');
export interface ConvocatoriaHitosModalComponentData {
  hitos: IConvocatoriaHito[],
  hito: IConvocatoriaHito;
  idModeloEjecucion: number;
  readonly: boolean;
}
@Component({
  templateUrl: './convocatoria-hitos-modal.component.html',
  styleUrls: ['./convocatoria-hitos-modal.component.scss']
})
export class ConvocatoriaHitosModalComponent implements OnInit {

  @ViewChild(MatAutocompleteTrigger) autocomplete: MatAutocompleteTrigger;
  formGroup: FormGroup;

  fxFlexProperties: FxFlexProperties;
  fxFlexProperties2: FxFlexProperties;
  fxFlexProperties3: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  modeloTiposHito$: Observable<IModeloTipoHito[]>;

  private modeloTiposHitoFiltered: IModeloTipoHito[];

  textSaveOrUpdate: string;
  title: string;

  suscripciones: Subscription[] = [];

  msgParamComentarioEntity = {};
  msgParamFechaInicioEntity = {};
  msgParamTipoEntity = {};

  constructor(
    private readonly logger: NGXLogger,
    public matDialogRef: MatDialogRef<ConvocatoriaHitosModalComponent>,
    private modeloEjecucionService: ModeloEjecucionService,
    @Inject(MAT_DIALOG_DATA) public data: ConvocatoriaHitosModalComponentData,
    private snackBarService: SnackBarService,
    private readonly translate: TranslateService) {

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(15%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxFlexProperties2 = new FxFlexProperties();
    this.fxFlexProperties2.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties2.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties2.gtMd = '0 1 calc(40%-10px)';
    this.fxFlexProperties2.order = '3';

    this.fxFlexProperties3 = new FxFlexProperties();
    this.fxFlexProperties3.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties3.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties3.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties3.order = '3';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit(): void {
    this.setupI18N();
    this.formGroup = new FormGroup({
      tipoHito: new FormControl(this.data?.hito?.tipoHito, [Validators.required, IsEntityValidator.isValid()]),
      fechaInicio: new FormControl(this.data?.hito?.fecha, [Validators.required]),
      comentario: new FormControl(this.data?.hito?.comentario, [Validators.maxLength(250)]),
      aviso: new FormControl(this.data?.hito?.generaAviso)
    });
    if (this.data.readonly) {
      this.formGroup.disable();
    }

    this.createValidatorDate(this.data?.hito?.tipoHito);

    const suscription = this.formGroup.controls.tipoHito.valueChanges.subscribe((value) => this.createValidatorDate(value));
    this.suscripciones.push(suscription);

    const suscriptionFecha = this.formGroup.controls.fechaInicio.valueChanges.subscribe(() =>
      this.createValidatorDate(this.formGroup.controls.tipoHito.value));
    this.suscripciones.push(suscriptionFecha);

    this.textSaveOrUpdate = this.data?.hito?.tipoHito ? MSG_ACEPTAR : MSG_ANADIR;
    this.loadTiposHito();
    this.suscripciones.push(this.formGroup.get('fechaInicio').valueChanges.subscribe(
      (value) => this.validarFecha(value)));
  }

  private setupI18N(): void {
    this.translate.get(
      CONVOCATORIA_HITO_TIPO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTipoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      CONVOCATORIA_HITO_FECHA_INICIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaInicioEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      CONVOCATORIA_HITO_COMENTARIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamComentarioEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.data.hito?.tipoHito) {
      this.translate.get(
        CONVOCATORIA_HITO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);

    } else {
      this.translate.get(
        CONVOCATORIA_HITO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            TITLE_NEW_ENTITY,
            { entity: value, ...MSG_PARAMS.GENDER.MALE }
          );
        })
      ).subscribe((value) => this.title = value);
    }
  }

  /**
   * Validacion de fechas a la hora de seleccionar
   * un tipo de hito en el modal
   * @param tipoHito convocatoria tipoHito
   */
  private createValidatorDate(tipoHito: ITipoHito): void {
    let fechas: DateTime[] = [];
    if (tipoHito && typeof tipoHito !== 'string') {
      const convocatoriasHitos = this.data.hitos.filter(hito =>
        hito.tipoHito.id === (tipoHito as ITipoHito).id &&
        (!hito.fecha.equals(this.data.hito.fecha)));
      fechas = convocatoriasHitos.map(hito => hito.fecha);
    }
    this.formGroup.setValidators([
      TipoHitoValidator.notInDate('fechaInicio', fechas, this.data?.hitos?.map(hito => hito.tipoHito))
    ]);
  }

  /**
   * Si la fecha actual es inferior - Checkbox disabled
   * Si la fecha actual es superior - Checkbox enable
   */
  private validarFecha(date: DateTime) {
    if (date <= DateTime.now()) {
      this.formGroup.get('aviso').disable();
      this.formGroup.get('aviso').setValue(false);
    } else {
      this.formGroup.get('aviso').enable();
    }
  }

  loadTiposHito() {
    this.suscripciones.push(
      this.modeloEjecucionService.findModeloTipoHitoConvocatoria(this.data.idModeloEjecucion).subscribe(
        (res: SgiRestListResult<IModeloTipoHito>) => {
          this.modeloTiposHitoFiltered = res.items;
          this.modeloTiposHito$ = this.formGroup.controls.tipoHito.valueChanges
            .pipe(
              startWith(''),
              map(value => this.filtroTipoHito(value))
            );
        },
        (error) => {
          this.logger.error(error);
          if (this.data.idModeloEjecucion) {
            this.snackBarService.showError(MSG_ERROR_INIT);
          } else {
            this.snackBarService.showError(MSG_ERROR_TIPOS);
          }
        })
    );
  }

  /**
   * Devuelve el nombre de un tipo de hito.
   * @param tipoHito tipo de hito.
   * @returns nombre de un tipo de hito.
   */
  getTipoHito(tipoHito?: ITipoHito): string | undefined {
    return typeof tipoHito === 'string' ? tipoHito : tipoHito?.nombre;
  }

  /**
   * Filtra la lista devuelta por el servicio.
   *
   * @param value del input para autocompletar
   */
  filtroTipoHito(value: string): IModeloTipoHito[] {
    const filterValue = value.toString().toLowerCase();
    return this.modeloTiposHitoFiltered.filter(modeloTipoHito =>
      modeloTipoHito.tipoHito?.nombre.toLowerCase().includes(filterValue));
  }

  /**
   * Cierra la ventana modal y devuelve el hito modificado o creado.
   *
   * @param hito hito modificado o creado.
   */
  closeModal(hito?: IConvocatoriaHito): void {
    this.matDialogRef.close(hito);
  }

  saveOrUpdate(): void {
    if (FormGroupUtil.valid(this.formGroup)) {
      this.loadDatosForm();
      this.closeModal(this.data.hito);
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM_GROUP);
    }
  }

  /**
   * Método para actualizar la entidad con los datos de un formGroup
   *
   * @returns Comentario con los datos del formulario
   */
  private loadDatosForm(): void {
    this.data.hito.comentario = this.formGroup.get('comentario').value;
    this.data.hito.fecha = this.formGroup.get('fechaInicio').value;
    this.data.hito.tipoHito = this.formGroup.get('tipoHito').value;
    this.data.hito.generaAviso = this.formGroup.get('aviso').value ? this.formGroup.get('aviso').value : false;
  }

}
