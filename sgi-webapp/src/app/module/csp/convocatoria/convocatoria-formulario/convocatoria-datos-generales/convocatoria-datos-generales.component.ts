import { Component, OnInit, ViewChild } from '@angular/core';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { NGXLogger } from 'ngx-logger';
import { SnackBarService } from '@core/services/snack-bar.service';
import { ConvocatoriaActionService } from '../../convocatoria.action.service';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { Observable, Subscription, BehaviorSubject } from 'rxjs';
import { ModeloEjecucionService } from '@core/services/csp/modelo-ejecucion.service';
import { SgiRestListResult } from '@sgi/framework/http/types';
import { startWith, map } from 'rxjs/operators';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { ITipoAmbitoGeografico } from '@core/models/csp/tipo-ambito-geografico';
import { TipoAmbitoGeograficoService } from '@core/services/csp/tipo-ambito-geografico.service';
import { ITipoRegimenConcurrencia } from '@core/models/csp/tipo-regimen-concurrencia';
import { TipoRegimenConcurrenciaService } from '@core/services/csp/tipo-regimen-concurrencia.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { IAreaTematicaArbol } from '@core/models/csp/area-tematica-arbol';
import { ConvocatoriaDatosGeneralesFragment } from './convocatoria-datos-generales.fragment';
import { IModeloEjecucion, ITipoFinalidad } from '@core/models/csp/tipos-configuracion';
import { IModeloTipoFinalidad } from '@core/models/csp/modelo-tipo-finalidad';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { GLOBAL_CONSTANTS } from '@core/utils/global-constants';
import { IConvocatoriaAreaTematica } from '@core/models/csp/convocatoria-area-tematica';
import { MatDialog } from '@angular/material/dialog';
import { ConvocatoriaAreaTematicaModalComponent } from '../../modals/convocatoria-area-tematica-modal/convocatoria-area-tematica-modal.component';
import { MatAutocompleteTrigger } from '@angular/material/autocomplete';

