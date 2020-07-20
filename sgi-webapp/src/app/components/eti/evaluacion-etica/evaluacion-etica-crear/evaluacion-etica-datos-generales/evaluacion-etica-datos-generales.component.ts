import {Component, OnInit} from '@angular/core';
import {NGXLogger} from 'ngx-logger';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {FxFlexProperties} from '@core/models/flexLayout/fx-flex-properties';
import {FxLayoutProperties} from '@core/models/flexLayout/fx-layout-properties';
import {FormGroupUtil} from '@core/services/form-group-util';
import {AbstractTabComponent} from '@shared/formularios-tabs/abstract-tab/abstract-tab.component';
import {UnidadMedida} from '@core/models/unidad-medida';
import {UnidadMedidaService} from '@core/services/unidad-medida.service';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-evaluacion-etica-datos-generales',
  templateUrl: './evaluacion-etica-datos-generales.component.html',
  styleUrls: ['./evaluacion-etica-datos-generales.component.scss']
})
export class EvaluacionEticaDatosGeneralesComponent extends AbstractTabComponent<{}> implements OnInit {
  FormGroupUtil = FormGroupUtil;
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

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
    this.logger.debug(EvaluacionEticaDatosGeneralesComponent.name, 'ngOnInit()', 'start');
    this.formGroup = new FormGroup({
      titulo: new FormControl('', [Validators.required]),
      tipoActividad: new FormControl('', [Validators.required]),
      tipoFinanciacion: new FormControl('', [Validators.required]),
      fechaInicio: new FormControl('', [Validators.required]),
      fechaFin: new FormControl('', [Validators.required]),
      resumen: new FormControl('', [Validators.required]),
      valorSocial: new FormControl('', [Validators.required]),
    });
    this.logger.debug(EvaluacionEticaDatosGeneralesComponent.name, 'ngOnInit()', 'end');
  }

  getDatosIniciales(): {} {
    return {
      titulo: '',
      tipoActividad: '',
      tipoFinanciacion: '',
      fechaInicio: '',
      fechaFin: '',
      resumen: '',
      valorSocial: '',
    };
  }

  crearObservable(): Observable<UnidadMedida> {
    this.logger.debug(EvaluacionEticaDatosGeneralesComponent.name, 'crearObservable()', 'start');
    const unidad = new UnidadMedida();
    unidad.abreviatura = String(Math.floor(Math.random() * (10_000_000 - 1)) + 1);
    unidad.descripcion = EvaluacionEticaDatosGeneralesComponent.name;
    this.logger.debug(EvaluacionEticaDatosGeneralesComponent.name, 'crearObservable()', 'end');
    return this.unidadMedidaService.create(unidad);
  }
}
