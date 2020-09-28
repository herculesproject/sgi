import { Injectable } from '@angular/core';

import { ActionService } from '@core/services/action-service';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { IConvocatoria } from '@core/models/csp/convocatoria';

@Injectable()
export class ConvocatoriaActionService extends ActionService {

  public readonly FRAGMENT = {


  };


  private convocatoria: IConvocatoria;

  constructor(fb: FormBuilder, route: ActivatedRoute, service: ConvocatoriaService) {
    super();
    this.convocatoria = {} as IConvocatoria;
    if (route.snapshot.data.acta) {
      this.convocatoria = route.snapshot.data.convocatoria;
      this.enableEdit();
    }


  }

}
