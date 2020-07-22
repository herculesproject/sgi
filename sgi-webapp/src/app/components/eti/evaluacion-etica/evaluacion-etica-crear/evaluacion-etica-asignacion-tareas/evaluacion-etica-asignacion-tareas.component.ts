import { Component } from '@angular/core';
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
  selector: 'app-evaluacion-etica-equipo-asignacion-tareas',
  templateUrl: './evaluacion-etica-asignacion-tareas.component.html',
  styleUrls: ['./evaluacion-etica-asignacion-tareas.component.scss']
})
export class EvaluacionEticaAsignacionTareasComponent extends AbstractTabComponent<{}> {

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

  crearFormGroup(): FormGroup {
    this.logger.debug(EvaluacionEticaAsignacionTareasComponent.name, 'crearFormGroup()', 'start');
    const formGroup = new FormGroup({
      titulo: new FormControl('', [Validators.required]),
    });
    this.logger.debug(EvaluacionEticaAsignacionTareasComponent.name, 'crearFormGroup()', 'end');
    return formGroup;
  }

  getDatosIniciales(): {} {
    this.logger.debug(EvaluacionEticaAsignacionTareasComponent.name, 'getDatosIniciales()', 'start');
    const datos = {
      titulo: ''
    };
    this.logger.debug(EvaluacionEticaAsignacionTareasComponent.name, 'getDatosIniciales()', 'end');
    return datos;
  }

  crearObservable(): Observable<UnidadMedida> {
    this.logger.debug(EvaluacionEticaAsignacionTareasComponent.name, 'crearObservable()', 'start');
    const unidad = new UnidadMedida();
    unidad.abreviatura = String(Math.floor(Math.random() * (10_000_000 - 1)) + 1);
    unidad.descripcion = EvaluacionEticaAsignacionTareasComponent.name;
    this.logger.debug(EvaluacionEticaAsignacionTareasComponent.name, 'crearObservable()', 'end');
    return this.unidadMedidaService.create(unidad);
  }

  getDatosFormulario(): {} {
    return this.formGroup.value;
  }
}
