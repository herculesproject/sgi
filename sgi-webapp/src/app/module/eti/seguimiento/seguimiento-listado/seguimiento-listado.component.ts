import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractPaginacionComponent } from '@core/component/abstract-paginacion.component';
import { Comite } from '@core/models/eti/comite';
import { EvaluacionListado } from '@core/models/eti/dto/evaluacion-listado';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { Persona } from '@core/models/sgp/persona';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ComiteService } from '@core/services/eti/comite.service';
import { EvaluadorService } from '@core/services/eti/evaluador.service';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { DateUtils } from '@core/utils/date-utils';
import { SgiAuthService } from '@sgi/framework/auth';
import { SgiRestFilter, SgiRestFilterType, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';

const MSG_ERROR = marker('eti.seguimiento.listado.error');

@Component({
  selector: 'sgi-seguimiento-listado',
  templateUrl: './seguimiento-listado.component.html',
  styleUrls: ['./seguimiento-listado.component.scss']
})
export class SeguimientoListadoComponent extends AbstractPaginacionComponent<IEvaluacion> implements OnInit {
  evaluaciones: EvaluacionListado[];
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  comiteListado: Comite[];
  comitesFiltrados: Observable<Comite[]>;

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    private readonly sgiAuthService: SgiAuthService,
    private readonly personaFisicaService: PersonaFisicaService,
    private readonly comiteService: ComiteService,
    private readonly evaluadorService: EvaluadorService
  ) {
    super(logger, snackBarService, MSG_ERROR);
    this.logger.debug(SeguimientoListadoComponent.name, 'constructor()', 'start');
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
    this.logger.debug(SeguimientoListadoComponent.name, 'constructor()', 'end');
  }

  ngOnInit() {
    this.logger.debug(SeguimientoListadoComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();
    this.formGroup = new FormGroup({
      comite: new FormControl(''),
      fechaEvaluacionInicio: new FormControl(''),
      fechaEvaluacionFin: new FormControl(''),
      memoriaNumReferencia: new FormControl(''),
      tipoConvocatoria: new FormControl('')
    });
    this.loadComites();
    this.logger.debug(SeguimientoListadoComponent.name, 'ngOnInit()', 'end');
  }

  protected createObservable(): Observable<SgiRestListResult<IEvaluacion>> {
    this.logger.debug(SeguimientoListadoComponent.name, 'createObservable()', 'start');
    const userRefId = this.sgiAuthService.authStatus$.getValue().userRefId;
    const observable$ = this.evaluadorService.getSeguimientos(userRefId, this.getFindOptions());
    this.logger.debug(SeguimientoListadoComponent.name, 'createObservable()', 'end');
    return observable$;
  }

  protected initColumnas(): void {
    this.logger.debug(SeguimientoListadoComponent.name, 'initColumnas()', 'start');
    this.columnas = ['convocatoriaReunion.comite.id', 'convocatoriaReunion.fechaEvaluacion',
      'memoria.numReferencia', 'solicitante', 'version', 'acciones'];
    this.logger.debug(SeguimientoListadoComponent.name, 'initColumnas()', 'end');
  }

  protected loadTable(reset?: boolean): void {
    this.logger.debug(SeguimientoListadoComponent.name, `loadTable(${reset})`, 'start');
    const evaluaciones$ = this.getObservableLoadTable(reset);
    this.suscripciones.push(
      evaluaciones$.subscribe(
        (evaluaciones: EvaluacionListado[]) => {
          this.evaluaciones = [];
          if (evaluaciones) {
            this.evaluaciones = evaluaciones;
            this.loadSolicitantes();
          }
        },
        () => {
          this.logger.error(SeguimientoListadoComponent.name, `loadTable(${reset})`, 'error');
          this.snackBarService.showError(MSG_ERROR);
        })
    );
    this.logger.debug(SeguimientoListadoComponent.name, `loadTable(${reset})`, 'end');
  }

  /**
   * Carga los datos de los solicitantes de las evaluaciones
   */
  private loadSolicitantes(): void {
    this.logger.debug(SeguimientoListadoComponent.name, `loadSolicitantes()`, 'start');
    this.evaluaciones.map((evaluacion: EvaluacionListado) => {
      const personaRef = evaluacion.memoria?.peticionEvaluacion?.personaRef;
      if (personaRef) {
        this.suscripciones.push(
          this.personaFisicaService.getInformacionBasica(personaRef).subscribe(
            (persona: Persona) => evaluacion.persona = persona
          )
        );
      }
    });
    this.logger.debug(SeguimientoListadoComponent.name, `loadSolicitantes()`, 'end');
  }

  protected createFiltros(): SgiRestFilter[] {
    this.logger.debug(SeguimientoListadoComponent.name, `createFiltros()`, 'start');
    const filtros = [];
    this.addFiltro(filtros, 'convocatoriaReunion.comite.id', SgiRestFilterType.EQUALS,
      this.formGroup.controls.comite.value.id);
    const inicio = DateUtils.getFechaInicioDia(this.formGroup.controls.fechaEvaluacionInicio.value);
    this.addFiltro(filtros, 'convocatoriaReunion.fechaEvaluacion', SgiRestFilterType.GREATHER_OR_EQUAL,
      DateUtils.formatFechaAsISODateTime(inicio));
    const fin = DateUtils.getFechaInicioDia(this.formGroup.controls.fechaEvaluacionFin.value);
    this.addFiltro(filtros, 'convocatoriaReunion.fechaEvaluacion', SgiRestFilterType.LOWER_OR_EQUAL,
      DateUtils.formatFechaAsISODateTime(fin));
    this.addFiltro(filtros, 'memoria.numReferencia', SgiRestFilterType.EQUALS,
      this.formGroup.controls.memoriaNumReferencia.value);
    this.logger.debug(SeguimientoListadoComponent.name, `createFiltros()`, 'end');
    return filtros;
  }

  /**
   * Carga todas los comites existentes
   */
  private loadComites(): void {
    this.logger.debug(SeguimientoListadoComponent.name, 'loadComites()', 'start');
    this.suscripciones.push(
      this.comiteService.findAll().subscribe(
        (res: SgiRestListResult<Comite>) => {
          if (res) {
            this.comiteListado = res.items;
            this.comitesFiltrados = this.formGroup.controls.comite.valueChanges
              .pipe(
                startWith(''),
                map(valor => this.filterComites(valor))
              );
          } else {
            this.comiteListado = [];
          }
        })
    );
    this.logger.debug(SeguimientoListadoComponent.name, 'loadComites()', 'end');
  }

  /**
   * Filtro de campo autocompletable comité.
   *
   * @param filtro valor a filtrar (string o nombre comité).
   * @return lista de comités filtrados.
   */
  private filterComites(filtro: string | Comite): Comite[] {
    const valorLog = filtro instanceof String ? filtro : JSON.stringify(filtro);
    this.logger.debug(SeguimientoListadoComponent.name, `filterComites(${valorLog})`, 'start');
    const result = this.comiteListado.filter(
      (comite: Comite) => comite.comite.toLowerCase().includes(
        typeof filtro === 'string' ? filtro.toLowerCase() : filtro.comite.toLowerCase()
      )
    );
    this.logger.debug(SeguimientoListadoComponent.name, `filterComites(${valorLog})`, 'end');
    return result;
  }

  /**
   * Devuelve el nombre de un comité.
   * @param comite comité
   * @returns nombre comité
   */
  getNombreComite(comite: Comite): string {
    return comite?.comite;
  }
}
