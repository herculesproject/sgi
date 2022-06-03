import { Injectable, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IEmpresaExplotacionResultados } from '@core/models/eer/empresa-explotacion-resultados';
import { ActionService } from '@core/services/action-service';
import { EmpresaEquipoEmprendedorService } from '@core/services/eer/empresa-equipo-emprendedor/empresa-equipo-emprendedor.service';
import { EmpresaExplotacionResultadosService } from '@core/services/eer/empresa-explotacion-resultados/empresa-explotacion-resultados.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { VinculacionService } from '@core/services/sgp/vinculacion.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { EMPRESA_EXPLOTACION_RESULTADOS_DATA_KEY } from './empresa-explotacion-resultados-data.resolver';
import { EmpresaEquipoEmprendedorFragment } from './empresa-explotacion-resultados-formulario/empresa-equipo-emprendedor/empresa-equipo-emprendedor.fragment';
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
    EQUIPO_EMPRENDEDOR: 'equipo-emprendedor'
  };

  private datosGenerales: EmpresaExplotacionResultadosDatosGeneralesFragment;
  private equipoEmprendedor: EmpresaEquipoEmprendedorFragment;

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
    empresaEquipoEmprendedorService: EmpresaEquipoEmprendedorService,
    vinculacionService: VinculacionService,
    sgiAuthService: SgiAuthService,
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

    this.equipoEmprendedor = new EmpresaEquipoEmprendedorFragment(
      logger,
      this.id,
      empresaExplotacionResultadosService,
      empresaEquipoEmprendedorService,
      personaService,
      vinculacionService,
      empresaService,
      sgiAuthService,
      this.data?.readonly
    );

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.EQUIPO_EMPRENDEDOR, this.equipoEmprendedor);

    this.datosGenerales.initialize();
  }

}
