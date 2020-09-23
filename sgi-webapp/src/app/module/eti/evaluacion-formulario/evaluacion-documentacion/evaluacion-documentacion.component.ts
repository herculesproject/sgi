import { Component, ViewChild, AfterViewInit } from '@angular/core';
import { DocumentacionMemoria } from '@core/models/eti/documentacion-memoria';
import { NGXLogger } from 'ngx-logger';

import { FormFragmentComponent } from '@core/component/fragment.component';
import { DocumentacionMemoriaListadoMemoriaComponent } from '../../documentacion-memoria/documentacion-memoria-listado-memoria/documentacion-memoria-listado-memoria.component';
import { EvaluacionEvaluadorEvaluarActionService } from '../../evaluacion-evaluador/evaluacion-evaluador.action.service';


@Component({
  selector: 'sgi-evaluacion-documentacion',
  templateUrl: './evaluacion-documentacion.component.html',
  styleUrls: ['./evaluacion-documentacion.component.scss']
})
export class EvaluacionDocumentacionComponent extends FormFragmentComponent<DocumentacionMemoria> implements AfterViewInit {

  @ViewChild('documentacionMemoriaListado') documentacion: DocumentacionMemoriaListadoMemoriaComponent;

  constructor(
    protected readonly logger: NGXLogger,
    private actionService: EvaluacionEvaluadorEvaluarActionService
  ) {
    super(actionService.FRAGMENT.DOCUMENTACION, actionService);
  }

  ngAfterViewInit() {
    this.documentacion.memoriaId = this.actionService.getEvaluacion().memoria.id;
    this.documentacion.ngAfterViewInit();
  }
}
