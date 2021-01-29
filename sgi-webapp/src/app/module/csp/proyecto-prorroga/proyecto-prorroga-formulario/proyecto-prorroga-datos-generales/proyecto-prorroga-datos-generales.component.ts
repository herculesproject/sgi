import { Component, OnInit, OnDestroy } from '@angular/core';
import { IProyectoProrroga, TipoProrrogaEnum } from '@core/models/csp/proyecto-prorroga';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { NGXLogger } from 'ngx-logger';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { Subscription, merge } from 'rxjs';
import { ProyectoProrrogaActionService } from '../../proyecto-prorroga.action.service';
import { ProyectoProrrogaDatosGeneralesFragment } from './proyecto-prorroga-datos-generales.fragment';
import { FormGroupUtil } from '@core/utils/form-group-util';

@Component({
  selector: 'sgi-solicitud-proyecto-prorroga-datos-generales',
  templateUrl: './proyecto-prorroga-datos-generales.component.html',
  styleUrls: ['./proyecto-prorroga-datos-generales.component.scss']
})
export class ProyectoProrrogaDatosGeneralesComponent extends FormFragmentComponent<IProyectoProrroga>
  implements OnInit, OnDestroy {
  formPart: ProyectoProrrogaDatosGeneralesFragment;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexProperties: FxFlexProperties;
  fxFlexProperties3: FxFlexProperties;
  private subscriptions: Subscription[] = [];
  periodoSeguimientosSelectedProyecto: IProyectoProrroga[] = [];
  FormGroupUtil = FormGroupUtil;

  tipoProrroga = Object.keys(TipoProrrogaEnum).map<string>(
    (key) => TipoProrrogaEnum[key]);

  constructor(
    protected logger: NGXLogger,
    protected actionService: ProyectoProrrogaActionService
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.logger.debug(ProyectoProrrogaDatosGeneralesComponent.name, 'constructor()', 'start');
    this.formPart = this.fragment as ProyectoProrrogaDatosGeneralesFragment;
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(36%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(32%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxFlexProperties3 = new FxFlexProperties();
    this.fxFlexProperties3.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties3.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties3.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties3.order = '3';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
    this.logger.debug(ProyectoProrrogaDatosGeneralesComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(ProyectoProrrogaDatosGeneralesComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();
    this.loadProrrogasSelectedProyecto();
    this.logger.debug(ProyectoProrrogaDatosGeneralesComponent.name, 'ngOnInit()', 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(ProyectoProrrogaDatosGeneralesComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(ProyectoProrrogaDatosGeneralesComponent.name, 'ngOnDestroy()', 'end');
  }

  private loadProrrogasSelectedProyecto(): void {
    this.logger.debug(ProyectoProrrogaDatosGeneralesComponent.name, `loadProrrogasSelectedProyecto()`, 'start');
    this.periodoSeguimientosSelectedProyecto = this.formPart.selectedProyectoProrrogas;
    this.logger.debug(ProyectoProrrogaDatosGeneralesComponent.name, `loadProrrogasSelectedProyecto()`, 'end');
  }

}
