import { Injectable } from '@angular/core';
import { SgiMutableRestService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env';
import { ISolicitud } from '@core/models/csp/solicitud';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IEstadoSolicitud } from '@core/models/csp/estado-solicitud';
import { IPersona } from '@core/models/sgp/persona';
import { SgiBaseConverter } from '@sgi/framework/core';
import { TipoFormularioSolicitud } from '@core/enums/tipo-formulario-solicitud';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { ISolicitudModalidad } from '@core/models/csp/solicitud-modalidad';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { ISolicitudModalidadBackend, SolicitudModalidadService } from './solicitud-modalidad.service';


interface ISolicitudBackend {
  /** Id */
  id: number;

  /** Activo */
  activo: boolean;

  /** Codigo externo */
  codigoExterno: string;

  /** Codigo registro interno */
  codigoRegistroInterno: string;

  /** Estado solicitud */
  estado: IEstadoSolicitud;

  /** Convocatoria */
  convocatoria: IConvocatoria;

  /** Convocatoria externa */
  convocatoriaExterna: string;

  /** Creador */
  creadorRef: string;

  /** Solicitante */
  solicitanteRef: string;

  /** Tipo formulario solicitud */
  formularioSolicitud: TipoFormularioSolicitud;

  /** Unidad gestion */
  unidadGestionRef: string;

  /** Observaciones */
  observaciones: string;

}


@Injectable({
  providedIn: 'root'
})
export class SolicitudService extends SgiMutableRestService<number, ISolicitudBackend, ISolicitud> {
  private static readonly MAPPING = '/solicitudes';

  private static readonly CONVERTER = new class extends SgiBaseConverter<ISolicitudBackend, ISolicitud> {
    toTarget(value: ISolicitudBackend): ISolicitud {
      return {
        id: value.id,
        activo: value.activo,
        codigoExterno: value.codigoExterno,
        codigoRegistroInterno: value.codigoRegistroInterno,
        estado: value.estado,
        convocatoria: value.convocatoria,
        convocatoriaExterna: value.convocatoriaExterna,
        creador: { personaRef: value.creadorRef } as IPersona,
        solicitante: { personaRef: value.solicitanteRef } as IPersona,
        formularioSolicitud: value.formularioSolicitud,
        unidadGestion: { acronimo: value.unidadGestionRef } as IUnidadGestion,
        observaciones: value.observaciones
      };
    }

    fromTarget(value: ISolicitud): ISolicitudBackend {
      return {
        id: value.id,
        activo: value.activo,
        codigoExterno: value.codigoExterno,
        codigoRegistroInterno: value.codigoRegistroInterno,
        estado: value.estado,
        convocatoria: value.convocatoria ? { id: value.convocatoria.id } as IConvocatoria : undefined,
        convocatoriaExterna: value.convocatoriaExterna,
        creadorRef: value.creador?.personaRef,
        solicitanteRef: value.solicitante?.personaRef,
        formularioSolicitud: value.formularioSolicitud,
        unidadGestionRef: value.unidadGestion?.acronimo,
        observaciones: value.observaciones
      };
    }
  }();

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      SolicitudService.name,
      logger,
      `${environment.serviceServers.csp}${SolicitudService.MAPPING}`,
      http,
      SolicitudService.CONVERTER
    );
  }

  /**
   * Desactiva una solicitud por su id
   *
   * @param id identificador de la solicitud.
   */
  desactivar(id: number): Observable<void> {
    this.logger.debug(SolicitudService.name, `desactivar(`, '-', 'start');
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, undefined).pipe(
      tap(() => this.logger.debug(SolicitudService.name, `desactivar()`, '-', 'end'))
    );
  }

  /**
   * Reactiva una solicitud por su id
   *
   * @param id identificador de la solicitud.
   */
  reactivar(id: number): Observable<void> {
    this.logger.debug(SolicitudService.name, `reactivar(`, '-', 'start');
    return this.http.patch<void>(`${this.endpointUrl}/${id}/reactivar`, undefined).pipe(
      tap(() => this.logger.debug(SolicitudService.name, `reactivar()`, '-', 'end'))
    );
  }

  /**
   * Recupera todas las solicitudes activas e inactivas visibles por el usuario
   *
   * @param options opciones de busqueda
   * @returns observable con la lista de solicitudes
   */
  findAllTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<ISolicitud>> {
    this.logger.debug(SolicitudService.name, `findAllTodos()`, '-', 'START');
    return this.find<ISolicitudBackend, ISolicitud>(`${this.endpointUrl}/todos`, options, SolicitudService.CONVERTER)
      .pipe(
        tap(() => this.logger.debug(SolicitudService.name, `findAllTodos()`, '-', 'END'))
      );
  }

  /**
   * Recupera todas las modalidades de la solicitud
   *
   * @param solicitudId Id de la solicitud
   * @param options opciones de busqueda
   * @returns observable con la lista de modalidades de la solicitud
   */
  findAllSolicitudModalidades(solicitudId: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<ISolicitudModalidad>> {
    this.logger.debug(SolicitudService.name,
      `findAllSolicitudModalidades(${solicitudId}, ${options ? JSON.stringify(options) : options}`, '-', 'start');

    const endpointUrl = `${this.endpointUrl}/${solicitudId}${SolicitudModalidadService.MAPPING}`;
    return this.find<ISolicitudModalidadBackend, ISolicitudModalidad>(endpointUrl, options, SolicitudModalidadService.CONVERTER)
      .pipe(
        tap(() => this.logger.debug(SolicitudService.name,
          `findAllSolicitudModalidades(${solicitudId}, ${options ? JSON.stringify(options) : options}`, '-', 'end'))
      );
  }

}
