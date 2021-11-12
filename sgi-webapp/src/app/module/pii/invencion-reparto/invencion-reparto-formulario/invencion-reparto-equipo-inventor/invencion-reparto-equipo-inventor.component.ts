import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IRepartoGasto } from '@core/models/pii/reparto-gasto';
import { IRepartoIngreso } from '@core/models/pii/reparto-ingreso';
import { SnackBarService } from '@core/services/snack-bar.service';
import { Subscription } from 'rxjs';
import { tap } from 'rxjs/operators';
import { InvencionRepartoActionService } from '../../invencion-reparto.action.service';
import { InvencionRepartoEquipoInventorFragment } from './invencion-reparto-equipo-inventor.fragment';

@Component({
  selector: 'sgi-invencion-reparto-equipo-inventor',
  templateUrl: './invencion-reparto-equipo-inventor.component.html',
  styleUrls: ['./invencion-reparto-equipo-inventor.component.scss']
})
export class InvencionRepartoEquipoInventorComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];

  formPart: InvencionRepartoEquipoInventorFragment;
  @ViewChild('sortGastos', { static: true }) sortGastos: MatSort;
  gastosDataSource = new MatTableDataSource<IRepartoGasto>();
  @ViewChild('sortIngresos', { static: true }) sortIngresos: MatSort;
  ingresosDataSource = new MatTableDataSource<IRepartoIngreso>();

  totalGastosCompensar = 0;
  totalIngresosRepartir = 0;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    public actionService: InvencionRepartoActionService,
    private readonly snackBarService: SnackBarService,
  ) {
    super(actionService.FRAGMENT.REPARTO_EQUIPO_INVENTOR, actionService);
    this.formPart = this.fragment as InvencionRepartoEquipoInventorFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.initializeGastosDataSource();
    this.initializeIngresosDataSource();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  private initializeGastosDataSource(): void {
    this.gastosDataSource.sortingDataAccessor = (repartoGasto: IRepartoGasto, property: string) => {
      switch (property) {
        case 'solicitudProteccion':
          return repartoGasto.invencionGasto?.solicitudProteccion?.titulo;
        case 'importePendienteDeducir':
          return repartoGasto.invencionGasto?.importePendienteDeducir;
        case 'importeADeducir':
          return repartoGasto.importeADeducir;
        default:
          const gastoColumn = this.formPart.gastosColumns.find(column => column.id === property);
          return gastoColumn ? repartoGasto.invencionGasto.gasto.columnas[gastoColumn.id] : repartoGasto[property];
      }
    };
    this.gastosDataSource.sort = this.sortGastos;
    this.subscriptions.push(this.formPart.getRepartoGastos$()
      .pipe(
        tap(repartoGastos => this.calculateTotalGastosCompensar(repartoGastos))
      )
      .subscribe(elements => this.gastosDataSource.data = elements));
  }

  private initializeIngresosDataSource(): void {
    this.ingresosDataSource.sortingDataAccessor = (repartoIngreso: IRepartoIngreso, property: string) => {
      switch (property) {
        case 'importePendienteRepartir':
          return repartoIngreso.invencionIngreso?.importePendienteRepartir;
        case 'importeARepartir':
          return repartoIngreso.importeARepartir;
        default:
          const gastoColumn = this.formPart.gastosColumns.find(column => column.id === property);
          return gastoColumn ? repartoIngreso.invencionIngreso.ingreso.columnas[gastoColumn.id] : repartoIngreso[property];
      }
    };
    this.ingresosDataSource.sort = this.sortIngresos;
    this.subscriptions.push(this.formPart.getRepartoIngresos$()
      .pipe(
        tap(repartoIngresos => this.calculateTotalIngresosRepartir(repartoIngresos))
      )
      .subscribe(elements => this.ingresosDataSource.data = elements));
  }

  private calculateTotalGastosCompensar(repartoGastos: IRepartoGasto[]): void {
    this.totalGastosCompensar = repartoGastos.reduce((accum, current) => {
      const importe = current.importeADeducir ?? 0;
      return accum + importe;
    }, 0);
  }

  private calculateTotalIngresosRepartir(repartoIngresos: IRepartoIngreso[]): void {
    this.totalIngresosRepartir = repartoIngresos.reduce((accum, current) => {
      const importe = current.importeARepartir ?? 0;
      return accum + importe;
    }, 0);
  }

  getTotalGastosCaptionColspan(): number {
    return this.formPart.displayGastosColumns.length - 1;
  }

  getTotalIngresosCaptionColspan(): number {
    return this.formPart.displayIngresosColumns.length - 1;
  }
}
