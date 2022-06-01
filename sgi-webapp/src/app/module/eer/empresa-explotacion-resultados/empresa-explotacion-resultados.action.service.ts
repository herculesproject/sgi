import { Injectable, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IEmpresaExplotacionResultados } from '@core/models/eer/empresa-explotacion-resultados';
import { ActionService } from '@core/services/action-service';
import { EmpresaExplotacionResultadosService } from '@core/services/eer/empresa-explotacion-resultados/empresa-explotacion-resultados.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { NGXLogger } from 'ngx-logger';
import { EMPRESA_EXPLOTACION_RESULTADOS_DATA_KEY } from './empresa-explotacion-resultados-data.resolver';
import { EmpresaExplotacionResultadosDatosGeneralesFragment } from './empresa-explotacion-resultados-formulario/empresa-explotacion-resultados-datos-generales/empresa-explotacion-resultados-datos-generales.fragment';
import { EMPRESA_EXPLOTACION_RESULTADOS_ROUTE_PARAMS } from './empresa-explotacion-resultados-route-params';


export interface IEmpresaExplotacionResultadosData {
  empresa: IEmpresaExplotacionResultados;
  isInvestigador: boolean;
  readonly: boolean;
}

@Injectable()
export class EmpresaExplotacionResultadosActionService extends ActionService implements OnDestroy {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datos-generales',
  };

  private datosGenerales: EmpresaExplotacionResultadosDatosGeneralesFragment;

  private readonly data: IEmpresaExplotacionResultadosData;
  public readonly id: number;

  get isInvestigador(): boolean {
    return this.data.isInvestigador;
  }

  get empresa(): IEmpresaExplotacionResultados {
    return this.datosGenerales.getValue();
  }

  get readonly(): boolean {
    return this.data?.readonly;
  }

  constructor(
    logger: NGXLogger,
    route: ActivatedRoute,
    empresaExplotacionResultadosService: EmpresaExplotacionResultadosService,
    empresaService: EmpresaService,
    personaService: PersonaService,
  ) {
    super();
    this.id = Number(route.snapshot.paramMap.get(EMPRESA_EXPLOTACION_RESULTADOS_ROUTE_PARAMS.ID));

    if (this.id) {
      this.enableEdit();
      this.data = route.snapshot.data[EMPRESA_EXPLOTACION_RESULTADOS_DATA_KEY];
    }

    this.datosGenerales = new EmpresaExplotacionResultadosDatosGeneralesFragment(
      logger,
      this.id, empresaExplotacionResultadosService,
      empresaService,
      personaService,
      this.data?.readonly
    );

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);

    this.datosGenerales.initialize();
  }

}
