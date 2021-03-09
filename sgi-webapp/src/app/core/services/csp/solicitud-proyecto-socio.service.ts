import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SOLICITUD_PROYECTO_EQUIPO_SOCIO_CONVERTER } from '@core/converters/csp/solicitud-proyecto-equipo-socio.converter';
import {
  SOLICITUD_PROYECTO_PERIODO_JUSTIFICACION_CONVERTER
} from '@core/converters/csp/solicitud-proyecto-periodo-justificacion.converter';
import { SOLICITUD_PROYECTO_PERIODO_PAGO_CONVERTER } from '@core/converters/csp/solicitud-proyecto-periodo-pago.converter';
import { SOLICITUD_PROYECTO_SOCIO_CONVERTER } from '@core/converters/csp/solicitud-proyecto-socio.converter';
import { ISolicitudProyectoEquipoSocioBackend } from '@core/models/csp/backend/solicitud-proyecto-equipo-socio-backend';
import { ISolicitudProyectoPeriodoJustificacionBackend } from '@core/models/csp/backend/solicitud-proyecto-periodo-justificacion-backend';
import { ISolicitudProyectoPeriodoPagoBackend } from '@core/models/csp/backend/solicitud-proyecto-periodo-pago-backend';
import { ISolicitudProyectoSocioBackend } from '@core/models/csp/backend/solicitud-proyecto-socio-backend';
import { ISolicitudProyectoEquipoSocio } from '@core/models/csp/solicitud-proyecto-equipo-socio';
import { ISolicitudProyectoPeriodoJustificacion } from '@core/models/csp/solicitud-proyecto-periodo-justificacion';
import { ISolicitudProyectoPeriodoPago } from '@core/models/csp/solicitud-proyecto-periodo-pago';
import { ISolicitudProyectoSocio } from '@core/models/csp/solicitud-proyecto-socio';
import { environment } from '@env';
import { SgiMutableRestService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { EmpresaEconomicaService } from '../sgp/empresa-economica.service';

@Injectable({
  providedIn: 'root'
})
export class SolicitudProyectoSocioService extends SgiMutableRestService<number, ISolicitudProyectoSocioBackend, ISolicitudProyectoSocio>  {
  private static readonly MAPPING = '/solicitudproyectosocio';

  constructor(
    protected http: HttpClient,
    private empresaEconomicaService: EmpresaEconomicaService
  ) {
    super(
      SolicitudProyectoSocioService.name,
      `${environment.serviceServers.csp}${SolicitudProyectoSocioService.MAPPING}`,
      http,
      SOLICITUD_PROYECTO_SOCIO_CONVERTER
    );
  }

  findById(id: number): Observable<ISolicitudProyectoSocio> {
    return super.findById(id).pipe(
      switchMap(solicitudProyectoSocio => {
        const personaRef = solicitudProyectoSocio.empresa.personaRef;
        return this.empresaEconomicaService.findById(personaRef).pipe(
          map(empresa => {
            solicitudProyectoSocio.empresa = empresa;
            return solicitudProyectoSocio;
          })
        );
      })
    );
  }

  /**
   * Devuelve el listado de ISolicitudProyectoPeriodoPago de un ISolicitudProyectoSocio
   *
   * @param id Id del ISolicitudProyectoSocio
   */
  findAllSolicitudProyectoPeriodoPago(id: number, options?: SgiRestFindOptions):
    Observable<SgiRestListResult<ISolicitudProyectoPeriodoPago>> {
    return this.find<ISolicitudProyectoPeriodoPagoBackend, ISolicitudProyectoPeriodoPago>(
      `${this.endpointUrl}/${id}/solicitudproyectoperiodopago`,
      options,
      SOLICITUD_PROYECTO_PERIODO_PAGO_CONVERTER
    );
  }

  /**
   * Devuelve el listado de ISolicitudProyectoPeriodoPago de un ISolicitudProyectoSocio
   *
   * @param id Id del ISolicitudProyectoSocio
   */
  findAllSolicitudProyectoPeriodoJustificacion(id: number, options?: SgiRestFindOptions):
    Observable<SgiRestListResult<ISolicitudProyectoPeriodoJustificacion>> {
    return this.find<ISolicitudProyectoPeriodoJustificacionBackend, ISolicitudProyectoPeriodoJustificacion>(
      `${this.endpointUrl}/${id}/solicitudproyectoperiodojustificaciones`,
      options,
      SOLICITUD_PROYECTO_PERIODO_JUSTIFICACION_CONVERTER
    );
  }

  /**
   * Devuelve el listado de ISolicitudProyectoEquipoSocio de un ISolicitudProyectoSocio
   *
   * @param id Id del ISolicitudProyectoSocio
   */
  findAllSolicitudProyectoEquipoSocio(id: number, options?: SgiRestFindOptions):
    Observable<SgiRestListResult<ISolicitudProyectoEquipoSocio>> {
    return this.find<ISolicitudProyectoEquipoSocioBackend, ISolicitudProyectoEquipoSocio>(
      `${this.endpointUrl}/${id}/solicitudproyectoequiposocio`,
      options,
      SOLICITUD_PROYECTO_EQUIPO_SOCIO_CONVERTER
    );
  }

  /**
   * Comprueba si un ISolicitudProyectoSocio tiene ISolicitudProyectoSocioEquipo,
   * ISolicitudProyectoSocioPeriodoPago y/o ISolicitudProyectoSocioPeriodoJustificacion 
   * relacionados
   *
   *  @param id Id deL proyecto
   */
  vinculaciones(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/vinculaciones`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }
}
