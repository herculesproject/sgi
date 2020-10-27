import { Component, OnInit, ViewChild, AfterViewInit, OnDestroy } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { FormGroup, FormControl } from '@angular/forms';
import { Observable, of, merge, Subscription } from 'rxjs';
import { NGXLogger } from 'ngx-logger';
import { SgiRestFilter, SgiRestFilterType, SgiRestSortDirection } from '@sgi/framework/http';
import { tap, map, catchError, startWith, switchMap } from 'rxjs/operators';

import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { IMemoriaPeticionEvaluacion } from '@core/models/eti/memoriaPeticionEvaluacion';
import { IComite } from '@core/models/eti/comite';
import { IPersona } from '@core/models/sgp/persona';
import { DialogService } from '@core/services/dialog.service';
import { ComiteService } from '@core/services/eti/comite.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';
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
const MSG_SUCCESS_ENVIAR_SECRETARIA = marker('eti.memorias.formulario.memorias.listado.enviarSecretaria.correcto');
const MSG_ERROR_ENVIAR_SECRETARIA = marker('eti.memorias.formulario.memorias.listado.enviarSecretaria.error');
const MSG_CONFIRM_ENVIAR_SECRETARIA = marker('eti.memorias.formulario.memorias.listado.enviarSecretaria.confirmar');
const MSG_SUCCESS_ENVIAR_SECRETARIA_RETROSPECTIVA = marker(
  'eti.memorias.formulario.memorias.listado.enviarSecretariaRetrospectiva.correcto');
const MSG_ERROR_ENVIAR_SECRETARIA_RETROSPECTIVA = marker('eti.memorias.formulario.memorias.listado.enviarSecretariaRetrospectiva.error');
const MSG_CONFIRM_ENVIAR_SECRETARIA_RETROSPECTIVA = marker('eti.memorias.formulario.memorias.listado.enviarSecretariaRetrospectiva.confirmar');


@Component({
  selector: 'sgi-memoria-listado-inv',
  templateUrl: './memoria-listado-inv.component.html',
  styleUrls: ['./memoria-listado-inv.component.scss']
})
export class MemoriaListadoInvComponent implements AfterViewInit, OnInit, OnDestroy {
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

  comiteListado: IComite[];
  filteredComites: Observable<IComite[]>;

  estadoMemoriaListado: TipoEstadoMemoria[];
  filteredEstadosMemoria: Observable<TipoEstadoMemoria[]>;

  buscadorFormGroup: FormGroup;

  private subscriptions: Subscription[] = [];

  textoUsuarioLabel = TEXT_USER_TITLE;
  textoUsuarioInput = TEXT_USER_TITLE;
  textoUsuarioButton = TEXT_USER_BUTTON;
  personaRef: string;
  datosSolicitante: string;

  private suscripciones: Subscription[] = [];

  constructor(
    private readonly logger: NGXLogger,
    private readonly comiteService: ComiteService,
    private readonly tipoEstadoMemoriaService: TipoEstadoMemoriaService,
    private readonly memoriaService: MemoriaService,
    protected readonly snackBarService: SnackBarService,
    protected readonly dialogService: DialogService,
  ) {
    this.displayedColumns = ['numReferencia', 'comite', 'estadoActual', 'fechaEvaluacion', 'fechaLimite', 'acciones'];
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
    this.logger.debug(MemoriaListadoInvComponent.name, 'ngOnInit()', 'start');

    this.buscadorFormGroup = new FormGroup({
      comite: new FormControl('', []),
      titulo: new FormControl('', []),
      numReferencia: new FormControl('', []),
      tipoEstadoMemoria: new FormControl('', [])
    });

    this.loadComites();
    this.loadEstadosMemoria();
    this.logger.debug(MemoriaListadoInvComponent.name, 'ngOnInit()', 'end');
  }

  ngAfterViewInit(): void {
    this.logger.debug(MemoriaListadoInvComponent.name, 'ngAfterViewInit()', 'start');

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

    this.logger.debug(MemoriaListadoInvComponent.name, 'ngAfterViewInit()', 'end');
  }

