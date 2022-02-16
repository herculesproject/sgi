import { Injectable, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ActionService } from '@core/services/action-service';
import { GRUPO_ROUTE_PARAMS } from './autorizacion-route-params';
import { GRUPO_DATA_KEY } from './grupo-data.resolver';

export interface IGrupoData {
  isInvestigador: boolean
}

@Injectable()
export class GrupoActionService extends ActionService implements OnDestroy {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datos-generales'
  };

  private readonly data: IGrupoData;
  public readonly id: number;

  get isInvestigador(): boolean {
    return this.data.isInvestigador;
  }

  constructor(
    route: ActivatedRoute
  ) {
    super();
    this.id = Number(route.snapshot.paramMap.get(GRUPO_ROUTE_PARAMS.ID));

    if (this.id) {
      this.enableEdit();
      this.data = route.snapshot.data[GRUPO_DATA_KEY];
    }
  }

}
