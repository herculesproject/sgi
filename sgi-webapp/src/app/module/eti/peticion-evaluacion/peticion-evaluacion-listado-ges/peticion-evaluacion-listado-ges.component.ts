import { Component, OnInit, ViewChild, AfterViewInit, OnDestroy } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { FormGroup, FormControl } from '@angular/forms';
import { Observable, of, merge, Subscription, zip } from 'rxjs';
import { NGXLogger } from 'ngx-logger';

import { SgiRestFilter, SgiRestFilterType, SgiRestSortDirection } from '@sgi/framework/http';
import { SgiAuthService } from '@sgi/framework/auth';

import { tap, map, catchError, startWith, switchMap } from 'rxjs/operators';

import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';

import { PeticionEvaluacion } from '@core/models/eti/peticion-evaluacion';
import { Comite } from '@core/models/eti/comite';
import { Persona } from '@core/models/sgp/persona';
import { DialogService } from '@core/services/dialog.service';
import { ComiteService } from '@core/services/eti/comite.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';

import { DateUtils } from '@core/utils/date-utils';
import { ROUTE_NAMES } from '@core/route.names';

import { PeticionEvaluacionService } from '@core/services/eti/peticion-evaluacion.service';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { Memoria } from '@core/models/eti/memoria';
import { TipoEstadoMemoria } from '@core/models/eti/tipo-estado-memoria';
import { TipoEstadoMemoriaService } from '@core/services/eti/tipo-estado-memoria.service';


const MSG_BUTTON_SAVE = marker('footer.eti.peticion-evaluacion.crear');
const TEXT_USER_TITLE = marker('eti.peticionEvaluacion.listado.buscador.solicitante');
const TEXT_USER_BUTTON = marker('eti.peticionEvaluacion.listado.buscador.buscar.solicitante');
const MSG_FOOTER = marker('eti.peticionEvaluacion.listado.nuevaPeticionEvaluacion');

@Component({
  selector: 'sgi-peticion-evaluacion-listado-ges',
  templateUrl: './peticion-evaluacion-listado-ges.component.html',
  styleUrls: ['./peticion-evaluacion-listado-ges.component.scss']
})
export class PeticionEvaluacionListadoGesComponent implements AfterViewInit, OnInit, OnDestroy {

  ROUTE_NAMES = ROUTE_NAMES;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns: string[];
  elementosPagina: number[];
  totalElementos: number;
  filter: SgiRestFilter[];

  textoCrear = MSG_FOOTER;

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;

  peticionesEvaluacion$: Observable<PeticionEvaluacion[]> = of();
  memorias$: Observable<Memoria[]> = of();

  comiteListado: Comite[];
  comitesSubscription: Subscription;
  filteredComites: Observable<Comite[]>;

  estadoMemoriaListado: TipoEstadoMemoria[];
  estadosMemoriaSubscription: Subscription;
  filteredEstadosMemoria: Observable<TipoEstadoMemoria[]>;

  buscadorFormGroup: FormGroup;

  dialogServiceSubscription: Subscription;
  dialogServiceSubscriptionGetSubscription: Subscription;
  peticionEvaluacionServiceDeleteSubscription: Subscription;
  memoriaServiceSubscription: Subscription;
  personaServiceOneSubscritpion: Subscription;

  textoUsuarioLabel = TEXT_USER_TITLE;
  textoUsuarioInput = TEXT_USER_TITLE;
  textoUsuarioButton = TEXT_USER_BUTTON;
  personaRef: string;
  datosSolicitante: string;

  constructor(
    private readonly logger: NGXLogger,
    private readonly peticionesEvaluacionService: PeticionEvaluacionService,
    private readonly snackBarService: SnackBarService,
    private readonly comiteService: ComiteService,
    private readonly tipoEstadoMemoriaService: TipoEstadoMemoriaService,
    private readonly dialogService: DialogService,
    private readonly sgiAuthService: SgiAuthService,
    private readonly memoriaService: MemoriaService,
    private readonly personaFisicaService: PersonaFisicaService
  ) {
    this.displayedColumns = ['solicitante', 'codigo', 'titulo', 'fuenteFinanciacion', 'fechaInicio', 'fechaFin', 'acciones'];
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
    this.logger.debug(PeticionEvaluacionListadoGesComponent.name, 'ngOnInit()', 'start');

    this.buscadorFormGroup = new FormGroup({
      comite: new FormControl('', []),
      titulo: new FormControl('', []),
      codigo: new FormControl('', []),
      tipoEstadoMemoria: new FormControl('', [])
    });

    this.getComites();
    this.getEstadosMemoria();
    this.logger.debug(PeticionEvaluacionListadoGesComponent.name, 'ngOnInit()', 'end');
  }

