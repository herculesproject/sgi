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

import { IComite } from '@core/models/eti/comite';
import { TipoEstadoActa } from '@core/models/eti/tipo-estado-acta';

import { ComiteService } from '@core/services/eti/comite.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TipoEstadoActaService } from '@core/services/eti/tipo-estado-acta.service';

import { DateUtils } from '@core/utils/date-utils';
import { IActaEvaluaciones } from '@core/models/eti/acta-evaluaciones';
import { ActaService } from '@core/services/eti/acta.service';
import { ROUTE_NAMES } from '@core/route.names';
import { Router, ActivatedRoute } from '@angular/router';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { IActa } from '@core/models/eti/acta';

const MSG_BUTTON_NEW = marker('footer.eti.acta.crear');
const MSG_ERROR = marker('eti.acta.listado.error');
const MSG_FINALIZAR_ERROR = marker('eti.acta.listado.finalizar.error');
const MSG_FINALIZAR_SUCCESS = marker('eti.acta.listado.finalizar.correcto');

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

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;

  actas$: Observable<IActaEvaluaciones[]> = of();

  comiteListado: IComite[];
  comitesSubscription: Subscription;
  filteredComites: Observable<IComite[]>;

  tipoEstadoActaListado: TipoEstadoActa[];
  tipoEstadoActaSubscription: Subscription;
  filteredTipoEstadoActa: Observable<TipoEstadoActa[]>;

  finalizarSubscription: Subscription;

  buscadorFormGroup: FormGroup;

  textoCrear = MSG_BUTTON_NEW;

  constructor(
    private readonly logger: NGXLogger,
    private readonly actasService: ActaService,
    private readonly snackBarService: SnackBarService,
    private readonly comiteService: ComiteService,
    private readonly tipoEstadoActaService: TipoEstadoActaService,
    private readonly router: Router,
    private readonly evaluacionService: EvaluacionService,
    private route: ActivatedRoute,
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
      .findActivasWithEvaluaciones({
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

    this.logger.debug(ActaListadoComponent.name, 'buildFilters()', 'end');
    return this.filter;
  }

  /**
   * Devuelve el nombre de un comité.
   * @param comite comité
   * returns nombre comité
   */
  getComite(comite: IComite): string {

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

    this.comitesSubscription = this.comiteService.findAll().subscribe(
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

    this.tipoEstadoActaSubscription = this.tipoEstadoActaService.findAll().subscribe(
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
  private _filterComite(value: string | IComite): IComite[] {
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



  /**
   * Finaliza el acta con el id recibido.
   * @param actaId id del acta a finalizar.
   */
  finishActa(actaId: number) {
    this.logger.debug(ActaListadoComponent.name,
      'finishActa()',
      'start');
    this.finalizarSubscription = this.actasService.finishActa(actaId).subscribe((acta) => {
      this.snackBarService.showSuccess(MSG_FINALIZAR_SUCCESS);
      this.loadTable(false);
    },
      catchError(() => {
        // On error reset pagination values
        this.paginator.firstPage();
        this.totalElementos = 0;
        this.snackBarService.showError(MSG_FINALIZAR_ERROR);
        this.logger.debug(ActaListadoComponent.name, 'loadTable()', 'end');
        return of([]);
      }));


    this.logger.debug(ActaListadoComponent.name,
      'finishActa()',
      'end');

  }

  /**
   * Comprueba si una acta se encuentra en estado finalizada.
   * @param acta acta a comprobar.
   * @return indicador de si el acta se encuentra finalizada.
   */
  isFinalizada(acta: IActaEvaluaciones): boolean {
    return acta.estadoActa.id === 2;
  }

  /**
   * Comprueba si una acta puede ser finalizado.
   * @param acta acta a comprobar.
   * @return indicador de si se puede finalizar el acta.
   */
  hasFinalizarActa(acta: IActaEvaluaciones): boolean {
    return acta.estadoActa.id === 1 && acta.numEvaluacionesNoEvaluadas === 0;
  }


  ngOnDestroy(): void {
    this.logger.debug(ActaListadoComponent.name,
      'ngOnDestroy()',
      'start');
    this.comitesSubscription?.unsubscribe();
    this.finalizarSubscription?.unsubscribe();

    this.logger.debug(ActaListadoComponent.name,
      'ngOnDestroy()',
      'end');

  }

}
