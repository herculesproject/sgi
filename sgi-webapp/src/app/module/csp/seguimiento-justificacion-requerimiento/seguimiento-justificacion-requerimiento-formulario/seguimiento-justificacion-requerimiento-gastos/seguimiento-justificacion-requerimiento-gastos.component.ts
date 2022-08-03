import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { FragmentComponent } from '@core/component/fragment.component';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { Subscription } from 'rxjs';
import { SeguimientoJustificacionRequerimientoActionService } from '../../seguimiento-justificacion-requerimiento.action.service';
import { IGastoRequerimientoJustificadoTableData, SeguimientoJustificacionRequerimientoGastosFragment } from './seguimiento-justificacion-requerimiento-gastos.fragment';

@Component({
  selector: 'sgi-seguimiento-justificacion-requerimiento-gastos',
  templateUrl: './seguimiento-justificacion-requerimiento-gastos.component.html',
  styleUrls: ['./seguimiento-justificacion-requerimiento-gastos.component.scss']
})
export class SeguimientoJustificacionRequerimientoGastosComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];

  formPart: SeguimientoJustificacionRequerimientoGastosFragment;

  dataSource = new MatTableDataSource<StatusWrapper<IGastoRequerimientoJustificadoTableData>>();
  elementsPage = [5, 10, 25, 100];

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    public actionService: SeguimientoJustificacionRequerimientoActionService,
  ) {
    super(actionService.FRAGMENT.GASTOS, actionService);
    this.formPart = this.fragment as SeguimientoJustificacionRequerimientoGastosFragment;
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
    this.dataSource.sortingDataAccessor = (wrapper: StatusWrapper<IGastoRequerimientoJustificadoTableData>, property: string) => {
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
}
