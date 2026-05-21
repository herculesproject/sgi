import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, ValidatorFn, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IGrupoRelacionInstitucional } from '@core/models/csp/grupo-relacion-institucional';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StringValidator } from '@core/validators/string-validator';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const GRUPO_RELACION_INSTITUCIONAL_ENTIDAD_KEY = marker('csp.grupo-relacion-institucional.entidad');
const GRUPO_RELACION_INSTITUCIONAL_INSTITUCION_KEY = marker('csp.grupo-relacion-institucional.institucion');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export const TIPO_INTRODUCCION_ENTIDAD = {
  SGE: 'SGE',
  MANUAL: 'MANUAL'
};

export interface GrupoRelacionInstitucionalModalData {
  titleEntity: string;
  relacionInstitucional: IGrupoRelacionInstitucional;
  relacionesExistentes: IGrupoRelacionInstitucional[];
  isEdit: boolean;
}

@Component({
  templateUrl: './grupo-relacion-institucional-modal.component.html',
  styleUrls: ['./grupo-relacion-institucional-modal.component.scss']
})
export class GrupoRelacionInstitucionalModalComponent
  extends DialogFormComponent<GrupoRelacionInstitucionalModalData> implements OnInit {

  textSaveOrUpdate: string;
  title: string;
  msgParamEntidad = {};
  msgParamInstitucion = {};

  readonly TIPO_INTRODUCCION_ENTIDAD = TIPO_INTRODUCCION_ENTIDAD;

  get tipoIntroduccion(): string {
    return this.formGroup?.get('tipoIntroduccion').value;
  }

  constructor(
    protected snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<GrupoRelacionInstitucionalModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: GrupoRelacionInstitucionalModalData,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data?.isEdit);
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.setupI18N();

    this.textSaveOrUpdate = this.data?.isEdit ? MSG_ACEPTAR : MSG_ANADIR;

    this.subscriptions.push(
      this.formGroup.get('tipoIntroduccion').valueChanges.subscribe((tipo) => this.onTipoIntroduccionChange(tipo))
    );
  }

  private setupI18N(): void {
    this.translate.get(
      GRUPO_RELACION_INSTITUCIONAL_ENTIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntidad = {
      entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });

    this.translate.get(
      GRUPO_RELACION_INSTITUCIONAL_INSTITUCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamInstitucion = {
      entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
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
            { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
          );
        })
      ).subscribe((value) => this.title = value);
    }
  }

  protected buildFormGroup(): FormGroup {
    const tipoIntroduccionInicial = this.data?.relacionInstitucional?.institucion
      ? TIPO_INTRODUCCION_ENTIDAD.MANUAL
      : TIPO_INTRODUCCION_ENTIDAD.SGE;

    const formGroup = new FormGroup(
      {
        tipoIntroduccion: new FormControl(tipoIntroduccionInicial),
        entidad: new FormControl(this.data?.relacionInstitucional?.entidad ?? null,
          tipoIntroduccionInicial === TIPO_INTRODUCCION_ENTIDAD.SGE
            ? [Validators.required, this.entidadDuplicatedValidator()]
            : []),
        institucion: new FormControl(this.data?.relacionInstitucional?.institucion ?? null,
          tipoIntroduccionInicial === TIPO_INTRODUCCION_ENTIDAD.MANUAL
            ? [Validators.required, Validators.maxLength(1000), this.institucionDuplicatedValidator()]
            : []),
      }
    );

    return formGroup;
  }

  protected getValue(): GrupoRelacionInstitucionalModalData {
    const tipo = this.formGroup.get('tipoIntroduccion').value;

    if (tipo === TIPO_INTRODUCCION_ENTIDAD.SGE) {
      this.data.relacionInstitucional.entidad = this.formGroup.get('entidad').value as IEmpresa;
      this.data.relacionInstitucional.institucion = null;
    } else {
      this.data.relacionInstitucional.entidad = null;
      this.data.relacionInstitucional.institucion = this.formGroup.get('institucion').value;
    }

    return this.data;
  }

  private onTipoIntroduccionChange(tipo: string): void {
    const entidadCtrl = this.formGroup.get('entidad');
    const institucionCtrl = this.formGroup.get('institucion');

    if (tipo === TIPO_INTRODUCCION_ENTIDAD.SGE) {
      entidadCtrl.setValidators([Validators.required, this.entidadDuplicatedValidator()]);
      institucionCtrl.setValidators([]);
      institucionCtrl.setValue(null, { emitEvent: false });
    } else {
      institucionCtrl.setValidators([Validators.required, Validators.maxLength(1000), this.institucionDuplicatedValidator()]);
      entidadCtrl.setValidators([]);
      entidadCtrl.setValue(null, { emitEvent: false });
    }

    entidadCtrl.updateValueAndValidity();
    institucionCtrl.updateValueAndValidity();
  }

  private entidadDuplicatedValidator(): ValidatorFn {
    const entidadIds = (this.data?.relacionesExistentes ?? [])
      .map(relacion => relacion.entidad?.id)
      .filter(id => !!id);
    return StringValidator.notIn(entidadIds, (empresa: IEmpresa) => empresa?.id);
  }

  private institucionDuplicatedValidator(): ValidatorFn {
    const institucionesExistentes = (this.data?.relacionesExistentes ?? [])
      .map(relacion => relacion.institucion)
      .filter(institucion => !!institucion);
    return StringValidator.notInIgnoreCaseAndAccent(institucionesExistentes);
  }

}
