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
  selector: 'app-acta-crear-memorias',
  templateUrl: './acta-crear-memorias.component.html',
  styleUrls: ['./acta-crear-memorias.component.scss']
})
export class ActaCrearMemoriasComponent extends AbstractTabComponent<any> {
  FormGroupUtil = FormGroupUtil;
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  constructor(
    protected readonly logger: NGXLogger,
  ) {
    super(logger);
  }

  crearFormGroup(): FormGroup {
    this.logger.debug(ActaCrearMemoriasComponent.name, 'crearFormGroup()', 'start');
    const formGroup = new FormGroup({
      // Comentado para pruebas
      //  titulo: new FormControl('', [Validators.required]),
    });
    this.logger.debug(ActaCrearMemoriasComponent.name, 'crearFormGroup()', 'end');
    return formGroup;
  }

  crearObservable(): Observable<any> {
    this.logger.debug(ActaCrearMemoriasComponent.name, 'crearObservable()', 'start');
    const observable = of({});
    this.logger.debug(ActaCrearMemoriasComponent.name, 'crearObservable()', 'end');
    return observable;
  }

  getDatosIniciales(): Acta {
    this.logger.debug(ActaCrearMemoriasComponent.name, 'crearObservable()', 'start');
    const datos = new Acta();
    this.logger.debug(ActaCrearMemoriasComponent.name, 'crearObservable()', 'end');
    return datos;
  }

  getDatosFormulario(): {} {
    return this.formGroup.value;
  }
}
