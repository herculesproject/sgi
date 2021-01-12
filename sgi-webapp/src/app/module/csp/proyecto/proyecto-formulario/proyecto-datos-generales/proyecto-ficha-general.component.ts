import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { IProyecto, TipoHorasAnualesEnum, TipoHojaFirmaEnum, TipoPlantillaJustificacionEnum } from '@core/models/csp/proyecto';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { ProyectoFichaGeneralFragment } from './proyecto-ficha-general.fragment';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ProyectoActionService } from '../../proyecto.action.service';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { Subscription, Observable, of } from 'rxjs';
import { map, startWith, tap } from 'rxjs/operators';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { IModeloEjecucion, ITipoFinalidad } from '@core/models/csp/tipos-configuracion';
import { SgiRestFilter, SgiRestFilterType, SgiRestFindOptions } from '@sgi/framework/http';
import { ModeloEjecucionService } from '@core/services/csp/modelo-ejecucion.service';
import { ModeloUnidadService } from '@core/services/csp/modelo-unidad.service';
import { ITipoAmbitoGeografico } from '@core/models/csp/tipo-ambito-geografico';
import { TipoAmbitoGeograficoService } from '@core/services/csp/tipo-ambito-geografico.service';
import { ClasificacionCVN } from '@core/enums/clasificacion-cvn';
import { TipoEstadoProyecto } from '@core/models/csp/estado-proyecto';

const MSG_ERROR_INIT = marker('csp.proyecto.datosGenerales.error.cargar');

@Component({
  selector: 'sgi-proyecto-ficha-general',
  templateUrl: './proyecto-ficha-general.component.html',
  styleUrls: ['./proyecto-ficha-general.component.scss']
})
export class ProyectoFichaGeneralComponent extends FormFragmentComponent<IProyecto> implements OnInit, OnDestroy {

  formPart: ProyectoFichaGeneralFragment;

  fxFlexProperties: FxFlexProperties;
  fxFlexPropertiesOne: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexPropertiesInline: FxFlexProperties;
  fxFlexPropertiesEntidad: FxFlexProperties;

  private finalidadFiltered = [] as ITipoFinalidad[];
  finalidades$: Observable<ITipoFinalidad[]>;

  private modelosEjecucionFiltered = [] as IModeloEjecucion[];
  modelosEjecucion$: Observable<IModeloEjecucion[]>;

  private unidadGestionFiltered = [] as IUnidadGestion[];
  unidadesGestion$: Observable<IUnidadGestion[]>;

  private tipoAmbitoGeograficoFiltered = [] as ITipoAmbitoGeografico[];
  tipoAmbitosGeograficos$: Observable<ITipoAmbitoGeografico[]>;

  private subscriptions = [] as Subscription[];

  cientificaCVN = Object.keys(ClasificacionCVN).map<string>(
    (key) => ClasificacionCVN[key]);

  tipoHoras = Object.keys(TipoHorasAnualesEnum).map<string>(
    (key) => TipoHorasAnualesEnum[key]);

  tipoHoja = Object.keys(TipoHojaFirmaEnum).map<string>(
    (key) => TipoHojaFirmaEnum[key]);

  plantilla = Object.keys(TipoPlantillaJustificacionEnum).map<string>(
    (key) => TipoPlantillaJustificacionEnum[key]);

  private obligatorioTimesheet: boolean;
  requiredHoras: boolean;

