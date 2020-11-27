import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IConvocatoriaEnlace } from '@core/models/csp/convocatoria-enlace';
import { ITipoEnlace } from '@core/models/csp/tipos-configuracion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SnackBarService } from '@core/services/snack-bar.service';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { IModeloTipoEnlace } from '@core/models/csp/modelo-tipo-enlace';
import { ModeloEjecucionService } from '@core/services/csp/modelo-ejecucion.service';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { StringValidator } from '@core/validators/string-validator';

const MSG_ERROR_INIT = marker('csp.convocatoria.enlace.error.cargar');
const MSG_ANADIR = marker('botones.aniadir');
const MSG_ACEPTAR = marker('botones.aceptar');
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

  constructor(
    protected logger: NGXLogger,
    protected snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<ConvocatoriaEnlaceModalComponent>,
    private modeloEjecucionService: ModeloEjecucionService,
    @Inject(MAT_DIALOG_DATA) public data: ConvocatoriaEnlaceModalComponentData,
  ) {
    super(logger, snackBarService, matDialogRef, data);
    this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'constructor()', 'start');
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.layoutAlign = 'row';
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties.order = '2';
    this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'constructor()', 'end');
  }

  ngOnInit() {
    this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();
    this.loadTiposEnlaces();
    this.textSaveOrUpdate = this.data.enlace.url ? MSG_ACEPTAR : MSG_ANADIR;
    this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'ngOnInit()', 'end');
  }

  /**
   * Carga todos los tipos de enlace
   */
  loadTiposEnlaces() {
    this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'loadTiposEnlaces()', 'start');
    this.subscriptions.push(this.modeloEjecucionService.findModeloTipoEnlace(this.data.idModeloEjecucion).subscribe(
      (res: SgiRestListResult<IModeloTipoEnlace>) => {
        this.modeloTiposEnlaceFiltered = res.items;
        this.modeloTiposEnlace$ = this.formGroup.controls.tipoEnlace.valueChanges
          .pipe(
            startWith(''),
            map(value => this.filtroTipoEnlace(value))
          );
        this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'loadTiposEnlaces()', 'end');
      },
      (error) => {
        this.snackBarService.showError(MSG_ERROR_INIT);
        this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'loadTiposEnlaces()', error);
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
    this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'getDatosForm()', 'start');
    this.data.enlace.url = this.formGroup.controls.url.value;
    this.data.enlace.descripcion = this.formGroup.controls.descripcion.value;
    this.data.enlace.tipoEnlace = this.formGroup.controls.tipoEnlace.value;
    this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'getDatosForm()', 'end');
    return this.data;
  }

  protected getFormGroup(): FormGroup {
    this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'getFormGroup()', 'start');
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
    this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'getFormGroup()', 'start');
    return formGroup;
  }

  getNombreTipoEnlace(tipoEnlace?: ITipoEnlace): string | undefined {
    return typeof tipoEnlace === 'string' ? tipoEnlace : tipoEnlace?.nombre;
  }
}
