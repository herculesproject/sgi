import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { NGXLogger } from 'ngx-logger';
import { Observable, Subscription } from 'rxjs';

import { AbstractTabComponent } from '@core/component/abstract-tab.component';

import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';

import { FormGroupUtil } from '@core/utils/form-group-util';

import { NullIdValidador } from '@core/validators/null-id-validador';
import { ActivatedRoute, Router } from '@angular/router';
import { EvaluadorService } from '@core/services/eti/evaluador.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { ComiteService } from '@core/services/eti/comite.service';
import { CargoComiteService } from '@core/services/eti/cargo-comite.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { IEvaluador } from '@core/models/eti/evaluador';
import { Comite } from '@core/models/eti/comite';
import { CargoComite } from '@core/models/eti/cargo-comite';
import { SgiRestListResult, SgiRestFilterType } from '@sgi/framework/http';
import { startWith, map } from 'rxjs/operators';
import { Persona } from '@core/models/sgp/persona';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';

const TEXT_USER_BUTTON = marker('eti.buscarUsuario.boton.buscar');
const TEXT_USER_TITLE = marker('eti.buscarUsuario.titulo');

const MSG_ERROR_INIT_ = marker('eti.evaluador.datosGenerales.error.init');
const MSG_NO_EXIST = marker('eti.evaluador.editar.no-encontrado');

@Component({
  selector: 'sgi-evaluador-datos-generales',
  templateUrl: './evaluador-datos-generales.component.html',
  styleUrls: ['./evaluador-datos-generales.component.scss']
})
export class EvaluadorDatosGeneralesComponent extends AbstractTabComponent<IEvaluador> implements OnInit {
  FormGroupUtil = FormGroupUtil;
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  comites: Comite[];
  cargosComite: CargoComite[];
  evaluador: IEvaluador;
  filteredComites: Observable<Comite[]>;
  filteredCargosComite: Observable<CargoComite[]>;

  @Output()
  selectorComite: EventEmitter<number> = new EventEmitter();

  @Output()
  selectorCargoComite: EventEmitter<number> = new EventEmitter();

  evaluadorServiceOneSubscritpion: Subscription;
  usuarioServiceOneSubscritpion: Subscription;

  textoUsuarioLabel = TEXT_USER_TITLE;
  textoUsuarioInput = TEXT_USER_TITLE;
  textoUsuarioButton = TEXT_USER_BUTTON;
  datosUsuarioEvaluadorEditado: string;

  persona: Persona;
  personaRef: string;

  constructor(
    protected readonly logger: NGXLogger,
    private readonly evaluadorService: EvaluadorService,
    private readonly usuarioService: PersonaService,
    private readonly comiteService: ComiteService,
    private readonly cargoComiteService: CargoComiteService,
    private readonly snackBarService: SnackBarService,
    private readonly route: ActivatedRoute,
    private readonly router: Router
  ) {
    super(logger);
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
    super.ngOnInit();
    this.logger.debug(EvaluadorDatosGeneralesComponent.name, 'ngOnInit()', 'start');

    this.cargarSelectorComites();
    this.cargarSelectorCargosComite();

    this.evaluador = {
      activo: null,
      cargoComite: null,
      comite: null,
      fechaAlta: null,
      fechaBaja: null,
      id: null,
      nombre: '',
      primerApellido: '',
      resumen: '',
      segundoApellido: '',
      personaRef: '',
      identificadorLetra: '',
      identificadorNumero: ''
    };

    this.logger.debug(EvaluadorDatosGeneralesComponent.name, 'ngOnInit()', 'end');
  }

  createFormGroup(): FormGroup {
    this.logger.debug(EvaluadorDatosGeneralesComponent.name, 'crearFormGroup()', 'start');
    const formGroup = new FormGroup({
      comite: new FormControl('', [new NullIdValidador().isValid()]),
      fechaAlta: new FormControl('', [Validators.required]),
      fechaBaja: new FormControl(''),
      cargoComite: new FormControl('', [new NullIdValidador().isValid()]),
      resumen: new FormControl(''),
    });
    this.logger.debug(EvaluadorDatosGeneralesComponent.name, 'crearFormGroup()', 'end');
    return formGroup;
  }

  crearObservable(): Observable<IEvaluador> {
    this.logger.debug(EvaluadorDatosGeneralesComponent.name, 'crearObservable()', 'start');
    const observable = this.evaluadorService.create(this.getDatosFormulario());
    this.logger.debug(EvaluadorDatosGeneralesComponent.name, 'crearObservable()', 'end');
    return observable;
  }

  cargarSelectorComites() {
    this.comites = [];
    this.suscripciones.push(
      this.comiteService.findAll().subscribe(
        (res: SgiRestListResult<Comite>) => {
          this.comites = res.items;
          this.logger.debug(EvaluadorDatosGeneralesComponent.name, 'cargarSelectorComites()', 'start');
          this.filteredComites = this.formGroup.controls.comite.valueChanges
            .pipe(
              startWith(''),
              map(value => this.filterComite(value))
            );
        },
        () => {
          this.snackBarService.showError(MSG_ERROR_INIT_);
          this.logger.debug(EvaluadorDatosGeneralesComponent.name, 'cargarSelectorComites()', 'end');
        }
      )
    );
  }

  cargarSelectorCargosComite() {
    this.cargosComite = [];
    this.suscripciones.push(
      this.cargoComiteService.findAll().subscribe(
        (res: SgiRestListResult<CargoComite>) => {
          this.cargosComite = res.items;
          this.logger.debug(EvaluadorDatosGeneralesComponent.name, 'cargarSelectorCargosComite()', 'start');
          this.filteredCargosComite = this.formGroup.controls.cargoComite.valueChanges
            .pipe(
              startWith(''),
              map(value => this.filterCargoComite(value))
            );
        },
        () => {
          this.snackBarService.showError(MSG_ERROR_INIT_);
          this.logger.debug(EvaluadorDatosGeneralesComponent.name, 'cargarSelectorCargosComite()', 'end');
        }
      )
    );
  }

