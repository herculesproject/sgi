import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogData } from '@block/dialog/dialog.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { IEvaluador } from '@core/models/eti/evaluador';
import { IMemoria } from '@core/models/eti/memoria';
import { IPersona } from '@core/models/sgp/persona';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { EvaluadorService } from '@core/services/eti/evaluador.service';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { NullIdValidador } from '@core/validators/null-id-validador';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { Observable, of, Subscription } from 'rxjs';
import { map, shareReplay, startWith, switchMap } from 'rxjs/operators';

const MSG_ERROR_FORM_GROUP = marker('error.form-group');
const MSG_ERROR_LOAD = marker('error.load');
const MSG_ERROR_EVALUADOR_REPETIDO = marker('error.eti.convocatoria-reunion.memoria.evaluador.duplicate');
const MEMORIA_EVALUADOR1_KEY = marker('eti.convocatoria-reunion.memoria.evaludador-1');
const MEMORIA_EVALUADOR2_KEY = marker('eti.convocatoria-reunion.memoria.evaludador-2');
const MEMORIA_KEY = marker('eti.memoria');
const TITLE_NEW_ENTITY = marker('title.new.entity');
@Component({
  selector: 'sgi-convocatoria-reunion-asignacion-memorias-modal',
  templateUrl: './convocatoria-reunion-asignacion-memorias-modal.component.html',
  styleUrls: ['./convocatoria-reunion-asignacion-memorias-modal.component.scss']
})
export class ConvocatoriaReunionAsignacionMemoriasModalComponent implements OnInit, OnDestroy {
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  FormGroupUtil = FormGroupUtil;
  formGroup: FormGroup;

  evaluadores: IEvaluador[];
  memorias: IMemoria[];

  filteredEvaluadoresEvaluador1: Observable<IEvaluador[]>;
  filteredEvaluadoresEvaluador2: Observable<IEvaluador[]>;
  filteredMemorias: Observable<IMemoria[]>;

  private subscriptions: Subscription[];

  idConvocatoria: number;
  isTipoConvocatoriaSeguimiento: boolean;
  filterData: { idComite: number, idTipoConvocatoria: number, fechaLimite: DateTime };
  filterMemoriasAsignables: SgiRestFilter;
  memoriasAsignadas: IMemoria[];
  evaluacion: IEvaluacion;

  isEdit = false;

