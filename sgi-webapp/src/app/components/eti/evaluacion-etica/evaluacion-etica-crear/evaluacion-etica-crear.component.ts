import {Component, ViewChild} from '@angular/core';
import {SnackBarService} from '@core/services/snack-bar.service';
import {TraductorService} from '@core/services/traductor.service';
import {AbstractFormularioComponent} from '@shared/formularios-tabs/abstract-formulario/abstract-formulario.component';
import {NGXLogger} from 'ngx-logger';

import {EvaluacionEticaAsignacionTareasComponent,} from './evaluacion-etica-asignacion-tareas/evaluacion-etica-asignacion-tareas.component';
import {EvaluacionEticaDatosGeneralesComponent,} from './evaluacion-etica-datos-generales/evaluacion-etica-datos-generales.component';
import {EvaluacionEticaEquipoInvestigadorComponent,} from './evaluacion-etica-equipo-investigador/evaluacion-etica-equipo-investigador.component';

@Component({
  selector: 'app-evaluacion-fisica-crear',
  templateUrl: './evaluacion-etica-crear.component.html',
  styleUrls: ['./evaluacion-etica-crear.component.scss']
})
export class EvaluacionEticaCrearComponent extends AbstractFormularioComponent {
  @ViewChild('datosGeneralesComponent') datosGeneralesComponent: EvaluacionEticaDatosGeneralesComponent;
  @ViewChild('equipoInvestigadorComponent') equipoInvestigadorComponent: EvaluacionEticaEquipoInvestigadorComponent;
  @ViewChild('asignacionTareasComponent') asignacionTareasComponent: EvaluacionEticaAsignacionTareasComponent;

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly traductor: TraductorService,
    protected readonly snackBarService: SnackBarService
  ) {
    super(logger, traductor, snackBarService);
    this.textoCrear = this.traductor.getTexto('footer.evaluacionEtica.guardar');
  }

  protected inicializarTabs() {
    this.tabs.set(0, this.datosGeneralesComponent);
    this.tabs.set(1, this.equipoInvestigadorComponent);
    this.tabs.set(2, this.asignacionTareasComponent);
  }

  enviarDatos(): void {
    this.logger.debug(EvaluacionEticaCrearComponent.name, 'guardarDatos()', 'start');
    this.subscripciones.push(this.tabs.get(0).mandarPeticion().subscribe((res1) => {
      // this.tabs.get(0).actualizarDatos(res1);
      this.tabs.get(0).warning = false;
      this.subscripciones.push(this.tabs.get(1).mandarPeticion().subscribe((res2) => {
        // this.tabs.get(1).actualizarDatos(res2);
        this.tabs.get(1).warning = false;
        this.subscripciones.push(this.tabs.get(2).mandarPeticion().subscribe((res3) => {
          // this.tabs.get(2).actualizarDatos(res3);
          this.tabs.get(2).warning = false;
          this.snackBarService.mostrarMensajeSuccess('Se añadieron todos los registros correctamente');
          this.logger.debug(EvaluacionEticaCrearComponent.name, 'guardarDatos()', 'end');
        }, () => {
          this.tabs.get(2).mostrarError();
          this.funError(2);
          this.logger.error(EvaluacionEticaCrearComponent.name, 'guardarDatos()', 'end');
        }));
      }, () => {
        this.tabs.get(1).mostrarError();
        this.funError(1);
        this.logger.error(EvaluacionEticaCrearComponent.name, 'guardarDatos()', 'end');
      }));
    }, () => {
      this.tabs.get(0).mostrarError();
      this.funError(0);
      this.logger.error(EvaluacionEticaCrearComponent.name, 'guardarDatos()', 'end');
    }));
  }

  private funError(indexTab: number) {
    this.logger.debug(EvaluacionEticaCrearComponent.name, 'funError(indexTab: number)', 'start');
    this.snackBarService.mostrarMensajeSuccess('Error en Tab ' + indexTab);
    this.logger.debug(EvaluacionEticaCrearComponent.name, 'funError(indexTab: number)', 'end');
  }
}
