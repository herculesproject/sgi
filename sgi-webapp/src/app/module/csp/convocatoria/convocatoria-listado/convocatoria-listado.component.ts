import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SgiRestFilter, SgiRestSortDirection } from '@sgi/framework/http/';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { NGXLogger } from 'ngx-logger';
import { FormGroup, FormControl } from '@angular/forms';
import { Observable, of, merge } from 'rxjs';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { map, catchError, tap, delay, startWith } from 'rxjs/operators';

import { ROUTE_NAMES } from '@core/route.names';

import { SnackBarService } from '@core/services/snack-bar.service';
import { IConvocatoria } from '@core/models/csp/convocatoria';

import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';

const MSG_BUTTON_NEW = marker('footer.csp.convocatoria.crear');
const MSG_ERROR = marker('csp.convocatoria.listado.error');

@Component({
  selector: 'sgi-convocatoria-listado',
  templateUrl: './convocatoria-listado.component.html',
  styleUrls: ['./convocatoria-listado.component.scss']
})
export class ConvocatoriaListadoComponent implements AfterViewInit, OnInit {

  ROUTE_NAMES = ROUTE_NAMES;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  convocatorias$: Observable<IConvocatoria[]> = of();

  displayedColumns: string[];
  elementosPagina: number[];
  totalElementos: number;
  filter: SgiRestFilter[];

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;

  buscadorFormGroup: FormGroup;

  textoCrear = MSG_BUTTON_NEW;

  constructor(
    private readonly logger: NGXLogger,
    private readonly snackBarService: SnackBarService,
    private readonly convocatoriaService: ConvocatoriaService) {

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(17%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

    this.displayedColumns = ['referencia', 'titulo', 'fechaInicioSolicitud', 'fechaFinSolicitud',
      'estadoConvocante', 'planInvestigacion', 'entidadFinanciadora', 'fuenteFinanciacion', 'activo', 'acciones'];
  }

  ngOnInit(): void {
    this.logger.debug(ConvocatoriaListadoComponent.name, 'ngOnInit()', 'start');

    this.buscadorFormGroup = new FormGroup({
      titulo: new FormControl('', []),
      referencia: new FormControl('', []),

    });

    this.logger.debug(ConvocatoriaListadoComponent.name, 'ngOnInit()', 'end');
  }

  ngAfterViewInit(): void {

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
        }),
      )
      .subscribe();
    this.loadTable();

  }


  private loadTable(reset?: boolean) {
    this.logger.debug(ConvocatoriaListadoComponent.name, 'loadTable()', 'start');
    // Do the request with paginator/sort/filter values
    this.convocatorias$ = this.convocatoriaService
      .findConvocatoria({
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
        // TODO eliminar delay cuando se realice llamada al back.
        delay(0),
        map((response) => {
          // Map response total
          this.totalElementos = response.total;
          // Reset pagination to first page
          if (reset) {
            this.paginator.pageIndex = 0;
          }
          this.logger.debug(ConvocatoriaListadoComponent.name, 'loadTable()', 'end');
          // Return the values
          return response.items;
        }),
        catchError(() => {
          // On error reset pagination values
          this.paginator.firstPage();
          this.totalElementos = 0;
          this.snackBarService.showError(MSG_ERROR);
          this.logger.debug(ConvocatoriaListadoComponent.name, 'loadTable()', 'end');
          return of([]);
        })
      );
  }

  private buildFilters(): SgiRestFilter[] {
    this.logger.debug(ConvocatoriaListadoComponent.name, 'buildFilters()', 'start');

    this.filter = [];


    this.logger.debug(ConvocatoriaListadoComponent.name, 'buildFilters()', 'end');
    return this.filter;
  }



  /**
   * Clean filters an reload the table
   */
  public onClearFilters() {

    this.logger.debug(ConvocatoriaListadoComponent.name,
      'onClearFilters()',
      'start');
    this.filter = [];
    this.buscadorFormGroup.reset();


    this.logger.debug(ConvocatoriaListadoComponent.name,
      'onClearFilters()',
      'end');
  }

}
