import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { Component, OnInit } from '@angular/core';
import { ConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';
import { AbstractTabComponent } from '@core/component/abstract-tab.component';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { NGXLogger } from 'ngx-logger';
import { SnackBarService } from '@core/services/snack-bar.service';
import { Observable, of, zip } from 'rxjs';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { NullIdValidador } from '@core/validators/null-id-validador';
import { HoraValidador } from '@core/validators/hora-validator';
import { MinutoValidador } from '@core/validators/minuto-validator';
import { Comite } from '@core/models/eti/comite';
import { ComiteService } from '@core/services/eti/comite.service';
import { TipoConvocatoriaReunion } from '@core/models/eti/tipo-convocatoria-reunion';
import { TipoConvocatoriaReunionService } from '@core/services/eti/tipo-convocatoria-reunion.service';
import { Evaluador } from '@core/models/eti/evaluador';
import { EvaluadorService } from '@core/services/eti/evaluador.service';
import { switchMap, map, startWith } from 'rxjs/operators';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';
import { Persona } from '@core/models/sgp/persona';
import { SgiRestFilterType, SgiRestListResult } from '@sgi/framework/http';

const MSG_ERROR_LOAD_COMITES = marker('eti.convocatoriaReunion.formulario.datosGenerales.comite.error.cargar');
const MSG_ERROR_LOAD_TIPOS_CONVOCATORIA = marker('eti.convocatoriaReunion.formulario.datosGenerales.tipoConvocatoriaReunion.error.cargar');
const MSG_ERROR_LOAD_CONVOCANTES = marker('eti.convocatoriaReunion.formulario.datosGenerales.convocantes.error.cargar');

@Component({
  selector: 'sgi-convocatoria-reunion-datos-generales',
  templateUrl: './convocatoria-reunion-datos-generales.component.html',
  styleUrls: ['./convocatoria-reunion-datos-generales.component.scss']
})
export class ConvocatoriaReunionDatosGeneralesComponent extends AbstractTabComponent<ConvocatoriaReunion> implements OnInit {
  FormGroupUtil = FormGroupUtil;
  fxFlexProperties: FxFlexProperties;
  fxFlexPropertiesInline: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  comites: Comite[];
  convocantes: Evaluador[];
  tiposConvocatoriaReunion: TipoConvocatoriaReunion[];

  filteredComites: Observable<Comite[]>;
  filteredTiposConvocatoriaReunion: Observable<TipoConvocatoriaReunion[]>;

  filterActivo = {
    field: 'activo',
    type: SgiRestFilterType.EQUALS,
    value: 'true'
  };

  constructor(
    protected readonly logger: NGXLogger,
    private readonly comiteService: ComiteService,
    private readonly evaluadorService: EvaluadorService,
    private readonly tipoConvocatoriaReunionService: TipoConvocatoriaReunionService,
    private readonly snackBarService: SnackBarService,
    private readonly usuarioService: PersonaFisicaService
  ) {
    super(logger);
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(32.7%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxFlexPropertiesInline = new FxFlexProperties();
    this.fxFlexPropertiesInline.sm = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.md = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.order = '3';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit() {
    super.ngOnInit();
    this.logger.debug(ConvocatoriaReunionDatosGeneralesComponent.name, 'ngOnInit()', 'start');
    this.comites = [];
    this.tiposConvocatoriaReunion = [];
    this.convocantes = [];

    // Inicializa los combos
    this.getComites();
    this.getTiposConvocatoriaReunion();
    this.getConvocantesComite();

    this.logger.debug(ConvocatoriaReunionDatosGeneralesComponent.name, 'ngOnInit()', 'end');
  }

  /**
   * Recupera un listado de los comites que hay en el sistema.
   */
  private getComites(): void {
    this.logger.debug(ConvocatoriaReunionDatosGeneralesComponent.name,
      'getComites()',
      'start');

    const comitesSelectSubscription = this.comiteService
      .findAll({ filters: [this.filterActivo] })
      .subscribe(
        (response: SgiRestListResult<Comite>) => {
          this.comites = response.items;

          this.filteredComites = this.formGroup.controls.comite.valueChanges
            .pipe(
              startWith(''),
              map(value => this._filterComite(value))
            );
        },
        () => {
          this.snackBarService.showError(MSG_ERROR_LOAD_COMITES);
        }
      );

    this.subscripciones.push(comitesSelectSubscription);

    this.logger.debug(ConvocatoriaReunionDatosGeneralesComponent.name,
      'getComites()',
      'end');
  }

  /**
   * Recupera un listado de los tipos de convocatoria reunion que hay en el sistema.
   */
  private getTiposConvocatoriaReunion(): void {
    this.logger.debug(ConvocatoriaReunionDatosGeneralesComponent.name,
      'getTiposConvocatoriaReunion()',
      'start');

    const tipoConvocatoriaSelectReunionSubscription = this.tipoConvocatoriaReunionService
      .findAll({ filters: [this.filterActivo] })
      .subscribe(
        (response: SgiRestListResult<TipoConvocatoriaReunion>) => {
          this.tiposConvocatoriaReunion = response.items;

          this.filteredTiposConvocatoriaReunion = this.formGroup.controls.tipoConvocatoriaReunion.valueChanges
            .pipe(
              startWith(''),
              map(value => this._filterTipoConvocatoriaReunion(value))
            );
        },
        () => {
          this.snackBarService.showError(MSG_ERROR_LOAD_TIPOS_CONVOCATORIA);
        });

    this.subscripciones.push(tipoConvocatoriaSelectReunionSubscription);

    this.logger.debug(ConvocatoriaReunionDatosGeneralesComponent.name,
      'getTiposConvocatoriaReunion()',
      'end');
  }


  /**
   * Recupera el listado de convocantes correspondiente al comite seleccionado.
   */
  private getConvocantesComite(): void {
    this.logger.debug(ConvocatoriaReunionDatosGeneralesComponent.name,
      'getConvocantesComite()',
      'start');

    const convocantesSelectSubscription =
      this.formGroup.controls.comite.valueChanges.pipe(
        switchMap((comite: Comite | string) => {
          if (typeof comite === 'string' || !comite.id) {
            return of([]);
          }

          const filterComite = {
            field: 'comite.id',
            type: SgiRestFilterType.EQUALS,
            value: comite.id.toString()
          };

          return this.evaluadorService
            .findAll({ filters: [filterComite] })
            .pipe(
              switchMap((response: SgiRestListResult<Evaluador>) => {
                const convocantes = response.items;

                const convocantesInfoUsuario$: Observable<Evaluador>[] = [];

                convocantes.forEach(convocante => {
                  const convocanteInfoUsuario$ = this.usuarioService.findById(convocante.personaRef).pipe(
                    map((usuario: Persona) => {
                      convocante.nombre = usuario.nombre;
                      convocante.primerApellido = usuario.primerApellido;
                      convocante.segundoApellido = usuario.segundoApellido;
                      return convocante;
                    }));

                  convocantesInfoUsuario$.push(convocanteInfoUsuario$);
                });

                return zip(...convocantesInfoUsuario$);
              })
            );
        })
      ).subscribe(
        (convocantes: Evaluador[]) => {
          this.convocantes = convocantes;
          this.formGroup.controls.convocantes.setValue(this.convocantes);
        },
        () => {
          this.snackBarService.showError(MSG_ERROR_LOAD_CONVOCANTES);
        }
      );


    this.subscripciones.push(convocantesSelectSubscription);

    this.logger.debug(ConvocatoriaReunionDatosGeneralesComponent.name,
      'getConvocantesComite()',
      'end');
  }

  /**
   * Filtro de campo autocompletable comite.
   * @param value value a filtrar (string o Comite.
   * @returns lista de comites filtrada.
   */
  private _filterComite(value: string | Comite): Comite[] {
    if (!value) {
      return this.comites;
    }

    let filterValue: string;
    if (typeof value === 'string') {
      filterValue = value.toLowerCase();
    } else {
      filterValue = value.comite.toLowerCase();
    }

    return this.comites.filter
      (comite => comite.comite.toLowerCase().includes(filterValue));
  }

  /**
   * Filtro de campo autocompletable tipo convocatoria reunion.
   * @param value value a filtrar (string o TipoConvocatoriaReunion).
   * @returns lista de tipos de convocatoria reunion filtrada.
   */
  private _filterTipoConvocatoriaReunion(value: string | TipoConvocatoriaReunion): TipoConvocatoriaReunion[] {
    if (!value) {
      return this.tiposConvocatoriaReunion;
    }

    let filterValue: string;
    if (typeof value === 'string') {
      filterValue = value.toLowerCase();
    } else {
      filterValue = value.nombre.toLowerCase();
    }

    return this.tiposConvocatoriaReunion.filter
      (tipoConvocatoriaReunion => tipoConvocatoriaReunion.nombre.toLowerCase().includes(filterValue));
  }

  /**
   * Devuelve el nombre del comite
   * @param comite comite
   *
   * @returns nombre del comite
   */
  getComite(comite: Comite): string {
    return comite?.comite;
  }

  /**
   * Devuelve el nombre del tipo de convocatoria reunion
   * @param tipoConvocatoriaReunion tipo convocatoria reunion
   *
   * @returns nombre del tipo de convocatoria reunion
   */
  getTipoConvocatoriaReunion(tipoConvocatoriaReunion: TipoConvocatoriaReunion): string {
    return tipoConvocatoriaReunion?.nombre;
  }

  crearFormGroup(): FormGroup {
    this.logger.debug(ConvocatoriaReunionDatosGeneralesComponent.name, 'crearFormGroup()', 'start');
    const formGroup = new FormGroup({
      comite: new FormControl(null, [new NullIdValidador().isValid()]),
      fechaEvaluacion: new FormControl(null, [Validators.required]),
      fechaLimite: new FormControl(null, [Validators.required]),
      tipoConvocatoriaReunion: new FormControl(null, [new NullIdValidador().isValid()]),
      horaInicio: new FormControl(null, [new HoraValidador().isValid()]),
      minutoInicio: new FormControl(null, [new MinutoValidador().isValid()]),
      lugar: new FormControl(null, [Validators.required]),
      ordenDia: new FormControl(null, [Validators.required]),
      convocantes: new FormControl(null),
    });
    this.logger.debug(ConvocatoriaReunionDatosGeneralesComponent.name, 'crearFormGroup()', 'end');
    return formGroup;
  }


  getDatosFormulario(): ConvocatoriaReunion {
    this.logger.debug(ConvocatoriaReunionDatosGeneralesComponent.name, 'getDatosFormulario()', 'start');
    const convocatoriaReunion = this.datosFormulario;
    convocatoriaReunion.comite = FormGroupUtil.getValue(this.formGroup, 'comite');
    convocatoriaReunion.fechaEvaluacion = FormGroupUtil.getValue(this.formGroup, 'fechaEvaluacion');
    convocatoriaReunion.fechaLimite = FormGroupUtil.getValue(this.formGroup, 'fechaLimite');
    convocatoriaReunion.tipoConvocatoriaReunion = FormGroupUtil.getValue(this.formGroup, 'tipoConvocatoriaReunion');
    convocatoriaReunion.horaInicio = FormGroupUtil.getValue(this.formGroup, 'horaInicio');
    convocatoriaReunion.minutoInicio = FormGroupUtil.getValue(this.formGroup, 'minutoInicio');
    convocatoriaReunion.lugar = FormGroupUtil.getValue(this.formGroup, 'lugar');
    convocatoriaReunion.ordenDia = FormGroupUtil.getValue(this.formGroup, 'ordenDia');
    convocatoriaReunion.activo = true;

    this.logger.debug(ConvocatoriaReunionDatosGeneralesComponent.name, 'getDatosFormulario()', 'end');
    return convocatoriaReunion;
  }

  /**
   * Recupera los convocantes seleccionados en el formulario
   *
   * @return lista de convocantes
   */
  getDatosConvocantesFormulario(): Evaluador[] {
    this.logger.debug(ConvocatoriaReunionDatosGeneralesComponent.name, 'getDatosConvocantesFormulario()', 'end');
    const convocantes = FormGroupUtil.getValue(this.formGroup, 'convocantes');

    this.logger.debug(ConvocatoriaReunionDatosGeneralesComponent.name, 'getDatosConvocantesFormulario()', 'end');

    return convocantes;
  }

}