  constructor(
    protected readonly logger: NGXLogger,
    protected actionService: ProyectoActionService,
    private readonly snackBarService: SnackBarService,
    private unidadGestionService: UnidadGestionService,
    private modeloEjecucionService: ModeloEjecucionService,
    private unidadModeloService: ModeloUnidadService,
    private tipoAmbitoGeograficoService: TipoAmbitoGeograficoService
  ) {
    super(actionService.FRAGMENT.FICHA_GENERAL, actionService);
    this.logger.debug(ProyectoFichaGeneralComponent.name, 'constructor()', 'start');
    this.formPart = this.fragment as ProyectoFichaGeneralFragment;

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

    this.logger.debug(ProyectoFichaGeneralComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(ProyectoFichaGeneralComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();
    this.loadUnidadesGestion();
    this.loadAmbitosGeograficos();

    if (!this.fragment.isEdit()) {
      this.formGroup.controls.estado.setValue(TipoEstadoProyecto.BORRADOR);
    }

    this.subscriptions.push(this.formGroup.controls.costeHora.valueChanges.pipe(
      tap(() => this.validarCoste())
    ).subscribe());

    this.subscriptions.push(this.formGroup.controls.timesheet.valueChanges.pipe(
      tap(() => this.validarTimesheet())
    ).subscribe());

    this.formGroup.controls.horasAnuales.disable();

    this.logger.debug(ProyectoFichaGeneralComponent.name, 'ngOnInit()', 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(ProyectoFichaGeneralComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(ProyectoFichaGeneralComponent.name, 'ngOnDestroy()', 'end');
  }

  /**
  * En caso de ser NO timesheet
  * y SI calculo coste
  * Error en el formulario
  */
  private validarTimesheet() {
    this.logger.debug(ProyectoFichaGeneralComponent.name, 'validarTimesheet()', 'start');
    if (this.formGroup.controls.timesheet.value === false && this.formGroup.controls.costeHora.value === true) {
      this.formGroup.controls.timesheet.setErrors({ invalid: true });
      this.formGroup.controls.timesheet.markAsTouched({ onlySelf: true });
    }
    this.logger.debug(ProyectoFichaGeneralComponent.name, 'validarTimesheet()', 'start');
  }

  /**
  * En caso de activar coste hora personal
  * Habilitamos horas anuales
  */
  private validarCoste() {
    this.logger.debug(ProyectoFichaGeneralComponent.name, 'validarCoste()', 'start');
    if (this.formGroup.controls.costeHora.value) {
      this.formGroup.controls.horasAnuales.enable();
      this.requiredHoras = true;
      if (this.formGroup.controls.timesheet.value === null || this.formGroup.controls.timesheet.value === false) {
        this.formGroup.controls.timesheet.setErrors({ invalid: true });
        this.formGroup.controls.timesheet.markAsTouched({ onlySelf: true });
        this.obligatorioTimesheet = true;
      } else {
        this.obligatorioTimesheet = false;
        this.formGroup.controls.timesheet.updateValueAndValidity({ onlySelf: true });
      }
    } else {
      this.formGroup.controls.horasAnuales.patchValue('');
      this.requiredHoras = false;
      this.formGroup.controls.horasAnuales.disable();
      if (this.formGroup.controls.timesheet.value === false) {
        this.obligatorioTimesheet = false;
        this.formGroup.controls.timesheet.updateValueAndValidity({ onlySelf: true });
      }
    }
    this.logger.debug(ProyectoFichaGeneralComponent.name, 'validarCoste()', 'start');
  }

  /**
   * Validacion timesheet por estado u obligatorio
   */
  validacionTimesheet() {
    this.logger.debug(ProyectoFichaGeneralComponent.name, 'validacionTimesheet()', 'start');
    if (this.formPart.abiertoRequired === true || this.obligatorioTimesheet === true) {
      this.logger.debug(ProyectoFichaGeneralComponent.name, 'validacionTimesheet()', 'end');
      return true;
    } else {
      this.logger.debug(ProyectoFichaGeneralComponent.name, 'validacionTimesheet()', 'end');
      return false;
    }
  }

  /**
   * Listado ambito geografico
   */
  private loadAmbitosGeograficos(): void {
    this.logger.debug(ProyectoFichaGeneralComponent.name, `loadAmbitosGeograficos()`, 'start');
    this.subscriptions.push(
      this.tipoAmbitoGeograficoService.findAll().subscribe(
        res => {
          this.tipoAmbitoGeograficoFiltered = res.items;
          this.tipoAmbitosGeograficos$ = this.formGroup.controls.ambitoGeografico.valueChanges
            .pipe(
              startWith(''),
              map(value => this.filtroTipoAmbitoGeografico(value))
            );
          this.logger.debug(ProyectoFichaGeneralComponent.name, `loadAmbitosGeograficos()`, 'end');
        },
        () => {
          this.snackBarService.showError(MSG_ERROR_INIT);
          this.logger.error(ProyectoFichaGeneralComponent.name, `loadAmbitosGeograficos()`, 'error');
        }
      )
    );
  }

  /**
   * Listado de modelos de ejecucion
   */
  loadModelosEjecucion(): void {
    this.logger.debug(ProyectoFichaGeneralComponent.name, `loadModelosEjecucion()`, 'start');
    const options = {
      filters: [
        {
          field: 'unidadGestionRef',
          type: SgiRestFilterType.EQUALS,
          value: this.formGroup.controls.unidadGestion.value?.acronimo,
        } as SgiRestFilter,
        {
          field: 'modeloEjecucion.activo',
          type: SgiRestFilterType.EQUALS,
          value: 'true',
        } as SgiRestFilter
      ]
    } as SgiRestFindOptions;
    const subcription = this.unidadModeloService.findAll(options).subscribe(
      res => {
        this.modelosEjecucionFiltered = res.items.map(item => item.modeloEjecucion);
        this.modelosEjecucion$ = this.formGroup.controls.modeloEjecucion.valueChanges
          .pipe(
            startWith(''),
            map(value => this.filtroModeloEjecucion(value))
          );
        this.logger.debug(ProyectoFichaGeneralComponent.name, `loadModelosEjecucion()`, 'end');
      },
      (error) => {
        this.snackBarService.showError(MSG_ERROR_INIT);
        this.logger.error(ProyectoFichaGeneralComponent.name, `loadModelosEjecucion()`, error);
      }
    );
    this.subscriptions.push(subcription);
  }

  /**
  * Listado de finalidades
  */
  loadFinalidades(): void {
    this.logger.debug(ProyectoFichaGeneralComponent.name, `loadFinalidades()`, 'start');
    const modeloEjecucion = this.formGroup.get('modeloEjecucion').value;
    if (modeloEjecucion) {
      const id = modeloEjecucion.id;
      if (id && !isNaN(id)) {
        const options = {
          filters: [
            {
              field: 'tipoFinalidad.activo',
              type: SgiRestFilterType.EQUALS,
              value: 'true',
            } as SgiRestFilter
          ]
        } as SgiRestFindOptions;
        this.subscriptions.push(
          this.modeloEjecucionService.findModeloTipoFinalidad(id, options).pipe(
            map(res => {
              return res.items.map(modeloTipoFinalidad => modeloTipoFinalidad.tipoFinalidad);
            })
          ).subscribe(
            tipoFinalidades => {
              this.finalidadFiltered = tipoFinalidades;
              this.finalidades$ = this.formGroup.controls.finalidad.valueChanges
                .pipe(
                  startWith(''),
                  map(value => this.filtroFinalidades(value))
                );
              this.logger.debug(ProyectoFichaGeneralComponent.name, `loadFinalidades()`, 'end');
            },
            (error) => {
              this.snackBarService.showError(MSG_ERROR_INIT);
              this.logger.error(ProyectoFichaGeneralComponent.name, `loadFinalidades()`, error);
            }
          )
        );
      }
    }
  }

  /**
   * Devuelve el nombre de una gestión unidad.
   *
   * @param unidadGestion gestión unidad.
   * @returns nombre de una gestión unidad.
   */
  getUnidadGestion(unidadGestion?: IUnidadGestion): string | undefined {
    return typeof unidadGestion === 'string' ? unidadGestion : unidadGestion?.nombre;
  }

  /**
   * Carga la lista de unidades de gestion seleccionables por el usuario
   */
  private loadUnidadesGestion(): void {
    this.logger.debug(ProyectoFichaGeneralComponent.name, `loadUnidadesGestion()`, 'start');
    this.subscriptions.push(
      this.unidadGestionService.findAllRestringidos().subscribe(
        res => {
          this.unidadGestionFiltered = res.items;
          this.unidadesGestion$ = this.formGroup.controls.unidadGestion.valueChanges
            .pipe(
              startWith(''),
              map(value => this.filtroUnidadGestion(value))
            );
          this.logger.debug(ProyectoFichaGeneralComponent.name, `loadUnidadesGestion()`, 'end');
        },
        (error) => {
          this.snackBarService.showError(MSG_ERROR_INIT);
          this.logger.error(ProyectoFichaGeneralComponent.name, `loadUnidadesGestion()`, error);
        }
      )
    );
  }

  /**
   * Filtra la lista devuelta por el servicio
   *
   * @param value del input para autocompletar
   */
  private filtroModeloEjecucion(value: string): IModeloEjecucion[] {
    // Si no es un string no se filtra
    if (typeof value !== 'string') {
      return this.modelosEjecucionFiltered;
    }
    const filterValue = value.toString().toLowerCase();
    return this.modelosEjecucionFiltered.filter(modeloEjecucion => modeloEjecucion.nombre.toLowerCase().includes(filterValue));
  }

  /**
   * Filtra la lista devuelta por el servicio
   *
   * @param value del input para autocompletar
   */
  private filtroFinalidades(value: string): ITipoFinalidad[] {
    // Si no es un string no se filtra
    if (typeof value !== 'string') {
      return this.finalidadFiltered;
    }
    const filterValue = value.toString().toLowerCase();
    return this.finalidadFiltered.filter(finalidad => finalidad.nombre.toLowerCase().includes(filterValue));
  }

  /**
   * Filtra la lista devuelta por el servicio
   *
   * @param value del input para autocompletar
   */
  private filtroUnidadGestion(value: string): IUnidadGestion[] {
    // Si no es un string no se filtra
    if (typeof value !== 'string') {
      return this.unidadGestionFiltered;
    }
    const filterValue = value.toString().toLowerCase();
    return this.unidadGestionFiltered.filter(unidadGestion => unidadGestion.nombre.toLowerCase().includes(filterValue));
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
   * Devuelve el nombre de una finalidad.
   * @param finalidad finalidad.
   * @returns nombre de una finalidad.
   */
  getFinalidad(finalidad?: ITipoFinalidad): string | undefined {
    return typeof finalidad === 'string' ? finalidad : finalidad?.nombre;
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
   * Filtra la lista devuelta por el servicio
   *
   * @param value del input para autocompletar
   */
  private filtroTipoAmbitoGeografico(value: string): ITipoAmbitoGeografico[] {
    // Si no es un string no se filtra
    if (typeof value !== 'string') {
      return this.tipoAmbitoGeograficoFiltered;
    }
    const filterValue = value?.toString().toLowerCase();
    return this.tipoAmbitoGeograficoFiltered.filter(
      ambitoGeografico => ambitoGeografico.nombre.toLowerCase().includes(filterValue)
    );
  }

  clearFinalidad(): void {
    this.logger.debug(ProyectoFichaGeneralComponent.name, `clearFinalidad()`, 'end');
    this.formGroup.get('finalidad').setValue('');
    this.finalidadFiltered = [];
    this.finalidades$ = of();
    this.logger.debug(ProyectoFichaGeneralComponent.name, `clearFinalidad()`, 'end');
  }

  clearModeloEjecuccion(): void {
    this.logger.debug(ProyectoFichaGeneralComponent.name, `clearModeloEjecuccion()`, 'end');
    this.formGroup.get('modeloEjecucion').setValue('');
    this.modelosEjecucionFiltered = [];
    this.modelosEjecucion$ = of();
    this.clearFinalidad();
    this.logger.debug(ProyectoFichaGeneralComponent.name, `clearModeloEjecuccion()`, 'end');
  }

}
