import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatAutocompleteTrigger } from '@angular/material/autocomplete';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IGrupoEquipoInstrumental } from '@core/models/csp/grupo-equipo-instrumental';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const GRUPO_EQUIPO_INSTRUMENTAL_NUM_REGISTRO_KEY = marker('csp.grupo-equipo-instrumental.numero-registro');
const GRUPO_EQUIPO_INSTRUMENTAL_NOMBRE_KEY = marker('csp.grupo-equipo-instrumental.nombre');
const GRUPO_EQUIPO_INSTRUMENTAL_DESCRIPCION_KEY = marker('csp.grupo-equipo-instrumental.descripcion');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface GrupoEquipoInstrumentalModalData {
  titleEntity: string;
  selectedEntidades: IGrupoEquipoInstrumental[];
  entidad: IGrupoEquipoInstrumental;
  isEdit: boolean;
}

@Component({
  templateUrl: './grupo-equipo-instrumental-modal.component.html',
  styleUrls: ['./grupo-equipo-instrumental-modal.component.scss']
})
export class GrupoEquipoInstrumentalModalComponent extends
  BaseModalComponent<GrupoEquipoInstrumentalModalData, GrupoEquipoInstrumentalModalComponent> implements OnInit {

  @ViewChild(MatAutocompleteTrigger) autocomplete: MatAutocompleteTrigger;
  fxLayoutProperties: FxLayoutProperties;

  textSaveOrUpdate: string;

  saveDisabled = false;

  msgParamNumRegistroEntity = {};
  msgParamNombreEntity = {};
  msgParamDescripcionEntity = {};
  title: string;

  constructor(
    protected snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<GrupoEquipoInstrumentalModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: GrupoEquipoInstrumentalModalData,
    private readonly translate: TranslateService) {
    super(snackBarService, matDialogRef, data);
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.xs = 'row';

  }

  ngOnInit(): void {
    super.ngOnInit();

    this.setupI18N();

    this.textSaveOrUpdate = this.data?.isEdit ? MSG_ACEPTAR : MSG_ANADIR;
  }

  private setupI18N(): void {
    this.translate.get(
      GRUPO_EQUIPO_INSTRUMENTAL_NUM_REGISTRO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNumRegistroEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      GRUPO_EQUIPO_INSTRUMENTAL_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      GRUPO_EQUIPO_INSTRUMENTAL_DESCRIPCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamDescripcionEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

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

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup(
      {
        nombre: new FormControl(this.data?.entidad?.nombre, [Validators.required, Validators.maxLength(100)]),
        descripcion: new FormControl(this.data?.entidad?.descripcion, Validators.maxLength(250)),
        numRegistro: new FormControl(this.data?.entidad?.numRegistro, Validators.maxLength(50)),
      }
    );

    return formGroup;
  }

  protected getDatosForm(): GrupoEquipoInstrumentalModalData {
    this.data.entidad.nombre = this.formGroup.get('nombre').value;
    this.data.entidad.descripcion = this.formGroup.get('descripcion').value;
    this.data.entidad.numRegistro = this.formGroup.get('numRegistro').value;
    return this.data;
  }

}
