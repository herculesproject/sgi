import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { FragmentComponent } from '@core/component/fragment.component';
import { IEntidadesFinanciadoras } from '@core/models/csp/entidades-financiadoras';
import { GLOBAL_CONSTANTS } from '@core/utils/global-constants';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';
import { ConvocatoriaActionService } from '../../convocatoria.action.service';
import { ConvocatoriaEntidadFinanciadoraModalComponent } from '../../modals/convocatoria-entidad-financiadora-modal/convocatoria-entidad-financiadora-modal.component';
import { ConvocatoriaEntidadesFinanciadorasFragment } from './convocatoria-entidades-financiadoras.fragment';

@Component({
  selector: 'sgi-convocatoria-entidades-financiadoras',
  templateUrl: './convocatoria-entidades-financiadoras.component.html',
  styleUrls: ['./convocatoria-entidades-financiadoras.component.scss']
})
export class ConvocatoriaEntidadesFinanciadorasComponent extends FragmentComponent implements OnInit, OnDestroy {

  private formPart: ConvocatoriaEntidadesFinanciadorasFragment;
  private subscriptions: Subscription[];

  totalElementos: number;
  columnas: string[];
  elementosPagina: number[];

  dataSource: MatTableDataSource<StatusWrapper<IEntidadesFinanciadoras>>;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly actionService: ConvocatoriaActionService,
    private matDialog: MatDialog
  ) {
    super(actionService.FRAGMENT.ENTIDADES_FINANCIADORAS, actionService);
    this.logger.debug(ConvocatoriaEntidadesFinanciadorasComponent.name, 'constructor()', 'start');
    this.formPart = this.fragment as ConvocatoriaEntidadesFinanciadorasFragment;
    this.elementosPagina = [5, 10, 25, 100];
    this.columnas = ['nombre', 'cif', 'fuenteFinanciacion', 'ambito', 'tipoFinanciacion',
      'porcentajeFinanciacion', 'acciones'];
    this.logger.debug(ConvocatoriaEntidadesFinanciadorasComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(ConvocatoriaEntidadesFinanciadorasComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();
    this.totalElementos = 0;
    this.subscriptions = [];
    this.dataSource = new MatTableDataSource<StatusWrapper<IEntidadesFinanciadoras>>();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.entidadesFinanciadoras$.subscribe(elements => {
      this.dataSource.data = elements;
      this.logger.debug(ConvocatoriaEntidadesFinanciadorasComponent.name, 'ngOnInit()', 'end');
    }));
  }

  ngOnDestroy(): void {
    this.logger.debug(ConvocatoriaEntidadesFinanciadorasComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(ConvocatoriaEntidadesFinanciadorasComponent.name, 'ngOnDestroy()', 'end');
  }

  openModal(wrapper?: StatusWrapper<IEntidadesFinanciadoras>): void {
    this.logger.debug(ConvocatoriaEntidadesFinanciadorasComponent.name, 'openEditModal()', 'start');
    const config = {
      width: GLOBAL_CONSTANTS.widthModalCSP,
      maxHeight: GLOBAL_CONSTANTS.maxHeightModal,
      data: wrapper ? wrapper.value : {} as IEntidadesFinanciadoras
    };
    const dialogRef = this.matDialog.open(ConvocatoriaEntidadFinanciadoraModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (entidadFinanciadora: IEntidadesFinanciadoras) => {
        if (entidadFinanciadora) {
          if (wrapper) {
            if (!wrapper.created) {
              wrapper.setEdited();
            }
            this.formPart.setChanges(true);
          } else {
            // TODO coger estos datos del back
            entidadFinanciadora.ambito = 'Propio';
            entidadFinanciadora.cif = 'R6695050';
            this.formPart.addEntidadFinanciadora(entidadFinanciadora);
          }
        }
        this.logger.debug(ConvocatoriaEntidadesFinanciadorasComponent.name, 'openEditModal()', 'end');
      }
    );
  }

}
