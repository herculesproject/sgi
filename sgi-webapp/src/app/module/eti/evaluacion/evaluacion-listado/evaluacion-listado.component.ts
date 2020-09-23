import { Component, OnInit, OnDestroy, AfterViewInit, ViewChild } from '@angular/core';
import { SgiRestFilter, SgiRestFilterType, SgiRestSortDirection } from '@sgi/framework/http';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { NGXLogger } from 'ngx-logger';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { TipoConvocatoriaReunionService } from '@core/services/eti/tipo-convocatoria-reunion.service';
import { ComiteService } from '@core/services/eti/comite.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormGroup, FormControl } from '@angular/forms';
import { Observable, Subscription, of, merge, zip } from 'rxjs';
import { TipoConvocatoriaReunion } from '@core/models/eti/tipo-convocatoria-reunion';
import { Comite } from '@core/models/eti/comite';
import { tap, map, switchMap, catchError, startWith } from 'rxjs/operators';
import { DateUtils } from '@core/utils/date-utils';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';
import { IEvaluacionSolicitante } from '@core/models/eti/dto/evaluacion-solicitante';
import { Persona } from '@core/models/sgp/persona';

const MSG_ERROR = marker('eti.evaluacion.listado.error');
const MSG_ERROR_LOAD_TIPOS_CONVOCATORIA = marker('eti.evaluacion.listado.buscador.tipoConvocatoria.error');
const TEXT_USER_TITLE = marker('eti.buscarSolicitante.titulo');
const TEXT_USER_BUTTON = marker('eti.buscarSolicitante.boton.buscar');

@Component({
  selector: 'sgi-evaluacion-listado',
  templateUrl: './evaluacion-listado.component.html',
  styleUrls: ['./evaluacion-listado.component.scss']
})
export class EvaluacionListadoComponent implements OnInit, OnDestroy, AfterViewInit {

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns: string[];
  elementosPagina: number[];
  totalElementos: number;
  filter: SgiRestFilter[];

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;

  evaluaciones$: Observable<IEvaluacionSolicitante[]> = of();

  comiteListado: Comite[];
  comitesSubscription: Subscription;
  filteredComites: Observable<Comite[]>;

  tipoConvocatoriaReunionListado: TipoConvocatoriaReunion[];
  tipoConvocatoriasReunionSubscription: Subscription;
  filteredTipoConvocatoriaReunion: Observable<TipoConvocatoriaReunion[]>;
  subscripciones: Subscription[];

  buscadorFormGroup: FormGroup;

  textoUsuarioLabel = TEXT_USER_TITLE;
  textoUsuarioInput = TEXT_USER_TITLE;
  textoUsuarioButton = TEXT_USER_BUTTON;
  datosUsuarioSolicitante: string;
  personaRefSolicitante: string;

  constructor(
    private readonly logger: NGXLogger,
    private readonly evaluacionesService: EvaluacionService,
    private readonly snackBarService: SnackBarService,
    private readonly comiteService: ComiteService,
    private readonly tipoConvocatoriaReunionService: TipoConvocatoriaReunionService,
    protected readonly personaFisicaService: PersonaFisicaService

  ) {
    this.displayedColumns = ['convocatoriaReunion.comite.comite', 'fechaDictamen', 'memoria.numReferencia', 'solicitante',
      'dictamen.nombre', 'version', 'acciones'];
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
    this.fxFlexProperties.gtMd = '0 1 calc(13%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

  }

  ngOnInit(): void {
    this.logger.debug(EvaluacionListadoComponent.name, 'ngOnInit()', 'start');

    this.buscadorFormGroup = new FormGroup({
      comite: new FormControl('', []),
      fechaEvaluacionInicio: new FormControl('', []),
      fechaEvaluacionFin: new FormControl('', []),
      referenciaMemoria: new FormControl('', []),
      tipoConvocatoriaReunion: new FormControl('', []),
      solicitante: new FormControl('', [])
    });

    this.getComites();

    this.getTipoConvocatoriasReunion();

    this.logger.debug(EvaluacionListadoComponent.name, 'ngOnInit()', 'end');
  }

  ngAfterViewInit(): void {
    this.logger.debug(EvaluacionListadoComponent.name, 'ngAfterViewInit()', 'start');

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

    this.logger.debug(EvaluacionListadoComponent.name, 'ngAfterViewInit()', 'end');
  }

