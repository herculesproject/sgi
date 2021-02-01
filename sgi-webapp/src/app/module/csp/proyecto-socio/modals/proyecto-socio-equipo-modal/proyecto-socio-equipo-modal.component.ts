import { Component, Inject, OnInit } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { IProyectoSocioEquipo } from '@core/models/csp/proyecto-socio-equipo';
import { IRolProyecto } from '@core/models/csp/rol-proyecto';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { RolProyectoService } from '@core/services/csp/rol-proyecto.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { NumberValidator } from '@core/validators/number-validator';
import { NGXLogger } from 'ngx-logger';
import { merge, Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';

const MSG_ERROR_INIT = marker('csp.proyecto.socio-equipo.rol-socio.error.cargar');
const MSG_ANADIR = marker('botones.aniadir');
const MSG_ACEPTAR = marker('botones.aceptar');

export interface ProyectoEquipoSocioModalData {
  proyectoSocioEquipo: IProyectoSocioEquipo;
  selectedProyectoSocioEquipos: IProyectoSocioEquipo[];
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

  constructor(
    private readonly logger: NGXLogger,
    protected snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<ProyectoSocioEquipoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ProyectoEquipoSocioModalData,
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
        this.formGroup.get('fechaInicio').valueChanges,
        this.formGroup.get('fechaFin').valueChanges
      ).subscribe(() => this.checkRangesDates())
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
        fechaInicio: new FormControl(this.data.proyectoSocioEquipo.fechaInicio, []),
        fechaFin: new FormControl(this.data.proyectoSocioEquipo.fechaFin, []),
      },
      {
        validators: [NumberValidator.isAfter('fechaInicio', 'fechaFin')]
      }
    );
    return formGroup;
  }

  private checkRangesDates(): void {
    const persona = this.formGroup.get('persona');
    const fechaInicioForm = this.formGroup.get('fechaInicio');
    const fechaFinForm = this.formGroup.get('fechaFin');

    // Limpiamos errores
    persona.setErrors(null);
    if (fechaFinForm.errors) {
      delete fechaFinForm.errors.range;
      if (Object.keys(fechaFinForm.errors).length === 0) {
        fechaFinForm.setErrors(null);
      }
    }
    if (fechaInicioForm.errors) {
      delete fechaInicioForm.errors.range;
      if (Object.keys(fechaInicioForm.errors).length === 0) {
        fechaInicioForm.setErrors(null);
      }
    }

    const selected = this.data.selectedProyectoSocioEquipos.filter(
      element => element.persona.personaRef === persona.value.personaRef);
    if (selected.length > 0) {
      const fechaInicio: Date = fechaInicioForm.value;
      const fechaFin: Date = fechaFinForm.value;

      // Comprueba si no se indicaron fechas no haya otros registros con ellas
      const value = selected.find(element => !element.fechaInicio && !element.fechaFin);
      if (!fechaInicio && !fechaFin || value) {
        persona.setErrors({ contains: true });
        persona.markAsTouched({ onlySelf: true });
      }

      // Comprueba solapamiento de fechaFin
      for (const element of selected) {
        if (fechaFin && fechaFin >= element.fechaInicio && fechaFin <= element.fechaFin) {
          this.addErrorRange(fechaFinForm);
          break;
        }
      }

      // Comprueba solapamiento de fechaInicio
      for (const element of selected) {
        if (fechaInicio && fechaInicio >= element.fechaInicio && fechaInicio <= element.fechaFin) {
          this.addErrorRange(fechaInicioForm);
          break;
        }
      }

      // Comprueba que el rango introducido pueda contener alguno de los existentes
      for (const element of selected) {
        if (fechaInicio && fechaFinForm &&
          fechaInicio <= element.fechaInicio && fechaFin >= element.fechaFin) {
          this.addErrorRange(fechaFinForm);
          this.addErrorRange(fechaInicioForm);
          break;
        }
      }
    }
  }

  private addErrorRange(formControl: AbstractControl): void {
    if (formControl.errors) {
      formControl.errors.range = true;
    } else {
      formControl.setErrors({ range: true });
    }
    formControl.markAsTouched({ onlySelf: true });
  }
}
