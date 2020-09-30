import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { FragmentComponent } from '@core/component/fragment.component';
import { IEntidadesConvocantes } from '@core/models/csp/entidades-convocantes';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs/internal/Subscription';
import { ConvocatoriaActionService } from '../../convocatoria.action.service';
import { ConvocatoriaEntidadesConvocantesFragment } from './convocatoria-entidades-convocantes.fragment';
import { GLOBAL_CONSTANTS } from '@core/utils/global-constants';
import { MatDialog } from '@angular/material/dialog';
import { ConvocatoriaEntidadConvocanteaModalComponent } from '../../modals/convocatoria-entidad-convocante-modal/convocatoria-entidad-convocante-modal.component';

@Component({
  selector: 'sgi-convocatoria-entidades-convocantes',
  templateUrl: './convocatoria-entidades-convocantes.component.html',
  styleUrls: ['./convocatoria-entidades-convocantes.component.scss']
})
export class ConvocatoriaEntidadesConvocantesComponent extends FragmentComponent implements OnInit, OnDestroy {

  private formPart: ConvocatoriaEntidadesConvocantesFragment;
  private subscriptions: Subscription[];

  totalElementos: number;
  columnas: string[];
  elementosPagina: number[];

  dataSource: MatTableDataSource<StatusWrapper<IEntidadesConvocantes>>;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly actionService: ConvocatoriaActionService,
    private matDialog: MatDialog,
  ) {
    super(actionService.FRAGMENT.ENTIDADES_CONVOCANTES, actionService);
    this.logger.debug(ConvocatoriaEntidadesConvocantesComponent.name, 'constructor()', 'start');
    this.formPart = this.fragment as ConvocatoriaEntidadesConvocantesFragment;
    this.elementosPagina = [5, 10, 25, 100];
    this.columnas = ['nombre', 'cif', 'plan', 'programa', 'itemPrograma', 'acciones'];
    this.logger.debug(ConvocatoriaEntidadesConvocantesComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(ConvocatoriaEntidadesConvocantesComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();
    this.totalElementos = 0;
    this.subscriptions = [];
    this.dataSource = new MatTableDataSource<StatusWrapper<IEntidadesConvocantes>>();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.entidadesConvocantes$.subscribe(elements => {
      this.dataSource.data = elements;
      this.logger.debug(ConvocatoriaEntidadesConvocantesComponent.name, 'ngOnInit()', 'end');
    }));
  }


  /**
   * Apertura de modal de entidad convocante (edición/creación)
   * @param entidadConvocante entidad convocante a editar.
   */
  openModalEntidadConvocante(entidadConvocante?: StatusWrapper<IEntidadesConvocantes>): void {
    this.logger.debug(ConvocatoriaEntidadesConvocantesComponent.name, 'openModalEntidadConvocante()', 'start');
    const config = {
      width: GLOBAL_CONSTANTS.widthModalCSP,
      maxHeight: GLOBAL_CONSTANTS.maxHeightModal,
      data: entidadConvocante?.value,
      autoFocus: false
    };
    this.matDialog.open(ConvocatoriaEntidadConvocanteaModalComponent, config);

    this.logger.debug(ConvocatoriaEntidadesConvocantesComponent.name, 'openModalEntidadConvocante()', 'end');

  }

  ngOnDestroy(): void {
    this.logger.debug(ConvocatoriaEntidadesConvocantesComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(ConvocatoriaEntidadesConvocantesComponent.name, 'ngOnDestroy()', 'end');
  }
}
