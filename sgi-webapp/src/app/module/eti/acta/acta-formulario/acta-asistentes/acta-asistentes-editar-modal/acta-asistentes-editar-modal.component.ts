import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IAsistente } from '@core/models/eti/asistente';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';

const MSG_ERROR_FORM = marker('error.form-group');
const ACTA_ASISTENTE_KEY = marker('eti.acta.asistente');
const ACTA_ASISTENTE_MOTIVO_KEY = marker('eti.acta.asistente.motivo');
const TITLE_NEW_ENTITY = marker('title.new.entity');

@Component({
  templateUrl: './acta-asistentes-editar-modal.component.html',
  styleUrls: ['./acta-asistentes-editar-modal.component.scss']
})
export class ActaAsistentesEditarModalComponent implements OnInit {

  FormGroupUtil = FormGroupUtil;
  formGroup: FormGroup;
  fxLayoutProperties: FxLayoutProperties;

  ocultarMotivo: boolean;

  estados =
    [
      { label: 'Sí', value: true },
      { label: 'No', value: false }
    ];

  msgParamMotivoEntity = {};
  title: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    public readonly matDialogRef: MatDialogRef<ActaAsistentesEditarModalComponent>,
    @Inject(MAT_DIALOG_DATA) public asistente: IAsistente,
    private readonly snackBarService: SnackBarService,
    private readonly translate: TranslateService
  ) {
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.layout = 'column';
  }

  ngOnInit(): void {
    this.initFormGroup();
    this.setupI18N();

    this.activarMotivo(this.asistente.asistencia);
  }

  private setupI18N(): void {
    this.translate.get(
      ACTA_ASISTENTE_MOTIVO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamMotivoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    if (this.asistente.asistencia) {
      this.translate.get(
        ACTA_ASISTENTE_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);

    } else {
      this.translate.get(
        ACTA_ASISTENTE_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            TITLE_NEW_ENTITY,
            { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
          );
        })
      ).subscribe((value) => this.title = value);
    }
  }

  /**
   * Inicializa el formGroup
   */
  private initFormGroup() {
    this.formGroup = new FormGroup({
      asistente: new FormControl(
        this.asistente?.evaluador?.persona?.nombre + ' ' + this.asistente?.evaluador?.persona?.apellidos,
        [Validators.required]
      ),
      asistencia: new FormControl(this.asistente.asistencia, [Validators.required]),
      motivo: new FormControl(this.asistente.motivo),
    });
    this.formGroup.controls.asistente.disable();
  }

  /**
   * Cierra la ventana modal y devuelve el asistencia si se ha modificado
   *
   * @param asistente asistencia modificada
   */
  closeModal(asistente?: IAsistente): void {
    this.matDialogRef.close(asistente);
  }

  /**
   * Comprueba el formulario y envia el asistente resultante
   */
  editComentario() {
    if (FormGroupUtil.valid(this.formGroup)) {
      this.closeModal(this.getDatosForm());
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM);
    }
  }

  /**
   * Método para actualizar la entidad con los datos de un formGroup
   */
  private getDatosForm(): IAsistente {
    const asistente = this.asistente;
    if (this.ocultarMotivo) {
      this.formGroup.controls.motivo.setValue('');
      asistente.motivo = '';
    } else {
      asistente.motivo = FormGroupUtil.getValue(this.formGroup, 'motivo');
    }
    asistente.asistencia = FormGroupUtil.getValue(this.formGroup, 'asistencia');
    return asistente;
  }

  /**
   * Recupera el valor de la asistencia
   * @param value valor del radio button seleccionado.
   */
  activarMotivo(value: boolean): void {
    if (value) {
      this.ocultarMotivo = true;
    } else {
      this.ocultarMotivo = false;
    }
  }
}
