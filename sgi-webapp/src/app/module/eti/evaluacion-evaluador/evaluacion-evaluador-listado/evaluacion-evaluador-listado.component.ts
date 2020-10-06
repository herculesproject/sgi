import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { Comite } from '@core/models/eti/comite';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { TipoConvocatoriaReunion } from '@core/models/eti/tipo-convocatoria-reunion';
import { IPersona } from '@core/models/sgp/persona';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ComiteService } from '@core/services/eti/comite.service';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { TipoConvocatoriaReunionService } from '@core/services/eti/tipo-convocatoria-reunion.service';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { DateUtils } from '@core/utils/date-utils';
import { SgiRestFilter, SgiRestFilterType, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { IEvaluacionSolicitante } from '@core/models/eti/evaluacion-solicitante';
import { EvaluadorService } from '@core/services/eti/evaluador.service';

const MSG_ERROR = marker('eti.evaluacion.listado.error');
const MSG_ERROR_LOAD_TIPOS_CONVOCATORIA = marker('eti.evaluacion.listado.buscador.tipoConvocatoria.error');

@Component({
  selector: 'sgi-evaluacion-evaluador-listado',
  templateUrl: './evaluacion-evaluador-listado.component.html',
  styleUrls: ['./evaluacion-evaluador-listado.component.scss']
})
export class EvaluacionEvaluadorListadoComponent extends AbstractTablePaginationComponent<IEvaluacion> implements OnInit {

  evaluaciones: IEvaluacion[];
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  comiteListado: Comite[];
  comitesFiltrados: Observable<Comite[]>;
  tiposConvocatoriaReunion: TipoConvocatoriaReunion[];

  constructor(
    protected readonly logger: NGXLogger,
    private readonly evaluadorService: EvaluadorService,
    private readonly personaFisicaService: PersonaFisicaService,
    private readonly comiteService: ComiteService,
    private readonly tipoConvocatoriaReunionService: TipoConvocatoriaReunionService,
    protected readonly snackBarService: SnackBarService
  ) {
    super(logger, snackBarService, MSG_ERROR);

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

  ngOnInit() {
    this.logger.debug(EvaluacionEvaluadorListadoComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();
    this.formGroup = new FormGroup({
      comite: new FormControl(''),
      fechaEvaluacionInicio: new FormControl(''),
      fechaEvaluacionFin: new FormControl(''),
      memoriaNumReferencia: new FormControl(''),
      tipoConvocatoria: new FormControl('')
    });
    this.loadComites();
    this.loadConvocatorias();
    this.logger.debug(EvaluacionEvaluadorListadoComponent.name, 'ngOnInit()', 'end');
  }

  protected createObservable(): Observable<SgiRestListResult<IEvaluacion>> {
    return this.evaluadorService.getEvaluaciones(this.getFindOptions());
  }

  protected initColumns() {
    this.logger.debug(EvaluacionEvaluadorListadoComponent.name, 'initColumns()', 'start');
    this.columnas = ['convocatoriaReunion.comite.id', 'convocatoriaReunion.fechaEvaluacion',
      'memoria.numReferencia', 'solicitante', 'version', 'acciones'];
    this.logger.debug(EvaluacionEvaluadorListadoComponent.name, 'initColumns()', 'end');
  }

  protected loadTable(reset?: boolean) {
    this.logger.debug(EvaluacionEvaluadorListadoComponent.name, `loadTable(${reset})`, 'start');
    const evaluaciones$ = this.getObservableLoadTable(reset);
    this.suscripciones.push(
      evaluaciones$.subscribe(
        (evaluaciones: IEvaluacion[]) => {
          if (evaluaciones) {
            this.evaluaciones = evaluaciones;
            this.loadSolicitantes();
          } else {
            this.evaluaciones = [];
          }
        },
        () => {
          this.snackBarService.showError(MSG_ERROR);
        })
    );
    this.logger.debug(EvaluacionEvaluadorListadoComponent.name, `loadTable(${reset})`, 'end');
  }

  /**
   * Carga los datos de los solicitantes de las evaluaciones
   */
  private loadSolicitantes(): void {
    this.logger.debug(EvaluacionEvaluadorListadoComponent.name,
      `buscarSolicitantes(evaluaciones: ${JSON.stringify(this.evaluaciones)})`, 'start');
    this.evaluaciones.map((evaluacion: IEvaluacionSolicitante) => {
      const personaRef = evaluacion.memoria?.peticionEvaluacion?.personaRef;
      if (personaRef) {
        this.suscripciones.push(
          this.personaFisicaService.getInformacionBasica(personaRef).subscribe(
            (persona: IPersona) => {
              evaluacion.persona = persona;
            }
          )
        );
      }
    });
    this.logger.debug(EvaluacionEvaluadorListadoComponent.name,
      `buscarSolicitantes(evaluaciones: ${JSON.stringify(this.evaluaciones)})`, 'end');
  }

  /**
   * Carga todas los comites existentes
   */
  private loadComites(): void {
    this.logger.debug(EvaluacionEvaluadorListadoComponent.name, 'getComites()', 'start');
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
    this.logger.debug(EvaluacionEvaluadorListadoComponent.name, 'getComites()', 'end');
  }

  /**
   * Carga todas las convocatorias existentes
   */
  private loadConvocatorias() {
    this.suscripciones.push(
      this.tipoConvocatoriaReunionService.findAll().subscribe(
        (res: SgiRestListResult<TipoConvocatoriaReunion>) => {
          this.tiposConvocatoriaReunion = res.items;
        },
        () => {
          this.snackBarService.showError(MSG_ERROR_LOAD_TIPOS_CONVOCATORIA);
        }
      )
    );
  }

  /**
   * Filtro de campo autocompletable comité.
   *
   * @param filtro valor a filtrar (string o nombre comité).
   * @return lista de comités filtrados.
   */
  private filterComites(filtro: string | Comite): Comite[] {
    const valorLog = filtro instanceof String ? filtro : JSON.stringify(filtro);
    this.logger.debug(EvaluacionEvaluadorListadoComponent.name, `filtrarComites(${valorLog})`, 'start');
    const result = this.comiteListado.filter(
      (comite: Comite) => comite.comite.toLowerCase().includes(
        typeof filtro === 'string' ? filtro.toLowerCase() : filtro.comite.toLowerCase()
      )
    );
    this.logger.debug(EvaluacionEvaluadorListadoComponent.name, `filtrarComites(${valorLog})`, 'end');
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

  /**
   * Devuelve el nombre de un tipo de convocatoria.
   * @param tipoConvocatoriaReunion Tipo de convocatoria
   * @returns El nombre del tipo de convocatoria
   */
  getNombreTipoConvocatoriaReunion(tipoConvocatoriaReunion: TipoConvocatoriaReunion): string {
    return tipoConvocatoriaReunion?.nombre;
  }

  protected createFilters(): SgiRestFilter[] {
    this.logger.debug(EvaluacionEvaluadorListadoComponent.name, `crearFiltros()`, 'start');
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
    this.addFiltro(filtros, 'convocatoriaReunion.tipoConvocatoriaReunion.id', SgiRestFilterType.EQUALS,
      this.formGroup.controls.tipoConvocatoria.value.id);
    this.logger.debug(EvaluacionEvaluadorListadoComponent.name, `crearFiltros()`, 'end');
    return filtros;
  }
}
