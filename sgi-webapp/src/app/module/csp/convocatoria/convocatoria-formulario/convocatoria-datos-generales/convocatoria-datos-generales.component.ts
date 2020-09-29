import { Component, OnInit, ViewChild } from '@angular/core';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { NGXLogger } from 'ngx-logger';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { ConvocatoriaActionService } from '../../convocatoria.action.service';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { IModeloEjecucion } from '@core/models/csp/modelo-ejecucion';
import { Observable, Subscription } from 'rxjs';
import { ModeloEjecucionService } from '@core/services/csp/modelo-ejecucion.service';
import { SgiRestListResult } from '@sgi/framework/http/types';
import { startWith, map, delay } from 'rxjs/operators';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MatAutocompleteTrigger } from '@angular/material/autocomplete';
import { IFinalidad } from '@core/models/csp/finalidad';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { IUnidadGestion } from '@core/models/csp/unidad-gestion';
import { IAmbitoGeografico } from '@core/models/csp/ambito-geografico';
import { AmbitoGeograficoService } from '@core/services/csp/ambito-geografico.service';
import { IRegimenConcurrencia } from '@core/models/csp/regimen-concurrencia';
import { RegimenConcurrenciaService } from '@core/services/csp/regimen-concurrencia.service';



const MSG_ERROR_INIT = marker('csp.convocatoria.datos.generales.error.cargar');

const clasificacionesProduccionAyuda = marker('csp.convocatoria.clasificacion.produccion.ayudas');
const clasificacionesProyectoCompetitivo = marker('csp.convocatoria.clasificacion.produccion.proyecto.competitivo');
const clasificacionesProyectoNoCompetitivo = marker('csp.convocatoria.clasificacion.produccion.proyecto.no.competitivo');
const clasificacionesProyectoEstancias = marker('csp.convocatoria.clasificacion.produccion.proyecto.estancias');

const destinatarioIndividual = marker('csp.convocatoria.destinatarios.individual');
const destinatarioEquipoProyecto = marker('csp.convocatoria.destinatarios.equipo.proyecto');
const destinatarioGrupoInvestigacion = marker('csp.convocatoria.destinatarios.grupo.investigacion');

const estadoBorrador = marker('csp.convocatoria.estado.borrador');
const estadoRegistrada = marker('csp.convocatoria.estado.registrada');

const proyectoColaborativoSi = marker('label.si');
const proyectoColaborativoNo = marker('label.no');

@Component({
  selector: 'sgi-convocatoria-datos-generales',
  templateUrl: './convocatoria-datos-generales.component.html',
  styleUrls: ['./convocatoria-datos-generales.component.scss']
})
export class ConvocatoriaDatosGeneralesComponent extends FormFragmentComponent<IConvocatoria> implements OnInit {

  @ViewChild(MatAutocompleteTrigger) autocomplete: MatAutocompleteTrigger;

  FormGroupUtil = FormGroupUtil;

  fxFlexProperties: FxFlexProperties;
  fxFlexPropertiesOne: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexPropertiesInline: FxFlexProperties;
  fxFlexPropertiesEntidad: FxFlexProperties;

  ambitoGeograficoFiltered: IAmbitoGeografico[];
  ambitosGeograficos: Observable<IAmbitoGeografico[]>;

  finalidadFiltered: IFinalidad[];
  finalidades: Observable<IFinalidad[]>;

  modelosEjecucionFiltered: IModeloEjecucion[];
  modelosEjecucion: Observable<IModeloEjecucion[]>;

  regimenConcurrenciaFiltered: IRegimenConcurrencia[];
  regimenesConcurrencia: Observable<IRegimenConcurrencia[]>;

  unidadGestionFiltered: IUnidadGestion[];
  unidadesGestion: Observable<IUnidadGestion[]>;

  suscripciones: Subscription[] = [];


  clasificacionesProduccion: string[] =
    [clasificacionesProduccionAyuda, clasificacionesProyectoCompetitivo, clasificacionesProyectoNoCompetitivo,
      clasificacionesProyectoEstancias];
  destinatarios: string[] = [destinatarioIndividual, destinatarioEquipoProyecto, destinatarioGrupoInvestigacion];
  estados: string[] = [estadoBorrador, estadoRegistrada];
  proyectosColaborativo: string[] = [proyectoColaborativoSi, proyectoColaborativoNo];


