import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { BaseRestService } from '@core/services/base-rest.service';
import { Direction, Filter, FilterType, ListResult } from '@core/services/types';
import { UrlUtils } from '@core/utils/url-utils';
import { NGXLogger } from 'ngx-logger';
import { merge, of, Observable } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';

@Component({
  selector: 'app-abstract-paginacion',
  templateUrl: './abstract-paginacion.component.html',
  styleUrls: ['./abstract-paginacion.component.scss']
})
export abstract class AbstractPaginacionComponent<T> implements OnInit, AfterViewInit {
  UrlUtils = UrlUtils;
  columnas: string[];
  elementosPagina: number[];
  totalElementos: number;
  filter: Filter;

  @ViewChild(MatSort, { static: false }) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;

  protected constructor(
    protected readonly logger: NGXLogger,
    protected readonly service: BaseRestService<T>,
  ) {
    this.elementosPagina = [5, 10, 25, 100];
  }

  ngOnInit(): void {
    this.logger.debug(AbstractPaginacionComponent.name, 'ngOnInit()', 'start');
    this.totalElementos = 0;
    this.filter = {
      field: undefined,
      type: FilterType.NONE,
      value: '',
    };
    this.inicializarColumnas();
    this.logger.debug(AbstractPaginacionComponent.name, 'ngOnInit()', 'end');
  }

  ngAfterViewInit(): void {
    this.logger.debug(AbstractPaginacionComponent.name, 'ngAfterViewInit()', 'start');
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
    this.logger.debug(AbstractPaginacionComponent.name, 'ngAfterViewInit()', 'end');
  }

  protected buildFilters(): Filter[] {
    this.logger.debug(AbstractPaginacionComponent.name, 'buildFilters()', 'start');
    if (this.filter.field && this.filter.type !== FilterType.NONE && this.filter.value) {
      this.logger.debug(AbstractPaginacionComponent.name, 'buildFilters()', 'end');
      return [this.filter];
    }
    this.logger.debug(AbstractPaginacionComponent.name, 'buildFilters()', 'end');
    return [];
  }

  /**
   * Load table data
   */
  onSearch() {
    this.logger.debug(AbstractPaginacionComponent.name, 'onSearch()', 'start');
    this.loadTable(true);
    this.logger.debug(AbstractPaginacionComponent.name, 'onSearch()', 'end');
  }

  /**
   * Clean filters an reload the table
   */
  onClearFilters() {
    this.logger.debug(AbstractPaginacionComponent.name, 'onClearFilters()', 'start');
    this.filter = {
      field: undefined,
      type: FilterType.NONE,
      value: '',
    };
    this.loadTable(true);
    this.logger.debug(AbstractPaginacionComponent.name, 'onClearFilters()', 'end');
  }

  /**
   * Devuelve el observable con la petición del listado paginado
   *
   * @param reset Inidica si reinicializa la paginación
   */
  protected getObservableLoadTable(reset?: boolean): Observable<T[]> {
    this.logger.debug(AbstractPaginacionComponent.name, 'getObservableLoadTable()', 'start');
    // Do the request with paginator/sort/filter values
    return this.service.findAll({
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
        map((response: ListResult<T>) => {
          // Map respose total
          this.totalElementos = response.total;
          // Reset pagination to first page
          if (reset) {
            this.paginator.pageIndex = 0;
          }
          this.logger.debug(AbstractPaginacionComponent.name, 'getObservableLoadTable()', 'end');
          // Return the values
          return response.items;
        }),
        catchError(() => {
          // On error reset pagination values
          this.paginator.firstPage();
          this.totalElementos = 0;
          this.mostrarMensajeErrorLoadTable();
          this.logger.debug(AbstractPaginacionComponent.name, 'getObservableLoadTable()', 'end');
          return of([]);
        })
      );
  }

  /**
   * Crea e indica el orden las columnas de la tabla
   */
  protected abstract inicializarColumnas();

  /**
   * Carga los datos de la tabla
   *
   * @param reset Indica si reinicializa la paginación
   */
  protected abstract loadTable(reset?: boolean);

  /**
   * Muestra un mensaje de error si se produce un error al cargar los datos de la tabla
   */
  protected abstract mostrarMensajeErrorLoadTable(): void;
}
