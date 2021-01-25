import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ThemePalette } from '@angular/material/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IModeloTipoFase } from '@core/models/csp/modelo-tipo-fase';
import { IProyectoPlazos } from '@core/models/csp/proyecto-plazo';
import { ITipoFase } from '@core/models/csp/tipos-configuracion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ModeloEjecucionService } from '@core/services/csp/modelo-ejecucion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { DateUtils } from '@core/utils/date-utils';
import { DateValidator } from '@core/validators/date-validator';
import { NullIdValidador } from '@core/validators/null-id-validador';
import { IRangeDates, RangeValidator } from '@core/validators/range-validator';
import { SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, Subscription } from 'rxjs';
import { map, startWith, tap } from 'rxjs/operators';

const MSG_ERROR_FORM_GROUP = marker('form-group.error');
const MSG_ERROR_INIT = marker('csp.proyecto.plazos.fases.error.cargar');
const MSG_ERROR_TIPOS = marker('csp.proyecto.tipo.fases.error.cargar');
const MSG_ANADIR = marker('botones.aniadir');
const MSG_ACEPTAR = marker('botones.aceptar');

export interface ProyectoPlazosModalComponentData {
  plazos: IProyectoPlazos[];
  plazo: IProyectoPlazos;
  idModeloEjecucion: number;
  readonly: boolean;
}

@Component({
  selector: 'sgi-proyecto-plazos-modal',
  templateUrl: './proyecto-plazos-modal.component.html',
  styleUrls: ['./proyecto-plazos-modal.component.scss']
})
export class ProyectoPlazosModalComponent implements OnInit, OnDestroy {

  formGroup: FormGroup;
  fxFlexProperties: FxFlexProperties;
  fxFlexProperties2: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  fxLayoutProperties2: FxLayoutProperties;
  modeloTipoFases$: Observable<IModeloTipoFase[]>;

  private modeloTipoFasesFiltered: IModeloTipoFase[];
  private suscripciones: Subscription[] = [];

  textSaveOrUpdate: string;

  /** ngx-mat-datetime-picker */
  showSeconds = true;
  defaultTimeStart = [0, 0, 0];
  defaultTimeEnd = [23, 59, 59];
  /** ngx-mat-datetime-picker */

