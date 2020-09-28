import { Injectable } from '@angular/core';
import { SgiRestService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http/';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { of } from 'rxjs';


const convocatorias: IConvocatoria[] = [
  {
    id: 1, referencia: 'REF001', titulo: 'Ayudas plan propio', fechaInicioSolicitud: new Date(),
    fechaFinSolicitud: new Date(), estadoConvocante: 'Universidad', planInvestigacion: 'Plan propio',
    entidadFinanciadora: 'Universidad', fuenteFinanciacion: 'Fondos propios', activo: true
  },
  {
    id: 2, referencia: 'REF002', titulo: 'Programa 2020', fechaInicioSolicitud: new Date(),
    fechaFinSolicitud: new Date(), estadoConvocante: 'AEI', planInvestigacion: 'Plan Nacional 2020',
    entidadFinanciadora: 'AEI', fuenteFinanciacion: 'Presupuestos generados estado', activo: false
  },
  {
    id: 3, referencia: 'REF003', titulo: 'Fondo COVID', fechaInicioSolicitud: new Date(),
    fechaFinSolicitud: new Date(), estadoConvocante: 'CRUE', planInvestigacion: 'Plan COVID',
    entidadFinanciadora: 'CSIC', fuenteFinanciacion: 'Fondos COVID', activo: true
  }

];

@Injectable({
  providedIn: 'root'
})

export class ConvocatoriaService extends SgiRestService<number, IConvocatoria> {
  private static readonly MAPPING = '/convocatorias';


  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      ConvocatoriaService.name,
      logger,
      `${environment.serviceServers.eti}${ConvocatoriaService.MAPPING}`,
      http
    );
  }



  /**
   * Recupera listado mock de convocatorias.
   * @param options opciones de búsqueda.
   * @returns listado de convocatorias.
   */
  findConvocatoria(options?: SgiRestFindOptions) {
    this.logger.debug(ConvocatoriaService.name, `findConvocatoria(${options ? JSON.stringify(options) : ''})`, '-', 'START');


    return of({
      page: null,
      total: convocatorias.length,
      items: convocatorias
    } as SgiRestListResult<IConvocatoria>);
  }


}
