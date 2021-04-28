import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IEquipoTrabajo } from '@core/models/eti/equipo-trabajo';
import { IPersona } from '@core/models/sgp/persona';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { DatosAcademicosService } from '@core/services/sgp/datos-academicos.service';
import { VinculacionService } from '@core/services/sgp/vinculacion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { TranslateService } from '@ngx-translate/core';
import { TipoColectivo } from '@shared/select-persona/select-persona.component';
import { Subscription } from 'rxjs';

const PERSONA_KEY = marker('eti.peticion-evaluacion.equipo-investigador.persona');
const MSG_ERROR_FORM = marker('error.form-group');

@Component({
  selector: 'sgi-equipo-investigador-crear-modal',
  templateUrl: './equipo-investigador-crear-modal.component.html',
  styleUrls: ['./equipo-investigador-crear-modal.component.scss']
})
export class EquipoInvestigadorCrearModalComponent implements OnInit, OnDestroy {

  FormGroupUtil = FormGroupUtil;
  formGroup: FormGroup;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  msgParamEntity = {};

  private subscriptions: Subscription[] = [];

  get tipoColectivoPersona() {
    return TipoColectivo.EQUIPO_TRABAJO_ETICA;
  }

  constructor(
    public readonly matDialogRef: MatDialogRef<EquipoInvestigadorCrearModalComponent>,
    private readonly translate: TranslateService,
    private readonly snackBarService: SnackBarService,
    private readonly datosAcademicosService: DatosAcademicosService,
    private readonly vinculacionService: VinculacionService
  ) { }

  ngOnInit(): void {
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'column';

    this.setupI18N();

    this.initFormGroup();
  }

  ngOnDestroy(): void {
    this.subscriptions?.forEach(suscription => suscription.unsubscribe());
  }

  private setupI18N(): void {
    this.translate.get(
      PERSONA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });
  }

  /**
   * Cierra la ventana modal y devuelve el equipoInvestigador si se ha añadido
   *
   */
  addPersonaEquipoInvestigador(): void {
    const equipoTrabajo: IEquipoTrabajo = {
      id: null,
      peticionEvaluacion: null,
      persona: this.formGroup.controls.persona.value
    };
    if (FormGroupUtil.valid(this.formGroup)) {
      this.matDialogRef.close(equipoTrabajo);
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM);
    }
  }

  /**
   * Setea la persona seleccionada a través del componente
   * @param personaSeleccionada persona seleccionada
   */
  private onSelectPersona(personaSeleccionada: IPersona): void {
    this.formGroup.controls.numDocumento.setValue(personaSeleccionada.numeroDocumento);
    this.formGroup.controls.nombreCompleto.setValue(`${personaSeleccionada.nombre} ${personaSeleccionada.apellidos}`);

    this.subscriptions.push(
      this.vinculacionService.findByPersonaId(personaSeleccionada.id)
        .subscribe((vinculacion) => {
          personaSeleccionada.vinculacion = vinculacion;
          this.formGroup.controls.vinculacion.setValue(vinculacion?.categoriaProfesional.nombre);
        })
    );

    this.subscriptions.push(
      this.datosAcademicosService.findByPersonaId(personaSeleccionada.id)
        .subscribe((datosAcademicos) => {
          personaSeleccionada.datosAcademicos = datosAcademicos;
          this.formGroup.controls.nivelAcademico.setValue(datosAcademicos?.nivelAcademico.nombre);
        })
    );
  }

  /**
   * Inicializa el formGroup
   */
  private initFormGroup() {
    this.formGroup = new FormGroup({
      numDocumento: new FormControl({ value: '', disabled: true }),
      nombreCompleto: new FormControl({ value: '', disabled: true }),
      vinculacion: new FormControl({ value: '', disabled: true }),
      nivelAcademico: new FormControl({ value: '', disabled: true }),
      persona: new FormControl(null, [Validators.required])
    });

    this.subscriptions.push(
      this.formGroup.controls.persona.valueChanges.subscribe((value) => {
        this.onSelectPersona(value);
      })
    );
  }

}
