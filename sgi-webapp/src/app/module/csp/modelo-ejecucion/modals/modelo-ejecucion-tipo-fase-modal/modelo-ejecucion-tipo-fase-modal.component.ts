import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IModeloTipoFase } from '@core/models/csp/modelo-tipo-fase';
import { ITipoFase } from '@core/models/csp/tipos-configuracion';
import { TipoFaseService } from '@core/services/csp/tipo-fase.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { requiredChecked } from '@core/validators/checkbox-validator';
import { TranslateService } from '@ngx-translate/core';
import { SgiRestListResult } from '@sgi/framework/http';
import { Observable, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const MODELO_EJECUCION_TIPO_FASE_KEY = marker('csp.tipo-fase');
const MODELO_EJECUCION_TIPO_FASE_TIPO_KEY = marker('csp.modelo-ejecucion-tipo-fase.tipo');
const MODELO_EJECUCION_TIPO_FASE_PROYECTO_KEY = marker('csp.modelo-ejecucion-tipo-fase.proyecto');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface ModeloEjecucionTipoFaseModalData {
  modeloTipoFase: IModeloTipoFase;
  tipoFases: ITipoFase[];
}

@Component({
  templateUrl: './modelo-ejecucion-tipo-fase-modal.component.html',
  styleUrls: ['./modelo-ejecucion-tipo-fase-modal.component.scss']
})
export class ModeloEjecucionTipoFaseModalComponent extends
  BaseModalComponent<IModeloTipoFase, ModeloEjecucionTipoFaseModalComponent> implements OnInit {
  tipoFases$: Observable<ITipoFase[]>;

  textSaveOrUpdate: string;
  title: string;
  msgParamTipoEntiy = {};
  msgParamProyectoEntiy = {};

  constructor(
    protected snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<ModeloEjecucionTipoFaseModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ModeloEjecucionTipoFaseModalData,
    private tipoFaseService: TipoFaseService,
    protected dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, data.modeloTipoFase);
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.setupI18N();

    if (!this.data.modeloTipoFase.tipoFase) {
      this.tipoFases$ = this.tipoFaseService.findAll().pipe(
        switchMap((result) => {
          const list = this.filterExistingTipoFase(result);
          return of(list);
        })
      );
      this.textSaveOrUpdate = MSG_ANADIR;

      this.translate.get(
        MODELO_EJECUCION_TIPO_FASE_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            TITLE_NEW_ENTITY,
            { entity: value, ...MSG_PARAMS.GENDER.MALE }
          );
        })
      ).subscribe((value) => this.title = value);
    } else {
      this.tipoFases$ = this.tipoFaseService.findAll().pipe(
        switchMap((result) => of(result.items))
      );
      this.textSaveOrUpdate = MSG_ACEPTAR;

      this.translate.get(
        MODELO_EJECUCION_TIPO_FASE_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    }
  }

  private setupI18N(): void {
    this.translate.get(
      MODELO_EJECUCION_TIPO_FASE_PROYECTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamProyectoEntiy = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      MODELO_EJECUCION_TIPO_FASE_TIPO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTipoEntiy = { entity: value, ...MSG_PARAMS.GENDER.MALE });
  }

  /**
   * Comprueba que no hay 2 valores en la lista
   *
   * @param result resultado
   */
  private filterExistingTipoFase(result: SgiRestListResult<ITipoFase>): ITipoFase[] {
    return result.items.filter((tipoFase: ITipoFase) => {
      return this.data.tipoFases.find((currentTipo) => currentTipo.id === tipoFase.id) ? false : true;
    });
  }

  protected getDatosForm(): IModeloTipoFase {
    const modeloTipoFase = this.data.modeloTipoFase;
    const disponible = this.formGroup.controls.disponible as FormGroup;
    modeloTipoFase.tipoFase = this.formGroup.get('tipoFase').value;
    modeloTipoFase.convocatoria = disponible.get('convocatoria').value;
    modeloTipoFase.proyecto = disponible.get('proyecto').value;
    modeloTipoFase.solicitud = false;
    return modeloTipoFase;
  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      tipoFase: new FormControl({
        value: this.data.modeloTipoFase?.tipoFase,
        disabled: this.data.modeloTipoFase.tipoFase
      }, Validators.required),
      disponible: new FormGroup({
        convocatoria: new FormControl(this.data.modeloTipoFase?.convocatoria),
        proyecto: new FormControl(this.data.modeloTipoFase?.proyecto)
      }, [requiredChecked(1)]),
    });
    return formGroup;
  }

  equals(o1: ITipoFase, o2: ITipoFase): boolean {
    return o1?.id === o2?.id;
  }
}
