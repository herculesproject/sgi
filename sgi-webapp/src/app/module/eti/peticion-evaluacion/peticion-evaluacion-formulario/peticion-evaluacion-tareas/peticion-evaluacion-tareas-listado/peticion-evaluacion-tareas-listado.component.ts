import { Component, OnInit } from '@angular/core';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject } from 'rxjs';
import { ITarea } from '@core/models/eti/tarea';
import { PeticionEvaluacionTareasEditarModalComponent } from '../peticion-evaluacion-tareas-editar-modal/peticion-evaluacion-tareas-editar-modal.component';
import { MatDialog } from '@angular/material/dialog';
import { ConvocatoriaReunionService } from '@core/services/eti/convocatoria-reunion.service';
import { GLOBAL_CONSTANTS } from '@core/utils/global-constants';
import { FragmentComponent } from '@core/component/fragment.component';
import { PeticionEvaluacionActionService } from '../../../peticion-evaluacion.action.service';
import { PeticionEvaluacionTareasFragment } from './peticion-evaluacion-tareas-listado.fragment';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { SnackBarService } from '@core/services/snack-bar.service';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { PeticionEvaluacionTareasCrearModalComponent } from '../peticion-evaluacion-tareas-crear-modal/peticion-evaluacion-tareas-crear-modal.component';
import { MatTableDataSource } from '@angular/material/table';

const MSG_ERROR_TAREA_REPETIDA = marker('eti.peticionEvaluacion.formulario.equipoInvestigador.listado.investigadorRepetido');

@Component({
  selector: 'sgi-peticion-evaluacion-tareas',
  templateUrl: './peticion-evaluacion-tareas-listado.component.html',
  styleUrls: ['./peticion-evaluacion-tareas-listado.component.scss']
})
export class PeticionEvaluacionTareasListadoComponent extends FragmentComponent implements OnInit {

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns: string[];

  tareas$: BehaviorSubject<StatusWrapper<ITarea>[]>;
  private listadoFragment: PeticionEvaluacionTareasFragment;

  datasource: MatTableDataSource<StatusWrapper<ITarea>> = new MatTableDataSource<StatusWrapper<ITarea>>();

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly convocatoriaReunionService: ConvocatoriaReunionService,
    protected readonly personaFisicaService: PersonaFisicaService,
    protected matDialog: MatDialog,
    protected readonly snackBarService: SnackBarService,
    actionService: PeticionEvaluacionActionService
  ) {
    super(actionService.FRAGMENT.TAREAS, actionService);
    this.tareas$ = (this.fragment as PeticionEvaluacionTareasFragment).tareas$;
    this.listadoFragment = this.fragment as PeticionEvaluacionTareasFragment;

    this.displayedColumns = ['equipoTrabajo.nombre', 'memoria.numRerecencia', 'tarea',
      'formacionEspecifica.nombre', 'organismo', 'acciones'];

  }

  ngOnInit(): void {
    super.ngOnInit();
    this.logger.debug(PeticionEvaluacionTareasListadoComponent.name, 'ngOnInit() - start');

    this.listadoFragment.tareas$.subscribe((tarea) => {
      this.datasource.data = tarea;
    });

    this.logger.debug(PeticionEvaluacionTareasListadoComponent.name, 'ngOnInit() - end');
  }

  /**
   * Abre la ventana modal para añadir una nueva tarea
   */
  openModalAddTarea(): void {
    this.logger.debug(PeticionEvaluacionTareasListadoComponent.name, 'openModalAddTarea() - start');

    const config = {
      width: GLOBAL_CONSTANTS.minWidthModal,
      maxHeight: GLOBAL_CONSTANTS.minHeightModal
    };

    const dialogRef = this.matDialog.open(PeticionEvaluacionTareasCrearModalComponent, config);

    dialogRef.afterClosed().subscribe(
      (tareaAniadida: ITarea) => {
        if (tareaAniadida) {
          const isRepetido =
            this.listadoFragment.tareas$.value.some(tareaWrapper =>
              tareaAniadida.equipoTrabajo.personaRef === tareaWrapper.value.equipoTrabajo.personaRef);

          if (isRepetido) {
            this.snackBarService.showError(MSG_ERROR_TAREA_REPETIDA);
            this.logger.debug(PeticionEvaluacionTareasListadoComponent.name, 'openModalAddTarea() - end');
            return;
          }

          this.listadoFragment.addTarea(tareaAniadida);

          this.logger.debug(PeticionEvaluacionTareasListadoComponent.name, 'openModalAddEquipoTrabajo() - end');
        }
      });

  }

  /**
   * Abre la ventana modal para modificar una tarea
   *
   * @param tarea tarea a modificar
   */
  openUpdateModal(asistente: StatusWrapper<ITarea>): void {
    this.logger.debug(PeticionEvaluacionTareasListadoComponent.name, 'openUpdateModal()', 'start');
    const config = {
      width: GLOBAL_CONSTANTS.minWidthModal,
      maxHeight: GLOBAL_CONSTANTS.minHeightModal,
      data: asistente.value
    };
    const dialogRef = this.matDialog.open(PeticionEvaluacionTareasEditarModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (resultado: ITarea) => {
        if (resultado) {
          asistente.setEdited();
          this.fragment.setChanges(true);
          this.fragment.setComplete(true);
        }
        this.logger.debug(PeticionEvaluacionTareasListadoComponent.name, 'openUpdateModal()', 'end');
      }
    );
  }
}
