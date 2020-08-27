import { Component, ViewChild } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { AbstractTabComponent } from '@core/component/abstract-tab.component';
import { DocumentacionMemoria } from '@core/models/eti/documentacion-memoria';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { Memoria } from '@core/models/eti/memoria';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { NGXLogger } from 'ngx-logger';

import {
  DocumentacionMemoriaListadoMemoriaComponent,
} from '../../../documentacion-memoria/documentacion-memoria-listado-memoria/documentacion-memoria-listado-memoria.component';

@Component({
  selector: 'sgi-evaluacion-documentacion',
  templateUrl: './evaluacion-documentacion.component.html',
  styleUrls: ['./evaluacion-documentacion.component.scss']
})
export class EvaluacionDocumentacionComponent extends AbstractTabComponent<DocumentacionMemoria> {
  FormGroupUtil = FormGroupUtil;
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  memoria: Memoria;
  @ViewChild('documentacionMemoriaListado') documentacion: DocumentacionMemoriaListadoMemoriaComponent;

  constructor(
    protected readonly logger: NGXLogger,
  ) {
    super(logger);
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  createFormGroup(): FormGroup {
    this.logger.debug(EvaluacionDocumentacionComponent.name, `createFormGroup()`, 'start');
    this.logger.debug(EvaluacionDocumentacionComponent.name, `createFormGroup()`, 'start');
    return null;
  }

  getDatosFormulario(): DocumentacionMemoria {
    this.logger.debug(EvaluacionDocumentacionComponent.name, `getDatosFormulario()`, 'start');
    this.logger.debug(EvaluacionDocumentacionComponent.name, `getDatosFormulario()`, 'start');
    return null;
  }

  /**
   * Carga los datos de la memoria en la pestaña
   *
   * @param evaluacion Evaluación para cargar su memoria
   */
  setMemoria(evaluacion: IEvaluacion): void {
    this.logger.debug(EvaluacionDocumentacionComponent.name,
      `setMemoria(${evaluacion ? JSON.stringify(evaluacion) : evaluacion})`, 'start');
    if (evaluacion) {
      this.memoria = evaluacion.memoria;
      this.documentacion.memoriaId = this.memoria.id;
      this.documentacion.ngAfterViewInit();
    }
    this.logger.debug(EvaluacionDocumentacionComponent.name,
      `setMemoria(${evaluacion ? JSON.stringify(evaluacion) : evaluacion})`, 'end');
  }
}
