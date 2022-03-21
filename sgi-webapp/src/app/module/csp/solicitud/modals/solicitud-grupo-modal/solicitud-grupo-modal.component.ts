import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IGrupo } from '@core/models/csp/grupo';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';

const MSG_ACEPTAR = marker('btn.ok');
const GRUPO_FECHA_INICIO_KEY = marker('label.fecha-inicio');
const GRUPO_FECHA_FIN_KEY = marker('label.fecha-fin');
const GRUPO_NOMBRE_KEY = marker('csp.solicitud.grupo.nombre');
const GRUPO_KEY = marker('csp.grupo');

@Component({
  selector: 'sgi-solicitud-grupo-modal',
  templateUrl: './solicitud-grupo-modal.component.html',
  styleUrls: ['./solicitud-grupo-modal.component.scss']
})
export class SolicitudGrupoModalComponent
  extends BaseModalComponent<IGrupo, SolicitudGrupoModalComponent> implements OnInit {

  textSaveOrUpdate: string;

  msgParamFechaFinEntity = {};
  msgParamFechaInicioEntity = {};
  msgParamNombreEntity = {};
  msgParamGrupoEntity = {};

  constructor(
    protected snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<SolicitudGrupoModalComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: IGrupo,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, data);
    this.textSaveOrUpdate = MSG_ACEPTAR;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

  }

  private setupI18N(): void {
    this.translate.get(
      GRUPO_FECHA_INICIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaInicioEntity =
      { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      GRUPO_FECHA_FIN_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaFinEntity =
      { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      GRUPO_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity =
      { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      GRUPO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamGrupoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
  }

  protected getFormGroup(): FormGroup {
    return new FormGroup(
      {
        nombre: new FormControl(null, [Validators.required, Validators.maxLength(250)]),
        fechaInicio: new FormControl(null, [Validators.required]),
        fechaFin: new FormControl(null, [Validators.required]),
      },
    );
  }

  protected getDatosForm(): IGrupo {
    return {
      nombre: this.formGroup.controls.nombre.value,
      fechaInicio: this.formGroup.controls.fechaInicio.value,
      fechaFin: this.formGroup.controls.fechaFin.value,
    } as IGrupo;
  }


}
