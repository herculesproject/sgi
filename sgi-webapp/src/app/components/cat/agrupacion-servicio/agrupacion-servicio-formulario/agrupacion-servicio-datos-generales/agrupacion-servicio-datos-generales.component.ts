import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { NGXLogger } from 'ngx-logger';

import { Observable, Subscription } from 'rxjs';
import { startWith, map } from 'rxjs/operators';

import { SeccionService } from '@core/services/seccion.service';

import { FxFlexProperties } from '@core/models/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/flexLayout/fx-layout-properties';

import { Seccion } from '@core/models/seccion';
import { Servicio } from '@core/models/servicio';
import { FormGroupUtil } from '@shared/config/form-group-util';


@Component({
  selector: 'app-agrupacion-servicio-datos-generales',
  templateUrl: './agrupacion-servicio-datos-generales.component.html',
  styleUrls: ['./agrupacion-servicio-datos-generales.component.scss']
})
export class AgrupacionServicioDatosGeneralesComponent implements OnInit, OnDestroy {

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  datosGeneralesFormGroup: FormGroup;

  FormGroupUtil = FormGroupUtil;

  seccionListado: Seccion[];
  filteredSecciones: Observable<Seccion[]>;

  seccionesSubscription: Subscription;

  constructor(
    private readonly logger: NGXLogger,
    private seccionService: SeccionService) {

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
    this.logger.debug(AgrupacionServicioDatosGeneralesComponent.name, 'ngOnInit()', 'start');

    this.getSecciones();

    this.logger.debug(AgrupacionServicioDatosGeneralesComponent.name, 'ngOnInit()', 'end');
  }

  /**
   * Crea el formulario de datos generales.
   * @returns formulario.
   */
  createGroup(): FormGroup {
    this.logger.debug(AgrupacionServicioDatosGeneralesComponent.name, 'createGroup()', 'start');

    this.datosGeneralesFormGroup = new FormGroup({
      nombre: new FormControl('', [
        Validators.maxLength(100),
        Validators.required
      ]),
      abreviatura: new FormControl('', [
        Validators.maxLength(10),
        Validators.minLength(2),
        Validators.required
      ]),
      contacto: new FormControl('', [
        Validators.maxLength(100),
        Validators.required
      ]),
      seccion: new FormControl('', [
        Validators.required
      ]),
    });

    this.logger.debug(AgrupacionServicioDatosGeneralesComponent.name, 'createGroup()', 'end');

    return this.datosGeneralesFormGroup;
  }

  /**
   * Setea los datos del servicio en el formulario de datos generales.
   * @param servicio servicio a actualizar.
   */
  setDatosForm(servicio: Servicio) {
    this.logger.debug(AgrupacionServicioDatosGeneralesComponent.name, 'setDatosForm(servicio: Servicio)', 'start');

    FormGroupUtil.setValue(
      this.datosGeneralesFormGroup,
      'nombre',
      servicio.nombre
    );

    FormGroupUtil.setValue(
      this.datosGeneralesFormGroup,
      'abreviatura',
      servicio.abreviatura
    );

    FormGroupUtil.setValue(
      this.datosGeneralesFormGroup,
      'contacto',
      servicio.contacto
    );
    FormGroupUtil.setValue(
      this.datosGeneralesFormGroup,
      'seccion',
      servicio.seccion
    );

    this.logger.debug(AgrupacionServicioDatosGeneralesComponent.name, 'setDatosForm(servicio: Servicio)', 'end');
  }

  /**
   * Recupera los datos del formulario datos generales de un servicio.
   * @param servicio servicio a actualizar/crear
   * @returns servicio con sus datos generales rellenos.
   */
  getDatosForm(servicio: Servicio): Servicio {
    this.logger.debug(AgrupacionServicioDatosGeneralesComponent.name,
      'getDatosForm()',
      'start');

    servicio.nombre = FormGroupUtil.getValue(
      this.datosGeneralesFormGroup,
      'nombre'
    );

    servicio.abreviatura = FormGroupUtil.getValue(
      this.datosGeneralesFormGroup,
      'abreviatura'
    );

    servicio.contacto = FormGroupUtil.getValue(
      this.datosGeneralesFormGroup,
      'contacto'
    );

    servicio.seccion = new Seccion();
    servicio.seccion = FormGroupUtil.getValue(
      this.datosGeneralesFormGroup,
      'seccion'
    );

    this.logger.debug(AgrupacionServicioDatosGeneralesComponent.name,
      'getDatosForm()',
      'end');

    return servicio;
  }



  /**
   * Recupera un listado de las secciones que hay en el sistema.
   */
  getSecciones(): void {
    this.logger.debug(AgrupacionServicioDatosGeneralesComponent.name,
      'getSecciones()',
      'start');

    this.seccionesSubscription = this.seccionService.findAll({}).subscribe(
      (response) => {
        this.seccionListado = response.items;

        this.filteredSecciones = this.datosGeneralesFormGroup.controls.seccion.valueChanges
          .pipe(
            startWith(''),
            map(value => this._filter(value))
          );
      });

    this.logger.debug(AgrupacionServicioDatosGeneralesComponent.name,
      'getSecciones()',
      'end');
  }


  /**
   * Devuelve el nombre de una seccion
   * @param seccion sección
   * @returns nombre de la sección
   */
  getSeccion(seccion: Seccion): string {

    return seccion.nombre;
  }


  /**
   * Filtro de campo autocompletable sección.
   * @param value value a filtrar (string o tipo sección).
   * @returns lista de secciones filtrada.
   */
  private _filter(value: string | Seccion): Seccion[] {
    let filterValue: string;
    if (typeof value === 'string') {
      filterValue = value.toLowerCase();
    } else {
      filterValue = value.nombre.toLowerCase();
    }

    return this.seccionListado.filter
      (seccion => seccion.nombre.toLowerCase().includes(filterValue));
  }

  ngOnDestroy(): void {
    this.logger.debug(AgrupacionServicioDatosGeneralesComponent.name,
      'ngOnDestroy()',
      'start');
    this.seccionesSubscription?.unsubscribe();

    this.logger.debug(AgrupacionServicioDatosGeneralesComponent.name,
      'ngOnDestroy()',
      'end');

  }
}
