import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IModeloUnidad } from '@core/models/csp/modelo-unidad';
import { ITipoUnidadGestion } from '@core/models/csp/tipos-configuracion';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiRestListResult } from '@sgi/framework/http';
import { Observable, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const MODELO_EJECUCION_UNIDAD_GESTION_KEY = marker('csp.modelo-ejecucion-unidad-gestion');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface IModeloEjecucionTipoUnidadModal {
  modeloTipoUnidad: IModeloUnidad;
  tipoUnidades: ITipoUnidadGestion[];
}

@Component({
  selector: 'sgi-modelo-ejecucion-tipo-unidad-gestion',
  templateUrl: './modelo-ejecucion-tipo-unidad-gestion-modal.component.html',
  styleUrls: ['./modelo-ejecucion-tipo-unidad-gestion-modal.component.scss']
})
export class ModeloEjecucionTipoUnidadGestionModalComponent extends
  BaseModalComponent<IModeloUnidad, ModeloEjecucionTipoUnidadGestionModalComponent> implements OnInit {
  tipoUnidad$: Observable<ITipoUnidadGestion[]>;

  msgParamUnidadGestionEntiy = {};
  title: string;

  constructor(
    protected readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<ModeloEjecucionTipoUnidadGestionModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: IModeloEjecucionTipoUnidadModal,
    private readonly unidadGestionService: UnidadGestionService,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, data.modeloTipoUnidad);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.tipoUnidad$ = this.unidadGestionService.findAll().pipe(
      switchMap((result: SgiRestListResult<ITipoUnidadGestion>) => {
        const list = this.filterExistingTipoUnidadGestion(result);
        return of(list);
      })
    );

  }

  private setupI18N(): void {
    this.translate.get(
      MODELO_EJECUCION_UNIDAD_GESTION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamUnidadGestionEntiy = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      MODELO_EJECUCION_UNIDAD_GESTION_KEY,
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


  private filterExistingTipoUnidadGestion(result: SgiRestListResult<ITipoUnidadGestion>): ITipoUnidadGestion[] {
    return result.items.filter((tipoUnidadGestion: ITipoUnidadGestion) => {
      return !this.data.tipoUnidades.find((currentTipo) => currentTipo.id === tipoUnidadGestion.id);
    });
  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      tipoUnidad: new FormControl(this.data.modeloTipoUnidad?.unidadGestion)
    });
    return formGroup;
  }

  protected getDatosForm(): IModeloUnidad {
    const modeloTipoUnidadGestion = this.data.modeloTipoUnidad;
    modeloTipoUnidadGestion.unidadGestion = this.formGroup.get('tipoUnidad').value;
    return modeloTipoUnidadGestion;
  }
}
