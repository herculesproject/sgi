import { Component, Inject, OnInit } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IRolProyecto } from '@core/models/csp/rol-proyecto';
import { ISolicitudProyectoSocioEquipo } from '@core/models/csp/solicitud-proyecto-socio-equipo';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { RolProyectoService } from '@core/services/csp/rol-proyecto.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { GLOBAL_CONSTANTS } from '@core/utils/global-constants';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { NumberValidator } from '@core/validators/number-validator';
import { IRange } from '@core/validators/range-validator';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { merge, Observable } from 'rxjs';
import { map, startWith, switchMap } from 'rxjs/operators';

const MSG_ERROR_INIT = marker('error.load');
const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const PROYECTO_SOCIO_EQUIPO_KEY = marker('csp.proyecto-socio');
const PROYECTO_SOCIO_EQUIPO_PERSONA_KEY = marker('csp.proyecto-socio.equipo.persona');
const PROYECTO_SOCIO_EQUIPO_ROL_PROYECTO_KEY = marker('csp.proyecto-socio.equipo.rol-proyecto.participacion');
const SOLICITUD_PROYECTO_SOCIO_KEY = marker('csp.proyecto-socio');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface SolicitudProyectoSocioEquipoModalData {
  solicitudProyectoSocioEquipo: ISolicitudProyectoSocioEquipo;
  duracion: number;
  selectedProyectoSocioEquipo: ISolicitudProyectoSocioEquipo[];
  mesInicioSolicitudProyectoSocio: number;
  mesFinSolicitudProyectoSocio: number;
  isEdit: boolean;
  readonly: boolean;
}

@Component({
  templateUrl: './solicitud-proyecto-socio-equipo-modal.component.html',
  styleUrls: ['./solicitud-proyecto-socio-equipo-modal.component.scss']
})
export class SolicitudProyectoSocioEquipoModalComponent extends
  BaseModalComponent<SolicitudProyectoSocioEquipoModalData, SolicitudProyectoSocioEquipoModalComponent> implements OnInit {
  private rolProyectoFiltered: IRolProyecto[] = [];
  rolProyectos$: Observable<IRolProyecto[]>;
  textSaveOrUpdate: string;

  msgParamPersonaEntity = {};
  msgParamRolProyectoEntity = {};
  msgParamEntity = {};
  title: string;

  constructor(
    private readonly logger: NGXLogger,
    protected snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<SolicitudProyectoSocioEquipoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: SolicitudProyectoSocioEquipoModalData,
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
        this.formGroup.get('mesInicio').valueChanges,
        this.formGroup.get('mesFin').valueChanges
      ).subscribe(
        () => this.checkRangesMeses()
      )
    );
  }

  private setupI18N(): void {

    this.translate.get(
      PROYECTO_SOCIO_EQUIPO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      PROYECTO_SOCIO_EQUIPO_PERSONA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamPersonaEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      PROYECTO_SOCIO_EQUIPO_ROL_PROYECTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamRolProyectoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    if (this.data.solicitudProyectoSocioEquipo.rolProyecto) {
      this.translate.get(
        SOLICITUD_PROYECTO_SOCIO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        SOLICITUD_PROYECTO_SOCIO_KEY,
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

  protected getDatosForm(): SolicitudProyectoSocioEquipoModalData {
    this.data.solicitudProyectoSocioEquipo.rolProyecto = this.formGroup.get('rolProyecto').value;
    this.data.solicitudProyectoSocioEquipo.persona = this.formGroup.get('persona').value;
    this.data.solicitudProyectoSocioEquipo.mesInicio = this.formGroup.get('mesInicio').value;
    this.data.solicitudProyectoSocioEquipo.mesFin = this.formGroup.get('mesFin').value;
    return this.data;
  }

  protected getFormGroup(): FormGroup {
    const mesInicio = this.data.mesInicioSolicitudProyectoSocio;
    const mesFinal = this.data.mesFinSolicitudProyectoSocio;
    const duracion = this.data.duracion;
    const formGroup = new FormGroup(
      {
        rolProyecto: new FormControl(this.data.solicitudProyectoSocioEquipo.rolProyecto, [
          Validators.required,
          IsEntityValidator.isValid()
        ]),
        persona: new FormControl({
          value: this.data.solicitudProyectoSocioEquipo.persona,
          disabled: this.data.isEdit
        }, [Validators.required]),
        mesInicio: new FormControl(this.data.solicitudProyectoSocioEquipo.mesInicio, [
          Validators.min(mesInicio ? mesInicio : 1),
          Validators.max(mesFinal ? mesFinal : (isNaN(duracion) ? GLOBAL_CONSTANTS.integerMaxValue : duracion)),
        ]),
        mesFin: new FormControl(this.data.solicitudProyectoSocioEquipo.mesFin, [
          Validators.min(mesInicio ? mesInicio : 1),
          Validators.max(mesFinal ? mesFinal : (isNaN(duracion) ? GLOBAL_CONSTANTS.integerMaxValue : duracion)),
        ]),
      },
      {
        validators: [NumberValidator.isAfterOptional('mesInicio', 'mesFin')]
      }
    );

    if (this.data.readonly) {
      formGroup.disable();
    }

    return formGroup;
  }

  private checkRangesMeses(): void {
    const personaForm = this.formGroup.get('persona');
    const mesInicioForm = this.formGroup.get('mesInicio');
    const mesFinForm = this.formGroup.get('mesFin');

    const mesInicio = mesInicioForm.value ? mesInicioForm.value : Number.MIN_VALUE;
    const mesFin = mesFinForm.value ? mesFinForm.value : Number.MAX_VALUE;

    const ranges = this.data.selectedProyectoSocioEquipo.filter(
      element => element.persona.personaRef === personaForm.value?.personaRef)
      .map(solicitudProyectoSocio => {
        const range: IRange = {
          inicio: solicitudProyectoSocio.mesInicio ? solicitudProyectoSocio.mesInicio : Number.MIN_VALUE,
          fin: solicitudProyectoSocio.mesFin ? solicitudProyectoSocio.mesFin : Number.MAX_VALUE
        };
        return range;
      });
    if (ranges.some(range => (mesInicio <= range.fin && range.inicio <= mesFin))) {
      if (mesInicioForm.value) {
        this.addError(mesInicioForm, 'range');
      }
      if (mesFinForm.value) {
        this.addError(mesFinForm, 'range');
      }
      if (!mesInicioForm.value && !mesFinForm.value) {
        this.addError(personaForm, 'contains');
      } else if (personaForm.errors) {
        this.deleteError(personaForm, 'contains');
      }
    } else {
      this.deleteError(mesInicioForm, 'range');
      this.deleteError(mesFinForm, 'range');
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
