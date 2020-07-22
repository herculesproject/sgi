import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { UnidadMedida } from '@core/models/cat/unidad-medida';
import { FxFlexProperties } from '@core/models/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/flexLayout/fx-layout-properties';
import { UnidadMedidaService } from '@core/services/cat/unidad-medida.service';
import { FormGroupUtil } from '@core/services/form-group-util';
import { AbstractTabComponent } from '@shared/formularios-tabs/abstract-tab/abstract-tab.component';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';

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

  crearFormGroup(): FormGroup {
    this.logger.debug(EvaluacionEticaDatosGeneralesComponent.name, 'crearFormGroup()', 'start');
    const formGroup = new FormGroup({
      titulo: new FormControl('', [Validators.required]),
      tipoActividad: new FormControl('', [Validators.required]),
      tipoFinanciacion: new FormControl('', [Validators.required]),
      fechaInicio: new FormControl('', [Validators.required]),
      fechaFin: new FormControl('', [Validators.required]),
      resumen: new FormControl('', [Validators.required]),
      valorSocial: new FormControl('', [Validators.required]),
    });
    this.logger.debug(EvaluacionEticaDatosGeneralesComponent.name, 'crearFormGroup()', 'end');
    return formGroup;
  }

  getDatosIniciales(): {} {
    this.logger.debug(EvaluacionEticaDatosGeneralesComponent.name, 'getDatosIniciales()', 'start');
    const datos = {
      titulo: '',
      tipoActividad: '',
      tipoFinanciacion: '',
      fechaInicio: '',
      fechaFin: '',
      resumen: '',
      valorSocial: '',
    };
    this.logger.debug(EvaluacionEticaDatosGeneralesComponent.name, 'getDatosIniciales()', 'end');
    return datos;
  }

  crearObservable(): Observable<UnidadMedida> {
    this.logger.debug(EvaluacionEticaDatosGeneralesComponent.name, 'crearObservable()', 'start');
    const unidad = new UnidadMedida();
    unidad.abreviatura = String(Math.floor(Math.random() * (10_000_000 - 1)) + 1);
    unidad.descripcion = EvaluacionEticaDatosGeneralesComponent.name;
    this.logger.debug(EvaluacionEticaDatosGeneralesComponent.name, 'crearObservable()', 'end');
    return this.unidadMedidaService.create(unidad);
  }

  getDatosFormulario(): {} {
    return this.formGroup.value;
  }
}
