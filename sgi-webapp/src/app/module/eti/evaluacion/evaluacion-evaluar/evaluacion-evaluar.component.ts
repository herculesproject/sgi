import { Component, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { AbstractFormularioComponent } from '@core/component/abstract-formulario.component';
import { NGXLogger } from 'ngx-logger';

import {
  EvaluacionComentariosComponent,
} from '../evaluacion-formulario/evaluacion-comentarios/evaluacion-comentarios.component';
import {
  EvaluacionDatosMemoriaComponent,
} from '../evaluacion-formulario/evaluacion-datos-memoria/evaluacion-datos-memoria.component';
import {
  EvaluacionDocumentacionComponent,
} from '../evaluacion-formulario/evaluacion-documentacion/evaluacion-documentacion.component';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';

const MSG_BUTTON_SAVE = marker('footer.eti.evaluacion.guardar');
const MSG_SUCCESS = marker('eti.evaluacion.evaluar.correcto');
const MSG_ERROR_SAVE = marker('eti.evaluacion.evaluar.comentarios.error.crear');
const MSG_ERROR_UPDATE = marker('eti.evaluacion.evaluar.comentarios.error.actualizar');
const MSG_ERROR_DELETE = marker('eti.evaluacion.evaluar.comentarios.error.eliminar');
const MSG_ERROR_EVALUACION = marker('eti.evaluacion.evaluar.error');

@Component({
  selector: 'sgi-evaluacion-evaluar',
  templateUrl: './evaluacion-evaluar.component.html',
  styleUrls: ['./evaluacion-evaluar.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class EvaluacionEvaluarComponent extends AbstractFormularioComponent implements OnInit {
  @ViewChild('comentarios') comentarios: EvaluacionComentariosComponent;
  @ViewChild('datosMemoria') datosMemoria: EvaluacionDatosMemoriaComponent;
  @ViewChild('documentacion') documentacion: EvaluacionDocumentacionComponent;

  textoCrear = MSG_BUTTON_SAVE;

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    private readonly route: ActivatedRoute,
    private readonly evaluacionService: EvaluacionService,
    private readonly router: Router,
  ) {
    super(logger, snackBarService);
    this.logger.debug(EvaluacionComentariosComponent.name, 'constructor', 'start');
    this.logger.debug(EvaluacionComentariosComponent.name, 'constructor', 'end');
  }

  ngOnInit() {
    this.logger.debug(EvaluacionComentariosComponent.name, 'ngOnInit', 'start');
    super.ngOnInit();
    const id = this.route.snapshot.params.id;
    if (id && !isNaN(id)) {
      this.suscripciones.push(
        this.evaluacionService.findById(id).subscribe(
          (evaluacion: IEvaluacion) => {
            this.comentarios.evaluacion = evaluacion;
            this.datosMemoria.setMemoria(evaluacion);
            this.documentacion.setMemoria(evaluacion);
          }
        )
      );
    } else {
      this.snackBarService.showError(MSG_ERROR_EVALUACION);
      this.logger.error(EvaluacionComentariosComponent.name, 'ngOnInit', 'error');
    }
    this.logger.debug(EvaluacionComentariosComponent.name, 'ngOnInit', 'end');
  }

  protected initTabs(): void {
    this.logger.debug(EvaluacionComentariosComponent.name, 'inicializarTabs', 'start');
    this.tabs.set(0, this.comentarios);
    this.tabs.set(1, this.datosMemoria);
    this.tabs.set(2, this.documentacion);
    this.logger.debug(EvaluacionComentariosComponent.name, 'inicializarTabs', 'end');
  }

  protected sendData() {
    this.logger.debug(EvaluacionComentariosComponent.name, 'enviarDatos', 'start');
    this.suscripciones.push(this.comentarios.createComentariosObservable().subscribe(
      creados => {
        creados.forEach(comentario => this.comentarios.editComentario(comentario));
        this.suscripciones.push(this.comentarios.editComentariosObservable().subscribe(
          () => {
            this.suscripciones.push(this.comentarios.deleteComentariosObservable().subscribe(
              () => {
                this.snackBarService.showSuccess(MSG_SUCCESS);
                this.router.navigate(['../'], { relativeTo: this.route });
              },
              () => {
                this.snackBarService.showError(MSG_ERROR_DELETE);
                this.logger.error(EvaluacionComentariosComponent.name, 'enviarDatos', 'error');
              }
            ));
          },
          () => {
            this.snackBarService.showError(MSG_ERROR_UPDATE);
            this.logger.error(EvaluacionComentariosComponent.name, 'enviarDatos', 'error');
          }
        ));
      },
      () => {
        this.snackBarService.showError(MSG_ERROR_SAVE);
        this.logger.error(EvaluacionComentariosComponent.name, 'enviarDatos', 'error');
      }
    ));
  }
}
