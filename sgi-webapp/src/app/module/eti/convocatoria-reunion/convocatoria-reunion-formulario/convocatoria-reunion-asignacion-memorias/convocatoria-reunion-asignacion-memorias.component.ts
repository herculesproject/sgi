import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Memoria } from '@core/models/eti/memoria';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { NGXLogger } from 'ngx-logger';
import { Observable, Subscription, of } from 'rxjs';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiRestListResult, SgiRestFilter, SgiRestFilterType } from '@sgi/framework/http';
import { startWith, map, switchMap, shareReplay } from 'rxjs/operators';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { NullIdValidador } from '@core/validators/null-id-validador';
import { IEvaluador } from '@core/models/eti/evaluador';
import { EvaluadorService } from '@core/services/eti/evaluador.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';
import { Persona } from '@core/models/sgp/persona';
import { DialogData } from '@block/dialog/dialog.component';
import { DateUtils } from '@core/utils/date-utils';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';

const MSG_ERROR_FORM_GROUP = marker('form-group.error');

@Component({
  selector: 'sgi-convocatoria-reunion-asignacion-memorias',
  templateUrl: './convocatoria-reunion-asignacion-memorias.component.html',
  styleUrls: ['./convocatoria-reunion-asignacion-memorias.component.scss']
})
export class ConvocatoriaReunionAsignacionMemoriasComponent implements OnInit, OnDestroy {
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  formGroup: FormGroup;

  evaluadores: IEvaluador[];
  memorias: Memoria[];

  filteredEvaluadoresEvaluador1: Observable<IEvaluador[]>;
  filteredEvaluadoresEvaluador2: Observable<IEvaluador[]>;
  filteredMemorias: Observable<Memoria[]>;

  subscriptions: Subscription[];

  idConvocatoria: number;
  isTipoConvocatoriaSeguimiento: boolean;
  filterData: { idComite: number, idTipoConvocatoria: number, fechaLimite: Date };
  filterMemoriasAsignables: SgiRestFilter[];
  memoriasAsignadas: Memoria[];

  constructor(
    protected readonly logger: NGXLogger,
    private readonly dialogRef: MatDialogRef<ConvocatoriaReunionAsignacionMemoriasComponent>,
    private readonly evaluadorService: EvaluadorService,
    private readonly memoriaService: MemoriaService,
    private readonly PersonafisicaService: PersonaFisicaService,
    private readonly snackBarService: SnackBarService,
    @Inject(MAT_DIALOG_DATA) public data: DialogData
  ) {
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

    this.subscriptions = [];

    const params: any = data.params;
    this.idConvocatoria = params.idConvocatoria;
    this.memoriasAsignadas = params.memoriasAsignadas;
    this.filterData = params.filterMemoriasAsignables;
    this.buildFilters();
  }

