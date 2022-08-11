import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IGastoJustificado } from '@core/models/sge/gasto-justificado';
import { ROUTE_NAMES } from '@core/route.names';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { GastosJustificadosModalComponent, IGastosJustificadosModalData } from '../../modals/gastos-justificados-modal/gastos-justificados-modal.component';
import { SeguimientoJustificacionRequerimientoActionService } from '../../seguimiento-justificacion-requerimiento.action.service';
import { IGastoRequerimientoJustificacionTableData, SeguimientoJustificacionRequerimientoGastosFragment } from './seguimiento-justificacion-requerimiento-gastos.fragment';

const GASTO_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.gasto');

@Component({
  selector: 'sgi-seguimiento-justificacion-requerimiento-gastos',
  templateUrl: './seguimiento-justificacion-requerimiento-gastos.component.html',
  styleUrls: ['./seguimiento-justificacion-requerimiento-gastos.component.scss']
})
export class SeguimientoJustificacionRequerimientoGastosComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];

  formPart: SeguimientoJustificacionRequerimientoGastosFragment;
  msgParamEntity = {};

  dataSource = new MatTableDataSource<StatusWrapper<IGastoRequerimientoJustificacionTableData>>();
  elementsPage = [5, 10, 25, 100];

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get ROUTE_NAMES() {
    return ROUTE_NAMES;
  }

  constructor(
    public actionService: SeguimientoJustificacionRequerimientoActionService,
    private matDialog: MatDialog,
    private readonly translate: TranslateService,
  ) {
    super(actionService.FRAGMENT.GASTOS, actionService);
    this.formPart = this.fragment as SeguimientoJustificacionRequerimientoGastosFragment;
    this.setupI18N();
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.initializeDataSource();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  private initializeDataSource(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor = (wrapper: StatusWrapper<IGastoRequerimientoJustificacionTableData>, property: string) => {
      switch (property) {
        case 'justificacionId':
          return wrapper.value?.gasto?.justificacionId;
        default:
          const gastoColumn = this.formPart.columns.find(column => column.id === property);
          return gastoColumn ? wrapper.value.gasto.columnas[gastoColumn.id] : wrapper.value[property];
      }
    };
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.getGastosRequerimientoTableData$()
      .subscribe(elements => this.dataSource.data = elements));
  }

  openGastosJustificadosModalComponent(): void {
    const data: IGastosJustificadosModalData = {
      requerimientoJustificacion: this.formPart.currentRequerimientoJustificacion,
      selectedGastosRequerimiento: this.dataSource.data
    };

    const config: MatDialogConfig<IGastosJustificadosModalData> = {
      data
    };

    const dialogRef = this.matDialog.open(GastosJustificadosModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalResponse: IGastoJustificado[]) => {
        if (modalResponse) {
          this.formPart.addGastoRequerimientoTableData(modalResponse);
        }
      }
    );
  }

  private setupI18N(): void {
    this.translate.get(
      GASTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });
  }
}
