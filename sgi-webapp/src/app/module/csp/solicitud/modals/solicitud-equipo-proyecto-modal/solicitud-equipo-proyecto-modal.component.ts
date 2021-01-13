import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { IRolProyecto } from '@core/models/csp/rol-proyecto';
import { ISolicitudProyectoEquipo } from '@core/models/csp/solicitud-proyecto-equipo';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { RolProyectoService } from '@core/services/csp/rol-proyecto.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { NumberValidator } from '@core/validators/number-validator';
import { NGXLogger } from 'ngx-logger';
import { Observable, range, merge } from 'rxjs';
import { map, startWith, tap } from 'rxjs/operators';

const MSG_ERROR_INIT = marker('csp.solicitud.equipo.proyecto.rol.error.cargar');
const TEXT_USER_TITLE = marker('eti.buscarSolicitante.titulo');
const TEXT_USER_BUTTON = marker('eti.buscarSolicitante.boton.buscar');
const MSG_ANADIR = marker('botones.aniadir');
const MSG_ACEPTAR = marker('botones.aceptar');

export interface EquipoProyectoModalData {
  solicitudProyectoEquipo: ISolicitudProyectoEquipo;
  selectedProyectoEquipos: ISolicitudProyectoEquipo[];
  isEdit: boolean;
}

@Component({
  templateUrl: './solicitud-equipo-proyecto-modal.component.html',
  styleUrls: ['./solicitud-equipo-proyecto-modal.component.scss']
})
export class SolicitudEquipoProyectoModalComponent extends
  BaseModalComponent<EquipoProyectoModalData, SolicitudEquipoProyectoModalComponent> implements OnInit {
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  textSaveOrUpdate: string;

  private rolProyectoFiltered: IRolProyecto[] = [];
  rolProyectos$: Observable<IRolProyecto[]>;

  constructor(
    protected logger: NGXLogger,
    protected snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<SolicitudEquipoProyectoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: EquipoProyectoModalData,
    private rolProyectoService: RolProyectoService
  ) {
    super(logger, snackBarService, matDialogRef, data);
    this.logger.debug(SolicitudEquipoProyectoModalComponent.name, 'constructor()', 'start');
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties.order = '2';
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.xs = 'row';
    this.textSaveOrUpdate = this.data.solicitudProyectoEquipo?.id ? MSG_ACEPTAR : MSG_ANADIR;
    this.logger.debug(SolicitudEquipoProyectoModalComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(SolicitudEquipoProyectoModalComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();
    this.loadRolProyectos();
    this.subscriptions.push(
      merge(
        this.formGroup.get('persona').valueChanges,
        this.formGroup.get('mesInicio').valueChanges,
        this.formGroup.get('mesFin').valueChanges
      ).pipe(
        tap(() => this.checkRangesMeses())
      ).subscribe()
    );
    this.logger.debug(SolicitudEquipoProyectoModalComponent.name, 'ngOnInit()', 'start');
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
        this.snackBarService.showError(MSG_ERROR_INIT);
        this.logger.error(SolicitudEquipoProyectoModalComponent.name, `loadUnidadesGestion()`, error);
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

  protected getFormGroup(): FormGroup {
    this.logger.debug(SolicitudEquipoProyectoModalComponent.name, `getFormGroup()`, 'start');
    const formGroup = new FormGroup(
      {
        rolProyecto: new FormControl(this.data.solicitudProyectoEquipo.rolProyecto, [
          Validators.required,
          IsEntityValidator.isValid()
        ]),
        persona: new FormControl({
          value: this.data.solicitudProyectoEquipo.persona,
          disabled: this.data.isEdit
        }, [Validators.required]),
        mesInicio: new FormControl(this.data.solicitudProyectoEquipo.mesInicio, [Validators.min(1), Validators.max(9999)]),
        mesFin: new FormControl(this.data.solicitudProyectoEquipo.mesFin, [Validators.min(1), Validators.max(9999)]),
      },
      {
        validators: [NumberValidator.isAfter('mesInicio', 'mesFin')]
      }
    );
    this.logger.debug(SolicitudEquipoProyectoModalComponent.name, `getFormGroup()`, 'end');
    return formGroup;
  }

  protected getDatosForm(): EquipoProyectoModalData {
    this.logger.debug(SolicitudEquipoProyectoModalComponent.name, `getDatosForm()`, 'start');
    this.data.solicitudProyectoEquipo.rolProyecto = this.formGroup.get('rolProyecto').value;
    this.data.solicitudProyectoEquipo.persona = this.formGroup.get('persona').value;
    this.data.solicitudProyectoEquipo.mesInicio = this.formGroup.get('mesInicio').value;
    this.data.solicitudProyectoEquipo.mesFin = this.formGroup.get('mesFin').value;
    this.logger.debug(SolicitudEquipoProyectoModalComponent.name, `getDatosForm()`, 'end');
    return this.data;
  }

  private checkRangesMeses(): void {
    this.logger.debug(SolicitudEquipoProyectoModalComponent.name, `checkRangesMeses()`, 'start');
    const persona = this.formGroup.get('persona');
    const mesInicioForm = this.formGroup.get('mesInicio');
    const mesFinForm = this.formGroup.get('mesFin');

    const proyectoEquipos = this.data.selectedProyectoEquipos.filter(
      element => element.persona.personaRef === persona.value.personaRef);
    if (proyectoEquipos.length > 0) {
      const mesInicio = mesInicioForm.value;
      const mesFin = mesFinForm.value;

      // Comprueba si no se indicaron fechas no haya otros registros con ellas
      if (!mesInicio && !mesFin ||
        proyectoEquipos.find(proyectoEquipo => !proyectoEquipo.mesInicio && !proyectoEquipo.mesFin)) {
        persona.setErrors({ contains: true });
        persona.markAsTouched({ onlySelf: true });
      } else {
        persona.setErrors(null);
      }

      // Comprueba solapamiento de mesFin
      if (mesFin) {
        for (const proyectoEquipo of proyectoEquipos) {
          if (mesFin >= proyectoEquipo.mesInicio && mesFin <= proyectoEquipo.mesFin) {
            mesFinForm.setErrors({ range: true });
            mesFinForm.markAsTouched({ onlySelf: true });
            break;
          }
        }
      }

      // Comprueba solapamiento de mesInicio
      if (mesInicio) {
        for (const proyectoEquipo of proyectoEquipos) {
          if (mesInicio >= proyectoEquipo.mesInicio && mesInicio <= proyectoEquipo.mesFin) {
            mesInicioForm.setErrors({ range: true });
            mesInicioForm.markAsTouched({ onlySelf: true });
            break;
          }
        }
      }
    } else {
      persona.setErrors(null);
      mesFinForm.setErrors(null);
      mesInicioForm.setErrors(null);
    }
    this.logger.debug(SolicitudEquipoProyectoModalComponent.name, `checkRangesMeses()`, 'end');
  }
}
