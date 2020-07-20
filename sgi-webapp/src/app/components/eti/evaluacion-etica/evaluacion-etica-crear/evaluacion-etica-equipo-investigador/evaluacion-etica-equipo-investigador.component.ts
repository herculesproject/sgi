import {Component, OnInit} from '@angular/core';
import {NGXLogger} from 'ngx-logger';
import {AbstractTabComponent} from '@shared/formularios-tabs/abstract-tab/abstract-tab.component';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {UnidadMedida} from '@core/models/unidad-medida';
import {UnidadMedidaService} from '@core/services/unidad-medida.service';
import {Observable} from 'rxjs';
import {FxFlexProperties} from '@core/models/flexLayout/fx-flex-properties';
import {FxLayoutProperties} from '@core/models/flexLayout/fx-layout-properties';
import {FormGroupUtil} from '@core/services/form-group-util';

@Component({
  selector: 'app-evaluacion-etica-equipo-investigador',
  templateUrl: './evaluacion-etica-equipo-investigador.component.html',
  styleUrls: ['./evaluacion-etica-equipo-investigador.component.scss']
})
export class EvaluacionEticaEquipoInvestigadorComponent extends AbstractTabComponent<{}> implements OnInit {
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  FormGroupUtil = FormGroupUtil;

  constructor(
    protected readonly logger: NGXLogger,
    private readonly unidadMedidaService: UnidadMedidaService,
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

  ngOnInit(): void {
    super.ngOnInit();
    this.logger.debug(EvaluacionEticaEquipoInvestigadorComponent.name, 'ngOnInit()', 'start');
    this.formGroup = new FormGroup({
      titulo: new FormControl('2', [Validators.required]),
    });
    this.logger.debug(EvaluacionEticaEquipoInvestigadorComponent.name, 'ngOnInit()', 'end');
  }

  getDatosIniciales(): {} {
    return {
      titulo: '2'
    };
  }

  crearObservable(): Observable<UnidadMedida> {
    this.logger.debug(EvaluacionEticaEquipoInvestigadorComponent.name, 'crearObservable()', 'start');
    const unidad = new UnidadMedida();
    unidad.abreviatura = String(Math.floor(Math.random() * (10_000_000 - 1)) + 1);
    unidad.descripcion = EvaluacionEticaEquipoInvestigadorComponent.name;
    this.logger.debug(EvaluacionEticaEquipoInvestigadorComponent.name, 'crearObservable()', 'end');
    return this.unidadMedidaService.create(unidad);
  }
}
