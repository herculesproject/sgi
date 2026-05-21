import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatAutocompleteTrigger } from '@angular/material/autocomplete';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IGrupoDescriptor } from '@core/models/csp/grupo-descriptor';
import { SnackBarService } from '@core/services/snack-bar.service';
import { I18nValidators } from '@core/validators/i18n-validator';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const GRUPO_DESCRIPTOR_TIPO_KEY = marker('csp.grupo-descriptor.tipo-descriptor-grupo');
const GRUPO_DESCRIPTOR_TEXTO_KEY = marker('csp.grupo-descriptor.texto');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface GrupoDescriptorModalData {
  titleEntity: string;
  descriptor: IGrupoDescriptor;
  isEdit: boolean;
}

@Component({
  templateUrl: './grupo-descriptor-modal.component.html',
  styleUrls: ['./grupo-descriptor-modal.component.scss']
})
export class GrupoDescriptorModalComponent extends DialogFormComponent<GrupoDescriptorModalData> implements OnInit {

  @ViewChild(MatAutocompleteTrigger) autocomplete: MatAutocompleteTrigger;

  textSaveOrUpdate: string;

  msgParamTipoDescriptorEntity = {};
  msgParamTextoEntity = {};
  title: string;

  constructor(
    protected snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<GrupoDescriptorModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: GrupoDescriptorModalData,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data?.isEdit);
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.setupI18N();

    this.textSaveOrUpdate = this.data?.isEdit ? MSG_ACEPTAR : MSG_ANADIR;
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup(
      {
        tipoDescriptorGrupo: new FormControl(this.data?.descriptor?.tipoDescriptorGrupo, [Validators.required]),
        texto: new FormControl(this.data?.descriptor?.texto ?? [], [I18nValidators.required, I18nValidators.maxLength(3000)]),
      }
    );

    return formGroup;
  }

  protected getValue(): GrupoDescriptorModalData {
    this.data.descriptor.tipoDescriptorGrupo = this.formGroup.get('tipoDescriptorGrupo').value;
    this.data.descriptor.texto = this.formGroup.get('texto').value;
    return this.data;
  }

  private setupI18N(): void {
    this.translate.get(
      GRUPO_DESCRIPTOR_TIPO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTipoDescriptorEntity = {
      entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });

    this.translate.get(
      GRUPO_DESCRIPTOR_TEXTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTextoEntity = {
      entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });

    if (this.data?.isEdit) {
      this.translate.get(
        this.data.titleEntity,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        this.data.titleEntity,
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
