import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ClasificacionCVN } from '@core/enums/clasificacion-cvn';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IEstadoProyecto } from '@core/models/csp/estado-proyecto';
import { IPrograma } from '@core/models/csp/programa';
import { IProyecto, TipoHojaFirmaEnum, TipoHorasAnualesEnum, TipoPlantillaJustificacionEnum } from '@core/models/csp/proyecto';
import { IProyectoEntidadConvocante } from '@core/models/csp/proyecto-entidad-convocante';
import { IProyectoEntidadFinanciadora } from '@core/models/csp/proyecto-entidad-financiadora';
import { IProyectoHito } from '@core/models/csp/proyecto-hito';
import { IProyectoSocio } from '@core/models/csp/proyecto-socio';
import { IProyectoPaqueteTrabajo } from '@core/models/csp/proyecto-paquete-trabajo';
import { IProyectoPlazos } from '@core/models/csp/proyecto-plazo';
import { IProyectoContexto } from '@core/models/csp/proyecto-contexto';
import { ISolicitud } from '@core/models/csp/solicitud';
import { ITipoAmbitoGeografico } from '@core/models/csp/tipo-ambito-geografico';
import { IModeloEjecucion, ITipoFinalidad } from '@core/models/csp/tipos-configuracion';
import { IEmpresaEconomica } from '@core/models/sgp/empresa-economica';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { environment } from '@env';
import { SgiBaseConverter } from '@sgi/framework/core';
import { SgiMutableRestService, SgiRestFilter, SgiRestFilterType, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, throwError } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { IProyectoEntidadFinanciadoraBackend, ProyectoEntidadFinanciadoraService } from './proyecto-entidad-financiadora.service';
import { IProyectoSocioBackend, ProyectoSocioService } from './proyecto-socio.service';

interface IProyectoBackend {

  /** Id */
  id: number;

  /** EstadoProyecto */
  estado: IEstadoProyecto;

  /** Titulo */
  titulo: string;

  /** Acronimo */
  acronimo: string;

  /** codigoExterno */
  codigoExterno: string;

  /** Fecha Inicio */
  fechaInicio: Date;

  /** Fecha Fin */
  fechaFin: Date;

  /** modelo ejecucion */
  modeloEjecucion: IModeloEjecucion;

  /** finalidad */
  finalidad: ITipoFinalidad;

  /** convocatoria */
  convocatoria: IConvocatoria;

  /** solicitud */
  solicitud: ISolicitud;

  /** ambitoGeografico */
  ambitoGeografico: ITipoAmbitoGeografico;

  /** plantillaJustificacion */
  plantillaJustificacion: TipoPlantillaJustificacionEnum;

  /** confidencial */
  confidencial: boolean;

  /** clasificacionCVN */
  clasificacionCVN: ClasificacionCVN;

  /** convocatoriaExterna */
  convocatoriaExterna: string;

  /** colaborativo */
  colaborativo: boolean;

  /** coordinadorExterno */
  coordinadorExterno: boolean;

  /** uniSubcontratada */
  uniSubcontratada: boolean;

  /** timesheet */
  timesheet: boolean;

  /** plantillaHojaFirma */
  plantillaHojaFirma: TipoHojaFirmaEnum;

  /** paquetesTrabajo */
  paquetesTrabajo: boolean;

  /** costeHora */
  costeHora: boolean;

  /** tipoHorasAnuales */
  tipoHorasAnuales: TipoHorasAnualesEnum;

  /** contratos */
  contratos: boolean;

  /** facturacion */
  facturacion: boolean;

  /** iva */
  iva: boolean;

  /** observaciones */
  observaciones: string;

  /** unidadGestionRef */
  unidadGestionRef: string;

  /** finalista */
  finalista: boolean;

  /** limitativo */
  limitativo: boolean;

  /** anualidades */
  anualidades: boolean;

  /** activo  */
  activo: boolean;
}

export interface IProyectoEntidadConvocanteBackend {
  id: number;
  entidadRef: string;
  programaConvocatoria: IPrograma;
  programa: IPrograma;
}

