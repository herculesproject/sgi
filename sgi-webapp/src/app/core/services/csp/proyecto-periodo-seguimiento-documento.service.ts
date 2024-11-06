import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProyectoPeriodoSeguimientoDocumento } from '@core/models/csp/proyecto-periodo-seguimiento-documento';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';
import { IProyectoPeriodoSeguimientoDocumentoResponse } from './proyecto-periodo-seguimiento/proyecto-periodo-seguimiento-documento-response';
import { PROYECTO_PERIODO_SEGUIMIENTO_DOCUMENTO_CONVERTER } from './proyecto-periodo-seguimiento/proyecto-periodo-seguimiento-documento.converter';

@Injectable({
  providedIn: 'root'
})
export class ProyectoPeriodoSeguimientoDocumentoService
  extends SgiMutableRestService<number, IProyectoPeriodoSeguimientoDocumentoResponse, IProyectoPeriodoSeguimientoDocumento> {
  private static readonly MAPPING = '/proyectoperiodoseguimientodocumentos';

  constructor(protected http: HttpClient) {
    super(
      ProyectoPeriodoSeguimientoDocumentoService.name,
      `${environment.serviceServers.csp}${ProyectoPeriodoSeguimientoDocumentoService.MAPPING}`,
      http,
      PROYECTO_PERIODO_SEGUIMIENTO_DOCUMENTO_CONVERTER
    );
  }

}
