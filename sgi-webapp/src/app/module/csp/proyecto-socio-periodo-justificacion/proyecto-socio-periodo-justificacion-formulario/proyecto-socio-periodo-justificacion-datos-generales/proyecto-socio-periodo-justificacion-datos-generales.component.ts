import { Component, OnDestroy } from '@angular/core';
import { IProyectoSocioPeriodoJustificacion } from '@core/models/csp/proyecto-socio-periodo-justificacion';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { ProyectoSocioPeriodoJustificacionDatosGeneralesFragment } from './proyecto-socio-periodo-justificacion-datos-generales.fragment';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { Subscription } from 'rxjs';
import { NGXLogger } from 'ngx-logger';
import { ProyectoSocioPeriodoJustificacionActionService } from '../../proyecto-socio-periodo-justificacion.action.service';

@Component({
  selector: 'sgi-proyecto-socio-periodo-justificacion-datos-generales',
  templateUrl: './proyecto-socio-periodo-justificacion-datos-generales.component.html',
  styleUrls: ['./proyecto-socio-periodo-justificacion-datos-generales.component.scss']
})
export class ProyectoSocioPeriodoJustificacionDatosGeneralesComponent extends
  FormFragmentComponent<IProyectoSocioPeriodoJustificacion> implements OnDestroy {
  formPart: ProyectoSocioPeriodoJustificacionDatosGeneralesFragment;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexProperties: FxFlexProperties;
  private subscriptions: Subscription[] = [];

  /** ngx-mat-datetime-picker */
  showSeconds = true;
  defaultFechaInicioPresentacion = [0, 0, 0];
  defaultFechaFinPresentacion = [23, 59, 59];

  constructor(
    protected logger: NGXLogger,
    protected actionService: ProyectoSocioPeriodoJustificacionActionService
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.logger.debug(ProyectoSocioPeriodoJustificacionDatosGeneralesComponent.name, 'constructor()', 'start');
    this.formPart = this.fragment as ProyectoSocioPeriodoJustificacionDatosGeneralesFragment;
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(36%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(32%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
    this.logger.debug(ProyectoSocioPeriodoJustificacionDatosGeneralesComponent.name, 'constructor()', 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(ProyectoSocioPeriodoJustificacionDatosGeneralesComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(ProyectoSocioPeriodoJustificacionDatosGeneralesComponent.name, 'ngOnDestroy()', 'end');
  }

}
