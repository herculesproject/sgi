import { AfterViewInit, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { UrlUtils } from '@core/utils/url-utils';
import {
  SgiRestFilter,
  SgiRestFilterType,
  SgiRestListResult,
  SgiRestService,
  SgiRestSortDirection,
} from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { merge, Observable, of, Subscription } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';

export abstract class AbstractPaginacionComponent<T> implements OnInit, OnDestroy, AfterViewInit {
  UrlUtils = UrlUtils;
  columnas: string[];
  elementosPagina: number[];
  totalElementos: number;
  filter: SgiRestFilter[];
  subscripciones: Subscription[];
  formGroup: FormGroup;

  @ViewChild(MatSort, { static: false }) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;

  protected constructor(
    protected readonly logger: NGXLogger,
    protected readonly service: SgiRestService<number | string, T>,
  ) {
    this.elementosPagina = [5, 10, 25, 100];
  }

  ngOnInit(): void {
    this.logger.debug(AbstractPaginacionComponent.name, 'ngOnInit()', 'start');
    this.totalElementos = 0;
    this.subscripciones = [];
    this.filter = [];
    this.inicializarColumnas();
    this.logger.debug(AbstractPaginacionComponent.name, 'ngOnInit()', 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(AbstractPaginacionComponent.name, 'ngOnDestroy()', 'start');
    this.subscripciones.forEach(x => x.unsubscribe());
    this.logger.debug(AbstractPaginacionComponent.name, 'ngOnDestroy()', 'end');
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

  /**
   * Load table data
   */
  onSearch() {
    this.logger.debug(AbstractPaginacionComponent.name, 'onSearch()', 'start');
    this.filter = this.crearFiltros();
    this.loadTable(true);
    this.logger.debug(AbstractPaginacionComponent.name, 'onSearch()', 'end');
  }

  /**
   * Clean filters an reload the table
   */
  onClearFilters() {
    this.logger.debug(AbstractPaginacionComponent.name, 'onClearFilters()', 'start');
    FormGroupUtil.clean(this.formGroup);
    this.filter = [{
      field: undefined,
      type: SgiRestFilterType.NONE,
      value: '',
    }];
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
        direction: SgiRestSortDirection.fromSortDirection(this.sort.direction),
        field: this.sort.active,
      },
      filters: this.filter,
    })
      .pipe(
        map((response: SgiRestListResult<T>) => {
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
          this.logger.error(AbstractPaginacionComponent.name, 'getObservableLoadTable()', 'error');
          return of([]);
        })
      );
  }

  protected agregarFiltro(filtros: SgiRestFilter[], nombre: string, tipo: SgiRestFilterType, valor: any) {
    this.logger.debug(AbstractPaginacionComponent.name,
      `agregarFiltro(${filtros}, ${nombre}, ${tipo} , ${valor})`, 'start');
    if (valor) {
      const filtro: SgiRestFilter = {
        field: nombre,
        type: tipo,
        value: valor,
      };
      filtros.push(filtro);
    }
    this.logger.debug(AbstractPaginacionComponent.name,
      `agregarFiltro(${filtros}, ${nombre}, ${tipo} , ${valor})`, 'end');
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

  /**
   * Crea los filtros para el listado
   */
  protected abstract crearFiltros(formGroup?: FormGroup): SgiRestFilter[];
}