  ngOnInit(): void {
    this.logger.debug(ConvocatoriaReunionAsignacionMemoriasComponent.name, 'ngOnInit()', 'start');

    this.formGroup = this.createFormGroup();

    if (this.idConvocatoria) {
      this.getMemoriasAsignablesConvocatoria();
    } else {
      if (this.isTipoConvocatoriaSeguimiento) {
        this.getMemoriasAsignablesConvocatoriaSeguimiento();
      } else {
        this.getMemoriasAsignablesConvocatoriaOrdExt();
      }
    }

    this.getEvaluadores();

    this.logger.debug(ConvocatoriaReunionAsignacionMemoriasComponent.name, 'ngOnInit()', 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(ConvocatoriaReunionAsignacionMemoriasComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptions?.forEach(x => x.unsubscribe());
    this.logger.debug(ConvocatoriaReunionAsignacionMemoriasComponent.name, 'ngOnDestroy()', 'end');
  }

  /**
   * Crear el FormGroup
   *
   */
  createFormGroup(): FormGroup {
    this.logger.debug(ConvocatoriaReunionAsignacionMemoriasComponent.name, 'createFormGroup()', 'start');
    const formGroup = new FormGroup({
      memoria: new FormControl(null, [new NullIdValidador().isValid()]),
      evaluador1: new FormControl(null, [Validators.required]),
      evaluador2: new FormControl(null, [Validators.required])
    });
    this.logger.debug(ConvocatoriaReunionAsignacionMemoriasComponent.name, 'createFormGroup()', 'end');
    return formGroup;
  }

  /**
   * Construye los filtros necesarios para la búsqueda de las memorias asignables.
   *
   */
  private buildFilters(): void {
    this.logger.debug(ConvocatoriaReunionAsignacionMemoriasComponent.name, 'buildFilters(filterData)', 'start');
    this.filterMemoriasAsignables = [];

    if (this.filterData && this.filterData.idComite &&
      this.filterData.idTipoConvocatoria &&
      this.filterData.fechaLimite) {

      this.isTipoConvocatoriaSeguimiento = (this.filterData.idTipoConvocatoria === 3) ? true : false;

      const filtroComite = {
        field: 'comite.id',
        type: SgiRestFilterType.EQUALS,
        value: this.filterData.idComite.toString(),
      };

      const filtroFechaLimite = {
        field: 'fechaEnvioSecretaria',
        type: SgiRestFilterType.LOWER_OR_EQUAL,
        value: DateUtils.formatFechaAsISODate(this.filterData.fechaLimite),
      };

      this.filterMemoriasAsignables.push(filtroComite);
      this.filterMemoriasAsignables.push(filtroFechaLimite);
    }
  }


  /**
   * Recupera un listado de los memorias asignables a la convocatoria.
   */
  private getMemoriasAsignablesConvocatoria(): void {
    this.logger.debug(ConvocatoriaReunionAsignacionMemoriasComponent.name,
      'getMemoriasAsignablesConvocatoria()',
      'start');

    this.subscriptions.push(this.memoriaService
      .findAllMemoriasAsignablesConvocatoria(this.idConvocatoria)
      .subscribe(
        (response: SgiRestListResult<Memoria>) => {
          this.memorias = response.items;

          // Eliminar de la lista las memorias que ya están asignadas
          this.memorias = this.memorias.filter(
            (memoria: Memoria) => {
              return (!this.memoriasAsignadas.some(e => e.id === memoria.id));
            }
          );

          this.filteredMemorias = this.formGroup.controls.memoria.valueChanges
            .pipe(
              startWith(''),
              map(value => this._filterMemoria(value))
            );
        },
        () => {
          this.snackBarService.showError('eti.convocatoriaReunion.formulario.asignacionMemorias.memoria.error.cargar');
        }
      ));

    this.logger.debug(ConvocatoriaReunionAsignacionMemoriasComponent.name,
      'getMemoriasAsignablesConvocatoria()',
      'end');
  }

  /**
   * Recupera un listado de las memorias asignables si la convocatoria es de tipo seguimiento.
   */
  private getMemoriasAsignablesConvocatoriaSeguimiento(): void {
    this.logger.debug(ConvocatoriaReunionAsignacionMemoriasComponent.name,
      'getMemoriasAsignablesConvocatoriaSeguimiento',
      'start');

    this.subscriptions.push(this.memoriaService
      .findAllAsignablesTipoConvocatoriaSeguimiento({ filters: this.filterMemoriasAsignables })
      .subscribe(
        (response: SgiRestListResult<Memoria>) => {
          this.memorias = response.items;

          // Eliminar de la lista las memorias que ya están asignadas
          this.memorias = this.memorias.filter(
            (memoria: Memoria) => {
              return (!this.memoriasAsignadas.some(e => e.id === memoria.id));
            }
          );

          this.filteredMemorias = this.formGroup.controls.memoria.valueChanges
            .pipe(
              startWith(''),
              map(value => this._filterMemoria(value))
            );
        },
        () => {
          this.snackBarService.showError('eti.convocatoriaReunion.formulario.asignacionMemorias.memoria.error.cargar');
        }
      ));

    this.logger.debug(ConvocatoriaReunionAsignacionMemoriasComponent.name,
      'getMemoriasAsignablesConvocatoriaSeguimiento()',
      'end');
  }

  /**
   * Recupera un listado de las memorias asignables si la convocatoria es de tipo ordinaria / extraordinaria.
   */
  private getMemoriasAsignablesConvocatoriaOrdExt(): void {
    this.logger.debug(ConvocatoriaReunionAsignacionMemoriasComponent.name,
      'getMemoriasAsignablesConvocatoriaOrdExt',
      'start');

    this.subscriptions.push(this.memoriaService
      .findAllAsignablesTipoConvocatoriaOrdExt({ filters: this.filterMemoriasAsignables })
      .subscribe(
        (response: SgiRestListResult<Memoria>) => {
          this.memorias = response.items;

          // Eliminar de la lista las memorias que ya están asignadas
          this.memorias = this.memorias.filter(
            (memoria: Memoria) => {
              return (!this.memoriasAsignadas.some(e => e.id === memoria.id));
            }
          );

          this.filteredMemorias = this.formGroup.controls.memoria.valueChanges
            .pipe(
              startWith(''),
              map(value => this._filterMemoria(value))
            );
        },
        () => {
          this.snackBarService.showError('eti.convocatoriaReunion.formulario.asignacionMemorias.memoria.error.cargar');
        }
      ));

    this.logger.debug(ConvocatoriaReunionAsignacionMemoriasComponent.name,
      'getMemoriasAsignablesConvocatoriaOrdExt()',
      'end');
  }


  /**
   * Recupera un listado de los evaluadores que hay en el sistema.
   */
  private getEvaluadores(): void {
    this.logger.debug(ConvocatoriaReunionAsignacionMemoriasComponent.name,
      'getEvaluadores()',
      'start');

    const evaluadoresMemoriaSeleccionada$ =
      this.formGroup.controls.memoria.valueChanges.pipe(
        switchMap((memoria: Memoria | string) => {
          if (typeof memoria === 'string' || !memoria.id) {
            return of([]);
          }

          return this.evaluadorService
            .findAllMemoriasAsignablesConvocatoria(memoria.comite.id, memoria.id)
            .pipe(
              switchMap((response) => {

                if (response.items) {
                  const evaluadores = response.items;

                  const personaRefsEvaluadores = evaluadores.map((convocante: IEvaluador) => convocante.personaRef);

                  const evaluadoresWithDatosPersona$ = this.PersonafisicaService.findByPersonasRefs(personaRefsEvaluadores).pipe(
                    map((result: SgiRestListResult<Persona>) => {
                      const personas = result.items;

                      evaluadores.forEach((evaluador: IEvaluador) => {
                        const datosPersonaEvaluador = personas.find((persona: Persona) => evaluador.personaRef === persona.personaRef);
                        evaluador.nombre = datosPersonaEvaluador?.nombre;
                        evaluador.primerApellido = datosPersonaEvaluador?.primerApellido;
                        evaluador.segundoApellido = datosPersonaEvaluador?.segundoApellido;
                      });

                      return evaluadores;
                    }));

                  return evaluadoresWithDatosPersona$;
                } else {
                  return of([]);
                }
              })
            );
        }),
        shareReplay(1));

    this.subscriptions.push(evaluadoresMemoriaSeleccionada$.subscribe(
      (evaluadores: IEvaluador[]) => {
        this.evaluadores = evaluadores;

        this.filteredEvaluadoresEvaluador1 = this.formGroup.controls.evaluador1.valueChanges
          .pipe(
            startWith(''),
            map(value => this._filterEvaluador(value))
          );
      },
      () => {
        this.snackBarService.showError('eti.convocatoriaReunion.formulario.asignacionMemorias.evaluador1.error.cargar');
      }
    ));

    this.subscriptions.push(evaluadoresMemoriaSeleccionada$.subscribe(
      (evaluadores: IEvaluador[]) => {
        this.evaluadores = evaluadores;

        this.filteredEvaluadoresEvaluador2 = this.formGroup.controls.evaluador2.valueChanges
          .pipe(
            startWith(''),
            map(value => this._filterEvaluador(value))
          );
      },
      () => {
        this.snackBarService.showError('eti.convocatoriaReunion.formulario.asignacionMemorias.evaluador2.error.cargar');
      }
    ));

    this.logger.debug(ConvocatoriaReunionAsignacionMemoriasComponent.name,
      'getEvaluadores()',
      'end');
  }



  /**
   * Filtro de campo autocompletable memoria.
   * @param value value a filtrar (string o memoria).
   * @returns lista de memorias filtrada.
   */
  private _filterMemoria(value: string | Memoria): Memoria[] {
    if (!value) {
      return this.memorias;
    }

    let filterValue: string;
    if (typeof value === 'string') {
      filterValue = value.toLowerCase();
    } else {
      filterValue = (value.numReferencia + ' - ' + value.titulo).toLowerCase();
    }

    return this.memorias.filter
      (memoria => (memoria.numReferencia + ' - ' + memoria.titulo).toLowerCase().includes(filterValue));
  }

  /**
   * Filtro de campo autocompletable evaluador.
   * @param value value a filtrar (string o evaluador).
   * @returns lista de evaluadores filtrada.
   */
  private _filterEvaluador(value: string | IEvaluador): IEvaluador[] {
    if (!value) {
      return this.evaluadores;
    }

    let filterValue: string;
    if (typeof value === 'string') {
      filterValue = value.toLowerCase();
    } else {
      filterValue = (value.nombre + ' ' + value.primerApellido + ' ' + value.segundoApellido).toLowerCase();
    }

    return this.evaluadores.filter
      (evaluador =>
        (evaluador.nombre + ' ' + evaluador.primerApellido + ' ' + evaluador.segundoApellido).toLowerCase().includes(filterValue));
  }

  /**
   * Devuelve el nombre completo del evaluador
   * @param evaluador Evaluador
   *
   * @returns nombre completo del evaluador
   */
  getEvaluador(evaluador: IEvaluador): string {
    return evaluador ? evaluador.nombre + ' ' + evaluador.primerApellido + ' ' + evaluador.segundoApellido : '';
  }

  /**
   * Devuelve la referencia y el titulo de la memoria
   * @param memoria Memoria
   *
   * @returns referencia y titulo memoria
   */
  getMemoria(memoria: Memoria): string {
    return memoria ? (memoria.numReferencia + ' - ' + memoria.titulo) : '';
  }

  getDatosForm(): IEvaluacion {
    this.logger.debug(ConvocatoriaReunionAsignacionMemoriasComponent.name, 'getDatosFormulario()', 'start');
    const returnData = {
      id: null,
      memoria: FormGroupUtil.getValue(this.formGroup, 'memoria'),
      comite: null,
      convocatoriaReunion: null,
      tipoEvaluacion: null,
      version: null,
      dictamen: null,
      evaluador1: FormGroupUtil.getValue(this.formGroup, 'evaluador1'),
      evaluador2: FormGroupUtil.getValue(this.formGroup, 'evaluador2'),
      fechaDictamen: null,
      esRevMinima: null,
      activo: null,
    };
    this.logger.debug(ConvocatoriaReunionAsignacionMemoriasComponent.name, 'getDatosFormulario()', 'end');
    return returnData;
  }

  /**
   * Confirmar asignación
   */
  onAsignarmemoria(): void {
    this.logger.debug(ConvocatoriaReunionAsignacionMemoriasComponent.name, 'onAsignarmemoria()', 'start');

    if (FormGroupUtil.valid(this.formGroup)) {
      this.dialogRef.close(this.getDatosForm());
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM_GROUP);
    }

    this.logger.debug(ConvocatoriaReunionAsignacionMemoriasComponent.name, 'onAsignarmemoria()', 'end');
  }

  onCancel(): void {
    this.logger.debug(ConvocatoriaReunionAsignacionMemoriasComponent.name, 'onCancel()', 'start');
    this.dialogRef.close();
    this.logger.debug(ConvocatoriaReunionAsignacionMemoriasComponent.name, 'onCancel()', 'end');
  }



}
