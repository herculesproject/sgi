import { Component, ViewChild, AfterViewInit, OnInit } from '@angular/core';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { NGXLogger } from 'ngx-logger';

import {
  EvaluacionListadoAnteriorMemoriaComponent,
} from '../evaluacion-listado-anterior-memoria/evaluacion-listado-anterior-memoria.component';
import { FormFragmentComponent } from '@core/component/fragment.component';

import { IEvaluacion } from '@core/models/eti/evaluacion';
import { DictamenService } from '@core/services/eti/dictamen.service';
import { Subscription, Observable } from 'rxjs';
import { IDictamen } from '@core/models/eti/dictamen';
import { EvaluacionActionService } from '../../evaluacion/evaluacion.action.service';
import { EvaluacionEvaluacionFragment } from './evaluacion-evaluacion.fragment';
import { IMemoria } from '@core/models/eti/memoria';

@Component({
  selector: 'sgi-evaluacion-evaluacion',
  templateUrl: './evaluacion-evaluacion.component.html',
  styleUrls: ['./evaluacion-evaluacion.component.scss']
})
export class EvaluacionEvaluacionComponent extends FormFragmentComponent<IMemoria> implements AfterViewInit, OnInit {

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexPropertiesInline: FxFlexProperties;

  @ViewChild('evaluaciones') evaluaciones: EvaluacionListadoAnteriorMemoriaComponent;

  dictamenListado: IDictamen[];
  filteredDictamenes: Observable<IDictamen[]>;
  dictamenSubscription: Subscription;

  constructor(
    protected readonly logger: NGXLogger,
    private actionService: EvaluacionActionService,
    private dictamenService: DictamenService
  ) {
    super(actionService.FRAGMENT.EVALUACIONES, actionService);
    this.logger.debug(EvaluacionEvaluacionComponent.name, 'constructor()', 'start');
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(32%-10px)';
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

    this.logger.debug(EvaluacionEvaluacionComponent.name, 'constructor()', 'end');
  }

  ngOnInit() {
    this.logger.debug(EvaluacionEvaluacionComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();
    (this.fragment as EvaluacionEvaluacionFragment).evaluacion$.subscribe((evaluacion) => {
      if (evaluacion) {
        this.getDictamenes(evaluacion);
      }
    });
    this.logger.debug(EvaluacionEvaluacionComponent.name, 'ngOnInit()', 'end');
  }

  ngAfterViewInit(): void {
    this.logger.debug(EvaluacionEvaluacionComponent.name, 'ngAfterViewInit()', 'start');
    this.evaluaciones.memoriaId = this.actionService.getEvaluacion()?.memoria?.id;
    this.evaluaciones.evaluacionId = this.actionService.getEvaluacion()?.id;
    this.evaluaciones.ngAfterViewInit();
    this.logger.debug(EvaluacionEvaluacionComponent.name, 'ngAfterViewInit()', 'end');
  }

  /**
   * Devuelve el nombre de un dictamen.
   * @param dictamen dictamen
   * returns nombre dictamen
   */
  getDictamen(dictamen: IDictamen): string {
    return dictamen?.nombre;
  }

  /**
   * * Recupera un listado de los dictamenes que hay en el sistema.
   */
  getDictamenes(evaluacion: IEvaluacion): void {
    this.logger.debug(EvaluacionEvaluacionComponent.name,
      'getDictamenes()',
      'start');

    // TipoEvaluacion: Memoria y TipoEstadoMemoria: En secretaría revisión mínima
    if ((evaluacion.tipoEvaluacion.id === 2)
      && (evaluacion.memoria.estadoActual.id === 4)) {

      this.dictamenSubscription = this.dictamenService.findAllByMemoriaRevisionMinima().subscribe(
        (response) => {

          this.dictamenListado = response.items;
        });
    }

    // TipoEvaluacion: Memoria y TipoEstadoMemoria: NO En secretaría revisión mínima
    if ((evaluacion.tipoEvaluacion.id === 2)
      && (evaluacion.memoria.estadoActual.id !== 4)) {

      this.dictamenSubscription = this.dictamenService.findAllByMemoriaNoRevisionMinima().subscribe(
        (response) => {

          this.dictamenListado = response.items;
        });
    }

    // TipoEvaluacion: Retrospectiva
    if (evaluacion.tipoEvaluacion.id === 1) {

      this.dictamenSubscription = this.dictamenService.findAllByRetrospectiva().subscribe(
        (response) => {
          this.dictamenListado = response.items;
        });
    }

    this.logger.debug(EvaluacionEvaluacionComponent.name,
      'getDictamenes()',
      'end');
  }

}
