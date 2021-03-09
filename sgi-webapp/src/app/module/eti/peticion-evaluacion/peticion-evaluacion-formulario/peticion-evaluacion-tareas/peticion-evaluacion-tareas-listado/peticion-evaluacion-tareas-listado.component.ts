import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { IEquipoTrabajo } from '@core/models/eti/equipo-trabajo';
import { IMemoriaPeticionEvaluacion } from '@core/models/eti/memoria-peticion-evaluacion';
import { ITarea } from '@core/models/eti/tarea';
import { ITareaWithIsEliminable } from '@core/models/eti/tarea-with-is-eliminable';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { DialogService } from '@core/services/dialog.service';
import { ConvocatoriaReunionService } from '@core/services/eti/convocatoria-reunion.service';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { GLOBAL_CONSTANTS } from '@core/utils/global-constants';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, Subscription } from 'rxjs';
import { PeticionEvaluacionActionService } from '../../../peticion-evaluacion.action.service';
import { PeticionEvaluacionTareasModalComponent } from '../peticion-evaluacion-tareas-modal/peticion-evaluacion-tareas-modal.component';
import { PeticionEvaluacionTareasFragment } from './peticion-evaluacion-tareas-listado.fragment';

const MSG_CONFIRM_DELETE = marker('eti.peticionEvaluacion.tareas.listado.eliminar');

@Component({
  selector: 'sgi-peticion-evaluacion-tareas',
  templateUrl: './peticion-evaluacion-tareas-listado.component.html',
  styleUrls: ['./peticion-evaluacion-tareas-listado.component.scss']
})
export class PeticionEvaluacionTareasListadoComponent extends FragmentComponent implements OnInit, OnDestroy {

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns: string[];

  tareas$: BehaviorSubject<StatusWrapper<ITareaWithIsEliminable>[]>;
  private listadoFragment: PeticionEvaluacionTareasFragment;
  private subscriptions: Subscription[] = [];
  elementosPagina: number[] = [5, 10, 25, 100];
  datasource: MatTableDataSource<StatusWrapper<ITareaWithIsEliminable>> = new MatTableDataSource<StatusWrapper<ITareaWithIsEliminable>>();

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    protected readonly dialogService: DialogService,
    protected readonly convocatoriaReunionService: ConvocatoriaReunionService,
    protected readonly personaFisicaService: PersonaFisicaService,
    protected matDialog: MatDialog,
    protected readonly snackBarService: SnackBarService,
    actionService: PeticionEvaluacionActionService
  ) {
    super(actionService.FRAGMENT.TAREAS, actionService);
    this.tareas$ = (this.fragment as PeticionEvaluacionTareasFragment).tareas$;
    this.listadoFragment = this.fragment as PeticionEvaluacionTareasFragment;

    this.displayedColumns = ['nombreCompleto', 'numReferencia', 'tarea',
      'formacionEspecifica', 'organismo', 'acciones'];

  }

  ngOnInit(): void {
    super.ngOnInit();
    this.datasource.paginator = this.paginator;
    this.datasource.sort = this.sort;
    this.listadoFragment.tareas$.subscribe((tarea) => {
      this.datasource.data = tarea;
    });

    this.datasource.sortingDataAccessor =
      (wrapper: StatusWrapper<ITareaWithIsEliminable>, property: string) => {
        switch (property) {
          case 'nombreCompleto':
            return wrapper.value.equipoTrabajo?.persona.nombre + ' ' + wrapper.value.equipoTrabajo?.persona.primerApellido + ' ' +
              wrapper.value.equipoTrabajo?.persona.segundoApellido;
          case 'numReferencia':
            return wrapper.value.memoria?.numReferencia;
          case 'tarea':
            return wrapper.value.tipoTarea ? wrapper.value.tipoTarea?.nombre : wrapper.value.tarea;
          case 'formacionEspecifica':
            return wrapper.value.formacionEspecifica ? wrapper.value.formacionEspecifica?.nombre : wrapper.value.formacion;
          default:
            return wrapper.value[property];
        }
      };
  }

  /**
   * Abre la ventana modal para añadir una nueva tarea
   */
  openModalAddTarea(): void {
    const tarea: ITareaWithIsEliminable = {
      eliminable: true
    } as ITareaWithIsEliminable;

    const equiposTrabajo: IEquipoTrabajo[] = this.listadoFragment.equiposTrabajo;
    const memorias: IMemoriaPeticionEvaluacion[] = this.listadoFragment.memorias;

    const config = {
      width: GLOBAL_CONSTANTS.minWidthModal,
      maxHeight: GLOBAL_CONSTANTS.minHeightModal,
      data: {
        tarea, equiposTrabajo, memorias
      },
      autoFocus: false
    };

    const dialogRef = this.matDialog.open(PeticionEvaluacionTareasModalComponent, config);

    dialogRef.afterClosed().subscribe(
      (tareaAniadida: ITareaWithIsEliminable) => {
        if (tareaAniadida) {
          this.listadoFragment.addTarea(tareaAniadida);
        }
      });
  }

  /**
   * Abre la ventana modal para modificar una tarea
   *
   * @param tarea tarea a modificar
   */
  openUpdateModal(tarea: StatusWrapper<ITareaWithIsEliminable>): void {
    const equiposTrabajo: IEquipoTrabajo[] = this.listadoFragment.equiposTrabajo;
    const memorias: IMemoriaPeticionEvaluacion[] = this.listadoFragment.memorias;

    const config = {
      width: GLOBAL_CONSTANTS.minWidthModal,
      maxHeight: GLOBAL_CONSTANTS.minHeightModal,
      data: {
        tarea: tarea.value, equiposTrabajo, memorias
      },
      autoFocus: false
    };
    const dialogRef = this.matDialog.open(PeticionEvaluacionTareasModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (resultado: ITarea) => {
        if (resultado) {
          tarea.setEdited();
          this.fragment.setChanges(true);
          this.fragment.setComplete(true);
        }
      }
    );
  }

  /**
   * Elimina la persona del equipo de trabajo.
   *
   * @param wrappedTarea equipo de trabajo a eliminar.
   */
  delete(wrappedTarea: StatusWrapper<ITareaWithIsEliminable>): void {
    const dialogSubscription = this.dialogService.showConfirmation(
      MSG_CONFIRM_DELETE
    ).subscribe((aceptado) => {
      if (aceptado) {
        this.listadoFragment.deleteTarea(wrappedTarea);
      }
    });

    this.subscriptions.push(dialogSubscription);
  }

  ngOnDestroy(): void {
    this.subscriptions?.forEach(x => x.unsubscribe());
  }

}
