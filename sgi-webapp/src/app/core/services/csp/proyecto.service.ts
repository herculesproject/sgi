import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ClasificacionCVN } from '@core/enums/clasificacion-cvn';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IEstadoProyecto } from '@core/models/csp/estado-proyecto';
import { IProyecto, TipoHojaFirmaEnum, TipoHorasAnualesEnum, TipoPlantillaJustificacionEnum } from '@core/models/csp/proyecto';
import { ISolicitud } from '@core/models/csp/solicitud';
import { ITipoAmbitoGeografico } from '@core/models/csp/tipo-ambito-geografico';
import { IModeloEjecucion, ITipoFinalidad } from '@core/models/csp/tipos-configuracion';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { environment } from '@env';
import { SgiBaseConverter } from '@sgi/framework/core';
import { SgiMutableRestService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

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


@Injectable({
  providedIn: 'root'
})
export class ProyectoService extends SgiMutableRestService<number, IProyectoBackend, IProyecto> {
  private static readonly MAPPING = '/proyectos';


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
}
