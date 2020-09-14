import { Component, OnInit } from '@angular/core';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { Subscription } from 'rxjs';
import { NGXLogger } from 'ngx-logger';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { MatDialog } from '@angular/material/dialog';
import { ConvocatoriaReunionAsignacionMemoriasComponent } from '../convocatoria-reunion-asignacion-memorias/convocatoria-reunion-asignacion-memorias.component';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';
import { GLOBAL_CONSTANTS } from '@core/utils/global-constants';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { FragmentComponent } from '@core/component/fragment.component';
import { ConvocatoriaReunionActionService } from '../../convocatoria-reunion.action.service';
import { ConvocatoriaReunionListadoMemoriasFragment } from './convocatoria-reunion-listado-memorias.fragment';
import { MatTableDataSource } from '@angular/material/table';


@Component({
  selector: 'sgi-convocatoria-reunion-listado-memorias',
  templateUrl: './convocatoria-reunion-listado-memorias.component.html',
  styleUrls: ['./convocatoria-reunion-listado-memorias.component.scss']
})
export class ConvocatoriaReunionListadoMemoriasComponent extends FragmentComponent implements OnInit {

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns: string[];

  datasource: MatTableDataSource<StatusWrapper<IEvaluacion>> = new MatTableDataSource<StatusWrapper<IEvaluacion>>();

  private subscriptions: Subscription[] = [];
  private listadoFragment: ConvocatoriaReunionListadoMemoriasFragment;

  constructor(
    protected readonly logger: NGXLogger,
    private readonly matDialog: MatDialog,
    protected readonly evaluacionService: EvaluacionService,
    protected readonly dialogService: DialogService,
    protected readonly personaFisicaService: PersonaFisicaService,
    protected readonly snackBarService: SnackBarService,
    private actionService: ConvocatoriaReunionActionService
  ) {
    super(actionService.FRAGMENT.ASIGNACION_MEMORIAS, actionService);
    this.listadoFragment = this.fragment as ConvocatoriaReunionListadoMemoriasFragment;

    this.displayedColumns = ['referencia', 'version', 'dictamen.nombre', 'acciones'];
  }

  ngOnInit(): void {
    this.listadoFragment.evaluaciones$.subscribe((evaluaciones) => {
      this.datasource.data = evaluaciones;
    });
  }

  /**
   * Abre una ventana modal para añadir una nueva asignación de memoria al listado de memorias de una evaluación.
   *
   * Esta ventana solo se debería abrir si tenemos idConvocatoriaReunion (estamos modificando) o cuando estamos
   * creando una nueva ConvocatoriaReunion y ya tenemos establecidos los valores necesarios para obtener las memorias
   * asignables (Comité, Tipo Convocatoria y Fecha Límite)
   *
   */
  openDialogAsignarMemoria(): void {
    this.logger.debug(ConvocatoriaReunionListadoMemoriasComponent.name, 'openDialogAsignarMemoria()', 'start');

    const config = {
      width: GLOBAL_CONSTANTS.maxWidthModal,
      maxHeight: GLOBAL_CONSTANTS.maxHeightModal,
      ...this.matDialog,
      data: {
        params: {
          idConvocatoria: this.listadoFragment.getKey() as number,
          filterMemoriasAsignables: this.actionService.getDatosAsignacion(),
          memoriasAsignadas: this.listadoFragment.evaluaciones$.value.map(evaluacion => evaluacion.value.memoria)
        }
      }
    };

    const dialogRef = this.matDialog.open(ConvocatoriaReunionAsignacionMemoriasComponent, config);

    this.subscriptions.push(
      dialogRef.afterClosed().subscribe(
        (evaluacion: IEvaluacion) => {
          if (evaluacion) {
            this.listadoFragment.addEvaluacion(evaluacion);
          }
        }
      ));

    this.logger.debug(ConvocatoriaReunionListadoMemoriasComponent.name, 'openDialogAsignarMemoria()', 'end');

  }


  /**
   * Elimina la convocatoria reunion.
   * @param evaluacionId id de la evaluacion a eliminar.
   * @param event evento lanzado.
   */
  borrar(wrappedEvaluacion: StatusWrapper<IEvaluacion>): void {
    this.logger.debug(ConvocatoriaReunionListadoMemoriasComponent.name,
      'borrar(convocatoriaReunionId: number, $event: Event) - start');

    const dialogSubscription = this.dialogService.showConfirmation(
      'eti.convocatoriaReunion.listado.eliminar'
    ).subscribe((aceptado) => {
      if (aceptado) {
        this.listadoFragment.deleteEvaluacion(wrappedEvaluacion);
      }
    });

    this.subscriptions.push(dialogSubscription);

    this.logger.debug(ConvocatoriaReunionListadoMemoriasComponent.name,
      'borrar(convocatoriaReunionId: number, $event: Event) - end');
  }

}
