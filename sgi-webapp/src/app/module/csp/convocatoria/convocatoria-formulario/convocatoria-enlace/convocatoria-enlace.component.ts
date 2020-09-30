import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { NGXLogger } from 'ngx-logger';
import { IEnlace } from '@core/models/csp/enlace';
import { ConvocatoriaActionService } from '../../convocatoria.action.service';
import { FragmentComponent } from '@core/component/fragment.component';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { ConvocatoriaEnlaceFragment } from './convocatoria-enlace.fragment';
import { Subscription } from 'rxjs';
import { MatTableDataSource } from '@angular/material/table';
import { GLOBAL_CONSTANTS } from '@core/utils/global-constants';
import { ConvocatoriaEnlaceModalComponent } from '../../modals/convocatoria-enlace-modal/convocatoria-enlace-modal.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'sgi-convocatoria-enlace',
  templateUrl: './convocatoria-enlace.component.html',
  styleUrls: ['./convocatoria-enlace.component.scss']
})
export class ConvocatoriaEnlaceComponent extends FragmentComponent implements OnInit, OnDestroy {

  private formPart: ConvocatoriaEnlaceFragment;
  private subscriptions: Subscription[];

  totalElementos: number;
  displayedColumns: string[];
  elementosPagina: number[];

  dataSource: MatTableDataSource<StatusWrapper<IEnlace>>;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;


  constructor(
    protected readonly logger: NGXLogger,
    protected readonly actionService: ConvocatoriaActionService,
    private matDialog: MatDialog
  ) {
    super(actionService.FRAGMENT.ENLACES, actionService);
    this.logger.debug(ConvocatoriaEnlaceComponent.name, 'constructor()', 'start');
    this.formPart = this.fragment as ConvocatoriaEnlaceFragment;
    this.elementosPagina = [5, 10, 25, 100];
    this.displayedColumns = ['url', 'descripcion', 'tipoEnlace', 'acciones'];
    this.logger.debug(ConvocatoriaEnlaceComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(ConvocatoriaEnlaceComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();
    this.totalElementos = 0;
    this.subscriptions = [];
    this.dataSource = new MatTableDataSource<StatusWrapper<IEnlace>>();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.enlace$.subscribe(elements => {
      this.dataSource.data = elements;
      this.logger.debug(ConvocatoriaEnlaceComponent.name, 'ngOnInit()', 'end');
    }));
  }

  ngOnDestroy(): void {
    this.logger.debug(ConvocatoriaEnlaceComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(ConvocatoriaEnlaceComponent.name, 'ngOnDestroy()', 'end');
  }

  openModal(wrapper?: StatusWrapper<IEnlace>): void {
    this.logger.debug(ConvocatoriaEnlaceComponent.name, 'openEditModal()', 'start');
    const config = {
      width: GLOBAL_CONSTANTS.widthModalCSP,
      maxHeight: GLOBAL_CONSTANTS.maxHeightModal,
      data: wrapper ? wrapper.value : {} as IEnlace
    };
    const dialogRef = this.matDialog.open(ConvocatoriaEnlaceModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (entidadFinanciadora: IEnlace) => {
        if (entidadFinanciadora) {
          if (wrapper) {
            if (!wrapper.created) {
              wrapper.setEdited();
            }
            this.formPart.setChanges(true);
          } else {
            this.formPart.addEnlace(entidadFinanciadora);
          }
        }
        this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'openEditModal()', 'end');
      }
    );
  }
}

