import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IPlazosFases } from '@core/models/csp/plazos-fases';
import { ITipoPlazosFases } from '@core/models/csp/tipo-plazos-fases';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ModeloEjecucionService } from '@core/services/csp/modelo-ejecucion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, Subscription } from 'rxjs';
import { map, startWith } from 'rxjs/operators';

const MSG_ERROR_FORM_GROUP = marker('form-group.error');
const MSG_ERROR_INIT = marker('csp.convocatoria.plazos.fases.error.cargar');

@Component({
  selector: 'sgi-convocatoria-plazos-fase-modal',
  templateUrl: './convocatoria-plazos-fase-modal.component.html',
  styleUrls: ['./convocatoria-plazos-fase-modal.component.scss']
})
export class ConvocatoriaPlazosFaseModalComponent implements OnInit, OnDestroy {

  formGroup: FormGroup;
  fxFlexProperties: FxFlexProperties;
  fxFlexProperties2: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  fxLayoutProperties2: FxLayoutProperties;

  FormGroupUtil = FormGroupUtil;

  plazosFaseFiltered: ITipoPlazosFases[];
  plazosFase: Observable<ITipoPlazosFases[]>;

  plazosFasesData: IPlazosFases;

  suscripciones: Subscription[];

  constructor(
    private readonly logger: NGXLogger,
    private readonly snackBarService: SnackBarService,
    @Inject(MAT_DIALOG_DATA) public plazosFases: IPlazosFases,
    public readonly matDialogRef: MatDialogRef<ConvocatoriaPlazosFaseModalComponent>,
    private readonly modeloEjecucionService: ModeloEjecucionService
  ) {
    this.logger.debug(ConvocatoriaPlazosFaseModalComponent.name, 'constructor()', 'start');

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

    this.logger.debug(ConvocatoriaPlazosFaseModalComponent.name, 'constructor()', 'end');
  }


  ngOnInit(): void {
    this.logger.debug(ConvocatoriaPlazosFaseModalComponent.name, 'ngOnInit()', 'start');
    this.suscripciones = [];
    this.initFormGroup();
    this.loadTiposJustificacion();
    this.plazosFasesData = this.plazosFases;
    this.logger.debug(ConvocatoriaPlazosFaseModalComponent.name, 'ngOnInit()', 'start');
  }

  /**
   * Inicializa formulario de creación/edición de periodo de justificacion
   */
  private initFormGroup() {
    this.logger.debug(ConvocatoriaPlazosFaseModalComponent.name, 'initFormGroup()', 'start');

    this.formGroup = new FormGroup({
      fechaInicio: new FormControl(this.plazosFases?.fechaInicio, [Validators.required]),
      fechaFin: new FormControl(this.plazosFases?.fechaFin, [Validators.required]),
      tipoPlazosFases: new FormControl(this.plazosFases?.tipoPlazosFases?.nombre, [Validators.required]),
      observaciones: new FormControl(this.plazosFases?.observaciones, [])
    });
    this.logger.debug(ConvocatoriaPlazosFaseModalComponent.name, 'initFormGroup()', 'end');
  }

  loadTiposJustificacion() {

    this.logger.debug(ConvocatoriaPlazosFaseModalComponent.name, 'loadTiposJustificacion()', 'start');
    this.suscripciones.push(
      this.modeloEjecucionService.findPlazoFase(1).subscribe(
        (res: SgiRestListResult<ITipoPlazosFases>) => {
          this.plazosFaseFiltered = res.items;
          this.plazosFase = this.formGroup.controls.tipoPlazosFases.valueChanges
            .pipe(
              startWith(''),
              map(value => this.filtroTipoPlazosFase(value))
            );
          this.logger.debug(ConvocatoriaPlazosFaseModalComponent.name, 'loadTiposJustificacion()', 'end');
        },
        () => {
          this.snackBarService.showError(MSG_ERROR_INIT);
          this.logger.debug(ConvocatoriaPlazosFaseModalComponent.name, 'loadTiposJustificacion()', 'end');
        }
      )
    );
    this.logger.debug(ConvocatoriaPlazosFaseModalComponent.name, 'loadTiposJustificacion()', 'end');
  }

  /**
   * Actualizar o guardar datos
   */
  saveOrUpdate(): void {
    this.logger.debug(ConvocatoriaPlazosFaseModalComponent.name, 'saveOrUpdate()', 'start');
    if (FormGroupUtil.valid(this.formGroup)) {
      this.loadDatosForm();
      this.closeModal(this.plazosFasesData);
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM_GROUP);
    }
    this.logger.debug(ConvocatoriaPlazosFaseModalComponent.name, 'saveOrUpdate()', 'end');
  }

  /**
   * Método para actualizar la entidad con los datos de un formGroup
   *
   * @returns Comentario con los datos del formulario
   */
  private loadDatosForm(): void {
    this.logger.debug(ConvocatoriaPlazosFaseModalComponent.name, 'loadDatosForm()', 'start');
    this.plazosFasesData.fechaInicio = FormGroupUtil.getValue(this.formGroup, 'fechaInicio');
    this.plazosFasesData.fechaFin = FormGroupUtil.getValue(this.formGroup, 'fechaFin');
    this.plazosFasesData.tipoPlazosFases = FormGroupUtil.getValue(this.formGroup, 'tipoPlazosFases');
    this.plazosFasesData.observaciones = FormGroupUtil.getValue(this.formGroup, 'observaciones');
    this.logger.debug(ConvocatoriaPlazosFaseModalComponent.name, 'loadDatosForm()', 'end');
  }



  /**
   * Devuelve el nombre tipo de plazos fase
   * @param tipoPeriodo tipo de plazos fase
   * @returns nombre de plazos fase
   */
  getTipoPlazosFase(tipoPeriodo?: ITipoPlazosFases): string | undefined {
    return typeof tipoPeriodo === 'string' ? tipoPeriodo : tipoPeriodo?.nombre;
  }


  /**
   * Filtra la lista devuelta por el servicio.
   *
   * @param value del input para autocompletar
   */
  filtroTipoPlazosFase(value: string): ITipoPlazosFases[] {
    const filterValue = value.toString().toLowerCase();
    return this.plazosFaseFiltered.filter(tipoLazoFase => tipoLazoFase.nombre.toLowerCase().includes(filterValue));
  }


  /**
   * Cierra la ventana modal
   *
   */
  closeModal(plazos?: IPlazosFases): void {
    this.logger.debug(ConvocatoriaPlazosFaseModalComponent.name, 'closeModal()', 'start');
    this.matDialogRef.close(plazos);
    this.logger.debug(ConvocatoriaPlazosFaseModalComponent.name, 'closeModal()', 'end');
  }


  ngOnDestroy(): void {
    this.logger.debug(ConvocatoriaPlazosFaseModalComponent.name, 'ngOnDestroy()', 'start');
    this.suscripciones?.forEach(x => x.unsubscribe());
    this.logger.debug(ConvocatoriaPlazosFaseModalComponent.name, 'ngOnDestroy()', 'end');
  }


}
