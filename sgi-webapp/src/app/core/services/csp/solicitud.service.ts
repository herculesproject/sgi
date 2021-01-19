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
import { Observable, of, from } from 'rxjs';
import { tap, map, catchError, mergeMap, switchMap } from 'rxjs/operators';
import { ISolicitudModalidadBackend, SolicitudModalidadService } from './solicitud-modalidad.service';
import { ISolicitudDocumento } from '@core/models/csp/solicitud-documento';
import { ISolicitudHito } from '@core/models/csp/solicitud-hito';
import { ISolicitudProyectoDatos } from '@core/models/csp/solicitud-proyecto-datos';
import { ISolicitudProyectoEquipo } from '@core/models/csp/solicitud-proyecto-equipo';
import { SolicitudProyectoEquipoService, ISolicitudProyectoEquipoBackend } from './solicitud-proyecto-equipo.service';
import { PersonaFisicaService } from '../sgp/persona-fisica.service';
import { ISolicitudProyectoSocio } from '@core/models/csp/solicitud-proyecto-socio';
import { SolicitudProyectoSocioService, ISolicitudProyectoSocioBackend } from './solicitud-proyecto-socio.service';
import { EmpresaEconomicaService } from '../sgp/empresa-economica.service';


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

  constructor(
    logger: NGXLogger,
    protected http: HttpClient,
    private personaFisicaService: PersonaFisicaService,
    private empresaEconomicaService: EmpresaEconomicaService
  ) {
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

  /**
   * Recupera listado de historico estado
   * @param id solicitud
   * @param options opciones de búsqueda.
   */
  findEstadoSolicitud(solicitudId: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IEstadoSolicitud>> {
    this.logger.debug(SolicitudService.name, `findEstadoSolicitud(${solicitudId}, ${options})`, '-', 'start');
    const endpointUrl = `${this.endpointUrl}/${solicitudId}/estadosolicitudes`;
    return this.find<IEstadoSolicitud, IEstadoSolicitud>(endpointUrl, options)
      .pipe(
        tap(() => this.logger.debug(SolicitudService.name, `findEstadoSolicitud(${solicitudId}, ${options})`, '-', 'end'))
      );
  }

  /**
   * Recupera todos los documentos
   *
   * @param id Id de la solicitud
   * @param options Opciones de busqueda
   * @returns observable con la lista de documentos de la solicitud
   */
  findDocumentos(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<ISolicitudDocumento>> {
    this.logger.debug(SolicitudService.name,
      `findDocumentos(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'start');
    const url = `${this.endpointUrl}/${id}/solicituddocumentos`;
    return this.find<ISolicitudDocumento, ISolicitudDocumento>(url, options).pipe(
      tap(() => this.logger.debug(SolicitudService.name, `findDocumentos(${id}, ${options ? JSON.stringify(options) : options}`,
        '-', 'end'))
    );
  }

  /**
   * Recupera los hitos de la solicitud
   *
   * @param solicitudId Id de la solicitud
   * @param options opciones de busqueda
   * @returns observable con la lista de modalidades de la solicitud
   */
  findHitosSolicitud(solicitudId: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<ISolicitudHito>> {
    this.logger.debug(SolicitudService.name,
      `findHitosSolicitud(${solicitudId}, ${options ? JSON.stringify(options) : options}`, '-', 'start');

    const endpointUrl = `${this.endpointUrl}/${solicitudId}/solicitudhitos`;
    return this.find<ISolicitudHito, ISolicitudHito>(endpointUrl, options)
      .pipe(
        tap(() => this.logger.debug(SolicitudService.name,
          `findHitosSolicitud(${solicitudId}, ${options ? JSON.stringify(options) : options}`, '-', 'end'))
      );
  }

  /**
   * Comprueba si una solicitud está asociada a una convocatoria SGI.
   *
   * @param id Id de la solicitud.
   */
  hasConvocatoriaSGI(id: number): Observable<boolean> {
    this.logger.debug(SolicitudService.name, `hasConvocatoriaSGI(id: ${id})`, '-', 'start');
    const url = `${this.endpointUrl}/${id}/convocatoria-sgi`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(x => x.status === 200),
      tap(() => this.logger.debug(SolicitudService.name, `hasConvocatoriaSGI(id: ${id})`, '-', 'end'))
    );
  }

  /**
   * Devuelve los datos del proyecto de una solicitud
   *
   * @param solicitudId Id de la solicitud
   */
  findSolicitudProyectoDatos(solicitudId: number): Observable<ISolicitudProyectoDatos> {
    this.logger.debug(SolicitudService.name, `findSolicitudProyectoDatos(${solicitudId})`, '-', 'start');
    const endpointUrl = `${this.endpointUrl}/${solicitudId}/solicitudproyectodatos`;
    return this.http.get<ISolicitudProyectoDatos>(endpointUrl).pipe(
      tap(() => this.logger.debug(SolicitudService.name, `findSolicitudProyectoDatos(${solicitudId})`, '-', 'end')),
    );
  }

  /**
   * Devuelve los proyectoEquipos de una solicitud
   *
   * @param solicitudId Id de la solicitud
   */
  findAllSolicitudProyectoEquipo(solicitudId: number): Observable<SgiRestListResult<ISolicitudProyectoEquipo>> {
    this.logger.debug(SolicitudService.name, `findAllSolicitudProyectoEquipo(${solicitudId})`, '-', 'start');
    const endpointUrl = `${this.endpointUrl}/${solicitudId}/solicitudproyectoequipo`;
    return this.find<ISolicitudProyectoEquipo, ISolicitudProyectoEquipoBackend>(endpointUrl).pipe(
      map((result) => {
        const converted: SgiRestListResult<ISolicitudProyectoEquipo> = {
          items: result.items.map(solicitudProyectoEquipoBackend =>
            SolicitudProyectoEquipoService.CONVERTER.toTarget(solicitudProyectoEquipoBackend)),
          page: result.page,
          total: result.total
        };
        return converted;
      }),
      switchMap(resultList =>
        from(resultList.items).pipe(
          mergeMap(wrapper => this.loadSolicitante(wrapper)),
          switchMap(() => of(resultList))
        )
      ),
      tap(() => this.logger.debug(SolicitudService.name, `findAllSolicitudProyectoEquipo(${solicitudId})`, '-', 'end')),
    );
  }

  private loadSolicitante(solicitudProyectoEquipo: ISolicitudProyectoEquipo): Observable<ISolicitudProyectoEquipo> {
    this.logger.debug(SolicitudService.name,
      `loadSolicitante(solicitanteRef: ${solicitudProyectoEquipo})`, 'start');
    const personaRef = solicitudProyectoEquipo.persona.personaRef;
    return this.personaFisicaService.getInformacionBasica(personaRef).pipe(
      map(solicitante => {
        solicitudProyectoEquipo.persona = solicitante;
        return solicitudProyectoEquipo;
      }),
      tap(() => this.logger.debug(SolicitudService.name,
        `loadSolicitante(solicitanteRef: ${solicitudProyectoEquipo})`, 'end')),
      catchError(() => of(solicitudProyectoEquipo))
    );
  }

  /**
   * Comprueba si existe una solicitudProyectoDatos asociada a una solicitud
   *
   * @param id Id de la solicitud
   */
  existsSolictudProyectoDatos(id: number): Observable<boolean> {
    this.logger.debug(SolicitudService.name, `existsSolictudProyectoDatos(id: ${id})`, '-', 'start');
    const url = `${this.endpointUrl}/${id}/solicitudproyectodatos`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(x => x.status === 200),
      tap(() => this.logger.debug(SolicitudService.name, `existsSolictudProyectoDatos(id: ${id})`, '-', 'end')),
    );
  }


  /**
   * Recupera los ISolicitudProyectoSocio de la solicitud
   *
   * @param id Id de la solicitud
   * @param options opciones de busqueda
   * @returns observable con la lista de ISolicitudProyectoSocio de la solicitud
   */
  findAllSolicitudProyectoSocio(id: number, options?: SgiRestFindOptions): Observable<ISolicitudProyectoSocio[]> {
    this.logger.debug(SolicitudService.name,
      `findAllSolicitudProyectoSocio(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'start');
    const endpointUrl = `${this.endpointUrl}/${id}/solicitudproyectosocio`;
    return this.find<ISolicitudProyectoSocio, ISolicitudProyectoSocioBackend>(endpointUrl, options)
      .pipe(
        map((result) => result.items.map(solicitudProyectoSocioBackend =>
          SolicitudProyectoSocioService.CONVERTER.toTarget(solicitudProyectoSocioBackend))
        ),
        tap(() => this.logger.debug(SolicitudService.name,
          `findAllSolicitudProyectoSocio(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'end'))
      );
  }
}
