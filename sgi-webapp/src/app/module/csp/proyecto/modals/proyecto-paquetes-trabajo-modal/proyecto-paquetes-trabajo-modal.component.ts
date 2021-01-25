import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IProyectoPaqueteTrabajo } from '@core/models/csp/proyecto-paquete-trabajo';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SnackBarService } from '@core/services/snack-bar.service';
import { DateValidator } from '@core/validators/date-validator';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';
import moment from 'moment';
import { StringValidator } from '@core/validators/string-validator';

const MSG_ERROR_FORM_GROUP = marker('form-group.error');
const MSG_ANADIR = marker('botones.aniadir');
const MSG_ACEPTAR = marker('botones.aceptar');

export interface PaquetesTrabajoModalData {
  paquetesTrabajo: IProyectoPaqueteTrabajo[];
  fechaInicio: Date;
  fechaFin: Date;
  paqueteTrabajo: IProyectoPaqueteTrabajo;
}

@Component({
  selector: 'sgi-proyecto-paquetes-trabajo-modal',
  templateUrl: './proyecto-paquetes-trabajo-modal.component.html',
  styleUrls: ['./proyecto-paquetes-trabajo-modal.component.scss']
})
export class ProyectoPaquetesTrabajoModalComponent implements OnInit, OnDestroy {

  formGroup: FormGroup;

  textSaveOrUpdate: string;

  fxFlexProperties: FxFlexProperties;
  fxFlexProperties2: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  fxLayoutProperties2: FxLayoutProperties;
  private suscripciones: Subscription[] = [];

  constructor(
    private logger: NGXLogger,
    public matDialogRef: MatDialogRef<ProyectoPaquetesTrabajoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: PaquetesTrabajoModalData,
    private snackBarService: SnackBarService) {

    this.logger.debug(ProyectoPaquetesTrabajoModalComponent.name, 'constructor()', 'start');

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
    this.logger.debug(ProyectoPaquetesTrabajoModalComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(ProyectoPaquetesTrabajoModalComponent.name, 'ngOnInit()', 'start');
    this.initFormGroup();

    this.textSaveOrUpdate = this.data?.paqueteTrabajo?.nombre ? MSG_ACEPTAR : MSG_ANADIR;
    this.logger.debug(ProyectoPaquetesTrabajoModalComponent.name, 'ngOnInit()', 'end');
  }

  /**
   * Inicializa formulario de creación/edición de paquetes trabajo
   */
  private initFormGroup() {
    this.logger.debug(ProyectoPaquetesTrabajoModalComponent.name, 'initFormGroup()', 'start');

    this.formGroup = new FormGroup({
      nombre: new FormControl(this.data?.paqueteTrabajo?.nombre,
        [Validators.maxLength(250), Validators.required,
        StringValidator.notIn(this.data?.paquetesTrabajo?.map(paquete => paquete?.nombre))]),
      fechaInicio: new FormControl(this.data?.paqueteTrabajo?.fechaInicio, [Validators.required]),
      fechaFin: new FormControl(this.data?.paqueteTrabajo?.fechaFin, Validators.required),
      personaMes: new FormControl(this.data?.paqueteTrabajo?.personaMes, [
        Validators.min(0), Validators.max(9999), Validators.required]),
      descripcion: new FormControl(this.data?.paqueteTrabajo?.descripcion, [Validators.maxLength(250)])
    },
      {
        validators: [
          ValidarRangoProyecto.rangoProyecto('fechaInicio', 'fechaFin', this.data),
          DateValidator.isAfter('fechaInicio', 'fechaFin')]
      });

    this.logger.debug(ProyectoPaquetesTrabajoModalComponent.name, 'initFormGroup()', 'end');
  }

  /**
   * Actualizar o guardar datos
   */
  saveOrUpdate(): void {
    this.logger.debug(ProyectoPaquetesTrabajoModalComponent.name, 'saveOrUpdate()', 'start');
    if (this.formGroup.valid) {
      this.loadDatosForm();
      this.closeModal(this.data.paqueteTrabajo);
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM_GROUP);
    }
    this.logger.debug(ProyectoPaquetesTrabajoModalComponent.name, 'saveOrUpdate()', 'end');
  }

  /**
   * Método para actualizar la entidad con los datos de un formGroup
   *
   * @returns Comentario con los datos del formulario
   */
  private loadDatosForm(): void {
    this.logger.debug(ProyectoPaquetesTrabajoModalComponent.name, 'loadDatosForm()', 'start');
    this.data.paqueteTrabajo.nombre = this.formGroup.get('nombre').value;
    this.data.paqueteTrabajo.fechaFin = this.formGroup.get('fechaFin').value;
    this.data.paqueteTrabajo.fechaInicio = this.formGroup.get('fechaInicio').value;
    this.data.paqueteTrabajo.personaMes = this.formGroup.get('personaMes').value;
    this.data.paqueteTrabajo.descripcion = this.formGroup.get('descripcion').value;

    this.logger.debug(ProyectoPaquetesTrabajoModalComponent.name, 'loadDatosForm()', 'end');
  }

  /**
   * Cierra la ventana modal y devuelve el paqueteTrabajo modificado o creado.
   *
   * @param paqueteTrabajo paqueteTrabajo modificado o creado.
   */
  closeModal(paqueteTrabajo?: IProyectoPaqueteTrabajo): void {
    this.logger.debug(ProyectoPaquetesTrabajoModalComponent.name, 'closeModal()', 'start');
    this.matDialogRef.close(paqueteTrabajo);
    this.logger.debug(ProyectoPaquetesTrabajoModalComponent.name, 'closeModal()', 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(ProyectoPaquetesTrabajoModalComponent.name, 'ngOnDestroy()', 'start');
    this.suscripciones?.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(ProyectoPaquetesTrabajoModalComponent.name, 'ngOnDestroy()', 'end');
  }

}

/**
 * Validar rango de fechas de proyecto
 * con lo insertado en el formulario
 */
export class ValidarRangoProyecto {

  static rangoProyecto(fechaInicioInput: string, fechaFinInput: string, paqueteTrabajo: PaquetesTrabajoModalData): ValidatorFn {
    return (formGroup: FormGroup): ValidationErrors | null => {

      const fechaInicioForm = formGroup.controls[fechaInicioInput];
      const fechaFinForm = formGroup.controls[fechaFinInput];

      if ((fechaInicioForm.errors && !fechaInicioForm.errors.invalid ||
        fechaFinForm.errors && !fechaFinForm.errors.invalid)) {
        return;
      }

      const fechaInicio = moment(fechaInicioForm.value).format('YYYY-MM-DD');
      const fechaFin = moment(fechaFinForm.value).format('YYYY-MM-DD');
      const fechaProyectoInicio = moment(paqueteTrabajo.fechaInicio).format('YYYY-MM-DD');
      const fechaProyectoFin = moment(paqueteTrabajo.fechaFin).format('YYYY-MM-DD');

      if (formGroup.controls.fechaInicio.value !== null && formGroup.controls.fechaFin.value !== null) {
        if (!((fechaProyectoInicio <= fechaInicio) &&
          (fechaProyectoFin >= fechaFin))) {
          fechaInicioForm.setErrors({ invalid: true });
          fechaInicioForm.markAsTouched({ onlySelf: true });
          fechaFinForm.setErrors({ invalid: true });
          fechaFinForm.markAsTouched({ onlySelf: true });
        } else {
          fechaFinForm.setErrors(null);
          fechaInicioForm.setErrors(null);
        }

      };
    }
  }
}
