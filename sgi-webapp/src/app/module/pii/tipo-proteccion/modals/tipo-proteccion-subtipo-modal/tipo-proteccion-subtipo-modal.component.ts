import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { ITipoProteccion } from '@core/models/pii/tipo-proteccion';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';

const SUBTIPO_PROTECCION_KEY = marker('pii.subtipo-proteccion');
const SUBTIPO_PROTECCION_NOMBRE = marker('pii.tipo-proteccion.nombre');
const SUBTIPO_PROTECCION_DESCRIPCION = marker('pii.tipo-proteccion.descripcion');
const MSG_ACEPTAR = marker('btn.ok');

@Component({
  selector: 'sgi-tipo-proteccion-subtipo-modal',
  templateUrl: './tipo-proteccion-subtipo-modal.component.html',
  styleUrls: ['./tipo-proteccion-subtipo-modal.component.scss']
})
export class TipoProteccionSubtipoModalComponent
  extends BaseModalComponent<StatusWrapper<ITipoProteccion>, TipoProteccionSubtipoModalComponent> implements OnInit, OnDestroy {

  subtipoProteccion: StatusWrapper<ITipoProteccion>;

  msgParamNombreEntity = {};
  msgParamDescripcionEntity = {};
  title: string;

  textSaveOrUpdate: string;

  constructor(
    protected readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<TipoProteccionSubtipoModalComponent>,
    @Inject(MAT_DIALOG_DATA) subtipoProteccion: StatusWrapper<ITipoProteccion>,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, subtipoProteccion);
    if (subtipoProteccion) {
      this.subtipoProteccion = subtipoProteccion;
    } else {
      this.subtipoProteccion = new StatusWrapper<ITipoProteccion>({ activo: true } as ITipoProteccion);
    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      SUBTIPO_PROTECCION_NOMBRE,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SUBTIPO_PROTECCION_DESCRIPCION,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamDescripcionEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SUBTIPO_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.title = value);

    this.textSaveOrUpdate = MSG_ACEPTAR;
  }

  protected getDatosForm(): StatusWrapper<ITipoProteccion> {
    if (this.formGroup.touched) {
      this.subtipoProteccion.value.nombre = this.formGroup.controls.nombre.value;
      this.subtipoProteccion.value.descripcion = this.formGroup.controls.descripcion.value;
      this.subtipoProteccion.setEdited();
    }
    return this.subtipoProteccion;
  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      nombre: new FormControl(this.subtipoProteccion?.value.nombre, [Validators.maxLength(50)]),
      descripcion: new FormControl(this.subtipoProteccion?.value.descripcion, [Validators.maxLength(250)]),
    });
    return formGroup;
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
  }

}