  private loadTable(reset?: boolean) {
    this.logger.debug(EvaluacionListadoComponent.name, 'loadTable()', 'start');

    // Do the request with paginator/sort/filter values
    this.evaluaciones$ = this.evaluacionesService.findAllByMemoriaAndRetrospectivaEnEvaluacion({
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
        switchMap((response) => {
          // Map response total
          this.totalElementos = response.total;
          // Reset pagination to first page
          if (reset) {
            this.paginator.pageIndex = 0;
          }

          if (response.items) {
            // Solicitantes
            const listObservables: Observable<IEvaluacionSolicitante>[] = [];
            response.items.forEach((evaluacion) => {
              const evaluacion$ = this.personaFisicaService.getInformacionBasica(evaluacion.memoria?.peticionEvaluacion?.personaRef).pipe(
                map((personaInfo) => {
                  evaluacion.persona = personaInfo;

                  return evaluacion;
                })
              );
              listObservables.push(evaluacion$);
            });

            this.logger.debug(EvaluacionListadoComponent.name, 'loadTable()', 'end');
            return zip(...listObservables);
          } else {
            return of([]);
          }

          // Return the values
          // return response.items;
        }),
      ),
      catchError(() => {
        // On error reset pagination values
        this.paginator.firstPage();
        this.totalElementos = 0;
        this.snackBarService.showError(MSG_ERROR);
        this.logger.debug(EvaluacionListadoComponent.name, 'loadTable()', 'end');
        return of([]);
      });

  }


  private buildFilters(): SgiRestFilter[] {
    this.logger.debug(EvaluacionListadoComponent.name, 'buildFilters()', 'start');

    this.filter = [];
    if (this.buscadorFormGroup.controls.comite.value) {
      this.logger.debug(EvaluacionListadoComponent.name, 'buildFilters()', 'comite');
      const filterComite: SgiRestFilter = {
        field: 'convocatoriaReunion.comite.id',
        type: SgiRestFilterType.EQUALS,
        value: this.buscadorFormGroup.controls.comite.value.id,
      };

      this.filter.push(filterComite);

    }
    if (this.buscadorFormGroup.controls.fechaEvaluacionInicio.value) {
      this.logger.debug(EvaluacionListadoComponent.name, 'buildFilters()', 'fechaEvaluacionInicio');
      const fechaFilter = DateUtils.getFechaInicioDia(this.buscadorFormGroup.controls.fechaEvaluacionInicio.value);
      const filterFechaEvaluacionInicio: SgiRestFilter = {
        field: 'fechaDictamen',
        type: SgiRestFilterType.GREATHER_OR_EQUAL,
        value: DateUtils.formatFechaAsISODate(fechaFilter),
      };

      this.filter.push(filterFechaEvaluacionInicio);

    }

    if (this.buscadorFormGroup.controls.fechaEvaluacionFin.value) {
      this.logger.debug(EvaluacionListadoComponent.name, 'buildFilters()', 'fechaEvaluacionFin');
      const fechaFilter = DateUtils.getFechaFinDia(this.buscadorFormGroup.controls.fechaEvaluacionFin.value);
      const filterFechaEvaluacionFin: SgiRestFilter = {
        field: 'fechaDictamen',
        type: SgiRestFilterType.LOWER_OR_EQUAL,
        value: DateUtils.formatFechaAsISODate(fechaFilter),
      };

      this.filter.push(filterFechaEvaluacionFin);

    }

    if (this.buscadorFormGroup.controls.referenciaMemoria.value) {
      this.logger.debug(EvaluacionListadoComponent.name, 'buildFilters()', 'referenciaMemoria');
      const filterReferenciaMemoria: SgiRestFilter = {
        field: 'memoria.numReferencia',
        type: SgiRestFilterType.LIKE,
        value: this.buscadorFormGroup.controls.referenciaMemoria.value,
      };

      this.filter.push(filterReferenciaMemoria);

    }

    if (this.buscadorFormGroup.controls.tipoConvocatoriaReunion.value) {
      this.logger.debug(EvaluacionListadoComponent.name, 'buildFilters()', 'tipoConvocatoriaReunion');
      const filterTipoConvocatoriaReunion: SgiRestFilter = {
        field: 'convocatoriaReunion.tipoConvocatoriaReunion.id',
        type: SgiRestFilterType.EQUALS,
        value: this.buscadorFormGroup.controls.tipoConvocatoriaReunion.value.id,
      };

      this.filter.push(filterTipoConvocatoriaReunion);

    }

    if (this.personaRefSolicitante) {
      this.logger.debug(EvaluacionListadoComponent.name, 'buildFilters()', 'solicitante');
      const filterPersonaRef: SgiRestFilter = {
        field: 'memoria.peticionEvaluacion.personaRef',
        type: SgiRestFilterType.EQUALS,
        value: this.personaRefSolicitante,
      };
      this.filter.push(filterPersonaRef);
    }

    this.logger.debug(EvaluacionListadoComponent.name, 'buildFilters()', 'end');
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
   * Devuelve el nombre de un tipo convocatoria reunión.
   * @param convocatoria tipo convocatoria reunión.
   * returns nombre tipo convocatoria reunión.
   */
  getTipoConvocatoriaReunion(convocatoria: TipoConvocatoriaReunion): string {

    return convocatoria?.nombre;

  }

  /**
   * Recupera un listado de los comités que hay en el sistema.
   */
  getComites(): void {
    this.logger.debug(EvaluacionListadoComponent.name,
      'getComites()',
      'start');

    this.comitesSubscription = this.comiteService.findAll().subscribe(
      (response) => {
        this.comiteListado = response.items;

        this.filteredComites = this.buscadorFormGroup.controls.comite.valueChanges
          .pipe(
            startWith(''),
            map(value => this.filterComite(value))
          );
      });

    this.logger.debug(EvaluacionListadoComponent.name,
      'getComites()',
      'end');
  }

  /**
   * Recupera un listado de los tipos convocatoria que hay en el sistema.
   */
  getTipoConvocatoriasReunion(): void {
    this.logger.debug(EvaluacionListadoComponent.name,
      'getTipoConvocatoriasReunion()',
      'start');

    this.tipoConvocatoriasReunionSubscription = this.tipoConvocatoriaReunionService.findAll().subscribe(
      (response) => {
        this.tipoConvocatoriaReunionListado = response.items;

        this.filteredTipoConvocatoriaReunion = this.buscadorFormGroup.controls.tipoConvocatoriaReunion.valueChanges
          .pipe(
            startWith(''),
            map(value => this.filterTipoConvocatoriaReunion(value))
          );
      },
      () => {
        this.snackBarService.showError(MSG_ERROR_LOAD_TIPOS_CONVOCATORIA);
      }
    );

    this.logger.debug(EvaluacionListadoComponent.name,
      'getTipoConvocatoriasReunion()',
      'end');
  }


  /**
   * Filtro de campo autocompletable comité.
   * @param value value a filtrar (string o nombre comité).
   * @returns lista de comités filtrados.
   */
  private filterComite(value: string | Comite): Comite[] {
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
   * Filtro de campo autocompletable tipo convocatoria reunión.
   * @param value value a filtrar (string o nombre tipo convocatoria reunion).
   * @returns lista de tipos de convocatorias filtrados.
   */
  private filterTipoConvocatoriaReunion(value: string | TipoConvocatoriaReunion): TipoConvocatoriaReunion[] {
    let filterValue: string;
    if (typeof value === 'string') {
      filterValue = value.toLowerCase();
    } else {
      filterValue = value.nombre.toLowerCase();
    }

    return this.tipoConvocatoriaReunionListado.filter
      (tipoConvocatoriaReunion => tipoConvocatoriaReunion.nombre.toLowerCase().includes(filterValue));
  }

  /**
   * Load table data
   */
  public onSearch() {
    this.loadTable(true);
  }

  /**
   * Setea el persona seleccionado a través del componente
   * @param persona Persona seleccionada
   */
  public setPersona(persona: Persona) {
    this.personaRefSolicitante = persona.personaRef;
  }

  /**
   * Clean filters an reload the table
   */
  public onClearFilters() {

    this.logger.debug(EvaluacionListadoComponent.name,
      'onClearFilters()',
      'start');
    this.filter = [];
    this.buscadorFormGroup.reset();
    this.personaRefSolicitante = '';
    this.datosUsuarioSolicitante = '';

    this.loadTable(true);

    this.logger.debug(EvaluacionListadoComponent.name,
      'onClearFilters()',
      'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(EvaluacionListadoComponent.name,
      'ngOnDestroy()',
      'start');
    this.comitesSubscription?.unsubscribe();
    this.tipoConvocatoriasReunionSubscription?.unsubscribe();

    this.logger.debug(EvaluacionListadoComponent.name,
      'ngOnDestroy()',
      'end');

  }

}
