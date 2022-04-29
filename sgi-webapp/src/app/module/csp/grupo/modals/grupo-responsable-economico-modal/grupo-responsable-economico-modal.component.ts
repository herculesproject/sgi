import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatAutocompleteTrigger } from '@angular/material/autocomplete';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IGrupoResponsableEconomico } from '@core/models/csp/grupo-responsable-economico';
import { IRolProyecto } from '@core/models/csp/rol-proyecto';
import { DateValidator } from '@core/validators/date-validator';
import { IRange } from '@core/validators/range-validator';
import { TranslateService } from '@ngx-translate/core';
import { DateTime } from 'luxon';
import { merge, Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const GRUPO_RESPONSABLE_ECONOMICO_FECHA_FIN_KEY = marker('csp.grupo-responsable-economico.fecha-fin');
const GRUPO_RESPONSABLE_ECONOMICO_FECHA_INICIO_KEY = marker('csp.grupo-responsable-economico.fecha-inicio');
const GRUPO_RESPONSABLE_ECONOMICO_RESPONSABLE_ECONOMICO_KEY = marker('csp.grupo-responsable-economico');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface GrupoResponsableEconomicoModalData {
  titleEntity: string;
  selectedEntidades: IGrupoResponsableEconomico[];
  entidad: IGrupoResponsableEconomico;
  fechaInicioMin: DateTime;
  fechaFinMax: DateTime;
  isEdit: boolean;
}

@Component({
  templateUrl: './grupo-responsable-economico-modal.component.html',
  styleUrls: ['./grupo-responsable-economico-modal.component.scss']
})
export class GrupoResponsableEconomicoModalComponent extends DialogFormComponent<GrupoResponsableEconomicoModalData> implements OnInit {

  TIPO_COLECTIVO = TipoColectivo;

  @ViewChild(MatAutocompleteTrigger) autocomplete: MatAutocompleteTrigger;

  textSaveOrUpdate: string;

  rolesGrupo$: Observable<IRolProyecto[]>;
  colectivosIdRolParticipacion: string[];

  msgParamFechaFinEntity = {};
  msgParamFechaInicioEntity = {};
  msgParamMiembroEntity = {};
  msgParamRolParticipacionEntity = {};
  msgParamHoraEntity = {};
  msgParamDedicacionEntity = {};
  msgParamParticipacionEntity = {};
  title: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    matDialogRef: MatDialogRef<GrupoResponsableEconomicoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: GrupoResponsableEconomicoModalData,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data?.isEdit);
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.setupI18N();

    this.textSaveOrUpdate = this.data?.isEdit ? MSG_ACEPTAR : MSG_ANADIR;

    this.subscriptions.push(
      merge(
        this.formGroup.get('miembro').valueChanges,
        this.formGroup.get('fechaInicio').valueChanges,
        this.formGroup.get('fechaFin').valueChanges
      ).subscribe(() => this.checkRangesDates())
    );
  }

  private setupI18N(): void {

    this.translate.get(
      GRUPO_RESPONSABLE_ECONOMICO_RESPONSABLE_ECONOMICO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamMiembroEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      GRUPO_RESPONSABLE_ECONOMICO_FECHA_FIN_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaFinEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });

    this.translate.get(
      GRUPO_RESPONSABLE_ECONOMICO_FECHA_INICIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaInicioEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });


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

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup(
      {
        miembro: new FormControl(this.data?.entidad?.persona, [
          Validators.required
        ]),
        fechaInicio: new FormControl(this.data?.entidad?.fechaInicio, [
          this.data.fechaFinMax ? DateValidator.isBetween(this.data.fechaInicioMin, this.data.fechaFinMax) :
            DateValidator.minDate(this.data.fechaInicioMin),
          Validators.required
        ]),
        fechaFin: new FormControl(this.data?.entidad?.fechaFin, [
          this.data.fechaFinMax ? DateValidator.isBetween(this.data.fechaInicioMin, this.data.fechaFinMax) :
            DateValidator.minDate(this.data.fechaInicioMin)
        ]),
      },
      {
        validators: [
          DateValidator.isAfter('fechaInicio', 'fechaFin', false)
        ]
      }
    );

    return formGroup;
  }

  protected getValue(): GrupoResponsableEconomicoModalData {
    this.data.entidad.persona = this.formGroup.get('miembro').value;
    this.data.entidad.fechaInicio = this.formGroup.get('fechaInicio').value;
    this.data.entidad.fechaFin = this.formGroup.get('fechaFin').value;
    return this.data;
  }

  private checkRangesDates(): void {
    const miembroForm = this.formGroup.get('miembro');
    const fechaInicioForm = this.formGroup.get('fechaInicio');
    const fechaFinForm = this.formGroup.get('fechaFin');

    const fechaInicio = fechaInicioForm.value ? fechaInicioForm.value.toMillis() : Number.MIN_VALUE;
    const fechaFin = fechaFinForm.value ? fechaFinForm.value.toMillis() : Number.MAX_VALUE;
    const ranges = this.data.selectedEntidades.filter(element => element.persona.id === miembroForm.value?.id)
      .map(value => {
        const range: IRange = {
          inicio: value.fechaInicio ? value.fechaInicio.toMillis() : Number.MIN_VALUE,
          fin: value.fechaFin ? value.fechaFin.toMillis() : Number.MAX_VALUE,
        };
        return range;
      });

    if (ranges.some(range => (fechaInicio <= range.fin && range.inicio <= fechaFin))) {
      if (fechaInicioForm.value) {
        this.addError(fechaInicioForm, 'range');
      }
      if (fechaFinForm.value) {
        this.addError(fechaFinForm, 'range');
      }
      if (!fechaInicioForm.value && !fechaFinForm.value) {
        this.addError(miembroForm, 'contains');
      } else if (miembroForm.errors) {
        this.deleteError(miembroForm, 'contains');
      }
    } else {
      this.deleteError(fechaInicioForm, 'range');
      this.deleteError(fechaFinForm, 'range');
      this.deleteError(miembroForm, 'contains');
    }
  }

  private deleteError(formControl: AbstractControl, errorName: string): void {
    if (formControl.errors) {
      delete formControl.errors[errorName];
      if (Object.keys(formControl.errors).length === 0) {
        formControl.setErrors(null);
      }
    }
  }

  private addError(formControl: AbstractControl, errorName: string): void {
    if (!formControl.errors) {
      formControl.setErrors({});
    }
    formControl.errors[errorName] = true;
    formControl.markAsTouched({ onlySelf: true });
  }

}
