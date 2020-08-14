import { AfterViewInit, Component, OnDestroy, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { Servicio } from '@core/models/cat/servicio';
import { ServicioService } from '@core/services/cat/servicio.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';

import { SgiRestSortDirection, SgiRestFilter, SgiRestFilterType } from '@sgi/framework/http';
import { UrlUtils } from '@core/utils/url-utils';
import { NGXLogger } from 'ngx-logger';
import { merge, Observable, of, Subscription } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';

@Component({
  selector: 'app-agrupacion-servicio-listado',
  templateUrl: './agrupacion-servicio-listado.component.html',
  styleUrls: ['./agrupacion-servicio-listado.component.scss']
})
export class AgrupacionServicioListadoComponent implements AfterViewInit, OnDestroy {

  UrlUtils = UrlUtils;

  displayedColumns: string[];
  elementosPagina: number[];
  totalElementos: number;
  filter: SgiRestFilter;


  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;

  servicios$: Observable<Servicio[]> = of();
  dialogSubscription: Subscription;
  servicioDeleteSubscription: Subscription;

  constructor(
    private readonly logger: NGXLogger,
    private readonly servicioService: ServicioService,
    private readonly dialogService: DialogService,
    private readonly snackBarService: SnackBarService
  ) {
    this.logger.debug(AgrupacionServicioListadoComponent.name, 'ngOnInit()', 'start');

    this.displayedColumns = ['abreviatura', 'nombre', 'contacto', 'seccion', 'acciones'];
    this.elementosPagina = [5, 10, 25, 100];
    this.totalElementos = 0;
    this.filter = {
      field: undefined,
      type: SgiRestFilterType.NONE,
      value: '',
    };

    this.logger.debug(AgrupacionServicioListadoComponent.name, 'ngOnInit()', 'end');
  }


  ngAfterViewInit(): void {
    this.logger.debug(AgrupacionServicioListadoComponent.name, 'ngAfterViewInit()', 'start');

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

    this.logger.debug(AgrupacionServicioListadoComponent.name, 'ngAfterViewInit()', 'end');
  }

  private loadTable(reset?: boolean) {
    this.logger.debug(AgrupacionServicioListadoComponent.name, 'loadTable()', 'start');
    // Do the request with paginator/sort/filter values
    this.servicios$ = this.servicioService
      .findAll({
        page: {
          index: reset ? 0 : this.paginator.pageIndex,
          size: this.paginator.pageSize
        },
        sort: {
          direction: SgiRestSortDirection.fromSortDirection(this.sort.direction),
          field: this.sort.active
        },
        filters: this.buildFilters()
      })
      .pipe(
        map((response) => {
          // Map response total
          this.totalElementos = response.total;
          // Reset pagination to first page
          if (reset) {
            this.paginator.pageIndex = 0;
          }
          this.logger.debug(AgrupacionServicioListadoComponent.name, 'loadTable()', 'end');
          // Return the values
          return response.items;
        }),
        catchError(() => {
          // On error reset pagination values
          this.paginator.firstPage();
          this.totalElementos = 0;
          this.snackBarService.showError('cat.servicio.listado.error');
          this.logger.debug(AgrupacionServicioListadoComponent.name, 'loadTable()', 'end');
          return of([]);
        })
      );
  }

  /**
   * Load table data
   */
  public onSearch() {
    this.logger.debug(AgrupacionServicioListadoComponent.name, 'onSearch()', 'start');
    this.loadTable(true);
    this.logger.debug(AgrupacionServicioListadoComponent.name, 'onSearch()', 'end');

  }

  /**
   * Clean filters an reload the table
   */
  public onClearFilters() {
    this.logger.debug(AgrupacionServicioListadoComponent.name, 'onClearFilters()', 'start');
    this.filter = {
      field: undefined,
      type: SgiRestFilterType.NONE,
      value: '',
    };
    this.loadTable(true);
    this.logger.debug(AgrupacionServicioListadoComponent.name, 'onClearFilters()', 'end');
  }

  /**
   * Elimina la agrupación servicio seleccionado.
   * @param servicioId id del servicio a eliminar.
   * @param event evento lanzado.
   */
  borrarSeleccionado(servicioId: number, $event: Event): void {
    this.logger.debug(AgrupacionServicioListadoComponent.name,
      'borrarSeleccionado(servicioId: number, $event: Event) - start');

    $event.stopPropagation();
    $event.preventDefault();

    this.dialogSubscription = this.dialogService.showConfirmation(
      'cat.servicio.listado.eliminar'
    ).subscribe(
      (aceptado) => {
        if (aceptado) {
          this.servicioDeleteSubscription = this.servicioService
            .deleteById(servicioId).pipe(
              map(() => {
                return this.loadTable();
              })
            ).subscribe(
              () => this.snackBarService.showSuccess('cat.servicio.listado.eliminarConfirmado')
            );
        }
      });

    this.logger.debug(AgrupacionServicioListadoComponent.name,
      'borrarSeleccionado(servicioId: number, $event: Event) - end');
  }

  private buildFilters(): SgiRestFilter[] {
    this.logger.debug(AgrupacionServicioListadoComponent.name, 'buildFilters()', 'start');
    if (
      this.filter.field &&
      this.filter.type !== SgiRestFilterType.NONE &&
      this.filter.value
    ) {
      this.logger.debug(AgrupacionServicioListadoComponent.name, 'buildFilters()', 'end');
      return [this.filter];
    }
    this.logger.debug(AgrupacionServicioListadoComponent.name, 'buildFilters()', 'end');
    return [];
  }

  ngOnDestroy(): void {
    this.logger.debug(AgrupacionServicioListadoComponent.name, 'ngOnDestroy()', 'start');
    this.dialogSubscription?.unsubscribe();
    this.servicioDeleteSubscription?.unsubscribe();
    this.logger.debug(AgrupacionServicioListadoComponent.name, 'ngOnDestroy()', 'end');
  }

}
