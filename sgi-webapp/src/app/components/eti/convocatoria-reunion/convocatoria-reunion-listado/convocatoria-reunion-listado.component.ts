import { Component, OnInit, AfterViewInit, OnDestroy, ViewChild } from '@angular/core';
import { UrlUtils } from '@core/utils/url-utils';
import { DateUtils } from '@core/utils/date-utils';
import { Filter, FilterType, Direction } from '@core/services/types';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { Observable, of, Subscription, merge } from 'rxjs';
import { ConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';
import { NGXLogger } from 'ngx-logger';
import { TraductorService } from '@core/services/traductor.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { ConvocatoriaReunionService } from '@core/services/eti/convocatoria-reunion.service';
import { tap, map, catchError, startWith } from 'rxjs/operators';
import { FormGroup, FormBuilder, FormControl } from '@angular/forms';
import { FxFlexProperties } from '@core/models/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/flexLayout/fx-layout-properties';
import { FormGroupUtil } from '@core/services/form-group-util';
import { Comite } from '@core/models/eti/comite';
import { ComiteService } from '@core/services/eti/comite.service';
import { TipoConvocatoriaReunionService } from '@core/services/eti/tipo-convocatoria-reunion.service';
import { TipoConvocatoriaReunion } from '@core/models/eti/tipo-convocatoria-reunion';

@Component({
  selector: 'app-convocatoria-reunion-listado',
  templateUrl: './convocatoria-reunion-listado.component.html',
  styleUrls: ['./convocatoria-reunion-listado.component.scss']
})
export class ConvocatoriaReunionListadoComponent implements OnInit, AfterViewInit, OnDestroy {

  UrlUtils = UrlUtils;

  FormGroupUtil = FormGroupUtil;

  displayedColumns: string[];
  elementosPagina: number[];
  totalElementos: number;
  filter: Filter[];

  filterActivo = {
    field: 'activo',
    type: FilterType.EQUALS,
    value: 'true'
  };

  buscadorFormGroup: FormGroup;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;


  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;

  comiteListado: Comite[];
  filteredComites: Observable<Comite[]>;

  tipoConvocatoriaReunionListado: TipoConvocatoriaReunion[];
  filteredTiposConvocatoriaReunion: Observable<TipoConvocatoriaReunion[]>;

  convocatoriaReunion$: Observable<ConvocatoriaReunion[]> = of();
  dialogSubscription: Subscription;
  convocatoriaReunionDeleteSubscription: Subscription;
  comitesSubscription: Subscription;
  tiposConvocatoriaReunionSubscription: Subscription;

  constructor(
    private readonly logger: NGXLogger,
    private readonly convocatoriaReunionService: ConvocatoriaReunionService,
    private readonly traductor: TraductorService,
    private readonly dialogService: DialogService,
    private readonly snackBarService: SnackBarService,
    private readonly comiteService: ComiteService,
    private readonly tipoConvocatoriaReunionService: TipoConvocatoriaReunionService,
    private formBuilder: FormBuilder
  ) {
    this.logger.debug(ConvocatoriaReunionListadoComponent.name, 'constructor()', 'start');

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

    this.displayedColumns = ['comite', 'fecha-evaluacion', 'codigo', 'hora', 'lugar', 'tipoConvocatoriaReunion', 'fechaEnvio', 'acciones'];
    this.elementosPagina = [5, 10, 25, 100];
    this.totalElementos = 0;
    this.filter = [];

    this.logger.debug(ConvocatoriaReunionListadoComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(ConvocatoriaReunionListadoComponent.name, 'ngOnInit()', 'start');

    // Inicializa el formulario de busqueda
    this.buscadorFormGroup = this.formBuilder.group({
      comite: new FormControl('', []),
      tipoConvocatoriaReunion: new FormControl('', []),
      fechaEvaluacionDesde: new FormControl('', []),
      fechaEvaluacionHasta: new FormControl('', [])
    });

    // Recupera los valores de los combos
    this.getComites();
    this.getTiposConvocatoriaReunion();

    this.logger.debug(ConvocatoriaReunionListadoComponent.name, 'ngOnInit()', 'end');
  }

  ngAfterViewInit(): void {
    this.logger.debug(ConvocatoriaReunionListadoComponent.name, 'ngAfterViewInit()', 'start');

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

    this.logger.debug(ConvocatoriaReunionListadoComponent.name, 'ngAfterViewInit()', 'end');
  }


  /**
   * Recupera un listado de los comites que hay en el sistema.
   */
  private getComites(): void {
    this.logger.debug(ConvocatoriaReunionListadoComponent.name,
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

    this.logger.debug(ConvocatoriaReunionListadoComponent.name,
      'getComites()',
      'end');
  }

  /**
   * Recupera un listado de los tipos de convocatoria reunion que hay en el sistema.
   */
  private getTiposConvocatoriaReunion(): void {
    this.logger.debug(ConvocatoriaReunionListadoComponent.name,
      'getTiposConvocatoriaReunion()',
      'start');

    this.tiposConvocatoriaReunionSubscription = this.tipoConvocatoriaReunionService.findAll({ filters: [this.filterActivo] }).subscribe(
      (response) => {
        this.tipoConvocatoriaReunionListado = response.items;

        this.filteredTiposConvocatoriaReunion = this.buscadorFormGroup.controls.tipoConvocatoriaReunion.valueChanges
          .pipe(
            startWith(''),
            map(value => this._filterTipoConvocatoriaReunion(value))
          );
      });

    this.logger.debug(ConvocatoriaReunionListadoComponent.name,
      'getTiposConvocatoriaReunion()',
      'end');
  }

  /**
   * Devuelve el nombre del comite
   * @param comite comite
   *
   * @returns nombre del comite
   */
  getComite(comite: Comite): string {
    return comite?.comite;
  }

  /**
   * Devuelve el nombre del tipo de convocatoria reunion
   * @param tipoConvocatoriaReunion tipo convocatoria reunion
   *
   * @returns nombre del tipo de convocatoria reunion
   */
  getTipoConvocatoriaReunion(tipoConvocatoriaReunion: TipoConvocatoriaReunion): string {
    return tipoConvocatoriaReunion?.nombre;
  }


  /**
   * Filtro de campo autocompletable comite.
   * @param value value a filtrar (string o Comite.
   * @returns lista de comites filtrada.
   */
  private _filterComite(value: string | Comite): Comite[] {
    if (!value) {
      return this.comiteListado;
    }

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
   * Filtro de campo autocompletable tipo convocatoria reunion.
   * @param value value a filtrar (string o TipoConvocatoriaReunion).
   * @returns lista de tipos de convocatoria reunion filtrada.
   */
  private _filterTipoConvocatoriaReunion(value: string | TipoConvocatoriaReunion): TipoConvocatoriaReunion[] {
    if (!value) {
      return this.tipoConvocatoriaReunionListado;
    }

    let filterValue: string;
    if (typeof value === 'string') {
      filterValue = value.toLowerCase();
    } else {
      filterValue = value.nombre.toLowerCase();
    }

    return this.tipoConvocatoriaReunionListado.filter
      (tipoConvocatoriaReunion => tipoConvocatoriaReunion.nombre.toLowerCase().includes(filterValue));
  }

  private loadTable(reset?: boolean) {
    this.logger.debug(ConvocatoriaReunionListadoComponent.name, 'loadTable()', 'start');
    // Do the request with paginator/sort/filter values
    this.convocatoriaReunion$ = this.convocatoriaReunionService
      .findAll({
        page: {
          index: reset ? 0 : this.paginator.pageIndex,
          size: this.paginator.pageSize
        },
        sort: {
          direction: Direction.fromSortDirection(this.sort.direction),
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
          this.logger.debug(ConvocatoriaReunionListadoComponent.name, 'loadTable()', 'end');
          // Return the values
          return response.items;
        }),
        catchError(() => {
          // On error reset pagination values
          this.paginator.firstPage();
          this.totalElementos = 0;
          this.snackBarService.mostrarMensajeError(
            this.traductor.getTexto('eti.convocatoriaReunion.listado.error')
          );
          this.logger.debug(ConvocatoriaReunionListadoComponent.name, 'loadTable()', 'end');
          return of([]);
        })
      );
  }

  /**
   * Load table data
   */
  public onSearch() {
    this.logger.debug(ConvocatoriaReunionListadoComponent.name, 'onSearch()', 'start');
    this.loadTable(true);
    this.logger.debug(ConvocatoriaReunionListadoComponent.name, 'onSearch()', 'end');

  }

  /**
   * Clean filters an reload the table
   */
  public onClearFilters() {
    this.logger.debug(ConvocatoriaReunionListadoComponent.name, 'onClearFilters()', 'start');
    this.filter = [];
    this.buscadorFormGroup.reset();
    this.loadTable(true);
    this.logger.debug(ConvocatoriaReunionListadoComponent.name, 'onClearFilters()', 'end');
  }

  /**
   * Elimina la convocatoria reunion.
   * @param convocatoriaReunionId id de la convocatoria reunion a eliminar.
   * @param event evento lanzado.
   */
  borrar(convocatoriaReunionId: number, $event: Event): void {
    this.logger.debug(ConvocatoriaReunionListadoComponent.name,
      'borrar(convocatoriaReunionId: number, $event: Event) - start');

    $event.stopPropagation();
    $event.preventDefault();

    this.dialogService.dialogGenerico(
      this.traductor.getTexto('eti.convocatoriaReunion.listado.eliminar'),
      this.traductor.getTexto('eti.convocatoriaReunion.listado.aceptar'),
      this.traductor.getTexto('eti.convocatoriaReunion.listado.cancelar'));

    this.dialogSubscription = this.dialogService.getAccionConfirmada().subscribe(
      (aceptado: boolean) => {
        if (aceptado) {
          this.convocatoriaReunionDeleteSubscription = this.convocatoriaReunionService
            .deleteById(convocatoriaReunionId).pipe(
              map(() => {
                return this.loadTable();
              })
            ).subscribe(() => {
              this.snackBarService
                .mostrarMensajeSuccess(
                  this.traductor.getTexto('eti.convocatoriaReunion.listado.eliminarConfirmado'));
            });
        }
        aceptado = false;
      });

    this.logger.debug(ConvocatoriaReunionListadoComponent.name,
      'borrarSeleccionado(convocatoriaReunionId: number, $event: Event) - end');
  }

  /**
   * Envia la convocatoria reunion.
   * @param convocatoriaReunionId id de la convocatoria reunion a enviar.
   * @param event evento lanzado.
   */
  enviar(convocatoriaReunionId: number, $event: Event): void {
  }

  private buildFilters(): Filter[] {
    this.logger.debug(ConvocatoriaReunionListadoComponent.name, 'buildFilters()', 'start');
    this.filter = [];
    this.filter.push(this.filterActivo);

    const comite = FormGroupUtil.getValue(this.buscadorFormGroup, 'comite');
    if (comite) {
      const filterComite = {
        field: 'comite.id',
        type: FilterType.EQUALS,
        value: comite.id,
      };

      this.filter.push(filterComite);
    }

    const tipoConvocatoriaReunion = FormGroupUtil.getValue(this.buscadorFormGroup, 'tipoConvocatoriaReunion');
    if (tipoConvocatoriaReunion) {
      const filterTipoConvocatoriaReunion = {
        field: 'tipoConvocatoriaReunion.id',
        type: FilterType.EQUALS,
        value: tipoConvocatoriaReunion.id,
      };

      this.filter.push(filterTipoConvocatoriaReunion);
    }

    const fechaEvaluacionDesde = FormGroupUtil.getValue(this.buscadorFormGroup, 'fechaEvaluacionDesde');
    if (fechaEvaluacionDesde) {
      const fechaFilter = DateUtils.getFechaInicioDia(fechaEvaluacionDesde);
      const filterFechaEvaluacionDesde = {
        field: 'fechaEvaluacion',
        type: FilterType.GREATHER_OR_EQUAL,
        value: DateUtils.formatFechaAsISODateTime(fechaFilter),
      };

      this.filter.push(filterFechaEvaluacionDesde);
    }

    const fechaEvaluacionHasta = FormGroupUtil.getValue(this.buscadorFormGroup, 'fechaEvaluacionHasta');
    if (fechaEvaluacionHasta) {
      const fechaFilter = DateUtils.getFechaFinDia(fechaEvaluacionHasta);
      const filterFechaEvaluacionHasta = {
        field: 'fechaEvaluacion',
        type: FilterType.LOWER_OR_EQUAL,
        value: DateUtils.formatFechaAsISODateTime(fechaFilter),
      };

      this.filter.push(filterFechaEvaluacionHasta);
    }

    this.logger.debug(ConvocatoriaReunionListadoComponent.name, 'buildFilters()', 'end');

    return this.filter;
  }

  ngOnDestroy(): void {
    this.logger.debug(ConvocatoriaReunionListadoComponent.name, 'ngOnDestroy()', 'start');
    this.dialogSubscription?.unsubscribe();
    this.convocatoriaReunionDeleteSubscription?.unsubscribe();
    this.comitesSubscription?.unsubscribe();
    this.tiposConvocatoriaReunionSubscription?.unsubscribe();
    this.logger.debug(ConvocatoriaReunionListadoComponent.name, 'ngOnDestroy()', 'end');
  }

}
