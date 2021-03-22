import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoPaqueteTrabajo } from '@core/models/csp/proyecto-paquete-trabajo';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SnackBarService } from '@core/services/snack-bar.service';
import { DateValidator } from '@core/validators/date-validator';
import { StringValidator } from '@core/validators/string-validator';
import { TranslateService } from '@ngx-translate/core';
import { DateTime } from 'luxon';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const MSG_ERROR_FORM_GROUP = marker('error.form-group');
const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const PAQUETE_TRABAJO_DESCRIPCION_KEY = marker('csp.proyecto-paquete-trabajo.descripcion');
const PAQUETE_TRABAJO_FECHA_FIN_KEY = marker('csp.proyecto-paquete-trabajo.fecha-fin');
const PAQUETE_TRABAJO_FECHA_INICIO_KEY = marker('csp.proyecto-paquete-trabajo.fecha-inicio');
const PAQUETE_TRABAJO_NOMBRE_KEY = marker('csp.proyecto-paquete-trabajo.nombre');
const PAQUETE_TRABAJO_PERSONA_MES_KEY = marker('csp.proyecto-paquete-trabajo.persona-mes');
const PAQUETE_TRABAJO_KEY = marker('csp.proyecto-paquete-trabajo');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface PaquetesTrabajoModalData {
  paquetesTrabajo: IProyectoPaqueteTrabajo[];
  fechaInicio: DateTime;
  fechaFin: DateTime;
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

  msgParamDescripcionEntity = {};
  msgParamNombreEntity = {};
  msgParamFechaFinEntity = {};
  msgParamFechaInicioEntity = {};
  msgParamPersonaMesEntity = {};
  title: string;

  constructor(
    public matDialogRef: MatDialogRef<ProyectoPaquetesTrabajoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: PaquetesTrabajoModalData,
    private snackBarService: SnackBarService,
    private readonly translate: TranslateService) {

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
  }

  ngOnInit(): void {
    this.initFormGroup();

    this.setupI18N();

    this.textSaveOrUpdate = this.data?.paqueteTrabajo?.nombre ? MSG_ACEPTAR : MSG_ANADIR;
  }

  private setupI18N(): void {
    this.translate.get(
      PAQUETE_TRABAJO_DESCRIPCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamDescripcionEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PAQUETE_TRABAJO_FECHA_FIN_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaFinEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });

    this.translate.get(
      PAQUETE_TRABAJO_FECHA_INICIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaInicioEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });

    this.translate.get(
      PAQUETE_TRABAJO_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PAQUETE_TRABAJO_PERSONA_MES_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamPersonaMesEntity = { entity: value, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.data?.paqueteTrabajo?.nombre) {
      this.translate.get(
        PAQUETE_TRABAJO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        PAQUETE_TRABAJO_KEY,
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
   * Inicializa formulario de creación/edición de paquetes trabajo
   */
  private initFormGroup() {
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
  }

  /**
   * Actualizar o guardar datos
   */
  saveOrUpdate(): void {
    if (this.formGroup.valid) {
      this.loadDatosForm();
      this.closeModal(this.data.paqueteTrabajo);
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM_GROUP);
    }
  }

  /**
   * Método para actualizar la entidad con los datos de un formGroup
   *
   * @returns Comentario con los datos del formulario
   */
  private loadDatosForm(): void {
    this.data.paqueteTrabajo.nombre = this.formGroup.get('nombre').value;
    this.data.paqueteTrabajo.fechaFin = this.formGroup.get('fechaFin').value;
    this.data.paqueteTrabajo.fechaInicio = this.formGroup.get('fechaInicio').value;
    this.data.paqueteTrabajo.personaMes = this.formGroup.get('personaMes').value;
    this.data.paqueteTrabajo.descripcion = this.formGroup.get('descripcion').value;
  }

  /**
   * Cierra la ventana modal y devuelve el paqueteTrabajo modificado o creado.
   *
   * @param paqueteTrabajo paqueteTrabajo modificado o creado.
   */
  closeModal(paqueteTrabajo?: IProyectoPaqueteTrabajo): void {
    this.matDialogRef.close(paqueteTrabajo);
  }

  ngOnDestroy(): void {
    this.suscripciones?.forEach(subscription => subscription.unsubscribe());
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

      if (formGroup.controls.fechaInicio.value !== null && formGroup.controls.fechaFin.value !== null) {
        if (!((paqueteTrabajo.fechaInicio <= fechaInicioForm.value) &&
          (paqueteTrabajo.fechaFin >= fechaFinForm.value))) {
          fechaInicioForm.setErrors({ invalid: true });
          fechaInicioForm.markAsTouched({ onlySelf: true });
          fechaFinForm.setErrors({ invalid: true });
          fechaFinForm.markAsTouched({ onlySelf: true });
        } else {
          fechaFinForm.setErrors(null);
          fechaInicioForm.setErrors(null);
        }

      }
    };
  }
}
