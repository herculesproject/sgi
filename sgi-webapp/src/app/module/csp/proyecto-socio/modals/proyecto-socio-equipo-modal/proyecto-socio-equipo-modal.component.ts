import { Component, Inject, OnInit } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoSocioEquipo } from '@core/models/csp/proyecto-socio-equipo';
import { IRolProyecto } from '@core/models/csp/rol-proyecto';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { RolProyectoService } from '@core/services/csp/rol-proyecto.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { DateValidator } from '@core/validators/date-validator';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { NumberValidator } from '@core/validators/number-validator';
import { IRange } from '@core/validators/range-validator';
import { TranslateService } from '@ngx-translate/core';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { merge, Observable } from 'rxjs';
import { map, startWith, switchMap } from 'rxjs/operators';

const MSG_ERROR_INIT = marker('error.load');
const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const PROYECTO_SOCIO_EQUIPO_FECHA_FIN_KEY = marker('csp.proyecto-socio.equipo.fecha-fin-participacion');
const PROYECTO_SOCIO_EQUIPO_FECHA_INICIO_KEY = marker('csp.proyecto-socio.equipo.fecha-inicio-participacion');
const PROYECTO_SOCIO_EQUIPO_PERSONA_KEY = marker('csp.proyecto-socio.equipo.persona');
const PROYECTO_SOCIO_EQUIPO_ROL_PARTICIPACION_KEY = marker('csp.proyecto-socio.equipo.rol-proyecto.participacion');
const PROYECTO_SOCIO_KEY = marker('csp.proyecto-socio');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface ProyectoEquipoSocioModalData {
  proyectoSocioEquipo: IProyectoSocioEquipo;
  selectedProyectoSocioEquipos: IProyectoSocioEquipo[];
  fechaInicioProyectoSocio: DateTime;
  fechaFinProyectoSocio: DateTime;
  isEdit: boolean;
}

@Component({
  templateUrl: './proyecto-socio-equipo-modal.component.html',
  styleUrls: ['./proyecto-socio-equipo-modal.component.scss']
})
export class ProyectoSocioEquipoModalComponent extends
  BaseModalComponent<ProyectoEquipoSocioModalData, ProyectoSocioEquipoModalComponent> implements OnInit {
  private rolProyectoFiltered: IRolProyecto[] = [];
  rolProyectos$: Observable<IRolProyecto[]>;
  textSaveOrUpdate: string;

  msgParamFechaFinEntity = {};
  msgParamFechaInicioEntity = {};
  msgParamPersonaEntity = {};
  msgParamRolParticipacionEntity = {};
  title: string;

  constructor(
    private readonly logger: NGXLogger,
    protected snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<ProyectoSocioEquipoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ProyectoEquipoSocioModalData,
    private rolProyectoService: RolProyectoService,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, data);
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties.order = '2';
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.xs = 'row';
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.loadRolProyectos();

    this.setupI18N();

    this.textSaveOrUpdate = this.data.isEdit ? MSG_ACEPTAR : MSG_ANADIR;
    this.subscriptions.push(
      merge(
        this.formGroup.get('persona').valueChanges,
        this.formGroup.get('fechaInicio').valueChanges,
        this.formGroup.get('fechaFin').valueChanges
      ).subscribe(() => this.checkRangesDates())
    );
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_SOCIO_EQUIPO_FECHA_FIN_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaFinEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });

    this.translate.get(
      PROYECTO_SOCIO_EQUIPO_FECHA_INICIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaInicioEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });

    this.translate.get(
      PROYECTO_SOCIO_EQUIPO_PERSONA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamPersonaEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      PROYECTO_SOCIO_EQUIPO_ROL_PARTICIPACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamRolParticipacionEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    if (this.data.isEdit) {
      this.translate.get(
        PROYECTO_SOCIO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        PROYECTO_SOCIO_KEY,
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



  private loadRolProyectos(): void {
    const subcription = this.rolProyectoService.findAll().pipe(
      map(result => result.items)
    ).subscribe(
      res => {
        this.rolProyectoFiltered = res;
        this.rolProyectos$ = this.formGroup.get('rolProyecto').valueChanges.pipe(
          startWith(''),
          map(value => this.filtroRolProyecto(value))
        );
      },
      error => {
        this.logger.error(error);
        this.snackBarService.showError(MSG_ERROR_INIT);
      }
    );
    this.subscriptions.push(subcription);
  }

  private filtroRolProyecto(value: string): IRolProyecto[] {
    const filterValue = value.toString().toLowerCase();
    return this.rolProyectoFiltered.filter(
      rolProyecto => rolProyecto.nombre.toLowerCase().includes(filterValue));
  }

  getNombreRolProyecto(rolProyecto?: IRolProyecto): string {
    return typeof rolProyecto === 'string' ? rolProyecto : rolProyecto?.nombre;
  }

  protected getDatosForm(): ProyectoEquipoSocioModalData {
    this.data.proyectoSocioEquipo.rolProyecto = this.formGroup.get('rolProyecto').value;
    this.data.proyectoSocioEquipo.persona = this.formGroup.get('persona').value;
    this.data.proyectoSocioEquipo.fechaFin = this.formGroup.get('fechaFin').value;
    this.data.proyectoSocioEquipo.fechaInicio = this.formGroup.get('fechaInicio').value;
    return this.data;
  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup(
      {
        rolProyecto: new FormControl(this.data.proyectoSocioEquipo.rolProyecto, [
          Validators.required,
          IsEntityValidator.isValid()
        ]),
        persona: new FormControl({
          value: this.data.proyectoSocioEquipo.persona,
          disabled: this.data.isEdit
        }, [Validators.required]),
        fechaInicio: new FormControl(this.data.proyectoSocioEquipo.fechaInicio, [
          DateValidator.minDate(this.data.fechaInicioProyectoSocio),
          DateValidator.maxDate(this.data.fechaFinProyectoSocio)
        ]),
        fechaFin: new FormControl(this.data.proyectoSocioEquipo.fechaFin, [
          DateValidator.minDate(this.data.fechaInicioProyectoSocio),
          DateValidator.maxDate(this.data.fechaFinProyectoSocio)
        ]),
      },
      {
        validators: [NumberValidator.isAfterOptional('fechaInicio', 'fechaFin')]
      }
    );
    return formGroup;
  }

  private checkRangesDates(): void {
    const personaForm = this.formGroup.get('persona');
    const fechaInicioForm = this.formGroup.get('fechaInicio');
    const fechaFinForm = this.formGroup.get('fechaFin');

    const fechaInicio = fechaInicioForm.value ? fechaInicioForm.value.toMillis() : Number.MIN_VALUE;
    const fechaFin = fechaFinForm.value ? fechaFinForm.value.toMillis() : Number.MAX_VALUE;
    const ranges = this.data.selectedProyectoSocioEquipos
      .filter(element => element.persona.personaRef === personaForm.value?.personaRef)
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
        this.addError(personaForm, 'contains');
      } else if (personaForm.errors) {
        this.deleteError(personaForm, 'contains');
      }
    } else {
      this.deleteError(fechaInicioForm, 'range');
      this.deleteError(fechaFinForm, 'range');
      this.deleteError(personaForm, 'contains');
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
