import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogActionComponent } from '@core/component/dialog-action.component';
import { MSG_PARAMS } from '@core/i18n';
import { ITipoGrupo } from '@core/models/csp/tipo-grupo';
import { TipoGrupoService } from '@core/services/csp/tipo-grupo/tipo-grupo.service';
import { I18nValidators } from '@core/validators/i18n-validator';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const TIPO_GRUPO_KEY = marker('csp.tipo-grupo');
const TIPO_GRUPO_NOMBRE_KEY = marker('csp.tipo-grupo.nombre');
const TIPO_GRUPO_DESCRIPCION_KEY = marker('csp.tipo-grupo.descripcion');
const TITLE_NEW_ENTITY = marker('title.new.entity');

@Component({
  templateUrl: './tipo-grupo-modal.component.html',
  styleUrls: ['./tipo-grupo-modal.component.scss']
})
export class TipoGrupoModalComponent extends DialogActionComponent<ITipoGrupo> implements OnInit, OnDestroy {

  private readonly tipoGrupo: ITipoGrupo;
  title: string;
  msgParamNombreEntity = {};
  msgParamDescripcionEntity = {};

  constructor(
    matDialogRef: MatDialogRef<TipoGrupoModalComponent>,
    @Inject(MAT_DIALOG_DATA) data: ITipoGrupo,
    private readonly tipoGrupoService: TipoGrupoService,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data?.id);

    if (this.isEdit()) {
      this.tipoGrupo = { ...data };
    } else {
      this.tipoGrupo = { activo: true } as ITipoGrupo;
    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  protected getValue(): ITipoGrupo {
    this.tipoGrupo.nombre = this.formGroup.controls.nombre.value;
    this.tipoGrupo.descripcion = this.formGroup.controls.descripcion.value;
    return this.tipoGrupo;
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup({
      nombre: new FormControl(this.tipoGrupo?.nombre ?? [], [I18nValidators.maxLength(50), I18nValidators.required]),
      descripcion: new FormControl(this.tipoGrupo?.descripcion ?? [], [I18nValidators.maxLength(250)]),
    });
  }

  protected saveOrUpdate(): Observable<ITipoGrupo> {
    const tipoGrupo = this.getValue();
    return this.isEdit() ? this.tipoGrupoService.update(tipoGrupo.id, tipoGrupo) :
      this.tipoGrupoService.create(tipoGrupo);
  }

  private setupI18N(): void {
    this.translate.get(
      TIPO_GRUPO_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      TIPO_GRUPO_DESCRIPCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamDescripcionEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.isEdit()) {
      this.translate.get(
        TIPO_GRUPO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        TIPO_GRUPO_KEY,
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