  constructor(
    private logger: NGXLogger,
    private snackBarService: SnackBarService,
    @Inject(MAT_DIALOG_DATA) public data: ProyectoPlazosModalComponentData,
    public matDialogRef: MatDialogRef<ProyectoPlazosModalComponent>,
    private modeloEjecucionService: ModeloEjecucionService,
  ) {
    this.logger.debug(ProyectoPlazosModalComponent.name, 'constructor()', 'start');

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.layoutAlign = 'row';
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties.order = '2';
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.xs = 'column';

    this.fxLayoutProperties2 = new FxLayoutProperties();
    this.fxLayoutProperties2.gap = '20px';
    this.fxLayoutProperties2.layout = 'row';
    this.fxLayoutProperties2.xs = 'column';

    this.fxFlexProperties2 = new FxFlexProperties();
    this.fxFlexProperties2.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties2.md = '0 1 calc(50%-10px)';
    this.fxFlexProperties2.gtMd = '0 1 calc(50%-10px)';
    this.fxFlexProperties2.order = '3';

    this.logger.debug(ProyectoPlazosModalComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(ProyectoPlazosModalComponent.name, 'ngOnInit()', 'start');
    this.initFormGroup();

    const suscription = this.formGroup.controls.fechaFin.valueChanges.subscribe((value) => this.validatorGeneraAviso(value));
    this.suscripciones.push(suscription);

    this.loadTipoFases();

    this.validatorGeneraAviso(this.formGroup.controls.fechaFin.value);
    this.textSaveOrUpdate = this.data.plazo?.tipoFase ? MSG_ACEPTAR : MSG_ANADIR;
    this.logger.debug(ProyectoPlazosModalComponent.name, 'ngOnInit()', 'start');
  }

  /**
   * Validamos fecha para activar o inactivar el checkbox generaAviso
   */
  private validatorGeneraAviso(fechaFinInput: Date) {
    this.logger.debug(ProyectoPlazosModalComponent.name, 'validatorGeneraAviso()', 'start');
    const fechaActual = new Date();
    const fechaFin = fechaFinInput;
    if (fechaFin <= fechaActual) {
      this.formGroup.get('generaAviso').disable();
      this.formGroup.get('generaAviso').setValue(false);
    } else {
      this.formGroup.get('generaAviso').enable();
    }
    this.logger.debug(ProyectoPlazosModalComponent.name, 'validatorGeneraAviso()', 'end');
  }

  /**
   * Inicializa formulario de creación/edición de plazos y fases
   */
  private initFormGroup() {
    this.logger.debug(ProyectoPlazosModalComponent.name, 'initFormGroup()', 'start');

    this.formGroup = new FormGroup({
      fechaInicio: new FormControl(this.data?.plazo?.fechaInicio, [Validators.required]),
      fechaFin: new FormControl(this.data?.plazo?.fechaFin, Validators.required),
      tipoFase: new FormControl(this.data?.plazo?.tipoFase, [Validators.required, new NullIdValidador().isValid()]),
      observaciones: new FormControl(this.data?.plazo?.observaciones, [Validators.maxLength(250)]),
      generaAviso: new FormControl(this.data?.plazo?.generaAviso)
    });

    if (this.data.readonly) {
      this.formGroup.disable();
    }

    this.createValidatorDate(this.data?.plazo?.tipoFase);

    const suscription = this.formGroup.controls.tipoFase.valueChanges.subscribe((value) => this.createValidatorDate(value));
    this.suscripciones.push(suscription);

    this.logger.debug(ProyectoPlazosModalComponent.name, 'initFormGroup()', 'end');
  }

  /**
   * Validacion del rango de fechas a la hora de seleccionar
   * un tipo de fase en el modal
   * @param tipoFase proyecto tipoFase
   */
  private createValidatorDate(tipoFase: ITipoFase | string): void {
    this.logger.debug(ProyectoPlazosModalComponent.name, `createValidatorDate(tipoFase: ${tipoFase})`, 'end');
    let rangoFechas: IRangeDates[] = [];
    if (tipoFase && typeof tipoFase !== 'string') {
      const proyectoFases = this.data.plazos.filter(plazo =>
        plazo.tipoFase.id === (tipoFase as ITipoFase).id &&
        (plazo.fechaInicio !== this.data.plazo.fechaInicio && plazo.fechaFin !== this.data.plazo.fechaFin));
      rangoFechas = proyectoFases.map(
        fase => {
          const rango: IRangeDates = {
            inicio: DateUtils.fechaToDate(fase.fechaInicio),
            fin: DateUtils.fechaToDate(fase.fechaFin)
          };
          return rango;
        }
      );
    }
    this.formGroup.setValidators([
      DateValidator.isAfter('fechaInicio', 'fechaFin'),
      DateValidator.isBefore('fechaFin', 'fechaInicio'),
      RangeValidator.notOverlaps('fechaInicio', 'fechaFin', rangoFechas)
    ]);
    this.logger.debug(ProyectoPlazosModalComponent.name, `createValidatorDate(tipoFase: ${tipoFase})`, 'end');
  }

  loadTipoFases() {
    this.logger.debug(ProyectoPlazosModalComponent.name, 'loadTipoFases()', 'start');
    this.suscripciones.push(
      this.modeloEjecucionService.findModeloTipoFaseModeloEjecucionProyecto(this.data.idModeloEjecucion).subscribe(
        (res: SgiRestListResult<IModeloTipoFase>) => {
          this.modeloTipoFasesFiltered = res.items;
          this.modeloTipoFases$ = this.formGroup.controls.tipoFase.valueChanges
            .pipe(
              startWith(''),
              map(value => this.filtroTipoPlazosFase(value))
            );
          this.logger.debug(ProyectoPlazosModalComponent.name, 'loadTipoFases()', 'end');
        },
        () => {
          if (this.data.idModeloEjecucion) {
            this.snackBarService.showError(MSG_ERROR_INIT);
          } else {
            this.snackBarService.showError(MSG_ERROR_TIPOS);
          }
          this.logger.debug(ProyectoPlazosModalComponent.name, 'loadTipoFases()', 'end');
        })
    );


    this.logger.debug(ProyectoPlazosModalComponent.name, 'loadTipoFases()', 'end');
  }

  /**
   * Actualizar o guardar datos
   */
  saveOrUpdate(): void {
    this.logger.debug(ProyectoPlazosModalComponent.name, 'saveOrUpdate()', 'start');
    if (this.formGroup.valid) {
      this.loadDatosForm();
      this.closeModal(this.data.plazo);
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM_GROUP);
    }
    this.logger.debug(ProyectoPlazosModalComponent.name, 'saveOrUpdate()', 'end');
  }

  /**
   * Método para actualizar la entidad con los datos de un formGroup
   *
   * @returns Comentario con los datos del formulario
   */
  private loadDatosForm(): void {
    this.logger.debug(ProyectoPlazosModalComponent.name, 'loadDatosForm()', 'start');
    this.data.plazo.fechaInicio = this.formGroup.controls.fechaInicio.value;
    this.data.plazo.fechaFin = this.formGroup.controls.fechaFin.value;
    this.data.plazo.tipoFase = this.formGroup.controls.tipoFase.value;
    this.data.plazo.observaciones = this.formGroup.controls.observaciones.value;
    this.data.plazo.generaAviso = this.formGroup.controls.generaAviso.value;
    this.logger.debug(ProyectoPlazosModalComponent.name, 'loadDatosForm()', 'end');
  }

  /**
   * Devuelve el nombre tipo de plazos fase
   * @param tipoFase tipo de plazos fase
   * @returns nombre de plazos fase
   */
  getTipoPlazosFase(tipoFase?: ITipoFase): string | undefined {
    return typeof tipoFase === 'string' ? tipoFase : tipoFase?.nombre;
  }

  /**
   * Filtra la lista devuelta por el servicio.
   *
   * @param value del input para autocompletar
   */
  private filtroTipoPlazosFase(value: string): IModeloTipoFase[] {
    const filterValue = value.toString().toLowerCase();
    return this.modeloTipoFasesFiltered.filter(modeloTipoFase =>
      modeloTipoFase.tipoFase?.nombre?.toLowerCase().includes(filterValue));
  }

  /**
   * Cierra la ventana modal
   *
   */
  closeModal(plazos?: IProyectoPlazos): void {
    this.logger.debug(ProyectoPlazosModalComponent.name, 'closeModal()', 'start');
    this.matDialogRef.close(plazos);
    this.logger.debug(ProyectoPlazosModalComponent.name, 'closeModal()', 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(ProyectoPlazosModalComponent.name, 'ngOnDestroy()', 'start');
    this.suscripciones?.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(ProyectoPlazosModalComponent.name, 'ngOnDestroy()', 'end');
  }


}
