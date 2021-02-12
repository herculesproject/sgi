import { Component, Inject, OnInit } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { IRolProyecto } from '@core/models/csp/rol-proyecto';
import { ISolicitudProyectoEquipoSocio } from '@core/models/csp/solicitud-proyecto-equipo-socio';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { RolProyectoService } from '@core/services/csp/rol-proyecto.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { GLOBAL_CONSTANTS } from '@core/utils/global-constants';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { NumberValidator } from '@core/validators/number-validator';
import { NGXLogger } from 'ngx-logger';
import { merge, Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';

const MSG_ERROR_INIT = marker('csp.solicitud.equipo.socio.equipo.socio.rol.error.cargar');
const MSG_ANADIR = marker('botones.aniadir');
const MSG_ACEPTAR = marker('botones.aceptar');

export interface SolicitudProyectoEquipoSocioModalData {
  solicitudProyectoEquipoSocio: ISolicitudProyectoEquipoSocio;
  selectedProyectoEquipoSocios: ISolicitudProyectoEquipoSocio[];
  mesInicioSolicitudProyectoSocio: number;
  mesFinSolicitudProyectoSocio: number;
  isEdit: boolean;
}

@Component({
  templateUrl: './solicitud-proyecto-socio-equipo-socio-modal.component.html',
  styleUrls: ['./solicitud-proyecto-socio-equipo-socio-modal.component.scss']
})
export class SolicitudProyectoSocioEquipoSocioModalComponent extends
  BaseModalComponent<SolicitudProyectoEquipoSocioModalData, SolicitudProyectoSocioEquipoSocioModalComponent> implements OnInit {
  private rolProyectoFiltered: IRolProyecto[] = [];
  rolProyectos$: Observable<IRolProyecto[]>;
  textSaveOrUpdate: string;

  constructor(
    private readonly logger: NGXLogger,
    protected snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<SolicitudProyectoSocioEquipoSocioModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: SolicitudProyectoEquipoSocioModalData,
    private rolProyectoService: RolProyectoService
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

  protected getDatosForm(): SolicitudProyectoEquipoSocioModalData {
    this.data.solicitudProyectoEquipoSocio.rolProyecto = this.formGroup.get('rolProyecto').value;
    this.data.solicitudProyectoEquipoSocio.persona = this.formGroup.get('persona').value;
    this.data.solicitudProyectoEquipoSocio.mesInicio = this.formGroup.get('mesInicio').value;
    this.data.solicitudProyectoEquipoSocio.mesFin = this.formGroup.get('mesFin').value;
    return this.data;
  }

  protected getFormGroup(): FormGroup {
    const mesInicio = this.data.mesInicioSolicitudProyectoSocio;
    const mesFinal = this.data.mesFinSolicitudProyectoSocio;
    const duracion = this.data.solicitudProyectoEquipoSocio?.solicitudProyectoSocio?.solicitudProyectoDatos?.duracion;
    const formGroup = new FormGroup(
      {
        rolProyecto: new FormControl(this.data.solicitudProyectoEquipoSocio.rolProyecto, [
          Validators.required,
          IsEntityValidator.isValid()
        ]),
        persona: new FormControl({
          value: this.data.solicitudProyectoEquipoSocio.persona,
          disabled: this.data.isEdit
        }, [Validators.required]),
        mesInicio: new FormControl(this.data.solicitudProyectoEquipoSocio.mesInicio, [
          Validators.min(mesInicio ? mesInicio : 1),
          Validators.max(mesFinal ? mesFinal : (isNaN(duracion) ? GLOBAL_CONSTANTS.integerMaxValue : duracion)),
        ]),
        mesFin: new FormControl(this.data.solicitudProyectoEquipoSocio.mesFin, [
          Validators.min(mesInicio ? mesInicio : 1),
          Validators.max(mesFinal ? mesFinal : (isNaN(duracion) ? GLOBAL_CONSTANTS.integerMaxValue : duracion)),
        ]),
      },
      {
        validators: [NumberValidator.isAfter('mesInicio', 'mesFin')]
      }
    );
    return formGroup;
  }

  private checkRangesMeses(): void {
    const persona = this.formGroup.get('persona');
    const mesInicioForm = this.formGroup.get('mesInicio');
    const mesFinForm = this.formGroup.get('mesFin');

    // Limpiamos errores
    persona.setErrors(null);
    if (mesFinForm.errors) {
      delete mesFinForm.errors.range;
      if (Object.keys(mesFinForm.errors).length === 0) {
        mesFinForm.setErrors(null);
      }
    }
    if (mesInicioForm.errors) {
      delete mesInicioForm.errors.range;
      if (Object.keys(mesInicioForm.errors).length === 0) {
        mesInicioForm.setErrors(null);
      }
    }

    const selected = this.data.selectedProyectoEquipoSocios.filter(
      element => element.persona.personaRef === persona.value?.personaRef);
    if (selected.length > 0) {
      // Comprueba si no se indicaron fechas no haya otros registros con ellas
      const mesInicio = mesInicioForm.value;
      const mesFin = mesFinForm.value;
      const value = selected.find(element => !element.mesInicio && !element.mesFin);
      if (!mesInicio && !mesFin || value) {
        persona.setErrors({ contains: true });
        persona.markAsTouched({ onlySelf: true });
      } else {
        persona.setErrors(null);
      }

      if (mesFin === 0 || mesInicio === 0) {
        persona.setErrors(null);
      }

      // Comprueba solapamiento de mesFin
      for (const proyectoEquipo of selected) {
        if (mesFin && mesFin >= proyectoEquipo.mesInicio && mesFin <= proyectoEquipo.mesFin) {
          this.addErrorRange(mesFinForm);
          break;
        }
      }

      // Comprueba solapamiento de mesInicio
      for (const proyectoEquipo of selected) {
        if (mesInicio && mesInicio >= proyectoEquipo.mesInicio && mesInicio <= proyectoEquipo.mesFin) {
          this.addErrorRange(mesInicioForm);
          break;
        }
      }

      // Comprueba que el rango introducido pueda contener alguno de los existentes
      for (const proyectoEquipo of selected) {
        if (mesInicio && mesFin &&
          mesInicio <= proyectoEquipo.mesInicio && mesFin >= proyectoEquipo.mesFin) {
          this.addErrorRange(mesFinForm);
          this.addErrorRange(mesInicioForm);
          break;
        }
      }
    }
  }

  private addErrorRange(formControl: AbstractControl) {
    if (formControl.errors) {
      formControl.errors.range = true;
    } else {
      formControl.setErrors({ range: true });
    }
    formControl.markAsTouched({ onlySelf: true });
  }
}
