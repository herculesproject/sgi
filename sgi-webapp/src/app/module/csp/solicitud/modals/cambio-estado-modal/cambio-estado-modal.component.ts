import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { Estado, ESTADO_MAP } from '@core/models/csp/estado-solicitud';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { TranslateService } from '@ngx-translate/core';

const MSG_ERROR_FORM_GROUP = marker('error.form-group');
const SOLICITUD_CAMBIO_ESTADO_COMENTARIO = marker('csp.solicitud.estado-solicitud.comentario');

export interface SolicitudCambioEstadoModalComponentData {
  estadoActual: Estado;
  estadoNuevo: Estado;
  comentario: string;
}
@Component({
  selector: 'sgi-cambio-estado-modal',
  templateUrl: './cambio-estado-modal.component.html',
  styleUrls: ['./cambio-estado-modal.component.scss']
})
export class CambioEstadoModalComponent implements OnInit {

  formGroup: FormGroup;

  fxFlexProperties: FxFlexProperties;
  fxFlexProperties2: FxFlexProperties;
  fxFlexProperties3: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  msgParamComentarioEntity = {};


  get ESTADO_MAP() {
    return ESTADO_MAP;
  }

  constructor(public matDialogRef: MatDialogRef<SolicitudCambioEstadoModalComponentData>,
    @Inject(MAT_DIALOG_DATA) public data: SolicitudCambioEstadoModalComponentData,
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
      estadoActual: new FormControl(this.data.estadoActual),
      estadoNuevo: new FormControl(this.data.estadoNuevo),
      comentario: new FormControl('', [Validators.maxLength(2000), Validators.required])
    });
  }

  private setupI18N(): void {
    this.translate.get(
      SOLICITUD_CAMBIO_ESTADO_COMENTARIO,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamComentarioEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });
  }


  /**
 * Cierra la ventana modal.
 *
 */
  closeModal(comentario?: string): void {
    this.matDialogRef.close(comentario);
  }


  saveOrUpdate(): void {
    if (FormGroupUtil.valid(this.formGroup)) {
      this.closeModal(this.formGroup.get('comentario').value);
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM_GROUP);
    }
  }

}