@Injectable({
  providedIn: 'root'
})
export class ProyectoService extends SgiMutableRestService<number, IProyectoBackend, IProyecto> {
  private static readonly MAPPING = '/proyectos';
  private static readonly ENTIDAD_CONVOCANTES_MAPPING = '/entidadconvocantes';

  private static readonly CONVERTER = new class extends SgiBaseConverter<IProyectoBackend, IProyecto> {
    toTarget(value: IProyectoBackend): IProyecto {
      return {
        id: value.id,
        estado: value.estado,
        activo: value.activo,
        titulo: value.titulo,
        acronimo: value.acronimo,
        codigoExterno: value.codigoExterno,
        fechaInicio: value.fechaInicio,
        fechaFin: value.fechaFin,
        modeloEjecucion: value.modeloEjecucion,
        finalidad: value.finalidad,
        convocatoria: value.convocatoria,
        convocatoriaExterna: value.convocatoriaExterna,
        solicitud: value.solicitud,
        ambitoGeografico: value.ambitoGeografico,
        plantillaJustificacion: value.plantillaJustificacion,
        confidencial: value.confidencial,
        clasificacionCVN: value.clasificacionCVN,
        colaborativo: value.colaborativo,
        coordinadorExterno: value.coordinadorExterno,
        uniSubcontratada: value.uniSubcontratada,
        timesheet: value.timesheet,
        plantillaHojaFirma: value.plantillaHojaFirma,
        paquetesTrabajo: value.paquetesTrabajo,
        costeHora: value.costeHora,
        tipoHorasAnuales: value.tipoHorasAnuales,
        contratos: value.contratos,
        facturacion: value.facturacion,
        iva: value.iva,
        finalista: value.finalista,
        limitativo: value.limitativo,
        anualidades: value.anualidades,
        unidadGestion: { acronimo: value.unidadGestionRef } as IUnidadGestion,
        observaciones: value.observaciones,
        comentario: value.estado.comentario
      };
    }

    fromTarget(value: IProyecto): IProyectoBackend {
      return {
        id: value.id,
        estado: value.estado,
        titulo: value.titulo,
        acronimo: value.acronimo,
        codigoExterno: value.codigoExterno,
        fechaInicio: value.fechaInicio,
        fechaFin: value.fechaFin,
        unidadGestionRef: value.unidadGestion?.acronimo,
        modeloEjecucion: value.modeloEjecucion,
        finalidad: value.finalidad,
        convocatoria: value.convocatoria ? { id: value.convocatoria.id } as IConvocatoria : undefined,
        convocatoriaExterna: value.convocatoriaExterna,
        solicitud: value.solicitud,
        ambitoGeografico: value.ambitoGeografico,
        plantillaJustificacion: value.plantillaJustificacion,
        confidencial: value.confidencial,
        clasificacionCVN: value.clasificacionCVN,
        colaborativo: value.colaborativo,
        coordinadorExterno: value.coordinadorExterno,
        uniSubcontratada: value.uniSubcontratada,
        timesheet: value.timesheet,
        plantillaHojaFirma: value.plantillaHojaFirma,
        paquetesTrabajo: value.paquetesTrabajo,
        costeHora: value.costeHora,
        tipoHorasAnuales: value.tipoHorasAnuales,
        contratos: value.contratos,
        facturacion: value.facturacion,
        iva: value.iva,
        observaciones: value.observaciones,
        finalista: value.finalista,
        limitativo: value.limitativo,
        anualidades: value.anualidades,
        activo: value.activo
      };
    }
  }();

