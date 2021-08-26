import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { FragmentComponent } from '@core/component/fragment.component';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { Subscription } from 'rxjs';
import { EjecucionEconomicaActionService } from '../../ejecucion-economica.action.service';
import { EstadoTipo, ValidacionGasto, ValidacionGastosFragment } from './validacion-gastos.fragment';

@Component({
  selector: 'sgi-validacion-gastos',
  templateUrl: './validacion-gastos.component.html',
  styleUrls: ['./validacion-gastos.component.scss']
})
export class ValidacionGastosComponent extends FragmentComponent implements OnInit, OnDestroy {
  formPart: ValidacionGastosFragment;
  private subscriptions: Subscription[] = [];

  formGroup: FormGroup;

  elementsPage = [5, 10, 25, 100];

  readonly dataSource = new MatTableDataSource<ValidacionGasto>();

  @ViewChild(MatPaginator, { static: true }) private paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) private sort: MatSort;

  constructor(
    public actionService: EjecucionEconomicaActionService
  ) {
    super(actionService.FRAGMENT.VALIDACION_GASTOS, actionService);
    this.formPart = this.fragment as ValidacionGastosFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.loadForm();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor = (gasto: ValidacionGasto, property: string) => {
      switch (property) {
        case 'anualidad':
          return gasto.anualidad;
        case 'proyecto':
          return gasto.proyecto?.titulo;
        case 'agrupacionGasto':
          return gasto.agrupacionGasto?.nombre;
        case 'conceptoGasto':
          return gasto.conceptoGasto?.nombre;
        case 'aplicacionPresupuestaria':
          return gasto.partidaPresupuestaria;
        case 'codigoEconomico':
          return `${gasto.codigoEconomico?.id} ${gasto.codigoEconomico?.nombre ? '-' : ''} ${gasto.codigoEconomico?.nombre}`;
        default:
          const gastoColumn = this.formPart.columns.find(column => column.id === property);
          return gastoColumn ? gasto.columnas[gastoColumn.id] : gasto[property];
      }
    };
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.gastos$.subscribe(
      (data) => {
        const fechaColumn = this.formPart.columns.find(column => column.name === 'Fecha');
        data.sort((a, b) => {
          return b.anualidad.localeCompare(a.anualidad)
            || a.proyecto?.titulo.localeCompare(b.proyecto?.titulo)
            || a.agrupacionGasto?.nombre.localeCompare(b.agrupacionGasto?.nombre)
            || a.partidaPresupuestaria.localeCompare(b.partidaPresupuestaria)
            || `${a.codigoEconomico?.id} ${a.codigoEconomico?.nombre ? '-' : ''} ${a.codigoEconomico?.nombre}`
              .localeCompare(`${b.codigoEconomico?.id} ${b.codigoEconomico?.nombre ? '-' : ''} ${b.codigoEconomico?.nombre}`)
            || b.columnas[fechaColumn.id]?.toString().localeCompare(a.columnas[fechaColumn.id]?.toString());
        });

        this.dataSource.data = data;
      })
    );
  }

  onSearch(): void {
    const estado = this.formGroup.controls.estado.value;
    const fechaDesde = LuxonUtils.toBackend(this.formGroup.controls.fechaDesde.value);
    const fechaHasta = LuxonUtils.toBackend(this.formGroup.controls.fechaHasta.value);
    this.formPart.searchGastos(estado, fechaDesde, fechaHasta);
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  private loadForm() {
    this.formGroup = new FormGroup({
      estado: new FormControl(EstadoTipo.BLOQUEADOS, Validators.required),
      fechaDesde: new FormControl(),
      fechaHasta: new FormControl()
    });
  }

}
