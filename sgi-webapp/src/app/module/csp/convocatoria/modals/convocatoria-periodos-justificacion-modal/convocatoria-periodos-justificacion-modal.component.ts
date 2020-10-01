import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IPeriodoJustificacion } from '@core/models/csp/periodo-justificacion';
import { ITipoPeriodoJustificacion } from '@core/models/csp/tipo-periodo-justificacion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ModeloEjecucionService } from '@core/services/csp/modelo-ejecucion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, Subscription } from 'rxjs';
import { map, startWith } from 'rxjs/operators';

const MSG_ERROR_INIT = marker('csp.convocatoria.periodo.justificacion.error.cargar');
const MSG_ERROR_FORM_GROUP = marker('form-group.error');

@Component({
  templateUrl: './convocatoria-periodos-justificacion-modal.component.html',
  styleUrls: ['./convocatoria-periodos-justificacion-modal.component.scss']
})
export class ConvocatoriaPeriodosJustificacionModalComponent implements OnInit, OnDestroy {

  formGroup: FormGroup;
  fxFlexProperties: FxFlexProperties;
  fxFlexProperties2: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  FormGroupUtil = FormGroupUtil;

  tiposPeriodoFiltered: ITipoPeriodoJustificacion[];
  tiposPeriodo: Observable<ITipoPeriodoJustificacion[]>;

  periodoJustificacionData: IPeriodoJustificacion;

  suscripciones: Subscription[];

  constructor(
    private readonly logger: NGXLogger,
    private readonly snackBarService: SnackBarService,
    @Inject(MAT_DIALOG_DATA) public periodoJustificacion: IPeriodoJustificacion,
    public readonly matDialogRef: MatDialogRef<ConvocatoriaPeriodosJustificacionModalComponent>,
    private readonly modeloEjecucionService: ModeloEjecucionService
  ) {
    this.logger.debug(ConvocatoriaPeriodosJustificacionModalComponent.name, 'constructor()', 'start');

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(20%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxFlexProperties2 = new FxFlexProperties();
    this.fxFlexProperties2.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties2.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties2.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties2.order = '3';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

    this.logger.debug(ConvocatoriaPeriodosJustificacionModalComponent.name, 'constructor()', 'end');
  }


  ngOnInit(): void {
    this.logger.debug(ConvocatoriaPeriodosJustificacionModalComponent.name, 'ngOnInit()', 'start');
    this.suscripciones = [];
    this.initFormGroup();
    this.loadTiposJustificacion();
    this.periodoJustificacionData = this.periodoJustificacion;
    this.logger.debug(ConvocatoriaPeriodosJustificacionModalComponent.name, 'ngOnInit()', 'start');
  }

  /**
   * Inicializa formulario de creación/edición de periodo de justificacion
   */
  private initFormGroup() {
    this.logger.debug(ConvocatoriaPeriodosJustificacionModalComponent.name, 'initFormGroup()', 'start');

    this.formGroup = new FormGroup({
      numPeriodo: new FormControl(this.periodoJustificacion?.numPeriodo, [Validators.required]),
      tipoPeriodo: new FormControl(this.periodoJustificacion?.tipoJustificacion?.nombre, [Validators.required]),
      desdeMes: new FormControl(this.periodoJustificacion?.mesInicial, [Validators.required]),
      hastaMes: new FormControl(this.periodoJustificacion?.mesFinal, [Validators.required]),
      fechaInicio: new FormControl(this.periodoJustificacion?.fechaInicio, []),
      fechaFin: new FormControl(this.periodoJustificacion?.fechaFin, []),
      observaciones: new FormControl(this.periodoJustificacion?.observaciones, [])
    });
    this.logger.debug(ConvocatoriaPeriodosJustificacionModalComponent.name, 'initFormGroup()', 'end');
  }

  loadTiposJustificacion() {

    this.logger.debug(ConvocatoriaPeriodosJustificacionModalComponent.name, 'loadTiposJustificacion()', 'start');
    this.suscripciones.push(
      this.modeloEjecucionService.findTipoJustificacion(1).subscribe(
        (res: SgiRestListResult<ITipoPeriodoJustificacion>) => {
          this.tiposPeriodoFiltered = res.items;
          this.tiposPeriodo = this.formGroup.controls.tipoPeriodo.valueChanges
            .pipe(
              startWith(''),
              map(value => this.filtroTipoPeriodo(value))
            );
          this.logger.debug(ConvocatoriaPeriodosJustificacionModalComponent.name, 'loadTiposJustificacion()', 'end');
        },
        () => {
          this.snackBarService.showError(MSG_ERROR_INIT);
          this.logger.debug(ConvocatoriaPeriodosJustificacionModalComponent.name, 'loadTiposJustificacion()', 'end');
        }
      )
    );
    this.logger.debug(ConvocatoriaPeriodosJustificacionModalComponent.name, 'loadTiposJustificacion()', 'end');
  }

  /**
   * Actualizar o guardar datos
   */
  saveOrUpdate(): void {
    this.logger.debug(ConvocatoriaPeriodosJustificacionModalComponent.name, 'saveOrUpdate()', 'start');
    if (FormGroupUtil.valid(this.formGroup)) {
      this.loadDatosForm();
      this.closeModal(this.periodoJustificacionData);
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM_GROUP);
    }
    this.logger.debug(ConvocatoriaPeriodosJustificacionModalComponent.name, 'saveOrUpdate()', 'end');
  }