  private async loadTable(reset?: boolean) {
    this.logger.debug(MemoriaListadoInvComponent.name, 'loadTable()', 'start');
    // Do the request with paginator/sort/filter values

    this.memorias$ = this.memoriaService
      .findAllByPersonaRefPeticionEvaluacion({
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
    this.logger.debug(MemoriaListadoInvComponent.name, 'buildFilters()', 'start');
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

    this.logger.debug(MemoriaListadoInvComponent.name, 'buildFilters()', 'end');

    return this.filter;
  }

  /**
   * Devuelve el nombre de un comité.
   * @param comite comités
   * returns nombre comité
   */
  getComite(comite: IComite): string {

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
    this.logger.debug(MemoriaListadoInvComponent.name,
      'getComites()',
      'start');

    this.subscriptions.push(this.comiteService.findAll().subscribe(
      (response) => {
        this.comiteListado = response.items;

        this.filteredComites = this.buscadorFormGroup.controls.comite.valueChanges
          .pipe(
            startWith(''),
            map(value => this.filterComite(value))
          );
      }));

    this.logger.debug(MemoriaListadoInvComponent.name,
      'getComites()',
      'end');
  }

  /**
   * Recupera un listado de los estados memoria que hay en el sistema.
   */
  loadEstadosMemoria(): void {
    this.logger.debug(MemoriaListadoInvComponent.name,
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

    this.logger.debug(MemoriaListadoInvComponent.name,
      'getEstadosMemoria()',
      'end');
  }


  /**
   * Filtro de campo autocompletable comité.
   * @param value value a filtrar (string o nombre comité).
   * @returns lista de comités filtrados.
   */
  private filterComite(value: string | IComite): IComite[] {
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
   * Clean filters an reload the table
   */
  public onClearFilters() {

    this.logger.debug(MemoriaListadoInvComponent.name,
      'onClearFilters()',
      'start');
    this.filter = [];
    this.buscadorFormGroup.reset();
    this.personaRef = null;
    this.loadTable(true);
    this.loadComites();
    this.loadEstadosMemoria();
    this.datosSolicitante = '';
    this.logger.debug(MemoriaListadoInvComponent.name,
      'onClearFilters()',
      'end');
  }

  hasPermisoEnviarSecretaria(estadoMemoriaId: number, responsable: boolean): boolean {

    // Si el estado es 'Completada', 'Favorable pendiente de modificaciones mínima',
    // 'Pendiente de correcciones', 'No procede evaluar', 'Completada seguimiento anual',
    // 'Completada seguimiento final' o 'En aclaracion seguimiento final' se muestra el botón de enviar.
    if ((estadoMemoriaId === 2 || estadoMemoriaId === 6 || estadoMemoriaId === 7
      || estadoMemoriaId === 8 || estadoMemoriaId === 11 || estadoMemoriaId === 16
      || estadoMemoriaId === 21) && !responsable) {
      return true;
    } else {
      return false;
    }

  }

  enviarSecretaria(memoria: IMemoriaPeticionEvaluacion) {
    this.logger.debug(MemoriaListadoInvComponent.name, 'enviarSecretaria(memoria: IMemoriaPeticionEvaluacion) - start');

    this.dialogService.showConfirmation(MSG_CONFIRM_ENVIAR_SECRETARIA)
      .pipe(switchMap((aceptado) => {
        if (aceptado) {
          return this.memoriaService.enviarSecretaria(memoria.id);
        }
        return of();
      })).subscribe(
        () => {
          this.loadTable();
          this.snackBarService.showSuccess(MSG_SUCCESS_ENVIAR_SECRETARIA);
        },
        () => {
          this.snackBarService.showError(MSG_ERROR_ENVIAR_SECRETARIA);
        }
      );

    this.logger.debug(MemoriaListadoInvComponent.name, 'enviarSecretaria(memoria: IMemoriaPeticionEvaluacion) - end');

  }

  hasPermisoEnviarSecretariaRetrospectiva(memoria: IMemoria, responsable: boolean): boolean {

    // Si el estado es 'Completada', es de tipo CEEA y requiere retrospectiva se muestra el botón de enviar.
    // Si la retrospectiva ya está 'En secretaría' no se muestra el botón.
    if (memoria.estadoActual.id === 2 && memoria.comite.comite === 'CEEA' && memoria.requiereRetrospectiva
      && memoria.retrospectiva.estadoRetrospectiva.id !== 3 && !responsable) {
      return true;
    } else {
      return false;
    }

  }

  enviarSecretariaRetrospectiva(memoria: IMemoriaPeticionEvaluacion) {
    this.logger.debug(MemoriaListadoInvComponent.name, 'enviarSecretariaRetrospectiva(memoria: IMemoriaPeticionEvaluacion) - start');

    this.dialogService.showConfirmation(MSG_CONFIRM_ENVIAR_SECRETARIA_RETROSPECTIVA)
      .pipe(switchMap((aceptado) => {
        if (aceptado) {
          return this.memoriaService.enviarSecretariaRetrospectiva(memoria.id);
        }
        return of();
      })).subscribe(
        () => {
          this.loadTable();
          this.snackBarService.showSuccess(MSG_SUCCESS_ENVIAR_SECRETARIA_RETROSPECTIVA);
        },
        () => {
          this.snackBarService.showError(MSG_ERROR_ENVIAR_SECRETARIA_RETROSPECTIVA);
        }
      );

    this.logger.debug(MemoriaListadoInvComponent.name, 'enviarSecretariaRetrospectiva(memoria: IMemoriaPeticionEvaluacion) - end');
  }

  ngOnDestroy(): void {
    this.logger.debug(MemoriaListadoInvComponent.name,
      'ngOnDestroy()',
      'start');
    this.subscriptions?.forEach(x => x.unsubscribe());
    this.logger.debug(MemoriaListadoInvComponent.name,
      'ngOnDestroy()',
      'end');

  }

}
