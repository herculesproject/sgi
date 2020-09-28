import { Component, OnInit, ViewChild } from '@angular/core';
import { MatAutocompleteTrigger } from '@angular/material/autocomplete';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { IPeticionEvaluacion } from '@core/models/eti/peticion-evaluacion';
import { ITipoActividad } from '@core/models/eti/tipo-actividad';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { TipoActividadService } from '@core/services/eti/tipo-actividad.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { NGXLogger } from 'ngx-logger';
import { Observable, Subscription } from 'rxjs';
import { map, startWith } from 'rxjs/operators';

import { PeticionEvaluacionActionService } from '../../peticion-evaluacion.action.service';

@Component({
  selector: 'sgi-peticion-evaluacion-datos-generales',
  templateUrl: './peticion-evaluacion-datos-generales.component.html',
  styleUrls: ['./peticion-evaluacion-datos-generales.component.scss']
})
export class PeticionEvaluacionDatosGeneralesComponent extends FormFragmentComponent<IPeticionEvaluacion> implements OnInit {
  @ViewChild(MatAutocompleteTrigger) autocomplete: MatAutocompleteTrigger;

  FormGroupUtil = FormGroupUtil;
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexPropertiesInline: FxFlexProperties;

  tipoActividades: ITipoActividad[] = [];
  tipoActividadSuscripcion: Subscription;
  filteredTipoActividad: Observable<ITipoActividad[]>;

  suscripciones: Subscription[] = [];


  constructor(
    protected readonly logger: NGXLogger,
    private readonly tipoActividadService: TipoActividadService,
    private actionService: PeticionEvaluacionActionService
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(50%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(50%-10px)';
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

  ngOnInit(): void {

    this.logger.debug(PeticionEvaluacionDatosGeneralesComponent.name, 'ngOnInit()', 'start');

    super.ngOnInit();

    this.getTiposActividad();

    this.logger.debug(PeticionEvaluacionDatosGeneralesComponent.name, 'ngOnInit()', 'end');

  }


  /**
   * Recupera los tipos de actividad
   */
  getTiposActividad() {

    this.suscripciones.push(this.tipoActividadService.findAll().subscribe(
      (response) => {
        this.tipoActividades = response.items;


        this.filteredTipoActividad = this.formGroup.controls.tipoActividad.valueChanges
          .pipe(
            startWith(''),
            map(value => this.filterTipoActividad(value))
          );

      }));

  }

  /**
   * Devuelve el nombre de un tipo de actividad
   * @param tipoActividad tipo actividad
   * @returns nombre de un tipo de actividad
   */
  getTipoActividad(tipoActividad?: ITipoActividad): string | undefined {

    return tipoActividad?.nombre;
  }


  /**
   * Filtro de campo autocompletable tipo actividad.
   * @param value value a filtrar (string o nombre tipo actividad).
   * @returns lista de tipos de actividad filtrados.
   */
  private filterTipoActividad(value: string | ITipoActividad): ITipoActividad[] {
    let filterValue: string;
    if (typeof value === 'string') {
      filterValue = value.toLowerCase();
    } else {
      filterValue = value.nombre.toLowerCase();
    }

    return this.tipoActividades.filter
      (tipoActividad => tipoActividad.nombre.toLowerCase().includes(filterValue));
  }


}