  ngAfterViewInit(): void {
    this.logger.debug(PeticionEvaluacionListadoGesComponent.name, 'ngAfterViewInit()', 'start');

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

    this.logger.debug(PeticionEvaluacionListadoGesComponent.name, 'ngAfterViewInit()', 'end');
  }

  private async loadTable(reset?: boolean) {
    this.logger.debug(PeticionEvaluacionListadoGesComponent.name, 'loadTable()', 'start');
    // Do the request with paginator/sort/filter values
    this.peticionesEvaluacion$ = this.peticionesEvaluacionService
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
          this.logger.debug(PeticionEvaluacionListadoGesComponent.name, 'loadTable()', 'end');
          let peticionesEvaluacionByFilters: PeticionEvaluacion[] = new Array();
          peticionesEvaluacionByFilters = response.items;
          if (this.buscadorFormGroup.controls.comite.value || this.buscadorFormGroup.controls.tipoEstadoMemoria.value) {
            peticionesEvaluacionByFilters = this.filterPeticionEvaluacionByFilters(
              peticionesEvaluacionByFilters);
          }

          // Return the values
          return this.loadDatosUsuario(peticionesEvaluacionByFilters);
        }),
        catchError(() => {
          // On error reset pagination values
          this.paginator.firstPage();
          this.totalElementos = 0;
          this.snackBarService.showError('eti.peticionEvaluacion.listado.error');
          this.logger.debug(PeticionEvaluacionListadoGesComponent.name, 'loadTableCreador()', 'end');
          return of([]);
        })
      );
  }

  loadDatosUsuario(peticionesEvaluacion: PeticionEvaluacion[]): PeticionEvaluacion[] {

    peticionesEvaluacion.forEach((peticionEvaluacion) => {
      this.personaServiceOneSubscritpion = this.personaFisicaService.getInformacionBasica(peticionEvaluacion.personaRef)
        .subscribe(
          (persona: Persona) => {
            peticionEvaluacion.nombre = persona.nombre;
            peticionEvaluacion.primerApellido = persona.primerApellido;
            peticionEvaluacion.segundoApellido = persona.segundoApellido;
            peticionEvaluacion.identificadorNumero = persona.identificadorNumero;
            peticionEvaluacion.identificadorLetra = persona.identificadorLetra;
            if (this.personaRef) {
              this.datosSolicitante = persona.nombre + ' ' + persona.primerApellido + ' ' +
                persona.segundoApellido + '(' + persona.identificadorNumero + persona.identificadorLetra + ')';
            }
          },
          () => {
            this.snackBarService.showError('eti.peticionEvaluacion.listado.error');
            this.logger.debug(
              PeticionEvaluacionListadoGesComponent.name,
              'loadDatosUsuario()',
              'end'
            );
          }
        );
    });

    return peticionesEvaluacion;

  }

  private filterPeticionEvaluacionByFilters(peticionesEvaluacion: PeticionEvaluacion[]): PeticionEvaluacion[] {
    const peticionesEvaluacionByComiteExists: PeticionEvaluacion[] = new Array();
    this.peticionesEvaluacion$ = of();
    peticionesEvaluacion.forEach((peticionEvaluacion, i) => {
      this.memoriaServiceSubscription = this.memoriaService
        .findAll({
          filters: this.buildFiltersMemoria(peticionEvaluacion.id)
        })
        .subscribe(
          (response) => {
            const memorias: Memoria[] = response.items;
            if (memorias && memorias.length > 0) {
              if (peticionesEvaluacionByComiteExists.indexOf(peticionEvaluacion) === -1) {
                peticionesEvaluacionByComiteExists.push(peticionEvaluacion);
              }
            }
            if (i === (peticionesEvaluacion.length - 1)) {
              this.peticionesEvaluacion$ = of(peticionesEvaluacionByComiteExists);
            }
          },
          () => {
            this.snackBarService.showError('eti.peticionEvaluacion.listado.error');
            this.logger.debug(
              PeticionEvaluacionListadoGesComponent.name,
              'filterPeticionEvaluacionByComite()',
              'end'
            );
          }
        );
    });
    return peticionesEvaluacionByComiteExists;
  }

  private buildFiltersMemoria(idPeticionEvaluacion?: number): SgiRestFilter[] {
    this.logger.debug(PeticionEvaluacionListadoGesComponent.name, 'buildFiltersMemoria()', 'start');

    this.filter = [];
    if (this.buscadorFormGroup.controls.comite.value) {
      this.logger.debug(PeticionEvaluacionListadoGesComponent.name, 'buildFiltersMemoria()', 'comite');
      const filterComite: SgiRestFilter = {
        field: 'comite.id',
        type: SgiRestFilterType.EQUALS,
        value: this.buscadorFormGroup.controls.comite.value.id,
      };

      this.filter.push(filterComite);

    }

    if (this.buscadorFormGroup.controls.tipoEstadoMemoria.value) {
      this.logger.debug(PeticionEvaluacionListadoGesComponent.name, 'buildFiltersMemoria()', 'estadoMemoria');
      const filterEstadoMemoria: SgiRestFilter = {
        field: 'estadoActual.id',
        type: SgiRestFilterType.EQUALS,
        value: this.buscadorFormGroup.controls.tipoEstadoMemoria.value.id,
      };

      this.filter.push(filterEstadoMemoria);

    }

    if (this.buscadorFormGroup.controls.codigo.value) {
      this.logger.debug(PeticionEvaluacionListadoGesComponent.name, 'buildFilters()', 'codigo');
      const filterCodigo: SgiRestFilter = {
        field: 'peticionEvaluacion.codigo',
        type: SgiRestFilterType.LIKE,
        value: this.buscadorFormGroup.controls.codigo.value,
      };

      this.filter.push(filterCodigo);

    }

    if (this.buscadorFormGroup.controls.titulo.value) {
      this.logger.debug(PeticionEvaluacionListadoGesComponent.name, 'buildFilters()', 'titulo');
      const filterTitulo: SgiRestFilter = {
        field: 'peticionEvaluacion.titulo',
        type: SgiRestFilterType.LIKE,
        value: this.buscadorFormGroup.controls.titulo.value,
      };

      this.filter.push(filterTitulo);

    }

    if (idPeticionEvaluacion) {
      const filterPeticionEvaluacion: SgiRestFilter = {
        field: 'peticionEvaluacion.id',
        type: SgiRestFilterType.EQUALS,
        value: idPeticionEvaluacion.toString(),
      };
      this.filter.push(filterPeticionEvaluacion);
    }

    return this.filter;
  }

  private buildFilters(): SgiRestFilter[] {
    this.logger.debug(PeticionEvaluacionListadoGesComponent.name, 'buildFilters()', 'start');
    this.filter = [];
    if (this.buscadorFormGroup.controls.codigo.value) {
      this.logger.debug(PeticionEvaluacionListadoGesComponent.name, 'buildFilters()', 'codigo');
      const filterCodigo: SgiRestFilter = {
        field: 'codigo',
        type: SgiRestFilterType.LIKE,
        value: this.buscadorFormGroup.controls.codigo.value,
      };

      this.filter.push(filterCodigo);

    }

    if (this.buscadorFormGroup.controls.titulo.value) {
      this.logger.debug(PeticionEvaluacionListadoGesComponent.name, 'buildFilters()', 'titulo');
      const filterTitulo: SgiRestFilter = {
        field: 'titulo',
        type: SgiRestFilterType.LIKE,
        value: this.buscadorFormGroup.controls.titulo.value,
      };

      this.filter.push(filterTitulo);

    }

    if (this.personaRef) {
      this.logger.debug(PeticionEvaluacionListadoGesComponent.name, 'buildFilters()', 'titulo');
      const filterPersonaRef: SgiRestFilter = {
        field: 'personaRef',
        type: SgiRestFilterType.EQUALS,
        value: this.personaRef,
      };

      this.filter.push(filterPersonaRef);

    }

    this.logger.debug(PeticionEvaluacionListadoGesComponent.name, 'buildFilters()', 'end');
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
   * Devuelve el nombre de un estado memoria.
   * @param tipoEstadoMemoria tipo estado memoria
   * returns nombre estadoMemoria
   */
  getEstadoMemoria(tipoEstadoMemoria: TipoEstadoMemoria): string {

    return tipoEstadoMemoria?.nombre;

  }

  /**
   * Recupera un listado de los comités que hay en el sistema.
   */
  getComites(): void {
    this.logger.debug(PeticionEvaluacionListadoGesComponent.name,
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

    this.logger.debug(PeticionEvaluacionListadoGesComponent.name,
      'getComites()',
      'end');
  }

  /**
   * Recupera un listado de los estados memoria que hay en el sistema.
   */
  getEstadosMemoria(): void {
    this.logger.debug(PeticionEvaluacionListadoGesComponent.name,
      'getEstadosMemoria()',
      'start');

    this.estadosMemoriaSubscription = this.tipoEstadoMemoriaService.findAll().subscribe(
      (response) => {
        this.estadoMemoriaListado = response.items;

        this.filteredEstadosMemoria = this.buscadorFormGroup.controls.tipoEstadoMemoria.valueChanges
          .pipe(
            startWith(''),
            map(value => this.filterEstadoMemoria(value))
          );
      });

    this.logger.debug(PeticionEvaluacionListadoGesComponent.name,
      'getEstadosMemoria()',
      'end');
  }


  /**
   * Filtro de campo autocompletable comité.
   * @param value value a filtrar (string o nombre comité).
   * @returns lista de comités filtrados.
   */
  private filterComite(value: string | Comite): Comite[] {
    let filterValue: string;
    if (value === null) {
      value = '';
    }
    if (typeof value === 'string') {
      filterValue = value.toLowerCase();
    } else {
      filterValue = value.comite.toLowerCase();
    }

    return this.comiteListado.filter
      (comite => comite.comite.toLowerCase().includes(filterValue));
  }

  /**
   * Filtro de campo autocompletable estado memoria.
   * @param value value a filtrar (string o nombre estado memoria).
   * @returns lista de estados memoria filtrados.
   */
  private filterEstadoMemoria(value: string | TipoEstadoMemoria): TipoEstadoMemoria[] {
    let filterValue: string;
    if (value === null) {
      value = '';
    }
    if (typeof value === 'string') {
      filterValue = value.toLowerCase();
    } else {
      filterValue = value.nombre.toLowerCase();
    }

    return this.estadoMemoriaListado.filter
      (estadoMemoria => estadoMemoria.nombre.toLowerCase().includes(filterValue));
  }

  /**
   * Setea el persona seleccionado a través del componente
   * @param personaRef referencia del persona seleccionado
   */
  public setUsuario(solicitante: Persona) {
    this.personaRef = solicitante?.personaRef;
  }

  /**
   * Load table data
   */
  public onSearch() {
    this.loadTable(true);
  }

  /**
   * Elimina la peticion de evaluación con el id recibido por parametro.
   * @param peticionEvaluacionId id de la petición de evaluación
   * @param event evento lanzado
   */
  borrar(peticionEvaluacionId: number, $event: Event): void {
    this.logger.debug(PeticionEvaluacionListadoGesComponent.name,
      'borrar(peticionEvaluacionId: number, $event: Event) - start');

    $event.stopPropagation();
    $event.preventDefault();

    this.dialogServiceSubscriptionGetSubscription = this.dialogService.showConfirmation(
      'eti.peticionEvaluacion.listado.eliminar'
    ).subscribe(
      (aceptado: boolean) => {
        if (aceptado) {
          this.peticionEvaluacionServiceDeleteSubscription = this.peticionesEvaluacionService
            .deleteById(peticionEvaluacionId)
            .pipe(
              map(() => {
                return this.loadTable();
              })
            ).subscribe(() => {
              this.snackBarService.showSuccess('peticionEvaluacion.listado.eliminarConfirmado');
            });
        }
        aceptado = false;
      });

    this.logger.debug(PeticionEvaluacionListadoGesComponent.name,
      'borrar(peticionEvaluacionId: number, $event: Event) - end');
  }

  /**
   * Clean filters an reload the table
   */
  public onClearFilters() {

    this.logger.debug(PeticionEvaluacionListadoGesComponent.name,
      'onClearFilters()',
      'start');
    this.filter = [];
    this.buscadorFormGroup.reset();
    this.personaRef = null;
    this.loadTable(true);
    this.getComites();
    this.getEstadosMemoria();
    this.datosSolicitante = '';
    this.logger.debug(PeticionEvaluacionListadoGesComponent.name,
      'onClearFilters()',
      'end');
  }


  ngOnDestroy(): void {
    this.logger.debug(PeticionEvaluacionListadoGesComponent.name,
      'ngOnDestroy()',
      'start');
    this.comitesSubscription?.unsubscribe();

    this.logger.debug(PeticionEvaluacionListadoGesComponent.name,
      'ngOnDestroy()',
      'end');

  }

}
