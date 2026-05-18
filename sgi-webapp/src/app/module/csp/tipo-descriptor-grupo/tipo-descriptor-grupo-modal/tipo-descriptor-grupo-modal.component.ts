import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogActionComponent } from '@core/component/dialog-action.component';
import { MSG_PARAMS } from '@core/i18n';
import { ITipoDescriptorGrupo } from '@core/models/csp/tipo-descriptor-grupo';
import { TipoDescriptorGrupoService } from '@core/services/csp/tipo-descriptor-grupo/tipo-descriptor-grupo.service';
import { I18nValidators } from '@core/validators/i18n-validator';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const TIPO_DESCRIPTOR_GRUPO_KEY = marker('csp.tipo-descriptor-grupo');
const TIPO_DESCRIPTOR_GRUPO_NOMBRE_KEY = marker('csp.tipo-descriptor-grupo.nombre');
const TITLE_NEW_ENTITY = marker('title.new.entity');

@Component({
  templateUrl: './tipo-descriptor-grupo-modal.component.html',
  styleUrls: ['./tipo-descriptor-grupo-modal.component.scss']
})
export class TipoDescriptorGrupoModalComponent extends DialogActionComponent<ITipoDescriptorGrupo> implements OnInit, OnDestroy {

  private readonly tipoDescriptorGrupo: ITipoDescriptorGrupo;
  title: string;
  msgParamNombreEntity = {};

  constructor(
    matDialogRef: MatDialogRef<TipoDescriptorGrupoModalComponent>,
    @Inject(MAT_DIALOG_DATA) data: ITipoDescriptorGrupo,
    private readonly tipoDescriptorGrupoService: TipoDescriptorGrupoService,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data?.id);

    if (this.isEdit()) {
      this.tipoDescriptorGrupo = { ...data };
    } else {
      this.tipoDescriptorGrupo = { activo: true } as ITipoDescriptorGrupo;
    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  protected getValue(): ITipoDescriptorGrupo {
    this.tipoDescriptorGrupo.nombre = this.formGroup.controls.nombre.value;
    return this.tipoDescriptorGrupo;
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      nombre: new FormControl(this.tipoDescriptorGrupo?.nombre ?? [], [I18nValidators.maxLength(50), I18nValidators.required]),
    });
    return formGroup;
  }

  protected saveOrUpdate(): Observable<ITipoDescriptorGrupo> {
    const tipoDescriptorGrupo = this.getValue();
    return this.isEdit() ? this.tipoDescriptorGrupoService.update(tipoDescriptorGrupo.id, tipoDescriptorGrupo) :
      this.tipoDescriptorGrupoService.create(tipoDescriptorGrupo);
  }

  private setupI18N(): void {
    this.translate.get(
      TIPO_DESCRIPTOR_GRUPO_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.isEdit()) {
      this.translate.get(
        TIPO_DESCRIPTOR_GRUPO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        TIPO_DESCRIPTOR_GRUPO_KEY,
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
