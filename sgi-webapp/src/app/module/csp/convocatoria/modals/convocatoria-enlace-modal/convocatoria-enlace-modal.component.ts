import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaEnlace } from '@core/models/csp/convocatoria-enlace';
import { IModeloTipoEnlace } from '@core/models/csp/modelo-tipo-enlace';
import { ITipoEnlace } from '@core/models/csp/tipos-configuracion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ModeloEjecucionService } from '@core/services/csp/modelo-ejecucion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { StringValidator } from '@core/validators/string-validator';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { map, startWith, switchMap } from 'rxjs/operators';

const MSG_ERROR_INIT = marker('error.load');
const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const CONVOCATORIA_ENLACE_KEY = marker('csp.convocatoria-enlace');
const CONVOCATORIA_ENLACE_URL_KEY = marker('csp.convocatoria-enlace.url');
const CONVOCATORIA_ENLACE_DESCRIPCION_KEY = marker('csp.convocatoria-enlace.descripcion');
const CONVOCATORIA_ENLACE_TIPO_KEY = marker('csp.convocatoria-enlace.tipo-enlace');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface ConvocatoriaEnlaceModalComponentData {
  enlace: IConvocatoriaEnlace;
  idModeloEjecucion: number;
  selectedUrls: string[];
  readonly: boolean;
}
@Component({
  templateUrl: './convocatoria-enlace-modal.component.html',
  styleUrls: ['./convocatoria-enlace-modal.component.scss']
})
export class ConvocatoriaEnlaceModalComponent extends
  BaseModalComponent<ConvocatoriaEnlaceModalComponentData, ConvocatoriaEnlaceModalComponent> implements OnInit {
  fxLayoutProperties: FxLayoutProperties;
  fxFlexProperties: FxFlexProperties;

  modeloTiposEnlace$: Observable<IModeloTipoEnlace[]>;
  private modeloTiposEnlaceFiltered: IModeloTipoEnlace[];
  textSaveOrUpdate: string;

  msgParamUrlEntity = {};
  msgParamDescripcionEntity = {};
  msgParamTipoEnlaceEntity = {};
  title: string;

  constructor(
    private readonly logger: NGXLogger,
    protected snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<ConvocatoriaEnlaceModalComponent>,
    private modeloEjecucionService: ModeloEjecucionService,
    @Inject(MAT_DIALOG_DATA) public data: ConvocatoriaEnlaceModalComponentData,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, data);
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.layoutAlign = 'row';
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties.order = '2';
  }

  ngOnInit() {
    super.ngOnInit();
    this.setupI18N();
    this.loadTiposEnlaces();
    this.textSaveOrUpdate = this.data.enlace.url ? MSG_ACEPTAR : MSG_ANADIR;
  }


  private setupI18N(): void {
    this.translate.get(
      CONVOCATORIA_ENLACE_URL_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamUrlEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      CONVOCATORIA_ENLACE_DESCRIPCION_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamDescripcionEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      CONVOCATORIA_ENLACE_TIPO_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamTipoEnlaceEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    if (this.data.enlace.url) {
      this.translate.get(
        CONVOCATORIA_ENLACE_KEY,
        MSG_PARAMS.CARDINALIRY.PLURAL
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        CONVOCATORIA_ENLACE_KEY,
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

  /**
   * Carga todos los tipos de enlace
   */
  loadTiposEnlaces(): void {
    this.subscriptions.push(this.modeloEjecucionService.findModeloTipoEnlace(this.data.idModeloEjecucion).subscribe(
      (res) => {
        this.modeloTiposEnlaceFiltered = res.items;
        this.modeloTiposEnlace$ = this.formGroup.controls.tipoEnlace.valueChanges
          .pipe(
            startWith(''),
            map(value => this.filtroTipoEnlace(value))
          );
      },
      (error) => {
        this.logger.error(error);
        this.snackBarService.showError(MSG_ERROR_INIT);
      })
    );
  }

  /**
   * Filtra la lista devuelta por el servicio.
   *
   * @param value del input para autocompletar
   */
  filtroTipoEnlace(value: string): IModeloTipoEnlace[] {
    const filterValue = value.toString().toLowerCase();
    return this.modeloTiposEnlaceFiltered.filter(modeloTipoLazoEnlace =>
      modeloTipoLazoEnlace.tipoEnlace?.nombre.toLowerCase().includes(filterValue));
  }

  protected getDatosForm(): ConvocatoriaEnlaceModalComponentData {
    this.data.enlace.url = this.formGroup.controls.url.value;
    this.data.enlace.descripcion = this.formGroup.controls.descripcion.value;
    this.data.enlace.tipoEnlace = this.formGroup.controls.tipoEnlace.value;
    return this.data;
  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      url: new FormControl(this.data.enlace.url, [
        Validators.maxLength(250),
        StringValidator.notIn(this.data.selectedUrls.filter(url => url !== this.data.enlace.url))
      ]),
      descripcion: new FormControl(this.data.enlace.descripcion, [Validators.maxLength(250)]),
      tipoEnlace: new FormControl(this.data.enlace.tipoEnlace, [IsEntityValidator.isValid()]),
    });
    if (this.data.readonly) {
      formGroup.disable();
    }
    return formGroup;
  }

  getNombreTipoEnlace(tipoEnlace?: ITipoEnlace): string | undefined {
    return typeof tipoEnlace === 'string' ? tipoEnlace : tipoEnlace?.nombre;
  }
}
