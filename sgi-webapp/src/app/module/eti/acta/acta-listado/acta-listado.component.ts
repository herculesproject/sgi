import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { Component, OnInit, ViewChild, AfterViewInit, OnDestroy } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { FormGroup, FormControl } from '@angular/forms';
import { Observable, of, merge, Subscription, zip } from 'rxjs';
import { NGXLogger } from 'ngx-logger';

import { SgiRestFilter, SgiRestFilterType, SgiRestSortDirection } from '@sgi/framework/http';
import { tap, map, catchError, startWith, switchMap } from 'rxjs/operators';

import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';

import { Acta } from '@core/models/eti/acta';
import { Comite } from '@core/models/eti/comite';
import { TipoEstadoActa } from '@core/models/eti/tipo-estado-acta';

import { ComiteService } from '@core/services/eti/comite.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TipoEstadoActaService } from '@core/services/eti/tipo-estado-acta.service';


import { DateUtils } from '@core/utils/date-utils';

import { ActaListado } from '@core/models/eti/acta-listado';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { ActaService } from '@core/services/eti/acta.service';
import { ROUTE_NAMES } from '@core/route.names';

const MSG_BUTTON_NEW = marker('footer.eti.acta.crear');
const MSG_ERROR = marker('eti.acta.listado.error');

@Component({
  selector: 'sgi-acta-listado',
  templateUrl: './acta-listado.component.html',
  styleUrls: ['./acta-listado.component.scss']
})
export class ActaListadoComponent implements AfterViewInit, OnInit, OnDestroy {
  ROUTE_NAMES = ROUTE_NAMES;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns: string[];
  elementosPagina: number[];
  totalElementos: number;
  filter: SgiRestFilter[];

  filterActivo = {
    field: 'activo',
    type: SgiRestFilterType.EQUALS,
    value: 'true'
  };

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;

  actas$: Observable<ActaListado[]> = of();

  comiteListado: Comite[];
  comitesSubscription: Subscription;
  filteredComites: Observable<Comite[]>;

  tipoEstadoActaListado: TipoEstadoActa[];
  tipoEstadoActaSubscription: Subscription;
  filteredTipoEstadoActa: Observable<TipoEstadoActa[]>;

  buscadorFormGroup: FormGroup;

  textoCrear = MSG_BUTTON_NEW;

