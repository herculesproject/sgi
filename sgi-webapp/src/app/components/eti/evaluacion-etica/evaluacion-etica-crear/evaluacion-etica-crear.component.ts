import { Component, ViewChild, OnInit } from '@angular/core';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TraductorService } from '@core/services/traductor.service';
import { AbstractFormularioComponent } from '@shared/formularios-tabs/abstract-formulario/abstract-formulario.component';
import { NGXLogger } from 'ngx-logger';

import { EvaluacionEticaAsignacionTareasComponent, } from './evaluacion-etica-asignacion-tareas/evaluacion-etica-asignacion-tareas.component';
import { EvaluacionEticaDatosGeneralesComponent, } from './evaluacion-etica-datos-generales/evaluacion-etica-datos-generales.component';
import { EvaluacionEticaEquipoInvestigadorComponent, } from './evaluacion-etica-equipo-investigador/evaluacion-etica-equipo-investigador.component';

@Component({
  selector: 'app-evaluacion-fisica-crear',
  templateUrl: './evaluacion-etica-crear.component.html',
  styleUrls: ['./evaluacion-etica-crear.component.scss']
})

export class EvaluacionEticaCrearComponent extends AbstractFormularioComponent implements OnInit {
  @ViewChild('datosGeneralesComponent', { static: true }) datosGeneralesComponent: EvaluacionEticaDatosGeneralesComponent;
  @ViewChild('equipoInvestigadorComponent', { static: true }) equipoInvestigadorComponent: EvaluacionEticaEquipoInvestigadorComponent;
  @ViewChild('asignacionTareasComponent', { static: true }) asignacionTareasComponent: EvaluacionEticaAsignacionTareasComponent;

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly traductor: TraductorService,
    protected readonly snackBarService: SnackBarService
  ) {
    super(logger, traductor, snackBarService);
    this.textoCrear = this.traductor.getTexto('footer.eti.evaluacionEtica.guardar');
  }

  ngOnInit() {
    this.datosGeneralesComponent.crearFormGroup();
    this.equipoInvestigadorComponent.crearFormGroup();
    this.asignacionTareasComponent.crearFormGroup();
  }

  protected inicializarTabs() {
    this.tabs.set(0, this.datosGeneralesComponent);
    this.tabs.set(1, this.equipoInvestigadorComponent);
    this.tabs.set(2, this.asignacionTareasComponent);
  }

  enviarDatos(): void {
    this.logger.debug(EvaluacionEticaCrearComponent.name, 'guardarDatos()', 'start');

  }

  private funError(indexTab: number) {
    this.logger.debug(EvaluacionEticaCrearComponent.name, 'funError(indexTab: number)', 'start');
    this.snackBarService.mostrarMensajeSuccess('Error en Tab ' + indexTab);
    this.logger.debug(EvaluacionEticaCrearComponent.name, 'funError(indexTab: number)', 'end');
  }
}