  getDatosIniciales(): IEvaluador {
    this.logger.debug(EvaluadorDatosGeneralesComponent.name, 'getDatosIniciales()', 'start');
    const datos = {
      activo: true,
      cargoComite: null,
      comite: null,
      fechaAlta: null,
      fechaBaja: null,
      id: null,
      nombre: '',
      primerApellido: '',
      resumen: '',
      segundoApellido: '',
      personaRef: '',
      identificadorLetra: '',
      identificadorNumero: ''
    };
    this.logger.debug(EvaluadorDatosGeneralesComponent.name, 'getDatosIniciales()', 'end');
    return datos;
  }

  getDatosFormulario(): IEvaluador {
    this.logger.debug(EvaluadorDatosGeneralesComponent.name, 'getDatosIntroducidos()', 'start');
    const evaluador = this.datosFormulario;
    evaluador.comite = FormGroupUtil.getValue(this.formGroup, 'comite');
    evaluador.fechaAlta = FormGroupUtil.getValue(this.formGroup, 'fechaAlta');
    evaluador.fechaBaja = FormGroupUtil.getValue(this.formGroup, 'fechaBaja');
    evaluador.cargoComite = FormGroupUtil.getValue(this.formGroup, 'cargoComite');
    evaluador.resumen = FormGroupUtil.getValue(this.formGroup, 'resumen');
    evaluador.activo = true;
    if (this.personaRef) {
      evaluador.personaRef = this.personaRef;
    }

    this.logger.debug(EvaluadorDatosGeneralesComponent.name, 'getDatosIntroducidos()', 'end');
    return evaluador;
  }

  /**
   * Setea los datos en el formulario.
   * @params Acta
   */
  setDatosFormulario(evaluador: IEvaluador) {
    this.logger.debug(EvaluadorDatosGeneralesComponent.name, 'setDatosFormulario()', 'start');

    this.formGroup.controls.fechaAlta.setValue(evaluador.fechaAlta);
    this.formGroup.controls.fechaBaja.setValue(evaluador.fechaBaja);
    this.formGroup.controls.resumen.setValue(evaluador.resumen);
    this.formGroup.controls.comite.setValue(evaluador.comite);
    this.formGroup.controls.cargoComite.setValue(evaluador.cargoComite);

    this.selectComite(evaluador.comite);
    this.selectCargoComite(evaluador.cargoComite);
    if (evaluador.personaRef) {
      this.loadDatosUsuario(evaluador.personaRef);
    }


    this.logger.debug(EvaluadorDatosGeneralesComponent.name, 'setDatosFormulario()', 'end');
  }


  selectComite(comite: Comite | string) {
    this.logger.debug(EvaluadorDatosGeneralesComponent.name, 'selectComite(comite: Comite)', 'start');
    this.selectorComite.emit(comite ? (comite as Comite).id : null);
    this.logger.debug(EvaluadorDatosGeneralesComponent.name, 'selectComite(comite: Comite)', 'end');
  }

  selectCargoComite(cargoComite: CargoComite | string) {
    this.logger.debug(EvaluadorDatosGeneralesComponent.name, 'selectCargoComite(cargoComite: CargoComite)', 'start');
    this.selectorCargoComite.emit(cargoComite ? (cargoComite as CargoComite).id : null);
    this.logger.debug(EvaluadorDatosGeneralesComponent.name, 'selectCargoComite(cargoComite: CargoComite)', 'end');
  }

  loadDatosUsuario(personaRef: string): void {
    this.usuarioServiceOneSubscritpion = this.usuarioService
      .findById(personaRef)
      .subscribe(
        (persona: Persona) => {
          this.datosUsuarioEvaluadorEditado = persona.nombre + ' ' + persona.primerApellido + ' '
            + persona.segundoApellido + ' (' + persona.identificadorNumero + persona.identificadorLetra + ')';
        },
        () => {
          this.snackBarService.showError(MSG_NO_EXIST);
          this.router.navigate(['../'], { relativeTo: this.route });
          this.logger.debug(
            EvaluadorDatosGeneralesComponent.name,
            'loadDatosUsuario()',
            'end'
          );
        }
      );
  }

  /**
   * Devuelve el nombre de un comité.
   * @param comite comités
   * returns nombre comité
   */
  getComite(comite: Comite): string {

    return comite?.comite;

  }

  /**
   * Devuelve el nombre de un comité.
   * @param comite comités
   * returns nombre comité
   */
  getCargoComite(cargoComite: CargoComite): string {

    return cargoComite?.nombre;

  }

  /**
   * Filtro de campo autocompletable comité.
   * @param value value a filtrar (string o nombre comité).
   * @returns lista de comités filtrados.
   */
  private filterComite(value: string | Comite): Comite[] {
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
   * Filtro de campo autocompletable cargo comité.
   * @param value value a filtrar (string o nombre cargo comité).
   * @returns lista de cargos comité filtrados.
   */
  private filterCargoComite(value: string | CargoComite): CargoComite[] {
    let filterValue: string;
    if (typeof value === 'string') {
      filterValue = value.toLowerCase();
    } else {
      filterValue = value.nombre.toLowerCase();
    }

    return this.cargosComite.filter
      (cargoComite => cargoComite.nombre.toLowerCase().includes(filterValue));
  }

  /**
   * Setea el persona seleccionado a través del componente
   * @param personaRef referencia del persona seleccionado
   */
  public setUsuario(personaRef: string) {
    this.personaRef = personaRef;
  }

}