  static readonly ENTIDAD_CONVOCANTE_CONVERTER =
    new class extends SgiBaseConverter<IProyectoEntidadConvocanteBackend, IProyectoEntidadConvocante> {
      toTarget(value: IProyectoEntidadConvocanteBackend): IProyectoEntidadConvocante {
        return {
          id: value.id,
          entidad: { personaRef: value.entidadRef } as IEmpresaEconomica,
          programaConvocatoria: value.programaConvocatoria,
          programa: value.programa
        };
      }

      fromTarget(value: IProyectoEntidadConvocante): IProyectoEntidadConvocanteBackend {
        return {
          id: value.id,
          entidadRef: value.entidad?.personaRef,
          programaConvocatoria: value.programaConvocatoria,
          programa: value.programa
        };
      }
    }();

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      ProyectoService.name,
      logger,
      `${environment.serviceServers.csp}${ProyectoService.MAPPING}`,
      http,
      ProyectoService.CONVERTER
    );
  }

  /**
   * Devuelve todos que estén asociadas a
   * las unidades de gestión a las que esté vinculado el usuario
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<IProyecto>> {
    this.logger.debug(ProyectoService.name, `${this.findTodos.name}(`, '-', 'START');
    return this.find<IProyecto, IProyecto>(`${this.endpointUrl}/todos`, options).pipe(
      tap(() => this.logger.debug(ProyectoService.name, `${this.findTodos.name}()`, '-', 'END'))
    );
  }

  /**
   * Recupera los paquete trabajo de un proyecto
   * @param idProyecto Identificador del proyecto.
   * @returns Listado de paquete trabajo.
   */
  findPaqueteTrabajoProyecto(idProyecto: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IProyectoPaqueteTrabajo>> {
    this.logger.debug(ProyectoService.name, `findPaqueteTrabajoProyecto(${idProyecto}, ${options})`, '-', 'start');
    const endpointUrl = `${this.endpointUrl}/${idProyecto}/proyectopaquetetrabajos`;
    return this.find<IProyectoPaqueteTrabajo, IProyectoPaqueteTrabajo>(endpointUrl, options)
      .pipe(
        tap(() => this.logger.debug(ProyectoService.name, `findPaqueteTrabajoProyecto(${idProyecto}, ${options})`, '-', 'end'))
      );
  }

  /**
   * Recupera los plazos de un proyecto
   * @param idProyecto Identificador del proyecto.
   * @returns Listado de plazos.
   */
  findPlazosProyecto(idProyecto: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IProyectoPlazos>> {
    this.logger.debug(ProyectoService.name, `findPlazosProyecto(${idProyecto}, ${options})`, '-', 'start');
    const endpointUrl = `${this.endpointUrl}/${idProyecto}/proyectofases`;
    return this.find<IProyectoPlazos, IProyectoPlazos>(endpointUrl, options)
      .pipe(
        tap(() => this.logger.debug(ProyectoService.name, `findPlazosProyecto(${idProyecto}, ${options})`, '-', 'end'))
      );
  }

  /**
   * Devuelve los datos del proyecto contexto
   *
   * @param proyectoID Id del proyecto
   */
  findProyectoContexto(proyectoID: number): Observable<IProyectoContexto> {
    this.logger.debug(ProyectoService.name, `findProyectoContexto(${proyectoID})`, '-', 'start');
    const endpointUrl = `${this.endpointUrl}/${proyectoID}/proyecto-contextoproyectos`;
    return this.http.get<IProyectoContexto>(endpointUrl).pipe(
      tap(() => this.logger.debug(ProyectoService.name, `findProyectoContexto(${proyectoID})`, '-', 'end')),
    );
  }

  /**
   * Desactivar proyecto
   * @param options opciones de búsqueda.
   */
  desactivar(id: number): Observable<void> {
    this.logger.debug(ProyectoService.name, `${this.desactivar.name}(`, '-', 'start');
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, undefined).pipe(
      tap(() => this.logger.debug(ProyectoService.name, `${this.desactivar.name}()`, '-', 'end'))
    );
  }

  /**
   * Reactivar proyecto
   * @param options opciones de búsqueda.
   */
  reactivar(id: number): Observable<void> {
    this.logger.debug(ProyectoService.name, `${this.reactivar.name}(`, '-', 'start');
    return this.http.patch<void>(`${this.endpointUrl}/${id}/reactivar`, undefined).pipe(
      tap(() => this.logger.debug(ProyectoService.name, `${this.reactivar.name}()`, '-', 'end'))
    );
  }

  private findEntidadesFinanciadoras(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IProyectoEntidadFinanciadora>> {
    this.logger.debug(ProyectoService.name,
      `findEntidadesFinanciadoras(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'start');
    return this.find<IProyectoEntidadFinanciadoraBackend, IProyectoEntidadFinanciadora>(
      `${this.endpointUrl}/${id}/proyectoentidadfinanciadoras`, options, ProyectoEntidadFinanciadoraService.CONVERTER).pipe(
        tap(() => this.logger.debug(ProyectoService.name,
          `findEntidadesFinanciadoras(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'end'))
      );
  }

  private findEntidadesFinanciadorasFilterAjenas(id: number, ajenas: boolean, options?: SgiRestFindOptions): Observable<SgiRestListResult<IProyectoEntidadFinanciadora>> {
    this.logger.debug(ProyectoService.name,
      `findEntidadesFinanciadorasFilterAjenas(${id}, ${ajenas}, ${options ? JSON.stringify(options) : options}`, '-', 'start');
    let queryOptions: SgiRestFindOptions = options;
    if (queryOptions) {
      let filterExists: SgiRestFilter;
      if (queryOptions.filters) {
        filterExists = queryOptions.filters.find(filter => filter.field === 'ajena');
      }
      else {
        filterExists = { field: 'ajena' } as SgiRestFilter;
      }
      if (filterExists) {
        // Force value
        filterExists.type = SgiRestFilterType.EQUALS;
        filterExists.value = `${ajenas}`;
      }
    }
    else {
      queryOptions = {
        filters: [
          {
            field: 'ajena',
            type: SgiRestFilterType.EQUALS,
            value: `${ajenas}`
          }
        ]
      };
    }
    return this.findEntidadesFinanciadoras(id, queryOptions).pipe(
      tap(() => this.logger.debug(ProyectoService.name,
        `findEntidadesFinanciadorasFilterAjenas(${id}, ${ajenas}, ${options ? JSON.stringify(options) : options}`, '-', 'end'))
    );
  }

  /**
   * Obtiene el listado de entidades financiadores asociadas al proyecto que NO son ajenas a la convocatoria
   * @param id Identificador del proyecto
   * @param options Opciones de filtrado/ordenación
   */
  findEntidadesFinanciadorasPropias(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IProyectoEntidadFinanciadora>> {
    this.logger.debug(ProyectoService.name,
      `findEntidadesFinanciadorasPropias(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'start');
    return this.findEntidadesFinanciadorasFilterAjenas(id, false, options).pipe(
      tap(() => this.logger.debug(ProyectoService.name,
        `findEntidadesFinanciadorasPropias(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'end'))
    );
  }

  /**
   * Obtiene el listado de entidades financiadores asociadas al proyecto que son ajenas a la convocatoria
   * @param id Identificador del proyecto
   * @param options Opciones de filtrado/ordenación
   */
  findEntidadesFinanciadorasAjenas(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IProyectoEntidadFinanciadora>> {
    this.logger.debug(ProyectoService.name,
      `findEntidadesFinanciadorasAjenas(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'start');
    return this.findEntidadesFinanciadorasFilterAjenas(id, true, options).pipe(
      tap(() => this.logger.debug(ProyectoService.name,
        `findEntidadesFinanciadorasAjenas(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'end'))
    );
  }

  /**
   * Recupera los hitos de un proyecto
   * @param idProyecto Identificador del proyecto.
   * @returns Listado de hitos.
   */
  findHitosProyecto(idProyecto: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IProyectoHito>> {
    this.logger.debug(ProyectoService.name, `findHitosProyecto(${idProyecto}, ${options})`, '-', 'start');
    const endpointUrl = `${this.endpointUrl}/${idProyecto}/proyectohitos`;
    return this.find<IProyectoHito, IProyectoHito>(endpointUrl, options)
      .pipe(
        tap(() => this.logger.debug(ProyectoService.name, `findHitosProyecto(${idProyecto}, ${options})`, '-', 'end'))
      );
  }

  /**
   * Recupera los IProyectoSocio del proyecto
   *
   * @param id Id de la solicitud
   * @param options opciones de busqueda
   * @returns observable con la lista de IProyectoSocio del proyecto
   */
  findAllProyectoSocioProyecto(id: number, options?: SgiRestFindOptions): Observable<IProyectoSocio[]> {
    this.logger.debug(ProyectoService.name,
      `findAllProyectoSocioProyecto(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'start');
    const endpointUrl = `${this.endpointUrl}/${id}/proyectosocios`;
    return this.find<IProyectoSocio, IProyectoSocioBackend>(endpointUrl, options)
      .pipe(
        map((result) => result.items.map(solicitudProyectoSocioBackend =>
          ProyectoSocioService.CONVERTER.toTarget(solicitudProyectoSocioBackend))
        ),
        tap(() => this.logger.debug(ProyectoService.name,
          `findAllProyectoSocioProyecto(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'end'))
      );
  }

  /**
   * Recupera los ProyectoEntidadConvocante de un proyecto
   * @param id Identificador del proyecto.
   * @returns Listado de ProyectoEntidadConvocante.
   */
  findAllEntidadConvocantes(idProyecto: number, options?: SgiRestFindOptions):
    Observable<SgiRestListResult<IProyectoEntidadConvocante>> {
    this.logger.debug(ProyectoService.name,
      'findAllEntidadConvocantes()', '-', 'start');
    const endpointUrl = `${this.endpointUrl}/${idProyecto}/${ProyectoService.ENTIDAD_CONVOCANTES_MAPPING}`;
    return this.find<IProyectoEntidadConvocanteBackend, IProyectoEntidadConvocante>(endpointUrl, options,
      ProyectoService.ENTIDAD_CONVOCANTE_CONVERTER)
      .pipe(
        tap(() => this.logger.debug(ProyectoService.name,
          'findAllEntidadConvocantes()', '-', 'end'))
      );
  }

  public createEntidadConvocante(idProyecto: number, element: IProyectoEntidadConvocante): Observable<IProyectoEntidadConvocante> {
    this.logger.debug(ProyectoService.name, 'createEntidadConvocante()', '-', 'START');
    const endpointUrl = `${this.endpointUrl}/${idProyecto}/${ProyectoService.ENTIDAD_CONVOCANTES_MAPPING}`;
    return this.http.post<IProyectoEntidadConvocanteBackend>(endpointUrl,
      ProyectoService.ENTIDAD_CONVOCANTE_CONVERTER.fromTarget(element)).pipe(
        // TODO: Explore the use a global HttpInterceptor with or without a custom error
        catchError((error: HttpErrorResponse) => {
          // Log the error
          this.logger.error(ProyectoService.name, 'createEntidadConvocante():', error);
          // Pass the error to subscribers. Anyway they would decide what to do with the error.
          return throwError(error);
        }),
        map(response => {
          this.logger.debug(ProyectoService.name, 'createEntidadConvocante()', '-', 'END');
          return ProyectoService.ENTIDAD_CONVOCANTE_CONVERTER.toTarget(response);
        })
      );
  }

  public deleteEntidadConvocanteById(idProyecto: number, id: number) {
    this.logger.debug(ProyectoService.name, 'deleteEntidadConvocanteById()', '-', 'START');
    const endpointUrl = `${this.endpointUrl}/${idProyecto}/${ProyectoService.ENTIDAD_CONVOCANTES_MAPPING}`;
    return this.http.delete<IProyectoEntidadConvocante>(`${endpointUrl}/${id}`).pipe(
      // TODO: Explore the use a global HttpInterceptor with or without a custom error
      catchError((error: HttpErrorResponse) => {
        // Log the error
        this.logger.error(ProyectoService.name, 'deleteEntidadConvocanteById():', error);
        // Pass the error to subscribers. Anyway they would decide what to do with the error.
        return throwError(error);
      }),
      map(() => {
        this.logger.debug(ProyectoService.name, 'deleteEntidadConvocanteById()', '-', 'END');
      })
    );
  }

  setEntidadConvocantePrograma(idProyecto: number, id: number, programa: IPrograma): Observable<IProyectoEntidadConvocante> {
    this.logger.debug(ProyectoService.name, 'setEntidadConvocantePrograma()', '-', 'start');
    const endpointUrl = `${this.endpointUrl}/${idProyecto}/${ProyectoService.ENTIDAD_CONVOCANTES_MAPPING}`;
    return this.http.patch<IProyectoEntidadConvocante>(`${endpointUrl}/${id}/programa`, programa).pipe(
      tap(() => this.logger.debug(ProyectoService.name, 'setEntidadConvocantePrograma()', '-', 'end'))
    );
  }
}
