import { Component, OnInit, OnDestroy } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { MatDialogRef } from '@angular/material/dialog';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { IEquipoTrabajo } from '@core/models/eti/equipo-trabajo';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { Persona } from '@core/models/sgp/persona';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';

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


  nuevaPersonaEquipo: Persona;

  constructor(
    private readonly logger: NGXLogger,
    public readonly matDialogRef: MatDialogRef<EquipoInvestigadorCrearModalComponent>
  ) { }

  ngOnInit(): void {
    this.logger.debug(EquipoInvestigadorCrearModalComponent.name, 'ngOnInit()', 'start');


    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'column';

    this.initFormGroup();
    this.logger.debug(EquipoInvestigadorCrearModalComponent.name, 'ngOnInit()', 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(EquipoInvestigadorCrearModalComponent.name, 'ngOnDestroy()', 'start');
    this.logger.debug(EquipoInvestigadorCrearModalComponent.name, 'ngOnDestroy()', 'end');
  }

  /**
   * Cierra la ventana modal y devuelve el equipoInvestigador si se ha añadido
   *
   */
  addPersonaEquipoInvestigador(): void {
    this.logger.debug(EquipoInvestigadorCrearModalComponent.name, 'addPersonaEquipoInvestigador()', 'start');

    const equipoTrabajo: IEquipoTrabajo = {
      id: null,
      peticionEvaluacion: {
        id: null,
        solicitudConvocatoriaRef: null,
        codigo: null,
        titulo: null,
        tipoActividad: null,
        fuenteFinanciacion: null,
        fechaInicio: null,
        fechaFin: null,
        resumen: null,
        valorSocial: null,
        otroValorSocial: null,
        objetivos: null,
        disMetodologico: null,
        externo: null,
        tieneFondosPropios: null,
        personaRef: null,
        nombre: null,
        primerApellido: null,
        segundoApellido: null,
        identificadorNumero: null,
        identificadorLetra: null,
        nivelAcademico: null,
        vinculacion: null,
        activo: null
      },
      personaRef: this.nuevaPersonaEquipo?.personaRef,
      nombre: this.nuevaPersonaEquipo?.nombre,
      primerApellido: this.nuevaPersonaEquipo?.primerApellido,
      segundoApellido: this.nuevaPersonaEquipo?.segundoApellido,
      identificadorNumero: this.nuevaPersonaEquipo?.identificadorNumero,
      identificadorLetra: this.nuevaPersonaEquipo?.identificadorLetra,
      nivelAcademico: this.nuevaPersonaEquipo?.nivelAcademico,
      vinculacion: this.nuevaPersonaEquipo?.vinculacion,
      isEliminable: true
    };


    this.matDialogRef.close(equipoTrabajo);
    this.logger.debug(EquipoInvestigadorCrearModalComponent.name, 'addPersonaEquipoInvestigador()', 'end');
  }


  /**
   * Setea el persona seleccionado a través del componente
   * @param persona persona seleccionada
   */
  public onSelectPersona(personaSeleccionada: Persona): void {
    personaSeleccionada.vinculacion = 'PDI';
    personaSeleccionada.nivelAcademico = 'Licenciado';
    this.nuevaPersonaEquipo = personaSeleccionada;
    this.formGroup.controls.numDocumento.setValue(`${personaSeleccionada.identificadorNumero}${personaSeleccionada.identificadorLetra}`);
    this.formGroup.controls.nombreCompleto.setValue(`${personaSeleccionada.nombre} ${personaSeleccionada.primerApellido} ${personaSeleccionada.segundoApellido}`);
    this.formGroup.controls.vinculacion.setValue(personaSeleccionada.vinculacion);
    this.formGroup.controls.nivelAcademico.setValue(personaSeleccionada.nivelAcademico);
  }

  /**
   * Inicializa el formGroup
   */
  private initFormGroup() {
    this.logger.debug(EquipoInvestigadorCrearModalComponent.name, 'initFormGroup()', 'start');
    this.formGroup = new FormGroup({
      numDocumento: new FormControl({ value: '', disabled: true }, [Validators.required]),
      nombreCompleto: new FormControl({ value: '', disabled: true }),
      vinculacion: new FormControl({ value: '', disabled: true }),
      nivelAcademico: new FormControl({ value: '', disabled: true })
    });

    this.logger.debug(EquipoInvestigadorCrearModalComponent.name, 'initFormGroup()', 'end');
  }

}