const MSG_ERROR_INIT = marker('csp.convocatoria.datos.generales.error.cargar');
const LABEL_BUSCADOR_EMPRESAS_ECONOMICAS = marker('csp.convocatoria.entidad.gestora');


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
  formPart: ConvocatoriaDatosGeneralesFragment;
  label = LABEL_BUSCADOR_EMPRESAS_ECONOMICAS;

  @ViewChild(MatAutocompleteTrigger) autocomplete: MatAutocompleteTrigger;

  areasTematicas$: BehaviorSubject<StatusWrapper<IAreaTematicaArbol>[]>;

  fxFlexProperties: FxFlexProperties;
  fxFlexPropertiesOne: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexPropertiesInline: FxFlexProperties;
  fxFlexPropertiesEntidad: FxFlexProperties;

  private tipoAmbitoGeograficoFiltered = [] as ITipoAmbitoGeografico[];
  tipoAmbitosGeograficos$: Observable<ITipoAmbitoGeografico[]>;

  private finalidadFiltered = [] as ITipoFinalidad[];
  finalidades$: Observable<ITipoFinalidad[]>;

  private modelosEjecucionFiltered = [] as IModeloEjecucion[];
  modelosEjecucion$: Observable<IModeloEjecucion[]>;

  private tipoRegimenConcurrenciaFiltered = [] as ITipoRegimenConcurrencia[];
  tipoRegimenesConcurrencia$: Observable<ITipoRegimenConcurrencia[]>;

  private unidadGestionFiltered = [] as IUnidadGestion[];
  unidadesGestion$: Observable<IUnidadGestion[]>;

  private suscripciones = [] as Subscription[];

  convocatoriaAreaTematicas = new MatTableDataSource<StatusWrapper<IConvocatoriaAreaTematica>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;
  columns = ['listadoAreas', 'areaTematica', 'observaciones', 'acciones'];
  numPage = [5, 10, 25, 50];

  clasificacionesProduccion = [
    clasificacionesProduccionAyuda, clasificacionesProyectoCompetitivo,
    clasificacionesProyectoNoCompetitivo, clasificacionesProyectoEstancias
  ];
  destinatarios = [
    destinatarioIndividual,
    destinatarioEquipoProyecto,
    destinatarioGrupoInvestigacion
  ];
  estados = [estadoBorrador, estadoRegistrada];
  proyectosColaborativo = [proyectoColaborativoSi, proyectoColaborativoNo];

  constructor(
    protected readonly logger: NGXLogger,
    protected actionService: ConvocatoriaActionService,
    private readonly snackBarService: SnackBarService,
    private modeloEjecucionService: ModeloEjecucionService,
    private unidadGestionService: UnidadGestionService,
    private regimenConcurrenciaService: TipoRegimenConcurrenciaService,
    private tipoAmbitoGeograficoService: TipoAmbitoGeograficoService,
    private matDialog: MatDialog
  ) {

    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, 'constructor()', 'start');
    this.formPart = this.fragment as ConvocatoriaDatosGeneralesFragment;

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

    this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, 'constructor()', 'end');
  }

  ngOnInit() {
    this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();
    this.loadUnidadesGestion();
    this.loadTipoRegimenConcurrencia();
    this.loadAmbitosGeograficos();
    this.loadAreaTematicas();
    this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, 'ngOnInit()', 'end');
  }

  private loadUnidadesGestion() {
    this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, `${this.loadUnidadesGestion.name}()`, 'start');
    this.suscripciones.push(
      // TODO Debería filtrar por el rol
      this.unidadGestionService.findAll().subscribe(
        res => {
          this.unidadGestionFiltered = res.items;
          this.unidadesGestion$ = this.formGroup.controls.unidadGestion.valueChanges
            .pipe(
              startWith(''),
              map(value => this.filtroUnidadGestion(value))
            );
          this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, `${this.loadUnidadesGestion.name}()`, 'end');
        },
        () => {
          this.snackBarService.showError(MSG_ERROR_INIT);
          this.logger.error(ConvocatoriaDatosGeneralesComponent.name, `${this.loadUnidadesGestion.name}()`, 'error');
        }
      )
    );
  }

  loadModelosEjecucion() {
    this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, `${this.loadModelosEjecucion.name}()`, 'start');
    this.formGroup.get('modeloEjecucion').setValue('');
    this.suscripciones.push(
      // TODO Cambiar endpoint para que filtre con la unidad de gestión
      this.modeloEjecucionService.findAll().subscribe(
        res => {
          this.modelosEjecucionFiltered = res.items;
          this.modelosEjecucion$ = this.formGroup.controls.modeloEjecucion.valueChanges
            .pipe(
              startWith(''),
              map(value => this.filtroModeloEjecucion(value))
            );
          this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, `${this.loadModelosEjecucion.name}()`, 'end');
        },
        () => {
          this.snackBarService.showError(MSG_ERROR_INIT);
          this.logger.error(ConvocatoriaDatosGeneralesComponent.name, `${this.loadModelosEjecucion.name}()`, 'error');
        }
      )
    );
  }

  loadFinalidades() {
    this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, `${this.loadFinalidades.name}()`, 'start');
    this.formGroup.get('finalidad').setValue('');
    const modeloEjecucion = this.formGroup.get('modeloEjecucion').value;
    if (modeloEjecucion) {
      const id = modeloEjecucion.id;
      if (id && !isNaN(id)) {
        this.suscripciones.push(
          this.modeloEjecucionService.findModeloTipoFinalidad(id).pipe(
            map(res => {
              const tipoFinalidades = res.items.map(modeloTipoFinalidad => modeloTipoFinalidad.tipoFinalidad);
              return tipoFinalidades;
            })
          ).subscribe(
            tipoFinalidades => {
              this.finalidadFiltered = tipoFinalidades;
              this.finalidades$ = this.formGroup.controls.finalidad.valueChanges
                .pipe(
                  startWith(''),
                  map(value => this.filtroFinalidades(value))
                );
              this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, `${this.loadFinalidades.name}()`, 'end');
            },
            () => {
              this.snackBarService.showError(MSG_ERROR_INIT);
              this.logger.error(ConvocatoriaDatosGeneralesComponent.name, `${this.loadFinalidades.name}()`, 'error');
            }
          )
        );
      }
    }
  }

  private loadTipoRegimenConcurrencia() {
    this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, `${this.loadTipoRegimenConcurrencia.name}()`, 'start');
    this.suscripciones.push(
      this.regimenConcurrenciaService.findAll().subscribe(
        res => {
          this.tipoRegimenConcurrenciaFiltered = res.items;
          this.tipoRegimenesConcurrencia$ = this.formGroup.controls.tipoRegimenConcurrencia.valueChanges
            .pipe(
              startWith(''),
              map(value => this.filtroRegimenConcurrencia(value))
            );
          this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, `${this.loadTipoRegimenConcurrencia.name}()`, 'end');
        },
        () => {
          this.snackBarService.showError(MSG_ERROR_INIT);
          this.logger.error(ConvocatoriaDatosGeneralesComponent.name, `${this.loadTipoRegimenConcurrencia.name}()`, 'error');
        }
      )
    );
  }

  private loadAmbitosGeograficos() {
    this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, `${this.loadAmbitosGeograficos.name}()`, 'start');
    this.suscripciones.push(
      this.tipoAmbitoGeograficoService.findAll().subscribe(
        res => {
          this.tipoAmbitoGeograficoFiltered = res.items;
          this.tipoAmbitosGeograficos$ = this.formGroup.controls.tipoAmbitoGeografico.valueChanges
            .pipe(
              startWith(''),
              map(value => this.filtroTipoAmbitoGeografico(value))
            );
          this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, `${this.loadAmbitosGeograficos.name}()`, 'end');
        },
        () => {
          this.snackBarService.showError(MSG_ERROR_INIT);
          this.logger.error(ConvocatoriaDatosGeneralesComponent.name, `${this.loadAmbitosGeograficos.name}()`, 'error');
        }
      )
    );
  }

  private loadAreaTematicas() {
    this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, `${this.loadAreaTematicas.name}()`, 'start');
    const subscription = this.formPart.convocatoriaAreaTematicas$.subscribe(
      wrappers => this.convocatoriaAreaTematicas.data = wrappers
    );
    this.convocatoriaAreaTematicas.paginator = this.paginator;
    this.convocatoriaAreaTematicas.sort = this.sort;
    this.suscripciones.push(subscription);
    this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, `${this.loadAreaTematicas.name}()`, 'end');
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
  private filtroModeloEjecucion(value: string): IModeloEjecucion[] {
    const filterValue = value.toString().toLowerCase();
    return this.modelosEjecucionFiltered.filter(modeloEjecucion => modeloEjecucion.nombre.toLowerCase().includes(filterValue));
  }

  /**
   * Devuelve el nombre de una finalidad.
   * @param finalidad finalidad.
   * @returns nombre de una finalidad.
   */
  getFinalidad(finalidad?: ITipoFinalidad): string | undefined {
    return typeof finalidad === 'string' ? finalidad : finalidad?.nombre;
  }

  /**
   * Filtra la lista devuelta por el servicio
   *
   * @param value del input para autocompletar
   */
  private filtroFinalidades(value: string): ITipoFinalidad[] {
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
  private filtroUnidadGestion(value: string): IUnidadGestion[] {
    const filterValue = value.toString().toLowerCase();
    return this.unidadGestionFiltered.filter(unidadGestion => unidadGestion.nombre.toLowerCase().includes(filterValue));
  }

  /**
   * Devuelve el nombre de un régimen ocurrencia.
   * @param regimenConcurrencia régimen ocurrencia.
   * @returns nombre de un régimen ocurrencia..
   */
  getTipoRegimenConcurrencia(regimenConcurrencia?: ITipoRegimenConcurrencia): string | undefined {
    return typeof regimenConcurrencia === 'string' ? regimenConcurrencia : regimenConcurrencia?.nombre;
  }

  /**
   * Filtra la lista devuelta por el servicio
   *
   * @param value del input para autocompletar
   */
  private filtroRegimenConcurrencia(value: string): ITipoRegimenConcurrencia[] {
    const filterValue = value.toString().toLowerCase();
    return this.tipoRegimenConcurrenciaFiltered.filter(
      regimenConcurrencia => regimenConcurrencia.nombre.toLowerCase().includes(filterValue)
    );
  }

  /**
   * Devuelve el nombre de un ámbito geográfico.
   * @param tipoAmbitoGeografico ámbito geográfico.
   * @returns nombre de un ámbito geográfico.
   */
  getTipoAmbitoGeografico(tipoAmbitoGeografico?: ITipoAmbitoGeografico): string | undefined {
    return typeof tipoAmbitoGeografico === 'string' ? tipoAmbitoGeografico : tipoAmbitoGeografico?.nombre;
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
  private filtroTipoAmbitoGeografico(value: string): ITipoAmbitoGeografico[] {
    const filterValue = value.toString().toLowerCase();
    return this.tipoAmbitoGeograficoFiltered.filter(
      ambitoGeografico => ambitoGeografico.nombre.toLowerCase().includes(filterValue)
    );
  }

  openModal(): void {
    this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, `${this.openModal.name}()`, 'start');
    const areaTematica = { activo: true } as IAreaTematicaArbol;
    const config = {
      width: GLOBAL_CONSTANTS.widthModalCSP,
      maxHeight: GLOBAL_CONSTANTS.maxHeightModal,
      data: { areaTematica }
    };
    // const dialogRef = this.matDialog.open(ConvocatoriaAreaTematicaModalComponent, config);
    // dialogRef.afterClosed().subscribe(
    //   (result: IConvocatoriaAreaTematica) => {
    //     if (result) {
    //       // this.formPart.addModeloTipoDocumento(result);
    //     }
    //     this.logger.debug(ConvocatoriaDatosGeneralesComponent.name, `${this.openModal.name}()`, 'end');
    //   }
    // );
  }
}
