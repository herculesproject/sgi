import { Injectable } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env';
import { ConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';
import { SgiMutableRestService } from '@sgi/framework/http';
import { Comite } from '@core/models/eti/comite';
import { TipoConvocatoriaReunion } from '@core/models/eti/tipo-convocatoria-reunion';
import { SgiBaseConverter } from '@sgi/framework/core/';
import { IAsistente } from '@core/models/eti/asistente';
import { tap } from 'rxjs/operators';
import { Evaluacion } from '@core/models/eti/evaluacion';


interface IConvocatoriaReunion {

  /** ID */
  id: number;
  /** Comite */
  comite: Comite;
  /** Tipo Convocatoria Reunion */
  tipoConvocatoriaReunion: TipoConvocatoriaReunion;
  /** Fecha evaluación */
  fechaEvaluacion: Date;
  /** Hora fin */
  horaInicio: number;
  /** Minuto inicio */
  minutoInicio: number;
  /** Fecha Limite */
  fechaLimite: Date;
  /** Lugar */
  lugar: string;
  /** Orden día */
  ordenDia: string;
  /** Año */
  anio: number;
  /** Numero acta */
  numeroActa: number;
  /** Fecha Envío */
  fechaEnvio: Date;
  /** Activo */
  activo: boolean;
}


@Injectable({
  providedIn: 'root',
})


export class ConvocatoriaReunionService extends SgiMutableRestService<number, IConvocatoriaReunion, ConvocatoriaReunion> {
  private static readonly MAPPING = '/convocatoriareuniones';
  private static readonly CONVERTER = new class extends SgiBaseConverter<IConvocatoriaReunion, ConvocatoriaReunion> {
    toTarget(value: IConvocatoriaReunion): ConvocatoriaReunion {
      return {
        id: value.id,
        comite: value.comite,
        tipoConvocatoriaReunion: value.tipoConvocatoriaReunion,
        fechaEvaluacion: value.fechaEvaluacion,
        horaInicio: value.horaInicio,
        minutoInicio: value.minutoInicio,
        fechaLimite: value.fechaLimite,
        lugar: value.lugar,
        ordenDia: value.ordenDia,
        anio: value.anio,
        numeroActa: value.numeroActa,
        fechaEnvio: value.fechaEnvio,
        activo: value.activo,
        convocantes: [],
        codigo: `ACTA${value.numeroActa}/${value.anio}/${value.comite.comite}`,
      };
    }
    fromTarget(value: ConvocatoriaReunion): IConvocatoriaReunion {
      return {
        id: value.id,
        comite: value.comite,
        tipoConvocatoriaReunion: value.tipoConvocatoriaReunion,
        fechaEvaluacion: value.fechaEvaluacion,
        horaInicio: value.horaInicio,
        minutoInicio: value.minutoInicio,
        fechaLimite: value.fechaLimite,
        lugar: value.lugar,
        ordenDia: value.ordenDia,
        anio: value.anio,
        numeroActa: value.numeroActa,
        fechaEnvio: value.fechaEnvio,
        activo: value.activo
      };
    }
  }();


  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      ConvocatoriaReunionService.name,
      logger,
      `${environment.serviceServers.eti}${ConvocatoriaReunionService.MAPPING}`,
      http, ConvocatoriaReunionService.CONVERTER
    );
  }

  /**
   * Devuelve todos los asitentes por convocatoria id.
   * @param idConvocatoria id convocatoria.
   */
  findAsistentes(idConvocatoria: number) {
    this.logger.debug(ConvocatoriaReunionService.name, `findAsistentes(${idConvocatoria})`, '-', 'START');
    return this.find<IAsistente, IAsistente>(`${this.endpointUrl}/${idConvocatoria}/asistentes`, null).pipe(
      tap(() => this.logger.debug(ConvocatoriaReunionService.name, `findAsistentes(${idConvocatoria})`, '-', 'END'))
    );
  }

  /**
   * Devuelve todos las evaluaciones por convocatoria id.
   * @param idConvocatoria id convocatoria.
   */
  findEvaluacionesActivas(idConvocatoria: number) {
    this.logger.debug(ConvocatoriaReunionService.name, `findEvaluacionesActivas(${idConvocatoria})`, '-', 'START');
    return this.find<Evaluacion, Evaluacion>(`${this.endpointUrl}/${idConvocatoria}/evaluaciones-activas`, null).pipe(
      tap(() => this.logger.debug(ConvocatoriaReunionService.name, `findEvaluacionesActivas(${idConvocatoria})`, '-', 'END'))
    );
  }

}
