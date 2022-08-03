import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IRequerimientoJustificacion } from '@core/models/csp/requerimiento-justificacion';
import { ActionService } from '@core/services/action-service';
import { IncidenciaDocumentacionRequerimientoService } from '@core/services/csp/incidencia-documentacion-requerimiento/incidencia-documentacion-requerimiento.service';
import { ProyectoPeriodoJustificacionService } from '@core/services/csp/proyecto-periodo-justificacion/proyecto-periodo-justificacion.service';
import { RequerimientoJustificacionService } from '@core/services/csp/requerimiento-justificacion/requerimiento-justificacion.service';
import { SeguimientoJustificacionService } from '@core/services/sge/seguimiento-justificacion/seguimiento-justificacion.service';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, Observable } from 'rxjs';
import { EJECUCION_ECONOMICA_DATA_KEY } from '../ejecucion-economica/ejecucion-economica-data.resolver';
import { IEjecucionEconomicaData } from '../ejecucion-economica/ejecucion-economica.action.service';
import { REQUERIMIENTO_JUSTIFICACION_DATA_KEY } from './seguimiento-justificacion-requerimiento-data.resolver';
import { SeguimientoJustificacionRequerimientoDatosGeneralesFragment } from './seguimiento-justificacion-requerimiento-formulario/seguimiento-justificacion-requerimiento-datos-generales/seguimiento-justificacion-requerimiento-datos-generales.fragment';
import { SeguimientoJustificacionRequerimientoGastosFragment } from './seguimiento-justificacion-requerimiento-formulario/seguimiento-justificacion-requerimiento-gastos/seguimiento-justificacion-requerimiento-gastos.fragment';
import { SEGUIMIENTO_JUSTIFICACION_REQUERIMIENTO_ROUTE_PARAMS } from './seguimiento-justificacion-requerimiento-route-params';

export interface IRequerimientoJustificacionData {
  requerimientoJustificacion: IRequerimientoJustificacion;
  canEdit: boolean;
}

@Injectable()
export class SeguimientoJustificacionRequerimientoActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datos-generales',
    GASTOS: 'gastos'
  };

  private nombreTipoRequerimiento$ = new BehaviorSubject<string>('');
  private readonly data: IRequerimientoJustificacionData;
  private readonly dataEjecucionEconomica: IEjecucionEconomicaData;
  public readonly: boolean;

  private datosGenerales: SeguimientoJustificacionRequerimientoDatosGeneralesFragment;
  private gastos: SeguimientoJustificacionRequerimientoGastosFragment;

  constructor(
    logger: NGXLogger,
    route: ActivatedRoute,
    requerimientoJustificacionService: RequerimientoJustificacionService,
    proyectoPeriodoJustificacionService: ProyectoPeriodoJustificacionService,
    incidenciaDocumentacionRequerimientoService: IncidenciaDocumentacionRequerimientoService,
    seguimientoJustificacionService: SeguimientoJustificacionService
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
      incidenciaDocumentacionRequerimientoService
    );

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);

    if (this.isEdit()) {
      this.gastos = new SeguimientoJustificacionRequerimientoGastosFragment(
        this.data.requerimientoJustificacion,
        requerimientoJustificacionService,
        seguimientoJustificacionService,
        proyectoPeriodoJustificacionService
      );

      this.datosGenerales.initialize();
      this.subscriptions.push(
        this.datosGenerales.tipoRequerimientoControlValueChanges()
          .subscribe(tipoRequerimiento => this.changeNombreTipoRequerimiento(tipoRequerimiento?.nombre ?? ''))
      );
      this.addFragment(this.FRAGMENT.GASTOS, this.gastos);
    }
  }

  getNombreTipoRequerimiento$(): Observable<string> {
    return this.nombreTipoRequerimiento$.asObservable();
  }

  changeNombreTipoRequerimiento(newNombreTipoRequerimiento: string): void {
    this.nombreTipoRequerimiento$.next(newNombreTipoRequerimiento);
  }
}
