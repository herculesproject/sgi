import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IRequerimientoJustificacion } from '@core/models/csp/requerimiento-justificacion';
import { ActionService } from '@core/services/action-service';
import { IncidenciaDocumentacionRequerimientoService } from '@core/services/csp/incidencia-documentacion-requerimiento/incidencia-documentacion-requerimiento.service';
import { ProyectoPeriodoJustificacionService } from '@core/services/csp/proyecto-periodo-justificacion/proyecto-periodo-justificacion.service';
import { ProyectoProyectoSgeService } from '@core/services/csp/proyecto-proyecto-sge.service';
import { RequerimientoJustificacionService } from '@core/services/csp/requerimiento-justificacion/requerimiento-justificacion.service';
import { NGXLogger } from 'ngx-logger';
import { EJECUCION_ECONOMICA_DATA_KEY } from '../ejecucion-economica/ejecucion-economica-data.resolver';
import { IEjecucionEconomicaData } from '../ejecucion-economica/ejecucion-economica.action.service';
import { REQUERIMIENTO_JUSTIFICACION_DATA_KEY } from './seguimiento-justificacion-requerimiento-data.resolver';
import { SeguimientoJustificacionRequerimientoDatosGeneralesFragment } from './seguimiento-justificacion-requerimiento-formulario/seguimiento-justificacion-requerimiento-datos-generales/seguimiento-justificacion-requerimiento-datos-generales.fragment';
import { SEGUIMIENTO_JUSTIFICACION_REQUERIMIENTO_ROUTE_PARAMS } from './seguimiento-justificacion-requerimiento-route-params';

export interface IRequerimientoJustificacionData {
  requerimientoJustificacion: IRequerimientoJustificacion;
  canEdit: boolean;
}

@Injectable()
export class SeguimientoJustificacionRequerimientoActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datosGenerales',
  };

  private readonly data: IRequerimientoJustificacionData;
  private readonly dataEjecucionEconomica: IEjecucionEconomicaData;
  public readonly: boolean;

  private datosGenerales: SeguimientoJustificacionRequerimientoDatosGeneralesFragment;

  constructor(
    logger: NGXLogger,
    route: ActivatedRoute,
    requerimientoJustificacionService: RequerimientoJustificacionService,
    proyectoPeriodoJustificacionService: ProyectoPeriodoJustificacionService,
    proyectoProyectoSgeService: ProyectoProyectoSgeService,
    incidenciaDocumentacionRequerimientoService: IncidenciaDocumentacionRequerimientoService
  ) {
    super();
    this.data = {} as IRequerimientoJustificacionData;
    this.dataEjecucionEconomica = route.snapshot.parent.data[EJECUCION_ECONOMICA_DATA_KEY];
    const id = Number(route.snapshot.paramMap.get(SEGUIMIENTO_JUSTIFICACION_REQUERIMIENTO_ROUTE_PARAMS.ID));
    if (id) {
      this.data = route.snapshot.data[REQUERIMIENTO_JUSTIFICACION_DATA_KEY];
      this.enableEdit();
    }

    this.datosGenerales = new SeguimientoJustificacionRequerimientoDatosGeneralesFragment(
      logger,
      this.data.requerimientoJustificacion,
      this.data.canEdit,
      this.dataEjecucionEconomica.proyectoSge.id,
      requerimientoJustificacionService,
      proyectoPeriodoJustificacionService,
      proyectoProyectoSgeService,
      incidenciaDocumentacionRequerimientoService
    );

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
  }

}
