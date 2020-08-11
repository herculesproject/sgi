import { Comite } from './comite';
import { TipoConvocatoriaReunion } from './tipo-convocatoria-reunion';
import { IAsistente } from './asistente';

export class ConvocatoriaReunion {

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
  /** Convocantes */
  convocantes: IAsistente[];
  /** Código */
  codigo: string;

  constructor(convocatoriaReunion?: ConvocatoriaReunion) {
    this.id = convocatoriaReunion?.id;
    this.comite = convocatoriaReunion?.comite;
    this.tipoConvocatoriaReunion = convocatoriaReunion?.tipoConvocatoriaReunion;
    this.fechaEvaluacion = convocatoriaReunion?.fechaEvaluacion;
    this.horaInicio = 15;
    this.minutoInicio = convocatoriaReunion?.minutoInicio;
    this.fechaLimite = convocatoriaReunion?.fechaLimite;
    this.lugar = convocatoriaReunion?.lugar;
    this.ordenDia = convocatoriaReunion?.ordenDia;
    this.anio = convocatoriaReunion?.anio;
    this.numeroActa = convocatoriaReunion?.numeroActa;
    this.fechaEnvio = convocatoriaReunion?.fechaEnvio;
    this.activo = convocatoriaReunion?.activo;
    this.convocantes = convocatoriaReunion?.convocantes;
  }



}
