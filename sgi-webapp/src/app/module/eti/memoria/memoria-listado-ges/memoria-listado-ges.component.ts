import { Component, OnInit, ViewChild, AfterViewInit, OnDestroy } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { FormGroup, FormControl } from '@angular/forms';
import { Observable, of, merge, Subscription } from 'rxjs';
import { NGXLogger } from 'ngx-logger';
import { SgiRestFilter, SgiRestFilterType, SgiRestSortDirection } from '@sgi/framework/http';
import { tap, map, catchError, startWith } from 'rxjs/operators';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { IMemoriaPeticionEvaluacion } from '@core/models/eti/memoriaPeticionEvaluacion';
import { Comite } from '@core/models/eti/comite';
import { IPersona } from '@core/models/sgp/persona';
import { DialogService } from '@core/services/dialog.service';
import { ComiteService } from '@core/services/eti/comite.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { ROUTE_NAMES } from '@core/route.names';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { TipoEstadoMemoria } from '@core/models/eti/tipo-estado-memoria';
import { TipoEstadoMemoriaService } from '@core/services/eti/tipo-estado-memoria.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { MEMORIAS_ROUTE } from '../memoria-route-names';
import { IMemoria } from '@core/models/eti/memoria';

const MSG_BUTTON_SAVE = marker('footer.eti.peticionEvaluacion.crear');
const TEXT_USER_TITLE = marker('eti.peticionEvaluacion.listado.buscador.solicitante');
const TEXT_USER_BUTTON = marker('eti.peticionEvaluacion.listado.buscador.buscar.solicitante');
const MSG_ESTADO_ANTERIOR_OK = marker('eti.memoria.listado.volverEstadoAnterior.ok');
const MSG_ESTADO_ANTERIOR_ERROR = marker('eti.memoria.listado.volverEstadoAnterior.error');
const MSG_RECUPERAR_ESTADO = marker('eti.memoria.listado.volverEstadoAnterior.confirmacion');

@Component({
  selector: 'sgi-memoria-listado-ges',
  templateUrl: './memoria-listado-ges.component.html',
  styleUrls: ['./memoria-listado-ges.component.scss']
})
export class MemoriaListadoGesComponent implements AfterViewInit, OnInit, OnDestroy {
  MEMORIAS_ROUTE = MEMORIAS_ROUTE;
  ROUTE_NAMES = ROUTE_NAMES;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns: string[];
  elementosPagina: number[];
  totalElementos: number;
  filter: SgiRestFilter[];

  textoCrear = MSG_BUTTON_SAVE;

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;


  memorias$: Observable<IMemoriaPeticionEvaluacion[]>;

  comiteListado: Comite[];
  filteredComites: Observable<Comite[]>;

  estadoMemoriaListado: TipoEstadoMemoria[];
  filteredEstadosMemoria: Observable<TipoEstadoMemoria[]>;

  buscadorFormGroup: FormGroup;

  private suscripciones: Subscription[];

  textoUsuarioLabel = TEXT_USER_TITLE;
  textoUsuarioInput = TEXT_USER_TITLE;
  textoUsuarioButton = TEXT_USER_BUTTON;
  personaRef: string;
  datosSolicitante: string;

  constructor(
    private readonly logger: NGXLogger,
    private readonly snackBarService: SnackBarService,
    private readonly comiteService: ComiteService,
    private readonly tipoEstadoMemoriaService: TipoEstadoMemoriaService,
    private readonly dialogService: DialogService,
    private readonly memoriaService: MemoriaService
  ) {
    this.displayedColumns = ['num_referencia', 'comite', 'estado', 'fechaEvaluacion', 'fechaLimite', 'acciones'];
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
    this.logger.debug(MemoriaListadoGesComponent.name, 'ngOnInit()', 'start');

    this.buscadorFormGroup = new FormGroup({
      comite: new FormControl('', []),
      titulo: new FormControl('', []),
      numReferencia: new FormControl('', []),
      tipoEstadoMemoria: new FormControl('', [])
    });

    this.loadComites();
    this.loadEstadosMemoria();
    this.logger.debug(MemoriaListadoGesComponent.name, 'ngOnInit()', 'end');
  }

  ngAfterViewInit(): void {
    this.logger.debug(MemoriaListadoGesComponent.name, 'ngAfterViewInit()', 'start');

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

    this.logger.debug(MemoriaListadoGesComponent.name, 'ngAfterViewInit()', 'end');
  }