  constructor(
    private readonly logger: NGXLogger,
    private readonly actasService: ActaService,
    private readonly snackBarService: SnackBarService,
    private readonly comiteService: ComiteService,
    private readonly tipoEstadoActaService: TipoEstadoActaService,
    private readonly evaluacionService: EvaluacionService
  ) {
    this.displayedColumns = ['comite', 'fechaEvaluacion', 'numero', 'convocatoria',
      'numeroIniciales', 'numeroRevisiones', 'numeroTotal', 'estadoActual.nombre', 'acciones'];
    this.elementosPagina = [5, 10, 25, 100];
    this.totalElementos = 0;

    this.filter = [{
      field: undefined,
      type: SgiRestFilterType.NONE,
      value: '',
    }];

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(17%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit(): void {
    this.logger.debug(ActaListadoComponent.name, 'ngOnInit()', 'start');

    this.buscadorFormGroup = new FormGroup({
      comite: new FormControl('', []),
      fechaEvaluacionInicio: new FormControl('', []),
      fechaEvaluacionFin: new FormControl('', []),
      numeroActa: new FormControl('', []),
      tipoEstadoActa: new FormControl('', [])
    });

    this.getComites();

    this.getTipoEstadoActas();

    this.logger.debug(ActaListadoComponent.name, 'ngOnInit()', 'end');
  }

  ngAfterViewInit(): void {
    this.logger.debug(ActaListadoComponent.name, 'ngAfterViewInit()', 'start');

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

    this.logger.debug(ActaListadoComponent.name, 'ngAfterViewInit()', 'end');
  }

  private loadTable(reset?: boolean) {
    this.logger.debug(ActaListadoComponent.name, 'loadTable()', 'start');
    // Do the request with paginator/sort/filter values
    this.actas$ = this.actasService
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
          this.logger.debug(ActaListadoComponent.name, 'loadTable()', 'end');
          // Return the values
          return response.items;
        }),
        switchMap((actas: Acta[]) => {
          const actasListado$: Observable<any>[] = [];
          actas.forEach(acta => {
            const actaListado: ActaListado = new ActaListado(acta);

            let filter: SgiRestFilter[] = [];
            const filterConvocatoriaReunion: SgiRestFilter = {
              field: 'convocatoriaReunion.id',
              type: SgiRestFilterType.EQUALS,
              value: acta.convocatoriaReunion.id.toString(),
            };

            filter.push(filterConvocatoriaReunion);

            let filterTipoEstado: SgiRestFilter = {
              field: 'memoria.estadoActual.id',
              type: SgiRestFilterType.EQUALS,
              value: '1',
            };

            filter.push(filterTipoEstado);

            actaListado.numeroMemoriasTotales = 0;

            const memorias$ = this.evaluacionService
              .findAll({
                page: {
                  index: 0,
                  size: null
                },
                filters: filter
              }).pipe(
                switchMap((responseIniciales) => {
                  actaListado.numeroMemoriasIniciales = responseIniciales.total;
                  actaListado.numeroMemoriasTotales = actaListado.numeroMemoriasTotales + actaListado.numeroMemoriasIniciales;

                  filter = [];

                  filter.push(filterConvocatoriaReunion);

                  filterTipoEstado = {
                    field: 'memoria.estadoActual.id',
                    type: SgiRestFilterType.EQUALS,
                    value: '1',
                  };

                  filter.push(filterTipoEstado);


                  const actaListado$ = this.evaluacionService
                    .findAll({
                      page: {
                        index: 0,
                        size: null
                      },

                      filters: filter
                    }).pipe(
                      map((responseRevision) => {

                        actaListado.numeroMemoriasRevisiones = responseRevision.total;
                        actaListado.numeroMemoriasTotales = actaListado.numeroMemoriasTotales + actaListado.numeroMemoriasRevisiones;
                        return actaListado;
                      })
                    );

                  return actaListado$;

                })
              );


            actasListado$.push(memorias$);
          });

          return zip(...actasListado$);
        }),
        catchError(() => {
          // On error reset pagination values
          this.paginator.firstPage();
          this.totalElementos = 0;
          this.snackBarService.showError(MSG_ERROR);
          this.logger.debug(ActaListadoComponent.name, 'loadTable()', 'end');
          return of([]);
        })
      );
  }


  private buildFilters(): SgiRestFilter[] {
    this.logger.debug(ActaListadoComponent.name, 'buildFilters()', 'start');

    this.filter = [];
    if (this.buscadorFormGroup.controls.comite.value) {
      this.logger.debug(ActaListadoComponent.name, 'buildFilters()', 'comite');
      const filterComite: SgiRestFilter = {
        field: 'convocatoriaReunion.comite.id',
        type: SgiRestFilterType.EQUALS,
        value: this.buscadorFormGroup.controls.comite.value.id,
      };

      this.filter.push(filterComite);

    }
    if (this.buscadorFormGroup.controls.fechaEvaluacionInicio.value) {
      this.logger.debug(ActaListadoComponent.name, 'buildFilters()', 'fechaEvaluacionInicio');
      const fechaFilter = DateUtils.getFechaInicioDia(this.buscadorFormGroup.controls.fechaEvaluacionInicio.value);
      const filterFechaEvaluacionInicio: SgiRestFilter = {
        field: 'convocatoriaReunion.fechaEvaluacion',
        type: SgiRestFilterType.GREATHER_OR_EQUAL,
        value: DateUtils.formatFechaAsISODateTime(fechaFilter),
      };

      this.filter.push(filterFechaEvaluacionInicio);

    }

    if (this.buscadorFormGroup.controls.fechaEvaluacionFin.value) {
      this.logger.debug(ActaListadoComponent.name, 'buildFilters()', 'fechaEvaluacionFin');
      const fechaFilter = DateUtils.getFechaFinDia(this.buscadorFormGroup.controls.fechaEvaluacionFin.value);
      const filterFechaEvaluacionFin: SgiRestFilter = {
        field: 'convocatoriaReunion.fechaEvaluacion',
        type: SgiRestFilterType.LOWER_OR_EQUAL,
        value: DateUtils.formatFechaAsISODateTime(fechaFilter),
      };

      this.filter.push(filterFechaEvaluacionFin);

    }

    if (this.buscadorFormGroup.controls.numeroActa.value) {
      this.logger.debug(ActaListadoComponent.name, 'buildFilters()', 'numeroActa');
      const filterNumeroActa: SgiRestFilter = {
        field: 'numero',
        type: SgiRestFilterType.EQUALS,
        value: this.buscadorFormGroup.controls.numeroActa.value,
      };

      this.filter.push(filterNumeroActa);

    }

    if (this.buscadorFormGroup.controls.tipoEstadoActa.value) {
      this.logger.debug(ActaListadoComponent.name, 'buildFilters()', 'tipoEstadoActa');
      const filterTipoEstadoActa: SgiRestFilter = {
        field: 'estadoActual.id',
        type: SgiRestFilterType.EQUALS,
        value: this.buscadorFormGroup.controls.tipoEstadoActa.value.id,
      };

      this.filter.push(filterTipoEstadoActa);

    }

    this.filter.push(this.filterActivo);
    this.logger.debug(ActaListadoComponent.name, 'buildFilters()', 'end');
    return this.filter;
  }

  /**
   * Devuelve el nombre de un comité.
   * @param comite comité
   * returns nombre comité
   */
  getComite(comite: Comite): string {

    return comite?.comite;

  }

  /**
   * Devuelve el nombre de un tipo estado acta.
   * @param estado tipo estado acta
   * returns nombre tipo estado acta
   */
  getTipoEstadoActa(estado: TipoEstadoActa): string {

    return estado?.nombre;

  }

  /**
   * Recupera un listado de los comités que hay en el sistema.
   */
  getComites(): void {
    this.logger.debug(ActaListadoComponent.name,
      'getComites()',
      'start');

    this.comitesSubscription = this.comiteService.findAll({ filters: [this.filterActivo] }).subscribe(
      (response) => {
        this.comiteListado = response.items;

        this.filteredComites = this.buscadorFormGroup.controls.comite.valueChanges
          .pipe(
            startWith(''),
            map(value => this._filterComite(value))
          );
      });

    this.logger.debug(ActaListadoComponent.name,
      'getComites()',
      'end');
  }

  /**
   * Recupera un listado de los tipos de estados de acta que hay en el sistema.
   */
  getTipoEstadoActas(): void {
    this.logger.debug(ActaListadoComponent.name,
      'getTipoEstadoActa()',
      'start');

    this.tipoEstadoActaSubscription = this.tipoEstadoActaService.findAll({ filters: [this.filterActivo] }).subscribe(
      (response) => {
        this.tipoEstadoActaListado = response.items;

        this.filteredTipoEstadoActa = this.buscadorFormGroup.controls.tipoEstadoActa.valueChanges
          .pipe(
            startWith(''),
            map(value => this._filterTipoEstado(value))
          );
      });

    this.logger.debug(ActaListadoComponent.name,
      'getTipoEstadoActa()',
      'end');
  }


  /**
   * Filtro de campo autocompletable comité.
   * @param value value a filtrar (string o nombre comité).
   * @returns lista de comités filtrados.
   */
  private _filterComite(value: string | Comite): Comite[] {
    let filterValue: string;
    if (typeof value === 'string') {
      filterValue = value.toLowerCase();
    } else {
      filterValue = value.comite.toLowerCase();
    }

    return this.comiteListado.filter
      (comite => comite.comite.toLowerCase().includes(filterValue));
  }

  /**
   * Filtro de campo autocompletable tipo estado acta.
   * @param value value a filtrar (string o nombre tipo estado acta).
   * @returns lista de tipos de estados filtrados.
   */
  private _filterTipoEstado(value: string | TipoEstadoActa): TipoEstadoActa[] {
    let filterValue: string;
    if (typeof value === 'string') {
      filterValue = value.toLowerCase();
    } else {
      filterValue = value.nombre.toLowerCase();
    }

    return this.tipoEstadoActaListado.filter
      (tipoEstadoActa => tipoEstadoActa.nombre.toLowerCase().includes(filterValue));
  }

  /**
   * Load table data
   */
  public onSearch() {
    this.loadTable(true);
  }


  /**
   * Clean filters an reload the table
   */
  public onClearFilters() {

    this.logger.debug(ActaListadoComponent.name,
      'onClearFilters()',
      'start');
    this.filter = [];
    this.buscadorFormGroup.reset();
    this.loadTable(true);

    this.logger.debug(ActaListadoComponent.name,
      'onClearFilters()',
      'end');
  }


  ngOnDestroy(): void {
    this.logger.debug(ActaListadoComponent.name,
      'ngOnDestroy()',
      'start');
    this.comitesSubscription?.unsubscribe();

    this.logger.debug(ActaListadoComponent.name,
      'ngOnDestroy()',
      'end');

  }

}
