import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { AbstractTabComponent } from '@core/component/abstract-tab.component';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { Memoria } from '@core/models/eti/memoria';
import { Persona } from '@core/models/sgp/persona';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { NGXLogger } from 'ngx-logger';

import {
  EvaluacionListadoAnteriorMemoriaComponent,
} from '../evaluacion-listado-anterior-memoria/evaluacion-listado-anterior-memoria.component';


@Component({
  selector: 'sgi-evaluacion-datos-memoria',
  templateUrl: './evaluacion-datos-memoria.component.html',
  styleUrls: ['./evaluacion-datos-memoria.component.scss']
})
export class EvaluacionDatosMemoriaComponent extends AbstractTabComponent<Memoria> implements OnInit {
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexPropertiesInline: FxFlexProperties;

  evaluacion: IEvaluacion;
  memoria: Memoria;
  @ViewChild('evaluaciones') evaluaciones: EvaluacionListadoAnteriorMemoriaComponent;

  constructor(
    protected readonly logger: NGXLogger,
    private readonly personaFisicaService: PersonaFisicaService
  ) {
    super(logger);
    this.logger.debug(EvaluacionDatosMemoriaComponent.name, 'constructor()', 'start');
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(32%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxFlexPropertiesInline = new FxFlexProperties();
    this.fxFlexPropertiesInline.sm = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.md = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.order = '3';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

    this.logger.debug(EvaluacionDatosMemoriaComponent.name, 'constructor()', 'end');
  }

  createFormGroup(): FormGroup {
    this.logger.debug(EvaluacionDatosMemoriaComponent.name, 'crearFormGroup()', 'start');
    const formGroup = new FormGroup({
      comite: new FormControl({ value: '', disabled: true }),
      fechaEvaluacion: new FormControl({ value: '', disabled: true }),
      referenciaMemoria: new FormControl({ value: '', disabled: true }),
      solicitante: new FormControl({ value: '', disabled: true }),
      version: new FormControl({ value: '', disabled: true })
    });
    this.logger.debug(EvaluacionDatosMemoriaComponent.name, 'crearFormGroup()', 'end');
    return formGroup;
  }

  /**
   * Carga los datos de la memoria en la pestaña
   *
   * @param evaluacion Evaluación para cargar su memoria
   */
  setMemoria(evaluacion: IEvaluacion): void {
    this.logger.debug(EvaluacionDatosMemoriaComponent.name,
      `setMemoria(${evaluacion ? JSON.stringify(evaluacion) : evaluacion})`, 'start');
    if (evaluacion) {
      this.evaluacion = evaluacion;
      this.memoria = evaluacion.memoria;
      this.loadSolicitante();
      this.loadEvaluacionesAnteriores();
      FormGroupUtil.setValue(this.formGroup, 'comite', this.memoria?.comite?.comite);
      FormGroupUtil.setValue(this.formGroup, 'fechaEvaluacion', this.memoria?.fechaEnvioSecretaria);
      FormGroupUtil.setValue(this.formGroup, 'referenciaMemoria', this.memoria?.numReferencia);
      FormGroupUtil.setValue(this.formGroup, 'version', this.memoria?.version);
    }
    this.logger.debug(EvaluacionDatosMemoriaComponent.name,
      `setMemoria(${evaluacion ? JSON.stringify(evaluacion) : evaluacion})`, 'end');
  }

  getDatosFormulario(): Memoria {
    this.logger.debug(EvaluacionDatosMemoriaComponent.name, `getDatosFormulario()`, 'start');
    this.logger.debug(EvaluacionDatosMemoriaComponent.name, `getDatosFormulario()`, 'end');
    return null;
  }

  /**
   * Carga los datos de los solicitantes de las evaluaciones
   */
  private loadSolicitante(): void {
    this.logger.debug(EvaluacionDatosMemoriaComponent.name, `loadSolicitante()`, 'start');
    const usuarioRef = this.memoria?.personaRef;
    if (usuarioRef) {
      this.suscripciones.push(
        this.personaFisicaService.getInformacionBasica(usuarioRef).subscribe(
          (persona: Persona) =>
            FormGroupUtil.setValue(this.formGroup, 'solicitante',
              `${persona?.nombre} ${persona?.primerApellido} ${persona?.segundoApellido}`),
          () => FormGroupUtil.setValue(this.formGroup, 'solicitante', usuarioRef)
        )
      );
    }
    this.logger.debug(EvaluacionDatosMemoriaComponent.name, `findSolicitante()`, 'end');
  }

  /**
   * Carga la evaluaciones anteriores relacionadas con la memoria
   */
  private loadEvaluacionesAnteriores(): void {
    this.logger.debug(EvaluacionDatosMemoriaComponent.name, 'loadEvaluacionesAnteriores()', 'start');
    this.evaluaciones.memoriaId = this.memoria?.id;
    this.evaluaciones.evaluacionId = this.evaluacion?.id;
    this.evaluaciones.ngAfterViewInit();
    this.logger.debug(EvaluacionDatosMemoriaComponent.name, 'loadEvaluacionesAnteriores()', 'end');
  }
}
