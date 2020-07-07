import { AfterViewInit, Component, OnDestroy, ViewChild } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { TipoFungible } from '@core/models/tipo-fungible';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TipoFungibleService } from '@core/services/tipo-fungible.service';
import { TraductorService } from '@core/services/traductor.service';
import { UrlUtils } from '@core/utils/url-utils';
import { NGXLogger } from 'ngx-logger';
import { merge, Observable, of, Subscription } from 'rxjs';
import { Direction, Filter, FilterType } from '@core/services/types';
import { MatPaginator } from '@angular/material/paginator';
import { catchError, map, tap } from 'rxjs/operators';

@Component({
  selector: 'app-tipo-fungible-listado',
  templateUrl: './tipo-fungible-listado.component.html',
  styleUrls: ['./tipo-fungible-listado.component.scss'],
})
export class TipoFungibleListadoComponent implements AfterViewInit, OnDestroy {
  UrlUtils = UrlUtils;
  columnas: string[];
  elementosPagina: number[];

  tipoFungibleServiceSubscription: Subscription;
  dialogServiceSubscription: Subscription;
  tipoFungibleServiceDeleteSubscription: Subscription;

  tiposFungible$: Observable<TipoFungible[]> = of();
  totalElementos: number;
  filter: Filter;
  @ViewChild(MatSort, { static: false }) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;

  constructor(
    private readonly logger: NGXLogger,
    private readonly tipoFungibleService: TipoFungibleService,
    private readonly traductor: TraductorService,
    private readonly dialogService: DialogService,
    private readonly snackBarService: SnackBarService
  ) {
    this.logger.debug(TipoFungibleListadoComponent.name, 'ngOnInit()', 'start');
    this.columnas = ['nombre', 'servicio', 'acciones'];
    this.elementosPagina = [5, 10, 25, 100];
    this.totalElementos = 0;
    this.filter = {
      field: undefined,
      type: FilterType.NONE,
      value: '',
    };
    this.logger.debug(TipoFungibleListadoComponent.name, 'ngOnInit()', 'end');
  }

  ngAfterViewInit(): void {
    this.logger.debug(TipoFungibleListadoComponent.name, 'ngAfterViewInit()', 'start');

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
    this.logger.debug(TipoFungibleListadoComponent.name, 'ngAfterViewInit()', 'end');
  }

  private loadTable(reset?: boolean) {
    this.logger.debug(TipoFungibleListadoComponent.name, 'loadTable()', 'start');
    // Do the request with paginator/sort/filter values
    this.tiposFungible$ = this.tipoFungibleService
      .findAll({
        page: {
          index: reset ? 0 : this.paginator.pageIndex,
          size: this.paginator.pageSize,
        },
        sort: {
          direction: Direction.fromSortDirection(this.sort.direction),
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
          this.logger.debug(TipoFungibleListadoComponent.name, 'loadTable()', 'end');
          // Return the values
          return response.items;
        }),
        catchError(() => {
          // On error reset pagination values
          this.paginator.firstPage();
          this.totalElementos = 0;
          this.snackBarService.mostrarMensajeError(
            this.traductor.getTexto('tipo-fungible.listado.error')
          );
          this.logger.debug(TipoFungibleListadoComponent.name, 'loadTable()', 'end');
          return of([]);
        })
      );
  }

  private buildFilters(): Filter[] {
    this.logger.debug(TipoFungibleListadoComponent.name, 'buildFilters()', 'start');
    if (
      this.filter.field &&
      this.filter.type !== FilterType.NONE &&
      this.filter.value
    ) {
      this.logger.debug(TipoFungibleListadoComponent.name, 'buildFilters()', 'end');
      return [this.filter];
    }
    this.logger.debug(TipoFungibleListadoComponent.name, 'buildFilters()', 'end');
    return [];
  }

  /**
   * Load table data
   */
  public onSearch() {
    this.logger.debug(TipoFungibleListadoComponent.name, 'onSearch()', 'start');
    this.loadTable(true);
    this.logger.debug(TipoFungibleListadoComponent.name, 'onSearch()', 'end');

  }

  /**
   * Clean filters an reload the table
   */
  public onClearFilters() {
    this.logger.debug(TipoFungibleListadoComponent.name, 'onClearFilters()', 'start');
    this.filter = {
      field: undefined,
      type: FilterType.NONE,
      value: '',
    };
    this.loadTable(true);
    this.logger.debug(TipoFungibleListadoComponent.name, 'onClearFilters()', 'end');
  }

  /**
   * Elimina el tipo fungible del listado con el id recibido por parametro.
   * @param tipoFungibleId id tipo fungible
   */
  borrarSeleccionado(tipoFungibleId: number): void {
    this.logger.debug(
      TipoFungibleListadoComponent.name,
      'borrarSeleccionado(tipoFungibleId: number) - start'
    );
    this.dialogService.dialogGenerico(
      this.traductor.getTexto('tipo-fungible.listado.eliminar'),
      this.traductor.getTexto('tipo-fungible.listado.aceptar'),
      this.traductor.getTexto('tipo-fungible.listado.cancelar')
    );

    this.dialogServiceSubscription = this.dialogService
      .getAccionConfirmada()
      .subscribe((aceptado: boolean) => {
        if (aceptado) {
          this.tipoFungibleServiceDeleteSubscription = this.tipoFungibleService
            .deleteById(tipoFungibleId)
            .pipe(
              map(() => {
                return this.loadTable();
              })
            )
            .subscribe(() => {
              this.snackBarService.mostrarMensajeSuccess(
                this.traductor.getTexto(
                  'tipo-fungible.listado.eliminarConfirmado'
                )
              );
            });
        }
        aceptado = false;
      });

    this.logger.debug(
      TipoFungibleListadoComponent.name,
      'borrarSeleccionado(tipoFungibleId: number) - end'
    );
  }

  ngOnDestroy(): void {
    this.logger.debug(
      TipoFungibleListadoComponent.name,
      'ngOnDestroy() - start'
    );
    this.tipoFungibleServiceSubscription?.unsubscribe();
    this.dialogServiceSubscription?.unsubscribe();
    this.tipoFungibleServiceDeleteSubscription?.unsubscribe();
    this.logger.debug(TipoFungibleListadoComponent.name, 'ngOnDestroy() - end');
  }
}