  private async loadTable(reset?: boolean) {
    this.logger.debug(MemoriaListadoGesComponent.name, 'loadTable()', 'start');
    // Do the request with paginator/sort/filter values

    this.memorias$ = this.memoriaService
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
      }).pipe(
        map((response) => {
          // Return the values
          return response.items;
        }),
        catchError(() => {
          return of([]);
        })
      );

  }

  private buildFilters(): SgiRestFilter[] {
    this.logger.debug(MemoriaListadoGesComponent.name, 'buildFilters()', 'start');
    this.filter = [];

    const comite = FormGroupUtil.getValue(this.buscadorFormGroup, 'comite');
    if (comite) {
      const filterComite = {
        field: 'comite.id',
        type: SgiRestFilterType.EQUALS,
        value: comite.id,
      };

      this.filter.push(filterComite);
    }

    const titulo = FormGroupUtil.getValue(this.buscadorFormGroup, 'titulo');
    if (titulo) {
      const filterTitulo = {
        field: 'peticionEvaluacion.titulo',
        type: SgiRestFilterType.LIKE,
        value: titulo,
      };

      this.filter.push(filterTitulo);
    }

    const numReferencia = FormGroupUtil.getValue(this.buscadorFormGroup, 'numReferencia');
    if (numReferencia) {
      const filterRef = {
        field: 'numReferencia',
        type: SgiRestFilterType.EQUALS,
        value: numReferencia,
      };

      this.filter.push(filterRef);
    }

    const tipoEstadoMemoria = FormGroupUtil.getValue(this.buscadorFormGroup, 'tipoEstadoMemoria');
    if (tipoEstadoMemoria) {
      const filterTipoEstadoMemoria = {
        field: 'estadoActual.id',
        type: SgiRestFilterType.EQUALS,
        value: tipoEstadoMemoria.id,
      };

      this.filter.push(filterTipoEstadoMemoria);
    }

    if (this.personaRef) {
      const filterPersona = {
        field: 'personaRef',
        type: SgiRestFilterType.EQUALS,
        value: this.personaRef,
      };

      this.filter.push(filterPersona);
    }

    this.logger.debug(MemoriaListadoGesComponent.name, 'buildFilters()', 'end');

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
  loadComites(): void {
    this.logger.debug(MemoriaListadoGesComponent.name,
      'getComites()',
      'start');

    const comitesSubscription = this.comiteService.findAll().subscribe(
      (response) => {
        this.comiteListado = response.items;

        this.filteredComites = this.buscadorFormGroup.controls.comite.valueChanges
          .pipe(
            startWith(''),
            map(value => this.filterComite(value))
          );
      });

    this.suscripciones.push(comitesSubscription);

    this.logger.debug(MemoriaListadoGesComponent.name,
      'getComites()',
      'end');
  }

  /**
   * Recupera un listado de los estados memoria que hay en el sistema.
   */
  loadEstadosMemoria(): void {
    this.logger.debug(MemoriaListadoGesComponent.name,
      'getEstadosMemoria()',
      'start');

    const estadosMemoriaSubscription = this.tipoEstadoMemoriaService.findAll().subscribe(
      (response) => {
        this.estadoMemoriaListado = response.items;

        this.filteredEstadosMemoria = this.buscadorFormGroup.controls.tipoEstadoMemoria.valueChanges
          .pipe(
            startWith(''),
            map(value => this.filterEstadoMemoria(value))
          );
      });

    this.suscripciones.push(estadosMemoriaSubscription);

    this.logger.debug(MemoriaListadoGesComponent.name,
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
  public setUsuario(solicitante: IPersona) {
    this.personaRef = solicitante?.personaRef;
  }

  /**
   * Load table data
   */
  public onSearch() {
    this.loadTable(true);
  }

  /**
   * Se recupera el estado anterior de la memoria
   * @param memoria la memoria a reestablecer el estado
   */
  private recuperarEstadoAnteriorMemoria(memoria: IMemoriaPeticionEvaluacion) {
    const recuperarEstadoSubscription = this.memoriaService.recuperarEstadoAnterior(memoria.id).pipe(
      map((response: IMemoria) => {
        if (response) {
          // Si todo ha salido bien se recarga la tabla con el cambio de estado actualizado y el aviso correspondiente
          this.snackBarService.showSuccess(MSG_ESTADO_ANTERIOR_OK);
          this.loadTable(true);
        } else {
          // Si no se puede cambiar de estado se muestra el aviso
          this.snackBarService.showError(MSG_ESTADO_ANTERIOR_ERROR);
        }
      })
    ).subscribe();

    this.suscripciones.push(recuperarEstadoSubscription);
  }

  /**
   * Confirmación para recuperar el estado de la memoria
   * @param memoria la memoria a reestablecer el estado
   */
  public recuperarEstadoAnterior(memoria: IMemoriaPeticionEvaluacion) {
    const dialogServiceSubscription = this.dialogService.showConfirmation(MSG_RECUPERAR_ESTADO).pipe(
      map((aceptado: boolean) => {
        if (aceptado) {
          this.recuperarEstadoAnteriorMemoria(memoria);
        }
        this.logger.debug(MemoriaListadoGesComponent.name,
          `${this.recuperarEstadoAnterior.name}(${memoria})`, 'end');
      })
    ).subscribe();

    this.suscripciones.push(dialogServiceSubscription);
  }

  /**
   * Clean filters an reload the table
   */
  public onClearFilters() {

    this.logger.debug(MemoriaListadoGesComponent.name,
      'onClearFilters()',
      'start');
    this.filter = [];
    this.buscadorFormGroup.reset();
    this.personaRef = null;
    this.loadTable(true);
    this.loadComites();
    this.loadEstadosMemoria();
    this.datosSolicitante = '';
    this.logger.debug(MemoriaListadoGesComponent.name,
      'onClearFilters()',
      'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(MemoriaListadoGesComponent.name,
      'ngOnDestroy()',
      'start');
    this.suscripciones.forEach(x => x.unsubscribe());
    this.logger.debug(MemoriaListadoGesComponent.name,
      'ngOnDestroy()',
      'end');

  }

}
