import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AfterViewInit, Component, OnDestroy, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { TipoReservable } from '@core/models/cat/tipo-reservable';
import { TipoReservableService } from '@core/services/cat/tipo-reservable.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiRestSortDirection, SgiRestFilter, SgiRestFilterType } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { merge, Observable, of, Subscription } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { ROUTE_NAMES } from '@core/route.names';

const MSG_SUCCESS_DELETE = marker('cat.tipo-reservable.listado.eliminarConfirmado');
const MSG_CONFIRM_DELETE = marker('cat.tipo-reservable.listado.eliminar');
const MSG_ERROR = marker('cat.tipo-reservable.listado.error');

@Component({
  selector: 'sgi-tipo-reservable-listado',
  templateUrl: './tipo-reservable-listado.component.html',
  styleUrls: ['./tipo-reservable-listado.component.scss']
})
export class TipoReservableListadoComponent implements AfterViewInit, OnDestroy {
  ROUTE_NAMES = ROUTE_NAMES;

  columnas: string[];
  elementosPagina: number[];
  dataSource: MatTableDataSource<TipoReservable>;

  tiposReservable$: Observable<TipoReservable[]> = of();
  totalElementos: number;
  filter: SgiRestFilter;
  @ViewChild(MatSort, { static: false }) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;

  tipoReservableSubscription: Subscription;
  dialogServiceSubscription: Subscription;
  dialogServiceSubscriptionGetSubscription: Subscription;
  tipoReservableServiceDeleteSubscription: Subscription;

  constructor(
    private readonly logger: NGXLogger,
    private readonly tipoReservableService: TipoReservableService,
    private readonly dialogService: DialogService,
    private readonly snackBarService: SnackBarService) {
    this.logger.debug(TipoReservableListadoComponent.name, 'ngOnInit()', 'start');
    this.columnas = ['descripcion', 'servicio', 'estado', 'duracionMin', 'diasAnteMax', 'horasAnteAnular', 'diasVistaMaxCalen', 'acciones'];
    this.elementosPagina = [5, 10, 25, 100];
    this.totalElementos = 0;
    this.filter = {
      field: undefined,
      type: SgiRestFilterType.NONE,
      value: '',
    };
    this.logger.debug(TipoReservableListadoComponent.name, 'ngOnInit()', 'end');

  }

  ngAfterViewInit(): void {
    this.logger.debug(TipoReservableListadoComponent.name, 'ngAfterViewInit()', 'start');

    // Merge events that trigger load table data
    merge(
      // Link pageChange event to fire new request
      this.paginator.page,
      // Link sortChange event to fire new request
      this.sort.sortChange
    )
      .pipe(
        tap(() => {
          // Load table
          this.loadTable();
        })
      )
      .subscribe();
    // First load
    this.loadTable();
    this.logger.debug(TipoReservableListadoComponent.name, 'ngAfterViewInit()', 'end');
  }

  private loadTable(reset?: boolean) {
    this.logger.debug(TipoReservableListadoComponent.name, 'loadTable()', 'start');
    // Do the request with paginator/sort/filter values
    this.tiposReservable$ = this.tipoReservableService
      .findAll({
        page: {
          index: reset ? 0 : this.paginator.pageIndex,
          size: this.paginator.pageSize,
        },
        sort: {
          direction: SgiRestSortDirection.fromSortDirection(this.sort.direction),
          field: this.sort.active,
        },
        filters: this.buildFilters(),
      })
      .pipe(
        map((response) => {
          // Map respose total
          this.totalElementos = response.total;
          // Reset pagination to first page
          if (reset) {
            this.paginator.pageIndex = 0;
          }
          this.logger.debug(TipoReservableListadoComponent.name, 'loadTable()', 'end');
          // Return the values
          return response.items;
        }),
        catchError(() => {
          // On error reset pagination values
          this.paginator.firstPage();
          this.totalElementos = 0;
          this.snackBarService.showError(MSG_ERROR);
          this.logger.debug(TipoReservableListadoComponent.name, 'loadTable()', 'end');
          return of([]);
        })
      );
  }

  private buildFilters(): SgiRestFilter[] {
    this.logger.debug(TipoReservableListadoComponent.name, 'buildFilters()', 'start');
    if (
      this.filter.field &&
      this.filter.type !== SgiRestFilterType.NONE &&
      this.filter.value
    ) {
      this.logger.debug(TipoReservableListadoComponent.name, 'buildFilters()', 'end');
      return [this.filter];
    }
    this.logger.debug(TipoReservableListadoComponent.name, 'buildFilters()', 'end');
    return [];
  }

  /**
   * Load table data
   */
  public onSearch() {
    this.logger.debug(TipoReservableListadoComponent.name, 'onSearch()', 'start');
    this.loadTable(true);
    this.logger.debug(TipoReservableListadoComponent.name, 'onSearch()', 'end');

  }

  /**
   * Clean filters an reload the table
   */
  public onClearFilters() {
    this.logger.debug(TipoReservableListadoComponent.name, 'onClearFilters()', 'start');
    this.filter = {
      field: undefined,
      type: SgiRestFilterType.NONE,
      value: '',
    };
    this.loadTable(true);
    this.logger.debug(TipoReservableListadoComponent.name, 'onClearFilters()', 'end');
  }

  /**
   * Elimina la unidad de listado con el id recibido por parametro.
   * @param tipoReservableId id tipo reservable
   * @param index posicion en la tabla
   * @param $event evento
   */
  borrarSeleccionado(tipoReservableId: number): void {
    this.logger.debug(TipoReservableListadoComponent.name,
      'borrarSeleccionado(tipoReservableId: number) - start');

    this.dialogServiceSubscriptionGetSubscription = this.dialogService.showConfirmation(
      MSG_CONFIRM_DELETE
    ).subscribe(
      (aceptado) => {
        if (aceptado) {
          this.tipoReservableServiceDeleteSubscription = this.tipoReservableService
            .deleteById(tipoReservableId)
            .pipe(
              map(() => {
                return this.loadTable();
              })
            ).subscribe(() => {
              this.snackBarService.showSuccess(MSG_SUCCESS_DELETE);
            });
        }
        aceptado = false;
      });

    this.logger.debug(TipoReservableListadoComponent.name,
      'borrarSeleccionado(tipoReservableId: number) - end');
  }

  ngOnDestroy(): void {
    this.logger.debug(
      TipoReservableListadoComponent.name, 'ngOnDestroy() - start');

    this.tipoReservableSubscription?.unsubscribe();
    this.dialogServiceSubscription?.unsubscribe();
    this.tipoReservableServiceDeleteSubscription?.unsubscribe();
    this.dialogServiceSubscriptionGetSubscription?.unsubscribe();

    this.logger.debug(
      TipoReservableListadoComponent.name, 'ngOnDestroy() - end');
  }

}