  msgParamEvaludador1Entity = {};
  msgParamEvaludador2Entity = {};
  msgParamMemoriaEntity = {};
  title: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    private readonly logger: NGXLogger,
    private readonly dialogRef: MatDialogRef<ConvocatoriaReunionAsignacionMemoriasModalComponent>,
    private readonly evaluadorService: EvaluadorService,
    private readonly memoriaService: MemoriaService,
    private readonly PersonafisicaService: PersonaFisicaService,
    private readonly snackBarService: SnackBarService,
    @Inject(MAT_DIALOG_DATA) public data: DialogData,
    private readonly translate: TranslateService
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
    if (params) {
      this.idConvocatoria = params.idConvocatoria;
      this.memoriasAsignadas = params.memoriasAsignadas;
      this.filterData = params.filterMemoriasAsignables;
      this.evaluacion = params.evaluacion;
    }
    this.buildFilter();
  }

  ngOnInit(): void {
    this.initFormGroup();
    this.setupI18N();

    if (this.idConvocatoria) {
      this.loadMemoriasAsignablesConvocatoria();
    } else {
      if (this.isTipoConvocatoriaSeguimiento) {
        this.loadMemoriasAsignablesConvocatoriaSeguimiento();
      } else {
        this.loadMemoriasAsignablesConvocatoriaOrdExt();
      }
    }

    this.isEdit = this.evaluacion?.memoria ? true : false;

    this.loadEvaluadores();
  }

  private setupI18N(): void {
    this.translate.get(
      MEMORIA_EVALUADOR1_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEvaludador1Entity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      MEMORIA_EVALUADOR2_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEvaludador2Entity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      MEMORIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamMemoriaEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });

    if (this.evaluacion.memoria) {
      this.translate.get(
        MEMORIA_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        MEMORIA_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            TITLE_NEW_ENTITY,
            { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
          );
        })
      ).subscribe((value) => this.title = value);
    }
  }

  /**
   * Inicializa el formGroup
   */
  private initFormGroup() {
    this.formGroup = new FormGroup({
      memoria: new FormControl(this.evaluacion.memoria, [Validators.required]),
      evaluador1: new FormControl(this.evaluacion.evaluador1),
      evaluador2: new FormControl(this.evaluacion.evaluador2),
    });
  }

  ngOnDestroy(): void {
    this.subscriptions?.forEach(x => x.unsubscribe());
  }

  /**
   * Crear el FormGroup
   *
   */
  createFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      memoria: new FormControl(null, [new NullIdValidador().isValid()]),
      evaluador1: new FormControl(null, [Validators.required]),
      evaluador2: new FormControl(null, [Validators.required])
    });
    return formGroup;
  }

  /**
   * Construye los filtros necesarios para la búsqueda de las memorias asignables.
   *
   */
  private buildFilter(): void {
    if (this.filterData && this.filterData.idComite &&
      this.filterData.idTipoConvocatoria &&
      this.filterData.fechaLimite) {
      this.isTipoConvocatoriaSeguimiento = (this.filterData.idTipoConvocatoria === 3) ? true : false;

      this.filterMemoriasAsignables = new RSQLSgiRestFilter('comite.id', SgiRestFilterOperator.EQUALS, this.filterData.idComite.toString())
        .and('fechaEnvioSecretaria', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(this.filterData.fechaLimite));
    }
    else {
      this.filterMemoriasAsignables = undefined;
    }
  }

  /**
   * Recupera un listado de los memorias asignables a la convocatoria.
   */
  private loadMemoriasAsignablesConvocatoria(): void {
    this.subscriptions.push(this.memoriaService
      .findAllMemoriasAsignablesConvocatoria(this.idConvocatoria)
      .subscribe(
        (response: SgiRestListResult<IMemoria>) => {
          this.memorias = response.items;

          // Eliminar de la lista las memorias que ya están asignadas
          this.memorias = this.memorias.filter(
            (memoria: IMemoria) => {
              return (!this.memoriasAsignadas.some(e => e.id === memoria.id));
            }
          );

          this.filteredMemorias = this.formGroup.controls.memoria.valueChanges
            .pipe(
              startWith(''),
              map(value => this._filterMemoria(value))
            );
        },
        (error) => {
          this.logger.error(error);
          this.snackBarService.showError(MSG_ERROR_LOAD);
        }
      ));
  }

  /**
   * Recupera un listado de las memorias asignables si la convocatoria es de tipo seguimiento.
   */
  private loadMemoriasAsignablesConvocatoriaSeguimiento(): void {
    this.subscriptions.push(this.memoriaService
      .findAllAsignablesTipoConvocatoriaSeguimiento({ filter: this.filterMemoriasAsignables })
      .subscribe(
        (response: SgiRestListResult<IMemoria>) => {
          this.memorias = response.items;

          // Eliminar de la lista las memorias que ya están asignadas
          this.memorias = this.memorias.filter(
            (memoria: IMemoria) => {
              return (!this.memoriasAsignadas.some(e => e.id === memoria.id));
            }
          );

          this.filteredMemorias = this.formGroup.controls.memoria.valueChanges
            .pipe(
              startWith(''),
              map(value => this._filterMemoria(value))
            );
        },
        (error) => {
          this.logger.error(error);
          this.snackBarService.showError(MSG_ERROR_LOAD);
        }
      ));
  }

  /**
   * Recupera un listado de las memorias asignables si la convocatoria es de tipo ordinaria / extraordinaria.
   */
  private loadMemoriasAsignablesConvocatoriaOrdExt(): void {
    this.subscriptions.push(this.memoriaService
      .findAllAsignablesTipoConvocatoriaOrdExt({ filter: this.filterMemoriasAsignables })
      .subscribe(
        (response: SgiRestListResult<IMemoria>) => {
          this.memorias = response.items;

          // Eliminar de la lista las memorias que ya están asignadas
          this.memorias = this.memorias.filter(
            (memoria: IMemoria) => {
              return (!this.memoriasAsignadas.some(e => e.id === memoria.id));
            }
          );

          this.filteredMemorias = this.formGroup.controls.memoria.valueChanges
            .pipe(
              startWith(''),
              map(value => this._filterMemoria(value))
            );
        },
        (error) => {
          this.logger.error(error);
          this.snackBarService.showError(MSG_ERROR_LOAD);
        }
      ));
  }

  /**
   * Recupera un listado de los evaluadores que hay en el sistema.
   */
  private loadEvaluadores(): void {
    const evaluadoresMemoriaSeleccionada$ =
      this.formGroup.controls.memoria.valueChanges.pipe(
        switchMap((memoria: IMemoria | string) => {
          if (typeof memoria === 'string' || !memoria.id) {
            return of([]);
          }

          return this.evaluadorService
            .findAllMemoriasAsignablesConvocatoria(memoria.comite.id, memoria.id)
            .pipe(
              switchMap((response) => {

                if (response.items) {
                  const evaluadores = response.items;

                  const personaRefsEvaluadores = evaluadores.map((convocante: IEvaluador) => convocante.persona.personaRef);

                  if (personaRefsEvaluadores.length === 0) {
                    return of([]);
                  }

                  const evaluadoresWithDatosPersona$ = this.PersonafisicaService.findByPersonasRefs(personaRefsEvaluadores).pipe(
                    map((result: SgiRestListResult<IPersona>) => {
                      const personas = result.items;

                      evaluadores.forEach((evaluador: IEvaluador) => {
                        const datosPersonaEvaluador = personas.find(
                          (persona: IPersona) => evaluador.persona.personaRef === persona.personaRef
                        );
                        evaluador.persona = datosPersonaEvaluador;
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
      (error) => {
        this.logger.error(error);
        this.snackBarService.showError(MSG_ERROR_LOAD);
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
      (error) => {
        this.logger.error(error);
        this.snackBarService.showError(MSG_ERROR_LOAD);
      }
    ));
  }

  /**
   * Filtro de campo autocompletable memoria.
   * @param value value a filtrar (string o memoria).
   * @returns lista de memorias filtrada.
   */
  private _filterMemoria(value: string | IMemoria): IMemoria[] {
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
      filterValue = (value.persona.nombre + ' ' + value.persona.primerApellido + ' ' + value.persona.segundoApellido).toLowerCase();
    }

    return this.evaluadores.filter
      (evaluador =>
        (evaluador.persona.nombre + ' ' + evaluador.persona.primerApellido + ' ' + evaluador.persona.segundoApellido)
          .toLowerCase().includes(filterValue));
  }

  /**
   * Devuelve el nombre completo del evaluador
   * @param evaluador Evaluador
   *
   * @returns nombre completo del evaluador
   */
  getEvaluador(evaluador: IEvaluador): string {
    return evaluador ? evaluador.persona.nombre + ' ' + evaluador.persona.primerApellido + ' ' + evaluador.persona.segundoApellido : '';
  }

  /**
   * Devuelve la referencia y el titulo de la memoria
   * @param memoria Memoria
   *
   * @returns referencia y titulo memoria
   */
  getMemoria(memoria: IMemoria): string {
    return memoria ? (memoria.numReferencia + ' - ' + memoria.titulo) : '';
  }

  getDatosForm(): IEvaluacion {
    const convocatoriaReunion: IConvocatoriaReunion = {
      activo: null,
      anio: null,
      codigo: null,
      comite: null,
      fechaEnvio: null,
      fechaEvaluacion: null,
      fechaLimite: null,
      horaInicio: null,
      id: null,
      lugar: null,
      minutoInicio: null,
      numeroActa: null,
      ordenDia: null,
      tipoConvocatoriaReunion: null
    };

    this.evaluacion.memoria = FormGroupUtil.getValue(this.formGroup, 'memoria');
    this.evaluacion.convocatoriaReunion = convocatoriaReunion;
    this.evaluacion.evaluador1 = FormGroupUtil.getValue(this.formGroup, 'evaluador1');
    this.evaluacion.evaluador2 = FormGroupUtil.getValue(this.formGroup, 'evaluador2');

    return this.evaluacion;
  }

  /**
   * Confirmar asignación
   */
  onAsignarmemoria(): void {
    const evaluacion: IEvaluacion = this.getDatosForm();

    if (evaluacion.evaluador1 === evaluacion.evaluador2) {
      this.snackBarService.showError(MSG_ERROR_EVALUADOR_REPETIDO);
      return;
    }

    if (FormGroupUtil.valid(this.formGroup)) {
      this.dialogRef.close(evaluacion);
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM_GROUP);
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }

}