  /**
   * Método para actualizar la entidad con los datos de un formGroup
   *
   * @returns Comentario con los datos del formulario
   */
  private loadDatosForm(): void {
    this.logger.debug(ConvocatoriaPeriodosJustificacionModalComponent.name, 'loadDatosForm()', 'start');
    this.periodoJustificacionData.numPeriodo = FormGroupUtil.getValue(this.formGroup, 'numPeriodo');
    this.periodoJustificacionData.mesInicial = FormGroupUtil.getValue(this.formGroup, 'desdeMes');
    this.periodoJustificacionData.mesFinal = FormGroupUtil.getValue(this.formGroup, 'hastaMes');
    this.periodoJustificacionData.fechaInicio = FormGroupUtil.getValue(this.formGroup, 'fechaInicio');
    this.periodoJustificacionData.fechaFin = FormGroupUtil.getValue(this.formGroup, 'fechaFin');
    this.periodoJustificacionData.tipoJustificacion = FormGroupUtil.getValue(this.formGroup, 'tipoPeriodo');
    this.periodoJustificacionData.observaciones = FormGroupUtil.getValue(this.formGroup, 'observaciones');
    this.logger.debug(ConvocatoriaPeriodosJustificacionModalComponent.name, 'loadDatosForm()', 'end');
  }



  /**
   * Devuelve el nombre tipo de periodo de justificacion
   * @param tipoPeriodo tipo tipo de periodo de justificacion
   * @returns nombre tipo de periodo de justificacion
   */
  getTipoPeriodo(tipoPeriodo?: ITipoPeriodoJustificacion): string | undefined {
    return typeof tipoPeriodo === 'string' ? tipoPeriodo : tipoPeriodo?.nombre;
  }


  /**
   * Filtra la lista devuelta por el servicio.
   *
   * @param value del input para autocompletar
   */
  filtroTipoPeriodo(value: string): ITipoPeriodoJustificacion[] {
    const filterValue = value.toString().toLowerCase();
    return this.tiposPeriodoFiltered.filter(tipoPeriodo => tipoPeriodo.nombre.toLowerCase().includes(filterValue));
  }


  /**
   * Cierra la ventana modal
   *
   */
  closeModal(periodo?: IPeriodoJustificacion): void {
    this.logger.debug(ConvocatoriaPeriodosJustificacionModalComponent.name, 'closeModal()', 'start');
    this.matDialogRef.close(periodo);
    this.logger.debug(ConvocatoriaPeriodosJustificacionModalComponent.name, 'closeModal()', 'end');
  }


  ngOnDestroy(): void {
    this.logger.debug(ConvocatoriaPeriodosJustificacionModalComponent.name, 'ngOnDestroy()', 'start');
    this.suscripciones?.forEach(x => x.unsubscribe());
    this.logger.debug(ConvocatoriaPeriodosJustificacionModalComponent.name, 'ngOnDestroy()', 'end');
  }


}
