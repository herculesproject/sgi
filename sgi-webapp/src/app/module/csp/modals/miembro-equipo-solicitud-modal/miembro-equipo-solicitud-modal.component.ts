import { Component, Inject, OnInit } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IMiembroEquipoSolicitud } from '@core/models/csp/miembro-equipo-solicitud';
import { IRolProyecto } from '@core/models/csp/rol-proyecto';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { RolProyectoService } from '@core/services/csp/rol-proyecto.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { GLOBAL_CONSTANTS } from '@core/utils/global-constants';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { NumberValidator } from '@core/validators/number-validator';
import { IRange } from '@core/validators/range-validator';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { merge } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

const MSG_ERROR_INIT = marker('error.load');
const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const MIEMBRO_EQUIPO_SOLICITUD_ROL_PARTICIPACION_KEY = marker('csp.miembro-equipo-solicitud.rol-participacion');
const MIEMBRO_EQUIPO_SOLICITUD_MIEMBRO_KEY = marker('csp.miembro-equipo-solicitud.miembro');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface MiembroEquipoSolicitudModalData {
  titleEntity: string;
  entidad: IMiembroEquipoSolicitud;
  selectedEntidades: IMiembroEquipoSolicitud[];
  mesInicialMin: number;
  mesFinalMax: number;
  isEdit: boolean;
  readonly: boolean;
}

@Component({
  templateUrl: './miembro-equipo-solicitud-modal.component.html',
  styleUrls: ['./miembro-equipo-solicitud-modal.component.scss']
})
export class MiembroEquipoSolicitudModalComponent extends
  BaseModalComponent<MiembroEquipoSolicitudModalData, MiembroEquipoSolicitudModalComponent> implements OnInit {
  fxFlexProperties: FxFlexProperties;
  fxFlexPropertiesFullWidth: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  textSaveOrUpdate: string;

  saveDisabled = false;
  rolesProyecto: IRolProyecto[] = [];
  colectivoIdRolParticipacion: string;

  msgParamRolParticipacionEntity = {};
  msgParamMiembroEntity = {};
  msgParamEntity = {};
  title: string;

  compareRolProyecto = (option: IRolProyecto, value: IRolProyecto) => option?.id === value?.id;

  constructor(
    private readonly logger: NGXLogger,
    protected snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<MiembroEquipoSolicitudModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: MiembroEquipoSolicitudModalData,
    private personaService: PersonaService,
    private rolProyectoService: RolProyectoService,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, data);

    this.fxFlexPropertiesFullWidth = new FxFlexProperties();
    this.fxFlexPropertiesFullWidth.sm = '0 1 100%';
    this.fxFlexPropertiesFullWidth.md = '0 1 100%';
    this.fxFlexPropertiesFullWidth.gtMd = '0 1 100%';
    this.fxFlexPropertiesFullWidth.order = '1';

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties.order = '2';
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.xs = 'row';
    this.textSaveOrUpdate = this.data.entidad?.id ? MSG_ACEPTAR : MSG_ANADIR;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.loadRolProyectos();
    this.setupI18N();
    this.colectivoIdRolParticipacion = this.data.entidad?.rolProyecto?.colectivoRef;

    this.subscriptions.push(
      this.formGroup.get('rolProyecto').valueChanges
        .subscribe((rolProyecto) => {
          this.checkSelectedRol(rolProyecto);
        })
    );

    this.subscriptions.push(
      merge(
        this.formGroup.get('miembro').valueChanges,
        this.formGroup.get('mesInicio').valueChanges,
        this.formGroup.get('mesFin').valueChanges
      ).subscribe(() => {
        this.checkRangesMeses();
      })
    );
  }

  private setupI18N(): void {
    this.translate.get(
      MIEMBRO_EQUIPO_SOLICITUD_ROL_PARTICIPACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamRolParticipacionEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      MIEMBRO_EQUIPO_SOLICITUD_MIEMBRO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamMiembroEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      this.data.titleEntity,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    if (this.data.entidad.rolProyecto) {
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

  private loadRolProyectos(): void {
    const subcription = this.rolProyectoService.findAll().pipe(
      map(result => result.items)
    ).subscribe(
      res => {
        this.rolesProyecto = res;
      },
      error => {
        this.logger.error(error);
        this.snackBarService.showError(MSG_ERROR_INIT);
      }
    );
    this.subscriptions.push(subcription);
  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup(
      {
        rolProyecto: new FormControl(this.data.entidad.rolProyecto, [
          Validators.required,
          IsEntityValidator.isValid()
        ]),
        miembro: new FormControl({
          value: this.data.entidad.persona,
          disabled: !this.data.entidad.rolProyecto
        }, [Validators.required]),
        mesInicio: new FormControl(this.data.entidad.mesInicio, [
          Validators.min(this.data.mesInicialMin ?? 1),
          Validators.max(this.data.mesFinalMax ?? GLOBAL_CONSTANTS.integerMaxValue)
        ]),
        mesFin: new FormControl(this.data.entidad.mesFin, [
          Validators.min(this.data.mesInicialMin ?? 1),
          Validators.max(this.data.mesFinalMax ?? GLOBAL_CONSTANTS.integerMaxValue)
        ]),
      },
      {
        validators: [
          NumberValidator.isAfterOptional('mesInicio', 'mesFin'),
        ]
      }
    );

    if (this.data.readonly) {
      formGroup.disable();
    }

    return formGroup;
  }

  protected getDatosForm(): MiembroEquipoSolicitudModalData {
    this.data.entidad.rolProyecto = this.formGroup.get('rolProyecto').value;
    this.data.entidad.persona = this.formGroup.get('miembro').value;
    this.data.entidad.mesInicio = this.formGroup.get('mesInicio').value;
    this.data.entidad.mesFin = this.formGroup.get('mesFin').value;
    return this.data;
  }

  private checkSelectedRol(rolProyecto: IRolProyecto): void {
    this.colectivoIdRolParticipacion = rolProyecto?.colectivoRef;

    if (rolProyecto && this.formGroup.controls.miembro.value) {
      this.saveDisabled = true;
      this.subscriptions.push(
        this.personaService.isPersonaInColectivo(this.formGroup.controls.miembro.value.id, rolProyecto.colectivoRef)
          .subscribe((result) => {
            if (!result) {
              this.formGroup.controls.miembro.setValue(undefined);
            }
            this.saveDisabled = false;
          })
      );
    } else if (rolProyecto && this.formGroup.controls.miembro.disabled) {
      this.formGroup.controls.miembro.enable();
    } else if (!rolProyecto) {
      this.formGroup.controls.miembro.disable();
      this.formGroup.controls.miembro.setValue(undefined);
    }
  }

  private checkRangesMeses(): void {
    const miembroForm = this.formGroup.get('miembro');
    const mesInicioForm = this.formGroup.get('mesInicio');
    const mesFinForm = this.formGroup.get('mesFin');

    const mesInicio = mesInicioForm.value ? mesInicioForm.value : Number.MIN_VALUE;
    const mesFin = mesFinForm.value ? mesFinForm.value : Number.MAX_VALUE;
    const ranges = this.data.selectedEntidades
      .filter(element => element.persona.id === miembroForm.value?.id)
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
        this.addError(miembroForm, 'contains');
      } else if (miembroForm.errors) {
        this.deleteError(miembroForm, 'contains');
      }
    } else {
      this.deleteError(mesInicioForm, 'range');
      this.deleteError(mesFinForm, 'range');
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
