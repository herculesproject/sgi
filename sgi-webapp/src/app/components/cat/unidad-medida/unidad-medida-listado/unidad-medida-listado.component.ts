import { AfterViewInit, Component, OnDestroy, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { UnidadMedida } from '@core/models/unidad-medida';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TraductorService } from '@core/services/traductor.service';
import { Direction, Filter, FilterType } from '@core/services/types';
import { UnidadMedidaService } from '@core/services/unidad-medida.service';
import { UrlUtils } from '@core/utils/url-utils';
import { NGXLogger } from 'ngx-logger';
import { merge, Observable, of, Subscription } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';

@Component({
  selector: 'app-unidad-medida-listado',
  templateUrl: './unidad-medida-listado.component.html',
  styleUrls: ['./unidad-medida-listado.component.scss'],
})
export class UnidadMedidaListadoComponent implements AfterViewInit, OnDestroy {
  UrlUtils = UrlUtils;
  columnas: string[];
  elementosPagina: number[];

  unidadesMedida$: Observable<UnidadMedida[]> = of();
  totalElementos: number;
  filter: Filter;
  @ViewChild(MatSort, { static: false }) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;
  borrarUnidadService: Subscription;

  constructor(
    private readonly logger: NGXLogger,
    private readonly unidadMedidaService: UnidadMedidaService,
    private readonly traductor: TraductorService,
    private readonly dialogService: DialogService,
    private readonly snackBarService: SnackBarService
  ) {
    this.logger.debug(UnidadMedidaListadoComponent.name, 'ngOnInit()', 'start');
    this.columnas = ['abreviatura', 'descripcion', 'acciones'];
    this.elementosPagina = [5, 10, 25, 100];
    this.totalElementos = 0;
    this.filter = {
      field: undefined,
      type: FilterType.NONE,
      value: '',
    };
    this.logger.debug(UnidadMedidaListadoComponent.name, 'ngOnInit()', 'end');
  }

  ngAfterViewInit(): void {
    this.logger.debug(UnidadMedidaListadoComponent.name, 'ngAfterViewInit()', 'start');

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
    this.logger.debug(UnidadMedidaListadoComponent.name, 'ngAfterViewInit()', 'end');
  }

  private loadTable(reset?: boolean) {
    this.logger.debug(UnidadMedidaListadoComponent.name, 'loadTable()', 'start');
    // Do the request with paginator/sort/filter values
    this.unidadesMedida$ = this.unidadMedidaService
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
          this.logger.debug(UnidadMedidaListadoComponent.name, 'loadTable()', 'end');
          // Return the values
          return response.items;
        }),
        catchError(() => {
          // On error reset pagination values
          this.paginator.firstPage();
          this.totalElementos = 0;
          this.snackBarService.mostrarMensajeError(
            this.traductor.getTexto('unidad-medida.listado.error')
          );
          this.logger.debug(UnidadMedidaListadoComponent.name, 'loadTable()', 'end');
          return of([]);
        })
      );
  }

  private buildFilters(): Filter[] {
    this.logger.debug(UnidadMedidaListadoComponent.name, 'buildFilters()', 'start');
    if (
      this.filter.field &&
      this.filter.type !== FilterType.NONE &&
      this.filter.value
    ) {
      this.logger.debug(UnidadMedidaListadoComponent.name, 'buildFilters()', 'end');
      return [this.filter];
    }
    this.logger.debug(UnidadMedidaListadoComponent.name, 'buildFilters()', 'end');
    return [];
  }

  /**
   * Load table data
   */
  public onSearch() {
    this.logger.debug(UnidadMedidaListadoComponent.name, 'onSearch()', 'start');
    this.loadTable(true);
    this.logger.debug(UnidadMedidaListadoComponent.name, 'onSearch()', 'end');

  }

  /**
   * Clean filters an reload the table
   */
  public onClearFilters() {
    this.logger.debug(UnidadMedidaListadoComponent.name, 'onClearFilters()', 'start');
    this.filter = {
      field: undefined,
      type: FilterType.NONE,
      value: '',
    };
    this.loadTable(true);
    this.logger.debug(UnidadMedidaListadoComponent.name, 'onClearFilters()', 'end');
  }

  /**
   * Elimina la unidad de listado con el id recibido por parametro.
   * @param unidadMedidaId id unidad medida
   */
  borrarSeleccionado(unidadMedidaId: number) {
    this.logger.debug(
      UnidadMedidaListadoComponent.name,
      'borrarSeleccionado(unidadMedidaId: number) - start'
    );
    this.dialogService.dialogGenerico(
      this.traductor.getTexto('unidad-medida.listado.eliminar'),
      this.traductor.getTexto('unidad-medida.listado.aceptar'),
      this.traductor.getTexto('unidad-medida.listado.cancelar')
    );
    this.dialogService.getAccionConfirmada().subscribe((aceptado: boolean) => {
      if (aceptado) {
        this.borrarUnidadService = this.unidadMedidaService
          .deleteById(unidadMedidaId)
          .pipe(
            map(() => {
              return this.loadTable();
            })
          )
          .subscribe(() => {
            this.snackBarService.mostrarMensajeSuccess(
              this.traductor.getTexto(
                'unidad-medida.listado.eliminarConfirmado'
              )
            );
          });
      }
      aceptado = false;
    });
    this.logger.debug(
      UnidadMedidaListadoComponent.name,
      'borrarSeleccionado(unidadMedidaId: number) - end'
    );
  }

  ngOnDestroy(): void {
    this.logger.debug(
      UnidadMedidaListadoComponent.name,
      'ngOnDestroy() - start'
    );
    this.borrarUnidadService?.unsubscribe();
    this.logger.debug(UnidadMedidaListadoComponent.name, 'ngOnDestroy() - end');
  }
}
