import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IComite } from '@core/models/eti/comite';
import { IMemoria } from '@core/models/eti/memoria';
import { IPeticionEvaluacion } from '@core/models/eti/peticion-evaluacion';
import { TipoEstadoMemoria } from '@core/models/eti/tipo-estado-memoria';
import { IPersona } from '@core/models/sgp/persona';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { ComiteService } from '@core/services/eti/comite.service';
import { PeticionEvaluacionService } from '@core/services/eti/peticion-evaluacion.service';
import { TipoEstadoMemoriaService } from '@core/services/eti/tipo-estado-memoria.service';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiRestFilter, SgiRestFilterType, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, startWith, catchError, switchMap } from 'rxjs/operators';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { BuscarPersonaComponent } from '@shared/buscar-persona/buscar-persona.component';


const MSG_BUTTON_SAVE = marker('footer.eti.peticionEvaluacion.crear');
const MSG_ERROR = marker('eti.peticionEvaluacion.listado.error');
const TEXT_USER_TITLE = marker('eti.peticionEvaluacion.listado.buscador.solicitante');
const TEXT_USER_BUTTON = marker('eti.peticionEvaluacion.listado.buscador.buscar.solicitante');

@Component({
  selector: 'sgi-peticion-evaluacion-listado-ges',
  templateUrl: './peticion-evaluacion-listado-ges.component.html',
  styleUrls: ['./peticion-evaluacion-listado-ges.component.scss']
})
export class PeticionEvaluacionListadoGesComponent extends AbstractTablePaginationComponent<IPeticionEvaluacion> implements OnInit {

  ROUTE_NAMES = ROUTE_NAMES;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns: string[];
  totalElementos: number;

  textoCrear = MSG_BUTTON_SAVE;

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;
  @ViewChild(BuscarPersonaComponent, { static: false }) private buscarPersona: BuscarPersonaComponent;

  peticionesEvaluacion$: Observable<IPeticionEvaluacion[]> = of();
  memorias$: Observable<IMemoria[]> = of();

  comiteListado: IComite[];
  filteredComites: Observable<IComite[]>;

  estadoMemoriaListado: TipoEstadoMemoria[];
  filteredEstadosMemoria: Observable<TipoEstadoMemoria[]>;

  textoUsuarioLabel = TEXT_USER_TITLE;
  textoUsuarioInput = TEXT_USER_TITLE;
  textoUsuarioButton = TEXT_USER_BUTTON;

  constructor(
    protected readonly logger: NGXLogger,
    private readonly peticionesEvaluacionService: PeticionEvaluacionService,
    protected readonly snackBarService: SnackBarService,
    private readonly comiteService: ComiteService,
    private readonly tipoEstadoMemoriaService: TipoEstadoMemoriaService,
    private readonly personaFisicaService: PersonaFisicaService
  ) {
    super(logger, snackBarService, MSG_ERROR);

    this.totalElementos = 0;

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

    super.ngOnInit();

    this.formGroup = new FormGroup({
      comite: new FormControl('', []),
      titulo: new FormControl('', []),
      codigo: new FormControl('', []),
      tipoEstadoMemoria: new FormControl('', []),
      solicitante: new FormControl('', [])
    });

    this.getComites();
    this.getEstadosMemoria();
    this.logger.debug(PeticionEvaluacionListadoGesComponent.name, 'ngOnInit()', 'end');
  }

  protected createObservable(): Observable<SgiRestListResult<IPeticionEvaluacion>> {
    this.logger.debug(PeticionEvaluacionListadoGesComponent.name, 'createObservable()', 'start');
    return this.peticionesEvaluacionService.findAll(this.getFindOptions()).pipe(
      map((response) => {
        // Return the values
        return response;
      }),
      switchMap((response) => {
        if (!response.items || response.items.length === 0) {
          this.logger.debug(PeticionEvaluacionListadoGesComponent.name, 'createObservable()', 'end');
          return of({} as SgiRestListResult<IPeticionEvaluacion>);
        }
        const personaRefsEvaluadores = new Set<string>();

        response.items.forEach((peticionEvaluacion: IPeticionEvaluacion) => {
          personaRefsEvaluadores.add(peticionEvaluacion?.personaRef);
        });

        const personaSubscription = this.personaFisicaService.findByPersonasRefs([...personaRefsEvaluadores]).subscribe((result) => {
          const personas = result.items;
          response.items.forEach((peticionEvaluacion: IPeticionEvaluacion) => {
            const datosPersona = personas.find((persona) =>
              peticionEvaluacion.personaRef === persona.personaRef);
            peticionEvaluacion.nombre = datosPersona?.nombre;
            peticionEvaluacion.primerApellido = datosPersona?.primerApellido;
            peticionEvaluacion.segundoApellido = datosPersona?.segundoApellido;
          });
        });
        this.suscripciones.push(personaSubscription);
        this.logger.debug(PeticionEvaluacionListadoGesComponent.name, 'createObservable()', 'end');
        let peticionesListado: SgiRestListResult<IPeticionEvaluacion>;
        return of(peticionesListado = {
          page: response.page,
          total: response.total,
          items: response.items
        });
      }),
      catchError(() => {
        this.snackBarService.showError(MSG_ERROR);
        this.logger.debug(
          PeticionEvaluacionListadoGesComponent.name,
          'createObservable()',
          'end'
        );
        return of({} as SgiRestListResult<IPeticionEvaluacion>);
      })
    );
  }