  constructor(
    protected readonly logger: NGXLogger,
    private readonly convocatoriaService: ConvocatoriaService,
    private readonly snackBarService: SnackBarService,
    private actionService: ConvocatoriaActionService,
    private modeloEjecucionService: ModeloEjecucionService,
    private unidadGestionService: UnidadGestionService,
    private regimenConcurrenciaService: RegimenConcurrenciaService,
    private ambitoGeograficoService: AmbitoGeograficoService) {

    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(32%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxFlexPropertiesEntidad = new FxFlexProperties();
    this.fxFlexPropertiesEntidad.sm = '0 1 calc(36%-10px)';
    this.fxFlexPropertiesEntidad.md = '0 1 calc(36%-10px)';
    this.fxFlexPropertiesEntidad.gtMd = '0 1 calc(36%-10px)';
    this.fxFlexPropertiesEntidad.order = '3';

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(36%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(32%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxFlexPropertiesInline = new FxFlexProperties();
    this.fxFlexPropertiesInline.sm = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.md = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.order = '4';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }


  ngOnInit() {
    super.ngOnInit();
    this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, 'ngOnInit()', 'start');

    // Carga de combos
    this.loadModelosEjecucion();
    this.loadFinalidades();
    this.loadUnidadesGestion();
    this.loadRegimenesConcurrencia();
    this.loadAmbitosGeograficos();
  }


  loadUnidadesGestion() {

    this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, 'loadUnidadesGestion()', 'start');
    this.suscripciones.push(
      this.unidadGestionService.findUnidadesGestion().subscribe(
        (res: SgiRestListResult<IModeloEjecucion>) => {
          this.unidadGestionFiltered = res.items;
          this.unidadesGestion = this.formGroup.controls.unidadGestion.valueChanges
            .pipe(

              startWith(''),
              map(value => this.filtroUnidadGestion(value))
            );
          this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, 'loadUnidadesGestion()', 'end');
        },
        () => {
          this.snackBarService.showError(MSG_ERROR_INIT);
          this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, 'loadUnidadesGestion()', 'end');
        }
      )
    );
    this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, 'loadUnidadesGestion()', 'end');
  }


  loadModelosEjecucion() {

    this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, 'loadModelosEjecucion()', 'start');
    this.suscripciones.push(
      this.modeloEjecucionService.findModelosEjecucion().subscribe(
        (res: SgiRestListResult<IModeloEjecucion>) => {
          this.modelosEjecucionFiltered = res.items;
          this.modelosEjecucion = this.formGroup.controls.modeloEjecucion.valueChanges
            .pipe(

              startWith(''),
              map(value => this.filtroModeloEjecucion(value))
            );
          this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, 'loadModelosEjecucion()', 'end');
        },
        () => {
          this.snackBarService.showError(MSG_ERROR_INIT);
          this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, 'loadModelosEjecucion()', 'end');
        }
      )
    );
    this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, 'loadModelosEjecucion()', 'end');
  }


  loadFinalidades() {

    this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, 'loadFinalidades()', 'start');

    this.suscripciones.push(
      this.modeloEjecucionService.findFinalidades(1).subscribe(
        (res: SgiRestListResult<IModeloEjecucion>) => {
          this.finalidadFiltered = res.items;
          this.finalidades = this.formGroup.controls.finalidad.valueChanges
            .pipe(

              startWith(''),
              map(value => this.filtroFinalidades(value))
            );
          this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, 'loadFinalidades()', 'end');
        },
        () => {
          this.snackBarService.showError(MSG_ERROR_INIT);
          this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, 'loadFinalidades()', 'end');
        }
      )
    );

    this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, 'loadFinalidades()', 'end');
  }


  loadRegimenesConcurrencia() {

    this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, 'loadRegimenesConcurrencia()', 'start');
    this.suscripciones.push(
      this.regimenConcurrenciaService.findRegimenConcurrencia().subscribe(
        (res: SgiRestListResult<IRegimenConcurrencia>) => {
          this.regimenConcurrenciaFiltered = res.items;
          this.regimenesConcurrencia = this.formGroup.controls.regimenConcurrencia.valueChanges
            .pipe(

              startWith(''),
              map(value => this.filtroRegimenConcurrencia(value))
            );
          this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, 'loadRegimenesConcurrencia()', 'end');
        },
        () => {
          this.snackBarService.showError(MSG_ERROR_INIT);
          this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, 'loadRegimenesConcurrencia()', 'end');
        }
      )
    );
    this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, 'loadRegimenesConcurrencia()', 'end');
  }


  loadAmbitosGeograficos() {
    this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, 'loadAmbitosGeograficos()', 'start');
    this.suscripciones.push(
      this.ambitoGeograficoService.findAmbitoGeografico().subscribe(
        (res: SgiRestListResult<IAmbitoGeografico>) => {
          this.ambitoGeograficoFiltered = res.items;
          this.ambitosGeograficos = this.formGroup.controls.ambitoGeografico.valueChanges
            .pipe(

              startWith(''),
              map(value => this.filtroAmbitoGeografico(value))
            );
          this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, 'loadAmbitosGeograficos()', 'end');
        },
        () => {
          this.snackBarService.showError(MSG_ERROR_INIT);
          this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, 'loadAmbitosGeograficos()', 'end');
        }
      )
    );
    this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, 'loadAmbitosGeograficos()', 'end');
  }


  /**
   * Devuelve el nombre de un modelo de ejecución.
   * @param modeloEjecucion modelo de ejecución.
   * @returns nombre de un modelo de ejecución.
   */
  getModeloEjecucion(modeloEjecucion?: IModeloEjecucion): string | undefined {
    return typeof modeloEjecucion === 'string' ? modeloEjecucion : modeloEjecucion?.nombre;
  }


  /**
   * Filtra la lista devuelta por el servicio
   *
   * @param value del input para autocompletar
   */
  filtroModeloEjecucion(value: string): IModeloEjecucion[] {
    const filterValue = value.toString().toLowerCase();
    return this.modelosEjecucionFiltered.filter(modeloEjecucion => modeloEjecucion.nombre.toLowerCase().includes(filterValue));
  }



  /**
   * Devuelve el nombre de una finalidad.
   * @param finalidad finalidad.
   * @returns nombre de una finalidad.
   */
  getFinalidad(finalidad?: IFinalidad): string | undefined {
    return typeof finalidad === 'string' ? finalidad : finalidad?.nombre;
  }


  /**
   * Filtra la lista devuelta por el servicio
   *
   * @param value del input para autocompletar
   */
  filtroFinalidades(value: string): IFinalidad[] {
    const filterValue = value.toString().toLowerCase();
    return this.finalidadFiltered.filter(finalidad => finalidad.nombre.toLowerCase().includes(filterValue));
  }


  /**
   * Devuelve el nombre de una gestión unidad.
   * @param unidadGestion gestión unidad.
   * @returns nombre de una gestión unidad.
   */
  getUnidadGestion(unidadGestion?: IUnidadGestion): string | undefined {
    return typeof unidadGestion === 'string' ? unidadGestion : unidadGestion?.nombre;
  }


  /**
   * Filtra la lista devuelta por el servicio
   *
   * @param value del input para autocompletar
   */
  filtroUnidadGestion(value: string): IUnidadGestion[] {
    const filterValue = value.toString().toLowerCase();
    return this.unidadGestionFiltered.filter(unidadGestion => unidadGestion.nombre.toLowerCase().includes(filterValue));
  }



  /**
   * Devuelve el nombre de un régimen ocurrencia.
   * @param regimenConcurrencia régimen ocurrencia.
   * @returns nombre de un régimen ocurrencia..
   */
  getRegimenConcurrencia(regimenConcurrencia?: IRegimenConcurrencia): string | undefined {
    return typeof regimenConcurrencia === 'string' ? regimenConcurrencia : regimenConcurrencia?.nombre;
  }


  /**
   * Filtra la lista devuelta por el servicio
   *
   * @param value del input para autocompletar
   */
  filtroRegimenConcurrencia(value: string): IRegimenConcurrencia[] {
    const filterValue = value.toString().toLowerCase();
    return this.regimenConcurrenciaFiltered.filter(regimenConcurrencia => regimenConcurrencia.nombre.toLowerCase().includes(filterValue));
  }


  /**
   * Devuelve el nombre de un ámbito geográfico.
   * @param ambitoGeografico ámbito geográfico.
   * @returns nombre de un ámbito geográfico.
   */
  getAmbitoGeografico(ambitoGeografico?: IAmbitoGeografico): string | undefined {
    return typeof ambitoGeografico === 'string' ? ambitoGeografico : ambitoGeografico?.nombre;
  }


  /**
   * Devuelve el nombre de una clasificación proudcción.
   * @param clasificacionProduccion  clasificación proudcción.
   * @returns nombre de una  clasificación proudcción.
   */
  getClasificacionProduccion(clasificacionProduccion?: string): string | undefined {
    return clasificacionProduccion;
  }

  /**
   * Devuelve el nombre de un destinatario.
   * @param destinatario destinatario.
   * @returns nombre de un destinatario.
   */
  getDestinatario(destinatario?: string): string | undefined {
    return destinatario;
  }

  /**
   * Devuelve el nombre de un estado.
   * @param estado estado.
   * @returns nombre de un estado.
   */
  getEstado(estado?: string): string | undefined {
    return estado;
  }

  /**
   * Devuelve el nombre de un proyecto colaborativo.
   * @param proyectoColaborativo proyecto colaborativo.
   * @returns nombre de un proyecto colaborativo.
   */
  getProyectoColaborativo(proyectoColaborativo?: string): string | undefined {
    return proyectoColaborativo;
  }



  /**
   * Filtra la lista devuelta por el servicio
   *
   * @param value del input para autocompletar
   */
  filtroAmbitoGeografico(value: string): IAmbitoGeografico[] {
    const filterValue = value.toString().toLowerCase();
    return this.ambitoGeograficoFiltered.filter(ambitoGeografico => ambitoGeografico.nombre.toLowerCase().includes(filterValue));
  }




}
