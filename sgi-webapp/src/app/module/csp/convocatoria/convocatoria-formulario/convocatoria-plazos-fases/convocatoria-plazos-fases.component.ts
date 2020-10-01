import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { NGXLogger } from 'ngx-logger';
import { FragmentComponent } from '@core/component/fragment.component';
import { ConvocatoriaPlazosFasesFragment } from './convocatoria-plazos-fases.fragment';
import { BehaviorSubject, Subscription } from 'rxjs';
import { MatTableDataSource } from '@angular/material/table';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { IPlazosFases } from '@core/models/csp/plazos-fases';
import { ConvocatoriaActionService } from '../../convocatoria.action.service';
import { GLOBAL_CONSTANTS } from '@core/utils/global-constants';
import { MatDialog } from '@angular/material/dialog';
import { ConvocatoriaPlazosFaseModalComponent } from '../../modals/convocatoria-plazos-fase-modal/convocatoria-plazos-fase-modal.component';

@Component({
  selector: 'sgi-convocatoria-plazos-fases',
  templateUrl: './convocatoria-plazos-fases.component.html',
  styleUrls: ['./convocatoria-plazos-fases.component.scss']
})

export class ConvocatoriaPlazosFasesComponent extends FragmentComponent implements OnInit, OnDestroy {

  private formPart: ConvocatoriaPlazosFasesFragment;
  private subscriptions: Subscription[];

  totalElementos: number;
  displayedColumns: string[];
  elementosPagina: number[];

  dataSource: MatTableDataSource<StatusWrapper<IPlazosFases>>;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  plazosFase$: BehaviorSubject<StatusWrapper<IPlazosFases>[]>;

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly actionService: ConvocatoriaActionService,
    private matDialog: MatDialog
  ) {
    super(actionService.FRAGMENT.PLAZOS_FASES, actionService);
    this.logger.debug(ConvocatoriaPlazosFasesComponent.name, 'constructor()', 'start');
    this.formPart = this.fragment as ConvocatoriaPlazosFasesFragment;
    this.plazosFase$ = (this.fragment as ConvocatoriaPlazosFasesFragment).plazosFase$;
    this.elementosPagina = [5, 10, 25, 100];
    this.displayedColumns = ['fechaInicio', 'fechaFin', 'tipoFase', 'observaciones', 'acciones'];
    this.logger.debug(ConvocatoriaPlazosFasesComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(ConvocatoriaPlazosFasesComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();
    this.totalElementos = 0;
    this.subscriptions = [];
    this.dataSource = new MatTableDataSource<StatusWrapper<IPlazosFases>>();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.plazosFase$.subscribe(elements => {
      this.dataSource.data = elements;
      this.logger.debug(ConvocatoriaPlazosFasesComponent.name, 'ngOnInit()', 'end');
    }));
  }

  /**
   * Apertura de modal de plazos fase
   * @param plazo Identificador de plazos fase al guardar/editar
   */
  openModalPeriodo(plazo?: StatusWrapper<IPlazosFases>): void {
    this.logger.debug(ConvocatoriaPlazosFasesComponent.name, 'openModalPeriodo()', 'start');
    const config = {
      width: GLOBAL_CONSTANTS.widthModalCSP,
      maxHeight: GLOBAL_CONSTANTS.maxHeightModal,
      data: plazo ? plazo.value : {} as IPlazosFases,
      autoFocus: false
    };

    const dialogRef = this.matDialog.open(ConvocatoriaPlazosFaseModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (plazosFase: IPlazosFases) => {
        if (plazosFase) {
          if (plazo) {
            if (!plazo.created) {
              plazo.setEdited();
            }
            this.formPart.setChanges(true);
          } else {
            // TODO coger estos datos del back

            this.formPart.addPlazosFases(plazosFase);
          }
        }
        this.logger.debug(ConvocatoriaPlazosFasesComponent.name, 'openModalPeriodo()', 'end');
      }
    );


    this.logger.debug(ConvocatoriaPlazosFasesComponent.name, 'openModalPeriodo()', 'end');

  }

  ngOnDestroy(): void {
    this.logger.debug(ConvocatoriaPlazosFasesComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(ConvocatoriaPlazosFasesComponent.name, 'ngOnDestroy()', 'end');
  }
}