  protected initColumns(): void {
    this.logger.debug(PeticionEvaluacionListadoGesComponent.name, 'initColumns()', 'start');
    this.displayedColumns = ['solicitante', 'codigo', 'titulo', 'fuenteFinanciacion', 'fechaInicio', 'fechaFin', 'acciones'];
    this.logger.debug(PeticionEvaluacionListadoGesComponent.name, 'initColumns()', 'end');
  }

  protected createFilters(): SgiRestFilter[] {
    this.logger.debug(PeticionEvaluacionListadoGesComponent.name, 'createFilters()', 'start');

    const filtro: SgiRestFilter[] = [];

    this.addFiltro(filtro, 'peticionEvaluacion.codigo', SgiRestFilterType.LIKE, this.formGroup.controls.codigo.value);
    this.addFiltro(filtro, 'peticionEvaluacion.titulo', SgiRestFilterType.LIKE, this.formGroup.controls.titulo.value);
    this.addFiltro(filtro, 'comite.id', SgiRestFilterType.EQUALS, this.formGroup.controls.comite.value.id);
    this.addFiltro(filtro, 'estadoActual.id', SgiRestFilterType.EQUALS, this.formGroup.controls.tipoEstadoMemoria.value.id);
    this.addFiltro(filtro, 'peticionEvaluacion.personaRef', SgiRestFilterType.EQUALS, this.formGroup.controls.solicitante.value);

    this.logger.debug(PeticionEvaluacionListadoGesComponent.name, 'createFilters()', 'end');
    return filtro;
  }

  protected loadTable(reset?: boolean) {
    this.logger.debug(PeticionEvaluacionListadoGesComponent.name, 'loadTable()', 'start');
    this.peticionesEvaluacion$ = this.getObservableLoadTable(reset);
    this.logger.debug(PeticionEvaluacionListadoGesComponent.name, 'loadTable()', 'end');
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
  getComites(): void {
    this.logger.debug(PeticionEvaluacionListadoGesComponent.name,
      'getComites()',
      'start');

    const comitesSubscription = this.comiteService.findAll().subscribe(
      (response) => {
        this.comiteListado = response.items;

        this.filteredComites = this.formGroup.controls.comite.valueChanges
          .pipe(
            startWith(''),
            map(value => this.filterComite(value))
          );
      });

    this.suscripciones.push(comitesSubscription);

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

    const estadosMemoriaSubscription = this.tipoEstadoMemoriaService.findAll().subscribe(
      (response) => {
        this.estadoMemoriaListado = response.items;

        this.filteredEstadosMemoria = this.formGroup.controls.tipoEstadoMemoria.valueChanges
          .pipe(
            startWith(''),
            map(value => this.filterEstadoMemoria(value))
          );
      });

    this.suscripciones.push(estadosMemoriaSubscription);

    this.logger.debug(PeticionEvaluacionListadoGesComponent.name,
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
   * @param solicitante persona seleccionado
   */
  public setUsuario(solicitante: IPersona) {
    this.logger.debug(PeticionEvaluacionListadoGesComponent.name, 'setUsuario()', 'start');
    this.formGroup.controls.solicitante.setValue(solicitante.personaRef);
    this.logger.debug(PeticionEvaluacionListadoGesComponent.name, 'setUsuario()', 'end');
  }

  /**
   * Clean filters an reload the table
   */
  public onClearFilters() {

    this.logger.debug(PeticionEvaluacionListadoGesComponent.name, 'onClearFilters()', 'start');
    super.onClearFilters();
    this.buscarPersona.clear();
    this.logger.debug(PeticionEvaluacionListadoGesComponent.name, 'onClearFilters()', 'end');
  }
}
