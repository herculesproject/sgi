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

import { IEvaluador } from '@core/models/eti/evaluador';
import { Comite } from '@core/models/eti/comite';
import { Persona } from '@core/models/sgp/persona';
import { DialogService } from '@core/services/dialog.service';
import { ComiteService } from '@core/services/eti/comite.service';
import { SnackBarService } from '@core/services/snack-bar.service';

import { DateUtils } from '@core/utils/date-utils';
import { ROUTE_NAMES } from '@core/route.names';

import { EvaluadorService } from '@core/services/eti/evaluador.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';


const MSG_BUTTON_SAVE = marker('footer.eti.evaluador.crear');
const TEXT_USER_TITLE = marker('eti.buscarUsuario.titulo');
const TEXT_USER_BUTTON = marker('eti.buscarUsuario.boton.buscar');

@Component({
  selector: 'sgi-evaluador-listado',
  templateUrl: './evaluador-listado.component.html',
  styleUrls: ['./evaluador-listado.component.scss']
})
export class EvaluadorListadoComponent implements AfterViewInit, OnInit, OnDestroy {

  ROUTE_NAMES = ROUTE_NAMES;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns: string[];
  elementosPagina: number[];
  totalElementos: number;
  filter: SgiRestFilter[];

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;

  evaluadores$: Observable<IEvaluador[]> = of();

  comiteListado: Comite[];
  comitesSubscription: Subscription;
  filteredComites: Observable<Comite[]>;

  buscadorFormGroup: FormGroup;

  dialogServiceSubscription: Subscription;
  dialogServiceSubscriptionGetSubscription: Subscription;
  evaluadorServiceDeleteSubscription: Subscription;

  textoCrear = MSG_BUTTON_SAVE;
  textoUsuarioLabel = TEXT_USER_TITLE;
  textoUsuarioInput = TEXT_USER_TITLE;
  textoUsuarioButton = TEXT_USER_BUTTON;
  datosUsuarioEvaluador: string;
  personaRef: string;

  personaServiceOneSubscritpion: Subscription;

  personasRef: string[];

  personas$: Observable<Persona[]> = of();

  constructor(
    private readonly logger: NGXLogger,
    private readonly evaluadoresService: EvaluadorService,
    private readonly snackBarService: SnackBarService,
    private readonly comiteService: ComiteService,
    private readonly personaService: PersonaService,
    private readonly personaFisicaService: PersonaFisicaService,
    private readonly dialogService: DialogService
  ) {
    this.displayedColumns = ['nombre', 'identificadorNumero', 'comite', 'cargoComite', 'fechaAlta', 'fechaBaja', 'activo', 'acciones'];
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
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

  }

  ngOnInit(): void {
    this.logger.debug(EvaluadorListadoComponent.name, 'ngOnInit()', 'start');

    this.buscadorFormGroup = new FormGroup({
      comite: new FormControl('', []),
      estado: new FormControl('', [])
    });

    this.getComites();

    this.logger.debug(EvaluadorListadoComponent.name, 'ngOnInit()', 'end');
  }

  ngAfterViewInit(): void {
    this.logger.debug(EvaluadorListadoComponent.name, 'ngAfterViewInit()', 'start');

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

    this.logger.debug(EvaluadorListadoComponent.name, 'ngAfterViewInit()', 'end');
  }

