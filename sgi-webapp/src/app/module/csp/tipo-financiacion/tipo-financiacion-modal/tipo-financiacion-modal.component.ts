import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { ITipoFinanciacion } from '@core/models/csp/tipos-configuracion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';

const MSG_ERROR_FORM_GROUP = marker('error.form-group');
const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const TIPO_FINANCIACION_KEY = marker('csp.tipo-financiacion');
const TIPO_FINANCIACION_NOMBRE_KEY = marker('csp.tipo-financiacion.nombre');
const TITLE_NEW_ENTITY = marker('title.new.entity');
@Component({
  selector: 'sgi-tipo-financiacion-modal',
  templateUrl: './tipo-financiacion-modal.component.html',
  styleUrls: ['./tipo-financiacion-modal.component.scss']
})
export class TipoFinanciacionModalComponent implements OnInit {
  formGroup: FormGroup;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexProperties: FxFlexProperties;
  public tipoFinanciacion: ITipoFinanciacion;
  textSaveOrUpdate: string;

  msgParamNombreEntity = {};
  title: string;

  constructor(
    private readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<TipoFinanciacionModalComponent>,
    @Inject(MAT_DIALOG_DATA) tipoFinanciacion: ITipoFinanciacion,
    private readonly translate: TranslateService

  ) {
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.layoutAlign = 'row';
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties.order = '2';
    if (tipoFinanciacion.id) {
      this.tipoFinanciacion = { ...tipoFinanciacion };
      this.textSaveOrUpdate = MSG_ACEPTAR;
    } else {
      this.tipoFinanciacion = { activo: true } as ITipoFinanciacion;
      this.textSaveOrUpdate = MSG_ANADIR;
    }
  }

  ngOnInit(): void {
    this.setupI18N();
    this.formGroup = new FormGroup({
      nombre: new FormControl(this.tipoFinanciacion?.nombre),
      descripcion: new FormControl(this.tipoFinanciacion?.descripcion),
    });
  }

  private setupI18N(): void {
    this.translate.get(
      TIPO_FINANCIACION_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    if (this.tipoFinanciacion.nombre) {

      this.translate.get(
        TIPO_FINANCIACION_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        TIPO_FINANCIACION_KEY,
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


  closeModal(tipoFinanciacion?: ITipoFinanciacion): void {
    this.matDialogRef.close(tipoFinanciacion);
  }

  saveOrUpdate(): void {
    if (FormGroupUtil.valid(this.formGroup)) {
      this.loadDatosForm();
      this.closeModal(this.tipoFinanciacion);
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM_GROUP);
    }
  }

  /**
   * Método para actualizar la entidad con los datos de un formGroup
   */
  private loadDatosForm(): void {
    this.tipoFinanciacion.nombre = this.formGroup.get('nombre').value;
    this.tipoFinanciacion.descripcion = this.formGroup.get('descripcion').value;
  }
}
