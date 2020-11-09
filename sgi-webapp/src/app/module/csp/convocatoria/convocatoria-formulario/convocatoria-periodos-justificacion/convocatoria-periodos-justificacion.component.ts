import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { NGXLogger } from 'ngx-logger';
import { FragmentComponent } from '@core/component/fragment.component';
import { ConvocatoriaPeriodosJustificacionFragment } from './convocatoria-periodo-justificacion.fragment';
import { Subscription } from 'rxjs';
import { MatTableDataSource } from '@angular/material/table';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { IPeriodoJustificacion } from '@core/models/csp/periodo-justificacion';
import { ConvocatoriaActionService } from '../../convocatoria.action.service';
import { ConvocatoriaPeriodosJustificacionModalComponent } from '../../modals/convocatoria-periodos-justificacion-modal/convocatoria-periodos-justificacion-modal.component';
import { GLOBAL_CONSTANTS } from '@core/utils/global-constants';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'sgi-convocatoria-periodos-justificacion',
  templateUrl: './convocatoria-periodos-justificacion.component.html',
  styleUrls: ['./convocatoria-periodos-justificacion.component.scss']
})
export class ConvocatoriaPeriodosJustificacionComponent extends FragmentComponent implements OnInit, OnDestroy {

  private formPart: ConvocatoriaPeriodosJustificacionFragment;
  private subscriptions: Subscription[];

  totalElementos: number;
  displayedColumns: string[];
  elementosPagina: number[];

  dataSource: MatTableDataSource<StatusWrapper<IPeriodoJustificacion>>;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly actionService: ConvocatoriaActionService,
    private matDialog: MatDialog
  ) {
    super(actionService.FRAGMENT.PERIODO_JUSTIFICACION, actionService);
    this.logger.debug(ConvocatoriaPeriodosJustificacionComponent.name, 'constructor()', 'start');

    this.formPart = this.fragment as ConvocatoriaPeriodosJustificacionFragment;
    this.elementosPagina = [5, 10, 25, 100];
    this.displayedColumns = ['numPeriodo', 'mesInicial', 'mesFinal', 'fechaInicio', 'fechaFin', 'observaciones', 'acciones'];
    this.logger.debug(ConvocatoriaPeriodosJustificacionComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(ConvocatoriaPeriodosJustificacionComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();
    this.totalElementos = 0;
    this.subscriptions = [];
    this.dataSource = new MatTableDataSource<StatusWrapper<IPeriodoJustificacion>>();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.periodosJustificacion$.subscribe(elements => {
      this.dataSource.data = elements;
      this.logger.debug(ConvocatoriaPeriodosJustificacionComponent.name, 'ngOnInit()', 'end');
    }));
  }


  /**
   * Apertura de modal de periodos justificacion (edición/creación)
   * @param idPeriodoJustificacion Identificador de periodos justificacion a editar.
   */
  openModalPeriodo(justificacion?: StatusWrapper<IPeriodoJustificacion>): void {
    this.logger.debug(ConvocatoriaPeriodosJustificacionComponent.name, 'openModalPeriodo()', 'start');
    const config = {
      width: GLOBAL_CONSTANTS.widthModalCSP,
      maxHeight: GLOBAL_CONSTANTS.maxHeightModal,
      data: justificacion ? justificacion.value : {} as IPeriodoJustificacion,
      autoFocus: false
    };

    const dialogRef = this.matDialog.open(ConvocatoriaPeriodosJustificacionModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (periodosJustificacion: IPeriodoJustificacion) => {
        if (periodosJustificacion) {
          if (justificacion) {
            if (!justificacion.created) {
              justificacion.setEdited();
            }
            this.formPart.setChanges(true);
          } else {
            // TODO coger estos datos del back

            this.formPart.addPeriodoJustificacion(periodosJustificacion);
          }
        }
        this.logger.debug(ConvocatoriaPeriodosJustificacionModalComponent.name, 'openEditModal()', 'end');
      }
    );


    this.logger.debug(ConvocatoriaPeriodosJustificacionComponent.name, 'openModalPeriodo()', 'end');

  }

  ngOnDestroy(): void {
    this.logger.debug(ConvocatoriaPeriodosJustificacionComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(ConvocatoriaPeriodosJustificacionComponent.name, 'ngOnDestroy()', 'end');
  }
}