  private loadTable(reset?: boolean) {
    this.logger.debug(EvaluadorListadoComponent.name, 'loadTable()', 'start');
    // Do the request with paginator/sort/filter values
    this.evaluadores$ = this.evaluadoresService
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
          this.logger.debug(EvaluadorListadoComponent.name, 'loadTable()', 'end');
          // Return the values
          return this.getDatosEvaluadores(response.items);
        }),
        catchError(() => {
          // On error reset pagination values
          this.paginator.firstPage();
          this.totalElementos = 0;
          this.snackBarService.showError('eti.evaluador.listado.error');
          this.logger.debug(EvaluadorListadoComponent.name, 'loadTable()', 'end');
          return of([]);
        })
      );
  }


  private buildFilters(): SgiRestFilter[] {
    this.logger.debug(EvaluadorListadoComponent.name, 'buildFilters()', 'start');

    this.filter = [];
    if (this.buscadorFormGroup.controls.comite.value) {
      this.logger.debug(EvaluadorListadoComponent.name, 'buildFilters()', 'comite');
      const filterComite: SgiRestFilter = {
        field: 'comite.id',
        type: SgiRestFilterType.EQUALS,
        value: this.buscadorFormGroup.controls.comite.value.id,
      };

      this.filter.push(filterComite);

    }

    if (this.buscadorFormGroup.controls.estado.value) {
      this.logger.debug(EvaluadorListadoComponent.name, 'buildFilters()', 'estado');
      const filterFechaBaja: SgiRestFilter = {
        field: 'fechaBaja',
        type: SgiRestFilterType.GREATHER_OR_EQUAL,
        value: DateUtils.formatFechaAsISODate(new Date()),
      };

      const filterFechaAlta: SgiRestFilter = {
        field: 'fechaAlta',
        type: SgiRestFilterType.LOWER_OR_EQUAL,
        value: DateUtils.formatFechaAsISODate(new Date()),
      };

      this.filter.push(filterFechaBaja);
      this.filter.push(filterFechaAlta);

    }

    if (this.personaRef) {
      const filterUsuarioRef: SgiRestFilter = {
        field: 'personaRef',
        type: SgiRestFilterType.EQUALS,
        value: this.personaRef,
      };
      this.filter.push(filterUsuarioRef);
    }

    this.logger.debug(EvaluadorListadoComponent.name, 'buildFilters()', 'end');
    return this.filter;
  }

  /**
   * Devuelve el nombre de un comité.
   * @param comite comités
   * returns nombre comité
   */
  getComite(comite: Comite): string {

    return comite?.comite;

  }

  /**
   * Devuelve los datos rellenos de evaluadores
   * @param evaluadores el listado de evaluadores
   * returns los evaluadores con todos sus datos
   */
  getDatosEvaluadores(evaluadores: IEvaluador[]): IEvaluador[] {
    this.personasRef = [];
    evaluadores.forEach((evaluador) => {

      if ((evaluador.fechaAlta != null && new Date(evaluador.fechaAlta) <= new Date()) &&
        ((evaluador.fechaBaja != null && new Date(evaluador.fechaBaja) >= new Date()) || (evaluador.fechaBaja == null))) {
        evaluador.activo = true;
      } else {
        evaluador.activo = false;
      }

      if (evaluador.personaRef && evaluador.personaRef !== '') {
        this.personasRef.push(evaluador.personaRef);
      }

      // cambiar en futuro pasando las referencias de las personas
      evaluador = this.loadDatosUsuario(evaluador);
    });
    return evaluadores;
  }

  private buildFilterPersonasRef(): SgiRestFilter[] {
    this.logger.debug(EvaluadorListadoComponent.name, 'buildFilterPersonasRef()', 'start');

    this.filter = [];
    if (this.personasRef) {
      const filterPersonaRef: SgiRestFilter = {
        field: 'personaRefs',
        type: SgiRestFilterType.EQUALS,
        value: this.personasRef.toString(),
      };

      this.filter.push(filterPersonaRef);
    }
    this.logger.debug(EvaluadorListadoComponent.name, 'buildFilterPersonasRef()', 'end');
    return this.filter;
  }

  /**
   * Devuelve los datos de persona del evaluador
   * @param evaluador el evaluador
   * returns el evaluador con los datos de persona
   */
  loadDatosUsuario(evaluador: IEvaluador): IEvaluador {
    this.personaServiceOneSubscritpion = this.personaFisicaService.getInformacionBasica(evaluador.personaRef)
      .subscribe(
        (persona: Persona) => {
          evaluador.nombre = persona.nombre;
          evaluador.primerApellido = persona.primerApellido;
          evaluador.segundoApellido = persona.segundoApellido;
          evaluador.identificadorNumero = persona.identificadorNumero;
          evaluador.identificadorLetra = persona.identificadorLetra;
        },
        () => {
          this.snackBarService.showError('eti.evaluador.actualizar.no-encontrado');
          this.logger.debug(
            EvaluadorListadoComponent.name,
            'loadDatosUsuario()',
            'end'
          );
        }
      );
    return evaluador;
  }

  /**
   * Devuelve si el estado es activo o inactivo.
   * @param estado estado activo/inactivo
   * returns activo o inactivo
   */
  getEstado(estado: boolean): boolean {

    return estado;
  }

  /**
   * Recupera un listado de los comités que hay en el sistema.
   */
  getComites(): void {
    this.logger.debug(EvaluadorListadoComponent.name,
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

    this.logger.debug(EvaluadorListadoComponent.name,
      'getComites()',
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
   * Load table data
   */
  public onSearch() {
    this.loadTable(true);
  }

  /**
   * Elimina el evaluador con el id recibido por parametro.
   * @param evaluadorId id evaluador
   * @param event evento lanzado
   */
  borrar(evaluadorId: number, $event: Event): void {
    this.logger.debug(EvaluadorListadoComponent.name,
      'borrar(evaluadorId: number, $event: Event) - start');

    $event.stopPropagation();
    $event.preventDefault();

    this.dialogServiceSubscriptionGetSubscription = this.dialogService.showConfirmation(
      'eti.evaluador.listado.eliminar'
    ).subscribe(
      (aceptado: boolean) => {
        if (aceptado) {
          this.evaluadorServiceDeleteSubscription = this.evaluadoresService
            .deleteById(evaluadorId)
            .pipe(
              map(() => {
                return this.loadTable();
              })
            ).subscribe(() => {
              this.snackBarService.showSuccess('evaluador.listado.eliminarConfirmado');
            });
        }
        aceptado = false;
      });

    this.logger.debug(EvaluadorListadoComponent.name,
      'borrar(evaluadorId: number, $event: Event) - end');
  }

  /**
   * Setea el persona seleccionado a través del componente
   * @param personaRef referencia del persona seleccionado
   */
  public setUsuario(personaRef: string) {
    this.personaRef = personaRef;
  }


  /**
   * Clean filters an reload the table
   */
  public onClearFilters() {

    this.logger.debug(EvaluadorListadoComponent.name,
      'onClearFilters()',
      'start');
    this.filter = [];
    this.buscadorFormGroup.reset();
    this.personaRef = '';
    this.datosUsuarioEvaluador = '';
    this.loadTable(true);

    this.logger.debug(EvaluadorListadoComponent.name,
      'onClearFilters()',
      'end');
  }


  ngOnDestroy(): void {
    this.logger.debug(EvaluadorListadoComponent.name,
      'ngOnDestroy()',
      'start');
    this.comitesSubscription?.unsubscribe();

    this.logger.debug(EvaluadorListadoComponent.name,
      'ngOnDestroy()',
      'end');

  }

}
