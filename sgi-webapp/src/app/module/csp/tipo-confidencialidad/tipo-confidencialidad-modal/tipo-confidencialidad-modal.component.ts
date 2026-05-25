import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogActionComponent } from '@core/component/dialog-action.component';
import { MSG_PARAMS } from '@core/i18n';
import { ITipoConfidencialidad } from '@core/models/csp/tipo-confidencialidad';
import { TipoConfidencialidadService } from '@core/services/csp/tipo-confidencialidad/tipo-confidencialidad.service';
import { I18nValidators } from '@core/validators/i18n-validator';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const TIPO_CONFIDENCIALIDAD_KEY = marker('csp.tipo-confidencialidad');
const TIPO_CONFIDENCIALIDAD_NOMBRE_KEY = marker('csp.tipo-confidencialidad.nombre');
const TITLE_NEW_ENTITY = marker('title.new.entity');

@Component({
  templateUrl: './tipo-confidencialidad-modal.component.html',
  styleUrls: ['./tipo-confidencialidad-modal.component.scss']
})
export class TipoConfidencialidadModalComponent extends DialogActionComponent<ITipoConfidencialidad> implements OnInit, OnDestroy {

  private readonly tipoConfidencialidad: ITipoConfidencialidad;
  title: string;
  msgParamNombreEntity = {};

  constructor(
    matDialogRef: MatDialogRef<TipoConfidencialidadModalComponent>,
    @Inject(MAT_DIALOG_DATA) data: ITipoConfidencialidad,
    private readonly tipoConfidencialidadService: TipoConfidencialidadService,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data?.id);

    if (this.isEdit()) {
      this.tipoConfidencialidad = { ...data };
    } else {
      this.tipoConfidencialidad = { activo: true } as ITipoConfidencialidad;
    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  protected getValue(): ITipoConfidencialidad {
    this.tipoConfidencialidad.nombre = this.formGroup.controls.nombre.value;
    return this.tipoConfidencialidad;
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      nombre: new FormControl(this.tipoConfidencialidad?.nombre ?? [], [I18nValidators.maxLength(50), I18nValidators.required]),
    });
    return formGroup;
  }

  protected saveOrUpdate(): Observable<ITipoConfidencialidad> {
    const tipoConfidencialidad = this.getValue();
    return this.isEdit() ? this.tipoConfidencialidadService.update(tipoConfidencialidad.id, tipoConfidencialidad) :
      this.tipoConfidencialidadService.create(tipoConfidencialidad);
  }

  private setupI18N(): void {
    this.translate.get(
      TIPO_CONFIDENCIALIDAD_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.isEdit()) {
      this.translate.get(
        TIPO_CONFIDENCIALIDAD_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        TIPO_CONFIDENCIALIDAD_KEY,
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

}
