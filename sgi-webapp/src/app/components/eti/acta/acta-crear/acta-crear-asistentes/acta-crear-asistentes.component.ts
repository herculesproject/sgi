import { Component } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Acta } from '@core/models/eti/acta';
import { FxFlexProperties } from '@core/models/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/flexLayout/fx-layout-properties';
import { FormGroupUtil } from '@core/services/form-group-util';
import { AbstractTabComponent } from '@shared/formularios-tabs/abstract-tab/abstract-tab.component';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';

@Component({
  selector: 'app-acta-crear-asistentes',
  templateUrl: './acta-crear-asistentes.component.html',
  styleUrls: ['./acta-crear-asistentes.component.scss']
})
export class ActaCrearAsistentesComponent extends AbstractTabComponent<any> {
  FormGroupUtil = FormGroupUtil;
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  constructor(
    protected readonly logger: NGXLogger,
  ) {
    super(logger);
  }

  crearFormGroup(): FormGroup {
    this.logger.debug(ActaCrearAsistentesComponent.name, 'crearFormGroup()', 'start');
    const formGroup = new FormGroup({
      // Comentado para pruebas
      //  titulo: new FormControl('', [Validators.required]),
    });
    this.logger.debug(ActaCrearAsistentesComponent.name, 'crearFormGroup()', 'end');
    return formGroup;
  }

  crearObservable(): Observable<any> {
    this.logger.debug(ActaCrearAsistentesComponent.name, 'crearObservable()', 'start');
    const observable = of({});
    this.logger.debug(ActaCrearAsistentesComponent.name, 'crearObservable()', 'end');
    return observable;
  }

  getDatosIniciales(): Acta {
    this.logger.debug(ActaCrearAsistentesComponent.name, 'crearObservable()', 'start');
    const datos = new Acta();
    this.logger.debug(ActaCrearAsistentesComponent.name, 'crearObservable()', 'end');
    return datos;
  }

  getDatosFormulario(): {} {
    return this.formGroup.value;
  }
}
