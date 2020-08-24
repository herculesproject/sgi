import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AfterViewInit, Component, Input, OnChanges, OnDestroy, SimpleChanges, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { Registro } from '@core/models/cat/registro';
import { Servicio } from '@core/models/cat/servicio';
import { ServicioService } from '@core/services/cat/servicio.service';
import { SolicitudService } from '@core/services/cat/solicitud.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiRestSortDirection, SgiRestFilter, SgiRestFilterType } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { merge, Observable, of, Subscription } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';

const MSG_SUCCESS_ALTA = marker('cat.solicitud.alta.correcto');
const MSG_ERROR_ALTA = marker('cat.solicitud.alta.error');
const MSG_ERROR = marker('cat.solicitud.listado.error');

@Component({
  selector: 'sgi-solicitud-listado',
  templateUrl: './solicitud-listado.component.html',
  styleUrls: ['./solicitud-listado.component.scss']
})
export class SolicitudListadoComponent implements AfterViewInit, OnDestroy, OnChanges {
  columnas: string[];
  elementosPagina: number[];

  servicio$: Observable<Servicio[]> = of();
  totalElementos: number;
  filter: SgiRestFilter;
  @ViewChild(MatSort, { static: false }) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;
  servicioSeleccionado: Servicio;
  @Input() registro: Registro;
  updateSolicitudService: Subscription;

  constructor(
    private readonly logger: NGXLogger,
    private readonly solicitudService: SolicitudService,
    private readonly servicioService: ServicioService,
    private snackBarService: SnackBarService
  ) {
    this.logger.debug(SolicitudListadoComponent.name, 'ngOnInit()', 'start');
    this.columnas = ['nombre', 'contacto', 'acciones'];
    this.elementosPagina = [5, 10, 25, 100];
    this.totalElementos = 0;
    this.filter = {
      field: undefined,
      type: SgiRestFilterType.NONE,
      value: '',
    };
    this.logger.debug(SolicitudListadoComponent.name, 'ngOnInit()', 'end');
  }

  ngAfterViewInit(): void {
    this.logger.debug(SolicitudListadoComponent.name, 'ngAfterViewInit()', 'start');

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
    this.logger.debug(SolicitudListadoComponent.name, 'ngAfterViewInit()', 'end');
  }

  private loadTable(reset?: boolean) {
    this.logger.debug(SolicitudListadoComponent.name, 'loadTable()', 'start');
    // Do the request with paginator/sort/filter values
    this.servicio$ = this.servicioService
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
          this.logger.debug(SolicitudListadoComponent.name, 'loadTable()', 'end');
          // Return the values
          return response.items;
        }),
        catchError(() => {
          // On error reset pagination values
          this.paginator.firstPage();
          this.totalElementos = 0;
          this.snackBarService.showError(MSG_ERROR);
          this.logger.debug(SolicitudListadoComponent.name, 'loadTable()', 'end');
          return of([]);
        })
      );
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.registro) {
      if (this.registro.servicio) {
        this.servicioSeleccionado = this.registro.servicio;
      }
    }
  }

  private buildFilters(): SgiRestFilter[] {
    this.logger.debug(SolicitudListadoComponent.name, 'buildFilters()', 'start');
    if (
      this.filter.field &&
      this.filter.type !== SgiRestFilterType.NONE &&
      this.filter.value
    ) {
      this.logger.debug(SolicitudListadoComponent.name, 'buildFilters()', 'end');
      return [this.filter];
    }
    this.logger.debug(SolicitudListadoComponent.name, 'buildFilters()', 'end');
    return [];
  }

  /**
   * Load table data
   */
  public onSearch() {
    this.logger.debug(SolicitudListadoComponent.name, 'onSearch()', 'start');
    this.loadTable(true);
    this.logger.debug(SolicitudListadoComponent.name, 'onSearch()', 'end');

  }

  /**
   * Clean filters an reload the table
   */
  public onClearFilters() {
    this.logger.debug(SolicitudListadoComponent.name, 'onClearFilters()', 'start');
    this.filter = {
      field: undefined,
      type: SgiRestFilterType.NONE,
      value: '',
    };
    this.loadTable(true);
    this.logger.debug(SolicitudListadoComponent.name, 'onClearFilters()', 'end');
  }

  /**
   * Selecciona el servicio en el que se quiere dar de alta y se actualiza el registro
   * @param servicio Servicio
   */
  seleccionarServicio(servicio: Servicio): void {
    this.servicioSeleccionado = servicio;
    this.solicitudService.registro.servicio = servicio;
    this.actualizarSolicitud();
  }

  /**
   * Actualiza un registro existente en el back
   */
  private actualizarSolicitud() {
    this.logger.debug(
      SolicitudListadoComponent.name,
      'actualizarSolicitud()',
      'start'
    );
    this.registro = this.solicitudService.registro;
    this.updateSolicitudService = this.solicitudService
      .update(this.registro.id, this.registro)
      .subscribe(
        () => {
          this.snackBarService.showSuccess(MSG_SUCCESS_ALTA);
          this.logger.debug(
            SolicitudListadoComponent.name,
            'actualizarSolicitud()',
            'end'
          );
        },
        () => {
          this.snackBarService.showError(MSG_ERROR_ALTA);
          this.logger.debug(
            SolicitudListadoComponent.name,
            'actualizarSolicitud()',
            'end'
          );
        }
      );
  }

  ngOnDestroy(): void {
    this.logger.debug(
      SolicitudListadoComponent.name,
      'ngOnDestroy() - start'
    );
    this.updateSolicitudService?.unsubscribe();
    this.logger.debug(SolicitudListadoComponent.name, 'ngOnDestroy() - end');
  }

}
